"""Acquire public LGD tables and reconcile them with website evidence.

No CAPTCHA submissions, hidden API calls, name-based unit merges or invented URLs.
python tools/administrative_coverage.py --collect
python tools/administrative_coverage.py
"""
import argparse
import json
import re
from collections import Counter
from pathlib import Path
from urllib.parse import urljoin, urlsplit, urlunsplit
from bs4 import BeautifulSoup
from government_sources import CachedClient, check_robots, now, save_json

ROOT = Path(__file__).resolve().parents[1]
BATCH = ROOT / "data/collection/phase2b-2026-10-15"
SOURCE = ROOT / "data/collection/igod-2026-10-14"
ORIGIN = "https://lgdirectory.gov.in"
PATHS = ("globalviewstateforcitizen.do", "reportonStatewiseEntityDetails.do",
         "getStateWiseUrbanLB.do", "globalviewdistrictforcitizen.do",
         "globalViewLocalBodyForCitizen.do", "downloadDirectory.do")


def state_key(value):
    value = re.sub(r"^the\s+", "", value.casefold().strip()).replace("&", "and")
    return " ".join(re.findall(r"[a-z0-9]+", value))


def collect():
    client = CachedClient(BATCH / "administrative_snapshots", 3, 25, retain_pages=False)
    robots_meta, robots = check_robots(client, ORIGIN)
    if robots is None or not robots.can_fetch("JanSetuLocalInventory", ORIGIN + "/"):
        raise ValueError("LGD robots unavailable or disallowed; no retrieval")
    nav_path = BATCH / "administrative_navigation.json"
    if nav_path.exists():
        navigation = json.loads(nav_path.read_text(encoding="utf-8"))
    else:
        meta, body = client.fetch(ORIGIN + "/")
        if meta.get("status") != "OK" or meta.get("httpStatus") != 200:
            raise ValueError("Public LGD navigation unavailable")
        soup = BeautifulSoup(body, "html.parser")
        links = []
        for a in soup.select("a[href]"):
            p = urlsplit(urljoin(ORIGIN + "/", a["href"]))
            if p.hostname == "lgdirectory.gov.in":
                # Retain public path only, never an anonymous-session CSRF value.
                links.append({"name": a.get_text(" ", strip=True),
                              "url": urlunsplit((p.scheme, p.netloc, p.path, "", ""))})
        navigation = {"checkedAt": now(), "robots": robots_meta, "home": meta, "links": links}
        save_json(nav_path, navigation)
    known = {x["url"] for x in navigation["links"]}
    output = BATCH / "administrative_pages.json"
    old = json.loads(output.read_text(encoding="utf-8"))["pages"] if output.exists() else []
    cached = {x["url"]: x for x in old}
    pages = []
    for path in PATHS:
        url = ORIGIN + "/" + path
        if url not in known or not robots.can_fetch("JanSetuLocalInventory", url):
            pages.append({"url": url, "status": "NOT_SOURCE_LINKED_OR_DISALLOWED", "tables": []})
            continue
        if url in cached:
            pages.append(cached[url])
            continue
        meta, body = client.fetch(url)
        soup = BeautifulSoup(body, "html.parser")
        tables = []
        for table in soup.select("table"):
            headers = [x.get_text(" ", strip=True) for x in table.select("th")]
            if any("Officer" in h or "User Name" in h for h in headers):
                continue
            rows = [[x.get_text(" ", strip=True) for x in tr.find_all(["td", "th"], recursive=False)]
                    for tr in table.select("tr")]
            tables.append({"headers": headers, "rows": rows})
        pages.append({"url": url, "retrieval": meta, "tables": tables,
                      "captchaPresent": bool(soup.select('[name*=aptcha], [id*=aptcha], img[src*=aptcha]'))})
    save_json(output, {"checkedAt": now(), "pages": pages})


def number(value):
    return int(value.replace(",", "")) if re.fullmatch(r"[\d,]+", value) else None


