import copy,json,subprocess,sys,tempfile,unittest
from pathlib import Path
from unittest.mock import patch
import service_catalog as sc

class Phase3CRegressionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.catalog=sc.read(sc.ROOT/'app/src/main/assets/catalog.json');cls.pilot=sc.read(sc.PUBLISHED)
        cls.baseline=sc.read(sc.ROOT/'data/validation_reports/phase3c-baseline.json')
    def sample(self):return copy.deepcopy(next(s for s in self.pilot['services'] if s['id']=='dl-income-certificate'))
    def validate(self,s):sc.validate_service(s,self.catalog,self.pilot['organizations'])
    def test_raw_website_directory_and_backlog_bytes_preserved(self):
        import hashlib
        for path in ['app/src/main/assets/catalog.json','data/review_queue.json']:
            self.assertEqual(self.baseline['sha256'][path],hashlib.sha256((sc.ROOT/path).read_bytes()).hexdigest())
    def test_all_new_published_facts_and_route_reviews_have_provenance(self):
        old={s['id'] for s in self.baseline['services']['services']}
        new=[s for s in self.pilot['services'] if s['id'] not in old]
        self.assertEqual(27,len(new))
        for row in new:
            self.validate(row);self.assertIsNotNone(row.get('applicationRouteEvidence'))
            for e in row['evidence']:self.assertRegex(e['contentSha256'],r'^[a-f0-9]{64}$')
        bad=self.sample();bad['beneficiaryGroups']=[{'text':'Invented audience','evidenceIds':['absent']}]
        with self.assertRaises(ValueError):self.validate(bad)
    def test_unknown_and_partial_requirements_never_become_empty_or_guaranteed(self):
        rows={s['id']:s for s in self.pilot['services']}
        self.assertIsNone(rows['py-income-certificate']['documents'])
        self.assertIsNone(rows['od-ews-certificate']['eligibility'])
        self.assertIn('partial',rows['kl-income-certificate']['reviewNotes'].lower())
        bad=self.sample();bad['documents']=[]
        with self.assertRaises(ValueError):self.validate(bad)
    def test_duplicate_identity_rejected_but_same_name_in_another_state_allowed(self):
        row=self.sample();other=copy.deepcopy(row);other['id']='second-copy';other['title']=row['title'].upper()+'!'
        with self.assertRaises(ValueError):sc.validate_collection([row,other],self.catalog,self.pilot['organizations'])
        kl=next(s for s in self.pilot['services'] if s['id']=='kl-income-certificate')
        other.update(state='Kerala',websiteId=kl['websiteId'],organizationId=kl['organizationId'])
        sc.validate_collection([row,other],self.catalog,self.pilot['organizations'])
    def test_ambiguous_candidates_cannot_publish(self):
        for key in ('possibleDuplicateOf','duplicateOf'):
            row=self.sample();row[key]='dl-domicile-certificate'
            with self.assertRaises(ValueError):self.validate(row)
    def test_invalid_redirected_unconfirmed_and_http_only_routes_rejected(self):
        for url in ['https://example.gov.in/ bad','https://example.gov.in/\npath','https://example.gov.in.evil.test/','https://user@example.gov.in/','http://example.gov.in/']:
            self.assertFalse(sc.official_url(url))
        self.assertTrue(sc.official_url('https://example.gov.in/my%20file.pdf'))
        for change in [{'status':'unconfirmed'},{'tlsVerified':False},{'httpStatus':503},{'entryReviewed':False},{'linkedFromSource':False},
                       {'finalUrl':'https://elsewhere.gov.in/login'},{'finalUrl':'https://evil.test/','redirectReviewed':True}]:
            row=self.sample();row['applicationRouteEvidence'].update(change)
            with self.subTest(change=change),self.assertRaises(ValueError):self.validate(row)
        row=self.sample();row['applicationRouteEvidence'].update(finalUrl='https://elsewhere.gov.in/login',redirectReviewed=True)
        self.validate(row) # An explicit editorial redirect review is required.
    def test_pending_amendment_is_digest_bound_idempotent_and_retains_original(self):
        amendments=sc.read(sc.DIRECTORY/'phase3c-pending-reviews.json');original=self.baseline['serviceQueue']
        amended=sc.amend_pending(original,amendments)
        self.assertEqual(amended,sc.amend_pending(amended,amendments));self.assertEqual(original,self.baseline['serviceQueue'])
        for a in amendments:
            q=next(q for q in amended if q['id']==a['id'] and q['status']=='NEEDS_REVIEW')
            prior=next(q for q in original if q['id']==a['id'])
            self.assertEqual(prior,q['history'][0]['previous']);self.assertEqual('NEEDS_REVIEW',q['status'])
        bad=copy.deepcopy(amendments);bad[0]['previousSha256']='0'*64
        with self.assertRaises(ValueError):sc.amend_pending(original,bad)
    def test_import_publish_replay_preserves_decisions_and_all_bytes(self):
        with tempfile.TemporaryDirectory() as tmp:
            published=Path(tmp)/'catalog.json';queue=Path(tmp)/'queue.json'
            sc.write(published,self.pilot);sc.write(queue,sc.read(sc.QUEUE));before=(published.read_bytes(),queue.read_bytes())
            with patch.object(sc,'PUBLISHED',published),patch.object(sc,'QUEUE',queue):
                for args in [['import',str(sc.DIRECTORY/'phase3c-candidates.json'),'--revision'],['publish',str(sc.DIRECTORY/'phase3c-approvals.json')]]:
                    with patch.object(sys,'argv',['service_catalog.py']+args):sc.main()
            self.assertEqual(before,(published.read_bytes(),queue.read_bytes()))
    def test_failed_publication_does_not_partially_mutate_either_catalog_or_queue(self):
        with tempfile.TemporaryDirectory() as tmp:
            p=Path(tmp)/'catalog.json';q=Path(tmp)/'queue.json';a=Path(tmp)/'approvals.json'
            row=self.sample();row['id']='semantic-copy';row.update(publicationStatus='draft',verificationStatus='needs_review')
            sc.write(p,self.pilot);sc.write(q,[{'id':row['id'],'status':'NEEDS_REVIEW','candidate':row}])
            sc.write(a,{'version':14,'organizations':self.pilot['organizations'],'approvals':{row['id']:{'reviewedBy':'Test','notes':'Duplicate must fail','candidateSha256':sc.candidate_digest(row)}}})
            before=(p.read_bytes(),q.read_bytes())
            with patch.object(sc,'PUBLISHED',p),patch.object(sc,'QUEUE',q),patch.object(sys,'argv',['service_catalog.py','publish',str(a)]),self.assertRaises(ValueError):sc.main()
            self.assertEqual(before,(p.read_bytes(),q.read_bytes()))
    def test_ledger_counts_evidence_and_jurisdictions_do_not_inflate_publication(self):
        ledger=sc.read(sc.DIRECTORY/'phase3c-ledger.json')['candidates'];published={s['id'] for s in self.pilot['services']}
        self.assertEqual(54,len(ledger));self.assertEqual(27,sum(r['id'] in published for r in ledger))
        self.assertEqual(2,sum(r['level']=='Local' for r in ledger));self.assertFalse(any(r['level']=='Local' and r['id'] in published for r in ledger))
        for r in ledger:
            for e in r['sources']:
                d=sc.read(sc.ROOT/e['researchFile']);self.assertEqual(d['sha256'],e['sha256']);self.assertTrue(d['success'])

if __name__=='__main__':unittest.main()
