"""Local service editorial workflow: research, import, validate, approve, publish, report.

The original website catalog and website review queue are never written here.
"""
import argparse,copy,hashlib,json,re,subprocess,time,io
from datetime import date,datetime,timezone
from pathlib import Path
from urllib.parse import urlsplit,urljoin

ROOT=Path(__file__).resolve().parents[1]
DIRECTORY=ROOT/'data/services'
PUBLISHED=DIRECTORY/'catalog.json'
QUEUE=DIRECTORY/'review_queue.json'

def read(path,default=None):
    return json.loads(path.read_text(encoding='utf-8')) if path.exists() else default

def write(path,value):
    path.parent.mkdir(parents=True,exist_ok=True)
    temporary=path.with_suffix(path.suffix+'.tmp')
    temporary.write_text(json.dumps(value,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    temporary.replace(path)

def official_url(value):
    try:
        if not isinstance(value,str) or re.search(r'\s|[\x00-\x1f\x7f]',value):return False
        u=urlsplit(value)
        return u.scheme=='https' and u.hostname and u.hostname.endswith(('.gov.in','.nic.in')) and not u.username and not u.password and u.port in (None,443) and not u.fragment
    except (ValueError,TypeError):return False

def validate_service(row,catalog,organizations,publication=True):
    for field in ('id','websiteId','organizationId','title','description','department','level','informationUrl','sourceUrl','verifiedOn','reviewNotes'):
        if not isinstance(row.get(field),str) or not row[field].strip():raise ValueError(f'Missing {field}')
    if not re.fullmatch(r'[a-z0-9][a-z0-9-]{2,100}',row['id']):raise ValueError('Invalid service ID')
    sites={s['id']:s for s in catalog['websites']};categories={c['id'] for c in catalog['categories']}
    orgs={o['id']:o for o in organizations}
    if row['websiteId'] not in sites:raise ValueError('Unknown website')
    if row['organizationId'] not in orgs or row['websiteId'] not in orgs[row['organizationId']]['websiteIds']:raise ValueError('Unknown website organization relationship')
    if orgs[row['organizationId']]['name']!=sites[row['websiteId']]['organization']:raise ValueError('Organization identity differs from existing website')
    if not row.get('categories') or not set(row['categories'])<=categories:raise ValueError('Unknown categories')
    if row['level'] not in ('Central','State','District','Local'):raise ValueError('Invalid level')
    if row['level']!='Central' and not row.get('state'):raise ValueError('Regional service requires a state')
    if row['level']=='Central' and (row.get('state') or row.get('district')):raise ValueError('Central service cannot claim a regional jurisdiction')
    if row.get('state') and row['state'] not in {s.get('state') for s in sites.values()}:raise ValueError('Unknown state jurisdiction')
    if row.get('state') and sites[row['websiteId']].get('state') not in (None,row['state']):raise ValueError('Service jurisdiction conflicts with regional website')
    if row['level'] in ('District','Local') and not row.get('district'):raise ValueError('District/local service requires a district')
    if row.get('applicationMode') not in ('online','assisted','information'):raise ValueError('Invalid application mode')
    for name in ('informationUrl','sourceUrl','applicationUrl'):
        if (row.get(name) is not None or name!='applicationUrl') and not official_url(row.get(name)):raise ValueError(f'Invalid official {name}')
    if row['applicationMode']=='online' and not row.get('applicationUrl'):raise ValueError('Online service needs confirmed application route')
    if row['applicationMode']!='online' and row.get('applicationUrl') is not None:raise ValueError('Information/assisted service must not expose Apply')
    checked=date.fromisoformat(row['verifiedOn'])
    if checked>date.today() or (date.today()-checked).days>30:raise ValueError('Service review is future-dated or stale')
    if publication and (row.get('verificationStatus')!='verified' or row.get('publicationStatus')!='published'):raise ValueError('Service not approved for publication')
    evidence=row.get('evidence',[]);ids={e['id'] for e in evidence}
    if not evidence or len(ids)!=len(evidence):raise ValueError('Missing/duplicate evidence')
    for e in evidence:
        if not official_url(e.get('url')) or not e.get('publisher') or not e.get('summary') or not e.get('locator'):raise ValueError('Incomplete official evidence')
        if e.get('checkedOn')!=row['verifiedOn']:raise ValueError('Evidence review date differs')
    if not any(e['url']==row['informationUrl'] for e in evidence):raise ValueError('Information URL needs evidence')
    if not row.get('linkEvidenceIds') or not set(row['linkEvidenceIds'])<=ids:raise ValueError('Application/information route requires evidence references')
    for field in ('eligibility','fees','deadline','processing'):
        facts=[row[field]] if row.get(field) is not None else []
        for fact in facts:validate_fact(fact,ids)
    for field in ('documents','conditionalDocuments','procedure','beneficiaryGroups'):
        facts=row.get(field)
        if facts is not None:
            if not isinstance(facts,list) or not facts:raise ValueError('Unknown lists must be null, not empty')
            for fact in facts:validate_fact(fact,ids)
    if not row.get('citizenIntents') or not row.get('synonyms'):raise ValueError('Search phrases missing')
    if row.get('possibleDuplicateOf') or row.get('duplicateOf'):raise ValueError('Unresolved duplicate candidate')
    route=row.get('applicationRouteEvidence')
    if route is not None:
        if route.get('status') not in ('confirmed','unconfirmed','not_applicable'):raise ValueError('Invalid route review')
        if not route.get('notes') or route.get('checkedOn')!=row['verifiedOn'] or not route.get('evidenceIds') or not set(route['evidenceIds'])<=ids:raise ValueError('Route review lacks provenance')
        if route['status']=='confirmed':
            if row['applicationMode']!='online' or route.get('url')!=row.get('applicationUrl') or not official_url(route.get('finalUrl')) or route.get('tlsVerified') is not True or route.get('httpStatus')!=200:raise ValueError('Unconfirmed application route')
            if not route.get('linkedFromSource') or not route.get('entryReviewed'):raise ValueError('HTTP success alone is not a reviewed application entry')
            if route.get('finalUrl')!=route.get('url') and not route.get('redirectReviewed'):raise ValueError('Unreviewed application redirect')
        elif row.get('applicationUrl') is not None or row['applicationMode']=='online':raise ValueError('Unconfirmed route cannot expose Apply')
    return row

def candidate_digest(row):
    return hashlib.sha256(json.dumps(row,sort_keys=True,ensure_ascii=False).encode()).hexdigest()

def validate_collection(services,catalog,organizations):
    seen=set();ids=set()
    for row in services:
        validate_service(row,catalog,organizations)
        identity=tuple(re.sub(r'\W+',' ',str(row.get(k) or '').casefold()).strip() for k in ('title','department','level','state','district'))
        if row['id'] in ids or identity in seen:raise ValueError('Duplicate service identity in jurisdiction')
        ids.add(row['id']);seen.add(identity)

def amend_pending(queue,amendments):
    """Explicit digest-bound amendments retain the full prior pending decision."""
    result=copy.deepcopy(queue)
    for amendment in amendments:
        digest=candidate_digest(amendment)
        if any(h.get('amendmentSha256')==digest for q in result for h in q.get('history',[])):continue
        matches=[q for q in result if q['id']==amendment['id'] and q['status']=='NEEDS_REVIEW']
        if len(matches)!=1:raise ValueError('Amendment must identify one pending case')
        entry=matches[0]
        if candidate_digest(entry)!=amendment.get('previousSha256'):raise ValueError('Pending case changed since review')
        if not amendment.get('reviewedBy') or not amendment.get('reason') or date.fromisoformat(amendment['checkedOn'])>date.today():raise ValueError('Incomplete pending review')
        prior=copy.deepcopy(entry);entry.setdefault('history',[]).append({'amendmentSha256':digest,'previous':prior,'reviewedBy':amendment['reviewedBy'],'checkedOn':amendment['checkedOn']})
        entry.update(reason=amendment['reason'],lastReviewedOn=amendment['checkedOn'],reviewEvidence=amendment.get('reviewEvidence',[]))
        if amendment.get('candidate') is not None:
            if amendment['candidate']['id']!=entry['id']:raise ValueError('Amendment changed stable ID')
            entry['candidate']=amendment['candidate']
    return result

def validate_fact(fact,ids):
    if not isinstance(fact,dict) or not fact.get('text') or not fact.get('evidenceIds') or not set(fact['evidenceIds'])<=ids:raise ValueError('Fact lacks supporting evidence')

def merged_catalog(catalog=None):
    if catalog is None:catalog=read(ROOT/'app/src/main/assets/catalog.json')
    result=copy.deepcopy(catalog);pilot=read(PUBLISHED,{'version':catalog['version'],'services':[],'organizations':[]})
    existing={s['id'] for s in result.get('services',[])}
    for service in pilot['services']:
        if service['id'] in existing:raise ValueError('Duplicate service ID')
        validate_service(service,catalog,pilot['organizations']);existing.add(service['id'])
    validate_collection(pilot['services'],catalog,pilot['organizations'])
    result['services']=result.get('services',[])+pilot['services']
    result['serviceOrganizations']=pilot['organizations']
    result['version']=max(catalog['version'],pilot['version'])
    return result

def research(url,output):
    if not official_url(url):raise ValueError('Research only explicit official HTTPS URLs')
    output=output.resolve()
    if not output.is_relative_to((DIRECTORY/'research').resolve()):raise ValueError('Research metadata must remain under data/services/research')
    marker=b'\n__JANSETU_HTTP__'
    result=subprocess.run(['curl.exe','--fail','--silent','--show-error','--location','--max-redirs','4','--max-time','30','--max-filesize','4000000','--proto','=https','--proto-redir','=https',
        '--write-out','\n__JANSETU_HTTP__%{http_code}\t%{url_effective}\t%{num_redirects}',url],capture_output=True)
    body,_,tail=result.stdout.rpartition(marker)
    fields=tail.decode(errors='replace').split('\t')
    meta={'url':url,'retrievedAt':datetime.now(timezone.utc).isoformat(),'httpStatus':int(fields[0]) if fields and fields[0].isdigit() else 0,
        'finalUrl':fields[1] if len(fields)>1 else url,'redirects':int(fields[2]) if len(fields)>2 and fields[2].isdigit() else 0,
        'success':result.returncode==0,'tlsVerified':result.returncode==0,'curlExit':result.returncode}
    if result.returncode:
        meta.update(error=result.stderr.decode(errors='replace')[:500],review='Failed normal retrieval; not evidence of service retirement or a confirmed route')
        write(output,meta);print(f'Retrieval held ({result.returncode} / HTTP {meta["httpStatus"]}): {output.name}');return meta
    if not official_url(meta['finalUrl']):
        meta.update(success=False,review='Redirect left the permitted official HTTPS scope; not accepted as service evidence')
        write(output,meta);return meta
    from bs4 import BeautifulSoup
    links=[];title=None
    if body.startswith(b'%PDF'):
        from pypdf import PdfReader
        text=' '.join(page.extract_text() or '' for page in PdfReader(io.BytesIO(body)).pages)
    else:
        soup=BeautifulSoup(body,'html.parser')
        title=soup.title.get_text(' ',strip=True) if soup.title else None
        links=[{'text':a.get_text(' ',strip=True)[:160],'url':urljoin(meta['finalUrl'],a['href'])} for a in soup.select('a[href]') if official_url(urljoin(meta['finalUrl'],a['href']))]
        for tag in soup(['script','style','input']):tag.decompose()
        text=' '.join(soup.stripped_strings)
    # Retrieval text is a local research aid. Published evidence is a concise
    # editorial summary with citations, never a wholesale copy of the source.
    meta.update(sha256=hashlib.sha256(body).hexdigest(),bytes=len(body),text=text[:120000],links=links,title=title,
        review='Unreviewed retrieval; HTTP success alone is not publication approval or proof of a completed transaction')
    write(output,meta)
    print(f'Retrieved {len(body)} bytes: {output.name}')
    return meta

def main():
    p=argparse.ArgumentParser(description=__doc__);sub=p.add_subparsers(dest='command',required=True)
    r=sub.add_parser('research');r.add_argument('url');r.add_argument('--output',type=Path,required=True)
    i=sub.add_parser('import');i.add_argument('path',type=Path);i.add_argument('--revision',action='store_true',help='Explicitly stage changed published services; retains prior decisions')
    a=sub.add_parser('publish');a.add_argument('approvals',type=Path)
    a=sub.add_parser('amend-pending');a.add_argument('path',type=Path)
    sub.add_parser('check');sub.add_parser('report')
    args=p.parse_args();catalog=read(ROOT/'app/src/main/assets/catalog.json')
    if args.command=='research':research(args.url,args.output);return
    if args.command=='amend-pending':
        updated=amend_pending(read(QUEUE,[]),read(args.path));write(QUEUE,updated);print('Pending review history retained');return
    if args.command=='import':
        incoming=read(args.path);queue=read(QUEUE,[]);known={r['id'] for r in queue};published={r['id']:r for r in read(PUBLISHED,{'services':[]})['services']}
        for row in incoming['services']:
            if args.revision and row['id'] in published:
                proposed={**row,'verificationStatus':'verified','publicationStatus':'published'}
                if proposed==published[row['id']]:continue
                digest=candidate_digest(row)
                if not any(q.get('revisionDigest')==digest for q in queue):queue.append({'id':row['id'],'status':'REVISION_NEEDS_REVIEW','revisionDigest':digest,'candidate':row})
            elif row['id'] not in known|set(published):queue.append({'id':row['id'],'status':'NEEDS_REVIEW','candidate':row});known.add(row['id'])
        write(QUEUE,queue);print(f'{len(queue)} service candidates retained; existing decisions preserved');return
    if args.command=='publish':
        approvals=read(args.approvals);pilot=read(PUBLISHED,{'version':10,'services':[],'organizations':[]});queue=read(QUEUE,[])
        orgs=approvals['organizations'];by_org={o['id']:o for o in pilot['organizations']};by_org.update({o['id']:o for o in orgs})
        published={s['id'] for s in pilot['services']}
        admitted=[]
        for entry in queue:
            approval=approvals.get('approvals',{}).get(entry['id'])
            revision=entry['status']=='REVISION_NEEDS_REVIEW'
            if not approval or entry['status'] in ('REJECTED','PUBLISHED') or entry['id'] in published and not revision:continue
            if not approval.get('reviewedBy') or not approval.get('notes') or not approval.get('candidateSha256'):raise ValueError('Approval requires named reviewer, notes and exact candidate digest')
            row=copy.deepcopy(entry['candidate'])
            digest=candidate_digest(row)
            if digest!=approval['candidateSha256']:raise ValueError('Candidate changed since approval')
            row.update(verificationStatus='verified',publicationStatus='published')
            if row['id'] not in published and row.get('applicationRouteEvidence') is None:raise ValueError('New services require an explicit route review')
            validate_service(row,catalog,list(by_org.values()))
            admitted.append(row);published.add(row['id']);entry.update(status='PUBLISHED',decision=approval)
        if admitted and pilot['services'] and approvals['version']<=pilot['version']:raise ValueError('Publication version must increase')
        updated={s['id']:s for s in pilot['services']}
        updated.update({s['id']:s for s in admitted})
        # Validate the entire proposed publication before writing either file.
        validate_collection(list(updated.values()),catalog,list(by_org.values()))
        pilot.update(version=max(pilot['version'],approvals['version']),organizations=list(by_org.values()),services=list(updated.values()))
        write(PUBLISHED,pilot);write(QUEUE,queue);print(f'Published {len(admitted)} services; total {len(pilot["services"])}')
    combined=merged_catalog(catalog)
    if args.command=='report':
        pilot=read(PUBLISHED,{'services':[],'organizations':[]});queue=read(QUEUE,[])
        report={'websites':len(combined['websites']),'services':len(combined['services']),'serviceOrganizations':len(pilot['organizations']),'pendingServices':sum(r['status'] in ('NEEDS_REVIEW','REVISION_NEEDS_REVIEW') for r in queue),'unknownEligibility':sum(s.get('eligibility') is None for s in pilot['services']),'unknownDocuments':sum(s.get('documents') is None for s in pilot['services'])}
        write(ROOT/'data/validation_reports/service-catalog-summary.json',report);print(json.dumps(report))
    else:print(f'Services valid: {len(combined["services"])}; website catalog untouched')

if __name__=='__main__':main()
