"""Offline catalog review pipeline. Run ``python tools/catalog_pipeline.py --help``.

Source adapters accept editor-supplied JSON arrays or CSV rows. Nothing discovered
is published merely because a URL responds: an editor must record an approval.
"""

import argparse
import csv
import json
import hashlib
from functools import lru_cache
from collections import Counter
from datetime import date, datetime, timezone
from pathlib import Path
from urllib.parse import urlsplit, urlunsplit

ROOT = Path(__file__).resolve().parents[1]
CATALOG = ROOT / "app/src/main/assets/catalog.json"
QUEUE = ROOT / "data/review_queue.json"
CANDIDATE_STATUSES = {
    "DISCOVERED", "NEEDS_OWNERSHIP_REVIEW", "NEEDS_URL_REVIEW",
    "VERIFIED", "REJECTED", "TEMPORARILY_UNREACHABLE", "RESOLVED_DUPLICATE",
}


def canonical_url(value: str) -> str:
    parts = urlsplit(value.strip())
    if parts.scheme.lower() != "https" or not parts.hostname or parts.username or parts.password:
        raise ValueError("An HTTPS URL without embedded credentials is required")
    if parts.port not in (None, 443):
        raise ValueError("Unexpected port")
    host = parts.hostname.lower().removeprefix("www.")
    path = parts.path or "/"
    return urlunsplit(("https", host, path, parts.query, ""))


def load_rows(path: Path) -> list[dict]:
    if path.suffix.lower() == ".csv":
        with path.open(newline="", encoding="utf-8-sig") as stream:
            return list(csv.DictReader(stream))
    if path.suffix.lower() == ".json":
        rows = json.loads(path.read_text(encoding="utf-8"))
        if isinstance(rows, dict):
            rows = rows.get("websites", [])
        if not isinstance(rows, list) or not all(isinstance(row, dict) for row in rows):
            raise ValueError("Expected a JSON list of website records")
        return rows
    raise ValueError("Only .csv and .json sources are supported")


def validate_record(row: dict, category_ids: set[str]) -> dict:
    required = ("id", "title", "description", "url", "organization", "level", "sourceUrl", "sourceType")
    for name in required:
        if not isinstance(row.get(name), str) or not row[name].strip():
            raise ValueError(f"Missing {name}")
    canonical_url(row["url"])
    canonical_url(row["sourceUrl"])
    categories = row.get("categories", [])
    if isinstance(categories, str):
        categories = [item.strip() for item in categories.split("|") if item.strip()]
    if not categories or not set(categories) <= category_ids:
        raise ValueError("Unknown or missing category")
    keywords = row.get("keywords", [])
    if isinstance(keywords, str):
        keywords = [item.strip() for item in keywords.split("|") if item.strip()]
    if not isinstance(keywords, list):
        raise ValueError("Invalid keywords")
    if row.get("browserSupport", "external") not in ("external", "in_app"):
        raise ValueError("Invalid browser support")
    if row.get("browserJavaScript", False) and row.get("browserSupport", "external") != "in_app":
        raise ValueError("JavaScript requires reviewed in-app browser support")
    return {**row, "url": row["url"].strip(), "categories": categories, "keywords": keywords}


