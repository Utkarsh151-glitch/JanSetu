"""Phase 8 guard: JanSetu stays local-only until an account and backend decision is recorded.

These tests fail if a network client, account endpoint, analytics SDK or hard-coded secret is added to the app.
Such a change first needs: a recorded owner decision (docs/PHASE8_ACCOUNT_ARCHITECTURE_DECISION.md), a privacy-policy
update and a Play Data safety review.
"""
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCES = sorted((ROOT / "app/src/main/java").rglob("*.kt"))
GRADLE = (ROOT / "app/build.gradle.kts").read_text(encoding="utf-8")


class LocalOnlyBoundaryTest(unittest.TestCase):
    def test_the_app_has_no_network_client(self):
        pattern = re.compile(r"HttpURLConnection|HttpsURLConnection|okhttp3|retrofit2|ktor|Volley|URL\([^)]*\)\.open|java\.net\.Socket\b")
        offenders = [str(p.relative_to(ROOT)) for p in SOURCES if pattern.search(p.read_text(encoding="utf-8"))]
        self.assertEqual([], offenders)

    def test_no_account_analytics_or_crash_reporting_dependency(self):
        dependencies = re.findall(r'(?:implementation|api)\("([^"]+)"\)', GRADLE)
        forbidden = re.compile(r"firebase|crashlytics|analytics|sentry|supabase|appwrite|auth0|amplify|okta|onesignal|play-services-auth", re.I)
        self.assertEqual([], [d for d in dependencies if forbidden.search(d)])

    def test_no_backend_endpoints_or_embedded_secrets(self):
        endpoint = re.compile(r"https?://(?![a-z0-9.-]*(?:gov\.in|nic\.in))[a-z0-9.-]+\.[a-z]{2,}(?:/|\")", re.I)
        secret = re.compile(r"(api[_-]?key|client[_-]?secret|access[_-]?token|BEGIN (RSA |EC )?PRIVATE KEY)", re.I)
        found = []
        for path in SOURCES:
            text = path.read_text(encoding="utf-8")
            found += [(path.name, m.group(0)) for m in endpoint.finditer(text)]
            found += [(path.name, m.group(0)) for m in secret.finditer(text)]
        self.assertEqual([], found)

    def test_no_signing_secret_is_packaged_or_committed_to_sources(self):
        self.assertFalse(list((ROOT / "app/src").rglob("*.pem")))
        self.assertFalse(list((ROOT / "app/src").rglob("*.jks")) + list((ROOT / "app/src").rglob("*.keystore")))
        self.assertIn('providers.environmentVariable("JANSETU_KEYSTORE")', GRADLE)


if __name__ == "__main__":
    unittest.main()
