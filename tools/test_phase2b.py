import copy
import hashlib
import json
import tempfile
import unittest
from datetime import date, datetime, timedelta, timezone
from pathlib import Path
from unittest.mock import patch
from administrative_coverage import parse_admin, match_unit, state_key, coverage
from catalog_pipeline import verify_source_snapshot, validate_directory_eligibility, publish, stage_discovery
from government_sources import IgodAdapter, normalize_url
from review_queue import safe_redirect, same_organization, triage, source_changes

ROOT = Path(__file__).resolve().parents[1]


class ReviewRulesTest(unittest.TestCase):
    def setUp(self):
        self.row = {"id": "one", "title": "District Office", "organization": "District Office", "url": "https://example.gov.in/",
            "sourceId": "igod", "publicationScope": "local_development", "state": "Goa", "sourceStates": ["Goa"],
            "governmentLevel": "district", "reviewStatus": "DISCOVERED", "healthStatus": "RESPONDING",
            "sourceUrl": "https://igod.gov.in/organization/one", "sourceRetrievedOn": "2026-10-14",
            "sourceEvidence": [{"sourceId": "igod", "originalName": "District Office", "originalUrl": "https://example.gov.in/",
                "sourcePage": "https://igod.gov.in/sg/GA/E042/organizations", "retrievedAt": "2026-10-14", "preview": False}],
            "destinationHealth": {"status": "RESPONDING", "checkedAt": "2026-10-14T12:00:00+00:00", "httpStatus": 200,
                "finalUrl": "https://example.gov.in/", "redirects": [], "robotsChecks": [{"resolvedRules": True, "allowed": True}]}}
        self.item = {"record": self.row, "status": "DISCOVERED", "collectionManaged": True, "reviewHistory": [{"status": "DISCOVERED"}]}
        self.catalog = {"version": 1, "categories": [], "websites": []}
        self.date = datetime(2026,10,15,tzinfo=timezone.utc)

    def run_triage(self, item=None, catalog=None, groups=None):
        return triage({"records": [copy.deepcopy(item or self.item)]}, catalog or self.catalog, groups or [], {"Goa"}, {}, self.date)

    def test_redirect_chain_cannot_hide_cross_domain_hop_or_scheme_downgrade(self):
        h = copy.deepcopy(self.row["destinationHealth"])
        h.update(finalUrl="https://www.example.gov.in/home", redirects=[{"from":self.row["url"],"to":"https://www.example.gov.in/home","httpStatus":301}])
        self.assertTrue(safe_redirect(h,self.row["url"]))
        for end in ("http://example.gov.in/home", "https://evil.gov.in/home", "https://example.gov.in.evil.test/home"):
            bad=copy.deepcopy(h);bad["redirects"][0]["to"]=end
            self.assertFalse(safe_redirect(bad,self.row["url"]))
        h["redirects"][0]["from"]="https://example.gov.in/missing-hop"
        self.assertFalse(safe_redirect(h,self.row["url"]))

    def test_exact_duplicate_resolved_but_similar_name_is_not_merged(self):
        site={**self.row,"id":"published"}
        result, approvals, archive, decisions=self.run_triage(catalog={**self.catalog,"websites":[site]})
        self.assertEqual([],result["records"]);self.assertEqual("published",archive[0]["triage"]["duplicateOf"])
        self.assertEqual({},approvals)
        other={**site,"title":"District Office Two","organization":"District Office Two"}
        self.assertFalse(same_organization(self.row,other))
        result,_,archive,decisions=self.run_triage(catalog={**self.catalog,"websites":[other]})
        self.assertEqual([],archive);self.assertEqual("LIKELY_DUPLICATE",decisions[0]["bucket"])

    def test_authentication_convergence_and_www_guess_are_not_duplicate_proof(self):
        site={**self.row,"id":"published","url":"https://www.example.gov.in/"}
        with patch("review_queue.validate_directory_eligibility"):
            _,_,archive,_=self.run_triage(catalog={**self.catalog,"websites":[site]})
        self.assertEqual([],archive)
        h=copy.deepcopy(self.item);h["record"]["destinationHealth"].update(finalUrl="https://example.gov.in/login",redirects=[{"from":self.row["url"],"to":"https://example.gov.in/login","httpStatus":302}])
        site={**site,"observedFinalUrl":"https://example.gov.in/login"}
        with patch("review_queue.validate_directory_eligibility"):
            _,_,archive,_=self.run_triage(h,{**self.catalog,"websites":[site]})
        self.assertEqual([],archive)

    def test_shared_url_organizations_and_missing_district_state_stay_pending(self):
        item=copy.deepcopy(self.item)
        item["record"]["sourceEvidence"].append({"originalName":"Another Organization"})
        item["record"]["sourceOrganizationIds"]=["a","b"]
        _, approvals, _, decisions=self.run_triage(item)
        self.assertEqual({},approvals);self.assertTrue(decisions[0]["unresolvedConflicts"])
        item=copy.deepcopy(self.item);item["record"].update(state=None,district="North Goa")
        _, approvals, _, decisions=self.run_triage(item)
        self.assertEqual({},approvals);self.assertEqual("OWNERSHIP_OPERATOR_REVIEW",decisions[0]["bucket"])

    def test_domain_and_response_do_not_override_editor_ownership_or_rejection(self):
        for status in ("NEEDS_OWNERSHIP_REVIEW","NEEDS_URL_REVIEW","REJECTED","RESOLVED_DUPLICATE"):
            item={**self.item,"status":status,"reason":"Existing editor decision","reviewHistory":[{"status":status}]}
            with patch("review_queue.validate_directory_eligibility") as gate:
                q,approvals,_,_=self.run_triage(item)
            self.assertEqual(status,q["records"][0]["status"]);self.assertEqual({},approvals);gate.assert_not_called()

    def test_network_403_429_500_tls_and_404_are_inconclusive_not_rejected(self):
        for status in ("NETWORK_ERROR","ACCESS_DENIED","SERVER_ERROR","TLS_ERROR","HTTP_ERROR","ROBOTS_UNRESOLVED"):
            item=copy.deepcopy(self.item);item["record"]["destinationHealth"]["status"]=status
            q,approvals,_,d=self.run_triage(item)
            self.assertEqual("INCONCLUSIVE",d[0]["bucket"]);self.assertEqual({},approvals)
            self.assertEqual("TEMPORARILY_UNREACHABLE",q["records"][0]["status"])

    def test_ready_is_local_listing_only_and_repeat_triage_is_identical(self):
        with patch("review_queue.validate_directory_eligibility"):
            q,approvals,_,d=self.run_triage()
            q2,a2,_,_=triage(q,self.catalog,[],{"Goa"},{},self.date)
        self.assertEqual(q,q2);self.assertEqual(approvals,a2)
        self.assertEqual("LOCAL_DIRECTORY_ONLY",d[0]["publicationEligibility"])
        self.assertNotEqual("verified",d[0]["ownershipConfidence"])
        item=copy.deepcopy(self.item);item["record"]["destinationHealth"]["status"]="SERVER_ERROR"
        q,_,_,_=self.run_triage(item)
        q2,_,_,_=triage(q,self.catalog,[],{"Goa"},{},self.date)
        self.assertEqual(q,q2)

    def test_likely_duplicate_group_blocks_admission_and_preserves_history(self):
        q,approvals,archive,d=self.run_triage(groups=[{"websiteIds":["one","two"]}])
        self.assertEqual({},approvals);self.assertEqual([],archive)
        self.assertEqual("LIKELY_DUPLICATE",d[0]["bucket"])
        self.assertEqual({"status":"DISCOVERED"},q["records"][0]["reviewHistory"][0])

    def test_source_disappearances_and_changed_urls_never_delete_input(self):
        old=[{"id":"a","url":"https://a.gov.in/","sourceOrganizationIds":["org"]}]
        new=[{"id":"b","url":"https://b.gov.in/","sourceOrganizationIds":["org"]}]
        changes=source_changes(old,new)
        self.assertEqual(1,len(changes["notSeen"]));self.assertEqual(1,len(changes["changedOrganizationUrlSets"]))
        self.assertEqual("https://a.gov.in/",old[0]["url"])
        self.assertEqual({"added":[],"notSeen":[],"changedOrganizationUrlSets":[],"changedNames":[]},source_changes(old,old))

    def test_archived_alias_stays_resolved_on_import_but_changed_redirect_reopens_review(self):
        row=copy.deepcopy(self.row);row.update(firstDiscovered='2026-10-14', reviewStatus='DISCOVERED')
        target={**row,'id':'published','url':'https://www.example.gov.in/'}
        row['destinationHealth'].update(finalUrl=target['url'],redirects=[{'from':row['url'],'to':target['url'],'httpStatus':301}])
        alias={'url':row['url'],'targetId':'published','observedFinalUrl':target['url'],'title':row['title']}
        q={'records':[], 'resolvedAliases':[alias]}
        catalog={**self.catalog,'websites':[target]}
        q,approvals,counts=stage_discovery([row],catalog,q)
        self.assertEqual([],q['records']);self.assertEqual(1,counts['resolvedAliasesRetained']);self.assertEqual({},approvals)
        row['destinationHealth']['finalUrl']='https://other.gov.in/'
        q,approvals,_=stage_discovery([row],catalog,q)
        self.assertEqual('NEEDS_URL_REVIEW',q['records'][0]['status']);self.assertEqual({},approvals)

    def test_latest_failed_check_keeps_valid_listing_and_manual_record(self):
        site={**self.row,'verificationStatus':'directory_listed','healthCheckedAt':'2026-10-14T12:00:00+00:00','reviewHistory':[]}
        manual={**copy.deepcopy(site),'id':'curated','url':'https://curated.gov.in/','verificationStatus':'verified'}
        old_manual=copy.deepcopy(manual)
        catalog={**self.catalog,'websites':[site,manual]}
        row=copy.deepcopy(self.row);row.update(healthStatus='SERVER_ERROR',firstDiscovered='2026-10-14')
        row['destinationHealth'].update(status='SERVER_ERROR',checkedAt='2026-10-15T12:00:00+00:00',reason='HTTP 503')
        stage_discovery([row],catalog,{'records':[]})
        self.assertEqual('SERVER_ERROR',site['latestHealthStatus']);self.assertEqual('RESPONDING',site['healthStatus'])
        self.assertEqual(old_manual,manual);self.assertEqual(2,len(catalog['websites']))
        before=copy.deepcopy(catalog)
        stage_discovery([row],catalog,{'records':[]})
        self.assertEqual(before,catalog)

    def test_new_admission_requires_a_recent_observation_without_resetting_evidence_date(self):
        for days in (31,-1):
            row=copy.deepcopy(self.row);row['destinationHealth']['checkedAt']=(date.today()-timedelta(days=days)).isoformat()
            original=row['destinationHealth']['checkedAt']
            with patch('catalog_pipeline.verify_source_snapshot'), self.assertRaisesRegex(ValueError,'admission window'):
                validate_directory_eligibility(row)
            self.assertEqual(original,row['destinationHealth']['checkedAt'])


