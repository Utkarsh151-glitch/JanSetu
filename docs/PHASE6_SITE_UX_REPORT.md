# Phase 6A — government website UX audit and intervention plan

Date of evidence: 17 October 2026. This report records what was observed, decides what JanSetu should do for each exact page, and ranks the improvements to investigate first. It applies only to the pages listed. **No new styling was added to any live government website**, no allowlist was widened, and no catalog, service fact, review queue or update key was changed.

Companion records: per-page evidence in [page_audits.json](../data/page_audits.json); page decisions in the machine-checked [site_interventions.json](../data/site_interventions.json); the updated [audit](PORTAL_UX_AUDIT.md), [intervention matrix](UI_INTERVENTION_MATRIX.md) and [manual checklist](UX_AUDIT_MANUAL_CHECKLIST.md).

## Outcome in brief

| Decision | Exact pages |
| --- | --- |
| **Investigate first (low risk, JanSetu-side)** | National Portal home in JanSetu's in-app browser (`https://www.india.gov.in/`). Make page text follow the person's Android font size and keep their place when the screen rotates. This is class **B**. |
| **Investigate (page-specific CSS, not allowlisted)** | TN e-Sevai service list (`https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx`). One rule lets the clipped table scroll sideways. This is class **C** and helps desktop readers who zoom in; phone readers need JanSetu guidance instead. |
| **Guidance rather than styling (D)** | RTI guidelines (`https://rtionline.gov.in/guidelines.php?request`), RTI home, NSP home, DigiLocker home, Coimbatore home. |
| **Remain unchanged** | NSP application, RTI login, CPGRAMS lodge (all **E**). The Chennai directory destination (**F**: needs URL repair, not CSS). Lakshadweep home (**A**: no need shown). The RTI guidelines' acknowledgement checkbox and Submit/Cancel. |
| **Existing pilot to withdraw** | NSP `/home` "Readable text". It measurably changed text size but not the page's documented problems. |

## Method and evidence types

Every observation is labelled by how it was obtained. The three kinds are not interchangeable.

| Evidence type | What was done | Where |
| --- | --- | --- |
| **Live page, automated** | `tools/phase6_site_audit.py`: one read-only load per page in local headless Chrome 155. TLS validation was on, Chrome kept its own headless user agent, and nothing was clicked or typed. Metrics were taken at 390×844, then the same document was resized to 320px (WCAG reflow width) and 195px (200% zoom on a 390px phone). Further read-only probes: `phase6_page_structure.py` (mobile viewport handling) and `phase6_tn_desktop_zoom.py`. | [screening_runs/2026-10-17.json](../data/screening_runs/2026-10-17.json), `data/validation_reports/phase6a-*.json`, `docs/screenshots/phase6/*-390.png`, `*-zoom200.png` |
| **Live page, real Android browser** | Emulator (API 37, 1080×2400, 420dpi). Chrome 145 opened the two selected pages by URL; only scroll gestures were used. JanSetu's isolated QA app opened the National Portal in its in-app WebView; only JanSetu's A/A+ control was used. Android font scale 1.0 and 2.0, plus landscape; all settings restored. | `docs/screenshots/phase6/*-android-*.png`, [android-portal](../data/validation_reports/phase6a-android-portal.json), [android-chrome](../data/validation_reports/phase6a-android-chrome.json) |
| **Static fixture (mocked)** | Three local HTML files modelled on the measured structures, used to test stylesheet mechanics and reversibility. No live page involved. | `tools/fixtures/phase6/`, `tools/test_phase6_fixtures.py` |
| **Human review** | **Not performed.** No TalkBack or screen-reader listening, keyboard pass, or moderated citizen task. | Release gates below |

Interpretation rules: a single load is a dated observation, not a benchmark or a claim about every visitor. Font-size and contrast counts are automated flags (contrast ignores background images). The headless pilot comparison inserts the stylesheet with author origin, which approximates the extension's user-origin insertion. HTTP 403 from the National Portal and 500 from UIDAI are **access results, not UI findings**: neither page is called defective on that basis.

## Problem definitions for the investigated pages

### National Portal home in JanSetu's in-app browser — `https://www.india.gov.in/` (existing pilot)