def import_source(path: Path, catalog: dict, current_queue: dict | None = None) -> dict:
    existing = catalog["websites"]
    ids = {row["id"] for row in existing}
    urls = {canonical_url(row["url"]) for row in existing}
    category_ids = {row["id"] for row in catalog["categories"]}
    now = datetime.now(timezone.utc).isoformat(timespec="seconds")
    queue = []
    seen_ids, seen_urls = set(), set()
    queued = (current_queue or {}).get("records", [])
    queued_ids = {item.get("record", {}).get("id") for item in queued}
    queued_urls = set()
    for item in queued:
        try:
            queued_urls.add(canonical_url(item["record"]["url"]))
        except (KeyError, ValueError, TypeError):
            pass
    for index, raw in enumerate(load_rows(path), 1):
        status, reason = "DISCOVERED", "Ownership, destination and taxonomy need editorial review"
        try:
            key = canonical_url(raw["url"])
            if raw.get("id") in queued_ids or key in queued_urls:
                continue  # Re-importing the same retained candidate is idempotent.
            if raw.get("id") in ids or raw.get("id") in seen_ids or \
                    key in urls or key in seen_urls:
                status, reason = "NEEDS_URL_REVIEW", "Duplicate ID or canonical URL"
            else:
                seen_ids.add(raw.get("id"))
                seen_urls.add(key)
                try:
                    validate_record(raw, category_ids)
                except (ValueError, TypeError) as error:
                    reason = f"Candidate needs publication metadata: {error}"
            if not raw.get("sourceUrl"):
                status, reason = "NEEDS_OWNERSHIP_REVIEW", "Missing source URL"
        except (ValueError, TypeError) as error:
            status, reason = "NEEDS_URL_REVIEW", str(error)
        except KeyError:
            status, reason = "NEEDS_URL_REVIEW", "Missing URL"
        if raw.get("reviewStatus") in CANDIDATE_STATUSES and status == "DISCOVERED":
            status = raw["reviewStatus"]
            reason = raw.get("reviewReason", reason)
        row = dict(raw)
        queue.append({"sourceFile": path.name, "sourceRow": index, "importedAt": now,
                      "status": status, "reason": reason, "record": row,
                      "reviewHistory": [{"at": now, "status": status, "reason": reason}]})
    return {"importedAt": now, "sourceFile": str(path), "records": queue}


def publish(queue: dict, catalog: dict, approvals: dict) -> tuple[dict, dict]:
    """Publish explicitly approved records; leave all other candidates in review."""
    published = list(catalog["websites"])
    ids = {row["id"] for row in published}
    urls = {canonical_url(row["url"]) for row in published}
    category_ids = {row["id"] for row in catalog["categories"]}
    remaining = []
    for item in queue["records"]:
        raw = item["record"]
        approval = approvals.get(raw.get("id"), {})
        if item.get("status") in {"REJECTED", "RESOLVED_DUPLICATE"} or not approval.get("approvedBy") or not approval.get("ownershipEvidence"):
            remaining.append(item)
            continue
        try:
            row = validate_record(raw, category_ids)
            canonical_url(approval["ownershipEvidence"])
            directory_local = approval.get("evidencePolicy") == "directory_listed_local_v1"
            reviewed = date.fromisoformat(approval.get("sourceRetrievedOn") if directory_local else approval["verifiedOn"])
            if reviewed > date.today():
                raise ValueError("Future verification date")
            if directory_local:
                validate_directory_eligibility(raw)
            key = canonical_url(row["url"])
            if row["id"] in ids or key in urls:
                raise ValueError("Duplicate ID or canonical URL")
            row = {field: row[field] for field in (
                "id", "title", "description", "url", "organization", "level",
                "categories", "keywords", "sourceUrl", "sourceType")}
            for field in ("parentOrganization", "jurisdiction", "state", "languages", "useCases",
                          "knownLimitations", "browserSupport", "browserJavaScript", "aliases", "district",
                          "primarySector", "secondarySectors", "governmentLevel", "sourceRetrievedOn",
                          "sourceId", "sourceCategories", "sourceSectors", "sourceOrganizationIds",
                          "ownershipConfidence", "publicationScope", "rootDomain", "observedFinalUrl",
                          "healthStatus", "healthCheckedAt", "sourceStateCode", "classificationConfidence"):
                if field in raw:
                    row[field] = raw[field]
            for field in ("websiteEntityId", "landingPageId"):
                if field in raw:
                    row[field] = raw[field]
            for field in ("rootUrl", "firstDiscovered", "lastSourceChecked", "purposes", "sourceStates"):
                if field in raw:
                    row[field] = raw[field]
            row.update(verificationStatus="directory_listed" if directory_local else "verified",
                       verifiedOn="" if directory_local else reviewed.isoformat())
            if directory_local:
                row["sourceAuthority"] = "LISTED_BY_OFFICIAL_DIRECTORY"
                row["publicationEligibility"] = "LOCAL_DIRECTORY_ONLY"
                row["sourceEvidence"] = [{k: evidence[k] for k in
                    ("sourceId", "sourcePage", "listingPage", "detailUrl", "retrievedAt", "originalName", "originalUrl", "category", "preview")
                    if k in evidence} for evidence in raw["sourceEvidence"]]
                for evidence in row["sourceEvidence"]:
                    evidence["originalUrl"] = evidence["originalUrl"].strip()
                row["healthStatus"] = raw["destinationHealth"]["status"]
                row["healthCheckedAt"] = raw["destinationHealth"]["checkedAt"]
                row["observedFinalUrl"] = raw["destinationHealth"]["finalUrl"]
            row["ownershipEvidence"] = approval["ownershipEvidence"]
            row["reviewedBy"] = approval["approvedBy"]
            row["reviewHistory"] = item.get("reviewHistory", []) + [{
                "at": datetime.now(timezone.utc).isoformat(timespec="seconds"),
                "status": "DIRECTORY_LISTED_LOCAL" if directory_local else "VERIFIED", "reviewer": approval["approvedBy"],
                "evidence": approval["ownershipEvidence"],
            }]
            published.append(row)
            ids.add(row["id"])
            urls.add(key)
        except (ValueError, TypeError, KeyError) as error:
            if approval.get("evidencePolicy") == "directory_listed_local_v1" and str(error) == "Duplicate ID or canonical URL":
                item = {**item, "status": "NEEDS_URL_REVIEW", "reviewHistory": item.get("reviewHistory", []) + [{
                    "at": datetime.now(timezone.utc).isoformat(timespec="seconds"), "status": "NEEDS_URL_REVIEW",
                    "reason": "Legacy catalog uniqueness collision; distinct source URL retained, equivalence not assumed"}]}
            remaining.append({**item, "reason": str(error)})
    return {**catalog, "version": catalog["version"] + (len(published) > len(catalog["websites"])),
            "websites": published}, {**queue, "records": remaining}


