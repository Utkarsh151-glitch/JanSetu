# JanSetu Phase 4 — End-to-End Quality, Reliability and Accessibility Hardening

## Objective

Harden the existing JanSetu application before the final UI/UX redesign and release preparation.

The current app has:
- 7,347 government website records.
- 52 published service records.
- Natural-language search and service detail pages.
- My Applications with encrypted local persistence and user-maintained progress history.
- Optional local deadline reminders.
- Signed, versioned, manually imported catalog updates.
- A limited Chrome companion and a carefully restricted readability pilot.

Inspect the actual repository and latest `docs/QA_REPORT.md` before changing anything. Work within the current architecture and preserve all existing functionality.

This phase is for reliability, end-to-end behaviour, privacy, compatibility and accessibility. Do not redesign the entire UI yet.

## 1. Re-establish a trustworthy QA baseline

Reproduce the most recent Phase 3C build and test results.

The report records 35 JVM tests and 28 emulator tests in the full run, with a separate repeated catalog-boundary test after changing the Android test cleanup configuration. The report also records an outer PowerShell wrapper returning a nonzero status despite Gradle logs reporting BUILD SUCCESSFUL.

- Inspect the actual Gradle outputs, test XML, lint XML and process exit statuses.
- Repeat the full applicable test suite after the current test-runner configuration changes.
- Resolve inconsistencies between command exit status, Gradle outcomes and reported results.
- Ensure tests do not uninstall JanSetu, erase real local tracker data or invalidate Keystore keys unexpectedly.
- Keep test profiles, database files and keys isolated from the real emulator profile.
- Save a reproducible baseline report and identify any failure that remains unresolved.

Do not mark a test as passing solely because a partial or redirected log contains BUILD SUCCESSFUL.

## 2. Test complete citizen journeys

Test complete workflows against the current app rather than testing individual screens in isolation.

### Website discovery

- Search by portal name, purpose, common synonym and reasonable misspelling.
- Apply jurisdiction and category filters.
- Open website details and use the official-site handoff.
- Confirm loading, failure, Back navigation and returning to the previous query behave correctly.

### Service discovery

- Search for a supported citizen need.
- Verify result type, service identity and applicable jurisdiction.
- Open the service detail, inspect sources and requirements, and return to search.
- Confirm that unknown eligibility or document requirements remain visibly unknown.
- Verify that unconfirmed application routes and information-only services do not expose a misleading Apply action.
- Test empty results and queries for unsupported jurisdictions.

### My Applications

- Create a manual application.
- Create an application from a supported service.
- Edit it, update status, record progress history and change a deadline.
- Test optional and masked reference numbers.
- Test filtering, sorting, cancellation of deletion, confirmed deletion and empty state.
- Force-stop and reopen the app and verify persistence.
- Import a signed catalog update and restore the bundled catalog.
- Confirm that saved tracker records and status history survive catalog changes.

### Reminders

- Test permission granted and denied.
- Verify scheduling, rescheduling, cancellation, duplicate prevention and deletion.
- Check timezone changes, daylight-saving boundaries, past deadlines, reboot behaviour and battery/notification restrictions where supported.
- Observe at least one real future test reminder where practical, and distinguish actual delivery from merely successful scheduling.

Never enable a reminder on the retained demo application without an explicit test plan.

## 3. Privacy, storage and recovery

Inspect the tracker storage, encryption, Android Keystore use, logs, backup exclusions, catalog imports and deletion behaviour.

Verify that:
- Reference numbers are masked by default.
- Credentials, OTPs and unnecessary identity documents are never collected or logged.
- Logs and crash diagnostics do not reveal notes, references or application history.
- Corrupted data and unavailable encryption keys produce a clear, non-destructive recovery path.
- Failed catalog imports preserve the existing catalog and user data.
- Confirmed deletion removes the intended data without affecting unrelated applications.
- Backup, device-transfer, uninstall and device/key-loss consequences are accurately explained to users.
- Catalog updates cannot modify or delete application-tracker records.
- Private signing keys are excluded from distributed artifacts and test output.

Do not claim a complete security audit merely because specific risky patterns were not found.

If the current local-first design lacks recovery or export, document the risk and propose the smallest safe improvement. Do not add a cloud backend in this phase.

## 4. Browser and official-site safety

Exercise the existing browser handoff and limited WebView behaviour with safe test conditions.

Inspect:
- TLS certificate errors.
- Same-host and cross-host redirects.
- External schemes and malformed URLs.
- File download/upload handoff.
- Authentication, payment and sensitive application journeys.
- WebView lifecycle, rotation and process recreation.
- JavaScript and DOM-storage permissions per approved website.
- Back navigation, page-loading failure and offline states.

Never bypass certificate errors or weaken domain restrictions to make a test pass. Sensitive authentication and payment flows should be handed off safely when compatibility cannot be established.

Keep the existing readability pilot restricted to its reviewed page. Do not expand injected styling to other government portals during this phase.

## 5. Accessibility and device compatibility

Inspect the actual service, search, tracker, settings, dialogs and browser-control screens.

Test:
- Spoken TalkBack traversal and meaningful control labels.
- Keyboard/focus order where applicable.
- Font scaling to at least 1.3× and 2× where practical.
- Narrow widths, portrait and landscape layouts.
- Dark theme, contrast and visible focus.
- Long localized strings, empty states, validation messages and error dialogs.
- Navigation and state restoration after rotation and process recreation.

Use the existing emulator and any available additional device/API configurations. Distinguish an automated accessibility smoke test from a complete manual accessibility assessment.

Record screenshots and actual outcomes, including unresolved defects.

## 6. Performance and operational reliability

Use repeatable measurements on the current implementation.

Measure cold launch, catalog readiness, search interaction, service-detail opening, tracker loading and signed catalog import/restore. Separate JVM timings from real emulator timings and report repeat counts and conditions.

Investigate only demonstrated bottlenecks. Do not introduce architectural rewrites or new backend services without evidence.

Check that large result sets remain lazy, searches do not block the UI thread, and expensive database/cryptographic operations are handled safely off the main thread.

## 7. Fix policy

Prioritise defects that can:
1. Lose or expose user data.
2. Direct citizens to an incorrect or unverified destination.
3. Misstate application eligibility, progress or availability.
4. Break a complete citizen journey.
5. Make critical screens unusable with accessibility settings.
6. Cause crashes, hangs or unacceptable responsiveness.

Fix the underlying cause and add regression tests. Keep existing website records, published service facts, pending review decisions and approved browser permissions unchanged unless a specific correction is justified and documented.

Do not add speculative features or start the full visual redesign.

## 8. Deliverables

Update the repository with:
- Fixes for reproduced defects.
- Automated regression tests and test fixtures.
- `docs/PHASE4_QUALITY_REPORT.md`.
- Updated `docs/QA_REPORT.md`.
- A reproducible manual end-to-end checklist.
- Before/after performance results.
- Accessibility findings and screenshots.
- An explicit list of remaining release blockers and risks.

Run applicable Python, Node, JVM, lint, debug build and connected emulator tests. Verify the final APK and installed package match the current source.

Report actual passing, failing, skipped and not-run checks separately. A missing device, external service or permission must be recorded as a limitation—not a passing test.

## Completion criteria

The main citizen journeys work end-to-end, tracker data survives supported app and catalog lifecycle operations, sensitive data is handled deliberately, browser handoff follows existing security restrictions, and critical accessibility/reliability issues are resolved or documented.

Only after this phase has a stable, reproducible QA result should JanSetu proceed to the final visual redesign.