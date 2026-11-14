# Phase 8: account and backend architecture decision

18 October 2026. Status: **proposed; awaiting the project owner's decision** (release blocker RB-13). This blocks only optional account and sync features. The local product does not depend on it.

## Current state (inspected)

- No backend, server, database host, API or account exists. The app contains no network client: there is no HTTP library and no `HttpURLConnection`.
- The app's only network activity:
  - opening official websites in the device browser
  - the National Portal in a limited WebView
  - the email app for support drafts
- Everything personal is on the device: preferences, My Applications (encrypted) and My Documents (encrypted, separate key). All of it is excluded from backup and transfer.
- Browsing, search, service pages, the tracker, the vault and preparation all work offline with no account.

## Does any feature need an account?

| Feature | Needs an account? | Why |
| --- | --- | --- |
| Directory, search, service pages, website profiles | No | Public information, bundled offline. |
| My Applications and reminders | No | Local and encrypted. Reminders are local alarms. |
| My Documents and Prepare documents | No | Local and encrypted. Uploads happen through the person's own browser. |
| Catalogue updates | No | Signed files today. A future download channel needs a server, but not an identity. |
| Report a problem | No | An email draft today. A form endpoint would need rate limiting, not accounts. |
| Using the same records on several devices, or recovering after losing a phone | **Yes** | Needs a server-side identity plus end-to-end encrypted sync (Phase 9). |

**Conclusion:** an account is justified only by optional multi-device sync and recovery. No current feature needs one.

## Options considered (if the owner chooses to offer sync)

| Option | Strengths | Weaknesses for JanSetu |
| --- | --- | --- |
| A. No accounts (status quo) | No personal data leaves the device; no server to breach; no operating cost; the simplest legal position | No recovery after losing a device; no multi-device use |
| B. Managed identity platform (for example Firebase Authentication or Google Identity) | Mature, cheap at small scale, Android SDKs | Data location and processor terms must be checked against Indian law. It adds a third-party SDK and analytics risk to the APK, and a vendor dependency. |
| C. Open-source managed backend (for example Supabase, hosted in an Indian region) | Postgres; row-level security; self-hostable later | Operating responsibility grows; the region and terms must be confirmed; it still needs E2EE for documents |
| D. Self-hosted identity (for example Keycloak or Ory) on an Indian cloud | Full control over residency and logs | The highest operating and security burden: patching, backups, incident response, on-call |
| E. Passkeys (Android Credential Manager) plus a small server | Phishing-resistant; no passwords stored at all | Still needs a server and a recovery path for people without passkey-capable devices |
| F. Phone-number OTP | Familiar in India | SMS cost, SIM-swap takeover risk and telecom sender-registration requirements. A phone number is also strongly identifying, which conflicts with data minimisation. |

**Recommendation if sync is approved:**
- Passkeys first (E), with a verified-email fallback.
- A minimal server that stores only an account ID, credentials (public keys or a salted password hash), a device list, wrapped vault keys and opaque encrypted blobs (see Phase 9).
- Hosting in an Indian region, with a documented data-processing agreement.
- No third-party analytics SDK in the APK.
- Hashed passwords only (Argon2id); rate limits; generic errors so accounts cannot be enumerated; short-lived access tokens with revocable refresh tokens; account deletion that removes server data and reports when it is done.

## Legal and operational prerequisites (the owner decides; not legal advice)

- Indian data-protection law (the Digital Personal Data Protection Act, 2023 and its rules) would apply to account data: notice and consent, purpose limitation, erasure, breach handling and a contact for grievances. Get advice before collecting any account data.
- Hosting region, cost ceiling, processor contracts, on-call responsibility and an incident process.
- An updated privacy policy, in-app disclosures and Play Data safety answers before any account feature ships.

## Threat model for a future account system

| Threat | Required control |
| --- | --- |
| Credential stuffing or account takeover | Passkeys; rate limiting; breached-password checks for any password fallback; alerts for new sign-ins |
| Session theft on the device | Tokens encrypted with a Keystore key; short lifetimes; server-side revocation on sign-out or "sign out all devices" |
| Account enumeration | The same response for known and unknown emails during sign-up and recovery |
| Recovery abuse | Recovery never decrypts data by itself: a recovery key is needed for E2EE data (Phase 9). Notification on every recovery attempt. |
| Server breach | The server holds only ciphertext and wrapped keys; minimal metadata; secrets in a managed secret store, never in the repository or APK |
| Insider access | E2EE means operators cannot read documents or records; access-controlled audit logs |
| Silent migration of local data | Never: sign-in starts empty, and moving local data needs an explicit choice for each data type (Phase 10 and 9) |
| A backend outage presented as success | Every account action reports Unavailable, Offline or Failed explicitly; local features keep working |

## What Phase 8 delivers now

1. This decision record and threat model.
2. Guard tests (`tools/test_account_boundary.py`) that fail if a network client, account endpoint or hard-coded secret appears in the app. Such a change would need a recorded decision, a privacy-policy update and Data safety review first.
3. No account code, mock UI or "sign in" screen. A non-working sign-in would be a dead or misleading control.

## Exit-gate status

Authentication is not implemented, because no current feature needs it. The local-only behaviour is tested by the existing suites and the new guard tests. Phase 8 stays `NEEDS_REVIEW` until the owner either:
- accepts option A for launch, or
- approves sync with a hosting and legal decision, which restarts this phase with option E.
