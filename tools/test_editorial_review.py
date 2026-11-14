"""Phase 6C: human review decisions, evidence consistency, staged revisions, packaging exclusions and queue rules."""
import copy
import json
import shutil
import tempfile
import unittest
from datetime import date
from pathlib import Path

from editorial_review import (INDEX, ROOT, ReviewError, build_index, check_index, check_revisions, claim_flags, claim_items,
                              effective, empty_reviews, import_decisions, profile_digest, snapshot_files)
from enrichment_queue import build_queue
from service_catalog import merged_catalog
from website_enrichment import CLAIMS, RUNTIME_KEYS, ClaimsError, load_snapshots, runtime_profiles, validate

TODAY = date(2026, 10, 18)


def read(path):
    return json.loads(Path(path).read_text(encoding="utf-8"))


class ReviewWorkflowTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.claims = read(CLAIMS)
        cls.profile = next(p for p in cls.claims["websites"] if p["websiteId"] == "cpgrams")
        cls.snapshots = snapshot_files()

    def decision(self, verdict="approve", claim="operator", **changes):
        claims = {}
        for item in claim_items(self.profile):
            flagged = bool(claim_flags(self.profile, item, self.snapshots))
            claims[item["key"]] = {"decision": "approve", "note": "", "sourceOpened": True if flagged else None}
        if verdict != "approve":
            claims[claim] = {"decision": verdict, "note": "Wording overstates the source.", "sourceOpened": True}
        exported = {"schemaVersion": 1, "reviewer": {"name": "Asha Verma", "role": "Editor"}, "reviewedOn": "2026-10-18", "attestation": True,
                    "decisions": [{"websiteId": "cpgrams", "profileDigest": profile_digest(self.profile), "decision": verdict,
                                   "notes": "" if verdict == "approve" else "Needs correction.", "claims": claims}]}
        for key, value in changes.items():
            if key in exported:
                exported[key] = value
            else:
                exported["decisions"][0][key] = value
        return exported

    def rejected(self, exported):
        with self.assertRaises(ReviewError) as raised:
            import_decisions(exported, self.claims, empty_reviews(), "test.json", TODAY)
        return " | ".join(raised.exception.errors)

    def test_complete_named_decision_is_appended_and_original_kept(self):
        before = empty_reviews()
        after = import_decisions(self.decision(), self.claims, before, "test.json", TODAY)
        self.assertEqual([], before["decisions"])  # input never mutated
        self.assertEqual(1, len(after["decisions"]))
        again = self.decision()
        self.assertIn("already imported", " ".join(self._errors(lambda: import_decisions(again, self.claims, after, "t2.json", TODAY))))

    def _errors(self, fn):
        try:
            fn()
        except ReviewError as error:
            return error.errors
        self.fail("expected a ReviewError")

    def test_reviewer_identity_attestation_and_dates_are_required(self):
        self.assertIn("reviewer name is required", self.rejected(self.decision(reviewer={"name": ""})))
        self.assertIn("automated agents cannot", self.rejected(self.decision(reviewer={"name": "Claude Code"})))
        self.assertIn("must confirm", self.rejected(self.decision(attestation=False)))
        self.assertIn("in the future", self.rejected(self.decision(reviewedOn="2026-12-01")))
        self.assertIn("before the evidence was captured", self.rejected(self.decision(reviewedOn="2026-10-01")))

    def test_every_claim_needs_an_explicit_decision_and_flagged_claims_need_the_source_opened(self):
        partial = self.decision()
        partial["decisions"][0]["claims"].pop("purpose")
        self.assertIn("every claim needs a decision", self.rejected(partial))
        unconfirmed = self.decision()
        unconfirmed["decisions"][0]["claims"]["operator"]["sourceOpened"] = False
        self.assertIn("without confirming the source was opened", self.rejected(unconfirmed))
        mixed = self.decision()
        mixed["decisions"][0]["claims"]["purpose"] = {"decision": "revise", "note": "x"}
        self.assertIn("only be approved when every claim", self.rejected(mixed))
        silent = self.decision("hold", notes="")
        self.assertIn("profile-level note", self.rejected(silent))

    def test_stale_packets_and_unpublished_profiles_are_refused(self):
        self.assertIn("stale packet", self.rejected(self.decision(profileDigest="0" * 64)))
        draft = next(p for p in self.claims["websites"] if p["status"] == "proposed")
        exported = self.decision(websiteId=draft["websiteId"], profileDigest=profile_digest(draft))
        self.assertIn("only published profiles", self.rejected(exported))

    def test_effective_state_comes_only_from_matching_human_decisions(self):
        reviews = import_decisions(self.decision(), self.claims, empty_reviews(), "test.json", TODAY)
        state = effective(self.profile, reviews)
        self.assertEqual(("human_reviewed", "2026-10-18"), (state["reviewStatus"], state["reviewedOn"]))
        changed = copy.deepcopy(self.profile)
        changed["fields"]["purpose"]["value"] += " (edited)"
        stale = effective(changed, reviews)
        self.assertEqual(("source_checked", 1), (stale["reviewStatus"], stale["staleDecisions"]))
        self.assertEqual("source_checked", effective(self.profile, empty_reviews())["reviewStatus"])

    def test_revise_withholds_claims_and_hold_removes_the_profile_from_the_app(self):
        revise = import_decisions(self.decision("revise"), self.claims, empty_reviews(), "r.json", TODAY)
        row = next(r for r in runtime_profiles(self.claims, revise) if r["websiteId"] == "cpgrams")
        self.assertEqual("source_checked", row["reviewStatus"])
        self.assertEqual(["operator"], effective(self.profile, revise)["withheld"])
        self.assertNotIn("operator", row)
        self.assertIn("purpose", row)
        # A withheld purpose means nothing checked is left to show: the profile is not packaged at all.
        no_purpose = import_decisions(self.decision("revise", claim="purpose"), self.claims, empty_reviews(), "p.json", TODAY)
        self.assertNotIn("cpgrams", {r["websiteId"] for r in runtime_profiles(self.claims, no_purpose)})
        hold = import_decisions(self.decision("hold", notes="Owner unclear."), self.claims, empty_reviews(), "h.json", TODAY)
        self.assertNotIn("cpgrams", {r["websiteId"] for r in runtime_profiles(self.claims, hold)})
        self.assertIn("cpgrams", {p["websiteId"] for p in self.claims["websites"]})  # claims and evidence untouched

    def test_reviewed_content_cannot_be_changed_silently(self):
        reviews = import_decisions(self.decision(), self.claims, empty_reviews(), "test.json", TODAY)
        claims = copy.deepcopy(self.claims)
        target = next(p for p in claims["websites"] if p["websiteId"] == "cpgrams")
        old = profile_digest(target)
        target["fields"]["purpose"]["value"] = "Changed wording."
        with self.assertRaises(ReviewError):
            check_revisions(claims, reviews)
        target["revisionHistory"] = [{"date": "2026-10-19", "by": "Editor", "change": "Purpose wording corrected after review", "previousDigest": old}]
        check_revisions(claims, reviews)
        self.assertEqual(1, len(reviews["decisions"]))  # the earlier decision is preserved, now stale

    def test_runtime_package_carries_only_permitted_fields(self):
        reviews = import_decisions(self.decision(), self.claims, empty_reviews(), "test.json", TODAY)
        rows = runtime_profiles(self.claims, reviews)
        text = json.dumps(rows, ensure_ascii=False)
        for row in rows:
            self.assertLessEqual(set(row), RUNTIME_KEYS)
            self.assertTrue(all(set(e) == {"id", "url", "checkedOn", "claim"} for e in row["evidence"]))
        for forbidden in ("Asha Verma", "quote", "profileDigest", "reviewer", "Wording overstates", "heldReason", "publicationDecision"):
            self.assertNotIn(forbidden, text)
        live = {r["websiteId"] for r in rows}
        unpublished = {p["websiteId"] for p in self.claims["websites"] if p["status"] != "published"}
        self.assertFalse(live & unpublished)
        reviewed = next(r for r in rows if r["websiteId"] == "cpgrams")
        self.assertEqual(("human_reviewed", "2026-10-18"), (reviewed["reviewStatus"], reviewed["reviewedOn"]))

    def test_repository_has_no_recorded_human_review_yet(self):
        reviews = read(ROOT / "data/website_enrichment/reviews.json")
        self.assertEqual([], reviews["decisions"])
        self.assertTrue(all(p["reviewStatus"] != "human_reviewed" and "humanReviewer" not in p for p in self.claims["websites"]))


class EvidenceConsistencyTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.claims = read(CLAIMS)
        cls.index = read(INDEX)

    def test_current_evidence_matches_its_pins(self):
        result = check_index(self.claims, self.index)
        self.assertEqual(sum(len(p["evidence"]) for p in self.claims["websites"]), result["pinned"])

    def sandbox(self):
        root = Path(tempfile.mkdtemp())
        self.addCleanup(shutil.rmtree, root)
        for pin in self.index["pins"].values():
            target = root / pin["snapshot"]
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(ROOT / pin["snapshot"], target)
        return root

    def test_changed_or_missing_snapshots_are_detected(self):
        root = self.sandbox()
        pin = self.index["pins"]["cpgrams/cp-purpose"]
        record = read(root / pin["snapshot"])
        record["text"] = record["text"].replace("24x7", "at any time")
        (root / pin["snapshot"]).write_text(json.dumps(record), encoding="utf-8")
        with self.assertRaises(ReviewError) as raised:
            check_index(self.claims, self.index, root)
        self.assertTrue(any("changed after it was pinned" in e for e in raised.exception.errors))
        (root / pin["snapshot"]).unlink()
        with self.assertRaises(ReviewError) as raised:
            check_index(self.claims, self.index, root)
        self.assertTrue(any("missing" in e for e in raised.exception.errors))

    def test_edited_quotes_and_unpinned_or_removed_evidence_are_refused(self):
        claims = copy.deepcopy(self.claims)
        profile = next(p for p in claims["websites"] if p["websiteId"] == "cpgrams")
        profile["evidence"][0]["quote"] = "is an online platform"
        with self.assertRaises(ReviewError):
            check_index(claims, self.index)
        with self.assertRaises(ReviewError):
            build_index(claims, snapshot_files(), self.index)  # an existing pin is never rewritten
        claims = copy.deepcopy(self.claims)
        profile = next(p for p in claims["websites"] if p["websiteId"] == "cpgrams")
        profile["evidence"].append(dict(profile["evidence"][0], id="cp-new"))
        with self.assertRaises(ReviewError) as raised:
            check_index(claims, self.index)
        self.assertTrue(any("not pinned" in e for e in raised.exception.errors))
        claims = copy.deepcopy(self.claims)
        profile = next(p for p in claims["websites"] if p["websiteId"] == "cpgrams")
        removed = profile["evidence"].pop()
        with self.assertRaises(ReviewError):
            check_index(claims, self.index)
        profile["revisionHistory"] = [{"date": "2026-10-19", "by": "Editor", "change": "Evidence retired", "retiredEvidence": [removed["id"]],
                                       "previousDigest": profile_digest(next(p for p in self.claims["websites"] if p["websiteId"] == "cpgrams"))}]
        check_index(claims, self.index)


class SecondBatchTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.catalog = merged_catalog()
        cls.claims = read(CLAIMS)
        cls.batch = read(ROOT / "data/website_enrichment/batch-2026-10-18.json")

    def test_every_batch_site_has_a_recorded_publication_decision(self):
        profiles = {p["websiteId"]: p for p in self.claims["websites"]}
        for item in self.batch["websites"]:
            profile = profiles[item["websiteId"]]
            decision = profile["publicationDecision"]
            self.assertEqual(profile["batchId"], "2026-10-18")
            self.assertIn(decision["decision"], {"publish", "draft", "hold"})
            self.assertTrue(decision["basis"] and decision["by"])
            self.assertNotEqual("human_reviewed", profile["reviewStatus"])
        statuses = [profiles[i["websiteId"]]["status"] for i in self.batch["websites"]]
        self.assertEqual({"published": 10, "proposed": 8, "held": 2}, {s: statuses.count(s) for s in set(statuses)})
        self.assertEqual("igod-ffa4189a2ac66991", self.batch["websites"][0]["websiteId"])  # Himachal portal led the queue

    def test_drafts_are_validated_but_never_packaged_and_flags_reach_reviewers(self):
        validate(self.claims, self.catalog, self.catalog["services"], load_snapshots())
        live = {r["websiteId"] for r in runtime_profiles(self.claims)}
        drafts = {p["websiteId"] for p in self.claims["websites"] if p["status"] == "proposed"}
        self.assertEqual(8, len(drafts))
        self.assertFalse(live & drafts)
        janasunani = next(p for p in self.claims["websites"] if p["websiteId"] == "igod-5891bfd68a83ed15")
        purpose = next(i for i in claim_items(janasunani) if i["key"] == "purpose")
        self.assertTrue(any("translation" in f for f in claim_flags(janasunani, purpose, snapshot_files())))

    def test_queue_never_reselects_checked_draft_or_held_sites_and_keeps_priority_order(self):
        service_queue = read(ROOT / "data/services/review_queue.json")
        queue = build_queue(self.catalog, self.claims, service_queue)
        profiled = {p["websiteId"] for p in self.claims["websites"]}
        batch = queue["nextBatch"]
        self.assertFalse({b["websiteId"] for b in batch} & profiled)
        self.assertEqual([b["priority"] for b in batch], sorted((b["priority"] for b in batch), reverse=True))
        rows = {r["websiteId"]: r for r in queue["queue"]}
        self.assertEqual("draft", rows["igod-18649bde195e5a01"]["state"])
        self.assertTrue(rows["igod-18649bde195e5a01"]["pending"].startswith("Draft awaiting publication decision"))
        self.assertEqual("held", rows["igod-44b4a596b1818cab"]["state"])

    def test_claims_validator_refuses_human_review_written_into_claims(self):
        claims = copy.deepcopy(self.claims)
        claims["websites"][0]["reviewStatus"] = "human_reviewed"
        claims["websites"][0]["humanReviewer"] = "Someone"
        with self.assertRaises(ClaimsError) as raised:
            validate(claims, self.catalog, self.catalog["services"], load_snapshots())
        self.assertTrue(any("recorded only through reviews.json" in e for e in raised.exception.errors))


