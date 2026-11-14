"""Low-information website queue and the next prioritised enrichment batch (Phase 6B).

Usage (from the project root):
  python tools/website_enrichment.py queue      (or: python tools/enrichment_queue.py)

Writes data/website_enrichment/queue.json: one row for every catalogue website that
still lacks at least one checked field, and the next batch to research. It reads the
catalogue, the service records, the service review queue and claims.json, and never
modifies any of them. Unknown fields stay unknown here: nothing is inferred.
"""
from __future__ import annotations

import json
import re
import sys
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "tools"))

from website_enrichment import CLAIMS, FACT_FIELDS, LINK_FIELDS, official  # noqa: E402

QUEUE = ROOT / "data/website_enrichment/queue.json"
FIELDS = ["purpose", "operator", "audience", "jurisdiction", "languages", "help", "tasks"]
assert set(FIELDS) == FACT_FIELDS | LINK_FIELDS
NEXT_BATCH_SIZE = 20
PUBLICATION_REQUIREMENTS = [
    "A dated, read-only snapshot of the official HTTPS page (normal TLS validation, no sign-in, no form use).",
    "Each fact cites evidence whose exact quote appears in that saved snapshot; the validator refuses anything else.",
    "No website-level eligibility, documents, fees, deadlines, phone numbers, emails or addresses.",
    "Help and task links are official HTTPS pages observed on a saved page, and never sign-in, application or payment pages.",
    "reviewStatus is source_checked (automated, labelled as such) or human_reviewed with a named reviewer.",
    "Facts that are uncertain stay in heldFields or a held profile and are not packaged.",
]
# Priority points. They are deliberately simple so a reviewer can recompute them by hand.
PRIORITY = {
    "published_service_link": 100,  # a published JanSetu service sends people to this website
    "pending_service_link": 60,     # a service candidate awaiting review names this website
    "state_ut_portal": 40,          # IGOD category "State Portal"
    "citizen_service_portal": 30,   # e-District, citizen-services, seva/sewa, grievance, pension or scholarship portal (URL or title)
    "state_with_services": 10,      # its state/UT already has published JanSetu services
    "district_site": 10,            # IGOD category "Districts"
    "citizen_sector": 10,           # primary sector is a citizen-facing service area
}
CITIZEN_SECTORS = {"social_security_welfare", "employment_pensions", "health_family_welfare", "education_scholarships",
                   "land_housing", "transport", "local_civic", "utilities_local", "agriculture_rural"}
SERVICE_PORTAL = re.compile(r"e-?district|citizen services?|\bjan ?seva|\bseva\b|sewa\b|sevai|meeseva|grievance|pension|scholarship", re.I)
# Short codes keep the 7,000-row queue small; the legend is written into the file.
PENDING = {
    "not_in_batch_template": "Not yet in an enrichment batch. The listed description is an IGOD category template, not a checked purpose.",
    "not_in_batch": "Not yet in an enrichment batch.",
}


def read_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def description_status(site: dict, profile: dict | None) -> str:
    if profile and profile["status"] == "published" and "purpose" in profile.get("fields", {}):
        return "purpose_checked_against_official_page"
    if site["verificationStatus"] == "verified":
        return "reviewed_record_text"
    return "directory_template"


def priority(site: dict, published_links: Counter, pending_links: Counter, service_states: set[str]) -> tuple[int, list[str]]:
    reasons = []
    if published_links[site["id"]]:
        reasons.append("published_service_link")
    if pending_links[site["id"]]:
        reasons.append("pending_service_link")
    categories = site.get("sourceCategories", [])
    if "State Portal" in categories:
        reasons.append("state_ut_portal")
    if SERVICE_PORTAL.search(site["url"] + " " + site["title"]):
        reasons.append("citizen_service_portal")
    if site.get("state") and site["state"] in service_states:
        reasons.append("state_with_services")
    if "Districts" in categories:
        reasons.append("district_site")
    if site.get("primarySector") in CITIZEN_SECTORS:
        reasons.append("citizen_sector")
    return sum(PRIORITY[reason] for reason in reasons), reasons


