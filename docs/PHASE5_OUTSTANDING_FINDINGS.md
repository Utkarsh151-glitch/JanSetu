# Phase 5 outstanding visual and accessibility findings

Date: 17 October 2026. Build: 0.4.1/code 5, after the Phase 5 redesign and extension. Environment: API 37 emulator `Medium_Phone_API_37.0`, 1080x2400, 420dpi. All reproduction steps use the isolated `in.jansetu.app.qa` package and synthetic data.

Severity: **High** blocks a citizen task or misleads, **Medium** degrades a task or accessibility, **Low** is cosmetic or a verification gap.

## Open

| ID | Area | Finding | How to reproduce | Severity | Status |
| --- | --- | --- | --- | --- | --- |
| P5-A1 | Accessibility | Spoken TalkBack traversal, announcements and heading navigation have not been heard by a person. Automated semantics and Accessibility Test Framework checks pass, but they cannot confirm speech quality or order. | Enable TalkBack, then follow the TalkBack section of [PHASE5_MANUAL_CHECKLIST.md](PHASE5_MANUAL_CHECKLIST.md). | Medium | Not run (release gate) |
| P5-A2 | Accessibility | Physical keyboard and Switch Access focus order and the visible focus indicator are unverified. | Connect a keyboard to the emulator or a device. Tab through Home, Search filters, Settings and the editor. | Medium | Not run |
| P5-A3 | Accessibility | Colour contrast is supported by computed palette ratios only (all text pairs ≥ 5.97:1 light and ≥ 7.17:1 dark). The ATF validator ran without screenshots, so no rendered-pixel contrast check was made. | Run ATF with screenshot capture enabled, or measure the captures in `docs/screenshots/phase5/after/` with a contrast tool. | Low | Open |
| P5-A4 | Accessibility | Home task and category tiles now carry an explicit accessible name. This fixed an ATF "no speakable text" result for tiles cut off at the screen edge. TalkBack should read each tile once; confirm there is no duplicate reading. | TalkBack on Home: swipe to a Common task tile and a category tile. | Low | Needs manual confirmation |
| P5-L1 | Localisation | Phase 5 Hindi strings are JanSetu's own wording with no independent translation review. | Settings → Language → हिन्दी. Review Home, Search, Settings, Help and onboarding text. | Medium | Open |
| P5-L2 | Localisation | Service details and My Applications remain English in Hindi mode. This is stated in Settings, not hidden. | Switch to Hindi and open a service and My Applications. | Medium | Known limitation |
| P5-L3 | Localisation | Home Common-task shortcuts search with English queries, so the search field shows English text in Hindi mode. | Switch to Hindi and tap "आय प्रमाण पत्र" on Home. | Low | Open |
| P5-V1 | Visual | At 2x text on a 320dp-wide display, long headings (for example "What government service do you need?") wrap to four lines, and the wordmark tagline wraps to three. Everything stays reachable by scrolling, nothing is clipped, and the decorative hero is hidden at large text. | `adb shell settings put system font_scale 2.0; adb shell wm density 540`, then open Home. Restore afterwards. | Low | Accepted (user-chosen scale) |
| P5-V2 | Visual | In compact mode (narrow screen or font scale > 1.3) the bottom bar shows icons without text labels. Accessible names remain and every screen has a visible heading, but sighted users must recognise the icons. | Same as P5-V1, or `wm density 540` at 1.0x text. | Low | Accepted trade-off; revisit with user research |
| P5-V3 | Visual | Android 8–11 launch window, Android 12+ splash on real devices, the Android 13+ themed (monochrome) launcher icon, and edge-to-edge bars on Android 8–14 were not observed on those versions. Only API 37 was available. | Install the debug APK on API 26–34 devices and cold-start it. Enable themed icons on Android 13+. | Medium | Not run |
| P5-V4 | Visual | The reminder notification's new bridge small icon and teal accent have not been seen on a delivered notification. | QA-only probe from the Phase 4 checklist reminder plan. | Low | Not run |
| P5-M1 | Motion | Reduce motion removes JanSetu's own transitions and decorative motion. Material component micro-feedback (ripples, switch thumb, menus) follows Android's animator scale, not the in-app switch. | Turn on Reduce motion, then toggle a switch and open a filter menu. | Low | By design; documented |
| P5-B1 | Brand | The logo and wordmark have had no trademark search or legal review. | n/a | Medium | Release gate |
| P5-T1 | Test evidence | The inherited Phase 4 "Accessibility Framework smoke check" did not enable ATF before Phase 5, so it checked nothing. It is fixed, and Phase 5 adds real ATF checks on seven screens. Earlier phase reports that cite it overstate that evidence. | See `Phase4AccessibilityAutomationTest` and `Phase5AccessibilityChecksTest`. | Low | Fixed in code; historical reports annotated in QA_REPORT.md |

## Fixed during Phase 5 (for traceability)

| ID | Finding | Fix | Evidence |
| --- | --- | --- | --- |
| P5-F1 | Bottom-bar labels broke mid-word at 2x text on 320dp ("Hom e", "Sear ch") in the baseline. The first-pass fix still clipped the selected label ("Hom"). | Compact mode shows icons only, with accessible names. | `before/applications-large-narrow.png`, `after-review-prepolish/home-large-narrow.png`, `after/home-large-narrow.png` |
| P5-F2 | In landscape, the Home hero illustration overflowed its slot and covered the heading (first extension build). | Fixed-height illustration band with fit scaling. | `after-review-prepolish/home-landscape.png` vs `after/home-landscape.png` |
| P5-F3 | Home "View My Applications" button sat outside the hero card padding (first-pass redesign). | Dedicated My Applications card. | `after-first-pass/home.png` |
| P5-F4 | Editor validation errors appeared only at the bottom of the form. | Error under each field, plus a live-region summary. | `before/application-editor-errors.png` vs `after/application-editor-errors.png` |
| P5-F5 | Clearing search and viewing history had no confirmation. | Confirmation dialog in Search and Settings. | Code: `JanSetuConfirmDialog` uses in `Screens.kt` |
| P5-F6 | Status and navigation bar icon contrast on Android 8–14 followed the system, not the in-app theme. | `enableEdgeToEdge()` plus theme-driven `WindowCompat` contrast. | Code: `MainActivity.kt` (not observed on Android 8–14; see P5-V3) |
| P5-F7 | Onboarding's primary action fell below the fold once illustration and steps were added. | Pinned action footer. | `after/onboarding.png` |
| P5-F8 | Result summary read "1 portals". | Singular and plural wording. | `after/search-results.png` |
| P5-F9 | Baseline: tapping Home could restore a stale Search screen. | Fixed in the first-pass redesign (`restoreState = destination != "home"`). | `MainFlowTest` |
