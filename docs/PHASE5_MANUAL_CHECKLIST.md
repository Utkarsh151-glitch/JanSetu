# Phase 5 manual UI and accessibility checklist

Use `in.jansetu.app.qa` (JanSetu QA) only. Never clear, uninstall or seed `in.jansetu.app`. Record the emulator or device model, API level, font scale, display size, theme, animation scales and TalkBack state before starting, and restore them afterwards. Use synthetic data only; never enter real credentials, OTPs, Aadhaar numbers or identity documents.

The [Phase 4 checklist](PHASE4_MANUAL_CHECKLIST.md) still applies to browser safety, reminders, signed catalog update/restore and performance. The items below cover the Phase 5 redesign and extension. Checkboxes are operator steps, not results; record actual outcomes in [PHASE5_UI_QA_REPORT.md](PHASE5_UI_QA_REPORT.md).

## TalkBack and keyboard

- [ ] **Home:** the first heading read is "JanSetu" (read once, not "image, JanSetu"). The hero, task and empty-state illustrations are skipped. Each Common task tile reads its label and "button". "Settings" is labelled.
- [ ] **Search:** the search field announces its label and the example hint. "Clear search text" is labelled. Each filter chip reads its current value and "No filter" or "Filter applied". Menu options read their check state. "Clear filters" appears and is announced. Result cards read the type ("Website" or "Verified service"), the title and the coverage ("Kerala only · State").
- [ ] **Website detail:** headings are reachable with heading navigation (About this website, JanSetu guide, How we checked this). The official-site button is followed by its destination caption.
- [ ] **Service detail:** the back control reads "Back". The application mode is read in words. Each section card title is a heading. "Evidence and review" announces "Show sources" or "Hide sources" and its expanded state. Per-fact "Source: …" links are reachable.
- [ ] **My Applications:** cards read "Your status: …", any deadline wording ("Your deadline passed: …"), and "Updated …". Filter and sort chips read their values. Remove all asks for confirmation.
- [ ] **Editor:** after Save with an invalid date, the error summary is announced, and each field reads its own error. The checkbox rows toggle with a single activation and read their state. The reference eye icon reads "Show reference" or "Hide reference".
- [ ] **Detail:** "Your recorded status: …" is read with "Maintained by you … not a decision … from a government department". The history entries are read as single items. Delete asks for confirmation.
- [ ] **Settings:** Theme and Language are radio groups with selected state. Reduce motion and Larger text are switches with on/off state. The reminder permission state is read in words.
- [ ] **Feedback:** saving, updating and deleting an application, and clearing history, each produce a success notice that TalkBack announces without moving focus.
- [ ] **Physical keyboard:** Tab and arrow keys reach every control in a logical order with a visible focus indicator. Enter or Space activates chips, radio rows, switches and expandable sections.
- [ ] Record whether synthesized speech was actually heard, and on which TalkBack version.

## Visual and responsive

- [ ] Light and dark themes, through both the system setting and the in-app Appearance setting. Check text contrast on cards, notices and status labels. Meaning must never depend on colour alone.
- [ ] Font scale 1.3x and 2.0x at 320dp width (`wm density 540` on a 1080px panel), portrait and landscape. Labels must not clip or overlap. Every action must be reachable by scrolling. The bottom bar shows only the selected label when narrow or at large text.
- [ ] "Larger text in JanSetu" on top of 1.0x and 2.0x system text. The overall scale must be capped at 2.0x and official websites must be unaffected.
- [ ] Hindi on Home, Search, website detail, Settings, Help and onboarding. Long Hindi strings must wrap. Service details and My Applications stay in English, as the Settings note states.
- [ ] Launcher icon in the app drawer and home screen, the themed (monochrome) icon on Android 13+, the splash screen on Android 12+, and the launch window on Android 8–11 if a device is available.
- [ ] Reminder notification (QA-only, synthetic record): the small icon shows the bridge mark and the accent colour is teal. Delivery itself remains a Phase 4 gate.

## Motion

- [ ] With default animations, screens cross-fade briefly. Expandable evidence animates open and closed. The onboarding illustration rises in once, with no loop.
- [ ] With **Reduce motion** on, or Android **Remove animations** on, screen changes, evidence expansion, the filter-clear chip and the onboarding illustration change instantly.
- [ ] No animation delays typing, tapping Save, cancelling a dialog or opening an official website.

## Branding boundaries

- [ ] No screen shows a government emblem, seal, flag or department logo as JanSetu branding. Every official action names its destination host. "Independent" or "not a government app" wording appears on onboarding, Home (About card), Help and Settings.
