# Phase 6C: editorial review of website profiles

18 October 2026. This report sets out the human-review workflow for JanSetu's website profiles and the review state of the first batch. **No profile has been reviewed by a person.** All 25 published profiles (15 from the first batch, 10 from the second) are still `source_checked`: an automated agent compared each claim with saved copies of official pages. The app says so on every profile.

Related reports:

- [enrichment report](PHASE6C_ENRICHMENT_REPORT.md): second batch
- [release gate proposal](PHASE6C_RELEASE_GATE_PROPOSAL.md)
- [manual checklist](PHASE6C_MANUAL_CHECKLIST.md)

## Baseline at the start of Phase 6C

These counts were regenerated from the data, not copied from Phase 6B.

| Measure | Count |
|---|---:|
| Website catalogue records | 7,347 (9 reviewed in earlier phases, 7,338 IGOD directory listings) |
| Websites with a checked purpose | 17 (9 reviewed records + 8 directory records with Phase 6B profiles) |
| Profiles source-checked automatically (published) | 15 |
| Profiles reviewed by a person | 0 |
| Held profiles | 3: UIDAI (HTTP 500 twice), National Portal (HTTP 403), MSME (HTTP 403) |
| Missing-information queue | 7,344 websites (7,329 not yet checked, 3 held, 12 checked with gaps) |
| Verified service records | 52 published; 31 service cases still NEEDS_REVIEW (83 unique ids, 86 queue entries) |
| IGOD candidates outside the catalogue | 3,823, unchanged |
| Protected files | 8 of 8 match the Phase 4 baseline |
| Preview profile | Tracker DB, alarm registry and catalog update unchanged. The preferences hash had changed to `5ff177e1…` because the preview app was used on the device at 19:22 IST on 17 October, after the Phase 6B gate. That was ordinary use, not tooling. This is the Phase 6C baseline. |

## How human review works

| Piece | Role |
|---|---|
| `data/website_enrichment/claims.json` | Automated drafts and their evidence. A review never edits it. It now refuses `human_reviewed` and `humanReviewer`. |
| `data/website_enrichment/evidence_index.json` | Pins each of the 185 evidence quotes to the saved snapshot that contains it: file, text hash, character offset and surrounding context. A changed, missing or unpinned snapshot fails the build. |
| `data/website_enrichment/review/review-packet-<batch>.html` | Offline reviewer packet (internal: contains short quotes). |
| `data/website_enrichment/reviews.json` | Append-only human decisions. Empty today. |
| `tools/editorial_review.py` | `index`, `check`, `packet`, `import` and `status` commands. |

### The reviewer packet

For every published profile in the batch, the packet shows:

- the website name and its exact official URL
- every claim JanSetu displays: purpose, operator, audience, jurisdiction, languages, help links and task links
- for each claim, every source behind it:
  - the official page (a link the reviewer can open)
  - what the source is cited for
  - the saved quotation
  - where it is in the saved copy (file, whether it is page text, title, meta description or a link label, and the character offset)
  - a highlighted excerpt of the surrounding text
- for links, whether the target page itself was loaded or only seen as a link
- the automated review status (labelled "machine check, not a human review"), held fields and the notes from the check
- flags that require the reviewer to confirm they opened the source:
  - every operator, audience, language and jurisdiction claim
  - links that were not loaded
  - links to a page section
  - links to another host
  - translated quotes
  - points the drafter marked
- Approve / Revise / Hold for each claim and for the whole profile, with notes

The reviewer enters their name, optional role and the review date, and confirms that they compared the claims with the official pages themselves. **Save decisions file** downloads a JSON file. Nothing is uploaded, and nobody edits JSON by hand.

### Importing decisions

The maintainer runs `python tools/editorial_review.py import <file>`. The import checks every rule again and appends all decisions or none:

- **Reviewer.** A named person is required. Names that look like an automated agent (Claude, Codex, bot, script…) are refused, and the reviewer must have confirmed they compared the claims.
- **Date.** Not in the future, and not before the evidence was captured.
- **Current content.** The decision must match the profile's current content digest; a stale packet is refused.
- **Completeness.**
  - Every claim needs a decision.
  - A flagged claim can be approved only with "I opened the source" ticked.
  - A profile can be approved only if every claim is approved.
  - Revise and Hold need notes.
