# Phase 7: My Documents (secure local document vault)

18 October 2026. My Documents keeps encrypted copies of a person's PDFs, JPEGs and PNGs on their phone. When a service record lists documents, "Prepare documents" turns them into a private checklist. JanSetu never uploads, sends or submits a document. My Documents needs no account and works fully offline.

## What it does

| Capability | Behaviour |
| --- | --- |
| Import | The person picks a file in Android's own picker (Storage Access Framework, `ACTION_OPEN_DOCUMENT`). JanSetu reads it once as a stream and encrypts it as it reads. The source URI and folder path are never stored. The original file is not changed. |
| Validation | **Type:** taken from the file's content, never its name (PDF, JPEG or PNG). The stated MIME type must agree with the content. <br>**Structure:** PNG needs IHDR and IEND; JPEG needs an end marker; PDF needs `%%EOF`. <br>**Decoding:** images must decode, at most 60 megapixels (a guard against decompression bombs). PDFs must open with 1–2,000 pages. A password-protected PDF is kept but marked "no preview". <br>**Limits:** 15 MB per file, 300 documents, 500 MB in total, and at least 64 MB must stay free on the device. <br>**Duplicates:** refused. <br>Every refusal names the problem and the next step, and leaves no files behind. |
| List | Search (name and category), filters (category, type), sort (newest, oldest, name), sizes and dates, storage usage. |
| Open | An in-app preview, decrypted in memory only. Images are decoded at a bounded size. PDFs render one page at a time through `PdfRenderer`, over a read-only proxy file descriptor (`StorageManager.openProxyFileDescriptor`). No plaintext temporary file is ever written. |
| Rename and recategorise | Revision-checked, so a stale edit is refused. Names are cleaned of path characters, control characters and bidirectional overrides. |
| Delete | One document, with confirmation. Its encrypted file and the preparation choices that used it are removed. "Remove all documents" also deletes the vault key. |
| Use on a website | **Export a copy:** `ACTION_CREATE_DOCUMENT`, after a warning that the copy is unencrypted. <br>**System file picker** (off by default): see below. |
| Prepare documents | Shown on a service page only when the service record lists documents (29 of 52 services). See below. |

Entry points: a Home card, Settings → Privacy and your data, and "Prepare documents" on service pages.

## Storage boundaries

