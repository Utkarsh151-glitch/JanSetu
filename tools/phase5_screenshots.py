"""Capture reproducible isolated-QA Phase 5 UI evidence.

Usage: python tools/phase5_screenshots.py before|after

`before` drives the retained pre-Phase-5 QA build (.local/phase4-baseline-qa.apk)
and `after` drives the current QA build. Both runs use the same emulator display
settings, synthetic data and screen sequence so captures are comparable.

This script only clears and drives in.jansetu.app.qa. It never reads, writes,
stops or uninstalls the preview package. Every device setting it changes
(night mode, font scale, density, rotation, network) is recorded first and
restored in a finally block.
"""
from __future__ import annotations

import json
import os
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from datetime import date, timedelta
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ADB = Path(os.environ["LOCALAPPDATA"]) / "Android/Sdk/platform-tools/adb.exe"
PACKAGE = "in.jansetu.app.qa"
ACTIVITY = PACKAGE + "/in.jansetu.app.MainActivity"

# Labels that differ between the pre-Phase-5 baseline and the redesign.
LABELS = {
    "before": {
        "ready": "What do you need to do?",
        "field": "What are you looking for?",
        "explore_hi": "जनसेतु देखें",
        "deadline": "Your deadline (optional, YYYY-MM-DD)",
        "consent": "Consent to local application storage",
        "service_detail": "Verified service information",
        "website_detail": "Source verified",
        "open_in_app": "Open official website in JanSetu",
        "catalog": "Offline catalog and updates",
    },
    "after": {
        "ready": "What government service do you need?",
        "field": "What government service do you need?",
        "explore_hi": "जनसेतु देखें",
        "deadline": "Your deadline (optional)",
        "consent": "Store this application on this device under the local privacy terms above.",
        "service_detail": "Verified service information",
        "website_detail": "Source verified",
        "open_in_app": "Open official website in JanSetu",
        "catalog": "Offline catalog and updates",
    },
}


def adb(*args: str, timeout: float = 60, check: bool = True) -> bytes:
    result = subprocess.run([str(ADB), *args], capture_output=True, timeout=timeout)
    if check and result.returncode:
        raise RuntimeError(f"ADB failed: {' '.join(args)}\n{result.stderr.decode(errors='replace')}")
    return result.stdout


def shell(command: str) -> str:
    return adb("shell", command).decode(errors="replace").strip()


def tree() -> ET.Element:
    for attempt in range(4):
        try:
            adb("shell", "uiautomator", "dump", "/sdcard/phase5-qa.xml")
            return ET.fromstring(adb("exec-out", "cat", "/sdcard/phase5-qa.xml"))
        except (RuntimeError, ET.ParseError):
            time.sleep(1 + attempt)
    raise RuntimeError("UI hierarchy unavailable")


def values(node: ET.Element) -> list[str]:
    return [v for v in (node.get("text"), node.get("content-desc")) if v]


def texts(root: ET.Element) -> list[str]:
    return [v for node in root.iter("node") for v in values(node)]


def find(root: ET.Element, label: str, contains: bool = False) -> list[ET.Element]:
    return [n for n in root.iter("node") if any((label in v) if contains else (v == label) for v in values(n))]


def wait(label: str, timeout: float = 45, contains: bool = False) -> ET.Element:
    end = time.monotonic() + timeout
    while time.monotonic() < end:
        root = tree()
        if find(root, label, contains):
            return root
        time.sleep(0.4)
    raise TimeoutError(f"QA label unavailable: {label}")


def centre(node: ET.Element) -> tuple[int, int]:
    x1, y1, x2, y2 = map(int, re.findall(r"\d+", node.get("bounds", "")))
    return (x1 + x2) // 2, (y1 + y2) // 2


def tap(label: str, contains: bool = False, index: int | None = None) -> None:
    root = wait(label, contains=contains)
    nodes = find(root, label, contains)
    if index is None and len(nodes) != 1:
        raise ValueError(f"Expected one visible '{label}', found {len(nodes)}")
    x, y = centre(nodes[0 if index is None else index])
    adb("shell", "input", "tap", str(x), str(y))
    time.sleep(0.6)


