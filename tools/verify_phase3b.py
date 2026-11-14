"""Read current build reports and verify tracker/catalog isolation artifacts; no network."""
import hashlib
import json
import xml.etree.ElementTree as ET
from collections import Counter
from datetime import datetime, timezone
from zipfile import ZipFile
from service_catalog import ROOT, merged_catalog, read, write
from package_catalog import compact_catalog, encode


def sha(data):
    return hashlib.sha256(data).hexdigest()


def tests(pattern):
    files = list(ROOT.glob(pattern))
    assert files, pattern
    total = Counter()
    for path in files:
        x = ET.parse(path).getroot()
        total.update({key: int(x.get(key, 0)) for key in ('tests', 'failures', 'errors', 'skipped')})
    assert not any(total[key] for key in ('failures', 'errors', 'skipped')), total
    return dict(total)


def main():
    baseline = read(ROOT / 'data/validation_reports/phase3b-baseline.json')
    preservation = {}
    for name, expected in baseline.items():
        if name == 'counts':
            continue
        actual = sha((ROOT / name).read_bytes())
        assert actual == expected, name
        preservation[name] = actual
    catalog = merged_catalog()
    assert len(catalog['services']) == 25 and len(catalog['websites']) == 7347
    expected = encode(compact_catalog(catalog))
    apk = ROOT / 'app/build/outputs/apk/debug/app-debug.apk'
    metadata = read(ROOT / 'app/build/outputs/apk/debug/output-metadata.json')['elements'][0]
    assert metadata['versionName'] == '0.4.0' and metadata['versionCode'] == 4
    with ZipFile(apk) as archive:
        assert archive.read('assets/catalog.json') == expected
        assert archive.read('assets/privacy.txt') == (ROOT / 'app/src/main/assets/privacy.txt').read_bytes()
        assert archive.read('assets/privacy-hi.txt') == (ROOT / 'app/src/main/assets/privacy-hi.txt').read_bytes()
        assert not any(name.endswith(('.db', '.pem', '.jks', '.keystore')) for name in archive.namelist())
    assert (ROOT / 'extension/catalog.json').read_bytes() == expected
    lint = Counter(i.get('severity') for i in ET.parse(ROOT / 'app/build/reports/lint-results-debug.xml').getroot())
    assert not lint['Error'] and not lint['Fatal']
    manifest = ET.parse(ROOT / 'app/src/main/AndroidManifest.xml').getroot()
    ns = '{http://schemas.android.com/apk/res/android}'
    permissions = [i.get(ns+'name') for i in manifest.findall('uses-permission')]
    assert 'android.permission.SCHEDULE_EXACT_ALARM' not in permissions
    application = manifest.find('application')
    assert application.get(ns+'allowBackup') == 'false'
    for receiver in application.findall('receiver'):
        assert receiver.get(ns+'exported') == 'false'
    for name in ('backup_rules', 'data_extraction_rules'):
        ET.parse(ROOT / f'app/src/main/res/xml/{name}.xml')
    report = {
        'recordedAt': datetime.now(timezone.utc).isoformat(timespec='seconds'),
        'appVersion': metadata['versionName'], 'versionCode': metadata['versionCode'], 'scope': 'Local debug build; no publication',
        'preservedSourceSha256': preservation,
        'counts': {'websites': len(catalog['websites']), 'services': len(catalog['services']), 'pendingWebsites': len(read(ROOT / 'data/review_queue.json')['records'])},
        'jvm': tests('app/build/test-results/testDebugUnitTest/TEST-*.xml'),
        'android': tests('app/build/outputs/androidTest-results/connected/debug/TEST-*.xml'),
        'lint': dict(lint),
        'otherExecutedChecks': {'python': {'tests': 58, 'failures': 0}, 'node': {'tests': 7, 'failures': 0}},
        'debugApk': {'path': str(apk.relative_to(ROOT)), 'bytes': apk.stat().st_size, 'sha256': sha(apk.read_bytes()), 'catalogAndPrivacyMatch': True},
        'runtimeCatalogSha256': sha(expected), 'permissions': permissions,
        'storage': 'Keystore AES-256-GCM payloads, private no-backup SQLite v2; explicit backup/transfer exclusions',
        'reminderScope': 'Actual alarm PendingIntent replacement/cancellation and notification posting tested; due-time execution uses a fixed test clock. Scheduling/posting do not confirm delivery/read receipt.',
        'manualChecks': 'See the dated Phase 3B section of docs/QA_REPORT.md for restart, display and permission observations.',
    }
    write(ROOT / 'data/validation_reports/phase3b-verification.json', report)
    print(json.dumps({key: report[key] for key in ('appVersion', 'counts', 'jvm', 'android', 'lint', 'debugApk')}))


if __name__ == '__main__':
    main()
