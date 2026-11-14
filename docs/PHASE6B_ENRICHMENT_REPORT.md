# Phase 6B: website enrichment, first batch

17 October 2026. This is the first evidence-backed enrichment batch: a small set checked end to end against official pages, not a review of the whole catalogue. Catalogue-wide counts are in [PHASE6B_CATALOGUE_QUALITY_REPORT.md](PHASE6B_CATALOGUE_QUALITY_REPORT.md).

## Result

| | Count |
|---|---:|
| Websites in the batch | 18 |
| **Profiles published** (`source_checked`, automated; human review pending) | **15** (8 directory records + 7 earlier reviewed records) |
| Profiles held (no content observation) | 3 |
| Profiles reviewed by a person | 0 |
| Evidence items (each: official URL, supported claim, check date, exact quote) | 96, from 34 official pages |
| Facts published | purpose 15 · operator 15 · jurisdiction 15 · audience 9 · languages 7 |
| Official links published | help/contact/FAQ/grievance 30 (on 14 sites) · task pages 8 (on 7 sites) |
| Fields held (looked for, not stated or not safe to link) | 15 (audience 6, languages 6, help 2, tasks 1) |
| Notes from the check (uncertainty, shown in the app) | 29 |

Of 7,347 websites, 17 now have a verified purpose (0.23%): the 9 earlier reviewed records and these 8 directory records. 7,330 still have none.

## Batch selection

The rule is in [batch-2026-10-17.json](../data/website_enrichment/batch-2026-10-17.json). The batch takes:

- the 9 earlier reviewed websites
- every directory website that a published JanSetu service links to
- the state/UT portals for the regions that already have published services

It is ordered by the number of linked services. This gives the most value to people who reach a website from a verified service.

## Method

1. **Capture (read-only).** Run `python tools/website_enrichment.py capture <batch>` and then `capture <batch> --targeted`.
   - Each start page was loaded once in local headless Chrome 155 with normal TLS validation and Chrome's own headless user agent. There was no clicking, typing, sign-in or form use.
   - The tool saved the page's text, title, language, meta description and links as a dated snapshot. It then followed at most three same-host About/Contact/Help/FAQ links.
   - After the first pass, sign-in, apply, registration and payment links are never followed (see "Excluded" below).
   - Totals: 41 first-pass loads and 12 targeted loads, giving 53 snapshot files, 46 of them with page text. Logs are in `data/website_enrichment/research/2026-10-17/capture-log*.json` and `data/validation_reports/phase6b-capture*.log`.
2. **Targeted loads.**
   - TN e-Sevai's About, Services Offered, Contact and FAQ pages have Tamil menu labels that the English follow rule cannot match, so they were loaded directly.
   - Kerala's FAQ was loaded without the session token its home-page link carries.
   - Each DNS failure (revenue.py.gov.in, incometax.gov.in, electoralsearch.eci.gov.in) and the UIDAI HTTP 500 got **one** same-day retry. All three DNS retries loaded; UIDAI returned 500 again.
   - HTTP 403 responses are an access decision, so they were not retried or worked around.
3. **Claims.** [claims.json](../data/website_enrichment/claims.json) holds, for each website:
   - status and review status
   - the reviewer: "Claude Code (automated agent); human review pending"
   - the evidence list: id, URL, claim, check date and the **exact quote**
   - JanSetu's short plain-language fields, each citing evidence ids
   - `heldFields` with reasons, and uncertainty notes
4. **Validation.** `python tools/website_enrichment.py validate` lists every problem it finds and fails if any exist. It checks that:
   - every quote appears verbatim (whitespace-normalised) in the saved snapshot of that exact official HTTPS URL
   - every link is an official HTTPS page, either captured or present as an anchor on a cited page whose text is the cited quote
   - no sign-in, application or payment page is cited or linked
   - a published profile has a purpose and is `source_checked` or `human_reviewed`
   - `human_reviewed` names a person, and a machine check is never labelled as human review
   - held profiles carry a reason and no facts
   - website-level eligibility, documents, fees, deadlines, phone numbers, emails and addresses are refused

   Result: `{"profiles": 18, "published": 15, "fieldsPublished": {"audience": 9, "help": 30, "jurisdiction": 15, "languages": 7, "operator": 15, "purpose": 15, "tasks": 8}}`.
5. **Packaging.** Every Gradle build re-runs the validator. Only published profiles reach the app, and without quotes; held profiles and held fields stay in the repository.

## Per-website results

Evidence is shown as items / distinct official pages.

