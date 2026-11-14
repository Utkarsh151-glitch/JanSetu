"""Phase 6D: reconciling AI recommendations, staging, reviewer queue, packets and release exclusions."""
import copy
import json
import unittest
import zipfile
from datetime import date
from pathlib import Path

from editorial_review import (INDEX, PACKET_DIR, ReviewError, effective, empty_reviews, import_decisions, load_reviews,
                              profile_digest, snapshot_files, write_packet)
from review_reconciliation import build, stage, validate_advice, wording_check
from website_enrichment import CLAIMS, runtime_profiles

ROOT = Path(__file__).resolve().parents[1]
ADVICE = PACKET_DIR / "ai-prereview-2026-10-18.json"
TODAY = date(2026, 10, 18)


def read(path):
    return json.loads(Path(path).read_text(encoding="utf-8"))


class ValidateRecommendationsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.claims, cls.index, cls.advice = read(CLAIMS), read(INDEX), read(ADVICE)

    def problems(self, advice):
        return {p["type"] for p in validate_advice(advice, self.claims, self.index)["problems"]}

    def test_real_recommendations_reconcile_without_problems(self):
        report = validate_advice(self.advice, self.claims, self.index)
        self.assertEqual((157, 25, []), (report["recommendations"], report["profiles"], report["problems"]))

    def test_unknown_website_and_claim_keys_are_reported(self):
        advice = copy.deepcopy(self.advice)
        advice["profiles"][0]["websiteId"] = "not-a-site"
        advice["profiles"][1]["claims"][0]["key"] = "help:https://example.gov.in/nowhere"
        self.assertTrue({"unknown_website", "unknown_claim_key"} <= self.problems(advice))

    def test_missing_unpinned_and_unexplained_stale_entries_are_reported(self):
        advice = copy.deepcopy(self.advice)
        advice["profiles"][0]["claims"][0]["evidenceIds"] = ["no-such-evidence"]
        advice["profiles"][1]["sourceProfileDigest"] = "f" * 64
        index = copy.deepcopy(self.index)
        wid = advice["profiles"][2]["websiteId"]
        eid = advice["profiles"][2]["claims"][0]["evidenceIds"][0]
        index["pins"].pop(f"{wid}/{eid}")
        problems = {p["type"] for p in validate_advice(advice, self.claims, index)["problems"]}
        self.assertTrue({"unknown_evidence", "stale_unexplained", "unpinned_evidence"} <= problems)

    def test_duplicate_malformed_and_decision_bearing_entries_are_reported(self):
        advice = copy.deepcopy(self.advice)
        claims = advice["profiles"][0]["claims"]
        claims.append(copy.deepcopy(claims[0]))
        claims[1]["aiRecommendation"] = "APPROVE"
        claims[2]["formalDecision"] = "approve"
        advice["profiles"].append({"websiteId": "cpgrams", "claims": "oops"})
        self.assertTrue({"duplicate", "malformed_claim", "claims_human_decision", "malformed_profile"} <= self.problems(advice))

    def test_unsupported_or_broadened_wording_is_flagged(self):
        sources = ["The portal is maintained by the Department of Information Technology."]
        check = wording_check("Run by the Ministry of Finance for all citizens since 2019.", sources)
        self.assertTrue({"Ministry", "Finance", "2019"} <= set(check["unsupportedTerms"]), check)
        self.assertIn("all", check["broadeningTerms"])
        self.assertEqual([], wording_check("Maintained by the Department of Information Technology", sources)["unsupportedTerms"])


class StagingTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.claims, cls.result = read(CLAIMS), build("2026-10-18")
        cls.staged = {(r["websiteId"], r["claimKey"]): r for r in cls.result["staged"]}

    def test_every_recommendation_keeps_its_audit_trail_and_stays_a_proposal(self):
        self.assertEqual(157, len(self.result["staged"]))
        self.assertEqual({"REVISE": 14, "HOLD": 22, "APPROVE_CANDIDATE": 121}, self.result["summary"]["byAiRecommendation"])
        for record in self.result["staged"]:
            self.assertIn(record["stagedAction"], {"APPROVE_CANDIDATE", "REVISE", "HOLD", "NEEDS_MORE_EVIDENCE"})
            self.assertTrue(record["aiReason"] and record["requiredHumanAction"])
            self.assertNotIn("reviewer", record)
        text = json.dumps(self.result, ensure_ascii=False)
        self.assertNotIn("human_reviewed", text.replace("unless it says human_reviewed", ""))
        self.assertEqual(0, self.result["humanDecisions"])
        self.assertEqual(25, self.result["summary"]["publishedProfilesAwaitingAPerson"])

    def test_holds_never_disappear_and_state_their_safety_action(self):
        holds = [r for r in self.result["staged"] if r["aiRecommendation"] == "HOLD"]
        self.assertEqual(22, len(holds))
        self.assertTrue(all(r["safetyAction"] or r["outcome"] for r in holds))
        withheld = [r for r in self.result["staged"] if r["outcome"] == "withheld_claim"]
        self.assertEqual(4, len(withheld))
        self.assertTrue(all(r["safetyAction"] == "withhold_claim" and not r["publicationState"]["claimInApp"] for r in withheld))
        janasunani = self.staged[("igod-5891bfd68a83ed15", "purpose")]
        self.assertEqual(("HOLD", "staged_language_review"), (janasunani["stagedAction"], janasunani["outcome"]))

    def test_risk_order_puts_operator_and_jurisdiction_first(self):
        ranks = [r["riskRank"] for r in self.result["staged"]]
        self.assertEqual(ranks, sorted(ranks))
        self.assertEqual("Department or operator", self.result["staged"][0]["riskCategory"])
        self.assertEqual(list(range(1, 158)), [r["reviewOrder"] for r in self.result["staged"]])

    def test_applied_revisions_cite_existing_evidence_and_add_no_unsupported_names(self):
        applied = [r for r in self.result["staged"] if r["outcome"] == "applied_revision"]
        self.assertEqual(14, len(applied))
        for record in applied:
            self.assertEqual([], record["wordingCheck"]["applied"]["unsupportedTerms"], record["claimKey"])
            self.assertEqual([], record["wordingCheck"]["applied"]["broadeningTerms"], record["claimKey"])

    def test_drafts_held_and_language_reviews_each_have_a_task(self):
        queue = read(PACKET_DIR / "review-queue.json")
        self.assertEqual(13, len(queue["draftsAndHeld"]))  # 8 drafts + 5 held
        self.assertTrue(all(t["task"] and t["rule"] for t in queue["draftsAndHeld"]))
        languages = {lang for t in queue["languageReview"] for lang in t["languages"]}
        self.assertTrue({"or", "ml", "hi", "ta"} <= languages)

    def test_staging_never_changes_what_the_app_shows(self):
        rows = runtime_profiles(self.claims, load_reviews())
        self.assertEqual(25, len(rows))
        self.assertFalse(any(r["reviewStatus"] == "human_reviewed" for r in rows))
        text = json.dumps(rows, ensure_ascii=False)
        for marker in ("stagedAction", "aiRecommendation", "reviewOrder", "requiredHumanAction", "revisionHistory"):
            self.assertNotIn(marker, text)


class ImporterAndPacketTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.claims = read(CLAIMS)

    def decision_file(self, name):
        profile = next(p for p in self.claims["websites"] if p["websiteId"] == "rti-online")
        from editorial_review import claim_flags, claim_items
        snaps = snapshot_files()
        claims = {i["key"]: {"decision": "approve", "note": "", "sourceOpened": True if claim_flags(profile, i, snaps) else None}
                  for i in claim_items(profile)}
        return {"reviewer": {"name": name}, "reviewedOn": "2026-10-18", "attestation": True,
                "decisions": [{"websiteId": "rti-online", "profileDigest": profile_digest(profile), "decision": "approve", "notes": "", "claims": claims}]}

    def test_ai_and_placeholder_identities_and_ai_files_are_refused(self):
        for name in ("ChatGPT", "Claude Code", "Gemini assistant", "Test Reviewer", "Smoke Test", "Dummy User"):
            with self.assertRaises(ReviewError, msg=name):
                import_decisions(self.decision_file(name), self.claims, empty_reviews(), "x.json", TODAY)
        for path in (ADVICE, PACKET_DIR / "staging-2026-10-18.json", PACKET_DIR / "review-queue.json"):
            with self.assertRaises(ReviewError) as raised:
                import_decisions(read(path), self.claims, empty_reviews(), path.name, TODAY)
            self.assertIn("never be imported", raised.exception.errors[0])

    def test_earlier_decisions_are_preserved_when_more_are_imported(self):
        first = import_decisions(self.decision_file("Asha Verma"), self.claims, empty_reviews(), "a.json", TODAY)
        second_file = self.decision_file("Ravi Kumar")
        second = import_decisions(second_file, self.claims, first, "b.json", TODAY)
        self.assertEqual(first["decisions"][0], second["decisions"][0])
        self.assertEqual(2, len(second["decisions"]))
        self.assertEqual([], read(ROOT / "data/website_enrichment/reviews.json")["decisions"])

    def test_versioned_packet_covers_current_profiles_in_risk_order(self):
        packets = sorted(PACKET_DIR.glob("review-packet-all-*.json"))
        self.assertTrue(packets)
        data = read(packets[-1])
        profiles = data["profiles"]
        self.assertEqual(25, len(profiles))
        current = {p["websiteId"]: profile_digest(p) for p in self.claims["websites"]}
        self.assertTrue(all(current[p["websiteId"]] == p["digest"] for p in profiles))
        self.assertIn(data["packetVersion"], packets[-1].name)
        orders = [p["firstOrder"] for p in profiles]
        self.assertEqual(orders, sorted(orders))

    def test_packet_generation_refuses_stale_staging(self):
        claims = copy.deepcopy(self.claims)
        target = next(p for p in claims["websites"] if p["websiteId"] == "cpgrams")
        target["fields"]["purpose"]["value"] += " (changed)"
        with self.assertRaises(ReviewError) as raised:
            write_packet(claims, read(INDEX), empty_reviews(), {}, all_profiles=True)
        self.assertIn("stale", raised.exception.errors[0])


class ReleaseExclusionTest(unittest.TestCase):
    """Internal research text, quotations, advice and staging never reach runtime or release artifacts."""

    def test_runtime_catalog_and_built_apks_carry_no_internal_material(self):
        claims = read(CLAIMS)
        # Link labels and names ("Contact Us", a ministry's name) legitimately appear; sentences of official text must not.
        quotes = [e["quote"] for p in claims["websites"] for e in p["evidence"] if len(e["quote"].split()) >= 8]
        self.assertGreater(len(quotes), 50)
        artifacts = [ROOT / "app/build/generated/runtimeAssets/catalog.json"]
        extension = [p for p in (ROOT / "extension").rglob("*") if p.is_file() and "node_modules" not in p.parts]
        self.assertFalse([p.name for p in extension if p.suffix in {".py", ".pem"} or "review" in p.name or "claims" in p.name])
        self.assertNotIn("websiteProfiles", (ROOT / "extension/catalog.json").read_text(encoding="utf-8"))
        artifacts += [p for p in extension if p.suffix in {".json", ".js", ".mjs", ".html"}]
        for apk in (ROOT / "app/build/outputs/apk").glob("*/*.apk"):
            with zipfile.ZipFile(apk) as z:
                names = z.namelist()
                self.assertFalse([n for n in names if any(k in n for k in ("research/", "claims.json", "evidence_index", "prereview", "staging-", "review-packet"))], apk.name)
                artifacts.append(("apk", z.read("assets/catalog.json").decode("utf-8")))
        for artifact in artifacts:
            text = artifact[1] if isinstance(artifact, tuple) else artifact.read_text(encoding="utf-8")
            for marker in ("aiRecommendation", "stagedAction", "revisionHistory", "previousDigest", "humanReviewer", "\"quote\""):
                self.assertNotIn(marker, text)
            self.assertFalse([q for q in quotes if q in text][:3])

    def test_identifiers_and_protected_sources_are_preserved(self):
        runtime = read(ROOT / "app/build/generated/runtimeAssets/catalog.json")
        source = read(ROOT / "app/src/main/assets/catalog.json")
        self.assertEqual([w["id"] for w in source["websites"]], [w["id"] for w in runtime["websites"]])
        self.assertEqual(52, len(runtime["services"]))
        import hashlib
        baseline = read(ROOT / "data/validation_reports/phase4-source-baseline.json")["protectedSha256"]
        self.assertEqual(baseline, {k: hashlib.sha256((ROOT / k).read_bytes()).hexdigest() for k in baseline})


if __name__ == "__main__":
    unittest.main()
