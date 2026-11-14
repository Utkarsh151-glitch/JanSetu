"""Phase 17 (owner-authorised): build the evidence-backed revision of the RTI request service record.

Usage (from the project root): python tools/phase17_rti_candidate.py
Writes data/services/phase17-rti-candidate.json for the official workflow:
  python tools/service_catalog.py import data/services/phase17-rti-candidate.json --revision
  python tools/service_catalog.py publish data/services/phase17-rti-approvals.json
Every fact cites the official captures in data/services/research/ made on 2026-10-18.
"""
import copy
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CHECKED = "2026-10-18"
GUIDELINES = "https://rtionline.gov.in/guidelines.php?request"
RULES_PDF = "https://cic.gov.in/sites/default/files/RTI/RTIRules2012.pdf"


def capture(name: str) -> dict:
    data = json.loads((ROOT / "data/services/research" / name).read_text(encoding="utf-8"))
    assert data["success"] and data["tlsVerified"] and data["httpStatus"] == 200, name
    return data


# Official wording each published statement rests on (whitespace-normalised).
GUIDELINE_PHRASES = (
    "Do not upload Aadhar Card or PAN Card or any other personal Identification (Except BPL Card)",
    "(a) Internet banking", "Master/Visa", "Using UPI", "Using RuPay Card",
    "wait for the 24-48 working hours", "Please do not make additional attempt to make payment again",
    "helprtionline-dopt[at]nic[dot]in", "No RTI fee is required to be paid by any citizen who is below poverty line",
    "must attach a copy of the certificate issued by the appropriate government",
    "As per RTI Act, no fee has to be paid for first appeal")
RULE_PHRASES = ("accompanied by a fee of rupees ten",
                "No fee under rule 3 and rule 4 shall be charged from any person who, is below poverty line")


def verified_sources() -> tuple[dict, dict]:
    """Returns the two captures after checking that every cited phrase is in the official text."""
    guidelines = capture("rti-2026-10-18.json")
    rules = capture("rti-rules-2012-pdf-2026-10-18.json")
    assert guidelines["finalUrl"] == GUIDELINES and rules["finalUrl"] == RULES_PDF
    squash = lambda value: " ".join(value.split())
    text, rules_text = squash(guidelines["text"]), squash(rules["text"])
    for phrase in GUIDELINE_PHRASES:
        assert phrase in text, phrase
    for phrase in RULE_PHRASES:
        assert phrase in rules_text, phrase
    return guidelines, rules


def main() -> None:
    guidelines, rules = verified_sources()
    catalog = json.loads((ROOT / "data/services/catalog.json").read_text(encoding="utf-8"))
    row = copy.deepcopy(next(s for s in catalog["services"] if s["id"] == "rti-central-request"))
    row["verifiedOn"] = CHECKED
    row["evidence"] = [
        {"id": "rti", "url": GUIDELINES, "publisher": "Department of Personnel and Training", "checkedOn": CHECKED,
         "summary": "RTI Online guidelines for requests to central public authorities: attachments, payment modes, payment reconciliation, the BPL fee exemption and the first appeal.",
         "locator": "Guidelines page: \"Do not upload Aadhar Card or PAN Card…\"; payment modes (a)–(d); \"After making payment… 24-48 working hours\"; \"No RTI fee… below poverty line\"; \"Submit First Appeal\"",
         "contentSha256": guidelines["sha256"]},
        {"id": "rti-rules-2012", "url": RULES_PDF,
         "publisher": "Central Information Commission (published copy of the Right to Information Rules, 2012, G.S.R. 603(E))",
         "checkedOn": CHECKED,
         "summary": "Rule 3: a request is accompanied by a fee of rupees ten. Rule 5: no fee under rules 3 and 4 for a person below the poverty line who submits a copy of the appropriate Government's certificate with the application.",
         "locator": "Rules 3 and 5", "contentSha256": rules["sha256"]},
    ]
    row["conditionalDocuments"] = [
        {"text": "If you are below the poverty line: a copy of the BPL certificate issued by the appropriate government, attached with the application, to be exempt from the fee.",
         "evidenceIds": ["rti", "rti-rules-2012"]}]
    row["procedure"] = [
        {"text": "Do not upload an Aadhaar card, PAN card or any other personal identification. The only exception is a BPL card when you claim the fee exemption.",
         "evidenceIds": ["rti"]},
        {"text": "If you have paid but no registration number appears, wait 24–48 working hours while the payment is reconciled, and do not pay again. If it has still not appeared, email the RTI Online helpdesk (helprtionline-dopt[at]nic[dot]in) with the transaction details.",
         "evidenceIds": ["rti"]}]
    row["fees"] = {
        "text": "₹10 per request (Right to Information Rules, 2012, rule 3). RTI Online accepts internet banking, Mastercard or Visa credit and debit cards, UPI and RuPay cards. Applicants below the poverty line pay no fee if they attach a copy of their BPL certificate (rule 5).",
        "evidenceIds": ["rti", "rti-rules-2012"]}
    row["reviewNotes"] = ("Official guidance re-checked on 2026-10-18 by Claude Code (automated agent) under the project owner's Phase 17 "
        "authorisation, against the RTI Online guidelines and the Right to Information Rules, 2012 (earlier check by Codex on 2026-10-16). "
        "Not a human editorial review and not a completed transaction. Applies to requests to central public authorities through RTI Online; "
        "state public authorities follow their own RTI rules. The linked organization identifies the existing website operator; the responsible "
        "department is listed separately. Unknown fields stay null.")
    out = ROOT / "data/services/phase17-rti-candidate.json"
    out.write_text(json.dumps({"services": [row]}, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Candidate written: {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
