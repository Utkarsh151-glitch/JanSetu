# Phase 5 UI QA report

Date: 17 October 2026. App `in.jansetu.app` 0.4.1/code 5 (target SDK 36). Scope: the Phase 5 native UI redesign plus the Phase 5 extension (logo, launcher icon, splash, illustrations, motion, working settings). Design decisions are in [PHASE5_DESIGN_PLAN.md](PHASE5_DESIGN_PLAN.md), asset provenance in [PHASE5_ASSET_CREDITS.md](PHASE5_ASSET_CREDITS.md), open issues in [PHASE5_OUTSTANDING_FINDINGS.md](PHASE5_OUTSTANDING_FINDINGS.md), and the human checklist in [PHASE5_MANUAL_CHECKLIST.md](PHASE5_MANUAL_CHECKLIST.md).

## Result

The final isolated gate (`python tools/run_phase5_quality.py extension-final`) passed on the final source. All tests ran in the separate `in.jansetu.app.qa` package. The preview app's protected profile was byte-identical before and after both the gate and the preview installation.

| Gate | Command | Exit | Actual result |
| --- | --- | --- | --- |
| Python validation/tools | `python -m unittest discover -s tools -p "test_*.py"` | 0 | **71 tests passed** |
| Chrome extension | `node --test extension/core.test.mjs` | 0 | **7 passed**, 0 failed, 0 skipped |
| Android JVM | `:app:testDebugUnitTest` | 0 | **43 tests**, 0 failures/errors/skips (38 existing + 5 new in `Phase5PresentationTest`) |
| Lint | `:app:lintDebug` | 0 | **0 errors, 0 fatal, 14 warnings** (8 UseKtx, 3 GradleDependency, 1 AndroidGradlePluginVersion, 1 ObsoleteSdkInt, 1 NewerVersionAvailable). Down from 23 before this session. |
| Debug and QA APK | `:app:assembleDebug` (and the QA build for instrumentation) | 0 | Debug 21,217,487 bytes, SHA-256 `a27ef5be61b6453a337eb0c45d01ed713d7f6214ee62b61f3ab9d2ac2d495e10`. QA 20,376,811 bytes, SHA-256 `a97d1f0475cbc839704e4e9c7757cc21ff7f4c2c7906bcfad4288eaaf9f0ca74` |
| Isolated instrumentation (API 37) | `:app:connectedQaAndroidTest` | 0 | **41 tests**, 0 failures/errors/skips (34 existing + 6 `Phase5DesignTest` + 1 `Phase5AccessibilityChecksTest`) |
| Protected data | gate hash check against the Phase 4 baseline | — | Website asset, both review queues, service catalog, update public key and extension sources **unchanged** |

Gradle took 552 s. Evidence (logs, XML, lint XML, result JSON) is in `data/validation_reports/phase5/extension-final/`.

### Other runs, reported for completeness

| Run | Outcome |
| --- | --- |
| `phase5/final` (previous session, first-pass redesign) | Passed: 71 / 7 / 38 JVM / 34 Android, 23 lint warnings. Superseded. |
| `phase5/extension-pre-polish` | Passed: 71 / 7 / 43 / 41, 26 lint warnings. The review capture that followed found the landscape and compact-navigation defects, so the code changed and this run is superseded. |
| `phase5/extension-final-attempt-1` and `-2` | **Failed before any instrumented test ran.** Windows refused to start the test runner's Java process ("CreateProcess error=5, Access is denied") after build, lint and JVM tests. A targeted `gradlew.bat` run then succeeded, and the third gate attempt passed. No test failed; the cause appears environmental and was not reproduced. |
| Targeted run 1 (Phase 5 design, ATF, main flow) | 7/8. The new Accessibility Test Framework test **failed**: "no speakable text" on two Home tiles cut off by the screen edge. Fixed with explicit accessible names. |
| Targeted run 2 (ATF) | Passed. |

## Preview package: installation and protected data

`python tools/phase5_install_preview.py` installed the gate-verified debug APK with `adb install -r` only. Record: `data/validation_reports/phase5-extension/preview-install.json`.

- APK identity (`aapt`): `in.jansetu.app`, versionCode 5, versionName 0.4.1, label "JanSetu", minSdk 26, targetSdk 36, icon `res/mipmap-anydpi-v26/ic_launcher.xml`.
- Signature (`apksigner verify`): verifies, APK Signature Scheme v2.
- Install: `Success`. `firstInstallTime` is unchanged at 2026-10-16 22:34:07, confirming an in-place update rather than a reinstall. `lastUpdateTime` is 2026-10-17 05:06:09.
- The installed `base.apk` SHA-256 on the device matches the gate artifact (`a27ef5be…`).
- Launch: `Status: ok`, `LaunchState: WARM`. The redesigned Home rendered with the retained profile ([screenshot](screenshots/phase5/after/preview-installed-home.png)).

