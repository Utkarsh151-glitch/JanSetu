"""Run fresh quality gates with actual native exit codes and isolated QA instrumentation.

python tools/run_phase4_quality.py baseline
python tools/run_phase4_quality.py final
Logs contain test diagnostics, not captured app-private payloads or signing keys.
"""
import argparse,hashlib,json,os,re,shutil,subprocess,sys,time
from pathlib import Path
from datetime import datetime,timezone
import xml.etree.ElementTree as ET

ROOT=Path(__file__).resolve().parents[1]
SDK=Path(os.environ.get('ANDROID_HOME',Path(os.environ['LOCALAPPDATA'])/'Android/Sdk'))
ADB=SDK/'platform-tools/adb.exe'
JAVA=Path(os.environ.get('JAVA_HOME',r'C:\Program Files\Java\jdk-17'))

def adb(*args):return subprocess.run([str(ADB),*args],capture_output=True,timeout=30)
def preview_profile():
    result={}
    for name in ('no_backup/tracker/applications.db','shared_prefs/jansetu_preferences.xml','shared_prefs/tracker_alarm_ids.xml','files/catalog-update.jansetu'):
        r=adb('shell','run-as','in.jansetu.app','sha256sum',name)
        if r.returncode==0:result[name]=r.stdout.decode().split()[0]
        elif b'No such file' in r.stderr or b'No such file' in r.stdout:result[name]=None
        else:raise RuntimeError('Cannot verify the retained preview profile')
    return result

def run(command,name,directory,env):
    start=time.monotonic()
    with (directory/f'{name}.stdout.log').open('wb') as out,(directory/f'{name}.stderr.log').open('wb') as err:
        process=subprocess.run(command,cwd=ROOT,env=env,stdout=out,stderr=err)
    result=dict(exitCode=process.returncode,seconds=round(time.monotonic()-start,3))
    print(name+': '+json.dumps(result),flush=True)
    return result

def xml_totals(paths,started):
    files=list(paths);assert files,'No test XML'
    total={k:0 for k in ('tests','failures','errors','skipped')}
    for file in files:
        assert file.stat().st_mtime>=started-2,f'Stale XML: {file.name}'
        root=ET.parse(file).getroot()
        for key in total:total[key]+=int(root.get(key,0))
    assert total['tests'] and total['failures']==total['errors']==total['skipped']==0,total
    return total

def main():
    p=argparse.ArgumentParser();p.add_argument('stage',choices=('baseline','final'));args=p.parse_args()
    directory=ROOT/'data/validation_reports/phase4'/args.stage;directory.mkdir(parents=True,exist_ok=True)
    if (directory/'result.json').exists():raise ValueError('Preserve prior run; choose a new stage directory after archiving it')
    assert adb('get-state').stdout.strip()==b'device','A responsive emulator is required'
    assert adb('shell','am','force-stop','in.jansetu.app').returncode==0
    before=preview_profile();assert before['no_backup/tracker/applications.db'],'Retained preview profile missing'
    started=time.time();env=os.environ.copy();env['JAVA_HOME']=str(JAVA)
    env['JANSETU_SEARCH_BENCHMARK']=str(directory/'search-jvm.json')
    result=dict(startedAt=datetime.now(timezone.utc).isoformat(),stage=args.stage,previewBefore=before,commands={},scope='Functional app code; Android suite in in.jansetu.app.qa, independent UID/Keystore/files')
    commands=[('python',[sys.executable,'-m','unittest','discover','-s','tools','-p','test_*.py']),('node',['node','--test','extension/core.test.mjs']),
        ('gradle',[str(JAVA/'bin/java.exe'),'-Xmx64m','-Xms64m','-Dorg.gradle.appname=gradlew','-classpath',str(ROOT/'gradle/wrapper/gradle-wrapper.jar'),'org.gradle.wrapper.GradleWrapperMain',
        ':app:testDebugUnitTest',':app:lintDebug',':app:assembleDebug',':app:connectedQaAndroidTest','--no-daemon','--max-workers=1','-Pkotlin.compiler.execution.strategy=in-process','-Pjansetu.forceTests=true','--console=plain'])]
    try:
        for name,cmd in commands:
            result['commands'][name]=run(cmd,name,directory,env)
            assert result['commands'][name]['exitCode']==0,f'{name} returned a nonzero process status'
        result['pythonTests']=int(re.search(r'Ran (\d+) tests', (directory/'python.stderr.log').read_text(encoding='utf-8')).group(1))
        tap=(directory/'node.stdout.log').read_text(encoding='utf-8')
        result['nodeTests']={k:int(re.search(r'# '+k+r' (\d+)',tap).group(1)) for k in ('tests','pass','fail','skipped')}
        assert result['nodeTests']['fail']==result['nodeTests']['skipped']==0
        result['jvm']=xml_totals((ROOT/'app/build/test-results/testDebugUnitTest').glob('TEST-*.xml'),started)
        result['android']=xml_totals((ROOT/'app/build/outputs/androidTest-results/connected/qa').rglob('TEST-*.xml'),started)
        if args.stage=='baseline':assert (result['jvm']['tests'],result['android']['tests'])==(35,28)
        for label,source in [('jvm-xml',ROOT/'app/build/test-results/testDebugUnitTest'),('android-xml',ROOT/'app/build/outputs/androidTest-results/connected/qa')]:
            dest=directory/label;dest.mkdir(exist_ok=True)
            for i,file in enumerate(source.rglob('TEST-*.xml')):shutil.copyfile(file,dest/f'{i}-{file.name}')
        lint=ROOT/'app/build/reports/lint-results-debug.xml';shutil.copyfile(lint,directory/'lint.xml')
        issues=ET.parse(lint).getroot().findall('issue');result['lint']={s:sum(i.get('severity')==s for i in issues) for s in ('Error','Fatal','Warning')}
        assert result['lint']['Error']==result['lint']['Fatal']==0
        assert adb('shell','pm','path','in.jansetu.app.qa').returncode==0,'QA app was uninstalled'
        result['previewAfter']=preview_profile();assert result['previewAfter']==before,'Preview data changed during QA'
        baseline=json.loads((ROOT/'data/validation_reports/phase4-source-baseline.json').read_text(encoding='utf-8'))
        for name,digest in baseline['protectedSha256'].items():assert hashlib.sha256((ROOT/name).read_bytes()).hexdigest()==digest,f'Protected data changed: {name}'
        result['artifacts']={}
        for variant in ('debug','qa'):
            apk=ROOT/f'app/build/outputs/apk/{variant}/app-{variant}.apk'
            result['artifacts'][variant]={'bytes':apk.stat().st_size,'sha256':hashlib.sha256(apk.read_bytes()).hexdigest()}
        if args.stage=='baseline':
            for name,digest in baseline['sourceSha256'].items():assert hashlib.sha256((ROOT/name).read_bytes()).hexdigest()==digest,'Baseline functional source drift: '+name
            shutil.copyfile(ROOT/'app/build/outputs/apk/qa/app-qa.apk',ROOT/'.local/phase4-baseline-qa.apk')
        result['passed']=True
    except Exception as error:
        result.update(passed=False,error=str(error))
        raise
    finally:
        result['finishedAt']=datetime.now(timezone.utc).isoformat()
        (directory/'result.json').write_text(json.dumps(result,indent=2)+'\n',encoding='utf-8')
    print(json.dumps(result,indent=2))

if __name__=='__main__':main()
