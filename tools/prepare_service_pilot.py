"""Recreate the reviewed Phase 3A candidate/approval files; does not publish them.

Run after reviewing the official sources. Publication is a separate explicit CLI step.
"""
import hashlib,json
from pathlib import Path
from service_catalog import ROOT,DIRECTORY,read,write

DAY='2026-10-16'
web=read(ROOT/'app/src/main/assets/catalog.json');sites={s['id']:s for s in web['websites']}
sources={
 'nsp':('https://scholarships.gov.in/All-Scholarships','National Scholarship Portal','Current AICTE scheme list','nsp-list'),
 'pragati':('https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2010_G.pdf','AICTE','Sections 2, 6, 10 and renewals; July 2020 guidelines linked by current NSP','pragati'),
 'saksham':('https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2012_G.pdf','AICTE','Sections 2, 6, 7 and 8; July 2020 guidelines linked by current NSP','saksham'),
 'swanath':('https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_3039_G.pdf','AICTE','May 2025 guidelines, sections 2, 7 and 10','swanath'),
 'tn':('https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx','Tamil Nadu e-Governance Agency','Named service row in official eSevai centre list','service-list-tn'),
 'tn-community':('https://serviceonline.gov.in/configuretn/login.do','Tamil Nadu e-Governance Agency / ServicePlus','Description, access steps and service-specific documents','community-tn'),
 'ncs':('https://ncs.gov.in/faq','Directorate General of Employment','Jobseeker navigation and general FAQ','ncs'),
 'ncs-register':('https://ncs.gov.in/assets/pdf/Jobseeker%20registration%20flowchart%201.pdf','Directorate General of Employment','One-page jobseeker registration flowchart',None),
 'uidai':('https://www.uidai.gov.in/en/my-aadhaar','UIDAI','Named My Aadhaar service card; access requires official authentication','uidai'),
 'cpgrams':('https://pgportal.gov.in/Home/Faq','DARPG','FAQ 3, 10 and 15','cpgrams'),
 'rti':('https://rtionline.gov.in/guidelines.php?request','Department of Personnel and Training','Guidelines 1–2, 6, 12, 16–18','rti'),
 'udyam':('https://udyamregistration.gov.in/','Ministry of MSME','New registration, Must Follow and Must Know sections',None),
 'pmegp':('https://www.pib.gov.in/newsite/archiveContent.aspx?lang=2&reg=48&relid=286631','Press Information Bureau / Ministry of MSME','23 March 2026 official PMEGP release',None)
}
def evidence(key,summary):
    url,publisher,locator,research=sources[key]
    row={'id':key,'url':url,'publisher':publisher,'checkedOn':DAY,'summary':summary,'locator':locator}
    if research:
        snapshot=read(DIRECTORY/'research'/f'{research}.json')
        if snapshot:row['contentSha256']=snapshot['sha256']
    return row
def fact(text,*keys):return {'text':text,'evidenceIds':list(keys)}
organizations={};rows=[]
def add(id,website,title,description,categories,department,ev,phrases,synonyms,application=None,mode=None,state=None,**facts):
    site=sites[website];org='org-'+hashlib.sha256(site['organization'].encode()).hexdigest()[:16]
    organizations.setdefault(org,{'id':org,'name':site['organization'],'websiteIds':[]})
    if website not in organizations[org]['websiteIds']:organizations[org]['websiteIds'].append(website)
    notes=f'Official service information reviewed by Codex on {DAY}; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null.'
    row={'id':id,'websiteId':website,'organizationId':org,'title':title,'description':description,'categories':categories,'department':department,
         'level':'State' if state else 'Central','state':state,'district':None,'citizenIntents':phrases,'synonyms':synonyms,
         'informationUrl':ev[0]['url'],'sourceUrl':ev[0]['url'],'applicationUrl':application,'applicationMode':mode or ('online' if application else 'information'),
         'evidence':ev,'linkEvidenceIds':[e['id'] for e in ev],'verifiedOn':DAY,'verificationStatus':'candidate','publicationStatus':'draft','reviewNotes':notes,
         **{k:None for k in ('eligibility','documents','conditionalDocuments','procedure','fees','deadline','processing')},**facts}
    rows.append(row);return row

