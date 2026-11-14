# JanSetu Phase 3B — My Applications and Application Tracker

## Objective

Implement a complete first version of JanSetu's application tracker, integrated with the existing Phase 3A service-search and service-detail experience.

The application currently has:
- 7,347 existing website records.
- 25 source-reviewed service-information records.
- Citizen-intent search and service filters.
- Service-detail pages with official source evidence and external links.
- An existing signed, versioned, offline catalog-update mechanism.
- No application tracker and no deployed account/backend system.

Verify the current repository and reports before changing anything. Do not assume older counts or replace working Phase 3A features.

## 1. Inspect and plan before implementation

Inspect:
- Navigation and the existing Home, Search, service detail and Settings screens.
- Catalog/service identifiers and application modes.
- Existing local storage, repositories, preferences and database dependencies.
- Notification permissions and scheduling support.
- Backup configuration and privacy policy.
- Existing automated tests and emulator test conventions.
- `APPLICATION_TRACKING_BOUNDARY.md` and the current QA report.

Write a brief implementation plan and identify the smallest reliable architecture before editing.

## 2. Add a My Applications area

Add a clearly accessible My Applications screen to the existing app navigation.

Users must be able to:
- Add an application from a service detail page when that service has a confirmed application route.
- Add an application manually when their service is not in the catalog.
- View all saved applications.
- Filter by user-maintained status and sort by upcoming deadline, submission date or most recent update.
- Open an application to see its details and progress history.
- Edit and delete an application, with confirmation before deletion.
- See useful empty states when they have not added any applications.

Do not show an active Apply action for services whose application route remains unverified, information-only entries, or assisted services without a confirmed online route. Do not change their existing publication or verification status.

## 3. Design a proper application record

Each locally saved record should support:
- A stable, locally generated application-record ID.
- Optional linked service ID and the service name captured when the record was created.
- Application title and department or organization, where known.
- Official application/information URL where verified.
- User-entered submission date.
- Optional application/reference number.
- User-maintained status.
- Optional deadline and reminder settings.
- User notes.
- Creation and last-updated timestamps.
- A history of status changes and user-recorded progress events.

Use the service's actual jurisdiction and official links. Keep application records separate from the service catalog so catalog updates cannot silently overwrite a person's application history.

Reference numbers and notes must remain optional. Do not collect passwords, OTPs, Aadhaar numbers, identity documents, uploaded application documents or other unnecessary sensitive information.

## 4. Implement meaningful status tracking

Include sensible statuses such as:
- Planning to apply
- Preparing documents
- Ready to submit
- Submitted
- Follow-up needed
- Approved
- Rejected
- Withdrawn

Allow the user to change status and optionally record a note or date for the change.

Clearly state that tracker statuses are **maintained by the user**. A status such as Submitted or Approved must not be displayed as independently verified by a government department.

Preserve progress history when an application is edited. Handle invalid dates and inconsistent deadline/submission-date combinations gracefully.

## 5. Use a local-first profile model

For this phase, implement My JanSetu/My Applications as a local profile stored on the device. Do not add a fake login screen or pretend that user records are synchronised to a server.

Select the persistence mechanism appropriate to the existing repository:
- Reuse a suitable existing database when available.
- Otherwise, use an appropriate persistent Android data store, with a structured database such as Room if the relational records and history justify it.

Provide a repository abstraction so a future authenticated, multi-device sync implementation can be considered separately.

Requirements:
- Records persist across app restarts, process death and device rotation.
- Database/schema changes use safe migrations.
- Failures do not silently erase existing records.
- Deleting one record must not delete unrelated records.
- Confirm destructive actions and provide a deliberate way to remove tracker data.
- Inspect Android backup behaviour and make an explicit, documented decision about whether sensitive tracker data is excluded from backup.
- Never put reference numbers or private notes into analytics, logs or crash messages.

Do not add a cloud database, authentication provider or backend in this phase.

## 6. Add optional local deadline reminders

Users may opt into reminders for an application's deadline.

- Use supported Android scheduling mechanisms suited to the existing project.
- Handle Android notification permission requirements and permission denial.
- Let users enable, change or cancel reminders.
- Reschedule or cancel reminders correctly when the application or deadline changes.
- Prevent duplicate reminders.
- Keep reminder text privacy-preserving; do not expose reference numbers or notes on the lock screen.
- Explain when reminders are unavailable or notifications are disabled.
- Test timezone/date-boundary behaviour, past deadlines, deleted records and rescheduling.

Never claim that a reminder was delivered if only scheduling was confirmed. No network connection should be required for an ordinary local reminder.

## 7. Connect the tracker to Phase 3A

From a service page, provide an Add to My Applications action only when appropriate.

When adding an application:
- Prefill only facts already supported by that service record.
- Let the user review and edit the title and relevant details.
- Preserve the source URL and service identity where available.
- Do not infer that an application has been submitted just because the user opened the official portal.
- Do not tell users they are eligible based on search rankings.
- Do not create fake live government application-status integrations.

The tracker must also allow manual entries for services that are not yet covered by the pilot.

## 8. Keep this phase functional, not a visual redesign

Use the existing Compose and navigation conventions. Make only the layout changes needed for usable tracker screens.

Do not redesign the whole Home/Search interface, introduce a new branding system, or change the visual design of unrelated screens. A dedicated UI/UX redesign will happen after the functional scope and data model stabilise.

The tracker must still be responsive to narrow screens and larger font sizes, and must have accessible labels and sensible focus/navigation order.

## 9. Testing requirements

Add automated tests for:
- Creating, reading, updating and deleting application records.
- Persistence after app restart.
- Database migrations and recovery from invalid/corrupt records.
- Multiple applications linked to the same service.
- Manual records with no service ID.
- Preservation of application history through edits.
- Filtering, sorting and empty states.
- Date validation and deadline changes.
- Reminder scheduling, cancellation, duplicate prevention and permission denial.
- Catalog updates not modifying user records.
- Deletion and tracker privacy behaviour.
- Navigation from a service detail page into the tracker.
- Honest status labelling and the absence of fake live-status claims.

Run Python, Node and Android tests applicable to the modified code. Run JVM tests, lint, debug build and connected emulator tests. Inspect the key flows on the existing emulator.

Do not call a test successful unless it actually ran. Report skipped device tests and environment failures separately.

## 10. Deliverables

Update the repository and add:
- The tracker screens, record model, storage/repository implementation and reminder functionality.
- `docs/APPLICATION_TRACKER_REPORT.md`, describing the implementation, privacy decisions, data-storage approach, reminder behaviour and limitations.
- `docs/QA_REPORT.md`, with dated results for this phase.
- Automated tests and test output summaries.
- A documented boundary between local tracker data and any future online profile/sync feature.

Produce a current debug APK and install it on the emulator if the environment permits.

## Completion criteria

A user can select an appropriate verified service or enter an application manually, save it, update progress, record an optional reference number, set a deadline reminder, close and reopen JanSetu without losing the record, and delete the record deliberately.

All existing Phase 3A search results, service evidence, official links, 7,347 website records and review decisions must remain intact. Report actual before/after checks and any outstanding limitations.