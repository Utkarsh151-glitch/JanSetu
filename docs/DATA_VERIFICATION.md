# Catalog review and publication

The Android app and optional browser extension use the same bundled `catalog.json`. It retains nine manually reviewed portals and adds official-directory listings under an explicit **local development** policy. Counts are in [CATALOG_COVERAGE.md](CATALOG_COVERAGE.md). An HTTPS response alone does not establish government ownership. [Source registry](../data/source_registry.json), [taxonomy](../data/taxonomy.json), [classification rules](../data/discovery_classification.json), [portal profiles](../data/portal_profiles.json) and [page audits](../data/page_audits.json) preserve evidence and uncertainty.

## Live collection and local evidence policy

`discover_government_sites.py` enumerates actual public IGOD links and the offset/limit routes exposed by its page scripts. It saves snapshots, timestamps, hashes, result totals, pagination and failures in a resumable manifest. The source-provided page sizes are preserved. Scoped state/category batches do not alter the national batch. Use a new dated batch for fresh acquisition; rerunning a batch resumes its cached work.

`prepare_discovery_catalog.py` normalizes organization names/URLs, retains original values, joins repeated source organization IDs, flags uncertain matches, and keeps organizations, website hosts and landing pages in separate stores. Individual services are not inferred from websites. Classification rules are editable data and have confidence separate from source authority. IGOD state codes are navigation codes; LGD identifiers have not been guessed.

Destination checking uses normal HTTPS certificate validation, explicit same-host/www redirects, robots checks, rate limits and bounded retries. Eight workers may check distinct hosts, with one check at a time per host. HTTP source URLs are held for URL review. Unexpected redirects, default server pages, challenges, failed/inconclusive checks and external operators stay in review. Response bodies are processed in memory and discarded; cached health evidence includes status, redirects, title, size and hash. No credentials, OTPs or filled forms are collected.

For local directory admission, a record must have complete attributable IGOD linkage, retained successful source snapshots with matching hashes, a responding HTTPS institutional destination with resolved/permitted robots checks, and no unresolved URL/ownership/rejection decision. Institutional suffixes provide an additional restriction, not proof by themselves. `stage-discovery` emits evidence-policy approvals; `publish` revalidates them and creates `verificationStatus: directory_listed`, an empty manual `verifiedOn`, actual source/health dates and `publicationScope: local_development`. The app and extension show **Listed by IGOD**. The original nine `verified` records are preserved exactly.

NIC's [copyright policy](https://igod.gov.in/website_policies) requires permission to reproduce material. The user authorized building locally first and obtaining permission before public distribution. This is not recorded as public reuse permission. `python tools/check_catalog.py --public-release` deliberately rejects the current local-only data. Release builds remain development artifacts.

```powershell
python tools/discover_government_sites.py --source igod --dry-run
python tools/discover_government_sites.py --source igod --delay 1
python tools/prepare_discovery_catalog.py --check --workers 8 --timeout 10
python tools/catalog_pipeline.py stage-discovery data/collection/igod-2026-10-14/normalized_candidates.json
python tools/catalog_pipeline.py publish data/collection/igod-2026-10-14/local_approvals.json
python tools/catalog_pipeline.py export data/collection/igod-2026-10-14/review_export.csv
python tools/discovery_report.py
```

Staging and publication reruns are idempotent. New evidence may refresh local directory metadata while preserving its history; curated records are not rewritten. Rejections and explicit ownership/URL questions require an editor. Inconclusive fresh updates retain the last dated local entry for review rather than declaring the portal fraudulent or permanently retired. Before/after catalog diffs and duplicate groups are exported with the batch.

## Editor workflow

