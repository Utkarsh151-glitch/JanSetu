# JanSetu — Codex Project Execution Guide

> **Purpose:** This file is the single source of instructions for Codex to build JanSetu, an Android-first Indian government-service discovery app, through implementation, verification, and release preparation.
>
> **How to use:** Place this file at the root of the repository as `JANSETU_CODEX_PLAN.md`. In Codex, ask it to read and follow this file. Codex should execute the phases in order, preserve working code, and keep this file's progress checklist updated. Do not expect one run to verify every government website in India; the catalog must report actual coverage.

---

## 0. Mission and product principles

Build **JanSetu** (working name; keep branding configurable): an Android application that helps people discover Indian government websites by what they need to do, understand how to use those services, and access official portals through a consistent, accessible interface.

The product has two complementary layers:

1. **Discovery:** a searchable, categorized directory of verified official websites and services.
2. **Service companion:** a polished native JanSetu page for each published record, with plain-language guidance, verified checklists, source attribution, and a safe route to the official service.

For compatible websites, provide an in-app browser. Where authorized APIs or integrations exist, provide deeper native experiences. Do not claim that JanSetu has redesigned the underlying page of a third-party website unless it genuinely has.

### Non-negotiable principles

- Build a real Android application, not just a mockup or landing page.
- Inspect the repository before editing; preserve working code and architecture.
- Use real, attributable sources. Never fabricate websites, links, schemes, fees, deadlines, eligibility, or test results.
- Do not claim nationwide catalog completeness without evidence.
- Every published record must have provenance and meet a documented verification policy.
- Do not inject arbitrary CSS or JavaScript into third-party sites to force a redesign.
- Do not bypass TLS/certificate errors or weaken security to make a website load.
- Never collect government passwords, OTPs, or authentication secrets.
- Minimize personal data and make privacy behavior match the documentation.
- No dead buttons, fake success states, or mock functionality represented as complete.
- Run tests and builds where the environment supports them. Report what actually ran and what remains unverified.
- If a capability is blocked by missing credentials, unavailable sources, or unavailable infrastructure, implement independent work that can proceed, document the exact blocker, and never pretend the blocked feature works.

---

## 1. Operating instructions for Codex

At the start of each session:

1. Read this file fully.
2. Inspect the repository tree, README, build files, manifests, dependencies, source code, tests, environment, and current Git changes.
3. Read the progress checklist and identify the next incomplete phase.
4. Preserve existing architecture and useful code. Do not recreate the project unnecessarily.
5. Write a short task checklist, then implement the phase; do not stop at a plan.
6. Run relevant tests/build/lint commands, fix regressions, and record actual outcomes.
7. Update the progress checklist and documentation before finishing.
8. Summarize changed files, functionality completed, commands run and results, known limitations, and the next phase.

Do not start a later phase while earlier phase acceptance criteria are materially unmet, unless the remaining issue is explicitly documented as blocked and later work can proceed independently. Do not ask the user to decide ordinary engineering details; use reasonable defaults and document trade-offs. Ask only when a genuinely blocking decision cannot be resolved safely.

### Repository discipline

- Avoid destructive rewrites and unrelated refactors.
- Never overwrite user changes without inspecting them.
- Keep secrets out of source control. Use environment variables and local untracked configuration.
- Use stable, compatible dependency versions and verify them rather than guessing.
- Keep catalog data separate from UI code.
- Keep documentation synchronized with actual implementation.
- Do not add empty folders or boilerplate that serves no purpose.
- Do not claim a file, APK, deployment, or test exists unless it has actually been created or verified.

---

## 2. Recommended architecture

Inspect the existing repository first. If starting from an empty repository, use the following as the default.

### Android

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Room for local catalog caching
- Kotlin Coroutines and Flow
- Retrofit or an equivalent maintained HTTP client if networking is needed
- WorkManager for suitable scheduled refreshes
- Android WebView for compatible official websites
- Gradle with reproducible dependency configuration

Separate presentation, domain logic, repositories, networking, local storage, search, catalog, browser, integrations, and settings. Use dependency injection only if it improves maintainability for the chosen project size.

### Backend (only when it materially helps)

Choose **one** backend stack if one is needed, preferably FastAPI or Node.js, with PostgreSQL. Do not introduce multiple backend stacks without a clear requirement. A local bundled catalog must remain usable when the backend is unavailable.

### Suggested repository layout

Adapt this to the real repository; do not create empty directories just to match it.

```text
app/
  src/main/
  src/test/
  src/androidTest/
data/
  schemas/
  source_registry/
  import_examples/
  validation_reports/
backend/                 # if needed
admin/                    # if a separate admin interface is needed
docs/
  ARCHITECTURE.md
  DATA_SOURCES.md
  DATA_VERIFICATION.md
  CATALOG_COVERAGE.md
  WEBVIEW_SECURITY.md
  PRIVACY.md
  TERMS.md
  DEPLOYMENT.md
  QA_REPORT.md
  RELEASE_CHECKLIST.md
  KNOWN_LIMITATIONS.md
JANSETU_CODEX_PLAN.md
```

---

## Phase 1 — Initial Android foundation and core UX

### Goal

Create or stabilize a buildable Android app with a polished visual foundation, navigation, data models, and initial end-to-end flows.

### Tasks

