# JanSetu Phase 3A — Verified Services and Citizen-Intent Search

## Objective

Turn JanSetu's existing government website directory into a service-discovery application. Citizens should be able to search for what they need in ordinary language, find relevant government services, understand their purpose and requirements, and open the correct official website.

The latest QA report dated 16 October 2026 recorded:
- 7,347 website records.
- 9 manually reviewed websites.
- 7,338 directory-listed records.
- Zero individual service records.
- 3,823 pending website-review cases.
- Existing search, filters, organization browsing, compact runtime catalog, and signed catalog-update functionality.

Verify these figures against the current repository before making changes. The report is a baseline, not permission to overwrite newer work.

## 1. Audit the existing implementation first

Inspect the repository's current data models, search index, ranking logic, result cards, filters, navigation, catalog packaging, editorial tools, and tests.

Document what already works and identify the minimum changes needed.

Do not:
- Rebuild the existing search interface from scratch.
- replace the authoritative catalog with a new dataset.
- reset or discard human review decisions.
- reprocess all 7,347 websites unnecessarily.
- add a backend or paid AI API without demonstrating the need.

Retain the existing search and navigation features unless a measured or reproduced issue justifies modifying them.

## 2. Introduce individual service records

A website is not the same as a service. One department portal can provide many different services.

Create a separate, validated service catalog linked to the existing website and organization records.

Each service record should support:
- Stable service ID and official service name.
- Plain-language explanation of what the service does.
- Common citizen search phrases and synonyms.
- Service category and responsible department.
- Applicable central, state/UT, district, or local jurisdiction.
- Eligibility conditions, where confirmed.
- Required documents and conditional documents, where confirmed.
- Application procedure, fees, deadlines, and processing information where officially documented.
- Official information and application URLs, distinguished where necessary.
- Supporting source URLs, evidence, last-checked date, verification state, and review notes.
- Publication status and relationships to relevant websites.

Use the existing project conventions where possible. Unknown information must remain unknown; do not invent requirements or infer eligibility from a service name.

## 3. Improve citizen-intent search using the existing system

Preserve the existing indexed search architecture and enhance its ranking to include the new service records.

Support examples such as:
- "I need a scholarship for engineering."
- "How can I get a caste certificate?"
- "Find government jobs."
- "I want to apply for a pension."
- "How do I obtain a birth certificate?"
- "I need a subsidy for my small business."

Search service names, plain-language descriptions, citizen intents, synonyms, categories, departments, jurisdiction, and applicable website metadata.

Prioritise relevant verified services for specific service-oriented queries. Continue showing relevant general websites when a service record is not yet available. Distinguish clearly between a service result and a general website result.

Support reasonable spelling mistakes, useful filters, and meaningful empty states. Do not report a service as available merely because the search terms match a portal's name.

Measure performance on the existing full catalog before and after changes. Keep the existing bounded/lazy result rendering and off-main-thread work. Do not introduce an LLM simply to make the feature appear intelligent.

## 4. Build service information pages

Each published service should have a readable information page containing:
- What the service is for.
- Who may apply.
- Documents required.
- How to apply.
- Fees and deadlines, when verified.
- Official links to further information and the application.
- The source and date of last verification.

Show a clear indication when a field is unverified or unavailable. Do not guarantee eligibility, approval, or processing times.

Keep government application flows on the official site unless a specific, authorised integration exists. Do not collect portal passwords, OTPs, or unnecessary identity documents.

## 5. Start with a small, evidence-backed service pilot

Implement a maintainable editorial workflow and seed approximately 25–50 high-demand service records only where reliable official evidence is available.

Investigate categories such as scholarships, certificates, pensions, employment, licences, and subsidies. Treat these as research priorities, not claims that verified records already exist.

For every published record:
- Verify the official service and relevant jurisdiction.
- Confirm the official information/application URL.
- Attach supporting evidence for eligibility and document requirements.
- Mark missing facts as unknown.
- Keep ambiguous cases in a review queue.
- Preserve existing website records and all earlier manual decisions.

If 25–50 records cannot be verified, publish fewer rather than weakening the evidence standard. Do not create fake records just to meet a count.

## 6. Design for future profile-based application tracking

Do not build the full tracker in this phase, but document the integration boundary for a future profile feature.

The future tracker should support manually recorded applications, service association, submission date, user-maintained status, optional reference number, deadline, notes, and optional reminders.

Live government application-status retrieval must be a separate integration requiring a legitimate official interface and tested authorization. Do not present manually entered progress as a verified government status.

Any future account system must consider consent, secure storage, data minimisation, deletion, and whether cloud synchronisation is necessary.

## 7. Testing and deliverables

Add tests for:
- Service schema and validation.
- Official-source provenance and publication gates.
- Unknown eligibility and document information.
- Search relevance and ranking.
- Spelling variations and synonyms.
- Category and jurisdiction filters.
- Service versus website result classification.
- Missing or malformed URLs.
- Compatibility with the full existing catalog.
- Preservation of existing records and review decisions.
- Search responsiveness on the current large dataset.

Update the actual repository with:
- The service catalog and validation rules.
- A reproducible source-backed service research/import workflow.
- Citizen-intent search enhancements.
- Service detail screens.
- A small verified pilot dataset where evidence permits.
- `docs/SERVICE_CATALOG_REPORT.md`.
- `docs/SEARCH_QA_REPORT.md`, documenting representative queries and actual result rankings.
- Relevant automated tests and updated project documentation.

Run the applicable Python, Node, Android JVM, lint, build, and emulator tests. Report actual results, failures, performance measurements, and unfinished work. Do not claim that any test passed unless it was run.

## Completion criteria

A citizen can search by need, discover a relevant verified service where one exists, understand its purpose and available eligibility/document guidance, see the supporting official sources, and open the correct official page.

Existing directory functionality remains intact, unsupported claims are not published, performance remains measured, and incomplete service coverage is clearly disclosed.