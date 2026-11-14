"""Reproduce the dated, manually reviewed Phase 3C proposals from retained sources.

Prepares drafts, a research ledger and exact-digest editorial decisions. Does not
fetch, publish or modify the website directory. Publication is a separate command.
This is an October 16 snapshot, not an automatic fresh verification job.
"""
import copy,hashlib
from service_catalog import ROOT,DIRECTORY,read,write,candidate_digest

DAY='2026-10-16'
BASE=read(ROOT/'data/validation_reports/phase3c-baseline.json')
SITES={s['id']:s for s in read(ROOT/'app/src/main/assets/catalog.json')['websites']}
ORGS={o['id']:copy.deepcopy(o) for o in BASE['services']['organizations']}
ROWS=[];LEDGER=[];APPROVED=set()

def source(name):return read(DIRECTORY/'research/phase3c'/f'{name}.json')
def fact(text,eid):return {'text':text,'evidenceIds':[eid]} if text else None
def evidence(name,publisher,summary,locator):
    d=source(name)
    if not d.get('success') or not d.get('text','').strip():raise ValueError(f'Unreadable evidence: {name}')
    return {'id':name,'url':d['url'],'publisher':publisher,'checkedOn':DAY,'summary':summary,'locator':locator,'contentSha256':d['sha256']}

def add(id,title,website,state,department,src,description=None,category='documents',publish=False,
        eligibility=None,docs=None,conditional=None,procedure=None,fees=None,processing=None,
        groups=None,intents=None,synonyms=None,extra=None,route=None,reason=None,level=None,district=None):
    eid=src['id'];site=SITES[website];oid='org-'+hashlib.sha256(site['organization'].encode()).hexdigest()[:16]
    org=ORGS.setdefault(oid,{'id':oid,'name':site['organization'],'websiteIds':[]})
    if website not in org['websiteIds']:org['websiteIds'].append(website)
    row=dict(id=id,title=title,websiteId=website,organizationId=oid,description=description or f'Official information about {title.lower()}.',
        categories=[category],department=department,level=level or ('State' if state else 'Central'),state=state,district=district,
        informationUrl=src['url'],sourceUrl=src['url'],applicationUrl=None,applicationMode='information',
        evidence=[src]+(extra or []),linkEvidenceIds=[eid],verifiedOn=DAY,verificationStatus='needs_review',publicationStatus='draft',
        citizenIntents=intents or [f'get {title.lower()}'],synonyms=synonyms or [title.lower()],
        eligibility=fact(eligibility,eid),documents=[fact(t,eid) for t in docs] if docs else None,
        conditionalDocuments=[fact(t,eid) for t in conditional] if conditional else None,
        procedure=[fact(t,eid) for t in procedure] if procedure else None,fees=fact(fees,eid),processing=fact(processing,eid),deadline=None,
        beneficiaryGroups=[fact(t,eid) for t in groups] if groups else None,
        reviewNotes='Source reviewed by Codex on '+DAY+'. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. '+(reason or ''),
        applicationRouteEvidence={'status':'unconfirmed','checkedOn':DAY,'evidenceIds':[eid],'notes':reason or 'Only official information was reviewed; no current transaction entry was confirmed.'})
    if route:
        d=source(route)
        linksource=next((e for e in row['evidence'] if any(l['url']==d['url'] for l in source(e['id']).get('links',[]))),None)
        if not d.get('success') or not d.get('tlsVerified') or linksource is None:raise ValueError('Route must be observed in the official source')
        row.update(applicationMode='online',applicationUrl=d['url'],linkEvidenceIds=[eid,linksource['id']])
        row['applicationRouteEvidence']=dict(status='confirmed',checkedOn=DAY,evidenceIds=[eid,linksource['id']],url=d['url'],finalUrl=d['finalUrl'],httpStatus=d['httpStatus'],tlsVerified=True,linkedFromSource=True,entryReviewed=True,redirectReviewed=False,
            notes='Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.')
    ROWS.append(row)
    if publish:APPROVED.add(id)
    LEDGER.append(dict(id=id,title=title,level=row['level'],state=state,district=district,category=category,investigatedOn=DAY,
        discovered=True,sourceReviewed=publish,decision='publish' if publish else 'pending',reason=reason or 'Service-specific fact and route review complete; only selected sourced facts are proposed.',
        sources=[{'url':e['url'],'researchFile':f'data/services/research/phase3c/{e["id"]}.json','sha256':e['contentSha256']} for e in row['evidence']]))
    return row

