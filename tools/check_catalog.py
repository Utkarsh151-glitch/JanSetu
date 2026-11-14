"""Static publication checks for the bundled starter catalog.

Run from the repository root: python tools/check_catalog.py
"""

import json
import argparse
from pathlib import Path
from urllib.parse import urlparse
from catalog_pipeline import canonical_url, validate_directory_eligibility
from service_catalog import merged_catalog


def secure_url(value: str) -> bool:
    try:
        parsed = urlparse(value)
        return parsed.scheme.lower() == "https" and bool(parsed.hostname) and \
            not parsed.username and not parsed.password and parsed.port in (None, 443)
    except ValueError:
        return False


def main(public_release=False) -> None:
    path = Path(__file__).resolve().parents[1] / "app/src/main/assets/catalog.json"
    catalog = merged_catalog(json.loads(path.read_text(encoding="utf-8")))
    assert catalog["version"] > 0
    categories = catalog["categories"]
    websites = catalog["websites"]
    services = catalog.get("services", [])
    guides = catalog.get("guides", [])
    category_ids = {category["id"] for category in categories}
    website_ids = {site["id"] for site in websites}
    assert len(category_ids) == len(categories), "Duplicate category ID"
    assert len(website_ids) == len(websites), "Duplicate website ID"
    assert len({service["id"] for service in services}) == len(services), "Duplicate service ID"
    for site in websites:
        assert site["title"] and site["organization"]
        assert site["verificationStatus"] in ("verified", "directory_listed")
        assert site.get("browserSupport", "external") in ("external", "in_app")
        assert not site.get("browserJavaScript", False) or site.get("browserSupport") == "in_app"
        assert site["sourceType"]
        if site["verificationStatus"] == "directory_listed":
            assert site.get("sourceRetrievedOn") and site.get("healthCheckedAt")
            assert site.get("sourceEvidence") and site.get("sourceId") == "igod"
            assert site.get("publicationScope") == "local_development"
            assert site.get("healthStatus") == "RESPONDING"
            assert site.get("browserSupport", "external") == "external"
            for evidence in site["sourceEvidence"]:
                assert evidence["sourceId"] == "igod" and urlparse(evidence["sourcePage"]).hostname == "igod.gov.in"
                assert evidence.get("retrievedAt") and secure_url(evidence["originalUrl"])
        else:
            assert site["verifiedOn"]
        assert secure_url(site["url"]) and secure_url(site["sourceUrl"])
        assert site["categories"] and set(site["categories"]) <= category_ids
    for service in services:
        assert service["websiteId"] in website_ids
        assert service["verificationStatus"] == "verified"
        assert (service.get("applicationUrl") is None or secure_url(service["applicationUrl"])) and secure_url(service["sourceUrl"])
    assert len({guide["websiteId"] for guide in guides}) == len(guides), "Duplicate guide"
    for guide in guides:
        assert guide["websiteId"] in website_ids
        assert guide["title"] and guide["steps"] and all(guide["steps"])
        assert guide["reviewedOn"] and secure_url(guide["sourceUrl"])
    domains = {urlparse(site["url"]).hostname for site in websites}
    assert len({canonical_url(site['url']) for site in websites}) == len(websites), "Duplicate normalized landing URL"
    if public_release:
        assert not any(site.get("publicationScope") == "local_development" for site in websites), \
            "Public distribution blocked: directory entries are local-development only; record NIC permission and adopt a reviewed public evidence policy first"
    print(
        f"Catalog valid: {len(websites)} websites ({sum(s['verificationStatus']=='verified' for s in websites)} source-verified, "
        f"{sum(s['verificationStatus']=='directory_listed' for s in websites)} directory listed), {len(domains)} unique domains, "
        f"{len(services)} services, {len(guides)} guides, {len(categories)} categories"
    )


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--public-release", action="store_true")
    main(parser.parse_args().public_release)
