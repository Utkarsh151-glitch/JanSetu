# Phase 2A live collection run

Batch `igod-2026-10-14`, adapter/manifest version 1. Generated 2026-10-14T19:15:44+00:00.

This is a real, cached public-source integration run. Fixture tests are reported separately. The bundled additions are for **local development only**, following the user's instruction; NIC reproduction permission is required before public distribution. A directory listing and an HTTP response are not manual ownership or service verification.

| Measure | Actual |
| --- | ---: |
| Raw listing rows, including category previews/repeated listings | 23169 |
| Distinct source organization identities (uncertain matches retained) | 20946 |
| Unique normalized source URLs before redirect equivalence | 11168 |
| Distinct source website hosts | 10825 |
| Source URLs matching old catalog landing URLs | 6 |
| Existing curated website records preserved | 9 |
| New locally bundled website records | 7261 |
| Source/health-qualified URLs before editor decisions and uniqueness gates | 7342 |
| Current bundled websites | 7270 |
| Current individual services | 0 |
| Retained unpublished website candidates | 3902 |
| Source organizations lacking a valid website URL | 2356 |
| Suspected organization duplicate groups | 29 |
| Rejected / ownership-review / URL-review queue records | 0 / 1166 / 102 |
| Newly confirmed permanently retired destinations | 0 (errors are inconclusive; no automatic retirement) |
| Observed safe redirect-equivalence groups / aliases | 4 / 5 |
| IGOD listing categories discovered / count-reconciled | 462 / 461 |
| Total pages discovered / processed / pending / failed | 3586 / 3586 / 0 / 0 |

Raw rows are not website counts. One organization can have no URL or multiple URLs, and many sector/category listings repeat the same organization. Exact URLs are normalized without inventing HTTPS; observed safe redirects are recorded separately. Source organization IDs anchor cross-directory matches. Same-name/different-URL cases remain review groups. `organizations.json`, `website_entities.json` and normalized landing-page records distinguish organization, website host and URL; no fictitious service entities are generated. The 15-row New Additions view exposes no total/history export or pagination, so only its observed rows are claimed.

## Per-state/UT coverage

IGOD codes below are source navigation codes. No LGD administrative identifiers were guessed. A visited state homepage does not mean its directory is complete.

| State/UT | IGOD code | Branch enumerated | Complete / listing categories | Normalized URLs | Bundled | District names bundled |
| --- | --- | --- | ---: | ---: | ---: | ---: |
| Andaman and Nicobar Islands | AN | Yes | 7 / 7 | 38 | 11 | 3 |
| Andhra Pradesh | AP | Yes | 14 / 14 | 203 | 134 | 26 |
| Arunachal Pradesh | AR | Yes | 10 / 10 | 103 | 73 | 21 |
| Assam | AS | Yes | 14 / 14 | 248 | 198 | 18 |
| Bihar | BR | Yes | 13 / 13 | 210 | 113 | 38 |
| Chandigarh | CH | Yes | 9 / 9 | 66 | 48 | 1 |
| Chhattisgarh | CG | Yes | 12 / 12 | 143 | 91 | 28 |
| Dadra and Nagar Haveli and Daman and Diu | ND | Yes | 6 / 6 | 40 | 35 | 2 |
| Delhi | DL | Yes | 13 / 13 | 219 | 36 | 8 |
| Goa | GA | Yes | 9 / 9 | 102 | 58 | 2 |
| Gujarat | GJ | Yes | 13 / 13 | 371 | 223 | 29 |
| Haryana | HR | Yes | 13 / 13 | 201 | 139 | 21 |
| Himachal Pradesh | HP | Yes | 13 / 13 | 128 | 58 | 12 |
| Jammu and Kashmir | JK | Yes | 11 / 11 | 148 | 67 | 20 |
| Jharkhand | JH | Yes | 10 / 10 | 109 | 80 | 24 |
| Karnataka | KA | Yes | 11 / 11 | 179 | 114 | 30 |
| Kerala | KL | Yes | 14 / 14 | 288 | 169 | 14 |
| Ladakh | LA | Yes | 6 / 6 | 47 | 44 | 2 |
| Lakshadweep | LD | Yes | 6 / 6 | 22 | 14 | 1 |
| Madhya Pradesh | MP | Yes | 14 / 14 | 235 | 149 | 53 |
| Maharashtra | MH | Yes | 14 / 14 | 299 | 194 | 33 |
| Manipur | MN | Yes | 10 / 10 | 93 | 58 | 14 |
| Meghalaya | ML | Yes | 12 / 12 | 118 | 88 | 11 |
| Mizoram | MZ | Yes | 8 / 8 | 95 | 84 | 8 |
| Nagaland | NL | Yes | 7 / 7 | 67 | 54 | 15 |
| Odisha | OD | Yes | 14 / 14 | 239 | 67 | 0 |
| Puducherry | PY | Yes | 9 / 9 | 70 | 45 | 2 |
| Punjab | PB | Yes | 12 / 12 | 154 | 108 | 23 |
| Rajasthan | RJ | Yes | 12 / 12 | 199 | 144 | 40 |
| Sikkim | SK | Yes | 8 / 8 | 41 | 26 | 2 |
| Tamil Nadu | TN | Yes | 13 / 13 | 224 | 144 | 37 |
| Telangana | TS | Yes | 12 / 12 | 183 | 118 | 33 |
| Tripura | TR | Yes | 11 / 11 | 108 | 80 | 8 |
| Uttar Pradesh | UP | Yes | 14 / 14 | 273 | 196 | 75 |
| Uttarakhand | UK | Yes | 12 / 12 | 174 | 123 | 13 |
| West Bengal | WB | Yes | 13 / 13 | 219 | 103 | 21 |

## Bundled primary-sector coverage

- agriculture_rural: 312
- business_msme: 31
- education_scholarships: 2045
- employment_pensions: 176
- environment_natural_resources: 143
- government_information: 2107
- health_family_welfare: 224
- identity_civil_registration: 17
- industry_trade: 211
- justice_legal: 841
- land_housing: 112
- local_civic: 115
- other_verified: 12
- public_finance: 88
- rti_grievance: 41
- safety_disaster: 283
- social_security_welfare: 172
- taxation: 61
- transport: 166
- utilities_local: 113

## Destination checks (source authority is a separate field)

- ACCESS_DENIED: 10
- ACCESS_OR_CONTENT_REVIEW: 4
- CONTENT_REVIEW: 130
- DEFAULT_SERVER_PAGE: 4
- HTTP_ERROR: 57
- NETWORK_ERROR: 114
- REDIRECT_REVIEW: 172
- RESPONDING: 8250
- ROBOTS_DISALLOWED: 98
- ROBOTS_UNRESOLVED: 2302
- SERVER_ERROR: 25
- URL_REVIEW: 2

Robots-unresolved causes (destination was not retrieved):
- ACCESS_DENIED: 470
- HTTP_ERROR: 10
- NETWORK_ERROR: 1109
- OK: 3
- REDIRECT_REVIEW: 143
- SERVER_ERROR: 164
- TLS_ERROR: 403

Redirect-review reasons (unsafe/unexpected targets were not followed):
- Invalid redirect: 2
- Redirect limit reached: 3
- Redirect loop: 33
- Unexpected host or scheme; not followed: 134

Exact affected URLs, statuses, response codes and errors are in `destination_health.json`; suspected organization matches and safe aliases are in `duplicate_review.json` and `safe_redirect_equivalents.json`. Unpublished records retain their decisions in `review_queue.json`.

## Source category and pagination matrix

All expected totals and offsets come from the acquired public page/JavaScript. Partial HTML uses the AJAX header observed during normal browser scroll. Requests retain the source-provided page sizes (usually five rows); a probe with a larger limit returned 302 and was not used. Count mismatches keep a category incomplete.

