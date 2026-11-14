# JanSetu Phase 6C — AI-assisted Review Handoff

**Internal working file. Do not publish. Not a human review and not importable by `tools/editorial_review.py`.**

This is a claim-by-claim AI recommendation pass over the two uploaded review packets and available public official pages. It deliberately does **not** fill in a reviewer name, attest that a human opened a source, or write a formal approve/revise/hold decision. Every claim still requires a real person to make the final decision in JanSetu’s review packet.

## Scope and counts

- Packets reviewed: `review-packet-2026-10-17.json` (15 profiles) and `review-packet-2026-10-18.json` (10 profiles).
- Total profiles: **25**; total individual claims: **157** (99 + 58).
- Official pages were opened where the web reader could access/extract them. Tool timeouts and empty extraction are described as inconclusive, never as proof that the government site is down.
- No live form was submitted and no personal data/credentials were entered.
- `JANSETU_PHASE6C_AI_REVIEW_RECOMMENDATIONS.json` contains machine-readable per-claim suggestions, source IDs, original flags, and suggested wording where a change is recommended.

## Recommendations by profile

### Tamil Nadu eSevai Portal, Tamil Nadu (`igod-2715637a8f399700`)
- **AI triage:** `REVISE_BEFORE_HUMAN_REVIEW`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=6, REVISE=2.
- Existing uncertainties: The service list publishes a timeline, fee and document list for each service. JanSetu does not copy these into the website record; they belong to individual service records.; The application-tracking page is on a separate official host (tnedistrict.tn.gov.in) and was not loaded.
- **purpose** — `APPROVE_CANDIDATE`: Tamil Nadu Government's e-Sevai portal for certificates, licences and other government services, with a published list of the services offered.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `REVISE`: Tamil Nadu e-Governance Agency (TNeGA), Information Technology and Digital Services Department, Government of Tamil Nadu
  - Reason: The evidence names TNeGA as the state nodal agency and lists the IT & Digital Services Department in the header, but the current wording combines agency, department and government as a single operator. Separate ownership/department from implementation role.
  - Suggested replacement wording: Government of Tamil Nadu; the checked About page identifies Tamil Nadu e-Governance Agency (TNeGA) as the State Nodal Agency for government e-governance initiatives.
- **jurisdiction** — `APPROVE_CANDIDATE`: Tamil Nadu
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **languages** — `REVISE`: Tamil and English (the home page was served in Tamil; the About, Contact, FAQ and service-list pages in English)
  - Reason: The claim infers site-wide language availability because different subpages were served in Tamil and English; no language selector was found. Narrow the wording to page-level observation or leave the languages field unknown.
  - Suggested replacement wording: Tamil was observed on the home page and English on the checked About/Contact/FAQ/service-list pages; a site-wide language switch was not confirmed.
- **tasks** — `APPROVE_CANDIDATE`: List of services offered
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx (AI check: not_exactly_confirmed; The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.)
- **tasks** — `APPROVE_CANDIDATE`: Track an application
  - Reason: Although the original packet marked this as link-only, this exact target was opened in the current AI browser check and its public purpose was consistent with the label. A human must still open the target in the review packet and attest before import.
  - Target: https://tnedistrict.tn.gov.in/tneda/ (AI check: loaded; Official Tamil Nadu e-District page opened and showed Application Status and certificate verification links; no data entered.)
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://www.tnesevai.tn.gov.in/Pages/FAQ.aspx (AI check: not_exactly_confirmed; The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.)
- **help** — `APPROVE_CANDIDATE`: Contact TNeGA
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://www.tnesevai.tn.gov.in/Pages/ContactUs.aspx (AI check: not_exactly_confirmed; The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.)

### eDistrict - Services, Kerala (`igod-50b73c3937b8d29b`)
- **AI triage:** `READY_FOR_HUMAN_REVIEW_NOT_APPROVAL`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=7.
- Existing uncertainties: The tracking page was observed as a link and was not loaded.
- **purpose** — `APPROVE_CANDIDATE`: Kerala's eDistrict portal for government certificates and services, also delivered through Common Service Centres.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Kerala State IT Mission (implements and manages the portal)
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **audience** — `APPROVE_CANDIDATE`: Citizens; services are also available through Common Service Centres (CSCs)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: Kerala
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **languages** — `APPROVE_CANDIDATE`: English and Malayalam (language switch observed)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **tasks** — `APPROVE_CANDIDATE`: Track an application
  - Reason: The exact official target was opened in this AI check and identifies itself as Track Application. A human still needs to open/confirm it in the review packet; no form was submitted.
  - Target: https://edistrict.kerala.gov.in/openSearch.do?openStat=openSearch&lang=en (AI check: loaded; Official Track Application page opened; no form values were entered.)
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://edistrict.kerala.gov.in/generalService.htm?event=faq (AI check: not_exactly_confirmed; Other pages on this host were seen, but this exact URL was not fully confirmed.)

### eDistrict Portal, Odisha (`igod-5e885f0e333d5206`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=3, HOLD=2, REVISE=1.
- Existing uncertainties: The FAQ and contact information are sections of the home page, not separate pages.
- **purpose** — `APPROVE_CANDIDATE`: Odisha's eDistrict portal for citizen services delivered by district administrations, including e-certificates from the Revenue & Disaster Management Department.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `REVISE`: Government of Odisha, using NIC's ServicePlus platform
  - Reason: Saved wording calls Government of Odisha the operator, while evidence says it implemented NIC ServicePlus. The portal page does not name a responsible department; avoid asserting a specific operating department.
  - Suggested replacement wording: The Government of Odisha says it implemented NIC’s ServicePlus platform to deliver citizen services; the responsible operating department is not named on the checked page.
- **audience** — `APPROVE_CANDIDATE`: Citizens
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: Odisha
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **help** — `HOLD`: FAQ section (home page)
  - Reason: The homepage was fetched, but the saved #team target is not confirmed as a functioning FAQ section in the accessible page content. Test the fragment manually; remove or revise if it does not land correctly.
  - Target: https://edistrict.odisha.gov.in/#team (AI check: anchor_not_confirmed; Homepage was loaded but the extracted page did not confirm an FAQ section at #team.)
