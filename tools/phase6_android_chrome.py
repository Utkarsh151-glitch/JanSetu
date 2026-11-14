"""Phase 6A: observe the two selected public pages in the emulator's own Chrome.

Usage: python tools/phase6_android_chrome.py

Opens each public URL in com.android.chrome with a VIEW intent, captures the
first screen and one scrolled screen at Android font scale 1.0 and 2.0. Only
scroll gestures are used; nothing on the page is tapped, typed or submitted.
JanSetu's packages are not involved. Device settings are restored.
"""
from __future__ import annotations

import json
import sys
import time
from datetime import datetime, timezone
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import phase5_screenshots as ui  # noqa: E402

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "docs/screenshots/phase6"
RECORD = ROOT / "data/validation_reports/phase6a-android-chrome.json"
PAGES = [
    ("rti-guidelines", "https://rtionline.gov.in/guidelines.php?request"),
    ("tn-esevai-service-list", "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx"),
]


def capture(name: str, taken: list[str]) -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    (OUT / f"{name}.png").write_bytes(ui.adb("exec-out", "screencap", "-p"))
    taken.append(name)
    print("captured", name, flush=True)


def chrome_view(url: str) -> None:
    ui.adb("shell", "am", "start", "-a", "android.intent.action.VIEW", "-d", url, "-p", "com.android.chrome")
    time.sleep(10)


def swipe() -> None:
    ui.adb("shell", "input", "swipe", "540", "1900", "540", "800", "400")
    time.sleep(2)


def main() -> None:
    original = ui.device_state()
    taken: list[str] = []
    started = datetime.now(timezone.utc).isoformat(timespec="seconds")
    try:
        for scale in ("1.0", "2.0"):
            ui.shell(f"settings put system font_scale {scale}")
            time.sleep(3)
            for page_id, url in PAGES:
                chrome_view(url)
                capture(f"{page_id}-android-chrome-font{scale[0]}", taken)
                swipe()
                capture(f"{page_id}-android-chrome-font{scale[0]}-scrolled", taken)
    finally:
        ui.restore(original)
        ui.adb("shell", "input", "keyevent", "3")
    RECORD.write_text(json.dumps({
        "startedAt": started, "browser": "com.android.chrome on the API 37 emulator", "pages": PAGES,
        "deviceBefore": original, "deviceAfter": ui.device_state(), "captured": taken,
        "scope": "Read-only: VIEW intent plus scroll gestures only."}, indent=2) + "\n", encoding="utf-8")
    print(json.dumps({"captured": taken}, indent=2))


if __name__ == "__main__":
    main()
