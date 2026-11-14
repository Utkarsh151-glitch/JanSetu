# Phase 4 quality, reliability and accessibility report

Date: 17 October 2026 (local development only)  
Scope: the supplied [Phase 4 request](PHASE4_REQUEST.md), app 0.4.1/code 5, 7,347 websites and 52 services.

## Outcome

The Phase 3C baseline was first reproduced in an isolated `in.jansetu.app.qa` application. The final hardened build then passed every automated gate. The normal preview app (`in.jansetu.app`) was force-stopped during QA and its encrypted tracker database, preferences, reminder registry and imported-catalog file had identical SHA-256 values before and after the final run.

| Gate | Result |
| --- | --- |
| Python validation/tool tests | PASS - 71 tests |
| Chrome companion extension tests | PASS - 7 tests |
| Android JVM tests | PASS - 38 tests, 0 failures/errors/skips |
| API 37 isolated QA instrumentation | PASS - 34 tests, 0 failures/errors/skips |
| Debug lint | PASS - 0 errors, 0 fatal issues, 22 pre-existing warnings |
| Debug APK | PASS - 20,737,425 bytes, SHA-256 `c8b98a4adcf2e2a670c2bfbce23708de241b29aff18c68a3372ddabf7f5c4857` |
| QA APK | PASS - 19,899,007 bytes, SHA-256 `44851146386bbcfb7130794693495b975c5758bcb7f0bd59cd6e09e7900045ff` |

The original Phase 3C baseline was also recorded: 68 Python, 7 extension, 35 JVM and 28 Android tests all passed. Its preview-profile hashes also matched before and after. Both baseline and final logs, fresh XML results and lint XML are retained under `data/validation_reports/phase4/`.

The final debug APK was then installed over the retained preview package with `adb install -r` and launched successfully. Its encrypted database, settings and reminder-registry hashes remained identical before and after installation. `apksigner verify` passed (v2), and `aapt` confirmed package `in.jansetu.app`, label `JanSetu`, version 0.4.1/code 5, min SDK 26 and target SDK 36.

## Defects reproduced and fixed

| Finding | Resolution | Regression evidence |
| --- | --- | --- |
| An encoded or query/fragment login, payment, upload or application route could remain inside the read-only browser. | `BrowserPolicy` repeatedly normalizes escaped URL components; sensitive journeys and escaped CR/LF or backslashes are refused for embedded browsing. HTTP main-frame errors show recoverable guidance. | `BrowserPolicyTest` covers same-host authentication, payment, nested encoding, query/fragment and control-character cases. |
| Reading tracker ciphertext after Keystore-key loss created a fresh replacement key. | Decryption now requires an existing key. Ciphertext is retained and exposed as unreadable instead of being overwritten or made misleadingly unrecoverable. | `lostKeyReadPreservesCiphertextAndDoesNotGenerateAReplacementKey`. |
| A saved application could still expose an application button after the current catalog stopped confirming that route. | Saved evidence/history remains available, while the application action is withheld unless the current verified route exactly matches the snapshot. | `aSavedSnapshotCannotOfferAnApplicationActionAfterCurrentRouteBecomesUnconfirmed`. |
| At 2x text in a short 320dp viewport, tracker rows were below non-scrollable controls. | The tracker heading, controls, rows and destructive actions now share one `LazyColumn`; every row remains reachable. | `shortViewportAtTwoTimesTextKeepsSavedApplicationsReachable`. |
| Delhi and Puducherry were stored with historical `State` source values, but the citizen level filter needed their real Union Territory jurisdiction. | Search presentation maps those two protected source values to Union Territory without changing catalog JSON. | `unionTerritoryFilterUsesActualJurisdictionWithoutChangingSerializedFacts`. |
| Earlier connected tests could target the preview package. | The QA build type has application ID `in.jansetu.app.qa`, and all instrumentation runs under its separate UID, files, preferences, alarms and Android Keystore. | The final harness hashes the preview profile before/after and refuses a changed result. |

## End-to-end checks

Automated journeys cover fuzzy portal search, type/category/state filters, service facts and unknowns, official external handoff with Back-context preservation, empty/unsupported state behaviour, application create/edit/history/masking/delete flow, signed catalog update/restore preservation, tracker draft restoration, reminder scheduling/reconciliation logic, and catalog boundaries.

The QA build uses only synthetic records and keys. No credentials, OTPs, identity documents, payment or live government submission was entered. Catalog source data, queues, signing public key and extension permissions/style profile are hash-protected by the Phase 4 tests and were unchanged.

## Privacy and recovery

Tracker records remain local AES-GCM encrypted data in no-backup storage. They have no account, cloud sync, analytics or automatic government-status lookup. A missing or invalidated device Keystore key can make a record permanently unreadable; Phase 4 preserves that ciphertext rather than resetting it. The reviewed recovery boundary and a password-encrypted export proposal are in [PHASE4_RECOVERY_REVIEW.md](PHASE4_RECOVERY_REVIEW.md).

## Accessibility and compatibility

Passing evidence includes the 2x-text/320dp tracker regression, labelled-control Android Accessibility Framework smoke check (*Phase 5 correction: this test did not call `enableAccessibilityChecks()`, so the framework did not actually run. Phase 5 enabled it and added real checks on seven screens; see [PHASE5_UI_QA_REPORT.md](PHASE5_UI_QA_REPORT.md)*), Compose semantics checks already used by the UI suite, keyboard-accessible Compose controls, and existing portrait/dark/large-text captures. Visual evidence includes the [narrow, large-text tracker capture](screenshots/phase3b-narrow-large.png), [dark large-text capture](screenshots/dark-large.png) and [local TalkBack state capture](screenshots/local-talkback.png). The exact repeatable operator steps and restoration instructions are in [PHASE4_MANUAL_CHECKLIST.md](PHASE4_MANUAL_CHECKLIST.md).

Manual spoken TalkBack traversal, audible announcements, physical keyboard navigation, real device matrix coverage, OEM notification behaviour, reboot/Doze delivery, live TLS failure injection and real government authentication/payment/upload compatibility are **not run**. They require direct human observation or constrained external systems and must remain release gates.

## Performance

The baseline three-repeat API 37 emulator sample is retained in `data/validation_reports/phase4/performance-before.json`. It uses a force-stopped QA process with OS/disk caches retained, a 1080x2400/420dpi emulator, English/light mode, 1.0 font scale, 7,347 websites, 52 services and an empty QA tracker. Timings include ADB/UIAutomator polling, so they are upper bounds for the scripted journey and are not rendering or server-latency claims.

| Median before | Time |
| --- | ---: |
| Cold catalog readiness | 36.33 s |
| Search interaction | 13.73 s |
| Open service | 6.52 s |
| Empty tracker | 6.26 s |
| Signed import | 23.91 s |
| Restore bundled catalog | 19.09 s |

The attempted matching post-change repetition was invalidated by transient ADB/UI-automation failures while its QA-only signed-update fixture was running. It is excluded from this report rather than compared with the baseline. Phase 4 made safety and layout fixes, not an optimization change, so no performance improvement is claimed.

## Remaining release blockers

- Obtain NIC/public-data reuse permission before public distribution.
- Complete human TalkBack, keyboard, multi-device, language and visual accessibility review.
- Test browser failure/login/payment/upload paths only with safe local fixtures or approved non-production accounts.
- Observe a real future QA-only notification on supported physical devices, including reboot, timezone and battery-management variants.
- Resolve or formally triage the 22 remaining lint warnings, acquire production signing, host final policy documents and complete store declarations.

No deployment, production signing, store upload or change to a government website occurred in this phase.
