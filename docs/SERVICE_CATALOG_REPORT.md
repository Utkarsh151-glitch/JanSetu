# Service catalog report — Phase 3C

Current snapshot: 16 October 2026; app 0.4.1/code 5; service/runtime catalog v13. Reproduce with `python tools/report_service_coverage.py`.

| Measure | Actual |
| --- | --- |
| Published services | 52 |
| New investigations / discovered | 54 / 54 |
| New published / held | 27 / 27 |
| Older pending cases rechecked / still pending | 4 / 4 |
| Explicit old-service revisions | 1 (PMEGP) |
| All pending service cases | 31 |
| Website operator registry / referenced by published services | 15 / 14 (one pending-only Himachal operator) |
| Unchanged websites / pending website cases | 7,347 / 3,823 |
| Unknown eligibility / documents | 37 / 23 |

See [coverage](SERVICE_COVERAGE_MATRIX.md), [evidence gaps](SERVICE_GAPS_REPORT.md), [search QA](SEARCH_QA_REPORT.md) and [build/test QA](QA_REPORT.md) for scope and results. Verified means reviewed official information, not a completed transaction, confirmed personal eligibility or a current application window.

## Reproduce the editorial snapshot

```powershell
python tools/prepare_phase3c.py
python tools/service_catalog.py amend-pending data/services/phase3c-pending-reviews.json
python tools/service_catalog.py import data/services/phase3c-candidates.json --revision
python tools/service_catalog.py publish data/services/phase3c-approvals.json
python tools/service_catalog.py check
python tools/report_service_coverage.py
python tools/package_extension.py
python tools/package_catalog.py update --version 14 --output artifacts/catalog-v14.jansetu
```

These replay retained dated research and decisions; they do not refresh a source. Replay preserves published decisions and is idempotent. For a new review date, retrieve explicit official URLs with normal TLS into a new dated research batch, inspect their actual content/redirects, stage new or revised drafts and approve exact candidate digests with named reviewer/notes. Unresolved stubs use a digest-bound amendment preserving their previous decision/history. New services require explicit route evidence; null application URLs remain information-only. Whole-collection validation runs before either publication file is replaced; writes are individually atomic, not a cross-file database transaction. Back up editorial files before a new batch.

## Published service index

