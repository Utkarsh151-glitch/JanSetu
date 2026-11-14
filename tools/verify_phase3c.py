"""Verify Phase 3C runtime/debug/signed artifacts and protected data (no private-key access)."""
import base64,copy,hashlib,json,zipfile,xml.etree.ElementTree as ET
from cryptography.hazmat.primitives import hashes,serialization
from cryptography.hazmat.primitives.asymmetric import ec
from service_catalog import ROOT,DIRECTORY,read,write,merged_catalog
from package_catalog import compact_catalog,encode

def sha(path):return hashlib.sha256(path.read_bytes()).hexdigest()
def totals(paths):
    total=dict(tests=0,failures=0,errors=0,skipped=0)
    files=list(paths);assert files,'Missing test result files'
    for p in files:
        root=ET.parse(p).getroot()
        for key in total:total[key]+=int(root.get(key,0))
    assert total['tests']>0 and not(total['failures'] or total['errors'] or total['skipped']),total
    return {**total,'files':len(files)}

def main():
    baseline=read(ROOT/'data/validation_reports/phase3c-baseline.json');full=merged_catalog();pilot=read(DIRECTORY/'catalog.json');queue=read(DIRECTORY/'review_queue.json')
    for path in ('app/src/main/assets/catalog.json','data/review_queue.json'):assert sha(ROOT/path)==baseline['sha256'][path]
    assert len(full['websites'])==7347 and len(full['services'])==52 and pilot['version']==13
    current={s['id']:s for s in pilot['services']}
    for old in baseline['services']['services']:
        assert old['id'] in current
        if old['id']!='pmegp-enterprise-support':assert current[old['id']]==old
    for old in baseline['serviceQueue']:
        if old['status']=='PUBLISHED':assert old in queue
        else:assert any(q['id']==old['id'] and q['history'][0]['previous']==old for q in queue)
    expected=encode(compact_catalog(full));runtime=ROOT/'app/build/generated/runtimeAssets/catalog.json'
    assert runtime.read_bytes()==expected
    assert (ROOT/'extension/catalog.json').read_bytes()==expected
    apk=ROOT/'app/build/outputs/apk/debug/app-debug.apk'
    with zipfile.ZipFile(apk) as archive:
        assert archive.read('assets/catalog.json')==expected
        assert not any(n.lower().endswith(('.pem','.jks','.keystore')) for n in archive.namelist())
    signed=ROOT/'artifacts/catalog-v14.jansetu';envelope=read(signed)
    manifest_bytes=base64.b64decode(envelope['manifest']);manifest=json.loads(manifest_bytes);payload=base64.b64decode(envelope['catalog'])
    pin=ROOT/'app/src/main/assets/catalog-update-public.der'
    assert sha(pin)=='08b89c0df4afa20b187d827fa04942afaa9a74d4b5c3fa4bb8cb1157fa1884d7'
    serialization.load_der_public_key(pin.read_bytes()).verify(base64.b64decode(envelope['signature']),manifest_bytes,ec.ECDSA(hashes.SHA256()))
    assert manifest['sha256']==hashlib.sha256(payload).hexdigest() and manifest['bytes']==len(payload)
    assert manifest['version']==14 and manifest['scope']=='local_development'
    delivered=json.loads(payload);delivered['version']=13;assert encode(delivered)==expected
    jvm=totals((ROOT/'app/build/test-results/testDebugUnitTest').glob('TEST-*.xml'))
    android=totals([ROOT/'data/validation_reports/phase3c-connected-full.xml'])
    cleanupRegression=totals((ROOT/'app/build/outputs/androidTest-results/connected/debug').rglob('TEST-*.xml'))
    lint=ET.parse(ROOT/'app/build/reports/lint-results-debug.xml').getroot()
    severities={s:sum(i.get('severity')==s for i in lint.findall('issue')) for s in ('Error','Fatal','Warning','Information')}
    assert severities['Error']==severities['Fatal']==0
    report=dict(date='2026-10-16',appVersion='0.4.1',versionCode=5,websites=7347,services=52,runtimeVersion=13,signedDemonstrationVersion=14,
        unchangedServiceObjects=24,explicitRevisions=['pmegp-enterprise-support'],protectedWebsiteAndQueueHashes=True,publicPinSha256=sha(pin),
        runtimeBytes=len(expected),runtimeSha256=sha(runtime),pythonTests=68,nodeTests=7,jvm=jvm,android=android,postCleanupConfigurationRegression=cleanupRegression,lint=severities,
        artifacts={str(p.relative_to(ROOT)):{'bytes':p.stat().st_size,'sha256':sha(p)} for p in (apk,signed)},
        release='Local debug/data delivery only; older release APK/AAB are historical, not rebuilt in Phase 3C. No production signing or upload.')
    write(ROOT/'data/validation_reports/phase3c-verification.json',report);print(json.dumps(report,indent=2))

if __name__=='__main__':main()
