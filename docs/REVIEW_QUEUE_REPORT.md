# Review queue report - Phase 2B

Generated 2026-10-14T21:17:20+00:00; rules `phase2b-v1`. Run `python tools/review_queue.py --apply`, then the existing publication command with the generated local approvals, then rerun triage for final counts.

The entire original queue is retained in the Phase 2B baseline. Evidence is reused from Phase 2A: no new destination requests, no response date reset, no ownership approval inferred from a domain or HTTP response. Exact source URL/name membership and cached source hashes are checked for publication. Fragment anchors identify the same HTTP response; only anchors are ignored for source retrieval equality.

| Measure | Actual |
| --- | ---: |
| Original candidates examined across the phase | 3902 |
| Candidates examined this invocation | 3823 |
| Original candidates accounted for | 3902 |
| Automatically resolved across phase | 79 |
| Duplicate aliases confirmed and archived | 2 |
| Ownership cases resolved | 0 |
| Eligible for local publication now | 0 |
| Records actually newly bundled | 77 |
| Still pending | 3823 |
| Rejected with evidence | 0 |

## Current triage buckets

- INCONCLUSIVE: 2349
- LIKELY_DUPLICATE: 25
- OWNERSHIP_OPERATOR_REVIEW: 1174
- URL_REDIRECT_REVIEW: 275

## Deterministic rules and decisions

- Exact URLs match without assuming www/bare equivalence. A redirect alias requires the recorded same-host HTTPS chain, matching organization evidence, one unambiguous published target and no authentication convergence. Alias records/evidence/history are archived; no organization is deleted or merged.
- Similar names and the 29 existing suspected groups are suggestions only. Shared source organization names/IDs and multiple jurisdictions are recorded as conflicts, not silently flattened.
- Explicit ownership, URL and rejection decisions survive. Transient HTTP/TLS/access/robots problems stay inconclusive. Neither a 404 alone nor a default server page proves retirement or fraud. Zero automatic rejections and ownership certifications are introduced.
- READY_LOCAL_PUBLICATION passes the existing narrow institutional HTTPS, robots, source-content/hash and response gates. It remains Listed by IGOD and local development only. Source authority, health, ownership and publication eligibility are separate fields.
- Prioritisation exposes citizen impact, source authority, regional gaps, duplicate/redirect risk, safe discoverability and age. It is an editorial order, not a trust score.

## Reproducible artifacts

`data/collection/phase2b-2026-10-15/`: baseline queue/catalog, `triage_decisions.json` (current pending evidence/reasons/conflicts), `prioritized_review.csv`, `resolved_review.json`, `published_review.json` (original candidates, admission history and explicitly dated post-admission evidence re-evaluation), `local_approvals.json`, `review_summary.json`, `catalog_diff.json`, and `source_changes.json`. Each retained candidate has triage history; reruns with unchanged evidence do not duplicate decisions.

Source additions, changed organization URL sets and disappeared listings are recorded separately. They never cause silent deletion or URL replacement. No fresh source acquisition was performed in Phase 2B, so no present-day disappearance is inferred from cached data.

Baseline website preservation: 0 removed and 0 changed. New directory entries: 77. All nine manually curated records must remain byte-equivalent as JSON objects.

## Remaining work

Resolve explicit operator/canonical URL questions using official evidence, obtain permitted LGD unit exports, and review names/renames before linking administrative IDs. NIC permission and a public evidence policy remain required before distribution. No extension allowlist or UI redesign is introduced.
