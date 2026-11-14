# QA report

Latest: [Phases 8–16, 18 October 2026](#phases-8-16---18-october-2026). Earlier phase results below are historical checkpoints.

## Phases 8-16 - 18 October 2026

**Overall status: ENGINEERING IN PROGRESS — RELEASE BLOCKED.** See [the release blocker register](JANSETU_RELEASE_BLOCKERS.md) and [the release-blocked report](PHASE15_RELEASE_BLOCKED_REPORT.md).

| Gate | Python | Extension | JVM | API 37 | Lint | Preview and protected data |
| --- | --- | --- | --- | --- | --- | --- |
| `phase10-final` (preparation and tracker) | 143 | 13 | 78 | 72 | 0 errors / 14 warnings | Unchanged |
| `phase12-final` (browser text size, NSP withdrawal, accessibility checks, artifact inspector) | **148** | 13 | **81** | **75** | 0 errors / 14 warnings, none new since Phase 6D | Unchanged |

`phase12-final` archived artifacts: debug `842993e3bc544c95…`, QA `82c72e79d01c4e62…`. The first attempt (`phase12-final-attempt1`) failed because the emulator's system process was killed by its watchdog. Its window manager was blocked for 71 s in a SurfaceFlinger screenshot call that never returned, and two tracker tests failed as a result. The emulator was cold-booted with its original flags. Preview hashes were identical afterwards ([record](../data/validation_reports/phase12-emulator-restart.json)), and the re-run passed.

What these gates add:

- **Phase 8:** guard tests for the local-only boundary: no network client, account SDK, endpoint or secret.
- **Phase 10:** application export (JVM); preparation summary, changed-requirement warning and the checklist's tracker shortcut; the full service → checklist → document → application journey through real navigation.
- **Phase 12:** website text size from the Android font size; A and A+; choices kept across recreation. The NSP pilot withdrawal in the extension tests and the validator.
- **Phase 13:** Accessibility Test Framework checks on My Documents, the document page, Prepare documents, the service page and the Home card, in light and dark and at 2× text.
- **Phase 15:** `inspect_release_artifact.py` and its regression test. A fresh unsigned **release** APK (`e4daf85c02c33735…`) passed `--release` inspection: not debuggable, only the reviewed permissions and exported components, no restricted material.
- **Phase 16:** `stale_evidence.py` and its tests.

Not done (needs people, owner decisions or permissions):
- manual TalkBack, Switch Access and keyboard checks, and physical devices
- the live National Portal check at 1.3× and 2.0×
- the RTI card
- removal of the protected extension files
- signing
- any distribution



## Phase 7 - 18 October 2026

This phase added My Documents: an encrypted local document vault, Prepare documents, an opt-in read-only system file picker and Export a copy. See [PHASE7_DOCUMENT_VAULT.md](PHASE7_DOCUMENT_VAULT.md).

Final gate: `python tools/run_phase5_quality.py phase7-final`, which passed. Results are in [result.json](../data/validation_reports/phase5/phase7-final/result.json). The first attempt is archived as `phase7-final-attempt1`; its failure is described below. Device tests used only `in.jansetu.app.qa`.

| Check | Actual result |
| --- | --- |
| Python | exit 0, **139 passed** (135 + 4 in the new `test_android_manifest.py`) |
| Extension (node) | exit 0, 13 passed |
| `:app:testDebugUnitTest` | exit 0, **76 passed** (54 + 22 vault tests: format, cipher, preparation, picker IDs) |
| `:app:lintDebug` | **0 errors**, 14 warnings (identical to Phase 6D; the new code's 1 error and 5 warnings were fixed) |
| `:app:assembleDebug` | Debug APK `cbecd12adb019515…`; QA APK `b31297f828d18e00…`. Both were inspected after the build: assets are only `catalog.json`, the two privacy policies and the update public key. The release-exclusion test passes on them. |
| `:app:connectedQaAndroidTest` (API 37) | **68 passed** (50 + 18: vault repository 8, provider 4, real system picker 2, UI 4) |
| Protected data | Unchanged (8 files against the Phase 4 baseline) |
| Preview profile | Identical before and after, and after the emulator restart: tracker `d3591936…`, preferences `5ff177e1…`, alarm registry `cb5a631a…` |

Problems found and fixed:

1. **Extension catalogue.** Regenerating the extension's privacy page also put website profiles into `extension/catalog.json`. The Phase 6D release-exclusion test failed the first gate attempt. `package_extension.py` now leaves profiles out.
2. **System picker.** In DocumentsUI on API 37, tapping a grid item opens a preview. The picker test now uses list view and taps the file name.
3. **Lint.** The new code raised 1 error and 5 warnings: a cast to `Activity`, configuration width, `usableSpace`, and missing KTX helpers. All fixed.
4. **Display.** The quota showed "500.0 MB"; it now shows "500 MB".
5. **Emulator hang.** The emulator hung for the third time, during manual DocumentsUI exploration. It was restarted with its original no-snapshot flags, and the preview hashes were identical: [record](../data/validation_reports/phase7-emulator-restart.json).

Not done:
- physical devices (Android 8–14)
- OEM file pickers
- real browser upload fields on live government sites
- spoken TalkBack on the new screens
- a password-protected PDF fixture



## Phase 6D - 18 October 2026

This phase covered review reconciliation, staged corrections and readiness for human review. Human review is deferred by project decision.

**AI-assisted review is not human approval.** No profile has been reviewed by a person, `reviews.json` has 0 decisions, and the catalogue remains blocked from public release.

Reports:

- [reconciliation](PHASE6D_REVIEW_RECONCILIATION_REPORT.md)
- [staged corrections](PHASE6D_STAGED_CORRECTIONS.md)
- [human review queue](PHASE6D_HUMAN_REVIEW_QUEUE.md)
- [manual checklist](PHASE6D_MANUAL_CHECKLIST.md)

Final gate: `python tools/run_phase5_quality.py phase6d-final`, which passed. Results are in [result.json](../data/validation_reports/phase5/phase6d-final/result.json); the gate used only `in.jansetu.app.qa`.

| Check | Actual result |
| --- | --- |
| `python -m unittest discover -s tools -p "test_*.py"` | exit 0, **135 passed** (118 + 17 new in `test_review_reconciliation.py`). Run again after the gate: 135 passed. |
| `node --test extension/core.test.mjs extension/assist.test.mjs` | exit 0, 13 passed |
| `:app:testDebugUnitTest` | exit 0, 54 passed |
| `:app:lintDebug` | 0 errors, 14 warnings (unchanged) |
| `:app:assembleDebug` | Debug APK `1ecab4dcb7b72179…`; QA APK `8876b14f1e0ed659…`. Tests confirm both contain no research files, claims, evidence index, AI advice, staging or packets, and no sentence-length quotes. |
| `:app:connectedQaAndroidTest` (API 37) | **50 passed** |
| `review_reconciliation.py --check` | 157 recommendations, 0 problems |
| `editorial_review.py check` | 193 pins across 72 snapshots match; 0 decisions |
| Protected data | Unchanged (8 files against the Phase 4 baseline) |
| Preview profile | Identical before and after. The baseline taken at the start of the phase matched Phase 6C: preferences `5ff177e1…`. |

Problems found and fixed:

1. Reconciliation found an unsupported sentence ("Each scholarship scheme belongs to its own ministry or body.") in the 18 October NSP operator revision. It was removed with a second recorded revision.
2. `test_editorial_review.py` assumed one revision per profile. It now checks the first entry, which names the digest the AI reviewed.
3. The first release-exclusion test flagged a ministry name that also appears in the base catalogue. The test now looks for sentence-length quotes, which are what the text-reuse restriction protects.
4. The review queue gave held sites the drafts' rule. Held sites now have their own rule.

Not done: any human review, language review, or fresh visits to held or inaccessible sites. All need real people using a normal browser.

## Phase 6C - 18 October 2026

This phase covered editorial validation and priority-batch enrichment:

- an auditable human-review workflow (packet, append-only decisions, evidence pins, staged revisions)
- a second batch of 20 websites: 10 published, 8 drafts, 2 held
- coverage by segment and a release-gate proposal

**No profile has been reviewed by a person**; `reviews.json` has 0 decisions.

Reports:

- [editorial review](PHASE6C_EDITORIAL_REVIEW_REPORT.md)
- [enrichment](PHASE6C_ENRICHMENT_REPORT.md)
- [release gate](PHASE6C_RELEASE_GATE_PROPOSAL.md)
- [manual checklist](PHASE6C_MANUAL_CHECKLIST.md)

Final gate: `python tools/run_phase5_quality.py phase6c-final`, which passed. Results are in [result.json](../data/validation_reports/phase5/phase6c-final/result.json); the gate used only `in.jansetu.app.qa`.

| Check | Actual result |
| --- | --- |
| `python -m unittest discover -s tools -p "test_*.py"` | exit 0, **113 passed** (97 + 16 new in `test_editorial_review.py`; Phase 6B tests now read counts per batch) |
| `node --test extension/core.test.mjs extension/assist.test.mjs` | exit 0, **13 passed** |
| `:app:testDebugUnitTest` | exit 0, **54 passed** (53 + 1 new review-date test; profile expectations updated for 25 profiles) |
| `:app:lintDebug` | **0 errors**, 14 warnings (identical to Phase 6B) |
| `:app:assembleDebug` | debug APK `4ef64d7cd62bb3ebdace2b9349f49e96b6d481c8899f25cc0d571f7209c49b32`; QA APK `bf507923ae53436056ff111dc81419bed8d57dcd98faecd507871195532b33b2`. Both checked: no research files, claims, evidence index or review packets; packaged profiles carry no quotes. |
| `:app:connectedQaAndroidTest` (API 37) | **50 passed** (47 + 3 new `Phase6cReviewDisplayTest`). ATF checks are unchanged and passing. |
| `website_enrichment.py validate` | 38 profiles, 25 published, every quote verbatim |
| `editorial_review.py check` | 185 evidence pins across 67 snapshots match; 0 decisions; no unrecorded revisions |
| Protected data | Unchanged (8 files against the Phase 4 baseline) |
| Preview profile | Identical before and after the gate and both device runs. The phase baseline preferences hash is `5ff177e1…`; it changed from Phase 6B's `78d11267…` through ordinary use of the preview app on the device at 19:22 IST on 17 October, before any Phase 6C device work. |
| Live evidence | 25 + 39 read-only loads (64 snapshots, 42 with text). One local internet outage, with one retry. DoPPW returned HTTP 403 and Gujarat DPPF a captcha; neither was retried or bypassed. |

Problems found and fixed:

1. The emulator hung again (0 CPU, not responding) before a device run. It was restarted with its original no-snapshot flags, and the preview hashes were identical afterwards: [record](../data/validation_reports/phase6c-emulator-restart.json).
2. The first capture pass lost internet partway through. The affected pages were retried once and the reason was recorded.
3. The application-journey rule rejected Nagaland's "How to Apply" link. The link was removed instead of loosening the rule.
4. The evidence check computed snapshot paths relative to the project, so sandbox copies failed. It now takes a root.
5. A test withheld the purpose claim, which correctly drops the whole profile. That case is now tested on its own.
6. Two ranking expectations changed for real reasons and were updated:
   - Odisha's checked grievance portal now ranks with CPGRAMS.
   - Scholarship results include the new checked state portals.

   The compaction test now compares the catalog without profiles.

### Addendum, 18 October: AI pre-review applied

The advisory AI pre-review (`data/website_enrichment/review/ai-prereview-2026-10-18.json`) was applied as follows:

- **Link check.** 29 read-only loads. All held and link-only targets are now loaded, and the page sections behind `#team` and `#contact` links were confirmed.
- **Revisions.** 14 profiles revised with recorded `revisionHistory`. 3 mismatched or sensitive links were withdrawn (Delhi accessibility-only Help, Himachal personal-details form, Odisha Aadhaar+OTP status), and ECI jurisdiction is now held.
- **New evidence.** 8 new evidence pins, 193 in total.
- **Review status.** No human review is recorded or implied.

Gate `python tools/run_phase5_quality.py phase6c-prereview` passed:

| Check | Result |
| --- | --- |
| Python | **118** passed (113 + 5 advisory and revision tests) |
| Node | 13 passed |
| JVM | 54 passed |
| API 37 instrumented | **50** passed |
| Lint | 0 errors, 14 warnings (unchanged) |
| Protected files | Unchanged |
| Preview profile | Identical before and after |
| Debug APK | `d173de46bf389515…`; contains no quotes, advice or revision metadata |

Not done:

- any human review (it needs named reviewers)
- publication decisions on the 8 drafts
- manual TalkBack and device checks
- installation on the preview app
- the permission decisions (NIC data reuse, reuse of official-page text), which remain release blockers

## Phase 6B - 17 October 2026

Catalogue information quality and citizen-first discovery:

- reproducible field-completeness report
- 15 published source-checked website profiles; 3 held
- six-part website page with honest missing states
- checked-information labels, filter and ranking

No catalogue record, review queue, service record, identifier, provenance, update key, browser policy or extension file changed. Reports:

- [catalogue quality](PHASE6B_CATALOGUE_QUALITY_REPORT.md)
- [enrichment batch](PHASE6B_ENRICHMENT_REPORT.md)
- [manual checklist](PHASE6B_MANUAL_CHECKLIST.md)

Final gate: `python tools/run_phase5_quality.py phase6b-final`, which passed. Results are in [result.json](../data/validation_reports/phase5/phase6b-final/result.json); the gate used only `in.jansetu.app.qa`.

| Check | Actual result |
| --- | --- |
| `python -m unittest discover -s tools -p "test_*.py"` | exit 0, **97 passed** (84 earlier + 13 new enrichment/queue/completeness tests) |
| `node --test extension/core.test.mjs extension/assist.test.mjs` | exit 0, **13 passed**, 0 failed, 0 skipped |
| `:app:testDebugUnitTest` | exit 0, **53 passed** (44 + 9 new `WebsiteProfilesTest`) |
| `:app:lintDebug` | **0 errors**, 0 fatal, 14 warnings (identical to the Phase 5 final run; none in new code) |
| `:app:assembleDebug` | debug APK SHA-256 `145858d87438e94b781038bb68db87c7cb976c7f529e7d83c6f72e35fb355996`; QA APK `2a2ad42e2d97792c0a9d9da2aafc0bffb642f3d0ba9a42799902b8219bcddb5a` |
| `:app:connectedQaAndroidTest` (API 37) | **47 passed** (41 + 6 new `Phase6bWebsiteInformationTest`; ATF checks now also cover a checked profile and a directory-only record) |
| `python tools/website_enrichment.py validate` | 18 profiles, 15 published; every quote found verbatim in its saved snapshot |
| `python tools/catalog_completeness.py` | Reproducible; matches the tables in the catalogue quality report |
| Protected data | Unchanged (8 files against the Phase 4 baseline) |
| Preview profile | Identical before and after the gate, the separate device runs and the emulator restart: tracker DB `d3591936…`, preferences `78d11267…`, alarm IDs `cb5a631a…`, no imported catalog |
| Live evidence | 41 + 12 read-only headless Chrome 155 loads with normal TLS validation (53 snapshots, 46 with text). 3 DNS failures retried once, all loaded. UIDAI HTTP 500 twice. National Portal and MSME HTTP 403, not retried. |

Problems found and fixed during the phase:

1. The claims validator crashed on links without their own snapshot (`any(None)`). It now lists every error instead of stopping at the first.
2. The sign-in check matched "registration" in the Udyam host name. It now checks only the path, query, fragment and label, as BrowserPolicy does.
3. Search ranked CPGRAMS below the 30 directory sites with "Grievance" in their title. Records with checked information now get +3 (no result is hidden).
4. The JVM tests ran out of heap at Gradle's 512 MB default. The test heap is now 1 GB, and the new suite parses the catalogue once.
5. Three new test expectations were wrong and were corrected to the real behaviour:
   - the first "grievance" service is the CPGRAMS appeal
   - template counts exclude checked profiles
   - the Sources toggle is exposed as a state description, not text
6. The completeness tool first reported beneficiary groups as unmodelled. 10 services have them, and the report now says so.
7. The emulator hung during the screenshot run (0 CPU, offline, host free RAM 736 MB). It was restarted with its original `-no-snapshot-load -no-snapshot-save` flags after stopping build daemons. The preview hashes were identical afterwards: [record](../data/validation_reports/phase6b-emulator-restart.json).
8. In the screenshot script, swipes over the open keyboard were read as glide-typing. The script now hides the keyboard first.

Screenshots: [docs/screenshots/phase6b/](screenshots/phase6b/) (10 screens, QA app).

Not done:

- human review of any profile (all are `source_checked`)
- manual TalkBack, keyboard and physical-device checks
- installing this build on the preview app
- checking reuse permission for official website text (a release blocker)
- profiles for UIDAI, the National Portal and MSME

## Phase 6A - 17 October 2026

Government-website UX audit and intervention plan. No new styling on any live site, no allowlist or permission change, and no change to catalogs, service facts, review queues or the update key. Report: [PHASE6_SITE_UX_REPORT.md](PHASE6_SITE_UX_REPORT.md). Decisions: [site_interventions.json](../data/site_interventions.json).

| Check | Actual result |
| --- | --- |
| `python -m unittest discover -s tools -p "test_*.py"` | exit 0, **84 passed** (71 earlier + 8 registry-validator + 5 static-fixture browser/lint tests); none skipped |
| `node --test extension/core.test.mjs extension/assist.test.mjs` | exit 0, **13 passed** (7 + 6 new) |
| `:app:testDebugUnitTest` | exit 0, **44 passed** (43 + 1 new BrowserPolicy navigation test) |
| `python inventory_audit.py` / `python site_interventions.py` (in `tools/`) | Pass: 18 audited pages; extension styles exactly `scholarships.gov.in/home`; in-app host exactly `www.india.gov.in` |
| Protected data | Unchanged (8 files against the Phase 4 baseline) |
| Preview profile | Unchanged across all Phase 6A device steps. The preferences file had already changed at 15:44:08 IST before Phase 6A device work, most likely from use of the preview app; see the report. |
| Live evidence | One read-only headless load per page (12 pages: 10 × 200, National Portal 403, UIDAI 500, both recorded as inconclusive); emulator Chrome 145 (2 pages); JanSetu QA in-app WebView (National Portal) |

Not run: the API 37 instrumented suite (no Android app source changed; Phase 5's 41 passing tests apply), lint and APK assembly (no app change), and any human review (TalkBack, keyboard, moderated tasks, installed-extension checks).

## Phase 5 - 17 October 2026

Completed the native Android UI/UX redesign and the Phase 5 extension (JanSetu logo, adaptive and monochrome launcher icon, splash branding, original vector illustrations, restrained motion with Reduce motion, and working Theme, Language, Larger text and reminder-permission settings). Full report: [PHASE5_UI_QA_REPORT.md](PHASE5_UI_QA_REPORT.md). Design: [PHASE5_DESIGN_PLAN.md](PHASE5_DESIGN_PLAN.md). Assets: [PHASE5_ASSET_CREDITS.md](PHASE5_ASSET_CREDITS.md). Open items: [PHASE5_OUTSTANDING_FINDINGS.md](PHASE5_OUTSTANDING_FINDINGS.md). Manual steps: [PHASE5_MANUAL_CHECKLIST.md](PHASE5_MANUAL_CHECKLIST.md).

| Final validation (`python tools/run_phase5_quality.py extension-final`) | Actual result |
| --- | --- |
| `python -m unittest discover -s tools -p 'test_*.py'` | exit 0, **71 passed** |
| `node --test extension/core.test.mjs` | exit 0, **7 passed** |
| `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:connectedQaAndroidTest` | exit 0. **43 JVM tests and 41 API 37 QA tests passed**, zero failures/errors/skips. Lint has **0 errors/0 fatal and 14 warnings**, down from 23. |
| Isolated-profile integrity | **PASS.** Preview tracker database, preferences, reminder registry and imported-catalog state had identical SHA-256 before and after the gate, and before and after installation. |
| Protected data | **PASS.** Website asset, review queues, service catalog, public key and extension sources unchanged. |
| Preview install | **PASS.** `adb install -r` of the gate-verified debug APK (21,217,487 bytes, SHA-256 `a27ef5be61b6453a337eb0c45d01ed713d7f6214ee62b61f3ab9d2ac2d495e10`). `aapt`: `in.jansetu.app` 0.4.1/5, min 26/target 36. `apksigner`: v2. The on-device APK hash matches. `firstInstallTime` is unchanged. The redesigned Home launched with the retained profile. |

Before/after evidence: 33 matching pairs captured under identical emulator settings, plus 6 after-only captures (Help, in-app dark, splash, launcher, preview install), in `docs/screenshots/phase5/`. The "before" set comes from the retained pre-Phase-5 QA APK installed only in `in.jansetu.app.qa`.

Failures seen and handled:

- Two gate attempts failed **before any instrumented test ran** with a Windows "CreateProcess error=5" starting the test runner's Java process. They were archived and the third attempt passed.
- The first targeted run of the new Accessibility Test Framework test failed on two Home tiles cut off at the screen edge; this was fixed.
- A pre-polish passing gate was superseded after visual review found a landscape hero overflow and a clipped compact-navigation label; both were fixed.

The inherited Phase 4 "Accessibility Framework smoke check" never enabled ATF, so it checked nothing. It is corrected, and real ATF checks now cover seven redesigned screens.

Not run: spoken TalkBack, physical keyboard and Switch Access, Android 8–14 and physical devices (including the pre-Android-12 launch window and themed icons), delivered-notification appearance, independent Hindi review and brand legal review. These remain release gates. No deployment, signing or store activity took place, and Phase 6 has not started.

## Phase 4 - 17 October 2026

Completed local end-to-end quality, reliability and accessibility hardening from the supplied Phase 4 request. The complete report, known limits and preserved evidence are in [PHASE4_QUALITY_REPORT.md](PHASE4_QUALITY_REPORT.md); repeatable manual checks are in [PHASE4_MANUAL_CHECKLIST.md](PHASE4_MANUAL_CHECKLIST.md).

| Final validation | Actual result |
| --- | --- |
| `python -m unittest discover -s tools -p 'test_*.py'` | **71 passed** |
| `node --test extension/core.test.mjs` | **7 passed** |
| `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:connectedQaAndroidTest --no-daemon --max-workers=1 '-Pkotlin.compiler.execution.strategy=in-process' -Pjansetu.forceTests=true --console=plain` | **BUILD SUCCESSFUL**. **38 JVM tests and 34 API 37 QA tests passed**, zero failures/errors/skips. Lint has **0 errors/0 fatal** and 22 existing warnings. |
| Isolated-profile integrity check | **PASS**. The final harness recorded identical pre/post SHA-256 values for the preview encrypted tracker database, preferences, reminder registry and imported catalog. |
| Protected data check | **PASS**. The 7,347 website asset, review queues, service data, public key and extension permission/style source hashes remain unchanged. |
| Final preview install/package inspection | **PASS**. `adb install -r` launched the debug APK over `in.jansetu.app` without changing tracker/profile hashes; `apksigner` v2 verification and `aapt` package/version/min/target checks passed. |

The QA application is `in.jansetu.app.qa`, with a separate UID, files, preferences, alarm registry and Android Keystore from the preview package `in.jansetu.app`. The Phase 3C baseline was also replayed before fixes: 68 Python, 7 Node, 35 JVM and 28 Android tests passed, with the preview profile unchanged.

Fixed issues include browser-sensitive URL routing, encrypted tracker key-loss preservation, route confirmation after catalog changes, a 2x-text/320dp tracker reachability defect, and the Delhi/Puducherry citizen jurisdiction filter. The final debug APK is 20,737,425 bytes, SHA-256 `c8b98a4adcf2e2a670c2bfbce23708de241b29aff18c68a3372ddabf7f5c4857`.

Not run: manual spoken TalkBack traversal, physical keyboard assessment, real-device/OEM reminder delivery, reboot/Doze/timezone behaviour, full device matrix and live government authentication/payment/upload browser paths. These remain explicit release gates; no real credentials, transaction or production deployment was used.

## Phase 3C — 16 October 2026

Implemented the [supplied request](PHASE3C_REQUEST.md) locally, following the [audit and implementation plan](PHASE3C_IMPLEMENTATION_PLAN.md). App **0.4.1/code 5**; bundled service/runtime catalog **v13**; signed local demonstration **v14**. No public release, production signing, backend, cloud sync or government status API was introduced.

### Data, review and coverage

- Investigated **54 additional concrete candidates** through targeted official sources; **27 published, 27 held**. Total: **52 services**. The operator registry has 15 exact relationships; 14 are referenced by published services and one is pending-only for Himachal. There are 20 central services and 32 regional services: Tamil Nadu 9, Delhi 5, Kerala 8, Odisha 8 and Puducherry 2. Himachal's seven candidates, including two local Shimla operations, remain pending. No district/local service is published.
- Rechecked the four earlier pending cases first. Both DigiLocker operations, state-specific driving-licence application and PMEGP direct application remain pending with specific dated evidence/reasons and preserved prior decisions. Total pending service cases: **31**. HTTP/JavaScript/TLS failures were not converted into retirement or ownership decisions.
- PMEGP alone has an explicitly reviewed revision: the canonical non-www host passes normal TLS but its public intake notice says fresh submissions are unavailable. No Apply action is published. All 25 original IDs remain; the other **24 service objects are unchanged**. Earlier published queue decisions are retained.
- The original **7,347-website asset and 3,823-case website queue remain byte-for-byte unchanged**, verified against the retained [baseline](../data/validation_reports/phase3c-baseline.json). No nationwide crawl or automatic website-backlog resolution occurred.
- Unknown eligibility: **37**; unknown document lists: **23**; unknown applicant-group metadata: **42**. Known lists can be partial. Kerala certificate naming/ration-card verification, Odisha older/incomplete modal language, Puducherry mismatched document appendices and old overseas-voter guidance are explicitly handled rather than guessed. New information-only entries have no Apply or service-tracker shortcut. New confirmed Delhi/Kerala actions open reviewed shared login entries, not a guaranteed or prefilled transaction.
- Added optional fact-sourced applicant groups and explicit route evidence; URL/jurisdiction/duplicate gates; digest-bound pending amendments with prior-history retention; whole-collection validation before publication writes; and idempotent import/review/publication replay. Individually atomic editorial file writes are not represented as a cross-file database transaction.

Reproducible deliverables: [coverage matrix](SERVICE_COVERAGE_MATRIX.md), [gaps audit](SERVICE_GAPS_REPORT.md), [current catalog/workflow](SERVICE_CATALOG_REPORT.md), [search QA](SEARCH_QA_REPORT.md), [machine-readable coverage](../data/validation_reports/phase3c-service-coverage.json) and the retained research ledger/candidates/digest approvals under `data/services/`. The matrix includes all 36 state/UT rows and separate investigated/discovered/source-reviewed/published measures; a general portal never counts as service coverage.

### Actual commands and outcomes

| Command/check | Actual result |
| --- | --- |
| `python -m unittest discover -s tools -p 'test_*.py'` | **68 passed**. Includes provenance, unknown/partial facts, duplicate/ambiguous candidates, invalid/unconfirmed/redirected routes, full proposed-publication failure safety, replay idempotency and baseline preservation. |
| `python tools/service_catalog.py check`; report generation | Valid merged 52-service catalog; reports agree with actual queue/catalog and all 36 administrative rows. Replaying publication admits zero records. |
| `python tools/package_extension.py`; `node --test extension/core.test.mjs` | Same validated compact catalog packaged; **7 Node tests passed**. No extension permissions or styling profile changed; its interface still lists portals/guides rather than offering a new service/tracker UI. |
| `python tools/smoke_extension.py` | Chrome panel catalog/search/privacy/guide/control/stylesheet smoke passed with mocked extension APIs; installed toolbar/activeTab behavior is not covered. |
| `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:connectedDebugAndroidTest --no-daemon --max-workers=1 '-Pkotlin.compiler.execution.strategy=in-process' --console=plain` | Gradle **BUILD SUCCESSFUL, 8m59s**. **35 JVM and 28 API 37 tests passed**, zero failures/errors/skips. Lint: **0 errors, 22 warnings**. Full connected XML retained in `data/validation_reports/phase3c-connected-full.xml`. |
| Targeted `:app:connectedDebugAndroidTest` with `'-Pandroid.testInstrumentationRunnerArguments.class=in.jansetu.app.TrackerCatalogBoundaryTest'` after cleanup configuration fix | **1 repeated boundary test passed**, BUILD SUCCESSFUL in 1m21s; app remained installed and the fresh preview database stayed byte-for-byte unchanged. |
| `python tools/package_catalog.py update --version 14 --output artifacts/catalog-v14.jansetu` | Signed local package built: **14,522,343 bytes**; 52 services, same content as bundled v13 with only delivery version increased. |
| `python tools/verify_phase3c.py` | Runtime/debug-APK/extension payload identity, signed v14 payload/hash/signature, unchanged public pin, source/queue/service/decision preservation and actual XML/lint gates passed. APK contains no PEM/keystore file. |
| `apksigner verify`; `aapt dump badging` | Debug signature exit 0. Package `in.jansetu.app`, version 0.4.1/code 5, min 26/target 36 confirmed. |
| `adb install -r app/build/outputs/apk/debug/app-debug.apk` and launch | Installed and launched on `emulator-5554`, `Medium_Phone_API_37.0`; final preview remains on the new Kerala service search. |

That is **138 distinct passing tests**, plus one repeated connected boundary test after the test-runner configuration fix. The current connected output contains that targeted rerun; the full 28-test XML snapshot is retained separately, and the artifact verifier reports both. The outer redirected PowerShell Gradle wrapper returned a nonzero status despite both Gradle logs ending BUILD SUCCESSFUL; independent test XML, lint XML, APK validation and device checks passed. Existing SDK XML/deprecated icon/lint warnings remain. Older release APK/AAB files are Phase 3A checkpoints, not Phase 3C artifacts.

Artifact hashes and exact result totals: [phase3c-verification.json](../data/validation_reports/phase3c-verification.json). Debug APK: **20,644,064 bytes**, SHA-256 `43e7961fdf53fd7bd75afe51f6c24be0c3a252941c3d38cd7ac3f14356704556`. Runtime payload: **10,891,448 bytes**. The private signing key was not read by the verifier or packaged.

### Failures found and corrected

The initial Python replay regression failed because `import --revision` restaged newly published drafts as revisions when only their publication status differed. Import now compares the normalized proposed published object and skips identical content; the complete 68-test suite then passed. Strong duplicate checks validate the entire proposed publication before any catalog/queue write.

The existing emulator stopped responding to shell/control commands while still listed as connected. Restarted the same AVD without wiping userdata and restarted ADB; cold boot completed and the 28-test suite passed.

**The first connected suite's default AGP cleanup uninstalled JanSetu**, removing the old preview demo profile and its Keystore keys. This was a test-runner cleanup effect, not a catalog migration, and the old demo was not recovered. The UTP log explicitly recorded `uninstall_after_test: true` and target-app uninstall. Added the installed AGP-supported `android.injected.androidTest.leaveApksInstalledAfterRun=true` to `gradle.properties`. Created a clearly labeled fresh local demo, reran the isolated signed catalog-boundary test, and confirmed the app and exact encrypted database survived cleanup. Use the dedicated emulator: some UI tests deliberately reset test preferences. The full suite was not repeated after this configuration-only correction; the same APK and the targeted cleanup/preservation gate were verified.

### Device and signed-update inspection

At 1080×2400/420 dpi, font scale 1.0, English/light mode, inspected the actual 52-service search, a new Kerala income-certificate result, its scoped native page, sourced partial documents and explicit unknown eligibility. Screenshots are actual device captures; no new TalkBack/narrow-screen or authenticated government transaction audit is claimed.

Imported `catalog-v14.jansetu` through Android's document picker, observed version 14/local-update status, force-stopped/reopened the app and observed version 14 again. Confirmed restore returned to bundled v13. The **fresh** Tracker demo's title, planning status, deadline 2026-10-25, reminder-off setting, note and history remained visible. Its encrypted database SHA-256 stayed identical before/after the cleanup regression, signed import, restart and restore. Separately, the isolated Android boundary regression preserves a service snapshot, reference, submission date, deadline, enabled reminder preference/key and two history events even when the signed catalog removes all services. No real reminder was enabled for the preview demo.

Evidence: [all-service count](screenshots/phase3c-search-all.png), [new regional search](screenshots/phase3c-search.png), [native service](screenshots/phase3c-service.png), [signed import](screenshots/phase3c-update-installed.png), [restart persistence](screenshots/phase3c-update-restarted.png), [bundle restore](screenshots/phase3c-restored.png), [fresh demo before](screenshots/phase3c-tracker-before.png), [fresh demo after](screenshots/phase3c-tracker-after.png), and [device check record](../data/validation_reports/phase3c-device-checks.json).

### Remaining limits

The 31 unresolved service cases, wider jurisdiction/category coverage, missing eligibility/documents, independent editorial/translation/accessibility review, and real authenticated submission/payment/browser compatibility remain open. Search timing is a 14-query JVM observation, not a device latency guarantee or a controlled comparison against the older eight-query run. No underlying government website was redesigned or made faster in Phase 3C; the limited existing companion readability profile is unchanged. NIC directory reuse, site-specific terms and production release requirements remain separate from local development. Nothing was uploaded or deployed.

## Phase 1 work session, 12 October 2026

## Commands and actual results

| Command/check | Result |
| --- | --- |
| Repository and environment audit | Workspace initially contained only `JANSETU_CODEX_PLAN.md`; it was not a Git repository. JDK 17 exists. No Android SDK, emulator, `adb`, or Gradle installation was found. |
| Gradle 8.13 `wrapper` task | Passed after updating the Kotlin 2.3 `compilerOptions` DSL. Wrapper scripts and JAR generated. |
| `gradlew.bat help --console=plain` with JDK 17 | Passed; Gradle project configuration succeeds. |
| `gradlew.bat :app:dependencies --configuration debugCompileClasspath --console=plain` | Passed; dependencies resolved, including Compose 1.10.5, Material 3 1.4.0, Navigation 2.9.8, and Core KTX 1.17.0. |
| `gradlew.bat :app:testDebugUnitTest :app:assembleDebug --console=plain` | Blocked before compilation: `SDK location not found`. No APK or Gradle unit-test result exists. |
| `python tools/check_catalog.py` | Passed: `Catalog valid: 4 verified websites, 4 unique domains, 0 services, 4 categories`. |
| Python `xml.etree.ElementTree` parse of `app/src/main/**/*.xml` | Passed: 5 Android XML files parsed. This is syntax checking only, not Android resource compilation. |

The first wrapper attempt failed on deprecated `kotlinOptions { jvmTarget = "17" }`, which Kotlin 2.3 treats as an error. It was changed to `compilerOptions`, and the wrapper task passed on rerun.

## Remaining verification after 12 October

- Install Android SDK Platform 36 and Build Tools 35.0.0 or newer through Android Studio, with the user's acceptance of the Android SDK license. Then run `gradlew.bat :app:testDebugUnitTest :app:assembleDebug` and fix any compile or test failures.
- Run `gradlew.bat :app:connectedDebugAndroidTest` on an emulator or device. No UI test or accessibility inspection ran in this session.
- Check portrait, large font, TalkBack, dark theme, browser handoff, and device back navigation on a device. These are not verified by Gradle configuration.

No clean security audit or release readiness is claimed. The app has no `INTERNET` permission, WebView, credential collection, or backend in this phase. The external browser handles official site access.

## 13 October 2026 build session

The user authorized installation of the Android command-line SDK and acceptance of its licenses. Command-line tools were downloaded from Google and matched the published SHA-256 checksum. SDK Platform 36, Build Tools 35.0.0, and Platform Tools were installed locally. `local.properties` points to that SDK and is ignored by `.gitignore`.

| Command/check | Result |
| --- | --- |
| `gradlew.bat :app:testDebugUnitTest :app:assembleDebug --console=plain` | Passed after fixing Kotlin's `in` package keyword and `LocalSettings` setter name clashes. Three unit tests passed with zero failures. |
| `gradlew.bat :app:assembleDebugAndroidTest :app:lintDebug --console=plain` | UI test APK compiled. Lint first found two errors: an API 27 navigation bar attribute in base resources and an unescaped SDK path. Both were fixed. |
| `gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug --console=plain` | Passed. Lint: zero errors, 15 warnings. |
| `python tools/check_catalog.py` | Passed: 4 verified websites, 4 unique domains, 0 services, 4 categories. |
| `aapt dump badging app/build/outputs/apk/debug/app-debug.apk` | Confirmed package `in.jansetu.app`, version 0.1.0, minimum API 26, target API 35, label JanSetu. |
| `adb devices` | No connected device or emulator. The Android UI test did not run. |

The debug APK exists at `app/build/outputs/apk/debug/app-debug.apk` (19,671,526 bytes). The compiled UI test APK exists at `app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk`. The 15 lint warnings cover older target/dependency versions, backup configuration, icon resource suggestions, and Kotlin style suggestions; none is a lint error. See the generated `app/build/reports/lint-results-debug.html` for details.

Device behavior remains unverified: onboarding, navigation, search UI, portrait and large font layouts, TalkBack, dark theme, browser handoff, and back navigation need an Android phone or emulator. The next verification command is `gradlew.bat :app:connectedDebugAndroidTest` with a device connected. Phase 1 remains open until its user-facing flows are observed.

## 13 October 2026 emulator session

Android Studio completed installation and created the `Medium_Phone_API_37.0` AVD. `emulator -accel-check` reported Windows Hypervisor Platform usable. The emulator booted, `adb install -r app/build/outputs/apk/debug/app-debug.apk` returned `Success`, and `adb shell am start -n in.jansetu.app/.MainActivity` opened JanSetu. Onboarding, Home, Search, and a portal detail were observed on screen. The external-site action opened Chrome's first-run screen; the official page itself was not reached. A clean Home capture is saved at `docs/screenshots/home.png`.

`gradlew.bat :app:connectedDebugAndroidTest --console=plain` ran one test on the Android 37 AVD and failed before its search assertion. Espresso threw `java.lang.NoSuchMethodException: android.hardware.input.InputManager.getInstance []` during `Espresso.onIdle`. This is a test-framework/platform compatibility failure; it does not establish whether the search assertion passes. The connected test task removed the app package afterward; the debug APK was reinstalled and JanSetu was returned to Home.

The VS Code Android Emulator Launcher extension was installed but could not find the `emulator` command because the existing VS Code process lacked Android SDK paths. `ANDROID_HOME` and the SDK emulator, platform-tools, and command-line-tools paths were added to the user environment. VS Code must be fully restarted to inherit them. The AVD can be launched directly from Android Studio Device Manager meanwhile.

Phase 1 still needs a passing Android UI test on a compatible emulator or test framework, plus large-font, TalkBack, dark-theme, and full browser navigation checks. The app launch and core screens are now observed rather than merely compiled.

## 13 October 2026 follow-on implementation and verification

The earlier Espresso failure was caused by transitive Espresso 3.5.0 calling `InputManager.getInstance` on Android 37. Explicit Espresso 3.7.0 resolved it. The app now has a manual catalog review pipeline, improved deterministic search, two source-backed guides, a limited WebView for the National Portal, and a separate local Chrome companion panel.

| Command/check | Actual result |
| --- | --- |
| `gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug --console=plain` | Passed after the new browser/guide changes. Six unit tests passed, zero failures. Lint task passed; warnings remain. |
| `gradlew.bat :app:connectedDebugAndroidTest --console=plain` | Passed on `Medium_Phone_API_37.0`: three UI tests, zero failures (onboarding/navigation, search, guide source action). |
| `python -m unittest discover -s tools -p 'test_*.py'` | Three catalog pipeline tests passed. |
| `python tools/check_catalog.py` | Passed: four verified websites, four unique domains, zero services, two guides, four categories. |
| `python tools/catalog_pipeline.py report` | Wrote the reproducible `docs/CATALOG_COVERAGE.md`; no candidate import has run. |
| `python tools/package_extension.py` | Copied the checked catalog into `extension/catalog.json`. |
| `node --test extension/core.test.mjs` | Three extension logic tests passed: verified record filtering, exact host matching, and honest search results. |
| `python tools/smoke_extension.py` | Chrome rendered the panel with stubbed extension APIs. Catalog/search, empty state, and matching DigiLocker guide passed. This does not test toolbar or side-panel permissions in an installed extension. |
| API 37 emulator manual flow | APK installed and launched. National Portal detail and WebView were opened. With JavaScript disabled, the live page stalled; after enabling the portal's own JavaScript/DOM storage only for this record, its home page rendered. Capture: `docs/screenshots/browser-js.png`. |

The first compile of the new WebView failed because `settings` resolved to `LocalSettings` rather than `WebView.settings`; qualifying it fixed the build. The first new UI test compile failed on an unnecessary `assertExists` import; removing the import fixed the connected suite. An attempt to load the unpacked extension in automated headless Chrome stalled before the extension worker appeared, so the actual installed side panel remains unverified. The separate rendered-panel smoke test passed.

**Still open:** large-font and TalkBack inspection; dark theme and contrast; browser TLS-error simulation, cross-host redirects, downloads/uploads, authentication/payment flows, lifecycle stress; link freshness; extension install/action behavior; regional catalog and full source coverage; release signing, Play requirements, and production validation. A passing local build is not a release claim.

## 14 October 2026 catalog, support, and release preparation

Five additional official-source-backed central portals were published through the review pipeline: National Scholarship Portal, Udyam Registration, Income Tax e-Filing, RTI Online, and UIDAI Aadhaar Services. Three other candidates remain in review because live destination checks failed or timed out. The current published catalog has nine distinct domains, two guides, eight represented categories, zero individual services, and zero state/local portals. The reproducible report is `docs/CATALOG_COVERAGE.md`.

The app now shows a bundled privacy policy in Settings and opens support or portal-report email drafts addressed to Utkarsh's supplied support address. No email is sent automatically. Target SDK is 36. Optional release signing reads four environment variables, all required together. No production key was created or supplied.

| Command/check | Actual result |
| --- | --- |
| `gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug :app:bundleRelease :app:assembleRelease :app:connectedDebugAndroidTest --console=plain` | Passed on the API 37 emulator: six unit tests and four UI tests, zero failures; lint, debug and release build tasks passed. |
| `python -m unittest discover -s tools -p 'test_*.py'` | Three catalog pipeline tests passed. |
| `python tools/check_catalog.py` | Passed: nine verified websites, nine unique domains, zero services, two guides, eleven taxonomy categories. |
| `node --test extension/core.test.mjs` | Three logic tests passed. |
| `python tools/smoke_extension.py` | Chrome rendered the panel; catalog, search, empty state, and matching-site guide passed. Installed extension toolbar behavior remains unverified. |
| `adb install -r .../app-debug.apk` and `adb shell am start -n in.jansetu.app/.MainActivity` | Passed on `emulator-5554`; the current app was reopened on Home after the UI suite. Screenshot: `docs/screenshots/home-current.png`. |

Build outputs at the end of this session: `app/build/outputs/apk/debug/app-debug.apk` (19,245,397 bytes), `app/build/outputs/apk/release/app-release-unsigned.apk` (12,631,308 bytes), and `app/build/outputs/bundle/release/app-release.aab` (12,270,986 bytes). `apksigner verify` failed for the release APK and `jarsigner -verify` reported the AAB unsigned, as expected without a production key. These are local artifacts, not Play-ready uploads. `aapt dump badging` confirms package `in.jansetu.app`, version `0.1.0`/code 1, min API 26, target API 36, label JanSetu, and `INTERNET` permission.

After the first large-font/dark-theme capture, dark status-bar icons were found against a dark background. `MainActivity` now sets system-bar icon appearance from the active theme. `gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug :app:bundleRelease :app:assembleRelease --console=plain` passed after this fix. The rebuilt debug APK was installed. A second API 37 screenshot at 1.3× font scale and dark theme showed light status icons, readable Home copy, and no visible clipping in the first viewport (`docs/screenshots/dark-large.png`). Font scale and theme were restored to 1.0/light, and the app was reopened on Home (`docs/screenshots/home-current.png`). The four connected UI tests passed before this small appearance change and were not rerun after it.

`python tools/package_extension.py` now also packages the bundled privacy policy into `extension/privacy.html`. The Chrome panel smoke test was extended and passed its policy-link popup/content check, alongside search, empty state, and contextual-guide checks. This still does not verify toolbar activation in an installed extension.

Catalog publication checks were tightened to reject nonstandard HTTPS ports and JavaScript-enabled records unless the site is explicitly configured for the in-app browser. The Android unit tests and debug/release builds passed again after this change; `python tools/check_catalog.py` and all three Python pipeline tests passed. The latest debug APK was reinstalled on the emulator.

`python tools/audit_links.py` added a repeatable, read-only portal URL probe. Its first HEAD-only result flagged Scholarship (404), Udyam (302), and UIDAI (500). A safe same-host GET fallback resolved all three. The final report shows eight HTTP 200 responses and one HTTP 206 partial response across the nine published URLs on this machine. The [National Scholarship Portal](https://scholarships.gov.in/), [Udyam Registration](https://udyamregistration.gov.in/), and [UIDAI My Aadhaar](https://uidai.gov.in/en/my-aadhaar) pages were also opened directly on 14 October. No catalog URL was changed. The probe does not establish content freshness, ownership, or login compatibility, and source/guide URLs were not included. Five Python tests now pass, including redirect policy tests.

## 14 October 2026 readability extension pilot

The Chrome companion now has a user-invoked Readable text/Restore original control on the National Scholarship Portal home page. It uses `activeTab` plus `scripting` to insert/remove one static stylesheet in the main frame. The control is offered only on the reviewed public host and `/home` path. It does not inject JavaScript or read page contents. A single-page app route change might leave the stylesheet active until Restore original or reload; that behavior has not been verified on the installed extension. Four Node tests pass, including the reviewed-path allowlist; the Chrome rendered-panel smoke test exercised the controls against mocked `chrome.scripting` and captured `docs/screenshots/extension-readability.png`. A separate fixture confirmed the stylesheet increases a 10px paragraph to at least 16px, leaves an input's font unchanged, and can be removed. Headless Chrome loaded National Scholarship Portal with HTTP 200 and captured before/after screenshots; National Portal and UIDAI returned HTTP 403 and 500 to Chrome and were removed from the allowlist. These tests do **not** prove installed-extension permission behavior. A manual visual and accessibility review remains mandatory before calling the pilot production ready.

The limited Android WebView now offers A+/A text zoom for the National Portal. The full Gradle command `gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug :app:bundleRelease :app:assembleRelease :app:connectedDebugAndroidTest --console=plain` passed after this change: six unit tests, four API 37 UI tests, lint and build tasks. The rebuilt debug APK installed and launched on `emulator-5554`. A manual live-page check showed the National Portal loading in the WebView and the A+ control increasing text size; captures are `docs/screenshots/browser-text-normal.png` and `docs/screenshots/browser-text-zoom.png`. The portal's cookie dialog reflowed into more lines at 125%, so this is a limited readability aid rather than a reviewed redesign of the portal. It does not speed up the page or change its server behavior.

The privacy policy and store/terms drafts are local files; a public privacy URL and Play Console declarations are still required. Browser edge cases, TalkBack and broader accessibility checks, broader catalog coverage, installed extension behavior, and release signing remain open.

## 14 October 2026 national inventory and UX audit

The official-source inventory added a source registry, seven-level taxonomy, sector/purpose profiles for all nine published portals, and six attributed regional candidates. They remain in the review queue. The queue now has nine candidates total (one `DISCOVERED`, one `NEEDS_OWNERSHIP_REVIEW`, one `NEEDS_URL_REVIEW`, six `TEMPORARILY_UNREACHABLE`). The published catalog still has nine central portals and zero state/UT or individual service records. The companion extension allowlist was not expanded. See [inventory](GOVERNMENT_WEBSITE_INVENTORY.md), [coverage](CATALOG_COVERAGE.md), [page audit](PORTAL_UX_AUDIT.md), [intervention matrix](UI_INTERVENTION_MATRIX.md) and [manual checklist](UX_AUDIT_MANUAL_CHECKLIST.md).

Ten exact page/entry records were classified: one A (limited landing viewport), three D (guidance), three E (sensitive journey), one F (one observed misdirected directory URL), and two requiring further manual review. No page passed a B or C approval. A read-only one-load, 390×844 Chrome screen produced five HTTP 200 pages and one HTTP 403 (National Portal, access inconclusive). The [screening results](../data/screening_runs/2026-10-14.json) contain measurements and conditions. A single load time is not a speed benchmark. The National Portal, Scholarship, RTI, DigiLocker, Lakshadweep and Coimbatore observations are documented with the saved public-page captures; credentials and form data were not collected.

After adding the Android rotation observation, the first inventory validation and four dependent Python tests failed because the edited JSON ended in a literal `\n` token. The stray token was removed; the final inventory validation and all ten Python tests passed. The one-request National Portal directory inspection returned HTTP 403 and yielded no import endpoint. These failed/inconclusive checks did not result in new published records.

| Command/check | Actual result |
| --- | --- |
| `python tools/inventory_audit.py` | Passed: nine published, nine retained candidates, ten page audits; evidence, classification, taxonomy and duplicate checks passed. |
| `python -m unittest discover -s tools -p 'test_*.py'` | Ten tests passed, including discovery-state and audit validation tests. |
| `python tools/check_catalog.py` | Passed: nine verified websites, nine domains, zero services, two guides, eleven app categories. |
| `node --test extension/core.test.mjs` and `python tools/smoke_extension.py` | Four Node tests and mocked Chrome panel smoke test passed. Installed extension behavior remains unverified. |
| `python tools/audit_links.py` | All nine published destination URLs responded during a read-only probe. Response alone does not prove ownership or availability to all users. |
| `gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug :app:bundleRelease :app:assembleRelease :app:connectedDebugAndroidTest --console=plain` | Passed before the browser rotation adjustment: six unit tests, four API 37 UI tests, lint, debug and unsigned release builds. |
| `gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug :app:connectedDebugAndroidTest --console=plain` | Passed after the rotation adjustment: six unit tests, four API 37 UI tests, lint and debug build. |
| `gradlew.bat :app:bundleRelease :app:assembleRelease --console=plain` | Passed after the rotation adjustment; release APK and AAB remain unsigned without a production key. |

At 1.3 system font scale and WebView A+ (125%), the National Portal cookie notice grows. In landscape its button row is partly obscured/clipped by adjacent page content; [the device capture](screenshots/audit-android-landscape-after-fix.png) records the state. The app briefly displayed “Browser unavailable” while its bundled catalog reloaded on rotation. `MainActivity` now shows Loading during that transition, and `BrowserScreen` saves A+ across activity recreation. A manual retest confirmed the `A` label (125% active) after rotation and the portal rendered again. This addresses JanSetu's state handling, not the portal's landscape layout. At portrait zoom, the notice could be scrolled to reach its buttons. No cookie choice was made.

Open gates: a full manual task journey, TalkBack and keyboard review, 200% text/reflow, contrast measurement, repeated latency measurements, actual installed-extension route-change behavior, canonical review for Chennai and regional ownership, release signing and a public privacy URL. No national-completeness, speed improvement, WCAG conformance or production-release claim is made.

## Phase 2A bulk collection - integration and recovery history

This section supersedes earlier sample-only coverage claims; the historical checks above remain intact. The user authorized local directory development while NIC reproduction permission is pending. Public distribution remains blocked.

The live IGOD acquisition processed 3,586 source pages and extracted 23,169 raw rows. All 461 listings that exposed result totals reconciled; New Additions provided 15 visible rows without a total/history. All 36 state/UT branches and their source-linked listings were enumerated. These are source-acquisition measurements, separate from destination health and local catalog admission.

Recovery details: Python urllib could not build a certificate chain here; installed curl/Schannel and normal Chrome validated TLS successfully. No certificate bypass was used. Five final source pagination requests failed DNS resolution and succeeded on the bounded retry run. Parsing was corrected to exclude sidebar previews from listing counts and retain plain unlinked district rows. Pagination follows source-provided offsets/page sizes and the AJAX header observed during normal browser scrolling. The unsupported larger-page probe returned 302 and was discarded.

The destination run encountered Windows file replacement locks. Unique process/thread temporary paths with bounded PermissionError retry were added; cached network evidence supports retrying local processing failures. Destination response bodies are discarded, retaining minimal health evidence; older homepage bodies were converted to metadata. No filled forms, credentials or OTPs were collected.

At the intermediate 1,321-entry checkpoint (nine curated and 1,312 local listings), Gradle debug build, lint, nine JVM tests and five API 37 connected tests passed. The first expanded connected run failed because Home was asserted before asynchronous catalog loading finished. The UI test now waits for the actual rendered state; regional filter/search tests also await actual results. A subsequent connected run passed. JVM observations at that checkpoint: catalog 4,428,584 bytes, parse approximately 59 ms, index 227 ms and thirty queries 753 ms. These are desktop JVM observations, not phone performance guarantees.

`python -m unittest discover -s tools -p "test_*.py"` passed 32 tests after fixing fresh scoped-batch creation. Tests cover real recorded HTML, pagination, missing structures, URL normalization, classification, incomplete categories, duplicate handling, bounded transient failures, redirect/default-server classification, source evidence gates, preserved manual records/review decisions, incremental refresh, body retention and scoped dry runs. Existing national batches cannot be silently reused for state-only collection. A national rerun retrieved no new source pages and kept exactly 23,169 rows, with zero pending/failed pages. The new state-scoped dry run made no requests or writes.

Final catalog admission, artifact sizes, search measurements and final verification commands are recorded below when that run finishes. Fixture success is not evidence of nationwide live coverage; the source manifest is the integration evidence.

Destination delay correction: an offline scan found explicit crawl-delay directives on 85 robots responses at that checkpoint. The earlier checker used its two-second default without raising it for these directives. This omission is recorded here rather than claiming those earlier requests honored every delay. The process was stopped at its checkpoint; CachedClient now stores and honors the larger per-host delay before subsequent requests. The resumed run reused cached responses and recovered local CHECK_EXCEPTION entries. A deterministic test confirms a ten-second directive overrides the two-second default. The full Python suite then passed 33 tests.

A subsequent large-catalog review found Home still composing every website under the Verified portals heading. Home now displays the original reviewed portals plus a bounded three-entry directory preview and a full-directory search action. This also removes the incorrect grouping of IGOD listings as verified. The connected navigation test exercises that directory action. Final Android checks cover this change.

The Home preview follow-up run passed JVM tests and the regional instrumented check, but the expanded MainFlow test failed when it attempted Settings during the return-to-Home transition (five tests executed, one failure). The test was changed to wait for the actual Settings node and scroll it into view; the targeted rerun is recorded with final results. Intermediate emulator metrics for 1,321 entries: parse 570 ms, index 1,754 ms and thirty searches 1,098 ms. These emulator measurements are distinct from the desktop JVM measurements.

The targeted Home rerun showed that waiting alone was insufficient: navigation restored Home at the directory preview, leaving Settings above the viewport. The test now scrolls the Home container back to the top before opening Settings. One targeted command initially failed because PowerShell split an unquoted dotted Gradle property; quoting the entire -P argument corrected invocation. An unnecessary onNode import then failed compilation and was removed. These were test/invocation failures, not destination evidence.

The emulator indexing measurement justified one local optimization: identical title/alias/organization/category strings now share token sets during index construction. Search weights and matching semantics are preserved; the final JVM and connected tests measure the expanded dataset after this change.

The next targeted attempt crashed in Compose SlotWriter during teardown. The earlier log shows CalledFromWrongThreadException: the test frame interceptor applied changes on DefaultDispatcher-worker-2. This provided a concrete scheduler diagnosis rather than evidence that a dependency upgrade was needed. MainFlow now explicitly uses StandardTestDispatcher for its Compose effect context, following the Android team's [test scheduler guidance](https://android-developers.googleblog.com/2025/12/whats-new-in-jetpack-compose-december.html). No production Compose dependency was changed. The new scheduler is validated by a targeted rerun and the final suite.

Further tracing corrected the earlier scroll-only hypothesis: a captured semantics tree showed Search still selected after tapping Home. The existing bottom-navigation restoreState=true could restore the saved Search child stack when returning to the retained Home root. Home navigation now disables saved-stack restoration; other destinations keep their existing restoration. A stable Home scroll-container tag supports the regression test. The scheduler fix remains separate from this production navigation fix. Repeated targeted attempts and their timeouts were diagnostic failures, not passing tests.

Discovery staging now preserves exact normalized www/bare URL variants separately. Legacy publication uniqueness collisions remain in URL review with history rather than silently overwriting a source candidate. A regression test checks that both variants survive staging and the collision stays unpublished. The Python suite passed 34 tests.

At 6,659 entries, the full debug build, lint, nine JVM tests and all five API 37 UI tests passed (including the Home regression and regional filters). Chrome panel smoke and all five Node tests passed. Desktop JVM observations: asset 26,016,491 bytes, parse 115 ms, index 136 ms, thirty searches 1,701 ms. API 37 observations: parse 2,726 ms, index 3,247 ms, thirty searches 2,787 ms. Index construction reuses identical field text, and UI search remains off the main thread. These are measured checkpoints; the final artifact figures follow below.

A final privacy scan found seven cached redirect-header files containing session/token parameters even though structured redirect metadata had been redacted. Header writing now redacts those parameters too; the cache is scrubbed before delivery. Generic token and session-bound URLs are held for review rather than fetched. A new regression test confirms token/cookie markers are absent from stored headers and metadata. The Python suite passed 36 tests. No token values were printed.

## Final Phase 2A result - 15 October 2026 (local time)

The final Android and Chrome catalog contains **7,270 website/landing records** across **7,040 hosts**: the original nine reviewed records remain exactly unchanged, and **7,261 IGOD entries** are explicitly directory-listed for local development. There are **3,902 unpublished candidates**, **zero individual services**, and two existing guides. All 36 state/UT branches were enumerated and their source-linked listings processed. The run extracted **23,169 raw rows** from **3,586 processed pages**, reconciled all **461 listings with displayed totals**, and retained 15 observed New Additions rows with no total/history claim. No source pages or destination URLs remain unchecked/pending. Check outcomes include unresolved/denied requests; acquisition completeness is not destination availability.

Final normalization retains 20,946 source organization identities, 11,168 URLs, and 2,356 organizations without valid URLs. Source identities can still include uncertain matches. There are 29 suspected duplicate groups and four observed safe redirect groups with five aliases. No new permanent retirement was asserted. Details and all state/sector rows are in DISCOVERY_RUN_REPORT.md.

| Final command/check | Actual result |
| --- | --- |
| `python tools/discover_government_sites.py --source igod --delay 1` | Resumed with no new source requests/pages; 23,169 rows, 3,586 processed, zero pending/failed. |
| `python tools/prepare_discovery_catalog.py --check --retry-exceptions --workers 8 --timeout 10` | All 11,168 URLs have outcomes; zero NOT_CHECKED or CHECK_EXCEPTION. Cached results reused. |
| `python tools/catalog_pipeline.py stage-discovery .../normalized_candidates.json` | Final safe rerun: zero new candidates, zero local refreshes/conflicts/holds, zero new approvals; decisions preserved. |
| `python tools/catalog_pipeline.py publish .../local_approvals.json` | Final admission added 612 entries; one prior session-bound listing returned to URL review with its prior record/profile/history preserved. Final total 7,270. |
| `python tools/check_catalog.py` and `python tools/inventory_audit.py` | Passed; all ten prior UX assessments remain valid and retained. |
| `python tools/catalog_pipeline.py report`, `python tools/discovery_report.py`, CSV/JSON review export | Completed with final counts; exports contain 3,902 review rows. |
| `python -m unittest discover -s tools -p "test_*.py"` | 37 tests passed on the final dataset, zero failures. |
| `python tools/package_extension.py`; `node --test extension/core.test.mjs` | Packaged 7,270 entries; five tests passed. No readability or WebView permission added. |
| `python tools/smoke_extension.py` | Chrome rendered final catalog, 30/60 result batching, search, privacy, guide, mocked readability controls and reversible stylesheet; passed. Installed extension toolbar remains unverified. |
| `python tools/audit_links.py --cached-discovery data/collection/igod-2026-10-14/destination_health.json` | Nine curated URLs probed live and responded; 7,261 directory checks reused with their actual original times. No blanket fresh-request claim. |
| `gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug :app:bundleRelease :app:assembleRelease :app:connectedDebugAndroidTest --console=plain` | Build, lint, unsigned release artifacts and all five API 37 tests passed. The initial JVM task was cached, so it was rerun separately as below. |
| `gradlew.bat :app:testDebugUnitTest --console=plain` after declaring asset input | Nine JVM tests actually reran against 7,270 entries; zero failures. The catalog asset is now a declared test input, preventing stale results on future data-only changes. |
| `python tools/check_catalog.py --public-release` | Expected rejection: directory additions remain local development data; NIC reproduction permission is pending. |
| Release signature checks | APK fails apksigner verification and AAB is unsigned, as expected without a production key. Not Play-ready. |
| `adb install -r .../app-debug.apk`; `adb shell am start -W -n in.jansetu.app/.MainActivity` | Success; rebuilt app opened on emulator-5554. APK catalog bytes exactly match the final asset. |

The header cleanup changed 13 cached header files; structured cleanup changed 15 evidence/data files. One session-bound responding target was reclassified for URL review. No destination HTML bodies remain. The initial post-policy refresh failed on that URL; the classifier now holds it rather than attempting equivalence or another request. Token values were never printed.

**Measured final catalog:** 28,375,808 uncompressed asset bytes. Desktop JVM parse 256 ms, index 292 ms, thirty queries 2,323 ms. API 37 emulator parse 3,292 ms, index 1,783 ms, thirty queries 3,170 ms (about 106 ms/query). These observations are not broad phone benchmarks. The cold debug Activity launch reported 8,803 ms while other checks were running; it is not a catalog-ready or server-performance measure. PSS observations: 216,827 KB on onboarding after load and 147,267 KB on Home after settling. The default-size Home and full Search view were visually inspected; no visible first-viewport clipping was found.

The asset size/load cost justifies a compact runtime catalog with full research evidence kept separately, then a versioned permitted update manifest/local database before a wider release. No backend was added. Search is indexed off the UI thread; Home renders a bounded preview, Android search uses lazy results, and Chrome renders bounded batches with cached host lookup.

Screenshots: [Home](screenshots/phase2a-home.png), [Search showing all 7,270 records](screenshots/phase2a-search.png), [directory detail and distinct source/response dates](screenshots/phase2a-directory-detail.png), and [onboarding](screenshots/phase2a-onboarding.png). An optional `adb shell uiautomator dump` returned a null root node on this API 37 environment; it was not counted as a passing inspection. Screenshots and the passing Compose UI tests provide the actual visual/navigation evidence. The app is left open on Search.

Build artifacts: debug APK 22,631,163 bytes; unsigned release APK 14,590,116 bytes; unsigned AAB 14,234,182 bytes. Hashes and aggregate test results are in `data/collection/igod-2026-10-14/verification.json`. Existing deprecation/SDK tooling warnings remain; no lint error blocked the build.

**Remaining:** NIC permission and a reviewed public evidence policy; 2,302 robots-unresolved and 98 robots-disallowed destinations, 172 redirect-review results, 1,166 ownership-review candidates and other documented response/URL questions; permitted LGD identifiers/dataset access; individual service research and more guides; compact catalog updates; installed-extension behavior, TalkBack, additional devices/browser failure paths, production signing, hosted privacy policy and Play submission. No later project phase was marked complete by this collection result. The next data task is targeted URL/operator review and permitted administrative coverage reconciliation.

Final bookkeeping check: a zero-page resume now persists its invocation timestamps, fetched-page count and collector/adapter hashes. The resumed national batch remained at 3,586 processed pages, 23,169 rows and zero pending/failed pages. Source registry links the final verification artifact; no app/catalog change or rebuild was required.

## Phase 2B review and administrative reconciliation - 15 October 2026

Baseline audited directly: 23,169 raw IGOD rows; 20,946 source organization identities; 11,168 normalized URLs; 7,270 bundled records; 3,902 review candidates; 1,166 ownership statuses; 29 suspected organization groups; four observed redirect groups/five aliases; 36 source branches; 3,586 processed pages; zero services. The entire baseline queue/catalog and the original source data remain retained.

The complete queue was triaged with cached evidence. Two safe redirect aliases (Rajasthan Forest and the www National Career Service entry) are archived with original evidence and matching existing target IDs. Seventy-eight entries passed the source/health proposal gates; one encountered the existing canonical URL uniqueness gate and remained in URL review. Seventy-seven were admitted locally as directory-listed records. All 7,270 baseline records, including all nine curated records, are unchanged as JSON objects. Final: **7,347 websites, 7,338 directory listings, 3,823 pending, two archived aliases, zero new ownership certifications/rejections, zero services and two guides**. All 3,902 original candidates are accounted for. Current buckets: 2,349 inconclusive, 1,174 ownership/operator, 275 URL/redirect, 25 likely duplicate. Stored review statuses can differ from triage priority buckets because prior explicit decisions remain intact.

Publication checks now validate cached source content URL/name membership and hashes, same-response fragment equivalence, complete same-host HTTPS redirect chains and a 30-day destination-observation admission window. Domain appearance alone never establishes ownership. Changed source names/organization URL sets and disappeared entries generate review-only diffs. Resolved aliases survive imports; changed redirect evidence reopens review. A newer inconclusive check can be displayed without erasing the original response evidence or silently deleting a valid listing. Android and Chrome status labels distinguish these observations; styling/WebView permissions are unchanged.

The live LGD pass made eight normal-TLS public-page/robots requests (cached reruns make no new requests): public state codes and the source-linked state-wise entities summary supplied all 36 stable state IDs and state/UT denominators. Totals: 784 districts, 5,050 urban bodies, 262,840 panchayats (675 district + 6,769 intermediate + 255,396 village), 14,163 traditional bodies and 60 cantonment boards. Individual district, local-body, urban summary and directory-download forms require CAPTCHA; no CAPTCHA submission/bypass was attempted. There are zero confirmed district/local-body stable-ID website links. This is an unresolved reconciliation gap, not a claim that those units lack websites. Derived public table rows/response hashes are retained; raw administrative homepage/contact bodies are discarded. No third-party dataset was substituted for an authoritative unit registry.

| Command/check | Actual result |
| --- | --- |
| `python tools/review_queue.py --apply` | All 3,902 baseline candidates processed; final rerun admits nothing further. Decisions/reasons/conflicts, prioritized CSV and archives written. |
| `python tools/catalog_pipeline.py publish data/collection/phase2b-2026-10-15/local_approvals.json` | 77 admitted; one proposed uniqueness collision held. Final website count 7,347. |
| `python tools/administrative_coverage.py --collect`, then `python tools/administrative_coverage.py` | Live public tables acquired, then cached evidence reconciled for 28 states/eight UTs. Unit-level CAPTCHA blockers recorded. |
| `python tools/discovery_report.py`; `python tools/catalog_pipeline.py report` | Regenerated actual state/sector/catalog/source counts; aliases excluded from pending category counts. |
| `python -m unittest discover -s tools -p "test_*.py"` | Final 53 tests passed. An intermediate 52-test run had one fixture failure: a shallow copied manual record shared a history list with the synthetic directory record. The fixture now owns its own list; production baseline equality was independently checked. |
| `python tools/check_catalog.py`; `python tools/inventory_audit.py` | Passed on 7,347 records; 3,823 candidates and all ten prior UX assessments retained. |
| Real queue rerun/object-preservation check | Proposed queue exactly equals stored queue; no approvals/resolutions on repeat. Every original 7,270 website object and all original nine curated objects match baseline. |
| `python tools/package_extension.py`; `node --test extension/core.test.mjs` | Exact 7,347-record catalog copied; six Node tests passed. |
| `python tools/smoke_extension.py` | Chrome search/empty/privacy/guide/batching and mocked readability/reversible stylesheet checks passed. Favicon 404 is harmless. Installed-extension toolbar behavior remains unverified. |
| `python tools/audit_links.py --cached-discovery data/collection/igod-2026-10-14/destination_health.json` | Nine curated destinations freshly probed and responded; 7,338 cached actual directory checks reused without changing their original dates. Review triage itself made zero destination requests. |
| `gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug :app:connectedDebugAndroidTest --console=plain` | Debug APK compiled and 11 JVM tests passed. Combined task failed before emulator execution because the Java child process returned CreateProcess error 5, Access is denied. This attempt was not counted as a passing device test. |
| `gradlew.bat :app:lintDebug :app:connectedDebugAndroidTest --no-daemon --console=plain` | Retry passed lint and all five API 37 emulator tests, zero failed/skipped. A fresh JVM could launch; the transient Windows denial was not attributed to app behavior. |
| `gradlew.bat :app:assembleRelease :app:bundleRelease --no-daemon --console=plain` | Unsigned release APK/AAB built successfully; signing behavior unchanged. |
| `python tools/check_catalog.py --public-release` | Expected rejection: local-only directory data still requires NIC permission/public evidence policy. |
| APK/AAB byte comparison; `adb install -r`; Activity launch | All three artifact catalog assets exactly equal the final repository asset. Debug install/launch succeeded; Home and Search visually inspected. Emulator left open on Search with 7,347/7,338 counts. |

New tests cover safe/unsafe redirects, exact and ambiguous duplicates, operator/jurisdiction conflicts, transient/denied/TLS/404 checks, old/future observation dates, source fragments/content tampering, review preservation, alias replay/reopening, changed/disappeared sources, rerun idempotency, latest health display, stable-ID matching with inactive/renamed/ambiguous units, administrative calculations and category/jurisdiction uniqueness. Lint/SDK deprecation/tooling warnings remain; no lint error blocked the retry.

Screenshots: [Phase 2B Home](screenshots/phase2b-home.png), [Search and actual counts](screenshots/phase2b-search.png). Artifact sizes/hashes, XML test totals and rule code hashes are in `data/collection/phase2b-2026-10-15/verification.json`.

Remaining: 3,823 explicit pending cases; permitted official district/local-body exports and rename/split/merger histories; zero confirmed lower-level unit links; NIC permission before public distribution; existing accessibility/device/installed-extension/signing/Play gates. Remote catalog updates and broader website redesign remain outside Phase 2B. No later project phase was marked complete. Next highest-value data task: import a permitted LGD unit export and resolve high-impact operator/canonical URL conflicts using official evidence.


## Local application completion — 16 October 2026

Implemented local work across phases 3–9 without publishing or expanding third-party styling permissions. Added compact runtime assets, signed manual catalog delivery/restore, organization browsing, configurable category groups, recent queries and history controls, lazy saved lists, Hindi JanSetu descriptions/guides/privacy, WebView lifecycle and auth/payment/file handoff, Chrome route cleanup and an empty-by-default authorized integration framework. App version: **0.2.0/code 2**. Native government APIs are **not operational**; no provider or credentials are configured. Backend/database/admin roles are not deployed; the single-editor CLI uses local OS/key access.

Actual data is unchanged: **7,347 websites, nine manually reviewed, 7,338 directory listed, 7,114 hosts, zero individual services, two guides and 3,823 pending cases**. The full authoritative catalog remains byte-for-byte identical to the previous Phase 2B release asset. Runtime projection retains every ID/URL, source/status/date and browser permission while reducing JSON from **28,819,757 to 10,758,951 bytes (62.67%)**. The four category groups and Hindi explanations come from separate local data files. Exact search-result IDs for representative queries match the full asset. Version 9 is a signed delivery demonstration of the same records, not a fresh collection pass.

| Command/check | Actual outcome |
| --- | --- |
| `python -m unittest discover -s tools -p 'test_*.py'` | 55 tests passed. Covers editorial pipeline/data rules and compact projection/signature tools. |
| `python tools/check_catalog.py` | Passed on the actual 7,347-record source. |
| `python tools/check_catalog.py --public-release` | Expected rejection: local-development IGOD data still requires reuse permission/public evidence handling. |
| `python tools/package_extension.py`; `node --test extension/core.test.mjs` | Compact projection packaged; seven tests passed, including actual background-listener behavior in a Node VM. No new manifest permissions. |
| `python tools/smoke_extension.py` | Chrome panel search/empty/privacy/guide/batching and reversible readability controls passed using mocked extension APIs. Harmless favicon 404; installed toolbar/activeTab behavior is not covered. |
| `python tools/package_catalog.py update --version 9 --output artifacts/catalog-v9.jansetu` | Signed 14,345,679-byte local package created. Private key is ignored and excluded from artifacts; only its public pin is bundled. |
| Final Gradle JVM/debug/lint/connected/release tasks | Results and APK/AAB hashes are recorded in `data/validation_reports/local-completion-2026-10-16.json`; 17 JVM and nine API 37 tests pass, zero failed/skipped. Lint: zero errors, 17 warnings. Debug APK and unsigned release APK/AAB build successfully. |
| Artifact projection check | APK/AAB/extension catalogs exactly match generated compact runtime bytes; authoritative full source is retained separately. No private signing key in artifacts. |
| `apksigner verify` | Debug signature passes. Unsigned release APK fails signature verification as expected; it is not a production-signed release. |

### Failures found and fixed

The first new organization-navigation test selected both a search field and its result. Filtering for a click action still matched both. A stable semantic test tag on the result card fixed the ambiguity; the nine-test rerun passed. A separate combined Gradle invocation failed with **No connected devices** after the emulator closed. Restarting the same AVD restored it; no app failure or memory-cause assertion was inferred. SDK XML/deprecated icon warnings remain.

The actual Android document-picker flow installed the signed version-9 file with Wi-Fi/mobile data disabled, and version 9 persisted after force-stop/relaunch. Confirmed restore removed the app-private update package. Inspection revealed a temporary mismatch between the old version and newly changed source label during restore; the app now commits catalog/source/recovery together and disables controls until restore completes. This fix was included in the final build/verification run.

### Scope of verification

Signed-update unit tests exercise trust-pin mismatch, tampered payload, replay/stale version, size limits and browser-permission escalation. An Android repository test uses an isolated directory and ephemeral test key to exercise actual AtomicFile persistence, failed-import preservation, bundle restore and corrupted-cache fallback with the real full runtime catalog. Compose tests cover onboarding/navigation, filters, guide/source/privacy actions, favorites/history, recent queries, Hindi guide/navigation and organization selection. Integration-provider tests are synthetic schema/failure tests, not live government API demonstrations.

The API 37 regional benchmark logged 7,347 records/10,758,951 bytes: parse **1,246 ms**, index **1,164 ms**, 30 searches **2,340 ms** in one observed run. These are emulator observations, not a production latency guarantee; startup with an imported update also validates and parses data and is not instant. Sensitive-form logging/JS bridge/TLS-proceed patterns were absent in the inspected app source; this is not a complete security audit.

Screenshots and final manual display checks are listed below. Earlier sections remain historical and their full-asset byte-equality/build counts do not describe the new compact packaging.

Remaining: independent translation/locale review; additional devices and full spoken TalkBack assessment; live TLS/authentication/payment/file/browser flows; installed-extension toolbar/activeTab/live-page regression; permitted district/local-body exports, ownership/queue review and more source-backed services/guides; production update-key handling, Android signing, reuse permission, hosted policy, final store assets/declarations/testing and publication. JanSetu has not been uploaded or deployed. The readability pilot affects one reviewed public page and cannot accelerate government servers or redesign all portals.


### Final local display and offline checks

- **Offline:** Wi-Fi and mobile data were disabled during the final nine-test suite and document-picker tests. The full catalog/search UI loaded, signed version 9 imported, and confirmed restore returned to version 8 with the package removed. Updated catalog/source state stays consistent while restore is in progress. Network-dependent government pages were not represented as available offline.
- **Display:** 720×1600 pixels at 320 dpi (360 dp width), font scale 1.3, dark mode and Hindi were inspected on Settings and Offline catalog. Labels wrapped without overlap; catalog actions remained reachable. This is one virtual-device configuration, not multiple physical devices.
- **TalkBack:** installed service bound successfully and visibly focused the Hindi Back control. This is a limited accessibility smoke check; audible output, complete traversal and gesture behavior were not certified. The service was disabled again afterward.
- **Restored environment:** 1080×2400, 420 dpi, font scale 1.0, light mode, English, Wi-Fi/mobile data enabled, accessibility services unset/disabled. The installed app reports version 0.2.0/code 2, target API 36/minimum 26.
- A final decorative correction replaces source-category `?` symbols with a neutral grid glyph. Debug/release APK and AAB were rebuilt and the changed card visually inspected; the full functional suite above ran immediately before this glyph-only correction. No functional behavior, catalog content or browser permission changed.

Evidence: [signed update installed](screenshots/local-update-installed.png), [restart persistence](screenshots/local-update-restarted.png), [restore to version 8](screenshots/local-restored.png), [Android file picker](screenshots/local-picker-final.png), [offline full search](screenshots/local-search.png), [Hindi narrow Settings](screenshots/local-hindi-narrow-settings.png), [Hindi narrow catalog](screenshots/local-hindi-narrow-catalog.png), [TalkBack focus](screenshots/local-talkback.png), [current category browsing](screenshots/local-browse.png). The manual ADB text-entry attempt encountered an Android keyboard/stylus tutorial and was not counted as a successful query-entry check; Compose tests provide the query/result evidence.

The final regional benchmark varied under emulator load: parse 1,731 ms, index 1,713 ms and 30 searches 2,725 ms. Earlier timings above are another observed run, not a threshold guarantee. Latest artifact sizes/hashes and manual-check scope are in [local validation](../data/validation_reports/local-completion-2026-10-16.json). Operator instructions: [DEPLOYMENT.md](DEPLOYMENT.md); remaining compatibility gates: [COMPATIBILITY.md](COMPATIBILITY.md).

## Phase 3A — 16 October 2026

Implemented [the supplied request](PHASE3A_REQUEST.md) locally in app **0.3.0/code 3**. Baseline inspection confirmed 7,347 websites, nine manual website reviews, 7,338 directory listings, zero individual services and 3,823 pending website cases. [Baseline hashes](../data/validation_reports/phase3a-baseline.json), preservation tests and final artifact checks confirm the authoritative website asset and original website queue remain byte-for-byte unchanged. No nationwide recollection or wholesale website recheck was performed.

Added a separate [25-record service pilot](../data/services/catalog.json), eight relationships to existing website operators, fact-level official evidence, explicit unknown fields, an editorial queue and import/review/revision/publication commands. Two document clarifications were published as version-11 revisions while retaining earlier decisions. Repeating the original publication admitted zero records. Source reviews are attributed to Codex; they are not human certifications or completed application transactions. Existing website verification statuses are preserved.

The existing cached index, lazy result list, regional/category controls and background queries now support distinct service cards, a result-type filter, conversational terms, curated intents and spelling variations. Native service pages show sourced requirements, jurisdiction, source dates, explicit unverified fields and official external links. No government passwords, OTPs or identity documents are collected. No backend, paid API or LLM was introduced. The [future tracking boundary](APPLICATION_TRACKING_BOUNDARY.md) is documented; a tracker is not implemented.

### Actual commands and outcomes

| Command/check | Outcome |
| --- | --- |
| `python -m unittest discover -s tools -p 'test_*.py'` | **58 passed**, latest run 12.397 seconds. Schema, provenance/publication rules, unknown facts, preservation and packaging included. |
| `python tools/check_catalog.py` | Valid merged catalog: 7,347 websites, 25 services, two guides and 19 categories. |
| `python tools/check_catalog.py --public-release` | Expected rejection: directory reuse permission/public evidence gates remain unresolved for publication. Local work continues. |
| `python tools/package_extension.py`; `node --test extension/core.test.mjs` | Merged compact catalog packaged; **seven Node tests passed**. Extension UI remains a website companion; no new manifest permissions or service-search UI claimed. |
| `python tools/smoke_extension.py` | Passed Chrome panel smoke with mocked extension APIs. Harmless favicon 404. Installed toolbar/activeTab behavior is not covered. |
| `python tools/package_catalog.py update --version 12 --output artifacts/catalog-v12.jansetu` | Signed local package created, 14,409,059 bytes; includes all 25 services. Public-pin verification passed without reading the private key during verification. |
| `:app:testDebugUnitTest :app:assembleDebug :app:lintDebug :app:connectedDebugAndroidTest :app:assembleRelease :app:bundleRelease` | Final combined Gradle run **BUILD SUCCESSFUL**, 5m24s. **22 JVM and 11 API 37 tests passed**, zero failures/errors/skips. Lint: **zero errors, 17 warnings**. Debug APK, unsigned release APK and AAB built. |
| `python tools/verify_phase3a.py` | Exact runtime bytes match debug APK, release APK, AAB and extension. Signed v12 payload matches the same catalog with only delivery version increased. Full source/queue hashes preserved; artifacts contain no PEM/keystore file. |
| `apksigner verify` | Debug APK passes (exit 0); unsigned release APK rejects as expected (exit 1, missing signature manifest). No production signing or upload occurred. |

The final Gradle command used JDK 17, `--no-daemon --max-workers=1 --console=plain`, and `JANSETU_SEARCH_BENCHMARK` pointing to the after-results JSON. Test XML totals, APK/AAB sizes and SHA-256 values, unchanged public pin and package verification are recorded in [phase3a-verification.json](../data/validation_reports/phase3a-verification.json). Release artifacts remain unsigned. SDK XML/deprecated icon warnings remain.

### Search measurements and fixes

On the full 7,347-website dataset, eight queries with a warm-up and five measured repetitions produced median/P95 query times of **35.38/86.53 ms before**, and **34.22/66.19 ms after** adding 25 services. These are JVM observations, not Android UI latency guarantees. Index preparation was 235.68 ms before and 79.58 ms after, but order/cache sensitivity prevents attributing that difference to an indexing speedup. The existing Android regional benchmark still exercises the website engine; no new Android service-search P95 is claimed.

All six requested example queries now return a relevant service first within pilot coverage; exact DigiLocker search retains the general portal because its operations remain unreviewed. Actual IDs/rankings and limitations are in [SEARCH_QA_REPORT.md](SEARCH_QA_REPORT.md). An initial generic complaint query ranked the appeal above the initial grievance; an exact curated-intent boost fixed that ordering, and a regression assertion passed. Tamil Nadu certificate/pension services retain their scope and do not appear as Bihar services through regional filtering. Search results do not decide eligibility or application availability.

### Research limits and incomplete work

- Pilot modes: **11 online, nine assisted, five information-only**. Nine entries cover Tamil Nadu delivery only. Birth-certificate printing is explicitly distinct from birth registration.
- **14 eligibility fields and 19 document lists are unknown**. Known lists can be partial. No deadlines, fees or processing times are invented.
- Four unresolved operations/routes remain in the separate service queue: two DigiLocker operations, driving-licence application and the PMEGP direct application route. The 3,823 website cases are unchanged.
- Source collection encountered an obsolete certificate page returning 404, a Udyam curl 403 (official page readable through the browser retrieval), a DigiLocker JavaScript shell without usable current instructions, and a PMEGP application host TLS name failure. TLS was not bypassed; the unconfirmed route has no Apply button. PMEGP's program information has a separate official source.
- Four Aadhaar records are information-only pending application-route review. Scholarship guidelines include older dated PDFs still linked from the current official scheme directory; their age and locators remain visible, and current windows are unverified.
- Broader state/service coverage, independent content/translation review, current application windows, full accessibility traversal and real authentication/payment/file/application transactions remain incomplete. No live status integration exists. The companion's existing limited readability pilot cannot accelerate government servers or redesign every portal.

### Device inspection

Installed the final debug APK on `emulator-5554`, AVD `Medium_Phone_API_37.0`. Search, service summary, requirements, conditional-document unknowns, fees/deadline unknowns, source evidence and official-link controls were visually inspected at 1080×2400/420 dpi, font scale 1.0, English/light mode. The Pragati application-route action launched Chrome at `https://scholarships.gov.in/`; the official NSP page visibly loaded. This confirms a public-page handoff, not a completed application or scheme-specific intake window. Automated Compose tests additionally exercised a natural-language community-certificate query → service page → Back, and exact official information/application URL dispatch.

Evidence: [search](screenshots/phase3a-search.png), [service summary](screenshots/phase3a-service.png), [requirements and unknowns](screenshots/phase3a-service-details.png), [official actions and evidence](screenshots/phase3a-actions.png), [NSP browser handoff](screenshots/phase3a-official-handoff.png). Earlier accessibility/narrow-screen checks are historical and are not claimed as a complete audit of the new service screen.

The Android document picker successfully imported `catalog-v12.jansetu`; [the installed screen](screenshots/phase3a-update-installed.png) shows version 12 and an imported local source. [Search after import](screenshots/phase3a-final-search.png) still shows 25 services and 7,347 websites with distinct service cards. This is the same website/service pilot delivered through the signed-update path, with no automatic download. The emulator remains open on Search for local inspection.

## Phase 3B — 16 October 2026

Implemented [the supplied request](PHASE3B_REQUEST.md), following the [pre-implementation plan](PHASE3B_IMPLEMENTATION_PLAN.md). Current debug app: **0.4.0/code 4**, minimum API 26/target 36. My Applications is a local profile with manual and confirmed-online-service entry, eight user-maintained statuses, optional reference/submission/deadline/notes, append-preserved progress history, filtering/sorting, editing and confirmed individual/all deletion. There is no account, cloud sync or live government-status API.

ApplicationRepository/SQLiteOpenHelper stores encrypted application/history payloads under `noBackupFilesDir`. Android Keystore AES-256-GCM authenticates row identities; references are masked by default. Schema v2 additively extends the defined v1 layout with delivery deduplication. Corrupt records are preserved with a warning and deliberate removal controls; database failure never silently creates an empty profile. Backup and device-transfer exclusions are explicit. Tracker records are independent of catalog imports/restores. See [APPLICATION_TRACKER_REPORT.md](APPLICATION_TRACKER_REPORT.md) and [future sync boundary](APPLICATION_TRACKING_BOUNDARY.md).

Baseline and final SHA-256 checks confirm **all four source files are unchanged**: authoritative website catalog, website review queue, service catalog and service review queue. Counts remain **7,347 websites, nine original manual website reviews, 25 services and 3,823 pending website cases**. Runtime catalog bytes are unchanged. Service routes, source evidence, review decisions and third-party browser/style permissions are preserved.

### Actual commands and final outcomes

| Command/check | Outcome |
| --- | --- |
| `python -m unittest discover -s tools -p 'test_*.py'` | **58 passed**, 15.669 seconds. No Python tests skipped/failed. |
| `node --test extension/core.test.mjs` | **Seven passed**, zero failed/skipped. Existing extension logic is unchanged. |
| `python tools/package_extension.py` | Validated unchanged 7,347/25 merged catalog and refreshed the shared privacy HTML. No extension permission expansion. |
| `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:connectedDebugAndroidTest` | Final combined run **BUILD SUCCESSFUL in 5m41s**. **32 JVM and 26 API 37 tests passed**, zero failures/errors/skips. Lint: **zero errors, 22 warnings**. |
| `python tools/verify_phase3b.py` | Passed source/queue hash preservation, exact debug/extension runtime catalog matching, both bundled privacy-language files, app version metadata, test XML totals, manifest backup/receiver rules and absence of packaged DB/PEM/keystore files. |
| `apksigner verify app/build/outputs/apk/debug/app-debug.apk` | Passed, exit 0. Debug signing only. Earlier Phase 3A release APK/AAB are historical and were not rebuilt for Phase 3B. |
| `adb install -r ...app-debug.apk`; installed package inspection | Installed successfully; reports 0.4.0/code 4, min API 26/target 36. No upload or publication occurred. |

The final Gradle run used JDK 17, `--no-daemon --max-workers=1 '-Pkotlin.compiler.execution.strategy=in-process' --console=plain`. Full artifact size/hash and machine-readable counts are in [phase3b-verification.json](../data/validation_reports/phase3b-verification.json). Debug APK: **20,870,117 bytes**, SHA-256 `b1dc5129b394c5f64b2f0ad87589c57e8e0e0c32174622104060f35fc77216ff`. Warnings cover dependency/resource/style suggestions; no lint error. SDK XML-version and deprecated icon warnings remain.

### What was tested

- JVM: strict dates/optional unknowns, inconsistent dates, future/past reminder times, closed statuses, filtering/sorting/empty results, malformed official URLs, provenance, privacy-safe object strings, deadline identity, denial/cancellation, replacement, duplicate delivery, deleted/expired records, and timezone/DST/date boundaries. The timezone test changes the provider on the same tracker instance.
- Android repository: CRUD, stale revisions, multiple records for one service, manual records, history preservation, close/reopen persistence, real additive schema migration, corrupt/invalid payload isolation, whole-database non-destructive failure, row-identity authentication, encrypted file contents and cascade deletion. Progress dates are checked across midnight and a timezone change on the same repository.
- Actual signed catalog installation removes the service association in an isolated catalog directory; the saved application snapshot/history remain identical. Bundle restore also leaves them unchanged. No production catalog key or user data was used in that test.
- Compose: natural-language service search → service → prefilled tracker → save; manual creation, invalid-date/consent guards, optional reference masking/reveal, status/progress edit, history preservation, confirmation cancellation and deletion; confirmed bulk deletion without preference changes; saved-state draft restoration; no Apply/Add shortcut for information-only or assisted entries. Test databases/keys are isolated from the real local profile.
- Android reminders: actual unique PendingIntent replacement/cancellation, opaque-ID registry, real generic notification posting and private visibility, stale/deleted-record rejection and persistent duplicate marker. Due-time execution uses a fixed test clock. This is not a claim that a real future alarm was delivered or read.

### Failures found and corrected

An initial bulk-deletion UI test required a stable list tag absent from its built UI; adding the tag and rebuilding resolved it. An intermediate build compiled a newly added timezone fixture against the previous repository constructor and failed; app/test sources were rebuilt consistently. Windows denied a Kotlin daemon spawn; fallback compilation worked, and final builds use in-process compilation. An unquoted Gradle property was split by PowerShell and rejected as an unknown task; the quoted form above works.

Enabling SQLite secure deletion initially used `execSQL` for a PRAGMA that returns a result. That initialization failed and caused 14 dependent emulator tests to fail; errors preserved data instead of resetting it. The final code uses `rawQuery` and consumes/checks the result. All 26 tests passed after the correction. Review also found that a stored default Clock zone could become stale within a running process. Reminder and progress-date logic now reads the current timezone, with same-instance regression tests. No failed/skipped run is counted as a passing final suite.

### Manual device inspection

On the existing API 37 emulator, created a **Tracker demo** manual record through the actual editor, entered a 25 October deadline and the note **Local test only**, opted into local storage and saved it. With notifications disabled, the record persisted and the UI correctly reported that no reminder was scheduled. After force-stop/relaunch, the Applications list still showed the same record/deadline. Granting POST_NOTIFICATIONS and resuming the app reconciled the future reminder; the detail screen reported scheduling only, and the opaque-ID registry contained that record. No real application was submitted and no future delivery is claimed.

Evidence: [empty Applications](screenshots/phase3b-applications-empty.png), [saved record and disabled notification state](screenshots/phase3b-record.png), [after process restart](screenshots/phase3b-restarted.png), [future reminder scheduled](screenshots/phase3b-reminder-scheduled.png).

Inspected the record summary and lower edit/history controls at **720×1600, 320 dpi (360 dp width), font scale 1.3, dark mode**. Content wraps and remains scrollable; the long bottom Applications label wraps across two lines. This is a limited visual/accessibility smoke check, not a full spoken TalkBack audit or multiple physical devices. Evidence: [narrow record](screenshots/phase3b-narrow-large.png), [lower controls](screenshots/phase3b-narrow-actions.png), [narrow list/navigation](screenshots/phase3b-narrow-list.png).

Finally edited the demo record to turn its reminder off and saved it. [The detail state](screenshots/phase3b-reminder-off.png) shows reminders off while keeping its deadline/note/history, and the alarm-ID registry is empty. The demo is labelled **Tracker demo / Local test only** and retained for inspection. Display is restored to 1080×2400/420 dpi, font scale 1.0, English/light mode; the app is left on My Applications. No demo reminder remains scheduled.

Remaining: tracker translation review, full spoken accessibility assessment, additional physical/API-level/OEM devices, reboot/power restriction behavior and real future due-time notification observation. Local backup exclusions mean there is no recovery/sync/export feature; device/key loss or uninstall/data clearing can lose records. Existing catalog-reuse, production signing, hosted policy and store/publication gates remain open.
