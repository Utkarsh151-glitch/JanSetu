# Local operation and release preparation

Updated: 16 October 2026. Nothing is deployed publicly. The catalog's directory-derived entries remain local development data; the public-release validator still rejects them.

## Build and run

Use JDK 17, Android SDK 36, Python 3.10+ and the Gradle wrapper. Install the pinned Python tools with `python -m pip install -r tools/requirements.txt`. Gradle calls Python to generate compact runtime assets; set `JANSETU_PYTHON` to an interpreter path if necessary.

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug --console=plain
$adbPath=Join-Path $env:LOCALAPPDATA 'Android\Sdk\platform-tools\adb.exe'
& $adbPath install -r app/build/outputs/apk/debug/app-debug.apk
& $adbPath shell am start -n in.jansetu.app/.MainActivity
```

Start `Medium_Phone_API_37.0` from Android Studio Device Manager first, or choose a connected Android 8+ phone. The installed VS Code emulator extension launches existing Android virtual devices; it does not supply the SDK or system image itself.

## Editorial workflow

1. Import a permitted CSV/JSON dataset with `tools/catalog_pipeline.py`; use its `--help` for stage/review/export commands. Retain original provenance and recorded decisions.
2. Validate with `python tools/check_catalog.py`. Review the queue and source evidence, canonical URLs, dates and scope. Website listings are separate from verified services and from ownership certifications.
3. Publish only an explicitly reviewed approval file with `python tools/catalog_pipeline.py publish PATH_TO_APPROVALS.json`. Run coverage/quality reports and relevant pipeline tests. Do not promote pending cases by changing counts or verification labels.
4. Regenerate the extension with `python tools/package_extension.py`. This packages the compact projection; the extension currently receives updates by repackaging/reloading it.
5. Deliver a signed Android catalog package as described below. Ordinary catalog content changes do not require Kotlin edits or another APK installation.

The editor is the local operating-system account with access to the repository and signing key. There is no public admin endpoint, backend account, database, migration or remote role system. Repository review and local file access are the authority boundary; this setup is unsuitable for an untrusted multi-user hosted admin service.

## Signed local updates

The APK pins `app/src/main/assets/catalog-update-public.der`. The corresponding **development-only** private key is stored in ignored `.local/catalog-update-private.pem`. Back it up securely outside packaged artifacts. Do not commit, email, upload or include the private key in an APK/extension. `keygen` reuses a matching key and refuses silent replacement of an existing trust pin. A new checkout with only the public key can build the app; signing updates also needs the matching private key.

```powershell
python tools/package_catalog.py keygen
python tools/package_catalog.py update --version 14 --output artifacts/catalog-v14.jansetu
```

Select a version strictly greater than the currently installed catalog (the effective bundled catalog is version 13). The Phase 3C version-14 demonstration contains the same 7,347 websites and 52 services as that bundle. Older demonstration packages are rejected by version checks. Use a higher version after another update is installed, or restore the bundle to repeat the local demonstration. Service import/review/revision commands are in SERVICE_CATALOG_REPORT.md. The private key remains in the ignored local directory and must never be distributed.

Copy the `.jansetu` file to the device's Downloads folder. In **Settings → Offline catalog → Import catalog update**, select the file. The file picker grants access only to the chosen document. The app checks ECDSA P-256/SHA-256 signature, exact payload digest, size, record count, scope, version, provenance and browser policy before atomically replacing app-private storage. A failed import preserves the previous catalog. Imported data stays available offline. A signature authenticates the editor, not government ownership or endorsement.

**Restore bundled catalog** removes the imported package after confirmation and preserves preferences/favorites. If a stored update is damaged or older than the new APK's bundle, startup falls back to the bundled catalog and displays a recovery notice. This is explicit local rollback; there is no remote revocation service or automatic update scheduler. Keep a verified APK and source snapshot as recovery material. A normal restart preserves the imported catalog. Uninstalling or clearing app storage removes it and local preferences.

## Production remains a separate step

Resolve permitted catalog reuse and public evidence handling, then run `python tools/check_catalog.py --public-release`. That gate currently fails as intended. Choose a separate production update key and trust-pin rotation/distribution policy before a public update channel. The development key is not a production credential.

Android release signing is independent: securely provide all four `JANSETU_KEYSTORE`, `JANSETU_STORE_PASSWORD`, `JANSETU_KEY_ALIAS`, `JANSETU_KEY_PASSWORD` variables. Then build `:app:assembleRelease :app:bundleRelease` and verify the signature. Without those variables the release APK/AAB are unsigned local artifacts. Do not expose signing secrets in command output.

There are no local development API URLs in the app to replace. No automatic catalog downloads, backend, analytics or government API adapter is configured. See [release checklist](RELEASE_CHECKLIST.md), [compatibility](COMPATIBILITY.md) and [QA](QA_REPORT.md) for the remaining device, policy and publication checks.
