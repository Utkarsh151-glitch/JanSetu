"""Normalize, deduplicate and health-check an acquired official directory batch.

Outputs feed catalog_pipeline.py, which alone stages and bundles approved data.
Run repeatedly as acquisition advances. Existing health results/review decisions
are retained. HTTP errors never imply fraud or a permanent outage.
"""

import argparse
import hashlib
from html import unescape
import json
import re
from collections import Counter, defaultdict
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path
from threading import Lock
from urllib.parse import urljoin, urlsplit

from bs4 import BeautifulSoup

from government_sources import CachedClient, check_robots, normalize_url, now, save_json

ROOT = Path(__file__).resolve().parents[1]
CONFIG = json.loads((ROOT / "data/discovery_classification.json").read_text(encoding="utf-8"))
LEVEL_LABELS = CONFIG["levelLabels"]
SECTOR_CATEGORY = CONFIG["sectorCategories"]
SOURCE_SECTORS = CONFIG["sourceSectorMap"]
NAME_RULES = CONFIG["nameRules"]


def identity(value):
    return " ".join(re.findall(r"[\w]+", value.casefold(), flags=re.UNICODE))


def source_context(entry, states, taxonomy):
    route = urlsplit(entry["listingPage"]).path
    state_match = re.search(r"/(?:sg|leg|apx)/([A-Z]{2})/", route)
    code = state_match[1] if state_match else None
    state = states.get(code, {}).get("name")
    is_ut = state in taxonomy["unionTerritories"]
    if "/E042/" in route or route == "/districts":
        level = "district"
    elif re.match(r"/leg/(?:[A-Z]{2}/)?L00[78]/", route):
        level = "municipal_local"
    elif route.startswith("/leg/"):
        level = "legislature"
    elif route.startswith("/jud/"):
        level = "judiciary"
    elif entry.get("sourceCategory") == "Statutory Bodies":
        level = "statutory_public_institution"
    elif code:
        level = "union_territory" if is_ut else "state"
    elif route.startswith(("/ug/", "/apx/", "/int/")):
        level = "union"
    else:
        level = "other_official_institution"
    return {"governmentLevel": level, "state": state, "sourceStateCode": code,
            "district": entry["originalName"] if level == "district" else None}