1. Audit the repository and build environment before making changes.
2. Set up or preserve the Android project and architecture.
3. Define configurable app name, application ID, theme, and branding.
4. Create a polished Material 3 design system with spacing, typography, colors, iconography, cards, buttons, loading states, empty states, and error states.
5. Implement light and dark themes.
6. Implement initial onboarding with optional language and state preference; allow skipping.
7. Implement the home screen with a prominent search field, need-based category area, featured/popular section where data supports it, and recent/favorite areas with proper empty states.
8. Implement navigation to search, categories, website details, favorites/history, and settings. Do not leave fake buttons.
9. Define initial catalog and service domain models. Keep data-driven content separate from presentation.
10. Add local storage and a repository abstraction suitable for bundled data and later remote updates.
11. Add basic unit/UI tests and build instructions.
12. Create a short architecture document.

### Acceptance criteria

- App compiles in the available environment.
- Home and navigation work.
- UI is responsive and accessible at a basic level.
- Empty/error/loading states exist.
- No core action is a dead end.
- Tests that are supported by the environment run and their results are recorded.

---

## Phase 2 — Real government website catalog and ingestion

### Goal

Build a real, expandable catalog and a repeatable pipeline to import, validate, and maintain official records.

### Source discovery

Investigate current, authoritative sources such as:

- `india.gov.in`
- `services.india.gov.in`
- Official ministry, department, state, union territory, district, and local-government directories
- `data.gov.in` where relevant
- Other verified official registries and documented public datasets

Check current accessibility, ownership, format, licensing, and permitted usage before relying on any source. Do not assume every source allows scraping.

### Catalog scope

Cover, as discoverable and verifiable:

- Central ministries and departments
- Central citizen-service and scheme portals
- State and union territory portals and departments
- District and local-government services
- Certificates, identity, education, scholarships, jobs, health, welfare, agriculture, transport, land/property, taxation, business, grievances, courts, utilities, and public information
- Statutory/regulatory bodies and relevant public institutions

These are target areas, not a claim that all records have been found.

### Data model

Support at least:

- Stable ID
- Official title and plain-language description
- Canonical URL and domain
- Organization and parent organization
- Government level and jurisdiction
- State/UT/district when relevant
- Primary and multiple secondary categories
- Citizen use cases, keywords, and synonyms
- Available language information
- Service type and capabilities
- API/integration availability only when verified
- Authentication/browser requirements when known
- Source URL, source type, and provenance
- Verification status and verification timestamp
- Last modified timestamp
- Known limitations and editorial notes
- UX assessment status when available

Model organizations, websites, and services separately where needed: one organization may own multiple sites and one site may host multiple services.

### Ingestion requirements

1. Implement source adapters for supported documented APIs and permitted public datasets.
2. Support appropriate CSV/JSON and documented source formats.
3. Normalize URLs/domains and identify canonical URLs.
4. Deduplicate without erasing meaningful language or service distinctions.
5. Validate URL structure and inspect redirects safely.
6. Track retrieval timestamps, source provenance, failures, and import method.
7. Preserve editorial corrections when source data is refreshed.
8. Use rate limits, backoff, caching, and controlled scheduled refreshes.
9. Respect source terms, access controls, licensing, and applicable crawling restrictions.
10. Maintain a review queue for ambiguous ownership, uncertain records, and failed checks.
11. Publish only records meeting a documented verification policy. An HTTP 200 response is not proof of government ownership.
12. Never invent records to reach a target count.

### Coverage report

Create `docs/CATALOG_COVERAGE.md` with actual, reproducible counts:

- Discovered records
- Published/verified records
- Unique domains
- Records awaiting review
- Duplicates resolved
- Broken/redirecting/stale URLs
- Categories represented
- States and UTs represented
- Coverage by source
- Last import time and failures

Do not claim “all government websites” or nationwide completeness without evidence.

### Acceptance criteria

- Import and validation commands are documented and repeatable.
- Real records have attributable sources and verification status.
- Duplicate detection and error reporting work.
- Coverage report reflects actual data.
- Tests cover parsing, canonicalization, deduplication, provenance, and failure handling.

---

## Phase 3 — Search and categorization

### Goal

Allow citizens to search by what they want to accomplish, even if they do not know the department or portal name.

### Categories

Implement a configurable hierarchy with categories such as:

- Identity and personal documents
- Certificates and registrations
- Education, scholarships, and students
- Government jobs and examinations
- Health and family welfare
- Pension and social-security benefits
- Agriculture and farmer services
- Land, property, and housing
- Transport and driving licences
- Taxes and financial services
- Business and entrepreneurship
- Complaints and grievance redressal
- Courts and legal services
- Electricity, water, and utilities
- Municipal/local services
- Information and public data

Allow each record to belong to multiple categories. Support both “browse by citizen need” and “browse by organization.”

### Search behavior

Support:

- Website, department, and service names
- Plain-language citizen queries
- Categories, synonyms, and use cases
- Common spelling errors and fuzzy matching
- State/UT and central/state/local filters
- Search suggestions and recent searches
- Pagination or incremental loading
- Helpful no-results suggestions
- Local search over cached catalog data

Representative queries to test:

- “I need an income certificate”
- “Find scholarships”
- “Check land records”
- “Apply for a driving licence”
- “Find government jobs”
- “File a complaint”
- “Find pension benefits”
- “Show services in Tamil Nadu”

Use deterministic search and curated synonyms first. Add semantic/AI search only if useful. Search results must always resolve to actual catalog records; AI must never invent a URL or represent unverified results as official.

Rank with explainable factors such as text relevance, exact match, use-case match, jurisdiction, verification status, and freshness.

### Acceptance criteria

- Search and filters work on actual catalog records.
- Tests cover ranking, synonyms, spelling tolerance, filters, duplicates, empty results, and verification.
- UI clearly distinguishes website results from individual service results.
- Search remains responsive as the catalog grows.

