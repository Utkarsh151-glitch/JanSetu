# Phase 16: post-launch operations runbook (prepared, not in use)

18 October 2026. **No release has been authorised**, so nothing here is running yet. This is the process to adopt at launch. It keeps JanSetu local-first: no analytics, crash SDK or server is added without a recorded decision and a privacy update.

## Ownership

| Role | Responsibility |
| --- | --- |
| Project owner (Utkarsh) | Release authorisation, signing keys, legal and permission decisions, kill-switch calls |
| Editor (named) | Report triage, link checks, catalogue corrections through the evidence and review workflow |
| Engineer (named) | Defects, dependency and security updates, the release gate |

Escalation: a report that a page is misleading or unsafe goes to the editor the same day, and to the owner if it affects a sensitive journey (sign-in, payment, upload, application).

## Feedback and problem reports

- In the app, "Report a problem" opens an email draft with the portal title and URL. Nothing is sent automatically.
- Triage within 2 working days, labelled: wrong link, outdated fact, misleading wording, app defect, accessibility or privacy.
- Never ask for, store or forward identity documents, passwords, OTPs or application details. Delete any received by mistake, and reply that they were deleted.

## Catalogue maintenance

1. **Freshness.** Every month, run `python tools/stale_evidence.py --days 90`; schedule read-only re-checks oldest first. Anything older than 180 days without a re-check is withheld from the next catalogue update.
2. **Corrections.** New evidence gets a new ID. `revisionHistory` records the previous digest. Review packets go to named reviewers, and the importer appends decisions. Never edit old quotes, and never record an AI suggestion as a human decision.
3. **Staged updates.** Build a signed catalogue file with a higher version and run the quality gate. Release it to testers before everyone.
4. **Rollback.** The app keeps the previous valid catalogue. Ship the prior version as a new, higher-numbered file, or tell people how to restore the bundled catalogue.

## Correcting misinformation quickly

- Withhold first, fix second. Remove the claim or link in a catalogue update, so the app shows "Not checked yet" rather than wrong information. Then re-check and republish through review.
- A dangerous link (phishing, a wrong payment host) gets an emergency catalogue update within 24 hours, and a note in the release log.

## Kill-switch criteria

Temporarily disable a feature through an app or catalogue update if:
- it misleads people about government status or approval
- it exposes personal data
- it breaks a sensitive journey's hand-off
- a security fix is not yet available

Candidates: the file-picker provider (already off by default; an update can disable the root), in-app browsing for a host, a website profile or a service record.

## Security and dependencies

- Monthly: check AndroidX, Kotlin and Compose releases and security bulletins, and run a dependency scanner. Apply security fixes through the full gate.
- Rotate the catalogue-update key under the RB-06 policy. Never put keys in the repository or the APK.

## Monitoring (privacy-preserving)

- No analytics or crash reporting is built in. Use Play Console's anonymised vitals (crashes, ANRs) only.
- If remote crash reporting is ever proposed, it needs: an owner decision, a privacy-policy update, Data safety changes, opt-in consent, and scrubbing of all personal fields. It must never include document contents, names, application details or URLs typed by the person.

## Deletion requests

- **Local data:** people delete it themselves in the app (single items, "Remove all", uninstall). JanSetu holds no server copy.
- **Support emails:** delete on request, subject to the retention rule the owner sets (RB-07).

## Regression testing

Every release candidate runs the full quality gate on the isolated QA build, the release-artifact inspection, and the manual checklists for the features it changes.