def normalize_entries(entries, states, taxonomy, existing):
    sites, orgs, unresolved = {}, {}, {}
    existing_by_key = {}
    existing_ids = {}
    for row in existing["websites"]:
        try:
            key = normalize_url(row["url"])
        except ValueError:
            continue
        existing_by_key.setdefault(key, row)
        existing_ids[row["id"]] = key
    identified = defaultdict(set)
    for entry in entries:
        if entry.get("detailUrl") and entry.get("originalUrl"):
            try:
                identified[(identity(entry["originalName"]), normalize_url(entry["originalUrl"]))].add(entry["detailUrl"])
            except ValueError:
                pass
    for e in entries:
        context = source_context(e, states, taxonomy)
        preferred = None
        if not e.get("detailUrl") and e.get("originalUrl"):
            try:
                matching = identified[(identity(e["originalName"]), normalize_url(e["originalUrl"]))]
                preferred = next(iter(matching)) if len(matching) == 1 else None
            except ValueError:
                pass
        org_key = e.get("detailUrl") or preferred or "name:" + identity(e["originalName"]) + ":" + (context["state"] or context["governmentLevel"])
        org = orgs.setdefault(org_key, {"id": "org-" + hashlib.sha256(org_key.encode()).hexdigest()[:16],
                                       "sourceOrganizationUrl": e.get("detailUrl") or preferred, "names": [], "websiteIds": [], "sourcePages": []})
        if e["originalName"] not in org["names"]:
            org["names"].append(e["originalName"])
        if e["sourcePage"] not in org["sourcePages"]:
            org["sourcePages"].append(e["sourcePage"])
        try:
            key = normalize_url(e.get("originalUrl") or "")
        except ValueError as error:
            unresolved.setdefault(org_key, {"organizationId": org["id"], "name": e["originalName"],
                                            "originalUrl": e.get("originalUrl"), "reason": str(error),
                                            "sourcePage": e["sourcePage"], **context})
            continue
        previous = existing_by_key.get(key)
        internal_id = previous["id"] if previous else "igod-" + hashlib.sha256(key.encode()).hexdigest()[:16]
        if not previous and internal_id in existing_ids and existing_ids[internal_id] != key:
            # Older staging could assign one host variant's generated ID to the
            # other variant. Keep that retained ID and assign a stable distinct ID.
            internal_id = "igod-" + hashlib.sha256(("landing:" + key).encode()).hexdigest()[:16]
        row = sites.setdefault(key, {
            "id": internal_id,
            "title": e["originalName"], "aliases": [], "description": "", "url": key,
            "rootDomain": urlsplit(key).hostname, "rootUrl": f"{urlsplit(key).scheme}://{urlsplit(key).netloc}/",
            "websiteEntityId": "web-" + hashlib.sha256(urlsplit(key).hostname.encode()).hexdigest()[:16],
            "landingPageId": "landing-" + hashlib.sha256(key.encode()).hexdigest()[:16],
            "organization": e["originalName"], "owner": None, "operator": None,
            "governmentLevel": context["governmentLevel"], "level": LEVEL_LABELS[context["governmentLevel"]],
            "jurisdiction": context["state"] or "India (exact scope requires review)",
            "state": context["state"], "sourceStateCode": context["sourceStateCode"], "district": context["district"],
            "sourceStates": [],
            "primarySector": None, "secondarySectors": [], "sourceCategories": [], "sourceSectors": [],
            "purposes": ["information"], "serviceType": "website", "classificationStatus": "SOURCE_CATEGORY_RULES",
            "classificationConfidence": "low", "sourceId": "igod", "sourceType": "Official IGOD directory listing",
            "sourceUrl": e.get("detailUrl") or e["sourcePage"], "sourceEvidence": [], "sourceOrganizationIds": [],
            "sourceAuthority": "LISTED_BY_OFFICIAL_DIRECTORY", "ownershipConfidence": "directory_linkage_only",
            "firstDiscovered": e["checkedAt"], "lastSourceChecked": e["checkedAt"],
            "publicationScope": "local_development", "publicationStatus": "PENDING_HEALTH_CHECK",
            "reviewStatus": "DISCOVERED", "browserSupport": "external", "browserJavaScript": False,
            "knownLimitations": "Listed by IGOD. Owner/operator and individual service journeys have not been manually reviewed.",
        })
        if e["originalName"] not in row["aliases"]:
            row["aliases"].append(e["originalName"])
        if org["id"] not in row["sourceOrganizationIds"]:
            row["sourceOrganizationIds"].append(org["id"])
        if row["id"] not in org["websiteIds"]:
            org["websiteIds"].append(row["id"])
        if e["sourceCategory"] not in row["sourceCategories"]:
            row["sourceCategories"].append(e["sourceCategory"])
        if e["listingPage"].split("/")[3] == "sector" and e["sourceCategory"] not in row["sourceSectors"]:
            row["sourceSectors"].append(e["sourceCategory"])
        # A categorized branch adds exact jurisdiction to a generic sector hit.
        if context["state"] and context["state"] not in row["sourceStates"]:
            row["sourceStates"].append(context["state"])
        priority = {"other_official_institution": 0, "union": 1, "state": 2, "union_territory": 2,
                    "statutory_public_institution": 3, "municipal_local": 3, "district": 4, "legislature": 5, "judiciary": 5}
        if priority.get(context["governmentLevel"], 0) > priority.get(row["governmentLevel"], 0):
            row["governmentLevel"] = context["governmentLevel"]
        if context["district"]:
            row["district"] = context["district"]
        if context["state"]:
            row["state"], row["sourceStateCode"] = context["state"], context["sourceStateCode"]
        row["level"] = LEVEL_LABELS[row["governmentLevel"]]
        if len(row["sourceStates"]) > 1:
            row["state"], row["sourceStateCode"] = None, None
            row["jurisdiction"] = "Multiple source state branches; exact jurisdiction requires review"
        else:
            row["jurisdiction"] = row["state"] or "India (exact scope requires review)"
        evidence = {"sourceId": "igod", "sourcePage": e["sourcePage"], "listingPage": e["listingPage"],
                    "detailUrl": e.get("detailUrl"), "originalName": e["originalName"],
                    "originalUrl": e.get("originalUrl"), "category": e["sourceCategory"],
                    "retrievedAt": e["checkedAt"], "snapshot": e["snapshot"], "preview": e["preview"]}
        if not any(v["sourcePage"] == evidence["sourcePage"] and v["originalName"] == evidence["originalName"] for v in row["sourceEvidence"]):
            row["sourceEvidence"].append(evidence)
        row["lastSourceChecked"] = max(row["lastSourceChecked"], e["checkedAt"])
    for row in sites.values():
        source_sectors = sorted({SOURCE_SECTORS[s] for s in row["sourceSectors"] if s in SOURCE_SECTORS})
        title_sectors = [sector for pattern, sector in NAME_RULES if re.search(pattern, row["title"], re.I)]
        if title_sectors:
            primary = title_sectors[0]
            confidence = "medium" if primary in source_sectors else "low"
        elif len(source_sectors) == 1:
            primary, confidence = source_sectors[0], "medium"
        elif row["governmentLevel"] in {"judiciary", "municipal_local", "district", "state", "union_territory", "legislature", "union"}:
            primary = {"judiciary": "justice_legal", "municipal_local": "local_civic"}.get(row["governmentLevel"], "government_information")
            confidence = "medium" if primary != "government_information" else "low"
        else:
            primary, confidence = "other_verified", "low"
        row.update(primarySector=primary, secondarySectors=[s for s in source_sectors if s != primary],
                   classificationConfidence=confidence, categories=[SECTOR_CATEGORY[primary]])
        row["keywords"] = sorted(set(row["aliases"] + row["sourceCategories"] + row["sourceSectors"] +
                                     [v for v in (row["state"], row["district"]) if v]))
        row["description"] = f"IGOD lists this website under {row['sourceCategories'][0]}. Open the official destination for current information."
    names = defaultdict(list)
    for row in sites.values():
        names[(identity(row["title"]), row["state"])].append(row["id"])
    suspected = [{"reason": "Same organization name/jurisdiction, different URLs; no automatic merge", "websiteIds": ids}
                 for ids in names.values() if len(ids) > 1]
    return sorted(sites.values(), key=lambda x: x["id"]), sorted(orgs.values(), key=lambda x: x["id"]), list(unresolved.values()), suspected


