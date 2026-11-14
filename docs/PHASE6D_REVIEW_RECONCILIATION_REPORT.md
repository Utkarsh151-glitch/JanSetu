# Phase 6D review reconciliation report

18 October 2026. Phase 6D covers review reconciliation, staged corrections and readiness for human review.

**AI-assisted review is not human approval.** No person has reviewed any profile. `data/website_enrichment/reviews.json` contains 0 decisions. Every published profile is labelled in the app as checked by an automated agent. The catalogue remains blocked from public release.

By project decision, human review is deferred while development continues. This phase therefore prepares the review rather than performing it. It:

1. checks the AI pre-review against the current data
2. stages each recommendation as a proposal with an audit trail
3. corrects only what the evidence supports
4. gives real reviewers a current, versioned, risk-ordered packet and queue

Related documents:

- [staged corrections](PHASE6D_STAGED_CORRECTIONS.md)
- [human review queue](PHASE6D_HUMAN_REVIEW_QUEUE.md)
- [manual checklist](PHASE6D_MANUAL_CHECKLIST.md)

## 1. Starting state

| Item | State |
| --- | --- |
| Website profiles | 38: 25 published, 8 drafts, 5 held |
| Evidence | 193 pins across 72 saved snapshots, all matching |
| Human decisions | 0 |
| AI pre-review | `ai-prereview-2026-10-18.json`, sha256 `b1a40518…`. It holds 157 claim recommendations over the 25 published profiles: 121 approve candidates, 14 revise, 22 hold. It was applied as advice in the Phase 6C addendum, with 14 revisions and 3 withdrawn links. |
| Packets | 17 October and 18 October batch packets, generated after the advisory revisions |

## 2. Validating the AI recommendations

`tools/review_reconciliation.py` checks every recommendation against `claims.json` and `evidence_index.json`. It fails on any problem.

| Check | What it catches | Result on the real file |
| --- | --- | --- |
| Malformed entries | Profiles missing their ID, claim list or a valid source digest; claims missing their key, a known recommendation, a reason or an evidence list | 0 |
| Unknown website | A `websiteId` not in `claims.json` | 0 |
| Unknown claim key | A claim key the profile does not have, unless a recorded revision explains its removal | 0. The 4 withdrawn claims are explained by their revisions. |
| Unknown evidence | An evidence ID that is not in the profile | 0 |
| Unpinned evidence | Evidence with no pin in the evidence index | 0 |
| Stale and unexplained | The profile changed after the advice, but no `revisionHistory` entry names the digest the AI saw | 0. All 15 changed profiles have revision entries. |
| Decision-bearing entries | A filled-in `formalDecision` or `humanSourceOpenedAttestation`; in all 157 AI entries the first is null and the second false | 0 |
| Duplicates | The same claim recommended twice | 0 |
| Unsupported wording | Names, places or numbers in the proposed or applied wording that the cited quotes lack | 2 AI proposals: NSP operator (the scholarship sentence) and ECI jurisdiction ("UT"). Neither is in the app now: the NSP sentence was removed, and ECI jurisdiction is withheld. |
| Broadening wording | Words such as "all", "only", "full" or "every" that the sources lack | 2 AI proposals: "only" for the DigiLocker audience, "full" for ECI jurisdiction. Neither was applied. |

### One real defect found

The wording check found that the National Scholarship Portal operator revision of 18 October had added "Each scholarship scheme belongs to its own ministry or body." The cited evidence (the site header) does not say this. The sentence was removed with a second `revisionHistory` entry that keeps the first one and records the previous digest. This is the only content change Phase 6D made to `claims.json`.

### Paraphrase noted for review

The wording check also lists, for information, words that do not block. DigiLocker's purpose and audience say "authentic digital documents"; the quote says "authentic documents". This was left for a reviewer to decide rather than changed by the agent.

## 3. Staging

Each of the 157 recommendations is now a staged record in `staging-2026-10-18.json`. Each record holds:

- the claim the AI saw and the current claim
- the AI's proposed wording, with evidence IDs and reason
- confidence and unresolved concerns
- whether a fresh source visit is needed
- the human action required

The file carries no reviewer or attestation and is refused by the importer.

| Staged action | Count |
| --- | --- |
| APPROVE_CANDIDATE | 135 |
| REVISE | 14 |
| HOLD | 7 |
| NEEDS_MORE_EVIDENCE | 1 |

| Risk (review order) | Claims | Of which not approve candidates |
| --- | --- | --- |
| 1. Department or operator | 24 | 9 revise |
| 2. Jurisdiction or audience | 40 | 3 revise, 1 needs more evidence |
| 3. Purpose | 25 | 1 hold (Odia) |
| 4. Task link | 11 | 1 hold (withdrawn: Aadhaar and OTP) |
| 5. Language or translation | 13 | 2 revise, 3 hold (Odia) |
| 6. Help or contact link | 44 | 2 hold (withdrawn links) |

Confidence: 82 high, 66 medium, 9 low. Nine claims need a fresh source visit.

All 22 AI holds are accounted for:

- 10 resolved by the read-only link check
- 4 resolved by new evidence
- 4 kept for an Odia reader
- 3 withdrawn from the app
- 1 turned into a revision

None was silently dropped.

## 4. Reviewer packet and queue

- `python tools/editorial_review.py packet --all` writes one packet for all 25 published profiles: `review-packet-all-2026-10-18-va8299123171f.html`. Profiles and claims appear in risk order, after a "Start here: highest-risk claims" list. For each claim, the packet shows:
  - its current publication state
  - the original automated claim
  - the staged action, outcome and confidence
  - what the reviewer must check
  - the AI advice and the sources
- The packet version is a hash of every profile's content plus the staging file. Packet generation refuses staging that no longer matches a profile. The importer refuses decisions made on stale content.
- `review-queue.json` and the [queue document](PHASE6D_HUMAN_REVIEW_QUEUE.md) list:
  - the 157 claims in order
  - a task for each of the 8 drafts and 5 held websites
  - language reviews for Odia, Tamil, Malayalam and Hindi
  - the 3 draft links that were seen but not opened

## 5. Safeguards

- **Importer.** It refuses:
  - AI recommendation, staging and queue files
  - reviewer names that look automated (Claude, GPT, Gemini, bot, script, AI…) or like placeholders (test, smoke, demo, dummy, sample…)
  - missing attestations, future dates and dates before the evidence was checked
  - stale profile digests
  - partial decisions

  It appends to earlier decisions and never rewrites them.
- **claims.json.** It cannot hold `human_reviewed` or a reviewer name; the validator refuses both.
- **Revision history.** Every content change needs a `revisionHistory` entry with the previous digest. No evidence quote or ID was edited, and no history entry was rewritten.
- **Browser safety.** `BrowserPolicy`, the allowlists and the application-journey rules are unchanged. The Odisha Aadhaar and OTP status link was withdrawn rather than allowed.
- **Inaccessible pages.** HTTP 403 and 500 pages and the Gujarat captcha stay held for a person to check in a normal browser. Nothing was retried around them.
- **Release exclusion.** The runtime catalogue keeps only an allowlist of profile keys. Its evidence carries an ID, URL, date and claim, with no quote. The new tests check the generated runtime catalogue, both built APKs and the extension folder for:
  - sentence-length quotes
  - AI advice, staging and revision fields
  - research, claims, index and packet files

  Short link labels such as "Contact Us" and ministry names do appear, as they did before.
- **Protected data.** The 8 protected catalogue and source files match the Phase 4 baseline. Website and service identifiers are unchanged.

## 6. Tests added

`tools/test_review_reconciliation.py` has 17 tests:

