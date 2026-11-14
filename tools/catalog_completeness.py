"""Reproducible catalogue field-completeness report (Phase 6B).

Usage (from the project root):
  python tools/catalog_completeness.py             writes data/validation_reports/phase6b-completeness.json
  python tools/catalog_completeness.py --markdown  also prints the tables used in docs/PHASE6B_CATALOGUE_QUALITY_REPORT.md

Read-only. Counts come from the merged runtime catalogue (websites + services), the
service review queue, the IGOD review queue and data/website_enrichment/claims.json.

Terms
  populated      the record has a non-empty value for the field, from any source
  directoryOnly  populated, but only from the IGOD directory import (not checked against the website)
  verified       checked against an official source: a manually reviewed record (earlier phases)
                 or a published Phase 6B source-checked profile; never inferred
  unknown        no value at all (total - populated)
A directory description that is an IGOD category template counts as populated and directoryOnly,
never as a verified purpose.
"""
from __future__ import annotations

import hashlib
import json
import sys
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "tools"))

from editorial_review import load_reviews  # noqa: E402
from website_enrichment import CLAIMS  # noqa: E402

# Phase 6B's report (phase6b-completeness.json) is kept as a historical record; this phase writes its own.
REPORT = ROOT / "data/validation_reports/phase6c-completeness.json"
INPUTS = ["app/src/main/assets/catalog.json", "data/services/catalog.json", "data/services/review_queue.json",
          "data/review_queue.json", "data/website_enrichment/claims.json", "data/website_enrichment/reviews.json"]
TEMPLATE_PREFIX = "IGOD lists this website under"


def read_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def present(value) -> bool:
    return value not in (None, "", [], {})


def pct(count: int, total: int) -> float:
    return round(100 * count / total, 2) if total else 0.0


def row(name: str, total: int, populated: set, verified: set, note: str = "") -> dict:
    directory_only = populated - verified
    return {"field": name, "populated": len(populated), "populatedPct": pct(len(populated), total),
            "directoryOnly": len(directory_only), "directoryOnlyPct": pct(len(directory_only), total),
            "verified": len(verified), "verifiedPct": pct(len(verified), total),
            "unknown": total - len(populated), "unknownPct": pct(total - len(populated), total), "note": note}


def website_rows(sites: list[dict], claims: dict, services: list[dict]) -> list[dict]:
    total = len(sites)
    reviewed = {s["id"] for s in sites if s["verificationStatus"] == "verified"}
    profiles = {p["websiteId"]: p for p in claims["websites"] if p["status"] == "published"}

    def checked(field: str) -> set:
        return {wid for wid, p in profiles.items() if present(p.get("fields", {}).get(field))}

    every = {s["id"] for s in sites}
    linked = {svc["websiteId"] for svc in services}
    template = {s["id"] for s in sites if s.get("description", "").startswith(TEMPLATE_PREFIX)}
    org_is_title = {s["id"] for s in sites if s["verificationStatus"] == "directory_listed"
                    and s.get("organization", "").strip().lower() == s["title"].strip().lower()}
    return [
        row("Title", total, {s["id"] for s in sites if present(s.get("title"))}, reviewed,
            "Directory titles are IGOD names; only the manually reviewed records were checked."),
        row("Official URL", total, {s["id"] for s in sites if present(s.get("url"))}, reviewed | set(profiles),
            "Directory URLs had an automated response check (healthStatus), which is not an ownership review. "
            "Verified: reviewed records and websites whose official pages were loaded for a published profile."),
        row("Purpose (what the website is for)", total, {s["id"] for s in sites if present(s.get("description"))},
            reviewed | checked("purpose"),
            f"{len(template)} directory descriptions are IGOD category templates. A template never counts as a verified purpose; "
            f"{len(template & checked('purpose'))} of those records have a checked purpose from a Phase 6B profile instead."),
        row("Department or operator", total, {s["id"] for s in sites if present(s.get("organization"))},
            reviewed | checked("operator"),
            f"For {len(org_is_title)} directory records the listed organisation is the same text as the title."),
        row("Who it serves (audience)", total, checked("audience"), checked("audience"),
            "No directory field; only published profiles that quote the website."),
        row("Jurisdiction", total, {s["id"] for s in sites if present(s.get("jurisdiction")) or present(s.get("state"))} | checked("jurisdiction"),
            checked("jurisdiction"), "Directory jurisdiction is the IGOD state grouping, not a checked statement."),
        row("Languages offered", total, checked("languages"), checked("languages"),
            "No directory field; only language switches observed on saved official pages."),
        row("Topics / categories", total, {s["id"] for s in sites if present(s.get("categories"))}, reviewed,
            "Directory categories come from IGOD classification (low or medium confidence)."),
        row("Official help or contact link", total, checked("help"), checked("help"),
            "Links to official help, FAQ, contact or grievance pages observed on a saved page. Phone numbers and emails are not copied."),
        row("Official task links", total, checked("tasks"), checked("tasks"),
            "Public task pages (for example, status tracking or service lists). Sign-in, application and payment pages are excluded."),
        row("Linked JanSetu services", total, linked & every, linked & every,
            "Websites that at least one published, source-reviewed service record links to."),
        row("Check date and source", total, {s["id"] for s in sites if present(s.get("sourceRetrievedOn")) or present(s.get("verifiedOn"))},
            reviewed | set(profiles), "Directory records carry the IGOD retrieval date; verified records carry an official-source check date."),
    ]


