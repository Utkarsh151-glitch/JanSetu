import json
import tempfile
import unittest
from pathlib import Path

from catalog_pipeline import canonical_url, import_source, publish


class PipelineTest(unittest.TestCase):
    def setUp(self):
        self.catalog = json.loads((Path(__file__).resolve().parents[1] / "app/src/main/assets/catalog.json").read_text(encoding="utf-8"))
        self.record = {
            "id": "example-government-portal", "title": "Example portal",
            "description": "A test candidate", "url": "https://www.example.gov.in/",
            "organization": "Example organization", "level": "Central",
            "categories": ["information"], "keywords": ["example"],
            "sourceUrl": "https://example.gov.in/about", "sourceType": "Official about page",
        }

    def test_canonicalization_rejects_unsafe_urls(self):
        self.assertEqual("https://example.gov.in/a", canonical_url(" HTTPS://WWW.Example.gov.in/a#frag "))
        for url in ("http://example.gov.in", "https://user:pass@example.gov.in", "javascript:alert(1)", "https://example.gov.in:8080"):
            with self.assertRaises(ValueError):
                canonical_url(url)

    def test_import_stages_duplicates_and_invalid_records(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "candidates.json"
            path.write_text(json.dumps([self.record, self.record, {**self.record, "id": "bad", "url": "http://bad.gov.in"}]))
            queue = import_source(path, self.catalog)["records"]
        self.assertEqual(3, len(queue))
        self.assertEqual("DISCOVERED", queue[0]["status"])
        self.assertEqual("NEEDS_URL_REVIEW", queue[1]["status"])
        self.assertEqual("NEEDS_URL_REVIEW", queue[2]["status"])
        self.assertIn("Duplicate", queue[1]["reason"])
        self.assertIn("HTTPS", queue[2]["reason"])

    def test_reimport_does_not_duplicate_a_retained_candidate(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "candidates.json"
            path.write_text(json.dumps([self.record]), encoding="utf-8")
            first = import_source(path, self.catalog)
            second = import_source(path, self.catalog, first)
        self.assertEqual(1, len(first["records"]))
        self.assertEqual([], second["records"])

    def test_publication_requires_explicit_evidence(self):
        queue = {"records": [{"record": self.record, "status": "review"}]}
        unchanged, remaining = publish(queue, self.catalog, {})
        self.assertEqual(len(self.catalog["websites"]), len(unchanged["websites"]))
        self.assertEqual(1, len(remaining["records"]))
        approved, remaining = publish(queue, self.catalog, {
            self.record["id"]: {"approvedBy": "reviewer", "ownershipEvidence": self.record["sourceUrl"], "verifiedOn": "2026-10-12"}
        })
        self.assertEqual(len(self.catalog["websites"]) + 1, len(approved["websites"]))
        self.assertEqual([], remaining["records"])
        self.assertEqual(self.record["sourceUrl"], approved["websites"][-1]["ownershipEvidence"])
        self.assertEqual("VERIFIED", approved["websites"][-1]["reviewHistory"][-1]["status"])


if __name__ == "__main__":
    unittest.main()
