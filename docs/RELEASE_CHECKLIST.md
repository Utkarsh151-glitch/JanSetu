# Release checklist — 16 October 2026

This is a release preparation checklist, not a claim of publication.

## Build and identity

- [x] Package `in.jansetu.app`, label JanSetu, minimum API 26, target API 36, version 0.4.0/code 4 configured.
- [x] Phase 3B debug APK built locally. Unsigned release APK/AAB are the earlier Phase 3A artifacts; rebuild the release artifacts after final QA/signing. Release signing accepts environment variables only when all four are provided.
- [ ] Choose and securely back up a production upload key. Set `JANSETU_KEYSTORE`, `JANSETU_STORE_PASSWORD`, `JANSETU_KEY_ALIAS`, and `JANSETU_KEY_PASSWORD` in the build environment; never commit them. Build `./gradlew.bat :app:bundleRelease :app:assembleRelease`, then verify the signed artifacts.
- [ ] Final app icon and store graphics reviewed for original branding and no government endorsement claim.

## Content and behavior

- [x] 7,347 local records pass catalog validation: nine manually reviewed and 7,338 directory-listed entries; 25 separately reviewed service-information records and two dated portal guides. Unknowns and route limitations remain explicit; the raw website source is unchanged. All runtime IDs/URLs/permissions are preserved by compact packaging.
- [ ] Resolve 3,823 queued candidates through the evidence gate as evidence permits; expand stable-ID and service coverage only when authoritative sources support it.
- [ ] Recheck every published destination and guide source before release, including redirects and freshness of factual guidance. A one-time HTTP probe of nine portal URLs passed; it does not cover the source URLs or guide content.
- [ ] Verify offline behavior, large font, TalkBack, dark mode, and narrow/large screens on representative devices.
- [ ] Exercise WebView TLS failure, cross-host navigation, authentication/payment handoff, downloads, lifecycle, and network failure. Keep unverified sites in the external browser.
- [ ] Install the unpacked Chrome extension in a real Chrome profile and check toolbar launch, active-tab match, side-panel layout, and that the permission prompt lists only the side panel and active tab (no page changes since Phase 17). The automated panel preview does not cover those behaviors.

- [x] Signed manual Android catalog import, atomic replacement, tamper/replay rejection, offline repository persistence and bundle recovery implemented and tested. See latest QA for document-picker checks.
- [ ] Production update signing key custody, trust-pin rotation and delivery/revocation policy defined before any public channel. Current key is development only.

- [ ] Recheck local tracker backup exclusions, notification permission/denial, reboot/timezone/battery behavior, deletion, reference masking and privacy declarations on representative physical devices.

## Privacy and distribution

- [ ] Obtain required directory reuse permission, resolve public evidence handling and pass `python tools/check_catalog.py --public-release`; it currently blocks distribution.
- [x] Bundled privacy policy and support email use Utkarsh / utkarshvaibhav268@gmail.com; settings and portal actions open an email draft.
- [ ] Publish the exact privacy policy at a stable public URL, complete Play Data safety declarations, review the terms draft, and make a publisher decision on support-message retention.
- [ ] Complete Play listing, screenshots, app access/content-rating/target-audience/government declarations, and any required closed test in the actual Play Console.
- [ ] Sign and upload the AAB, review Play pre-launch results, and explicitly authorize production publication.

## Build commands

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug :app:connectedDebugAndroidTest --console=plain
.\gradlew.bat :app:bundleRelease :app:assembleRelease --console=plain
python -m unittest discover -s tools -p 'test_*.py'
python tools/check_catalog.py
python tools/audit_links.py
python tools/catalog_pipeline.py report
python tools/package_extension.py
node --test extension/core.test.mjs
python tools/smoke_extension.py
```

There is no backend, database, migration, admin account, or production endpoint to deploy. Catalog publication is a reviewed local file change followed by signed manual Android update delivery; the extension still requires repackaging/reloading. Local editorial access uses the OS account and key files, not hosted admin authentication. See [local operations](DEPLOYMENT.md) and [compatibility](COMPATIBILITY.md). See [data verification](DATA_VERIFICATION.md), [browser security](WEBVIEW_SECURITY.md), [known limitations](KNOWN_LIMITATIONS.md), and [QA results](QA_REPORT.md).