SERVICE_FIELDS = [
    ("Purpose (description)", "description"),
    ("Applicant / beneficiary groups", "beneficiaryGroups"),
    ("Eligibility", "eligibility"),
    ("Required documents", "documents"),
    ("Conditional documents", "conditionalDocuments"),
    ("Procedure", "procedure"),
    ("Fees", "fees"),
    ("Deadline", "deadline"),
    ("Processing time", "processing"),
    ("Direct application URL", "applicationUrl"),
    ("Application route (mode)", "applicationMode"),
    ("Official information page", "informationUrl"),
    ("Responsible department", "department"),
]


def evidenced(value, evidence_ids: set) -> bool:
    entries = value if isinstance(value, list) else [value]
    return all(isinstance(e, dict) and e.get("evidenceIds") and set(e["evidenceIds"]) <= evidence_ids for e in entries)


def service_rows(services: list[dict]) -> list[dict]:
    total, out = len(services), []
    for label, key in SERVICE_FIELDS:
        populated = {s["id"] for s in services if present(s.get(key))}
        if key in {"eligibility", "documents", "conditionalDocuments", "procedure", "fees", "deadline", "processing", "beneficiaryGroups"}:
            verified = {s["id"] for s in services if present(s.get(key)) and evidenced(s[key], {e["id"] for e in s.get("evidence", [])})}
            note = "Verified = every entry cites evidence held on the service record."
            if key == "beneficiaryGroups":
                note += " Where it is empty, who may apply is either in eligibility text or not yet sourced."
        else:
            # Whole-record facts of a published service that passed source review.
            verified = {s["id"] for s in services if present(s.get(key)) and s["verificationStatus"] == "verified" and s.get("evidence")}
            note = "Verified = set on a published service record with official evidence."
        if key == "applicationUrl":
            note += " Null for information-only and assisted (centre) services, where no direct citizen URL was confirmed."
        out.append(row(label, total, populated, verified, note))
    return out


