# Architecture — current implementation

- `app/src/main/assets/catalog.json` is the authoritative website/editorial catalog. `data/services/catalog.json` is a separate reviewed service catalog and exact existing website-operator registry; `service_catalog.merged_catalog` validates and merges them for runtime/check/report tools, preserving the website source and queue bytes. `tools/package_catalog.py` creates a compact offline runtime projection under `app/build/generated/runtimeAssets`; Gradle packages that projection. All IDs, URLs, status/dates, source attribution and browser permissions are retained; redundant research evidence stays in the full source and collection files. Content is separate from UI code and includes stable IDs, category associations, source URLs, source type, and separate source/health or manual review dates.
- `CatalogRepository` abstracts catalog loading. The app uses `LocalCatalogRepository`, which verifies a pinned-key signed local update or recovers its bundled asset on an IO dispatcher. `AtomicFile` preserves the previous valid package across interrupted writes. Signature, hash, version, count, size and catalog/browser constraints are checked before replacement; Settings imports through Android OpenDocument and can restore the bundle. Query computation uses a background dispatcher; search results use LazyColumn. Home renders a bounded preview. A future remote or Room-backed repository can replace it without changing screen contracts.
- `CatalogParser` rejects unattributed, unpublished, duplicate, malformed, or non-HTTPS records before publication in the UI.
- `LocalSettings` stores onboarding completion, language, state preference, favorite IDs, recent search queries, and recent IDs in app-private `SharedPreferences`. No sensitive government data is stored.
- Compose UI is built with Material 3 and Navigation Compose. The app uses a local-first flow. The National Portal has a deliberately limited WebView path; other sites and cross-host links use `ACTION_VIEW` in the user's browser. See [WEBVIEW_SECURITY.md](WEBVIEW_SECURITY.md).
- `tools/catalog_pipeline.py` stages editor-provided CSV/JSON records, performs structural and duplicate checks, and publishes only with a recorded approval. Its queue is not part of the app. See [DATA_VERIFICATION.md](DATA_VERIFICATION.md).
- `extension/` is a separate Chrome side panel. `tools/package_extension.py` packages the checked compact catalog projection and privacy policy into it. A user-invoked static stylesheet can improve readability on one explicitly reviewed public page; there are no content scripts or broad host permissions. See [EXTENSION.md](EXTENSION.md).

The app name is defined as `app_name` in `app/build.gradle.kts`; application ID and version are in the same file. Light and dark palettes are in `ui/Theme.kt`. The actual expanded catalog is measured in QA_REPORT.md. Full source evidence remains in collection data; the bundled asset supports offline search. A compact versioned asset and signed manual update manifest are implemented. The private development signing key stays in ignored `.local/`; only its public key is bundled. No backend, network update service or Room database is needed for the local file workflow. Exact organization grouping and configurable category groups are data driven. `CitizenSearch` adds a small service index to the existing website index, shares deterministic normalization/scoring, ranks curated intents, distinguishes result types and retains lazy background search. Service facts carry evidence IDs; nullable fields mean unknown. `ServiceScreen` opens official links externally and collects no application data. `IntegrationRegistry` defines provider-neutral schemas and failure fallbacks; its default provider list is empty. See DEPLOYMENT.md and COMPATIBILITY.md.

Dependency versions were checked against the official [Android Gradle Plugin compatibility notes](https://developer.android.com/build/releases/agp-8-13-0-release-notes), [Compose BOM guidance](https://developer.android.com/develop/ui/compose/bom), [Compose runtime release notes](https://developer.android.com/jetpack/androidx/releases/compose-runtime), [AndroidX release notes](https://developer.android.com/jetpack/androidx/versions), [Kotlin Gradle compatibility](https://kotlinlang.org/docs/gradle-configure-project.html), and [kotlinx.serialization releases](https://github.com/Kotlin/kotlinx.serialization/releases) on 12 October 2026. The BOM is pinned to March 2026 because later Compose 1.12 libraries require AGP 9.2 and API 37; this project uses the stable AGP 8.13/JDK 17 path.


## Phase 3B local applications

A separate ApplicationRepository/SQLiteOpenHelper store owns encrypted application and append-preserved history payloads under noBackupFilesDir. Compose list/editor/detail screens call it on IO; catalog delivery has no write path into the tracker. ServiceSnapshot retains the creation-time identity, jurisdiction and official links. Revision checks prevent stale edits. AlarmManager uses one inexact PendingIntent per record UUID, record revalidation and persistent delivery guards. See [APPLICATION_TRACKER_REPORT.md](APPLICATION_TRACKER_REPORT.md) for schema migration, corruption, privacy and scheduling decisions. No cloud profile or live government status provider exists.

## Phase 7: My Documents (local vault)

The `in.jansetu.app.vault` package is a separate store with its own key. It shares nothing with the tracker.

- **`VaultFormat`** (pure Kotlin): types, signatures, validation rules, limits, query and sort.
- **`SegmentedCipher`** (pure Kotlin): streaming AES-256-GCM in segments, with a random key per document sealed by a `VaultKeyCipher`. It supports random-access reads.
- **`VaultStore`** (Android):
  - `KeystoreVaultKeys` (alias `jansetu_vault_v1`)
  - `VaultDatabase` (`no_backup/vault/documents.db`, with encrypted payloads)
  - `EncryptedVaultRepository` (objects in `no_backup/vault/objects/<uuid>.jsv`)
  - `VaultProxyFiles`: read-only `openProxyFileDescriptor`
  - `AndroidDocumentInspector`: `BitmapFactory` and `PdfRenderer` checks
  - `VaultPickerAccess`: opt-in setting and the HMAC key behind picker IDs
  - `VaultDependencies`: a singleton, with a test override
- **`VaultDocumentsProvider`**: a `DocumentsProvider` restricted by `MANAGE_DOCUMENTS`, read-only, with time-limited IDs.
- **`Preparation`** (pure Kotlin): derives requirement items from `Service.documents` and `Service.conditionalDocuments`, and records the person's choices.
- **UI** (`ui/DocumentScreens.kt`): My Documents, the document page and Prepare documents. Routes are `documents`, `document/{id}` and `prepare/{serviceId}`. All three screens use `FLAG_SECURE`.

The design, threat model and failure behaviour are in [PHASE7_DOCUMENT_VAULT.md](PHASE7_DOCUMENT_VAULT.md).