- **Purpose:** the national directory of government information and services; the only host JanSetu opens in its own read-only WebView.
- **Observed:** 17 Oct 2026, 15:45–15:48 IST. QA app, in-app WebView at A (100%) and A+ (125%), Android font 1.0 and 2.0, then landscape.
- **Issues:**
  1. **Confirmed, high:** page text does not follow the Android font size. At font 2.0 JanSetu's frame doubled but the page stayed at 100% ([font 1.0](screenshots/phase6/india-portal-android-a100-font1.png) vs [font 2.0](screenshots/phase6/india-portal-android-a100-font2.png)); A+ reaches 125% at most.
  2. **Confirmed, medium:** at A+ the header shifts sideways (emblem off-centre, menu icon clipped) and the consent dialog fills about half the screen ([A+](screenshots/phase6/india-portal-android-a125-font1.png)).
  3. **Confirmed in one session, low:** the site's floating "+" button overlaps the corner of "Accept all cookies".
  4. **Confirmed, medium (JanSetu's own defect):** rotating or changing font size recreates the WebView and reloads the page, losing the reader's place ([landscape shows the site's loading screen](screenshots/phase6/india-portal-android-landscape-font1.png)).
  5. **Confirmed, low (JanSetu frame):** at font 2.0 the "JanSetu does not control this page" note takes three lines.
- **Who is affected and which task:** people who have raised Android's font size and read National Portal pages inside JanSetu. Finding information on the page is impaired because text stays small unless they discover A+.
- **Headless Chrome:** HTTP 403 again (as on 14 October), so no Chrome metrics.

### NSP home — `https://scholarships.gov.in/home` (existing Chrome pilot)

- **Purpose:** scholarship discovery landing page; the only page the extension may restyle.
- **Observed:** 17 Oct 2026, headless Chrome at 390px and 1280px, with and without `extension/readability.css`.
- **Measured effect of the pilot:** median body text went from 16px to 16.8px. Median line height went from 1.69 to 1.65 (slightly *tighter*). No text block was under 16px before or after. Document width stayed 402px in a 390px viewport. Four of 18 measured blocks remained below 4.5:1 contrast (minimum 2.13). All 25 controls stayed visible; none was newly covered. Removing the sheet restored identical computed styles. [Before](screenshots/phase6/scholarship-home-390.png) / [after](screenshots/phase6/scholarship-home-readable-390.png); [1280 original](screenshots/phase6/scholarship-home-original-1280.png) / [1280 readable](screenshots/phase6/scholarship-home-readable-1280.png).
- **Conclusion:** the pilot is safe and reversible, but it does not improve the documented problems (overflow and contrast). Applying it is not evidence of a better page. The 14 October measure (8/26 paragraphs under 16px) used `<p>` elements; today's measure uses all text blocks, so the two are not directly comparable.

### RTI guidelines — `https://rtionline.gov.in/guidelines.php?request` (selected)

- **Purpose:** the rules a citizen must accept before the central RTI request form. JanSetu links it as the official information for two services.
- **Observed:** 17 Oct 2026 in emulator Chrome at Android font 1.0 and 2.0, and headless Chrome with mobile viewport handling.
- **Issues:**
  1. **Confirmed, high:** there is no viewport meta tag, so phones lay the page out at 980px and show it at 0.40 scale. 15.3px text appears at about 6px ([emulator Chrome](screenshots/phase6/rti-guidelines-android-chrome-font1.png)).
  2. **Confirmed, high:** the acknowledgement checkbox (15×15) and Submit/Cancel (about 42×21) appear at about 6px and 17×8px.
  3. **Confirmed, medium:** no headings and no `lang`; 21 rules in one list.
  4. **Observed once, medium:** Android font 2.0 did not visibly enlarge the page in Chrome ([font 2.0](screenshots/phase6/rti-guidelines-android-chrome-font2.png)). Chrome's own text-scaling setting was not changed.
- **Who and what task:** central RTI applicants on phones. They need to understand the fee exemption below the poverty line, that Aadhaar/PAN must not be uploaded, the payment modes, to wait 24–48 hours instead of paying twice, and that a first appeal costs nothing. JanSetu's RTI record already states three of these five rules.
- **Why not CSS:** a stylesheet cannot add a viewport meta tag (proved on a fixture), and the page's controls start the request and payment journey.

### TN e-Sevai service list — `https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx` (selected)

- **Purpose:** the official list of 236 e-Sevai services with timelines, fees, documents and apply links. JanSetu links it for seven Tamil Nadu services.
- **Observed:** 17 Oct 2026 in emulator Chrome at Android font 1.0 and 2.0; headless Chrome at 390/320/195px, with mobile viewport handling, and at desktop zoom widths.
- **Issues:**
  1. **Confirmed, high:** the 843px table sits in `div.smooth-overflow` with `overflow-x: hidden`, and `body` and `html` are also hidden. On a 390px phone the *Fee*, *List of Document Required* and *link to apply* columns start off-screen and **cannot be scrolled into view** ([390px](screenshots/phase6/tn-esevai-service-list-390.png), [200% zoom](screenshots/phase6/tn-esevai-service-list-zoom200.png)).
  2. **Confirmed, medium:** on desktop the same clipping begins at 150% browser zoom. At 200% the apply column and most of the documents column are hidden ([desktop zoom record](../data/validation_reports/phase6a-tn-desktop-zoom.json)).
  3. **Confirmed, medium:** median row height is 203 CSS px; in emulator Chrome one service fills most of a screen ([scrolled](screenshots/phase6/tn-esevai-service-list-android-chrome-font1-scrolled.png)).
  4. **Suspected (automated), medium:** 235 of 615 text blocks measured below 4.5:1 (minimum 3.66), mostly link text.
- **Who and what task:** Tamil Nadu residents finding a service's fee, required documents and apply link, on phones or with desktop zoom. The site's A-/A/A+ controls do not help, because larger text does not reveal clipped columns.
- **JanSetu gap:** JanSetu's nine TN service records hold no fees, and seven hold no document list, so JanSetu guidance cannot yet replace this page.

## How the two additional pages were chosen

Eight public-information pages that JanSetu links as "official information" were screened once. Selection weighed severity and confidence of the problem, compatibility (public, no sign-in, stable URL, normal response) and task importance (how many JanSetu services link to the page, and the consequence of misunderstanding it).

| Page | JanSetu services | Screening result | Selected |
| --- | --- | --- | --- |
| Kerala eDistrict FAQ | 8 | Readable at 390px; 357px content at 320px | No: minor |
| Odisha eDistrict home | 8 | Reflows to 195px; 4/19 blocks below 4.5:1 | No: needs manual contrast check |
| **TN e-Sevai service list** | 7 | Columns unreachable; tall rows; low-contrast links | **Yes** |
| UIDAI My Aadhaar | 4 | HTTP 500 (inconclusive) | No: access inconclusive |
| CPGRAMS FAQ | 2 | Readable; 351px at 320px | No: minor |
| **RTI guidelines** | 2 (high consequence) | 980px desktop layout, no viewport meta, tiny gate controls | **Yes** |
| NCS FAQ | 2 | Readable; overflow at 200% zoom | No: minor |
| Income-tax e-PAN help | 2 | 14px text, but reflows at 200% with own A+ controls | No: likely fine with browser zoom (B) |

DigiLocker home (an earlier B prospect) was rechecked rather than newly selected. At 200% zoom its content is 317px wide in a 195px viewport, with 29 controls off-screen, so it fails the B reflow condition.

## Intervention matrix (exact pages)

Classes: A no intervention; B text scaling; C page-specific styling candidate; D JanSetu guidance; E sensitive journey, unchanged; F reliability/navigation repair.

| Exact page | Class | Status | Allowed now | Never |
| --- | --- | --- | --- | --- |
| `https://www.india.gov.in/` | B | investigating | In-app A/A+ (existing), handoff | Restyling the consent dialog or site controls |
| `https://scholarships.gov.in/home` | D | pilot, withdrawal recommended | Readable text (existing, opt-in), restore | Extending to other NSP paths |
| `https://scholarships.gov.in/ApplicationForm/` | E | recommendation | Guidance, handoff | Any styling |
| `https://rtionline.gov.in/` | D | recommendation | Guidance, handoff | Generic text enlargement |
| `https://rtionline.gov.in/guidelines.php?request` | D | investigating | Guidance, handoff | Touching the checkbox or Submit/Cancel |
| `https://rtionline.gov.in/login.php` | E | recommendation | Handoff | Any styling |
| `https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx` | C | investigating | Guidance; rule tested on fixture only | Live use before approval; touching apply destinations |
| `https://www.digilocker.gov.in/` | D | recommendation | Guidance, handoff | Text-scale styling |
| `https://lakshadweep.gov.in/` | A | recommendation | Nothing needed | — |
| `https://www.ccmc.gov.in/` | D | recommendation | Guidance after ownership review | Styling payment or grievance hosts |
| `https://chennaicorporation.gov.in/` | F | recommendation | Verify canonical URL | CSS as a fix |
| `https://pgportal.gov.in/Home/LodgeGrievance` | E | recommendation | Guidance, handoff | Any styling |

The six screening-only pages are recorded as `NEEDS_MANUAL_REVIEW`. A decision for one page grants nothing on any other page of the same domain.

The in-app A/A+ is a browser-level text size, like Chrome's own setting. It applies to whichever public `www.india.gov.in` page is open in JanSetu's browser (sensitive journeys already leave for the external browser). Only the home page has been reviewed at 125%; the registry records this scope explicitly as a user preference rather than page styling.

## JanSetu Assist: proposed controls

A small, consistent set. Nothing here is built or exposed yet unless marked *existing*. Each control may only be offered where the registry allows it for that exact page.

| Control | Contexts | What it changes | Limits | How the person restores the original |
| --- | --- | --- | --- | --- |
| **Larger text** | Android in-app (*existing* A/A+, to extend); Chrome extension as tab zoom (proposal) | WebView `textZoom` or Chrome's per-tab zoom. No page code changes. | Pages that do not reflow will scroll sideways (DigiLocker at 200%; National Portal header at 125%). Offer only where reflow was checked. Chrome zoom scope must be per-tab, not per-site; the API's permission needs must be confirmed. | "Original size" (A) in one tap; tab zoom reset |
| **Comfortable line spacing** | Chrome extension only | Line height and paragraph spacing via a reviewed `:where()` stylesheet | Class C pages only; must show a measured gain (the NSP pilot did not) | Remove the stylesheet |
| **Readable content width** | Chrome extension only | `max-width` on the main text column | Higher layout risk; only for long single-column public text; not yet proposed for any page | Remove the stylesheet |
| **Reading view** | Android in-app, Chrome extension (future) | Shows the reviewed public text in JanSetu's own layout, labelled with source URL and time | Never on pages with forms, gates, sign-in or payment; must not omit content; not proposed for any page this phase | Close the view; the official page is untouched |
| **Restore original** | Every context with a presentation change | Removes JanSetu's change; for overflow rules it also resets scroll position (fixture finding) | Must be visible whenever a change is active | One action, plus reload as a fallback |
| **Contextual guidance** | JanSetu screens and the extension side panel | Plain-language steps, key rules and warnings, with source and check date | Must not invent rules; must name what is unverified; never replaces the official page | Not applicable: the official page is never altered |

JanSetu never changes the government website's code, and Assist must never say it does. Wording such as "changes how this page looks on your device only" and "the official site is unchanged" keeps the brand separate from the site.

## Safety boundaries and how they are enforced

| Boundary | Enforcement |
| --- | --- |
| TLS validation, existing host and URL restrictions | No change. `BrowserPolicy` remains the single in-app gate. A new JVM test covers a trailing-dot host, a punycode lookalike, subdomains, other official hosts, `signup`, an encoded `redirect=/login`, `ftp` and port 444. |
| One exact page per decision | `tools/site_interventions.py` requires HTTPS, no credentials, default port and no fragment, plus an exact host, path and query. It compares the registry against `core.mjs`, `background.js` and the catalog's in-app hosts, and fails on any drift. |
| No styling of E/F pages; restore always available | The validator rejects presentation-altering controls on E/F pages and any change without a restore control in the same context. The extension tests check that E/F pages are refused. |
| No broadened permissions | The extension test pins the manifest permissions to `sidePanel`, `activeTab` and `scripting`, with no host permissions and no content scripts. |
| Stylesheets only adjust typography, focus or one overflow axis | A CSS lint allows only `:where()` selectors and a fixed property list. It rejects `display`, `visibility`, `position`, colours, and any `overflow-x` other than `auto`. |
| Proposals cannot ship by accident | The proposed TN rule lives in `data/site_intervention_proposals/`, outside the extension package. Tests fail if a proposed stylesheet is placed in `extension/` or allowlisted. |
| Approval needs a person | `approved` status requires recorded rules, tests, rollback, reviewer, date and `humanReview: true`. |
| No forms, credentials or private content | Live probes never click, type or submit, and keep Chrome's real headless identity. No password, OTP or identity data was entered or stored. |

## Ranked candidate improvements

| Rank | Improvement | Evidence | Affected task | Risks | Acceptance criteria |
| --- | --- | --- | --- | --- | --- |
| 1 | **In-app browser follows Android font size** for the National Portal. Start `textZoom` from font scale, keep A/A+, keep page state across rotation and font change, and let the frame note collapse at large font. | Android captures at font 1.0 and 2.0; rotation reload | Reading National Portal pages inside JanSetu with large system text | 125% already shifts the header; the consent dialog grows | Text follows 1.3× and 2.0× by default. Consent buttons, site search and the site's accessibility menu remain visible and operable at each size. A returns to 100% in one tap. Rotation keeps page and scroll position. Instrumented test plus TalkBack check. |
| 2 | **RTI guidelines guidance:** a JanSetu "Before you request" card with all five rules, source and check date, then handoff. Add the two missing rules through the editorial service workflow. | Emulator Chrome at 0.40 scale; structure probe; fixture proving CSS cannot help | Filing a central RTI request without paying twice or uploading forbidden documents | Guidance going stale; implying JanSetu replaces the gate | All five rules shown with source and date. In a moderated task, participants answer the BPL-fee and missing-registration-number questions correctly. Official checkbox and Submit/Cancel untouched. |
| 3 | **TN service list:** (a) source fees and documents for JanSetu's TN records (editorial); (b) investigate the single `overflow-x: auto` rule for desktop readers at 150%+ zoom. | Structure probe; desktop zoom probe; emulator Chrome; fixture test | Finding fee, documents and apply link | The rule helps only desktop extension users, not phones; restore must reset scroll; columns might be reachable another way | All 8 columns reachable at 390px and 200% zoom with the rule. Nothing else restyled. Site A± still works. Apply links unchanged. Restore returns identical styles and scroll. Installed-extension, keyboard and screen-reader review by a person. |
| 4 | **Withdraw the NSP Readable text pilot** in the next extension release, or replace it with a rule tied to a documented problem. | Before/after metrics | Avoid implying an improvement that was not measured | Removing a control some people used | Release note; allowlist and registry updated together, with the validator passing. |
| 5 | **Next audit candidates:** income-tax e-PAN help (likely B through browser zoom alone), then Odisha eDistrict contrast, then Kerala FAQ reflow at 320px. | Screening | — | Screening only | Full problem definition before any class |

## Tests added in this phase

| Test | Type | Result |
| --- | --- | --- |
| `tools/test_site_interventions.py` (8) | Validator on real data plus mutated copies: allowlist drift, E page gaining styling, proposal leaking into the allowlist, approval without human review, missing restore, inexact URLs, unregistered in-app host | Pass |
| `tools/test_phase6_fixtures.py` (5) | Static fixtures in local headless Chrome. The pilot keeps every critical control visible, unchanged and uncovered, and restores identical styles. CSS cannot repair a page without a viewport meta. The TN rule restores real wheel scrolling to clipped columns, changes nothing else, and needs a scroll reset on restore. CSS lint rejects hiding, moving and recolouring. | Pass (not skipped) |
| `extension/assist.test.mjs` (6) | Mocked Chrome APIs. Panel allowlist and background route agree on 21 URLs. Only the exact reviewed page is accepted. Allowlist equals the registry's live pilots. E/F pages refused. Manifest permissions pinned. Restore uses the same descriptor as apply. | Pass |
| `BrowserPolicyTest` (+1) | JVM; in-app navigation boundary | Pass |

Full suites on 17 October 2026: Python **84** passed (71 earlier + 13 new); extension **13** passed (7 + 6); Android JVM **44** passed (43 + 1). Both validators pass. The API 37 instrumented suite was **not rerun**: no Android app source changed this phase (only a JVM test was added). The Phase 5 result (41 passed) stands for the unchanged app code. The gate script now includes `assist.test.mjs` for future runs.

## Protected data and the preview app

- The website catalog, both review queues, the service catalog, the update public key and the extension's manifest, background script and readability stylesheet all match the Phase 4 baseline hashes ([check](../data/validation_reports/phase6a/safety-check.json)).
- Emulator work used only `in.jansetu.app.qa`, the emulator's Chrome and read-only intents. The preview app was never cleared, uninstalled or seeded.
- **One observation needs the owner's confirmation.** Before any Phase 6A device step, the preview app's preferences file already differed from its Phase 5 final hash. Its modification time (metadata only; contents not read) was 15:44:08 IST, between the desktop-only screening and the first emulator step, while the preview app was open in the foreground. This most likely reflects someone using the app. The tracker database and reminder registry were unchanged. All Phase 6A device steps left every protected preview hash unchanged ([record](../data/validation_reports/phase6a-preview-profile.json)).

## Limitations and open release gates

- One emulator, one desktop browser build and one session per page. Live pages change, and no result is a claim about all visitors.
- No human review: TalkBack, keyboard, Switch Access, moderated tasks with citizens, and installed-extension behaviour on a real desktop profile remain required before any B or C change ships.
- Automated contrast ignores background images and gradients; contrast findings are marked suspected.
- Emulator Chrome's response to Android font scale was observed once, without changing Chrome's own text-scaling setting.
- The site's own accessibility tools (National Portal, TN e-Sevai A±, income-tax A+) were not exercised in depth.
- Any implementation phase must show improved readability or task completion against these acceptance criteria. Applying a stylesheet or a zoom level is not, by itself, an improvement.