for key,title,eligibility,documents in [
 ('pragati','AICTE Pragati Scholarship for Girl Students (Degree)','Selected conditions: girls entering year one, or year two through lateral entry, of an AICTE-approved degree; family income up to INR 8 lakh; at most two girls per family. Read the guidelines for exclusions.', 'State/UT income certificate.'),
 ('saksham','AICTE Saksham Scholarship for Specially-Abled Students (Degree)','Selected conditions: AICTE-approved degree admission in year one or lateral entry in year two; disability at least 40%; family income up to INR 8 lakh. Other conditions apply.', 'State/UT income certificate.'),
 ('swanath','AICTE Swanath Scholarship (Technical Degree)','Qualifying family circumstances include orphanhood, parental death due to COVID-19, a parent martyred in specified forces, or the specified critical illness/accident category. Income up to INR 8 lakh, regular AICTE-approved study and no other government/AICTE scholarship are also required.', 'Institution bonafide certificate and applicable income evidence; see category-specific checklist.')]:
    s=add(f'aicte-{key}-degree','national-scholarship-portal',title,'Scholarship support for eligible technical degree students.',['education'],'All India Council for Technical Education',
        [evidence(key,'Degree scholarship conditions and application procedure.'),evidence('nsp','The degree scheme is listed on the current NSP page.')],
        ['I need a scholarship for engineering',f'apply for {key} scholarship'],[key,'engineering scholarship','technical education','student financial assistance'],
        application='https://scholarships.gov.in/',eligibility=fact(eligibility,key),documents=[fact(documents,key)],
        procedure=[fact('Use the NSP student application process for this scheme; institution and State/UT scrutiny follow.',key)])
    s['reviewNotes']+=' Guideline publication year differs from review date; it remains linked on current NSP. Reconfirm the applicable academic year and complete conditions. Document list is partial.'

tn='igod-2715637a8f399700'
regional=[
 ('tn-community-certificate','Community Certificate (Tamil Nadu)','Community certification.','Revenue Administration',['community certificate','caste certificate','जाति प्रमाणपत्र']),
 ('tn-nativity-certificate','Nativity Certificate (Tamil Nadu)','Nativity certification.','Revenue Administration',['nativity certificate','residence origin']),
 ('tn-income-certificate','Income Certificate (Tamil Nadu)','Income certification.','Revenue Administration',['income certificate','earnings proof']),
 ('tn-birth-certificate-print','Printing of Birth Certificate (Tamil Nadu)','Print an existing birth record.','Directorate of Public Health',['birth certificate','birth record copy','जन्म प्रमाणपत्र']),
 ('tn-old-age-pension','Indira Gandhi National Old Age Pension Scheme — Tamil Nadu access','Old-age pension application access.','Revenue Administration',['pension','old age pension','senior citizen support']),
 ('tn-widow-pension','Destitute Widow Pension Scheme (Tamil Nadu)','Widow pension application access.','Revenue Administration',['widow pension','pension benefits']),
 ('tn-disability-pension','Differently Abled Pension Scheme (Tamil Nadu)','Disability pension application access.','Revenue Administration',['disability pension','differently abled benefits']),
 ('tn-unemployment-certificate','Unemployment Certificate (Tamil Nadu)','Unemployment certification.','Revenue Administration',['unemployment certificate','jobless certificate']),
 ('tn-legal-heir-certificate','Legal Heir Certificate (Tamil Nadu)','Legal heir certification.','Revenue Administration',['legal heir certificate','succession proof'])
]
for id,title,desc,dept,terms in regional:
    key='tn-community' if id in ('tn-community-certificate','tn-nativity-certificate') else 'tn'
    row=add(id,tn,title,desc,['welfare'] if 'pension' in id else ['documents'],f'{dept}, Government of Tamil Nadu',
        [evidence(key,terms[0]+' service.')],['How can I get a caste certificate' if id=='tn-community-certificate' else 'How do I obtain a birth certificate' if 'birth' in id else 'I want to apply for a pension' if 'pension' in id else 'apply for '+terms[0]],
        terms,mode='assisted',state='Tamil Nadu')
    if key=='tn-community':
        row['procedure']=[fact('Register/login on the official service platform and select the relevant service.',key)]
        row['documents']=[fact('Applicant photo, address evidence and self-declaration.',key)]
        row['conditionalDocuments']=[fact('Community: a family member’s certificate; nativity: birth or residence-history evidence, as applicable.',key)]
    row['reviewNotes']+=' Centre-assisted listing; no direct citizen application URL or complete eligibility checklist confirmed. Fees and processing-time columns may differ by service and are not reproduced. Birth printing is not birth registration.'

