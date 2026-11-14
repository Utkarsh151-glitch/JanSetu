# Local recovery and privacy review

Phase 4 scope: targeted source inspection and regression tests, not a complete security audit or certification. Final observed outcomes are in `PHASE4_QUALITY_REPORT.md`.

## Current boundary

The tracker stores AES-256-GCM authenticated payloads with Android Keystore keys, in `noBackupFilesDir`, separate from catalog files. Application/history identities are authenticated; references are masked by default. Backup and device transfer are excluded, including preferences. The app has no account, analytics, credential/OTP collection, identity-document upload, live government-status access or cloud sync. OS/browser/keyboard providers have their own data handling.

Uninstall, app-storage clearing, loss/invalidation of the Keystore key or loss of the device can make encrypted records permanently unavailable. Installing a new APK over the existing package preserves supported storage; uninstall/reinstall does not. Keeping ciphertext is not equivalent to a recoverable backup. Corrupt/unreadable records must remain intact until deliberate confirmed deletion; reads must not create replacement encryption keys.

No export or independent recovery key currently exists. A retry can help transient failures but cannot restore a permanently missing Keystore key. UI recovery wording must say that no reset occurred and warn against uninstalling/clearing storage while records are needed. Catalog failures should recommend retry or an in-place update rather than a destructive reinstall. Do not promise restoration from Android backup.

## Smallest proposed improvement (not implemented)

An explicit, user-controlled, password-encrypted local archive could provide device-independent recovery. It needs a reviewed design before release: authenticated encryption, bounded/versioned schema, strong passphrase-based key derivation with fresh salt, clear no-password-recovery warning, a system document picker, no cleartext intermediate file, review/preview of import conflicts, transactional restore, re-encryption with the destination device key and preservation of existing records on every failure. Export must require deliberate consent and never include government credentials or signing keys. It must not silently export drafts or upload anything.

This is a proposal, not a tested capability. Until implemented and independently reviewed, users must understand the single-device loss risk. No cloud backend is needed or introduced.