| Website | ID | Linked services | Status | Facts published | Help links | Task links | Evidence | Held fields |
|---|---|---:|---|---|---:|---:|---|---|
| Tamil Nadu eSevai Portal | `igod-2715637a8f399700` | 9 | published | purpose, operator, jurisdiction, languages | 2 | 2 | 7 / 5 | audience |
| eDistrict, Kerala | `igod-50b73c3937b8d29b` | 8 | published | purpose, operator, audience, jurisdiction, languages | 1 | 1 | 5 / 2 | help (others carry a session token) |
| eDistrict, Odisha | `igod-5e885f0e333d5206` | 8 | published | purpose, operator, audience, jurisdiction | 2 | 0 | 5 / 1 | tasks (service links open sign-in), languages |
| Online Citizen Services, Delhi | `igod-9908672336cdd912` | 5 | published | purpose, operator, audience, jurisdiction, languages | 3 | 1 | 9 / 2 | — |
| National Scholarship Portal | `national-scholarship-portal` | 3 | published | purpose, operator, audience, jurisdiction, languages | 2 | 0 | 6 / 1 | — |
| National Career Service | `national-career-service` | 3 | published | purpose, operator, audience, jurisdiction | 1 | 0 | 4 / 2 | languages |
| CPGRAMS | `cpgrams` | 2 | published | purpose, operator, audience, jurisdiction, languages | 2 | 1 | 11 / 3 | — |
| RTI Online | `rti-online` | 2 | published | purpose, operator, jurisdiction, languages | 2 | 0 | 6 / 1 | audience |
| Udyam Registration | `udyam-registration` | 1 | published | purpose, operator, audience, jurisdiction | 1 | 0 | 5 / 1 | — |
| Revenue & Disaster Management, Puducherry | `igod-7c7865e8e6982bc1` | 2 | published | purpose, operator, jurisdiction | 2 | 1 | 6 / 1 | audience |
| Income Tax e-Filing | `income-tax-efiling` | 2 | published | purpose, operator, audience, jurisdiction | 2 | 0 | 4 / 3 | languages |
| Voters' Service Portal (ECI) | `igod-85affa23dc47255b` | 2 | published | purpose, operator, jurisdiction, languages | 0 | 0 | 6 / 1 | help (none in page links), audience |
| DigiLocker | `digilocker` | 0 | published | purpose, operator, audience, jurisdiction | 2 | 0 | 5 / 3 | languages |
| Tamil Nadu state portal | `igod-ec86b21083980772` | 0 | published | purpose, operator, jurisdiction | 4 | 1 | 9 / 4 | languages, audience |
| Puducherry state portal | `igod-fd26b3c2335b6081` | 0 | published | purpose, operator, jurisdiction | 4 | 1 | 8 / 4 | languages, audience |
| UIDAI Aadhaar Services | `uidai` | 4 | **held** | — | — | — | — | HTTP 500 twice; existing reviewed record unchanged |
| National Portal of India | `india-portal` | 0 | **held** | — | — | — | — | HTTP 403; not retried |
| Ministry of MSME | `igod-1d591544b1c2e25d` | 1 | **held** | — | — | — | — | HTTP 403; not retried |

Findings the official pages state that are now shown as notes:

- **RTI Online** is for central public authorities only. The portal says applications for State Government authorities, including the Government of NCT of Delhi, would be returned.
- **CPGRAMS** says grievances sent by email are not attended to.
- **Udyam** says no private agency is authorised to do MSME registration.

## Evidence quality

- **Exact source text.** All 96 quotes are verbatim in saved official snapshots, and the validator proves it on every build. One quote is in Tamil (TN e-Sevai home page); the claim and app text are English.
- **Machine-generated, labelled as such.** The app text is JanSetu's own short summary, written by an automated agent from the quoted text. Every profile is `source_checked`, and the app says "Checked … by JanSetu's automated check against saved copies of the official pages. A person has not reviewed it yet." None is labelled human-reviewed.
- **Link-only observations.** Some published links were seen as anchors on a captured page but their target pages were not loaded. Examples are Delhi Help and tracking, Kerala tracking, TN e-Sevai tracking, Puducherry FAQ and online services, and the TN portal's grievances and online services. Each such profile has a note saying so.
- **Weakest evidence.** Odisha's FAQ and contact links point to sections of the home page (`#team`, `#contact`). NSP's operator is taken from the site header (Ministry of Electronics & Information Technology), and scheme owners differ by scholarship. TN e-Sevai's languages were inferred from which pages were served in Tamil and which in English, because no language switch was found.
- **Not inferred.**
  - Audience was published only where the page says who it serves.
  - Languages were published only where a language switch or statement was seen.
  - No eligibility, documents, fees, deadlines, phone numbers, emails or addresses were taken into website records.