---

## Phase 4 — Website details and service-companion UI

### Goal

Give every published website a consistent, attractive native JanSetu experience, even when its underlying site cannot be redesigned.

### Website detail page

Display where available:

- Official title and organization
- Jurisdiction and categories
- Plain-language explanation
- Common tasks/services
- Official domain and source
- Verification date
- Related services
- Favorite action
- Report-a-problem action
- Primary “Open official website” action
- Service-guide action

### Service guides

Use data-driven, per-service guides containing:

- Step-by-step instructions
- Eligibility criteria
- Required documents
- Fees and deadlines
- Prerequisites
- Official application link
- Troubleshooting
- Sources and last-reviewed dates

Only show factual details when supported by a trustworthy source. If information is unavailable, say so rather than guessing. Clearly distinguish JanSetu guidance from official government decisions.

Provide a useful detail page even when no verified guide exists. Never show a blank page just because metadata is incomplete.

### Design

Use the established Material 3 system, excellent typography, clear hierarchy, appropriate spacing, restrained motion, accessible touch targets, and useful loading/empty/error states. Do not use decorative elements at the cost of clarity.

### Acceptance criteria

- Every published record renders without crashing, including incomplete optional metadata.
- Every record has a useful native companion screen.
- Guide content is attributable and freshness-aware.
- All navigation/actions work.
- Tests cover guide rendering, missing data, sources, and navigation.

---

## Phase 5 — Secure in-app browser and compatibility framework

### Goal

Provide consistent navigation around compatible official websites without pretending to replace their underlying UI.

The browser extension requested in the 13 October 2026 session is a second, optional companion surface. Its first version uses a Manifest V3 side panel with the same verified local catalog and source-backed guides. It may show navigation and guidance beside an official tab. It cannot improve government server response times; it must not claim to redesign an official page. Site-specific page adjustments require separate ownership, permission, security, accessibility, and regression review. Do not inject generic CSS or JavaScript into government pages.

### Browser features

- Back, forward, refresh, and home
- Loading progress
- Visible current domain
- Link to the service guide
- Error/offline states and retry
- Safe external-browser fallback
- Deliberate download/upload behavior where appropriate
- Correct WebView lifecycle and back handling

### Security requirements

- HTTPS by default.
- Never bypass certificate/TLS errors.
- Validate URLs, redirects, and destination schemes.
- Block cleartext HTTP unless an explicit, narrowly reviewed exception is required.
- Avoid arbitrary JavaScript bridges.
- Disable unnecessary file/content access.
- Handle `intent:`, telephone, email, and other URI schemes deliberately.
- Do not inject arbitrary CSS/JavaScript into third-party pages.
- An optional browser extension may apply a static, reviewed, user-invoked readability stylesheet to explicitly allowlisted public information pages. It must be reversible and must not read forms, credentials, or page contents.
- Do not log credentials, OTPs, tokens, sensitive form values, or page contents.
- Do not store sensitive session data outside appropriate platform mechanisms.
- Permit required identity/payment redirects only through explicit, reviewed policy.
- Provide external-browser fallback for incompatible authentication/payment flows.

### Capability registry

Each site should have configuration indicating supported capabilities, such as:

- In-app browser support
- External browser required
- Verified service guide available
- Authorized API/integration available
- Upload/download support
- Authentication limitations
- Known compatibility issues

Do not claim compatibility without testing.

### Acceptance criteria

Test safe navigation, invalid certificates, redirects, disallowed schemes, file handling, authentication handoff, network failure, and back navigation. Produce a compatibility report and document any tests that could not run.

---

## Phase 6 — Backend, admin and catalog operations

### Goal

Enable catalog maintenance without editing app source code for routine content changes.

Use the existing backend if one exists. Otherwise, add a backend only when it materially improves central updates, search, or administration. Prefer one stack and PostgreSQL if needed.

### Admin features

- Authenticated admin access and role-based permissions
- Add/edit website and service records
- Review source provenance
- Assign categories and jurisdictions
- Edit service guides
- Verify canonical URLs
- Review user reports
- Approve/reject candidates
- View stale and broken records
- Run permitted imports
- Publish catalog changes
- Export coverage and quality reports

### API/data features where applicable

- Search and listings
- Website details
- Categories and filters
- Related services
- Catalog version information
- Report submissions
- Administrative endpoints

Implement validation, pagination, indexes, structured errors, rate limiting, migrations, secure configuration, and tests. Enforce authorization on the server, not merely by hiding UI controls.

Use versioned catalog updates so content can be refreshed without a required app release. Keep cached catalog content available when the backend is offline.

### Acceptance criteria

Demonstrate a complete record workflow: import/create → validate → review → publish → deliver update to app. Document database setup, migrations, admin access, and any unconfigured infrastructure.

---

## Phase 7 — Localization, advanced UX and authorized integrations

### Goal

Improve access for citizens across languages and implement deeper service experiences where genuinely supported.

### Localization/accessibility

- Localize all app-owned strings.
- Start with English and Hindi, with a scalable structure for Tamil and other Indian languages.
- Support Unicode, TalkBack, font scaling, contrast, touch targets, and localized dates/numbers.
- Allow language changes at any time.
- Clearly distinguish translated JanSetu guidance from the language available on the official site.
- Do not claim an official form has been translated when it has not.

### Integration framework

Create a provider-neutral adapter interface describing:

- Supported capabilities and operations
- Official source/API documentation
- Authorization requirements
- Input/output schemas
- Error handling and fallbacks
- Rate limits and known limitations

