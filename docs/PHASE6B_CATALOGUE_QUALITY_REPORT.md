# Phase 6B: catalogue information quality and citizen-first discovery

17 October 2026. This report covers catalogue quality: baseline, field coverage, the new website page and search. The evidence-backed enrichment batch is reported in [PHASE6B_ENRICHMENT_REPORT.md](PHASE6B_ENRICHMENT_REPORT.md), and the checks a person still has to do are in [PHASE6B_MANUAL_CHECKLIST.md](PHASE6B_MANUAL_CHECKLIST.md).

Two Phase 6 prompts were supplied together. This work follows **Phase 6B (Catalogue Information Quality and Citizen-First Discovery)**, which refines the earlier "Phase 6: Government Website Information Enrichment" request. The PHASE6B_* documents replace the PHASE6_CATALOGUE_QUALITY_REPORT, PHASE6_ENRICHMENT_REPORT and PHASE6_MANUAL_CHECKLIST names from the earlier prompt.

## Summary

- JanSetu lists **7,347 websites**. Only **17 (0.23%)** have a verified statement of what the website is for: 9 manually reviewed records from earlier phases and 8 directory records checked in this phase. **7,330 (99.77%)** still show only the IGOD directory listing.
- This phase published **15 source-checked website profiles** (purpose, operator, jurisdiction and, where stated, audience, languages, help and task links). **3** were held because their sites returned HTTP 500 or 403. No profile has been reviewed by a person yet; the app says so.
- The website page now answers six questions in order and says plainly when something has not been checked. Website records and service records stay separate.
- Search indexes only checked profile text. It labels results as **Website**, **Verified service**, **Purpose checked** or **Information under review**, and has a **Websites with checked information** filter.
- No catalogue record, review queue entry, service record, identifier, provenance or review history was changed or deleted to improve these numbers.

## Baseline (recalculated 17 October 2026)

Every count below can be reproduced with `python tools/catalog_completeness.py --markdown`. The tool writes [phase6b-completeness.json](../data/validation_reports/phase6b-completeness.json) with the SHA-256 of each input file.

| Measure | Count | What it means |
|---|---:|---|
| Websites in the catalogue | 7,347 | 7,338 `directory_listed` (IGOD) + 9 `verified` (manually reviewed in Phases 1–4) |
| IGOD candidates waiting in the website review queue | 3,823 | Temporarily unreachable 2,349 · needs ownership review 1,177 · needs URL review 297. Not in the catalogue and not changed. |
| Published service records | 52 | All `verified`/`published`, all with official evidence. 32 state services (Delhi, Kerala, Odisha, Puducherry, Tamil Nadu); 20 central. |
| Service review queue | 86 entries, 83 service ids | Latest status: 52 PUBLISHED, 31 NEEDS_REVIEW. Three ids have two PUBLISHED decisions in their history, so 55 PUBLISHED entries cover 52 services. |
| Websites that a published service links to | 14 | |
| Directory descriptions that are IGOD category templates | 7,338 (all directory records) | For example "IGOD lists this website under State Portal. Open the official destination for current information." These state where IGOD lists the site, not what it offers. |
| Directory records whose listed organisation equals the website title | 7,338 (all directory records) | The directory gives no separate department name. |

### Three kinds of coverage

| Coverage | Count | Share of 7,347 |
|---|---:|---:|
| **Directory coverage**: a record exists and the URL responded to an automated check | 7,347 | 100% |
| **Verified information coverage**: what the website is for was checked against an official source | 17 | 0.23% |
| … of which reviewed manually in earlier phases | 9 | 0.12% |
| … of which source-checked by automation in Phase 6B (human review pending) | 8 new + 7 overlapping reviewed records = 15 profiles | 0.20% |
| Websites with a checked official help or contact page | 14 | 0.19% |
| **Published service coverage**: websites with at least one source-reviewed service | 14 | 0.19% |

Directory coverage does not mean a website's purpose, owner or services were checked. JanSetu has **not** reviewed all 7,347 websites.

## Field coverage

*Populated* means the record has a value from any source. *Directory only* means the value comes only from the IGOD import. *Verified* means it was checked against an official source: a manually reviewed record or a published source-checked profile. *Unknown* means there is no value. Unknown values are never filled in by inference.

### Website fields (n = 7,347)

