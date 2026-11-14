import json,os,sys,tempfile,time,unittest
from pathlib import Path
from run_phase4_quality import run,xml_totals,ROOT

class QualityHarnessTest(unittest.TestCase):
    def test_success_log_cannot_override_native_failure_status(self):
        with tempfile.TemporaryDirectory() as tmp:
            result=run([sys.executable,'-c','import sys; print("BUILD SUCCESSFUL"); sys.exit(7)'],'fixture',Path(tmp),os.environ.copy())
            self.assertEqual(7,result['exitCode'])
    def test_fresh_xml_is_required_and_failures_and_skips_are_not_passing(self):
        with tempfile.TemporaryDirectory() as tmp:
            p=Path(tmp)/'TEST-fixture.xml';start=time.time()
            for attr in ('failures','errors','skipped'):
                counts={'tests':1,'failures':0,'errors':0,'skipped':0};counts[attr]=1
                p.write_text('<testsuite '+' '.join(f'{k}="{v}"' for k,v in counts.items())+'/>')
                with self.assertRaises(AssertionError):xml_totals([p],start)
            p.write_text('<testsuite tests="1" failures="0" errors="0" skipped="0"/>')
            self.assertEqual(1,xml_totals([p],start)['tests'])
            os.utime(p,(1,1))
            with self.assertRaises(AssertionError):xml_totals([p],start)
            with self.assertRaises(AssertionError):xml_totals([],start)
    def test_phase4_catalog_decisions_and_approved_browser_profiles_preserved(self):
        import hashlib
        baseline=json.loads((ROOT/'data/validation_reports/phase4-source-baseline.json').read_text(encoding='utf-8'))
        for name,digest in baseline['protectedSha256'].items():
            with self.subTest(name=name):self.assertEqual(digest,hashlib.sha256((ROOT/name).read_bytes()).hexdigest())

if __name__=='__main__':unittest.main()
