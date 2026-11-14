# Page intervention matrix

Updated 17 October 2026 (Phase 6A). Each row is a decision for **one exact page** (host, path and query). It never authorises anything on other pages of the same domain. The machine-checked source is [site_interventions.json](../data/site_interventions.json), validated by `python tools/site_interventions.py` against the extension allowlist (`extension/core.mjs`), the extension route check (`extension/background.js`) and the Android in-app host list. Evidence per page is in [page_audits.json](../data/page_audits.json); reasoning and the ranked plan are in [PHASE6_SITE_UX_REPORT.md](PHASE6_SITE_UX_REPORT.md).

## Classes

| Class | Meaning | Allowed JanSetu controls |
| --- | --- | --- |
| A | No intervention, or insufficient evidence that one would help | None; ordinary guidance links only |
| B | Low-risk readability through text scaling | Browser-level text size (in-app WebView `textZoom`, Chrome tab zoom) with a one-tap original size. Only where reflow, dialogs and controls were checked at the offered sizes. |
| C | Page-specific styling candidate requiring functional review | A reviewed stylesheet for that exact page: `:where()` selectors; typography, focus outline or `overflow-x: auto` only; user-invoked; with Restore. Classification alone never authorises injection. |
| D | JanSetu guidance is safer than changing the page | Native JanSetu guidance and official handoff |
| E | Sign-in, identity, CAPTCHA, payment, upload, application or other stateful journey | Handoff and independent guidance only; the page keeps its own presentation unless a separately authorised, tested integration exists |
| F | Navigation or reliability problem needing diagnosis | Verify the destination and offer a safe official alternative; CSS cannot fix it |
| NEEDS_MANUAL_REVIEW | Screening only | None |

## Current decisions

| Exact page | Class | Status | Evidence (17 Oct unless noted) | Constraint |
| --- | --- | --- | --- | --- |
| `https://www.india.gov.in/` (JanSetu in-app browser) | B | investigating | [Font 1.0](screenshots/phase6/india-portal-android-a100-font1.png) / [2.0](screenshots/phase6/india-portal-android-a100-font2.png): page ignores Android font size; [A+](screenshots/phase6/india-portal-android-a125-font1.png) shifts the header; [rotation reloads](screenshots/phase6/india-portal-android-landscape-font1.png) | Text size only. The consent dialog and site controls are never restyled. A/A+ is a browser-level preference across public pages of this host; only the home page has been reviewed. |
| `https://scholarships.gov.in/home` | D (existing C pilot) | pilot, **withdrawal recommended** | [Before](screenshots/phase6/scholarship-home-390.png) / [after](screenshots/phase6/scholarship-home-readable-390.png): no measured benefit on the documented problems | Do not extend; withdraw or replace with a problem-specific rule |
| `https://scholarships.gov.in/ApplicationForm/` | E | recommendation | Official Students page (14 Oct) | Never styled |
| `https://rtionline.gov.in/` | D | recommendation | [390px](screenshots/phase6/rti-home-390.png): still 980px wide | No generic enlargement |
| `https://rtionline.gov.in/guidelines.php?request` | D | investigating | [Emulator Chrome](screenshots/phase6/rti-guidelines-android-chrome-font1.png): 0.40 scale, no viewport meta | Guidance with all five rules; checkbox and Submit/Cancel stay as they are |
| `https://rtionline.gov.in/login.php` | E | recommendation | Credential and security-code fields (14 Oct) | Never styled |
| `https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx` | C | investigating, **not allowlisted** | [390px](screenshots/phase6/tn-esevai-service-list-390.png), [structure](../data/validation_reports/phase6a-page-structure.json), [desktop zoom](../data/validation_reports/phase6a-tn-desktop-zoom.json) | Proposal [`tn-esevai-service-list.css`](../data/site_intervention_proposals/tn-esevai-service-list.css) is outside the extension package; apply destinations untouched |
| `https://www.digilocker.gov.in/` | D | recommendation | [200% zoom](screenshots/phase6/digilocker-home-zoom200.png): 317px content in 195px viewport | B rejected; no styling |
| `https://lakshadweep.gov.in/` | A | recommendation | [First viewport](screenshots/audit-lakshadweep-home.png) (14 Oct) | No change needed |
| `https://www.ccmc.gov.in/` | D | recommendation | [Home](screenshots/audit-coimbatore-home.png) (14 Oct) | Guidance after ownership review; never style payment or grievance hosts |
| `https://chennaicorporation.gov.in/` | F | recommendation | Default server page via IGOD link (14 Oct) | Verify the canonical URL |
| `https://pgportal.gov.in/Home/LodgeGrievance` | E | recommendation | Registered sign-in (14 Oct) | Never styled |
| Kerala FAQ, Odisha home, UIDAI My Aadhaar, CPGRAMS FAQ, NCS FAQ, income-tax e-PAN help | NEEDS_MANUAL_REVIEW | recommendation | [Screening](../data/screening_runs/2026-10-17.json) | None until reviewed |

**Live presentation changes today:** the in-app A/A+ on `www.india.gov.in`, and the opt-in NSP `/home` stylesheet (withdrawal recommended). Nothing else. No B or C change has passed human review.

## Review a class C proposal must pass before allowlisting

1. **Problem definition.** Exact host, path and query; purpose; date; viewport, text settings and browser; screenshot; the specific problem; who is affected and which task is impaired; confirmed, suspected or not reproducible.
2. **Rules.** The complete stylesheet, using `:where()` selectors and the allowed property list only (`tools/site_interventions.py` lint). One rule per documented problem. Nothing that hides, moves, recolours or covers content or controls.
3. **Static-fixture tests.** Critical controls stay visible, uncovered and unchanged. No new horizontal overflow. Text is unchanged. Restore returns identical computed styles and scroll position.
4. **Live checks by a person** with the actually installed extension on desktop Chrome: apply and Restore; reload; same-page and cross-page navigation (the background script must remove the sheet off-route); 125%, 150% and 200% zoom; keyboard pass; screen reader; the site's own accessibility controls; every link destination unchanged.
5. **Measured benefit.** Show that the documented problem is reduced, for example columns reachable or text readable at a named size. A stylesheet being applied is not a benefit.
6. **Recording.** The registry entry moves to `approved` with `review.rules`, `testsPassed`, `rollback`, `reviewer`, `date` and `humanReview: true`. The extension allowlist and background route check change in the same commit. Both validators and `node --test extension/` pass.
7. **Withdrawal.** Removing the allowlist entry and registry approval together must be a one-line change, with a release note.

The TN proposal has passed steps 1–3 (problem definition, lint, fixture mechanics). Steps 4–6 are outstanding, so it stays unlisted.

## Investigation order (17 October)

1. National Portal in JanSetu: follow Android font size; keep page state on rotation (B).
2. RTI guidelines: JanSetu "Before you request" guidance with five sourced rules (D).
3. TN e-Sevai list: source TN fees and documents editorially; review the overflow rule for desktop zoom users (D + C).
4. Withdraw or replace the NSP pilot.
5. Screen next: income-tax e-PAN help (likely B through browser zoom only), Odisha contrast, Kerala reflow at 320px.

## Superseded: 14 October five-portal research order

The 14 October proposal ordered research as RTI Online, National Scholarship Portal, DigiLocker, Coimbatore and Lakshadweep. Phase 6A replaced it with the evidence-based order above. Specifically, DigiLocker's B prospect failed its 200% reflow condition, and the NSP stylesheet showed no measured benefit. Coimbatore (ownership review) and Lakshadweep (no-change control) keep their 14 October classes until rechecked.
