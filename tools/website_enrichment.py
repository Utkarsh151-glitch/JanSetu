"""Evidence-backed website enrichment: capture, validate, package and queue.

Usage (from the project root):
  python tools/website_enrichment.py capture data/website_enrichment/batch-2026-10-17.json
  python tools/website_enrichment.py capture data/website_enrichment/batch-2026-10-17.json --targeted
  python tools/website_enrichment.py validate
  python tools/website_enrichment.py queue

capture  Read-only. Loads each batch start page once in local headless Chrome
         (normal TLS validation, Chrome's own headless user agent, no clicking or
         typing) and saves the rendered text, title, language and links as a
         dated research snapshot. It then follows at most three links on the
         same official host whose label says About, Contact, Help, Grievance or
         FAQ. Nothing is published by capture.
validate Checks data/website_enrichment/claims.json. Every published fact must
         cite evidence whose exact quote appears in a saved snapshot of that
         official URL. Website-level eligibility, documents, fees, deadlines and
         contact numbers are refused. Machine-checked claims can never be labelled
         human-reviewed.
queue    Writes the low-information review queue and the next prioritised batch.

Website records, the IGOD review queue and service records are never modified.
"""
from __future__ import annotations

import hashlib
import json
import re
import sys
import time
from datetime import datetime, timezone
from pathlib import Path
from urllib.parse import urljoin, urlsplit

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "data/website_enrichment"
RESEARCH = DATA / "research"
CLAIMS = DATA / "claims.json"
CHROME = r"C:\Program Files\Google\Chrome\Application\chrome.exe"
FOLLOW = re.compile(r"\b(about|about us|contact|contact us|help|helpdesk|help desk|grievance|faq|faqs)\b", re.I)
# Sign-in, application, registration and payment pages are never followed or cited.
SENSITIVE = re.compile(r"login|log-in|signin|sign-in|signup|sign-up|register|registration|apply|otp|password|payment|checkout", re.I)
REVIEW_STATUSES = {"source_checked", "human_reviewed"}
# Values allowed in claims.json. "human_reviewed" only ever comes from reviews.json.
AUTOMATED_REVIEW_STATUSES = {"source_checked", "not_reviewed"}
STATUSES = {"published", "proposed", "held", "rejected"}
FACT_FIELDS = {"purpose", "operator", "audience", "jurisdiction", "languages"}
LINK_FIELDS = {"tasks", "help"}
LINK_KINDS = {"task": {"tasks"}, "help": {"help"}, "contact": {"help"}, "grievance": {"help"}, "faq": {"help"}}
# Website-level checklists are never inferred from a website; they belong to service records.
FORBIDDEN_FIELDS = {"eligibility", "documents", "requiredDocuments", "fees", "fee", "deadline", "deadlines",
                    "applicationStatus", "phone", "email", "helpline", "address"}


def official(url: str) -> bool:
    try:
        parts = urlsplit(url)
        host = (parts.hostname or "").lower()
        return parts.scheme == "https" and not parts.username and not parts.password and parts.port in (None, 443) \
            and (host.endswith(".gov.in") or host.endswith(".nic.in"))
    except ValueError:
        return False


def sensitive(url: str, label: str = "") -> bool:
    """Sign-in, application, registration or payment journey, judged on the path, query, fragment and label (not the host)."""
    parts = urlsplit(url)
    return bool(SENSITIVE.search(" ".join((parts.path, parts.query, parts.fragment, label))))


def normalise(text: str) -> str:
    return re.sub(r"\s+", " ", text or "").strip()


def snapshot_path(website_id: str, url: str, day: str) -> Path:
    return RESEARCH / day / website_id / (hashlib.sha256(url.encode()).hexdigest()[:16] + ".json")


