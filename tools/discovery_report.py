"""Reconcile a real collection manifest, normalize counts, and write coverage."""

import argparse
import json
import re
from collections import Counter
from pathlib import Path

from government_sources import now, save_json, normalize_url

ROOT = Path(__file__).resolve().parents[1]


def category_matrix(manifest, candidates=None, bundled_ids=None, pending_ids=None):
    pages = manifest["pages"]
    rows = []
    for url, parent in pages.items():
        if "listingPage" in parent or not (url.endswith("/organizations") or url.endswith("/districts") or url.endswith("/new_additions")):
            continue
        planned = [parent] + [pages[p["url"]] for p in parent.get("pagination", []) if p["url"] in pages]
        processed = [p for p in planned if p["status"] == "PROCESSED"]
        expected = parent.get("expectedResults")
        retrieved = sum(len(p.get("entries", [])) for p in processed)
        failures = [p for p in planned if p["status"] in {"FAILED", "PARSE_ERROR", "ROBOTS_DISALLOWED"}]
        pending = sum(p["status"] == "PENDING" for p in planned)
        complete = parent["status"] == "PROCESSED" and not failures and not pending and expected is not None and retrieved == expected
        attributed = [r for r in (candidates or []) if any(e.get("listingPage") == url for e in r.get("sourceEvidence", []))]
        bundled = sum(r["id"] in (bundled_ids or set()) for r in attributed)
        rows.append({"url": url, "name": parent.get("name", ""), "expectedResults": expected,
                     "expectedPages": len(planned) if parent["status"] == "PROCESSED" else None,
                     "processedPages": len(processed), "retrievedRows": retrieved,
                     "duplicateRowsWithinListing": retrieved - len({e.get("detailUrl") or (e["originalName"], e.get("originalUrl")) for p in processed for e in p.get("entries", [])}),
                     "pendingPages": pending, "failedPages": len(failures), "complete": complete,
                     "gap": None if expected is None else expected - retrieved,
                     "normalizedUrls": len(attributed), "bundledRecords": bundled,
                     "awaitingReview": sum(r["id"] in pending_ids for r in attributed) if pending_ids is not None else len(attributed) - bundled,
                     "resolvedAliasesOrExcluded": sum(r["id"] not in (bundled_ids or set()) and r["id"] not in pending_ids for r in attributed) if pending_ids is not None else 0,
                     "eligibleLocal": sum(r.get("publicationStatus") == "ELIGIBLE_LOCAL_DIRECTORY" for r in attributed),
                     "remainingReason": "No source total/history available" if expected is None else
                         ("" if complete else "Unprocessed/failed pages or count mismatch"),
                     "failures": [{"url": p["url"], "status": p["status"], "error": p.get("error")} for p in failures]})
    return rows


def state_coverage(manifest, rows, catalog, matrix):
    result = []
    for code, state in sorted(manifest["stateBranches"].items(), key=lambda pair: pair[1]["name"]):
        branch_pages = [p for p in manifest["pages"].values() if re.search(r"/(?:sg|leg|apx)/" + code + r"/", p["url"])]
        listings = [r for r in matrix if re.search(r"/(?:sg|leg|apx)/" + code + r"/", r["url"])]
        district_listings = [r for r in listings if "/E042/" in r["url"]]
        candidates = [r for r in rows if r.get("state") == state["name"]]
        bundled = [r for r in catalog["websites"] if r.get("state") == state["name"]]
        district_names = {r["district"] for r in bundled if r.get("district")}
        result.append({"state": state["name"], "sourceCode": code, "codeType": "IGOD navigation code, not an LGD administrative ID",
                       "branchEnumerated": state["url"] in manifest["pages"] and manifest["pages"][state["url"]]["status"] == "PROCESSED",
                       "discoveredPages": len(branch_pages), "processedPages": sum(p["status"] == "PROCESSED" for p in branch_pages),
                       "listingCategories": len(listings), "completeListingCategories": sum(r["complete"] for r in listings),
                       "branchesComplete": bool(listings) and all(r["complete"] for r in listings) and all(p["status"] == "PROCESSED" for p in branch_pages),
                       "sourceDistrictOrganizations": sum(r["expectedResults"] or 0 for r in district_listings),
                       "normalizedUrls": len(candidates), "bundledWebsites": len(bundled), "bundledDistrictNames": len(district_names)})
    return result