- **help** — `HOLD`: Contact section (home page)
  - Reason: The homepage was fetched, but the saved #contact target is not confirmed as a functioning contact section in the accessible page content. Test the fragment manually; remove or revise if it does not land correctly.
  - Target: https://edistrict.odisha.gov.in/#contact (AI check: anchor_not_confirmed; Homepage was loaded but the extracted page did not confirm a Contact section at #contact.)

### Online Citizen Services Portal, Delhi (`igod-9908672336cdd912`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=7, HOLD=2.
- Existing uncertainties: Applying for a service starts with sign-in (Login & Apply); JanSetu does not link to it.; The Help and tracking pages were observed as links and were not loaded.
- **purpose** — `APPROVE_CANDIDATE`: Delhi Government's eDistrict portal where citizens access government services, track applications and verify or print certificates.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Department of Information Technology, Government of National Capital Territory of Delhi
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **audience** — `APPROVE_CANDIDATE`: Citizens
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: National Capital Territory of Delhi
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **languages** — `APPROVE_CANDIDATE`: English and Hindi (language switch observed)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **tasks** — `HOLD`: Track an application
  - Reason: The packet marked the target as link-only and the current web reader timed out. Do not treat the timeout as proof of a broken page. Open the exact URL in a normal browser before approving the link.
  - Target: https://edistrict.delhigovt.nic.in/Public/TrackApplication (AI check: fetch_timeout; Web reader timed out; not evidence that the target is broken.)
- **help** — `APPROVE_CANDIDATE`: Contact details
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://edistrict.delhigovt.nic.in/in/en/Public/ContactDetails.html (AI check: not_exactly_confirmed; The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.)
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://edistrict.delhigovt.nic.in/in/en/Public/Faq.html (AI check: not_exactly_confirmed; The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.)
- **help** — `HOLD`: Help
  - Reason: The target was only seen as a link in the saved packet or has not been confirmed on its own. Open the exact destination in a normal browser before approving this link. A fetch timeout/error is not proof that the page is broken.
  - Target: https://edistrict.delhigovt.nic.in/in/en/Public/Help.html (AI check: not_exactly_confirmed; The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.)

### National Scholarship Portal (`national-scholarship-portal`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=5, HOLD=1, REVISE=1.
- Existing uncertainties: Each scholarship scheme belongs to its own ministry or body; scheme owners are shown on the individual service records, not here.; Applying needs a One Time Registration sign-in, which JanSetu does not link to directly.
- **purpose** — `APPROVE_CANDIDATE`: National portal for applying for the scholarship schemes listed on NSP.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `REVISE`: Ministry of Electronics & Information Technology, Government of India (named in the site header)
  - Reason: MeitY appears in the site header, but the portal contains schemes run by different ministries; the evidence does not establish MeitY as sole operator. Keep the site-header fact distinct from scheme ownership.
  - Suggested replacement wording: Government of India portal; the checked header names the Ministry of Electronics and Information Technology. Scholarship schemes may belong to different ministries.
- **audience** — `APPROVE_CANDIDATE`: Students, institutions, officers and the public each have a section
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: India (national)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **languages** — `APPROVE_CANDIDATE`: English original; other languages are machine translations (Bhashini)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **help** — `APPROVE_CANDIDATE`: NSP helpdesk
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://scholarships.gov.in/nsphelpdesk (AI check: loaded; NSP helpdesk page extracted, including Student/Institute/Officer/Public sections and helpdesk contact areas.)
- **help** — `HOLD`: PFMS helpdesk
  - Reason: The current web reader timed out on the exact target. The source page labels it PFMS Helpdesk, but verify the destination in a normal browser before publishing this link.
  - Target: https://scholarships.gov.in/pfmshelpdesk (AI check: fetch_timeout; Web reader timed out; not evidence that the target is broken.)

### National Career Service (`national-career-service`)
- **AI triage:** `REVISE_BEFORE_HUMAN_REVIEW`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=4, REVISE=1.
- Existing uncertainties: The site's Contact Us link points to a map service, not an official page, so JanSetu does not link to it.; The page's meta description refers to unrelated engineering services; JanSetu ignores it.; Job listings are posted by employers and are not checked by JanSetu.
- **purpose** — `APPROVE_CANDIDATE`: Employment and career services: job search and matching, career information, counselling and job fairs.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Ministry of Labour & Employment, Government of India
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **audience** — `REVISE`: Job seekers, employers, skill providers, career counsellors, placement organisations, households and government departments
  - Reason: The source lists Local Service Providers and Career Centers too, but the current summary omits them while reading like a complete enumeration. Either add the omitted groups or explicitly say “including”.
  - Suggested replacement wording: Job seekers, employers, skill providers, career counsellors, local service providers, career centres, placement organisations, households and government departments.
- **jurisdiction** — `APPROVE_CANDIDATE`: India (national)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://ncs.gov.in/faq (AI check: not_exactly_confirmed; The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.)

### CPGRAMS (`cpgrams`)
- **AI triage:** `READY_FOR_HUMAN_REVIEW_NOT_APPROVAL`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=8.
- Existing uncertainties: The portal says grievances sent by email are not attended to; they must be lodged on the portal.; Lodging a grievance needs sign-in; JanSetu links to information and status pages only.; The completeness of each language version was not checked.
- **purpose** — `APPROVE_CANDIDATE`: Lodge public grievances about government service delivery online, at any time, and follow their status.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Department of Administrative Reforms & Public Grievances, Ministry of Personnel, Public Grievances & Pensions
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **audience** — `APPROVE_CANDIDATE`: Citizens
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: Ministries and departments of the Government of India, and States
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **languages** — `APPROVE_CANDIDATE`: English, Hindi, Tamil and other Indian languages (language selector observed)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **tasks** — `APPROVE_CANDIDATE`: Check grievance status
  - Reason: The exact official View Status page opened in this AI check. No status details were entered. Human review still required by the project’s workflow.
  - Target: https://pgportal.gov.in/Status (AI check: loaded; Official CPGRAMS View Status page opened; no personal data entered.)
- **help** — `APPROVE_CANDIDATE`: FAQs and help
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://pgportal.gov.in/Home/Faq (AI check: not_exactly_confirmed; Other pages on this host were seen, but this exact URL was not fully confirmed.)
- **help** — `APPROVE_CANDIDATE`: Contact us
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://pgportal.gov.in/Home/ContactUs (AI check: not_exactly_confirmed; Other pages on this host were seen, but this exact URL was not fully confirmed.)