| Service ID | Jurisdiction | Mode | Department | Official information |
| --- | --- | --- | --- | --- |
| aicte-pragati-degree | Central | online | All India Council for Technical Education | [Source](https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2010_G.pdf) |
| aicte-saksham-degree | Central | online | All India Council for Technical Education | [Source](https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2012_G.pdf) |
| aicte-swanath-degree | Central | online | All India Council for Technical Education | [Source](https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_3039_G.pdf) |
| tn-community-certificate | Tamil Nadu | assisted | Revenue Administration, Government of Tamil Nadu | [Source](https://serviceonline.gov.in/configuretn/login.do) |
| tn-nativity-certificate | Tamil Nadu | assisted | Revenue Administration, Government of Tamil Nadu | [Source](https://serviceonline.gov.in/configuretn/login.do) |
| tn-income-certificate | Tamil Nadu | assisted | Revenue Administration, Government of Tamil Nadu | [Source](https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx) |
| tn-birth-certificate-print | Tamil Nadu | assisted | Directorate of Public Health, Government of Tamil Nadu | [Source](https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx) |
| tn-old-age-pension | Tamil Nadu | assisted | Revenue Administration, Government of Tamil Nadu | [Source](https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx) |
| tn-widow-pension | Tamil Nadu | assisted | Revenue Administration, Government of Tamil Nadu | [Source](https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx) |
| tn-disability-pension | Tamil Nadu | assisted | Revenue Administration, Government of Tamil Nadu | [Source](https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx) |
| tn-unemployment-certificate | Tamil Nadu | assisted | Revenue Administration, Government of Tamil Nadu | [Source](https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx) |
| tn-legal-heir-certificate | Tamil Nadu | assisted | Revenue Administration, Government of Tamil Nadu | [Source](https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx) |
| ncs-government-job-search | Central | online | Directorate General of Employment, Ministry of Labour & Employment | [Source](https://ncs.gov.in/faq) |
| ncs-jobseeker-registration | Central | online | Directorate General of Employment, Ministry of Labour & Employment | [Source](https://ncs.gov.in/assets/pdf/Jobseeker%20registration%20flowchart%201.pdf) |
| ncs-career-counselling | Central | online | Directorate General of Employment, Ministry of Labour & Employment | [Source](https://ncs.gov.in/faq) |
| aadhaar-download | Central | information | Unique Identification Authority of India | [Source](https://www.uidai.gov.in/en/my-aadhaar) |
| aadhaar-address-update | Central | information | Unique Identification Authority of India | [Source](https://www.uidai.gov.in/en/my-aadhaar) |
| aadhaar-pvc-card | Central | information | Unique Identification Authority of India | [Source](https://www.uidai.gov.in/en/my-aadhaar) |
| aadhaar-biometric-lock | Central | information | Unique Identification Authority of India | [Source](https://www.uidai.gov.in/en/my-aadhaar) |
| cpgrams-public-grievance | Central | online | Department of Administrative Reforms and Public Grievances | [Source](https://pgportal.gov.in/Home/Faq) |
| cpgrams-grievance-appeal | Central | online | Department of Administrative Reforms and Public Grievances | [Source](https://pgportal.gov.in/Home/Faq) |
| rti-central-request | Central | online | Department of Personnel and Training | [Source](https://rtionline.gov.in/guidelines.php?request) |
| rti-first-appeal | Central | online | Department of Personnel and Training | [Source](https://rtionline.gov.in/guidelines.php?request) |
| udyam-new-registration | Central | online | Ministry of Micro, Small and Medium Enterprises | [Source](https://udyamregistration.gov.in/) |
| pmegp-enterprise-support | Central | information | Ministry of MSME / Khadi and Village Industries Commission | [Source](https://common-pmegp.msme.gov.in/) |
| dl-income-certificate | Delhi | online | Revenue Department, Government of NCT of Delhi | [Source](https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=riKGp6qbpouao2hGbLw4dJUtjAFtwDFpZutrIuogId0=) |
| dl-domicile-certificate | Delhi | online | Revenue Department, Government of NCT of Delhi | [Source](https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=GDX0anvCbFI+Ydkd0X4TfOMzCufU1wuuYbY+d/MGkho=) |
| dl-obc-certificate | Delhi | online | Revenue Department, Government of NCT of Delhi | [Source](https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=JB43Aj/g9K8d1RKt88lGvwuLNCKqIbcj5IEnYbOVu3E=) |
| dl-sc-certificate | Delhi | online | Revenue Department, Government of NCT of Delhi | [Source](https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=lC/pNp5TPkzmQmDopE7xNpVTQ95OIoVTdMKB66fUUIU=) |
| dl-surviving-certificate | Delhi | online | Revenue Department, Government of NCT of Delhi | [Source](https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=jWdPxdcDpwWlm4OG+ipyvwLqhz7b9TKwPuPuOKYWVcM=) |
| kl-income-certificate | Kerala | online | Revenue Department, Government of Kerala | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-nativity-certificate | Kerala | online | Revenue Department, Government of Kerala | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-caste-obc-certificate | Kerala | online | Revenue Department, Government of Kerala | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-community-sc-st-certificate | Kerala | online | Revenue Department, Government of Kerala | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-domicile-certificate | Kerala | online | Revenue Department, Government of Kerala | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-family-membership-certificate | Kerala | online | Revenue Department, Government of Kerala | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-relationship-certificate | Kerala | online | Revenue Department, Government of Kerala | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-legal-heir-certificate | Kerala | online | Revenue Department, Government of Kerala | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| od-resident-certificate | Odisha | information | Revenue and Disaster Management Department, Government of Odisha | [Source](https://edistrict.odisha.gov.in/) |
| od-income-certificate | Odisha | information | Revenue and Disaster Management Department, Government of Odisha | [Source](https://edistrict.odisha.gov.in/) |
| od-sc-certificate | Odisha | information | Revenue and Disaster Management Department, Government of Odisha | [Source](https://edistrict.odisha.gov.in/) |
| od-st-certificate | Odisha | information | Revenue and Disaster Management Department, Government of Odisha | [Source](https://edistrict.odisha.gov.in/) |
| od-ews-certificate | Odisha | information | Revenue and Disaster Management Department, Government of Odisha | [Source](https://edistrict.odisha.gov.in/) |
| od-legal-heir-certificate | Odisha | information | Revenue and Disaster Management Department, Government of Odisha | [Source](https://edistrict.odisha.gov.in/) |
| od-obc-certificate | Odisha | information | Revenue and Disaster Management Department, Government of Odisha | [Source](https://edistrict.odisha.gov.in/) |
| od-sebc-certificate | Odisha | information | Revenue and Disaster Management Department, Government of Odisha | [Source](https://edistrict.odisha.gov.in/) |
| py-income-certificate | Puducherry | information | Revenue and Disaster Management Department, Government of Puducherry | [Source](https://revenue.py.gov.in/downloads/EODB/EODB-Income%20Certificate.pdf) |
| py-residence-certificate | Puducherry | information | Revenue and Disaster Management Department, Government of Puducherry | [Source](https://revenue.py.gov.in/downloads/EODB/EODB-Residence%20Certificate.pdf) |
| instant-epan-new | Central | information | Income Tax Department | [Source](https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/instant-e-pan/instant-UM?mobile-app=1) |
| instant-epan-download | Central | information | Income Tax Department | [Source](https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/instant-e-pan/instant-UM?mobile-app=1) |
| eci-new-elector-form6 | Central | information | Election Commission of India / Electoral Registration Officer | [Source](https://voters.eci.gov.in/formspdf/Form_6_English.pdf) |
| eci-elector-form8 | Central | information | Election Commission of India / Electoral Registration Officer | [Source](https://voters.eci.gov.in/formspdf/Form_8_English.pdf) |

The Android build merges the service catalog with the protected website directory and generates compact runtime assets. The Chrome companion receives the same merged data; its interface still lists portals/guides, without a new service/tracker UI. The limited readability profile is unchanged and cannot speed up government servers. My Applications remains encrypted, local and user-maintained; catalog updates do not write its database. No government login, OTP, cloud sync or live-status API was introduced.

The v14 signed file is a local update demonstration of the v13 content. Private signing material is excluded. Local development readiness is separate from distribution permission and release approval.
