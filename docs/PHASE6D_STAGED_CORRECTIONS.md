# Phase 6D staged corrections

**These are AI-assisted corrections. A person has not approved any of them.** Every profile below is still `source_checked` and the app labels it as checked by an automated agent. `reviews.json` contains 0 decisions.

Source files:

- AI recommendations: `data/website_enrichment/review/ai-prereview-2026-10-18.json` (sha256 `b1a40518…`, 157 claim recommendations over 25 published profiles).
- Staging: `data/website_enrichment/review/staging-2026-10-18.json`. It has one record per recommendation, holding the existing claim, the AI's wording, the current wording, evidence IDs, reason, confidence, concerns, whether a fresh source visit is needed, and the human action required.
- Change history: the `revisionHistory` of each profile in `data/website_enrichment/claims.json`. Each entry gives the date, the agent, what changed, why, and `previousDigest`, the exact content the change replaced. No evidence quote, evidence ID, earlier decision or earlier history entry was overwritten.

Regenerate and check with `python tools/review_reconciliation.py` (add `--check` to validate without writing).

## Staged actions

| Staged action | Count | Meaning |
| --- | --- | --- |
| APPROVE_CANDIDATE | 135 | No problem found by the AI or by reconciliation. A person must still confirm it against the source. |
| REVISE | 14 | The wording was changed to match the cited source more closely. A person must compare the new wording with every source. |
| HOLD | 7 | The claim depends on an automated translation (4), or the link was withdrawn because it leads somewhere sensitive or mislabelled (3). |
| NEEDS_MORE_EVIDENCE | 1 | ECI jurisdiction: withheld from the app until a source states it. |

The AI recommended HOLD 22 times. Every one is still accounted for:

| Outcome | Count |
| --- | --- |
| Resolved by the 18 October read-only link check (target loaded, content matched) | 10 |
| New evidence added | 4 |
| Kept on hold for a fluent Odia reader | 4 |
| Link withdrawn | 3 |
| Turned into a revision (Janasunani operator) | 1 |

## Revisions applied (14 claims)

All 14 came from the 18 October advisory pass. Phase 6D re-checked every applied wording against its cited quotes. The applied wordings now contain no names, numbers or broadening words that the sources lack.

| # | Profile | Claim | Before | Now | Notes |
| --- | --- | --- | --- | --- | --- |
| 1 | Tamil Nadu e-Sevai | operator | Tamil Nadu e-Governance Agency (TNeGA), Information Technology and Digital Services Department, Government of Tamil Nadu | Government of Tamil Nadu. Tamil Nadu e-Governance Agency (TNeGA) is the State Nodal Agency for its e-governance initiatives. | Keeps the owning government apart from the implementing agency. |
| 2 | Janasunani, Odisha | operator | General Administration and Public Grievance Department, Government of Odisha | Department of General Administration and Public Grievance, Government of Odisha | The AI said HOLD because the evidence was in Odia. English evidence from the contact page (`js-contact-en`) now supports the claim. |
| 3 | eDistrict Odisha | operator | Government of Odisha, using NIC's ServicePlus platform | Government of Odisha (it implemented NIC's ServicePlus platform for these services); the operating department is not named | |
| 4 | Odisha State Scholarship | operator | Government of Odisha; several state departments use it as a common platform | Government of Odisha; a common platform used by several state departments, with no single operating department named | |
| 5 | Nagaland Common Scholarship | operator | Government of Nagaland (no department is named on the home page) | Government of Nagaland (official portal); the responsible department is not named on the home page | |
| 6 | Tamil Nadu Government portal | operator | Government of Tamil Nadu; developed by National Informatics Centre, Tamil Nadu State Centre | Government of Tamil Nadu (owns and maintains the content); site designed and developed by National Informatics Centre | |
| 7 | Puducherry Government portal | operator | Government of Puducherry; website managed by the Department of Information Technology | Government of Puducherry. Officials of the Directorate of Information Technology manage the portal's content; Puducherry e-Governance Society is the nodal agency for the State Portal project. | The old wording named the wrong body ("Department"). |
| 8 | Himachal Pradesh Government portal | operator | Government of Himachal Pradesh; designed, developed and hosted by National Informatics Centre, Himachal Pradesh State Centre | Government of Himachal Pradesh (portal owner); designed, developed and hosted by National Informatics Centre, Himachal Pradesh State Centre | |
| 9 | National Scholarship Portal | operator | Ministry of Electronics & Information Technology, Government of India (named in the site header) | Government of India; the site header names the Ministry of Electronics & Information Technology | **Corrected again in Phase 6D**; see below. |
| 10 | DigiLocker | audience | Citizens; partner organisations (document issuers and requesters) have a separate section | Citizens who want to access authentic digital documents | The AI's wording added "only", which the source does not say. It was left out. |
| 11 | Bhavishya | audience | Retiring employees and the administrative authorities that process their pensions | Retiring employees and the administrative authorities involved in processing their pensions; which employees are covered is not stated on the checked pages | Needs a fresh source visit to state coverage. |
| 12 | National Career Service | audience | Job seekers, employers, skill providers, career counsellors, placement organisations, households and government departments | Job seekers, employers, skill providers, career counsellors, local service providers, career centres, placement organisations, households and government departments | |
| 13 | Tamil Nadu e-Sevai | languages | Tamil and English (the home page was served in Tamil; the About, Contact, FAQ and service-list pages in English) | Tamil (home page) and English (About, Contact, FAQ and service-list pages); no language switch was found | |
| 14 | ECI Voters' Service Portal | languages | English, Hindi, Tamil and other Indian languages (language selector observed) | English page with a language selector listing Hindi, Assamese, Bengali, Konkani, Gujarati, Malayalam, Odia, Punjabi, Tamil, Telugu, Marathi, Kannada and Urdu | Now cites the full selector. |