def build_queue(catalog: dict, claims: dict, service_queue: list[dict]) -> dict:
    profiles = {profile["websiteId"]: profile for profile in claims["websites"]}
    published_links = Counter(service["websiteId"] for service in catalog.get("services", []))
    pending_links = Counter((item.get("candidate") or {}).get("websiteId") for item in service_queue
                            if item.get("status") == "NEEDS_REVIEW")
    service_states = {service["state"] for service in catalog.get("services", []) if service.get("state")}
    rows, states = [], Counter()
    for site in catalog["websites"]:
        profile = profiles.get(site["id"])
        published = bool(profile and profile["status"] == "published")
        fields = profile.get("fields", {}) if published else {}
        missing = [name for name in FIELDS if not fields.get(name)]
        state = "checked" if published else {"proposed": "draft", "held": "held", "rejected": "held"}.get(profile["status"]) if profile else "not_checked"
        states[state] += 1
        if not missing:
            continue
        score, reasons = priority(site, published_links, pending_links, service_states)
        if state == "draft":
            pending = "Draft awaiting publication decision: " + profile.get("publicationDecision", {}).get("basis", "not yet decided")
        elif state == "held":
            pending = profile["heldReason"]
        elif state == "checked":
            held = {item["field"]: item["reason"] for item in profile.get("heldFields", [])}
            pending = "; ".join(f"{name}: {held.get(name, 'not stated on the saved official pages')}" for name in missing)
        else:
            pending = "not_in_batch_template" if site["verificationStatus"] == "directory_listed" else "not_in_batch"
        rows.append({
            "websiteId": site["id"], "title": site["title"], "state": state, "missing": missing,
            "sourceToCheck": site["url"],
            "urlCheck": f"{site['healthStatus']} {site.get('healthCheckedAt', '')[:10]}".strip() if site.get("healthStatus")
                        else f"reviewed record {site.get('verifiedOn') or 'undated'}",
            "domainOfficial": official(site["url"]),
            "descriptionStatus": description_status(site, profile),
            "pending": pending, "priority": score, "why": reasons,
        })
    rows.sort(key=lambda row: (-row["priority"], row["title"].lower(), row["websiteId"]))
    candidates = [row for row in rows if row["state"] == "not_checked" and row["domainOfficial"] and row["urlCheck"].startswith("RESPONDING")]
    return {
        "generatedFrom": {"catalogVersion": catalog.get("version"), "claimsBatch": claims.get("batchId")},
        "publicationRequirements": PUBLICATION_REQUIREMENTS,
        "priorityRule": PRIORITY,
        "pendingCodes": PENDING,
        "note": "Website rows only. The 3,823 IGOD candidates in data/review_queue.json and the service candidates in data/services/review_queue.json keep their own queues and are not changed.",
        "summary": {"websites": len(catalog["websites"]), "byState": dict(states), "queued": len(rows),
                    "queuedNotChecked": sum(row["state"] == "not_checked" for row in rows),
                    "queuedHeld": sum(row["state"] == "held" for row in rows),
                    "queuedDraft": sum(row["state"] == "draft" for row in rows),
                    "queuedCheckedWithGaps": sum(row["state"] == "checked" for row in rows)},
        "nextBatch": [{"websiteId": row["websiteId"], "title": row["title"], "start": row["sourceToCheck"],
                       "priority": row["priority"], "why": row["why"]} for row in candidates[:NEXT_BATCH_SIZE]],
        "queue": rows,
    }


def write_queue() -> dict:
    from service_catalog import merged_catalog
    catalog = merged_catalog()
    result = build_queue(catalog, read_json(CLAIMS), read_json(ROOT / "data/services/review_queue.json"))
    # One row per line keeps the large queue reviewable in a diff.
    body = json.dumps({k: v for k, v in result.items() if k != "queue"}, indent=1, ensure_ascii=False)[:-2]
    lines = ",\n".join("  " + json.dumps(row, ensure_ascii=False, sort_keys=True) for row in result["queue"])
    QUEUE.write_text(body + ',\n "queue": [\n' + lines + "\n ]\n}\n", encoding="utf-8")
    return result["summary"] | {"nextBatch": len(result["nextBatch"])}


if __name__ == "__main__":
    print(json.dumps(write_queue(), sort_keys=True))
