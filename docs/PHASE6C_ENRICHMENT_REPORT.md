# Phase 6C: second enrichment batch

18 October 2026. This batch covers the next 20 websites from the missing-information queue. It is not an enrichment of the whole directory: 7,309 websites are still not checked. Reviewer workflow and first-batch status are in [PHASE6C_EDITORIAL_REVIEW_REPORT.md](PHASE6C_EDITORIAL_REVIEW_REPORT.md).

## Result

| | Count |
|---|---:|
| Websites in the batch | 20 |
| **Published** (`source_checked`, automated; human review pending) | **10** |
| **Drafts** (proposed; validated but not in the app) | **8** |
| **Held** (no content observation) | **2** |
| Evidence items (official URL, supported claim, date, exact quote, pinned to a snapshot) | 89 |
| Published facts | purpose 10 · operator 9 · audience 6 · jurisdiction 10 · languages 3 |
| Published links | help/FAQ/contact/grievance 16 · task pages 4 |
| Review flags for a human reviewer (translations, inferred wording, unnamed departments) | 19 |

After this batch, 27 of 7,347 websites (0.37%) have a checked purpose: 9 reviewed records and 25 published profiles, of which 7 overlap. Reviewed by a person: 0.

## Selection

The queue was regenerated before selection. It confirmed **the Official Portal of Himachal Pradesh Government first**, at priority 100: 7 service candidates awaiting review point to it, and it is a state portal. The other 19 are the next rows under the unchanged Phase 6B priority rule; most are pension, scholarship and grievance portals.

The batch file is [batch-2026-10-18.json](../data/website_enrichment/batch-2026-10-18.json) and records the rule.

## Capture

Each page was loaded once, read-only, in headless Chrome 155 with normal TLS validation. There was no clicking or typing, and no sign-in, form or captcha. Sign-in, apply, registration and payment links are never followed.

- **First pass (25 loads).** 4 sites loaded: 7 pages. Then this machine lost its internet connection, and the remaining 18 loads failed with `ERR_INTERNET_DISCONNECTED`. That is a local outage, not a site response.
- **One retry for the outage failures (39 loads).** 35 returned content. The problems:
  - DoPPW returned **HTTP 403**. That is an access decision, so it was not retried further.
  - Gujarat DPPF returned a **"Firewall Captcha Authentication"** page, which was not solved or bypassed.
  - Telangana ePASS's About, Contact and FAQ pages returned an HTTP error.
- **Not cited.** Broadcast Seva's Help link led to a page titled "Login", which is never cited.

That gives 64 snapshot files, 42 with page text. Logs are in `data/website_enrichment/research/2026-10-18/capture-log*.json` and `data/validation_reports/phase6c-capture*.log`.

## Publication decisions

The rule applied, which every profile's `publicationDecision` records:

- **Publish** (`source_checked`) when:
  - the site states its purpose in its own words (any language), and
  - the operating body is named on the site, and
  - every quote is verified.

  Claims based on a non-English source carry a translation flag for the human reviewer.
- **Draft** when the purpose rests only on a page title, menu labels or FAQ questions, when the page is a staff sign-in gateway, or when the site looks out of date. A draft is validated like a published profile but never packaged.
- **Hold** when nothing could be observed (HTTP 403, captcha).

