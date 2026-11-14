import copy
import unittest

from inventory_audit import read, validate


class InventoryAuditTest(unittest.TestCase):
    def setUp(self):
        self.catalog = read("app/src/main/assets/catalog.json")
        self.queue = read("data/review_queue.json")
        self.taxonomy = read("data/taxonomy.json")
        self.profiles = read("data/portal_profiles.json")
        self.audits = read("data/page_audits.json")
        self.sources = read("data/source_registry.json")

    def check(self, **overrides):
        values = dict(catalog=self.catalog, queue=self.queue, taxonomy=self.taxonomy,
                      profiles=self.profiles, audits=self.audits, sources=self.sources)
        values.update(overrides)
        return validate(**values)

    def test_real_editorial_data_has_traceable_classes(self):
        result = self.check()
        self.assertEqual(len(self.catalog["websites"]), result["published"])
        self.assertEqual(len(self.queue["records"]), result["candidates"])
        self.assertEqual(9, sum(r['verificationStatus'] == 'verified' for r in self.catalog['websites']))
        self.assertEqual(len(self.audits["pages"]), result["pages"])
        self.assertEqual(18, result["pages"])

    def test_duplicate_candidate_cannot_hide_in_review(self):
        queue = copy.deepcopy(self.queue)
        queue["records"][0]["record"]["url"] = self.catalog["websites"][0]["url"]
        with self.assertRaisesRegex(AssertionError, "Duplicate candidate URL"):
            self.check(queue=queue)

    def test_classification_requires_evidence(self):
        audits = copy.deepcopy(self.audits)
        audits["pages"][0]["intervention"] = "A"
        audits["pages"][0]["evidence"] = []
        with self.assertRaisesRegex(AssertionError, "lacks evidence"):
            self.check(audits=audits)

    def test_taxonomy_can_extend_without_code_change(self):
        taxonomy = copy.deepcopy(self.taxonomy)
        profiles = copy.deepcopy(self.profiles)
        taxonomy["sectors"].append("marine_services")
        profiles["india-portal"]["secondarySectors"].append("marine_services")
        self.check(taxonomy=taxonomy, profiles=profiles)


if __name__ == "__main__":
    unittest.main()
