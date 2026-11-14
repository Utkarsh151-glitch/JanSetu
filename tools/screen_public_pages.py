"""Small, read-only browser screening sample; no crawling or form interaction.

Each listed public page is opened once. Results are environmental observations,
not accessibility certification or a site-speed benchmark.
"""

import json
import time
from datetime import datetime, timezone
from pathlib import Path

from playwright.sync_api import sync_playwright

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "data/screening_runs/2026-10-14.json"
IMAGES = ROOT / "docs/screenshots"
CHROME = r"C:\Program Files\Google\Chrome\Application\chrome.exe"
PAGES = [
    ("india-portal-home", "https://www.india.gov.in/"),
    ("scholarship-home", "https://scholarships.gov.in/home"),
    ("rti-home", "https://rtionline.gov.in/"),
    ("digilocker-home", "https://www.digilocker.gov.in/"),
    ("lakshadweep-home", "https://lakshadweep.gov.in/"),
    ("coimbatore-home", "https://www.ccmc.gov.in/"),
]

METRICS = """() => ({
  viewport: {width: innerWidth, height: innerHeight},
  documentWidth: document.documentElement.scrollWidth,
  horizontalOverflow: document.documentElement.scrollWidth > innerWidth + 2,
  images: document.images.length,
  imagesMissingAlt: [...document.images].filter(el => !el.hasAttribute('alt')).length,
  links: document.querySelectorAll('a[href]').length,
  buttons: document.querySelectorAll('button').length,
  formControls: document.querySelectorAll('input,select,textarea').length,
  smallParagraphs: [...document.querySelectorAll('p')].filter(el =>
    el.getClientRects().length && parseFloat(getComputedStyle(el).fontSize) < 16).length,
  visibleParagraphs: [...document.querySelectorAll('p')].filter(el => el.getClientRects().length).length
})"""


def main() -> None:
    rows = []
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True, executable_path=CHROME)
        try:
            for name, url in PAGES:
                page = browser.new_page(viewport={"width": 390, "height": 844}, device_scale_factor=1)
                row = {"id": name, "url": url, "checkedAt": datetime.now(timezone.utc).isoformat(timespec="seconds")}
                try:
                    started = time.monotonic()
                    response = page.goto(url, wait_until="domcontentloaded", timeout=18000)
                    row["elapsedMsSingleRun"] = round((time.monotonic() - started) * 1000)
                    row["httpStatus"] = response.status if response else None
                    row["finalUrl"] = page.url
                    if response and response.status < 400:
                        page.wait_for_timeout(1800)
                        row["screening"] = page.evaluate(METRICS)
                        shot = IMAGES / f"audit-{name}.png"
                        page.screenshot(path=str(shot))
                        row["screenshot"] = shot.relative_to(ROOT).as_posix()
                    else:
                        row["result"] = "access_or_server_response; no UI conclusion"
                except Exception as error:
                    row["result"] = f"inconclusive:{type(error).__name__}"
                finally:
                    page.close()
                rows.append(row)
                print(f"{name}: {row.get('httpStatus', row.get('result'))}")
                time.sleep(2)
        finally:
            browser.close()
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps({"method": "single read-only Chrome 390x844 viewport", "pages": rows},
                              indent=2) + "\n", encoding="utf-8")
    print(f"Wrote {OUT}")


if __name__ == "__main__":
    main()
