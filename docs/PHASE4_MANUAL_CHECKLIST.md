# Phase 4 manual end-to-end checklist

Use `in.jansetu.app.qa` (JanSetu QA) on the dedicated emulator. Never clear, uninstall or seed `in.jansetu.app`. The QA package has separate preferences, files, alarms and Keystore UID. Capture before/after hashes of the retained preview profile; do not export its decrypted contents. All form inputs below are synthetic test data.

## Reproduce automated gates

1. Keep the emulator responsive and use JDK 17. Run `python tools/run_phase4_quality.py baseline` for the retained Phase 3C functional code, or `final` for the hardened source. Existing run directories are preserved; archive/rename one explicitly before a new run.
2. Require every native child exit code to be zero, fresh JVM/QA connected XML with no failure/error/skip, lint with no error, matching protected data hashes, unchanged preview profile hashes and QA still installed after cleanup. A success string alone is insufficient.
3. See the quality report for focused red/green reproductions, performance commands, artifact/signature checks and actual results. The full-suite XML and separate stderr/stdout are retained under `data/validation_reports/phase4/`.

## Citizen journeys

- [ ] Finish/skip onboarding; search exact DigiLocker, purpose/synonym and one reasonable typo. Check result type. Choose category, state/UT and district where available; clear filters. Open detail, official/source handoff and Back; retain prior query/filter/scroll state. Verify unsupported-query and unsupported-state empty states.
- [ ] Search Kerala income certificate. Check service identity/state, evidence links, partial document caveat and unknown eligibility. Return to the same query. Open Puducherry income information: no Apply/tracker shortcut. Inspect confirmed shared-login action with an exact intercepted URL, not credentials or a transaction submission.
- [ ] Create manual `Phase 4 QA record` with fake reference `QA-ONLY-4`, future deadline and note `Synthetic test only`; consent explicitly. Verify default masking, reveal/hide, validation and no automatic submission. Keep its reminder OFF unless executing the separate alarm plan.
- [ ] Add a confirmed-service record, edit status/submission/deadline and add progress; verify earlier history. Filter and sort; cancel deletion, then confirm deletion of only the intended synthetic row. Verify unrelated record and empty/filter-empty states.
- [ ] Force-stop/reopen QA; rotate/recreate while searching and editing. Verify saved data and supported draft state. Import the signed v14 demo, reject a damaged/wrong-version file, restart, restore v13, and check saved records/history/references/reminder preferences remain unchanged. Never alter personal records to match changed catalog facts.

## Browser safety

- [ ] Exercise allowed-host read-only pages, same/cross-host navigation, auth/payment paths/query/fragment, malformed and external schemes. Never allow a rejected URL to load or bypass an SSL failure.
- [ ] With local synthetic fixtures, exercise HTTP errors/offline failures, Retry/Back, cancelled uploads, safe HTTPS download handoff, rotation/history/text zoom and pause/resume/disposal. Keep the actual approved host/JavaScript/DOM-storage/readability permissions unchanged.
- [ ] Distinguish callback/local-fixture tests from a real public network/TLS check and from untested login/payment/upload compatibility. No real credentials/OTP or identity files.

## Reminders: explicit test plan

The preview demo reminder stays OFF. Use only isolated QA databases/keys/registry IDs. Verify permission denial/grant, replacement/cancellation, current-row rechecks, duplicate suppression, date edits/deletion, past deadlines and timezone/DST policy. Restore QA notification permission and all device settings afterward.

A short future-alarm probe, if used, must be QA-only, use an ephemeral synthetic record and clearly state any test-clock substitution. Record actual callback/visible notification separately from scheduling. Cancel the alarm/notification and remove only the probe fixture. Normal 09:00 deadline delivery, real reboot, Doze/OEM battery restrictions and timezone changes require separate observed outcomes; do not infer them from a simulated clock or direct notification posting.

## Accessibility and compatibility

- [ ] Record original font, size/density, orientation, theme, animation scales, TalkBack and notification settings; restore them in `finally`/at the end.
- [ ] Inspect search/service/tracker/list/editor/settings/dialog/browser controls at 1.3x and 2x, 320dp narrow width, portrait and landscape, light/dark and long Hindi controls. Critical actions must be reachable by scrolling; validation and recovery messages must be readable.
- [ ] Traverse controls using keyboard/focus and available TalkBack. Verify labels, role/state, headings and announcements; record whether synthesized speech was actually heard. Automated semantics/ATF checks do not establish a complete manual accessibility assessment.
- [ ] Record exact available API/device configurations. Missing physical/older API devices are not passing compatibility tests.

## Performance and final package

- [ ] Run `python tools/phase4_device.py measure before` and `after` on matching conditions, three repeats each. Report host/ADB/polling overhead and force-stop versus truly cold OS/cache. Keep emulator results separate from JVM timings; do not claim a speedup from incomparable measurements.
- [ ] Validate final debug APK contents/signature/version and installed package bytes. Install over the preview app with `adb install -r`, never uninstall/clear. Confirm retained database and key readability. Restore device settings and leave a useful app screen visible.

Checkboxes are operator steps, not completed-test assertions. Actual pass/fail/not-run status belongs in `PHASE4_QUALITY_REPORT.md` and the recorded run JSON/XML.