- **Directory:** `no_backup/vault/`. It holds `objects/<random UUID>.jsv` (encrypted content) and `documents.db` (SQLite with `secure_delete=ON`; each row's payload is encrypted). Nothing in it is shared with My Applications, whose data is in `no_backup/tracker/applications.db` under Keystore alias `jansetu_tracker_v1`.
- **Keys:**
  - `jansetu_vault_v1`: AES-256-GCM in the Android Keystore. It is non-exportable and used only to seal per-document keys and metadata.
  - `jansetu_vault_picker_v1`: HMAC-SHA256 in the Keystore, used only for file-picker IDs.
- **Content format:** each document gets a random 256-bit key. The content is sealed with AES-256-GCM in 64 KiB segments. The nonce is a random prefix, a counter and a final-segment flag. The associated data is the header plus the document ID. Reordering, truncation, a swapped file or a changed header all fail authentication. The per-document key is sealed by the Keystore key for `dek:<id>`.
- **Encrypted metadata:** name, category, type, size, SHA-256, dates and preview flag, sealed for `document:<id>`. Preparation choices are sealed for `choice:<id>`. In plaintext the database holds only random IDs.
- **Backup:** `no_backup/` is never backed up. The existing backup and data-extraction rules exclude every domain from cloud backup and device transfer.
- **Screens:** My Documents, the document page and Prepare documents set `FLAG_SECURE`, with a counter so moving between them never drops it.

## Threat model

| Threat | Mitigation | Residual risk |
| --- | --- | --- |
| Another app reads the files | App-private storage; content is encrypted. The provider requires `MANAGE_DOCUMENTS`, which only the system picker holds. | None known for an unrooted device. |
| Offline extraction of storage (rooted device, forensic copy) | Content and metadata are encrypted. Keys are non-exportable Keystore keys, and hardware-backed where the device supports it. | An attacker able to use the app's Keystore keys on the device itself can decrypt. StrongBox is not required. |
| Someone using the unlocked phone | Screens are hidden from screenshots and recents. | **They can open JanSetu and view documents.** There is no in-app lock yet; see limitations. The person is told to keep a screen lock. |
| Malformed or hostile files | Content sniffing, MIME agreement, structural checks, bounded decoding, page and pixel limits, size limits. Parsing uses the platform's `BitmapFactory` and `PdfRenderer`. | Platform decoder vulnerabilities are patched by Android updates, not by JanSetu. |
| Plaintext leaks | Streaming encryption; previews in memory; proxy descriptors; no logging of names or contents (`toString` is redacted). | Decrypted bitmaps live in memory until garbage collection. An exported copy is plaintext by design, with a warning. |
| Overbroad sharing to apps and websites | Off by default; read-only, with no write, delete, rename, recents, search or thumbnails. Only the chosen file is granted. IDs are HMAC tokens that expire after 1 hour. "Stop all file-picker access" rotates the key. Turning the option off or deleting the document ends access. | A website that already uploaded the file keeps its copy. An app can copy the file during its hour of access. |
| Key loss (credential reset, OS fault) | The documents are kept, never deleted. The vault shows "locked" and refuses new imports until the person removes locked documents. A new key is never generated over existing documents. | The locked documents cannot be recovered. There is no recovery copy (Phase 9 decides on optional sync). |
| Crash during import | Ciphertext is written to `.partial` and renamed only after validation. Leftover `.partial` files are removed when the vault next opens. An encrypted file with no record is reported for deliberate removal. | None known. |
| Deleted data on flash storage | The file is deleted. "Remove all" deletes the key (crypto-shredding). | Erasing an individual file from flash storage cannot be guaranteed. Remnants stay encrypted, but the key survives single deletions. |
| Misleading claims | Prepare documents uses only recorded facts and keeps required, conditional and unconfirmed items separate. A choice is labelled "JanSetu has not checked whether this file meets the requirement." Tests forbid "met", "satisfied" or "ready to submit" wording. | The service records themselves may be partial; the screen says so. |

## Failure behaviour

| Situation | What the person sees | Data |
| --- | --- | --- |
| Storage read or write error | "My Documents could not be read or saved. Nothing has been deleted. Try again." | Unchanged |
| Key unavailable | A "Documents locked" notice and an option to remove the locked documents | Files kept |
| Unreadable record | Counted and kept, with an option to remove it | Kept until removed |
| Picker cancelled | Nothing happens | Unchanged |
| Source revoked or unreadable mid-import | An "unreadable source" message | Partial ciphertext removed |
| Export fails | A message; the partial destination is deleted where possible | Vault copy unchanged |
| Preview fails | "The preview could not be shown. The document is unchanged." | Unchanged |

## Prepare documents

- Requirements come only from the service record's `documents` (listed as required) and `conditionalDocuments` (needed only in some cases), each with its sources.
- A list the record doesn't have is shown as "Not confirmed in JanSetu's records", never as "nothing needed".
- Every screen says the list may be partial and gives the date the source was checked.
- The person may choose saved files for each item. The summary says "You chose files for N of M listed items. JanSetu has not checked whether any file meets a requirement."
- A choice is tied to a fingerprint of the requirement's wording. If the service record changes, earlier choices appear under "Earlier choices for changed requirements" and are never reassigned silently.
- Service data was not changed in this phase.

## File-picker integration (`VaultDocumentsProvider`)

- Registered with the authority `<applicationId>.vault`. The QA build has its own authority, `in.jansetu.app.qa.vault`.
- It is exported only to holders of `MANAGE_DOCUMENTS` and grants URI permissions.
- The root is hidden unless the person turns the option on.
- Verified on the API 37 emulator with Android's real DocumentsUI:
  - the root lists only the saved documents, with names, types and sizes
  - choosing one returns a URI that reads back the exact bytes
  - that URI stops working after "Stop all file-picker access"
  - cancelling returns nothing
- A browser upload field uses the same system picker, but no live government upload was attempted. The person always chooses the file themselves. JanSetu cannot and does not fill an upload field.

## Tests

| Suite | Count | Covers |
| --- | --- | --- |
| JVM (`app/src/test/.../vault`) | 22 | Signature detection, structure checks, MIME agreement, name cleaning, capacity rules, query and sort, metadata validation. The segmented cipher: round trips at every segment boundary, random access, no plaintext in ciphertext, tamper, truncation, reordering, swap, wrong-ID and wrong-key detection, size limits, unique keys. Preparation: only recorded facts, unknowns kept, stable keys, wording that never claims a requirement is met, outdated choices. Picker IDs: expiry, forgery, revocation. |
| Instrumented, repository (API 37) | 8 | Lifecycle with restart; rejections leaving nothing behind; storage under `no_backup` with no plaintext marker anywhere in app storage; key loss keeps files; interrupted-import clean-up; preparation choices cascade; proxy PDF with no temporary files; separation from the tracker key and storage. |
| Instrumented, provider | 4 | Permission and registration; hidden until enabled; read-only, revocation, deletion and disabling; locked documents not listed. |
| Instrumented, system picker | 2 | The real DocumentsUI: listing, choosing, reading the bytes, revocation, cancelling. |
| Instrumented, UI | 4 | Home → import (stubbed system picker) → search → preview → rename validation → delete; a rejected file and a cancelled picker; the preparation checklist's wording; the Prepare button only where requirements exist; `FLAG_SECURE` on and off. |

All test files are generated (coloured shapes, generated PDF text). No real identity document or personal data is used.

## Limitations and follow-ups

- **No in-app lock.** The vault relies on the device screen lock. An optional "require device unlock to open My Documents" setting is a Phase 14 security item and needs physical-device testing.
- **No recovery.** Key loss, uninstalling or clearing storage loses the documents. Optional end-to-end encrypted sync is a Phase 9 decision.
- **English only.** The Hindi navigation label and Home card are translated; the vault screens are English, as Settings states.
- **Not checked on physical devices.** Android 8–14, OEM document pickers and real browser upload fields (blocker RB-10). Behaviour of `openProxyFileDescriptor` on older Android versions is expected but unverified.
- **No live upload.** A password-protected PDF was not generated in tests. The code keeps such a PDF without a preview, but this path is untested.
