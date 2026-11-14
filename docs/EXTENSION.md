# JanSetu Companion browser extension

This is an optional Manifest V3 Chrome side panel. It gives a locally loaded search, verified portal shortcuts, and a source-backed guide when the active tab matches a listed official domain. **It cannot change any website:** it has no `scripting` permission, no content scripts, no host permissions and no page stylesheet. The NSP "Readable text" pilot was withdrawn on 18 October 2026 and removed in Phase 17 ([details](PHASE17_AUTHORISED_CORRECTIONS.md)). The extension does not read form contents or capture credentials.

## Try it in Chrome

1. From the repository root, run `python tools/check_catalog.py` and `python tools/package_extension.py` to generate the same compact runtime projection in `extension/catalog.json` (the full research catalog remains in Android source assets).
2. Open `chrome://extensions` in Chrome, turn on **Developer mode**, choose **Load unpacked**, and select this repository's `extension` folder.
3. Select the JanSetu Companion toolbar icon. The side panel opens. Search for a portal, or open a listed official portal and select the icon to see its summary and guide.

For a quick visual preview without installing the extension, open [extension-panel.png](screenshots/extension-panel.png), [extension-guide.png](screenshots/extension-guide.png), or [extension-readability.png](screenshots/extension-readability.png). The preview test runs `python tools/smoke_extension.py` (requires Python Playwright and Chrome); it stubs Chrome's extension APIs and does not verify toolbar or active-tab permission behavior on an installed extension.

Historical: the readability pilot was limited to `https://scholarships.gov.in/home`. It was withdrawn because it showed no measured benefit, and it was removed from the extension in Phase 17. Its stylesheet is archived as evidence at `data/retired/nsp-readable-text.css`.

The [original](screenshots/national-scholarship-portal-original.png) and [readable](screenshots/national-scholarship-portal-readable.png) screenshots are a headless browser preview of that page, taken after its initial content loaded. The visual change is modest by design; the app continues to show its own clearer directory and guides separately.

Chrome's [activeTab permission](https://developer.chrome.com/docs/extensions/develop/concepts/activeTab) grants temporary access after a user gesture. Its [scripting API](https://developer.chrome.com/docs/extensions/reference/api/scripting) supports inserting and removing a matching CSS file. These APIs do not bypass the official site's server delay.

## Permissions and data

- `sidePanel` displays JanSetu beside the current page.
- `activeTab` allows a temporary read of the current tab's URL after the user selects the toolbar icon, so the panel can match a host in the source-attributed local catalog. There is no broad host permission, background browsing history collection, content script, or extension storage.
- `scripting` was removed in Phase 17. The extension cannot insert CSS or JavaScript into any page.
- The catalog and guides are packaged locally. Opening an official link sends a normal browser request to that site. JanSetu has no account, analytics, or backend.
- The panel links to its local privacy-policy copy. Run `python tools/package_extension.py` after changing the Android catalog or policy so both extension copies stay synchronized.

The side panel matches listed official hosts while treating `www.` as equivalent; the readability control requires the reviewed host exactly. A lookalike suffix such as `digilocker.gov.in.evil.example` does not match. Search results include manually reviewed HTTPS records and explicitly attributed, evidence-gated local IGOD listings. The panel distinguishes their status and dates and renders 30 results at a time. NIC reproduction permission is pending before public distribution. The extension is an unpacked development build. The controls passed logic and mocked-browser tests, and a headless Chrome preview captured the live scholarship home page before and after the stylesheet. Actual CSS injection through an installed extension and its interaction with live site navigation remain unverified. It has not been submitted to the Chrome Web Store.


## Packaging note (Phase 7)

`tools/package_extension.py` writes the compact catalogue without `websiteProfiles`. The extension never shows website profiles, and profile claims derive from official-page text whose reuse is still undecided (release blocker RB-02). `tools/test_review_reconciliation.py` fails if profiles, quotations or review material appear in the extension folder.
