"""Evidence-based Phase 2B triage; no new network requests or ownership claims.

python tools/review_queue.py                 # report/proposal only
python tools/review_queue.py --apply         # persist statuses and safe duplicate archive
python tools/catalog_pipeline.py publish data/collection/phase2b-2026-10-15/local_approvals.json
"""
import argparse
import copy
import csv
import hashlib
import json
import re
from collections import Counter, defaultdict
from datetime import datetime, timezone
from pathlib import Path
from urllib.parse import urlsplit
from catalog_pipeline import canonical_url, validate_directory_eligibility
from government_sources import normalize_url, now, save_json

ROOT = Path(__file__).resolve().parents[1]
BATCH = ROOT / "data/collection/phase2b-2026-10-15"
SOURCE = ROOT / "data/collection/igod-2026-10-14"
RULE_VERSION = "phase2b-v1"
PROTECTED = {"REJECTED", "NEEDS_OWNERSHIP_REVIEW", "NEEDS_URL_REVIEW", "RESOLVED_DUPLICATE"}


def name_key(value):
    return " ".join(re.findall(r"\w+", (value or "").casefold()))


def safe_redirect(health, original):
    """Require an explicit uninterrupted same-host HTTPS chain, not a suffix guess."""
    try:
        current = normalize_url(original)
        host = urlsplit(current).hostname.removeprefix("www.")
        if not current.startswith("https://"):
            return False
        for hop in health.get("redirects", []):
            start, end = normalize_url(hop["from"]), normalize_url(hop["to"])
            if start != current or hop.get("httpStatus") not in {301, 302, 303, 307, 308}:
                return False
            if not end.startswith("https://") or urlsplit(end).hostname.removeprefix("www.") != host or end == current:
                return False
            current = end
        return current == normalize_url(health.get("finalUrl", ""))
    except (ValueError, KeyError, AttributeError):
        return False


def same_organization(a, b):
    common_ids = set(a.get("sourceOrganizationIds", [])) & set(b.get("sourceOrganizationIds", []))
    names_a = {name_key(n) for n in [a.get("title"), a.get("organization"), *a.get("aliases", [])] if n}
    names_b = {name_key(n) for n in [b.get("title"), b.get("organization"), *b.get("aliases", [])] if n}
    same_state = a.get("state") == b.get("state")
    return bool(common_ids) or bool(names_a & names_b) and same_state


def conflicts(row, states):
    result = []
    source_names = {name_key(e.get("originalName")) for e in row.get("sourceEvidence", []) if e.get("originalName")}
    if len(source_names) > 1 and len(set(row.get("sourceOrganizationIds", []))) > 1:
        result.append("Multiple source organization names/identities share this URL; operator relationship requires review")
    memberships = set(row.get("sourceStates", []))
    if row.get("state") and (row["state"] not in states or memberships and row["state"] not in memberships):
        result.append("State conflicts with authoritative source jurisdiction")
    if len(memberships) > 1:
        result.append("Multiple source state memberships; exact jurisdiction unresolved")
    if row.get("district") and not row.get("state"):
        result.append("District label without a single known state")
    if row.get("governmentLevel") in {"state", "union_territory", "district", "municipal_local"} and not row.get("state"):
        result.append("Regional government level has no unambiguous state jurisdiction")
    return result


def priority(row, bucket, coverage, as_of):
    impact = 30 if row.get("primarySector") in {"identity_civil_registration", "health_family_welfare", "social_security_welfare", "education_scholarships", "employment_pensions"} else 10
    authority = 15 if row.get("sourceId") == "igod" else 5
    regional = 20 if row.get("governmentLevel") in {"district", "municipal_local"} else 5 if row.get("state") else 0
    src = coverage.get(row.get("state"), {})
    gap = 10 if row.get("state") and src.get("bundledDistrictNames", 0) == 0 else 0
    risk = 15 if bucket in {"LIKELY_DUPLICATE", "OWNERSHIP_OPERATOR_REVIEW", "URL_REDIRECT_REVIEW"} else 0
    ready = 30 if bucket == "READY_LOCAL_PUBLICATION" else 0
    try:
        checked = datetime.fromisoformat(row.get("healthCheckedAt") or row.get("lastSourceChecked") or "").replace(tzinfo=timezone.utc)
        age = max(0, (as_of - checked).days)
    except ValueError:
        age = None
    age_score = min(age or 0, 30)
    return {"score": impact+authority+regional+gap+risk+ready+age_score,
            "citizenImpact": impact, "sourceAuthority": authority, "regionalCoverageGap": regional+gap,
            "risk": risk, "discoverability": ready, "ageDays": age, "ageScore": age_score}


