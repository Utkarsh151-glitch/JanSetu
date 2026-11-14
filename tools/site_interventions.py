"""Validate page-specific JanSetu Assist decisions against the real allowlists.

Usage: python tools/site_interventions.py

data/site_interventions.json records one decision per exact public page. This
check keeps three things in step:

* the Chrome extension's styling capability: any readablePaths allowlist in extension/core.mjs, any route check
  in extension/background.js, and the manifest's permissions (no `scripting`, page stylesheet or content script
  may exist without a live, reviewed styling decision);
* the Android in-app browser hosts (catalog websites with browserSupport=in_app);
* the reviewed page records in data/page_audits.json.

It never edits catalogs, allowlists or service facts.
"""
from __future__ import annotations

import json
import re
from pathlib import Path
from urllib.parse import urlsplit

ROOT = Path(__file__).resolve().parents[1]
CLASSES = set("ABCDEF") | {"NEEDS_MANUAL_REVIEW"}
STATUSES = {"recommendation", "investigating", "pilot", "approved", "retired"}
STATES = {"active", "proposed", "recommended_withdrawal", "retired"}
CONTEXTS = {"android_inapp", "chrome_extension", "jansetu_native"}
STYLING = {"readable_text_css", "overflow_scroll_css", "reading_view"}
CSS_PROPERTIES = {"font-size", "line-height", "letter-spacing", "text-decoration-thickness",
                  "text-underline-offset", "outline", "outline-offset", "overflow-x"}


def read(name: str):
    path = ROOT / name
    return json.loads(path.read_text(encoding="utf-8")) if name.endswith(".json") else path.read_text(encoding="utf-8")


def exact(url: str) -> tuple[str, str, str]:
    parts = urlsplit(url)
    assert parts.scheme == "https", f"Not HTTPS: {url}"
    assert parts.hostname and not parts.username and not parts.password and parts.port in (None, 443), f"Unsafe URL: {url}"
    assert not parts.fragment, f"Fragment is not part of a page identity: {url}"
    return parts.hostname.lower(), parts.path or "/", parts.query


def extension_allowlist(core_js: str, background_js: str, catalog: dict) -> set[tuple[str, str, str]]:
    """Pages the extension can style: readablePaths in core.mjs (absent means none), which must match background.js."""
    sites = {site["id"]: site for site in catalog["websites"]}
    block = re.search(r"const readablePaths = \{(.*?)\n\};", core_js, re.S)
    allowed = set()
    for site_id, paths in re.findall(r"'([a-z0-9-]+)'\s*:\s*new Set\(\[([^\]]*)\]\)", block.group(1) if block else ""):
        host = urlsplit(sites[site_id]["url"]).hostname.lower()
        for path in re.findall(r"'([^']+)'", paths):
            allowed.add((host, path, ""))
    routes = {(h.lower(), p, "") for h, p in re.findall(r"url\.hostname === '([^']+)' &&\s*url\.pathname === '([^']+)'", background_js)}
    assert routes == allowed, f"background.js route check {sorted(routes)} differs from core.mjs allowlist {sorted(allowed)}"
    return allowed


def lint_css(text: str, name: str) -> None:
    body = re.sub(r"/\*.*?\*/", "", text, flags=re.S)
    for selector, declarations in re.findall(r"([^{}]+)\{([^}]*)\}", body):
        selector = selector.strip()
        assert selector.startswith(":where("), f"{name}: selector must use :where() so it never outranks the site: {selector}"
        for declaration in filter(None, (d.strip() for d in declarations.split(";"))):
            prop, value = (part.strip() for part in declaration.split(":", 1))
            assert prop in CSS_PROPERTIES, f"{name}: property not allowed for page assistance: {prop}"
            if prop == "overflow-x":
                assert value.replace("!important", "").strip() == "auto", f"{name}: overflow-x may only become auto"
            if prop in {"outline", "outline-offset"}:
                assert ":focus-visible" in selector, f"{name}: outlines only for keyboard focus"


def extension_capability(manifest: dict, files: list[str]) -> None:
    """Without a live reviewed styling decision the extension may hold no way to change a page."""
    assert "scripting" not in manifest.get("permissions", []), "Extension requests scripting without a live reviewed styling decision"
    for key in ("content_scripts", "host_permissions", "optional_host_permissions"):
        assert not manifest.get(key), f"Extension declares {key} without a live reviewed styling decision"
    assert [f for f in files if f.endswith(".css")] == ["panel.css"], "Only the panel's own stylesheet may be packaged"