Implement native integrations only when documented APIs or other authorized access exist. Potential examples include public information retrieval or supported application-status checks, but do not assume a particular service offers an API.

Never scrape protected/private data to simulate an API. Never collect government passwords or OTPs. Validate returned data, identify its source, and do not fabricate transaction status.

If AI-assisted search/guidance is added, ground it in verified catalog records, show official sources, and clearly label uncertainty. AI must not invent links, fees, deadlines, eligibility, or status.

### Acceptance criteria

Test localized UI and integration failure paths. Demonstrate a real end-to-end integration only if a supported API and required credentials are available. Otherwise, finish the adapter framework and fallback, and explicitly mark the integration as not operational.

---

## Phase 8 — Comprehensive QA, security and bug fixing

### Goal

Bring the app to a verifiable release-candidate state.

### Required tests

**Unit tests**
- Search ranking and synonyms
- URL normalization and validation
- Domain policy
- Catalog parsing and deduplication
- Verification freshness
- Adapter selection
- Guide validation

**Integration tests**
- Database/repository behavior
- API validation and authorization
- Imports and incremental refresh
- Cache/offline behavior
- Publication workflow
- Pagination and failure handling

**Android UI tests**
- Onboarding
- Home/navigation
- Search and filters
- Categories
- Website details and guides
- Favorites/history
- Settings and language
- Browser navigation
- Offline, loading, and error states

**Security checks**
- Invalid certificates and redirects
- Disallowed URI schemes
- WebView bridge/file access
- Sensitive-data logging
- Unauthorized admin access
- Dependency vulnerabilities where tools permit

**Data-quality checks**
- Duplicate/invalid URLs
- Missing provenance
- Unverified published records
- Stale/broken URLs
- Incorrect category relationships

**Performance/accessibility**
- Startup and search latency
- Large-list rendering
- Memory use and WebView lifecycle
- TalkBack, contrast, font scaling, and touch targets

### Execute and fix

Run supported build, test, lint, and static-analysis commands. Fix failures and regressions rather than merely listing them. Do not weaken security controls to make tests pass.

Record actual commands, results, and environment limitations in `docs/QA_REPORT.md`. If device/emulator testing is unavailable, state exactly what remains unverified. Never claim complete coverage or a clean security audit without evidence.

---

## Phase 9 — Production release preparation

### Goal

Prepare a reproducible Android release candidate and all documentation needed for launch.

### Tasks

1. Verify application ID, app name, icons, splash screen, version name/code, Android compatibility, and production configuration.
2. Separate debug and production settings.
3. Provide secure release-signing instructions. Never commit signing keys or secrets.
4. Build a debug APK and, where possible, a signed release APK or Android App Bundle.
5. Run the available build and tests; report actual outcomes.
6. Verify that production settings do not accidentally point to local development services.
7. Document backend deployment, database migrations, environment variables, catalog import/refresh, and admin setup if applicable.
8. Prepare privacy policy and terms drafts that accurately reflect implemented behavior.
9. Prepare Play Store listing text, short description, feature description, screenshot plan, support information, and category recommendation.
10. Verify current Play Console requirements, target API rules, permissions, data-safety declarations, and testing requirements instead of guessing.
11. Prepare a release checklist covering catalog accuracy, source verification, WebView security, accessibility, offline behavior, privacy, and support.
12. Document unsupported sites, unimplemented integrations, operational risks, and remaining limitations.
13. Do not imply official government endorsement unless a genuine authorization/partnership exists.
14. Do not claim deployment or Play Store publication unless it actually happened.
15. Provide exact, verified artifact paths and reproducible build commands.

### Acceptance criteria

Deliver a buildable Android Studio project, actual build artifacts where possible, installation/build instructions, signing guidance, operational documentation, QA/security reports, privacy/terms drafts, Play Store listing draft, release checklist, and known limitations.

Clearly distinguish local build success from device testing and Play Store publication.

---

## Cross-phase quality gates

These requirements apply to every phase.

### Product quality
- No dead buttons or broken navigation.
- No mock data presented as verified government data.
- Helpful loading, empty, error, and offline states.
- Consistent design system and accessibility.
- Data-driven content and expandable taxonomy.
- Clear source attribution and freshness.

### Security and privacy
- No hardcoded secrets.
- No credential/OTP collection.
- No TLS bypass.
- No unsafe third-party UI injection.
- Minimal permissions and data collection.
- Server-side admin authorization.
- Accurate privacy documentation.

### Engineering
- Reproducible builds and migrations.
- Useful tests for changed functionality.
- Regression testing after changes.
- No unverified dependency/version guesses.
- Documentation kept current.
- No claims of success without evidence.

### Catalog trustworthiness
- Source provenance for every published record.
- Clear verification policy.
- Separate candidate/review records from published verified records.
- Actual coverage metrics.
- Broken/stale record monitoring.
- No claim of “all websites” without defensible evidence.

---

## Progress checklist

Codex: update this checklist at the end of every session. Mark an item complete only after the acceptance criteria have been checked.

- [x] Phase 1 — Android foundation and core UX
- [ ] Phase 2 — Real catalog and ingestion pipeline
- [x] Phase 3 — Search and categorization (local website catalog; services appear only when verified records exist)
- [x] Phase 4 — Service-companion UI (local website companions; two attributable guides, missing metadata stated)
- [ ] Phase 5 — Secure in-app browser
- [x] Phase 6 — Backend/admin/catalog operations (local editorial CLI and signed manual update delivery; hosted multi-user administration not configured)
- [ ] Phase 7 — Localization and authorized integrations
- [ ] Phase 8 — QA, security, and bug fixing
- [ ] Phase 9 — Release preparation