### RTI Online (`rti-online`)
- **AI triage:** `READY_FOR_HUMAN_REVIEW_NOT_APPROVAL`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=6.
- Existing uncertainties: The portal says applications for State Government authorities filed here would be returned.
- **purpose** — `APPROVE_CANDIDATE`: Submit RTI requests and first appeals online to central public authorities, and check their status.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Department of Personnel & Training, Government of India
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **jurisdiction** — `APPROVE_CANDIDATE`: Central Government public authorities only; not State Government authorities, including the Government of NCT of Delhi
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **languages** — `APPROVE_CANDIDATE`: English and Hindi
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://rtionline.gov.in/faq.php?pageid=1679091c5a880faf6fb5e6087eb1b2dc (AI check: loaded; Official FAQ page opened.)
- **help** — `APPROVE_CANDIDATE`: Contact us
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://rtionline.gov.in/Contactus.php (AI check: loaded; Official contact page opened.)

### Udyam Registration (`udyam-registration`)
- **AI triage:** `READY_FOR_HUMAN_REVIEW_NOT_APPROVAL`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=5.
- Existing uncertainties: The portal warns that no private agency is authorised to do MSME registration; JanSetu links only to the official portal.; The grievance link opens the separate MSME Champions portal (champions.gov.in), which was not loaded.
- **purpose** — `APPROVE_CANDIDATE`: The Government of India's official portal for Udyam (MSME) registration.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Ministry of Micro, Small and Medium Enterprises, Government of India
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **audience** — `APPROVE_CANDIDATE`: Micro, small and medium enterprises, including entrepreneurs registering for the first time
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: India (national)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **help** — `APPROVE_CANDIDATE`: Grievances (MSME Champions portal)
  - Reason: The target redirects to the official MSME CHAMPIONS grievance portal. Keep it labelled as the separate MSME CHAMPIONS grievance route, not as a Udyam registration support page.
  - Target: https://champions.gov.in/ (AI check: loaded_redirect; Redirected to the official MSME CHAMPIONS complaint page; confirms this host is an MSME grievance route, but human review should confirm the precise intended link.)

### Revenue and Disaster Management Department, Puducherry (`igod-7c7865e8e6982bc1`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=4, HOLD=2.
- Existing uncertainties: The first capture failed with a DNS error; one retry the same day loaded the page.; Emergency helpline numbers appear on the page; JanSetu does not copy contact numbers.; The contacts, charter and services pages were observed as links and were not loaded.
- **purpose** — `APPROVE_CANDIDATE`: Official website of Puducherry's Department of Revenue and Disaster Management, with its services and schemes, citizen charter and disaster-management information.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Department of Revenue and Disaster Management, Government of Puducherry
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **jurisdiction** — `APPROVE_CANDIDATE`: Puducherry
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **tasks** — `APPROVE_CANDIDATE`: Services and schemes
  - Reason: The exact official Services and Schemes URL opened in this AI check; the extracted response identifies the Revenue and Disaster Management Department page. Human review still required.
  - Target: https://revenue.py.gov.in/services_and_scheme.html (AI check: loaded; Official Department of Revenue and Disaster Management Services and Schemes page opened.)
- **help** — `HOLD`: Contacts
  - Reason: The exact target was not extracted by the web reader. This is inconclusive; check it in a normal browser before approving the contact link.
  - Target: https://revenue.py.gov.in/contact_us.html (AI check: fetch_error; Web reader did not extract target; normal-browser check required.)
- **help** — `HOLD`: Citizen charter
  - Reason: The exact Citizen Charter target was not independently loaded in this review. Open it in a normal browser before publishing the link.
  - Target: https://revenue.py.gov.in/citizen_charter.html (AI check: not_opened_this_session; Exact target needs human check.)

### Income Tax e-Filing (`income-tax-efiling`)
- **AI triage:** `READY_FOR_HUMAN_REVIEW_NOT_APPROVAL`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=6.
- Existing uncertainties: The first capture failed with a DNS error; one retry the same day loaded the pages.; Filing, payment and profile services need sign-in; JanSetu does not link to sign-in pages.
- **purpose** — `APPROVE_CANDIDATE`: Official single-window portal for income tax e-filing and related services.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Income Tax Department, Ministry of Finance, Government of India
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **audience** — `APPROVE_CANDIDATE`: Taxpayers and other stakeholders
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: India (national)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **help** — `APPROVE_CANDIDATE`: Help and support centre
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://www.incometax.gov.in/iec/foportal/help (AI check: loaded; Official Income Tax help page opened.)
- **help** — `APPROVE_CANDIDATE`: Contact us
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://www.incometax.gov.in/iec/foportal/contact-us (AI check: loaded; Official Income Tax contact page opened.)

### Voters' Service Portal, Election Commission of India (`igod-85affa23dc47255b`)
- **AI triage:** `REVISE_BEFORE_HUMAN_REVIEW`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=2, REVISE=2.
- Existing uncertainties: The search needs a captcha; JanSetu does not interact with it.; The first capture failed with a DNS error; one retry the same day loaded the page.; Voter registration (Form 6) and record changes (Form 8) are separate service records.
- **purpose** — `APPROVE_CANDIDATE`: Search for an entry in the electoral roll by EPIC number, by personal details or by mobile number.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Election Commission of India
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **jurisdiction** — `REVISE`: All states and union territories (chosen in the search)
  - Reason: The evidence shows a state selector, not an explicit statement that every State and Union Territory is covered. Avoid turning the selector observation into a full coverage claim without checking the complete selector and official scope.
  - Suggested replacement wording: The search page offers a state-selection control; full State/UT coverage should be confirmed before stating it explicitly.
- **languages** — `REVISE`: English, Hindi, Tamil and other Indian languages (language selector observed)
  - Reason: The evidence quotes Hindi and Tamil options, but “and other Indian languages” is broader than the recorded language evidence. List only options actually observed on the checked page, or cite the full selector.
  - Suggested replacement wording: English, Hindi and Tamil options were observed; additional language coverage was not established by the captured evidence.