def validate(registry: dict, audits: dict, catalog: dict, core_js: str, background_js: str,
             manifest: dict | None = None, extension_files: list[str] | None = None) -> dict:
    audit_ids = {page["pageId"] for page in audits["pages"]}
    audit_urls = {page["pageId"]: page["url"] for page in audits["pages"]}
    controls = registry["controls"]
    seen, styled_extension, inapp_zoom = set(), set(), set()
    counts = {value: 0 for value in CLASSES}
    for page in registry["pages"]:
        page_id = page["pageId"]
        assert page_id not in seen, f"Duplicate page: {page_id}"
        seen.add(page_id)
        assert page_id in audit_ids, f"No page audit record for {page_id}"
        assert page["url"] == audit_urls[page_id], f"Registry and audit URL differ for {page_id}"
        identity = exact(page["url"])
        klass, status = page["class"], page["status"]
        assert klass in CLASSES and status in STATUSES, f"Bad class/status for {page_id}"
        counts[klass] += 1
        if klass != "NEEDS_MANUAL_REVIEW":
            assert page["evidence"], f"Classification lacks evidence: {page_id}"
        for item in page["evidence"]:
            if not item.startswith("https://"):
                assert (ROOT / item).exists(), f"Missing evidence file for {page_id}: {item}"
        altering = set()
        for entry in page["controls"]:
            control, context, state = entry["control"], entry["context"], entry["state"]
            assert control in controls and context in CONTEXTS and state in STATES, f"Unknown control entry on {page_id}: {entry}"
            live = state in {"active", "recommended_withdrawal"}
            if controls[control]["altersPresentation"]:
                altering.add(context)
                assert klass not in {"E", "F"}, f"Class {klass} page cannot have a presentation-altering control: {page_id}"
                if control in STYLING:
                    assert klass in {"C", "D"}, f"Styling needs class C (or an existing pilot under D): {page_id}"
                    if klass == "D":
                        assert state in {"recommended_withdrawal", "retired"}, f"Class D page may only keep a styling pilot pending withdrawal: {page_id}"
                    if live:
                        assert status in {"pilot", "approved"}, f"Live styling needs pilot or approved status: {page_id}"
                    sheet = entry.get("stylesheet")
                    assert sheet and (ROOT / sheet).exists(), f"Styling control needs its stylesheet: {page_id}"
                    lint_css((ROOT / sheet).read_text(encoding="utf-8"), sheet)
                    if live and context == "chrome_extension":
                        styled_extension.add(identity)
                    if not live and state != "retired" and sheet.startswith("extension/"):
                        raise AssertionError(f"Proposed stylesheet must stay outside the extension package: {page_id}")
                if control == "text_zoom":
                    assert klass in {"B", "C"}, f"Text zoom needs class B or C: {page_id}"
                    if live and context == "android_inapp":
                        inapp_zoom.add(identity[0])
        for context in altering:
            assert any(e["control"] == "restore_original" and e["context"] == context for e in page["controls"]), \
                f"Presentation-altering control without restore in {context}: {page_id}"
        if status == "approved":
            review = page.get("review") or {}
            for key in ("rules", "testsPassed", "rollback", "reviewer", "date"):
                assert review.get(key), f"Approved page lacks review.{key}: {page_id}"
            assert review.get("humanReview") is True, f"Approval needs a recorded human review: {page_id}"

    allowlisted = extension_allowlist(core_js, background_js, catalog)
    assert allowlisted == styled_extension, \
        f"Extension allowlist {sorted(allowlisted)} differs from registry live styling {sorted(styled_extension)}"
    if manifest is not None and not styled_extension:
        extension_capability(manifest, sorted(extension_files or []))
    inapp_hosts = {urlsplit(site["url"]).hostname.lower() for site in catalog["websites"] if site.get("browserSupport") == "in_app"}
    assert inapp_hosts == inapp_zoom, f"In-app hosts {sorted(inapp_hosts)} differ from registry text-zoom hosts {sorted(inapp_zoom)}"
    return {"pages": len(seen), "classes": {k: v for k, v in counts.items() if v}, "extensionStyled": sorted(f"{h}{p}" for h, p, _ in allowlisted),
            "inAppHosts": sorted(inapp_hosts)}


def main() -> None:
    result = validate(read("data/site_interventions.json"), read("data/page_audits.json"),
                      read("app/src/main/assets/catalog.json"), read("extension/core.mjs"), read("extension/background.js"),
                      read("extension/manifest.json"), sorted(p.name for p in (ROOT / "extension").iterdir() if p.is_file()))
    print(json.dumps(result, sort_keys=True))


if __name__ == "__main__":
    main()
