# JanSetu Phase 5 design plan

Last updated: 17 October 2026. Covers the original [Phase 5 redesign request](#scope) and the **Phase 5 extension** (branding, logo, visual assets, motion and functional settings). Results are in [PHASE5_UI_QA_REPORT.md](PHASE5_UI_QA_REPORT.md); asset provenance is in [PHASE5_ASSET_CREDITS.md](PHASE5_ASSET_CREDITS.md).

## Scope

The work redesigns the native Jetpack Compose UI of the existing app (`in.jansetu.app`, 0.4.1/code 5, target SDK 36). It covers 7,347 website records, 52 published services, natural-language search, the encrypted local tracker, signed offline catalog updates and the restricted in-app browser.

It changes presentation and small interaction defects only. It does not change the website or service catalogs, the review queues, source evidence, the update public key, the tracker database format or encryption, URL policy, browser permissions or the Chrome extension. Phase 6 (improving government websites themselves) is separate and not started.

## Baseline (before Phase 5)

Baseline evidence is in `docs/screenshots/phase5/before/`. It was captured by `tools/phase5_screenshots.py before` from the retained pre-Phase-5 QA build (`.local/phase4-baseline-qa.apk`, SHA-256 `cc23adfe…6e3f87`). The capture used the same emulator, display settings and synthetic data as the redesign capture. That APK is the Phase 3C functional source with the isolated QA configuration. Phase 4 later changed only browser error routing, the tracker list's scroll container and the Delhi/Puducherry level label, so its visuals are representative. An earlier mixed-date set from 13–16 October is kept in `before-retained/` for history, and the first-pass redesign captures are kept in `after-first-pass/`.

### Navigation (audited)

- Bottom bar: Home, Search, Browse, Saved, Applications.
- Settings: gear icon on Home only.
- Detail routes: website, service, in-app browser, application editor and detail, privacy, catalog management and organisations.
- Not discoverable: help, guide listings, and an explanation of record types or application modes.

**Decision:** keep the five-item bottom bar. Its labels are short, task-based and already covered by tests. Changes:

- Add a **Help & guides** destination, reachable from Home ("How JanSetu works"), Browse and Settings.
- Expose Settings, privacy and support from Help as well as Home.
- When the screen is under 400dp wide or font scale is above 1.3, the bar shows icons only. Every icon keeps its accessible label and every destination has a visible heading, so labels no longer wrap or clip (the baseline broke them mid-word; an earlier fix still clipped the selected label).

### Visual issues found

| Area | Issue |
| --- | --- |
| Whole app | Screen-specific spacing, card and header patterns. Some screens had a "Back" text link, others an icon. No status-label, official-link or source-attribution components. Dark mode only followed the system. There was no logo beyond a placeholder launcher arch. |
| Home | Heading "What do you need to do?". A long sequence of portal cards before useful routes. Empty "Recently viewed" and "Favorites" cards always shown. Category glyphs were Unicode symbols (▣ ◎ ◈). No route to My Applications. |
| Search | Filters were plain text buttons in a horizontally scrolling row, with no selected state. No single way to clear filters. Website and service results looked alike. A state service showed only "State · Kerala" in small text. The empty state did not explain state filtering. |
| Website detail | Status, description, guide and source information ran as one flat sequence. The official-site button sat below the guide. Known limitations were shown in red text only. |
| Service detail | "Back" text link. Unknown facts rendered as an empty-bodied card plus a separate message. Evidence was a long text block. Official actions were mixed into the fact sections. |
| My Applications | Full-width outlined buttons for filter and sort. Cards showed status as primary-coloured text only. No overdue or upcoming distinction. No last-updated date. |
| Editor | All errors listed at the bottom, away from their fields. Date format embedded in labels. Reference reveal was a separate text button. |
| Settings | One long column of mixed buttons. Clearing history had no confirmation. No theme, motion or text-size controls. No explanation of reminder permissions. |
| Browser | Close appeared twice (arrow and "Close" text). Errors showed Retry only. |

## Design direction

A calm, civic, clearly independent identity:

- **Colour:** deep teal primary (`#0B5D66` light, `#91D5DB` dark), warm sand for guidance notices, indigo tertiary, and explicit success, warning and error containers. Every text pair passes WCAG AA (4.5:1) in both themes; the computed ratios are in the QA report.
- **Type:** Roboto system font in a fixed scale from headlineLarge 32sp to labelSmall 12sp, using SemiBold or Bold only for headings and labels.
- **Shape and space:** 18dp cards, 10dp chips, a 20dp page gutter and a 720dp maximum content width.
- **Brand:** an original bridge mark (two places joined by an arch) as an icon-only logo, a live-text wordmark, an adaptive launcher icon with a monochrome layer, and splash branding. It uses no seal, emblem, flag or department mark, and every official action names the destination host. The app repeatedly states that JanSetu is independent and not a government app.
- **Imagery:** one restrained hero, six task illustrations, and four empty-state/help illustrations. All are original vectors drawn from theme colours. There are no photographs, no people with faces, and no imagery implying approval or eligibility.
- **Motion:** short fades between screens (180ms in, 120ms out), animated filter-clear visibility, animated expandable evidence sections, a one-time onboarding illustration entrance, and snackbar feedback for tracker save, update and delete. There are no loops or bounces, no per-item animation in long result lists, and no delay before any action.

## Design system (implemented)

| Layer | Implementation |
| --- | --- |
| Theme | `ui/Theme.kt`: light and dark schemes; `JanSetuExtraColors` (success, warning, brand accent, illustration tones); type scale; shapes; `JanSetuSpacing`. `JanSetuTheme(settings)` applies the saved theme, reduced motion and larger text. |
| Motion | `ui/Motion.kt`: `JanSetuMotion` and `motionSpec()` return `snap()` when reduced motion is on, otherwise `tween()`. |
| Brand | `ui/Brand.kt`: `JanSetuLogoMark`, `JanSetuWordmark`, `JanSetuIllustration` (12 illustrations). |
| Components | `ui/JanSetuUi.kt`: page, screen header (icon back), section title, info card, `JanSetuNotice` (info, success, warning, error, each with an icon), `JanSetuStatusLabel`, `JanSetuBadge`, `JanSetuOfficialLinkButton` (always captioned with the destination), `JanSetuSourceAttribution`, `JanSetuFactRow`, `JanSetuSectionCard`, `JanSetuExpandableSection`, `JanSetuConfirmDialog`, `JanSetuLoading`, `JanSetuEmptyState` (optional illustration). |

## Screen changes

- **Home:** wordmark and settings; hero illustration (a fixed 96dp band, hidden above 1.3x text so the task stays first) and natural-language search; six clearly labelled editorial **Common tasks** (not usage-ranked; each verified by a JVM test to reach a published service); recent searches when present; a My Applications card; six category cards with consistent Material icons; bounded reviewed/directory previews (3 + 3); recent and favourite sections only when non-empty; an About card stating independence with a link to Help.
- **Search:** clear-text button; example placeholder; recent-search chips; filters as chips with check marks, current values and a "Filter applied" state description; an animated **Clear filters** chip; a state-filter notice that Central services are hidden; distinct **Website** and **Verified service** labels; a prominent coverage badge ("Kerala only · State", "Central government service"); plain-language application modes; a filter-aware empty state. Search still runs off the main thread in a lazy list.
- **Website detail:** labels for type, review status and jurisdiction; what the record represents (directory listing or manual review); the official-site button near the top with a destination caption; warning notices for inconclusive checks and known limitations; cards for "About this website", the guide (numbered steps) and "How we checked this".
- **Service detail:** icon header; coverage badge and application mode; department; guidance notice; an **Official actions** card (information, the confirmed route only when verified, and Add to My Applications only when allowed); icon section cards with per-fact source links; numbered procedure; unknown facts stated plainly; an expandable **Evidence and review** section whose header shows the source count and check date.
- **My Applications:** subtitle and status notice; filter and sort chips; "Showing n of m"; cards with icon and status label ("Your status: …"), a restrained deadline label (passed, today, due in n days, or later), department and last-updated date; an illustrated empty state with a "Show all statuses" recovery; an error-coloured remove-all action with confirmation.
- **Editor:** grouped sections; field errors under the matching field plus an announced summary; date hints as supporting text; a show/hide icon for the reference; whole-row checkbox targets.
- **Detail and history:** status card ("not a decision from a government department"); details, reminder (scheduling state only, never delivery), linked service and notes cards; a vertical history timeline; an outlined destructive delete with confirmation.
- **Settings:** grouped cards for Appearance (System, Light, Dark), Language (English, हिन्दी, with an honest coverage note), Motion and readability (Reduce motion, Larger text, Android text settings), Region, Reminders and notifications (live permission state, an allow action and settings links), Privacy and your data (tracker recovery limits; history clearing now confirmed), Offline catalog, and Help and about (version).
- **Onboarding:** wordmark, hero (one-time entrance), three plain steps, an independence notice, language, and an optional region. It asks for no personal information.
- **Help & guides (new):** how to search; what each label means, rendered with the real components; safety advice; tracker privacy; links to the existing source-backed guides; privacy policy and support.
- **Catalog management, organisations, browser:** shared headers and cards. The browser bar now has one close action, the host with a lock glyph, and a note that JanSetu does not control the page. The error state offers **Retry** and **Open in your browser**.

## Functional settings (extension)

| Control | Stored as | Effect | Verified by |
| --- | --- | --- | --- |
| Theme: System, Light, Dark | `theme_mode` | Material colour scheme, extra colours, illustration palette and status/navigation-bar icon contrast | `Phase5DesignTest.settingsControlsShowStateChangeBehaviourAndPersist` (colour probe and restart read) |
| Language: English, हिन्दी | `language` (existing) | All `t()` text. Hindi covers navigation, Home, Search, website pages, guides, Settings, Help and onboarding. Service bodies and My Applications remain English, and Settings says so | Same test, plus existing `LocalWorkflowsTest` Hindi checks |
| Reduce motion | `reduce_motion` | Screen fades become instant. Expandable sections, the filter-clear chip and the onboarding entrance use `snap()`. Android's "Remove animations" (animator scale 0) has the same effect | `reducedMotionMakesExpandableSectionsChangeImmediately` vs `fullMotionAnimatesExpandableSectionsOverSeveralFrames`; JVM `reducedMotionReplacesTimedAnimationWithAnImmediateChange` |
| Larger text in JanSetu | `larger_text` | Multiplies JanSetu's font scale by 1.15, capped at 2.0x, without reducing larger system sizes. When off, Android's own (non-linear) scaling is untouched | Device probe in the settings test; JVM cap test |
| Reminders and notifications | Android permission and channel state (not stored by JanSetu) | Shows the live state, requests the permission on Android 13+, opens notification settings, and explains that reminders are per-application and never auto-enabled | Manual check listed in the QA report |

Preferences are written only when the person changes a control. Opening the app never rewrites the preferences file, which keeps the protected preview-profile hash check meaningful. `changingSettingsDoesNotTouchTrackerRecordsOrHistory` verifies that settings changes leave tracker records and history intact.

## Interaction defects fixed (separate from visual changes)

1. **Clear history had no confirmation** in Search or Settings. It now uses `JanSetuConfirmDialog`, and the Settings path confirms with a snackbar.
2. **Home hero "View My Applications" button sat outside the card padding** in the first-pass redesign (visible in `after-first-pass/home.png`). It was replaced by a dedicated card.
3. **The first-pass search screenshot was obscured by the on-screen keyboard toolbar.** The capture script now dismisses the IME before each capture.
4. **Status and navigation bar contrast on Android 8–14 ignored the in-app theme** (light bars stayed light in dark mode). `enableEdgeToEdge()` plus `WindowCompat` icon contrast now follow the effective theme.
5. **Editor errors appeared only at the bottom of a long form.** Each error now sits under its field, with a live-region summary by the Save button.
6. **Onboarding's primary action fell below the fold** once the illustration and steps were added. The actions are now a pinned footer.
7. **Landscape hero overflow and clipped compact-navigation label**, found in the extension review capture (`after-review-prepolish/`). Fixed with a fixed-height illustration band and an icon-only compact bar.
8. **"1 portals" pluralisation** in the search summary.
9. **The inherited Phase 4 Accessibility Framework smoke test never enabled ATF**, so it checked nothing. It now calls `enableAccessibilityChecks()`, and `Phase5AccessibilityChecksTest` checks seven real screens in light and dark. Its first run found unlabeled tiles at the screen edge, which were fixed.

## Implementation sequence (as executed)

1. Inspect existing work. There is no Git repository, so file timestamps, code, tests, reports and screenshots were used instead. The first-pass redesign and its passing gate were preserved and built on.
2. Settings model (theme, reduced motion, larger text), theme tokens, motion policy and brand artwork.
3. Shared components, then Home, Search, website detail, service detail, tracker, Settings, onboarding, Help, catalog, organisations and browser.
4. Launcher icon, monochrome layer, splash and launch window, and notification icon and colour.
5. Tests: 5 JVM and 7 instrumented Phase 5 tests (6 design and settings tests plus 1 Accessibility Test Framework test). Two service tests switched from a "Back" text selector to the icon's accessible name, and the Phase 4 accessibility smoke test now enables ATF.
6. Before and after capture under matching settings, the full isolated QA gate, preview installation with hash comparison, then the reports.

## Deliberate boundaries

No backend, sync, analytics, new permission, network fetch, new UI or animation library, catalog/evidence change, URL-policy change, or browser-permission change. Manual TalkBack traversal, physical keyboard review, real-device and OEM reminder delivery, and public-release approvals remain open release gates.
