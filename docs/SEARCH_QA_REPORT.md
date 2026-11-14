# Search QA report — Phase 3C

Actual JDK 17 run on the full generated runtime asset: 7,347 websites and 52 services. `SearchPerformanceTest` uses 14 queries, one warm-up pass and five measured repetitions (70 timed queries). Raw results: `data/validation_reports/phase3c-search.json`. Reproduce with `JANSETU_SEARCH_BENCHMARK` set to that absolute path, then run `:app:testDebugUnitTest --rerun-tasks` and this report generator.

| Measurement | Observed |
| --- | --- |
| Index preparation (ms) | 83.80 |
| Median query (ms) | 41.88 |
| P95 query (ms) | 66.71 |

These are observed JVM measurements, not Android UI frame/startup or production latency guarantees. The Phase 3A eight-query run with 25 services measured median/P95 34.22/66.19 ms; the expanded query mix and machine load differ, so this is not a controlled before/after performance comparison. Phase 3A raw before/after JSON remains retained. Search/index architecture is unchanged.

| Query | Actual top three |
| --- | --- |
| I need a scholarship for engineering | service: `aicte-swanath-degree`; service: `aicte-pragati-degree`; service: `aicte-saksham-degree` |
| How can I get a caste certificate | service: `tn-community-certificate`; service: `kl-caste-obc-certificate`; service: `kl-community-sc-st-certificate` |
| Find government jobs | service: `ncs-government-job-search`; website: `igod-b1b71f174b533174`; website: `national-career-service` |
| I want to apply for a pension | service: `tn-widow-pension`; service: `tn-disability-pension`; service: `tn-old-age-pension` |
| How do I obtain a birth certificate | service: `tn-birth-certificate-print` |
| I need a subsidy for my small business | service: `pmegp-enterprise-support` |
| DigiLocker | website: `digilocker` |
| file a complaint | service: `cpgrams-public-grievance`; service: `cpgrams-grievance-appeal`; website: `igod-9278b77470ecaa55` |
| Kerala income certificate | service: `kl-income-certificate` |
| Delhi OBC certificate | service: `dl-obc-certificate` |
| Odisha resident certificate | service: `od-resident-certificate` |
| Puducherry residence certificate | service: `py-residence-certificate` |
| get a new instant e-PAN | service: `instant-epan-new` |
| register as a new voter | service: `eci-new-elector-form6` |

## Correctness and limits

JVM regressions verify top service IDs for Kerala income, Delhi OBC, Odisha resident and Puducherry residence queries with explicit state/type filters; e-PAN/new-voter intents; exact DigiLocker portal preference; category/type/central filters; no Bihar pension or Himachal/local service coverage. Existing scholarship/jobs/pension/birth-print/subsidy/complaint intents remain tested. No search result decides eligibility or a current intake window.

Generic national certificate/pension queries can rank records from different states. A state filter restricts service jurisdiction; it does not convert a portal into a service. Kerala Caste/OBC and Community/SC-ST naming remain distinct. Source-reviewed information-only services remain searchable but have no Apply or service-tracker shortcut. Confirmed Delhi/Kerala actions open the reviewed shared official login entry; authentication and submission are not claimed. Android service UI tests exercise the exact URL callbacks and unknown/no-Apply presentation; complete outcomes are in [QA](QA_REPORT.md).
