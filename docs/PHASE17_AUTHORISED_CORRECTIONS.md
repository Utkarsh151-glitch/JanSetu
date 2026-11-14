# Phase 17: authorised RTI corrections and NSP extension cleanup

18 October 2026. The project owner explicitly authorised changes to, and rebaselining of, the protected files needed for two tasks only:

1. Evidence-backed RTI guidance corrections and the "Before you request" card.
2. Complete removal of the withdrawn NSP Readable text extension pilot.

Nothing else was authorised, and nothing here signs, distributes or releases JanSetu.

## Task A: RTI guidance

### Evidence (captured read-only, TLS verified, nothing submitted)

All three captures used `python tools/service_catalog.py research <url>` (curl with HTTPS only and normal TLS validation). No RTI form was opened, no personal data entered, nothing uploaded, paid or submitted.

| Capture | URL | Result |
| --- | --- | --- |
| `data/services/research/rti-2026-10-18.json` | https://rtionline.gov.in/guidelines.php?request | HTTP 200; sha256 `f0ede7ab2e561ca3…`. The text is identical to the 15 October capture; only incidental markup bytes differ. |
| `data/services/research/rti-rules-2012-pdf-2026-10-18.json` | https://cic.gov.in/sites/default/files/RTI/RTIRules2012.pdf | HTTP 200; sha256 `8d73989088febe13…`. The Right to Information Rules, 2012 (G.S.R. 603(E)), as published by the CIC. |
| `data/services/research/rti-rules-2012-cic-2026-10-18.json`, `…-dopt-…json` | https://cic.gov.in/node/80, https://dopt.gov.in/acts/rti-rules-2012-0 | HTTP 200; index pages used to locate the official PDF |

### The five rules: before and after

| # | Rule | Before Phase 17 | After | Official source and wording | Kind |
| --- | --- | --- | --- | --- | --- |
| 1 | BPL fee exemption | In a combined fact: "BPL fee exemption requires the appropriate government-issued certificate…" | Conditional document: "If you are below the poverty line: a copy of the BPL certificate issued by the appropriate government, attached with the application, to be exempt from the fee." | Guidelines: "No RTI fee is required to be paid by any citizen who is below poverty line as per RTI Rules, 2012. However, the applicant must attach a copy of the certificate issued by the appropriate government…". **RTI Rules, 2012, rule 5**: "No fee under rule 3 and rule 4 shall be charged from any person who is below poverty line provided a copy of the certificate issued by the appropriate Government… is submitted alongwith the application." | Statutory (central RTI rules) |
| 2 | Do not upload Aadhaar or PAN | Same combined fact: "Do not upload Aadhaar, PAN or other identity documents." **This omitted the official BPL-card exception.** | Procedure: "Do not upload an Aadhaar card, PAN card or any other personal identification. The only exception is a BPL card when you claim the fee exemption." | Guidelines: "Do not upload Aadhar Card or PAN Card or any other personal Identification (Except BPL Card)." | RTI Online portal instruction |
| 3 | Payment methods | Not recorded (`fees` was null) | Fees: "₹10 per request (Right to Information Rules, 2012, rule 3). RTI Online accepts internet banking, Mastercard or Visa credit and debit cards, UPI and RuPay cards. Applicants below the poverty line pay no fee if they attach a copy of their BPL certificate (rule 5)." | Guidelines: payment modes "(a) Internet banking; (b) Using credit/debit card of Master/Visa; (c) Using UPI (d) Using RuPay Card. Fee… as prescribed in the RTI Rules, 2012." **Rule 3**: "accompanied by a fee of rupees ten". | The modes are a portal instruction; the amount is statutory |
| 4 | Wait 24–48 hours | Not recorded | Procedure: "If you have paid but no registration number appears, wait 24–48 working hours while the payment is reconciled, and do not pay again. If it has still not appeared, email the RTI Online helpdesk (helprtionline-dopt[at]nic[dot]in) with the transaction details." | Guidelines: "…if applicant didn't receive the registration number then applicant is advised to wait for the 24-48 working hours as registration number will be generated after reconciliation. Please do not make additional attempt to make payment again. If it is not generated within 24-48 hours kindly send an e-mail at helprtionline-dopt[at]nic[dot]in with transaction details." | Portal instruction. The qualifications are *working* hours, and only when no registration number arrives. |
| 5 | First appeal is free | `rti-first-appeal` fees: "No fee for the first appeal." | **Unchanged.** It is still accurate against today's identical text, and the card cites it from that record. | Guidelines: "As per RTI Act, no fee has to be paid for first appeal." The RTI Rules, 2012 prescribe fees only for requests (rule 3) and supplying information (rule 4). | Stated by the official portal for central first appeals |

**Scope qualification**, shown on the card and in the record's review notes: these rules apply to requests to **central** public authorities through RTI Online. State public authorities follow their own RTI rules.

### How it was published (official workflow)

1. `python tools/phase17_rti_candidate.py` builds the revised record. It first checks that every cited phrase occurs in the official captured text.
2. `python tools/service_catalog.py import data/services/phase17-rti-candidate.json --revision` stages a `REVISION_NEEDS_REVIEW` queue entry.
3. `python tools/service_catalog.py publish data/services/phase17-rti-approvals.json` publishes it.
   - The approval names "Claude Code automated official-source check under the project owner's Phase 17 authorisation (not a human editorial review)" and pins the candidate digest `b179c43a99c38ee3…`.
   - The service catalogue version goes from 13 to 14.

