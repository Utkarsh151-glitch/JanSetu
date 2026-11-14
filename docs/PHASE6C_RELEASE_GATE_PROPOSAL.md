# Phase 6C: proposed catalogue release gate

18 October 2026. This is a proposal for the team to decide. It sets out what "ready for a public catalogue release" should mean, how to measure it, where things stand today, and what evidence the team needs before approving.

**Short answer: the catalogue is not ready.** Two permission blockers are unresolved, and no profile has been reviewed by a person.

## Why not one percentage

"Percentage of the 7,347 websites with checked information" (today 0.37%) is a poor release measure:

- **It is dominated by low-traffic records.** About 7,000 records are institutional, district and court sites that most citizens never reach through JanSetu.
- **It rewards easy records.** It would push effort towards sites that are simple to describe rather than useful.
- **It says nothing about whether checked information is correct.** That can only be measured by people reviewing claims.

The proposal therefore sets criteria for the groups citizens actually depend on. The rest of the directory may ship with honest "Not checked yet" labels and a documented backlog plan.

## Coverage today, by segment

Run `python tools/catalog_completeness.py --markdown` to reproduce this table. The output file is [phase6c-completeness.json](../data/validation_reports/phase6c-completeness.json).

| Measure | All websites | Priority websites | Linked by published services | Per published service |
|---|---:|---:|---:|---:|
| n | 7,347 | 136 | 14 | 52 |
| Purpose checked (any) | 27 (0.37%) | 25 (18.38%) | 13 (92.86%) | 51 (98.08%) |
| Reviewed record (earlier phases) | 9 (0.12%) | 7 (5.15%) | 7 (50.0%) | 17 (32.69%) |
| Source-checked profile (automated) | 25 (0.34%) | 24 (17.65%) | 12 (85.71%) | 47 (90.38%) |
| **Profile reviewed by a person** | **0** | **0** | **0** | **0** |
| Department/operator checked | 26 (0.35%) | 24 (17.65%) | 13 (92.86%) | 51 (98.08%) |
| Jurisdiction checked (profile) | 25 (0.34%) | 24 (17.65%) | 12 (85.71%) | 47 (90.38%) |
| Official help link checked | 22 (0.3%) | 21 (15.44%) | 11 (78.57%) | 45 (86.54%) |
| Draft profile (not published) | 8 | 8 | 0 | 0 |
| Held profile | 5 | 4 | 2 | 5 |
| Not checked yet | 7,320 (99.63%) | 111 (81.62%) | 1 (7.14%) | 1 (1.92%) |

How the segments are defined:

- **Priority websites** carry at least one of these markers:
  - a published or pending service links to them
  - IGOD lists them as a "State Portal"
  - their name or URL identifies an e-District, citizen-service, pension, scholarship or grievance portal
- **Linked by published services** means at least one published service record points to the website.
- **Per published service** counts each of the 52 services by the state of the website it sends people to. The one service without a checked website is PMEGP, which links to msme.gov.in (held: HTTP 403).

Within the 136 priority websites:

| Group | Websites | Status |
|---|---:|---|
| State/UT portals | 27 | 3 checked |
| Citizen-service portals | 100 | 14 published, 8 drafts, 2 held, 76 not checked |
| Pending-service websites | 5 | all published |
| Service-linked websites | 14 | 12 published, 2 held |

## Proposed criteria

### A. Release blockers (all must pass)

