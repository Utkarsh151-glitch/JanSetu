# Phase 15: release-blocked report

18 October 2026. **Status: ENGINEERING IN PROGRESS — RELEASE BLOCKED.** No signed build exists. Nothing has been submitted to a store, and nothing will be without the project owner's explicit written authorisation (RB-12).

## Candidate built and inspected

| Item | Value |
| --- | --- |
| Build | `:app:assembleRelease` from the current source (not an earlier artifact) |
| Artifact | `app/build/outputs/apk/release/app-release-unsigned.apk`, 13,997,649 bytes, sha256 `e4daf85c02c33735fb8bd702d4e559157ff3cd7e864be654145ddb01218fe35d` |
| Identity | `in.jansetu.app`, version 0.4.1 (code 5); min API 26, target API 36 |
| Signing | **Unsigned.** Signing uses only the four `JANSETU_*` environment variables supplied by the owner; no key is in the repository (guard test). |
| Direct inspection (`python tools/inspect_release_artifact.py <apk> --release`) | **Passed:** <br>- not debuggable <br>- permissions are exactly INTERNET, POST_NOTIFICATIONS and RECEIVE_BOOT_COMPLETED (plus AndroidX's internal non-exported-receiver permission) <br>- exported: the launcher activity, the document provider (`MANAGE_DOCUMENTS`) and AndroidX's profile-install receiver (`DUMP`) <br>- assets: the catalogue, two privacy policies, the update public key and the ART baseline profile <br>- no research files, claims, evidence index, AI advice, staging or packets <br>- no internal review fields <br>- none of the 96 sentence-length official quotations appear in `catalog.json` |
| Gate | See the latest result in `data/validation_reports/phase5/` (QA_REPORT.md) |

**This candidate is not compliant for public distribution.** It contains the 7,338 directory-listed records (RB-01) and content derived from official pages whose reuse is undecided (RB-02). `python tools/check_catalog.py --public-release` fails as designed.

## Reduced-scope options (the owner decides; not built)

| Option | Content | Effect |
| --- | --- | --- |
| R1: Wait for permissions | The full catalogue once RB-01 and RB-02 are resolved | The intended product |
| R2: Reviewed portals only | The 9 manually reviewed portals. No directory listings. Services, guides and website profiles only if RB-02 allows JanSetu-written summaries of official pages. | A much smaller directory. The store copy and in-app counts must say so plainly, and must not imply the full directory. |
| R3: Tools without the directory | My Documents, My Applications and Prepare documents, with services only if RB-02 allows | Changes what the product is |

No reduced candidate was built. Shipping a much smaller catalogue is a product and legal decision, and the rules forbid using a reduced scope to misrepresent coverage. If the owner chooses R2 or R3, the build would add a packaging scope to `tools/package_catalog.py`, and the inspector and `--public-release` check would run on that build.

## Steps that remain before any public release

1. **RB-01, RB-02:** documented permissions, or a chosen reduced scope.
2. **RB-03, RB-04:** human editorial and language reviews.
3. **RB-05, RB-06:** the owner creates the production upload key and the catalogue-update key and keeps custody of them. Then build with `JANSETU_*` variables: `.\gradlew.bat :app:bundleRelease :app:assembleRelease`. Verify the signature with `apksigner verify --print-certs` and keep the checksums.
4. **Version:** bump to 0.5.0 (code 6) for the first candidate that includes Phase 7–12 features.
5. **Run checks** on the exact signed artifact:
   - the full quality gate
   - `inspect_release_artifact.py --release`
   - `stale_evidence.py --days 90` (RB-11)
   - a fresh link check
6. **RB-07:** privacy policy at a public URL; Play Data safety; government-information-app and target-audience declarations; screenshots from the signed build; store copy checked for any implied government affiliation (see `PLAY_LISTING.md`).
7. **RB-08:** brand and trademark review.
8. **RB-09, RB-10:** manual accessibility and physical-device checks.
9. **RB-12:** owner authorisation, then a closed test track before production.

## Rollback plan

- **Android:** keep the previous signed build and its checksum. Play releases can be halted during a staged rollout; a fix ships as a higher version code. Local data is never migrated destructively: tracker v2 and vault v1 refuse unknown schema versions instead of erasing.
- **Catalogue content:** signed catalogue updates are versioned. The app keeps the previous valid package and can restore the bundled catalogue.
- **Extension:** unpublish or ship the previous package. It has no server component.