def parse_admin(pages):
    states, counts = {}, {}
    for page in pages:
        if page.get("retrieval", {}).get("status") != "OK" or page.get("captchaPresent"):
            continue
        for table in page["tables"]:
            headers = table["headers"]
            if "State LGD Code" in headers:
                for row in table["rows"]:
                    if len(row) < 5 or not row[0].isdigit():
                        continue
                    key = state_key(row[2])
                    if key in states or any(s["lgdCode"] == int(row[1]) for s in states.values()):
                        raise ValueError("Duplicate administrative state identity")
                    states[key] = {"name": row[2], "lgdCode": int(row[1]), "type": row[4],
                                   "sourceUrl": page["url"], "checkedAt": page["retrieval"]["checkedAt"]}
            if "No. of Districts" in headers and "No. of Urban Bodies" in headers:
                for row in table["rows"]:
                    if len(row) != 15 or not row[0].isdigit():
                        continue
                    key = state_key(row[1])
                    if key in counts:
                        raise ValueError("Duplicate state summary")
                    values = [number(v) for v in row[2:]]
                    counts[key] = dict(zip(("districts", "districtPanchayats", "districtTraditionalBodies",
                        "subdistricts", "blocks", "intermediatePanchayats", "intermediateTraditionalBodies",
                        "villages", "villagePanchayats", "villageTraditionalBodies", "traditionalBodies",
                        "urbanBodies", "cantonmentBoards"), values))
                    counts[key].update(sourceUrl=page["url"], checkedAt=page["retrieval"]["checkedAt"],
                                       rawNotApplicable=[headers[i] for i, v in enumerate(row) if v == "N.A"])
    if len(states) != 36 or len(counts) != 36 or set(states) != set(counts):
        raise ValueError("State/code/summary tables incomplete; no complete reconciliation")
    return [{**s, "counts": counts[k]} for k, s in sorted(states.items())]


def match_unit(record, units):
    """Only a supplied stable ID resolves a district/local-body link.

    Exact names are suggestions, including renamed/ambiguous units, never matches.
    """
    kind = record.get("administrativeType")
    candidates = [u for u in units if u["type"] == kind and u["stateLgdCode"] == record.get("stateLgdCode")]
    code = record.get("administrativeLgdCode")
    if code is not None:
        matches = [u for u in candidates if u["lgdCode"] == code and u.get("active", True)]
        if len(matches) == 1:
            unit = matches[0]
            if state_key(record.get("administrativeName", "")) not in {state_key(unit["name"])} | {state_key(a) for a in unit.get("aliases", [])}:
                return {"status": "ID_NAME_CONFLICT", "suggestedIds": [code]}
            return {"status": "ID_MATCH", "lgdCode": code}
        return {"status": "ID_UNRESOLVED", "suggestedIds": []}
    matches = [u["lgdCode"] for u in candidates if state_key(u["name"]) == state_key(record.get("administrativeName", ""))]
    return {"status": "AMBIGUOUS_NAME" if len(matches) > 1 else "NAME_REQUIRES_CONFIRMATION" if matches else "UNMATCHED",
            "suggestedIds": matches}