Only `rti-central-request` changed, in six fields: `conditionalDocuments`, `evidence`, `fees`, `procedure`, `reviewNotes` and `verifiedOn`. The other 51 services, their order and the organisations are identical. The review queue keeps all 86 earlier entries and adds one `PUBLISHED` revision entry. The evidence locator's corrupted character (U+FFFD) is fixed in the revised evidence.

A side effect, and an improvement: Prepare documents now lists only the BPL certificate as a conditional document for the RTI request. Before, it also listed the "do not upload" instruction as if it were a document requirement.

### "Before you request" card

- **Where:** `ui/BeforeYouRequest.kt`, shown on the RTI request service page above "Official actions".
- **What it shows:** the five rules, each the exact service fact with "Source: publisher · checked date" links to official HTTPS pages, and the central-authority scope note.
- **Boundary statement:** "On RTI Online you read the guidelines, tick the acknowledgement yourself, and then choose Submit or Cancel. JanSetu does not tick it, submit, pay for or track your request, and does not check your documents."
- **Missing facts:** if a rule is missing from the record, the card shows fewer items and never fills the gap.
- **Unchanged:** the official site and its checkbox, Submit and Cancel. RTI Online opens in the person's browser; it is not an in-app host. The tracker shortcut and application route are unchanged.
- **Registry:** in `data/site_interventions.json`, the RTI page's `jansetu_guidance` control is now `active`, with a note recording the owner's authorisation. The page status is unchanged (not `approved`), because the content has not had a human editorial review (RB-03).

## Task B: NSP Readable text pilot removed

| Removed | Reason |
| --- | --- |
| `scripting` permission (`extension/manifest.json`) | Used only to insert and remove the pilot stylesheet |
| `extension/readability.css` | No remaining feature uses it. Archived as evidence at `data/retired/nsp-readable-text.css`, outside the package. The Phase 6A fixture tests and audit tools read the archive. |
| `background.js` tab-navigation listener | Removed the pilot stylesheet after leaving the reviewed route. Only `setPanelBehavior` remains. |
| `readablePaths` and `readableSiteForUrl` (`core.mjs`); `readabilityControls` (`panel.js`); `readability-panel.css` and its link (`panel.html`) | Dormant pilot code and its styles |
| Manifest description "opt-in readability aid"; the panel notice about Readable text; the privacy text "the extension removes it" | They described a capability that no longer exists |

**Kept, because remaining features need them:**
- `sidePanel`, for the companion panel itself.
- `activeTab`, which, after the person clicks the icon, lets the panel read the active tab's URL to show "On this website" for a listed official host.

There are no host permissions, content scripts, web-accessible resources or external messaging. The extension version goes from 0.2.0 to 0.3.0.

**Safeguards strengthened, not weakened:**
- `tools/site_interventions.py` again requires the allowlist and background routes to match exactly. The temporary allowance for a retired route is removed.
- It now fails if the extension declares `scripting`, content scripts or host permissions, or packages any stylesheet other than `panel.css`, without a live reviewed styling decision.

## Protected-file rebaseline

The procedure, and the record in `data/validation_reports/phase17-protected-rebaseline.json`:

1. **Before any edit:** all 8 protected hashes were recorded and matched the baseline (`phase17-protected-before.json`). Exact copies of the 5 in-scope files and the old baseline are in `data/validation_reports/phase17-originals/`.
2. **After editing:** only the `protectedSha256` values of the authorised files were changed in `data/validation_reports/phase4-source-baseline.json`, and the deleted stylesheet's line was removed. The `sourceSha256` and `phase3c` sections are unchanged, and all four checkers are unchanged.

| File | Before | After | Why |
| --- | --- | --- | --- |
| `data/services/catalog.json` | `dbbfac97529d4376…` | `312c24ac202da785…` | Task A RTI revision; version 13 → 14 |
| `data/services/review_queue.json` | `ff5bdfbb87691edc…` | `4de483690d4de461…` | Task A: the official workflow appended one revision entry |
| `extension/manifest.json` | `12daac2513e721be…` | `c1cbff7bc289f556…` | Task B: `scripting` removed; description and version |
| `extension/background.js` | `b7445ecb259caeba…` | `324e0677bdf03cf4…` | Task B: listener removed |
| `extension/readability.css` | `729be501fff81ffd…` | removed | Task B: file deleted; archived outside the package |
| `app/src/main/assets/catalog.json` | `af25cfc31eed917c…` | **unchanged** | — |
| `data/review_queue.json` | `5f5a69526379ca3d…` | **unchanged** | — |
| `app/src/main/assets/catalog-update-public.der` | `08b89c0df4afa20b…` | **unchanged** | — |

## Release boundaries

This phase does not change any release blocker other than RB-14 and RB-15, which are now resolved for these two items. JanSetu remains **release blocked**: NIC/IGOD permission, the decision on reusing official text, human editorial and language reviews, physical-device and accessibility checks, signing-key custody, the privacy-policy URL and publication authorisation are all still outstanding.