| Source category URL | Expected rows | Pages processed / expected | Retrieved rows | Gap | Complete | URLs | Bundled | Awaiting review |
| --- | ---: | ---: | ---: | ---: | --- | ---: | ---: | ---: |
| [Districts](https://igod.gov.in/districts) | 784 | 32 / 32 | 784 | 0 | Yes | 748 | 688 | 60 |
| [Apex Bodies (Central)](https://igod.gov.in/apx/APXC/organizations) | 11 | 1 / 1 | 11 | 0 | Yes | 11 | 7 | 4 |
| [Others](https://igod.gov.in/apx/OTHR/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Ministries](https://igod.gov.in/ug/E002/organizations) | 53 | 7 / 7 | 53 | 0 | Yes | 44 | 0 | 44 |
| [Independent Departments](https://igod.gov.in/ug/E053/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 2 | 0 | 2 |
| [Departments](https://igod.gov.in/ug/E003/organizations) | 50 | 6 / 6 | 50 | 0 | Yes | 46 | 0 | 46 |
| [Attached / Subordinated Offices](https://igod.gov.in/ug/E005/organizations) | 288 | 54 / 54 | 288 | 0 | Yes | 237 | 104 | 133 |
| [Commissions/ Committees](https://igod.gov.in/ug/E013/organizations) | 104 | 17 / 17 | 104 | 0 | Yes | 94 | 54 | 40 |
| [Statutory / Autonomous Bodies](https://igod.gov.in/ug/E051/organizations) | 23 | 1 / 1 | 23 | 0 | Yes | 23 | 23 | 0 |
| [Statutory Bodies](https://igod.gov.in/ug/STABDY/organizations) | 133 | 23 / 23 | 133 | 0 | Yes | 111 | 58 | 53 |
| [Autonomous Bodies](https://igod.gov.in/ug/AUTBDY/organizations) | 486 | 94 / 94 | 486 | 0 | Yes | 444 | 237 | 207 |
| [PSUs / JVs / Companies / Societies](https://igod.gov.in/ug/E059/organizations) | 293 | 55 / 55 | 293 | 0 | Yes | 251 | 41 | 210 |
| [Academies / Institutions](https://igod.gov.in/ug/E009/organizations) | 1731 | 343 / 343 | 1731 | 0 | Yes | 1640 | 1427 | 213 |
| [Schemes / Programmes / Missions / Applications](https://igod.gov.in/ug/SPMA/organizations) | 531 | 103 / 103 | 531 | 0 | Yes | 512 | 328 | 184 |
| [Divisions / Units / Wings](https://igod.gov.in/ug/DUW/organizations) | 78 | 12 / 12 | 78 | 0 | Yes | 70 | 34 | 36 |
| [Regional / Field Offices](https://igod.gov.in/ug/RFO/organizations) | 186 | 34 / 34 | 186 | 0 | Yes | 164 | 88 | 76 |
| [Councils](https://igod.gov.in/ug/CNCL/organizations) | 22 | 1 / 1 | 22 | 0 | Yes | 21 | 12 | 9 |
| [Societies](https://igod.gov.in/ug/SOC/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 2 | 1 | 1 |
| [Others](https://igod.gov.in/ug/OTHR/organizations) | 843 | 165 / 165 | 843 | 0 | Yes | 756 | 407 | 349 |
| [Parliament of India](https://igod.gov.in/leg/L001/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 0 | 5 |
| [State Legislative Assemblies/Councils of India](https://igod.gov.in/leg/L004/organizations) | 40 | 4 / 4 | 40 | 0 | Yes | 40 | 39 | 1 |
| [Others](https://igod.gov.in/leg/OTHR/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Supreme Court of India](https://igod.gov.in/jud/J001/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 3 | 0 |
| [High Courts](https://igod.gov.in/jud/J002/organizations) | 29 | 2 / 2 | 29 | 0 | Yes | 29 | 19 | 10 |
| [District Courts](https://igod.gov.in/jud/J003/organizations) | 723 | 141 / 141 | 723 | 0 | Yes | 723 | 719 | 4 |
| [Tribunals / Quasi-Judicial Bodies](https://igod.gov.in/jud/E082/organizations) | 12 | 1 / 1 | 12 | 0 | Yes | 11 | 7 | 4 |
| [Others](https://igod.gov.in/jud/OTHR/organizations) | 90 | 14 / 14 | 90 | 0 | Yes | 86 | 40 | 46 |
| [Districts - Andaman and Nicobar Islands](https://igod.gov.in/sg/AN/E042/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 3 | 0 |
| [Districts - Andhra Pradesh](https://igod.gov.in/sg/AP/E042/organizations) | 28 | 2 / 2 | 28 | 0 | Yes | 26 | 26 | 0 |
| [Districts - Arunachal Pradesh](https://igod.gov.in/sg/AR/E042/organizations) | 27 | 2 / 2 | 27 | 0 | Yes | 21 | 21 | 0 |
| [Districts - Assam](https://igod.gov.in/sg/AS/E042/organizations) | 35 | 3 / 3 | 35 | 0 | Yes | 34 | 18 | 16 |
| [Districts - Bihar](https://igod.gov.in/sg/BR/E042/organizations) | 38 | 4 / 4 | 38 | 0 | Yes | 38 | 38 | 0 |
| [Indian Missions](https://igod.gov.in/int/INMISS/organizations) | 154 | 27 / 27 | 154 | 0 | Yes | 143 | 104 | 39 |
| [Honorary Missions](https://igod.gov.in/int/HNMISS/organizations) | 24 | 1 / 1 | 24 | 0 | Yes | 3 | 1 | 2 |
| [Others](https://igod.gov.in/int/OTHR/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Districts - Chandigarh](https://igod.gov.in/sg/CH/E042/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 1 | 0 |
| [Districts - Chhattisgarh](https://igod.gov.in/sg/CG/E042/organizations) | 33 | 3 / 3 | 33 | 0 | Yes | 29 | 28 | 1 |
| [Districts - Dadra and Nagar Haveli and Daman and Diu](https://igod.gov.in/sg/ND/E042/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 2 | 1 |
| [Districts - Delhi](https://igod.gov.in/sg/DL/E042/organizations) | 13 | 1 / 1 | 13 | 0 | Yes | 10 | 8 | 2 |
| [Districts - Goa](https://igod.gov.in/sg/GA/E042/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 2 | 2 | 0 |
| [Districts - Gujarat](https://igod.gov.in/sg/GJ/E042/organizations) | 34 | 3 / 3 | 34 | 0 | Yes | 33 | 29 | 4 |
| [Districts - Haryana](https://igod.gov.in/sg/HR/E042/organizations) | 23 | 1 / 1 | 23 | 0 | Yes | 22 | 21 | 1 |
| [Districts - Himachal Pradesh](https://igod.gov.in/sg/HP/E042/organizations) | 12 | 1 / 1 | 12 | 0 | Yes | 12 | 12 | 0 |
| [Districts - Jammu and Kashmir](https://igod.gov.in/sg/JK/E042/organizations) | 20 | 1 / 1 | 20 | 0 | Yes | 20 | 20 | 0 |
| [Districts - Jharkhand](https://igod.gov.in/sg/JH/E042/organizations) | 24 | 1 / 1 | 24 | 0 | Yes | 24 | 24 | 0 |
| [Districts - Karnataka](https://igod.gov.in/sg/KA/E042/organizations) | 31 | 3 / 3 | 31 | 0 | Yes | 31 | 30 | 1 |
| [Districts - Kerala](https://igod.gov.in/sg/KL/E042/organizations) | 14 | 1 / 1 | 14 | 0 | Yes | 14 | 14 | 0 |
| [Districts - Ladakh](https://igod.gov.in/sg/LA/E042/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 2 | 2 | 0 |
| [Districts - Lakshadweep](https://igod.gov.in/sg/LD/E042/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 1 | 0 |
| [Districts - Madhya Pradesh](https://igod.gov.in/sg/MP/E042/organizations) | 55 | 7 / 7 | 55 | 0 | Yes | 53 | 53 | 0 |
| [Districts - Maharashtra](https://igod.gov.in/sg/MH/E042/organizations) | 36 | 4 / 4 | 36 | 0 | Yes | 36 | 33 | 3 |
| [Districts - Manipur](https://igod.gov.in/sg/MN/E042/organizations) | 16 | 1 / 1 | 16 | 0 | Yes | 14 | 14 | 0 |
| [Districts - Meghalaya](https://igod.gov.in/sg/ML/E042/organizations) | 12 | 1 / 1 | 12 | 0 | Yes | 11 | 11 | 0 |
| [Districts - Mizoram](https://igod.gov.in/sg/MZ/E042/organizations) | 11 | 1 / 1 | 11 | 0 | Yes | 8 | 8 | 0 |
| [Districts - Nagaland](https://igod.gov.in/sg/NL/E042/organizations) | 17 | 1 / 1 | 17 | 0 | Yes | 15 | 15 | 0 |
| [Districts - Odisha](https://igod.gov.in/sg/OD/E042/organizations) | 30 | 2 / 2 | 30 | 0 | Yes | 30 | 0 | 30 |
| [Districts - Puducherry](https://igod.gov.in/sg/PY/E042/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 2 | 2 | 0 |
| [Districts - Punjab](https://igod.gov.in/sg/PB/E042/organizations) | 23 | 1 / 1 | 23 | 0 | Yes | 23 | 23 | 0 |
| [Districts - Rajasthan](https://igod.gov.in/sg/RJ/E042/organizations) | 41 | 5 / 5 | 41 | 0 | Yes | 40 | 40 | 0 |
| [Districts - Sikkim](https://igod.gov.in/sg/SK/E042/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 2 | 2 | 0 |
| [Districts - Tamil Nadu](https://igod.gov.in/sg/TN/E042/organizations) | 38 | 4 / 4 | 38 | 0 | Yes | 37 | 37 | 0 |
| [Districts - Telangana](https://igod.gov.in/sg/TS/E042/organizations) | 33 | 3 / 3 | 33 | 0 | Yes | 33 | 33 | 0 |
| [Districts - Tripura](https://igod.gov.in/sg/TR/E042/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 8 | 8 | 0 |
| [Districts - Uttar Pradesh](https://igod.gov.in/sg/UP/E042/organizations) | 75 | 11 / 11 | 75 | 0 | Yes | 75 | 75 | 0 |
| [Districts - Uttarakhand](https://igod.gov.in/sg/UK/E042/organizations) | 13 | 1 / 1 | 13 | 0 | Yes | 13 | 13 | 0 |
| [Districts - West Bengal](https://igod.gov.in/sg/WB/E042/organizations) | 23 | 1 / 1 | 23 | 0 | Yes | 22 | 21 | 1 |
| [Agriculture & Cooperation](https://igod.gov.in/sector/GRNsIHQBsvhI6u6Q3tju/organizations) | 323 | 61 / 61 | 323 | 0 | Yes | 284 | 164 | 120 |
| [Animal Husbandry & Fishing](https://igod.gov.in/sector/_RNsIHQBsvhI6u6Q3tfu/organizations) | 75 | 11 / 11 | 75 | 0 | Yes | 65 | 29 | 36 |
| [Art & Culture](https://igod.gov.in/sector/DxNsIHQBsvhI6u6Q3tju/organizations) | 112 | 19 / 19 | 112 | 0 | Yes | 102 | 47 | 55 |
| [Chemicals & Fertilizers](https://igod.gov.in/sector/AhNsIHQBsvhI6u6Q3tju/organizations) | 34 | 3 / 3 | 34 | 0 | Yes | 31 | 9 | 22 |
| [Coal & Mine](https://igod.gov.in/sector/ABNsIHQBsvhI6u6Q3tju/organizations) | 37 | 4 / 4 | 37 | 0 | Yes | 34 | 10 | 24 |
| [Commerce & Industry](https://igod.gov.in/sector/BBNsIHQBsvhI6u6Q3tju/organizations) | 450 | 86 / 86 | 450 | 0 | Yes | 400 | 201 | 199 |
| [Communications & Information Technology](https://igod.gov.in/sector/ERNsIHQBsvhI6u6Q3tju/organizations) | 347 | 66 / 66 | 347 | 0 | Yes | 300 | 178 | 122 |
| [Defence](https://igod.gov.in/sector/GhNsIHQBsvhI6u6Q3tju/organizations) | 264 | 49 / 49 | 264 | 0 | Yes | 153 | 51 | 102 |
| [Education & Training](https://igod.gov.in/sector/BRNsIHQBsvhI6u6Q3tju/organizations) | 2674 | 531 / 531 | 2674 | 0 | Yes | 2526 | 1983 | 543 |
| [Employment & Labour](https://igod.gov.in/sector/BxNsIHQBsvhI6u6Q3tju/organizations) | 176 | 32 / 32 | 176 | 0 | Yes | 170 | 105 | 65 |
| [Energy & Power](https://igod.gov.in/sector/ARNsIHQBsvhI6u6Q3tju/organizations) | 119 | 20 / 20 | 119 | 0 | Yes | 114 | 45 | 69 |
| [Environment & Natural Resources](https://igod.gov.in/sector/_xNsIHQBsvhI6u6Q3tfu/organizations) | 210 | 38 / 38 | 210 | 0 | Yes | 197 | 116 | 81 |
| [Finance, Banking & Insurance](https://igod.gov.in/sector/CBNsIHQBsvhI6u6Q3tju/organizations) | 364 | 69 / 69 | 364 | 0 | Yes | 313 | 145 | 168 |
| [Food & Public Distribution](https://igod.gov.in/sector/ChNsIHQBsvhI6u6Q3tju/organizations) | 95 | 15 / 15 | 95 | 0 | Yes | 91 | 53 | 38 |
| [Forestry & Wildlife](https://igod.gov.in/sector/_hNsIHQBsvhI6u6Q3tfu/organizations) | 31 | 3 / 3 | 31 | 0 | Yes | 25 | 15 | 10 |
| [Governance & Administration](https://igod.gov.in/sector/FRNsIHQBsvhI6u6Q3tju/organizations) | 721 | 141 / 141 | 721 | 0 | Yes | 650 | 416 | 234 |
| [Health & Family welfare](https://igod.gov.in/sector/CxNsIHQBsvhI6u6Q3tju/organizations) | 417 | 80 / 80 | 417 | 0 | Yes | 380 | 187 | 193 |
| [Home affairs & National Security](https://igod.gov.in/sector/FBNsIHQBsvhI6u6Q3tju/organizations) | 316 | 60 / 60 | 316 | 0 | Yes | 292 | 193 | 99 |
| [Housing & Urban Development](https://igod.gov.in/sector/DBNsIHQBsvhI6u6Q3tju/organizations) | 144 | 25 / 25 | 144 | 0 | Yes | 128 | 69 | 59 |
| [Information & Broadcasting](https://igod.gov.in/sector/EBNsIHQBsvhI6u6Q3tju/organizations) | 84 | 13 / 13 | 84 | 0 | Yes | 69 | 30 | 39 |
| [International Affairs](https://igod.gov.in/sector/FxNsIHQBsvhI6u6Q3tju/organizations) | 28 | 2 / 2 | 28 | 0 | Yes | 26 | 10 | 16 |
| [Law & Justice](https://igod.gov.in/sector/ExNsIHQBsvhI6u6Q3tju/organizations) | 180 | 32 / 32 | 180 | 0 | Yes | 174 | 107 | 67 |
| [People & Organisations](https://igod.gov.in/sector/FhNsIHQBsvhI6u6Q3tju/organizations) | 64 | 9 / 9 | 64 | 0 | Yes | 64 | 28 | 36 |
| [Petroleum, Oil & Natural Gas](https://igod.gov.in/sector/AxNsIHQBsvhI6u6Q3tju/organizations) | 42 | 5 / 5 | 42 | 0 | Yes | 40 | 14 | 26 |
| [Rural Development & Panchayati Raj](https://igod.gov.in/sector/DRNsIHQBsvhI6u6Q3tju/organizations) | 168 | 30 / 30 | 168 | 0 | Yes | 155 | 104 | 51 |
| [Science, Technology & Research](https://igod.gov.in/sector/EhNsIHQBsvhI6u6Q3tju/organizations) | 305 | 57 / 57 | 305 | 0 | Yes | 285 | 123 | 162 |
| [Social Justice & Empowerment](https://igod.gov.in/sector/GBNsIHQBsvhI6u6Q3tju/organizations) | 197 | 36 / 36 | 197 | 0 | Yes | 180 | 119 | 61 |
| [Tourism](https://igod.gov.in/sector/DhNsIHQBsvhI6u6Q3tju/organizations) | 62 | 9 / 9 | 62 | 0 | Yes | 56 | 28 | 28 |
| [Transport & Infrastructure](https://igod.gov.in/sector/CRNsIHQBsvhI6u6Q3tju/organizations) | 215 | 39 / 39 | 215 | 0 | Yes | 198 | 99 | 99 |
| [Youth Affairs & Sports](https://igod.gov.in/sector/BhNsIHQBsvhI6u6Q3tju/organizations) | 41 | 5 / 5 | 41 | 0 | Yes | 36 | 20 | 16 |
| [Apex Bodies (State) - Andaman and Nicobar Islands](https://igod.gov.in/apx/AN/APXS/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 1 | 0 | 1 |
| [Apex Bodies (State) - Andhra Pradesh](https://igod.gov.in/apx/AP/APXS/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 2 | 2 |
| [Apex Bodies (State) - Arunachal Pradesh](https://igod.gov.in/apx/AR/APXS/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 4 | 2 | 2 |
| [Apex Bodies (State) - Assam](https://igod.gov.in/apx/AS/APXS/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 3 | 0 |
| [Apex Bodies (State) - Bihar](https://igod.gov.in/apx/BR/APXS/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 7 | 5 | 2 |
| [Apex Bodies (State) - Chandigarh](https://igod.gov.in/apx/CH/APXS/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 0 | 0 | 0 |
| [Apex Bodies (State) - Chhattisgarh](https://igod.gov.in/apx/CG/APXS/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 4 | 1 |
| [Apex Bodies (State) - Dadra and Nagar Haveli and Daman and Diu](https://igod.gov.in/apx/ND/APXS/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 1 | 0 |
| [Apex Bodies (State) - Delhi](https://igod.gov.in/apx/DL/APXS/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 2 | 0 | 2 |
| [Apex Bodies (State) - Goa](https://igod.gov.in/apx/GA/APXS/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 3 | 1 |
| [Apex Bodies (State) - Gujarat](https://igod.gov.in/apx/GJ/APXS/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 2 | 3 |
| [Apex Bodies (State) - Haryana](https://igod.gov.in/apx/HR/APXS/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 3 | 2 |
| [Apex Bodies (State) - Himachal Pradesh](https://igod.gov.in/apx/HP/APXS/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 0 | 5 |
| [Apex Bodies (State) - Jammu and Kashmir](https://igod.gov.in/apx/JK/APXS/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 3 | 1 |
| [Apex Bodies (State) - Jharkhand](https://igod.gov.in/apx/JH/APXS/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 2 | 1 | 1 |
| [Apex Bodies (State) - Karnataka](https://igod.gov.in/apx/KA/APXS/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 3 | 2 |
| [Apex Bodies (State) - Kerala](https://igod.gov.in/apx/KL/APXS/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 7 | 4 | 3 |
| [Apex Bodies (State) - Ladakh](https://igod.gov.in/apx/LA/APXS/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 1 | 0 |
| [Apex Bodies (State) - Lakshadweep](https://igod.gov.in/apx/LD/APXS/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 2 | 1 |
| [Apex Bodies (State) - Madhya Pradesh](https://igod.gov.in/apx/MP/APXS/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 4 | 2 | 2 |
| [Apex Bodies (State) - Maharashtra](https://igod.gov.in/apx/MH/APXS/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 4 | 3 | 1 |
| [Apex Bodies (State) - Manipur](https://igod.gov.in/apx/MN/APXS/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 4 | 2 | 2 |
| [Apex Bodies (State) - Meghalaya](https://igod.gov.in/apx/ML/APXS/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 3 | 1 |
| [Apex Bodies (State) - Mizoram](https://igod.gov.in/apx/MZ/APXS/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 4 | 0 |
| [Apex Bodies (State) - Nagaland](https://igod.gov.in/apx/NL/APXS/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 3 | 1 |
| [Apex Bodies (State) - Odisha](https://igod.gov.in/apx/OD/APXS/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 5 | 3 | 2 |
| [Apex Bodies (State) - Puducherry](https://igod.gov.in/apx/PY/APXS/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 3 | 0 |
| [Apex Bodies (State) - Punjab](https://igod.gov.in/apx/PB/APXS/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 2 | 2 |
| [Apex Bodies (State) - Rajasthan](https://igod.gov.in/apx/RJ/APXS/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 6 | 3 | 3 |
| [Apex Bodies (State) - Sikkim](https://igod.gov.in/apx/SK/APXS/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 2 | 2 |
| [Apex Bodies (State) - Tamil Nadu](https://igod.gov.in/apx/TN/APXS/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 3 | 2 | 1 |
| [Apex Bodies (State) - Telangana](https://igod.gov.in/apx/TS/APXS/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 2 | 1 |
| [Apex Bodies (State) - Tripura](https://igod.gov.in/apx/TR/APXS/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 3 | 0 |
| [Apex Bodies (State) - Uttar Pradesh](https://igod.gov.in/apx/UP/APXS/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 6 | 4 | 2 |
| [Apex Bodies (State) - Uttarakhand](https://igod.gov.in/apx/UK/APXS/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 6 | 5 | 1 |
| [Apex Bodies (State) - West Bengal](https://igod.gov.in/apx/WB/APXS/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 2 | 0 | 2 |
| [Departments - Andaman and Nicobar Islands](https://igod.gov.in/sg/AN/E003/organizations) | 32 | 3 / 3 | 32 | 0 | Yes | 19 | 4 | 15 |
| [Academies / Institutions - Andaman and Nicobar Islands](https://igod.gov.in/sg/AN/E009/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 3 | 1 |
| [Others - Andaman and Nicobar Islands](https://igod.gov.in/sg/AN/OTHR/organizations) | 10 | 1 / 1 | 10 | 0 | Yes | 9 | 0 | 9 |
| [Departments - Andhra Pradesh](https://igod.gov.in/sg/AP/E003/organizations) | 36 | 4 / 4 | 36 | 0 | Yes | 32 | 15 | 17 |
| [Attached / Subordinated Offices - Andhra Pradesh](https://igod.gov.in/sg/AP/E005/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 2 | 1 | 1 |
| [Commissions/ Committees - Andhra Pradesh](https://igod.gov.in/sg/AP/E013/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 6 | 4 | 2 |
| [Statutory / Autonomous Bodies - Andhra Pradesh](https://igod.gov.in/sg/AP/E051/organizations) | 17 | 1 / 1 | 17 | 0 | Yes | 13 | 8 | 5 |
| [Directorates / Commissionerates - Andhra Pradesh](https://igod.gov.in/sg/AP/E004/organizations) | 12 | 1 / 1 | 12 | 0 | Yes | 9 | 3 | 6 |
| [Boards / Undertakings - Andhra Pradesh](https://igod.gov.in/sg/AP/E011/organizations) | 34 | 3 / 3 | 34 | 0 | Yes | 30 | 19 | 11 |
| [PSUs / JVs / Companies / Societies - Andhra Pradesh](https://igod.gov.in/sg/AP/E059/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 3 | 1 |
| [Academies / Institutions - Andhra Pradesh](https://igod.gov.in/sg/AP/E009/organizations) | 22 | 1 / 1 | 22 | 0 | Yes | 20 | 17 | 3 |
| [Schemes / Programmes / Missions / Applications - Andhra Pradesh](https://igod.gov.in/sg/AP/SPMA/organizations) | 14 | 1 / 1 | 14 | 0 | Yes | 14 | 10 | 4 |
| [Others - Andhra Pradesh](https://igod.gov.in/sg/AP/OTHR/organizations) | 45 | 5 / 5 | 45 | 0 | Yes | 39 | 23 | 16 |
| [Departments - Arunachal Pradesh](https://igod.gov.in/sg/AR/E003/organizations) | 37 | 4 / 4 | 37 | 0 | Yes | 30 | 18 | 12 |
| [Directorates / Commissionerates - Arunachal Pradesh](https://igod.gov.in/sg/AR/E004/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 3 | 1 |
| [Boards / Undertakings - Arunachal Pradesh](https://igod.gov.in/sg/AR/E011/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 3 | 0 |
| [Academies / Institutions - Arunachal Pradesh](https://igod.gov.in/sg/AR/E009/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 9 | 7 | 2 |
| [Schemes / Programmes / Missions / Applications - Arunachal Pradesh](https://igod.gov.in/sg/AR/SPMA/organizations) | 15 | 1 / 1 | 15 | 0 | Yes | 15 | 12 | 3 |
| [Others - Arunachal Pradesh](https://igod.gov.in/sg/AR/OTHR/organizations) | 12 | 1 / 1 | 12 | 0 | Yes | 11 | 4 | 7 |
| [Departments - Assam](https://igod.gov.in/sg/AS/E003/organizations) | 53 | 7 / 7 | 53 | 0 | Yes | 53 | 50 | 3 |
| [Attached / Subordinated Offices - Assam](https://igod.gov.in/sg/AS/E005/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 8 | 8 | 0 |
| [Commissions/ Committees - Assam](https://igod.gov.in/sg/AS/E013/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 6 | 6 | 0 |
| [Statutory / Autonomous Bodies - Assam](https://igod.gov.in/sg/AS/E051/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 8 | 5 | 3 |
| [Directorates / Commissionerates - Assam](https://igod.gov.in/sg/AS/E004/organizations) | 41 | 5 / 5 | 41 | 0 | Yes | 41 | 40 | 1 |
| [Boards / Undertakings - Assam](https://igod.gov.in/sg/AS/E011/organizations) | 28 | 2 / 2 | 28 | 0 | Yes | 27 | 18 | 9 |
| [PSUs / JVs / Companies / Societies - Assam](https://igod.gov.in/sg/AS/E059/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 2 | 2 |
| [Academies / Institutions - Assam](https://igod.gov.in/sg/AS/E009/organizations) | 16 | 1 / 1 | 16 | 0 | Yes | 16 | 11 | 5 |
| [Schemes / Programmes / Missions / Applications - Assam](https://igod.gov.in/sg/AS/SPMA/organizations) | 14 | 1 / 1 | 14 | 0 | Yes | 14 | 12 | 2 |
| [Others - Assam](https://igod.gov.in/sg/AS/OTHR/organizations) | 33 | 3 / 3 | 33 | 0 | Yes | 31 | 23 | 8 |
| [Departments - Bihar](https://igod.gov.in/sg/BR/E003/organizations) | 39 | 4 / 4 | 39 | 0 | Yes | 39 | 7 | 32 |
| [Attached / Subordinated Offices - Bihar](https://igod.gov.in/sg/BR/E005/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 1 | 2 |
| [Commissions/ Committees - Bihar](https://igod.gov.in/sg/BR/E013/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 5 | 0 | 5 |
| [Statutory / Autonomous Bodies - Bihar](https://igod.gov.in/sg/BR/E051/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 6 | 4 | 2 |
| [Directorates / Commissionerates - Bihar](https://igod.gov.in/sg/BR/E004/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 8 | 7 | 1 |
| [Boards / Undertakings - Bihar](https://igod.gov.in/sg/BR/E011/organizations) | 21 | 1 / 1 | 21 | 0 | Yes | 20 | 8 | 12 |
| [Academies / Institutions - Bihar](https://igod.gov.in/sg/BR/E009/organizations) | 16 | 1 / 1 | 16 | 0 | Yes | 13 | 4 | 9 |
| [Schemes / Programmes / Missions / Applications - Bihar](https://igod.gov.in/sg/BR/SPMA/organizations) | 34 | 3 / 3 | 34 | 0 | Yes | 34 | 21 | 13 |
| [Others - Bihar](https://igod.gov.in/sg/BR/OTHR/organizations) | 32 | 3 / 3 | 32 | 0 | Yes | 31 | 14 | 17 |
| [Panchayati Raj Institutions](https://igod.gov.in/leg/L007/organizations) | 115 | 19 / 19 | 115 | 0 | Yes | 35 | 23 | 12 |
| [Panchayati Raj Institutions - Andaman and Nicobar Islands](https://igod.gov.in/leg/AN/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Andhra Pradesh](https://igod.gov.in/leg/AP/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Arunachal Pradesh](https://igod.gov.in/leg/AR/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Assam](https://igod.gov.in/leg/AS/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Bihar](https://igod.gov.in/leg/BR/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Chandigarh](https://igod.gov.in/leg/CH/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Chhattisgarh](https://igod.gov.in/leg/CG/L007/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 1 | 0 |
| [Panchayati Raj Institutions - Dadra and Nagar Haveli and Daman and Diu](https://igod.gov.in/leg/ND/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Delhi](https://igod.gov.in/leg/DL/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Goa](https://igod.gov.in/leg/GA/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Gujarat](https://igod.gov.in/leg/GJ/L007/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 6 | 6 | 0 |
| [Panchayati Raj Institutions - Haryana](https://igod.gov.in/leg/HR/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Himachal Pradesh](https://igod.gov.in/leg/HP/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Jammu and Kashmir](https://igod.gov.in/leg/JK/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Jharkhand](https://igod.gov.in/leg/JH/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Karnataka](https://igod.gov.in/leg/KA/L007/organizations) | 77 | 12 / 12 | 77 | 0 | Yes | 7 | 6 | 1 |
| [Panchayati Raj Institutions - Kerala](https://igod.gov.in/leg/KL/L007/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 1 | 1 | 0 |
| [Panchayati Raj Institutions - Ladakh](https://igod.gov.in/leg/LA/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Lakshadweep](https://igod.gov.in/leg/LD/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Madhya Pradesh](https://igod.gov.in/leg/MP/L007/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Maharashtra](https://igod.gov.in/leg/MH/L007/organizations) | 19 | 1 / 1 | 19 | 0 | Yes | 16 | 9 | 7 |
| [Panchayati Raj Institutions - Manipur](https://igod.gov.in/leg/MN/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Meghalaya](https://igod.gov.in/leg/ML/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Mizoram](https://igod.gov.in/leg/MZ/L007/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 0 | 1 |
| [Panchayati Raj Institutions - Nagaland](https://igod.gov.in/leg/NL/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Odisha](https://igod.gov.in/leg/OD/L007/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Puducherry](https://igod.gov.in/leg/PY/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Punjab](https://igod.gov.in/leg/PB/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Rajasthan](https://igod.gov.in/leg/RJ/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Sikkim](https://igod.gov.in/leg/SK/L007/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Tamil Nadu](https://igod.gov.in/leg/TN/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Telangana](https://igod.gov.in/leg/TS/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Tripura](https://igod.gov.in/leg/TR/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Uttar Pradesh](https://igod.gov.in/leg/UP/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - Uttarakhand](https://igod.gov.in/leg/UK/L007/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Panchayati Raj Institutions - West Bengal](https://igod.gov.in/leg/WB/L007/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 3 | 0 | 3 |
| [Local Government Bodies](https://igod.gov.in/leg/L008/organizations) | 275 | 51 / 51 | 275 | 0 | Yes | 122 | 51 | 71 |
| [Local Government Bodies - Andaman and Nicobar Islands](https://igod.gov.in/leg/AN/L008/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 0 | 0 | 0 |
| [Local Government Bodies - Andhra Pradesh](https://igod.gov.in/leg/AP/L008/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 2 | 1 |
| [Local Government Bodies - Arunachal Pradesh](https://igod.gov.in/leg/AR/L008/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 1 | 0 |
| [Local Government Bodies - Assam](https://igod.gov.in/leg/AS/L008/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 2 | 1 | 1 |
| [Local Government Bodies - Bihar](https://igod.gov.in/leg/BR/L008/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 2 | 1 | 1 |
| [Local Government Bodies - Chandigarh](https://igod.gov.in/leg/CH/L008/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Local Government Bodies - Chhattisgarh](https://igod.gov.in/leg/CG/L008/organizations) | 10 | 1 / 1 | 10 | 0 | Yes | 4 | 1 | 3 |
| [Local Government Bodies - Dadra and Nagar Haveli and Daman and Diu](https://igod.gov.in/leg/ND/L008/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Local Government Bodies - Delhi](https://igod.gov.in/leg/DL/L008/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 0 | 1 |
| [Local Government Bodies - Goa](https://igod.gov.in/leg/GA/L008/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 2 | 1 | 1 |
| [Local Government Bodies - Gujarat](https://igod.gov.in/leg/GJ/L008/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 8 | 4 | 4 |
| [Local Government Bodies - Haryana](https://igod.gov.in/leg/HR/L008/organizations) | 11 | 1 / 1 | 11 | 0 | Yes | 10 | 3 | 7 |
| [Local Government Bodies - Himachal Pradesh](https://igod.gov.in/leg/HP/L008/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 2 | 1 | 1 |
| [Local Government Bodies - Jammu and Kashmir](https://igod.gov.in/leg/JK/L008/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 2 | 0 | 2 |
| [Local Government Bodies - Jharkhand](https://igod.gov.in/leg/JH/L008/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 1 | 0 |
| [Local Government Bodies - Karnataka](https://igod.gov.in/leg/KA/L008/organizations) | 115 | 19 / 19 | 115 | 0 | Yes | 4 | 1 | 3 |
| [Local Government Bodies - Kerala](https://igod.gov.in/leg/KL/L008/organizations) | 10 | 1 / 1 | 10 | 0 | Yes | 3 | 2 | 1 |
| [Local Government Bodies - Ladakh](https://igod.gov.in/leg/LA/L008/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Local Government Bodies - Lakshadweep](https://igod.gov.in/leg/LD/L008/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Local Government Bodies - Madhya Pradesh](https://igod.gov.in/leg/MP/L008/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 5 | 2 | 3 |
| [Local Government Bodies - Maharashtra](https://igod.gov.in/leg/MH/L008/organizations) | 23 | 1 / 1 | 23 | 0 | Yes | 20 | 12 | 8 |
| [Local Government Bodies - Manipur](https://igod.gov.in/leg/MN/L008/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 1 | 0 |
| [Local Government Bodies - Meghalaya](https://igod.gov.in/leg/ML/L008/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 1 | 1 | 0 |
| [Local Government Bodies - Mizoram](https://igod.gov.in/leg/MZ/L008/organizations) | 2 | 1 / 1 | 2 | 0 | Yes | 2 | 2 | 0 |
| [Local Government Bodies - Nagaland](https://igod.gov.in/leg/NL/L008/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 1 | 0 |
| [Local Government Bodies - Odisha](https://igod.gov.in/leg/OD/L008/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 6 | 3 | 3 |
| [Local Government Bodies - Puducherry](https://igod.gov.in/leg/PY/L008/organizations) | 0 | 1 / 1 | 0 | 0 | Yes | 0 | 0 | 0 |
| [Local Government Bodies - Punjab](https://igod.gov.in/leg/PB/L008/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 7 | 2 | 5 |
| [Local Government Bodies - Rajasthan](https://igod.gov.in/leg/RJ/L008/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 2 | 0 | 2 |
| [Local Government Bodies - Sikkim](https://igod.gov.in/leg/SK/L008/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 0 | 0 | 0 |
| [Local Government Bodies - Tamil Nadu](https://igod.gov.in/leg/TN/L008/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 4 | 2 | 2 |
| [Local Government Bodies - Telangana](https://igod.gov.in/leg/TS/L008/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 3 | 2 | 1 |
| [Local Government Bodies - Tripura](https://igod.gov.in/leg/TR/L008/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 1 | 0 |
| [Local Government Bodies - Uttar Pradesh](https://igod.gov.in/leg/UP/L008/organizations) | 11 | 1 / 1 | 11 | 0 | Yes | 8 | 2 | 6 |
| [Local Government Bodies - Uttarakhand](https://igod.gov.in/leg/UK/L008/organizations) | 1 | 1 / 1 | 1 | 0 | Yes | 1 | 1 | 0 |
| [Local Government Bodies - West Bengal](https://igod.gov.in/leg/WB/L008/organizations) | 21 | 1 / 1 | 21 | 0 | Yes | 14 | 0 | 14 |
| [Departments - Chandigarh](https://igod.gov.in/sg/CH/E003/organizations) | 31 | 3 / 3 | 31 | 0 | Yes | 26 | 18 | 8 |
| [Commissions/ Committees - Chandigarh](https://igod.gov.in/sg/CH/E013/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 2 | 1 | 1 |
| [Boards / Undertakings - Chandigarh](https://igod.gov.in/sg/CH/E011/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 4 | 1 | 3 |
| [Academies / Institutions - Chandigarh](https://igod.gov.in/sg/CH/E009/organizations) | 29 | 2 / 2 | 29 | 0 | Yes | 24 | 22 | 2 |
| [Others - Chandigarh](https://igod.gov.in/sg/CH/OTHR/organizations) | 10 | 1 / 1 | 10 | 0 | Yes | 6 | 3 | 3 |
| [Departments - Chhattisgarh](https://igod.gov.in/sg/CG/E003/organizations) | 35 | 3 / 3 | 35 | 0 | Yes | 32 | 19 | 13 |
| [Attached / Subordinated Offices - Chhattisgarh](https://igod.gov.in/sg/CG/E005/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 2 | 1 |
| [Commissions/ Committees - Chhattisgarh](https://igod.gov.in/sg/CG/E013/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 3 | 2 |
| [Directorates / Commissionerates - Chhattisgarh](https://igod.gov.in/sg/CG/E004/organizations) | 10 | 1 / 1 | 10 | 0 | Yes | 10 | 8 | 2 |
| [Boards / Undertakings - Chhattisgarh](https://igod.gov.in/sg/CG/E011/organizations) | 16 | 1 / 1 | 16 | 0 | Yes | 15 | 5 | 10 |
| [Academies / Institutions - Chhattisgarh](https://igod.gov.in/sg/CG/E009/organizations) | 13 | 1 / 1 | 13 | 0 | Yes | 12 | 6 | 6 |
| [Schemes / Programmes / Missions / Applications - Chhattisgarh](https://igod.gov.in/sg/CG/SPMA/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 7 | 6 | 1 |
| [Others - Chhattisgarh](https://igod.gov.in/sg/CG/OTHR/organizations) | 19 | 1 / 1 | 19 | 0 | Yes | 14 | 6 | 8 |
| [Departments - Dadra and Nagar Haveli and Daman and Diu](https://igod.gov.in/sg/ND/E003/organizations) | 32 | 3 / 3 | 32 | 0 | Yes | 31 | 31 | 0 |
| [Others - Dadra and Nagar Haveli and Daman and Diu](https://igod.gov.in/sg/ND/OTHR/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 1 | 0 | 1 |
| [Departments - Delhi](https://igod.gov.in/sg/DL/E003/organizations) | 44 | 5 / 5 | 44 | 0 | Yes | 44 | 3 | 41 |
| [Commissions/ Committees - Delhi](https://igod.gov.in/sg/DL/E013/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 7 | 0 | 7 |
| [Statutory / Autonomous Bodies - Delhi](https://igod.gov.in/sg/DL/E051/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 1 | 4 |
| [Directorates / Commissionerates - Delhi](https://igod.gov.in/sg/DL/E004/organizations) | 11 | 1 / 1 | 11 | 0 | Yes | 11 | 1 | 10 |
| [Boards / Undertakings - Delhi](https://igod.gov.in/sg/DL/E011/organizations) | 15 | 1 / 1 | 15 | 0 | Yes | 15 | 2 | 13 |
| [PSUs / JVs / Companies / Societies - Delhi](https://igod.gov.in/sg/DL/E059/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 6 | 1 | 5 |
| [Academies / Institutions - Delhi](https://igod.gov.in/sg/DL/E009/organizations) | 58 | 8 / 8 | 58 | 0 | Yes | 56 | 7 | 49 |
| [Schemes / Programmes / Missions / Applications - Delhi](https://igod.gov.in/sg/DL/SPMA/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 9 | 6 | 3 |
| [Others - Delhi](https://igod.gov.in/sg/DL/OTHR/organizations) | 51 | 7 / 7 | 51 | 0 | Yes | 51 | 7 | 44 |
| [Departments - Goa](https://igod.gov.in/sg/GA/E003/organizations) | 50 | 6 / 6 | 50 | 0 | Yes | 45 | 24 | 21 |
| [Directorates / Commissionerates - Goa](https://igod.gov.in/sg/GA/E004/organizations) | 13 | 1 / 1 | 13 | 0 | Yes | 10 | 7 | 3 |
| [Boards / Undertakings - Goa](https://igod.gov.in/sg/GA/E011/organizations) | 11 | 1 / 1 | 11 | 0 | Yes | 9 | 3 | 6 |
| [Academies / Institutions - Goa](https://igod.gov.in/sg/GA/E009/organizations) | 16 | 1 / 1 | 16 | 0 | Yes | 15 | 10 | 5 |
| [Others - Goa](https://igod.gov.in/sg/GA/OTHR/organizations) | 12 | 1 / 1 | 12 | 0 | Yes | 12 | 7 | 5 |
| [Departments - Gujarat](https://igod.gov.in/sg/GJ/E003/organizations) | 30 | 2 / 2 | 30 | 0 | Yes | 28 | 22 | 6 |
| [Commissions/ Committees - Gujarat](https://igod.gov.in/sg/GJ/E013/organizations) | 18 | 1 / 1 | 18 | 0 | Yes | 16 | 8 | 8 |
| [Statutory / Autonomous Bodies - Gujarat](https://igod.gov.in/sg/GJ/E051/organizations) | 13 | 1 / 1 | 13 | 0 | Yes | 13 | 11 | 2 |
| [Directorates / Commissionerates - Gujarat](https://igod.gov.in/sg/GJ/E004/organizations) | 26 | 2 / 2 | 26 | 0 | Yes | 24 | 13 | 11 |
| [Boards / Undertakings - Gujarat](https://igod.gov.in/sg/GJ/E011/organizations) | 53 | 7 / 7 | 53 | 0 | Yes | 50 | 20 | 30 |
| [PSUs / JVs / Companies / Societies - Gujarat](https://igod.gov.in/sg/GJ/E059/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 2 | 2 |
| [Academies / Institutions - Gujarat](https://igod.gov.in/sg/GJ/E009/organizations) | 53 | 7 / 7 | 53 | 0 | Yes | 45 | 26 | 19 |
| [Schemes / Programmes / Missions / Applications - Gujarat](https://igod.gov.in/sg/GJ/SPMA/organizations) | 19 | 1 / 1 | 19 | 0 | Yes | 19 | 18 | 1 |
| [Others - Gujarat](https://igod.gov.in/sg/GJ/OTHR/organizations) | 126 | 22 / 22 | 126 | 0 | Yes | 116 | 59 | 57 |
| [Departments - Haryana](https://igod.gov.in/sg/HR/E003/organizations) | 46 | 6 / 6 | 46 | 0 | Yes | 45 | 36 | 9 |
| [Attached / Subordinated Offices - Haryana](https://igod.gov.in/sg/HR/E005/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 2 | 2 | 0 |
| [Commissions/ Committees - Haryana](https://igod.gov.in/sg/HR/E013/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 4 | 3 | 1 |
| [Statutory / Autonomous Bodies - Haryana](https://igod.gov.in/sg/HR/E051/organizations) | 10 | 1 / 1 | 10 | 0 | Yes | 8 | 5 | 3 |
| [Directorates / Commissionerates - Haryana](https://igod.gov.in/sg/HR/E004/organizations) | 14 | 1 / 1 | 14 | 0 | Yes | 12 | 5 | 7 |
| [Boards / Undertakings - Haryana](https://igod.gov.in/sg/HR/E011/organizations) | 25 | 1 / 1 | 25 | 0 | Yes | 20 | 5 | 15 |
| [Academies / Institutions - Haryana](https://igod.gov.in/sg/HR/E009/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 5 | 2 | 3 |
| [Schemes / Programmes / Missions / Applications - Haryana](https://igod.gov.in/sg/HR/SPMA/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 7 | 6 | 1 |
| [Others - Haryana](https://igod.gov.in/sg/HR/OTHR/organizations) | 64 | 9 / 9 | 64 | 0 | Yes | 59 | 46 | 13 |
| [Departments - Himachal Pradesh](https://igod.gov.in/sg/HP/E003/organizations) | 39 | 4 / 4 | 39 | 0 | Yes | 34 | 14 | 20 |
| [Commissions/ Committees - Himachal Pradesh](https://igod.gov.in/sg/HP/E013/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 8 | 1 | 7 |
| [Statutory / Autonomous Bodies - Himachal Pradesh](https://igod.gov.in/sg/HP/E051/organizations) | 11 | 1 / 1 | 11 | 0 | Yes | 11 | 5 | 6 |
| [Directorates / Commissionerates - Himachal Pradesh](https://igod.gov.in/sg/HP/E004/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 3 | 0 |
| [Boards / Undertakings - Himachal Pradesh](https://igod.gov.in/sg/HP/E011/organizations) | 15 | 1 / 1 | 15 | 0 | Yes | 14 | 6 | 8 |
| [PSUs / JVs / Companies / Societies - Himachal Pradesh](https://igod.gov.in/sg/HP/E059/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 6 | 2 | 4 |
| [Academies / Institutions - Himachal Pradesh](https://igod.gov.in/sg/HP/E009/organizations) | 13 | 1 / 1 | 13 | 0 | Yes | 7 | 3 | 4 |
| [Schemes / Programmes / Missions / Applications - Himachal Pradesh](https://igod.gov.in/sg/HP/SPMA/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 3 | 1 |
| [Others - Himachal Pradesh](https://igod.gov.in/sg/HP/OTHR/organizations) | 26 | 2 / 2 | 26 | 0 | Yes | 20 | 6 | 14 |
| [Departments - Jammu and Kashmir](https://igod.gov.in/sg/JK/E003/organizations) | 36 | 4 / 4 | 36 | 0 | Yes | 36 | 13 | 23 |
| [Attached / Subordinated Offices - Jammu and Kashmir](https://igod.gov.in/sg/JK/E005/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 1 | 0 | 1 |
| [Directorates / Commissionerates - Jammu and Kashmir](https://igod.gov.in/sg/JK/E004/organizations) | 20 | 1 / 1 | 20 | 0 | Yes | 19 | 5 | 14 |
| [Boards / Undertakings - Jammu and Kashmir](https://igod.gov.in/sg/JK/E011/organizations) | 17 | 1 / 1 | 17 | 0 | Yes | 12 | 4 | 8 |
| [Academies / Institutions - Jammu and Kashmir](https://igod.gov.in/sg/JK/E009/organizations) | 25 | 1 / 1 | 25 | 0 | Yes | 22 | 9 | 13 |
| [Schemes / Programmes / Missions / Applications - Jammu and Kashmir](https://igod.gov.in/sg/JK/SPMA/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 8 | 6 | 2 |
| [Others - Jammu and Kashmir](https://igod.gov.in/sg/JK/OTHR/organizations) | 21 | 1 / 1 | 21 | 0 | Yes | 19 | 6 | 13 |
| [Departments - Jharkhand](https://igod.gov.in/sg/JH/E003/organizations) | 28 | 2 / 2 | 28 | 0 | Yes | 28 | 26 | 2 |
| [Commissions/ Committees - Jharkhand](https://igod.gov.in/sg/JH/E013/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 1 | 2 |
| [Boards / Undertakings - Jharkhand](https://igod.gov.in/sg/JH/E011/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 9 | 2 | 7 |
| [Academies / Institutions - Jharkhand](https://igod.gov.in/sg/JH/E009/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 7 | 1 | 6 |
| [Schemes / Programmes / Missions / Applications - Jharkhand](https://igod.gov.in/sg/JH/SPMA/organizations) | 16 | 1 / 1 | 16 | 0 | Yes | 16 | 12 | 4 |
| [Others - Jharkhand](https://igod.gov.in/sg/JH/OTHR/organizations) | 14 | 1 / 1 | 14 | 0 | Yes | 13 | 8 | 5 |
| [Departments - Karnataka](https://igod.gov.in/sg/KA/E003/organizations) | 48 | 6 / 6 | 48 | 0 | Yes | 46 | 30 | 16 |
| [Statutory / Autonomous Bodies - Karnataka](https://igod.gov.in/sg/KA/E051/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 3 | 2 |
| [Directorates / Commissionerates - Karnataka](https://igod.gov.in/sg/KA/E004/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 4 | 3 | 1 |
| [Boards / Undertakings - Karnataka](https://igod.gov.in/sg/KA/E011/organizations) | 27 | 2 / 2 | 27 | 0 | Yes | 21 | 13 | 8 |
| [Academies / Institutions - Karnataka](https://igod.gov.in/sg/KA/E009/organizations) | 20 | 1 / 1 | 20 | 0 | Yes | 13 | 7 | 6 |
| [Schemes / Programmes / Missions / Applications - Karnataka](https://igod.gov.in/sg/KA/SPMA/organizations) | 12 | 1 / 1 | 12 | 0 | Yes | 12 | 8 | 4 |
| [Others - Karnataka](https://igod.gov.in/sg/KA/OTHR/organizations) | 34 | 3 / 3 | 34 | 0 | Yes | 28 | 10 | 18 |
| [Departments - Kerala](https://igod.gov.in/sg/KL/E003/organizations) | 62 | 9 / 9 | 62 | 0 | Yes | 52 | 31 | 21 |
| [Attached / Subordinated Offices - Kerala](https://igod.gov.in/sg/KL/E005/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 2 | 1 |
| [Commissions/ Committees - Kerala](https://igod.gov.in/sg/KL/E013/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 5 | 4 | 1 |
| [Statutory / Autonomous Bodies - Kerala](https://igod.gov.in/sg/KL/E051/organizations) | 12 | 1 / 1 | 12 | 0 | Yes | 11 | 5 | 6 |
| [Directorates / Commissionerates - Kerala](https://igod.gov.in/sg/KL/E004/organizations) | 12 | 1 / 1 | 12 | 0 | Yes | 11 | 8 | 3 |
| [Boards / Undertakings - Kerala](https://igod.gov.in/sg/KL/E011/organizations) | 37 | 4 / 4 | 37 | 0 | Yes | 34 | 12 | 22 |
| [PSUs / JVs / Companies / Societies - Kerala](https://igod.gov.in/sg/KL/E059/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 3 | 1 | 2 |
| [Academies / Institutions - Kerala](https://igod.gov.in/sg/KL/E009/organizations) | 71 | 11 / 11 | 71 | 0 | Yes | 59 | 34 | 25 |
| [Schemes / Programmes / Missions / Applications - Kerala](https://igod.gov.in/sg/KL/SPMA/organizations) | 22 | 1 / 1 | 22 | 0 | Yes | 21 | 13 | 8 |
| [Others - Kerala](https://igod.gov.in/sg/KL/OTHR/organizations) | 90 | 14 / 14 | 90 | 0 | Yes | 63 | 38 | 25 |
| [Departments - Ladakh](https://igod.gov.in/sg/LA/E003/organizations) | 33 | 3 / 3 | 33 | 0 | Yes | 33 | 32 | 1 |
| [Others - Ladakh](https://igod.gov.in/sg/LA/OTHR/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 1 | 2 |
| [Departments - Lakshadweep](https://igod.gov.in/sg/LD/E003/organizations) | 20 | 1 / 1 | 20 | 0 | Yes | 5 | 4 | 1 |
| [Others - Lakshadweep](https://igod.gov.in/sg/LD/OTHR/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 9 | 5 | 4 |
| [Departments - Madhya Pradesh](https://igod.gov.in/sg/MP/E003/organizations) | 54 | 7 / 7 | 54 | 0 | Yes | 50 | 28 | 22 |
| [Attached / Subordinated Offices - Madhya Pradesh](https://igod.gov.in/sg/MP/E005/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 2 | 2 |
| [Commissions/ Committees - Madhya Pradesh](https://igod.gov.in/sg/MP/E013/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 5 | 3 | 2 |
| [Statutory / Autonomous Bodies - Madhya Pradesh](https://igod.gov.in/sg/MP/E051/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 9 | 4 | 5 |
| [Directorates / Commissionerates - Madhya Pradesh](https://igod.gov.in/sg/MP/E004/organizations) | 15 | 1 / 1 | 15 | 0 | Yes | 12 | 11 | 1 |
| [Boards / Undertakings - Madhya Pradesh](https://igod.gov.in/sg/MP/E011/organizations) | 25 | 1 / 1 | 25 | 0 | Yes | 19 | 6 | 13 |
| [PSUs / JVs / Companies / Societies - Madhya Pradesh](https://igod.gov.in/sg/MP/E059/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 7 | 2 | 5 |
| [Academies / Institutions - Madhya Pradesh](https://igod.gov.in/sg/MP/E009/organizations) | 24 | 1 / 1 | 24 | 0 | Yes | 17 | 7 | 10 |
| [Schemes / Programmes / Missions / Applications - Madhya Pradesh](https://igod.gov.in/sg/MP/SPMA/organizations) | 15 | 1 / 1 | 15 | 0 | Yes | 15 | 10 | 5 |
| [Others - Madhya Pradesh](https://igod.gov.in/sg/MP/OTHR/organizations) | 40 | 4 / 4 | 40 | 0 | Yes | 34 | 18 | 16 |
| [Departments - Maharashtra](https://igod.gov.in/sg/MH/E003/organizations) | 42 | 5 / 5 | 42 | 0 | Yes | 41 | 34 | 7 |
| [Attached / Subordinated Offices - Maharashtra](https://igod.gov.in/sg/MH/E005/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 4 | 1 | 3 |
| [Commissions/ Committees - Maharashtra](https://igod.gov.in/sg/MH/E013/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 2 | 2 |
| [Statutory / Autonomous Bodies - Maharashtra](https://igod.gov.in/sg/MH/E051/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 4 | 1 | 3 |
| [Directorates / Commissionerates - Maharashtra](https://igod.gov.in/sg/MH/E004/organizations) | 24 | 1 / 1 | 24 | 0 | Yes | 18 | 15 | 3 |
| [Boards / Undertakings - Maharashtra](https://igod.gov.in/sg/MH/E011/organizations) | 38 | 4 / 4 | 38 | 0 | Yes | 30 | 11 | 19 |
| [PSUs / JVs / Companies / Societies - Maharashtra](https://igod.gov.in/sg/MH/E059/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 5 | 4 | 1 |
| [Academies / Institutions - Maharashtra](https://igod.gov.in/sg/MH/E009/organizations) | 55 | 7 / 7 | 55 | 0 | Yes | 38 | 22 | 16 |
| [Schemes / Programmes / Missions / Applications - Maharashtra](https://igod.gov.in/sg/MH/SPMA/organizations) | 18 | 1 / 1 | 18 | 0 | Yes | 18 | 11 | 7 |
| [Others - Maharashtra](https://igod.gov.in/sg/MH/OTHR/organizations) | 71 | 11 / 11 | 71 | 0 | Yes | 59 | 35 | 24 |
| [Departments - Manipur](https://igod.gov.in/sg/MN/E003/organizations) | 41 | 5 / 5 | 41 | 0 | Yes | 33 | 18 | 15 |
| [Directorates / Commissionerates - Manipur](https://igod.gov.in/sg/MN/E004/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 5 | 4 | 1 |
| [Boards / Undertakings - Manipur](https://igod.gov.in/sg/MN/E011/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 1 | 3 |
| [Academies / Institutions - Manipur](https://igod.gov.in/sg/MN/E009/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 4 | 1 |
| [Schemes / Programmes / Missions / Applications - Manipur](https://igod.gov.in/sg/MN/SPMA/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 6 | 4 | 2 |
| [Others - Manipur](https://igod.gov.in/sg/MN/OTHR/organizations) | 19 | 1 / 1 | 19 | 0 | Yes | 16 | 8 | 8 |
| [Departments - Meghalaya](https://igod.gov.in/sg/ML/E003/organizations) | 48 | 6 / 6 | 48 | 0 | Yes | 43 | 39 | 4 |
| [Commissions/ Committees - Meghalaya](https://igod.gov.in/sg/ML/E013/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 4 | 1 |
| [Statutory / Autonomous Bodies - Meghalaya](https://igod.gov.in/sg/ML/E051/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 1 | 2 |
| [Directorates / Commissionerates - Meghalaya](https://igod.gov.in/sg/ML/E004/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 3 | 2 |
| [Boards / Undertakings - Meghalaya](https://igod.gov.in/sg/ML/E011/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 3 | 2 |
| [Academies / Institutions - Meghalaya](https://igod.gov.in/sg/ML/E009/organizations) | 12 | 1 / 1 | 12 | 0 | Yes | 8 | 3 | 5 |
| [Schemes / Programmes / Missions / Applications - Meghalaya](https://igod.gov.in/sg/ML/SPMA/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 6 | 5 | 1 |
| [Others - Meghalaya](https://igod.gov.in/sg/ML/OTHR/organizations) | 29 | 2 / 2 | 29 | 0 | Yes | 26 | 14 | 12 |
| [Departments - Mizoram](https://igod.gov.in/sg/MZ/E003/organizations) | 46 | 6 / 6 | 46 | 0 | Yes | 45 | 45 | 0 |
| [Directorates / Commissionerates - Mizoram](https://igod.gov.in/sg/MZ/E004/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 4 | 1 |
| [Academies / Institutions - Mizoram](https://igod.gov.in/sg/MZ/E009/organizations) | 11 | 1 / 1 | 11 | 0 | Yes | 6 | 5 | 1 |
| [Others - Mizoram](https://igod.gov.in/sg/MZ/OTHR/organizations) | 26 | 2 / 2 | 26 | 0 | Yes | 16 | 12 | 4 |
| [Departments - Nagaland](https://igod.gov.in/sg/NL/E003/organizations) | 28 | 2 / 2 | 28 | 0 | Yes | 28 | 24 | 4 |
| [Directorates / Commissionerates - Nagaland](https://igod.gov.in/sg/NL/E004/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 4 | 0 |
| [Others - Nagaland](https://igod.gov.in/sg/NL/OTHR/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 6 | 2 | 4 |
| [Departments - Odisha](https://igod.gov.in/sg/OD/E003/organizations) | 44 | 5 / 5 | 44 | 0 | Yes | 43 | 2 | 41 |
| [Attached / Subordinated Offices - Odisha](https://igod.gov.in/sg/OD/E005/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 2 | 2 | 0 |
| [Commissions/ Committees - Odisha](https://igod.gov.in/sg/OD/E013/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 5 | 1 | 4 |
| [Statutory / Autonomous Bodies - Odisha](https://igod.gov.in/sg/OD/E051/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 2 | 3 |
| [Directorates / Commissionerates - Odisha](https://igod.gov.in/sg/OD/E004/organizations) | 20 | 1 / 1 | 20 | 0 | Yes | 17 | 6 | 11 |
| [Boards / Undertakings - Odisha](https://igod.gov.in/sg/OD/E011/organizations) | 34 | 3 / 3 | 34 | 0 | Yes | 30 | 6 | 24 |
| [PSUs / JVs / Companies / Societies - Odisha](https://igod.gov.in/sg/OD/E059/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 3 | 2 | 1 |
| [Academies / Institutions - Odisha](https://igod.gov.in/sg/OD/E009/organizations) | 34 | 3 / 3 | 34 | 0 | Yes | 25 | 5 | 20 |
| [Schemes / Programmes / Missions / Applications - Odisha](https://igod.gov.in/sg/OD/SPMA/organizations) | 14 | 1 / 1 | 14 | 0 | Yes | 12 | 10 | 2 |
| [Others - Odisha](https://igod.gov.in/sg/OD/OTHR/organizations) | 65 | 9 / 9 | 65 | 0 | Yes | 54 | 24 | 30 |
| [Departments - Puducherry](https://igod.gov.in/sg/PY/E003/organizations) | 44 | 5 / 5 | 44 | 0 | Yes | 40 | 26 | 14 |
| [Boards / Undertakings - Puducherry](https://igod.gov.in/sg/PY/E011/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 2 | 0 | 2 |
| [Academies / Institutions - Puducherry](https://igod.gov.in/sg/PY/E009/organizations) | 10 | 1 / 1 | 10 | 0 | Yes | 6 | 4 | 2 |
| [Schemes / Programmes / Missions / Applications - Puducherry](https://igod.gov.in/sg/PY/SPMA/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 4 | 0 |
| [Others - Puducherry](https://igod.gov.in/sg/PY/OTHR/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 7 | 3 | 4 |
| [Departments - Punjab](https://igod.gov.in/sg/PB/E003/organizations) | 39 | 4 / 4 | 39 | 0 | Yes | 35 | 22 | 13 |
| [Commissions/ Committees - Punjab](https://igod.gov.in/sg/PB/E013/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 2 | 2 | 0 |
| [Statutory / Autonomous Bodies - Punjab](https://igod.gov.in/sg/PB/E051/organizations) | 25 | 1 / 1 | 25 | 0 | Yes | 22 | 21 | 1 |
| [Directorates / Commissionerates - Punjab](https://igod.gov.in/sg/PB/E004/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 4 | 3 | 1 |
| [Boards / Undertakings - Punjab](https://igod.gov.in/sg/PB/E011/organizations) | 22 | 1 / 1 | 22 | 0 | Yes | 16 | 5 | 11 |
| [Academies / Institutions - Punjab](https://igod.gov.in/sg/PB/E009/organizations) | 16 | 1 / 1 | 16 | 0 | Yes | 14 | 9 | 5 |
| [Schemes / Programmes / Missions / Applications - Punjab](https://igod.gov.in/sg/PB/SPMA/organizations) | 10 | 1 / 1 | 10 | 0 | Yes | 10 | 7 | 3 |
| [Others - Punjab](https://igod.gov.in/sg/PB/OTHR/organizations) | 18 | 1 / 1 | 18 | 0 | Yes | 13 | 8 | 5 |
| [Departments - Rajasthan](https://igod.gov.in/sg/RJ/E003/organizations) | 58 | 8 / 8 | 58 | 0 | Yes | 58 | 39 | 19 |
| [Commissions/ Committees - Rajasthan](https://igod.gov.in/sg/RJ/E013/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 5 | 2 | 3 |
| [Statutory / Autonomous Bodies - Rajasthan](https://igod.gov.in/sg/RJ/E051/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 3 | 3 | 0 |
| [Directorates / Commissionerates - Rajasthan](https://igod.gov.in/sg/RJ/E004/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 4 | 4 | 0 |
| [Boards / Undertakings - Rajasthan](https://igod.gov.in/sg/RJ/E011/organizations) | 28 | 2 / 2 | 28 | 0 | Yes | 20 | 15 | 5 |
| [Academies / Institutions - Rajasthan](https://igod.gov.in/sg/RJ/E009/organizations) | 24 | 1 / 1 | 24 | 0 | Yes | 21 | 10 | 11 |
| [Schemes / Programmes / Missions / Applications - Rajasthan](https://igod.gov.in/sg/RJ/SPMA/organizations) | 15 | 1 / 1 | 15 | 0 | Yes | 15 | 13 | 2 |
| [Others - Rajasthan](https://igod.gov.in/sg/RJ/OTHR/organizations) | 25 | 1 / 1 | 25 | 0 | Yes | 21 | 13 | 8 |
| [Departments - Sikkim](https://igod.gov.in/sg/SK/E003/organizations) | 27 | 2 / 2 | 27 | 0 | Yes | 22 | 17 | 5 |
| [Directorates / Commissionerates - Sikkim](https://igod.gov.in/sg/SK/E004/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 1 | 0 | 1 |
| [Academies / Institutions - Sikkim](https://igod.gov.in/sg/SK/E009/organizations) | 5 | 1 / 1 | 5 | 0 | Yes | 3 | 1 | 2 |
| [Others - Sikkim](https://igod.gov.in/sg/SK/OTHR/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 4 | 1 | 3 |
| [Departments - Tamil Nadu](https://igod.gov.in/sg/TN/E003/organizations) | 49 | 6 / 6 | 49 | 0 | Yes | 42 | 34 | 8 |
| [Commissions/ Committees - Tamil Nadu](https://igod.gov.in/sg/TN/E013/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 2 | 2 |
| [Statutory / Autonomous Bodies - Tamil Nadu](https://igod.gov.in/sg/TN/E051/organizations) | 6 | 1 / 1 | 6 | 0 | Yes | 5 | 2 | 3 |
| [Directorates / Commissionerates - Tamil Nadu](https://igod.gov.in/sg/TN/E004/organizations) | 16 | 1 / 1 | 16 | 0 | Yes | 15 | 10 | 5 |
| [Boards / Undertakings - Tamil Nadu](https://igod.gov.in/sg/TN/E011/organizations) | 38 | 4 / 4 | 38 | 0 | Yes | 34 | 15 | 19 |
| [PSUs / JVs / Companies / Societies - Tamil Nadu](https://igod.gov.in/sg/TN/E059/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 8 | 2 | 6 |
| [Academies / Institutions - Tamil Nadu](https://igod.gov.in/sg/TN/E009/organizations) | 40 | 4 / 4 | 40 | 0 | Yes | 30 | 20 | 10 |
| [Schemes / Programmes / Missions / Applications - Tamil Nadu](https://igod.gov.in/sg/TN/SPMA/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 7 | 5 | 2 |
| [Others - Tamil Nadu](https://igod.gov.in/sg/TN/OTHR/organizations) | 46 | 6 / 6 | 46 | 0 | Yes | 34 | 12 | 22 |
| [Departments - Telangana](https://igod.gov.in/sg/TS/E003/organizations) | 41 | 5 / 5 | 41 | 0 | Yes | 36 | 22 | 14 |
| [Commissions/ Committees - Telangana](https://igod.gov.in/sg/TS/E013/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 8 | 5 | 3 |
| [Statutory / Autonomous Bodies - Telangana](https://igod.gov.in/sg/TS/E051/organizations) | 15 | 1 / 1 | 15 | 0 | Yes | 15 | 6 | 9 |
| [Directorates / Commissionerates - Telangana](https://igod.gov.in/sg/TS/E004/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 8 | 4 | 4 |
| [Boards / Undertakings - Telangana](https://igod.gov.in/sg/TS/E011/organizations) | 16 | 1 / 1 | 16 | 0 | Yes | 13 | 4 | 9 |
| [Academies / Institutions - Telangana](https://igod.gov.in/sg/TS/E009/organizations) | 11 | 1 / 1 | 11 | 0 | Yes | 10 | 6 | 4 |
| [Schemes / Programmes / Missions / Applications - Telangana](https://igod.gov.in/sg/TS/SPMA/organizations) | 13 | 1 / 1 | 13 | 0 | Yes | 13 | 8 | 5 |
| [Others - Telangana](https://igod.gov.in/sg/TS/OTHR/organizations) | 44 | 5 / 5 | 44 | 0 | Yes | 37 | 23 | 14 |
| [Departments - Tripura](https://igod.gov.in/sg/TR/E003/organizations) | 30 | 2 / 2 | 30 | 0 | Yes | 28 | 26 | 2 |
| [Commissions/ Committees - Tripura](https://igod.gov.in/sg/TR/E013/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 4 | 3 | 1 |
| [Statutory / Autonomous Bodies - Tripura](https://igod.gov.in/sg/TR/E051/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 3 | 3 | 0 |
| [Directorates / Commissionerates - Tripura](https://igod.gov.in/sg/TR/E004/organizations) | 13 | 1 / 1 | 13 | 0 | Yes | 13 | 12 | 1 |
| [Boards / Undertakings - Tripura](https://igod.gov.in/sg/TR/E011/organizations) | 9 | 1 / 1 | 9 | 0 | Yes | 9 | 5 | 4 |
| [Academies / Institutions - Tripura](https://igod.gov.in/sg/TR/E009/organizations) | 15 | 1 / 1 | 15 | 0 | Yes | 14 | 4 | 10 |
| [Others - Tripura](https://igod.gov.in/sg/TR/OTHR/organizations) | 21 | 1 / 1 | 21 | 0 | Yes | 20 | 11 | 9 |
| [Departments - Uttar Pradesh](https://igod.gov.in/sg/UP/E003/organizations) | 53 | 7 / 7 | 53 | 0 | Yes | 50 | 34 | 16 |
| [Attached / Subordinated Offices - Uttar Pradesh](https://igod.gov.in/sg/UP/E005/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 3 | 3 | 0 |
| [Commissions/ Committees - Uttar Pradesh](https://igod.gov.in/sg/UP/E013/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 6 | 4 | 2 |
| [Statutory / Autonomous Bodies - Uttar Pradesh](https://igod.gov.in/sg/UP/E051/organizations) | 10 | 1 / 1 | 10 | 0 | Yes | 10 | 6 | 4 |
| [Directorates / Commissionerates - Uttar Pradesh](https://igod.gov.in/sg/UP/E004/organizations) | 13 | 1 / 1 | 13 | 0 | Yes | 10 | 5 | 5 |
| [Boards / Undertakings - Uttar Pradesh](https://igod.gov.in/sg/UP/E011/organizations) | 23 | 1 / 1 | 23 | 0 | Yes | 15 | 9 | 6 |
| [PSUs / JVs / Companies / Societies - Uttar Pradesh](https://igod.gov.in/sg/UP/E059/organizations) | 4 | 1 / 1 | 4 | 0 | Yes | 2 | 1 | 1 |
| [Academies / Institutions - Uttar Pradesh](https://igod.gov.in/sg/UP/E009/organizations) | 35 | 3 / 3 | 35 | 0 | Yes | 30 | 20 | 10 |
| [Schemes / Programmes / Missions / Applications - Uttar Pradesh](https://igod.gov.in/sg/UP/SPMA/organizations) | 22 | 1 / 1 | 22 | 0 | Yes | 22 | 18 | 4 |
| [Others - Uttar Pradesh](https://igod.gov.in/sg/UP/OTHR/organizations) | 42 | 5 / 5 | 42 | 0 | Yes | 35 | 14 | 21 |
| [Departments - Uttarakhand](https://igod.gov.in/sg/UK/E003/organizations) | 50 | 6 / 6 | 50 | 0 | Yes | 44 | 37 | 7 |
| [Commissions/ Committees - Uttarakhand](https://igod.gov.in/sg/UK/E013/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 8 | 4 | 4 |
| [Statutory / Autonomous Bodies - Uttarakhand](https://igod.gov.in/sg/UK/E051/organizations) | 19 | 1 / 1 | 19 | 0 | Yes | 17 | 4 | 13 |
| [Directorates / Commissionerates - Uttarakhand](https://igod.gov.in/sg/UK/E004/organizations) | 10 | 1 / 1 | 10 | 0 | Yes | 9 | 9 | 0 |
| [Boards / Undertakings - Uttarakhand](https://igod.gov.in/sg/UK/E011/organizations) | 22 | 1 / 1 | 22 | 0 | Yes | 19 | 11 | 8 |
| [Academies / Institutions - Uttarakhand](https://igod.gov.in/sg/UK/E009/organizations) | 16 | 1 / 1 | 16 | 0 | Yes | 13 | 9 | 4 |
| [Schemes / Programmes / Missions / Applications - Uttarakhand](https://igod.gov.in/sg/UK/SPMA/organizations) | 8 | 1 / 1 | 8 | 0 | Yes | 8 | 5 | 3 |
| [Others - Uttarakhand](https://igod.gov.in/sg/UK/OTHR/organizations) | 45 | 5 / 5 | 45 | 0 | Yes | 34 | 23 | 11 |
| [Departments - West Bengal](https://igod.gov.in/sg/WB/E003/organizations) | 53 | 7 / 7 | 53 | 0 | Yes | 51 | 24 | 27 |
| [Attached / Subordinated Offices - West Bengal](https://igod.gov.in/sg/WB/E005/organizations) | 3 | 1 / 1 | 3 | 0 | Yes | 1 | 1 | 0 |
| [Commissions/ Committees - West Bengal](https://igod.gov.in/sg/WB/E013/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 6 | 2 | 4 |
| [Statutory / Autonomous Bodies - West Bengal](https://igod.gov.in/sg/WB/E051/organizations) | 18 | 1 / 1 | 18 | 0 | Yes | 17 | 12 | 5 |
| [Directorates / Commissionerates - West Bengal](https://igod.gov.in/sg/WB/E004/organizations) | 13 | 1 / 1 | 13 | 0 | Yes | 12 | 3 | 9 |
| [Boards / Undertakings - West Bengal](https://igod.gov.in/sg/WB/E011/organizations) | 21 | 1 / 1 | 21 | 0 | Yes | 18 | 5 | 13 |
| [Academies / Institutions - West Bengal](https://igod.gov.in/sg/WB/E009/organizations) | 33 | 3 / 3 | 33 | 0 | Yes | 26 | 17 | 9 |
| [Schemes / Programmes / Missions / Applications - West Bengal](https://igod.gov.in/sg/WB/SPMA/organizations) | 7 | 1 / 1 | 7 | 0 | Yes | 7 | 4 | 3 |
| [Others - West Bengal](https://igod.gov.in/sg/WB/OTHR/organizations) | 42 | 5 / 5 | 42 | 0 | Yes | 38 | 13 | 25 |
| [New Additions](https://igod.gov.in/organization/new_additions) | ? | 1 / 1 | 15 | ? | No | 15 | 13 | 2 |

## Exact source failures and remaining acquisition

No failed pages in the current manifest.
Pending source pages: 0. Exact URLs and per-page retry/cache evidence are in `data/collection/igod-2026-10-14/manifest.json`.

## Secondary sources and administrative coverage

- National Portal HTML was available through normal TLS curl, but its directory fields still contain dynamic Loading/zero values. The earlier normal Chrome check returned 403. It is IGOD-derived and is not counted as an independent inventory; no export or bypass was invented.
- data.gov.in redirects to www.data.gov.in, whose robots.txt disallows automated retrieval. Dataset collection stopped. Use a permitted official dataset/API workflow before resuming that source.
- LGD's public homepage displayed 36 states/UTs, 784 districts, 5,050 urban local bodies and 262,839 rural local bodies. These are administrative denominators, not website counts. The normal state-code link inspection timed out and the district-code view showed no data table in this environment. No identifiers or URLs were fabricated.
- Source directory snapshots/metadata are cached. Destination responses retain HTTP status, redirects, body hash/size and public page title only; homepage/contact/form bodies are discarded. Anonymous session cookies and CSRF tokens are removed from stored retrieval evidence; no credentials, OTPs or filled forms are collected.

## Publication and recovery

HTTP source links, unexpected-host redirects, inaccessible destinations, robot-disallowed checks, missing URLs and ambiguous external operators remain separate from ordinary public links. Rejected/edited review decisions survive reruns. The nine curated records retain their original descriptions, URLs, dates and browser policy. Adding directory data never grants a readability profile or in-app browser access.

```powershell
python tools/discover_government_sites.py --source igod --batch igod-2026-10-14 --delay 1
python tools/discover_government_sites.py --source igod --batch igod-2026-10-14 --retry-failed --delay 1
python tools/prepare_discovery_catalog.py --batch igod-2026-10-14 --check --workers 4 --timeout 10
python tools/catalog_pipeline.py stage-discovery data/collection/igod-2026-10-14/normalized_candidates.json
python tools/catalog_pipeline.py publish data/collection/igod-2026-10-14/local_approvals.json
python tools/check_catalog.py
python tools/inventory_audit.py
python tools/catalog_pipeline.py report
python tools/package_extension.py
```

Before public distribution: obtain and record NIC reproduction permission, review publication labels and inaccessible/partner destinations, obtain release signing and finish the existing accessibility/extension/device gates. The next data task is the documented unresolved source/health/ownership gap, not a new invented service inventory.