| Field | Populated | Directory only | Verified | Unknown |
|---|---:|---:|---:|---:|
| Title | 7,347 (100.0%) | 7,338 (99.88%) | 9 (0.12%) | 0 (0.0%) |
| Official URL | 7,347 (100.0%) | 7,330 (99.77%) | 17 (0.23%) | 0 (0.0%) |
| Purpose (what the website is for) | 7,347 (100.0%) | 7,330 (99.77%) | 17 (0.23%) | 0 (0.0%) |
| Department or operator | 7,347 (100.0%) | 7,330 (99.77%) | 17 (0.23%) | 0 (0.0%) |
| Who it serves (audience) | 9 (0.12%) | 0 (0.0%) | 9 (0.12%) | 7,338 (99.88%) |
| Jurisdiction | 7,345 (99.97%) | 7,330 (99.77%) | 15 (0.2%) | 2 (0.03%) |
| Languages offered | 7 (0.1%) | 0 (0.0%) | 7 (0.1%) | 7,340 (99.9%) |
| Topics / categories | 7,347 (100.0%) | 7,338 (99.88%) | 9 (0.12%) | 0 (0.0%) |
| Official help or contact link | 14 (0.19%) | 0 (0.0%) | 14 (0.19%) | 7,333 (99.81%) |
| Official task links | 7 (0.1%) | 0 (0.0%) | 7 (0.1%) | 7,340 (99.9%) |
| Linked JanSetu services | 14 (0.19%) | 0 (0.0%) | 14 (0.19%) | 7,333 (99.81%) |
| Check date and source | 7,347 (100.0%) | 7,330 (99.77%) | 17 (0.23%) | 0 (0.0%) |

Notes:

- **Purpose.** Every record has description text, but all 7,338 directory descriptions are IGOD templates. A template never counts as a verified purpose. 8 of those records now have a checked purpose from a Phase 6B profile.
- **Department.** For every directory record the "organisation" is the title text, so the app now shows "Not checked yet" instead of repeating the title.
- **Jurisdiction.** The directory value is the IGOD state grouping, shown with "(from the IGOD directory)". The 2 unknown records are the two held reviewed records (UIDAI, National Portal) that have no jurisdiction field.
- **Official URL.** Directory URLs passed an automated response check (`healthStatus` RESPONDING). That is not an ownership review.

### Service fields (n = 52)

| Field | Populated | Directory only | Verified | Unknown |
|---|---:|---:|---:|---:|
| Purpose (description) | 52 (100.0%) | 0 (0.0%) | 52 (100.0%) | 0 (0.0%) |
| Applicant / beneficiary groups | 10 (19.23%) | 0 (0.0%) | 10 (19.23%) | 42 (80.77%) |
| Eligibility | 15 (28.85%) | 0 (0.0%) | 15 (28.85%) | 37 (71.15%) |
| Required documents | 29 (55.77%) | 0 (0.0%) | 29 (55.77%) | 23 (44.23%) |
| Conditional documents | 17 (32.69%) | 0 (0.0%) | 17 (32.69%) | 35 (67.31%) |
| Procedure | 25 (48.08%) | 0 (0.0%) | 25 (48.08%) | 27 (51.92%) |
| Fees | 13 (25.0%) | 0 (0.0%) | 13 (25.0%) | 39 (75.0%) |
| Deadline | 0 (0.0%) | 0 (0.0%) | 0 (0.0%) | 52 (100.0%) |
| Processing time | 16 (30.77%) | 0 (0.0%) | 16 (30.77%) | 36 (69.23%) |
| Direct application URL | 24 (46.15%) | 0 (0.0%) | 24 (46.15%) | 28 (53.85%) |
| Application route (mode) | 52 (100.0%) | 0 (0.0%) | 52 (100.0%) | 0 (0.0%) |
| Official information page | 52 (100.0%) | 0 (0.0%) | 52 (100.0%) | 0 (0.0%) |
| Responsible department | 52 (100.0%) | 0 (0.0%) | 52 (100.0%) | 0 (0.0%) |

Every populated checklist entry (eligibility, documents, conditional documents, procedure, fees, processing, beneficiary groups) cites evidence held on its service record. A direct application URL is empty for the 19 information-only and 9 centre-assisted services, where no direct citizen route was confirmed. No service fact was added or changed in this phase.

## The website page

The website detail screen now answers six questions in order. Each answer is a checked fact, a directory value clearly labelled as unchecked, or an honest missing state. There is no generic filler.

