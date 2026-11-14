# Google Play listing draft

Publisher: **Utkarsh**  
Support: **utkarshvaibhav268@gmail.com**  
Suggested category: **Tools** (confirm available category in Play Console).  
Status: draft; JanSetu has not been submitted or published. Directory reuse permission and the public-release catalog gate remain unresolved. This copy is not approved for public distribution.

## Store copy

**App name:** JanSetu

**Short description:** Find selected official Indian government portals and clear guides.

**Full description:**

JanSetu helps you find selected Indian government websites from an attributed directory. Search by need, browse categories, read a short guide where one has been reviewed, save useful portals, and open the official website. The local development catalog contains 7,347 websites: nine manually reviewed portals and 7,338 entries listed by IGOD. Coverage is incomplete, and directory listing is distinct from independent ownership review. The local Phase 3A build also includes 25 service-information records, with incomplete eligibility/documents and limited Tamil Nadu delivery coverage clearly disclosed. Signed local catalog files update Android content without reinstalling the APK. My Applications supports encrypted local records, user-maintained progress and optional generic deadline reminders. My Documents keeps encrypted copies of PDFs and photos you choose, only on your phone, with an optional private checklist of the documents a service lists. JanSetu never uploads or submits documents: you can export a copy, or turn on read-only access from Android's file picker and choose the file yourself on the official website. There is no live government-status integration, account or cloud sync. Tracker and document data are excluded from backup/transfer and are lost if app data is cleared or the app is uninstalled. This draft must be revised to match the permitted final public catalog before publication.

The National Portal of India can open in JanSetu's limited browser. Other portals open in your device browser. JanSetu does not process applications, collect passwords or one-time codes, determine eligibility, or make government decisions. Always check the official website for current rules, deadlines, and fees.

JanSetu is independently developed by Utkarsh. It is not affiliated with, endorsed by, or an official app of the Government of India.

**Support line:** Report a wrong link or outdated guide from a portal page, or contact utkarshvaibhav268@gmail.com.

## Screenshot and asset plan

Capture fresh screenshots from the final signed build: onboarding, home with the final permitted catalog and accurate counts, search results and empty state, a portal detail with source/date, a reviewed guide, Settings/privacy, and the National Portal browser. Existing development captures in `docs/screenshots/` are reference only. Use original JanSetu branding; do not use a government emblem or imply endorsement.

## Console entries to prepare

- Host the exact [privacy policy](PRIVACY.md) at a public, active URL and enter it in Play Console. The policy is also bundled in Settings.
- Complete the Data safety form against the final APK/AAB and SDK dependency graph, including third-party official-site behavior and support email flow. Do not copy a blanket “no data collected” answer without that review.
  - My Documents (Phase 7): files, names and preparation choices are processed and stored only on the device, encrypted, and never sent to JanSetu. Under Play's definitions, on-device processing that is not transmitted off the device is not "collection". However, sharing through the file picker and exported copies is initiated by the user to other apps or websites, and the form's guidance on user-initiated transfers must be checked against the final build.
  - Re-check the answers whenever Phase 8 (accounts) or Phase 9 (sync) adds any transmission.
- Enter the support email, content rating, app access, target audience, and government-app declaration truthfully.
- For a newly created personal developer account, check whether closed testing requires at least 12 opted-in testers for 14 continuous days before production access.
- Upload only a signed AAB after final QA and publisher review. The current local AAB is unsigned.

Official references: [target API requirements](https://support.google.com/googleplay/android-developer/answer/11926878), [Data safety](https://support.google.com/googleplay/android-developer/answer/10787469), [privacy policy](https://support.google.com/googleplay/android-developer/answer/18258653), [new personal-account testing](https://support.google.com/googleplay/android-developer/answer/14151465), [government information apps](https://support.google.com/googleplay/android-developer/answer/9514050), and [impersonation](https://support.google.com/googleplay/android-developer/answer/9888374).