# Delhi: exact current list plus separately reviewed certificate PDFs. Nine
# further operations are discovered, not promoted from the generic list alone.
dl=evidence('delhi-list','eDistrict Delhi / Revenue Department','Service names, departments and public sign-in links.','Guidelines & Eligibility Criteria, Revenue and Social Welfare rows')
for key,title,elig,docs,conditional,groups in [
 ('income','Delhi Income Certificate','Selected conditions: an Indian individual permanently resident in NCT Delhi; confirm the current form and full conditions.',
  ['Identity and address evidence from the guideline alternatives; family-income evidence and applicable expenditure evidence.','Signed self-declaration and a recent passport-size photograph.'],['An earlier income certificate is optional; BPL/NFS evidence applies where available.'],None),
 ('domicile','Delhi Domicile Certificate','Selected conditions: Indian citizenship and continuous residence in Delhi for the preceding three years; see the guideline for minor applicants.',
  ['Identity evidence, proof of three years of residence, self-declaration and a photograph.'],['For a minor, the guideline lists parent identity and school/birth evidence alternatives.'],None),
 ('obc','Delhi OBC Certificate',None,['Identity/address evidence, self-declaration and photograph; caste and family-link evidence depend on the case.'],
  ['Delhi/Central OBC and migration cases have different criteria and evidence. Read sections A/B and the non-creamy-layer conditions; this is not a single universal checklist.'],['Other Backward Classes (OBC)']),
 ('sc','Delhi Scheduled Caste Certificate',None,['Selected identity/address evidence and self-declaration; family/caste evidence depends on the applicable case.'],
  ['The combined SC/ST guideline has distinct historical residence, family and migration cases. Read the complete applicable section.'],['Scheduled Castes (SC)']),
 ('surviving','Delhi Surviving Member Certificate','Selected conditions: an Indian individual resident in Delhi; the guideline defines the family evidence needed.',
  ['Applicant and surviving-family identity/address evidence, death certificate, signed declaration and photographs specified by the guideline.'],
  ['Additional family-member list when there are more than four survivors. Original declaration/physical verification may be required.'],None)]:
    src=evidence('delhi-'+key,'Revenue Department, Government of NCT of Delhi','Selected certificate requirements and conditional evidence.','Certificate guideline: eligibility, mandatory/conditional documents and important notes')
    add('dl-'+key+'-certificate',title,'igod-9908672336cdd912','Delhi','Revenue Department, Government of NCT of Delhi',src,
        publish=True,eligibility=elig,docs=docs,conditional=conditional,groups=groups,extra=[dl],route='delhi-login',
        procedure=['Use the official citizen login/application service, select the certificate and follow the current form. Document verification or an original declaration may also be required.'],
        intents=[f'get {key} certificate in Delhi',f'Delhi {key} certificate'],synonyms=[key+' certificate','Delhi certificate'])
for key,title,dept,cat in [
 ('st','Scheduled Tribe Certificate','Revenue','documents'),('ews','Income and Assets Certificate for Economically Weaker Section','Revenue','documents'),
 ('ncl','Non-Creamy Layer Certificate for OBC','Revenue','documents'),('marriage-registration','Registration of Marriage','Revenue','documents'),
 ('marriage-solemnization','Solemnization of Marriage','Revenue','documents'),('disability-id','Permanent Identity Card to the Disabled Person','Revenue','documents'),
 ('solvency','Solvency Certificate','Revenue','documents'),('lal-dora','Lal dora Certificate','Revenue','land'),('old-age-pension','Old Age Pension Scheme','Social Welfare','welfare')]:
    add('dl-'+key,title+' (Delhi)','igod-9908672336cdd912','Delhi',dept+' Department, Government of NCT of Delhi',dl,category=cat,
        reason='Listed in the current departmental table. Service-specific guideline/current requirements have not been reviewed; retain pending.')

