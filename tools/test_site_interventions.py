import copy
import unittest

from site_interventions import ROOT, read, validate


class SiteInterventionsTest(unittest.TestCase):
    def setUp(self):
        self.registry = read("data/site_interventions.json")
        self.audits = read("data/page_audits.json")
        self.catalog = read("app/src/main/assets/catalog.json")
        self.core = read("extension/core.mjs")
        self.background = read("extension/background.js")
        self.manifest = read("extension/manifest.json")
        self.files = sorted(p.name for p in (ROOT / "extension").iterdir() if p.is_file())

    def check(self, **overrides):
        values = dict(registry=self.registry, audits=self.audits, catalog=self.catalog,
                      core_js=self.core, background_js=self.background, manifest=self.manifest, extension_files=self.files)
        values.update(overrides)
        return validate(**values)

    def page(self, registry, page_id):
        return next(p for p in registry["pages"] if p["pageId"] == page_id)

    def test_real_registry_matches_every_allowlist(self):
        result = self.check()
        # The NSP Readable text pilot was withdrawn on 18 October 2026 and removed in Phase 17: no page can be styled.
        self.assertEqual([], result["extensionStyled"])
        nsp = self.page(self.registry, "scholarship-home")
        self.assertEqual("retired", nsp["status"])
        self.assertEqual({"retired"}, {c["state"] for c in nsp["controls"] if c["context"] == "chrome_extension"})
        self.assertEqual(["www.india.gov.in"], result["inAppHosts"])
        self.assertEqual(len(self.audits["pages"]), result["pages"])

    def test_sensitive_page_can_never_gain_styling(self):
        registry = copy.deepcopy(self.registry)
        self.page(registry, "rti-login")["controls"].append({"context": "chrome_extension", "control": "readable_text_css",
                                                             "state": "proposed", "stylesheet": "extension/readability.css"})
        with self.assertRaisesRegex(AssertionError, "Class E page cannot"):
            self.check(registry=registry)

    def test_allowlist_cannot_drift_from_reviewed_decisions(self):
        block = "\nconst readablePaths = {\n  'national-scholarship-portal': new Set(['/home'])\n};\n"
        # Re-adding a styling allowlist without a reviewed decision is refused, even with a matching background route.
        background = self.background + "\nconst reviewed = url.hostname === 'scholarships.gov.in' &&\n  url.pathname === '/home';\n"
        with self.assertRaisesRegex(AssertionError, "differs from registry live styling"):
            self.check(core_js=self.core + block, background_js=background)
        # An allowlisted path the background does not guard is refused.
        with self.assertRaisesRegex(AssertionError, "background.js route check"):
            self.check(core_js=self.core + block)
        # So is a background route with no allowlist entry.
        with self.assertRaisesRegex(AssertionError, "background.js route check"):
            self.check(background_js=background)

    def test_extension_cannot_regain_a_way_to_change_pages(self):
        for key, value in (("permissions", ["sidePanel", "activeTab", "scripting"]), ("content_scripts", [{"matches": ["https://*/*"], "css": ["x.css"]}]),
                           ("host_permissions", ["https://scholarships.gov.in/*"])):
            manifest = copy.deepcopy(self.manifest)
            manifest[key] = value
            with self.assertRaises(AssertionError, msg=key):
                self.check(manifest=manifest)
        with self.assertRaisesRegex(AssertionError, "Only the panel's own stylesheet"):
            self.check(extension_files=self.files + ["readability.css"])

    def test_proposals_stay_out_of_the_allowlist(self):
        registry = copy.deepcopy(self.registry)
        tn = self.page(registry, "tn-esevai-service-list")
        tn["controls"][0]["state"] = "active"
        with self.assertRaisesRegex(AssertionError, "Live styling needs pilot or approved status"):
            self.check(registry=registry)
        tn["status"] = "pilot"
        with self.assertRaisesRegex(AssertionError, "differs from registry live styling"):
            self.check(registry=registry)

    def test_approval_requires_a_recorded_human_review(self):
        registry = copy.deepcopy(self.registry)
        tn = self.page(registry, "tn-esevai-service-list")
        tn["status"] = "approved"
        tn["review"] = {"rules": ["overflow-x:auto"], "testsPassed": ["fixture"], "rollback": "remove and reset", "reviewer": "x", "date": "2026-10-17"}
        with self.assertRaisesRegex(AssertionError, "human review"):
            self.check(registry=registry)

    def test_presentation_change_needs_restore_in_the_same_context(self):
        registry = copy.deepcopy(self.registry)
        portal = self.page(registry, "india-portal-home")
        portal["controls"] = [c for c in portal["controls"] if c["control"] != "restore_original"]
        with self.assertRaisesRegex(AssertionError, "without restore"):
            self.check(registry=registry)

    def test_page_identity_is_exact(self):
        for url in ("http://rtionline.gov.in/", "https://rtionline.gov.in:8443/", "https://user@rtionline.gov.in/", "https://rtionline.gov.in/#top"):
            registry = copy.deepcopy(self.registry)
            audits = copy.deepcopy(self.audits)
            self.page(registry, "rti-home")["url"] = url
            next(p for p in audits["pages"] if p["pageId"] == "rti-home")["url"] = url
            with self.assertRaises(AssertionError, msg=url):
                self.check(registry=registry, audits=audits)

    def test_in_app_host_list_must_be_registered(self):
        catalog = copy.deepcopy(self.catalog)
        next(s for s in catalog["websites"] if s["id"] == "digilocker")["browserSupport"] = "in_app"
        with self.assertRaisesRegex(AssertionError, "In-app hosts"):
            self.check(catalog=catalog)


if __name__ == "__main__":
    unittest.main()
