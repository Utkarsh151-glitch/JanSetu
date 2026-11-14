"""Phase 6A: does the TN e-Sevai table clip for desktop readers who zoom in?

Usage: python tools/phase6_tn_desktop_zoom.py

One read-only headless Chrome load of the public page at 1280x800, then the same
document resized to the CSS widths a 1280px window has at 125%, 150% and 200%
browser zoom (1024, 853, 640). Records whether the fee/document/apply columns can
be scrolled into view. Nothing is clicked or typed; the page is not styled.
"""
from __future__ import annotations

import json
from datetime import datetime, timezone
from pathlib import Path

from playwright.sync_api import sync_playwright

ROOT = Path(__file__).resolve().parents[1]
CHROME = r"C:\Program Files\Google\Chrome\Application\chrome.exe"
URL = "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx"
OUT = ROOT / "data/validation_reports/phase6a-tn-desktop-zoom.json"
PROBE = r"""() => {
  const c = document.querySelector('div.smooth-overflow');
  const header = [...document.querySelectorAll('th')].find(th => /link to apply/i.test(th.innerText));
  const r = header ? header.getBoundingClientRect() : null;
  return {viewportWidth: innerWidth, containerFound: !!c, overflowX: c ? getComputedStyle(c).overflowX : null,
          clientWidth: c ? c.clientWidth : null, scrollWidth: c ? c.scrollWidth : null,
          applyColumnRight: r ? Math.round(r.right) : null,
          applyColumnFullyVisible: r ? r.right <= innerWidth : null,
          clippedWithoutScroll: c ? (c.scrollWidth > c.clientWidth + 1 && getComputedStyle(c).overflowX === 'hidden') : null};
}"""


def main() -> None:
    rows = []
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True, executable_path=CHROME)
        version = browser.version
        context = browser.new_context(viewport={"width": 1280, "height": 800}, ignore_https_errors=False)
        page = context.new_page()
        response = page.goto(URL, wait_until="domcontentloaded", timeout=30000)
        page.wait_for_timeout(3000)
        for zoom, width in (("100%", 1280), ("125%", 1024), ("150%", 853), ("200%", 640)):
            page.set_viewport_size({"width": width, "height": round(800 * width / 1280)})
            page.wait_for_timeout(700)
            rows.append({"desktopZoomEquivalent": zoom, **page.evaluate(PROBE)})
        status = response.status if response else None
        context.close()
        browser.close()
    result = {"checkedAt": datetime.now(timezone.utc).isoformat(timespec="seconds"), "browser": f"Chrome {version} (headless)",
              "url": URL, "httpStatus": status, "method": "One read-only load; CSS viewport widths equal to a 1280px window at each zoom.",
              "rows": rows}
    OUT.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