| # | Criterion | How it is measured | Status today |
|---|---|---|---|
| A1 | **NIC/IGOD data-reuse permission** is obtained and recorded | `python tools/check_catalog.py --public-release` passes; the permission letter is referenced in `data/source_registry.json` | **Not met** (pending) |
| A2 | **Reuse of official-page text** is decided and recorded. Until then no quotes, page text, excerpts, review packets or research snapshots ship. | The release build contains none of: `research/`, `claims.json` quotes, `evidence_index.json`, review packets. Packaging tests already assert this for the app catalogue; add the same check for the extension and any public repository export. | **Not met** (not checked) |
| A3 | Every website **linked by a published service** has a published profile with checked purpose, operator, jurisdiction and at least one official help or contact link, **approved by a named reviewer**. Otherwise the service page states that the website is not checked and why. | `editorial_review.py status` and the segment report ("Profile reviewed by a person" for the 14 websites) | 12 of 14 automated, **0 reviewed by a person**; MSME and UIDAI held |
| A4 | Every **flagged claim** on A3 websites is approved by a reviewer who confirmed they opened the source. Flags cover operator, audience, language, jurisdiction, link-only targets, other-host links and translations. | Import rules enforce the confirmation; count approved flagged claims in `reviews.json` | 0 approved (63 flagged in the first batch alone) |
| A5 | **Links work at release time.** Every published help and task link on A3 websites is re-captured, read-only, within 30 days before release; link-only targets are loaded at least once. | A fresh capture batch plus `website_enrichment.py validate` | 16 first-batch link targets were only seen as links |
| A6 | **Labels are honest.** Every record without a checked purpose shows "Not checked yet" or "Information under review", and no automated profile reads as human-reviewed. | Existing JVM and instrumented tests (Phase 6B/6C) | **Met** |
| A7 | Quality gate and data protection pass: Python, extension, JVM and API 37 suites; lint with no errors; protected files and preview hashes unchanged | `python tools/run_phase5_quality.py <run>` | Met in the latest gate (see QA report) |

### B. Accuracy of automated profiles (decided from real review data)

Automated `source_checked` profiles are shown today with a clear label. Whether that may continue in a public release should depend on how often reviewers find them wrong.

| # | Criterion | Recommendation and reasoning |
|---|---|---|
| B1 | Measure the **revision rate**: the share of first-batch claims that reviewers mark Revise or Hold, recorded per field. | Needs the first-batch human review (99 claims). This is the evidence the team lacks today. |
| B2 | Decide whether unreviewed automated profiles may stay visible in a public release. | **Suggested rule, for the team to confirm after B1:** keep showing automated profiles only if fewer than 1 in 20 first-batch claims needs revision, **and no operator or jurisdiction claim is wrong**. A wrong department or jurisdiction can send a citizen to the wrong office, so it should count more than a wording issue. If the rate is higher, show only human-reviewed profiles at release and tighten the drafting rules before more batches. |
| B3 | Recurring review. | Re-review a sample (suggest 1 in 5 profiles per batch) so B1 is tracked over time, not measured once. |

### C. Priority coverage (should; the team sets the scope)

| # | Criterion | Recommendation |
|---|---|---|
| C1 | **Every priority website is triaged**: it has a published profile, a draft with a recorded decision, or a recorded hold. Nothing in the 136 is left untouched. | This is a full, finite set rather than a percentage. Today 36 of 136 are triaged (24 published, 8 drafts, 4 held); 100 are untouched. At the measured pace of 20 sites per batch, about 5 more batches are needed. |
| C2 | All **27 state/UT portals** have a checked purpose and operator, and a reviewer has checked at least the operator wording. | The state portal is the fallback route for every state-level task. Today 3 of 27. |
| C3 | All **8 drafts** have a decision: publish after review, rewrite, or hold. | Drafts must not stay in limbo; each needs a person. |

### D. Backlog plan (documented, not completed)

| # | Item |
|---|---|
| D1 | The remaining directory (about 7,200 records) keeps "Not checked yet" labels and stays in `queue.json`, ordered by the published priority rule. |
| D2 | Throughput is measured: capture and drafting took one batch of 20 sites per session in this phase. Human review time per profile is **unknown until B1** and must be recorded before committing to dates. |
| D3 | Held sites (HTTP 403, captcha, server errors) are re-checked by a person in a normal browser. They are never worked around. |
| D4 | Evidence freshness: a profile is re-captured before each release if its evidence is older than an agreed interval. **Suggested: 180 days.** Portals change menus and contact pages often; HP ePASS still shows 2019 news. |

## Evidence the team needs before approving

1. The permission records (A1, A2).
2. `reviews.json` with the first-batch decisions from named reviewers, the measured revision rate, and time per profile (A3, A4, B1).
3. A fresh capture batch for the A3 websites within 30 days of the intended release (A5).
4. A passing quality gate on the release build, plus a check that the release artifacts contain no research material (A2, A7).
5. The triage counts for the 136 priority websites and the 27 state portals (C1, C2).

Until A1–A5 pass, JanSetu should stay in local development and keep its current "Not checked yet" and "automated check" labels.