### Current phase status — 16 October 2026

| Phase | Status | Remaining requirement or boundary |
| --- | --- | --- |
| 1. Android foundation | Complete locally | Additional device/accessibility coverage continues in Phase 8. |
| 2. Catalog and ingestion | Implemented locally; data coverage/reuse partial | 7,347 websites, nine manually reviewed; 3,823 pending website cases and 25 separately reviewed pilot services. Independent operator review, lower LGD IDs and permitted public reuse remain. No nationwide completeness claim. |
| 3. Search and categories | Complete for local catalog and Phase 3A service pilot | Ranking/filters, suggestions/recent searches, configurable groups, organization browsing and 25 services. Result types are distinct; measured rankings and preservation evidence are in SEARCH_QA_REPORT.md. Service coverage is incomplete. |
| 4. Companion UI | Complete for available local content | Every website has a native summary/source/action; 25 service information pages display sourced conditions or explicit unknowns. Two portal guides have JanSetu Hindi explanations. Unknown fees/documents/eligibility are explicitly deferred to official sources. More verified guidance is editorial work. |
| 5. Browser and compatibility | Implemented, QA partial | Host-limited browser, lifecycle and external handoff; narrow reversible Chrome CSS pilot. Live TLS/payment/file flows, installed extension and broader devices remain unverified. |
| 6. Catalog operations | Complete for local single-editor workflow | Import/validate/review/publish, compact asset and signed manual import/restore. No hosted admin, database, scheduled fetch or multi-user roles; local OS/key access is authority. See DEPLOYMENT.md. |
| 7. Localization and integrations | Framework implemented; partial | English/Hindi UI, privacy and two guides. Independent translation/locale review remains. Provider-neutral integration framework tested; zero live authorized API providers, explicitly not operational. |
| 8. QA and security | Local Phase 4 hardening complete; release certification remains | Isolated QA profile, 71 Python + 7 extension + 38 JVM + 34 API 37 Android passes, preserved-preview hash gate and tracked findings are in PHASE4_QUALITY_REPORT.md. Human TalkBack, physical-device/OEM, browser and multi-device certification remain. |
| 9. Release | Local artifacts/drafts prepared; public release pending | Production keys/signing, reuse permission, hosted policy, store assets/declarations/testing and publication remain external gates. |

The Chrome readability pilot is currently enabled only for the National Scholarship Portal home page. The Android text zoom control is limited to the National Portal WebView. Neither changes a government server's response time.

### Phase 2A collection implementation - 14 October 2026

The resumable IGOD adapter now follows public navigation and source-exposed pagination, retains snapshots/hashes and reconciles category result counts. The actual live statistics are in `docs/DISCOVERY_RUN_REPORT.md`; bundled counts are in `docs/CATALOG_COVERAGE.md`. Local directory entries show **Listed by IGOD**, preserving the nine manually reviewed records. Local reproduction permission was discussed with the user: the catalog is for development now, and NIC permission must be obtained before public distribution. The public-release catalog check enforces this boundary.

Organizations, website hosts, landing URLs and service entities are separated in structured data. No individual services were invented. State/UT, level, sector and district filters use only populated metadata. Search indexing runs off the UI thread, results render lazily, and the extension renders bounded result batches. Adding a directory record does not grant a CSS profile or WebView access. Neither Phase 2 nor later project phases are marked complete by these reports.

### Inventory and UX research — 14 October 2026

The controlled official-source pass retained six additional **unpublished** state, UT, district and municipal candidates. The catalog still publishes nine central portals and zero individual services. `docs/GOVERNMENT_WEBSITE_INVENTORY.md`, `docs/PORTAL_UX_AUDIT.md`, `docs/UI_INTERVENTION_MATRIX.md` and `docs/UX_AUDIT_MANUAL_CHECKLIST.md` record source limits, ten page-level assessments and a conditional five-portal research pilot. The National Portal now hosts the services directory formerly at `services.india.gov.in`; historical service links were not imported. Phase 2 and Phase 8 remain partial. No extension allowlist or third-party stylesheet was expanded.

### Work session — 14 October 2026 (local candidate and release drafts)

- **Phase 1 accepted locally:** The app compiles, installs, and opens on the API 37 emulator. Onboarding, navigation, search, guide/source, privacy, and report actions have passing UI coverage. Home was visually checked at normal and 1.3× font scale in light/dark themes; a dark status-bar icon contrast issue found during this check was fixed. TalkBack and other device sizes remain Phase 8 checks.
- **Phases 2–5 progress:** Nine verified central portals, two source-backed guides, three unpublished review candidates, a reproducible coverage report and one-time HTTPS link audit, deterministic search, a limited National Portal WebView, and an optional Chrome side-panel companion. The companion improves navigation and local search beside official sites. An opt-in typography stylesheet is piloted for the National Scholarship Portal home page; it does not accelerate servers or redesign forms.
- **Phases 6–7 open:** Local CSV/JSON editorial publication is operational, but there is no backend or remote update channel. Full Hindi content, state/local coverage, service records, and authorized integrations remain open.
- **Phases 8–9 progress:** Six unit tests, four API 37 UI tests, five pipeline tests, four extension logic tests, Chrome panel render smoke, Android lint, and debug/release builds passed in the latest work session; details are in `docs/QA_REPORT.md`. Utkarsh's supplied identity/support email is in the bundled privacy policy and release drafts. The release APK/AAB are unsigned pending a private upload key; no Play submission has occurred.