def decide(item, catalog_by_url, catalog_by_final, queue_by_url, duplicate_ids, states):
    row = item["record"]
    health = row.get("destinationHealth", {})
    issues = conflicts(row, states)
    decision = {"ruleVersion": RULE_VERSION, "bucket": "INCONCLUSIVE", "confidence": "low",
        "reason": "No conclusive destination evidence; retain for review", "unresolvedConflicts": issues,
        "sourceAuthority": row.get("sourceAuthority", "LISTED_BY_OFFICIAL_DIRECTORY" if row.get("sourceId") == "igod" else "EDITOR_SUPPLIED_SOURCE"),
        "destinationHealth": health.get("status", row.get("healthStatus", "NOT_CHECKED")),
        "ownershipConfidence": row.get("ownershipConfidence", "unreviewed"),
        "publicationEligibility": "NOT_ELIGIBLE", "lastCheckAt": health.get("checkedAt", row.get("healthCheckedAt")),
        "originalSources": [{k:e.get(k) for k in ("originalName", "originalUrl", "sourcePage", "listingPage", "detailUrl", "retrievedAt", "snapshot")} for e in row.get("sourceEvidence", [])],
        "evidenceUsed": {"sourceUrl": row.get("sourceUrl"), "normalizedDestination": row.get("url"),
            "observedFinalUrl": health.get("finalUrl"), "redirects": health.get("redirects", []),
            "robotsChecks": health.get("robotsChecks", [])}, "safeResolution": False}
    if item["status"] == "REJECTED":
        return {**decision, "bucket": "REJECTED", "reason": item.get("reason", "Recorded editor rejection"), "confidence": "recorded_decision"}
    try:
        url = normalize_url(row["url"])
        if not url.startswith("https://"):
            raise ValueError("Source lists unsupported HTTP; HTTPS not inferred")
    except (ValueError, KeyError) as error:
        return {**decision, "bucket": "URL_REDIRECT_REVIEW", "reason": str(error)}
    targets = catalog_by_url.get(url, [])
    if len(targets) == 1 and same_organization(row, targets[0]) and not issues and item["status"] not in PROTECTED:
        return {**decision, "bucket": "SAFE_DETERMINISTIC_RESOLUTION", "reason": "Exact normalized URL and organization evidence match a bundled record", "confidence": "high", "safeResolution": True, "duplicateOf": targets[0]["id"]}
    if targets or len(queue_by_url.get(url, [])) > 1:
        return {**decision, "bucket": "LIKELY_DUPLICATE", "reason": "Repeated exact URL with unresolved identity/decision conflict; no automatic merge"}
    if health.get("status") == "RESPONDING" and safe_redirect(health, url) and not re.search(r"login|signin|authenticate|/auth", health.get("finalUrl", ""), re.I):
        target = catalog_by_final.get(normalize_url(health["finalUrl"]), [])
        if len(target) == 1 and same_organization(row, target[0]) and not issues and item.get("collectionManaged"):
            return {**decision, "bucket": "SAFE_DETERMINISTIC_RESOLUTION", "reason": "Explicit safe redirect chain to one bundled destination with matching organization evidence; source alias retained", "confidence": "high", "safeResolution": True, "duplicateOf": target[0]["id"]}
    if row["id"] in duplicate_ids:
        issues.append("Same normalized organization name/jurisdiction occurs at a different URL; confirmation needed")
        return {**decision, "bucket": "LIKELY_DUPLICATE", "reason": issues[-1]}
    if issues or item["status"] == "NEEDS_OWNERSHIP_REVIEW":
        return {**decision, "bucket": "OWNERSHIP_OPERATOR_REVIEW", "reason": "; ".join(issues) if issues else item.get("reason", "Operator evidence unresolved")}
    if item["status"] == "NEEDS_URL_REVIEW":
        return {**decision, "bucket": "URL_REDIRECT_REVIEW", "reason": item.get("reason", "Recorded URL review decision preserved")}
    if not item.get("collectionManaged"):
        return {**decision, "reason": "Existing editorial decision preserved; no automatic replacement by directory evidence"}
    if health.get("status") in {"REDIRECT_REVIEW", "URL_REVIEW", "DEFAULT_SERVER_PAGE", "CONTENT_REVIEW"}:
        return {**decision, "bucket": "URL_REDIRECT_REVIEW", "reason": health.get("reason", "Canonical destination or response requires review")}
    if health.get("status") != "RESPONDING":
        return {**decision, "reason": "Unavailability/access/robots evidence is inconclusive, not fraud or retirement: " + health.get("reason", health.get("status", "not checked"))}
    if not safe_redirect(health, url):
        return {**decision, "bucket": "URL_REDIRECT_REVIEW", "reason": "Final destination is not supported by a complete safe HTTPS redirect chain"}
    if item["status"] in PROTECTED:
        return {**decision, "bucket": "OWNERSHIP_OPERATOR_REVIEW", "reason": "Recorded review decision requires an editor"}
    proposed = {**row, "reviewStatus": "DISCOVERED", "reviewConflicts": issues}
    try:
        validate_directory_eligibility(proposed)
    except (ValueError, TypeError, KeyError) as error:
        return {**decision, "bucket": "OWNERSHIP_OPERATOR_REVIEW" if "eligible" in str(error) else "URL_REDIRECT_REVIEW", "reason": str(error)}
    return {**decision, "bucket": "READY_LOCAL_PUBLICATION", "confidence": "high_for_directory_linkage_only",
        "reason": "Retained listing content/hash, secure safe response and local policy gates passed; ownership not manually reviewed",
        "publicationEligibility": "LOCAL_DIRECTORY_ONLY", "ownershipConfidence": "directory_linkage_only"}


