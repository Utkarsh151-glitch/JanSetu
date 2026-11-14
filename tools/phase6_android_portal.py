"""Phase 6A: observe the existing National Portal A/A+ control in JanSetu's
in-app browser on the emulator.

Usage: python tools/phase6_android_portal.py

Runs only the isolated in.jansetu.app.qa package (cleared first). Captures the
public National Portal home at WebView text 100% (A) and 125% (A+) with Android
font scale 1.0 and 2.0, plus one landscape view. Nothing on the page is clicked
except JanSetu's own A/A+ control. Every changed device setting is restored.
"""
from __future__ import annotations

import json
import sys
import time
from datetime import datetime, timezone
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import phase5_screenshots as ui  # noqa: E402  (shared, QA-package-only adb helpers)

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "docs/screenshots/phase6"
RECORD = ROOT / "data/validation_reports/phase6a-android-portal.json"


def capture(name: str, taken: list[str]) -> None:
    time.sleep(1.5)
    OUT.mkdir(parents=True, exist_ok=True)
    (OUT / f"{name}.png").write_bytes(ui.adb("exec-out", "screencap", "-p"))
    taken.append(name)
    print("captured", name, flush=True)


def open_portal() -> None:
    labels = ui.LABELS["after"]
    capture_helper = ui.Capture("after")
    capture_helper.fresh()
    ui.tap("Search")
    ui.wait("Search portals")
    ui.replace_search_text("National Portal of India")
    ui.wait("india.gov.in", contains=True)
    ui.hide_keyboard()
    ui.tap("india.gov.in", contains=True, index=0)
    ui.scroll_until(labels["open_in_app"])
    ui.tap(labels["open_in_app"])
    ui.wait("Close browser")
    time.sleep(12)  # allow the public page to load over the network


def main() -> None:
    original = ui.device_state()
    taken: list[str] = []
    notes: dict[str, str] = {}
    started = datetime.now(timezone.utc).isoformat(timespec="seconds")
    try:
        open_portal()
        capture("india-portal-android-a100-font1", taken)
        ui.tap("Larger website text")
        time.sleep(4)
        capture("india-portal-android-a125-font1", taken)
        ui.shell("settings put system font_scale 2.0")
        time.sleep(10)  # configuration change recreates the activity and reloads the page
        capture("india-portal-android-a125-font2", taken)
        if ui.find(ui.tree(), "Normal website text"):
            ui.tap("Normal website text")
            time.sleep(4)
            capture("india-portal-android-a100-font2", taken)
        else:
            notes["font2"] = "A/A+ control not found after font change"
        ui.shell("settings put system font_scale 1.0")
        ui.shell("settings put system accelerometer_rotation 0")
        ui.shell("settings put system user_rotation 1")
        time.sleep(10)
        capture("india-portal-android-landscape-font1", taken)
    finally:
        ui.restore(original)
    RECORD.write_text(json.dumps({
        "startedAt": started, "package": ui.PACKAGE, "url": "https://www.india.gov.in/",
        "deviceBefore": original, "deviceAfter": ui.device_state(), "captured": taken, "notes": notes,
        "scope": "JanSetu in-app WebView, QA package only; only JanSetu's own A/A+ control was used."}, indent=2) + "\n", encoding="utf-8")
    print(json.dumps({"captured": taken, "notes": notes}, indent=2))


if __name__ == "__main__":
    main()