- unknown website IDs and claim keys
- missing, stale and unpinned evidence
- unsupported and broadening wording
- duplicate, malformed and decision-bearing recommendations
- the staging audit trail and hold accounting
- risk ordering
- applied revisions free of unsupported terms
- draft, held and language tasks
- staging never reaching the app
- refusal of AI and placeholder identities and of AI, staging and queue files
- preservation of earlier decisions
- the versioned packet matching current content
- refusal of stale staging
- exclusion of quotes and internal fields from the runtime catalogue, APKs and extension
- preservation of identifiers and protected files

`test_editorial_review.py` now checks that a profile's first revision names the digest the AI reviewed, so a second revision (NSP) can follow it.

## 7. Gate

The gate command `python tools/run_phase5_quality.py phase6d-final` passed. Results are in [result.json](../data/validation_reports/phase5/phase6d-final/result.json). Device tests ran only in `in.jansetu.app.qa`.

| Check | Result |
| --- | --- |
| Python (`unittest discover -s tools`) | **135 passed**: 118 earlier + 17 in `test_review_reconciliation.py` |
| Extension (node) | 13 passed |
| JVM (`:app:testDebugUnitTest`) | 54 passed |
| API 37 instrumented (`:app:connectedQaAndroidTest`) | 50 passed |
| Lint | 0 errors, 14 warnings (unchanged) |
| Debug APK | sha256 `1ecab4dcb7b72179…` |
| QA APK | sha256 `8876b14f1e0ed659…` |
| Protected files | All 8 match the Phase 4 baseline |
| Preview profile (`in.jansetu.app`) | Identical before and after. Tracker DB `d3591936…`, preferences `5ff177e1…`, alarm registry `cb5a631a…`, no imported catalogue update. Not cleared, uninstalled or reseeded. |
| `review_reconciliation.py --check` | 157 recommendations, 0 problems |
| `editorial_review.py check` | 193 pins across 72 snapshots match; 0 decisions |

Two small changes came after the gate's Python stage:

- the held-site rule text in `review-queue.json`
- an extra importer assertion for the queue file

The full Python suite was run again afterwards, against the APKs this gate built: 135 passed.

The host had about 1 GB of free memory during the device tests, but the emulator did not hang. `app-release-unsigned.apk` in the build outputs is from 16 October, before website profiles existed. The exclusion test scans it too, but it is not a current release build.

## 8. Readiness

### Ready for continued development

- The search, filters and website pages showing the 25 source-checked profiles, clearly labelled as automated checks.
- The review tooling: packet, import, status, reconciliation and staging. Real reviewers can use it as soon as they are available.
- Further enrichment batches, using the same capture, evidence-pin and revision rules.

### Needs beta testing, with testers told the information is automated

- Whether citizens understand "checked by an automated agent" and "Information under review", and still trust the official links.
- Ranking with the checked-information boost, especially for grievance, scholarship and pension searches.
- TalkBack, larger text and Hindi mode on the website pages, on physical devices and Android 8 to 14.

### Must be done before public release

1. Real, named reviewers review the 25 published profiles and their decisions are imported, under release-gate thresholds that have been agreed ([PHASE6C_RELEASE_GATE_PROPOSAL.md](PHASE6C_RELEASE_GATE_PROPOSAL.md)).
2. Fluent readers confirm the Odia, Tamil, Malayalam and Hindi evidence.
3. NIC permission for reusing IGOD-derived data is documented, and `python tools/check_catalog.py --public-release` passes.
4. A decision on reusing official-page text is documented.
5. A person confirms that the final store bundle excludes all research, quotes, AI advice, staging and packets.
6. The open Phase 5 and 6 release gates are completed: spoken TalkBack, physical devices, independent Hindi review and brand legal review.

Until items 1 to 4 are done, the catalogue is **not ready for public release**.

## 9. Not done

- Any human review. It needs real people, and no reviewer name, date or attestation was added.
- Fresh visits to the 9 claims that need them, and to the held or inaccessible sites. These need a person using a normal browser.
- Directory-wide enrichment. 27 of 7,347 websites have a checked purpose.
