# JanSetu master roadmap: Phase 7 to post-launch

Started 18 October 2026, from the Phase 6D checkpoint. Live status is in [`data/project_execution_state.json`](../data/project_execution_state.json). The work record is in [JANSETU_EXECUTION_LOG.md](JANSETU_EXECUTION_LOG.md), and release blockers are in [JANSETU_RELEASE_BLOCKERS.md](JANSETU_RELEASE_BLOCKERS.md).

JanSetu stays an independent, Android-first guide. It never implies government endorsement, access to government records, or a transaction integration that does not exist. Browsing and the local document vault never require an account.

## Baseline (re-measured 18 October 2026)

| Item | Value |
| --- | --- |
| Websites | 7,347 (catalogue version 8) |
| Published services | 52 (service catalogue version 13) |
| Website profiles | 25 published (automated source checks), 8 drafts, 5 held |
| Human editorial decisions | 0 |
| App | `in.jansetu.app` 0.4.1 (code 5); QA build `in.jansetu.app.qa`; min API 26, target and compile API 36 |
| Toolchain | AGP 8.13.2, Kotlin 2.3.21, Gradle 8.13, JDK 17.0.16, Python 3.11, Node 22 |
| Last gate (`phase6d-final`) | Python 135, extension 13, JVM 54 and API 37 instrumented 50 tests, all passed; lint 0 errors, 14 warnings |
| Backend | None. Everything is on the device; catalogue updates are signed files imported by hand. |
| Version control | The folder is not a git repository. Changes are recorded in the execution log and by data hashes. |

## Phase plan

Each phase passes only when its exit gate is met and the evidence is recorded in the state file. Where a gate needs people, permissions or credentials, the independent engineering work is finished first and the phase is marked `BLOCKED` or `NEEDS_REVIEW` with the exact reason.

| Phase | Scope | Main deliverables | Exit gate |
| --- | --- | --- | --- |
| 7 | Secure local document vault | **My Documents:** import PDF, JPEG and PNG through the Storage Access Framework. Files are encrypted in segments with a per-file key wrapped by a dedicated Keystore key; metadata is encrypted too. Storage uses random IDs, sits outside backup and transfer, and never deletes a file on key loss. **Viewing:** search, filter, rename, recategorise, an in-app viewer with no plaintext temporary files, and delete. **Sharing:** opt-in, time-limited, read-only appearance in the Android file picker, plus "Export a copy". **Prepare documents:** from a service page, showing only the requirements already on record. | Full lifecycle tested on the device; data survives restarts; security and UI tests pass; privacy policy and threat model updated. |
| 8 | Accounts and backend architecture | A decision record and threat model. The current product needs no account: browsing, vault and tracker are local. Any account is justified only by opt-in sync (Phase 9). Build only a provider-neutral boundary that reports "unavailable" honestly; no live provider without owner credentials and a data-residency decision. | The decision is recorded; the account boundary is tested to never report false success; local-only behaviour is unchanged. |
| 9 | Optional end-to-end encrypted sync and recovery | A design analysis covering vault-key wrapping, recovery, device enrolment and revocation, conflicts, deletion and metadata minimisation. Implement only if a tested design and a backend exist; otherwise keep local-only storage. | Either a tested opt-in sync, or a documented decision to stay local-only, with nothing half-built shown to users. |
| 10 | Preparation and tracker integration | Link My Applications records to service preparation and chosen documents; keep service snapshots safe when catalogue data changes; let users export and delete their own data. Statuses stay user-maintained. | The service, preparation, document choice and tracking flow is tested end to end with no invented requirements and no tracker corruption. |
| 11 | Catalogue quality and discovery | Continue the Phase 6B–6D evidence workflow: services' linked websites, state portals, citizen services; fresh link checks; measured coverage; a translation workflow; a clean-release catalogue design if permissions fail. | A reproducible pipeline, accurate coverage figures and a backlog with no unsupported claims. |
| 12 | Website readability and JanSetu Assist | Fix the National Portal text scaling; keep scroll position through rotation and font changes; improve the RTI guidance; research the TN e-Sevai table; withdraw the NSP Readable text pilot from the production-bound extension. | Every released change has page-specific evidence, a reviewer decision, tests and rollback. |
| 13 | Accessibility, usability and beta preparation | Automated and scripted checks across TalkBack semantics, font scale, narrow and landscape screens, themes, Hindi, offline use and failures. A beta protocol with consent and minimal data collection. Real participants only after the distribution and privacy gates are cleared. | Critical journeys pass documented checks; remaining defects are triaged. Human checks are recorded only when people did them. |
| 14 | Security, privacy, legal and infrastructure | An end-to-end security review: exported components, URI grants, Keystore, parsing, browser policy, logging, backup, dependencies, artifacts. Align privacy policy, data safety and retention. Legal evidence register. | No unresolved critical issues; every legal and permission prerequisite documented and satisfied, or recorded as blocking. |
| 15 | Release engineering | A release candidate from current source. Owner-supplied signing; version bump; APK and AAB inspection for restricted material; checksums; store metadata; rollback plan. A release-blocked report if gates remain. | All automated checks and human gates pass. Publication stays an action the owner controls. |
| 16 | Post-launch operations | Report-a-problem triage, priority link checks, expiring evidence, staged updates with rollback, privacy-respecting crash and health monitoring, dependency updates, kill-switch criteria. | The team can detect, triage and fix defects and update the catalogue safely. |

## Ordering and dependencies

- Phases 7 and 10 deliver user value with no external dependency and come first.
- Phases 8 and 9 depend on owner decisions: hosting, cost, data residency under India's DPDP Act, and provider credentials. Their documentation and boundaries proceed regardless.
- Phase 11 can proceed technically, but its release value depends on the NIC/IGOD permission and the official-text reuse decision.
- Phase 12 follows a stable vault and catalogue.
- Phases 13 to 15 aggregate everything; their human and legal gates are listed in the release blocker register.

## Definition of done

The project is **engineering-complete for production review** only when every exit gate above is satisfied. Publication additionally needs explicit owner authorisation. If engineering is ready but external gates remain, the status is **ENGINEERING READY — RELEASE BLOCKED**, with the blockers named.
