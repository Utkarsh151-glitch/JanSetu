"""Regenerate Phase 3A service coverage and measured search reports (no network)."""
from collections import Counter
from service_catalog import ROOT,DIRECTORY,read,write,merged_catalog

def main():
    catalog=merged_catalog();services=catalog['services'];queue=read(DIRECTORY/'review_queue.json')
    before=read(ROOT/'data/validation_reports/phase3a-search-before.json')
    after=read(ROOT/'data/validation_reports/phase3a-search-after.json')
    modes=Counter(s['applicationMode'] for s in services)
    lines=['# Service catalog report — Phase 3A','', 'Review date: 16 October 2026. Local development only. App 0.3.0/code 3; effective catalog version '+str(catalog['version'])+'.','',
        '## Baseline and preservation','',
        'The baseline was checked before edits: 7,347 websites, nine manually reviewed websites, 7,338 directory-listed entries, zero service records and 3,823 pending website cases. `data/validation_reports/phase3a-baseline.json` records the original website-object hashes and full catalog/queue hashes. The website asset and website review queue remain byte-for-byte unchanged. No new nationwide collection or wholesale URL recheck was run.','',
        '## Existing implementation and minimum changes','',
        'Existing strengths retained: deterministic cached website index, weighted all-term matching with spelling tolerance, regional/category filters, organization browsing, lazy lists, background query work, native details, signed atomic offline updates and a separately packaged Chrome companion.','',
        'The old Service model had eight fields and no published records or service-result UI. Natural-language filler blocked several baseline queries. Added a separate service/organization catalog, field-level evidence references, publication gates and explicit unknowns. CitizenSearch reuses CatalogSearch token/scoring rules and its existing website index; only the small service index is additional. Cards, result-type filter and a service route extend the existing screen. No backend, account, paid API or LLM was added.','',
        '## Actual pilot coverage','',
        f'- {len(services)} published service-information records, linked to eight existing website-operator identities.',
        f'- Application modes: '+', '.join(f'{k}: {v}' for k,v in sorted(modes.items()))+'.',
        f'- Unknown eligibility: {sum(s["eligibility"] is None for s in services)}. Unknown document lists: {sum(s["documents"] is None for s in services)}. Known lists can be partial and do not constitute eligibility decisions.',
        '- Nine state-specific entries cover Tamil Nadu delivery only. Other states do not acquire those services through fuzzy matching or a state filter. Birth-certificate printing is not new birth registration.',
        '- Four unresolved services/routes remain in the service queue; the existing 3,823 website cases are separate. Two DigiLocker operations need fresh readable documentation, the driving-licence flow needs state-specific review, and the PMEGP application host failed normal TLS verification.',
        '- Four Aadhaar services and PMEGP are information-only while application routes remain unconfirmed. No Apply button is shown for them or assisted entries. No government login, OTP, identity document or live application status is collected.',
        '- Source-reviewed means cited service information was inspected by Codex; this is not human certification, independent ownership review of every parent website, a completed transaction or a guarantee that applications are open.','',
        '| Service ID | Scope | Mode | Official information |','| --- | --- | --- | --- |']
    for s in services:lines.append(f'| `{s["id"]}` | {s.get("state") or "Central"} | {s["applicationMode"]} | [{s["title"]}]({s["informationUrl"]}) |')
    lines+=['','## Evidence and publication rules','',
        'Records require a stable ID, existing website/operator relationship, responsible department, category and jurisdiction, official HTTPS information URL, reviewed source/evidence IDs, review date/notes and explicit published/verified status. Every populated fact references a source ID. Null means unknown; an empty document list is rejected because it could falsely imply no documents. Online mode requires an application URL. Official URL suffix validation is a structural gate; the reviewer must still verify the actual source and its relevance.','',
        'The service organization registry preserves exact operator names already present on websites. The responsible department is stored separately: for example, AICTE runs its schemes while NSP is the application platform. Existing website ownership/review statuses are not upgraded.','',
        'Official sources were read using ordinary HTTPS/browser retrieval. Local research metadata contains response hashes and public-page text, never submitted form data. Newer UIDAI routes include a beta-branded host and were held for review. Udyam returned 403 to curl but its official page was readable through web retrieval; service facts are source observations, not an availability promise. PMEGP has a current official PIB explanation, while its candidate transaction host failed TLS; no certificate bypass was used. AICTE 2020/2025 guidelines remain linked by the current NSP page; their publication years and need to reconfirm the applicable academic year are recorded.','',
        '## Reproducible local workflow','',
        '1. Research an explicit official URL: `python tools/service_catalog.py research URL --output data/services/research/NAME.json`. It retrieves public material with normal certificate validation, bounded redirects/size and no login/form submission. Do not bulk crawl or bypass restricted pages.',
        '2. Write candidate JSON with `services`, including evidence references and null unknown fields. `python tools/prepare_service_pilot.py` reproduces this session’s candidates and named Codex review declarations; it does not publish them or substitute for a fresh source review.',
        '3. Stage: `python tools/service_catalog.py import CANDIDATES.json`. Existing decisions are retained. For an intentional revision use `--revision`; a new queue entry preserves the earlier publication decision.',
        '4. Review the sources and exact candidate. Approval JSON includes a higher version, operator registry and per-ID `reviewedBy`, `notes`, and SHA-256 of the sorted UTF-8 candidate JSON. `python tools/service_catalog.py publish APPROVALS.json` verifies the digest and gates before admitting/revising data. No approval or a rejected case is never silently promoted.',
        '5. Run `python tools/service_catalog.py check`, `python tools/service_catalog.py report`, `python tools/check_catalog.py`, Python/JVM/UI tests and the reports. Re-running the pilot import/publication admitted zero additional records and retained prior decisions. Two conditional-document clarifications were delivered as version 11 revisions, preserving their original review entries.',
        '6. Build the app, run `python tools/package_extension.py`, then create a newer signed package, e.g. `python tools/package_catalog.py update --version 12 --output artifacts/catalog-v12.jansetu`. Android Settings imports the services and website projection together. The raw website asset still has zero embedded services; runtime/check/report tools merge the separate pilot. Extension currently remains a website companion and does not claim service-search UI.',
        '7. Re-run `python tools/report_service_pilot.py` after the benchmark test writes the actual before/after results. Reports do not make network requests.','',
        'Publication requires reviews no older than 30 days. This is an editorial admission gate, not continuous re-verification. Runtime displays original review dates; a signature authenticates the editor, not the government. Public distribution of the combined directory still fails its existing reuse-permission gate.','',
        '## Future tracking boundary','',
        'No tracker was built. [Application tracking boundary](APPLICATION_TRACKING_BOUNDARY.md) defines user-entered applications, optional references/reminders, consent, storage/deletion decisions and the separate authorization required for any live government status provider.','',
        '## Verification and remaining work','',
        'Actual commands, failures and final build/device totals are in [QA_REPORT.md](QA_REPORT.md). Ranking/performance evidence is in [SEARCH_QA_REPORT.md](SEARCH_QA_REPORT.md). Broader state/service coverage, full document/eligibility review, current application windows and live transaction compatibility remain incomplete.']
    (ROOT/'docs/SERVICE_CATALOG_REPORT.md').write_text('\n'.join(lines)+'\n',encoding='utf-8')
    lines=['# Search QA report — Phase 3A','','Measured on the same local JDK 17 JVM with the actual 7,347 websites; eight queries, one warm-up pass and five measured repetitions (40 timed queries). Before used the existing website-only index. After uses the compact runtime asset plus 25 services and the combined deterministic engine. These are observed JVM timings, not Android frame/startup or production latency guarantees.','',
        '| Measurement | Before | After |','| --- | ---: | ---: |',
        f'| Records | {before["websites"]} websites / {before["services"]} services | {after["websites"]} websites / {after["services"]} services |',
        f'| Index preparation (ms) | {before["indexMs"]:.2f} | {after["indexMs"]:.2f} |',
        f'| Median query (ms) | {before["medianQueryMs"]:.2f} | {after["medianQueryMs"]:.2f} |',
        f'| P95 query (ms) | {before["p95QueryMs"]:.2f} | {after["p95QueryMs"]:.2f} |','',
        'Index timings are order/cache-sensitive because other unit tests may have prepared a catalog. Query timings and actual result identities are the more useful comparison; no strict speedup is inferred from a single run. Matching keeps all meaningful terms, removes conversational filler, normalizes curated synonyms, permits one-edit mistakes for longer tokens, weights sourced service fields and boosts exact curated intents. Specific matched services precede general websites; an exact portal-name query retains its portal first. Ranking never determines personal eligibility.','',
        '| Query | Before: first website / count shown | After: actual top three |','| --- | --- | --- |']
    for old,new in zip(before['queries'],after['queries']):
        previous=old['topResults'][0] if old['topResults'] else 'No result'
        ranking='; '.join(f'{i+1}. {row["type"]}: `{row["id"]}`' for i,row in enumerate(new['topResults'][:3])) or 'No result'
        lines.append(f'| {new["query"]} | {previous} | {ranking} |')
    lines+=['','## Meaning of results','',
        '- Engineering scholarship returns three sourced schemes, each with its own conditions; it does not assert that the person qualifies.',
        '- Caste certificate maps to the Tamil Nadu Community Certificate service. Pension and birth-copy results also state Tamil Nadu, not nationwide availability. Birth printing does not register a new birth.',
        '- NCS is a search service; no vacancy or hiring outcome is guaranteed. PMEGP is information-only until an application route is reviewed.',
        '- DigiLocker remains a general website result because the two proposed operations lack fresh readable evidence. Driving-licence queries use relevant general websites where metadata matches; no service is fabricated.',
        '- Type/category/level/state/district filters apply to the service’s own jurisdiction. Bihar-filtered pension service results are empty in this pilot. Unknown eligibility/documents remain unknown in detail pages.',
        '- Unit tests include misspelling `scholrship technical`, plural/synonym `income certificates`, unsupported terms, classification and exact portal names. Source-only matching cannot publish a candidate service.',
        '- UI uses the existing lazy list, background dispatcher and debounce; result types have distinct keys. It renders actual records with no LLM-generated URLs.',
        '- Full query result evidence: `data/validation_reports/phase3a-search-before.json` and `phase3a-search-after.json`. Android/device results and unresolved browser/accessibility checks are recorded separately in QA_REPORT.md.']
    (ROOT/'docs/SEARCH_QA_REPORT.md').write_text('\n'.join(lines)+'\n',encoding='utf-8')
    print('Service catalog and measured search reports regenerated.')

if __name__=='__main__':main()