EXTRACT = r"""() => ({
  title: document.title,
  lang: document.documentElement.lang || null,
  metaDescription: (document.querySelector('meta[name="description"]') || {}).content || null,
  text: (document.body ? document.body.innerText : '').slice(0, 200000),
  links: [...document.querySelectorAll('a[href]')].map(a => ({text: (a.innerText || a.getAttribute('aria-label') || a.title || '').replace(/\s+/g, ' ').trim().slice(0, 120), href: a.href}))
    .filter(l => l.text && /^https:/.test(l.href)).slice(0, 600),
  // For a link to a page section (#id): does that section exist, and what does it say?
  anchor: location.hash ? (() => { const el = document.getElementById(decodeURIComponent(location.hash.slice(1)));
    return {id: location.hash.slice(1), found: !!el, text: el ? (el.innerText || '').replace(/\s+/g, ' ').trim().slice(0, 600) : null}; })() : null
})"""


def load_once(browser, website_id: str, url: str, day: str, note: str = "") -> dict:
    """One read-only page load. An earlier attempt on the same day is kept, never overwritten."""
    record = {"websiteId": website_id, "url": url, "checkedAt": datetime.now(timezone.utc).isoformat(timespec="seconds"),
              "browser": f"Chrome {browser.version} (headless)", "method": "single read-only load; no interaction"}
    if note:
        record["note"] = note
    context = browser.new_context(viewport={"width": 1280, "height": 900}, ignore_https_errors=False)
    page = context.new_page()
    try:
        response = page.goto(url, wait_until="domcontentloaded", timeout=40000)
        record["httpStatus"] = response.status if response else None
        record["finalUrl"] = page.url
        page.wait_for_timeout(4000)
        if response and response.status < 400:
            data = page.evaluate(EXTRACT)
            record.update({k: data[k] for k in ("title", "lang", "metaDescription")})
            if data.get("anchor"):
                record["anchor"] = data["anchor"]
            record["text"] = data["text"]
            record["textSha256"] = hashlib.sha256(data["text"].encode("utf-8")).hexdigest()
            record["links"] = data["links"]
        else:
            record["result"] = "access_or_server_response; not a content observation"
    except Exception as error:  # Recorded, never retried around.
        record["result"] = f"inconclusive:{type(error).__name__}:{str(error)[:140]}"
    finally:
        context.close()
    path = snapshot_path(website_id, url, day)
    attempt = 1
    while path.exists():
        attempt += 1
        path = path.with_name(path.name.split(".")[0].split("-attempt")[0] + f"-attempt{attempt}.json")
    record["attempt"] = attempt
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(record, indent=1, ensure_ascii=False) + "\n", encoding="utf-8")
    print(website_id, url, record.get("httpStatus"), record.get("result", ""), flush=True)
    time.sleep(2)
    return record


def follow_links(record: dict, start: str, limit: int = 3) -> list[str]:
    start_host, out = urlsplit(start).hostname, []
    for link in record.get("links", []):
        href = link["href"].split("#")[0]
        if FOLLOW.search(link["text"]) and not sensitive(href, link["text"]) \
                and urlsplit(href).hostname == start_host and official(href) and href != start and href not in out:
            out.append(href)
    return out[:limit]


def log_row(record: dict) -> dict:
    return {k: record.get(k) for k in ("websiteId", "url", "attempt", "httpStatus", "finalUrl", "title", "result", "note")}


def capture(batch_file: Path, targeted: bool = False) -> None:
    """Default: each batch start page plus up to three About/Contact/Help/FAQ links.
    --targeted: only the batch's listed `targetedPages` (one load each, no following unless `follow` is set)."""
    from playwright.sync_api import sync_playwright
    batch = json.loads(batch_file.read_text(encoding="utf-8"))
    day = batch["batchId"]
    log = []
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True, executable_path=CHROME)
        try:
            items = batch.get("targetedPages", []) if targeted else \
                [{"websiteId": item["websiteId"], "url": item["start"], "follow": True} for item in batch["websites"]]
            for item in items:
                if not official(item["url"]):
                    continue
                record = load_once(browser, item["websiteId"], item["url"], day, item.get("reason", ""))
                log.append(log_row(record))
                if item.get("follow") and record.get("text"):
                    for url in follow_links(record, item["url"]):
                        log.append(log_row(load_once(browser, item["websiteId"], url, day)))
        finally:
            browser.close()
    name = "capture-log-targeted.json" if targeted else "capture-log.json"
    (RESEARCH / day / name).write_text(json.dumps(log, indent=1, ensure_ascii=False) + "\n", encoding="utf-8")


