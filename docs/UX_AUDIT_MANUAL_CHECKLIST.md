# Manual portal UX audit checklist

Use one record per **exact page and task**, including application pages separately from homepages. Record the portal ID, final HTTPS URL, page type, owner/operator evidence, reviewer, date/time, device, OS/browser, viewport, language, network and text scale. Save only public-page screenshots; never capture credentials, OTPs, personal records or filled forms. Enter findings in [page_audits.json](../data/page_audits.json) and the page decision in [site_interventions.json](../data/site_interventions.json). Then, from `tools/`, run `python inventory_audit.py` and `python site_interventions.py`; both must pass.

1. **Provenance and reliability.** Open the official source and destination. Record redirects, final host, HTTPS and certificate behavior. Note 403, timeout, CAPTCHA or a default server page as an access/reliability observation, not a UI defect. Repeat failures before calling them persistent. Do not bypass TLS errors.
2. **Task path.** State the citizen's task in plain language. Find it from the homepage and from JanSetu. Count unclear choices and backtracks. Distinguish the portal, a service landing page and an application form. Record official instructions, languages, dates and any partner-operated destination.
3. **Mobile and zoom.** At 390px or a real small phone, check horizontal scrolling, clipping, menus, sticky headers, dialogs and buttons. Repeat at default, 125% and 200% text/browser zoom where supported; also test landscape/narrower viewport. Capture page length and whether controls remain reachable. Text zoom can change wrapping without repairing layout.
4. **Keyboard.** Use Tab/Shift+Tab from start to finish. Confirm visible focus, logical order, skip link, menu operation, modal escape, no keyboard trap and reachable submit/cancel actions. Record the exact failing control.
5. **Screen reader.** On Android use TalkBack; on desktop use an available screen reader. Check page title, heading order, names/roles/states, image alternatives, link purpose, form labels, error announcements and language changes. Automated missing-alt counts must be reviewed for decorative images.
6. **Visual clarity.** Inspect body font and spacing, contrast of text and focus, heading hierarchy, link affordance, touch targets, dense instructions, meaningful error messages and content hidden behind overlays. Use a contrast measurement tool for actual color pairs; a screenshot alone is insufficient.
7. **Forms and transactions.** Inspect only with safe test conditions approved by the portal owner. Do not use real credentials or submit a real request for this audit. Identify login, identity, CAPTCHA, upload, payment and stateful steps. Assign Class E unless a separate, specific review establishes a safe boundary.
8. **Language and content.** Record offered languages and whether key instructions are understandable. Do not call machine-translated content authoritative. Note outdated dates or contradictory instructions with exact source evidence.
9. **Intervention and rollback.** Choose A–F only when the record has evidence; otherwise use `NEEDS_MANUAL_REVIEW`. For B, demonstrate 125%/200% reflow and restore. For a future C, specify exact host/path, static rules, expected benefit, form exclusion, apply/restore and route-change behavior. For D, test JanSetu guidance with a real task. For F, confirm a safe alternate official link instead of styling.
10. **Actual installed extension and Android device.** Verify Chrome toolbar opening, active-tab permission, allowlisted URL matching, CSS apply/remove, reload and same-page route changes. On Android verify A+/A, navigation/back, cookie notice, external handoff, rotation and large system font. Repeat on at least one additional device/browser before release.

Reference [GIGW 3.0](https://guidelines.india.gov.in/new-features-of-gigw-3-0/) and [WCAG 2.1](https://www.w3.org/TR/WCAG21/) for assessment criteria. An automated scan or this checklist alone does not establish certification or full conformance.

## Phase 6 additions (17 October 2026)

### Problem definition, one per exact page

Record:

- the exact HTTPS host, path and query;
- the page's public purpose;
- the date, time and viewing conditions (device or emulator, browser and version, viewport, Android font scale, browser or WebView text zoom, orientation);
- reproducible evidence (screenshot or recorded measurement file);
- the specific problem;
- who is likely affected and which task is impaired;
- a status of **confirmed**, **suspected** (automated flag or single unclear observation) or **not reproducible**.

Label every item by evidence type: live automated, live real browser, static fixture (mocked), or human review. Never present one type as another.

### Safe live checks

- [ ] Load each public page once per check, with normal TLS validation and the browser's own user agent. Do not retry, spoof or work around a 403, 5xx, CAPTCHA or bot check. Record it as access-inconclusive, not as a UI defect.
- [ ] Do not click, type, tick, submit, sign in or upload on any page. Scroll gestures and the browser's own zoom are allowed. Use JanSetu's own controls only inside JanSetu.
- [ ] Never inject styles into a live page outside an approved allowlist. Measuring an existing pilot locally in a headless browser is allowed and must be stated as an author-origin approximation.
- [ ] Use only `in.jansetu.app.qa` on devices. Record preview-profile hashes before and after any device step. Restore font scale, density, rotation, night mode and network afterwards.

### Text size and reflow

- [ ] At 390px, check document width, horizontal overflow, clipped containers (`overflow-x: hidden` with scrollWidth > clientWidth) and controls past the right edge.
- [ ] Resize the same document to 320px (WCAG reflow width) and 195px (200% zoom on a 390px phone). On desktop, check 1024, 853 and 640px (125%, 150% and 200% of 1280px).
- [ ] On Android, compare font 1.0 and 2.0 in the browser being assessed. In JanSetu's in-app browser, also compare A (100%) and A+.
- [ ] Note pages without a viewport meta tag. CSS cannot repair them; prefer guidance.

### JanSetu Assist control acceptance

For **Larger text**:

- [ ] Every offered size keeps the consent dialog, site search, site accessibility menu and primary links visible and operable.
- [ ] "Original size" works in one tap.
- [ ] Rotation and font change keep the page and scroll position.

For **stylesheet controls (class C)**:

- [ ] Apply and Restore with the installed extension. Restore returns identical computed styles; for overflow rules it also resets scroll position.
- [ ] Reload, in-page and cross-page navigation all leave unreviewed routes unstyled.
- [ ] Check 125%, 150% and 200% zoom, keyboard and screen reader.
- [ ] Every link destination is unchanged.

For **Reading view** (future):

- [ ] Never offered on a page with forms, gates, sign-in or payment.
- [ ] Shows the source URL and time; nothing omitted compared with the page.
- [ ] Closing it returns to the untouched page.

For **Guidance**:

- [ ] Every rule cites its source page and check date and names what is unverified.
- [ ] A moderated task shows the guidance answers the citizen's question.

### Before calling a page improved

- [ ] Show the documented problem reduced (for example, text readable at a named size, columns reachable, or task completed without error), without hiding information or breaking navigation. Applying a stylesheet or a zoom level is not evidence of improvement.
