"""Run the Phase 5 UI regression gate against the isolated QA app.

Usage: python tools/run_phase5_quality.py [run-name]
Results go to data/validation_reports/phase5/<run-name> (default "final").
The command captures native exit codes, test XML totals, lint results, APK
digests and preview-profile hashes. It never clears or installs the preview.
"""
from __future__ import annotations

import hashlib
import json
import os
import re
import shutil
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SDK = Path(os.environ.get("ANDROID_HOME", Path(os.environ["LOCALAPPDATA"]) / "Android/Sdk"))
ADB = SDK / "platform-tools/adb.exe"
JAVA = Path(os.environ.get("JAVA_HOME", r"C:\Program Files\Java\jdk-17"))
RUN = sys.argv[1] if len(sys.argv) > 1 else "final"
if not re.fullmatch(r"[a-z0-9-]+", RUN):
    raise SystemExit("Run name must use lowercase letters, digits and hyphens")
OUT = ROOT / "data/validation_reports/phase5" / RUN


def adb(*args: str) -> subprocess.CompletedProcess[bytes]:
    return subprocess.run([str(ADB), *args], capture_output=True, timeout=30)


def preview_profile() -> dict[str, str | None]:
    result: dict[str, str | None] = {}
    for name in (
        "no_backup/tracker/applications.db",
        "shared_prefs/jansetu_preferences.xml",
        "shared_prefs/tracker_alarm_ids.xml",
        "files/catalog-update.jansetu",
    ):
        check = adb("shell", "run-as", "in.jansetu.app", "sha256sum", name)
        tokens = check.stdout.decode(errors="replace").split()
        if check.returncode == 0 and tokens:
            result[name] = tokens[0]
        elif b"No such file" in check.stdout or b"No such file" in check.stderr:
            result[name] = None
        else:
            raise RuntimeError(f"Cannot inspect retained preview item: {name}")
    return result


def run(name: str, command: list[str], env: dict[str, str]) -> dict[str, float | int]:
    started = time.monotonic()
    with (OUT / f"{name}.stdout.log").open("wb") as stdout, (OUT / f"{name}.stderr.log").open("wb") as stderr:
        process = subprocess.run(command, cwd=ROOT, env=env, stdout=stdout, stderr=stderr)
    result = {"exitCode": process.returncode, "seconds": round(time.monotonic() - started, 3)}
    print(f"{name}: {json.dumps(result)}", flush=True)
    if process.returncode:
        raise RuntimeError(f"{name} returned {process.returncode}")
    return result


def xml_totals(files: list[Path], started: float) -> dict[str, int]:
    if not files:
        raise RuntimeError("No test XML files found")
    total = {key: 0 for key in ("tests", "failures", "errors", "skipped")}
    for file in files:
        if file.stat().st_mtime < started - 2:
            raise RuntimeError(f"Stale test XML: {file.name}")
        root = ET.parse(file).getroot()
        for key in total:
            total[key] += int(root.get(key, 0))
    if not total["tests"] or any(total[key] for key in ("failures", "errors", "skipped")):
        raise RuntimeError(f"Unclean XML totals: {total}")
    return total


