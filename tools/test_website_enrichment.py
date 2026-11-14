"""Phase 6B: evidence-backed website enrichment, the low-information queue and the completeness report."""
import copy
import hashlib
import json
import unittest
from pathlib import Path

from catalog_completeness import TEMPLATE_PREFIX, build_report
from enrichment_queue import build_queue
from service_catalog import merged_catalog
from website_enrichment import (CLAIMS, ClaimsError, follow_links, load_snapshots, official, runtime_profiles,
                                sensitive, validate)

ROOT = Path(__file__).resolve().parents[1]
PROTECTED = ["app/src/main/assets/catalog.json", "data/review_queue.json", "data/services/catalog.json",
             "data/services/review_queue.json", "app/src/main/assets/catalog-update-public.der"]


def sha(path):
    return hashlib.sha256((ROOT / path).read_bytes()).hexdigest()


SNAPSHOT_URL = "https://portal.example.gov.in/about"
SNAPSHOTS = {SNAPSHOT_URL: [{"url": SNAPSHOT_URL, "title": "About the Portal", "metaDescription": None,
                             "text": "The portal offers  certificates\nto citizens of the State.",
                             "links": [{"text": "Contact Us", "href": "https://portal.example.gov.in/contact"},
                                       {"text": "Login", "href": "https://portal.example.gov.in/login"}]}]}
CATALOG = {"websites": [{"id": "w1", "title": "Example portal", "url": "https://portal.example.gov.in/", "verificationStatus": "directory_listed"}]}


def profile(**changes):
    base = {
        "websiteId": "w1", "status": "published", "reviewStatus": "source_checked",
        "reviewer": "Claude Code (automated agent); human review pending", "checkedOn": "2026-10-17",
        "evidence": [{"id": "e1", "url": SNAPSHOT_URL, "checkedOn": "2026-10-17", "claim": "Offers certificates.",
                      "quote": "The portal offers certificates to citizens"},
                     {"id": "e2", "url": SNAPSHOT_URL, "checkedOn": "2026-10-17", "claim": "Links to contact.", "quote": "Contact Us"}],
        "fields": {"purpose": {"value": "Certificates for citizens.", "evidenceIds": ["e1"]},
                   "help": [{"label": "Contact us", "url": "https://portal.example.gov.in/contact", "kind": "contact", "evidenceIds": ["e2"]}]},
        "heldFields": [{"field": "audience", "reason": "Not stated."}], "uncertainty": [],
    }
    base.update(changes)
    return {"websites": [base]}