add('ncs-government-job-search','national-career-service','Search government-sector jobs on NCS','Find published government-sector job listings; this is not a vacancy or selection guarantee.',['jobs'],'Directorate General of Employment, Ministry of Labour & Employment',
    [evidence('ncs','Government-jobs search is linked in jobseeker navigation.')],['Find government jobs','search govt jobs'],['government jobs','naukri','recruitment vacancies'],
    application='https://ncs.gov.in/job-listing?isGovernmentJob=true',procedure=[fact('Open government-sector search and inspect the employer’s vacancy notice.','ncs')])
add('ncs-jobseeker-registration','national-career-service','NCS Jobseeker Registration','Create a jobseeker profile on the official employment platform.',['jobs'],'Directorate General of Employment, Ministry of Labour & Employment',
    [evidence('ncs-register','Official jobseeker registration flowchart.'),evidence('ncs','Jobseeker registration is provided.')],['register as a jobseeker'],['employment registration','ncs account'],application='https://ncs.gov.in/',
    procedure=[fact('Choose jobseeker registration; follow the official flowchart.','ncs-register')])
add('ncs-career-counselling','national-career-service','Find a Career Counsellor through NCS','Explore official career-counselling access.',['jobs'],'Directorate General of Employment, Ministry of Labour & Employment',
    [evidence('ncs','Counsellor search is linked under jobseeker support.')],['find career counselling'],['career advice','counsellor'],application='https://ncs.gov.in/career-counsellors')

for id,title,desc,terms in [
 ('aadhaar-download','Download Aadhaar','Retrieve an electronic Aadhaar copy.',['download aadhaar','e aadhaar']),
 ('aadhaar-address-update','Aadhaar Address Update','Request an address change.',['aadhaar address update','change address']),
 ('aadhaar-pvc-card','Order Aadhaar PVC Card','Request a durable Aadhaar card.',['aadhaar pvc card','order aadhaar']),
 ('aadhaar-biometric-lock','Lock or Unlock Aadhaar Biometrics','Manage biometric authentication locking.',['aadhaar biometric lock','unlock fingerprint'])]:
    row=add(id,'uidai',title,desc,['documents'],'Unique Identification Authority of India',[evidence('uidai',terms[0]+' is listed.')],terms,terms,
        eligibility=fact('For Aadhaar holders; the listed online flow requires mobile OTP.','uidai'))
    row['reviewNotes']+=' UIDAI currently links a beta-branded service host. Direct application endpoint is held for route review; open the official information page and use its current service link. No OTP is entered into JanSetu.'

for id,title,desc,terms,elig in [
 ('cpgrams-public-grievance','Lodge a Public Grievance through CPGRAMS','Submit a grievance to the relevant public authority.',['file a complaint','lodge grievance'],'Excluded matters include RTI requests, court/subjudice matters, and personal/family disputes.'),
 ('cpgrams-grievance-appeal','Appeal a Dissatisfied CPGRAMS Grievance','Use the appeal process for eligible disposed central-government grievances.',['appeal grievance','unsatisfied complaint'],'The central grievance appeal provision follows mandatory feedback on disposal; see the official FAQ.')]:
    add(id,'cpgrams',title,desc,['grievances'],'Department of Administrative Reforms and Public Grievances',[evidence('cpgrams','Grievance submission, exclusions and appeal route.')],terms,terms,
        application='https://pgportal.gov.in/Home/LodgeGrievance' if 'public' in id else 'https://pgportal.gov.in/',eligibility=fact(elig,'cpgrams'))