def load_snapshots() -> dict[str, list[dict]]:
    by_url: dict[str, list[dict]] = {}
    for path in RESEARCH.glob("*/*/*.json"):
        record = json.loads(path.read_text(encoding="utf-8"))
        if record.get("text"):
            for key in {record["url"], record.get("finalUrl") or record["url"]}:
                by_url.setdefault(key, []).append(record)
    return by_url


def quote_found(quote: str, records: list[dict]) -> bool:
    quote = normalise(quote)
    return bool(quote) and any(
        quote in normalise(r.get("text", "")) or quote in normalise(r.get("title") or "")
        or quote in normalise(r.get("metaDescription") or "") or any(quote == normalise(l["text"]) for l in r.get("links", []))
        for r in records)


def link_observed(url: str, cited: list[dict], snapshots: dict[str, list[dict]]) -> bool:
    """The link target was itself captured, or a cited page has an anchor to it whose text is the cited quote."""
    if snapshots.get(url):
        return True
    return any(link["href"] == url and normalise(link["text"]) == normalise(item["quote"])
               for item in cited for record in snapshots.get(item["url"], []) for link in record.get("links", []))


class ClaimsError(ValueError):
    def __init__(self, errors: list[str]):
        super().__init__(f"{len(errors)} claim error(s):\n- " + "\n- ".join(errors))
        self.errors = errors


