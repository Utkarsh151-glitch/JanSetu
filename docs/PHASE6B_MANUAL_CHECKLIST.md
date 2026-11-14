# Phase 6B manual checklist

These checks need a person. Automated results are in [QA_REPORT.md](QA_REPORT.md#phase-6b---17-october-2026); batch details are in [PHASE6B_ENRICHMENT_REPORT.md](PHASE6B_ENRICHMENT_REPORT.md).

Use the isolated `in.jansetu.app.qa` build for app checks. Do not clear, uninstall or reseed the preview app (`in.jansetu.app`). If you install this build on the preview later, use `tools/phase5_install_preview.py`, which compares protected hashes before and after.

## 1. Human review of the 15 published profiles

For each profile in [claims.json](../data/website_enrichment/claims.json) with `"status": "published"`:

- [ ] Open each evidence URL in a normal browser and confirm that the quoted text is still on the page.
- [ ] Read the JanSetu wording for purpose, operator, audience, jurisdiction and languages. Each should say no more than the quote supports. Cross out anything that reads as advice, eligibility or a promise.
- [ ] Open every help and task link. It must reach the named public page (FAQ, contact, help, grievance information, status lookup, service list), not a sign-in, application or payment page.
- [ ] Check the notes ("uncertainty") still hold.
- [ ] Only after this review: set `"reviewStatus": "human_reviewed"` and add `"humanReviewer": "<name>"` and the date. Then run `python tools/website_enrichment.py validate`. Never mark a profile human-reviewed without doing the review.

Specific points to settle:

- [ ] **TN e-Sevai.** Is there a language switch? The languages value says only which pages were served in Tamil and which in English. The tracking page is on `tnedistrict.tn.gov.in` and was not loaded.
- [ ] **Kerala eDistrict.** Does `generalService.htm?event=faq` (without a token) still load the FAQ on another day?
- [ ] **Odisha eDistrict.** Do the `#team` (FAQ) and `#contact` anchors still jump to those sections?
- [ ] **NSP.** Is "Ministry of Electronics & Information Technology" the right operator to show, or should the operator be held?
- [ ] **ECI Voters' Service Portal.** Find out whether a help or contact page exists outside the single-page app.
- [ ] **Puducherry Revenue, Delhi, TN portal and Puducherry portal.** Open the links that were seen but not loaded.

## 2. Held websites

- [ ] Open `https://uidai.gov.in/en/my-aadhaar` (HTTP 500 twice), `https://www.india.gov.in/` (403) and `https://msme.gov.in/` (403) in a normal browser on a normal connection. Record what you see. Do not use any workaround for access controls.
- [ ] If the pages load, capture them with `tools/website_enrichment.py capture` from a new batch file and write claims in the same way. Keep them held if they still cannot be observed.

## 3. Website page (Android)

Use a checked profile (CPGRAMS, Delhi eDistrict), a held reviewed record (UIDAI) and a directory-only record (any district site).

- [ ] The six sections appear in order:
  1. What this website is for
  2. Department or organisation
  3. Who it serves and where
  4. Verified services and tasks
  5. Official help and contact
  6. When checked and where from
- [ ] A directory-only record says **Not checked yet** and labels the IGOD listing and jurisdiction as directory information. Nothing reads like a checked fact.
- [ ] "Not stated on the official pages JanSetu checked" appears only where a field was looked for (for example, ECI help) and is visibly different from "Not checked yet".
- [ ] The review status says the check was automated and that no person has reviewed it.
- [ ] A verified-service row opens the service page, and Back returns to the website.
- [ ] Help and task links open the official page in the browser. JanSetu never opens sign-in pages in its own browser.
- [ ] Hindi: section titles are in Hindi, and English summaries carry the note "जाँची गई जानकारी अंग्रेज़ी में जनसेतु का सारांश है।"
- [ ] Light and dark themes, Larger text, landscape and a narrow phone show no clipping.
- [ ] TalkBack:
  - section titles are headings
  - link rows are announced as buttons with their label and host
  - "Sources" announces "Show sources" / "Hide sources"
  - status labels are read once

## 4. Search

- [ ] Website cards show **Website** with **Purpose checked** or **Information under review**. Service cards show **Verified service**.
- [ ] The "Services and websites" chip has **Websites with checked information**. Choosing it shows only checked websites and no services. Clearing filters restores everything.
- [ ] Spot-check task intents in English and Hindi: scholarship / छात्रवृत्ति, pension / पेंशन, income certificate, grievance, state government services. A checked record should not hide a better-matching service, and no unchecked record should claim checked information.
- [ ] "grievance": CPGRAMS appears above directory sites that merely have "Grievance" in the title. Decide whether the +3 ranking boost for checked records is the right strength.

## 5. Separation and data protection

- [ ] Service pages are unchanged. No eligibility, documents, fees or deadlines appear on website pages.
- [ ] `python tools/catalog_completeness.py --markdown` reproduces the numbers in the catalogue quality report.
- [ ] Protected files still match `data/validation_reports/phase4-source-baseline.json`. The QA gate checks this.

## 6. Permissions before any public release

- [ ] Obtain and record NIC permission for IGOD-derived data. `python tools/check_catalog.py --public-release` must pass.
- [ ] Decide whether short summaries of official website text are acceptable to publish, and record the basis.
- [ ] Decide what happens to `data/website_enrichment/research/` (full page text) and the quotes in `claims.json`. Keep them out of any public repository or package until reuse is cleared.