- **Duplicates.** A decision already imported is refused.

### What a decision does

The decision is derived at packaging time:

- **Approve.** The profile becomes `human_reviewed` with its own `reviewedOn` date, for as long as its content matches the reviewed digest. The app says "Reviewed by a person … on <date>. Sources saved on <date>."
- **Revise or hold on a claim.** That claim is withheld from the app. Withholding the purpose removes the whole profile.
- **Hold on the profile.** The profile leaves the app.
- **Corrections.** If a maintainer corrects a reviewed profile, the build fails unless the profile has a `revisionHistory` entry naming the reviewed digest. The earlier decision stays in `reviews.json`, now stale, and the profile returns to `source_checked` until it is reviewed again. Retiring evidence also needs a `revisionHistory` entry.

Packaging never sends quotes, reviewer names, notes, digests or publication decisions to the app. The runtime profile keys are an explicit allowlist.

## AI pre-review and link check (18 October)

An AI-assisted pre-review was run outside this repository on the two packets. It is stored as `data/website_enrichment/review/ai-prereview-2026-10-18.json`, with a handoff note alongside.

- **It matched the content.** Both packet hashes and all 25 profile digests matched the content it reviewed.
- **It made no decisions.** It names no reviewer, records no attestation and makes no decisions. The importer refuses it (tested), and `reviews.json` still has **0 decisions**.
- **Its suggestions** covered 157 claims: 121 approve-candidates, 14 Revise and 22 Hold.

What was done with it:

1. **Re-checked holds.** Every held link and every link-only target was loaded once, read-only (29 pages, all HTTP 200). For links to a page section, the check confirmed the section exists. This resolved most holds:
   - Odisha eDistrict's `#team` FAQ and `#contact` sections, and Nagaland's `#contact`, exist with the right content.
   - The DigiLocker and Odisha scholarship contact pages have full content.
   - The CPGRAMS, Kerala, Delhi and Janasunani status pages are public lookups by reference number.
2. **Withdrew three links** whose targets did not match their label:
   - Delhi "Help" covers accessibility only.
   - Himachal "Helpline" is a personal-details form.
   - Odisha scholarship "Know your status" asks for an Aadhaar number and an OTP, so it is a sensitive journey.
3. **Applied all 14 Revise suggestions** as recorded revisions.
   - Each has a `revisionHistory` entry: date, author, what changed, the advice it answers, and the digest of the version the AI reviewed.
   - No existing evidence or quote was edited. New evidence was added:
     - Tamil Nadu footer (owner vs developer)
     - Puducherry FAQ (content team and nodal agency)
     - Janasunani contact page (English department name)
     - the full ECI language selector
     - the confirmed page sections
   - Two suggestions were not used word for word because they were reviewer notes rather than citizen-facing text:
     - ECI "jurisdiction" is now **held**, since the state selector is not a statement of coverage.
     - ECI "languages" now lists the full selector instead of hedging.
4. **Showed the advice in the packets.** Each claim shows the advice in a dashed box marked "advisory only, not a decision". Where the wording changed after the advice, the packet says so and shows the earlier text. Each profile also lists its revisions.
5. **Kept Odia translation flags for a person.** The Janasunani operator and jurisdiction now also cite the English contact page, but its purpose still rests on Odia text. An Odia reader should confirm it.

Result: 14 profiles changed content (15 revision entries; Puducherry Revenue was notes only). Every link the app shows has now had its target page loaded at least once.

## First batch: ready for review, no decisions recorded

Packet: `data/website_enrichment/review/review-packet-2026-10-17.html`. Regenerated after the revisions, it has 15 profiles and 97 claims. 50 claims are flagged for explicit source confirmation (63 before the link check), and all 37 help and task links now have a loaded target. The table below shows the state **before** the 18 October revisions; per-claim advice and revisions are in the packet.

Links are counted as loaded / link only. Every decision is currently **Pending (no reviewer)**.