for id,title,desc,terms in [
 ('rti-central-request','Submit an RTI Request to a Central Public Authority','Request government information.',['right to information','rti request']),
 ('rti-first-appeal','Submit an RTI First Appeal','Appeal an online RTI request.',['rti first appeal','information appeal'])]:
    row=add(id,'rti-online',title,desc,['transparency'],'Department of Personnel and Training',[evidence('rti','Online central RTI request and first-appeal instructions.')],terms,terms,application='https://rtionline.gov.in/',
        eligibility=fact('Indian citizens requesting information from covered Government of India ministries/departments.','rti'))
    if id=='rti-first-appeal':row['fees']=fact('No fee for the first appeal.','rti');row['procedure']=[fact('Choose Submit First Appeal and reference the original registration number.','rti')]
    else:row['conditionalDocuments']=[fact('BPL fee exemption requires the appropriate government-issued certificate. Do not upload Aadhaar, PAN or other identity documents.','rti')]

add('udyam-new-registration','udyam-registration','Udyam Registration for a New MSME','Register an eligible micro, small or medium enterprise.',['business'],'Ministry of Micro, Small and Medium Enterprises',[evidence('udyam','Free paperless registration and official new-registration route.')],
    ['register my small business','msme registration'],['udyam','business registration'],application='https://udyamregistration.gov.in/',
    documents=[fact('Registration requires no document uploads; Aadhaar and applicable PAN/GST details are handled by the official portal.','udyam')],fees=fact('Registration is free.','udyam'))

msme=next(s['id'] for s in web['websites'] if s['url'].startswith(('https://msme.gov.in/','https://www.msme.gov.in/')))
row=add('pmegp-enterprise-support',msme,'Prime Minister’s Employment Generation Programme (PMEGP)','Bank-linked margin-money subsidy for eligible new non-farm micro enterprises.',['business'],'Ministry of MSME / Khadi and Village Industries Commission',[evidence('pmegp','Official release confirms the enterprise-support programme.')],
    ['I need a subsidy for my small business','financial support for new micro enterprise'],['pmegp','business subsidy','small business subsidy'])
row['reviewNotes']+=' Information-only record. A candidate application host failed TLS verification on this machine, so no application button is published. Current eligibility, documents, funding window and route require further review.'

assert len(rows)==25
if __name__=='__main__':
    pending=[{'id':'digilocker-fetch-issued','reason':'FAQ body not readable in live retrieval; fresh service evidence needed.'},
             {'id':'digilocker-share-issued','reason':'Fresh operation-specific documentation not obtained.'},
             {'id':'driving-licence-application','reason':'State-specific application flow and document requirements not reviewed.'},
             {'id':'pmegp-direct-application','reason':'Candidate new application host failed normal certificate validation; no bypass.'}]
    write(DIRECTORY/'pilot_candidates.json',{'services':rows})
    approvals={s['id']:{'reviewedBy':'Codex official-source review (not a human verification claim)','notes':s['reviewNotes'],
        'candidateSha256':hashlib.sha256(json.dumps(s,sort_keys=True,ensure_ascii=False).encode()).hexdigest()} for s in rows}
    write(DIRECTORY/'pilot_approvals.json',{'version':10,'organizations':list(organizations.values()),'approvals':approvals})
    queue=read(DIRECTORY/'review_queue.json',[]);known={r['id'] for r in queue}
    for row in pending:
        if row['id'] not in known:queue.append({**row,'status':'NEEDS_REVIEW'})
    write(DIRECTORY/'review_queue.json',queue)
    print('Prepared 25 service candidates and explicit source-review approvals; four unresolved routes/services retained separately.')