def validate_directory_eligibility(row):
    """A local directory entry is not a manually verified official website.

    The explicit policy is intentionally narrow: attributable IGOD linkage,
    secure institutional destination, successful safe check, no redirect/owner
    ambiguity, and local-development scope pending NIC reproduction permission.
    """
    from prepare_discovery_catalog import eligible
    if row.get("sourceId") != "igod" or row.get("publicationScope") != "local_development" or not eligible(row):
        raise ValueError("Not eligible for local directory publication")
    if row.get("reviewStatus") in {"REJECTED", "NEEDS_URL_REVIEW", "NEEDS_OWNERSHIP_REVIEW"}:
        raise ValueError("Unresolved URL/ownership review")
    if row.get("reviewConflicts"):
        raise ValueError("Unresolved source or jurisdiction conflicts")
    for evidence in row["sourceEvidence"]:
        if urlsplit(evidence["sourcePage"]).hostname != "igod.gov.in":
            raise ValueError("Unsupported source evidence")
        if evidence.get("sourceId") != "igod" or not evidence.get("retrievedAt") or not evidence.get("originalUrl"):
            raise ValueError("Incomplete source evidence")
        if canonical_url(evidence["originalUrl"]) != canonical_url(row["url"]):
            raise ValueError("Evidence does not support this destination")
        verify_source_snapshot(evidence)
    health = row["destinationHealth"]
    from review_queue import safe_redirect
    if not safe_redirect(health, row["url"]):
        raise ValueError("Unsafe or incomplete destination redirect chain")
    if not health.get("checkedAt") or not 200 <= health.get("httpStatus", 0) < 300:
        raise ValueError("Missing conclusive destination check")
    checked_date = datetime.fromisoformat(health["checkedAt"].replace("Z", "+00:00")).date()
    age_days = (date.today() - checked_date).days
    if not 0 <= age_days <= 30:
        raise ValueError("Destination check is future-dated or older than the 30-day admission window")
    final_host = urlsplit(health.get("finalUrl", "")).hostname
    if not final_host or final_host.removeprefix("www.") != urlsplit(row["url"]).hostname.removeprefix("www."):
        raise ValueError("Unexpected final destination")


