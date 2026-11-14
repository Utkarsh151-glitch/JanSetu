# Phase 14: security, privacy and legal review (engineering part)

18 October 2026. This review covers the code as it stands after Phases 7–12. It was done by reading the source and running tests. **It is not an independent security audit or a legal review.** Both are still needed before public release (blockers RB-07 and RB-08).

## Attack surface

| Area | Implementation | Finding | Status |
| --- | --- | --- | --- |
| Backend, authentication, sessions | None exist | Nothing to attack. Guard tests fail if a network client, account SDK, endpoint or secret appears (`tools/test_account_boundary.py`). | OK |
| Exported components | The launcher activity, which accepts only `jansetu-local://application/<uuid>` and navigates inside the app. The document provider. Two receivers, not exported. | A deep link can open an application page only if its random UUID is known; nothing is returned to the caller. Pinned by `tools/test_android_manifest.py`. | OK |
| Document provider and URI grants | `MANAGE_DOCUMENTS` only; read-only; off by default. HMAC IDs expire after one hour and can be revoked. No recents, search or thumbnails. | DocumentsUI may give the receiving app a persistable grant. Expiry and revocation still end reads. A receiving app can copy the file within the hour; the screen says so. | Accepted residual |
| Keystore | Separate AES-256-GCM keys for the tracker (`jansetu_tracker_v1`) and the vault (`jansetu_vault_v1`), plus an HMAC key for the picker. Non-exportable; not tied to user authentication. | Anyone using the unlocked phone can open both stores. StrongBox is not required. | **Medium, open:** offer an optional "require device unlock" for My Documents and My Applications. Needs physical-device testing; tracked for the next build. |
| Key invalidation | Locked vault state; documents kept; imports refused; a new key is never generated over existing documents. The tracker keeps unreadable records. | Tested (`VaultRepositoryTest`, existing tracker tests). | OK |
| File parsing | Content sniffing; MIME agreement; structure checks; `BitmapFactory` bounds and sampled decode (60-megapixel cap); `PdfRenderer` page cap; size, count and quota limits. | Relies on the platform decoders, which Android security updates patch. | Accepted residual |
| Encrypted storage | Segmented AES-GCM per document; encrypted metadata; `secure_delete`; no plaintext temporary files (tested); proxy file descriptors for viewing. | Deleting one file does not guarantee flash erasure; "Remove all" deletes the key. | Accepted and disclosed |
| Database migrations | Tracker v1→v2 additive (tested). Vault v1 refuses downgrade or upgrade without a migration. | Any future vault migration must be additive and tested. | OK |
| In-app browser | National Portal only. HTTPS host policy (`BrowserPolicy`); TLS errors cancel; no mixed content; file and content access off; file uploads refused; downloads handed to the browser; Safe Browsing on. Phase 12 changed only the text-zoom start value and saved reading position. | Allowlists unchanged (`site_interventions.py` passes). | OK |
| Logging, analytics, crash reports | No `Log` calls in app code. Records, documents and choices have redacted `toString`. No analytics or crash SDK (guard test). | — | OK |
| Notifications | Generic text only; private lock-screen visibility | — | OK |
| Backup and device transfer | Every domain excluded; data in `no_backup` (tested) | — | OK |
| Screens | `FLAG_SECURE` on My Documents, the document page and Prepare documents | Tracker screens are not secure-flagged; reference numbers are masked until revealed. | Low; review with the in-app lock item |
| Dependencies | AndroidX core 1.17.0, activity-compose 1.12.3, navigation 2.9.8, Compose BOM 2026.03.00, kotlinx-serialization 1.9.0. Test-only libraries. | **Phase 17 OSV.dev query** (`data/validation_reports/phase17-dependency-scan.json`): 0 known advisories for the 119 Android release runtime dependencies. The local build and research tools are not shipped, but `cryptography` 46.0.5 (11 advisories, 3 high; fixed by 50.0.0) and `pypdf` 6.7.4 (71 advisories, 10 high, mostly resource exhaustion on crafted PDFs; fixed by 6.19.0) need upgrading. This is not a full SCA scan. | Open: upgrade the tool environment (owner approval); repeat before release |
| Build configuration | Release signing only from four environment variables; no key in the repository (guard test). Release `isMinifyEnabled = false`. | Without R8 the APK is larger and keeps symbol names. This is not a vulnerability, but it should be decided. | Low: test an R8 release build in Phase 15 |
| Release artifacts | Tests check that the APKs and extension carry no research files, quotes, AI advice, staging or packets. The extension leaves website profiles out. | Final store bundle still to be inspected (Phase 15). | Partially done |
| Extension | Permissions `sidePanel` and `activeTab` only; no host permissions, content scripts or page stylesheets (Phase 17) | The validator fails if styling capability returns without a reviewed decision | OK |

## Privacy

- The privacy policy (English and Hindi), dated 18 October 2026, now covers:
  - My Documents
  - preparation choices
  - file-picker sharing
  - exported copies
  - application export
  - the withdrawn extension style
- The Hindi wording is JanSetu's own and needs an independent review (RB-04).
- **Retention:** all personal data stays on the device until the person deletes it or uninstalls. JanSetu keeps no server copy, because no server exists. Support emails are the only data JanSetu receives; the owner must still decide how long to keep them (RB-07).
- **Data safety:** see `PLAY_LISTING.md`. Processing on the device is not "collection". User-initiated sharing and export must be checked against Play's current guidance on the final build.

## Legal and permission evidence register

| Item | Evidence on file | Status |
| --- | --- | --- |
| NIC/IGOD data reuse | None | **Blocking (RB-01)** |
| Official-page text reuse | None. Internal evidence is kept out of artifacts. | **Blocking (RB-02)** |
| Image, illustration, font and branding rights | `PHASE5_ASSET_CREDITS.md` (original artwork); no trademark search | **Blocking (RB-08)** |
| Privacy policy public URL | None | **Blocking (RB-07)** |
| Platform disclosures (Data safety, government-information app declaration) | Drafts only | **Blocking (RB-07)** |
| Independent security and legal review | None | **Blocking** |
| Catalogue provenance | Source attribution on every record; review and evidence workflow | OK for development; public release depends on RB-01 and RB-02 |

## Backend reliability

Not applicable: there is no backend. If Phase 8 approves one, this section must be completed before any account feature ships. It would cover environments, migrations, backups and restore tests, monitoring, privacy-preserving error tracking, rate limits, health checks, incident response, secret rotation and a recovery runbook.

## Exit-gate status

There is no unresolved **critical** engineering finding. One **medium** item is open: an optional in-app lock. The phase stays **BLOCKED** on legal, permission, disclosure and independent-review evidence: RB-01, RB-02, RB-04, RB-07 and RB-08.
