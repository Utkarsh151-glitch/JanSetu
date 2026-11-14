"""Reconcile AI-assisted review recommendations with the current claims (Phase 6D).

Usage (from the project root):
  python tools/review_reconciliation.py            write the staging file and the human-review queue
  python tools/review_reconciliation.py --check    validate only; exit non-zero on malformed, duplicate,
                                                   unknown, unexplained-stale or unpinned entries

Inputs   data/website_enrichment/review/ai-prereview-*.json (advisory), claims.json, evidence_index.json,
         reviews.json and the saved snapshots.
Outputs  data/website_enrichment/review/staging-<date>.json   proposed actions (never decisions)
         data/website_enrichment/review/review-queue.json      prioritised work for real reviewers

Nothing here edits claims.json, reviews.json or evidence. A recommendation is never a human decision:
staged actions carry no reviewer, no attestation and no decision, and the importer refuses them.
"""
from __future__ import annotations

import hashlib
import json
import re
import sys
from collections import Counter
from datetime import date
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "tools"))

from editorial_review import (INDEX, PACKET_DIR, claim_flags, claim_items, effective, load_reviews,  # noqa: E402
                              profile_digest, snapshot_files)
from website_enrichment import CLAIMS, normalise, read_json  # noqa: E402

AI_ACTIONS = {"APPROVE_CANDIDATE", "REVISE", "HOLD"}
STAGED_ACTIONS = AI_ACTIONS | {"NEEDS_MORE_EVIDENCE"}
QUEUE = PACKET_DIR / "review-queue.json"
# Risk order from the Phase 6D brief: what could misdirect a citizen comes first.
RISK = {"operator": (1, "Department or operator"), "jurisdiction": (2, "Jurisdiction or audience"), "audience": (2, "Jurisdiction or audience"),
        "purpose": (3, "Purpose of the portal"), "tasks": (4, "Task link"), "languages": (5, "Language or translation"),
        "help": (6, "Help or contact link")}
ABSOLUTES = {"all", "every", "only", "any", "always", "complete", "entire", "full", "sole", "never", "guaranteed"}
STOP = {"the", "a", "an", "and", "or", "of", "for", "to", "in", "on", "its", "it", "is", "are", "was", "by", "with", "this", "that",
        "each", "as", "be", "not", "no", "from", "has", "have", "their", "these", "those", "at", "may", "should", "checked", "page",
        "pages", "site", "portal", "official", "named", "names", "stated", "confirmed", "observed", "home", "about", "header",
        "content", "owner", "faq", "english", "tamil", "hindi", "odia", "malayalam"}


class ReconciliationError(ValueError):
    def __init__(self, errors: list[str]):
        super().__init__(f"{len(errors)} reconciliation error(s):\n- " + "\n- ".join(errors))
        self.errors = errors


def words(text: str) -> set[str]:
    return {w.lower() for w in re.findall(r"[\w&'-]+", normalise(text)) if len(w) > 1}


def stem(word: str) -> str:
    word = word.lower().removesuffix("'s")
    for suffix in ("ing", "ed", "es", "s"):
        if len(word) > 4 and word.endswith(suffix):
            return word[: -len(suffix)]
    return word


def wording_check(text: str, support: list[str]) -> dict:
    """Compare a proposed wording with its sources.

    unsupportedTerms  names and numbers (capitalised words, acronyms, digits) that no cited source or the earlier claim
                      contains: new factual content a reviewer must find in a source before approving
    broadeningTerms   absolutes (all, every, only…) the sources do not use
    newWords          other words absent from the sources, for information (often narrowing hedges such as 'not named')"""
    supported = {stem(w) for s in support for w in words(s)}
    # Whole tokens (so "e-Governance" stays one word) that carry a capital letter or a digit.
    names = {t for t in re.findall(r"[\w&'-]+", normalise(text)) if re.search(r"[A-Z0-9]", t)}
    unsupported = sorted(n for n in names if stem(n) not in supported and n.lower() not in STOP)
    absolutes = sorted(w for w in words(text) & ABSOLUTES if stem(w) not in supported)
    new = sorted(w for w in words(text) - STOP if stem(w) not in supported and not any(stem(w) == stem(n) for n in names))
    return {"unsupportedTerms": unsupported, "broadeningTerms": absolutes, "newWords": new}