def verify_source_snapshot(evidence):
    """Require cached successful source evidence, not just an asserted source URL."""
    snapshot = evidence.get("snapshot")
    if not snapshot:
        raise ValueError("Missing source retrieval snapshot")
    path = Path(snapshot)
    if not path.is_absolute():
        path = ROOT / path
    path = path.resolve()
    if not path.is_relative_to((ROOT / "data/collection").resolve()) or not path.is_file():
        raise ValueError("Source snapshot must be retained inside data/collection")
    meta_path = path.with_suffix(".json")
    if not meta_path.exists():
        raise ValueError("Missing source retrieval metadata")
    meta = json.loads(meta_path.read_text(encoding="utf-8"))
    from government_sources import normalize_url
    if meta.get("status") != "OK" or meta.get("httpStatus") != 200 or normalize_url(meta.get("url", "")) != normalize_url(evidence["sourcePage"]):
        raise ValueError("Source retrieval does not establish this page")
    stat = path.stat()
    digest, listings = _source_snapshot_listings(str(path), stat.st_mtime_ns, stat.st_size, meta["url"])
    if meta.get("sha256") != digest:
        raise ValueError("Source snapshot hash mismatch")
    key = normalize_url(evidence["originalUrl"])
    name = " ".join(evidence.get("originalName", "").split()).casefold()
    if not any(url == key and (not name or original_name == name) for url, original_name in listings):
        raise ValueError("Source snapshot does not contain the asserted listing")


@lru_cache(maxsize=4096)
def _source_snapshot_listings(path, modified, size, source_url):
    from government_sources import IgodAdapter, normalize_url
    content = Path(path).read_bytes()
    entries = IgodAdapter.parse(content.decode("utf-8"), source_url)["entries"]
    result = set()
    for row in entries:
        try:
            result.add((normalize_url(row.get("originalUrl") or ""), " ".join(row["originalName"].split()).casefold()))
        except ValueError:
            continue
    return hashlib.sha256(content).hexdigest(), result


