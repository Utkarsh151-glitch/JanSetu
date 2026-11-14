# Phase 10: preparation and tracker integration

18 October 2026. This phase connects service pages, Prepare documents (Phase 7), My Documents and My Applications into one local journey. Nothing new is sent anywhere. Statuses stay user-maintained, because no authorised government status API exists.

## Journey

1. **Service page.** "Prepare documents" opens the checklist, which shows only the requirements recorded from the official source (see Phase 7).
2. **Checklist.** The person chooses their own saved files. For a service with a confirmed online route, "Add to My Applications" opens the application editor, pre-filled from the service. It says: "This does not submit an application."
3. **Application page.** "Documents for this service" shows the person's choices: "You chose files for N of M listed items. JanSetu has not checked whether any file meets a requirement." A button opens the checklist.
4. **Changed service records.** When the service record's requirement wording changes, earlier choices are kept and flagged: "The document list for this service changed after you chose some files." The saved application snapshot is never rewritten, and the existing warnings for a removed service or changed route are unchanged.
5. **Export.** "Export my applications" first asks for confirmation that an unencrypted copy is wanted. It then saves readable JSON wherever the person chooses. The file contains:
   - titles and departments
   - each status, labelled as theirs
   - dates, reference numbers and notes
   - the reminder preference
   - the linked service's snapshot
   - the progress history

   The file states that "none of it is a government record or confirmation". It leaves out internal IDs, revision counters and reminder-delivery bookkeeping. Unreadable records are counted, not exported.
6. **Deletion.** This is unchanged: delete one application or remove all tracker data. Deleting a document in My Documents removes its preparation choices; it never changes an application record.

## Decisions kept

- **The tracker shortcut stays limited to services with a confirmed online route** (the Phase 3B rule, still tested). For assisted and information-only services, the checklist is the planning tool, and a manual application record is still possible.
- **Two stores, two keys.**
  - The tracker (`jansetu_tracker_v1`) holds no document data.
  - The vault (`jansetu_vault_v1`) holds no application data; preparation choices are keyed by service, not by application.
  - The application page reads both stores, each through its own repository.
- **No account migration.** Accounts do not exist (Phase 8); nothing is moved or uploaded.

## Tests

- **JVM** (`ApplicationExportTest`, 2 tests): the export's labelling, completeness, ordering and excluded internals, and an empty tracker.
- **Instrumented** (`TrackerPreparationTest`, 3 tests):
  - the checklist's tracker shortcut appears only for confirmed routes
  - the application page shows the summary and a warning about changed requirements, and the record stays unchanged
  - export happens only after confirmation and writes the labelled JSON
- **Instrumented** (`PreparationJourneyTest`, 1 test): the full journey through real navigation. Search, then the service, then the checklist, then choosing a saved document, then Add to My Applications, then saving. The application page shows the choice, the record is Planning with no submission date, and the stores stay separate.

## Limitations

- Statuses, dates and references are entered by the person. JanSetu cannot fetch an official status or confirm a submission.
- Export is JSON only and has no import. It is unencrypted by design and warns about it.
- The application editor and pages remain in English.
