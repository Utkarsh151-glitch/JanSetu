"""Phase 15: the artifact inspector really reads the built APK, and rejects what a release must not contain."""
import unittest
import zipfile
from pathlib import Path

from inspect_release_artifact import inspect

ROOT = Path(__file__).resolve().parents[1]
DEBUG = ROOT / "app/build/outputs/apk/debug/app-debug.apk"


class ReleaseArtifactTest(unittest.TestCase):
    def test_debug_build_is_clean_but_never_accepted_as_a_release(self):
        self.assertTrue(DEBUG.exists(), "Build the debug APK first (the quality gate does)")
        report = inspect(DEBUG, release=False)
        self.assertEqual([], report["problems"])
        self.assertTrue({"assets/catalog.json", "assets/privacy.txt", "assets/privacy-hi.txt"} <= set(report["assets"]))
        self.assertEqual(["android.permission.INTERNET", "android.permission.POST_NOTIFICATIONS", "android.permission.RECEIVE_BOOT_COMPLETED"],
                         sorted(p for p in report["manifest"]["permissions"] if p.startswith("android.")))
        release = inspect(DEBUG, release=True)
        self.assertIn("Release artifact is debuggable", release["problems"])

    def test_restricted_files_and_review_material_are_detected(self):
        tampered = ROOT / "app/build/tmp/inspector-tampered.apk"
        tampered.parent.mkdir(parents=True, exist_ok=True)
        try:
            with zipfile.ZipFile(DEBUG) as source, zipfile.ZipFile(tampered, "w") as target:
                for item in source.infolist():
                    data = source.read(item)
                    if item.filename == "assets/catalog.json":
                        data = data.replace(b'"websites":', b'"aiRecommendation":"x","websites":', 1)
                    target.writestr(item, data)
                target.writestr("assets/research/snapshot.json", b"{}")
            problems = " ".join(inspect(tampered, release=False)["problems"])
            self.assertIn("Restricted files packaged", problems)
            self.assertIn("Internal review material", problems)
            self.assertIn("Unexpected assets", problems)
        finally:
            tampered.unlink(missing_ok=True)


if __name__ == "__main__":
    unittest.main()
