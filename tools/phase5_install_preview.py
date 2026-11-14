"""Install the final Phase 5 debug APK over the retained preview app and verify it.

Usage: python tools/phase5_install_preview.py

Uses `adb install -r` only. It never uninstalls, clears or seeds
in.jansetu.app. Protected preview-profile hashes are compared before and
after installation and a launch; any difference fails the run.
"""
from __future__ import annotations

import hashlib
import json
import os
import re
import subprocess
import sys
import time
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "tools"))
from run_phase5_quality import preview_profile  # noqa: E402  (same hash definition as the gate)

SDK = Path(os.environ.get("ANDROID_HOME", Path(os.environ["LOCALAPPDATA"]) / "Android/Sdk"))
ADB = SDK / "platform-tools/adb.exe"
BUILD_TOOLS = sorted((SDK / "build-tools").iterdir())[-1]
APK = ROOT / "app/build/outputs/apk/debug/app-debug.apk"
OUT = ROOT / "data/validation_reports/phase5-extension"
SHOTS = ROOT / "docs/screenshots/phase5/after"
PACKAGE = "in.jansetu.app"


def run(*command: str, timeout: float = 120) -> str:
    result = subprocess.run(list(command), capture_output=True, text=True, timeout=timeout, encoding="utf-8", errors="replace")
    if result.returncode:
        raise RuntimeError(f"{' '.join(command)} failed: {result.stdout}\n{result.stderr}")
    return result.stdout + result.stderr


def adb(*args: str, timeout: float = 120) -> str:
    return run(str(ADB), *args, timeout=timeout)


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    gate = json.loads((ROOT / "data/validation_reports/phase5/extension-final/result.json").read_text(encoding="utf-8"))
    digest = hashlib.sha256(APK.read_bytes()).hexdigest()
    if not gate.get("passed") or gate["artifacts"]["debug"]["sha256"] != digest:
        raise RuntimeError("Debug APK is not the artifact verified by the final gate")
    record: dict[str, object] = {"startedAt": datetime.now(timezone.utc).isoformat(), "apk": {"bytes": APK.stat().st_size, "sha256": digest}}
    badging = run(str(BUILD_TOOLS / "aapt.exe"), "dump", "badging", str(APK))
    record["aapt"] = {
        "package": re.search(r"package: name='([^']+)' versionCode='(\d+)' versionName='([^']+)'", badging).groups(),
        "label": re.search(r"application-label:'([^']+)'", badging).group(1),
        "minSdk": re.search(r"sdkVersion:'(\d+)'", badging).group(1),
        "targetSdk": re.search(r"targetSdkVersion:'(\d+)'", badging).group(1),
        "icon": re.findall(r"application-icon-\d+:'([^']+)'", badging)[:1],
    }
    if record["aapt"]["package"][0] != PACKAGE:
        raise RuntimeError("Unexpected package identity")
    record["apksigner"] = run(str(BUILD_TOOLS / "apksigner.bat"), "verify", "--print-certs", "-v", str(APK)).splitlines()[:6]
    before = preview_profile()
    record["previewBefore"] = before
    record["install"] = adb("install", "-r", str(APK), timeout=300).strip().splitlines()[-1]
    package = adb("shell", "dumpsys", "package", PACKAGE)
    record["installed"] = {
        "versionName": re.search(r"versionName=(\S+)", package).group(1),
        "versionCode": re.search(r"versionCode=(\d+)", package).group(1),
        "lastUpdateTime": re.search(r"lastUpdateTime=([^\n]+)", package).group(1).strip(),
        "firstInstallTime": re.search(r"firstInstallTime=([^\n]+)", package).group(1).strip(),
    }
    remote = adb("shell", "pm", "path", PACKAGE).strip().splitlines()[0].removeprefix("package:")
    record["installedApkSha256"] = adb("shell", "sha256sum", remote).split()[0]
    if record["installedApkSha256"] != digest:
        raise RuntimeError("Installed preview APK differs from the verified artifact")
    record["launch"] = [line for line in adb("shell", "am", "start", "-W", "-n", f"{PACKAGE}/.MainActivity").splitlines() if line.startswith(("Status", "LaunchState", "TotalTime"))]
    time.sleep(12)
    (SHOTS / "preview-installed-home.png").write_bytes(subprocess.run([str(ADB), "exec-out", "screencap", "-p"], capture_output=True).stdout)
    record["previewAfter"] = preview_profile()
    record["previewUnchanged"] = record["previewAfter"] == before
    # Launcher drawer with both packages' new icons.
    adb("shell", "input", "keyevent", "3")
    time.sleep(1.5)
    adb("shell", "input", "keyevent", "284")
    time.sleep(4)
    (SHOTS / "launcher.png").write_bytes(subprocess.run([str(ADB), "exec-out", "screencap", "-p"], capture_output=True).stdout)
    adb("shell", "input", "keyevent", "3")
    time.sleep(1)
    # Leave the redesigned preview Home on screen.
    adb("shell", "am", "start", "-n", f"{PACKAGE}/.MainActivity")
    record["finishedAt"] = datetime.now(timezone.utc).isoformat()
    (OUT / "preview-install.json").write_text(json.dumps(record, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(record, indent=2))
    if not record["previewUnchanged"]:
        raise RuntimeError("Preview profile changed during installation or launch")


if __name__ == "__main__":
    main()
