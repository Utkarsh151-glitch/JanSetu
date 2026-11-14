# Portal UX and accessibility audit

## 17 October 2026 update (Phase 6A)

The full analysis, decisions and ranked plan are in [PHASE6_SITE_UX_REPORT.md](PHASE6_SITE_UX_REPORT.md). Structured records: [page_audits.json](../data/page_audits.json) (18 pages, each change kept in a dated `history`). Page decisions: [site_interventions.json](../data/site_interventions.json). Screening run: [screening_runs/2026-10-17.json](../data/screening_runs/2026-10-17.json).

**How evidence was gathered:**

- One read-only headless Chrome 155 load per page, at 390px and resized to 320px and 195px (about 200% zoom).
- Emulator Chrome 145 opened the two selected pages using scroll only, at Android font 1.0 and 2.0.
- JanSetu's isolated QA app opened the National Portal in-app, using only JanSetu's A/A+ control.
- No login, form, click on page content or style injection on any new site. The existing NSP pilot stylesheet was applied locally in headless Chrome only, to measure it.

| Page | What was observed on 17 October | Status | Class |
| --- | --- | --- | --- |
| [National Portal home](https://www.india.gov.in/), in JanSetu | Page text ignores Android font 2.0 ([1.0](screenshots/phase6/india-portal-android-a100-font1.png) / [2.0](screenshots/phase6/india-portal-android-a100-font2.png)); A+ shifts the header sideways; rotation reloads the page. Chrome: 403 (inconclusive). | Confirmed (emulator) | B, investigating |
| [NSP home](https://scholarships.gov.in/home) | Pilot stylesheet: text 16 → 16.8px, line height 1.69 → 1.65; 402px overflow and 4 low-contrast blocks unchanged; controls preserved; restore exact. | Confirmed (headless) | D; pilot withdrawal recommended |
| [RTI guidelines](https://rtionline.gov.in/guidelines.php?request) | No viewport meta, shown at 0.40 scale on phones ([emulator Chrome](screenshots/phase6/rti-guidelines-android-chrome-font1.png)); gate checkbox and buttons about 6–17px; no headings. | Confirmed | D (gate controls stay as they are) |
| [TN e-Sevai service list](https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx) | Fee, documents and apply columns clipped by `overflow-x:hidden`, unreachable at 390px and at 150%+ desktop zoom; rows about 203px tall. | Confirmed | C, investigating (not allowlisted) |
| [RTI home](https://rtionline.gov.in/) | Still 980px wide at 390/320/195px; 18 of 19 controls under 24px. | Confirmed (headless) | D |
| [DigiLocker home](https://www.digilocker.gov.in/) | Overflows at 200% zoom (317px in 195px, 29 controls off-screen); median text 14px. | Confirmed (headless) | D (B rejected) |
| Kerala FAQ, Odisha home, CPGRAMS FAQ, NCS FAQ, income-tax e-PAN help | Selection screening only ([table](PHASE6_SITE_UX_REPORT.md#how-the-two-additional-pages-were-chosen)) | Screening | NEEDS_MANUAL_REVIEW |
| UIDAI My Aadhaar | HTTP 500 to one request: access inconclusive, not a UI finding | — | NEEDS_MANUAL_REVIEW |

Unchanged since 14 October and not rechecked: NSP application (E), RTI login (E), CPGRAMS lodge (E), Lakshadweep home (A), Coimbatore home (D), Chennai destination (F).

## First page sample — 14 October 2026 (historical)

**14 October 2026.** The structured source of truth is [page_audits.json](../data/page_audits.json); the one-run, read-only 390 × 844 Chrome screening is [screening_runs/2026-10-14.json](../data/screening_runs/2026-10-14.json). Audit records identify exact URL, page type, date, reviewer, status, viewport, issues, severity, confidence, evidence, risk, recommendation, test status and limits. Ten pages/entry points were assessed across published and unpublished portals. Only five loaded pages have this session's mobile screenshot; other risk classes use official public descriptions or prior Android evidence.

### Method and interpretation

The sample opens explicit public URLs once. It does not crawl links, authenticate, submit forms, collect field values or inject CSS. It records response status, one load time, viewport/document width, link/image/form counts and visible paragraph font sizes. The single load time is **not a performance benchmark**. A missing `alt` attribute may belong to a decorative image; a small computed font is a flag for review, not a WCAG failure. Network 403/timeout is access inconclusive, not a finding about site UI. Screenshots permit a limited visual inspection, not keyboard, screen-reader or full-journey certification.

[GIGW 3.0](https://guidelines.india.gov.in/new-features-of-gigw-3-0/) points to a WCAG 2.1 AA accessibility baseline. We use the [W3C WCAG 2.1 criteria](https://www.w3.org/TR/WCAG21/) as references for reflow, resize text, focus, labels and contrast. No GIGW certification or WCAG conformance claim is made.

### Observed pages

| Page | Evidence and finding | Status | Recommendation |
| --- | --- | --- | --- |
| National Portal home | Android [100%](screenshots/browser-text-normal.png), [125% at 1.3 system font](screenshots/audit-android-large-web-125.png), and [landscape](screenshots/audit-android-landscape-after-fix.png): cookie notice grows and its side-by-side buttons clip behind adjacent content in landscape. Chrome screening returned 403. | Limited manual review | More review; official page has its own text/contrast tools. |
| Scholarship home | [Mobile capture](screenshots/audit-scholarship-home.png): 402px document width at 390px, hero partly clipped, 8/26 visible paragraphs under 16px. | Screenshot review plus screening | D: native task guidance; existing CSS pilot requires reevaluation. |
| Scholarship application entry | [Official Students page](https://scholarships.gov.in/Students) points to an OTR/password application route. | Public risk screening | E: no styling of sign-in or application. |
| RTI home | [Mobile capture](screenshots/audit-rti-home.png): 980px document width at 390px; warning and text are clipped. Own A+/A/A- controls visible. | Screenshot review plus screening | D: central-versus-state guidance and official-browser handoff. |
| RTI login | [Official login page](https://rtionline.gov.in/login.php) has username, password and security code. | Public risk screening | E: no styling. |
| DigiLocker home | [Mobile capture](screenshots/audit-digilocker-home.png): no horizontal overflow; 41/43 visible paragraphs below 16px. | Screenshot review plus screening | Manual review; possible B on public content after reflow tests. |
| Lakshadweep home | [Mobile capture](screenshots/audit-lakshadweep-home.png): introductory text reflows within 390px; native accessibility/language tools visible. | Limited screenshot review | A for this first viewport; no styling need evidenced. |
| Coimbatore home | [Mobile capture](screenshots/audit-coimbatore-home.png): dense civic navigation, 296 links in loaded document, native A controls, separate payment/grievance hosts. | Screenshot review plus screening | D: task guidance after ownership and destination review. |
| IGOD Chennai destination | [Directory link](https://igod.gov.in/index.php/leg/TN/L008/organizations) led to a default Tomcat page at the linked root. | One destination check | F: canonical URL/reliability investigation; CSS cannot repair it. |
| CPGRAMS lodge | [Official page](https://pgportal.gov.in/Home/LodgeGrievance) requires registered sign-in. | Public risk screening | E: existing JanSetu guidance; no styling. |

### Coverage and open checks (as of 14 October)

The sample has one A, three D, three E, one F and two `NEEDS_MANUAL_REVIEW`; no B or C has passed safe reflow and interaction checks. Class A is limited to the inspected public landing viewport, and class E is based on the function of the exact entry point. Missing-alt and font-size counts need a human to decide whether they affect actual tasks. National Portal landscape was checked on one Android emulator; keyboard, TalkBack/screen reader, 200% text, contrast measurement, end-to-end application, repeated latency and installed-extension testing remain open. Follow [the manual checklist](UX_AUDIT_MANUAL_CHECKLIST.md) before an intervention moves toward production.