def screen_size() -> tuple[int, int]:
    root = tree()
    first = next(root.iter("node"))
    _, _, w, h = map(int, re.findall(r"\d+", first.get("bounds", "0,0,1080,2400")))
    return w, h


def swipe_up() -> None:
    w, h = screen_size()
    adb("shell", "input", "swipe", str(w // 2), str(int(h * 0.78)), str(w // 2), str(int(h * 0.32)), "300")
    time.sleep(0.7)


def swipe_down() -> None:
    w, h = screen_size()
    adb("shell", "input", "swipe", str(w // 2), str(int(h * 0.3)), str(w // 2), str(int(h * 0.85)), "300")
    time.sleep(0.7)


def scroll_until(label: str, attempts: int = 10, contains: bool = False) -> None:
    for _ in range(attempts):
        if find(tree(), label, contains):
            return
        swipe_up()
    wait(label, timeout=3, contains=contains)


def scroll_top(times: int = 6) -> None:
    for _ in range(times):
        swipe_down()


def keyboard_shown() -> bool:
    state = shell("dumpsys input_method")
    return "mInputShown=true" in state or "isInputViewShown=true" in state


def hide_keyboard() -> None:
    for _ in range(3):
        if not keyboard_shown():
            return
        adb("shell", "input", "keyevent", "111")  # ESC
        time.sleep(0.5)
        if keyboard_shown():
            adb("shell", "input", "keyevent", "4")  # BACK closes only the keyboard
            time.sleep(0.6)


def type_into(label: str, value: str) -> None:
    tap(label)
    adb("shell", "input", "text", value.replace(" ", "%s"))
    time.sleep(0.8)


def clear_focused_field(length: int = 40) -> None:
    adb("shell", "input", "keyevent", "123")  # MOVE_END
    adb("shell", "input", "keyevent", *(["67"] * length))


def edit_field(root: ET.Element | None = None) -> ET.Element | None:
    root = root if root is not None else tree()
    return next((n for n in root.iter("node") if n.get("class") == "android.widget.EditText"), None)


def replace_search_text(value: str) -> None:
    field = edit_field()
    if field is None:
        raise RuntimeError("Search field unavailable")
    x, y = centre(field)
    adb("shell", "input", "tap", str(x), str(y))
    time.sleep(0.5)
    clear_focused_field()
    adb("shell", "input", "text", value.replace(" ", "%s"))
    time.sleep(0.8)


def scroll_up_until(label: str, attempts: int = 10) -> None:
    for _ in range(attempts):
        if find(tree(), label):
            return
        swipe_down()
    wait(label, timeout=3)


def back() -> None:
    hide_keyboard()
    adb("shell", "input", "keyevent", "4")
    time.sleep(1.0)


class Capture:
    def __init__(self, which: str):
        self.which = which
        self.labels = LABELS[which]
        self.out = ROOT / "docs/screenshots/phase5" / which
        self.out.mkdir(parents=True, exist_ok=True)
        self.taken: list[str] = []

    def shot(self, name: str, settle: float = 1.2) -> None:
        assert re.fullmatch(r"[a-z0-9-]+", name)
        hide_keyboard()
        time.sleep(settle)
        root = tree()
        assert any(node.get("package") == PACKAGE for node in root.iter("node")), "QA app is not visible"
        (self.out / f"{name}.png").write_bytes(adb("exec-out", "screencap", "-p"))
        self.taken.append(name)
        print(f"captured {self.which}/{name}", flush=True)

    def fresh(self, hindi: bool = False, onboarding_shot: bool = False) -> None:
        # The QA app has its own UID, Keystore, files and preferences by build type.
        adb("shell", "pm", "clear", PACKAGE)
        adb("shell", "am", "start", "-W", "-n", ACTIVITY)
        wait("Explore JanSetu")
        if onboarding_shot:
            self.shot("onboarding")
        if hindi:
            scroll_until("हिन्दी")
            tap("हिन्दी")
            wait(self.labels["explore_hi"])
        explore = self.labels["explore_hi"] if hindi else "Explore JanSetu"
        # Freshly cleared emulator activities sometimes ignore their first
        # Compose click. Tap from the same snapshot that showed the button,
        # then re-check, rather than waiting for a button that has gone.
        for _ in range(8):
            nodes = find(tree(), explore)
            if not nodes:
                break
            x, y = centre(nodes[0])
            adb("shell", "input", "tap", str(x), str(y))
            time.sleep(2)
        if not hindi:
            wait(self.labels["ready"], timeout=60)
        else:
            wait("होम", timeout=60)

    def go_home(self) -> None:
        """Return to Home. The pre-Phase-5 build can restore a stale child
        Search route when Home is tapped, so retry and fall back to Back."""
        for attempt in range(4):
            tap("Home") if attempt < 2 else back()
            # Home restores its earlier scroll position; return to the top first.
            scroll_top(3)
            try:
                wait(self.labels["ready"], timeout=6)
                return
            except TimeoutError:
                continue
        raise TimeoutError("Home unavailable")

    def main_journeys(self) -> None:
        L = self.labels
        self.fresh(onboarding_shot=True)
        self.shot("home")
        swipe_up(); swipe_up()
        self.shot("home-scrolled")
        tap("Search")
        wait("Search portals")
        type_into(L["field"], "DigiLocker")
        wait("www.digilocker.gov.in", contains=True)
        self.shot("search-results")
        tap("www.digilocker.gov.in", contains=True)
        wait(L["website_detail"])
        self.shot("website-detail")
        swipe_up(); swipe_up()
        self.shot("website-detail-sections")
        back()
        wait("Search portals")
        scroll_top(3)
        replace_search_text("zzqx portal 991")
        wait("No matching portals", timeout=30)
        self.shot("search-empty")
        replace_search_text("income certificate")
        wait("verified services", timeout=30, contains=True)
        self.shot("service-results")
        scroll_until("Income Certificate (Kerala)")
        tap("Income Certificate (Kerala)")
        wait(L["service_detail"])
        self.shot("service-detail")
        swipe_up()
        self.shot("service-detail-actions")
        swipe_up(); swipe_up()
        self.shot("service-detail-sections")
        back()
        tap("Applications")
        wait("No applications yet")
        self.shot("applications-empty")
        tap("Add an application manually")
        wait("Application title")
        self.shot("application-editor")
        type_into("Application title", "Phase 5 demo application")
        scroll_until(L["deadline"])
        type_into(L["deadline"], "2026-02-30")
        scroll_until("Save application")
        tap("Save application")
        time.sleep(1)
        self.shot("application-editor-errors")
        scroll_up_until("2026-02-30")
        x, y = centre(find(tree(), "2026-02-30")[0])
        adb("shell", "input", "tap", str(x), str(y))
        time.sleep(0.4)
        clear_focused_field(12)
        adb("shell", "input", "text", (date.today() + timedelta(days=5)).isoformat())
        hide_keyboard()
        scroll_until(L["consent"])
        tap(L["consent"])
        scroll_until("Save application")
        tap("Save application")
        wait("Application details")
        self.shot("application-detail")
        swipe_up(); swipe_up()
        self.shot("application-history")
        back()
        wait("My Applications")
        self.shot("applications-list")

    def settings_pages(self) -> None:
        L = self.labels
        self.go_home()
        tap("Settings")
        wait("Settings")
        self.shot("settings")
        swipe_up(); swipe_up()
        self.shot("settings-more")
        scroll_until(L["catalog"])
        tap(L["catalog"])
        wait("Import catalog update")
        self.shot("catalog")
        back()
        if self.which == "after":
            scroll_until("Help & guides")
            tap("Help & guides")
            wait("Finding what you need", timeout=20)
            self.shot("help")
            back()
        back()

    def browser(self, offline: bool) -> None:
        L = self.labels
        if offline:
            # Start from cleared QA data so the WebView has no cached copy of the page.
            self.fresh()
        tap("Search")
        wait("Search portals")
        scroll_top(3)
        replace_search_text("National Portal of India")
        wait("india.gov.in", contains=True)
        hide_keyboard()
        tap("india.gov.in", contains=True, index=0)
        scroll_until(L["open_in_app"])
        if offline:
            shell("cmd connectivity airplane-mode enable")
            shell("svc wifi disable"); shell("svc data disable")
            time.sleep(3)
        try:
            tap(L["open_in_app"])
            wait("Close browser")
            if offline:
                wait("could not load", timeout=40, contains=True)
                self.shot("browser-error", settle=1.5)
            else:
                time.sleep(8)
                self.shot("browser-controls", settle=1)
        finally:
            if offline:
                shell("cmd connectivity airplane-mode disable")
                shell("svc wifi enable"); shell("svc data enable")
        back()
        time.sleep(1)
        back()

    def dark_variants(self) -> None:
        shell("cmd uimode night yes")
        time.sleep(3)
        self.go_home()
        self.shot("home-dark")
        tap("Search")
        wait("Search portals")
        self.shot("search-dark")
        tap("Applications")
        wait("My Applications")
        self.shot("applications-list-dark")
        self.go_home()
        tap("Settings")
        wait("Settings")
        self.shot("settings-dark")
        back()
        shell("cmd uimode night no")
        time.sleep(3)

    def large_narrow(self) -> None:
        shell("settings put system font_scale 2.0")
        shell("wm density 540")
        time.sleep(4)
        self.go_home()
        self.shot("home-large-narrow")
        swipe_up()
        self.shot("home-large-narrow-scrolled")
        tap("Applications")
        wait("My Applications")
        swipe_up()
        self.shot("applications-large-narrow")
        tap("Add an application manually")
        wait("Add application")
        self.shot("application-editor-large-narrow")
        back()
        shell("wm density reset")
        shell("settings put system font_scale 1.0")
        time.sleep(4)

    def landscape(self) -> None:
        shell("settings put system accelerometer_rotation 0")
        shell("settings put system user_rotation 1")
        time.sleep(4)
        self.go_home()
        self.shot("home-landscape")
        shell("settings put system user_rotation 0")
        time.sleep(3)

    def hindi(self) -> None:
        self.fresh(hindi=True)
        self.shot("home-hindi")
        tap("सेटिंग्स")
        time.sleep(2)
        self.shot("settings-hindi")
        back()

    def launch_branding(self) -> None:
        """After-only: Android 12+ splash on a cold start, then the launcher icon."""
        # Launch and capture in one on-device command so the frame is taken
        # while the system splash is still showing.
        # The splash lasts only until the first frame, so take three captures
        # back to back on the device and keep each; review picks the splash frame.
        shell(f"am force-stop {PACKAGE}; sleep 1; am start -n {ACTIVITY} >/dev/null; "
              "screencap -p /sdcard/sp1.png; screencap -p /sdcard/sp2.png; screencap -p /sdcard/sp3.png")
        for index in (1, 2, 3):
            (self.out / f"splash-frame-{index}.png").write_bytes(adb("exec-out", "cat", f"/sdcard/sp{index}.png"))
            self.taken.append(f"splash-frame-{index}")
        time.sleep(4)
        adb("shell", "input", "keyevent", "3")  # HOME
        time.sleep(1.5)
        adb("shell", "input", "keyevent", "284")  # ALL_APPS opens the app drawer
        time.sleep(2.5)
        (self.out / "launcher.png").write_bytes(adb("exec-out", "screencap", "-p"))
        self.taken.append("launcher")
        adb("shell", "input", "keyevent", "3")
        time.sleep(1)
        adb("shell", "am", "start", "-W", "-n", ACTIVITY)
        wait(self.labels["ready"], timeout=60)

    def in_app_dark(self) -> None:
        """After-only: the in-app Appearance setting overrides a light system theme."""
        self.fresh()
        tap("Settings")
        wait("Settings")
        tap("Dark")
        time.sleep(2)
        self.shot("settings-dark-in-app")
        back()
        wait(self.labels["ready"])
        self.shot("home-dark-in-app")


def device_state() -> dict[str, str]:
    return {
        "night": shell("cmd uimode night"),
        "font_scale": shell("settings get system font_scale"),
        "density": shell("wm density"),
        "size": shell("wm size"),
        "accelerometer_rotation": shell("settings get system accelerometer_rotation"),
        "user_rotation": shell("settings get system user_rotation"),
        "animator_duration_scale": shell("settings get global animator_duration_scale"),
        "transition_animation_scale": shell("settings get global transition_animation_scale"),
        "wifi": shell("settings get global wifi_on"),
        "mobile_data": shell("settings get global mobile_data"),
        "airplane_mode": shell("settings get global airplane_mode_on"),
        "sdk": shell("getprop ro.build.version.sdk"),
    }


def restore(original: dict[str, str]) -> None:
    shell("cmd connectivity airplane-mode " + ("enable" if original.get("airplane_mode", "0").strip() == "1" else "disable"))
    shell("svc wifi enable" if original["wifi"].strip() == "1" else "svc wifi disable")
    shell("svc data enable" if original["mobile_data"].strip() == "1" else "svc data disable")
    shell("cmd uimode night " + ("yes" if "yes" in original["night"] else "no"))
    shell(f"settings put system font_scale {original['font_scale']}")
    shell("wm density reset")
    if original["accelerometer_rotation"] not in ("null", ""):
        shell(f"settings put system accelerometer_rotation {original['accelerometer_rotation']}")
    if original["user_rotation"] not in ("null", ""):
        shell(f"settings put system user_rotation {original['user_rotation']}")


def main() -> None:
    which = sys.argv[1] if len(sys.argv) > 1 else ""
    if which not in LABELS:
        raise SystemExit("Usage: python tools/phase5_screenshots.py before|after")
    assert adb("get-state").strip() == b"device"
    assert b"package:" in adb("shell", "pm", "path", PACKAGE)
    installed = shell(f"dumpsys package {PACKAGE}")
    original = device_state()
    capture = Capture(which)
    started = time.time()
    failures: list[str] = []
    only = next((a.split("=", 1)[1].split(",") for a in sys.argv[2:] if a.startswith("--only=")), None)
    try:
        steps = {
            "main_journeys": capture.main_journeys,
            "settings_pages": capture.settings_pages,
            "browser": lambda: capture.browser(False),
            "browser_offline": lambda: capture.browser(True),
            "dark_variants": capture.dark_variants,
            "large_narrow": capture.large_narrow,
            "landscape": capture.landscape,
            "hindi": capture.hindi,
        }
        if which == "after":
            steps["in_app_dark"] = capture.in_app_dark
            steps["launch_branding"] = capture.launch_branding
        if only:
            unknown = set(only) - set(steps)
            if unknown:
                raise SystemExit(f"Unknown steps: {sorted(unknown)}")
            capture.fresh()
        for name, step in steps.items():
            if only and name not in only:
                continue
            try:
                print(f"step {name}", flush=True)
                step()
            except Exception as error:  # Record and continue; a missing capture is reported, never faked.
                failures.append(f"{name}: {error}")
                print(f"STEP FAILED: {failures[-1]}", flush=True)
                restore(original)
                try:
                    capture.fresh()
                except Exception as recovery:
                    failures.append(f"recovery: {recovery}")
    finally:
        restore(original)
        record = {
            "set": which, "package": PACKAGE,
            "versionName": re.search(r"versionName=(\S+)", installed).group(1) if "versionName=" in installed else None,
            "lastUpdateTime": re.search(r"lastUpdateTime=([^\n]+)", installed).group(1).strip() if "lastUpdateTime=" in installed else None,
            "deviceBefore": original, "deviceAfter": device_state(),
            "captured": capture.taken, "failedSteps": failures, "seconds": round(time.time() - started, 1),
        }
        (capture.out / f"capture-record{'-' + '-'.join(only) if only else ''}.json").write_text(json.dumps(record, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        print(json.dumps({k: record[k] for k in ("set", "captured", "failedSteps", "seconds")}, indent=2, ensure_ascii=False))


if __name__ == "__main__":
    main()