def load_advice_files() -> list[tuple[Path, dict]]:
    return [(path, read_json(path)) for path in sorted(PACKET_DIR.glob("ai-prereview-*.json"))]


def validate_advice(advice: dict, claims: dict, index: dict) -> dict:
    """Structural and referential checks. Returns counts and a list of problems (never raises)."""
    profiles = {p["websiteId"]: p for p in claims["websites"]}
    problems, seen, counts = [], set(), Counter()
    for entry in advice.get("profiles", []):
        wid = entry.get("websiteId")
        if not isinstance(entry.get("claims"), list) or not wid or not re.fullmatch(r"[0-9a-f]{64}", entry.get("sourceProfileDigest", "")):
            problems.append({"type": "malformed_profile", "websiteId": wid})
            continue
        profile = profiles.get(wid)
        if profile is None:
            problems.append({"type": "unknown_website", "websiteId": wid})
            continue
        current = profile_digest(profile)
        explained = any(r.get("previousDigest") == entry["sourceProfileDigest"] for r in profile.get("revisionHistory", []))
        if entry["sourceProfileDigest"] != current and not explained:
            problems.append({"type": "stale_unexplained", "websiteId": wid})
        keys = {i["key"] for i in claim_items(profile)}
        removed_by_revision = " ".join(r.get("change", "") for r in profile.get("revisionHistory", []))
        evidence = {e["id"] for e in profile["evidence"]}
        for claim in entry["claims"]:
            counts["recommendations"] += 1
            key = claim.get("key")
            if not key or claim.get("aiRecommendation") not in AI_ACTIONS or not normalise(claim.get("reason", "")) \
                    or not isinstance(claim.get("evidenceIds"), list):
                problems.append({"type": "malformed_claim", "websiteId": wid, "key": key})
                continue
            if claim.get("formalDecision") or claim.get("humanSourceOpenedAttestation"):
                problems.append({"type": "claims_human_decision", "websiteId": wid, "key": key})
            if (wid, key) in seen:
                problems.append({"type": "duplicate", "websiteId": wid, "key": key})
            seen.add((wid, key))
            field = key.split(":", 1)[0]
            if key not in keys:
                target = key.split(":", 1)[1] if ":" in key else field
                if target not in removed_by_revision and field not in removed_by_revision:
                    problems.append({"type": "unknown_claim_key", "websiteId": wid, "key": key})
            missing = sorted(set(claim["evidenceIds"]) - evidence)
            if missing:
                problems.append({"type": "unknown_evidence", "websiteId": wid, "key": key, "evidenceIds": missing})
            unpinned = sorted(e for e in claim["evidenceIds"] if f"{wid}/{e}" not in index["pins"])
            if unpinned:
                problems.append({"type": "unpinned_evidence", "websiteId": wid, "key": key, "evidenceIds": unpinned})
    return {"recommendations": counts["recommendations"], "profiles": len(advice.get("profiles", [])), "problems": problems}


def risk_of(field: str, flags: list[str]) -> tuple[int, str]:
    rank, label = RISK.get(field, (7, "Minor wording"))
    if any("translation" in f.lower() for f in flags) and rank > 3:
        rank, label = 5, "Language or translation"
    return rank, label


