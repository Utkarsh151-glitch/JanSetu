import copy
import json
import subprocess
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

from catalog_pipeline import publish, stage_discovery, validate_directory_eligibility
from government_sources import CachedClient, IgodAdapter, normalize_url, check_robots
from prepare_discovery_catalog import destination_health, eligible, normalize_entries, source_context
from discovery_report import category_matrix, state_coverage

FIXTURES = Path(__file__).parent / "fixtures/igod"
ROOT = Path(__file__).resolve().parents[1]


class ParsingTest(unittest.TestCase):
    def test_real_ministry_html_and_exposed_pagination(self):
        result = IgodAdapter.parse((FIXTURES / "ministries.html").read_text(encoding="utf-8"), "https://igod.gov.in/ug/E002/organizations")
        self.assertEqual(53, result["expectedResults"])
        self.assertEqual(25, len(result["entries"]))
        self.assertEqual(28, sum(p["expectedRows"] for p in result["pagination"]))
        self.assertEqual("https://igod.gov.in/ug/E002/organizations_list_more/25/5?keyword=", result["pagination"][0]["url"])
        self.assertTrue(any(e["originalUrl"] is None for e in result["entries"]))
        self.assertTrue(all(e["detailUrl"].startswith("https://igod.gov.in/organization/") for e in result["entries"]))

    def test_real_partial_html(self):
        result = IgodAdapter.parse((FIXTURES / "pagination.html").read_text(encoding="utf-8"), "https://igod.gov.in/ug/E005/organizations_list_more/25/5?keyword=", {"name": "Attached offices", "listingPage": "https://igod.gov.in/ug/E005/organizations"})
        self.assertEqual(5, len(result["entries"]))
        self.assertEqual([], result["pagination"])

    def test_unlinked_district_is_not_silently_dropped(self):
        result = IgodAdapter.parse((FIXTURES / "districts-fragment.html").read_text(encoding="utf-8"), "https://igod.gov.in/districts/list_more/84/24", {"name": "Districts"})
        self.assertEqual(24, len(result["entries"]))
        self.assertTrue(any(e["originalName"] == "Bichom" and e["originalUrl"] is None for e in result["entries"]))

    def test_state_navigation_uses_actual_source_links(self):
        result = IgodAdapter.parse((FIXTURES / "state.html").read_text(encoding="utf-8"), "https://igod.gov.in/sg/MP/categories")
        self.assertTrue(any(c["url"] == "https://igod.gov.in/sg/MP/E042/organizations" for c in result["children"]))
        self.assertTrue(any(e["originalUrl"] == "https://mp.gov.in" and e["sourceCategory"] == "State Portal" for e in result["entries"]))
        self.assertFalse(any("Shabd" in e["originalName"] for e in result["entries"]))

    def test_structure_changes_are_not_empty_directories(self):
        for html in ("<html>Access denied</html>", "<main id='main-content'><div class='grid-content'><h2>Organizations</h2><div class='search-meta'>20 Results</div></div></main>"):
            with self.assertRaises(ValueError):
                IgodAdapter.parse(html, "https://igod.gov.in/ug/E002/organizations")