def write_report(batch):
    directory = ROOT / "data/collection" / batch
    manifest = json.loads((directory / "manifest.json").read_text(encoding="utf-8"))
    summary = json.loads((directory / "summary.json").read_text(encoding="utf-8"))
    rows = json.loads((directory / "normalized_candidates.json").read_text(encoding="utf-8"))
    from service_catalog import merged_catalog
    catalog = merged_catalog(json.loads((ROOT / "app/src/main/assets/catalog.json").read_text(encoding="utf-8")))
    queue = json.loads((ROOT / "data/review_queue.json").read_text(encoding="utf-8"))
    baseline_path = directory / "baseline_catalog.json"
    baseline = json.loads(baseline_path.read_text(encoding="utf-8")) if baseline_path.exists() else catalog
    matrix = category_matrix(manifest, rows, {r["id"] for r in catalog["websites"]}, {i["record"]["id"] for i in queue["records"]})
    states = state_coverage(manifest, rows, catalog, matrix)
    old_by_id = {r["id"]: r for r in baseline["websites"]}
    current_by_id = {r["id"]: r for r in catalog["websites"]}
    diff = {"baselineWebsites": len(old_by_id), "currentWebsites": len(current_by_id),
            "addedIds": sorted(set(current_by_id) - set(old_by_id)), "removedIds": sorted(set(old_by_id) - set(current_by_id)),
            "changedOriginalIds": sorted(i for i in old_by_id if i in current_by_id and old_by_id[i] != current_by_id[i])}
    save_json(directory / "catalog_diff.json", diff)
    save_json(directory / "source_coverage.json", {"batch": batch, "generatedAt": now(), "categories": matrix, "states": states})
    sectors = Counter(r.get("primarySector") or "curated_profile" for r in catalog["websites"])
    profiles = json.loads((ROOT / "data/portal_profiles.json").read_text(encoding="utf-8"))
    sectors = Counter(profiles[r["id"]]["primarySector"] for r in catalog["websites"])
    all_failures = [p for p in manifest["pages"].values() if p["status"] in {"FAILED", "PARSE_ERROR", "ROBOTS_DISALLOWED"}]
    pending = [p for p in manifest["pages"].values() if p["status"] == "PENDING"]
    source_orgs = json.loads((directory / "organizations.json").read_text(encoding="utf-8"))
    url_groups = len(rows)
    old_urls = {normalize_url(r["url"]) for r in baseline["websites"]}
    already = sum(normalize_url(r["url"]) in old_urls for r in rows)
    queue_statuses = Counter(item["status"] for item in queue["records"])
    unresolved_robots = Counter(r["destinationHealth"].get("reason", "unknown") for r in rows if r["healthStatus"] == "ROBOTS_UNRESOLVED")
    redirect_reasons = Counter(r["destinationHealth"].get("reason", "unknown") for r in rows if r["healthStatus"] == "REDIRECT_REVIEW")
    lines = ["# Phase 2A live collection run", "", f"Batch `{batch}`, adapter/manifest version {manifest['version']}. Generated {now()}.",
             "", "This is a real, cached public-source integration run. Fixture tests are reported separately. The bundled additions are for **local development only**, following the user's instruction; NIC reproduction permission is required before public distribution. A directory listing and an HTTP response are not manual ownership or service verification.",
             "", "| Measure | Actual |", "| --- | ---: |",
             f"| Raw listing rows, including category previews/repeated listings | {summary['rawEntries']} |",
             f"| Distinct source organization identities (uncertain matches retained) | {len(source_orgs)} |",
             f"| Unique normalized source URLs before redirect equivalence | {url_groups} |",
             f"| Distinct source website hosts | {len({r['rootDomain'] for r in rows})} |",
             f"| Source URLs matching old catalog landing URLs | {already} |",
             f"| Existing curated website records preserved | {len(old_by_id) - len(diff['removedIds']) - len(diff['changedOriginalIds'])} |",
             f"| New locally bundled website records | {len(diff['addedIds'])} |",
             f"| Source/health-qualified URLs before editor decisions and uniqueness gates | {summary['localEligible']} |",
             f"| Current bundled websites | {len(catalog['websites'])} |",
             f"| Current individual services | {len(catalog.get('services', []))} |",
             f"| Retained unpublished website candidates | {len(queue['records'])} |",
             f"| Source organizations lacking a valid website URL | {summary['missingOrInvalidUrlOrganizations']} |",
             f"| Suspected organization duplicate groups | {summary['suspectedDuplicateGroups']} |",
             f"| Rejected / ownership-review / URL-review queue records | {queue_statuses['REJECTED']} / {queue_statuses['NEEDS_OWNERSHIP_REVIEW']} / {queue_statuses['NEEDS_URL_REVIEW']} |",
             "| Newly confirmed permanently retired destinations | 0 (errors are inconclusive; no automatic retirement) |",
             f"| Observed safe redirect-equivalence groups / aliases | {summary.get('safeRedirectGroups',0)} / {summary.get('safeRedirectAliases',0)} |",
             f"| IGOD listing categories discovered / count-reconciled | {len(matrix)} / {sum(r['complete'] for r in matrix)} |",
             f"| Total pages discovered / processed / pending / failed | {len(manifest['pages'])} / {sum(p['status']=='PROCESSED' for p in manifest['pages'].values())} / {len(pending)} / {len(all_failures)} |",
             "", "Raw rows are not website counts. One organization can have no URL or multiple URLs, and many sector/category listings repeat the same organization. Exact URLs are normalized without inventing HTTPS; observed safe redirects are recorded separately. Source organization IDs anchor cross-directory matches. Same-name/different-URL cases remain review groups. `organizations.json`, `website_entities.json` and normalized landing-page records distinguish organization, website host and URL; no fictitious service entities are generated. The 15-row New Additions view exposes no total/history export or pagination, so only its observed rows are claimed.",
             "", "## Per-state/UT coverage", "", "IGOD codes below are source navigation codes. No LGD administrative identifiers were guessed. A visited state homepage does not mean its directory is complete.", "",
             "| State/UT | IGOD code | Branch enumerated | Complete / listing categories | Normalized URLs | Bundled | District names bundled |",
             "| --- | --- | --- | ---: | ---: | ---: | ---: |"]
    for s in states:
        lines.append(f"| {s['state']} | {s['sourceCode']} | {'Yes' if s['branchEnumerated'] else 'No'} | {s['completeListingCategories']} / {s['listingCategories']} | {s['normalizedUrls']} | {s['bundledWebsites']} | {s['bundledDistrictNames']} |")
    lines += ["", "## Bundled primary-sector coverage", ""]
    lines += [f"- {sector}: {count}" for sector, count in sorted(sectors.items())]
    lines += ["", "## Destination checks (source authority is a separate field)", ""]
    lines += [f"- {status}: {count}" for status, count in sorted(summary["health"].items())]
    lines += ["", "Robots-unresolved causes (destination was not retrieved):"]
    lines += [f"- {reason}: {count}" for reason, count in sorted(unresolved_robots.items())]
    lines += ["", "Redirect-review reasons (unsafe/unexpected targets were not followed):"]
    lines += [f"- {reason}: {count}" for reason, count in sorted(redirect_reasons.items())]
    lines += ["", "Exact affected URLs, statuses, response codes and errors are in `destination_health.json`; suspected organization matches and safe aliases are in `duplicate_review.json` and `safe_redirect_equivalents.json`. Unpublished records retain their decisions in `review_queue.json`."]
    lines += ["", "## Source category and pagination matrix", "", "All expected totals and offsets come from the acquired public page/JavaScript. Partial HTML uses the AJAX header observed during normal browser scroll. Requests retain the source-provided page sizes (usually five rows); a probe with a larger limit returned 302 and was not used. Count mismatches keep a category incomplete.", "",
              "| Source category URL | Expected rows | Pages processed / expected | Retrieved rows | Gap | Complete | URLs | Bundled | Awaiting review |",
              "| --- | ---: | ---: | ---: | ---: | --- | ---: | ---: | ---: |"]
    for r in matrix:
        lines.append(f"| [{r['name'].replace('|','/')}](" + r['url'] + f") | {r['expectedResults'] if r['expectedResults'] is not None else '?'} | {r['processedPages']} / {r['expectedPages'] or '?'} | {r['retrievedRows']} | {r['gap'] if r['gap'] is not None else '?'} | {'Yes' if r['complete'] else 'No'} | {r['normalizedUrls']} | {r['bundledRecords']} | {r['awaitingReview']} |")
    lines += ["", "## Exact source failures and remaining acquisition", ""]
    if all_failures:
        lines += [f"- `{p['url']}`: {p['status']}; {p.get('error', '')}" for p in all_failures]
    else:
        lines.append("No failed pages in the current manifest.")
    lines.append(f"Pending source pages: {len(pending)}. Exact URLs and per-page retry/cache evidence are in `data/collection/{batch}/manifest.json`.")
    lines += ["", "## Secondary sources and administrative coverage", "",
              "- National Portal HTML was available through normal TLS curl, but its directory fields still contain dynamic Loading/zero values. The earlier normal Chrome check returned 403. It is IGOD-derived and is not counted as an independent inventory; no export or bypass was invented.",
              "- data.gov.in redirects to www.data.gov.in, whose robots.txt disallows automated retrieval. Dataset collection stopped. Use a permitted official dataset/API workflow before resuming that source.",
              "- Phase 2A LGD inspection timed out on state codes. Phase 2B retrieved the public 36-state code table and complete state-wise aggregate summary (784 districts, 5,050 urban bodies, 262,840 panchayats); unit-level district/local-body views require CAPTCHA. The [administrative reconciliation](ADMINISTRATIVE_COVERAGE_RECONCILIATION.md) records actual LGD state IDs and gaps. Aggregate counts are not website counts.",
              "- Source directory snapshots/metadata are cached. Destination responses retain HTTP status, redirects, body hash/size and public page title only; homepage/contact/form bodies are discarded. Anonymous session cookies and CSRF tokens are removed from stored retrieval evidence; no credentials, OTPs or filled forms are collected.",
              "", "## Publication and recovery", "",
              "HTTP source links, unexpected-host redirects, inaccessible destinations, robot-disallowed checks, missing URLs and ambiguous external operators remain separate from ordinary public links. Rejected/edited review decisions survive reruns. The nine curated records retain their original descriptions, URLs, dates and browser policy. Adding directory data never grants a readability profile or in-app browser access.",
              "", "```powershell", f"python tools/discover_government_sites.py --source igod --batch {batch} --delay 1",
              f"python tools/discover_government_sites.py --source igod --batch {batch} --retry-failed --delay 1",
              f"python tools/prepare_discovery_catalog.py --batch {batch} --check --workers 4 --timeout 10",
              f"python tools/catalog_pipeline.py stage-discovery data/collection/{batch}/normalized_candidates.json",
              f"python tools/catalog_pipeline.py publish data/collection/{batch}/local_approvals.json",
              "python tools/check_catalog.py", "python tools/inventory_audit.py", "python tools/catalog_pipeline.py report",
              "python tools/package_extension.py", "```", "",
              "Before public distribution: obtain and record NIC reproduction permission, review publication labels and inaccessible/partner destinations, obtain release signing and finish the existing accessibility/extension/device gates. The next data task is the documented unresolved source/health/ownership gap, not a new invented service inventory.", ""]
    review_path = ROOT / "data/collection/phase2b-2026-10-15/review_summary.json"
    if review_path.exists():
        review = json.loads(review_path.read_text(encoding="utf-8"))
        lines += ["", "## Phase 2B review follow-up", "",
            f"Original queue examined: {review['baselineCandidates']}; deterministic resolutions: {review['automaticallyResolved']} ({review['actuallyPublished']} local admissions and {review['duplicatesConfirmed']} archived aliases); ownership certifications: {review['ownershipCasesResolved']}; pending: {review['pending']}; rejected: {review['rejected']}.",
            "See [review queue report](REVIEW_QUEUE_REPORT.md). Source acquisition remains the Phase 2A cached run; Phase 2B does not reset destination-check dates or assert a fresh national crawl. Category pending counts exclude confirmed archived aliases."]
    path = ROOT / "docs/DISCOVERY_RUN_REPORT.md"
    path.write_text("\n".join(lines), encoding="utf-8")
    print(json.dumps({"report": str(path), "rawEntries": summary["rawEntries"], "bundled": len(catalog["websites"]),
                      "completeCategories": sum(r["complete"] for r in matrix), "categories": len(matrix), "pendingPages": len(pending)}))


if __name__ == "__main__":
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--batch", default="igod-2026-10-14")
    write_report(p.parse_args().batch)
