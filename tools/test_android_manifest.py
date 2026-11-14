"""Phase 7: the manifest, backup rules and permissions stay as narrow as the privacy policy says."""
import unittest
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ANDROID = "{http://schemas.android.com/apk/res/android}"
MAIN = ROOT / "app/src/main"


class ManifestTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.manifest = ET.parse(MAIN / "AndroidManifest.xml").getroot()
        cls.app = cls.manifest.find("application")

    def test_permissions_are_unchanged_and_include_no_storage_access(self):
        permissions = sorted(p.get(ANDROID + "name") for p in self.manifest.findall("uses-permission"))
        self.assertEqual(["android.permission.INTERNET", "android.permission.POST_NOTIFICATIONS",
                          "android.permission.RECEIVE_BOOT_COMPLETED"], permissions)

    def test_backup_and_device_transfer_exclude_every_domain(self):
        self.assertEqual("false", self.app.get(ANDROID + "allowBackup"))
        self.assertEqual("false", self.app.get(ANDROID + "usesCleartextTraffic"))
        domains = {"root", "file", "database", "sharedpref", "external"}
        full = ET.parse(MAIN / "res/xml/backup_rules.xml").getroot()
        self.assertEqual(domains, {e.get("domain") for e in full.findall("exclude") if e.get("path") == "."})
        extraction = ET.parse(MAIN / "res/xml/data_extraction_rules.xml").getroot()
        for section in ("cloud-backup", "device-transfer"):
            self.assertEqual(domains, {e.get("domain") for e in extraction.find(section).findall("exclude") if e.get("path") == "."})
            self.assertFalse(extraction.find(section).findall("include"))

    def test_only_the_launcher_and_the_protected_document_provider_are_exported(self):
        exported = [(c.tag, c.get(ANDROID + "name")) for c in self.app if c.get(ANDROID + "exported") == "true"]
        self.assertEqual([("activity", ".MainActivity"), ("provider", ".vault.VaultDocumentsProvider")], exported)
        provider = self.app.find("provider")
        self.assertEqual("android.permission.MANAGE_DOCUMENTS", provider.get(ANDROID + "permission"))
        self.assertEqual("${applicationId}.vault", provider.get(ANDROID + "authorities"))
        self.assertEqual(["android.content.action.DOCUMENTS_PROVIDER"],
                         [a.get(ANDROID + "name") for a in provider.iter("action")])
        self.assertIsNone(provider.get(ANDROID + "readPermission"))
        self.assertIsNone(provider.get(ANDROID + "writePermission"))
        self.assertFalse([c for c in self.app.iter("provider") if "FileProvider" in (c.get(ANDROID + "name") or "")])

    def test_vault_storage_and_key_are_separate_from_the_tracker(self):
        store = (MAIN / "java/in/jansetu/app/vault/VaultStore.kt").read_text(encoding="utf-8")
        tracker = (MAIN / "java/in/jansetu/app/tracker/ApplicationStore.kt").read_text(encoding="utf-8")
        self.assertIn('"jansetu_vault_v1"', store)
        self.assertIn('"jansetu_tracker_v1"', tracker)
        self.assertNotIn("jansetu_tracker", store)
        self.assertIn('File(app.noBackupFilesDir, "vault")', store)
        self.assertNotIn("Log.", store + (MAIN / "java/in/jansetu/app/ui/DocumentScreens.kt").read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
