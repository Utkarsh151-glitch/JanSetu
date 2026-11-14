# Phase 13: accessibility, usability and beta readiness

18 October 2026. **Automated checks do not establish accessibility conformance.** No real beta participant has been recruited. Recruitment waits until the distribution permissions (RB-01, RB-02) and the privacy requirements of the chosen beta channel (RB-07) are cleared.

## What is checked automatically (API 37 emulator, QA build)

| Area | Automated evidence |
| --- | --- |
| Labels, touch targets, structure | Accessibility Test Framework checks on the Phase 5 and 6B screens (`Phase5AccessibilityChecksTest`), and on My Documents, the document page, Prepare documents, the service page with Prepare, and the Home card. These run in light and dark themes and at 2× font scale (`Phase13AccessibilityChecksTest`). |
| Large text and narrow screens | Phase 5 tests at 2× text on a 320 dp width and in landscape. The vault and preparation screens at 2× (above). |
| In-app browser text size | Website text starts from the Android font size, with A (100 %) and A+ (up to 200 %); choices survive recreation (`BrowserReadingTest`). |
| Document flows | Import, rejection, cancelled picker, search, preview, rename, delete, export (`DocumentsFlowTest`); the real system picker (`SystemPickerIntegrationTest`). |
| Tracker | Existing tracker suites, plus preparation integration and export (`TrackerPreparationTest`, `PreparationJourneyTest`). |
| Offline and failure | The catalogue is bundled and works offline. Storage failures show "nothing deleted" messages. The browser shows an error with retry and a hand-off to the device browser. |

## What needs people (manual checklist)

Use the isolated QA build and **synthetic documents only**: generated images, blank PDFs, made-up names. Never use real identity documents.

- [ ] **TalkBack, spoken.** Home → My Documents → add (system picker) → document page → preview → export → delete. Then a service → Prepare documents → choose a file → Add to My Applications → application page. Record any unlabelled control, wrong reading order or missing announcement.
- [ ] **Switch Access and a physical keyboard** on the same journeys.
- [ ] **Font sizes.** Android's largest font, with and without "Larger text in JanSetu". Dialogs, such as the category list, must stay scrollable and complete.
- [ ] **Hindi mode.** The navigation and Home card are in Hindi. Vault screens stay English, as Settings says, and the English-summary notices appear where expected.
- [ ] **Physical devices**, Android 8–14, at least two manufacturers:
  - the file picker shows "JanSetu: My Documents" only when the option is on
  - a browser upload field on a public test page (not a live government application) receives the chosen file
  - access ends after "Stop all file-picker access"
- [ ] **Recovery messages.** On a spare device, clear security credentials and confirm the "Documents locked" notice and the deliberate removal flow.
- [ ] **Reminders** on physical devices (an existing Phase 4 gate).
- [ ] **National Portal at 1.3× and 2.0× system font.** Consent buttons, site search and the accessibility menu remain visible and usable; A returns to 100 %; rotation keeps the page and reading position.

## Usability measures for a future beta

| Question | Task | Success measure |
| --- | --- | --- |
| Can people find the right service? | "You need an income certificate in Delhi." | Opens the Delhi Income Certificate service page without help |
| Do they understand what a website is for? | Open a checked website profile | Correctly states its purpose and who runs it |
| Can they tell checked facts from unchecked ones? | Compare a checked profile with a directory listing | Says which information was checked, and that neither was reviewed by a person |
| Can they prepare without being misled? | Choose files in Prepare documents | Says JanSetu has not checked the files and that nothing was submitted |
| Do they understand the privacy model? | Read the My Documents notice | Says documents stay on the phone and are lost on uninstall |

## Beta protocol (for when permissions are cleared)

- **Consent first,** with a plain-language information sheet: purpose, what is recorded and what is not, the right to stop, and how findings are used.
- **Minimal collection.** Task success, time and the participant's own words about confusion. No screen recording of personal content, and no real documents: participants use the provided synthetic files.
- **Triage.** Every finding becomes an issue with severity (blocks a task, misleads, slows), the screen, a reproduction and a proposed fix. Misleading-claim findings are fixed before the next round.
- **Retention.** Notes are anonymised at collection and deleted after the fixes ship, unless the participant agrees otherwise.

## Known limitations carried forward

- Spoken TalkBack, Switch Access, keyboard use and physical-device behaviour are unverified (RB-09, RB-10).
- Colour contrast relies on computed palette ratios; no rendered-pixel contrast was measured.
- The vault, tracker and service-detail screens are in English.

## Exit-gate status

The automated functional and accessibility checks pass, as recorded in the latest gate. The human checks above are not done, so the phase stays **BLOCKED (RB-09, RB-10)**, with the remaining work listed here.
