"""Bounded ADB checks against the isolated QA app, never the preview profile.

python tools/phase4_device.py measure before|after
python tools/phase4_device.py snapshot
python tools/phase4_device.py tap "visible label"
python tools/phase4_device.py capture name

Interaction timings include host ADB/input/UIAutomator polling overhead. They
are observed upper bounds, not frame timings. No content from the preview app
is dumped, and all app fixtures must be synthetic QA data.
"""
import argparse,hashlib,json,os,re,statistics,subprocess,time,sys
from pathlib import Path
import xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parents[1]
ADB=Path(os.environ['LOCALAPPDATA'])/'Android/Sdk/platform-tools/adb.exe'
PACKAGE='in.jansetu.app.qa';ACTIVITY=PACKAGE+'/in.jansetu.app.MainActivity'
sys.stdout.reconfigure(encoding='utf-8')
def adb(*args):
    r=subprocess.run([str(ADB),*args],capture_output=True,timeout=30)
    if r.returncode:raise RuntimeError('ADB operation failed: '+args[0])
    return r.stdout
def tree():
    adb('shell','uiautomator','dump','/sdcard/phase4-qa-ui.xml')
    return ET.fromstring(adb('exec-out','cat','/sdcard/phase4-qa-ui.xml'))
def labels(root):return [n.get('text') or n.get('content-desc') for n in root.iter('node') if n.get('text') or n.get('content-desc')]
def wait(text,timeout=35):
    deadline=time.monotonic()+timeout
    while time.monotonic()<deadline:
        root=tree()
        if text in labels(root):return root
    raise TimeoutError('QA UI label unavailable: '+text)
def wait_contains(text,timeout=35):
    deadline=time.monotonic()+timeout
    while time.monotonic()<deadline:
        root=tree()
        if any(text in label for label in labels(root)):return root
    raise TimeoutError('QA UI text unavailable: '+text)
def tap(text):
    nodes=[n for n in wait(text).iter('node') if n.get('text')==text or n.get('content-desc')==text]
    if len(nodes)!=1:raise ValueError('Ambiguous visible label: '+text)
    x,y,x2,y2=map(int,re.findall(r'\d+',nodes[0].get('bounds')))
    adb('shell','input','tap',str((x+x2)//2),str((y+y2)//2))
def capture(name):
    assert re.fullmatch('[a-z0-9-]+',name)
    assert any(n.get('package')==PACKAGE for n in tree().iter('node')),'QA app is not visible'
    (ROOT/f'docs/screenshots/phase4-{name}.png').write_bytes(adb('exec-out','screencap','-p'))
def launch():
    output=adb('shell','am','start','-W','-n',ACTIVITY).decode()
    root=tree()
    if 'Explore JanSetu' in labels(root):tap('Explore JanSetu')
    # Android restores the last navigation destination after a force-stop. The
    # timing route always starts on Home so each repeat exercises the same path.
    for _ in range(3):
        root=tree()
        if 'What do you need to do?' in labels(root): break
        if 'Home' in labels(root):tap('Home')
        else: adb('shell','input','keyevent','4')
        time.sleep(.4)
    wait('What do you need to do?')
    return output
def measure(stage):
    path=ROOT/f'data/validation_reports/phase4/performance-{stage}.json'
    if path.exists():raise ValueError('Retain previous measurements rather than overwrite them')
    launch();rows=[]
    for repeat in range(3):
        row={'repeat':repeat+1}
        adb('shell','am','force-stop',PACKAGE)
        start=time.monotonic();am=launch();row['coldCatalogReadyMs']=(time.monotonic()-start)*1000
        row['amStart']={k:int(v) for k,v in re.findall(r'(TotalTime|WaitTime|ThisTime): (\d+)',am)}
        tap('Search');wait('What are you looking for?');start=time.monotonic()
        tap('What are you looking for?');adb('shell','input','text','Kerala%sincome%scertificate');tree();adb('shell','input','keyevent','4')
        wait('Income Certificate (Kerala)');row['searchInteractionMs']=(time.monotonic()-start)*1000
        start=time.monotonic();tap('Income Certificate (Kerala)');wait('Verified service information');row['serviceOpenMs']=(time.monotonic()-start)*1000
        tap('Back');wait('Search portals');start=time.monotonic();tap('Applications');wait('No applications yet');row['emptyTrackerLoadMs']=(time.monotonic()-start)*1000
        tap('Home');wait('What do you need to do?');tap('Settings');tap('Offline catalog and updates');wait_contains('Version 13')
        tap('Import catalog update');wait('catalog-v14.jansetu');start=time.monotonic();tap('catalog-v14.jansetu');wait_contains('Version 14',75);row['signedImportMs']=(time.monotonic()-start)*1000
        tap('Restore bundled catalog');wait('Restore bundled catalog?');start=time.monotonic();tap('Restore');wait_contains('Bundled catalog restored.',75);wait_contains('Version 13',75);row['restoreMs']=(time.monotonic()-start)*1000
        rows.append(row);print(json.dumps(row),flush=True)
    source={str(p.relative_to(ROOT)):hashlib.sha256(p.read_bytes()).hexdigest() for p in ROOT.glob('app/src/main/**/*.kt')}
    apk=ROOT/'app/build/outputs/apk/qa/app-qa.apk'
    result=dict(stage=stage,package=PACKAGE,repeats=3,conditions='API37 emulator; 1080x2400/420 dpi, font1.0, English/light, 7347 websites/52 services, empty QA tracker. Host ADB/UIAutomator interaction upper bounds, not UI-frame latency. Cold = force-stopped process with OS/disk caches retained.',rows=rows,
        medians={k:statistics.median(r[k] for r in rows) for k in rows[0] if k.endswith('Ms')},sourceSha256=source,apkSha256=hashlib.sha256(apk.read_bytes()).hexdigest())
    path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(result,indent=2)+'\n',encoding='utf-8')
def main():
    p=argparse.ArgumentParser();p.add_argument('action',choices=('measure','tap','snapshot','capture'));p.add_argument('value',nargs='?');args=p.parse_args()
    if args.action=='measure':measure(args.value)
    elif args.action=='tap':tap(args.value)
    elif args.action=='capture':capture(args.value)
    else:
        for label in labels(tree()):print(label)
if __name__=='__main__':main()