def stage(advice: dict, claims: dict, index: dict, snapshots: dict, reviews: dict) -> list[dict]:
    profiles = {p["websiteId"]: p for p in claims["websites"]}
    out = []
    for entry in advice["profiles"]:
        profile = profiles[entry["websiteId"]]
        items = {i["key"]: i for i in claim_items(profile)}
        quotes = {e["id"]: e["quote"] for e in profile["evidence"]}
        state = effective(profile, reviews)
        for claim in entry["claims"]:
            key, ai = claim["key"], claim["aiRecommendation"]
            current = items.get(key)
            field = key.split(":", 1)[0]
            flags = claim_flags(profile, current, snapshots) if current else claim.get("packetFlags", [])
            cited = current["evidenceIds"] if current else claim["evidenceIds"]
            languages = {e["id"]: e.get("quoteLanguage") for e in profile["evidence"]}
            # Translation-dependent: flagged as a translation, or every cited quote is non-English.
            translated = any("translation" in f.lower() for f in flags) or (bool(cited) and all(languages.get(e) for e in cited))
            rank, label = risk_of(field, flags + (["translation"] if translated else []))
            support = [quotes[e] for e in cited if e in quotes] + [claim.get("claimText", "")]
            record = {
                "id": "stg-" + hashlib.sha256(f"{entry['websiteId']}|{key}|{entry['sourceProfileDigest']}".encode()).hexdigest()[:12],
                "websiteId": entry["websiteId"], "title": entry.get("title"), "batchId": entry.get("batchId"), "claimKey": key, "field": field,
                "riskRank": rank, "riskCategory": label, "aiRecommendation": ai, "aiReason": claim["reason"],
                "existingClaimAtAdvice": claim.get("claimText"), "currentClaim": current["text"] if current else None,
                "proposedWording": claim.get("suggestedText"), "evidenceIds": cited,
                "publicationState": {"profileStatus": profile["status"], "reviewStatus": state["reviewStatus"],
                                     "claimInApp": bool(current) and profile["status"] == "published" and key not in state["withheld"]},
                "adviceDigest": entry["sourceProfileDigest"], "currentDigest": profile_digest(profile),
                "unresolvedConcerns": [], "followUp": [],
            }
            if claim.get("suggestedText"):
                record["wordingCheck"] = {"proposed": wording_check(claim["suggestedText"], support)}
            if current and current["text"] != claim.get("claimText"):
                record["wordingCheck"] = record.get("wordingCheck", {}) | {"applied": wording_check(current["text"], [quotes[e] for e in cited if e in quotes] + [claim.get("claimText", "")])}
            # Outcome and staged action, keeping the AI recommendation in the record.
            if current is None:
                record["outcome"] = "withheld_claim"
                record["stagedAction"] = "NEEDS_MORE_EVIDENCE" if field in {"jurisdiction", "audience", "operator", "languages"} else "HOLD"
                record["safetyAction"] = "withhold_claim"
                record["requiredHumanAction"] = "Confirm the claim should stay withheld; restore it only with new evidence that states it."
            elif current["text"] != claim.get("claimText"):
                record["outcome"] = "applied_revision"
                record["stagedAction"] = "REVISE"
                record["safetyAction"] = "none"
                record["requiredHumanAction"] = "Compare the revised wording with every cited source; approve, revise or hold."
            elif sorted(current["evidenceIds"]) != sorted(claim["evidenceIds"]) and not translated:
                record["outcome"] = "evidence_added"
                record["stagedAction"] = "APPROVE_CANDIDATE"
                record["safetyAction"] = "none"
                record["requiredHumanAction"] = "Check the newly added evidence supports the unchanged wording; approve only if it does."
                record["followUp"].append(f"The AI recommended {ai}; new evidence was added on 18 October: "
                                          + ", ".join(sorted(set(current["evidenceIds"]) - set(claim["evidenceIds"]))))
            elif ai == "HOLD" and translated:
                record["outcome"] = "staged_language_review"
                record["stagedAction"] = "HOLD"
                record["safetyAction"] = "withhold_claim_if_translation_not_confirmed"
                record["requiredHumanAction"] = "A fluent reader confirms the translation against the source; hold the claim if it cannot be confirmed."
            elif ai == "HOLD" and "url" in current and snapshots.get(current["url"]):
                record["outcome"] = "resolved_by_link_check"
                record["stagedAction"] = "APPROVE_CANDIDATE"
                record["safetyAction"] = "none"
                record["requiredHumanAction"] = "Open the link in a normal browser and confirm it reaches the labelled public page."
                record["followUp"].append("The AI held this link; the 18 October read-only link check loaded the target and its content matched the label.")
            elif ai == "HOLD":
                record["outcome"] = "staged_for_review"
                record["stagedAction"] = "HOLD"
                record["safetyAction"] = "obtain_more_evidence"
                record["requiredHumanAction"] = "Open the source in a normal browser; hold the claim if it cannot be confirmed."
            else:
                record["outcome"] = "staged_for_review"
                record["stagedAction"] = "APPROVE_CANDIDATE"
                record["safetyAction"] = "none"
                record["requiredHumanAction"] = ("Open the source and the link destination; approve only if they say this." if "url" in (current or {})
                                                 else "Compare the claim with the cited sources; approve only if they say this.")
            if translated:
                record["unresolvedConcerns"].append("Depends on a translation by an automated agent; needs a fluent reader.")
                if record["outcome"] != "staged_language_review":
                    record["followUp"].append("Language review: confirm the translation before approving.")
            elif any(languages.get(e) for e in cited):
                record["unresolvedConcerns"].append("Some cited evidence is not in English (" + ", ".join(sorted({languages[e] for e in cited if languages.get(e)}))
                                                    + "); English evidence also supports the claim.")
            for which, check in (record.get("wordingCheck") or {}).items():
                label = "AI-proposed" if which == "proposed" else "Current"
                if check["unsupportedTerms"]:
                    record["unresolvedConcerns"].append(f"{label} wording has names or numbers not in the cited sources: " + ", ".join(check["unsupportedTerms"][:12]))
                if check["broadeningTerms"]:
                    record["unresolvedConcerns"].append(f"{label} wording uses broadening words not in the sources: " + ", ".join(check["broadeningTerms"]))
            if any("not stated" in normalise(x).lower() for x in [current["text"] if current else ""]):
                record["followUp"].append("More evidence could replace the 'not stated' wording (fresh source visit).")
            record["freshSourceVisitNeeded"] = record["stagedAction"] in {"HOLD", "NEEDS_MORE_EVIDENCE"} or bool(record["followUp"] and "fresh source" in " ".join(record["followUp"]))
            record["confidence"] = ("low" if record["stagedAction"] in {"HOLD", "NEEDS_MORE_EVIDENCE"} or translated
                                    else "medium" if record["unresolvedConcerns"] or record["outcome"] == "applied_revision" or rank <= 2
                                    else "high")
            out.append(record)
    return out