def digest(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main() -> None:
    if OUT.exists():
        raise RuntimeError(f"Archive existing Phase 5 run before replacement: {OUT}")
    OUT.mkdir(parents=True)
    if adb("get-state").stdout.strip() != b"device":
        raise RuntimeError("A responsive emulator is required")
    before = preview_profile()
    if not before["no_backup/tracker/applications.db"]:
        raise RuntimeError("Retained preview tracker profile is missing")
    started = time.time()
    environment = os.environ.copy()
    environment["JAVA_HOME"] = str(JAVA)
    environment["JANSETU_SEARCH_BENCHMARK"] = str(OUT / "search-jvm.json")
    result: dict[str, object] = {
        "startedAt": datetime.now(timezone.utc).isoformat(),
        "scope": "Phase 5 native UI redesign; tests run only in in.jansetu.app.qa",
        "run": RUN,
        "previewBefore": before,
        "commands": {},
    }
    try:
        commands = {
            "python": [sys.executable, "-m", "unittest", "discover", "-s", "tools", "-p", "test_*.py"],
            "node": ["node", "--test", "extension/core.test.mjs", "extension/assist.test.mjs"],
            "gradle": [
                str(JAVA / "bin/java.exe"), "-Xmx64m", "-Xms64m", "-Dorg.gradle.appname=gradlew",
                "-classpath", str(ROOT / "gradle/wrapper/gradle-wrapper.jar"), "org.gradle.wrapper.GradleWrapperMain",
                ":app:testDebugUnitTest", ":app:lintDebug", ":app:assembleDebug", ":app:connectedQaAndroidTest",
                "--no-daemon", "--max-workers=1", "-Pkotlin.compiler.execution.strategy=in-process",
                "-Pjansetu.forceTests=true", "--console=plain",
            ],
        }
        for name, command in commands.items():
            result["commands"][name] = run(name, command, environment)
        result["pythonTests"] = int(re.search(r"Ran (\d+) tests", (OUT / "python.stderr.log").read_text(encoding="utf-8")).group(1))
        tap = (OUT / "node.stdout.log").read_text(encoding="utf-8")
        result["nodeTests"] = {key: int(re.search(r"# " + key + r" (\d+)", tap).group(1)) for key in ("tests", "pass", "fail", "skipped")}
        if result["nodeTests"]["fail"] or result["nodeTests"]["skipped"]:
            raise RuntimeError(f"Extension test results: {result['nodeTests']}")
        result["jvm"] = xml_totals(list((ROOT / "app/build/test-results/testDebugUnitTest").glob("TEST-*.xml")), started)
        result["android"] = xml_totals(list((ROOT / "app/build/outputs/androidTest-results/connected/qa").rglob("TEST-*.xml")), started)
        for name, source in (("jvm-xml", ROOT / "app/build/test-results/testDebugUnitTest"), ("android-xml", ROOT / "app/build/outputs/androidTest-results/connected/qa")):
            destination = OUT / name
            destination.mkdir()
            for index, file in enumerate(source.rglob("TEST-*.xml")):
                shutil.copyfile(file, destination / f"{index}-{file.name}")
        lint = ROOT / "app/build/reports/lint-results-debug.xml"
        shutil.copyfile(lint, OUT / "lint.xml")
        issues = ET.parse(lint).getroot().findall("issue")
        result["lint"] = {severity: sum(issue.get("severity") == severity for issue in issues) for severity in ("Error", "Fatal", "Warning")}
        if result["lint"]["Error"] or result["lint"]["Fatal"]:
            raise RuntimeError(f"Lint gate failed: {result['lint']}")
        baseline = json.loads((ROOT / "data/validation_reports/phase4-source-baseline.json").read_text(encoding="utf-8"))
        result["protectedData"] = {}
        for name, expected in baseline["protectedSha256"].items():
            actual = digest(ROOT / name)
            result["protectedData"][name] = actual
            if actual != expected:
                raise RuntimeError(f"Protected catalog/extension source changed: {name}")
        result["previewAfter"] = preview_profile()
        if result["previewAfter"] != before:
            raise RuntimeError("Preview profile changed during the QA gate")
        result["artifacts"] = {}
        for variant in ("debug", "qa"):
            apk = ROOT / f"app/build/outputs/apk/{variant}/app-{variant}.apk"
            result["artifacts"][variant] = {"bytes": apk.stat().st_size, "sha256": digest(apk)}
        result["passed"] = True
    except Exception as error:
        result.update(passed=False, error=str(error))
        raise
    finally:
        result["finishedAt"] = datetime.now(timezone.utc).isoformat()
        (OUT / "result.json").write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