### DigiLocker (`digilocker`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=4, HOLD=1, REVISE=1.
- Existing uncertainties: The About page cites Rule 9A of the Digital Locker Rules, 2016 on the status of issued documents; JanSetu does not restate legal status.; Using an account needs sign-in; JanSetu does not link to sign-up or sign-in pages.
- **purpose** — `APPROVE_CANDIDATE`: Access authentic digital documents issued by registered government departments, educational institutions and other authorised organisations.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: National e-Governance Division, Ministry of Electronics and Information Technology
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **audience** — `REVISE`: Citizens; partner organisations (document issuers and requesters) have a separate section
  - Reason: The citizens/document access audience is supported by the About text, but the “partner organisations (issuers and requesters)” half rests mainly on a “Become a Partner” label. Confirm the partner page or narrow the audience claim.
  - Suggested replacement wording: Citizens accessing authentic digital documents; information for partner organisations should be described separately only after the partner section is checked.
- **jurisdiction** — `APPROVE_CANDIDATE`: India (national)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://www.digilocker.gov.in/web/about/faq (AI check: not_opened_this_session; Packet reports a loaded saved page; not separately opened during this review.)
- **help** — `HOLD`: Contact us
  - Reason: The current web reader returned a page title but no extractable body for this contact target. Check the actual rendered page in a normal browser before approving it.
  - Target: https://www.digilocker.gov.in/web/about/contact-us (AI check: empty_extraction; URL returned a DigiLocker page title but no extractable body text; does not prove failure; human visual check needed.)

### Official Portal of Tamil Nadu Government (`igod-ec86b21083980772`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=6, HOLD=1, REVISE=1.
- Existing uncertainties: The grievances and online-services pages were observed as links and were not loaded.
- **purpose** — `APPROVE_CANDIDATE`: Official portal of the Government of Tamil Nadu: single-window access to state government information and services, including announcements, press releases and government orders.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `REVISE`: Government of Tamil Nadu; developed by National Informatics Centre, Tamil Nadu State Centre
  - Reason: The claim combines content owner and technical developer. Official footer says content is owned/maintained by Government of Tamil Nadu, while NIC designed/developed the site. Represent those roles separately.
  - Suggested replacement wording: Content owner: Government of Tamil Nadu. Site designed and developed by the National Informatics Centre.
- **jurisdiction** — `APPROVE_CANDIDATE`: Tamil Nadu
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **tasks** — `APPROVE_CANDIDATE`: Online services
  - Reason: Although the original packet marked this as link-only, this exact target was opened in the current AI browser check and its public purpose was consistent with the label. A human must still open the target in the review packet and attest before import.
  - Target: https://www.tn.gov.in/services.php (AI check: loaded; Official Online Services page opened.)
- **help** — `APPROVE_CANDIDATE`: Help desk and helpline numbers
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://www.tn.gov.in/help_desk.php (AI check: loaded; Official help desk page opened.)
- **help** — `HOLD`: Contact directory
  - Reason: The exact contact directory target timed out in the web reader. This is not proof of an unavailable page; verify the target in a normal browser before approval.
  - Target: https://www.tn.gov.in/contact_directory.php (AI check: fetch_timeout; Web reader timed out; this is not evidence that the URL is broken.)
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://www.tn.gov.in/faq.php (AI check: not_exactly_confirmed; Other pages on this host were seen, but this exact URL was not fully confirmed.)
- **help** — `APPROVE_CANDIDATE`: Grievances
  - Reason: Although the original packet marked this as link-only, this exact target was opened in the current AI browser check and its public purpose was consistent with the label. A human must still open the target in the review packet and attest before import.
  - Target: https://www.tn.gov.in/grievances.php (AI check: loaded; Official grievances page opened; it links to different grievance destinations by subject.)

### Official Portal of Puducherry Government (`igod-fd26b3c2335b6081`)
- **AI triage:** `REVISE_BEFORE_HUMAN_REVIEW`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=7, REVISE=1.
- Existing uncertainties: The FAQ and online-services pages were observed as links and were not loaded.
- **purpose** — `APPROVE_CANDIDATE`: Official website of the Government of Puducherry, with links to departments, the citizen's charter, online services, e-District and public grievances.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `REVISE`: Government of Puducherry; website managed by the Department of Information Technology
  - Reason: The reviewed contact-page webmaster entry is weaker than the official portal FAQ. The FAQ says content updates are managed by officials in the Directorate of Information Technology and identifies Puducherry e-Governance Society as the nodal agency. Add this official source as new evidence; do not edit existing evidence quotes.
  - Suggested replacement wording: Government of Puducherry; the official portal FAQ says content updates are managed by the Directorate of Information Technology and identifies the Puducherry e-Governance Society as the State Portal project’s nodal agency.
- **jurisdiction** — `APPROVE_CANDIDATE`: Union Territory of Puducherry: Puducherry, Karaikal, Mahe and Yanam
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **tasks** — `APPROVE_CANDIDATE`: Online services
  - Reason: Although the original packet marked this as link-only, this exact target was opened in the current AI browser check and its public purpose was consistent with the label. A human must still open the target in the review packet and attest before import.
  - Target: https://www.py.gov.in/onlineservices (AI check: loaded; Official Online Services page opened.)
- **help** — `APPROVE_CANDIDATE`: Help desk
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://www.py.gov.in/help-desk (AI check: loaded; Official Help Desk page opened.)
- **help** — `APPROVE_CANDIDATE`: Contact us
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://www.py.gov.in/contact-us (AI check: loaded; Official Contact Us page opened.)
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: Although the original packet marked this as link-only, this exact target was opened in the current AI browser check and its public purpose was consistent with the label. A human must still open the target in the review packet and attest before import.
  - Target: https://www.py.gov.in/frequently-asked-questions (AI check: loaded; Official FAQ page opened; it explicitly says content updates are managed by Directorate of Information Technology and names the Puducherry e-Governance Society as nodal agency.)
- **help** — `APPROVE_CANDIDATE`: Public grievance (CPGRAMS)
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://pgportal.gov.in/ (AI check: loaded; Official CPGRAMS home page extracted.)

