"""Inspect a built APK (or AAB) directly before release: identity, manifest exposure and packaged content.

Usage (from the project root):
  python tools/inspect_release_artifact.py app/build/outputs/apk/release/app-release-unsigned.apk [--release]

--release additionally requires a non-debuggable build with no test-only components. The report is printed as
JSON; the exit code is non-zero if any check fails. Source-level tests are not a substitute for this check.
"""
from __future__ import annotations

import hashlib
import json
import os
import re
import subprocess
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SDK = Path(os.environ.get("ANDROID_HOME", Path(os.environ.get("LOCALAPPDATA", "")) / "Android/Sdk"))
ALLOWED_ASSETS = {"assets/catalog.json", "assets/privacy.txt", "assets/privacy-hi.txt", "assets/catalog-update-public.der"}
# ART baseline profiles that the Android Gradle plugin adds to release builds (performance data, no content).
BUILD_ASSETS = {"assets/dexopt/baseline.prof", "assets/dexopt/baseline.profm"}
ALLOWED_PERMISSIONS = {"android.permission.INTERNET", "android.permission.POST_NOTIFICATIONS", "android.permission.RECEIVE_BOOT_COMPLETED"}
FORBIDDEN_NAMES = re.compile(r"research/|claims\.json|evidence_index|prereview|staging-|review-packet|review-queue|reviews\.json|\.pem$|\.jks$|\.keystore$|\.local/", re.I)
FORBIDDEN_MARKERS = ("aiRecommendation", "stagedAction", "revisionHistory", "previousDigest", "\"quote\"", "humanSourceOpenedAttestation")
# Exported components allowed in a release build, with the permission each must require (None = launcher only).
ALLOWED_EXPORTS = {
    "in.jansetu.app.MainActivity": None,
    "in.jansetu.app.vault.VaultDocumentsProvider": "android.permission.MANAGE_DOCUMENTS",
    "androidx.profileinstaller.ProfileInstallReceiver": "android.permission.DUMP",
}


def aapt2() -> Path:
    candidates = sorted((SDK / "build-tools").glob("*/aapt2*"))
    if not candidates:
        raise SystemExit("aapt2 not found in the Android SDK build-tools")
    return candidates[-1]


def manifest(apk: Path) -> dict:
    """Parses `aapt2 dump xmltree` output into permissions, components and flags."""
    text = subprocess.run([str(aapt2()), "dump", "xmltree", "--file", "AndroidManifest.xml", str(apk)],
                          capture_output=True, text=True, check=True).stdout
    info = {"permissions": [], "components": [], "debuggable": False}
    current = None
    for line in text.splitlines():
        element = re.match(r"\s*E: (\S+)", line)
        if element:
            current = {"type": element.group(1)}
            if current["type"] in {"activity", "activity-alias", "provider", "receiver", "service"}:
                info["components"].append(current)
            continue
        attribute = re.match(r'\s*A: (?:http://schemas.android.com/apk/res/android:)?(\w+)(?:\(0x[0-9a-f]+\))?=(.*)', line)
        if not attribute or current is None:
            continue
        name, raw = attribute.group(1), attribute.group(2)
        value = re.match(r'"([^"]*)"', raw).group(1) if raw.startswith('"') else raw.split()[0]
        if current["type"] == "manifest" and name in {"package", "versionCode", "versionName"}:
            info[name] = value
        elif current["type"] == "uses-permission" and name == "name":
            info["permissions"].append(value)
        elif current["type"] == "application" and name == "debuggable":
            info["debuggable"] = value.lower() in {"true", "0xffffffff"}
        elif current["type"] == "uses-sdk" and name in {"minSdkVersion", "targetSdkVersion"}:
            info[name] = value
        elif current in info["components"] and name in {"name", "exported", "permission", "authorities"}:
            current[name] = value
    return info


def inspect(path: Path, release: bool) -> dict:
    problems: list[str] = []
    report: dict = {"artifact": str(path), "bytes": path.stat().st_size, "sha256": hashlib.sha256(path.read_bytes()).hexdigest()}
    with zipfile.ZipFile(path) as z:
        names = z.namelist()
        prefix = "base/" if path.suffix == ".aab" else ""
        assets = {n[len(prefix):] for n in names if n.startswith(prefix + "assets/")}
        report["assets"] = sorted(assets)
        if not ALLOWED_ASSETS <= assets or assets - ALLOWED_ASSETS - BUILD_ASSETS:
            problems.append(f"Unexpected assets: {sorted((assets - BUILD_ASSETS) ^ ALLOWED_ASSETS)}")
        bad = [n for n in names if FORBIDDEN_NAMES.search(n)]
        if bad:
            problems.append(f"Restricted files packaged: {bad[:10]}")
        catalog = z.read(prefix + "assets/catalog.json").decode("utf-8") if prefix + "assets/catalog.json" in names else ""
        markers = [m for m in FORBIDDEN_MARKERS if m in catalog]
        if markers:
            problems.append(f"Internal review material in catalog.json: {markers}")
        claims_path = ROOT / "data/website_enrichment/claims.json"
        if claims_path.exists():
            claims = json.loads(claims_path.read_text(encoding="utf-8"))
            quotes = [e["quote"] for p in claims["websites"] for e in p["evidence"] if len(e["quote"].split()) >= 8]
            leaked = [q[:60] for q in quotes if q in catalog]
            if leaked:
                problems.append(f"Official-page quotations in catalog.json: {leaked[:3]}")
            report["quotationsChecked"] = len(quotes)
    if path.suffix == ".apk":
        info = manifest(path)
        report["manifest"] = {k: info.get(k) for k in ("package", "versionCode", "versionName", "debuggable", "permissions")}
        permissions = set(info["permissions"]) - {f"{info.get('package')}.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"}
        if permissions != ALLOWED_PERMISSIONS:
            problems.append(f"Permissions differ from the reviewed set: {sorted(permissions ^ ALLOWED_PERMISSIONS)}")
        exported = [c for c in info["components"] if c.get("exported", "").lower() in {"true", "0xffffffff"}]
        report["exported"] = [{k: c.get(k) for k in ("type", "name", "permission")} for c in exported]
        for component in exported:
            name = component.get("name", "")
            if release and name not in ALLOWED_EXPORTS:
                problems.append(f"Unexpected exported component: {name}")
            elif name in ALLOWED_EXPORTS and ALLOWED_EXPORTS[name] and component.get("permission") != ALLOWED_EXPORTS[name]:
                problems.append(f"{name} must require {ALLOWED_EXPORTS[name]}")
        if release and info["debuggable"]:
            problems.append("Release artifact is debuggable")
    report["problems"] = problems
    report["passed"] = not problems
    return report


def main() -> None:
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    if len(args) != 1:
        raise SystemExit(__doc__)
    report = inspect(Path(args[0]), "--release" in sys.argv)
    print(json.dumps(report, indent=2))
    if not report["passed"]:
        raise SystemExit(1)


if __name__ == "__main__":
    main()