def safe_redirect_groups(rows, catalog):
    groups = defaultdict(list)
    existing_urls = {normalize_url(r["url"]) for r in catalog["websites"]}
    for row in rows:
        health = row.get("destinationHealth", {})
        if health.get("status") != "RESPONDING" or not health.get("finalUrl"):
            continue
        try:
            target = normalize_url(health["finalUrl"])
        except ValueError:
            continue  # Session-bound or invalid targets are not equivalence proof.
        # Different application entries can legitimately converge at a login;
        # do not erase those distinctions merely because their redirects match.
        if re.search(r"login|signin|sign-in|authenticate|/auth", target, re.I):
            continue
        groups[target].append(row)
    report = []
    for target, candidates in sorted(groups.items()):
        if len(candidates) < 2:
            continue
        preferred = min(candidates, key=lambda r: (r["url"] not in existing_urls, r["url"] != target, r["id"]))
        for row in candidates:
            if row is not preferred:
                row["duplicateOf"] = preferred["id"]
        report.append({"finalUrl": target, "preferredId": preferred["id"], "websiteIds": [r["id"] for r in candidates],
                       "sourceUrls": [r["url"] for r in candidates], "reason": "Observed same secure responding destination; alias retained, not separately bundled"})
    return report


def destination_health(row, client):
    result = {"url": row["url"], "checkedAt": now(), "redirects": [], "robotsChecks": [], "conclusive": False}
    if not row["url"].startswith("https://"):
        return {**result, "status": "URL_REVIEW", "reason": "Source lists HTTP; HTTPS not assumed"}
    current = row["url"]
    for _ in range(5):
        p = urlsplit(current)
        robots_meta, robots = check_robots(client, f"https://{p.netloc}")
        result["robotsChecks"].append({"url": robots_meta["url"], "httpStatus": robots_meta.get("httpStatus"),
            "status": robots_meta["status"], "resolvedRules": robots is not None,
            "allowed": robots.can_fetch("JanSetuLocalInventory", current) if robots else None,
            "snapshot": robots_meta.get("snapshot"), "checkedAt": robots_meta.get("checkedAt")})
        if robots is None:
            return {**result, "status": "ROBOTS_UNRESOLVED", "reason": robots_meta["status"], "robots": robots_meta}
        if not robots.can_fetch("JanSetuLocalInventory", current):
            return {**result, "status": "ROBOTS_DISALLOWED", "reason": "robots.txt disallows destination retrieval"}
        meta, html = client.fetch(current, max_bytes=2_000_000)
        result.update(httpStatus=meta.get("httpStatus"), finalUrl=current, retrieval=meta,
                      checkedAt=meta.get("checkedAt", result["checkedAt"]), policyEvaluatedAt=now())
        if meta["status"] != "OK":
            return {**result, "status": meta["status"], "reason": meta.get("error") or str(meta.get("httpStatus"))}
        if 300 <= meta["httpStatus"] < 400:
            location = meta.get("location")
            try:
                target = normalize_url(urljoin(current, location or ""))
            except ValueError:
                return {**result, "status": "REDIRECT_REVIEW", "reason": "Invalid redirect"}
            result["redirects"].append({"from": current, "to": target, "httpStatus": meta["httpStatus"]})
            if not location or not target.startswith("https://") or urlsplit(target).hostname.removeprefix("www.") != p.hostname.removeprefix("www."):
                return {**result, "status": "REDIRECT_REVIEW", "reason": "Unexpected host or scheme; not followed"}
            if target == current:
                return {**result, "status": "REDIRECT_REVIEW", "reason": "Redirect loop"}
            current = target
            continue
        match = re.search(r"<title\b[^>]*>(.*?)</title>", html[:100_000], re.I | re.S)
        title = meta.get("pageTitle") or (unescape(re.sub(r"<[^>]+>", "", match[1])).strip() if match else "")
        result["pageTitle"] = title[:250]
        if re.search(r"Apache Tomcat|Index of /|Welcome to nginx|IIS Windows Server", title, re.I):
            return {**result, "status": "DEFAULT_SERVER_PAGE", "reason": "Default hosting page requires canonical URL review"}
        if re.search(r"access denied|forbidden|captcha|verify you are|just a moment|blocked|error 404|not found|site under maintenance", title, re.I):
            return {**result, "status": "ACCESS_OR_CONTENT_REVIEW", "reason": "Challenge/error title; no bypass"}
        if 200 <= meta["httpStatus"] < 300 and meta.get("bodyBytes", len(html)) >= 300:
            return {**result, "status": "RESPONDING", "conclusive": True,
                    "reason": "Public HTTPS response, not ownership or transaction certification"}
        return {**result, "status": "CONTENT_REVIEW", "reason": "Empty/short response requires review"}
    return {**result, "status": "REDIRECT_REVIEW", "reason": "Redirect limit reached"}