def coverage(admin, manifest, rows, catalog, queue, taxonomy, matrix):
    from discovery_report import state_coverage
    source_states = {state_key(s["state"]): s for s in state_coverage(manifest, rows, catalog, matrix)}
    result = []
    for unit in admin:
        key = state_key(unit["name"])
        if key not in source_states:
            raise ValueError("Official state has no matching source branch")
        src = source_states[key]
        attributed = [r for r in rows if any(state_key(s) == key for s in r.get("sourceStates", [])) or state_key(r.get("state") or "") == key]
        bundled = [r for r in catalog["websites"] if state_key(r.get("state") or "") == key]
        pending = [i for i in queue["records"] if state_key(i["record"].get("state") or "") == key and i["status"] not in {"RESOLVED_DUPLICATE", "REJECTED"}]
        district = [r for r in attributed if r.get("governmentLevel") == "district"]
        local = [r for r in attributed if r.get("governmentLevel") == "municipal_local"]
        rural = [r for r in local if any("Panchayati" in c for c in r.get("sourceCategories", []))]
        urban = [r for r in local if any("Local Government Bodies" in c for c in r.get("sourceCategories", []))]
        published_ids = {r["id"] for r in bundled}
        counts = unit["counts"]
        result.append({"state": src["state"], "lgdCode": unit["lgdCode"], "type": unit["type"],
            "administrativeCounts": counts, "sourceBranch": src,
            "organizationsDiscovered": len({o for r in attributed for o in r.get("sourceOrganizationIds", [])}),
            "uniqueUrlsAttributed": len(attributed), "bundled": len(bundled), "pending": len(pending),
            "checkedDestinations": sum(r.get("healthStatus") not in {None, "NOT_CHECKED"} for r in attributed),
            "manuallyReviewedBundled": sum(r.get("verificationStatus") == "verified" for r in bundled),
            "districtUrls": len(district), "districtUrlsBundled": sum(r["id"] in published_ids for r in district),
            "urbanCategoryUrls": len(urban), "urbanCategoryUrlsBundled": sum(r["id"] in published_ids for r in urban),
            "ruralCategoryUrls": len(rural), "ruralCategoryUrlsBundled": sum(r["id"] in published_ids for r in rural),
            "lgdDistrictLinksConfirmed": 0, "lgdUrbanLinksConfirmed": 0, "lgdRuralLinksConfirmed": 0,
            "districtIdsUnlinked": counts["districts"], "urbanIdsUnlinked": counts["urbanBodies"],
            "ruralIdsUnlinked": sum(counts.get(f) or 0 for f in ("districtPanchayats", "intermediatePanchayats", "villagePanchayats")),
            "missingBundledSectors": sorted(set(taxonomy["sectors"]) - {r.get("primarySector") for r in bundled}),
            "statePortalUrls": [r["url"] for r in attributed if any(c in {"State Portal", "UT Portal", "Union Territory Portal"} for c in r.get("sourceCategories", []))],
            "administrativeStatus": "STATE_IDS_AND_AGGREGATES_COMPLETE; DISTRICT_LOCAL_IDS_CAPTCHA_BLOCKED",
            "websiteCoverageStatus": "PARTIAL; NO UNIT_LEVEL_COMPLETENESS_CLAIM"})
    return result