class ValidatorTest(unittest.TestCase):
    def errors(self, claims):
        with self.assertRaises(ClaimsError) as raised:
            validate(claims, CATALOG, [], SNAPSHOTS)
        return " | ".join(raised.exception.errors)

    def test_valid_profile_passes_with_whitespace_normalised_quote(self):
        self.assertEqual({"profiles": 1, "published": 1, "fieldsPublished": {"purpose": 1, "help": 1}},
                         validate(profile(), CATALOG, [], SNAPSHOTS))

    def test_quote_must_appear_verbatim_in_saved_snapshot(self):
        claims = profile()
        claims["websites"][0]["evidence"][0]["quote"] = "The portal offers pensions to citizens"
        self.assertIn("Quote not found verbatim", self.errors(claims))
        claims["websites"][0]["evidence"][0]["url"] = "https://portal.example.gov.in/unsaved"
        self.assertIn("No saved snapshot", self.errors(claims))

    def test_machine_check_is_never_labelled_human_review(self):
        # Phase 6C: human review exists only as named, dated decisions in reviews.json, never in claims.json.
        self.assertIn("recorded only through reviews.json", self.errors(profile(reviewStatus="human_reviewed")))
        self.assertIn("recorded only through reviews.json", self.errors(profile(humanReviewer="A Person")))
        self.assertIn("must not be labelled as human review", self.errors(profile(reviewer="Human reviewed")))

    def test_service_checklists_and_contact_details_are_refused_at_website_level(self):
        for field in ("fees", "eligibility", "documents", "deadline", "phone", "email"):
            claims = profile()
            claims["websites"][0]["fields"][field] = {"value": "x", "evidenceIds": ["e1"]}
            self.assertIn("checklist or contact detail refused", self.errors(claims), field)

    def test_links_must_be_official_observed_and_never_sign_in(self):
        for url, expected in (("https://example.com/contact", "official HTTPS"), ("http://portal.example.gov.in/contact", "official HTTPS"),
                              ("https://portal.example.gov.in/login", "Sign-in"), ("https://portal.example.gov.in/elsewhere", "not observed")):
            claims = profile()
            claims["websites"][0]["fields"]["help"][0]["url"] = url
            self.assertIn(expected, self.errors(claims), url)
        claims = profile()
        claims["websites"][0]["fields"]["help"][0]["kind"] = "task"
        self.assertIn("not allowed", self.errors(claims))

    def test_uncertain_cases_stay_held_and_out_of_the_app(self):
        held = profile(status="held", reviewStatus="not_reviewed")
        self.assertIn("needs a reason", self.errors(held))
        self.assertIn("keep facts out of the app", self.errors(held))
        self.assertIn("checked purpose", self.errors(profile(fields={})))
        ok = profile(status="held", reviewStatus="not_reviewed", heldReason="HTTP 403; not retried.", fields={}, evidence=[])
        self.assertEqual(0, validate(ok, CATALOG, [], SNAPSHOTS)["published"])
        self.assertEqual([], runtime_profiles(ok))

    def test_runtime_projection_drops_quotes_and_marks_not_stated_fields(self):
        row = runtime_profiles(profile())[0]
        self.assertNotIn("quote", json.dumps(row))
        self.assertEqual(["audience"], row["notStated"])
        self.assertEqual("Certificates for citizens.", row["purpose"]["value"])
        self.assertNotIn("audience", row)

    def test_unknown_website_and_duplicates_are_refused(self):
        self.assertIn("Unknown website", self.errors(profile(websiteId="nope")))
        claims = profile()
        claims["websites"].append(copy.deepcopy(claims["websites"][0]))
        self.assertIn("Duplicate profile", self.errors(claims))


class CaptureRulesTest(unittest.TestCase):
    def test_official_hosts_and_sensitive_paths(self):
        self.assertTrue(official("https://udyamregistration.gov.in/"))
        self.assertFalse(sensitive("https://udyamregistration.gov.in/"))  # host words do not count
        self.assertTrue(sensitive("https://x.gov.in/directApply.do?serviceId=1"))
        self.assertTrue(sensitive("https://x.gov.in/", "Login & Apply"))
        for url in ("http://x.gov.in/", "https://x.gov.in:8443/", "https://user@x.gov.in/", "https://x.com/"):
            self.assertFalse(official(url), url)

    def test_follow_links_stay_on_host_and_skip_sign_in(self):
        record = {"links": [{"text": "About Us", "href": "https://a.gov.in/about"}, {"text": "Contact", "href": "https://b.gov.in/contact"},
                            {"text": "Help", "href": "https://a.gov.in/login/help"}, {"text": "FAQ", "href": "https://a.gov.in/faq#top"},
                            {"text": "Grievance", "href": "https://a.gov.in/grievance"}, {"text": "Help desk", "href": "https://a.gov.in/desk"}]}
        self.assertEqual(["https://a.gov.in/about", "https://a.gov.in/faq", "https://a.gov.in/grievance"],
                         follow_links(record, "https://a.gov.in/"))


class FirstBatchTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.catalog = merged_catalog()
        cls.claims = json.loads(CLAIMS.read_text(encoding="utf-8"))

    def test_real_claims_validate_against_saved_snapshots(self):
        result = validate(self.claims, self.catalog, self.catalog["services"], load_snapshots())
        published = [p for p in self.claims["websites"] if p["status"] == "published"]
        self.assertEqual((len(self.claims["websites"]), len(published)), (result["profiles"], result["published"]))
        self.assertEqual(len(published), result["fieldsPublished"]["purpose"])
        self.assertTrue(all(p["reviewStatus"] != "human_reviewed" for p in self.claims["websites"]))
        # The Phase 6B first batch is unchanged by later batches.
        first = [p for p in self.claims["websites"] if p["batchId"] == "2026-10-17"]
        self.assertEqual((18, 15), (len(first), sum(p["status"] == "published" for p in first)))
        held = {p["websiteId"] for p in first if p["status"] != "published"}
        self.assertEqual({"uidai", "india-portal", "igod-1d591544b1c2e25d"}, held)

    def test_queue_keeps_every_unresolved_record_and_prioritises_service_portals(self):
        service_queue = json.loads((ROOT / "data/services/review_queue.json").read_text(encoding="utf-8"))
        queue = build_queue(self.catalog, self.claims, service_queue)
        rows = {row["websiteId"]: row for row in queue["queue"]}
        self.assertEqual(7347, queue["summary"]["websites"])
        self.assertEqual(7347 - len(self.claims["websites"]), queue["summary"]["queuedNotChecked"])
        self.assertTrue({"uidai", "india-portal", "igod-1d591544b1c2e25d"} <= set(rows))
        self.assertEqual(sum(1 for s in self.catalog["websites"]) - len(rows),
                         sum(1 for p in self.claims["websites"] if p["status"] == "published" and len(p["fields"]) == 7))
        directory = [row for row in queue["queue"] if row["state"] == "not_checked" and row["descriptionStatus"] == "directory_template"]
        self.assertTrue(directory and all(row["pending"] == "not_in_batch_template" and len(row["missing"]) == 7 for row in directory))
        batch = queue["nextBatch"]
        self.assertEqual(20, len(batch))
        self.assertTrue(all(rows[item["websiteId"]]["state"] == "not_checked" and official(item["start"]) for item in batch))
        # The Phase 6B queue put the Himachal Pradesh portal first; it is now profiled and never selected again.
        self.assertNotIn("igod-ffa4189a2ac66991", {item["websiteId"] for item in batch})

    def test_completeness_report_is_reproducible_and_never_counts_templates_as_verified(self):
        before = {path: sha(path) for path in PROTECTED}
        first, second = build_report(), build_report()
        self.assertEqual(first, second)
        self.assertEqual(before, {path: sha(path) for path in PROTECTED})
        rows = {row["field"]: row for row in first["websiteFields"]["rows"]}
        for row in first["websiteFields"]["rows"] + first["serviceFields"]["rows"]:
            self.assertLessEqual(row["verified"], row["populated"])
            self.assertEqual(row["populated"] + row["unknown"], first[("websiteFields" if row in first["websiteFields"]["rows"] else "serviceFields")]["total"])
        purpose = rows["Purpose (what the website is for)"]
        checked = {p["websiteId"] for p in self.claims["websites"] if p["status"] == "published"}
        unchecked_templates = sum(1 for s in self.catalog["websites"] if s["description"].startswith(TEMPLATE_PREFIX) and s["id"] not in checked)
        reviewed = {s["id"] for s in self.catalog["websites"] if s["verificationStatus"] == "verified"}
        self.assertEqual(len(reviewed | checked), purpose["verified"])
        # A template description is only ever "directory only"; it becomes verified solely through a checked profile purpose.
        self.assertEqual(unchecked_templates, purpose["directoryOnly"])
        coverage = first["coverage"]
        self.assertEqual(3823, coverage["directory"]["igodReviewQueueCandidates"])
        self.assertEqual(0, coverage["verifiedInformation"]["humanReviewed"])
        self.assertEqual({"PUBLISHED": 52, "NEEDS_REVIEW": 31}, coverage["publishedServices"]["serviceReviewQueueByLatestStatus"])
        services = {row["field"]: row for row in first["serviceFields"]["rows"]}
        self.assertEqual(52, services["Deadline"]["unknown"])
        self.assertEqual(sum(1 for s in self.catalog["services"] if s.get("fees") is None), services["Fees"]["unknown"])


if __name__ == "__main__":
    unittest.main()
