import unittest
from urllib.request import Request

from audit_links import ConservativeRedirect, ReviewRedirect, same_host


class LinkAuditTest(unittest.TestCase):
    def test_host_match_allows_www_only(self):
        self.assertTrue(same_host("https://www.india.gov.in/a", "https://india.gov.in/b"))
        self.assertFalse(same_host("https://india.gov.in/", "https://india.gov.in.evil.example/"))

    def test_redirects_outside_reviewed_host_stop_before_request(self):
        request = Request("https://www.india.gov.in/")
        handler = ConservativeRedirect()
        for destination in ("http://www.india.gov.in/", "https://other.gov.in/",
                            "https://www.india.gov.in.evil.example/"):
            with self.assertRaises(ReviewRedirect):
                handler.redirect_request(request, None, 302, "Found", {}, destination)


if __name__ == "__main__":
    unittest.main()
