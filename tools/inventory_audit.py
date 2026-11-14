"""Validate the local inventory enrichment and page-level UX evidence.

These files are editorial research data. Validation never publishes candidates to
the Android catalog or changes the extension's styling allowlist.
"""

import json
from pathlib import Path

from catalog_pipeline import CANDIDATE_STATUSES, canonical_url
from government_sources import normalize_url

ROOT = Path(__file__).resolve().parents[1]
AUDIT_STATES = {"NOT_AUDITED", "AUTOMATED_SCREENING", "MANUAL_REVIEW", "PILOT_TESTED"}
INTERVENTIONS = set("ABCDEF") | {"NEEDS_MANUAL_REVIEW"}


def read(name: str) -> dict:
    return json.loads((ROOT / name).read_text(encoding="utf-8"))


def validate(catalog: dict, queue: dict, taxonomy: dict, profiles: dict,
             audits: dict, sources: dict) -> dict:
    published = {site["id"]: site for site in catalog["websites"]}
    assert set(profiles) == set(published), "Every published site needs one profile"
    assert len(set(taxonomy["states"] + taxonomy["unionTerritories"])) == 36
    source_ids = {source["id"] for source in sources["sources"]}
    assert len(source_ids) == len(sources["sources"])
    for source in sources["sources"]:
        canonical_url(source["url"])
        assert source["maintainer"] and source["accessMethod"] and source["lastChecked"]
        assert source["automatedRetrieval"] in {"unconfirmed", "permitted", "not_used"}

    retained_ids, retained_urls = set(published), {normalize_url(site["url"]) for site in published.values()}
    for site_id, profile in profiles.items():
        assert profile["level"] in taxonomy["levels"]
        assert profile["primarySector"] in taxonomy["sectors"]
        assert set(profile["secondarySectors"]) <= set(taxonomy["sectors"])
        assert profile["purposes"] and set(profile["purposes"]) <= set(taxonomy["purposes"])
        assert canonical_url(profile["rootUrl"]).split("/")[2] == canonical_url(published[site_id]["url"]).split("/")[2]
        canonical_url(profile["landingUrl"])
        assert profile["citizenIntents"] and profile["confidence"] in {"high", "medium", "low"}

    pending = {}
    for item in queue["records"]:
        row = item["record"]
        assert item["status"] in CANDIDATE_STATUSES
        assert row["id"] not in retained_ids, f"Duplicate candidate ID: {row['id']}"
        retained_ids.add(row["id"])
        try:
            canonical_url(row["url"])
            key = normalize_url(row["url"])
        except ValueError:
            assert item["status"] == "NEEDS_URL_REVIEW", "Unsafe URL must remain in URL review"
            key = normalize_url(row["url"])
        assert key not in retained_urls, f"Duplicate candidate URL: {key}"
        retained_urls.add(key)
        assert row.get("sourceUrl") and row.get("sourceType")
        canonical_url(row["sourceUrl"])
        assert not row.get("sourceId") or row["sourceId"] in source_ids
        if row.get("primarySector"):
            assert row["primarySector"] in taxonomy["sectors"]
        if row.get("state"):
            assert row["state"] in taxonomy["states"] + taxonomy["unionTerritories"]
        assert item.get("reviewHistory") and item["reviewHistory"][-1]["status"] == item["status"]
        pending[row["id"]] = row

    page_ids = set()
    class_counts = {value: 0 for value in INTERVENTIONS}
    for audit in audits["pages"]:
        assert audit["pageId"] not in page_ids
        page_ids.add(audit["pageId"])
        assert audit["portalId"] in published or audit["portalId"] in pending
        canonical_url(audit["url"])
        assert audit["status"] in AUDIT_STATES
        assert audit["intervention"] in INTERVENTIONS
        assert audit["date"] and audit["reviewer"] and audit["pageType"]
        assert audit["conditions"] and audit["testStatus"] and audit["limitations"]
        assert audit["rationale"] and audit["risk"] in {"low", "medium", "high", "unknown"}
        if audit["intervention"] != "NEEDS_MANUAL_REVIEW":
            assert audit["evidence"], f"Classification lacks evidence: {audit['pageId']}"
        class_counts[audit["intervention"]] += 1
    return {"published": len(published), "candidates": len(pending),
            "pages": len(page_ids), "classes": class_counts}


def main() -> None:
    result = validate(read("app/src/main/assets/catalog.json"), read("data/review_queue.json"),
                      read("data/taxonomy.json"), read("data/portal_profiles.json"),
                      read("data/page_audits.json"), read("data/source_registry.json"))
    print(json.dumps(result, sort_keys=True))


if __name__ == "__main__":
    main()