def triage(queue, catalog, duplicate_groups, states, coverage, as_of):
    result = copy.deepcopy(queue)
    by_url, by_final, queued = defaultdict(list), defaultdict(list), defaultdict(list)
    for row in catalog["websites"]:
        by_url[normalize_url(row["url"])].append(row)
        try:
            final = normalize_url(row.get("observedFinalUrl") or row["url"])
            by_final[final].append(row)
        except ValueError:
            pass
    for item in result["records"]:
        try:
            queued[normalize_url(item["record"]["url"])].append(item)
        except ValueError:
            pass
    duplicate_ids = {i for group in duplicate_groups for i in group["websiteIds"]}
    approvals, resolved, decisions = {}, [], []
    for item in result["records"]:
        row = item["record"]
        decision = decide(item, by_url, by_final, queued, duplicate_ids, states)
        decision["priority"] = priority(row, decision["bucket"], coverage, as_of)
        payload = {"decision": decision, "url": row.get("url")}
        signature = hashlib.sha256(json.dumps(payload, sort_keys=True, ensure_ascii=False).encode()).hexdigest()
        # Timestamp/history updates only when the evidence-derived decision changes.
        if item.get("triage", {}).get("fingerprint") != signature:
            decision.update(fingerprint=signature, evaluatedAt=now())
            item["triage"] = decision
            item.setdefault("triageHistory", []).append(decision)
        else:
            decision = item["triage"]
        if decision["safeResolution"]:
            item["priorReviewStatus"] = item["status"]
            item["status"] = "RESOLVED_DUPLICATE"
            item["reason"] = decision["reason"]
            item.setdefault("reviewHistory", []).append({"at": now(), "status": item["status"], "reason": item["reason"], "ruleVersion": RULE_VERSION})
            resolved.append(item)
        elif item["status"] not in PROTECTED and item.get("collectionManaged"):
            desired = {"URL_REDIRECT_REVIEW": "NEEDS_URL_REVIEW", "LIKELY_DUPLICATE": "NEEDS_URL_REVIEW", "OWNERSHIP_OPERATOR_REVIEW": "NEEDS_OWNERSHIP_REVIEW", "INCONCLUSIVE": "TEMPORARILY_UNREACHABLE", "READY_LOCAL_PUBLICATION": "DISCOVERED"}[decision["bucket"]]
            if desired != item["status"]:
                item["status"], item["reason"] = desired, decision["reason"]
                item.setdefault("reviewHistory", []).append({"at": now(), "status": desired, "reason": item["reason"], "ruleVersion": RULE_VERSION})
        if decision["bucket"] == "READY_LOCAL_PUBLICATION":
            approvals[row["id"]] = {"approvedBy": "JanSetu local directory evidence policy v1 (automated Phase 2B)",
                "ownershipEvidence": row["sourceUrl"], "sourceRetrievedOn": row["sourceRetrievedOn"], "evidencePolicy": "directory_listed_local_v1"}
        # Separate fields, leaving all original evidence and editor decisions intact.
        if item.get("collectionManaged"):
            row["reviewStatus"] = item["status"]
            row["reviewConflicts"] = decision["unresolvedConflicts"]
        decisions.append({"id": row["id"], "title": row["title"], "url": row["url"], "state": row.get("state"), **decision})
    resolved_ids = {i["record"]["id"] for i in resolved}
    result["records"] = [i for i in result["records"] if i["record"]["id"] not in resolved_ids]
    return result, approvals, resolved, decisions


