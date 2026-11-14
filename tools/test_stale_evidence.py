import json
import unittest
from datetime import date
from pathlib import Path

from stale_evidence import stale

ROOT = Path(__file__).resolve().parents[1]


class StaleEvidenceTest(unittest.TestCase):
    def test_only_published_items_older_than_the_window_are_due_oldest_first(self):
        services = [{"id": "a", "publicationStatus": "published", "verifiedOn": "2026-01-01", "informationUrl": "https://x.gov.in/"},
                    {"id": "b", "publicationStatus": "published", "verifiedOn": "2026-10-10", "informationUrl": "https://y.gov.in/"},
                    {"id": "c", "publicationStatus": "draft", "verifiedOn": "2025-01-01", "informationUrl": "https://z.gov.in/"}]
        profiles = [{"websiteId": "p", "status": "published", "checkedOn": "2026-05-01"},
                    {"websiteId": "q", "status": "held", "checkedOn": "2025-01-01"}]
        due = stale(services, profiles, date(2026, 10, 18), 90)
        self.assertEqual(["a", "p"], [d["id"] for d in due])
        self.assertEqual(290, due[0]["ageDays"])

    def test_the_real_catalogue_is_currently_fresh_within_90_days(self):
        services = json.loads((ROOT / "data/services/catalog.json").read_text(encoding="utf-8"))["services"]
        profiles = json.loads((ROOT / "data/website_enrichment/claims.json").read_text(encoding="utf-8"))["websites"]
        self.assertEqual([], stale(services, profiles, date(2026, 10, 18), 90))
        # A year later, every published item is due.
        later = stale(services, profiles, date(2027, 10, 18), 90)
        self.assertEqual(52 + 25, len(later))


if __name__ == "__main__":
    unittest.main()