| # | Section | Checked profile shows | Otherwise shows |
|---|---|---|---|
| 1 | **What this website is for** | The checked purpose and "Checked against the official website on …" | Reviewed record: its reviewed description. Directory record: **Not checked yet**, then the directory listing text labelled as such. |
| 2 | **Department or organisation** | "Run by (checked)" | Reviewed record: "Run by". Directory record: "Not checked yet. The directory gives no separate department name." |
| 3 | **Who it serves and where** | Audience, jurisdiction and languages from the profile | "Not checked yet". Where a field was looked for but not on the page: "Not stated on the official pages JanSetu checked". Directory jurisdiction is marked "(from the IGOD directory)". |
| 4 | **Verified services and tasks** | Published service records for this website (each opens its service page) and official task pages, such as status tracking | "No JanSetu-verified services or task pages are linked to this website yet. A website listing is not a service." |
| 5 | **Official help and contact** | Links to official FAQ, help, contact and grievance pages. Phone numbers and emails are not copied. | "No official help or contact page has been checked yet. Use the official website." |
| 6 | **When checked and where from** | The review status in plain words ("…automated check… A person has not reviewed it yet."), a collapsible list of sources (claim, date, official page) and notes from the check | The IGOD listing source, its retrieval date and "The directory record says where the website is listed, not what it offers." |

The existing labels, the official-website button, the availability notice, known limitations, favourites, the JanSetu guide, Report a problem and the disclaimers are unchanged. Website records and service records stay separate. The page links to a service page and never copies service checklists (eligibility, documents, fees, deadlines) into the website record.

Screenshots (isolated QA app, 17 October 2026) are in [docs/screenshots/phase6b/](screenshots/phase6b/):

- checked profile: `detail-checked-top.png` and `detail-checked-1.png` to `detail-checked-3.png`
- directory-only record: `detail-directory-top.png`, `detail-directory-missing.png`, `detail-directory-sources.png`
- "not stated" (ECI): `detail-not-stated.png`
- search labels: `search-checked-label.png`
- checked filter: `search-checked-filter.png`

## Citizen-first search

- **Indexed text.** Only published profile text is added to the index: purpose (weight 5), task labels (4) and operator (2). Directory records gain nothing.
- **Labels.** Each result shows **Website** or **Verified service**. Website cards also show **Purpose checked** for a published profile or **Information under review** for a directory-only record. The existing `domain · Listed by IGOD` / `domain · Source checked` line is unchanged. A checked purpose replaces the IGOD template text on the card.
- **Filter.** **Websites with checked information** is a new option in the "Services and websites" chip. It only narrows website results to reviewed records and published profiles, and never adds a record.
- **Ranking.** Records with checked information get +3 points. This is enough for a checked keyword match (6 + 3) to rank above an unchecked title match (8). Nothing is hidden. For example, "grievance" now lists CPGRAMS first among websites, ahead of the 30 directory sites with "Grievance" in their title.
- **Task intents** (unit-tested):

  | Query | Result |
  |---|---|
  | "grievance" | A CPGRAMS verified service first, then CPGRAMS as the first website |
  | "scholarship" with the checked filter | Only National Scholarship Portal |
  | "pension" | Includes the Tamil Nadu old-age pension service |
  | "state government services" | The Tamil Nadu state portal in the top 5 websites |
  | "certificate" with the checked filter | No unchecked directory records |

## Data model and safety

- **Packaging.** `websiteProfiles` is a new optional catalogue array (default empty), so older catalogues and signed updates still parse. `tools/package_catalog.py` validates the claims against their saved snapshots on every build. It packages only published profiles and never packages quotes, held profiles or held fields.
- **Parser checks.** `CatalogParser` (bundled catalogue and signed imports) rejects a profile that:
  - names an unknown or duplicate website
  - has an unreviewed status or a future date
  - has no purpose
  - cites unknown evidence
  - has a non-HTTPS or non-government link, or a sign-in, application or payment link
  - uses a help or task link kind in the wrong field
  - names an unknown field
- **Unchanged protections.** Browser policy, URL restrictions, extension permissions and the site-intervention allowlists are unchanged.

## Remaining gaps

- 7,330 websites (99.77%) have no checked purpose, operator, audience, languages or help link.
- No profile has had human review. All 15 are `source_checked` by an automated agent.
- 31 service candidates remain NEEDS_REVIEW. No service has a deadline, and most lack eligibility (37 unknown) and fees (39 unknown).
- 3,823 IGOD candidates remain in the website review queue, untouched.
- Profile values are English summaries. In Hindi the app labels them as English summaries; there are no Hindi translations yet.
- Permission blockers (NIC/IGOD reuse and official-text reuse) are unresolved; see the enrichment report.

## Test results

See [QA_REPORT.md](QA_REPORT.md#phase-6b---17-october-2026) for the final gate.
