import copy,json,hashlib,unittest
from service_catalog import ROOT,PUBLISHED,read,validate_service,merged_catalog

class ServiceCatalogTest(unittest.TestCase):
    def setUp(self):
        self.website=read(ROOT/'app/src/main/assets/catalog.json');self.pilot=read(PUBLISHED)
    def test_actual_pilot_and_existing_websites_and_decisions_are_preserved(self):
        baseline=read(ROOT/'data/validation_reports/phase3a-baseline.json')
        self.assertEqual(baseline['catalogSha256'],hashlib.sha256((ROOT/'app/src/main/assets/catalog.json').read_bytes()).hexdigest())
        self.assertEqual(baseline['queueSha256'],hashlib.sha256((ROOT/'data/review_queue.json').read_bytes()).hexdigest())
        merged=merged_catalog(self.website)
        self.assertEqual(self.website['websites'],merged['websites']);self.assertEqual(52,len(merged['services']))
        baseline3c=read(ROOT/'data/validation_reports/phase3c-baseline.json')
        current={s['id']:s for s in merged['services']}
        for old in baseline3c['services']['services']:
            if old['id'] not in ('pmegp-enterprise-support', 'rti-central-request'):self.assertEqual(old,current[old['id']])
        queue=read(ROOT/'data/services/review_queue.json')
        for old in baseline3c['serviceQueue']:
            if old['status']=='PUBLISHED':self.assertIn(old,queue)
        self.assertEqual(31,sum(r['status']=='NEEDS_REVIEW' for r in queue))
    def test_service_gate_rejects_missing_facts_urls_or_publication(self):
        s=self.pilot['services'][0]
        mutations=[{'publicationStatus':'draft'},{'websiteId':'missing'},{'organizationId':'missing'},
            {'informationUrl':'http://example.gov.in/'},{'applicationUrl':'https://msme.gov.in.evil.example/'},
            {'evidence':[]},{'eligibility':{'text':'Unknown fact','evidenceIds':['absent']}},
            {'documents':[]},{'level':'State','state':None},{'verifiedOn':'2999-01-01'}]
        for mutation in mutations:
            with self.subTest(mutation=mutation),self.assertRaises((ValueError,KeyError)):
                validate_service({**s,**mutation},self.website,self.pilot['organizations'])
    def test_unknown_facts_are_null_and_not_no_documents_or_open_eligibility(self):
        for row in self.pilot['services']:validate_service(row,self.website,self.pilot['organizations'])
        self.assertTrue(any(s['eligibility'] is None and s['documents'] is None for s in self.pilot['services']))
        for s in self.pilot['services']:
            if s['applicationMode']!='online':self.assertIsNone(s['applicationUrl'])

if __name__=='__main__':unittest.main()