class NormalizationTest(unittest.TestCase):
    def setUp(self):
        self.taxonomy = json.loads((ROOT / "data/taxonomy.json").read_text(encoding="utf-8"))
        self.states = {"MP": {"name": "Madhya Pradesh"}, "LD": {"name": "Lakshadweep"}}
        self.entry = {"sourceId": "igod", "originalName": "Agar-Malwa", "originalUrl": "https://agarmalwa.nic.in",
                      "listingPage": "https://igod.gov.in/sg/MP/E042/organizations", "sourcePage": "https://igod.gov.in/sg/MP/E042/organizations",
                      "detailUrl": "https://igod.gov.in/organization/test", "sourceCategory": "Districts", "preview": False,
                      "checkedAt": "2026-10-13T22:00:00+00:00", "snapshot": "recorded.html"}

    def test_url_normalization_preserves_path_and_host_distinctions(self):
        self.assertEqual("https://www.example.gov.in/a?q=1", normalize_url(" HTTPS://WWW.Example.GOV.IN:443/a?q=1#section "))
        self.assertNotEqual(normalize_url("https://www.example.gov.in"), normalize_url("https://example.gov.in"))
        for url in ("javascript:alert(1)", "https://user:pass@example.gov.in", "https://127.0.0.1", "https://example.gov.in:8080"):
            with self.assertRaises(ValueError):
                normalize_url(url)

    def test_state_ut_district_and_other_levels(self):
        self.assertEqual("district", source_context(self.entry, self.states, self.taxonomy)["governmentLevel"])
        self.assertEqual("Madhya Pradesh", source_context(self.entry, self.states, self.taxonomy)["state"])
        for route, expected in [("/sg/LD/E003/organizations", "union_territory"), ("/leg/MP/L008/organizations", "municipal_local"),
                                ("/jud/J002/organizations", "judiciary"), ("/leg/L004/organizations", "legislature")]:
            self.assertEqual(expected, source_context({**self.entry, "listingPage": "https://igod.gov.in" + route}, self.states, self.taxonomy)["governmentLevel"])
        statutory = {**self.entry, "listingPage": "https://igod.gov.in/ug/E012/organizations", "sourceCategory": "Statutory Bodies"}
        self.assertEqual("statutory_public_institution", source_context(statutory, self.states, self.taxonomy)["governmentLevel"])
        statutory["sourceCategory"] = "Statutory / Autonomous Bodies"
        self.assertEqual("union", source_context(statutory, self.states, self.taxonomy)["governmentLevel"])

    def test_exact_url_dedup_keeps_source_evidence_and_organizations(self):
        entries = [self.entry, {**self.entry, "sourcePage": "https://igod.gov.in/districts", "detailUrl": "https://igod.gov.in/organization/another"}]
        rows, orgs, missing, suspected = normalize_entries(entries, self.states, self.taxonomy, {"websites": []})
        self.assertEqual(1, len(rows))
        self.assertEqual(2, len(orgs))
        self.assertEqual(2, len(rows[0]["sourceEvidence"]))
        self.assertEqual("Agar-Malwa", rows[0]["district"])
        self.assertEqual([], missing)

    def test_missing_url_is_an_unresolved_organization_not_a_fake_website(self):
        rows, orgs, missing, _ = normalize_entries([{**self.entry, "originalUrl": None}], self.states, self.taxonomy, {"websites": []})
        self.assertEqual([], rows)
        self.assertEqual(1, len(orgs))
        self.assertEqual(1, len(missing))

    def test_legacy_host_variant_id_collision_does_not_overwrite_identity(self):
        import hashlib
        url = normalize_url(self.entry["originalUrl"])
        old_id = "igod-" + hashlib.sha256(url.encode()).hexdigest()[:16]
        old = {"id": old_id, "url": "https://www.agarmalwa.nic.in/"}
        rows, *_ = normalize_entries([self.entry], self.states, self.taxonomy, {"websites": [old]})
        self.assertNotEqual(old_id, rows[0]["id"])
        rerun, *_ = normalize_entries([self.entry], self.states, self.taxonomy, {"websites": [old, rows[0]]})
        self.assertEqual(rows[0]["id"], rerun[0]["id"])