### Official Portal of Himachal Pradesh Government (`igod-ffa4189a2ac66991`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=4, HOLD=2, REVISE=1.
- Existing uncertainties: The FAQ answers are collapsed on the page; only the questions were captured.; The helpline page and services.hp.gov.in were seen as links and were not loaded.; Seven service candidates awaiting review link to this portal; they are reviewed as service records, not here.
- **purpose** — `APPROVE_CANDIDATE`: Official portal of the Government of Himachal Pradesh: single-window access to state government information and services.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `REVISE`: Government of Himachal Pradesh; designed, developed and hosted by National Informatics Centre, Himachal Pradesh State Centre
  - Reason: Evidence distinguishes Government of Himachal Pradesh as portal owner from NIC as designer/developer/host. Avoid collapsing those roles into one operator label.
  - Suggested replacement wording: Content owner: Government of Himachal Pradesh. Designed, developed and hosted by the National Informatics Centre, Himachal Pradesh State Centre.
- **audience** — `APPROVE_CANDIDATE`: Citizens and other stakeholders
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: Himachal Pradesh
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **tasks** — `HOLD`: Departmental citizen services
  - Reason: The target was only seen as a link in the packet and the current web reader could not extract it. This is inconclusive, not proof that the site is broken. Verify the exact host in a normal browser.
  - Target: https://services.hp.gov.in/ (AI check: fetch_error; Web reader did not extract the cross-host service portal; this is inconclusive and must be checked in a normal browser.)
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://himachal.gov.in/Content/HtmlPage?qs=LdEF%2BAuYkB6vDHvsMAvpTw%3D%3D (AI check: not_exactly_confirmed; The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.)
- **help** — `HOLD`: Helpline
  - Reason: The Helpline label was seen in the source page, but the target itself was not loaded. Verify that the destination opens to the intended Helpline page before approval.
  - Target: https://himachal.gov.in/Content/HtmlPage?qs=LdEF%2BAuYkB7rMRolWuVbNg%3D%3D (AI check: not_exactly_confirmed; The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.)

### SevanaPension Direct To Home Module, Kerala (`igod-ddb798b880079969`)
- **AI triage:** `READY_FOR_HUMAN_REVIEW_NOT_APPROVAL`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=5.
- Existing uncertainties: This is a tool for banks; pensioners are not its users.; The FAQ PDF linked from the page was not captured.
- **purpose** — `APPROVE_CANDIDATE`: Software that co-operative banks in Kerala use to disburse social security pensions (Sevana Pension Direct To Home module).
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **audience** — `APPROVE_CANDIDATE`: Co-operative banks that disburse social security pensions
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: Kerala
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **help** — `APPROVE_CANDIDATE`: Help and FAQ
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://dbt.lsgkerala.gov.in/Help_Faq.aspx (AI check: loaded; Official help/FAQ page opened.)
- **help** — `APPROVE_CANDIDATE`: Contact details
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://dbt.lsgkerala.gov.in/Contactdetails.aspx (AI check: loaded; Official contact page opened.)

### State Scholarship Portal, Odisha (`igod-781379c678ce1e21`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=5, HOLD=2, REVISE=1.
- Existing uncertainties: Scheme rules, eligibility and dates belong to individual scholarship records, not to this website record.; The status page was seen as a link and was not loaded.
- **purpose** — `APPROVE_CANDIDATE`: Odisha's common scholarship portal, linking students with scholarships offered by state departments.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `REVISE`: Government of Odisha; several state departments use it as a common platform
  - Reason: The evidence shows a Government of Odisha common scholarship platform used by multiple state departments but no single operating department. Say that explicitly rather than presenting it as an identified operator.
  - Suggested replacement wording: Government of Odisha common scholarship platform used by several state departments; the checked About page does not name a single responsible operating department.
- **audience** — `APPROVE_CANDIDATE`: Students seeking scholarships
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: Odisha
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **languages** — `APPROVE_CANDIDATE`: English and Odia (language label observed)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **tasks** — `HOLD`: Know your application status
  - Reason: The exact status page was not extractable in this AI check. The source menu contains a “Know Your Status” link, but verify the target in a normal browser before approving it.
  - Target: https://scholarship.odisha.gov.in/know-your-status (AI check: fetch_error; Web reader did not extract exact status target; this does not prove it is broken.)
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://scholarship.odisha.gov.in/faqs (AI check: fetch_error; Web reader did not extract page during this review; packet says earlier capture loaded it.)
- **help** — `HOLD`: Contact us
  - Reason: The exact contact URL returned a portal title but no extractable body in this check. Confirm that the rendered page provides meaningful contact information before publishing the link.
  - Target: https://scholarship.odisha.gov.in/content/contact-us (AI check: empty_extraction; URL returned a State Scholarship Portal title but no extractable body; needs human visual confirmation.)

### Bhavishya, Pension Sanction and Payment Tracking System (`igod-c01d8df9282d87de`)
- **AI triage:** `REVISE_BEFORE_HUMAN_REVIEW`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=6, REVISE=1.
- Existing uncertainties: Which employees may register is described on a 'Who Can Register Online' page that was not captured.; Pension rules and forms are on pensionersportal.gov.in and belong to service records.
- **purpose** — `APPROVE_CANDIDATE`: Online system from the Department of Pension & Pensioners' Welfare for tracking pension sanction and payment for retiring employees.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Department of Pension & Pensioners' Welfare, Ministry of Personnel, Public Grievances & Pensions, Government of India
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **audience** — `REVISE`: Retiring employees and the administrative authorities that process their pensions
  - Reason: The evidence supports individuals and administrative authorities in pension processing, but the captured pages do not establish whether the system is limited to central-government employees. Do not imply eligibility beyond the sources.
  - Suggested replacement wording: Retiring employees and administrative authorities involved in pension processing; the checked pages do not fully specify employee coverage or eligibility.
- **jurisdiction** — `APPROVE_CANDIDATE`: India (Government of India)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **tasks** — `APPROVE_CANDIDATE`: How to avail the service
  - Reason: Although the original packet marked this as link-only, this exact target was opened in the current AI browser check and its public purpose was consistent with the label. A human must still open the target in the review packet and attest before import.
  - Target: https://bhavishya.nic.in/availservice.aspx (AI check: loaded; Official How to Avail Service page opened.)
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://bhavishya.nic.in/FAQs.aspx?pid=faq (AI check: loaded; Official FAQ page opened.)
- **help** — `APPROVE_CANDIDATE`: Contact us
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://bhavishya.nic.in/ContactUs.aspx (AI check: loaded; Official contact page opened.)