def validate(claims: dict, catalog: dict, services: list[dict], snapshots: dict[str, list[dict]]) -> dict:
    """Checks every profile and raises ClaimsError listing every problem found (not just the first)."""
    sites = {site["id"]: site for site in catalog["websites"]}
    service_ids = {service["id"] for service in services}
    seen, published, fields_published, errors = set(), 0, {}, []

    def check(ok, message: str) -> bool:
        if not ok:
            errors.append(message)
        return bool(ok)

    for profile in claims["websites"]:
        website_id = profile["websiteId"]
        check(website_id in sites, f"Unknown website: {website_id}")
        check(website_id not in seen, f"Duplicate profile: {website_id}")
        seen.add(website_id)
        check(profile["status"] in STATUSES, f"Bad status: {website_id}")
        check(profile["reviewStatus"] in AUTOMATED_REVIEW_STATUSES, f"Bad review status: {website_id}")
        # Human review is never written here: it is derived from decisions in reviews.json (editorial_review.py).
        check(profile["reviewStatus"] != "human_reviewed" and "humanReviewer" not in profile,
              f"Human review is recorded only through reviews.json, never in claims.json: {website_id}")
        reviewer = profile.get("reviewer", "").lower()
        check("human" not in reviewer or "pending" in reviewer, f"Machine check must not be labelled as human review: {website_id}")
        check(re.fullmatch(r"\d{4}-\d{2}-\d{2}", profile.get("checkedOn", "")), f"Bad date: {website_id}")
        check(not (set(profile) & FORBIDDEN_FIELDS) and not (set(profile.get("fields", {})) & FORBIDDEN_FIELDS),
              f"Website-level checklist or contact detail refused: {website_id}")
        live = profile["status"] == "published"
        if profile["status"] in {"held", "rejected"}:
            check(normalise(profile.get("heldReason", "")), f"Unpublished profile needs a reason: {website_id}")
            check(not profile.get("fields"), f"Unpublished profiles keep facts out of the app: {website_id}")
        if profile["status"] == "proposed":
            # Drafts are validated like published profiles but never packaged.
            check(profile["reviewStatus"] == "source_checked", f"A draft must be source-checked before it is proposed: {website_id}")
        if profile.get("publicationDecision"):
            decision = profile["publicationDecision"]
            check(decision.get("decision") in {"publish", "hold", "draft"} and normalise(decision.get("basis", "")) and normalise(decision.get("by", "")),
                  f"publicationDecision needs decision, basis and decider: {website_id}")
            check({"publish": "published", "hold": "held", "draft": "proposed"}.get(decision.get("decision")) == profile["status"],
                  f"publicationDecision does not match the profile status: {website_id}")
        for held in profile.get("heldFields", []):
            check(held.get("field") in FACT_FIELDS | LINK_FIELDS and normalise(held.get("reason", "")),
                  f"Held field needs a known name and reason: {website_id}")
        for revision in profile.get("revisionHistory", []):
            # Staged corrections are auditable: when, who, what, and which reviewed content they replace.
            check(re.fullmatch(r"\d{4}-\d{2}-\d{2}", revision.get("date", "")) and normalise(revision.get("by", ""))
                  and normalise(revision.get("change", "")) and re.fullmatch(r"[0-9a-f]{64}", revision.get("previousDigest", "")),
                  f"revisionHistory entries need date, by, change and previousDigest: {website_id}")
        evidence = {item["id"]: item for item in profile["evidence"]}
        check(len(evidence) == len(profile["evidence"]), f"Duplicate evidence id: {website_id}")
        for item in profile["evidence"]:
            label = f"{website_id}/{item.get('id')}"
            check(official(item["url"]), f"Evidence must be an official HTTPS page: {label} {item['url']}")
            check(not sensitive(item["url"]), f"Sign-in, application or payment pages are never cited: {label}")
            check(normalise(item.get("claim", "")) and re.fullmatch(r"\d{4}-\d{2}-\d{2}", item.get("checkedOn", "")) and normalise(item.get("quote", "")),
                  f"Evidence needs claim, date and quote: {label}")
            records = snapshots.get(item["url"], [])
            if check(records, f"No saved snapshot for evidence URL: {label} {item['url']}"):
                check(quote_found(item.get("quote", ""), records), f"Quote not found verbatim in the saved snapshot: {label}")
        if live:
            check(profile["reviewStatus"] in REVIEW_STATUSES, f"Published profile must be source-checked: {website_id}")
            check("purpose" in profile.get("fields", {}), f"Published profile needs a checked purpose: {website_id}")
            published += 1
        for name, fact in profile.get("fields", {}).items():
            if not check(name in FACT_FIELDS | LINK_FIELDS, f"Unknown field {name}: {website_id}"):
                continue
            for entry in (fact if name in LINK_FIELDS else [fact]):
                ids = entry.get("evidenceIds") or []
                check(ids, f"{name} lacks evidence: {website_id}")
                check(set(ids) <= set(evidence), f"{name} cites unknown evidence: {website_id}")
                check(normalise(entry.get("value") or entry.get("label") or ""), f"{name} is empty: {website_id}")
                if name in LINK_FIELDS:
                    url = entry.get("url", "")
                    check(official(url), f"Link must be an official HTTPS URL: {website_id} {url}")
                    check(not sensitive(url, entry.get("label", "")), f"Sign-in, application or payment link refused: {website_id} {url}")
                    check(name in LINK_KINDS.get(entry.get("kind"), set()), f"Link kind {entry.get('kind')} not allowed in {name}: {website_id}")
                    check(link_observed(url, [evidence[e] for e in ids if e in evidence], snapshots),
                          f"Link not observed on a saved official page: {website_id} {url}")
                    if "serviceId" in entry:
                        check(entry["serviceId"] in service_ids, f"Task names an unknown service: {website_id}")
                if live:
                    fields_published[name] = fields_published.get(name, 0) + 1
    if errors:
        raise ClaimsError(errors)
    return {"profiles": len(seen), "published": published, "fieldsPublished": fields_published}


# The only keys a packaged profile may carry. No quotes, reviewer identities, notes or digests.
RUNTIME_KEYS = {"websiteId", "reviewStatus", "checkedOn", "reviewedOn", "evidence", "uncertainty", "notStated"} | FACT_FIELDS | LINK_FIELDS