### National Scholarship Portal: correction found by reconciliation

On 18 October the operator revision also added the sentence "Each scholarship scheme belongs to its own ministry or body." Phase 6D's wording check found that this sentence is not in the cited source (the site header). It was removed with a second `revisionHistory` entry; the first entry is unchanged. The point is still made, as an editorial note, in the profile's uncertainty notes, which the app shows: "Each scholarship scheme belongs to its own ministry or body; scheme owners are shown on the individual service records, not here." A reviewer should confirm that this note is fair.

This is the only content change Phase 6D made to `claims.json`.

## Claims withdrawn or withheld (4)

These claims are no longer in the app, but they remain in the record:

- The `revisionHistory` entry names each removed link.
- A `heldFields` entry gives the reason, which the packet shows.
- The original evidence is kept unchanged; no evidence was retired.

| Profile | Claim | Why | Before it can return |
| --- | --- | --- | --- |
| ECI Voters' Service Portal | jurisdiction ("All states and union territories, chosen in the search") | The evidence shows a state selector, not a statement of coverage. The AI's wording added "full" and "UT", which the source does not have. | A person finds a source that states the coverage. |
| Delhi Online Citizen Services | help link `…/Public/Help.html` | The page covers accessibility only, not help with services. | A real help page is found. |
| Himachal Pradesh Government portal | "Helpline" link | It opens a form that asks for personal details. JanSetu does not link to such forms as help. | A plain helpline page is found. |
| Odisha State Scholarship | task link "Know your application status" | The page asks for Aadhaar and an OTP. The app's application-journey restrictions were **not** loosened to keep it. | Not planned; it stays out unless policy changes through the normal review route. |

## Evidence added (4 claims)

| Profile | Claim | New evidence |
| --- | --- | --- |
| Janasunani, Odisha | jurisdiction | `js-contact-en` (English contact page) |
| eDistrict Odisha | help: `#contact` section | `od-contact-section` (the section is on the home page) |
| eDistrict Odisha | help: `#team` section, labelled FAQ | `od-faq-section` |
| Nagaland Common Scholarship | help: `#contact` section | `ng-contact-section` |

## Holds for a language reader (4 claims)

Four Janasunani claims (purpose, contact link, FAQ link and grievance-status link) depend on Odia text that the automated agent translated. They stay visible, but staged as HOLD with the safety action `withhold_claim_if_translation_not_confirmed`. If an Odia reader cannot confirm the translation, the claims are withheld.

## Holds resolved by the link check (10 claims)

The AI held these links because their targets had not been loaded. On 18 October each target was loaded read-only, and its content matched the label:

- Delhi: Track an application
- Himachal Pradesh: departmental citizen services
- DigiLocker: contact us
- Odisha State Scholarship: contact us
- Puducherry Revenue: citizen charter and contacts
- Jharkhand CM Fellowship: grievances
- Tamil Nadu Government portal: contact directory
- Uttarakhand Treasuries: citizen charter
- National Scholarship Portal: PFMS helpdesk

They are staged as APPROVE_CANDIDATE with high confidence. A person must still open each one.

## What was not changed

- No profile moved to `human_reviewed`, and no reviewer name, review date or attestation was added anywhere.
- `reviews.json` is untouched; no AI recommendation was imported.
- `BrowserPolicy`, the allowlists and the application-journey restrictions are unchanged.
- No HTTP error, captcha, TLS check or access restriction was bypassed.
- The 8 protected catalogue and source files are unchanged against the Phase 4 baseline.