### Broadcast Seva Portal (`igod-7bb47a1cab5183f2`)
- **AI triage:** `READY_FOR_HUMAN_REVIEW_NOT_APPROVAL`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=3.
- Existing uncertainties: The page also shows helpdesk numbers; JanSetu does not copy contact numbers.
- **purpose** — `APPROVE_CANDIDATE`: Single-point online facility for broadcast-related activities with the Ministry of Information and Broadcasting.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Ministry of Information and Broadcasting, Government of India
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **jurisdiction** — `APPROVE_CANDIDATE`: India (national)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.

### CM Fellowship Scheme for Academic Excellence, Jharkhand (`igod-e300869e279b9cd4`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=7, HOLD=1.
- Existing uncertainties: Eligibility for each fellowship belongs to scheme records; it is not copied here.; The grievances page was seen as a link and was not loaded.
- **purpose** — `APPROVE_CANDIDATE`: Jharkhand portal for applying to the CM Fellowship schemes, which support research and higher education with financial assistance and foreign education fellowships.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Department of Higher and Technical Education, Government of Jharkhand
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **audience** — `APPROVE_CANDIDATE`: Students of Jharkhand
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: Jharkhand
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **languages** — `APPROVE_CANDIDATE`: English and Hindi (language switch observed)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://scholarshipsdhte.jharkhand.gov.in/faq (AI check: fetch_error; Web reader did not extract target; packet includes saved evidence for the page.)
- **help** — `APPROVE_CANDIDATE`: Contact us
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://scholarshipsdhte.jharkhand.gov.in/contact (AI check: fetch_error; Web reader did not extract target; packet includes saved evidence for the page.)
- **help** — `HOLD`: Grievances
  - Reason: The Grievances URL was not loaded in the original packet and could not be extracted in this review. Verify that it is a public grievance information route rather than a sign-in/application journey.
  - Target: https://scholarshipsdhte.jharkhand.gov.in/grievances (AI check: fetch_error; Exact route not extracted; no claim about availability is made.)

### Common Scholarship Portal, Nagaland (`igod-dc063c5a252aabe5`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=3, HOLD=1, REVISE=1.
- Existing uncertainties: Scheme dates and guidelines on the page belong to scheme records.
- **purpose** — `APPROVE_CANDIDATE`: Nagaland's single-window portal where students find and apply for state government scholarship schemes.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `REVISE`: Government of Nagaland (no department is named on the home page)
  - Reason: The source says this is an official Government of Nagaland portal but does not name the responsible department. Preserve that limitation rather than presenting a department-level operator.
  - Suggested replacement wording: Official portal of the Government of Nagaland; the checked homepage does not identify the responsible department.
- **audience** — `APPROVE_CANDIDATE`: Students
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **jurisdiction** — `APPROVE_CANDIDATE`: Nagaland
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **help** — `HOLD`: Contact section (home page)
  - Reason: The packet treats #contact as link-only. In the current extracted homepage a Contact Us target was not found; verify the fragment and actual contact details manually or remove this link.
  - Target: https://scholarship.nagaland.gov.in/#contact (AI check: anchor_not_confirmed; Homepage was loaded but extracted content did not show a Contact Us target for this fragment.)

### Department of General Administration and Public Grievance, Odisha (`igod-5891bfd68a83ed15`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: HOLD=6.
- Existing uncertainties: The FAQ, contact and status pages were seen as links and were not loaded.; Registering a grievance needs sign-in; JanSetu does not link to it.
- **purpose** — `HOLD`: Odisha's public grievance redressal portal for registering grievances and following their resolution.
  - Reason: The wording is an automated translation from Odia. A competent Odia reader must compare the source and proposed English meaning before this claim is approved.
- **operator** — `HOLD`: General Administration and Public Grievance Department, Government of Odisha
  - Reason: The department name and its English rendering rely on Odia source text. Require a competent Odia-language reviewer before approval.
- **jurisdiction** — `HOLD`: Odisha
  - Reason: This field is linked to the Odia department label. A competent Odia reviewer should confirm that the cited label supports the jurisdiction claim; the official-domain signal alone should not replace that check.
- **tasks** — `HOLD`: Grievance status
  - Reason: Exact status target could not be extracted. The menu label is Odia and the English task label is translated; verify both the target and the translation with an Odia reader.
  - Target: https://janasunani.odisha.gov.in/complaint-status (AI check: fetch_error; Web reader did not extract this exact route; this does not prove it is unavailable.)
- **help** — `HOLD`: Frequently asked questions
  - Reason: Exact FAQ target could not be extracted. Confirm the URL in a normal browser and have an Odia reader confirm the translated link label.
  - Target: https://janasunani.odisha.gov.in/faq (AI check: fetch_error; Web reader did not extract this exact route; this does not prove it is unavailable.)
- **help** — `HOLD`: Contact
  - Reason: The target responded to this AI web check, but the source link label is Odia. Have an Odia reader confirm the translation and that the target is genuinely the public contact page.
  - Target: https://janasunani.odisha.gov.in/contact (AI check: loaded; Official Public Grievance Redressal Portal contact URL opened; label translation still needs a competent Odia reviewer.)

### Directorate of Treasuries, Pension & Entitlements, Uttarakhand (`igod-ef54536511a6523b`)
- **AI triage:** `HOLD_SELECTED_CLAIMS_UNTIL_CHECKED`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=5, HOLD=1.
- Existing uncertainties: The FAQ page covers digital signatures and e-Sign.; The site's Grievance Redressal page lists only the e-Procurement complaint mechanism, so it is not offered as a grievance route.
- **purpose** — `APPROVE_CANDIDATE`: Official website of the Directorate of Treasuries, Pension and Entitlements, Government of Uttarakhand.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Directorate of Treasuries, Pension and Entitlements, Government of Uttarakhand (developed and hosted by NIC)
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **jurisdiction** — `APPROVE_CANDIDATE`: Uttarakhand
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **languages** — `APPROVE_CANDIDATE`: English and Hindi (language switch observed)
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.
- **help** — `APPROVE_CANDIDATE`: Frequently asked questions
  - Reason: The uploaded packet records this target as loaded during the dated capture. Re-open it during human review and confirm it still reaches the named public-information page; current web-reader extraction failures alone do not invalidate the captured result.
  - Target: https://ekosh.uk.gov.in/frequently-asked-questionsfaqs/ (AI check: fetch_error; Web reader failed to fetch the exact FAQ target; packet says the source was previously loaded.)
