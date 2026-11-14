"""Phase 17: the protected-file rebaseline touched only the owner-authorised files, and the withdrawn NSP pilot
is gone from the extension (kept only as archived evidence)."""
import hashlib
import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
AUTHORISED = {"data/services/catalog.json", "data/services/review_queue.json", "extension/manifest.json",
              "extension/background.js", "extension/readability.css"}


def read(rel):
    return json.loads((ROOT / rel).read_text(encoding="utf-8"))


class ProtectedRebaselineTest(unittest.TestCase):
    def test_only_authorised_files_changed_and_every_protected_hash_matches(self):
        record = read("data/validation_reports/phase17-protected-rebaseline.json")
        baseline = read("data/validation_reports/phase4-source-baseline.json")["protectedSha256"]
        changed = {name for name, entry in record["files"].items() if entry["before"] != entry["after"]}
        self.assertEqual(AUTHORISED, changed)
        for name, entry in record["files"].items():
            if name not in AUTHORISED:
                self.assertEqual(entry["before"], baseline[name], name)
            self.assertTrue(entry["reason"])
        self.assertTrue(all(record["unrelatedUnchanged"].values()))
        for name, digest in baseline.items():
            self.assertEqual(digest, hashlib.sha256((ROOT / name).read_bytes()).hexdigest(), name)
        self.assertNotIn("extension/readability.css", baseline)

    def test_the_rest_of_the_baseline_file_is_untouched(self):
        old = read("data/validation_reports/phase17-originals/phase4-source-baseline.json")
        new = read("data/validation_reports/phase4-source-baseline.json")
        self.assertEqual(old["sourceSha256"], new["sourceSha256"])
        self.assertEqual(old["phase3c"], new["phase3c"])
        self.assertEqual(set(old) , set(new))

    def test_the_retired_stylesheet_is_archived_outside_the_extension(self):
        self.assertFalse((ROOT / "extension/readability.css").exists())
        self.assertFalse((ROOT / "extension/readability-panel.css").exists())
        archive = ROOT / "data/retired/nsp-readable-text.css"
        self.assertTrue(archive.exists())
        self.assertIn("RETIRED 2026-10-18", archive.read_text(encoding="utf-8"))
        manifest = read("extension/manifest.json")
        self.assertEqual(["sidePanel", "activeTab"], manifest["permissions"])


if __name__ == "__main__":
    unittest.main()