# Kerala FAQ explicitly distinguishes OBC Caste from SC/ST Community. The
# current DB-verification instruction overrides blindly requiring a ration upload.
kl=evidence('kerala-list','Kerala eDistrict / Kerala State IT Mission','Revenue and other department service list.','Revenue Department certificate services')
kf=evidence('kerala-faq','Kerala eDistrict / Kerala State IT Mission','Certificate naming, account procedure, supporting-evidence and service-time tables.','General questions; supporting documents and time-limit tables')
for key,title,docs,days,group in [
 ('income','Income Certificate',['Income evidence appropriate to the applicant, such as salary evidence, tax returns or relevant income records; affidavit where required.'],6,None),
 ('nativity','Nativity Certificate',['Selected birth/school evidence and affidavit; parent school evidence may apply.'],5,None),
 ('caste-obc','Caste Certificate (OBC)',['Selected caste evidence, school certificate and affidavit.'],3,'Other Backward Classes (OBC)'),
 ('community-sc-st','Community Certificate (SC/ST)',['Selected caste evidence, school certificate and affidavit.'],5,'Scheduled Castes / Scheduled Tribes (SC/ST)'),
 ('domicile','Domicile Certificate',['Selected identity/birth evidence and affidavit; parent school evidence may apply.'],3,None),
 ('family-membership','Family Membership Certificate',['Selected identity/school evidence, affidavit and death evidence where applicable.'],6,None),
 ('relationship','Relationship Certificate',['Selected identity/school evidence and affidavit.'],7,None),
 ('legal-heir','Legal Heir Certificate',['Selected death and identity evidence and affidavit.'],None,None)]:
    add('kl-'+key+'-certificate',title+' (Kerala)','igod-50b73c3937b8d29b','Kerala','Revenue Department, Government of Kerala',kf,publish=True,
        description='Kerala eDistrict guidance for '+title.lower()+'. Requirements differ from similarly named certificates in other states.',docs=docs,
        conditional=['The FAQ says to verify the ration-card number with PDS instead of uploading a copy. Other supporting evidence depends on the certificate and current form.'],
        procedure=['Create/sign in to the official eDistrict account, complete One Time Registration, then choose Apply for a Certificate and the appropriate applicant registration.'],
        processing=f'The FAQ lists {days} working days for this certificate; this is a published service standard, not a personal delivery guarantee.' if days else 'The FAQ describes a 30-working-day objection period after gazette notification, then issue within 15 working days after that period if there is no objection; confirm the applicable process.',
        groups=[group] if group else None,extra=[kl],route='kerala-login',
        intents=[f'get {title.lower()} in Kerala',f'Kerala {title.lower()}'],synonyms=[title.lower(),'Kerala certificate'])
for key,title in [('possession','Possession Certificate'),('one-same','One and the Same Certificate'),('non-remarriage','Non-Remarriage Certificate'),
                  ('valuation','Valuation Certificate'),('dependency','Dependency Certificate'),('non-creamy','Non-Creamy Layer Certificate'),('conversion','Conversion Certificate')]:
    add('kl-'+key+'-certificate',title+' (Kerala)','igod-50b73c3937b8d29b','Kerala','Revenue Department, Government of Kerala',kl,
        reason='Current service list confirms the operation. Individual eligibility and conditional evidence are not yet reviewed; remain pending.')