def draft_and_held_tasks(claims: dict) -> list[dict]:
    tasks = {
        "igod-0eda9ab58f816d6f": "Find out how pensioners raise grievances in Odisha (this site offers only staff sign-in), then publish, rewrite or hold.",
        "igod-6e1f53ab402e424c": "A Malayalam reader confirms the purpose and menu translations; open the pension search link (observed only).",
        "igod-18649bde195e5a01": "Confirm CPWD Sewa's scope and find a working contact page; open the complaint-status link (observed only).",
        "igod-45553a3bd372fff9": "Find an About page or statement of purpose for DoPPFI; capture it read-only before publishing.",
        "igod-c33cb8549415ad34": "A Hindi reader confirms the purpose; open the FAQ link (observed only).",
        "igod-b3a364f734caa1f1": "Check whether HP ePASS is still in use (latest news 2019) before publishing anything.",
        "igod-2bdd905020581df7": "Open the About, Contact and FAQ pages in a normal browser (they failed with HTTP errors); name the department.",
        "igod-421dca3648f50c0b": "Capture the CPAO Profile page; confirm whether 'central government pensioners' is stated.",
        "uidai": "Open https://uidai.gov.in/en/my-aadhaar in a normal browser (HTTP 500 twice); record what loads. The earlier reviewed record still stands.",
        "india-portal": "Open https://www.india.gov.in/ in a normal browser (HTTP 403 to headless Chrome); never work around access controls.",
        "igod-1d591544b1c2e25d": "Open https://msme.gov.in/ in a normal browser (HTTP 403). The PMEGP service links here; record what loads.",
        "igod-9783d3afd73ca927": "Open https://www.doppw.gov.in/ in a normal browser (HTTP 403); never work around access controls.",
        "igod-44b4a596b1818cab": "Open https://dppf.gujarat.gov.in/ in a normal browser (captcha page); JanSetu never solves or bypasses captchas.",
    }
    out = []
    for p in claims["websites"]:
        if p["status"] == "published":
            continue
        out.append({"websiteId": p["websiteId"], "batchId": p.get("batchId"), "status": p["status"],
                    "reason": p.get("heldReason") or (p.get("publicationDecision") or {}).get("basis"),
                    "task": tasks.get(p["websiteId"], "Decide publish, revise or hold with a named reviewer."),
                    "rule": ("A held site stays out of the app until a person records what it shows in a normal browser; no access control is worked around."
                             if p["status"] == "held" else
                             "A draft is never published because its title looks plausible; it needs a statement of purpose from the site and a named reviewer.")})
    return out


