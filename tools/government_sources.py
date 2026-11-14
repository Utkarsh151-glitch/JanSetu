"""Public source adapters. No credentials, TLS exceptions, or guessed endpoints."""

import hashlib
from html import unescape
import json
import re
import subprocess
import os
import threading
import time
from datetime import datetime, timezone
from pathlib import Path
from urllib.parse import urljoin, urlsplit, urlunsplit
from urllib.robotparser import RobotFileParser

from bs4 import BeautifulSoup

AGENT = "JanSetuLocalInventory/1.0 (public directory research; cached, rate limited)"


def redact_tokens(value):
    value = re.sub(r"((?:OWASP_CSRFTOKEN|csrf_token|access_token|id_token|refresh_token|token|api_key|apikey|sessionid|phpsessid)=)[^&\s\"'<>]+", r"\1[redacted]", value, flags=re.I)
    value = re.sub(r"<meta\b[^>]*name=[\"']csrf-token[\"'][^>]*>", '<meta name="csrf-token" content="[redacted]">', value, flags=re.I)
    return value


def now():
    return datetime.now(timezone.utc).isoformat(timespec="seconds")


def save_json(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    temporary = path.with_suffix(path.suffix + f".{os.getpid()}.{threading.get_ident()}.tmp")
    temporary.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    for attempt in range(6):
        try:
            temporary.replace(path)
            break
        except PermissionError:
            if attempt == 5:
                raise
            time.sleep(0.05 * 2 ** attempt)


def normalize_url(value):
    """Preserve host/path/query distinctions; no inferred www or HTTPS equivalence."""
    p = urlsplit(value.strip())
    if p.scheme.lower() not in {"http", "https"} or not p.hostname or p.username or p.password:
        raise ValueError("Invalid public web URL")
    if p.port not in (None, 80 if p.scheme.lower() == "http" else 443):
        raise ValueError("Nonstandard port")
    if re.search(r"(?:^|&)(?:OWASP_CSRFTOKEN|csrf_token|access_token|id_token|refresh_token|token|api_key|apikey|jsessionid|sessionid|phpsessid)=", p.query, re.I) or re.search(r";(?:jsessionid|sessionid)=", p.path, re.I):
        raise ValueError("Session/token-bound URL requires independent public landing page")
    host = p.hostname.lower().encode("idna").decode("ascii")
    if host in {"localhost"} or ":" in host or re.fullmatch(r"[\d.]+", host):
        raise ValueError("Local/IP destination requires review")
    return urlunsplit((p.scheme.lower(), host, p.path or "/", p.query, ""))


class CachedClient:
    """curl uses normal OS TLS validation (Schannel on Windows); never -k.

    Redirects are explicit and restricted to HTTPS on the same host (www variant
    permitted only for destination checks). Body limits prevent large downloads.
    """
    def __init__(self, directory, delay=2.0, timeout=20, executable="curl.exe", retain_pages=True):
        self.directory = Path(directory)
        self.directory.mkdir(parents=True, exist_ok=True)
        self.delay, self.timeout, self.executable = delay, timeout, executable
        self.retain_pages = retain_pages
        self.last_request = {}
        self.host_delays = {}

    def minimal_response(self, meta, body, body_path, meta_path):
        """Health evidence retains status/hash/title, not homepage/contact/form HTML."""
        if not self.retain_pages:
            meta.setdefault("bodyBytes", len(body.encode("utf-8")))
            match = re.search(r"<title\b[^>]*>(.*?)</title>", body[:100_000], re.I | re.S)
            if match:
                meta.setdefault("pageTitle", unescape(re.sub(r"<[^>]+>", "", match[1])).strip()[:250])
            if urlsplit(meta["url"]).path == "/robots.txt":
                meta["robotsText"] = "\n".join(line for line in body.splitlines()
                    if re.match(r"\s*(?:user-agent|allow|disallow|crawl-delay|sitemap)\s*:", line, re.I))
            meta["snapshot"] = str(meta_path)
            meta["bodyRetention"] = "discarded; title/status/size/hash only"
            if body_path.exists():
                body_path.unlink()
            save_json(meta_path, meta)
        return meta

    def fetch(self, url, retry_failed=False, refresh=False, method="GET", max_bytes=3_000_000):
        url = normalize_url(url)
        if not url.startswith("https://"):
            return {"url": url, "status": "UNSAFE_SCHEME", "checkedAt": now()}, ""
        key = hashlib.sha256((method + " " + url).encode()).hexdigest()
        meta_path = self.directory / (key + ".json")
        body_path = self.directory / (key + ".html")
        headers_path = self.directory / (key + ".headers")
        if meta_path.exists() and not refresh:
            meta = json.loads(meta_path.read_text(encoding="utf-8"))
            if not retry_failed or meta["status"] == "OK":
                body = body_path.read_text(encoding="utf-8", errors="replace") if body_path.exists() else ""
                if not self.retain_pages and body:
                    meta = self.minimal_response(meta, body, body_path, meta_path)
                return {**meta, "cacheHit": True}, body if self.retain_pages else meta.get("robotsText", "")
        host = urlsplit(url).hostname
        attempts = []
        for attempt in range(2):
            required_delay = max(self.delay, self.host_delays.get(host, 0))
            wait = required_delay - (time.monotonic() - self.last_request.get(host, 0))
            if wait > 0:
                time.sleep(wait)
            args = [self.executable, "--silent", "--show-error", "--proto", "=https",
                    "--connect-timeout", str(min(10, self.timeout)), "--max-time", str(self.timeout),
                    "--max-filesize", str(max_bytes), "--user-agent", AGENT,
                    "--dump-header", str(headers_path), "--output", str(body_path) if self.retain_pages else "-",
                    "--write-out", "%{json}" if self.retain_pages else "\nJANSETU_FETCH_META\n%{json}"]
            if method == "HEAD":
                args.append("--head")
            if re.search(r"(?:organizations_(?:list_)?more|districts/list_more)/", url):
                # Same public AJAX header observed during normal browser scroll.
                # Without it IGOD redirects its partial-HTML routes to navigation.
                args += ["--header", "X-Requested-With: XMLHttpRequest"]
            if self.retain_pages:
                result = subprocess.run(args + [url], capture_output=True, text=True, encoding="utf-8", errors="replace")
                output, error_text = result.stdout, result.stderr
                body = ""
            else:
                result = subprocess.run(args + [url], capture_output=True)
                body_bytes, _, metadata_bytes = result.stdout.rpartition(b"\nJANSETU_FETCH_META\n")
                body = body_bytes.decode("utf-8", errors="replace")
                output, error_text = metadata_bytes.decode("utf-8", errors="replace"), result.stderr.decode("utf-8", errors="replace")
            self.last_request[host] = time.monotonic()
            try:
                info = json.loads(output)
            except json.JSONDecodeError:
                info = {}
            code = info.get("http_code", 0)
            status = ("TLS_ERROR" if result.returncode in {35, 51, 58, 60, 77, 83, 90, 91} else
                      "ACCESS_DENIED" if code in {401, 403, 429} else
                      "NETWORK_ERROR" if result.returncode else
                      "SERVER_ERROR" if code >= 500 else
                      "HTTP_ERROR" if code >= 400 else "OK")
            headers = headers_path.read_text(encoding="utf-8", errors="replace") if headers_path.exists() else ""
            locations = re.findall(r"^location:\s*(.+)$", headers, re.I | re.M)
            # Anonymous response session cookies are unnecessary retrieval evidence.
            if headers_path.exists():
                headers_path.write_text(redact_tokens(re.sub(r"^set-cookie:.*(?:\n|$)", "", headers, flags=re.I | re.M)), encoding="utf-8")
            meta = {"url": url, "checkedAt": now(), "status": status, "httpStatus": code,
                    "transport": "curl normal TLS validation", "exitCode": result.returncode,
                    "error": redact_tokens(error_text.strip()[:800]), "location": redact_tokens(locations[-1].strip()) if locations else None,
                    "elapsedMs": round(info.get("time_total", 0) * 1000),
                    "snapshot": str(body_path), "method": method}
            attempts.append({k: meta[k] for k in ("checkedAt", "status", "httpStatus", "exitCode", "error")})
            if not self.retain_pages:
                meta["wireSha256"] = hashlib.sha256(body_bytes).hexdigest()
                meta["bodyBytes"] = len(body_bytes)
            if status not in {"NETWORK_ERROR", "SERVER_ERROR"} or attempt == 1:
                break
            time.sleep(max(required_delay, 2 ** (attempt + 1)))
        meta["attempts"] = attempts
        if body_path.exists():
            meta["wireSha256"] = hashlib.sha256(body_path.read_bytes()).hexdigest()
            body = body_path.read_text(encoding="utf-8", errors="replace")
            redacted = redact_tokens(body)
            if redacted != body:
                body_path.write_text(redacted, encoding="utf-8")
            meta["sha256"] = hashlib.sha256(body_path.read_bytes()).hexdigest()
        save_json(meta_path, meta)
        if not self.retain_pages:
            meta = self.minimal_response(meta, body, body_path, meta_path)
            return meta, meta.get("robotsText", "") if urlsplit(url).path == "/robots.txt" else body
        return meta, body_path.read_text(encoding="utf-8", errors="replace") if body_path.exists() else ""


class IgodAdapter:
    id = "igod"
    roots = ["https://igod.gov.in/categories", "https://igod.gov.in/sg/states",
             "https://igod.gov.in/sg/district/states", "https://igod.gov.in/districts",
             "https://igod.gov.in/sectors", "https://igod.gov.in/organization/new_additions"]

    @staticmethod
    def allowed_directory(url):
        p = urlsplit(url)
        return p.scheme == "https" and p.hostname == "igod.gov.in" and not p.query and (
            p.path in {"/categories", "/sectors", "/districts", "/organization/new_additions"} or
            re.fullmatch(r"/(?:apx|ug|sg|leg|jud|int|sector)/[\w/-]+/(?:categories|states|organizations)", p.path) or
            p.path in {"/apx/categories", "/ug/categories", "/leg/categories", "/jud/categories", "/int/categories", "/sg/states", "/apx/states"})

    @staticmethod
    def parse(html, url, context=None):
        context = context or {}
        soup = BeautifulSoup(html, "html.parser")
        main = soup.select_one("#main-content")
        fragment = bool(re.search(r"(?:list_more|organizations_more)/", url))
        container = soup if fragment else (main.select_one(".grid-content") if main else None)
        if container is None:
            raise ValueError("Directory main content missing; not an empty listing")
        if container.select_one('input[type="password"]') or "captcha" in container.get_text(" ", strip=True).lower():
            raise ValueError("Access challenge; do not bypass")
        heading = container.select_one("h2")
        title = heading.get_text(" ", strip=True) if heading else context.get("name", "")
        children = []
        if not fragment:
            for a in container.select("a[href]"):
                link = urljoin(url, a["href"])
                if IgodAdapter.allowed_directory(link) and link != url:
                    name = a.get_text(" ", strip=True)
                    box = a.find_parent(class_="cat-box")
                    h = box.find(["h3", "h4"]) if box else None
                    if name == "View All" and h:
                        name = h.get_text(" ", strip=True)
                    children.append({"url": link, "name": name, "parent": url})
        entries = []
        if urlsplit(url).path == "/districts" or "/districts/list_more/" in url:
            district_list = container if fragment else container.select_one("#conDirDataCnt")
            nodes = [(li.select_one(".search-title") or li) for li in district_list.select("li")] if district_list else []
        else:
            nodes = container.select(".search-title")
        for a in nodes:
            name = " ".join(a.get_text(" ", strip=True).split())
            if not name:
                continue
            row = a.find_parent(class_="search-row") or a.parent
            detail = row.select_one('a.btn-detail[href]')
            box = a.find_parent(class_="cat-box")
            h = box.find(["h3", "h4"]) if box else None
            entries.append({"originalName": name, "originalUrl": a.get("href"),
                            "sourcePage": url, "listingPage": context.get("listingPage", url),
                            "detailUrl": urljoin(url, detail["href"]) if detail else None,
                            "sourceCategory": h.get_text(" ", strip=True) if h else title,
                            "preview": bool(box), "sourceId": "igod"})
        scripts = "\n".join(s.get_text() for s in soup.select("script:not([src])"))
        variables = {k: int(v) for k, v in re.findall(r"var\s+(count|total_pages|per_page|items_on_first_page)\s*=\s*['\"](\d+)['\"]", scripts)}
        meta = soup.select_one(".search-meta")
        count_match = re.search(r"([\d,]+)\s+Results?", meta.get_text(" ", strip=True), re.I) if meta else None
        expected = int(count_match[1].replace(",", "")) if count_match else variables.get("count")
        pages = []
        if variables.get("count", 0) > len(entries):
            expression = next((m[1] for m in re.finditer(r"url:\s*([^\n]+)", scripts)
                               if "list_more" in m[1] or "organizations_more" in m[1]), None)
            if not expression or "+start" not in expression.replace(" ", ""):
                raise ValueError("Pagination total exceeds extracted rows but public endpoint is missing/changed")
            before_start = re.split(r"\+\s*start", expression, maxsplit=1)[0]
            prefix = "".join(re.findall(r"['\"]([^'\"]*)['\"]", before_start))
            if not prefix.startswith("https://igod.gov.in/"):
                raise ValueError("Unexpected pagination endpoint")
            first, size, total = variables["items_on_first_page"], variables["per_page"], variables["count"]
            if not size or len(entries) != first:
                raise ValueError("First-page size does not match source script")
            for start in range(first, total, size):
                query = "?keyword=" if re.search(r"data:\s*\{\s*['\"]keyword['\"]", scripts) else ""
                pages.append({"url": f"{prefix}{start}/{min(size, total-start)}{query}", "name": title,
                              "listingPage": url, "offset": start, "expectedRows": min(size, total-start)})
        is_listing = url.endswith("/organizations") or url.endswith("/districts") or url.endswith("/new_additions") or fragment
        if is_listing and not entries and expected != 0:
            raise ValueError("Missing/changed directory result structure")
        return {"name": title, "entries": entries, "children": children, "pagination": pages,
                "expectedResults": expected, "kind": "listing" if is_listing else "navigation",
                "sourcePagination": variables}


ADAPTERS = {"igod": IgodAdapter}


def check_robots(client, origin):
    url = origin.rstrip("/") + "/robots.txt"
    meta, body = client.fetch(url)
    for _ in range(3):
        if not 300 <= meta.get("httpStatus", 0) < 400:
            break
        target = urljoin(url, meta.get("location") or "")
        a, b = urlsplit(url), urlsplit(target)
        if b.scheme != "https" or b.hostname is None or b.hostname.removeprefix("www.") != a.hostname.removeprefix("www.") or target == url:
            return {**meta, "status": "REDIRECT_REVIEW"}, None
        url = target
        meta, body = client.fetch(url)
    # An explicit missing robots file permits public requests, but a challenge,
    # TLS error or server failure is unresolved and halts collection of this host.
    if meta.get("httpStatus") == 404:
        body = "User-agent: *\nAllow: /\n"
    elif meta["status"] != "OK" or not 200 <= meta.get("httpStatus", 0) < 300:
        return meta, None
    parser = RobotFileParser(url)
    parser.parse(body.splitlines())
    delay = parser.crawl_delay(AGENT) or parser.crawl_delay("*")
    if delay and hasattr(client, "host_delays"):
        for host in {urlsplit(origin).hostname, urlsplit(url).hostname}:
            client.host_delays[host] = max(client.host_delays.get(host, 0), delay)
    return {**meta, "text": body, "crawlDelay": delay}, parser