# Odisha public HTML contains service modals. DirectApply endpoints returned
# HTTP 200 with no readable entry text: retain information-only, not Apply.
od=evidence('odisha-home','eDistrict Odisha / Revenue and Disaster Management Department','Public revenue-certificate modal descriptions, document alternatives, fees and time standards.','e-Certificates: corresponding named service modal (not the aggregate application statistics)')
odreason='Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.'
for key,title,docs,days,group in [
 ('resident','Resident Certificate',['Selected residence-evidence alternatives include a utility bill, land record, rental agreement or employer certificate; provide the applicable evidence, not every listed item.'],10,None),
 ('income','Income Certificate',['Selected income evidence includes salary records, tax returns or other income evidence as applicable.'],10,None),
 ('sc','Scheduled Caste Certificate',['Selected identity, land or family caste evidence as applicable; the source presents alternatives rather than a universal mandatory list.'],15,'Scheduled Castes (SC)'),
 ('st','Scheduled Tribe Certificate',['Selected identity, land or family caste evidence as applicable; the source presents alternatives rather than a universal mandatory list.'],15,'Scheduled Tribes (ST)'),
 ('ews','Income and Asset Certificate (EWS)',['Identity, income and land evidence as applicable. Salary and agricultural-income proof are required when those income values are declared.'],10,'Economically Weaker Sections (EWS)'),
 ('legal-heir','Legal Heir Certificate',['Death certificate is explicitly mandatory in this service modal; identity and other evidence depend on the case.'],30,None),
 ('obc','OBC Certificate',['Selected identity evidence, self-declaration, land record or land passbook as applicable.'],15,'Other Backward Classes (OBC)'),
 ('sebc','SEBC Certificate',['Selected identity evidence, self-declaration, land record or land passbook as applicable.'],15,'Socially and Educationally Backward Classes (SEBC)')]:
    add('od-'+key+'-certificate',title+' (Odisha)','igod-5e885f0e333d5206','Odisha','Revenue and Disaster Management Department, Government of Odisha',od,publish=True,
        description='Official Odisha guidance for '+title.lower()+'. Full eligibility and a functioning transaction entry still need confirmation.',docs=docs,
        fees='The service modal lists no government service charge; other channel charges have not been verified.',
        processing=f'The modal lists {days} working days as the service standard; observed processing and individual outcomes can differ.',
        groups=[group] if group else None,reason=odreason,
        intents=[f'get {title.lower()} in Odisha',f'Odisha {title.lower()}'],synonyms=[title.lower(),'Odisha certificate'])
for key,title in [('guardianship','Guardianship Certificate'),('solvency','Solvency Certificate'),('ror','Certified Copy of RoR')]:
    add('od-'+key,title+' (Odisha)','igod-5e885f0e333d5206','Odisha','Revenue and Disaster Management Department, Government of Odisha',od,
        reason='Current portal lists this distinct service. Scope, conditional evidence and current application entry need individual review.')

# Puducherry procedure PDFs have apparently mismatched generic document lists.
# Do not repeat those lists as certificate-specific requirements.
for key,title in [('income','Income Certificate'),('residence','Residence Certificate')]:
    py=evidence('py-'+key,'Revenue and Disaster Management Department, Puducherry','Departmental certificate procedure and fee statement.','Steps I-IV; document appendix not adopted because it appears generic/mismatched')
    add('py-'+key+'-certificate',title+' (Puducherry)','igod-7c7865e8e6982bc1','Puducherry','Revenue and Disaster Management Department, Government of Puducherry',py,publish=True,
        procedure=['The departmental PDF describes eDistrict registration, selecting the Revenue service, submitting the current form and retaining the acknowledgement; approval follows official verification.'],
        fees='The procedure PDF states no certificate fee; other channel charges have not been verified.',
        reason='The PDF names edistrict.py.gov.in, but normal TLS retrieval failed (curl 35). Information only. The document appendix appears mismatched; eligibility and document requirements remain unknown rather than using a guessed checklist.')

# HP's old hostname redirects to HimSeva. The new current list confirms names,
# but per-service checklists/authenticated entries remain unreviewed.
hp=evidence('himachal-current-list','HimSeva, Government of Himachal Pradesh','Current departmental service list at the redirected portal.','Revenue; Municipal Corporation Shimla / Shimla Jal Prabandhan Nigam sections')
for key,title in [('income','Income Certificate'),('bonafide','Bonafide Himachali Certificate'),('caste','Caste (SC/ST) Certificate'),('legal-heir','Legal Heirs Certificate'),('unemployment','Un-Employment Certificate')]:
    add('hp-'+key,title+' (Himachal Pradesh)','igod-ffa4189a2ac66991','Himachal Pradesh','Revenue Department, Government of Himachal Pradesh',hp,
        reason='Old eDistrict link redirects to HimSeva; the current list confirms this operation. Service-specific checklist, conditions and public application entry need review.')
