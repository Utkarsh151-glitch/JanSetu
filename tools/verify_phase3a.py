"""Record local Phase 3A artifact/preservation checks after the build (no network)."""
import base64
import hashlib
import json
import xml.etree.ElementTree as ET
from collections import Counter
from datetime import datetime, timezone
from zipfile import ZipFile

from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import ec
from package_catalog import compact_catalog, encode
from service_catalog import ROOT, merged_catalog, read, write


def sha(data):
    return hashlib.sha256(data).hexdigest()


def suites(pattern):
    files = list(ROOT.glob(pattern))
    assert files, pattern
    totals = Counter()
    for path in files:
        suite = ET.parse(path).getroot()
        totals.update({key: int(suite.get(key, 0)) for key in ('tests', 'failures', 'errors', 'skipped')})
    assert totals['failures'] == totals['errors'] == totals['skipped'] == 0, totals
    return dict(totals)


def main():
    catalog = merged_catalog()
    expected = encode(compact_catalog(catalog))
    baseline = read(ROOT / 'data/validation_reports/phase3a-baseline.json')
    preservation = {}
    for path, key in [('app/src/main/assets/catalog.json', 'catalogSha256'), ('data/review_queue.json', 'queueSha256')]:
        actual = sha((ROOT / path).read_bytes())
        assert actual == baseline[key], path
        preservation[path] = actual
    assert (ROOT / 'extension/catalog.json').read_bytes() == expected
    assert (ROOT / 'app/build/generated/runtimeAssets/catalog.json').read_bytes() == expected
    artifacts = {}
    for relative, member in [
        ('app/build/outputs/apk/debug/app-debug.apk', 'assets/catalog.json'),
        ('app/build/outputs/apk/release/app-release-unsigned.apk', 'assets/catalog.json'),
        ('app/build/outputs/bundle/release/app-release.aab', 'base/assets/catalog.json'),
    ]:
        path = ROOT / relative
        with ZipFile(path) as archive:
            assert archive.read(member) == expected, relative
            assert not any(name.endswith(('.pem', '.jks', '.keystore')) for name in archive.namelist())
        artifacts[relative] = {'bytes': path.stat().st_size, 'sha256': sha(path.read_bytes()), 'catalogMatches': True}
    package_path = ROOT / 'artifacts/catalog-v12.jansetu'
    package = {key: base64.b64decode(value, validate=True) for key, value in read(package_path).items()}
    manifest = json.loads(package['manifest'])
    public = (ROOT / 'app/src/main/assets/catalog-update-public.der').read_bytes()
    serialization.load_der_public_key(public).verify(package['signature'], package['manifest'], ec.ECDSA(hashes.SHA256()))
    assert manifest['sha256'] == sha(package['catalog']) and manifest['bytes'] == len(package['catalog'])
    updated = dict(catalog, version=12)
    assert package['catalog'] == encode(compact_catalog(updated))
    artifacts['artifacts/catalog-v12.jansetu'] = {'bytes': package_path.stat().st_size, 'sha256': sha(package_path.read_bytes()), 'signatureValid': True, 'version': 12, 'services': len(updated['services'])}
    lint = Counter(issue.get('severity') for issue in ET.parse(ROOT / 'app/build/reports/lint-results-debug.xml').getroot())
    assert not lint['Error'] and not lint['Fatal'], lint
    report = {
        'recordedAt': datetime.now(timezone.utc).isoformat(timespec='seconds'),
        'scope': 'Local development; unsigned release artifacts; no publication',
        'appVersion': '0.3.0', 'versionCode': 3, 'bundledCatalogVersion': catalog['version'],
        'counts': read(ROOT / 'data/validation_reports/service-catalog-summary.json'),
        'applicationModes': dict(Counter(service['applicationMode'] for service in catalog['services'])),
        'preservedSourceSha256': preservation, 'runtimeBytes': len(expected), 'runtimeSha256': sha(expected),
        'publicKeySha256': sha(public), 'artifacts': artifacts,
        'jvm': suites('app/build/test-results/testDebugUnitTest/TEST-*.xml'),
        'android': suites('app/build/outputs/androidTest-results/connected/debug/TEST-*.xml'),
        'lint': dict(lint),
        'otherExecutedChecks': {
            'pythonUnittest': {'tests': 58, 'failures': 0},
            'nodeTest': {'tests': 7, 'failures': 0},
            'chromeSmoke': 'Passed with mocked extension APIs; installed activeTab flow not tested',
            'publicReleaseGate': 'Expected rejection of local IGOD-derived records',
        },
        'benchmarkBefore': read(ROOT / 'data/validation_reports/phase3a-search-before.json'),
        'benchmarkAfter': read(ROOT / 'data/validation_reports/phase3a-search-after.json'),
        'measurementLimit': 'JVM warm query observations; index timings cache/order sensitive; not Android latency guarantees',
    }
    write(ROOT / 'data/validation_reports/phase3a-verification.json', report)
    print(json.dumps({key: report[key] for key in ('appVersion', 'bundledCatalogVersion', 'runtimeBytes', 'jvm', 'android', 'lint')}))


if __name__ == '__main__':
    main()
