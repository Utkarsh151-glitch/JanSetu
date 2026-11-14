# Government website inventory - Phase 2A

The national collection is implemented and has run against the real public IGOD directory. Exact current numbers, all 36 state/UT rows, sector coverage and every category/pagination reconciliation are in [DISCOVERY_RUN_REPORT.md](DISCOVERY_RUN_REPORT.md). The earlier small inventory and its page-level evidence are retained in [INITIAL_AUDIT](GOVERNMENT_WEBSITE_INVENTORY_INITIAL_AUDIT.md); its nine-record counts describe that earlier checkpoint.


## Phase 2A checkpoint counts

| Measure | Count |
| --- | ---: |
| Raw directory rows | 23169 |
| Distinct source organization identities; uncertain matches retained | 20946 |
| Unique normalized source URLs | 11168 |
| Source organizations without valid URLs | 2356 |
| Bundled website/landing entries | 7270 |
| Original reviewed entries / local IGOD additions | 9 / 7261 |
| Bundled unique hosts | 7040 |
| Unpublished candidates | 3902 |
| Individual services | 0 |
| Source-backed guides | 2 |
| States/UTs with a local entry | 36 of 36 |
| Source pages processed / pending / failed | 3586 / 0 / 0 |
| Listings with totals reconciled | 461 of 461 |

The additional New Additions view supplied 15 visible rows without a total/history. All 20 primary sectors have at least one local entry. District and municipal classification currently covers 688 and 74 landing records; these are not administrative completeness claims. Per-state/district and sector counts are in the linked run report. These additions remain local-only; this is not a publicly released production inventory.

## Scope and evidence

IGOD navigation exposed Union bodies, state/UT organizations, districts, legislature, judiciary, international missions and sector listings. The collector follows actual links and the pagination loaded by normal public scrolling; it does not guess portal URLs or hidden APIs. Directory snapshots, retrieval dates, exact offsets, failures and code versions are recorded in the [collection manifest](../data/collection/igod-2026-10-14/manifest.json). Expected category totals are reconciled against extracted rows. The New Additions view exposes no total or history, so only the visible rows are claimed.

A raw listing row, source organization identity, website host, landing URL and individual service are different counts. Repeated sector/category memberships are deduplicated by actual URLs and source organization identifiers. Uncertain same-name matches and observed redirect equivalents are retained for review. Missing URLs remain in the organization store. No individual services, fees, eligibility rules or administrative identifiers were invented.

## Local catalog and publication policy

The original nine manually reviewed records are preserved. New entries enter the Android and Chrome catalog through the existing validation pipeline only when supported by a successful source snapshot, an institutional HTTPS destination, resolved permissive robots rules and a responding destination check. They display **Listed by IGOD**, with separate source and health dates. This is directory evidence, not independent ownership or transaction verification. External operators, unexpected redirects and inconclusive checks remain in the [review queue](../data/review_queue.json).

The user requested local development before seeking NIC reproduction permission. All directory-derived additions therefore have `publicationScope: local_development`; `python tools/check_catalog.py --public-release` blocks public distribution. The original nine records remain the reviewed baseline. The [catalog coverage](CATALOG_COVERAGE.md) reports bundled and pending counts separately.

Adding a directory entry grants neither WebView access nor extension CSS injection. Native search supports available state/UT, district, government-level and sector filters, alias matching and lazy result rendering. Chrome renders bounded batches. The existing two guides and page-level [UX audit](PORTAL_UX_AUDIT.md) remain intact.

## Secondary sources and coverage limits

| Source | Actual finding | Remaining action |
| --- | --- | --- |
| IGOD | Public navigation, listing HTML and source-exposed pagination collected under normal TLS validation; category counts reconciled. | NIC reproduction permission before public distribution; future dated batches for changes. |
| National Portal web directory | Same IGOD-derived data. Curl returned HTML containing Loading/zero fields; a prior Chrome check returned 403. No independent inventory counted. | Establish a permitted complete export if it adds coverage. |
| data.gov.in | Redirects to www; robots.txt disallows automated retrieval. Content collection stopped. | Obtain permitted official dataset/API access. |
| Local Government Directory | Public administrative totals observed; state-code view timed out and district-code view showed no data table. | Obtain permitted administrative IDs and reconcile districts/local bodies. Counts do not prove websites exist. |
| Independently linked government portals | Existing manual evidence preserved; acquired destinations checked separately. | Resolve specific URL/operator review groups and source gaps. |

The [source registry](../data/source_registry.json) and [run report](DISCOVERY_RUN_REPORT.md) retain timestamps and retrieval evidence. Destination homepage/contact/form bodies are discarded; health evidence retains status, redirects, body hash/size and title. No login, filled form, CAPTCHA bypass or TLS exception was used.

## Maintenance

See [DATA_VERIFICATION.md](DATA_VERIFICATION.md) for source/state/category batch commands, safe reruns, candidate import, review export, local publication and validation. Same-batch runs resume cached work; new dated batches capture changes. Manual corrections and rejected/ownership/URL decisions survive discovery refresh. Inconclusive responses do not prove fraud, permanent breakage or retirement.

The objective is all accessible entries in the covered official source categories. This is not a claim to cover every government website or service in India. Actual test commands and remaining release gates are in [QA_REPORT.md](QA_REPORT.md) and [KNOWN_LIMITATIONS.md](KNOWN_LIMITATIONS.md).

## Phase 2B follow-up - 15 October 2026

The current local catalog contains 7,347 websites (nine original reviewed portals and 7,338 directory listings), 3,823 pending candidates and two separately archived redirect aliases. All 7,270 Phase 2A records are preserved unchanged. LGD state IDs and authoritative state-wise aggregate counts are now available; individual district/local-body website links remain unresolved. See [review decisions](REVIEW_QUEUE_REPORT.md) and [administrative reconciliation](ADMINISTRATIVE_COVERAGE_RECONCILIATION.md). The Phase 2A table above is retained as historical evidence.