### Work session — 13 October 2026 (follow-on implementation)

- **Phase 1 verification:** Espresso was pinned to 3.7.0 to fix the Android 37 reflection failure. Onboarding/navigation and search UI tests passed on `Medium_Phone_API_37.0` before the later catalog/browser changes. A new guide UI test was added for the expanded flow; its result is recorded in `docs/QA_REPORT.md`.
- **Phase 2 work:** CSV/JSON candidate import, canonical URL checks, duplicate detection, review queue, evidence-gated publication, and pipeline tests. Five additional official-source-backed portals were published (nine total); three candidates remain in review because live destinations failed or timed out. No unattended crawling or nationwide import is claimed.
- **Phase 3 work:** deterministic all-term search, curated synonyms, typo tolerance, and level filtering in domain logic; unsupported needs return no results. Full regional catalog, hierarchy, organization browsing, and service-result UI remain open.
- **Phase 4 work:** two dated, official-source-backed guides render on the native companion page; other records show an honest missing-guide message.
- **Phase 5 work:** a host-limited Android WebView path was added for the National Portal, with HTTPS policy, TLS cancellation, restricted settings, external fallback, and unit tests. A separate opt-in Chrome side-panel extension provides local search and guides beside listed sites. Its later Readable text pilot applies a reviewed, reversible stylesheet to the National Scholarship Portal home page. Browser compatibility and live-page behavior still need device/browser QA; no server-speed claim is made.
- **Phases 6–9:** not complete. Local review/publication handles the current small catalog; a backend is not justified yet. Localization, comprehensive QA, and release documentation remain work in progress.

### Work session — 12 October 2026 (Phase 1 in progress)

- **Status:** Foundation implemented, but Phase 1 remains unchecked because Android compilation and UI verification could not run without an Android SDK.
- **Files/modules changed:** New Gradle/Android project, Compose screens and theme, catalog model/parser/repository, local settings, bundled catalog, unit/UI test sources, catalog checker, README, and `docs/` architecture, source, coverage, QA, and limitations notes.
- **Working in source:** First-run language/state preferences, home, search, categories, portal details, favorites, recent views, settings, and external-browser handoff. Four manually source-checked central portals are bundled; no service records or nationwide coverage is claimed.
- **Commands/results:** Gradle 8.13 wrapper generation passed; `gradlew.bat help` passed; `gradlew.bat :app:dependencies --configuration debugCompileClasspath` passed; `python tools/check_catalog.py` passed (4 verified websites, 4 unique domains, 4 categories, 0 services). `gradlew.bat :app:testDebugUnitTest :app:assembleDebug` stopped before compilation with `SDK location not found`. A first wrapper attempt exposed a Kotlin 2.3 `kotlinOptions` configuration error; it was fixed and the task passed on rerun.
- **Acceptance criteria still unmet:** Android app compilation, unit and device UI test execution, and observed navigation/accessibility on a device.
- **Blocker/dependency:** Android SDK Platform 36, Build Tools 35.0.0+, and a device or emulator are not installed in this environment. See `docs/QA_REPORT.md`.
- **Next action:** Install the SDK with license acceptance, run the debug unit-test and APK tasks, fix any failures, then verify on a device and mark Phase 1 complete before beginning the Phase 2 ingestion pipeline.

### Work session — 13 October 2026 (Phase 1 build verified; device verification pending)

- **Status:** Android SDK installed with user authorization. The app and Android UI test APKs compile; three unit tests pass; lint has zero errors. Phase 1 stays unchecked because no phone or emulator was connected to verify the UI flows.
- **Files/modules changed:** Escaped the Kotlin `in` package segment in source and tests; renamed `LocalSettings` update methods to avoid JVM setter clashes; fixed the UI test import; moved an API 27 navigation bar setting into `values-v27`; added APK installation instructions to `README.md`; updated `docs/QA_REPORT.md` and this checklist. Local `local.properties` is gitignored.
- **Working in build:** Debug APK at `app/build/outputs/apk/debug/app-debug.apk` (19,671,526 bytes), package `in.jansetu.app`, minimum Android 8.0. The Android UI test APK also builds.
- **Commands/results:** `gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug --console=plain` passed; 3 unit tests, 0 failures; lint 0 errors and 15 warnings. `python tools/check_catalog.py` passed with 4 verified websites and 4 unique domains. `aapt dump badging` confirmed APK identity. `adb devices` found no device.
- **Acceptance criteria still unmet:** Observed onboarding, home/navigation, search, accessibility, and external browser handoff on Android hardware or an emulator. The compiled UI test has not executed.
- **Blocker/dependency:** No Android device or emulator available in this environment. See `docs/QA_REPORT.md` for exact verification steps.
- **Next action:** Connect a phone or emulator, run `gradlew.bat :app:connectedDebugAndroidTest`, inspect main flows, then mark Phase 1 complete and begin Phase 2 ingestion.

### Work session — 13 October 2026 (emulator launch and UI check)

