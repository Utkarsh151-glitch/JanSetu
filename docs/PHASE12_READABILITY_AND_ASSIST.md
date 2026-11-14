# Phase 12: government website readability and JanSetu Assist

18 October 2026. This phase starts from the Phase 6A decisions in `data/site_interventions.json`. JanSetu never claims to have changed a government website. Every change below is to JanSetu's own browser or extension.

## Done

### 1. National Portal in-app browser follows the Android font size (Phase 6A item 1)

- **Text size.** Website text now starts at the person's Android font size: 130 % at 1.3×, 200 % at 2× (capped at 200 %, never below 100 %).
  - A returns the page to its own size in one tap.
  - A+ adds 25 %, up to 200 %.
  - The current size is shown and announced, for example "Website text size 130 percent".
- **Only the zoom changes.** Only the WebView's `textZoom` changes. No page code, stylesheet or control is altered. The browser's security settings (HTTPS host policy, TLS cancellation, no mixed content, no file access) are unchanged and asserted in the test.
- **Reading position.** The page and the person's own size choice are saved across rotation and font-size changes, which recreate the screen. The reading position is restored as a share of the page height, so it survives reflow.
- **Tests:**
  - `BrowserTextSizeTest` (JVM, 3): start values, steps, and saving and restoring position
  - `BrowserReadingTest` (instrumented, 2): 2× font gives 200 %; A and A+; zoom is the only change; choices survive recreation
- **Not done** (needs a person, RB-09): the live-page check that the consent buttons, site search and accessibility menu stay visible and usable at 1.3× and 2.0×, and a TalkBack check.

### 2. NSP "Readable text" pilot withdrawn (Phase 6A item 4)

- **Allowlist.** `extension/core.mjs`'s `readablePaths` is now empty, so the panel never offers Readable text and no page is styled.
- **Registry.** `data/site_interventions.json` marks the page and its controls `retired`. The evidence and acceptance criteria are kept for any future proposal.
- **Validator.** `tools/site_interventions.py` now allows the protected `background.js` to keep one inert, removal-only route, and only for a retired pilot. It still fails if the allowlist and background drift apart, or if a retired page is re-allowlisted without a new decision (tests added).
- **Privacy policy.** English and Hindi now say the extension no longer changes how any site looks. The extension's privacy page was regenerated.
- **Owner follow-up (RB-14).** The `scripting` permission, `readability.css`, the background removal listener and the manifest description ("opt-in readability aid") now serve only to remove the old stylesheet. Removing them means changing hash-protected extension files (`manifest.json`, `background.js`, `readability.css`), which needs the owner's approval to rebaseline those hashes. Until then the pilot is withdrawn functionally, but the files remain.

## Not done, with reasons

| Item | Status | Reason |
| --- | --- | --- |
| RTI "Before you request" card (Phase 6A item 2) | **Done in Phase 17** ([details](PHASE17_AUTHORISED_CORRECTIONS.md)); earlier status: pending | The card needs all five official rules. Two are not yet recorded, and recording them changes the hash-protected service catalogue with fresh evidence. A guidance card also needs a documented reviewer decision before release. JanSetu still links the official guidelines and keeps the three recorded facts on the service pages. |
| TN e-Sevai clipped table (Phase 6A item 3) | Investigating (unchanged) | The single `overflow-x:auto` rule helps desktop extension users only and is not allowlisted. Filling fee and document gaps through the editorial workflow (Phase 11) is the better route for phone readers. |
| Any new styling | None | No page has evidence of a measured benefit and a reviewer decision. |

## Exit-gate status

- The browser change has evidence, tests and rollback: A returns to 100 %, and the start value is computed fresh each time. Its live-page human check is outstanding.
- The pilot withdrawal is complete in the allowlist, registry, validator, tests and policy. Removing the protected files is pending owner approval.
- No unapproved proposal is on any production allowlist.
- Status: **NEEDS_REVIEW**, with RB-09 and RB-14 outstanding.
