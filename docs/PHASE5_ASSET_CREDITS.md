# Phase 5 asset credits and provenance

Date: 17 October 2026. Scope: every logo, icon, illustration and launch image added to the JanSetu Android app in Phase 5.

## Summary

All JanSetu brand and illustration artwork was drawn for this project as hand-written vector paths during Phase 5. No artwork was downloaded, traced, generated from a third-party image or copied from another product. No photographs are used.

The only third-party visual assets are the **Material Symbols / Material Icons** glyphs that were already a project dependency (`androidx.compose.material:material-icons-extended`). They are used for action, category and status icons.

| Asset | Where it lives | Origin | Licence / rights |
| --- | --- | --- | --- |
| JanSetu bridge mark (icon-only logo) | `app/src/main/java/in/jansetu/app/ui/Brand.kt` (`logoMarkVector`), `app/src/main/res/drawable/jansetu_mark.xml`, `docs/brand/jansetu-mark*.svg` | Original, drawn for JanSetu in Phase 5 | Project-owned artwork; same terms as the JanSetu repository |
| JanSetu wordmark | `Brand.kt` (`JanSetuWordmark`), `docs/brand/jansetu-wordmark*.svg` | Original. The name is set as live text in the system font so it scales with Android font size | Project-owned |
| Adaptive launcher icon (foreground, background, monochrome) | `res/drawable/ic_launcher_foreground.xml`, `res/drawable/ic_launcher_monochrome.xml`, `res/mipmap-anydpi-v26/ic_launcher.xml`, `res/values/colors.xml` | Original. Same bridge geometry as the mark | Project-owned |
| Launch/splash branding | `res/values-v31/styles.xml`, `res/values-night-v31/styles.xml` (Android 12+ system splash), `res/drawable/launch_window.xml` (Android 8–11 launch window) | Original. Reuses the mark | Project-owned |
| Reminder notification small icon | `res/drawable/ic_deadline_notification.xml` | Original monochrome bridge mark. It replaced a generic clock glyph | Project-owned |
| Home/onboarding hero illustration (citizen, bridge, generic service office) | `Brand.kt` (`Illustration.HERO`) | Original | Project-owned |
| Task illustrations: certificates, education, employment, pensions, business support, complaints | `Brand.kt` (`Illustration.CERTIFICATES` … `GRIEVANCES`) | Original | Project-owned |
| Empty-state and help illustrations: no results, no applications, nothing saved, offline/error, help signpost | `Brand.kt` (`Illustration.NO_RESULTS` … `HELP`) | Original | Project-owned |
| Action, category and tracker-status icons | Material Icons via `material-icons-extended` (already in `app/build.gradle.kts`) | Google Material Design icons | Apache License 2.0 |

No new Gradle dependency, font file, remote URL or network fetch was added for visual assets. All artwork is bundled and renders offline.

## Design constraints applied

- **No official imagery.** The mark is an arch bridge joining two dots, standing for a citizen and a public service. It contains no Ashoka emblem, lion capital, chakra, national flag colours in flag order, state or department seal, or any government logo. The "service office" in the hero is a generic building with no emblem, flag or text.
- **No implied endorsement, eligibility or approval.** No illustration shows a certificate being granted, a tick on an official document, an approval stamp or a person receiving a benefit. The certificate tile's check badge sits on a blank sheet and represents "documents" as a topic.
- **No fabricated people or testimonials.** Figures are simple geometric silhouettes with no faces, names or quotes.
- **Theme-aware colour.** In-app artwork is built in Kotlin from the active theme's colours (`JanSetuTheme.colors`), so the same asset adapts to light and dark mode. Launcher and splash assets use fixed brand teal `#0B5D66` with white and amber, and the system tints the monochrome icon.
- **Small-size legibility.** The mark uses three strokes and two dots on a 48-unit grid with no detail thinner than 2.2 units, so it stays recognisable at 24dp (notification) and in launcher masks. The launcher foreground keeps all content inside the 66dp adaptive-icon safe zone.

## Accessibility of imagery

- Decorative art (hero, task tiles, empty-state art, the logo mark beside the wordmark) is drawn with `contentDescription = null`. It therefore has no description and no image role, and TalkBack skips it. `Phase5DesignTest.decorativeArtIsHiddenFromScreenReadersButInformativeArtIsDescribed` checks this.
- The app name is real text and is marked as a heading once per screen, so TalkBack reads it once instead of reading both a logo and the name.
- `JanSetuIllustration` accepts a `contentDescription` for any future informative use. The test verifies that a described illustration is exposed.

## Fallbacks

Illustrations are compiled vector code, not loaded resources, so they cannot be missing at runtime. Category icons fall back to a generic Material "category" icon when a catalog category has no mapped icon (`categoryIcon` in `Screens.kt`). A future catalog category therefore never shows a blank tile.

## Photographs

None were added. A photograph of citizens or offices would add download size and offline storage, would need documented model and property releases, and could suggest a specific state, office or scheme. The vector hero conveys "citizen reaches a public service" without those risks. If photographs are considered later, record the source URL, photographer, licence (for example CC BY 4.0 with attribution text) and release status in this file before bundling them.