for key,title in [('water-connection','Application for Water Connection'),('sewer-connection','Application for Sewerage Connection')]:
    add('shimla-'+key,title+' (Shimla)','igod-ffa4189a2ac66991','Himachal Pradesh','Municipal Corporation Shimla / Shimla Jal Prabandhan Nigam',hp,
        category='utilities',level='Local',district='Shimla',reason='The current list places this operation under Municipal Corporation Shimla / SJPNL. No service-specific requirements or application route reviewed; local candidate only, not statewide coverage.')

# Current e-PAN manual now includes consent-based DigiLocker DOB evidence.
# Do not carry forward older "no documents" descriptions.
ep=evidence('epan-manual','Income Tax Department','Current prerequisites and separate new-PAN and download/status operations.','Overview, prerequisites and sections 3.1-3.3')
add('instant-epan-new','Instant e-PAN: new allotment','income-tax-efiling',None,'Income Tax Department',ep,publish=True,category='taxation',
    eligibility='Selected prerequisites: an individual without an allotted PAN, not a minor or representative assessee; valid Aadhaar, linked mobile and DigiLocker access. Confirm all declarations in the current manual.',
    docs=['The current manual requires consent-based DigiLocker proof of date of birth from its prescribed alternatives; it is not a no-documents service.'],
    procedure=['From official e-Filing Quick Links choose Instant e-PAN and Get New e-PAN, then follow the current declarations and consent workflow on the government portals.'],
    fees='The manual describes new digitally signed e-PAN allotment as free of cost.',groups=['Individual taxpayers'],
    intents=['get a new instant e-PAN','apply for instant PAN'],synonyms=['instant epan','new PAN card'],
    reason='Manual is readable and reviewed; it provides navigation instructions without a directly observed transaction URL. Information only; no credentials or OTPs were entered.')
add('instant-epan-download','Instant e-PAN: check request or download','income-tax-efiling',None,'Income Tax Department',ep,publish=True,category='taxation',
    procedure=['From official e-Filing Quick Links choose Instant e-PAN, then Check Status / Download e-PAN. The manual describes Aadhaar-linked mobile authentication on the official portal.'],
    intents=['download my instant e-PAN','check instant PAN request'],synonyms=['epan download','instant PAN status'],
    reason='A distinct operation for an existing Instant e-PAN request. Public transaction URL not confirmed; information only. JanSetu does not retrieve status or collect Aadhaar/OTP.')
vf=evidence('voter-form6','Election Commission of India','New-elector application, evidence alternatives and declarations.','Form 6: sections 7-8, declaration and accessibility notes')
add('eci-new-elector-form6','New voter registration: Form 6','igod-85affa23dc47255b',None,'Election Commission of India / Electoral Registration Officer',vf,publish=True,
    description='Official Form 6 information for first inclusion in an electoral roll; the responsible Electoral Registration Officer decides the application.',
    docs=['Selected evidence: photograph, age evidence and ordinary-residence evidence from the form alternatives. The form includes alternatives/field verification where specified evidence is unavailable.'],
    intents=['register as a new voter','first voter registration'],synonyms=['Form 6','new voter ID'],
    reason='Readable official PDF reviewed; the voters portal response is a JavaScript shell, so no online Apply action or current enrollment-window claim is made.')
vf=evidence('voter-form8','Election Commission of India','Existing-elector correction, shifting, replacement and disability-marking choices.','Form 8: purpose selection, sections 1-4 and supporting-evidence instructions')
add('eci-elector-form8','Voter record changes: Form 8','igod-85affa23dc47255b',None,'Election Commission of India / Electoral Registration Officer',vf,publish=True,
    description='Official Form 8 information for an existing electoral record: choose the appropriate correction, shifting, replacement or disability-marking operation.',
    conditional=['Evidence depends on the selected operation: residence proof for shifting, supporting proof for corrections, and the stated replacement/disability evidence where applicable.'],
    intents=['correct my voter details','change voter address'],synonyms=['Form 8','voter ID correction','EPIC replacement'],
    reason='Readable official PDF reviewed. Individual operation requirements differ; no single mandatory checklist or confirmed online Apply route is inferred.')
vf=evidence('voter-form6a','Election Commission of India','Overseas-elector form and appended older guidance.','Form 6A and appended guidelines with a 2017 example and legacy NVSP links')
add('eci-overseas-form6a','Overseas voter registration: Form 6A','igod-85affa23dc47255b',None,'Election Commission of India / Electoral Registration Officer',vf,
    reason='Official PDF is readable but appended instructions use older qualifying-date examples and legacy NVSP links. Current prerequisites and application route need reconciliation; pending.')