- **Status:** Android Studio created the `Medium_Phone_API_37.0` AVD. JanSetu installed and launched on it; onboarding, Home, Search, and a portal detail were observed. Home screenshot: `docs/screenshots/home.png`. Phase 1 remains unchecked.
- **Files/modules changed:** README emulator instructions, screenshot, `docs/QA_REPORT.md`, `docs/KNOWN_LIMITATIONS.md`, and this checklist. User environment now includes `ANDROID_HOME` and Android SDK command paths so the installed VS Code emulator extension can find them after a full VS Code restart.
- **Commands/results:** `emulator -accel-check` reported WHPX usable; `adb install -r` returned `Success`; `adb shell am start` opened the app. `gradlew.bat :app:connectedDebugAndroidTest --console=plain` ran one test, which failed before its assertion with `NoSuchMethodException: android.hardware.input.InputManager.getInstance` on Android 37. The app was reinstalled and returned to Home after the test task removed it.
- **Acceptance criteria still unmet:** A passing connected UI test, large-font and TalkBack inspection, dark theme, and full browser navigation to an official site.
- **Next action:** Use a compatible Android system image or update the test framework, rerun the connected test, then complete remaining device checks before marking Phase 1 complete.

For each phase, record:
- Date/status
- Files or modules changed
- Features actually completed
- Commands/tests actually run and their results
- Acceptance criteria still unmet
- Blockers and dependencies
- Next action

Do not mark blocked or partially implemented features as complete.

---

## Required final report

At the end of a phase or work session, provide a concise report with:

1. What changed.
2. What works end to end.
3. Files/modules changed.
4. Commands run and actual build/test results.
5. Actual catalog counts and verification breakdown, where applicable.
6. Security/privacy changes.
7. Known defects, blockers, and unverified behavior.
8. Exact next steps.

**Start now:** inspect the repository and environment, identify the current phase from the checklist and actual code, and implement that phase. Do not stop after giving a plan.


### Phase 2B work session - 15 October 2026

- Implemented and ran deterministic queue triage, evidence-backed alias resolution, prioritized review exports, stronger source-content/redirect/freshness gates, incremental source-change reports and administrative reconciliation.
- Examined all 3,902 original candidates; resolved 79 (77 local directory admissions and two preserved aliases). Pending 3,823; zero ownership certifications or rejections. Catalog 7,347 websites, nine curated unchanged, zero individual services. All 7,270 Phase 2A records remain unchanged.
- Acquired authoritative LGD state IDs and state-wise district/urban/panchayat totals for all 28 states/eight UTs. Lower-level district/local-body IDs and histories remain partial because the official forms require CAPTCHA; no website URL was invented and no full administrative website coverage claimed.
- Final checks: 53 Python, 11 JVM, six Node and five API 37 tests passed; Chrome panel smoke, lint, debug and unsigned release builds passed. Initial Java process-launch failure and a fixture failure/recovery are recorded in QA_REPORT.md.
- Android/Chrome keep original layout/host permissions. A newer inconclusive destination check has its own label and cannot erase earlier admission evidence. No government server-speed or broad redesign claim is introduced.
- Reports: docs/REVIEW_QUEUE_REPORT.md, docs/ADMINISTRATIVE_COVERAGE_RECONCILIATION.md, docs/CATALOG_COVERAGE.md, docs/DISCOVERY_RUN_REPORT.md, docs/QA_REPORT.md. Evidence: data/collection/phase2b-2026-10-15/.
- Status: local review pipeline implemented and exercised; lower-level administrative matching remains partial pending a permitted official export. Production publication and later phases remain open.


### Local application completion work — 16 October 2026

- Implemented compact runtime packaging without changing authoritative website records; retained every ID, URL, source/status/date and browser permission. Added category groups and separate Hindi JanSetu explanations.
- Implemented versioned ECDSA-signed offline catalog import via Android document picker, bounded validation, app-private atomic replacement, restart persistence, invalid-cache fallback and confirmed restore. The development private key remains ignored and is never packaged.
- Completed recent query/history controls, lazy saved lists, exact organization browsing, category hierarchy, Hindi guides/privacy and clearer metadata limitations. Browser auth/payment paths hand off externally; upload requests are cancelled with guidance, and WebView follows activity lifecycle. Chrome removes its packaged CSS after leaving the reviewed route.
- Added a provider-neutral integration schema/adapter/failure framework. No government API is enabled, no transaction/status is simulated and official website actions remain the available flow.
- App version is 0.2.0/code 2; data counts remain 7,347 websites, 7,338 directory listings, nine reviewed, zero services and two guides. Version-9 signed update demonstrates the same data, not a new collection pass.
- Architecture, local operation, compatibility, privacy, release checklist and store draft updated. Actual tests, failures/recoveries, screenshots and artifact hashes are recorded in the latest QA section and validation report.
- Local development continues independently of public release. NIC/public reuse, permitted lower-unit exports, independent review/translation, live integrations and unverified device/browser/security gates remain explicitly open. No upload or deployment occurred.


### Phase 3A — verified service information and citizen-intent search, 16 October 2026

- Implemented the request retained in `docs/PHASE3A_REQUEST.md`. Before-edit source and website-review hashes are in `data/validation_reports/phase3a-baseline.json`; all 7,347 website objects, all nine manual website reviews and the 3,823-case queue are preserved byte-for-byte.
- Added a separate 25-record service catalog, eight exact website-operator relationships, fact-level source references, explicit null unknowns, jurisdiction and application-mode gates. Nine entries concern Tamil Nadu delivery; 14 eligibility fields and 19 document lists remain unknown. Four unresolved operations/routes remain separate. Source review is attributed to Codex, not falsely described as human certification or completed government transactions.
- Extended the existing index and lazy/background search with service results, conversational normalization, curated intents, type filtering and spelling tolerance. Exact portal names retain the portal result; unsupported service coverage remains explicit. Service pages show requirements/source dates/official links, and assisted or unconfirmed routes have no Apply button.
- Local editorial workflow demonstrates import → validate → named/digested source review → publish → signed runtime delivery. Two clarifications were published as version-11 revisions without erasing prior decisions; repeated pilot publication admits zero records. App 0.3.0/code 3; signed demonstration update version 12 includes all 25 services.
- No backend, paid AI, account, application tracker or government status retrieval added. The future manual-tracker/privacy/integration boundary is documented separately.
- Deliverables: `docs/SERVICE_CATALOG_REPORT.md`, `docs/SEARCH_QA_REPORT.md`, `docs/APPLICATION_TRACKING_BOUNDARY.md`, service data/editorial tools, Kotlin models/index/UI and Python/JVM/Android tests. Actual commands, timings, failures and final artifact/test totals are in the latest QA section and `data/validation_reports/phase3a-verification.json`.
- Remaining: broader regional/service coverage, complete facts and application-route review, language review, current scheme windows and real transaction/browser compatibility. Existing permission, production-signing and publication gates remain; nothing was deployed.