## Excluded on purpose

- **Sign-in, application and payment pages.** Odisha's "Grievance Redressal on online services" link was followed during the first capture and turned out to be a ServicePlus sign-in page. Its snapshot (`6eb67af6811cacfa.json`) is kept as a capture record and is never cited. The capture tool now skips such links, and the validator and app parser refuse them. Also excluded:
  - NSP "Apply now" (OTR sign-in)
  - CPGRAMS "Lodge Public Grievance"
  - Delhi "Login & Apply"
  - Delhi "Register Grievances"
  - Odisha `directApply` links
  - Income Tax login and register
- **Non-official links.** The NCS "Contact Us" link (a maps service) and TN e-Sevai's Outlook "safelinks" redirect for a biometric driver download were excluded.
- **Unstable addresses.** Kerala's services and contact links carry session tokens.
- **Page metadata that contradicts the site.** NCS's meta description refers to unrelated engineering services and is ignored.

## Permission blockers (release, not local development)

1. **IGOD/NIC reuse.** The source registry records "Pending NIC permission … permission before public distribution". `python tools/check_catalog.py --public-release` still blocks distribution. Unchanged.
2. **Official website text.** Third-party reuse permission for text on these 15+3 websites has **not** been checked or granted. JanSetu therefore shows only its own short summaries, links and check dates; no quoted page text is packaged. However:
   - `data/website_enrichment/research/` holds full rendered page text from official sites as local review evidence.
   - `claims.json` holds short verbatim quotes.

   Both are **release blockers**: do not publish them in a public repository, extension or app package until reuse permission or a fair-dealing review is recorded. The packaging tests confirm that no quote reaches the app catalogue.
3. **Government logos and branding** were not copied.

## Remaining gaps and the queue

Run `python tools/website_enrichment.py queue` to write [queue.json](../data/website_enrichment/queue.json). It has one row for each of the 7,344 websites that still lack at least one checked field:

| Queue state | Rows |
|---|---:|
| Not yet checked | 7,329 |
| Held (no content observation) | 3 |
| Checked, with some fields not stated on the official pages | 12 |

Each row gives:

- the missing fields
- the source to check
- the URL check (`RESPONDING` with its date)
- whether the domain is official
- the description status: `directory_template`, `reviewed_record_text` or `purpose_checked_against_official_page`
- the pending reason
- the publication requirements

The 3,823 IGOD candidates and the 31 service candidates keep their own queues, unchanged.

## Next batch (20, from the queue's published priority rule)

Priority points:

| Reason | Points |
|---|---:|
| Published-service link | 100 |
| Pending-service link | 60 |
| State/UT portal | 40 |
| Citizen-service portal (e-District, seva/sewa, grievance, pension, scholarship) | 30 |
| State that already has services | 10 |
| District site | 10 |
| Citizen-facing sector | 10 |

1. Official Portal of Himachal Pradesh Government (`igod-ffa4189a2ac66991`): 7 service candidates await review here (100)
2. Public Grievances and Pension Administration Department, Odisha (50)
3. Sevana Pension - Social Security System, Kerala (50)
4. SevanaPension Direct To Home Module, Kerala (50)
5. State Scholarship Portal, Odisha (50)
6. Bhavishya, Pension Sanction and Payment Tracking System (40)
7. Broadcast Seva Portal (40)
8. Central Pension Accounting Office (CPAO) (40)
9. CM Fellowship Scheme for Academic Excellence, Jharkhand (40)
10. Common Scholarship Portal, Nagaland (40)
11. CPWD Sewa (40)
12. Department of General Administration and Public Grievance, Odisha (40)
13. Department of Pension & Pensioner's Welfare (DoPPW) (40)
14. Directorate of Pension and Provident Fund, Gujarat (40)
15. Directorate of Pension Provident Fund and Insurance, Madhya Pradesh (40)
16. Directorate of Treasuries, Pension & Entitlements, Uttarakhand (40)
17. e-Kalyan - Student Scholarships by Welfare Department, Jharkhand (40)
18. e-MSME Seva, Odisha (40)
19. ePASS scholarships, Himachal Pradesh (40)
20. ePASS scholarships, Telangana (40)

Before that batch, a person should review the 15 published profiles (see the manual checklist) and try UIDAI, the National Portal and MSME in a normal browser.
