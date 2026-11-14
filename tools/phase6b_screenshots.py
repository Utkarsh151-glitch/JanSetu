"""Capture Phase 6B website-information evidence from the isolated QA app.

Usage: python tools/phase6b_screenshots.py

Reuses the Phase 5 UI Automator helpers. Only in.jansetu.app.qa is installed,
cleared and driven; the preview package is never read, stopped or changed.
Screenshots go to docs/screenshots/phase6b/.
"""
from __future__ import annotations

import json
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from phase5_screenshots import (ROOT, Capture, adb, back, find, hide_keyboard, scroll_top, scroll_until,  # noqa: E402
                                shell, swipe_up, tap, tree, type_into, wait)

QA_APK = ROOT / "app/build/outputs/apk/qa/app-qa.apk"


class Phase6bCapture(Capture):
    def __init__(self):
        super().__init__("after")
        self.out = ROOT / "docs/screenshots/phase6b"
        self.out.mkdir(parents=True, exist_ok=True)

    def search_for(self, query: str) -> None:
        scroll_top(4)
        wait("Search portals")
        # The app's own clear button is more reliable than deleting key by key.
        if find(tree(), "Clear search text"):
            tap("Clear search text")
        type_into(self.labels["field"], query)
        # Swipes that start on an open keyboard become glide-typing and add words to the query.
        hide_keyboard()

    def open_website(self, query: str, card_text: str) -> None:
        self.search_for(query)
        scroll_until(card_text, contains=True)
        tap(card_text, contains=True, index=0)
        wait("What this website is for")

    def run(self) -> None:
        self.fresh()
        tap("Search")
        self.search_for("grievance")
        wait("verified services", timeout=30, contains=True)
        scroll_until("Purpose checked", contains=True)
        self.shot("search-checked-label")
        self.open_website("CPGRAMS", "pgportal.gov.in · Source checked")
        self.shot("detail-checked-top")
        for index in range(1, 4):
            swipe_up(); swipe_up()
            self.shot(f"detail-checked-{index}")
        back()
        self.open_website("Adilabad", "Listed by IGOD")
        self.shot("detail-directory-top")
        swipe_up()
        self.shot("detail-directory-missing")
        swipe_up(); swipe_up(); swipe_up()
        self.shot("detail-directory-sources")
        back()
        self.open_website("electoral roll", "electoralsearch.eci.gov.in")
        swipe_up(); swipe_up(); swipe_up()
        self.shot("detail-not-stated")
        back()
        self.search_for("scholarship")
        wait("verified services", timeout=30, contains=True)
        tap("Services and websites")
        tap("Websites with checked information")
        wait("Purpose checked", timeout=30, contains=True)
        self.shot("search-checked-filter")


def main() -> None:
    before = shell("pm list packages in.jansetu.app")
    adb("install", "-r", str(QA_APK), timeout=180)
    capture = Phase6bCapture()
    capture.run()
    (capture.out / "capture.json").write_text(json.dumps({"package": "in.jansetu.app.qa", "apk": QA_APK.name,
                                                           "screens": capture.taken, "packagesBefore": before.split()}, indent=1) + "\n",
                                              encoding="utf-8")


if __name__ == "__main__":
    main()