| Website | Official URL | Claims | Source pages | Links loaded / link only | Flagged | Held fields |
|---|---|---:|---:|---|---:|---|
| Tamil Nadu eSevai Portal | https://www.tnesevai.tn.gov.in/ | 8 | 5 | 3 / 1 | 4 | audience |
| eDistrict, Kerala | https://edistrict.kerala.gov.in/ | 7 | 2 | 1 / 1 | 5 | help |
| eDistrict, Odisha | https://edistrict.odisha.gov.in/ | 6 | 1 | 0 / 2 | 5 | tasks, languages |
| Online Citizen Services Portal, Delhi | https://edistrict.delhigovt.nic.in/ | 9 | 2 | 2 / 2 | 6 | — |
| National Scholarship Portal | https://scholarships.gov.in/ | 7 | 1 | 2 / 0 | 4 | — |
| National Career Service | https://ncs.gov.in/ | 5 | 2 | 1 / 0 | 3 | languages |
| CPGRAMS | https://pgportal.gov.in/ | 8 | 3 | 2 / 1 | 5 | — |
| RTI Online | https://rtionline.gov.in/ | 6 | 1 | 2 / 0 | 3 | audience |
| Udyam Registration | https://udyamregistration.gov.in/ | 5 | 1 | 0 / 1 | 4 | — |
| Revenue & Disaster Management, Puducherry | https://revenue.py.gov.in/ | 6 | 1 | 0 / 3 | 5 | audience |
| Income Tax e-Filing | https://www.incometax.gov.in/iec/foportal/ | 6 | 3 | 2 / 0 | 3 | languages |
| Voters' Service Portal (ECI) | https://electoralsearch.eci.gov.in/ | 4 | 1 | 0 / 0 | 3 | help, audience |
| DigiLocker | https://www.digilocker.gov.in/ | 6 | 3 | 1 / 1 | 4 | languages |
| Tamil Nadu state portal | https://www.tn.gov.in/ | 8 | 4 | 3 / 2 | 4 | languages, audience |
| Puducherry state portal | https://www.py.gov.in/ | 8 | 4 | 3 / 2 | 5 | languages, audience |

Claims the reviewer should not approve without checking (specific flags, beyond the standard operator, audience, language and jurisdiction confirmation):

- **TN e-Sevai.** Languages were inferred from which pages were served in Tamil or English. The tracking link goes to `tnedistrict.tn.gov.in` and was not loaded.
- **Odisha eDistrict.** The FAQ and contact links are sections of the home page. The department running the portal is not named; only the Government of Odisha is named, as ServicePlus implementer.
- **National Scholarship Portal.** The operator comes from the site header (MeitY), while the schemes belong to different ministries.
- **ECI Voters' Service Portal.** The jurisdiction is read from the state selector.
- **DigiLocker.** The partner part of the audience rests on a "Become a Partner" link label.
- **Tamil Nadu and Puducherry portals.** The operator wording combines owner and developer, or rests on the web-master entry.
- **Udyam and Puducherry portal.** The grievance links go to other hosts (`champions.gov.in`, `pgportal.gov.in`).
- **Not loaded.** 16 link targets were only seen as links. Open each one before approving.

Before the AI pre-review, two non-content edits were made to `claims.json`: a `batchId`, and `reviewFlags` on seven profiles. Neither changes any profile's content digest. The staged corrections made after the pre-review are described above and recorded in each profile's `revisionHistory`.

## Verification of the workflow

- **Browser smoke test.** The packet was opened in Chrome 155. A throwaway "Test Reviewer (smoke test only)" approved CPGRAMS and saved the file.
  - The save was refused while the name, the confirmation or any decision was missing.
  - The importer accepted the file in memory, and refused it once one flagged confirmation was removed.
  - **Nothing was imported into `reviews.json`**, which still has 0 decisions.
- **`tools/test_editorial_review.py`.** 16 tests cover:
  - decision rules and reviewer identity
  - dates and stale packets
  - flagged-claim confirmation
  - append-only import
  - withheld claims and profile holds
  - required revision history
  - changed, missing and unpinned snapshots
  - quote edits
  - runtime allowlist exclusions
  - draft exclusion
  - queue behaviour
- **App tests.** JVM tests check that `human_reviewed` requires its own review date on or after capture, and that automated profiles carry none. An instrumented test shows the human-review wording and date, and keeps the automated wording for everything else.

## Next actions

1. Two people from the team each review the first-batch packet, or split it with overlap on at least three profiles to compare judgements. Then import the files.
2. Record time per profile and the share of claims revised. Both feed the release gate.
3. Fix anything marked Revise through a staged correction (new evidence id plus a `revisionHistory` entry), then run a new packet for re-review.
