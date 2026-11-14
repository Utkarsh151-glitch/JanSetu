# Phase 6D human review queue

Work for real reviewers, highest risk first. **Nothing in this queue is a decision.** The AI pre-review and Phase 6D staging are hints that save reviewers time. They do not replace checking the source, and they never count as approval.

Files (all in `data/website_enrichment/review/`, internal only):

| File | Use |
| --- | --- |
| `review-packet-all-2026-10-18-va8299123171f.html` | **Start here.** One versioned packet covering all 25 published profiles, in risk order. It works offline in a normal browser. |
| `review-packet-2026-10-17.html`, `review-packet-2026-10-18.html` | The same profiles split by batch, for sharing work between reviewers. |
| `review-queue.json` | The ordered claim queue, the draft and held tasks, and the language reviews. |
| `staging-2026-10-18.json` | Full staging records behind the packet. |

The packet version (`a8299123171f`) is a hash of every profile's current content plus the staging file. If a profile changes, the staging no longer matches it, and the packet tool refuses to build a new packet until staging is regenerated. If a decision file covers a profile that has changed since its packet was made, the importer refuses the whole file and names each stale profile.

## What each claim in the packet shows

- **Current publication state**: whether the claim is in the app now, and its review status (all are "checked by an automated agent").
- **Original automated claim**, with the wording the AI saw if it has since changed.
- **Staging**: review order, risk category, staged action, outcome and confidence, then "What you must check", unresolved concerns and follow-up.
- **AI advice** (dashed box): its recommendation, reason and any suggested wording.
- **Flags** saying why the claim needs explicit confirmation.
- **Sources**: each cited page, the saved quote, where it sits in the saved copy, and a link to the live page.

## 1. Published profiles (25), in priority order

Claims are ordered by risk (operator, then jurisdiction and audience, purpose, task links, language, help links). Each profile is placed by its riskiest claim.

| Order | Profile | Batch | Claims | Flagged | Staged |
| --- | --- | --- | --- | --- | --- |
| 1 | Tamil Nadu e-Sevai | 17 Oct | 8 | 4 | 2 revise, 6 approve candidates |
| 2 | Janasunani, Odisha | 18 Oct | 6 | 3 | 1 revise, 4 hold (Odia), 1 approve candidate |
| 3 | eDistrict Odisha | 17 Oct | 6 | 5 | 1 revise, 5 approve candidates |
| 4 | Odisha State Scholarship | 18 Oct | 7 | 4 | 1 revise, 6 approve candidates |
| 5 | Nagaland Common Scholarship | 18 Oct | 5 | 4 | 1 revise, 4 approve candidates |
| 6 | Tamil Nadu Government portal | 17 Oct | 8 | 2 | 1 revise, 7 approve candidates |
| 7 | Puducherry Government portal | 17 Oct | 8 | 3 | 1 revise, 7 approve candidates |
| 8 | Himachal Pradesh Government portal | 18 Oct | 6 | 4 | 1 revise, 5 approve candidates |
| 9 | National Scholarship Portal | 17 Oct | 7 | 4 | 1 revise, 6 approve candidates |
| 10 | CPGRAMS | 17 Oct | 8 | 4 | 8 approve candidates |
| 11 | DigiLocker | 17 Oct | 6 | 3 | 1 revise, 5 approve candidates |
| 12 | eDistrict Kerala | 17 Oct | 7 | 4 | 7 approve candidates |
| 13 | Broadcast Seva | 18 Oct | 3 | 2 | 3 approve candidates |
| 14 | Puducherry Revenue and Disaster Management | 17 Oct | 6 | 2 | 6 approve candidates |
| 15 | ECI Voters' Service Portal | 17 Oct | 3 | 2 | 1 revise, 2 approve candidates; jurisdiction withheld |
| 16 | Delhi Online Citizen Services | 17 Oct | 8 | 4 | 8 approve candidates |
| 17 | e-MSME Seva, Odisha | 18 Oct | 3 | 2 | 3 approve candidates |
| 18 | Bhavishya | 18 Oct | 7 | 3 | 1 revise, 6 approve candidates |
| 19 | Jharkhand CM Fellowship | 18 Oct | 8 | 4 | 8 approve candidates |
| 20 | Uttarakhand Treasuries, Pension & Entitlements | 18 Oct | 6 | 3 | 6 approve candidates |
| 21 | Income Tax e-Filing | 17 Oct | 6 | 3 | 6 approve candidates |
| 22 | National Career Service | 17 Oct | 5 | 3 | 1 revise, 4 approve candidates |
| 23 | RTI Online | 17 Oct | 6 | 3 | 6 approve candidates |
| 24 | Udyam Registration | 17 Oct | 5 | 4 | 5 approve candidates |
| 25 | Sevana Pension Direct To Home, Kerala | 18 Oct | 5 | 2 | 5 approve candidates |

