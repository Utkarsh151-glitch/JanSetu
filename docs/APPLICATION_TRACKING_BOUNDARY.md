# Local application tracking and future sync boundary

Phase 3B adds a local My Applications profile, encrypted on-device records/history and optional local reminders. Phase 3A discovery and official links remain intact. No government status API, cloud account or synchronization has been added. See [implementation report](APPLICATION_TRACKER_REPORT.md).

## Local record

| Field | Meaning |
| --- | --- |
| `id` | Random local application UUID |
| `service.serviceId`, `service.catalogVersion` | Optional stable service association, captured title/jurisdiction/links and catalog version used |
| `submittedOn` | Date entered by the user; nullable |
| `status` | Planning, preparing, ready, submitted, follow-up, approved, rejected or withdrawn; always user maintained |
| `statusProvenance` | Always `user_entered` for this feature |
| `referenceNumber` | Optional; mask by default and never log |
| `deadline`, `notes` | Optional user-maintained values, separate from sourced service deadlines |
| `reminder` | Optional local reminder, only with explicit opt-in and any required platform permission |
| `updatedAt` | Local record modification time |

UI must say **Your recorded status**, not **Verified government status**. A changed or removed catalog service must not delete a person's application record; retain its ID/title snapshot and show that the current catalog association is unresolved. Unknown service deadlines must never become invented reminder dates.

## Separate integration contract

Live retrieval requires an authorized official provider, documented operation/schema, appropriate authentication and a tested failure path in the existing integration registry. Display the provider/source, retrieval time and authorization scope separately from user notes. A timeout leaves the last user entry intact and does not imply rejection or approval. There are no live providers configured today.

## Implemented privacy decisions

There is no cloud account, synchronization or export. Saving a new record requires local-storage consent; optional fields stay optional. Android Keystore AES-GCM encrypts record/history payloads, with row identity authenticated as additional data. App-private SQLite lives in `noBackupFilesDir`; backup and device-transfer exclusions are explicit. References are masked by default. Individual/all-record deletion requires confirmation and cancels reminders. No passwords, OTPs, Aadhaar numbers or documents are requested, and private values are not logged or inserted into notifications/support drafts. Loss of device/key, uninstall or clearing app storage can permanently lose records. This is not a forensic-erasure or compromised-device security guarantee.

## Future authenticated sync contract

Keep the repository interface separate from catalog delivery and network-provider code. Any future sync proposal needs an explicit account/consent model; encryption/key recovery; per-user authorization; local UUID-to-server mapping; conflict/version handling; append-preserving history; deletion tombstones and retention; offline queue/retry rules; notification ownership across devices; export/backup policy; and revised privacy/store declarations. Do not silently upload existing local records when adding an account. Local `revision` supports stale-edit detection, not distributed conflict resolution. A user-entered Approved status remains user entered even if synchronized.