def coverage(sites: list[dict], services: list[dict], claims: dict, service_queue: list[dict], igod_queue: dict) -> dict:
    published = [p for p in claims["websites"] if p["status"] == "published"]
    held = [p for p in claims["websites"] if p["status"] != "published"]
    reviewed = {s["id"] for s in sites if s["verificationStatus"] == "verified"}
    purpose = reviewed | {p["websiteId"] for p in published if "purpose" in p.get("fields", {})}
    unique_queue = {}
    for item in service_queue:
        unique_queue[item["id"]] = item["status"]
    return {
        "directory": {
            "catalogWebsites": len(sites),
            "byVerificationStatus": dict(Counter(s["verificationStatus"] for s in sites)),
            "igodReviewQueueCandidates": len(igod_queue["records"]),
            "igodReviewQueueByStatus": dict(Counter(r["status"] for r in igod_queue["records"])),
            "note": "Directory coverage means a record exists. It does not mean the website's purpose, owner or services were checked.",
        },
        "verifiedInformation": {
            "websitesWithVerifiedPurpose": len(purpose),
            "ofWhichReviewedRecords": len(reviewed),
            "ofWhichPhase6bProfiles": len(purpose - reviewed),
            "phase6bProfilesPublished": len(published),
            "phase6bProfilesHeld": len(held),
            "phase6bReviewStatus": dict(Counter(p["reviewStatus"] for p in published)),
            "humanReviewed": sum(p["reviewStatus"] == "human_reviewed" for p in published),
            "websitesWithCheckedHelpLink": sum(bool(p.get("fields", {}).get("help")) for p in published),
            "websitesWithoutVerifiedPurpose": len(sites) - len(purpose),
            "websitesWithoutVerifiedPurposePct": pct(len(sites) - len(purpose), len(sites)),
        },
        "publishedServices": {
            "services": len(services),
            "websitesWithServices": len({s["websiteId"] for s in services}),
            "states": sorted({s["state"] for s in services if s.get("state")}),
            "centralServices": sum(1 for s in services if not s.get("state")),
            "byApplicationMode": dict(Counter(s["applicationMode"] for s in services)),
            "serviceReviewQueueEntries": len(service_queue),
            "serviceReviewQueueUniqueIds": len(unique_queue),
            "serviceReviewQueueByLatestStatus": dict(Counter(unique_queue.values())),
            "note": "Three service ids have two PUBLISHED decisions each in the queue history, so 55 PUBLISHED entries cover 52 services.",
        },
    }


PRIORITY_REASONS = {"published_service_link", "pending_service_link", "state_ut_portal", "citizen_service_portal"}


def segment_coverage(catalog: dict, claims: dict, reviews: dict, service_queue: list[dict]) -> dict:
    """Coverage for the groups a release decision depends on, not just the whole directory.

    priority websites = any of the queue's main priority reasons (published or pending service link,
    state/UT portal, citizen-service portal); the minor 10-point reasons alone do not qualify."""
    from collections import Counter as _Counter
    from editorial_review import effective
    from enrichment_queue import priority
    sites, services = catalog["websites"], catalog.get("services", [])
    profiles = {p["websiteId"]: p for p in claims["websites"]}
    published_links = _Counter(s["websiteId"] for s in services)
    pending_links = _Counter((i.get("candidate") or {}).get("websiteId") for i in service_queue if i.get("status") == "NEEDS_REVIEW")
    service_states = {s["state"] for s in services if s.get("state")}

    def facts(site: dict) -> dict:
        profile = profiles.get(site["id"])
        state = effective(profile, reviews) if profile else None
        live = bool(profile and profile["status"] == "published" and state["published"])
        fields = profile.get("fields", {}) if live else {}
        withheld = set(state["withheld"]) if state else set()
        has = lambda name: bool(fields.get(name)) and name not in withheld
        reviewed_record = site["verificationStatus"] == "verified"
        return {"purposeChecked": has("purpose") or reviewed_record, "sourceCheckedProfile": live,
                "humanReviewedProfile": bool(live and state["reviewStatus"] == "human_reviewed"),
                "reviewedRecord": reviewed_record, "operatorChecked": has("operator") or reviewed_record,
                "jurisdictionChecked": has("jurisdiction"), "helpLinkChecked": has("help"),
                "held": bool(profile and profile["status"] == "held"),
                "draft": bool(profile and profile["status"] == "proposed"),
                "notChecked": not (has("purpose") or reviewed_record)}

    per_site = {s["id"]: facts(s) for s in sites}
    priority_ids = {s["id"] for s in sites if set(priority(s, published_links, pending_links, service_states)[1]) & PRIORITY_REASONS}
    linked_ids = set(published_links) & set(per_site)

    def summarise(ids: list[str]) -> dict:
        n = len(ids)
        out = {"n": n}
        for key in ("purposeChecked", "reviewedRecord", "sourceCheckedProfile", "humanReviewedProfile", "operatorChecked",
                    "jurisdictionChecked", "helpLinkChecked", "draft", "held", "notChecked"):
            count = sum(per_site[i][key] for i in ids)
            out[key] = {"count": count, "pct": pct(count, n)}
        return out

    return {
        "definitions": {
            "priorityWebsites": "Websites with a published or pending service link, IGOD 'State Portal', or an e-District/citizen-service/pension/scholarship/grievance portal name.",
            "linkedByPublishedServices": "Websites that at least one published service record links to (websiteId).",
            "perPublishedService": "Each of the published services, counted by the information available for the website it links to.",
        },
        "allWebsites": summarise([s["id"] for s in sites]),
        "priorityWebsites": summarise(sorted(priority_ids)),
        "linkedByPublishedServices": summarise(sorted(linked_ids)),
        "perPublishedService": summarise([s["websiteId"] for s in services]),
    }


