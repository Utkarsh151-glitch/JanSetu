import base64
import copy
import json
import unittest
from pathlib import Path
from cryptography.hazmat.primitives.asymmetric import ec
from cryptography.hazmat.primitives import hashes
from cryptography.exceptions import InvalidSignature
from package_catalog import compact_catalog, create_package

class RuntimeCatalogTest(unittest.TestCase):
    def setUp(self):
        self.catalog=json.loads((Path(__file__).resolve().parents[1]/'app/src/main/assets/catalog.json').read_text(encoding='utf-8'))

    def test_projection_preserves_source_and_every_url_permission_and_date(self):
        before=copy.deepcopy(self.catalog);runtime=compact_catalog(self.catalog)
        self.assertEqual(before,self.catalog)
        for old,new in zip(self.catalog['websites'],runtime['websites']):
            for field in ('id','url','sourceUrl','verificationStatus','verifiedOn','browserSupport','browserJavaScript','healthCheckedAt'):
                self.assertEqual(old.get(field),new.get(field))
            if old.get('sourceEvidence'):self.assertEqual(1,len(new['sourceEvidence']))
        self.assertEqual(4,len(runtime['categoryGroups']))

    def test_update_signature_covers_manifest_and_payload_hash(self):
        key=ec.generate_private_key(ec.SECP256R1());package=json.loads(create_package(self.catalog,key))
        manifest=base64.b64decode(package['manifest']);signature=base64.b64decode(package['signature'])
        key.public_key().verify(signature,manifest,ec.ECDSA(hashes.SHA256()))
        with self.assertRaises(InvalidSignature):key.public_key().verify(signature,manifest+b' ',ec.ECDSA(hashes.SHA256()))
        self.assertNotIn('PRIVATE KEY',json.dumps(package))

if __name__=='__main__':unittest.main()