- **help** — `HOLD`: Citizen charter
  - Reason: The Citizen Charter was only seen as a link in the packet and was not independently loaded here. Verify that the exact URL opens the intended charter page.
  - Target: https://ekosh.uk.gov.in/citizen-charter/ (AI check: fetch_error; Web reader failed to fetch the exact Citizen Charter target; still requires normal-browser check.)

### e-MSME Seva, Odisha (`igod-af6bdd4b418c67a8`)
- **AI triage:** `READY_FOR_HUMAN_REVIEW_NOT_APPROVAL`. Current formal review state remains `source_checked`.
- Claim triage counts: APPROVE_CANDIDATE=3.
- **purpose** — `APPROVE_CANDIDATE`: Gateway (e-MSME Seva) to the online application systems of Odisha's MSME Department and its directorates and boards.
  - Reason: Saved packet evidence appears to support this wording. This is only a preliminary AI recommendation; a named human must open/check the official source and record the decision.
- **operator** — `APPROVE_CANDIDATE`: Micro, Small & Medium Enterprises Department, Government of Odisha
  - Reason: The saved source names this organisation/department or describes its role. Confirm owner vs content owner vs developer/host with the official page before approving; do not infer a precise operating department.
- **jurisdiction** — `APPROVE_CANDIDATE`: Odisha
  - Reason: The saved evidence appears consistent with the field, but the workflow requires a person to confirm this potentially consequential scope claim directly against the official page.

## Highest-priority edits before human review

1. **Separate owner/content owner from developer/host** in operator labels for Tamil Nadu e-Sevai, Tamil Nadu State Portal, Himachal Pradesh portal, and Puducherry State Portal. These are role distinctions, not cosmetic edits.
2. **Narrow unsupported scope** for Tamil Nadu e-Sevai language availability, ECI State/UT coverage, the ECI “other Indian languages” phrase, DigiLocker partner-audience claims, the National Scholarship Portal operator label, and Bhavishya audience/employee coverage.
3. **Use explicit unknowns rather than overclaiming** for the Odisha eDistrict department operator, Odisha State Scholarship Portal operating department, Nagaland responsible department, and the National Career Service audience list.
4. **Hold specific link claims for actual target checks** where the original packet recorded link-only evidence or this AI web reader could not extract the target. See the JSON for the exact URL and reason.
5. **Require competent Odia language review** of the Janasunani purpose/operator/jurisdiction wording and the translated status/FAQ/contact labels before those claims are approved.
6. Keep all automated profiles labelled as machine-checked until an actual person records a decision. Do not convert this file into `reviews.json` entries.

## Links that need normal-browser checks

These are **not declared broken**. Automated web fetches can be blocked, timeout, or fail to extract a dynamically rendered page. Open them in a normal browser and confirm the content and destination before approving:

- `https://bhavishya.nic.in/AboutUs.aspx` — **not_exactly_confirmed**: Other pages on this host were seen, but this exact URL was not fully confirmed.
- `https://edistrict.delhigovt.nic.in/` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://edistrict.delhigovt.nic.in/Public/TrackApplication` — **fetch_timeout**: Web reader timed out; not evidence that the target is broken.
- `https://edistrict.delhigovt.nic.in/in/en/Public/AboutUs.html` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://edistrict.delhigovt.nic.in/in/en/Public/ContactDetails.html` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://edistrict.delhigovt.nic.in/in/en/Public/Faq.html` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://edistrict.delhigovt.nic.in/in/en/Public/Help.html` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://edistrict.kerala.gov.in/generalService.htm?event=faq` — **not_exactly_confirmed**: Other pages on this host were seen, but this exact URL was not fully confirmed.
- `https://edistrict.odisha.gov.in/#contact` — **anchor_not_confirmed**: Homepage was loaded but the extracted page did not confirm a Contact section at #contact.
- `https://edistrict.odisha.gov.in/#team` — **anchor_not_confirmed**: Homepage was loaded but the extracted page did not confirm an FAQ section at #team.
- `https://ekosh.uk.gov.in/about-department/` — **not_opened_this_session**: Packet contains a saved source page; exact About page could not be separately extracted.
- `https://ekosh.uk.gov.in/citizen-charter/` — **fetch_error**: Web reader failed to fetch the exact Citizen Charter target; still requires normal-browser check.
- `https://ekosh.uk.gov.in/frequently-asked-questionsfaqs/` — **fetch_error**: Web reader failed to fetch the exact FAQ target; packet says the source was previously loaded.
- `https://electoralsearch.eci.gov.in/` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://himachal.gov.in/` — **not_opened_this_session**: Packet has a saved official portal snapshot and About text; homepage was not separately fetched successfully in this review.
- `https://himachal.gov.in/Content/HtmlPage?qs=LdEF%2BAuYkB4QOC0eN%2F%2B6qQ%3D%3D` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://himachal.gov.in/Content/HtmlPage?qs=LdEF%2BAuYkB6vDHvsMAvpTw%3D%3D` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://himachal.gov.in/Content/HtmlPage?qs=LdEF%2BAuYkB7rMRolWuVbNg%3D%3D` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://janasunani.odisha.gov.in/` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://janasunani.odisha.gov.in/complaint-status` — **fetch_error**: Web reader did not extract this exact route; this does not prove it is unavailable.
- `https://janasunani.odisha.gov.in/faq` — **fetch_error**: Web reader did not extract this exact route; this does not prove it is unavailable.
- `https://ncs.gov.in/` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://ncs.gov.in/faq` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://new.broadcastseva.gov.in/digigov-portal-web-app/` — **not_exactly_confirmed**: Other pages on this host were seen, but this exact URL was not fully confirmed.
- `https://odishamsme.nic.in/` — **not_exactly_confirmed**: Other pages on this host were seen, but this exact URL was not fully confirmed.
- `https://pgportal.gov.in/Home/ContactUs` — **not_exactly_confirmed**: Other pages on this host were seen, but this exact URL was not fully confirmed.
- `https://pgportal.gov.in/Home/Faq` — **not_exactly_confirmed**: Other pages on this host were seen, but this exact URL was not fully confirmed.
- `https://revenue.py.gov.in/` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://revenue.py.gov.in/citizen_charter.html` — **not_opened_this_session**: Exact target needs human check.
- `https://revenue.py.gov.in/contact_us.html` — **fetch_error**: Web reader did not extract target; normal-browser check required.
- `https://scholarship.nagaland.gov.in/#contact` — **anchor_not_confirmed**: Homepage was loaded but extracted content did not show a Contact Us target for this fragment.
- `https://scholarship.odisha.gov.in/` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://scholarship.odisha.gov.in/content/about-us` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://scholarship.odisha.gov.in/content/contact-us` — **empty_extraction**: URL returned a State Scholarship Portal title but no extractable body; needs human visual confirmation.
- `https://scholarship.odisha.gov.in/faqs` — **fetch_error**: Web reader did not extract page during this review; packet says earlier capture loaded it.
- `https://scholarship.odisha.gov.in/know-your-status` — **fetch_error**: Web reader did not extract exact status target; this does not prove it is broken.
- `https://scholarships.gov.in/` — **not_exactly_confirmed**: Other pages on this host were seen, but this exact URL was not fully confirmed.
- `https://scholarships.gov.in/pfmshelpdesk` — **fetch_timeout**: Web reader timed out; not evidence that the target is broken.
- `https://scholarshipsdhte.jharkhand.gov.in/` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://scholarshipsdhte.jharkhand.gov.in/contact` — **fetch_error**: Web reader did not extract target; packet includes saved evidence for the page.
- `https://scholarshipsdhte.jharkhand.gov.in/faq` — **fetch_error**: Web reader did not extract target; packet includes saved evidence for the page.
- `https://scholarshipsdhte.jharkhand.gov.in/grievances` — **fetch_error**: Exact route not extracted; no claim about availability is made.
- `https://services.hp.gov.in/` — **fetch_error**: Web reader did not extract the cross-host service portal; this is inconclusive and must be checked in a normal browser.
- `https://www.digilocker.gov.in/web/about/about-digilocker` — **not_opened_this_session**: Packet contains saved source evidence; this precise About URL was not separately opened during this review.
- `https://www.digilocker.gov.in/web/about/contact-us` — **empty_extraction**: URL returned a DigiLocker page title but no extractable body text; does not prove failure; human visual check needed.
- `https://www.digilocker.gov.in/web/about/faq` — **not_opened_this_session**: Packet reports a loaded saved page; not separately opened during this review.
- `https://www.incometax.gov.in/iec/foportal/` — **not_exactly_confirmed**: Other pages on this host were seen, but this exact URL was not fully confirmed.
- `https://www.py.gov.in/about-puducherry` — **not_exactly_confirmed**: Other pages on this host were seen, but this exact URL was not fully confirmed.
- `https://www.tn.gov.in/contact_directory.php` — **fetch_timeout**: Web reader timed out; this is not evidence that the URL is broken.
- `https://www.tn.gov.in/faq.php` — **not_exactly_confirmed**: Other pages on this host were seen, but this exact URL was not fully confirmed.
- `https://www.tnesevai.tn.gov.in/` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://www.tnesevai.tn.gov.in/Pages/AboutUs.aspx` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://www.tnesevai.tn.gov.in/Pages/ContactUs.aspx` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://www.tnesevai.tn.gov.in/Pages/FAQ.aspx` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.
- `https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx` — **not_exactly_confirmed**: The current web reader did not fully confirm this exact URL; this is not a finding that the site is broken. Normal-browser review required.

