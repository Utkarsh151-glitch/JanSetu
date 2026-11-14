"""Copy the validated Android catalog into the unpacked Chrome extension."""
import json
import html
import shutil
from package_catalog import compact_catalog, encode
from service_catalog import merged_catalog
from pathlib import Path

from check_catalog import main as validate_catalog

root = Path(__file__).resolve().parents[1]
source = root / "app/src/main/assets/catalog.json"
destination = root / "extension/catalog.json"
catalog = merged_catalog()
validate_catalog()
# The extension never shows website profiles. They are derived from official-page text, whose reuse is still
# undecided (release blocker RB-02), so they stay out of the extension package.
extension_catalog = {key: value for key, value in compact_catalog(catalog).items() if key != "websiteProfiles"}
destination.write_bytes(encode(extension_catalog))
policy = (root / "app/src/main/assets/privacy.txt").read_text(encoding="utf-8")
(root / "extension/privacy.html").write_text(
    '<!doctype html><html lang="en"><head><meta charset="utf-8">'
    '<meta name="viewport" content="width=device-width, initial-scale=1">'
    '<title>JanSetu Privacy Policy</title><style>body{font:16px/1.6 system-ui,sans-serif;'
    'max-width:760px;margin:32px auto;padding:0 20px;color:#172824;background:#f8faf8}'
    'pre{font:inherit;white-space:pre-wrap}</style></head><body><pre>'
    + html.escape(policy) + '</pre></body></html>', encoding="utf-8")
print(f"Packaged {len(catalog['websites'])} attributed portal entries in {destination}")
