# Capability and compatibility report

Updated: 16 October 2026. These are implemented limits, not universal compatibility claims.

| Surface | Capability | Limits and evidence |
| --- | --- | --- |
| Android directory | Offline search, four category groups, organization-name browsing, region filters, favorites, history, two guides, signed local catalog import/restore | Actual 7,347-record asset. Repository and Compose tests exercise local workflows; source coverage remains incomplete. |
| Hindi | Navigation, catalog management, privacy policy, JanSetu explanations and two guide translations | Source titles/organizations preserved. Translations are JanSetu guidance; independent language review and broader locale/number formatting remain open. |
| National Portal (`www.india.gov.in`) | Limited in-app WebView, navigation/progress/retry, guide, external fallback, reversible larger text | Only this reviewed entry can enable in-app browsing. Authentication/payment paths and cross-host links hand off externally. File upload requests are cancelled with external-browser guidance; downloads use external browser. |
| Other 7,346 website records | Native JanSetu companion page and external browser | Catalog admission does not enable WebView, CSS changes, a working transaction or ownership certification. |
| Chrome NSP home page | User-invoked packaged readability CSS; restore; automatic removal when a tab URL leaves the reviewed route | Only exact HTTPS `scholarships.gov.in/home`, no query string. Other pages receive no styling authorization. Node tests cover route cleanup; panel smoke uses mocked extension APIs. Installed toolbar, real activeTab permission and live form compatibility remain unverified. |
| Native government APIs | Provider-neutral adapter/schema/error/fallback framework | Default registry is empty. No authorized provider/credentials have been configured; no live status, application or transaction integration operates. Synthetic adapter tests demonstrate validation and fallback only. |

The integration descriptor records source documentation, authorization, public-input and output schemas, operations, rate-limit metadata and limitations. A future adapter must implement actual provider-specific authorization/rate control and pass a live permitted integration test before it can be enabled. The current application exposes official website actions, with no simulated native service button.

## Browser checks

Unit tests cover safe HTTPS, host boundaries, disallowed schemes and authentication/payment handoff policy. Source review retains TLS cancellation, no JavaScript bridge, disabled file/content access and explicit lifecycle cleanup. This is not a complete penetration test. Live invalid-certificate, payment/identity, upload/download and multiple-device browser flows remain release gates. There is no TLS bypass to make a failing portal appear functional.

## What improves the website experience

JanSetu supplies consistent native discovery, summaries and guides before visiting a portal. The Chrome pilot changes text spacing, link presentation and focus outlines on one reviewed page. Android text zoom is limited to the National Portal browser. Neither changes government infrastructure, accelerates a slow government response, translates official forms or redesigns all catalog websites. Broader page changes need individual permission, security, accessibility and regression review.
