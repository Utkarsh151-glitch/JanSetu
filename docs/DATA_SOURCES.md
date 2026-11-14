# Manually reviewed baseline sources

The first four records were checked on 12 October 2026. Five further records were reviewed against official pages on 13 October 2026. Publication uses a portal's own ownership/scope statement or an attributable official page, never an HTTP status alone:

| Record | Official site | Attribution source | Finding |
| --- | --- | --- | --- |
| National Portal of India | https://www.india.gov.in/ | https://www.india.gov.in/ | Footer identifies NIC, MeitY, Government of India. |
| DigiLocker | https://www.digilocker.gov.in/ | https://www.digilocker.gov.in/ | About and footer identify NeGD, MeitY, Government of India. |
| CPGRAMS | https://pgportal.gov.in/ | https://pgportal.gov.in/ | About/footer identify DARPG as content owner and NIC as host. |
| National Career Service | https://ncs.gov.in/ | https://ncs.gov.in/about-us | About page identifies Ministry of Labour and Employment, Government of India. |
| National Scholarship Portal | https://scholarships.gov.in/ | https://scholarships.gov.in/aboutUs | Official about page describes scholarship application and processing functions. The portal itself names multiple scheme ministries; its catalog organization is the portal rather than an unverified single owner. |
| Udyam Registration | https://udyamregistration.gov.in/ | https://udyamregistration.gov.in/ | Home page explicitly identifies Ministry of MSME and warns against unofficial registration sites. |
| Income Tax e-Filing | https://www.incometax.gov.in/iec/foportal/ | https://www.incometax.gov.in/iec/foportal/ | Footer identifies Income Tax Department, Ministry of Finance, Government of India. |
| RTI Online | https://rtionline.gov.in/ | https://rtionline.gov.in/ | Home identifies Department of Personnel & Training and says the portal is for central public authorities, not state authorities. |
| UIDAI Aadhaar Services | https://uidai.gov.in/en/my-aadhaar | https://uidai.gov.in/en/my-aadhaar | UIDAI's official My Aadhaar page lists service routes; authority site identifies UIDAI under MeitY. |

At the earlier nine-record checkpoint, PM-KISAN, Parivahan Sewa and eCourts were held after destination failures/timeouts. Current decisions and later source/health evidence are retained in `data/review_queue.json`; manual ownership/URL decisions are not erased by collection.

## Manual verification rule

An editor must check the live HTTPS destination and an authoritative ownership statement, record the canonical URL and source, and date the check. These nine baseline records have `verificationStatus=verified`. The app parser requires HTTPS, source attribution, valid categories and unique IDs; these manually reviewed records also require a verification date. This is a manual starter process; it is **not** a recurring link or content audit. Do not add a record solely because its domain ends in `.gov.in`, and do not treat an HTTP 200 as ownership proof.

The descriptions summarize the source pages. Two plain-language guides now summarize steps from DigiLocker's [FAQ](https://wb.digilocker.gov.in/web/about/faq) and CPGRAMS' [grievance page](https://pgportal.gov.in/Home/LodgeGrievance), reviewed on 13 October 2026. No eligibility, fees, or deadlines are asserted. Portal content and availability can change after verification.

## Phase 2A local directory evidence

The expanded catalog also supports `verificationStatus=directory_listed` with `publicationScope=local_development`, not a fabricated manual verification date. Exact IGOD snapshots, category pages, retrieval times and separate destination checks support these additions. See [DATA_VERIFICATION.md](DATA_VERIFICATION.md), [source registry](../data/source_registry.json) and [DISCOVERY_RUN_REPORT.md](DISCOVERY_RUN_REPORT.md). NIC reproduction permission is pending before public distribution. New catalog links grant no additional CSS or WebView access.
