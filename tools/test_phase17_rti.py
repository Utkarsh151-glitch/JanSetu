"""Phase 17: the RTI guidance correction is evidence-backed, went through the official workflow, and changed
nothing else in the service catalogue."""
import json
import unittest
from pathlib import Path

from phase17_rti_candidate import GUIDELINES, RULES_PDF, verified_sources
from service_catalog import candidate_digest, official_url

ROOT = Path(__file__).resolve().parents[1]


def read(rel):
    return json.loads((ROOT / rel).read_text(encoding="utf-8"))


class RtiGuidanceEvidenceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.guidelines, cls.rules = verified_sources()
        cls.services = {s["id"]: s for s in read("data/services/catalog.json")["services"]}
        cls.rti = cls.services["rti-central-request"]

    def test_every_statement_cites_a_current_official_capture(self):
        evidence = {e["id"]: e for e in self.rti["evidence"]}
        self.assertEqual({"rti", "rti-rules-2012"}, set(evidence))
        self.assertEqual(GUIDELINES, evidence["rti"]["url"])
        self.assertEqual(RULES_PDF, evidence["rti-rules-2012"]["url"])
        self.assertEqual(self.guidelines["sha256"], evidence["rti"]["contentSha256"])
        self.assertEqual(self.rules["sha256"], evidence["rti-rules-2012"]["contentSha256"])
        for item in evidence.values():
            self.assertTrue(official_url(item["url"]))
            self.assertEqual("2026-10-18", item["checkedOn"])
            self.assertNotIn("�", item["locator"] + item["summary"])
        self.assertEqual("2026-10-18", self.rti["verifiedOn"])

    def test_the_five_rules_are_present_with_their_qualifications(self):
        bpl = self.rti["conditionalDocuments"]
        self.assertEqual(1, len(bpl))
        self.assertIn("BPL certificate", bpl[0]["text"])
        self.assertEqual(["rti", "rti-rules-2012"], bpl[0]["evidenceIds"])
        identity, wait = self.rti["procedure"]
        self.assertIn("Aadhaar card, PAN card", identity["text"])
        self.assertIn("only exception is a BPL card", identity["text"])
        self.assertIn("24–48 working hours", wait["text"])
        self.assertIn("do not pay again", wait["text"])
        fees = self.rti["fees"]["text"]
        for mode in ("internet banking", "Mastercard or Visa", "UPI", "RuPay"):
            self.assertIn(mode, fees)
        self.assertIn("₹10", fees)
        appeal = self.services["rti-first-appeal"]
        self.assertEqual("No fee for the first appeal.", appeal["fees"]["text"])
        self.assertEqual(GUIDELINES, next(e for e in appeal["evidence"] if e["id"] == "rti")["url"])
        # No document requirement is presented as satisfied, and identity documents are not listed as requirements.
        self.assertIsNone(self.rti["documents"])
        self.assertNotIn("Aadhaar", json.dumps(self.rti["conditionalDocuments"]))

    def test_scope_is_stated_and_no_human_review_is_claimed(self):
        notes = self.rti["reviewNotes"]
        self.assertIn("Not a human editorial review", notes)
        self.assertIn("central public authorities", notes)
        self.assertIn("state public authorities follow their own RTI rules", notes)

    def test_published_through_the_official_workflow_with_an_honest_approval(self):
        candidate = read("data/services/phase17-rti-candidate.json")["services"][0]
        approval = read("data/services/phase17-rti-approvals.json")["approvals"]["rti-central-request"]
        self.assertEqual(candidate_digest(candidate), approval["candidateSha256"])
        self.assertIn("not a human editorial review", approval["reviewedBy"])
        queue = read("data/services/review_queue.json")
        entries = [e for e in queue if e["id"] == "rti-central-request"]
        self.assertEqual(["PUBLISHED", "PUBLISHED"], [e["status"] for e in entries])
        self.assertEqual(approval["candidateSha256"], entries[-1]["revisionDigest"])
        self.assertEqual({**candidate, "verificationStatus": "verified", "publicationStatus": "published"}, self.rti)

    def test_nothing_else_in_the_service_catalogue_changed(self):
        original = read("data/validation_reports/phase17-originals/data__services__catalog.json")
        current = read("data/services/catalog.json")
        self.assertEqual((13, 14), (original["version"], current["version"]))
        self.assertEqual(original["organizations"], current["organizations"])
        before = {s["id"]: s for s in original["services"]}
        self.assertEqual(list(before), [s["id"] for s in current["services"]])
        self.assertEqual(["rti-central-request"], [s["id"] for s in current["services"] if s != before[s["id"]]])
        old_queue = read("data/validation_reports/phase17-originals/data__services__review_queue.json")
        new_queue = read("data/services/review_queue.json")
        self.assertEqual(old_queue, new_queue[:len(old_queue)])
        self.assertEqual(1, len(new_queue) - len(old_queue))


if __name__ == "__main__":
    unittest.main()
