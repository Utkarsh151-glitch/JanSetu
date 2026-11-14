# Service coverage matrix — Phase 3C

Snapshot: 16 October 2026, service catalog v13. Reproduce with `python tools/report_service_coverage.py`. Counts come from `data/services/catalog.json` and `phase3c-ledger.json`; all 36 administrative rows use the retained [LGD public state table](https://lgdirectory.gov.in/globalviewstateforcitizen.do), not portal counts.

## How to read the counts

Each additional candidate has a stable ID. Investigated = targeted source inspected; discovered = a distinct operation named in a readable current source; source-reviewed = the selected facts and route decision completed the publication review; published = an admitted service, never a general website. New columns cover this phase only; published totals include the 25 preserved baseline services. A held candidate may have partially reviewed evidence without a completed publication review. Source review does not mean a completed government transaction.

| Scope | New investigated | New discovered | New source-reviewed | Published total |
| --- | --- | --- | --- | --- |
| Central | 5 | 5 | 4 | 20 |
| All regional scopes | 49 | 49 | 23 | 32 |
| District | 0 | 0 | 0 | 0 |
| Local | 2 | 2 | 0 | 0 |

## States and union territories

Local candidates are counted under their parent state; this does not establish statewide service availability. UT service records use the backward-compatible `State` level plus canonical state/UT name; the administrative type below retains the UT distinction.

| State / UT | Type | New investigated | Discovered | Source-reviewed | Published total | Last published review |
| --- | --- | --- | --- | --- | --- | --- |
| Andaman And Nicobar Islands | UT | 0 | 0 | 0 | 0 | None |
| Andhra Pradesh | State | 0 | 0 | 0 | 0 | None |
| Arunachal Pradesh | State | 0 | 0 | 0 | 0 | None |
| Assam | State | 0 | 0 | 0 | 0 | None |
| Bihar | State | 0 | 0 | 0 | 0 | None |
| Chandigarh | UT | 0 | 0 | 0 | 0 | None |
| Chhattisgarh | State | 0 | 0 | 0 | 0 | None |
| The Dadra And Nagar Haveli And Daman And Diu | UT | 0 | 0 | 0 | 0 | None |
| Delhi | UT | 14 | 14 | 5 | 5 | 2026-10-16 |
| Goa | State | 0 | 0 | 0 | 0 | None |
| Gujarat | State | 0 | 0 | 0 | 0 | None |
| Haryana | State | 0 | 0 | 0 | 0 | None |
| Himachal Pradesh | State | 7 | 7 | 0 | 0 | None |
| Jammu And Kashmir | UT | 0 | 0 | 0 | 0 | None |
| Jharkhand | State | 0 | 0 | 0 | 0 | None |
| Karnataka | State | 0 | 0 | 0 | 0 | None |
| Kerala | State | 15 | 15 | 8 | 8 | 2026-10-16 |
| Ladakh | UT | 0 | 0 | 0 | 0 | None |
| Lakshadweep | UT | 0 | 0 | 0 | 0 | None |
| Madhya Pradesh | State | 0 | 0 | 0 | 0 | None |
| Maharashtra | State | 0 | 0 | 0 | 0 | None |
| Manipur | State | 0 | 0 | 0 | 0 | None |
| Meghalaya | State | 0 | 0 | 0 | 0 | None |
| Mizoram | State | 0 | 0 | 0 | 0 | None |
| Nagaland | State | 0 | 0 | 0 | 0 | None |
| Odisha | State | 11 | 11 | 8 | 8 | 2026-10-16 |
| Puducherry | UT | 2 | 2 | 2 | 2 | 2026-10-16 |
| Punjab | State | 0 | 0 | 0 | 0 | None |
| Rajasthan | State | 0 | 0 | 0 | 0 | None |
| Sikkim | State | 0 | 0 | 0 | 0 | None |
| Tamil Nadu | State | 0 | 0 | 0 | 9 | 2026-10-16 |
| Telangana | State | 0 | 0 | 0 | 0 | None |
| Tripura | State | 0 | 0 | 0 | 0 | None |
| Uttar Pradesh | State | 0 | 0 | 0 | 0 | None |
| Uttarakhand | State | 0 | 0 | 0 | 0 | None |
| West Bengal | State | 0 | 0 | 0 | 0 | None |

## Categories, modes and verification

Category totals count membership and can exceed the number of services. Zero means no published service in this dataset.

| Category | Published services |
| --- | --- |
| Documents & identity | 35 |
| Complaints & grievances | 2 |
| Jobs & careers | 3 |
| Government information | 0 |
| Education & scholarships | 3 |
| Business & registration | 2 |
| Taxes & finance | 2 |
| Information requests | 2 |
| Agriculture & farmers | 0 |
| Transport & licences | 0 |
| Courts & legal services | 0 |
| Health & family welfare | 0 |
| Welfare & benefits | 3 |
| Land, property & housing | 0 |
| Water, power & sanitation | 0 |
| Local government & civic services | 0 |
| Environment & forests | 0 |
| Industry, trade & commerce | 0 |
| Police, safety & disasters | 0 |

| Mode | Published |
| --- | --- |
| online | 24 |
| assisted | 9 |
| information | 19 |

| Verification status / date | Published |
| --- | --- |
| ('verified', '2026-10-16') | 52 |

## Documented applicant groups

Only sourced `beneficiaryGroups` facts count. Legacy title/eligibility text is not used to guess a group. These are overlapping service counts, not people or confirmed personal eligibility.

| Documented group | Services |
| --- | --- |
| Economically Weaker Sections (EWS) | 1 |
| Individual taxpayers | 1 |
| Other Backward Classes (OBC) | 3 |
| Scheduled Castes (SC) | 2 |
| Scheduled Castes / Scheduled Tribes (SC/ST) | 1 |
| Scheduled Tribes (ST) | 1 |
| Socially and Educationally Backward Classes (SEBC) | 1 |

Group metadata remains unknown for 42 services. Eligibility is unknown for 37; document lists are unknown for 23. Populated lists can be partial.

## Research trail and decisions

| Candidate | Jurisdiction | Decision | Official source |
| --- | --- | --- | --- |
| dl-income-certificate | Delhi | publish | [Source](https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=riKGp6qbpouao2hGbLw4dJUtjAFtwDFpZutrIuogId0=) |
| dl-domicile-certificate | Delhi | publish | [Source](https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=GDX0anvCbFI+Ydkd0X4TfOMzCufU1wuuYbY+d/MGkho=) |
| dl-obc-certificate | Delhi | publish | [Source](https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=JB43Aj/g9K8d1RKt88lGvwuLNCKqIbcj5IEnYbOVu3E=) |
| dl-sc-certificate | Delhi | publish | [Source](https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=lC/pNp5TPkzmQmDopE7xNpVTQ95OIoVTdMKB66fUUIU=) |
| dl-surviving-certificate | Delhi | publish | [Source](https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=jWdPxdcDpwWlm4OG+ipyvwLqhz7b9TKwPuPuOKYWVcM=) |
| dl-st | Delhi | pending | [Source](https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html) |
| dl-ews | Delhi | pending | [Source](https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html) |
| dl-ncl | Delhi | pending | [Source](https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html) |
| dl-marriage-registration | Delhi | pending | [Source](https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html) |
| dl-marriage-solemnization | Delhi | pending | [Source](https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html) |
| dl-disability-id | Delhi | pending | [Source](https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html) |
| dl-solvency | Delhi | pending | [Source](https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html) |
| dl-lal-dora | Delhi | pending | [Source](https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html) |
| dl-old-age-pension | Delhi | pending | [Source](https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html) |
| kl-income-certificate | Kerala | publish | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-nativity-certificate | Kerala | publish | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-caste-obc-certificate | Kerala | publish | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-community-sc-st-certificate | Kerala | publish | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-domicile-certificate | Kerala | publish | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-family-membership-certificate | Kerala | publish | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-relationship-certificate | Kerala | publish | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-legal-heir-certificate | Kerala | publish | [Source](https://edistrict.kerala.gov.in/generalService.htm?event=faq) |
| kl-possession-certificate | Kerala | pending | [Source](https://edistrict.kerala.gov.in/servicesNew.html) |
| kl-one-same-certificate | Kerala | pending | [Source](https://edistrict.kerala.gov.in/servicesNew.html) |
| kl-non-remarriage-certificate | Kerala | pending | [Source](https://edistrict.kerala.gov.in/servicesNew.html) |
| kl-valuation-certificate | Kerala | pending | [Source](https://edistrict.kerala.gov.in/servicesNew.html) |
| kl-dependency-certificate | Kerala | pending | [Source](https://edistrict.kerala.gov.in/servicesNew.html) |
| kl-non-creamy-certificate | Kerala | pending | [Source](https://edistrict.kerala.gov.in/servicesNew.html) |
| kl-conversion-certificate | Kerala | pending | [Source](https://edistrict.kerala.gov.in/servicesNew.html) |
| od-resident-certificate | Odisha | publish | [Source](https://edistrict.odisha.gov.in/) |
| od-income-certificate | Odisha | publish | [Source](https://edistrict.odisha.gov.in/) |
| od-sc-certificate | Odisha | publish | [Source](https://edistrict.odisha.gov.in/) |
| od-st-certificate | Odisha | publish | [Source](https://edistrict.odisha.gov.in/) |
| od-ews-certificate | Odisha | publish | [Source](https://edistrict.odisha.gov.in/) |
| od-legal-heir-certificate | Odisha | publish | [Source](https://edistrict.odisha.gov.in/) |
| od-obc-certificate | Odisha | publish | [Source](https://edistrict.odisha.gov.in/) |
| od-sebc-certificate | Odisha | publish | [Source](https://edistrict.odisha.gov.in/) |
| od-guardianship | Odisha | pending | [Source](https://edistrict.odisha.gov.in/) |
| od-solvency | Odisha | pending | [Source](https://edistrict.odisha.gov.in/) |
| od-ror | Odisha | pending | [Source](https://edistrict.odisha.gov.in/) |
| py-income-certificate | Puducherry | publish | [Source](https://revenue.py.gov.in/downloads/EODB/EODB-Income%20Certificate.pdf) |
| py-residence-certificate | Puducherry | publish | [Source](https://revenue.py.gov.in/downloads/EODB/EODB-Residence%20Certificate.pdf) |
| hp-income | Himachal Pradesh | pending | [Source](https://himseva.hp.gov.in/pages/staticSite/serviceList.xhtml) |
| hp-bonafide | Himachal Pradesh | pending | [Source](https://himseva.hp.gov.in/pages/staticSite/serviceList.xhtml) |
| hp-caste | Himachal Pradesh | pending | [Source](https://himseva.hp.gov.in/pages/staticSite/serviceList.xhtml) |
| hp-legal-heir | Himachal Pradesh | pending | [Source](https://himseva.hp.gov.in/pages/staticSite/serviceList.xhtml) |
| hp-unemployment | Himachal Pradesh | pending | [Source](https://himseva.hp.gov.in/pages/staticSite/serviceList.xhtml) |
| shimla-water-connection | Himachal Pradesh | pending | [Source](https://himseva.hp.gov.in/pages/staticSite/serviceList.xhtml) |
| shimla-sewer-connection | Himachal Pradesh | pending | [Source](https://himseva.hp.gov.in/pages/staticSite/serviceList.xhtml) |
| instant-epan-new | Central | publish | [Source](https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/instant-e-pan/instant-UM?mobile-app=1) |
| instant-epan-download | Central | publish | [Source](https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/instant-e-pan/instant-UM?mobile-app=1) |
| eci-new-elector-form6 | Central | publish | [Source](https://voters.eci.gov.in/formspdf/Form_6_English.pdf) |
| eci-elector-form8 | Central | publish | [Source](https://voters.eci.gov.in/formspdf/Form_8_English.pdf) |
| eci-overseas-form6a | Central | pending | [Source](https://voters.eci.gov.in/formspdf/Form_6A_English.pdf) |

No nationwide coverage or active application window is implied. District/local records are not published yet. Directory reuse permission and production release checks remain separate from local development.