assert len(ROWS)==54 and len({r['id'] for r in ROWS})==54
# Explicitly reviewed revision retains the original ID/operator and all existing
# nullable facts. Previous published queue decisions are not overwritten.
revision=copy.deepcopy(next(s for s in BASE['services']['services'] if s['id']=='pmegp-enterprise-support'))
pr=source('pmegp-route');notice=source('pmegp-intake-review')
revision['evidence'].append(evidence('pmegp-route','PMEGP / Ministry of MSME','Canonical portal and public intake notice; application availability must be rechecked.','Homepage; fresh-intake notice in public JavaScript (window.NewApplicationAccept)'))
revision.update(informationUrl=pr['url'],sourceUrl=pr['url'],linkEvidenceIds=['pmegp-route'],verificationStatus='needs_review',publicationStatus='draft',
    reviewNotes=revision['reviewNotes']+' Phase 3C: the canonical non-www host responds with valid TLS. Its public fresh-intake notice says new submissions are unavailable pending approval of continuation/revised guidelines. The www host still fails TLS. No application button or open-window claim is published.',
    applicationRouteEvidence={'status':'unconfirmed','checkedOn':DAY,'evidenceIds':['pmegp-route'],'notes':'Canonical homepage responds, but fresh-intake notice says submission is unavailable. A reachable homepage is not an active application route.'})
ROWS.append(revision);APPROVED.add(revision['id'])

reasons={
 'digilocker-fetch-issued':('DigiLocker homepage confirms fetching as a feature, but current FAQ retrieval is a JavaScript shell; issued-document workflow and conditional prerequisites still need readable operation-specific review.',['digilocker-home','digilocker-faq','digilocker-readable-faq']),
 'digilocker-share-issued':('Homepage describes sharing but current FAQ body is a JavaScript shell; operation-specific consent and issued-versus-uploaded distinctions remain unreviewed.',['digilocker-home','digilocker-faq']),
 'driving-licence-application':('Delhi-specific permanent/learner pages were retrieved but contain mixed/possibly old requirements. Sarathi and central FAQ retrieval failed (curl 56); no current state transaction entry/checklist confirmed.',['delhi-driving','delhi-learner','sarathi-route','driving-faq']),
 'pmegp-direct-application':('Canonical non-www host now passes normal TLS, while www fails curl 60. Public homepage notice says fresh intake is unavailable pending scheme-continuation approval. Retain pending; do not infer an active Apply route.',['pmegp-route','pmegp-route-www','pmegp-intake-review'])}
amendments=[]
for entry in BASE['serviceQueue']:
    if entry['id'] not in reasons or entry['status']!='NEEDS_REVIEW':continue
    reason,names=reasons[entry['id']]
    amendments.append(dict(id=entry['id'],previousSha256=candidate_digest(entry),reviewedBy='Codex',checkedOn=DAY,reason=reason,
        reviewEvidence=[{'researchFile':f'data/services/research/phase3c/{n}.json','url':source(n)['url']} for n in names]))
write(DIRECTORY/'phase3c-candidates.json',{'services':ROWS})
write(DIRECTORY/'phase3c-ledger.json',{'reviewedOn':DAY,'scope':'targeted local editorial research; no nationwide crawl','candidates':LEDGER,'existingCaseReviews':amendments})
write(DIRECTORY/'phase3c-pending-reviews.json',amendments)
write(DIRECTORY/'phase3c-approvals.json',dict(version=13,organizations=list(ORGS.values()),approvals={r['id']:{'reviewedBy':'Codex','reviewedOn':DAY,'candidateSha256':candidate_digest(r),
    'notes':'Reviewed retained official source text and linked public entry where confirmed. Selected facts only; unknown fields and route limitations retained. Local editorial approval, not government endorsement.'} for r in ROWS if r['id'] in APPROVED}))
print(f'Prepared {len(LEDGER)} new investigations, {len(APPROVED)-1} proposed new publications, one explicit PMEGP revision and {len(amendments)} pending-case reviews; no publication performed.')
