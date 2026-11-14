"""Conservative live link audit of published portal URLs; never changes the catalog.

Run from the repository root: python tools/audit_links.py
The report distinguishes automated access failures from proof of a broken site.
"""

import json
import argparse
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime, timezone
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.parse import urljoin, urlparse
from urllib.request import HTTPRedirectHandler, Request, build_opener

from check_catalog import secure_url

ROOT = Path(__file__).resolve().parents[1]
CATALOG = ROOT / "app/src/main/assets/catalog.json"
REPORT = ROOT / "docs/LINK_AUDIT.md"


def same_host(a: str, b: str) -> bool:
    return urlparse(a).hostname.lower().removeprefix("www.") == \
        urlparse(b).hostname.lower().removeprefix("www.")


class ReviewRedirect(Exception):
    def __init__(self, destination: str):
        self.destination = destination


class ConservativeRedirect(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        destination = urljoin(req.full_url, newurl)
        if not secure_url(destination) or not same_host(req.full_url, destination):
            raise ReviewRedirect(destination)
        return super().redirect_request(req, fp, code, msg, headers, destination)


def probe(site: dict) -> dict:
    url = site["url"]
    if not secure_url(url):
        return {"id": site["id"], "url": url, "result": "invalid_catalog_url"}
    opener = build_opener(ConservativeRedirect())
    request_headers = {"User-Agent": "JanSetuLinkAudit/0.1 (+manual editorial review)"}
    try:
        for method in ("HEAD", "GET"):
            headers = {**request_headers, **({"Range": "bytes=0-0"} if method == "GET" else {})}
            request = Request(url, headers=headers, method=method)
            try:
                with opener.open(request, timeout=10) as response:
                    return {"id": site["id"], "url": url, "result": "responded",
                            "status": response.status, "finalUrl": response.url,
                            "method": method}
            except HTTPError as error:
                # Some official sites reject HEAD while serving the same URL to a browser.
                if method == "HEAD":
                    continue
                return {"id": site["id"], "url": url,
                        "result": "access_restricted" if error.code in (401, 403, 429) else "http_error",
                        "status": error.code, "method": method}
    except ReviewRedirect as redirect:
        return {"id": site["id"], "url": url, "result": "redirect_review",
                "destination": redirect.destination}
    except (URLError, TimeoutError, OSError) as error:
        return {"id": site["id"], "url": url, "result": "network_unknown",
                "detail": type(error).__name__}
    return {"id": site["id"], "url": url, "result": "unknown"}


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--cached-discovery", type=Path, help="Reuse exact destination checks from the identified live integration batch; curated records are still probed live")
    args = parser.parse_args()
    catalog = json.loads(CATALOG.read_text(encoding="utf-8"))
    sites = catalog["websites"]
    cached = json.loads(args.cached_discovery.read_text(encoding="utf-8")) if args.cached_discovery else {}
    if isinstance(cached, list):
        cached = {row["url"]: row["destinationHealth"] for row in cached}
    if args.cached_discovery:
        missing = [site["id"] for site in sites if site["verificationStatus"] == "directory_listed" and
                   (site["url"] not in cached or cached[site["url"]].get("url") != site["url"])]
        if missing:
            parser.error(f"Cached evidence missing or URL mismatched for {len(missing)} directory entries; refresh evidence explicitly")
    def check(site):
        evidence = cached.get(site["url"])
        if site["verificationStatus"] == "directory_listed" and evidence:
            return {"id": site["id"], "url": site["url"], "result": evidence["status"].lower(),
                    "status": evidence.get("httpStatus"), "finalUrl": evidence.get("finalUrl"),
                    "method": "cached actual GET", "checkedAt": evidence.get("checkedAt")}
        return {**probe(site), "checkedAt": datetime.now(timezone.utc).isoformat(timespec="seconds")}
    with ThreadPoolExecutor(max_workers=4) as pool:
        results = list(pool.map(check, sites))
    checked = datetime.now(timezone.utc).isoformat(timespec="seconds")
    lines = ["# Published portal link audit", "", f"Checked: {checked}",
             "", "Automated responses only indicate reachability from this machine. Access restrictions, timeouts, and redirects need human review; this audit does not establish ownership or change publication status.",
             "", f"Cached integration evidence: {args.cached_discovery or 'none; all requests live'}. Each row records its actual check time; a cached row is not a new request.",
             "", "| Portal ID | Result | Detail / method / actual time |", "| --- | --- | --- |"]
    for row in results:
        detail = str(row.get("status", row.get("destination", row.get("detail", ""))))
        if row.get("finalUrl") != row["url"] and row.get("finalUrl"):
            detail += " → " + row["finalUrl"]
        detail += " · " + row.get("method", "live probe") + " · " + row["checkedAt"]
        lines.append(f"| {row['id']} | {row['result']} | {detail.replace('|', '&#124;')} |")
    lines.extend(["", "Re-run with `python tools/audit_links.py`. Investigate any non-response on the official website before editing the catalog.", ""])
    REPORT.write_text("\n".join(lines), encoding="utf-8")
    (ROOT / "data/collection/link_audit.json").write_text(json.dumps({"generatedAt": checked, "results": results}, indent=2) + "\n", encoding="utf-8")
    print(f"Audited {len(results)} published portals; wrote {REPORT}")
    for row in results[:20]:
        print(f"{row['id']}: {row['result']}")


if __name__ == "__main__":
    main()
