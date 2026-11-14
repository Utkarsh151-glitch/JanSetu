# JanSetu Phase 3C — Service Coverage, Verification and Data Quality

## Objective

Expand JanSetu's useful, source-backed government-service coverage while preserving the existing application, website catalog, service catalog, and My Applications tracker.

This phase is about data and core functionality, not a visual redesign.

### Current baseline

Inspect the repository and latest QA report to confirm:
- 7,347 website records.
- 25 individual service records.
- My Applications is implemented locally with encrypted storage and user-maintained statuses.
- The service catalog contains records with explicitly unknown eligibility or document requirements.
- Four service operations/routes were previously unresolved.
- 3,823 website-review cases remain pending.
- Government-directory reuse permission and production-release requirements remain unresolved.

Treat those figures as a baseline only. Do not overwrite newer data or reset review decisions.

## 1. Audit the current service catalog

Inspect:
- `data/services/catalog.json` and the service research/evidence records.
- Service editorial queue and publication rules.
- Service-to-website and service-to-organization relationships.
- Application routes and modes.
- Jurisdiction and regional-filter behaviour.
- `SERVICE_CATALOG_REPORT.md`, `SEARCH_QA_REPORT.md` and the latest `QA_REPORT.md`.

Produce a concise report identifying:
- Missing or weak evidence.
- Outdated or ambiguous requirements.
- Unconfirmed official application URLs.
- Incomplete jurisdiction coverage.
- Duplicate services and possible naming conflicts.
- Missing links between services and their responsible departments.
- Gaps between the citizen's search intent and available service records.

Do not start a new nationwide crawl just to increase the record count.

## 2. Build a service-coverage matrix

Create a reproducible report that measures coverage by:
- Central government.
- State and UT.
- District and local government, where authoritative data is available.
- Service category.
- Applicant or beneficiary group, where documented.
- Application mode: online, assisted or information-only.
- Verification status and last-reviewed date.

Distinguish between a jurisdiction being investigated, a relevant service being discovered, a service being source-reviewed, and a service being published.

Do not label a state as covered merely because the app contains a general portal serving that state.

Create:
- `docs/SERVICE_COVERAGE_MATRIX.md`
- `docs/SERVICE_GAPS_REPORT.md`

Include actual counts, source links, known limitations and a clear explanation of how each coverage figure was calculated.

## 3. Resolve existing service-review cases

Review the existing unresolved candidates first, especially:
- The two DigiLocker operations requiring current, readable official evidence.
- The driving-licence service requiring jurisdiction-specific review.
- The PMEGP direct application route with the previously observed TLS hostname problem.

Use authoritative evidence and normal TLS verification. A failed request, HTTP error, JavaScript shell, or inaccessible page is not permission to guess a URL or bypass security.

For each candidate, either:
- Publish after satisfying the existing review gates.
- Revise the candidate using new evidence.
- Retain it as pending with a specific reason.
- Reject it only when evidence supports rejection.

Do not display Apply buttons for unconfirmed routes. Keep information-only records useful without pretending that their transaction flow is confirmed.

## 4. Expand the service catalog in a controlled manner

Research additional high-value services from authoritative official sources.

Prioritise useful coverage across central services and a selection of states/UTs, rather than concentrating on one jurisdiction. Investigate categories such as education, income and community certificates, pensions, employment, identity-related services, business support, grievances and licences.

Aim to investigate at least 50 additional service candidates, and publish those that meet the evidence requirements. Treat this as a research target, not a requirement to manufacture 50 published records.

For every proposed service:
- Establish the correct service and responsible authority.
- Confirm the jurisdiction.
- Record official information and application URLs separately.
- Cite sources for each important populated fact.
- Record source and review dates.
- Mark unknown eligibility, documents, fees, deadlines and processing times explicitly.
- Identify whether a route is online, assisted or information-only.
- Preserve rejected and unresolved candidates with reasons.

Use the existing editorial import/review/publication pipeline. Do not write directly into the published catalog to avoid its validation rules.

No service record should imply that a user is personally eligible or that an application window is currently open without supporting evidence.

## 5. Protect search and My Applications

Maintain backwards compatibility with existing service IDs and website records.

Verify that:
- New service records appear in citizen-intent search.
- Service ranking respects jurisdiction and result type.
- Exact website-name queries continue to find the right portal.
- Regional filters do not misrepresent a local service as nationally available.
- Every published service detail page displays source information and unknown fields honestly.
- Confirmed application actions open the intended official URL.
- Information-only or unconfirmed routes do not show a false Apply action.
- Saved application records, status history, reminders and references survive catalog updates and restoration.
- Catalog revisions never alter or delete personal application records.

Do not collect government credentials, OTPs or unnecessary identity documents. Do not add live application-status claims or cloud sync in this phase.

## 6. Maintainability and update workflow

Keep the distinction between the authoritative website directory and the service catalog.

Extend existing editorial tools if needed so the workflow remains reproducible:
1. Research official sources.
2. Create a candidate with evidence.
3. Validate the schema and source links.
4. Review the proposed facts and application route.
5. Publish only after the existing gates pass.
6. Generate reports and package the updated runtime catalog.
7. Verify the catalog-update mechanism without changing saved tracker data.

Do not resolve the 3,823 website-review cases automatically by treating an unresponsive destination as retired or an official-looking hostname as proof of ownership. Handle that backlog through its existing review workflow and permitted authoritative sources.

## 7. Testing and deliverables

Add regression tests covering:
- Evidence provenance and service publication rules.
- Unknown and partial eligibility/document requirements.
- Duplicate and ambiguous service candidates.
- Invalid, redirected and unconfirmed application URLs.
- Jurisdiction and state-filter correctness.
- New service-search queries and ranking.
- Preservation of existing service and website records.
- Catalog import/update/restore with saved application records.
- Application-route availability and accurate action labels.
- Review workflow idempotency and preservation of previous decisions.

Update:
- `docs/SERVICE_CATALOG_REPORT.md`
- `docs/SERVICE_COVERAGE_MATRIX.md`
- `docs/SERVICE_GAPS_REPORT.md`
- `docs/QA_REPORT.md`

Run applicable Python, Node, Android JVM, lint, build and connected-emulator tests. Record actual counts and failures. Build and install the debug APK when the environment permits.

## 8. Scope protection

Do not redesign the whole UI in this phase. Do not replace the current navigation or introduce a new design system. Make only small usability fixes required for correct data presentation or broken workflows.

Do not introduce a backend, AI API or new service merely to make the catalog larger. Do not fabricate service information, application routes, coverage figures or test results.

## Completion criteria

JanSetu has a reproducible service-coverage report, a better-evidenced and more geographically balanced service catalog, clear handling of unresolved routes, useful search results, and no regressions in saved applications or the existing website directory.

The report must clearly distinguish published records from candidates, and local development readiness from legal permission to distribute the directory publicly.