class NetworkTest(unittest.TestCase):
    def test_redirect_header_tokens_and_session_cookies_are_not_retained(self):
        def response(args, **kwargs):
            Path(args[args.index("--dump-header") + 1]).write_text("HTTP/1.1 302 Found\nLocation: /home?token=PRIVATE_TOKEN_MARKER\nSet-Cookie: sid=PRIVATE_COOKIE_MARKER\n")
            return subprocess.CompletedProcess([], 0, b'\nJANSETU_FETCH_META\n' + json.dumps({"http_code": 302}).encode(), b"")
        with tempfile.TemporaryDirectory() as directory, patch("government_sources.subprocess.run", side_effect=response):
            meta, _ = CachedClient(directory, delay=0, retain_pages=False).fetch("https://example.gov.in/")
            self.assertEqual("/home?token=[redacted]", meta["location"])
            stored = "".join(p.read_text() for p in Path(directory).iterdir())
            self.assertNotIn("PRIVATE_TOKEN_MARKER", stored)
            self.assertNotIn("PRIVATE_COOKIE_MARKER", stored)
        for url in ("https://example.gov.in/?token=opaque", "https://example.gov.in/home;jsessionid=opaque"):
            with self.assertRaises(ValueError):
                normalize_url(url)

    def test_destination_crawl_delay_overrides_default_before_next_request(self):
        body = b'User-agent: *\nAllow: /\nCrawl-delay: 10\n'
        response = body + b'\nJANSETU_FETCH_META\n' + json.dumps({"http_code": 200}).encode()
        with tempfile.TemporaryDirectory() as directory, patch("government_sources.subprocess.run", return_value=subprocess.CompletedProcess([], 0, response, b"")), patch("government_sources.time.monotonic", return_value=100), patch("government_sources.time.sleep") as sleep:
            client = CachedClient(directory, delay=2, retain_pages=False)
            meta, _ = check_robots(client, "https://example.gov.in")
            self.assertEqual(10, meta["crawlDelay"])
            client.fetch("https://example.gov.in/")
            sleep.assert_called_once_with(10)

    def test_health_cache_discards_homepage_and_form_body(self):
        body = b'<html><title>Public portal</title><p>PRIVATE_CONTACT_MARKER</p><input value="PRIVATE_FORM_MARKER"></html>'
        response = body + b'\nJANSETU_FETCH_META\n' + json.dumps({"http_code": 200}).encode()
        with tempfile.TemporaryDirectory() as directory, patch("government_sources.subprocess.run", return_value=subprocess.CompletedProcess([], 0, response, b"")):
            client = CachedClient(directory, delay=0, retain_pages=False)
            meta, memory_body = client.fetch("https://example.gov.in/")
            self.assertIn("PRIVATE_CONTACT_MARKER", memory_body)
            self.assertEqual("Public portal", meta["pageTitle"])
            self.assertEqual([], list(Path(directory).glob("*.html")))
            stored = "".join(p.read_text(encoding="utf-8") for p in Path(directory).glob("*.json"))
            self.assertNotIn("PRIVATE_CONTACT_MARKER", stored)
            self.assertNotIn("PRIVATE_FORM_MARKER", stored)
            _, cached_body = client.fetch("https://example.gov.in/")
            self.assertEqual("", cached_body)
    def test_transient_server_failure_retries_and_rerun_uses_cache(self):
        with tempfile.TemporaryDirectory() as directory:
            client = CachedClient(directory, delay=0)
            responses = [subprocess.CompletedProcess([], 0, json.dumps({"http_code": 503}), ""), subprocess.CompletedProcess([], 0, json.dumps({"http_code": 200}), "")]
            with patch("government_sources.subprocess.run", side_effect=responses) as request, patch("government_sources.time.sleep"):
                meta, _ = client.fetch("https://example.gov.in/")
                self.assertEqual("OK", meta["status"])
                self.assertEqual(2, request.call_count)
                client.fetch("https://example.gov.in/")
                self.assertEqual(2, request.call_count)

    def test_tls_and_access_denial_are_not_retried_or_bypassed(self):
        for return_code, http, expected in [(60, 0, "TLS_ERROR"), (0, 403, "ACCESS_DENIED"), (0, 429, "ACCESS_DENIED")]:
            with tempfile.TemporaryDirectory() as directory, patch("government_sources.subprocess.run", return_value=subprocess.CompletedProcess([], return_code, json.dumps({"http_code": http}), "blocked")) as request:
                meta, _ = CachedClient(directory, delay=0).fetch("https://example.gov.in/")
                self.assertEqual(expected, meta["status"])
                self.assertEqual(1, request.call_count)
                self.assertNotIn("--insecure", request.call_args[0][0])

    def test_cross_host_redirect_not_followed_and_default_server_not_healthy(self):
        class Client:
            def __init__(self, redirect): self.redirect = redirect; self.requests = []
            def fetch(self, url, **kwargs):
                self.requests.append(url)
                if url.endswith("/robots.txt"): return {"url": url, "status": "OK", "httpStatus": 200}, "User-agent: *\nAllow: /"
                if self.redirect: return {"status": "OK", "httpStatus": 302, "location": "https://unexpected.example/"}, ""
                return {"status": "OK", "httpStatus": 200}, "<title>Apache Tomcat/9</title>" + "x" * 300
        for redirect, expected in [(True, "REDIRECT_REVIEW"), (False, "DEFAULT_SERVER_PAGE")]:
            client = Client(redirect)
            result = destination_health({"url": "https://example.gov.in/"}, client)
            self.assertEqual(expected, result["status"])
            self.assertFalse(any("unexpected" in u for u in client.requests))