def source_changes(previous, current):
    """Record disappearances/URL changes; this function cannot delete or redirect a site."""
    old = {normalize_url(r["url"]): r for r in previous}
    new = {normalize_url(r["url"]): r for r in current}
    removed = [{"id": old[u]["id"], "url": u, "sourceOrganizationIds": old[u].get("sourceOrganizationIds", []),
                "action": "SOURCE_NOT_SEEN_REVIEW_ONLY; RETAIN_EXISTING_RECORD"} for u in sorted(old.keys()-new.keys())]
    added = [{"id": new[u]["id"], "url": u} for u in sorted(new.keys()-old.keys())]
    org_old, org_new = defaultdict(set), defaultdict(set)
    for mapping, rows in ((org_old, previous), (org_new, current)):
        for r in rows:
            for org in r.get("sourceOrganizationIds", []):
                mapping[org].add(normalize_url(r["url"]))
    changed = [{"organizationId": org, "beforeUrls": sorted(org_old[org]), "afterUrls": sorted(org_new[org]),
                "action": "CHANGED_URL_SET_REQUIRES_REVIEW; NO_AUTOMATIC_REPLACEMENT"} for org in sorted(org_old.keys()&org_new.keys()) if org_old[org] != org_new[org]]
    names = [{"url": u, "beforeName": old[u].get("title"), "afterName": new[u].get("title"),
              "action": "SOURCE_NAME_CHANGED; IDENTITY_HISTORY_REQUIRES_REVIEW"} for u in sorted(old.keys()&new.keys()) if old[u].get("title") != new[u].get("title")]
    return {"added": added, "notSeen": removed, "changedOrganizationUrlSets": changed, "changedNames": names}