def build_report() -> dict:
    from service_catalog import merged_catalog
    catalog = merged_catalog()
    sites, services = catalog["websites"], catalog.get("services", [])
    claims = read_json(CLAIMS)
    service_queue = read_json(ROOT / "data/services/review_queue.json")
    igod_queue = read_json(ROOT / "data/review_queue.json")
    return {
        "inputs": {path: hashlib.sha256((ROOT / path).read_bytes()).hexdigest() for path in INPUTS},
        "catalogVersion": catalog.get("version"),
        "definitions": {"populated": "non-empty value from any source", "directoryOnly": "populated only from the IGOD import",
                        "verified": "checked against an official source (reviewed record or published source-checked profile); never inferred",
                        "unknown": "no value"},
        "coverage": coverage(sites, services, claims, service_queue, igod_queue),
        "segments": segment_coverage(catalog, claims, load_reviews(), service_queue),
        "websiteFields": {"total": len(sites), "rows": website_rows(sites, claims, services)},
        "serviceFields": {"total": len(services), "rows": service_rows(services)},
    }


def markdown(report: dict) -> str:
    out = ["### Coverage by segment", "",
           "| Measure | All websites | Priority websites | Linked by published services | Per published service |",
           "|---|---:|---:|---:|---:|"]
    seg = report["segments"]
    columns = ("allWebsites", "priorityWebsites", "linkedByPublishedServices", "perPublishedService")
    out.append("| n | " + " | ".join(f"{seg[c]['n']:,}" for c in columns) + " |")
    labels = {"purposeChecked": "Purpose checked (any)", "reviewedRecord": "Reviewed record (earlier phases)",
              "sourceCheckedProfile": "Source-checked profile (automated)", "humanReviewedProfile": "Profile reviewed by a person",
              "operatorChecked": "Department/operator checked", "jurisdictionChecked": "Jurisdiction checked (profile)",
              "helpLinkChecked": "Official help link checked", "draft": "Draft profile (not published)", "held": "Held profile",
              "notChecked": "Not checked yet"}
    for key, label in labels.items():
        out.append(f"| {label} | " + " | ".join(f"{seg[c][key]['count']:,} ({seg[c][key]['pct']}%)" for c in columns) + " |")
    out.append("")
    for key, title in (("websiteFields", "Website fields"), ("serviceFields", "Service fields")):
        total = report[key]["total"]
        out += [f"### {title} (n = {total:,})", "",
                "| Field | Populated | Directory only | Verified | Unknown |", "|---|---:|---:|---:|---:|"]
        for r in report[key]["rows"]:
            out.append(f"| {r['field']} | {r['populated']:,} ({r['populatedPct']}%) | {r['directoryOnly']:,} ({r['directoryOnlyPct']}%) "
                       f"| {r['verified']:,} ({r['verifiedPct']}%) | {r['unknown']:,} ({r['unknownPct']}%) |")
        out.append("")
    return "\n".join(out)


def main() -> None:
    report = build_report()
    REPORT.write_text(json.dumps(report, indent=1, ensure_ascii=False) + "\n", encoding="utf-8")
    print(json.dumps(report["coverage"], indent=1, ensure_ascii=False))
    if "--markdown" in sys.argv:
        sys.stdout.reconfigure(encoding="utf-8")
        print(markdown(report))


if __name__ == "__main__":
    main()