class PublicationTest(unittest.TestCase):
    def setUp(self):
        self.row = {"id": "test-listing", "title": "Example", "description": "Source listed", "url": "https://example.gov.in/",
                    "organization": "Example", "level": "State", "categories": ["information"], "keywords": ["example"],
                    "sourceUrl": "https://igod.gov.in/organization/example", "sourceType": "Official directory", "sourceId": "igod",
                    "publicationScope": "local_development", "reviewStatus": "DISCOVERED", "sourceRetrievedOn": "2026-10-13",
                    "firstDiscovered": "2026-10-13", "sourceEvidence": [{"sourceId": "igod", "sourcePage": "https://igod.gov.in/sg/MP/E003/organizations", "retrievedAt": "2026-10-13", "originalUrl": "https://example.gov.in/", "preview": False, "category": "Departments"}],
                    "destinationHealth": {"status": "RESPONDING", "httpStatus": 200, "checkedAt": "2026-10-13", "finalUrl": "https://example.gov.in/", "robotsChecks": [{"resolvedRules": True, "allowed": True}]}}
        self.catalog = {"version": 1, "categories": [{"id": "information"}], "websites": []}

    def test_local_listing_publication_has_explicit_non_manual_status(self):
        # Unit-isolated policy test; live snapshot integrity is separately checked
        # during the real pipeline run, never inferred from this mocked check.
        with patch("catalog_pipeline.verify_source_snapshot"):
            queue, approvals, counts = stage_discovery([self.row], self.catalog, {"records": []})
            updated, remaining = publish(queue, self.catalog, approvals)
        self.assertEqual(1, len(updated["websites"]))
        self.assertEqual("directory_listed", updated["websites"][0]["verificationStatus"])
        self.assertEqual("", updated["websites"][0]["verifiedOn"])
        self.assertEqual([], remaining["records"])

    def test_asserted_source_url_without_retained_evidence_cannot_publish(self):
        with self.assertRaises(ValueError):
            validate_directory_eligibility(self.row)

    def test_www_variant_is_retained_for_review_not_silently_overwritten(self):
        variant = copy.deepcopy(self.row)
        variant.update(id="www-variant", url="https://www.example.gov.in/")
        variant["sourceEvidence"][0]["originalUrl"] = variant["url"]
        variant["destinationHealth"]["finalUrl"] = variant["url"]
        with patch("catalog_pipeline.verify_source_snapshot"):
            queue, approvals, _ = stage_discovery([self.row, variant], self.catalog, {"records": []})
            self.assertEqual(2, len(queue["records"]))
            updated, remaining = publish(queue, self.catalog, approvals)
        self.assertEqual(1, len(updated["websites"]))
        self.assertEqual(variant["url"], remaining["records"][0]["record"]["url"])
        self.assertEqual("NEEDS_URL_REVIEW", remaining["records"][0]["status"])

    def test_session_bound_local_listing_returns_to_review_with_prior_evidence(self):
        with patch("catalog_pipeline.verify_source_snapshot"):
            queue, approvals, _ = stage_discovery([copy.deepcopy(self.row)], self.catalog, {"records": []})
            bundled, remaining = publish(queue, self.catalog, approvals)
        changed = {**self.row, "healthStatus": "URL_REVIEW", "destinationHealth": {"status": "URL_REVIEW", "reason": "Session-bound final URL"}}
        held, approvals, counts = stage_discovery([changed], bundled, remaining)
        self.assertEqual([], bundled["websites"])
        self.assertEqual("NEEDS_URL_REVIEW", held["records"][0]["status"])
        self.assertEqual("directory_listed", held["records"][0]["priorPublishedRecord"]["verificationStatus"])
        self.assertEqual(1, counts["localHeldForReview"])
        self.assertEqual({}, approvals)

    def test_incremental_refresh_updates_only_directory_data_and_preserves_history(self):
        with patch("catalog_pipeline.verify_source_snapshot"):
            queue, approvals, _ = stage_discovery([copy.deepcopy(self.row)], self.catalog, {"records": []})
            first, remaining = publish(queue, self.catalog, approvals)
            history = list(first["websites"][0]["reviewHistory"])
            changed = {**self.row, "description": "Updated source description"}
            _, approvals, counts = stage_discovery([changed], first, remaining)
        self.assertEqual({}, approvals)
        self.assertEqual(1, counts["refreshedLocalListings"])
        self.assertEqual("Updated source description", first["websites"][0]["description"])
        self.assertEqual(history, first["websites"][0]["reviewHistory"][:-1])

    def test_ambiguous_external_blocked_and_inconclusive_health_are_ineligible(self):
        for changed in ({"url": "https://vendor.example/"}, {"reviewStatus": "NEEDS_OWNERSHIP_REVIEW"}, {"destinationHealth": {"status": "NETWORK_ERROR"}}, {"publicationScope": "public"}):
            with self.assertRaises((ValueError, KeyError, TypeError)):
                validate_directory_eligibility({**self.row, **changed})

    def test_safe_rerun_preserves_curated_records_and_rejections(self):
        curated = {**self.row, "description": "Curated description", "verificationStatus": "verified"}
        old = copy.deepcopy(curated)
        queue, approvals, counts = stage_discovery([self.row], {**self.catalog, "websites": [curated]}, {"records": []})
        self.assertEqual(old, curated)
        self.assertEqual({}, approvals)
        rejected = {"records": [{"record": self.row, "collectionManaged": True, "status": "REJECTED", "reviewHistory": [{"status": "REJECTED", "reason": "editor"}]}]}
        queue, approvals, _ = stage_discovery([self.row], self.catalog, rejected)
        self.assertEqual("REJECTED", queue["records"][0]["status"])
        self.assertEqual("editor", queue["records"][0]["reviewHistory"][0]["reason"])
        self.assertEqual({}, approvals)