class AdvisoryPrereviewTest(unittest.TestCase):
    """The 18 Oct AI pre-review is advisory: it can shape staged revisions and the packet, never a decision."""

    @classmethod
    def setUpClass(cls):
        cls.claims = read(CLAIMS)
        cls.advice_path = ROOT / "data/website_enrichment/review/ai-prereview-2026-10-18.json"
        cls.advice = read(cls.advice_path)

    def test_advisory_file_is_never_importable_as_human_review(self):
        self.assertFalse(self.advice["importableByEditorialReviewPy"])
        self.assertFalse(self.advice["formalHumanReviewCompleted"])
        with self.assertRaises(ReviewError):
            import_decisions(self.advice, self.claims, empty_reviews(), self.advice_path.name, TODAY)
        self.assertEqual([], read(ROOT / "data/website_enrichment/reviews.json")["decisions"])

    def test_every_revision_traces_to_the_reviewed_version_and_is_recorded(self):
        advised = {p["websiteId"]: p["sourceProfileDigest"] for p in self.advice["profiles"]}
        revised = [p for p in self.claims["websites"] if p.get("revisionHistory")]
        self.assertEqual(15, len(revised))
        for profile in revised:
            # The first revision answers the advice and names the exact version the AI reviewed; later ones chain on.
            entry = profile["revisionHistory"][0]
            self.assertEqual(advised[profile["websiteId"]], entry["previousDigest"])
            self.assertTrue(all("Advisory only" in r["reason"] for r in profile["revisionHistory"]))
            self.assertNotIn("human_reviewed", json.dumps(profile))
        changed = [p for p in revised if profile_digest(p) != p["revisionHistory"][0]["previousDigest"]]
        self.assertEqual(14, len(changed))

    def test_incomplete_revision_entries_are_refused(self):
        catalog = merged_catalog()
        claims = copy.deepcopy(self.claims)
        next(p for p in claims["websites"] if p.get("revisionHistory"))["revisionHistory"].append({"date": "2026-10-19", "change": "x"})
        with self.assertRaises(ClaimsError) as raised:
            validate(claims, catalog, catalog["services"], load_snapshots())
        self.assertTrue(any("revisionHistory entries need" in e for e in raised.exception.errors))

    def test_packet_shows_advice_as_advisory_and_marks_revised_wording(self):
        from editorial_review import packet_data
        rows = packet_data(self.claims, read(INDEX), snapshot_files(), empty_reviews())
        claims = [c for r in rows for c in r["claims"]]
        advised = [c for c in claims if c["advisory"]]
        self.assertGreater(len(advised), 140)
        self.assertEqual(14, sum(1 for c in advised if c["advisory"]["wordingChangedSince"]))
        nsp = next(c for r in rows if r["websiteId"] == "national-scholarship-portal" for c in r["claims"] if c["key"] == "operator")
        self.assertEqual("REVISE", nsp["advisory"]["recommendation"])
        self.assertTrue(nsp["advisory"]["wordingChangedSince"])
        html_text = (ROOT / "data/website_enrichment/review/review-packet-2026-10-17.html").read_text(encoding="utf-8")
        self.assertIn("advisory only, not a decision", html_text)

    def test_link_check_closed_link_only_targets_and_excluded_sensitive_ones(self):
        snapshots = snapshot_files()
        links = [(p["websiteId"], l["url"]) for p in self.claims["websites"] if p["status"] == "published"
                 for name in ("help", "tasks") for l in p["fields"].get(name, [])]
        self.assertTrue(links and all(snapshots.get(url) for _, url in links))
        urls = {url for _, url in links}
        self.assertNotIn("https://scholarship.odisha.gov.in/know-your-status", urls)  # Aadhaar + OTP journey
        self.assertNotIn("https://himachal.gov.in/Content/HtmlPage?qs=LdEF%2BAuYkB7rMRolWuVbNg%3D%3D", urls)  # personal-data form
        self.assertNotIn("https://edistrict.delhigovt.nic.in/in/en/Public/Help.html", urls)  # accessibility help only
        eci = next(p for p in self.claims["websites"] if p["websiteId"] == "igod-85affa23dc47255b")
        self.assertNotIn("jurisdiction", eci["fields"])
        self.assertIn("jurisdiction", {h["field"] for h in eci["heldFields"]})


if __name__ == "__main__":
    unittest.main()
