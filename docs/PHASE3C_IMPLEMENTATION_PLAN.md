# Phase 3C plan and baseline audit

Baseline verified before edits: app 0.4.0/code 4; 7,347 websites; 25 services, catalog version 11; encrypted local tracker; four unresolved service cases; 3,823 website cases. Full service/queue snapshots and hashes are retained in `data/validation_reports/phase3c-baseline.json`. The authoritative website catalog, website queue, tracker database/key and original service IDs are protected.

## Audit findings

- 16 central and nine Tamil Nadu entries; no published district/local service jurisdiction. A portal's geographic presence is not service coverage.
- 14 unknown eligibility fields, 19 unknown document lists; known lists may be partial. Older AICTE guidelines are linked from current NSP but academic-year applicability remains a caveat.
- DigiLocker fetch/share and a state-specific driving-licence operation lack reviewed flows. PMEGP's previously attempted application host failed ordinary TLS. Aadhaar operations remain information-only pending route review.
- Department text exists separately from exact website-operator identities. No responsible-department identifier/independent ownership certification is inferred from the operator registry.
- Existing publication gates enforce fields, official host suffixes and evidence IDs, but lack collection-level semantic duplicate checks, explicit route observations and an idempotent way to enrich an existing pending stub while retaining its history.
- Generic certificate/pension queries currently surface Tamil Nadu records when no state is selected; an explicit other-state filter correctly removes them. Broader state coverage must come from actual service records, not renamed portal matches.

## Work sequence

1. Extend targeted public-source retrieval to retain HTTP/final-URL/normal-TLS failures and official links. No credential submission, certificate bypass or nationwide crawl.
2. Recheck the four existing cases. Record dated decisions/history, admitting only evidence-supported services/routes. Never promote a failed/JS-only response to proof of a transaction.
3. Investigate at least 50 additional concrete candidates across selected states/UTs and central priorities. Retain an explicit research ledger: investigated/discovered/source-reviewed/published are different measures. Publish the supportable subset using import → exact-digest source review → publish; preserve pending/previous decisions.
4. Add optional sourced beneficiary-group and route-evidence metadata; preserve old IDs/content unless an explicitly reviewed revision is necessary. Strengthen duplicate, route and provenance gates. Keep unconfirmed application URLs null.
5. Generate coverage/gaps/catalog reports from the actual data and ledger, including all 36 state/UT rows, central/district/local scope, categories, groups, modes and dates. Unknown facts and uninvestigated regions remain explicit.
6. Regression-test publication/idempotency/preservation, new query ranking and regional filters, action gating and signed catalog install/restore with saved tracker data. Package updated runtime/extension/signed local catalog, run applicable Python/Node/JVM/lint/debug/emulator checks, install the debug APK, and record actual outcomes.

No navigation/design-system overhaul, backend, live status API, cloud sync, website-backlog auto-resolution or public distribution is included.