def stage_discovery(rows, catalog, queue):
    """Merge evidence, preserving curated records, rejected rows and review history."""
    from government_sources import normalize_url
    existing_urls = {normalize_url(r["url"]): r for r in catalog["websites"]}
    resolved_aliases = {normalize_url(r["url"]): r for r in queue.get("resolvedAliases", [])}
    queued = {}
    for item in queue["records"]:
        try:
            queued[normalize_url(item["record"]["url"])] = item
        except (ValueError, KeyError):
            queued[item["record"].get("url")] = item
    now = datetime.now(timezone.utc).isoformat(timespec="seconds")
    counts = {"alreadyPublished": 0, "newCandidates": 0, "updatedCandidates": 0, "refreshedLocalListings": 0, "localRefreshConflicts": 0, "localHeldForReview": 0, "resolvedAliasesRetained": 0}
    approvals = {}
    for row in rows:
        try:
            key = normalize_url(row["url"])
        except ValueError:
            key = row["url"]
        if key in resolved_aliases:
            from review_queue import safe_redirect, name_key
            alias = resolved_aliases[key]
            health = row.get("destinationHealth", {})
            target_exists = any(r["id"] == alias["targetId"] for r in catalog["websites"])
            identity_unchanged = bool(set(row.get("sourceOrganizationIds", [])) & set(alias.get("sourceOrganizationIds", []))) or name_key(row.get("title")) == name_key(alias["title"])
            if target_exists and identity_unchanged and health.get("status") == "RESPONDING" and safe_redirect(health, key) and health.get("finalUrl") == alias["observedFinalUrl"]:
                counts["resolvedAliasesRetained"] += 1
                continue
            row["reviewStatus"] = "NEEDS_URL_REVIEW"
            row["reviewReason"] = "Previously resolved alias evidence changed; confirmation required"
        if key in existing_urls:
            counts["alreadyPublished"] += 1
            old = existing_urls[key]
            if old.get("verificationStatus") == "directory_listed":
                health = row.get("destinationHealth", {})
                if health.get("status") != "RESPONDING" and health.get("checkedAt", "") >= (old.get("latestHealthCheckedAt") or old.get("healthCheckedAt", "")):
                    latest = {"latestHealthStatus": health.get("status", "NOT_CHECKED"), "latestHealthCheckedAt": health.get("checkedAt"), "latestHealthReason": health.get("reason", "Inconclusive check")}
                    if any(old.get(k) != v for k, v in latest.items()):
                        old.update(latest)
                        old.setdefault("reviewHistory", []).append({"at": now, "status": "DESTINATION_CHECK_INCONCLUSIVE", "reason": latest["latestHealthReason"]})
                        counts["refreshedLocalListings"] += 1
                    if health.get("status") != "URL_REVIEW":
                        continue  # Keep prior valid evidence; expose the newer uncertainty.
                if row.get("healthStatus") == "URL_REVIEW":
                    row["id"] = old["id"]
                    row["firstDiscovered"] = old.get("firstDiscovered", row["firstDiscovered"])
                    reason = row.get("destinationHealth", {}).get("reason", "Destination URL requires review")
                    held = {"record": row, "status": "NEEDS_URL_REVIEW", "reason": reason,
                        "collectionManaged": True, "priorPublishedRecord": old,
                        "reviewHistory": old.get("reviewHistory", []) + [{"at": now, "status": "NEEDS_URL_REVIEW", "reason": reason}]}
                    queue["records"].append(held)
                    queued[key] = held
                    catalog["websites"] = [r for r in catalog["websites"] if r["id"] != old["id"]]
                    counts["localHeldForReview"] += 1
                    continue
                try:
                    row["id"] = old["id"]
                    row["firstDiscovered"] = old.get("firstDiscovered", row["firstDiscovered"])
                    approval = {row["id"]: {"approvedBy": "JanSetu local directory evidence policy v1 (automated)",
                        "ownershipEvidence": row["sourceUrl"], "sourceRetrievedOn": row["sourceRetrievedOn"],
                        "evidencePolicy": "directory_listed_local_v1"}}
                    refreshed, _ = publish({"records": [{"record": row, "status": "DISCOVERED"}]},
                                           {**catalog, "websites": []}, approval)
                    if not refreshed["websites"]:
                        raise ValueError("Fresh local evidence does not pass publication policy")
                    updated = refreshed["websites"][0]
                    for field in ("latestHealthStatus", "latestHealthCheckedAt", "latestHealthReason"):
                        if field in old:
                            updated[field] = old[field]
                    if old.get("latestHealthCheckedAt") and health.get("checkedAt", "") >= old["latestHealthCheckedAt"]:
                        updated.update(latestHealthStatus=health["status"], latestHealthCheckedAt=health["checkedAt"], latestHealthReason=health.get("reason", "Recorded response"))
                    updated["reviewHistory"] = old.get("reviewHistory", [])
                    if updated != old:
                        updated["reviewHistory"] = updated["reviewHistory"] + [{"at": now, "status": "DIRECTORY_LISTED_LOCAL_REFRESH", "reason": "Updated source evidence; curated records unaffected"}]
                        old.clear()
                        old.update(updated)
                        counts["refreshedLocalListings"] += 1
                except (ValueError, KeyError, TypeError):
                    counts["localRefreshConflicts"] += 1
            continue  # Never rewrite a curated production record from collection.
        if key in queued:
            item = queued[key]
            # Only collector-generated metadata may refresh; editor fields survive.
            if item.get("collectionManaged"):
                previous = item["record"]
                row["firstDiscovered"] = previous.get("firstDiscovered", row["firstDiscovered"])
                row["id"] = previous["id"]
                if item.get("triage"):
                    row["reviewStatus"] = item["status"]
                    row["reviewConflicts"] = item["triage"].get("unresolvedConflicts", [])
                item["record"] = row
            else:
                for field in ("sourceEvidence", "destinationHealth", "healthStatus", "healthCheckedAt"):
                    item["record"][field] = row.get(field)
                # Generic discovered/transient candidates can meet the newly
                # authorized LOCAL listing policy. Explicit ownership/URL questions
                # and rejections still require an editor; their decisions survive.
                if item["status"] not in {"DISCOVERED", "TEMPORARILY_UNREACHABLE"}:
                    continue
                try:
                    validate_directory_eligibility(row)
                except (ValueError, TypeError, KeyError):
                    continue
                old_record = item["record"]
                item.setdefault("priorEditorRecord", old_record)
                row["id"] = old_record["id"]
                row["firstDiscovered"] = item.get("importedAt", row["firstDiscovered"])
                item["record"] = row
                item["collectionManaged"] = True
            counts["updatedCandidates"] += 1
        else:
            item = {"record": row, "status": row["reviewStatus"], "reason": row.get("reviewReason", "Official-source discovery; local evidence policy pending"),
                    "sourceFile": "normalized_candidates.json", "importedAt": now, "collectionManaged": True,
                    "reviewHistory": [{"at": now, "status": row["reviewStatus"], "reason": "Staged by collection pipeline"}]}
            queue["records"].append(item)
            queued[key] = item
            counts["newCandidates"] += 1
        # Do not overwrite a recorded editorial rejection or URL/ownership decision.
        if item["status"] not in {"REJECTED", "RESOLVED_DUPLICATE", "NEEDS_URL_REVIEW", "NEEDS_OWNERSHIP_REVIEW"}:
            try:
                validate_directory_eligibility(row)
                approvals[row["id"]] = {"approvedBy": "JanSetu local directory evidence policy v1 (automated)",
                    "ownershipEvidence": row["sourceUrl"], "sourceRetrievedOn": row["sourceRetrievedOn"],
                    "evidencePolicy": "directory_listed_local_v1"}
            except (ValueError, TypeError, KeyError):
                pass
    queue["importedAt"] = now
    if counts["refreshedLocalListings"] or counts["localHeldForReview"]:
        catalog["version"] += 1
    return queue, approvals, counts