class CompletenessTest(unittest.TestCase):
    def test_fresh_scoped_dry_run_works_without_requests_or_writes(self):
        from argparse import Namespace
        from discover_government_sites import collect
        import tempfile
        with tempfile.TemporaryDirectory() as directory:
            args = Namespace(source="igod", batch="mp-scoped", state="MP", category=None, dry_run=True)
            with patch("discover_government_sites.COLLECTION", Path(directory)), patch("discover_government_sites.CachedClient") as client:
                collect(args)
                client.assert_not_called()
                self.assertEqual([], list(Path(directory).iterdir()))

    def test_existing_national_batch_cannot_be_reused_for_a_state(self):
        from argparse import Namespace
        from discover_government_sites import collect
        import tempfile
        with tempfile.TemporaryDirectory() as directory:
            batch = Path(directory) / "national"
            batch.mkdir()
            (batch / "manifest.json").write_text(json.dumps({"pages": {}, "scope": {"state": None, "category": None}}))
            args = Namespace(source="igod", batch="national", state="MP", category=None, dry_run=True)
            with patch("discover_government_sites.COLLECTION", Path(directory)), patch("discover_government_sites.CachedClient") as client:
                with self.assertRaises(ValueError):
                    collect(args)
                client.assert_not_called()

    def test_missing_pagination_and_failure_do_not_mark_category_complete(self):
        url = "https://igod.gov.in/ug/E002/organizations"
        more = "https://igod.gov.in/ug/E002/organizations_list_more/25/5?keyword="
        first = {"url": url, "status": "PROCESSED", "expectedResults": 30, "entries": [{"originalName": str(i), "originalUrl": None} for i in range(25)], "pagination": [{"url": more}]}
        continuation = {"url": more, "listingPage": url, "status": "PENDING"}
        manifest = {"pages": {url: first, more: continuation}}
        matrix = category_matrix(manifest)
        self.assertFalse(matrix[0]["complete"])
        self.assertEqual(5, matrix[0]["gap"])
        continuation.update(status="FAILED", error="HTTP 403")
        self.assertFalse(category_matrix(manifest)[0]["complete"])
        continuation.update(status="PROCESSED", entries=[{"originalName": str(i), "originalUrl": None} for i in range(25,30)])
        self.assertTrue(category_matrix(manifest)[0]["complete"])

    def test_state_home_only_is_not_full_state_directory_coverage(self):
        url = "https://igod.gov.in/sg/MP/categories"
        manifest = {"stateBranches": {"MP": {"name": "Madhya Pradesh", "url": url}}, "pages": {url: {"url": url, "status": "PROCESSED"}}}
        row = state_coverage(manifest, [], {"websites": []}, [])[0]
        self.assertTrue(row["branchEnumerated"])
        self.assertFalse(row["branchesComplete"])


if __name__ == "__main__":
    unittest.main()
