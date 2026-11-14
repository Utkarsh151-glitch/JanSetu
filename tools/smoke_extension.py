"""Render the extension panel in Chrome without installing it into a user profile."""

from functools import partial
import json
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from threading import Thread

from playwright.sync_api import sync_playwright

EXTENSION = Path(__file__).resolve().parents[1] / "extension"


def main() -> None:
    handler = partial(SimpleHTTPRequestHandler, directory=str(EXTENSION))
    server = ThreadingHTTPServer(("127.0.0.1", 0), handler)
    Thread(target=server.serve_forever, daemon=True).start()
    origin = f"http://127.0.0.1:{server.server_port}"
    try:
        with sync_playwright() as playwright:
            browser = playwright.chromium.launch(
                headless=True, executable_path=r"C:\Program Files\Google\Chrome\Application\chrome.exe")
            try:
                page = browser.new_page(viewport={"width": 420, "height": 840})
                errors = []
                page.on("pageerror", lambda error: errors.append(str(error)))
                page.add_init_script("""window.chrome = {
                  runtime: {getURL: path => new URL(path, location.href).href},
                  tabs: {query: async () => []}
                };""")
                page.goto(f"{origin}/panel.html")
                page.get_by_role("heading", name="Find a portal").wait_for()
                catalog = json.loads((EXTENSION / "catalog.json").read_text(encoding="utf-8"))
                listed = sum(s["verificationStatus"] == "directory_listed" for s in catalog["websites"])
                assert page.get_by_text(f"{len(catalog['websites'])} portals · {listed} directory listed").is_visible()
                assert page.locator('#results article.card').count() == min(30, len(catalog['websites']))
                if len(catalog['websites']) > 30:
                    page.get_by_role('button', name='Show more portals').click()
                    assert page.locator('#results article.card').count() == min(60, len(catalog['websites']))
                page.screenshot(path=str(EXTENSION.parent / "docs/screenshots/extension-panel.png"), full_page=True)
                page.get_by_role("searchbox").fill("CPGRAMS")
                assert page.get_by_text("1 portal · 0 directory listed").is_visible()
                assert page.get_by_role("heading", name="CPGRAMS").is_visible()
                page.get_by_role("searchbox").fill("not-a-real-portal-991299")
                assert page.get_by_text("No matching portal", exact=False).is_visible()
                with page.expect_popup() as policy_popup:
                    page.get_by_role("link", name="Privacy policy").click()
                policy_page = policy_popup.value
                policy_page.get_by_text("JanSetu Privacy Policy", exact=False).wait_for()
                assert policy_page.get_by_text("utkarshvaibhav268@gmail.com", exact=False).is_visible()
                assert not errors, errors
                companion = browser.new_page(viewport={"width": 420, "height": 840})
                companion.add_init_script("""window.chrome = {
                  runtime: {getURL: path => new URL(path, location.href).href},
                  tabs: {query: async () => [{url: 'https://www.digilocker.gov.in/dashboard'}]}
                };""")
                companion.goto(f"{origin}/panel.html")
                companion.get_by_role("heading", name="Find an issued document").wait_for()
                assert companion.get_by_text("Guide reviewed 2026-10-13").is_visible()
                companion.screenshot(path=str(EXTENSION.parent / "docs/screenshots/extension-guide.png"), full_page=True)
                # The withdrawn Readable text pilot is gone: no page-appearance controls on the former pilot page.
                nsp = browser.new_page(viewport={"width": 420, "height": 840})
                nsp.add_init_script("""window.chrome = {
                  runtime: {getURL: path => new URL(path, location.href).href},
                  tabs: {query: async () => [{id: 42, url: 'https://scholarships.gov.in/home'}]}
                };""")
                nsp.goto(f"{origin}/panel.html")
                nsp.get_by_text("ON THIS WEBSITE").wait_for()
                assert nsp.get_by_role("button", name="Readable text").count() == 0
                assert nsp.get_by_role("button", name="Restore original").count() == 0
                print("Panel rendered; catalog, search, privacy and guide passed in Chrome; no page-appearance controls remain")
            finally:
                browser.close()
    finally:
        server.shutdown()
        server.server_close()


if __name__ == "__main__":
    main()