def coverage_report(catalog: dict, queue: dict) -> str:
    websites = catalog["websites"]
    profiles = json.loads((ROOT / "data/portal_profiles.json").read_text(encoding="utf-8"))
    taxonomy = json.loads((ROOT / "data/taxonomy.json").read_text(encoding="utf-8"))
    sources = json.loads((ROOT / "data/source_registry.json").read_text(encoding="utf-8"))
    domains = {urlsplit(row["url"]).hostname for row in websites}
    represented = {category for row in websites for category in row["categories"]}
    states = {row["state"] for row in websites if row.get("state")}
    candidate_states = {item["record"].get("state") for item in queue["records"] if item["record"].get("state")}
    level_counts = Counter(profiles[row["id"]]["level"] for row in websites)
    sector_counts = Counter(profiles[row["id"]]["primarySector"] for row in websites)
    status_counts = Counter(item["status"] for item in queue["records"])
    source_counts = {}
    for row in websites:
        source_counts[row["sourceType"]] = source_counts.get(row["sourceType"], 0) + 1
    lines = [
        "# Catalog coverage — generated from local data",
        "",
        "Run `python tools/catalog_pipeline.py report` to regenerate this file. Counts describe retained records, not all government websites.",
        "",
        "| Measure | Actual |", "| --- | ---: |",
        f"| Discovered/retained records including archived aliases | {len(websites) + len(queue['records']) + len(queue.get('resolvedAliases', []))} |",
        f"| Active bundled/review records | {len(websites) + len(queue['records'])} |",
        f"| Bundled websites (local development catalog) | {len(websites)} |",
        f"| Manually source-verified websites | {sum(r['verificationStatus'] == 'verified' for r in websites)} |",
        f"| Official-directory-listed local entries | {sum(r['verificationStatus'] == 'directory_listed' for r in websites)} |",
        f"| Published services | {len(catalog.get('services', []))} |",
        f"| Source-backed guides | {len(catalog.get('guides', []))} |",
        f"| Unique published domains | {len(domains)} |",
        f"| Retained unpublished candidates | {len(queue['records'])} |",
        f"| Confirmed source aliases retained separately | {len(queue.get('resolvedAliases', []))} |",
        f"| Rejected candidates retained | {status_counts['REJECTED']} |",
        f"| Duplicate candidate rows in review | {sum('Duplicate' in item.get('reason', '') for item in queue['records'])} |",
        f"| Categories represented | {len(represented)} |",
        f"| States/UTs with a locally bundled entry | {len(states)} of {len(taxonomy['states']) + len(taxonomy['unionTerritories'])} |",
        f"| States/UTs with a retained candidate | {len(candidate_states)} |",
        f"| Published individual service records | {len(catalog.get('services', []))} |",
        "| Link and content freshness | See separate `docs/LINK_AUDIT.md`; content freshness not systematically measured |",
        "", "## Published websites by level", "",
    ]
    lines.extend(f"- {level}: {level_counts[level]}" for level in taxonomy["levels"])
    lines.extend(["", "## Published websites by primary sector", ""])
    lines.extend(f"- {sector}: {sector_counts[sector]}" for sector in taxonomy["sectors"])
    lines.extend(["", "## Candidate review statuses", ""])
    lines.extend(f"- {status}: {count}" for status, count in sorted(status_counts.items()))
    lines.extend(["", "## State/UT coverage", "",
                  f"Published: {', '.join(sorted(states)) or 'none'}.",
                  f"Candidates only: {', '.join(sorted(candidate_states - states)) or 'none'}.",
                  f"No bundled entry: {', '.join(sorted(set(taxonomy['states'] + taxonomy['unionTerritories']) - states)) or 'none'}.",
                  "", "## Published sources by attribution type", ""])
    lines.extend(f"- {name}: {count}" for name, count in sorted(source_counts.items()))
    candidate_sources = Counter(item["record"].get("sourceId", "legacy_manual") for item in queue["records"])
    lines.extend(["", "## Candidate source registry IDs", ""])
    lines.extend(f"- {name}: {count}" for name, count in sorted(candidate_sources.items()))
    lines.extend(["", "Administrative denominators, LGD identifiers and unresolved unit links: [administrative reconciliation](ADMINISTRATIVE_COVERAGE_RECONCILIATION.md). Review decisions and separate examined/resolved/published counts: [review queue report](REVIEW_QUEUE_REPORT.md).", "", f"Last candidate import: {queue.get('importedAt', 'none')}",
                  f"Last source-registry check: {sources['checkedAt']}",
                  "", "Counts cover retained local records only. Legacy services.india.gov.in pages are historical; its root now redirects to india.gov.in/services. No nationwide coverage or continuous link-monitoring claim is made.", ""])
    return "\n".join(lines)


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("command", choices=("import", "stage-discovery", "publish", "export", "report"))
    parser.add_argument("file", nargs="?", type=Path, help="CSV/JSON input for import; JSON approvals for publish")
    args = parser.parse_args()
    catalog = json.loads(CATALOG.read_text(encoding="utf-8"))
    queue = json.loads(QUEUE.read_text(encoding="utf-8")) if QUEUE.exists() else {"records": []}
    if args.command == "export":
        if not args.file:
            parser.error("export needs an output .json or .csv file")
        if args.file.suffix.lower() == ".json":
            args.file.write_text(json.dumps(queue, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        elif args.file.suffix.lower() == ".csv":
            columns = ["id", "title", "url", "level", "state", "district", "primarySector", "sourceUrl", "sourceType", "healthStatus", "status", "reason"]
            with args.file.open("w", newline="", encoding="utf-8-sig") as stream:
                writer = csv.DictWriter(stream, fieldnames=columns)
                writer.writeheader()
                writer.writerows({**{k: item["record"].get(k, "") for k in columns[:-2]}, "status": item["status"], "reason": item["reason"]} for item in queue["records"])
        else:
            parser.error("export requires .json or .csv")
        print(f"Exported {len(queue['records'])} review rows to {args.file}")
    elif args.command == "stage-discovery":
        if not args.file:
            parser.error("stage-discovery needs normalized_candidates.json")
        baseline = args.file.parent / "baseline_catalog.json"
        if not baseline.exists():
            baseline.write_text(json.dumps(catalog, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
            (args.file.parent / "baseline_queue.json").write_text(json.dumps(queue, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        taxonomy = json.loads((ROOT / "data/taxonomy.json").read_text(encoding="utf-8"))
        known_categories = {r["id"] for r in catalog["categories"]}
        for category in taxonomy.get("appCategories", []):
            if category["id"] not in known_categories:
                catalog["categories"].append(category)
                known_categories.add(category["id"])
        incoming_rows = load_rows(args.file)
        from review_queue import source_changes
        observed_path = args.file.parent / "source_observed.json"
        previous_rows = json.loads(observed_path.read_text(encoding="utf-8")) if observed_path.exists() else incoming_rows
        changes = source_changes(previous_rows, incoming_rows)
        queue, approvals, counts = stage_discovery(incoming_rows, catalog, queue)
        (args.file.parent / "source_changes.json").write_text(json.dumps(changes, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        observed_path.write_text(json.dumps([{k: row.get(k) for k in ("id", "title", "url", "sourceOrganizationIds")} for row in incoming_rows], ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        if counts["localHeldForReview"]:
            profiles_path = ROOT / "data/portal_profiles.json"
            profiles = json.loads(profiles_path.read_text(encoding="utf-8"))
            retained_ids = {r["id"] for r in catalog["websites"]}
            for item in queue["records"]:
                if item.get("priorPublishedRecord") and item["record"]["id"] in profiles:
                    item.setdefault("priorPublishedProfile", profiles[item["record"]["id"]])
            profiles_path.write_text(json.dumps({k: v for k, v in profiles.items() if k in retained_ids}, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        QUEUE.write_text(json.dumps(queue, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        CATALOG.write_text(json.dumps(catalog, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        out = args.file.parent / "local_approvals.json"
        out.write_text(json.dumps(approvals, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        print(json.dumps({**counts, "localEligibleApprovals": len(approvals), "approvals": str(out)}))
    elif args.command == "import":
        if not args.file:
            parser.error("import needs a source file")
        incoming = import_source(args.file, catalog, queue)
        queue["records"].extend(incoming["records"])
        queue["importedAt"] = incoming["importedAt"]
        QUEUE.parent.mkdir(exist_ok=True)
        QUEUE.write_text(json.dumps(queue, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        print(f"Staged {len(incoming['records'])} rows for review at {QUEUE}")
    elif args.command == "publish":
        if not args.file:
            parser.error("publish needs an approvals JSON file")
        updated, remaining = publish(queue, catalog, json.loads(args.file.read_text(encoding="utf-8")))
        # Profiles stay separate from website/application entities and preserve
        # all manually curated metadata. Newly bundled entries get source-based
        # research classification, explicitly retaining owner/operator uncertainty.
        profiles_path = ROOT / "data/portal_profiles.json"
        profiles = json.loads(profiles_path.read_text(encoding="utf-8"))
        originals = {item["record"]["id"]: item["record"] for item in queue["records"]}
        for website in updated["websites"]:
            if website["id"] in profiles and website["verificationStatus"] == "verified":
                continue
            raw = originals.get(website["id"], website)
            profiles[website["id"]] = {"level": raw.get("governmentLevel", "other_official_institution"),
                "primarySector": raw.get("primarySector", "other_verified"),
                "secondarySectors": raw.get("secondarySectors", []), "purposes": raw.get("purposes", ["information"]),
                "rootUrl": raw.get("rootUrl", "https://" + urlsplit(raw["url"]).netloc + "/"),
                "landingUrl": raw["url"], "owner": None, "operator": None,
                "citizenIntents": raw.get("keywords", [raw["title"]]),
                "confidence": raw.get("classificationConfidence", "low"),
                "notes": "Official-directory classification, not independent ownership or transaction review"}
        profiles_path.write_text(json.dumps(profiles, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        CATALOG.write_text(json.dumps(updated, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        QUEUE.write_text(json.dumps(remaining, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        print(f"Published {len(updated['websites']) - len(catalog['websites'])}; {len(remaining['records'])} remain in review")
    else:
        from service_catalog import merged_catalog
        report = coverage_report(merged_catalog(catalog), queue)
        path = ROOT / "docs/CATALOG_COVERAGE.md"
        path.write_text(report, encoding="utf-8")
        print(report)


if __name__ == "__main__":
    main()