| Protected preview item | Before | After |
| --- | --- | --- |
| `no_backup/tracker/applications.db` (encrypted tracker) | `d359193618972893f868f6b28c6c1be112b843594ad2621071d0eee0d5cdca37` | identical |
| `shared_prefs/jansetu_preferences.xml` | `91c4897bb27a6f960465e3a186d3bcd23129f1b44a0c9f1815b4909490dc7bde` | identical |
| `shared_prefs/tracker_alarm_ids.xml` (reminder registry) | `cb5a631ab4b4587a1d60e78f681345ffeb41e3ca7746f94e5112d676cfdb3cd4` | identical |
| `files/catalog-update.jansetu` (imported catalog) | absent | absent |

The same values were recorded at the start of this session (`data/validation_reports/phase5-extension/preview-before.json`) and before and after every gate run. New settings keys (`theme_mode`, `reduce_motion`, `larger_text`) are written only when a person changes a control, so opening the app never rewrites preferences. The preview package was never cleared, uninstalled or seeded. Keystore keys were not touched.

## Screenshots

Captured with `python tools/phase5_screenshots.py before|after` on the same emulator (`Medium_Phone_API_37.0`, 1080x2400, 420dpi, font scale 1.0, light, animations at default). Each run's device state before and after is recorded in its `capture-record*.json`.

