"""One-page, read-only check of the National Portal directory's dynamic requests.

Run manually, at most once per research session. This does not follow result links,
extract records, bypass access controls, or provide a bulk import endpoint.
"""

import argparse
from pathlib import Path
from urllib.parse import urljoin, urlsplit

from playwright.sync_api import sync_playwright

URL = "https://www.india.gov.in/directory/web-directory/state-uts"
CHROME = r"C:\Program Files\Google\Chrome\Application\chrome.exe"


def browser_probe() -> None:
    observed: set[tuple[str, str, str]] = set()
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True, executable_path=CHROME)
        try:
            page = browser.new_page(viewport={"width": 1280, "height": 900})

            def record(request) -> None:
                if request.resource_type in {"xhr", "fetch"}:
                    parsed = urlsplit(request.url)
                    observed.add((request.resource_type, parsed.hostname or "", parsed.path))

            page.on("request", record)
            try:
                response = page.goto(URL, wait_until="domcontentloaded", timeout=20000)
                page.wait_for_timeout(4000)
                print(f"HTTP {response.status if response else 'no response'}; final URL {page.url}")
                print(f"Title: {page.title()}")
                for kind, host, path in sorted(observed):
                    print(f"{kind}: {host}{path}")
            except Exception as error:
                print(f"Inconclusive: {type(error).__name__}")
        finally:
            browser.close()


def source_probes():
    """One cached public check per secondary source; no API keys or scraping bypass."""
    from bs4 import BeautifulSoup
    from government_sources import CachedClient, check_robots, now, save_json, redact_tokens
    directory = Path(__file__).resolve().parents[1] / "data/collection/secondary"
    client = CachedClient(directory / "snapshots", delay=2, timeout=20)
    results = []
    for source_id, urls in [
        ("india-web-directory", ["https://www.india.gov.in/directory/web-directory"]),
        ("data-gov", ["https://data.gov.in/", "https://data.gov.in/catalog/local-government-directory-lgd"]),
        ("lgd", ["https://lgdirectory.gov.in/"]),
    ]:
        origin = "https://" + urlsplit(urls[0]).netloc
        robots_meta, robots = check_robots(client, origin)
        result = {"sourceId": source_id, "checkedAt": now(), "robots": robots_meta, "pages": []}
        if robots is None:
            result["result"] = "ROBOTS_UNRESOLVED; no category enumeration or empty-count inference"
        else:
            client.delay = max(2, robots_meta.get("crawlDelay") or 0)
            for url in urls:
                if not robots.can_fetch("JanSetuLocalInventory", url):
                    result["pages"].append({"url": url, "status": "ROBOTS_DISALLOWED"})
                    continue
                meta, html = client.fetch(url)
                if 300 <= meta.get("httpStatus", 0) < 400 and meta.get("location"):
                    target = urljoin(url, meta["location"])
                    if urlsplit(target).scheme == "https" and urlsplit(target).hostname.removeprefix("www.") == urlsplit(url).hostname.removeprefix("www."):
                        final_robots, final_parser = check_robots(client, "https://" + urlsplit(target).netloc)
                        if final_parser is not None and final_parser.can_fetch("JanSetuLocalInventory", target):
                            old_url = url
                            meta, html = client.fetch(target)
                            meta["redirectedFrom"] = old_url
                page = dict(meta)
                if meta["status"] == "OK" and meta["httpStatus"] == 200:
                    soup = BeautifulSoup(html, "html.parser")
                    page["title"] = soup.title.get_text(" ", strip=True) if soup.title else None
                    page["visibleTextSample"] = soup.get_text(" ", strip=True)[:3500]
                    page["publicRelevantLinks"] = [{"name": a.get_text(" ", strip=True), "url": redact_tokens(urljoin(meta["url"], a["href"]))}
                        for a in soup.select('a[href]') if any(w in a.get_text(" ", strip=True).lower() for w in ("district", "download", "api", "policy", "copyright", "state", "local bod"))][:100]
                    if source_id == "lgd":
                        import re
                        body = soup.get_text(" ", strip=True)
                        page["administrativeTotals"] = {key: int(m[1]) for key, pattern in {
                            "statesAndUTs": r"No\. of States/Union Territories \(UTs\)\s+(\d+)",
                            "districts": r"No\. of Districts\s+(\d+)",
                            "urbanLocalBodies": r"No\. of Urban Local Bodies\s+(\d+)",
                            "ruralLocalBodies": r"No\. of Rural Local Bodies\s+(\d+)",
                        }.items() if (m := re.search(pattern, body, re.I))}
                result["pages"].append(page)
        results.append(result)
        save_json(directory / "observations.json", results)
        print(source_id, result.get("result") or [(p["url"], p["status"], p.get("httpStatus")) for p in result["pages"]], flush=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--official-source-probes", action="store_true")
    args = parser.parse_args()
    if args.official_source_probes:
        source_probes()
    else:
        browser_probe()


if __name__ == "__main__":
    main()