| Website | ID | Priority | Decision | Facts | Help | Tasks | Evidence | Held fields |
|---|---|---:|---|---|---:|---:|---:|---|
| Official Portal of Himachal Pradesh Government | `igod-ffa4189a2ac66991` | 100 | published | purpose, operator, audience, jurisdiction | 2 | 1 | 5 | languages |
| Pension grievance portal, Odisha | `igod-0eda9ab58f816d6f` | 50 | **draft**: title only; staff sign-in gateway | purpose, operator, audience, jurisdiction | 0 | 0 | 3 | help, languages |
| Sevana Pension, Kerala | `igod-6e1f53ab402e424c` | 50 | **draft**: Malayalam title and menu, translated | purpose, operator, jurisdiction | 0 | 1 | 4 | languages, help |
| SevanaPension Direct To Home Module, Kerala | `igod-ddb798b880079969` | 50 | published | purpose, audience, jurisdiction | 2 | 0 | 4 | operator, languages |
| State Scholarship Portal, Odisha | `igod-781379c678ce1e21` | 50 | published | purpose, operator, audience, jurisdiction, languages | 2 | 1 | 7 | — |
| Bhavishya (pension tracking) | `igod-c01d8df9282d87de` | 40 | published | purpose, operator, audience, jurisdiction | 2 | 1 | 7 | languages |
| Broadcast Seva | `igod-7bb47a1cab5183f2` | 40 | published | purpose, operator, jurisdiction | 0 | 0 | 2 | audience, help |
| CM Fellowship, Jharkhand | `igod-e300869e279b9cd4` | 40 | published | purpose, operator, audience, jurisdiction, languages | 3 | 0 | 8 | — |
| Common Scholarship Portal, Nagaland | `igod-dc063c5a252aabe5` | 40 | published | purpose, operator, audience, jurisdiction | 1 | 0 | 4 | languages, tasks |
| CPWD Sewa | `igod-18649bde195e5a01` | 40 | **draft**: purpose from labels | purpose, operator, jurisdiction | 0 | 1 | 4 | help |
| Janasunani grievance portal, Odisha | `igod-5891bfd68a83ed15` | 40 | published, translation flagged | purpose, operator, jurisdiction | 2 | 1 | 6 | audience, languages |
| DoPPW | `igod-9783d3afd73ca927` | 40 | **held**: HTTP 403 | — | — | — | 0 | — |
| Directorate of Pension & PF, Gujarat | `igod-44b4a596b1818cab` | 40 | **held**: captcha wall | — | — | — | 0 | — |
| DoPPFI, Madhya Pradesh | `igod-45553a3bd372fff9` | 40 | **draft**: no purpose statement captured | purpose, operator, jurisdiction, languages | 0 | 0 | 4 | help |
| Treasuries, Pension & Entitlements, Uttarakhand | `igod-ef54536511a6523b` | 40 | published | purpose, operator, jurisdiction, languages | 2 | 0 | 7 | audience |
| e-Kalyan, Jharkhand | `igod-c33cb8549415ad34` | 40 | **draft**: Hindi labels, translated | purpose, operator, jurisdiction, languages | 1 | 0 | 6 | — |
| e-MSME Seva, Odisha | `igod-af6bdd4b418c67a8` | 40 | published | purpose, operator, jurisdiction | 0 | 0 | 3 | audience, help, tasks |
| ePASS, Himachal Pradesh | `igod-b3a364f734caa1f1` | 40 | **draft**: FAQ questions only; latest news 2019 | purpose, operator, jurisdiction | 2 | 0 | 5 | languages |
| ePASS, Telangana | `igod-2bdd905020581df7` | 40 | **draft**: home page only; labels | purpose | 0 | 0 | 4 | operator, jurisdiction, help |
| CPAO | `igod-421dca3648f50c0b` | 40 | **draft**: no purpose statement captured | purpose, operator, jurisdiction, languages | 2 | 0 | 6 | — |

Findings now shown to citizens, from the official pages:

- **SevanaPension Direct To Home** is software that **co-operative banks** use to disburse pensions; it is not a pensioner portal.
- **Odisha's pension grievance site** offers only staff sign-in. It stays a draft until someone confirms how pensioners raise grievances.
- **Uttarakhand eKosh's "Grievance Redressal" page** lists only an e-Procurement complaint mechanism, so it is not offered as a grievance route.
- **CPAO's site** warns that fraudsters call pensioners about life certificates and ask for OTPs. This is kept in the draft's notes.
- **HP ePASS** shows flash news from 2019.

Excluded on purpose:

- Nagaland's "How to Apply" page (the application-journey rule)
- Broadcast Seva's Help (a login page) and its Contact link (no target)
- CPWD Sewa's Contact link (redirected to an error page)
- e-MSME Seva's listed systems (sign-in pages or other portals)
- all sign-in, registration and payment links
- phone numbers and email addresses

## Evidence quality

- All 89 quotes are verbatim in saved snapshots and pinned in `evidence_index.json` (185 pins in total across both batches).
- 11 quotes are in Odia, Malayalam or Hindi. Every claim that depends on them is flagged as an automated-agent translation.
- Drafts and held profiles never reach the app. Tests check this for all 8 drafts and 5 held profiles.

## Remaining gaps

| Group | Not checked |
|---|---:|
| All websites | 7,309 not yet in any batch (99.5%) |
| Priority websites (136) | 111 without a checked purpose: 100 not yet in a batch, 8 drafts, 3 held |
| State/UT portals (27) | 24 not checked |
| Citizen-service portals (100) | 76 not checked |
| Websites linked by published services (14) | MSME held (HTTP 403); UIDAI held, but its earlier reviewed record still stands |

## Next batch (from the regenerated queue)

The queue never re-selects a site that already has a published, draft or held profile.

| Priority | Websites |
|---:|---|
| 40 | Gujarat State Scholarship Portal · Haryana Gau Seva Aayog · Integrated Financial Pension Management System (Rajasthan) · Integrated Pensioners' Portal · Jan Sevaayein Hamare Dwaar · Kritagyata (pension tracking) · Ministry of Personnel, Public Grievances and Pensions · National Overseas Scholarship Portal |
| 40 | State/UT portals: Andaman & Nicobar Islands, Andhra Pradesh, Arunachal Pradesh, Assam, Chandigarh, Chhattisgarh, Gujarat, Haryana, Jharkhand, Ladakh, Lakshadweep, Madhya Pradesh |

The full list is in [queue.json](../data/website_enrichment/queue.json). Before running it, the team should resolve the 8 drafts and review the first batch (see the release gate proposal).

## Permission blockers (unchanged)

1. **NIC/IGOD data-reuse permission** is pending. `check_catalog.py --public-release` still blocks distribution.
2. **Permission to reuse official-page text** has not been checked. `data/website_enrichment/research/` (full page text), the quotes in `claims.json`, `evidence_index.json` excerpts and the review packets are internal review material. Keep them out of any public repository, app, extension or release artifact until this is resolved. Packaging tests confirm that no quote, excerpt, reviewer detail or note reaches the app.
