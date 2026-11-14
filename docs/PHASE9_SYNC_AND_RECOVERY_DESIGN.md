# Phase 9: optional encrypted sync and recovery (design analysis)

18 October 2026. **Decision: keep local-only storage.** The design below is what any future sync must meet. No sync code, toggle or "backup" control exists in the app, so nothing incomplete is shown to people. Implementing it depends on the Phase 8 owner decision (blocker RB-13).

## Why not "sign in and upload"

Signing in does not make storage private. Server-side ("at rest") encryption protects against a stolen disk, not against the service operator, a compromised server or a legal demand to the host. Documents in My Documents (identity proofs, income and caste certificates) and application records are sensitive. Any sync must therefore be **end-to-end encrypted**: only the person's devices can decrypt.

## Required design

### Keys

| Key | Where | Purpose |
| --- | --- | --- |
| Vault data key (VDK), random 256-bit | Never leaves a device unwrapped | Wraps the per-document keys (today the device's Keystore key does this) and the tracker's record keys |
| Device key, one per enrolled device | That device's Keystore, non-exportable | Wraps the VDK for that device |
| Recovery key, random 256-bit, shown once as words or a QR code | Kept by the person, offline | Wraps the VDK for recovery |
| Server account credential | Passkey or password hash | Authenticates to the server only. **It never decrypts data.** |

- **Enrolling a new device:** an existing device approves it and wraps the VDK to the new device's public key. Or the person enters the recovery key. A password alone never yields the VDK.
- **Revoking a device:** its wrapped VDK is deleted. Rotating the VDK re-wraps every content key (not the content) and gives the new VDK to the remaining devices, so a revoked device cannot read anything synced afterwards.

### Recovery cases

| Case | Outcome |
| --- | --- |
| Lost password, devices still available | Reset the server credential from a signed-in device; the data keys are untouched |
| Lost device, recovery key available | Enrol a new device with the recovery key; revoke the lost device |
| Lost device and recovery key | **Data cannot be recovered**, and this is stated plainly at set-up. There is no secret downgrade such as server escrow. |
| Server operator compelled to disclose | Only ciphertext, wrapped keys and minimal metadata exist on the server |

### What the server may store

- An opaque account ID and the credential.
- Each device's ID, public key and enrolment time.
- Wrapped VDKs.
- Blobs: random ID, ciphertext, size rounded to buckets, version counter and an update timestamp at coarse granularity.

Never stored: document names, categories, types, service IDs, application titles or statuses, or plaintext hashes of content. Server logs record request type and outcome only; no blob IDs together with IP addresses beyond a short security window.

### Sync behaviour

- **Opt-in for each data type** (documents; applications). Turning sync on never silently uploads existing items: the person sees counts and confirms.
- **Conflicts:** each record carries a version and a device ID. On a concurrent edit, both versions are kept and the person chooses; there is no last-write-wins for tracker history. Tracker history is append-only and merges by event ID.
- **Deletion:** a tombstone (ID and version) propagates. A device that comes back online after revocation cannot resurrect deleted items. Tombstones are pruned after every device has acknowledged them, or after 90 days.
- **Transfer:** TLS with normal validation, plus E2EE payloads. Large documents are chunked using the existing segment format.
- **Failure:** sync status is shown honestly (Synced, Waiting, Offline, Failed, Conflict). Local data is always the working copy; a server outage never blocks local use.

### My Applications

The tracker uses its own Keystore key (`jansetu_tracker_v1`) and an append-preserving history. It has reminders, and its sensitivity is lower than documents but not low: reference numbers and notes. Syncing it would need:
- a schema with stable event IDs (already present)
- merge rules for the record fields
- a rule that reminders are scheduled only by each device locally

This is analysed but not implemented.

## Exit-gate status

The documented decision is to retain local-only storage. No incomplete sync feature exists or is presented. If the owner approves accounts and hosting (Phase 8), this phase reopens against the design above. Its tests must cover: no plaintext leaving the device (an interception test), key rotation after revocation, a recovery-key round trip, conflict preservation, tombstone propagation, and an honest status when offline or after a server failure.