## Instructions for the coding agent

- Treat the JSON file as an advisory review backlog, not an importable decisions file.
- Apply only the explicit **suggested wording** as a staged revision proposal. For every wording change, follow the repository rule: add a new evidence ID if new evidence is used, preserve old evidence and history, and add the required `revisionHistory` entry. Do not edit or repurpose existing evidence quotes.
- Do not set `human_reviewed`, do not fabricate reviewer names/dates/attestations, do not append to `reviews.json`, and do not run the importer on this recommendations file.
- Put claims marked `HOLD` into the manual-review packet as unresolved. Where the issue is a language translation, assign a fluent reviewer. Where the issue is a link target, require the exact destination to be opened and labelled safely.
- Regenerate both dated review packets after any staged content changes; profile digests will change. The human reviewer must use the fresh packet and produce a fresh decision file.
- Run `python tools/website_enrichment.py validate`, `python tools/editorial_review.py check`, the QA gate and protected-profile hash checks after the revisions. A passing build does not substitute for human review.
- Keep review packets, quotes and page snapshots internal until source-text reuse permission is resolved.

## Web observations that support important edits

- The Kerala eDistrict official home page states that the project provides government services to citizens through Common Service Centres and says it is implemented/managed by Kerala State IT Mission. <https://edistrict.kerala.gov.in/>
- Odisha eDistrict says the Government of Odisha implemented NIC’s ServicePlus platform to provide services to citizens, but the extracted page does not name a single responsible department. <https://edistrict.odisha.gov.in/>
- CPGRAMS About Us identifies DARPG as content owner, and its language selector lists many Indian languages. <https://pgportal.gov.in/Home/AboutUs>
- RTI Online says its portal is for Central Government public authorities and warns applications for State Government authorities, including Delhi, would be returned. <https://rtionline.gov.in/>
- Tamil Nadu State Portal footer separates content ownership/maintenance by Government of Tamil Nadu from design/development by NIC. <https://www.tn.gov.in/services.php>
- Puducherry’s official State Portal FAQ says its content updates are managed by officials in the Directorate of Information Technology and names the Puducherry e-Governance Society as nodal agency. <https://www.py.gov.in/frequently-asked-questions>
- SevanaPension Direct To Home describes itself as software used by co-operative banks to disburse social security pensions in Kerala, not a public pensioner portal. <https://dbt.lsgkerala.gov.in/>
- Bhavishya’s official page identifies the system as a pension-sanction and payment-tracking initiative of the Department of Pension & Pensioners’ Welfare. <https://bhavishya.nic.in/>
- Broadcast Seva states it provides a single-point facility for broadcast-related permissions, registrations and licences and identifies the Ministry of Information and Broadcasting as owner. <https://new.broadcastseva.gov.in/>
- Nagaland’s portal states it is a single-window platform for discovering, applying for and tracking government scholarship schemes. <https://scholarship.nagaland.gov.in/>

**Final state:** preliminary recommendations are prepared for all 157 claims, but **formal human review remains 0** until a named reviewer personally checks the claims and imports decisions through the project’s existing workflow.