def run(apply=False):
    BATCH.mkdir(exist_ok=True)
    read = lambda p: json.loads(p.read_text(encoding="utf-8"))
    queue = read(ROOT / "data/review_queue.json")
    catalog = read(ROOT / "app/src/main/assets/catalog.json")
    for name, value in (("baseline_queue.json", queue), ("baseline_catalog.json", catalog)):
        if not (BATCH / name).exists():
            save_json(BATCH / name, value)
    baseline = read(BATCH / "baseline_queue.json")
    normalized = read(SOURCE / "normalized_candidates.json")
    source_before = BATCH / "source_baseline.json"
    if not source_before.exists():
        save_json(source_before, normalized)
    save_json(BATCH / "source_changes.json", source_changes(read(source_before), normalized))
    taxonomy = read(ROOT / "data/taxonomy.json")
    coverage = {r["state"]: r for r in read(SOURCE / "source_coverage.json")["states"]}
    as_of = datetime.now(timezone.utc)
    proposed, approvals, resolved, decisions = triage(queue, catalog, read(SOURCE / "duplicate_review.json"),
        set(taxonomy["states"]+taxonomy["unionTerritories"]), coverage, as_of)
    archive_path = BATCH / "resolved_review.json"
    archive = read(archive_path) if archive_path.exists() else []
    archived_ids = {i["record"]["id"] for i in archive}
    if apply:
        archive += [i for i in resolved if i["record"]["id"] not in archived_ids]
        proposed["resolvedAliases"] = [{"url": i["record"]["url"], "targetId": i["triage"]["duplicateOf"],
            "observedFinalUrl": i["record"].get("destinationHealth", {}).get("finalUrl"),
            "sourceOrganizationIds": i["record"].get("sourceOrganizationIds", []), "title": i["record"]["title"]} for i in archive]
        save_json(ROOT / "data/review_queue.json", proposed)
        save_json(archive_path, archive)
    save_json(BATCH / "local_approvals.json", approvals)
    save_json(BATCH / "triage_decisions.json", sorted(decisions, key=lambda d: (-d["priority"]["score"], d["id"])))
    cols = ("id", "title", "url", "state", "bucket", "reason", "confidence", "sourceAuthority", "destinationHealth", "ownershipConfidence", "publicationEligibility", "lastCheckAt", "priority", "unresolvedConflicts")
    with (BATCH / "prioritized_review.csv").open("w", encoding="utf-8-sig", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=cols)
        writer.writeheader()
        for d in sorted(decisions, key=lambda d: (-d["priority"]["score"], d["id"])):
            writer.writerow({k: json.dumps(d[k],ensure_ascii=False) if isinstance(d.get(k), (dict,list)) else d.get(k) for k in cols})
    old_catalog = read(BATCH / "baseline_catalog.json")
    originals = {r["id"]: r for r in old_catalog["websites"]}
    current = {r["id"]: r for r in catalog["websites"]}
    diff = {"baselineCount": len(originals), "currentCount": len(current), "addedIds": sorted(current.keys()-originals.keys()),
            "removedIds": sorted(originals.keys()-current.keys()), "changedBaselineIds": sorted(i for i in originals.keys()&current.keys() if originals[i]!=current[i])}
    save_json(BATCH / "catalog_diff.json", diff)
    baseline_ids = {i["record"]["id"] for i in baseline["records"]}
    published = baseline_ids & current.keys()
    publication_path = BATCH / "published_review.json"
    publication_history = read(publication_path) if publication_path.exists() else []
    recorded = {i["originalCandidate"]["record"]["id"] for i in publication_history}
    baseline_catalog = read(BATCH / "baseline_catalog.json")
    baseline_items = {i["record"]["id"]: i for i in baseline["records"]}
    for identifier in sorted(published - recorded):
        original = baseline_items[identifier]
        # Preserve the entire candidate and rerun its evidence rules against the
        # baseline, explicitly dating this re-evaluation after admission.
        _, _, _, evidence = triage({"records": [original]}, baseline_catalog,
            read(SOURCE / "duplicate_review.json"), set(taxonomy["states"]+taxonomy["unionTerritories"]), coverage, as_of)
        publication_history.append({"originalCandidate": original, "admittedRecord": current[identifier],
            "evidenceReevaluatedAt": now(), "reevaluatedDecision": evidence[0],
            "admissionHistory": current[identifier].get("reviewHistory", []),
            "note": "Original candidate and recorded admission preserved; this evidence re-evaluation is not a fresh network check."})
    save_json(publication_path, publication_history)
    buckets = Counter(d["bucket"] for d in decisions)
    summary = {"generatedAt": now(), "ruleVersion": RULE_VERSION, "applied": apply, "baselineCandidates": len(baseline_ids),
        "candidatesExaminedThisRun": len(decisions), "baselineCandidatesAccountedFor": len((baseline_ids & {i["record"]["id"] for i in proposed["records"]}) | published | {i["record"]["id"] for i in archive+resolved}),
        "automaticallyResolved": len(archive) + len(published), "duplicatesConfirmed": len(archive), "ownershipCasesResolved": 0,
        "eligibleLocalThisRun": len(approvals), "actuallyPublished": len(published),
        "pending": sum(i["status"] != "REJECTED" for i in proposed["records"]), "rejected": sum(i["status"] == "REJECTED" for i in proposed["records"]),
        "buckets": dict(buckets), "catalogDiff": diff, "newNetworkRequests": 0,
        "note": "Processed is not manually verified. Automatic resolutions are local directory admissions or evidence-backed duplicate aliases; ownership remains unreviewed."}
    save_json(BATCH / "review_summary.json", summary)
    lines = ["# Review queue report - Phase 2B", "", f"Generated {summary['generatedAt']}; rules `{RULE_VERSION}`. Run `python tools/review_queue.py --apply`, then the existing publication command with the generated local approvals, then rerun triage for final counts.", "",
        "The entire original queue is retained in the Phase 2B baseline. Evidence is reused from Phase 2A: no new destination requests, no response date reset, no ownership approval inferred from a domain or HTTP response. Exact source URL/name membership and cached source hashes are checked for publication. Fragment anchors identify the same HTTP response; only anchors are ignored for source retrieval equality.", "",
        "| Measure | Actual |", "| --- | ---: |",
        *[f"| {label} | {summary[key]} |" for label,key in (("Original candidates examined across the phase","baselineCandidates"),("Candidates examined this invocation","candidatesExaminedThisRun"),("Original candidates accounted for","baselineCandidatesAccountedFor"),("Automatically resolved across phase","automaticallyResolved"),("Duplicate aliases confirmed and archived","duplicatesConfirmed"),("Ownership cases resolved","ownershipCasesResolved"),("Eligible for local publication now","eligibleLocalThisRun"),("Records actually newly bundled","actuallyPublished"),("Still pending","pending"),("Rejected with evidence","rejected"))], "",
        "## Current triage buckets", "", *[f"- {k}: {v}" for k,v in sorted(buckets.items())], "",
        "## Deterministic rules and decisions", "",
        "- Exact URLs match without assuming www/bare equivalence. A redirect alias requires the recorded same-host HTTPS chain, matching organization evidence, one unambiguous published target and no authentication convergence. Alias records/evidence/history are archived; no organization is deleted or merged.",
        "- Similar names and the 29 existing suspected groups are suggestions only. Shared source organization names/IDs and multiple jurisdictions are recorded as conflicts, not silently flattened.",
        "- Explicit ownership, URL and rejection decisions survive. Transient HTTP/TLS/access/robots problems stay inconclusive. Neither a 404 alone nor a default server page proves retirement or fraud. Zero automatic rejections and ownership certifications are introduced.",
        "- READY_LOCAL_PUBLICATION passes the existing narrow institutional HTTPS, robots, source-content/hash and response gates. It remains Listed by IGOD and local development only. Source authority, health, ownership and publication eligibility are separate fields.",
        "- Prioritisation exposes citizen impact, source authority, regional gaps, duplicate/redirect risk, safe discoverability and age. It is an editorial order, not a trust score.", "",
        "## Reproducible artifacts", "",
        "`data/collection/phase2b-2026-10-15/`: baseline queue/catalog, `triage_decisions.json` (current pending evidence/reasons/conflicts), `prioritized_review.csv`, `resolved_review.json`, `published_review.json` (original candidates, admission history and explicitly dated post-admission evidence re-evaluation), `local_approvals.json`, `review_summary.json`, `catalog_diff.json`, and `source_changes.json`. Each retained candidate has triage history; reruns with unchanged evidence do not duplicate decisions.", "",
        "Source additions, changed organization URL sets and disappeared listings are recorded separately. They never cause silent deletion or URL replacement. No fresh source acquisition was performed in Phase 2B, so no present-day disappearance is inferred from cached data.", "",
        f"Baseline website preservation: {len(diff['removedIds'])} removed and {len(diff['changedBaselineIds'])} changed. New directory entries: {len(diff['addedIds'])}. All nine manually curated records must remain byte-equivalent as JSON objects.", "",
        "## Remaining work", "", "Resolve explicit operator/canonical URL questions using official evidence, obtain permitted LGD unit exports, and review names/renames before linking administrative IDs. NIC permission and a public evidence policy remain required before distribution. No extension allowlist or UI redesign is introduced."]
    (ROOT / "docs/REVIEW_QUEUE_REPORT.md").write_text("\n".join(lines)+"\n", encoding="utf-8")
    print(json.dumps(summary,ensure_ascii=True))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apply", action="store_true")
    run(parser.parse_args().apply)
