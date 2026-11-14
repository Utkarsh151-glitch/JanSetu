"""Compact runtime assets and signed local catalog updates (no backend).

python tools/package_catalog.py assets --output app/build/generated/runtimeAssets
python tools/package_catalog.py keygen
python tools/package_catalog.py update --version 9 --output artifacts/catalog-v9.jansetu
"""
import argparse
import base64
import hashlib
import json
import shutil
from datetime import datetime, timezone
from pathlib import Path
from service_catalog import merged_catalog

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'app/src/main/assets'
WEBSITE_FIELDS = set('id title description url organization level categories keywords sourceUrl sourceType verificationStatus verifiedOn parentOrganization jurisdiction state languages useCases knownLimitations browserSupport browserJavaScript aliases district primarySector secondarySectors sourceId sourceRetrievedOn healthStatus healthCheckedAt latestHealthStatus latestHealthCheckedAt observedFinalUrl publicationScope ownershipConfidence sourceAuthority publicationEligibility'.split())


def website_profiles(catalog):
    """Published, validated profiles only; drafts, held profiles, held fields, quotes and reviewer details never reach the app.
    Evidence must still match its pinned snapshots, and human review comes only from reviews.json."""
    from website_enrichment import CLAIMS, load_snapshots, runtime_profiles, validate
    from editorial_review import INDEX, check_index, check_revisions, load_reviews
    claims=json.loads(CLAIMS.read_text(encoding='utf-8'))
    validate(claims,catalog,catalog.get('services',[]),load_snapshots())
    check_index(claims,json.loads(INDEX.read_text(encoding='utf-8')))
    reviews=load_reviews()
    check_revisions(claims,reviews)
    return runtime_profiles(claims,reviews)


def compact_catalog(catalog):
    translations=json.loads((ROOT/'data/localizations/hi.json').read_text(encoding='utf-8'))
    groups=json.loads((ROOT/'data/category_groups.json').read_text(encoding='utf-8'))
    sites = []
    for original in catalog['websites']:
        row = {k:v for k,v in original.items() if k in WEBSITE_FIELDS}
        if row['id'] in translations['descriptions']:row['descriptionHi']=translations['descriptions'][row['id']]
        evidence = original.get('sourceEvidence', [])
        if evidence:
            preferred = next((e for e in evidence if not e.get('preview', True)), evidence[0])
            row['sourceEvidence'] = [{k:preferred[k] for k in ('sourceId','sourcePage','originalUrl','retrievedAt')}]
        sites.append(row)
    parents={category:g['id'] for g in groups for category in g['categories']}
    return {**catalog, 'websites':sites,
        'categories':[{**c,'parentId':parents[c['id']]} for c in catalog['categories']],
        'categoryGroups':[{k:g[k] for k in ('id','title','titleHi')} for g in groups],
        'guides':[{**g,**translations['guides'].get(g['websiteId'],{})} for g in catalog.get('guides',[])],
        'websiteProfiles':website_profiles(catalog)}


def encode(value):
    return json.dumps(value,ensure_ascii=False,separators=(',',':'),sort_keys=True).encode('utf-8')


def create_package(catalog, key):
    from cryptography.hazmat.primitives import hashes
    from cryptography.hazmat.primitives.asymmetric import ec
    payload = encode(compact_catalog(catalog))
    if len(payload)>16_000_000:
        raise ValueError('Catalog exceeds local update size limit')
    manifest=encode({'format':1,'scope':'local_development','version':catalog['version'],
        'websites':len(catalog['websites']),'sha256':hashlib.sha256(payload).hexdigest(),
        'bytes':len(payload),'generatedAt':datetime.now(timezone.utc).isoformat(timespec='seconds')})
    signature=key.sign(manifest,ec.ECDSA(hashes.SHA256()))
    return encode({k:base64.b64encode(v).decode('ascii') for k,v in
                   (('manifest',manifest),('catalog',payload),('signature',signature))})


def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('command',choices=('assets','keygen','update'))
    parser.add_argument('--output',type=Path)
    parser.add_argument('--version',type=int)
    parser.add_argument('--key',type=Path,default=ROOT/'.local/catalog-update-private.pem')
    args=parser.parse_args()
    if args.command=='keygen':
        from cryptography.hazmat.primitives import serialization
        from cryptography.hazmat.primitives.asymmetric import ec
        public_path=ASSETS/'catalog-update-public.der'
        if args.key.exists():
            key=serialization.load_pem_private_key(args.key.read_bytes(),None)
        elif public_path.exists():
            raise ValueError('Pinned public key already exists; private key required. Do not silently rotate trust.')
        else:
            key=ec.generate_private_key(ec.SECP256R1())
            args.key.parent.mkdir(parents=True,exist_ok=True)
            with args.key.open('xb') as stream:
                stream.write(key.private_bytes(serialization.Encoding.PEM,serialization.PrivateFormat.PKCS8,serialization.NoEncryption()))
        public=key.public_key().public_bytes(serialization.Encoding.DER,serialization.PublicFormat.SubjectPublicKeyInfo)
        if public_path.exists() and public_path.read_bytes()!=public:
            raise ValueError('Private key does not match pinned update public key')
        public_path.write_bytes(public)
        print(json.dumps({'publicKeySha256':hashlib.sha256(public).hexdigest(),'privateKey':'local ignored file; never packaged'}))
        return
    catalog=merged_catalog()
    if args.command=='assets':
        if args.output is None:parser.error('assets requires --output')
        if not args.output.resolve().is_relative_to((ROOT/'app/build').resolve()):
            raise ValueError('Generated runtime assets must stay inside app/build')
        args.output.mkdir(parents=True,exist_ok=True)
        names={p.name for p in ASSETS.iterdir() if p.is_file()}
        for stale in args.output.iterdir():
            if stale.is_file() and stale.name not in names:stale.unlink()
        for file in ASSETS.iterdir():
            if file.is_file() and file.name!='catalog.json':shutil.copy2(file,args.output/file.name)
        payload=encode(compact_catalog(catalog))
        (args.output/'catalog.json').write_bytes(payload)
        print(json.dumps({'runtimeBytes':len(payload),'sourceBytes':(ASSETS/'catalog.json').stat().st_size,'websites':len(catalog['websites'])}))
    else:
        from cryptography.hazmat.primitives import serialization
        if args.output is None or args.version is None:parser.error('update requires --output and --version')
        if args.output.suffix!='.jansetu':raise ValueError('Update output must use .jansetu extension')
        if args.version<=catalog['version']:raise ValueError('Update version must be newer than bundled version')
        from check_catalog import main as validate
        validate()
        key=serialization.load_pem_private_key(args.key.read_bytes(),None)
        public=key.public_key().public_bytes(serialization.Encoding.DER,serialization.PublicFormat.SubjectPublicKeyInfo)
        if public!=(ASSETS/'catalog-update-public.der').read_bytes():raise ValueError('Untrusted signing key')
        catalog['version']=args.version
        args.output.parent.mkdir(parents=True,exist_ok=True)
        package=create_package(catalog,key)
        args.output.write_bytes(package)
        print(json.dumps({'file':str(args.output),'version':args.version,'packageBytes':len(package),'scope':'local_development'}))


if __name__=='__main__':main()