def run():
    read = lambda p: json.loads(p.read_text(encoding="utf-8"))
    pages = read(BATCH / "administrative_pages.json")["pages"]
    admin = parse_admin(pages)
    catalog = read(ROOT / "app/src/main/assets/catalog.json")
    queue = read(ROOT / "data/review_queue.json")
    rows = read(SOURCE / "normalized_candidates.json")
    manifest = read(SOURCE / "manifest.json")
    from discovery_report import category_matrix
    matrix = category_matrix(manifest, rows, {r["id"] for r in catalog["websites"]})
    states = coverage(admin, manifest, rows, catalog, queue, read(ROOT / "data/taxonomy.json"), matrix)
    totals = {f: sum(s["counts"].get(f) or 0 for s in admin) for f in admin[0]["counts"] if f not in {"sourceUrl", "checkedAt", "rawNotApplicable"}}
    save_json(BATCH / "administrative_units.json", {"stateUnits": admin, "districtUnits": [], "localBodyUnits": [],
        "limitations": "Public state codes and aggregate counts only. Individual district/local-body codes require CAPTCHA-protected reports; no bypass."})
    output = {"generatedAt": now(), "states": states, "totals": totals, "stateTypes": dict(Counter(s["type"] for s in admin)),
        "blockedPages": [p["url"] for p in pages if p.get("captchaPresent")], "confirmedDistrictOrLocalBodyWebsiteLinks": 0}
    save_json(BATCH / "administrative_coverage.json", output)
    lines = ["# Administrative coverage reconciliation - Phase 2B", "", f"Generated {output['generatedAt']}. Regenerate: `python tools/administrative_coverage.py`.", "",
        "Live public LGD state-code and state-wise administrative summary tables establish 28 states, eight UTs and their stable state IDs. Unit-level district and local-body reports require CAPTCHA; no submission or bypass was attempted. IGOD codes remain separate from LGD codes.", "",
        f"Authoritative aggregate totals: {totals['districts']} districts; {totals['urbanBodies']} urban bodies; {totals['districtPanchayats']} district, {totals['intermediatePanchayats']} intermediate and {totals['villagePanchayats']} village panchayats; {totals['traditionalBodies']} traditional bodies; {totals['cantonmentBoards']} cantonment boards.", "",
        "All district/local-body IDs remain unlinked to JanSetu: zero confirmed stable-ID website links. This quantifies the reconciliation gap, not the number of units without websites. Website and source-category counts are not administrative-unit counts. N.A is retained explicitly in evidence; it is not treated as a failed fetch. No URLs or lower-level IDs were invented.", "",
        "## State/UT administrative and website evidence", "",
        "D = districts; U = urban bodies; R = district + intermediate + village panchayats. Website columns use actual source jurisdiction; multiple-state source memberships are attributed separately and must not be summed as national totals.", "",
        "| State/UT | LGD | D | U | R | Source listings complete | Org identities | URLs | Checked | Bundled | Pending | District URLs bundled/discovered | Urban-category URLs bundled/discovered | Rural-category URLs bundled/discovered |",
        "| --- | ---: | ---: | ---: | ---: | --- | ---: | ---: | ---: | ---: | ---: | --- | --- | --- |"]
    for s in states:
        c=s["administrativeCounts"];src=s["sourceBranch"]
        urban_count = c['urbanBodies'] if c['urbanBodies'] is not None else 'N.A'
        lines.append(f"| {s['state']} | {s['lgdCode']} | {c['districts']} | {urban_count} | {s['ruralIdsUnlinked']} | {src['completeListingCategories']}/{src['listingCategories']} | {s['organizationsDiscovered']} | {s['uniqueUrlsAttributed']} | {s['checkedDestinations']} | {s['bundled']} | {s['pending']} | {s['districtUrlsBundled']}/{s['districtUrls']} | {s['urbanCategoryUrlsBundled']}/{s['urbanCategoryUrls']} | {s['ruralCategoryUrlsBundled']}/{s['ruralCategoryUrls']} |")
    lines += ["", "Urban-category and Panchayati Raj category memberships can overlap. Neither certifies a municipal corporation or individual panchayat identity. Ownership-reviewed website counts and known state-portal URLs are retained separately in the JSON.", "", "## Sector and local coverage gaps", ""]
    for s in states:
        urban_count = s['urbanIdsUnlinked'] if s['urbanIdsUnlinked'] is not None else 'N.A (source not applicable)'
        lines.append(f"- {s['state']}: no bundled record in {', '.join(s['missingBundledSectors']) or 'none of the research sectors'}; {s['districtIdsUnlinked']} district, {urban_count} urban and {s['ruralIdsUnlinked']} rural administrative IDs unlinked. State-portal URLs discovered: {len(s['statePortalUrls'])}. Website coverage partial; source listing branch {'complete' if s['sourceBranch']['branchesComplete'] else 'partial'}.")
    lines += ["", "## Rename, split, merger and ambiguity handling", "",
        "State mapping accepts punctuation/case and the source's leading 'The' for Dadra and Nagar Haveli and Daman and Diu. LGD code 38, Ladakh 37 and Jammu and Kashmir 1 come from the live code table, not historical census IDs. Exact lower-level names are suggestions only; duplicate names, inactive IDs and ID/name conflicts remain unresolved. No old district name is silently mapped onto a new/split district. A public aggregate cannot establish those histories.", "",
        "## Source evidence and blockers", "", "[LGD state codes](https://lgdirectory.gov.in/globalviewstateforcitizen.do), [state-wise entities summary](https://lgdirectory.gov.in/reportonStatewiseEntityDetails.do). Retrieval timestamps, hashes, sanitised table rows and CAPTCHA observations: `data/collection/phase2b-2026-10-15/administrative_pages.json`.", ""]
    lines += [f"- CAPTCHA-protected: {url}" for url in output["blockedPages"]]
    lines += ["", "Next: obtain a user-exported permitted LGD district/local-body dataset, import stable IDs with source/version evidence, and reconcile specific website links. Data.gov.in was previously robots-disallowed and was not re-crawled. No percentage of full local-government website coverage is asserted."]
    (ROOT / "docs/ADMINISTRATIVE_COVERAGE_RECONCILIATION.md").write_text("\n".join(lines)+"\n", encoding="utf-8")
    print(json.dumps({"states": len(states), "totals": totals, "confirmedUnitWebsiteLinks": 0}))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--collect", action="store_true")
    args = parser.parse_args()
    if args.collect:
        collect()
    run()
