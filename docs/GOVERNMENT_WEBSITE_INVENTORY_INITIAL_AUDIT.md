# Government website inventory — controlled first pass

**Checked 14 October 2026.** This is a retained local sample, not a count of Indian government websites. Run `python tools/inventory_audit.py` to validate its provenance and `python tools/catalog_pipeline.py report` for reproducible counts.

## Scope and sources

The intended scope includes Union bodies and citizen services, all states and UTs, districts, local government, judiciary, legislatures, regulators, public institutions and officially linked external operators. A website, its actual landing page and each individual service are separate entities. The current production app has **nine websites and zero individual service records**.

The structured [source registry](../data/source_registry.json) records owner, access method, date, limits and automation status for the sources below.

| Source | Finding and access decision |
| --- | --- |
| [IGOD](https://igod.gov.in/about_us) | NIC describes coverage across Union, state/UT, district, legislature, judiciary, local bodies and sectors. Specific indexed pages supplied six new candidates. Live IGOD requests sometimes failed TLS validation or returned 502 here, so no bulk collection was attempted. Automated reuse terms remain unconfirmed. |
| [National Portal web directory](https://www.india.gov.in/directory/web-directory) | The page says its directory data comes from IGOD. Static views showed loading/zero counts. A single read-only Chrome check of its state/UT page returned 403; the dynamic data mechanism and complete result set remain unconfirmed. Its [robots file](https://www.india.gov.in/robots.txt) allowed paths with a 10-second crawl delay, which is not a reuse license. |
| [National Portal services](https://www.india.gov.in/services) | Current service discovery surface. Static output showed zero while the interactive home advertises far more; neither was imported as a measured inventory. |
| [Former services portal](https://services.india.gov.in/) | The root redirected to `india.gov.in/services` during this check. Its [historical notice](https://services.india.gov.in/page/show/about_us/en) and archived pages indicate migration/discontinuation effective May 2026. Old service URLs are not treated as current application links. |
| Individual official portals | Existing nine records keep their specific official `sourceUrl`. New Lakshadweep and municipal candidates retain both directory evidence and destination observations. |

No undocumented API, login bypass, certificate exception, uncontrolled crawl or assumed scraping permission was used. `tools/inspect_directory.py` performs one read-only request and reports request paths only; it found a 403 here, not a reliable import endpoint. `data/discovery_batch_2026-10-14.json` is a small manually attributed example batch.

## Retained records

| Measure | Count |
| --- | ---: |
| Published, source-attributed websites | 9 |
| New directory-linked unpublished candidates | 6 |
| Earlier unpublished candidates | 3 |
| Total retained websites/candidates | 18 |
| Published individual services | 0 |
| Rejected records retained | 0 |

The six new candidates cover Madhya Pradesh (state and district), Tamil Nadu (municipal) and Lakshadweep (UT). The earlier three are PM-KISAN, Parivahan and eCourts. The [review queue](../data/review_queue.json) records status and history; **none of the six new links was published to the Android app or Chrome extension**. The Greater Chennai Corporation link in IGOD opened a default Tomcat page, so it is `NEEDS_URL_REVIEW`. Timeouts for MP portal links are `TEMPORARILY_UNREACHABLE`, not proof that those sites are broken. Coimbatore remains in ownership/partner review; Lakshadweep remains discovered pending editorial publication checks.

## Published catalog classification

The machine-readable [profiles](../data/portal_profiles.json) separate jurisdiction, primary sector, purpose, root, landing page and owner/operator uncertainty. These are editorial classifications, not claims that every listed transaction works.

| Portal | Level | Primary sector | Main purpose | Uncertainty |
| --- | --- | --- | --- | --- |
| National Portal of India | Union | Government information | Information and service directory | Content belongs to respective ministries. |
| DigiLocker | Union | Identity/civil records | Authenticated document retrieval | Separate operator detail not established here. |
| CPGRAMS | Union | RTI/grievance | Grievance application and status | Authenticated journeys not audited. |
| National Career Service | Union | Employment/pensions | Jobs, guidance and account services | Transaction-level scope needs review. |
| National Scholarship Portal | Union | Education/scholarships | Discovery, application and status | Scheme ministries vary; single portal owner needs confirmation. |
| Udyam Registration | Union | Business/MSME | Registration and information | Application journey not audited. |
| Income Tax e-Filing | Union | Taxation | Filing, account and payment-related services | Sensitive journeys not audited. |
| RTI Online | Union | RTI/grievance | Application, payment and status | Central authorities only; state RTI must use another route. |
| UIDAI Aadhaar Services | Statutory/public institution, Union jurisdiction | Identity/civil records | Information and account-dependent records | Individual subservices may use other hosts. |

The [coverage report](CATALOG_COVERAGE.md) gives exact level, sector, source and state/UT counts. There are **zero published state/UT, district and municipal portals**; all 36 states/UTs lack a published local portal. Health, agriculture, land, transport, utilities and several other sectors lack a published primary-sector record. Even represented sectors have sparse coverage. Candidate presence does not close a publication gap.

## Maintenance rule

Use official source evidence to stage candidates, record the source URL and retrieval date, normalize HTTPS URLs and retain duplicates for review. An editor must independently confirm the destination, owner and operating organization, taxonomy and citizen-facing page before approving publication. An HTTP response, directory listing or `.gov.in` suffix alone is insufficient. Keep corrections and review history. Do not import the older services portal's counts or links as current services. See [data verification](DATA_VERIFICATION.md) and [manual UX checklist](UX_AUDIT_MANUAL_CHECKLIST.md).
