# Phase 6C manual checklist

These steps need people. The tooling records and enforces decisions; it cannot make them. Reports:

- [editorial review](PHASE6C_EDITORIAL_REVIEW_REPORT.md)
- [enrichment](PHASE6C_ENRICHMENT_REPORT.md)
- [release gate proposal](PHASE6C_RELEASE_GATE_PROPOSAL.md)

## 1. Review the first batch (15 profiles)

- [ ] Open `data/website_enrichment/review/review-packet-2026-10-17.html` in a normal browser. It works offline.
- [ ] Enter your full name, role and the review date.
- [ ] For each profile:
  - Open the official website.
  - For every claim, open each source page and find the quoted text. The packet shows where it is in the saved copy and highlights the surrounding text.
  - Choose **Approve**, **Revise** or **Hold** for the claim. Revise and Hold need a note saying what is wrong.
  - For flagged claims (operator, audience, language, jurisdiction, links that were not loaded, other-host links, translations), open the source and the link destination, then tick "I opened the source".
  - Decide the profile: Approve only if every claim is approved.
- [ ] Dashed boxes show the AI pre-review's advice. It is a hint, not a decision. Where it says the advice was given on earlier wording, judge the current wording. Decide every claim yourself from the source.
- [ ] Do not approve anything you did not check yourself. Leave a profile undecided to skip it.
- [ ] Save the decisions file and send it to the maintainer.
- [ ] Note the minutes spent per profile. The release gate needs this number.
- [ ] Suggested split: two reviewers, overlapping on at least three profiles (for example CPGRAMS, TN e-Sevai and the Puducherry portal) to compare judgements.

## 2. Maintainer: import and apply

- [ ] `python tools/editorial_review.py import <decisions file>`. If anything is missing it refuses the whole file and lists why.
- [ ] `python tools/editorial_review.py status` shows the effective state, withheld claims and stale decisions.
- [ ] Rebuild the app. Approved profiles show "Reviewed by a person … on <date>"; withheld claims disappear; held profiles leave the app.
- [ ] To correct a claim marked Revise:
  1. Add new evidence with a **new** id. Never edit an existing quote.
  2. Change the field.
  3. Add a `revisionHistory` entry with the date, who, what changed, and `previousDigest` (shown by `status`; also in the packet).
  4. If evidence is removed, list it in `retiredEvidence`.
  5. Run `python tools/editorial_review.py index`, then `check`, then `packet --batch <id>` for re-review.
- [ ] Never write `human_reviewed` or a reviewer name into `claims.json`; the validator refuses it.

## 3. Second batch (20 websites)

- [ ] Review the 10 published profiles with `review-packet-2026-10-18.html`, in the same way as section 1.
- [ ] Odia, Malayalam and Hindi quotes: a reader of the language confirms the translation flags (Janasunani, Sevana Pension, e-Kalyan).
- [ ] Decide each of the 8 drafts:
  - Odisha pension grievance (staff sign-in gateway): how do pensioners raise grievances?
  - Sevana Pension (Malayalam)
  - CPWD Sewa: confirm scope and find a working contact page.
  - Madhya Pradesh DoPPFI and CPAO: find an About or Profile page.
  - e-Kalyan (Hindi)
  - HP ePASS: is it still in use, given news from 2019?
  - Telangana ePASS: department, and the About and FAQ pages, which failed to load.

  To publish a draft, change its status and `publicationDecision` with your name and basis, then put it through review.
- [ ] Held sites: open DoPPW (HTTP 403) and Gujarat DPPF (captcha) in a normal browser. Record what you find. Do not use any workaround.

## 4. App checks (isolated QA build)

- [ ] An approved profile shows the human-review wording and its review date; automated ones still say "A person has not reviewed it yet".
- [ ] Draft and held websites show "Information under review" and "Not checked yet".
- [ ] Search "grievance" or "scholarship": checked portals lead; the "Websites with checked information" filter shows only checked websites.
- [ ] TalkBack reads the review status, and the new second-batch pages in Hindi mode show the English-summary note.

## 5. Permissions and release decisions

- [ ] Record NIC permission for IGOD-derived data. `python tools/check_catalog.py --public-release` must pass.
- [ ] Record the decision on reusing official-page text.
- [ ] Confirm that release artifacts exclude `data/website_enrichment/research/`, quotes, `evidence_index.json` and review packets.
- [ ] Decide the release-gate thresholds (B2, C1, D4) after the first-batch review data exists.

## 6. Device care

- [ ] The emulator hung twice (17 and 18 October). Before long device runs, check free host memory and stop idle build daemons.
- [ ] If the emulator hangs, restart it only with `-no-snapshot-load -no-snapshot-save`, then compare preview hashes. See `data/validation_reports/phase6*-emulator-restart.json`.
- [ ] Never clear, uninstall or reseed `in.jansetu.app`.
