# JanSetu execution log

A record of the work, newest last. Each entry says what was done, which commands ran with what result, and what is next. Statuses are in [`data/project_execution_state.json`](../data/project_execution_state.json).

## 18 October 2026: start of the master execution (Phase 7 onwards)

- Re-inspected the repository. It is not a git repository; there is no backend, server, account or analytics code.
  - App code: 26 Kotlin source files (about 5,000 lines).
  - Tools: Python tools under `tools/`.
  - A separate Chrome extension.
- Re-measured the baseline. The figures match the Phase 6D report; see the roadmap.
  - All 8 protected source files match the Phase 4 baseline.
  - `reviews.json` has 0 decisions.
- Created the roadmap, release blocker register, execution state file and this log.
- Phase 7 design chosen after reading the tracker storage, which is reused as a pattern only:
  - **Separate storage and key.** A new Keystore key (`jansetu_vault_v1`) and a new no-backup directory (`no_backup/vault/`). Nothing is shared with the tracker database or its key.
  - **Content encryption.** AES-256-GCM in 64 KiB segments with a random per-document key. The per-document key is wrapped by the Keystore key and tied to the document's ID. This allows streaming import and random-access reading, so no plaintext temporary file is needed.
  - **Viewing.** Through `StorageManager.openProxyFileDescriptor`: PDFs with `PdfRenderer`, images decoded in memory.
  - **File picker.** A system file-picker provider that is off by default. When on, it is read-only, gives time-limited document IDs, and can be revoked by rotating its key. "Export a copy" is always available as the fallback.

## 18 October 2026: Phase 7 implementation

- **Implemented** the `vault` package, the document screens and navigation, the Home and Settings entries, and "Prepare documents" on service pages. See [PHASE7_DOCUMENT_VAULT.md](PHASE7_DOCUMENT_VAULT.md).
- **Privacy policy:** a My Documents section added to the English and Hindi versions (dated 18 October 2026) and to `docs/PRIVACY.md`. The extension's privacy page was regenerated.
- **Problems found and fixed:**
  1. A unit test expected a removed bidirectional-override character to become a space. It is removed, and the test now says so.
  2. Lint raised 1 error (casting `LocalContext` to `Activity`) and 5 new warnings in the new code. All were fixed, so lint is back to the 14 inherited warnings.
  3. In the real system picker (DocumentsUI on API 37), tapping a grid item opens a preview. The integration test switches to list view and taps the file name, as a person would.
  4. Re-running `package_extension.py` (to refresh the privacy page) put `websiteProfiles` into `extension/catalog.json`, because the compact catalogue has carried profiles since Phase 6B. The Phase 6D release-exclusion test caught it in the first gate attempt (archived as `phase7-final-attempt1`). `package_extension.py` now leaves profiles out; the extension never used them, and their reuse is blocker RB-02.
  5. The quota label showed "500.0 MB"; it now shows "500 MB".
- **Emulator hang (third occurrence).** It happened during manual exploration of DocumentsUI. The emulator was restarted with its original no-snapshot flags and the preview hashes were identical afterwards: [record](../data/validation_reports/phase7-emulator-restart.json). Only the isolated QA package's data was cleared.
- **Visual review.** The vault screens were captured through a temporary test (since deleted) that renders them without the window protection. Reviewed: the list, the file-picker card, the document page with preview, the edit dialog, the preparation checklist, the service page button, the Home card and dark theme. The only defect found was the "500.0 MB" label above. In the dark capture, the light page behind dark content came from rendering outside the app's Scaffold, not from the app.

## 18 October 2026: Phase 7 gate, then Phases 8–16

- **Phase 7 gate `phase7-final`: passed.** Python 139, extension 13, JVM 76, API 37 68; lint 0 errors and 14 warnings; preview and protected data unchanged. The archived `phase7-final-attempt1` failed on the extension-catalogue leak described above.
- **Phase 8 (`NEEDS_REVIEW`).** The decision record concludes that no current feature needs an account. Six options were evaluated: the recommendation is a passkey-first account with E2EE sync, and only if the owner wants sync. Guard tests added (`test_account_boundary.py`).
- **Phase 9 (`NEEDS_REVIEW`).** The decision is to retain local-only storage, with an E2EE sync and recovery design and its test requirements. No sync UI exists.
- **Phase 10.**
  - **Added:** preparation summary on the application page, a warning when requirements change, "Add to My Applications" from the checklist (confirmed routes only, so the Phase 3B rule is kept), and the application export (`ApplicationExport`). The privacy policy no longer says "no export".
  - **Tests:** export (JVM, 2); `TrackerPreparationTest` (3); `PreparationJourneyTest` (1), which walks through the real navigation.
  - **Gate `phase10-final`: passed.** Python 143, extension 13, JVM 78, API 37 72; lint 0 errors and 14 warnings; preview and protected data unchanged.
- **Phase 11.** Coverage re-measured; status and backlog written. No new capture, because human review is the bottleneck.
- **Phase 12.**
  - National Portal browser: text zoom starts from the Android font size, with A and A+ controls and the size announced. The reading position is saved across recreation (`BrowserTextSizeTest` 3, `BrowserReadingTest` 2).
  - NSP pilot withdrawn: allowlist emptied; registry set to retired; the validator allows only a retired pilot's inert background route; extension, validator and policy updated. Removing the protected extension files waits for the owner (RB-14, RB-15).
  - The RTI card is deferred: it needs evidence for two missing rules, a protected-file change and a reviewer decision.
- **Phase 13.** `Phase13AccessibilityChecksTest` runs ATF on the new screens (light, dark and 2× text). Its first run timed out because, at 2× text, the document cards were below the first screen; it now waits on the always-visible Add button. Beta-readiness plan and manual checklist written.
- **Phase 14.** Engineering security and privacy review written. No critical findings. Medium: no in-app lock yet. Legal and permission evidence register recorded.
- **Phase 15.**
  - Added `tools/inspect_release_artifact.py` (aapt2 manifest dump plus zip and content checks) and its regression test.
  - Built and inspected a fresh unsigned release APK: it passes. The first inspection flagged ART baseline-profile assets; these are now allowed by exact name.
  - Release-blocked report and reduced-scope options written; nothing was built or submitted for distribution.
- **Phase 16.** Operations runbook and `tools/stale_evidence.py` (with tests) written.
- **Gate `phase12-final`: passed** (after an emulator system-process crash archived as `phase12-final-attempt1`). Python 148, extension 13, JVM 81, API 37 75; lint 0 errors and 14 warnings, none new; preview and protected data unchanged.
- **End of this run.** Decisions waiting for the owner are listed in `data/project_execution_state.json` (`ownerDecisionsNeeded`).