1. Identify a portal from an authoritative directory or its official page. Check ownership, the final HTTPS destination, permitted reuse, and the factual description manually. Do not enter login credentials or bypass access controls.
2. Save candidates as a JSON array or CSV file. Required fields are `id`, `title`, `description`, `url`, `organization`, `level`, `categories`, `keywords`, `sourceUrl`, and `sourceType`. In CSV, separate categories and keywords with `|`. Use IDs and category names already present in the catalog.
3. Run `python tools/catalog_pipeline.py import path/to/candidates.json`. New rows enter `data/review_queue.json` with `DISCOVERED`, `NEEDS_OWNERSHIP_REVIEW`, `NEEDS_URL_REVIEW` or an explicitly recorded `TEMPORARILY_UNREACHABLE` status. `VERIFIED` and `REJECTED` are also supported review statuses. Reimporting a retained candidate does not duplicate it. Invalid or duplicate new rows retain a reason; the app never reads this queue. The small attributed batch at `data/discovery_batch_2026-10-14.json` shows state, UT, district and municipal examples.
4. Investigate each queued row. Map its research sector to an approved production catalog category before publication. Record approvals in a local JSON object keyed by ID, with `approvedBy`, `ownershipEvidence`, and `verifiedOn` (YYYY-MM-DD). The reviewer must verify the evidence, canonical landing URL and operating organization; the script can only check that an HTTPS evidence URL was recorded. A `REJECTED` candidate stays unpublished.
5. Run `python tools/catalog_pipeline.py publish path/to/approvals.json`. Only explicitly approved, structurally valid, nonduplicate rows are published. Published records retain the reviewer, ownership evidence and review-history event. Keep the approval file in the editorial record; do not include personal details or secrets.
6. Run `python tools/check_catalog.py`, `python tools/inventory_audit.py`, `python tools/audit_links.py`, `python tools/catalog_pipeline.py report`, `python tools/package_extension.py`, the Python unit tests, and the Android tests. Review the generated link and coverage reports before a release.

The pipeline also accepts editor-provided CSV/JSON files. The link audit follows only safe redirects and can reuse explicitly identified actual discovery checks with their original timestamps, avoiding thousands of duplicate requests. It never changes publication status. No scheduled refresh or remote update channel exists. The previous six-page UX screening and ten editorial page assessments remain separate from the bulk inventory; new directory pages have not received an accessibility audit. The retired `services.india.gov.in` directory is not an active import source.

The current source statements are recorded in [DATA_SOURCES.md](DATA_SOURCES.md). Guide steps for DigiLocker use [DigiLocker's FAQ](https://wb.digilocker.gov.in/web/about/faq); CPGRAMS steps use its [grievance page](https://pgportal.gov.in/Home/LodgeGrievance). Both were reviewed on 13 October 2026. No fees, eligibility, or deadlines are displayed.

Discovery staging retains www and bare-host variants as distinct source URLs. Observed safe redirect aliases are exported separately. The legacy bundled-catalog uniqueness rule may hold a conflicting variant in URL review; it is not silently dropped or declared equivalent.

## Delivering approved local changes

After publication and validation, `python tools/package_catalog.py update --version NEXT_VERSION --output artifacts/catalog-NEXT_VERSION.jansetu` signs a compact local Android update with the pinned development key. Replace `NEXT_VERSION` with a monotonically increasing integer above the installed catalog. Settings → Offline catalog imports and verifies that file without another APK installation; restore returns to the bundle. The full editorial asset and research evidence remain retained. Signatures attest to the editor, not independent government verification. See [local operation](DEPLOYMENT.md) for key handling and recovery. There is no automatic fetch or scheduled update service.

## Phase 2B review policy

`tools/review_queue.py` classifies all retained candidates using cached source/destination evidence. Safe aliases require an explicit same-host HTTPS redirect chain, matching organization evidence, a single bundled target and no login convergence. Exact www/bare differences are never guessed equivalent. Publication checks now require the actual source URL/name to occur in the retained HTML, in addition to its retrieval metadata/hash. Source-page fragment anchors share one HTTP response. Ownership, conflicting jurisdictions and URL review decisions remain separate from health observations.

Resolved aliases are archived with original evidence/history and remembered during import. Changed alias evidence reopens URL review. Newly unavailable checks can be stored as `latestHealthStatus`/`latestHealthCheckedAt` without erasing the prior admission response; Android and Chrome display the newer uncertainty. Source disappearance, changed names and changed organization URL sets generate review-only diffs; no automatic deletion or URL replacement occurs. A response older than 30 days should be rechecked before a new editorial admission; existing dated evidence is not silently relabelled as fresh. Public distribution remains blocked pending NIC permission.