class SourceEvidenceTest(unittest.TestCase):
    def test_fragments_are_same_response_but_false_url_and_name_evidence_fail(self):
        fixture=(ROOT/'tools/fixtures/igod/ministries.html').read_bytes()
        entry=IgodAdapter.parse(fixture.decode('utf-8'),'https://igod.gov.in/ug/E002/organizations')['entries'][0]
        with tempfile.TemporaryDirectory(dir=ROOT/'data/collection') as directory:
            path=Path(directory)/'source.html';path.write_bytes(fixture)
            path.with_suffix('.json').write_text(json.dumps({'status':'OK','httpStatus':200,'url':'https://igod.gov.in/ug/E002/organizations','sha256':hashlib.sha256(fixture).hexdigest()}))
            e={'snapshot':str(path),'sourcePage':'https://igod.gov.in/ug/E002/organizations#tab1','originalName':entry['originalName'],'originalUrl':entry['originalUrl']}
            verify_source_snapshot(e)
            for field,value in [('originalUrl','https://invented.gov.in/'),('originalName','Invented ministry'),('sourcePage','https://igod.gov.in/ug/E003/organizations')]:
                with self.assertRaises(ValueError):verify_source_snapshot({**e,field:value})


class AdministrativeTest(unittest.TestCase):
    def test_live_recorded_state_ids_and_counts_reconcile_without_website_invention(self):
        pages=json.loads((ROOT/'data/collection/phase2b-2026-10-15/administrative_pages.json').read_text(encoding='utf-8'))['pages']
        admin=parse_admin(pages)
        self.assertEqual(36,len(admin));self.assertEqual(28,sum(s['type']=='State' for s in admin))
        self.assertEqual(784,sum(s['counts']['districts'] for s in admin))
        self.assertTrue(all('url' not in s for s in admin))
        dadra=next(s for s in admin if s['lgdCode']==38)
        self.assertEqual(state_key('Dadra and Nagar Haveli and Daman and Diu'),state_key(dadra['name']))
        bad=copy.deepcopy(pages);table=bad[0]['tables'][-1];table['rows'].append(table['rows'][1])
        with self.assertRaises(ValueError):parse_admin(bad)

    def test_matching_requires_stable_id_and_flags_duplicate_names_renames_and_inactive_units(self):
        units=[{'type':'district','stateLgdCode':1,'lgdCode':100,'name':'Current District','aliases':['Old District'],'active':True},
               {'type':'district','stateLgdCode':1,'lgdCode':101,'name':'Current District','active':True}]
        row={'administrativeType':'district','stateLgdCode':1,'administrativeName':'Current District'}
        self.assertEqual('AMBIGUOUS_NAME',match_unit(row,units)['status'])
        self.assertEqual('ID_MATCH',match_unit({**row,'administrativeLgdCode':100,'administrativeName':'Old District'},units)['status'])
        self.assertEqual('ID_NAME_CONFLICT',match_unit({**row,'administrativeLgdCode':100,'administrativeName':'Different District'},units)['status'])
        units[0]['active']=False
        self.assertEqual('ID_UNRESOLVED',match_unit({**row,'administrativeLgdCode':100},units)['status'])

    def test_coverage_never_infers_unit_links_from_same_name_or_many_website_records(self):
        admin=[{'name':'Goa','lgdCode':30,'type':'State','counts':{'districts':2,'urbanBodies':14,'villagePanchayats':191}}]
        manifest={'stateBranches':{'GA':{'name':'Goa','url':'https://igod.gov.in/sg/GA/categories'}},'pages':{}}
        row={'id':'one','state':'Goa','governmentLevel':'district','district':'North Goa','sourceStates':['Goa'], 'sourceOrganizationIds':['org'], 'healthStatus':'RESPONDING'}
        result=coverage(admin,manifest,[row],{'websites':[row]}, {'records':[]},{'sectors':['health']},[])[0]
        self.assertEqual(2,result['districtIdsUnlinked']);self.assertEqual(0,result['lgdDistrictLinksConfirmed'])
        self.assertEqual(1,result['districtUrlsBundled']);self.assertEqual(['health'],result['missingBundledSectors'])


if __name__=='__main__':unittest.main()