Total: 153 current claims. The other 4 of the 157 AI recommendations were for claims since withdrawn; see [staged corrections](PHASE6D_STAGED_CORRECTIONS.md).

For every claim, the reviewer:

1. Opens each cited source in a normal browser and finds the quoted text.
2. Decides Approve, Revise or Hold. Revise and Hold need a note.
3. For flagged claims, also opens the link destination and ticks "I opened the source".

Approve a profile only if every one of its claims is approved.

Points to look at closely:

- **Operators (9 revised).** Check that the owner, the implementing agency and the developer or host are each named correctly, and that no body is named that the source does not name.
- **Janasunani (4 holds).** These need an Odia reader; see section 3.
- **ECI jurisdiction** is withheld. Look for a page that states coverage. A state selector does not count.
- **Bhavishya audience** says coverage "is not stated on the checked pages". A fresh visit may find a statement that can replace this.
- **National Scholarship Portal**: confirm that the uncertainty note about scheme owners is fair.
- **DigiLocker** purpose and audience both say "authentic *digital* documents", but the cited quote says "authentic documents". Decide whether the paraphrase is acceptable or should be revised. The reconciliation wording check lists such added words for information; it does not block them.
- **10 links resolved by the link check** are approve candidates with high confidence, but the reviewer must still open each one.

## 2. Drafts (8) and held websites (5)

None of these is shown in the app. A draft is never published because its title looks plausible: it needs a statement of purpose from the site and a named reviewer's decision. A held site stays out of the app until a person records what it shows in a normal browser, with no workaround of any access control.

| Website | State | Task |
| --- | --- | --- |
| Odisha pension grievance | draft | The site offers only staff sign-in. Find out how pensioners raise grievances, then publish, rewrite or hold. |
| Sevana Pension (Malayalam) | draft | A Malayalam reader confirms the purpose and the menu translations. Open the pension search link, which was seen but not opened. |
| CPWD Sewa | draft | Confirm its scope and find a working contact page. Open the complaint-status link, which was seen but not opened. |
| Madhya Pradesh DoPPFI | draft | Find an About page or statement of purpose and capture it read-only before publishing. |
| e-Kalyan (Hindi) | draft | A Hindi reader confirms the purpose. Open the FAQ link, which was seen but not opened. |
| HP ePASS | draft | Check whether it is still in use (its latest news is from 2019) before publishing anything. |
| Telangana ePASS | draft | Open the About, Contact and FAQ pages in a normal browser (they returned HTTP errors) and name the department. |
| CPAO | draft | Capture the Profile page and confirm whether it states "central government pensioners". |
| DoPPW | held | Open https://www.doppw.gov.in/ in a normal browser (HTTP 403 to the capture tool). |
| Gujarat DPPF | held | Open https://dppf.gujarat.gov.in/ in a normal browser (it showed a captcha). JanSetu never solves or bypasses captchas. |
| UIDAI (first batch) | held | Open https://uidai.gov.in/en/my-aadhaar (HTTP 500 twice). The earlier reviewed website record still stands. |
| India portal (first batch) | held | Open https://www.india.gov.in/ (HTTP 403). |
| Ministry of MSME (first batch) | held | Open https://msme.gov.in/ (HTTP 403). The PMEGP service links here. |

To publish a draft, the maintainer changes its `status` and `publicationDecision`, giving the reviewer's name and basis. The profile is then reviewed like any other.

## 3. Language review

A fluent reader confirms each quoted text and the English wording that depends on it.

| Language | Website | State | Evidence |
| --- | --- | --- | --- |
| Odia | Janasunani | published; 4 claims on hold | `js-purpose`, `js-dept`, `js-status`, `js-faq`, `js-contact` |
| Odia | Odisha State Scholarship | published | `os-odia` |
| Tamil | Tamil Nadu e-Sevai | published | `tn-home-services` |
| Malayalam | Sevana Pension | draft | `sp-search`, `sp-schemes` |
| Hindi | e-Kalyan | draft | `ek-dept`, `ek-reg`, `ek-faq` |

If a translation cannot be confirmed, the dependent claim is withheld. English evidence already supports Janasunani's operator and jurisdiction.

## 4. Links seen but not opened

Every link in a published profile has been loaded read-only at least once. Three links in drafts were only seen on a saved page: the Sevana Pension pensioner search, the CPWD Sewa complaint search and the e-Kalyan FAQ. Open them during the draft review above. If a page asks for sign-in, personal details, Aadhaar or an OTP, do not link to it.

## Suggested split

Use two reviewers. Both review profiles 1 to 9 (the operator revisions), so their judgements can be compared. Then reviewer A takes the 2026-10-17 batch and reviewer B the 2026-10-18 batch. Record the minutes spent on each profile; the release-gate thresholds depend on it.