def eligible(row):
    health = row.get("destinationHealth", {})
    host = urlsplit(row["url"]).hostname or ""
    attributed = any(not e["preview"] or e["category"] in {"State Portal", "Union Territory Portal", "UT Portal"} for e in row["sourceEvidence"])
    # Suffix is never evidence by itself. This restriction additionally holds
    # partner/vendor domains for owner/operator review even when IGOD lists them.
    institutional_host = any(host == s or host.endswith("." + s) for s in ("gov.in", "nic.in", "ac.in", "edu.in", "mil.in"))
    robots = health.get("robotsChecks", [])
    permitted_check = bool(robots) and all(r.get("resolvedRules") and r.get("allowed") for r in robots)
    return attributed and institutional_host and permitted_check and health.get("status") == "RESPONDING" and row["url"].startswith("https://")


def prepare(args):
    directory = ROOT / "data/collection" / args.batch
    manifest = json.loads((directory / "manifest.json").read_text(encoding="utf-8"))
    catalog = json.loads((ROOT / "app/src/main/assets/catalog.json").read_text(encoding="utf-8"))
    taxonomy = json.loads((ROOT / "data/taxonomy.json").read_text(encoding="utf-8"))
    entries = [dict(e, checkedAt=p["retrieval"]["checkedAt"], snapshot=p["retrieval"].get("snapshot"))
               for p in manifest["pages"].values() if p["status"] == "PROCESSED" for e in p.get("entries", [])]
    queue_path = ROOT / "data/review_queue.json"
    queued = json.loads(queue_path.read_text(encoding="utf-8"))["records"] if queue_path.exists() else []
    retained = {"websites": catalog["websites"] + [item["record"] for item in queued]}
    rows, orgs, unresolved, suspected = normalize_entries(entries, manifest["stateBranches"], taxonomy, retained)
    health_path = directory / "destination_health.json"
    health = json.loads(health_path.read_text(encoding="utf-8")) if health_path.exists() else {}
    if args.check:
        client = CachedClient(directory / "destination_snapshots", delay=2, timeout=args.timeout, retain_pages=False)
        pending = [r for r in rows if r["url"] not in health or
                   (args.revalidate and "robotsChecks" not in health[r["url"]]) or
                   (args.retry_exceptions and health.get(r["url"], {}).get("status") == "CHECK_EXCEPTION")]
        if args.limit:
            pending = pending[:args.limit]
        locks = {urlsplit(r["url"]).hostname.removeprefix("www."): Lock() for r in pending}
        def check(row):
            with locks[urlsplit(row["url"]).hostname.removeprefix("www.")]:
                try:
                    result = destination_health(row, client)
                except Exception as error:
                    result = {"url": row["url"], "status": "CHECK_EXCEPTION", "conclusive": False,
                              "checkedAt": now(), "reason": type(error).__name__ + ": " + str(error)[:500]}
                return row["url"], result
        with ThreadPoolExecutor(max_workers=args.workers) as pool:
            futures = [pool.submit(check, row) for row in pending]
            for number, future in enumerate(as_completed(futures), 1):
                key, result = future.result()
                health[key] = result
                if number % 10 == 0:
                    save_json(health_path, health)
                if number % 50 == 0 or number == len(futures):
                    print(f"Destination checks {number}/{len(futures)}; {Counter(v['status'] for v in health.values())}", flush=True)
        save_json(health_path, health)
    for row in rows:
        row["destinationHealth"] = health.get(row["url"], {"status": "NOT_CHECKED", "conclusive": False})
        if row["destinationHealth"].get("status") == "RESPONDING":
            try:
                normalize_url(row["destinationHealth"].get("finalUrl", ""))
            except ValueError:
                row["destinationHealth"] = {**row["destinationHealth"], "status": "URL_REVIEW", "conclusive": False,
                    "reason": "Invalid/session-bound final URL; independent public landing page requires review"}
                health[row["url"]] = row["destinationHealth"]
        row["observedFinalUrl"] = row["destinationHealth"].get("finalUrl")
        row["healthStatus"] = row["destinationHealth"]["status"]
        row["healthCheckedAt"] = row["destinationHealth"].get("checkedAt")
        row["publicationStatus"] = "ELIGIBLE_LOCAL_DIRECTORY" if eligible(row) else "PENDING_REVIEW"
        if not row["url"].startswith("https://") or row["healthStatus"] in {"REDIRECT_REVIEW", "DEFAULT_SERVER_PAGE", "CONTENT_REVIEW", "URL_REVIEW"}:
            row["reviewStatus"] = "NEEDS_URL_REVIEW"
        elif row["healthStatus"] in {"NETWORK_ERROR", "SERVER_ERROR", "TLS_ERROR"}:
            row["reviewStatus"] = "TEMPORARILY_UNREACHABLE"
        elif not any(urlsplit(row["url"]).hostname.endswith("." + suffix) for suffix in ("gov.in", "nic.in", "ac.in", "edu.in")):
            row["reviewStatus"] = "NEEDS_OWNERSHIP_REVIEW"
        row["sourceRetrievedOn"] = row["lastSourceChecked"][:10]
    redirect_groups = safe_redirect_groups(rows, catalog)
    for row in rows:
        if row.get("duplicateOf"):
            row["publicationStatus"] = "DUPLICATE_EQUIVALENT"
            row["reviewStatus"] = "NEEDS_URL_REVIEW"
            row["reviewReason"] = "Observed safe redirect alias of " + row["duplicateOf"]
    summary = {"version": 1, "batch": args.batch, "preparedAt": now(), "acquisitionUpdatedAt": manifest.get("updatedAt"),
               "rawEntries": len(entries), "distinctSourceOrganizations": len(orgs), "uniqueNormalizedUrls": len(rows),
               "missingOrInvalidUrlOrganizations": len(unresolved), "suspectedDuplicateGroups": len(suspected),
               "safeRedirectGroups": len(redirect_groups), "safeRedirectAliases": sum(bool(r.get("duplicateOf")) for r in rows),
               "localEligible": sum(eligible(r) and not r.get("duplicateOf") for r in rows), "health": dict(Counter(r["healthStatus"] for r in rows)),
               "permission": "Local development only; NIC permission required before public reproduction"}
    if not args.dry_run:
        save_json(health_path, health)
        save_json(directory / "raw_entries.json", entries)
        save_json(directory / "normalized_candidates.json", rows)
        save_json(directory / "organizations.json", orgs)
        websites = {}
        for row in rows:
            website = websites.setdefault(row["websiteEntityId"], {"id": row["websiteEntityId"], "host": row["rootDomain"],
                "landingPageIds": [], "organizationIds": [], "owner": None, "operator": None})
            website["landingPageIds"].append(row["landingPageId"])
            website["organizationIds"] = sorted(set(website["organizationIds"] + row["sourceOrganizationIds"]))
        save_json(directory / "website_entities.json", list(websites.values()))
        save_json(directory / "unresolved_organizations.json", unresolved)
        save_json(directory / "duplicate_review.json", suspected)
        save_json(directory / "safe_redirect_equivalents.json", redirect_groups)
        save_json(directory / "summary.json", summary)
    print(json.dumps(summary, indent=2), flush=True)


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--batch", default="igod-2026-10-14")
    p.add_argument("--check", action="store_true")
    p.add_argument("--revalidate", action="store_true", help="Re-evaluate older checks against cached robots evidence after policy changes")
    p.add_argument("--retry-exceptions", action="store_true", help="Retry local processing/storage errors, reusing cached network responses")
    p.add_argument("--dry-run", action="store_true")
    p.add_argument("--limit", type=int, default=0)
    p.add_argument("--workers", type=int, default=4)
    p.add_argument("--timeout", type=int, default=15)
    args = p.parse_args()
    if not 1 <= args.workers <= 8 or (args.dry_run and args.check):
        p.error("Use one to eight workers across distinct hosts; dry-run must not perform checks")
    prepare(args)


if __name__ == "__main__":
    main()