### Phase 3B ? local My Applications, 16 October 2026

- Added a separate local-profile repository, encrypted SQLite application/history tables, additive migration and non-destructive corruption handling. No account, backend or cloud sync.
- My Applications navigation supports manual and confirmed-online-service entries, user-maintained statuses/history, optional references/dates/notes, filtering/sorting, editing and confirmed individual/all deletion. Existing Phase 3A catalog/review data remains unchanged.
- Optional generic local deadline reminders use inexact alarms, notification permission/channel checks, unique replacement/cancellation and persistent delivery guards. Boot/timezone/clock/app resume reconcile future reminders. Scheduling/posting does not confirm delivery.
- App 0.4.0/code 4. Implementation/privacy/sync boundary: `docs/APPLICATION_TRACKER_REPORT.md` and `docs/APPLICATION_TRACKING_BOUNDARY.md`. Actual tests and display checks: latest `docs/QA_REPORT.md` section.
- Remaining: broader service/region coverage, complete tracker translation/accessibility and physical-device/OEM scheduling checks; publication/reuse permission, production signing/store gates remain. No government live-status integration added.


### Phase 3C - service coverage, verification and data quality, 16 October 2026

- Implemented the supplied request retained in `docs/PHASE3C_REQUEST.md`, following the pre-implementation audit/plan and retained baseline snapshot.
- Investigated 54 additional concrete services through targeted official sources. Published 27, bringing the total to 52: 20 central and 32 regional services in five states/UTs. No district/local service is published; two Shimla operations are explicitly pending local candidates.
- Rechecked all four older pending operations and retained them with specific reasons and full prior review history. PMEGP alone has an explicit reviewed revision: canonical normal TLS works, but the fresh-intake notice prevents an Apply action. All original IDs and 24 other service objects remain unchanged; prior publication decisions remain intact.
- Added sourced optional applicant-group and application-route evidence, stronger URL/jurisdiction/duplicate gates, digest-bound pending amendments, whole-collection pre-publication validation and idempotent replay. Unknown eligibility/documents remain null (37/23 respectively); populated lists may be partial.
- Generated `docs/SERVICE_COVERAGE_MATRIX.md`, `docs/SERVICE_GAPS_REPORT.md`, current catalog/search reports and machine-readable coverage from actual published records and the research ledger. General portal presence does not count as service coverage.
- Protected all 7,347 authoritative websites and the 3,823-case website queue byte-for-byte. My Applications encryption/schema, user-maintained status boundary and personal records remain separate from catalog updates. Signed update/restore regressions include reference, dates, reminder preferences and two history events.
- App 0.4.1/code 5; bundled runtime/service version 13 and signed local demonstration version 14. Runtime assets and Chrome companion data use the same validated catalog. No UI overhaul, new browser styling profile, backend, cloud sync or live government-status integration.
- Actual final tests, artifact hashes, device inspection and limitations are recorded in the latest `docs/QA_REPORT.md` section and `data/validation_reports/phase3c-verification.json`. Pending: 31 service reviews, wider geography/categories, independent source/language/accessibility review, real authenticated transactions and existing production/distribution requirements. No public deployment or upload.


### Phase 4 - end-to-end quality, reliability and accessibility hardening, 17 October 2026

- Replayed Phase 3C as an isolated `in.jansetu.app.qa` baseline, then ran the final QA gate with 71 Python, seven extension, 38 JVM and 34 API 37 Android tests, all passing with zero failures/errors/skips. Lint has zero errors/fatals; 22 existing warnings remain.
- Added a QA build type and fresh-XML/native-exit test harness. It keeps instrumentation files, preferences, alarms and Keystore separate from `in.jansetu.app`, then hashes the preview database/preferences/alarm registry/catalog file before and after testing. The preview profile was unchanged.
- Fixed browser sensitive-journey canonicalization and HTTP error feedback; key-loss reads preserve ciphertext without minting replacement keys; saved application records hide no-longer-confirmed application routes while retaining evidence/history; 2x text short-viewport tracker reachability; and citizen-facing Union Territory filtering for Delhi/Puducherry without data rewrites.
- Reports and repeatable manual checks: `docs/PHASE4_QUALITY_REPORT.md`, `docs/PHASE4_MANUAL_CHECKLIST.md`, `docs/PHASE4_RECOVERY_REVIEW.md`, `data/validation_reports/phase4/` and `tools/run_phase4_quality.py`.
- Remaining release gates are human spoken TalkBack/physical-keyboard review, real-device/OEM reminder observations, reboot/Doze/timezone coverage, live safe-browser fixture checks, broader device/locale review, data reuse permission, production signing and publication requirements. No government transaction, credential/OTP collection, deployment or public upload occurred.