def language_tasks(claims: dict) -> list[dict]:
    out = []
    for p in claims["websites"]:
        langs = sorted({e.get("quoteLanguage") for e in p["evidence"] if e.get("quoteLanguage")})
        if langs:
            out.append({"websiteId": p["websiteId"], "status": p["status"], "languages": langs,
                        "evidenceIds": [e["id"] for e in p["evidence"] if e.get("quoteLanguage")],
                        "task": "A fluent reader confirms the quoted text and the English wording that depends on it."})
    return out


def build(today: str | None = None) -> dict:
    today = today or date.today().isoformat()
    claims, index, reviews, snapshots = read_json(CLAIMS), read_json(INDEX), load_reviews(), snapshot_files()
    files = load_advice_files()
    validations, staged = [], []
    for path, advice in files:
        report = validate_advice(advice, claims, index)
        validations.append({"file": path.name, "sha256": hashlib.sha256(path.read_bytes()).hexdigest()} | report)
        staged += stage(advice, claims, index, snapshots, reviews)
    staged.sort(key=lambda r: (r["riskRank"], {"HOLD": 0, "NEEDS_MORE_EVIDENCE": 0, "REVISE": 1, "APPROVE_CANDIDATE": 2}[r["stagedAction"]],
                               r["websiteId"], r["claimKey"]))
    for n, record in enumerate(staged, 1):
        record["reviewOrder"] = n
    published = [p for p in claims["websites"] if p["status"] == "published"]
    return {
        "generatedOn": today,
        "notice": "Preliminary AI-assisted findings staged for human review. Not decisions: no reviewer, no attestation, not importable.",
        "humanDecisions": len(reviews["decisions"]),
        "validation": validations,
        "summary": {"recommendations": len(staged), "byAiRecommendation": dict(Counter(r["aiRecommendation"] for r in staged)),
                    "byStagedAction": dict(Counter(r["stagedAction"] for r in staged)), "byOutcome": dict(Counter(r["outcome"] for r in staged)),
                    "byRisk": dict(Counter(r["riskCategory"] for r in staged)),
                    "publishedProfilesAwaitingAPerson": sum(1 for p in published if effective(p, reviews)["reviewStatus"] != "human_reviewed")},
        "staged": staged,
        "draftsAndHeld": draft_and_held_tasks(claims),
        "languageReview": language_tasks(claims),
    }


def main() -> None:
    result = build()
    problems = [p for v in result["validation"] for p in v["problems"]]
    if "--check" in sys.argv:
        print(json.dumps({"recommendations": result["summary"]["recommendations"], "problems": problems}, ensure_ascii=False))
        if problems:
            raise ReconciliationError([json.dumps(p) for p in problems])
        return
    staging = PACKET_DIR / f"staging-{result['generatedOn']}.json"
    staging.write_text(json.dumps({k: v for k, v in result.items() if k not in {"draftsAndHeld", "languageReview"}}, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    queue = {"generatedOn": result["generatedOn"], "notice": "Work for real reviewers, highest risk first. Nothing here is a decision.",
             "claims": [{k: r[k] for k in ("reviewOrder", "websiteId", "title", "claimKey", "riskCategory", "stagedAction", "outcome",
                                            "requiredHumanAction", "confidence")} for r in result["staged"]],
             "draftsAndHeld": result["draftsAndHeld"], "languageReview": result["languageReview"]}
    QUEUE.write_text(json.dumps(queue, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    print(json.dumps(result["summary"], ensure_ascii=False, indent=1))
    print("problems:", len(problems))


if __name__ == "__main__":
    main()
