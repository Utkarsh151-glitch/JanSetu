"""Capture before/after browser screenshots of reviewed public pages without installing the extension."""

from pathlib import Path
from playwright.sync_api import sync_playwright

ROOT = Path(__file__).resolve().parents[1]
CSS = ROOT / "data/retired/nsp-readable-text.css"
OUT = ROOT / "docs/screenshots"
PAGES = {
    "india-portal": "https://www.india.gov.in/",
    "national-scholarship-portal": "https://scholarships.gov.in/home",
    "uidai": "https://uidai.gov.in/en/my-aadhaar",
}


def main() -> None:
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(
            headless=True, executable_path=r"C:\Program Files\Google\Chrome\Application\chrome.exe")
        try:
            for name, url in PAGES.items():
                page = browser.new_page(viewport={"width": 1280, "height": 900}, device_scale_factor=1)
                try:
                    response = page.goto(url, wait_until="domcontentloaded", timeout=20000)
                    if response is None or response.status >= 400:
                        print(f"{name}: HTTP {response.status if response else 'no response'}; no visual claim")
                        continue
                    page.wait_for_timeout(2500)
                    page.evaluate("window.scrollTo(0, 700)")
                    page.wait_for_timeout(200)
                    page.screenshot(path=str(OUT / f"{name}-original.png"))
                    page.add_style_tag(path=str(CSS))
                    page.wait_for_timeout(200)
                    page.screenshot(path=str(OUT / f"{name}-readable.png"))
                    print(f"{name}: HTTP {response.status}; before/after screenshots captured")
                except Exception as error:
                    print(f"{name}: {type(error).__name__}; no visual claim")
                finally:
                    page.close()
        finally:
            browser.close()


if __name__ == "__main__":
    main()