- **before/** comes from the retained pre-Phase-5 QA APK (`.local/phase4-baseline-qa.apk`) installed only into `in.jansetu.app.qa`. It is the Phase 3C functional UI; Phase 4 made no other visual change.
- **after/** comes from the final QA build verified by the gate.
- There are **33 matching pairs**: onboarding, home, home-scrolled, search-results, search-empty, website-detail, website-detail-sections, service-results, service-detail, service-detail-actions, service-detail-sections, applications-empty, application-editor, application-editor-errors, application-detail, application-history, applications-list, settings, settings-more, catalog, browser-controls, browser-error (offline), home-dark, search-dark, applications-list-dark, settings-dark (system dark), home-large-narrow, home-large-narrow-scrolled, applications-large-narrow, application-editor-large-narrow (2.0x text at 320dp), home-landscape, home-hindi and settings-hindi.
- **After-only:** help (new screen), settings-dark-in-app and home-dark-in-app (in-app Dark with a light system), splash (Android 12+ system splash with the bridge mark), launcher (drawer with the new icon for both packages), and preview-installed-home.
- Historical sets are kept: `before-retained/` (mixed-date images retained by the previous session), `after-first-pass/` (previous session's first redesign) and `after-review-prepolish/` (the review capture that exposed the defects below).

## Changed screens and interactions

| Screen | Visual change | Interaction change |
| --- | --- | --- |
| App-wide | New theme tokens, light/dark plus in-app override, status labels, badges, notices with icons, section cards, empty states with art | Short screen fades. Reduce motion makes them instant. |
| Launcher, splash, notification | Bridge mark adaptive icon with monochrome layer; Android 12+ splash; launch window for Android 8–11; bridge notification icon with teal accent | — |
| Onboarding | Wordmark, hero (one-time entrance), three plain steps, independence notice | Actions pinned in a footer |
| Home | Wordmark, hero, six labelled editorial Common tasks, recent searches, My Applications card, Material category icons, About card | Task tiles open Search with a query. "How JanSetu works" opens Help. Empty recent/favourite sections are hidden. |
| Search | Chip filters with check marks; Website and Verified service labels; coverage badge; application-mode labels | Clear text button; animated Clear filters; state-filter notice; filter-aware empty state; clearing history now asks for confirmation |
| Website detail | Status and jurisdiction labels; record-type explanation; About, guide and "How we checked" cards; warning notices | Official-site button moved above the fold, with the destination host |
| Service detail | Icon header; coverage badge; Official actions card; icon section cards; numbered procedure | Evidence collapses into an expandable section (header shows source count and date); per-fact source links stay visible |
| My Applications | Status labels with icons; deadline wording (passed / today / due in n days); updated date; illustrated empty state | Filter and sort chips; "Show all statuses" recovery; inline confirmation after remove-all |
| Editor | Grouped sections; supporting hints | Errors under each field plus an announced summary; reference show/hide icon; whole-row checkboxes |
| Detail | Status card; details, reminder, linked-service and notes cards; history timeline | Inline confirmation after save or update; destructive delete is outlined in red and still confirmed |
| Settings | Grouped cards | **New working controls:** Theme, Language (radio), Reduce motion, Larger text, Android display settings link, notification permission and settings; confirmed history clearing |
| Help & guides | New screen | Reachable from Home, Browse and Settings |
| Browser | Single close control, host with a lock glyph, "JanSetu does not control this page" note | Error offers Retry and Open in your browser |
| Bottom navigation | Same five destinations | Icons only (with accessible names) when narrower than 400dp or above 1.3x text |

Navigation destinations, routes, URL policy, browser restrictions, signed-update validation, the restore confirmation and tracker persistence are unchanged. Search still runs off the main thread in a lazy list. The JVM search benchmark gives a median of 40.2 ms and p95 of 61.7 ms per query, against 42.0 ms and 62.1 ms in the previous gate. These are host-JVM figures and are not a device performance claim.

## Defects found and fixed in this session

The Phase 5 interaction fixes are listed in [PHASE5_DESIGN_PLAN.md](PHASE5_DESIGN_PLAN.md#interaction-defects-fixed-separate-from-visual-changes), with traceable IDs in [PHASE5_OUTSTANDING_FINDINGS.md](PHASE5_OUTSTANDING_FINDINGS.md#fixed-during-phase-5-for-traceability). Found by testing or review in this session:

1. ATF found unlabeled Home tiles at the screen edge. Fixed with explicit names; ATF now passes.
2. Landscape hero overflowed onto the heading (`after-review-prepolish/home-landscape.png`). Fixed with a fixed-height band.
3. The compact bottom bar still clipped the selected label at 2x/320dp ("Hom"). Fixed with icons only.
4. Onboarding's primary action fell below the fold. Fixed with a pinned footer.
5. Snackbar feedback could cover bottom buttons. Replaced with inline success notices.
6. Search summary read "1 portals".
7. The inherited Phase 4 ATF test never enabled the checks. It now does.
8. Tracker dates used US order ("Oct 18, 2026"). Changed to day-month-year ("18 Oct 2026").

## Accessibility evidence

- **Accessibility Test Framework:** `Phase5AccessibilityChecksTest` runs ATF (enabled explicitly) over Home, Search with service results, service detail, website detail, Settings, Help and onboarding, in light and dark. It passes with no ATF errors.
- **Semantics tests:** `Phase5DesignTest` verifies that decorative art has no description or role while informative art is described; that the brand name is a single heading; that radio and switch state is exposed; and that settings take effect and persist.
- **Computed contrast (WCAG ratio):** onSurface/background 14.64 (light) and 14.99 (dark); onSurfaceVariant/card 5.97 and 9.06; primary/white 7.58; primary/card (dark) 9.33; notice containers 8.18–12.77 (light) and 7.17–7.86 (dark); error text/background 6.25 and 10.53; outline/background 3.15 and 6.86 (non-text). All text pairs exceed AA 4.5:1.
- **Responsive captures:** 2.0x text at 320dp, landscape, dark and Hindi (listed above). Nothing clipped; all actions are reachable by scrolling.
- **Not run (human required):** spoken TalkBack traversal, physical keyboard and Switch Access, real devices and Android 8–14. See the manual checklist. Automated checks do not establish full accessibility conformance.

## Settings and motion verification

| Requirement | Evidence |
| --- | --- |
| Theme shows its current value, changes colours and persists | `settingsControlsShowStateChangeBehaviourAndPersist`: selected-state assertions, a background-colour probe, and a new `LocalSettings` read as after a restart. Captures `settings-dark-in-app`, `home-dark-in-app`. |
| Language persists and applies | Same test (Hindi header) plus `LocalWorkflowsTest`; captures `home-hindi`, `settings-hindi` |
| Reduce motion has a real effect | `reducedMotionMakesExpandableSectionsChangeImmediately` (height already final after 3 frames) vs `fullMotionAnimatesExpandableSectionsOverSeveralFrames`; JVM `motionSpec` test |
| Larger text has a real effect and is capped | Font-scale probe in the settings test; JVM cap test |
| Settings changes do not touch tracker data | `changingSettingsDoesNotTouchTrackerRecordsOrHistory` |
| Reminder permission explained; never auto-enabled | Settings card with live state, request and settings links. Per-application reminder defaults are unchanged (off). Manual check pending for a denied permission on Android 13+. |
| Branding renders in both themes | `logoAndIllustrationsRenderVisiblyInLightAndDarkThemes` (pixel sampling of logo and all 12 illustrations) |

## Known limitations and release gates

These remain open and unresolved: NIC/public-data reuse permission; human TalkBack, keyboard and multi-device review; independent Hindi translation review; brand trademark and legal review; production signing, hosted policies and store declarations; real-device reminder delivery; and the remaining 14 lint warnings. Full list: [PHASE5_OUTSTANDING_FINDINGS.md](PHASE5_OUTSTANDING_FINDINGS.md) and [KNOWN_LIMITATIONS.md](KNOWN_LIMITATIONS.md). No deployment, store upload, production signing or change to any government website took place. Phase 6 has not started.
