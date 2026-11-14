# Phase 11: catalogue quality and discovery (status and backlog)

18 October 2026. No new enrichment batch was captured in this run. The pipeline from Phases 6B–6D is in place and was re-measured. Human review is still the bottleneck: adding more automated profiles would grow a queue that no person has started (0 decisions).

## Measured coverage (`tools/catalog_completeness.py`)

| Segment | Websites | Purpose checked | Source-checked profile | Human-reviewed profile |
| --- | --- | --- | --- | --- |
| All websites | 7,347 | 27 (0.37 %) | 25 (0.34 %) | 0 |
| Priority websites (service-linked, state portals, citizen-service, pension, scholarship and grievance portals) | 136 | 25 (18.4 %) | 24 (17.6 %) | 0 |
| Linked by published services | 14 | 13 (92.9 %) | 12 (85.7 %) | 0 |
| Published services, counted by their website | 52 | 51 (98.1 %) | 47 (90.4 %) | 0 |

Profiles: 25 published (automated source checks), 8 drafts, 5 held. Evidence: 193 pins across 72 saved snapshots, all matching. Reconciliation: 157 AI recommendations staged, 0 problems.

## What the pipeline guarantees (already tested)

- **Evidence for every claim:** an exact quote, URL, check date and snapshot pin. Quotations never reach the app or the extension.
- **Labels:** reviewed facts and automated checks are labelled differently in the app ("Reviewed by a person" versus "checked by an automated agent").
- **Review workflow:** append-only human decisions; versioned packets; refusal of stale or AI-sourced imports.
- **Corrections:** staged with `revisionHistory` and the previous digest; withheld claims are removed from the app.
- **Translations:** quotes not in English are flagged, and the review queue lists them by language (Odia, Tamil, Malayalam, Hindi). Dependent claims are withheld if a fluent reader cannot confirm them.
- **Access:** no scraping around access controls. HTTP 403 and 500 pages and captchas are held for a person.
- **Freshness:** `tools/stale_evidence.py` lists published items older than a window; all are within 90 days today.

## Backlog (in priority order)

1. **Human review of the 25 published profiles** (RB-03), using `review-packet-all-2026-10-18-va8299123171f.html`. Language reviews follow (RB-04).
2. **Decide the 8 drafts and check the 5 held sites** in a normal browser; tasks are in `docs/PHASE6D_HUMAN_REVIEW_QUEUE.md`.
3. **RTI guidelines.** Capture the official page read-only and record the two missing rules as evidence-backed service facts. This changes the hash-protected service catalogue, so it needs owner approval, then the Phase 12 RTI card.
4. **TN e-Sevai fees and documents** for the Tamil Nadu services, through the editorial workflow (Phase 6A item 3a).
5. **Next enrichment batch:** the 111 priority websites not yet checked, starting with state portals and service-linked sites. Only start it once reviewers are available, so the queue does not grow unreviewed.
6. **Fresh link check before release** (RB-11): `stale_evidence.py` plus a read-only re-capture of every published destination.
7. **Clean-release catalogue:** if RB-01 or RB-02 is refused, use the Phase 15 reduced-scope options. Never relabel unreviewed data.

## Exit-gate status

- The pipeline is reproducible and its checks pass.
- Coverage is measured accurately.
- Reviewed and automated facts are distinguishable.
- The backlog is actionable and contains no unsupported service claims.

The phase is **NEEDS_REVIEW**. Its useful output depends on human reviewers (RB-03, RB-04) and on the permissions (RB-01, RB-02).
