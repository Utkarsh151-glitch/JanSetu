# Phase 6D manual checklist

Human review is deferred by project decision, but the workflow is ready for when reviewers are available. These steps need people; the tooling only records and enforces their decisions. AI-assisted review is not human approval, and no step below may be completed by an AI tool, a test identity or a smoke-test account.

Reports:

- [reconciliation report](PHASE6D_REVIEW_RECONCILIATION_REPORT.md)
- [staged corrections](PHASE6D_STAGED_CORRECTIONS.md)
- [human review queue](PHASE6D_HUMAN_REVIEW_QUEUE.md)

The Phase 6C checklist ([PHASE6C_MANUAL_CHECKLIST.md](PHASE6C_MANUAL_CHECKLIST.md)) still applies. This list replaces its sections 1 and 3 with the versioned packet.

## 1. Before review starts

- [ ] Maintainer runs `python tools/review_reconciliation.py --check`. It must report `"problems": []`.
- [ ] Maintainer runs `python tools/editorial_review.py check`. Every evidence pin must match its saved snapshot.
- [ ] If either check fails, or a profile has changed since the packet was made, regenerate:
  1. `python tools/review_reconciliation.py`
  2. `python tools/editorial_review.py packet --all`

  Send reviewers only the newest `review-packet-all-*.html`.
- [ ] Choose two named reviewers and record their roles. A reviewer must be a real person; names like "Test", "Smoke", "Demo" or "Claude" are refused.

## 2. Published profiles (25)

- [ ] Open the versioned packet in a normal browser and enter your full name, role and the review date.
- [ ] Work from the "Start here: highest-risk claims" list down.
- [ ] For each claim:
  - Read the staging block.
  - Open every source and find the quote.
  - Decide Approve, Revise or Hold.

  The staged action and the AI advice are hints only.
- [ ] For the 14 revised claims, compare the current wording with every cited source. Do not approve wording that names a body, place, number or group the source does not.
- [ ] For the 10 links resolved by the link check, open each link yourself.
- [ ] Tick "I opened the source" only when you did.
- [ ] Approve a profile only if every claim is approved.
- [ ] Save the decisions file. Note the minutes spent per profile.

## 3. Language review

- [ ] An Odia reader checks the Janasunani quotes (`js-*`) and the Odisha State Scholarship quote (`os-odia`), and the English wording that depends on them. If a translation cannot be confirmed, hold that claim.
- [ ] A Tamil reader checks `tn-home-services` (Tamil Nadu e-Sevai).
- [ ] A Malayalam reader checks the Sevana Pension draft (`sp-search`, `sp-schemes`).
- [ ] A Hindi reader checks the e-Kalyan draft (`ek-dept`, `ek-reg`, `ek-faq`).

## 4. Drafts and held websites

- [ ] Work through the 8 draft tasks in the [queue](PHASE6D_HUMAN_REVIEW_QUEUE.md#2-drafts-8-and-held-websites-5). For each, record publish, rewrite or hold, with your name and basis.
- [ ] Open DoPPW, Gujarat DPPF, UIDAI My Aadhaar, the India portal and Ministry of MSME in a normal browser, and record what loads. Do not use a VPN, solve a captcha for the tool, or change TLS settings.
- [ ] Open the three draft links that were seen but not opened. Do not link to anything that asks for sign-in, personal details, Aadhaar or an OTP.

## 5. Maintainer: import

- [ ] `python tools/editorial_review.py import <decisions file>`. The import is refused as a whole if:
  - the file contains a stale profile
  - a decision is missing
  - the reviewer looks automated or like a placeholder
  - the file is an AI recommendation or a staging file
- [ ] Never import `ai-prereview-*.json`, `staging-*.json` or `review-queue.json`; the importer refuses them.
- [ ] `python tools/editorial_review.py status`, then rebuild and run the gate.
- [ ] Apply "Revise" decisions as new revisions:
  - Add new evidence under a new ID.
  - Record the change in `revisionHistory` with `previousDigest`.
  - Never edit an existing quote.

## 6. Release blockers (unchanged)

- [ ] NIC permission for IGOD-derived data is recorded, and `python tools/check_catalog.py --public-release` passes.
- [ ] A decision on reusing official-page text (quotes and summaries) is recorded.
- [ ] Release artifacts are confirmed to exclude everything under `data/website_enrichment/` except what the runtime packager emits: no quotes, snapshots, evidence index, AI advice, staging, queue or packets. `tools/test_review_reconciliation.py` checks the built APKs and the extension folder, but a person confirms the final store bundle.
- [ ] Human decisions exist for the published profiles, under the release-gate thresholds still to be agreed ([PHASE6C_RELEASE_GATE_PROPOSAL.md](PHASE6C_RELEASE_GATE_PROPOSAL.md)).

## 7. Device care

- [ ] Before long device runs, check free host memory; the Phase 6D gate ran with about 1 GB free.
- [ ] If the emulator hangs, restart it only with `-no-snapshot-load -no-snapshot-save` and compare the preview hashes.
- [ ] Never clear, uninstall or reseed `in.jansetu.app`.
