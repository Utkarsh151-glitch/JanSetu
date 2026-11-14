# JanSetu release blockers

Each blocker stays open until documented evidence exists. Engineering work never closes a legal, permission or human-review blocker by itself. The live status is also in [`data/project_execution_state.json`](../data/project_execution_state.json).

| ID | Blocker | Type | Owner | Evidence needed to close | Status |
| --- | --- | --- | --- | --- | --- |
| RB-01 | NIC/IGOD permission to reuse directory data (7,338 directory-listed records) | Legal / permission | Project owner | A written permission or licence on file; `python tools/check_catalog.py --public-release` passes | Open |
| RB-02 | Decision on reusing official-page text (quotations, summaries, guides) | Legal | Project owner | A documented decision. Until then, internal evidence stays out of artifacts (enforced by tests). | Open |
| RB-03 | Human editorial review of the 25 published website profiles | Human review | Named reviewers | Decisions imported into `reviews.json` through the importer, under agreed release-gate thresholds. Currently 0. | Open |
| RB-04 | Language review: Odia, Tamil, Malayalam and Hindi evidence; independent review of the Hindi UI and Hindi privacy policy | Human review | Fluent reviewers | Recorded reviewer decisions | Open |
| RB-05 | Production app signing key, created and held by the owner | Credential | Project owner | Signed release built from environment variables; key never in the repository | Open |
| RB-06 | Production catalogue-update signing key custody, pin rotation and revocation policy | Credential / policy | Project owner | A documented custody and rotation policy and a production key. The current key is for development only. | Open |
| RB-07 | Privacy policy at a stable public URL; Play Data safety and other declarations | Distribution | Project owner | Published URL; completed Play Console forms matching the app's actual behaviour | Open |
| RB-08 | Brand and artwork trademark search and legal review | Legal | Project owner | Review record | Open |
| RB-09 | Human accessibility checks: spoken TalkBack, Switch Access, physical keyboard; physical devices on Android 8–14 | Human / device | QA testers | Recorded manual results | Open |
| RB-10 | Reminder delivery and backup exclusion checked on physical devices; document vault checked on physical devices | Human / device | QA testers | Recorded manual results | Open |
| RB-11 | Fresh link and evidence checks of every published destination shortly before release | Engineering + human | Editors | Dated link-check report within the agreed freshness window | Open |
| RB-12 | Explicit owner authorisation for public publication | Owner decision | Project owner | Written authorisation | Open |
| RB-13 | Account and backend decision (hosting, cost, data residency under the DPDP Act, provider credentials). This blocks only optional account and sync features; the local product does not depend on it. | Owner decision | Project owner | A recorded decision (see the Phase 8 decision record) | Open; not blocking the local-only release |
| RB-14 | Withdraw the NSP Readable text pilot from the production-bound extension | Engineering + owner approval | Engineering / owner | **Resolved (Phase 17, 18 October 2026):** `scripting`, `readability.css`, the background listener and the panel controls are removed; the protected hashes were rebaselined under owner authorisation ([record](PHASE17_AUTHORISED_CORRECTIONS.md)) | **Closed** |
| RB-15 | Owner approval to change hash-protected files for evidence-backed corrections: the RTI guideline rules in the service catalogue (Phase 11 and 12) and the RB-14 extension clean-up | Owner decision | Project owner | **Resolved for these two items (Phase 17):** owner authorisation recorded; rebaseline record `data/validation_reports/phase17-protected-rebaseline.json`. Any other protected-file change needs new approval. | **Closed** |

## Closed blockers

- RB-14 and RB-15 (Phase 17, 18 October 2026). The other release blockers remain open.