def runtime_profiles(claims: dict, reviews: dict | None = None) -> list[dict]:
    """Published profiles only, without quotes (the app shows links and dates, not copied page text).

    Human review comes only from reviews.json: an approval of the current content makes a profile
    human_reviewed; claims a reviewer marked revise or hold are withheld; a profile hold removes it."""
    from editorial_review import effective, empty_reviews
    reviews = reviews or empty_reviews()
    out = []
    for profile in claims["websites"]:
        state = effective(profile, reviews)
        if profile["status"] != "published" or not state["published"]:
            continue
        withheld = set(state["withheld"])
        fields = profile.get("fields", {})
        row = {"websiteId": profile["websiteId"], "reviewStatus": state["reviewStatus"], "checkedOn": profile["checkedOn"],
               "evidence": [{k: item[k] for k in ("id", "url", "checkedOn", "claim")} for item in profile["evidence"]],
               "uncertainty": profile.get("uncertainty", []),
               "notStated": sorted({item["field"] for item in profile.get("heldFields", [])} - set(fields))}
        if state.get("reviewedOn"):
            row["reviewedOn"] = state["reviewedOn"]
        for name in FACT_FIELDS:
            if name in fields and name not in withheld:
                row[name] = {"value": fields[name]["value"], "evidenceIds": fields[name]["evidenceIds"]}
        for name in LINK_FIELDS:
            row[name] = [{k: entry[k] for k in ("label", "url", "kind", "evidenceIds") if k in entry} | ({"serviceId": entry["serviceId"]} if "serviceId" in entry else {})
                         for entry in fields.get(name, []) if f"{name}:{entry['url']}" not in withheld]
        if "purpose" not in row:
            continue  # a profile whose purpose a reviewer withheld is not shown as checked
        assert set(row) <= RUNTIME_KEYS, f"Unexpected runtime keys: {set(row) - RUNTIME_KEYS}"
        out.append(row)
    return out


def read_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def dump_claims(claims: dict) -> str:
    """Stable, reviewable layout: one evidence item, link or held field per line."""
    def one(value) -> str:
        return json.dumps(value, ensure_ascii=False)

    lines = ["{"]
    top = [k for k in claims if k != "websites"]
    for key in top:
        lines.append(f'  {one(key)}: {one(claims[key])},')
    lines.append('  "websites": [')
    for i, profile in enumerate(claims["websites"]):
        lines.append("    {")
        keys = list(profile)
        for j, key in enumerate(keys):
            value, end = profile[key], "," if j < len(keys) - 1 else ""
            if key in {"evidence", "heldFields", "uncertainty", "revisionHistory"} and value:
                lines.append(f"      {one(key)}: [")
                lines += [f"        {one(item)}{',' if n < len(value) - 1 else ''}" for n, item in enumerate(value)]
                lines.append(f"      ]{end}")
            elif key == "fields" and value:
                lines.append('      "fields": {')
                names = list(value)
                for n, name in enumerate(names):
                    comma = "," if n < len(names) - 1 else ""
                    if isinstance(value[name], list):
                        lines.append(f"        {one(name)}: [")
                        lines += [f"          {one(item)}{',' if m < len(value[name]) - 1 else ''}" for m, item in enumerate(value[name])]
                        lines.append(f"        ]{comma}")
                    else:
                        lines.append(f"        {one(name)}: {one(value[name])}{comma}")
                lines.append(f"      }}{end}")
            else:
                lines.append(f"      {one(key)}: {one(value)}{end}")
        lines.append("    }" + ("," if i < len(claims["websites"]) - 1 else ""))
    lines.append("  ]")
    lines.append("}")
    return "\n".join(lines) + "\n"


def main() -> None:
    command = sys.argv[1] if len(sys.argv) > 1 else ""
    if command == "capture":
        capture(Path(sys.argv[2]), targeted="--targeted" in sys.argv[3:])
    elif command == "validate":
        sys.path.insert(0, str(ROOT / "tools"))
        from service_catalog import merged_catalog
        catalog = merged_catalog()
        print(json.dumps(validate(read_json(CLAIMS), catalog, catalog.get("services", []), load_snapshots()), sort_keys=True))
    elif command == "queue":
        from enrichment_queue import write_queue
        print(json.dumps(write_queue(), sort_keys=True))
    else:
        raise SystemExit(__doc__)


if __name__ == "__main__":
    main()
