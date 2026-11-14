"""Human editorial review of website profiles (Phase 6C).

Usage (from the project root):
  python tools/editorial_review.py index            pin each evidence quote to its saved snapshot (hash + location)
  python tools/editorial_review.py check            evidence consistency: pinned snapshots unchanged and present
  python tools/editorial_review.py packet [--batch ID | --all]   write the offline reviewer packet (HTML) for published profiles;
                                                                 --all writes one versioned packet for every published profile,
                                                                 ordered by risk, and refuses stale staging
  python tools/editorial_review.py import FILE      validate a decisions file exported from the packet and append it
  python tools/editorial_review.py status           show the effective review state of every profile

How decisions work
  claims.json   automated drafts and their evidence. A review never edits it.
  reviews.json  append-only human decisions. Each names a real reviewer, a date, a decision for
                every claim, and the digest of the exact profile content that was reviewed.
  runtime       derived from both: "approve" makes a profile human_reviewed only while its content
                still matches the reviewed digest; claims a person marked "revise" or "hold" are
                withheld; a profile-level "hold" removes the profile. Automated checks never count
                as human review.
A profile edited after a decision must carry a revisionHistory entry naming the reviewed digest,
so corrections are staged and auditable rather than silent.
"""
from __future__ import annotations

import hashlib
import html
import json
import re
import sys
from datetime import date
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "tools"))

from website_enrichment import (CLAIMS, FACT_FIELDS, LINK_FIELDS, RESEARCH, normalise,  # noqa: E402
                                official, read_json)

REVIEWS = ROOT / "data/website_enrichment/reviews.json"
INDEX = ROOT / "data/website_enrichment/evidence_index.json"
PACKET_DIR = ROOT / "data/website_enrichment/review"
DECISIONS = {"approve", "revise", "hold"}
FACT_ORDER = ["purpose", "operator", "audience", "jurisdiction", "languages"]
# A reviewer must be a named person; automated agents can never record a human review.
AUTOMATED = re.compile(r"\b(claude|codex|gpt|chatgpt|gemini|copilot|llm|automated|automation|agent|bot|script|ai)\b", re.I)
# Test or placeholder identities never create production decisions.
PLACEHOLDER = re.compile(r"\b(test|tester|smoke|dummy|example|sample|placeholder|fake|demo|anonymous|unknown)\b", re.I)


class ReviewError(ValueError):
    def __init__(self, errors: list[str]):
        super().__init__(f"{len(errors)} review error(s):\n- " + "\n- ".join(errors))
        self.errors = errors


# ---------------------------------------------------------------- profile content

def profile_digest(profile: dict) -> str:
    """Digest of what a reviewer judges: the fields and the evidence (not status or review metadata)."""
    content = {"websiteId": profile["websiteId"], "fields": profile.get("fields", {}),
               "evidence": [{k: e.get(k) for k in ("id", "url", "checkedOn", "claim", "quote")} for e in profile.get("evidence", [])]}
    return hashlib.sha256(json.dumps(content, sort_keys=True, ensure_ascii=False).encode("utf-8")).hexdigest()


def claim_items(profile: dict) -> list[dict]:
    """Every claim JanSetu would display, with a stable key the reviewer decides on."""
    fields, items = profile.get("fields", {}), []
    for name in FACT_ORDER:
        if name in fields:
            items.append({"key": name, "field": name, "text": fields[name]["value"], "evidenceIds": fields[name]["evidenceIds"]})
    for name in ("tasks", "help"):
        for link in fields.get(name, []):
            items.append({"key": f"{name}:{link['url']}", "field": name, "text": link["label"], "url": link["url"],
                          "kind": link["kind"], "evidenceIds": link["evidenceIds"]})
    return items


def claim_flags(profile: dict, item: dict, snapshots: dict) -> list[str]:
    """Reasons a claim must be confirmed against its source rather than approved in bulk."""
    flags = list(profile.get("reviewFlags", {}).get(item["key"], [])) + list(profile.get("reviewFlags", {}).get(item["field"], []))
    if "url" in item:
        host = item["url"].split("/")[2]
        if not snapshots.get(item["url"]):
            flags.append("Link target was not loaded; it was only seen as a link on a saved page.")
        if "#" in item["url"]:
            flags.append("Link points to a section of a page, not a separate page.")
        site_hosts = {e["url"].split("/")[2] for e in profile.get("evidence", [])}
        if host not in site_hosts:
            flags.append(f"Link goes to a different official host ({host}).")
    if item["field"] in {"operator", "audience", "languages", "jurisdiction"}:
        flags.append(f"{item['field'].capitalize()} claims need explicit confirmation (ownership, audience, language and jurisdiction are never auto-approved).")
    return list(dict.fromkeys(flags))


# ---------------------------------------------------------------- evidence index

def snapshot_files() -> dict[str, list[tuple[Path, dict]]]:
    by_url: dict[str, list[tuple[Path, dict]]] = {}
    for path in sorted(RESEARCH.glob("*/*/*.json")):
        record = json.loads(path.read_text(encoding="utf-8"))
        if record.get("text"):
            for key in {record["url"], record.get("finalUrl") or record["url"]}:
                by_url.setdefault(key, []).append((path, record))
    return by_url


def locate(quote: str, files: list[tuple[Path, dict]], root: Path = ROOT) -> dict | None:
    """Where the quote is in a saved snapshot: page text (with offset and context), title, meta or a link label."""
    q = normalise(quote)
    for path, record in files:
        text = normalise(record.get("text", ""))
        where = text.find(q)
        base = {"snapshot": path.relative_to(root).as_posix(), "textSha256": record.get("textSha256"),
                "capturedAt": record.get("checkedAt")}
        if where >= 0:
            return base | {"match": "page text", "offset": where,
                           "context": text[max(0, where - 140):where] + "⟦" + text[where:where + len(q)] + "⟧" + text[where + len(q):where + len(q) + 140]}
        for name in ("title", "metaDescription"):
            if q and q in normalise(record.get(name) or ""):
                return base | {"match": "page title" if name == "title" else "meta description", "offset": None, "context": normalise(record[name])}
        for link in record.get("links", []):
            if q == normalise(link["text"]):
                return base | {"match": "link label", "offset": None, "context": f"Link text “{link['text']}” → {link['href']}"}
    return None


def quote_sha(quote: str) -> str:
    return hashlib.sha256(normalise(quote).encode("utf-8")).hexdigest()


def build_index(claims: dict, files_by_url: dict, existing: dict | None = None) -> dict:
    """Adds pins for new evidence. Existing pins are never rewritten: a changed quote or snapshot is an error."""
    existing = existing or {"pins": {}}
    pins, errors = dict(existing["pins"]), []
    for profile in claims["websites"]:
        for item in profile.get("evidence", []):
            key = f"{profile['websiteId']}/{item['id']}"
            found = locate(item["quote"], files_by_url.get(item["url"], []))
            if key in pins:
                if pins[key]["quoteSha256"] != quote_sha(item["quote"]) or pins[key]["url"] != item["url"]:
                    errors.append(f"Evidence {key} changed after it was pinned; add new evidence with a new id and a revisionHistory entry.")
                continue
            if found is None:
                errors.append(f"Evidence {key}: quote not found in any saved snapshot of {item['url']}")
                continue
            pins[key] = {"url": item["url"], "quoteSha256": quote_sha(item["quote"])} | found
    if errors:
        raise ReviewError(errors)
    return {"schemaVersion": 1, "note": "Pins each evidence quote to the saved snapshot that contains it. Internal review data: contains page text excerpts.",
            "pins": dict(sorted(pins.items()))}


def check_index(claims: dict, index: dict, root: Path = ROOT) -> dict:
    """Every evidence item is pinned, its snapshot file still exists with the same text hash, and the quote is still there."""
    errors, live = [], set()
    for profile in claims["websites"]:
        for item in profile.get("evidence", []):
            key = f"{profile['websiteId']}/{item['id']}"
            live.add(key)
            pin = index["pins"].get(key)
            if pin is None:
                errors.append(f"Evidence {key} is not pinned; run `python tools/editorial_review.py index`.")
                continue
            if pin["quoteSha256"] != quote_sha(item["quote"]) or pin["url"] != item["url"]:
                errors.append(f"Evidence {key} no longer matches its pin (quote or URL changed).")
            path = root / pin["snapshot"]
            if not path.exists():
                errors.append(f"Evidence {key}: pinned snapshot is missing ({pin['snapshot']}).")
                continue
            record = json.loads(path.read_text(encoding="utf-8"))
            if record.get("textSha256") != pin["textSha256"] or hashlib.sha256(record.get("text", "").encode("utf-8")).hexdigest() != pin["textSha256"]:
                errors.append(f"Evidence {key}: snapshot {pin['snapshot']} changed after it was pinned.")
            elif locate(item["quote"], [(path, record)], root) is None:
                errors.append(f"Evidence {key}: quote no longer found in {pin['snapshot']}.")
    retired = {f"{p['websiteId']}/{e}" for p in claims["websites"] for r in p.get("revisionHistory", []) for e in r.get("retiredEvidence", [])}
    for key in set(index["pins"]) - live - retired:
        errors.append(f"Pinned evidence {key} was removed without a revisionHistory entry retiring it.")
    if errors:
        raise ReviewError(errors)
    return {"pinned": len(live), "snapshots": len({index['pins'][k]['snapshot'] for k in live})}


# ---------------------------------------------------------------- decisions

def empty_reviews() -> dict:
    return {"schemaVersion": 1, "note": "Append-only human review decisions. Never edit by hand; use the review packet and `editorial_review.py import`.",
            "decisions": []}


def validate_decision(decision: dict, claims: dict, today: date | None = None) -> list[str]:
    today = today or date.today()
    profiles = {p["websiteId"]: p for p in claims["websites"]}
    wid, errors = decision.get("websiteId"), []
    profile = profiles.get(wid)
    if profile is None:
        return [f"Unknown website in decision: {wid}"]
    label = f"{wid}"
    reviewer = decision.get("reviewer") or {}
    name = normalise(reviewer.get("name", ""))
    if len(name) < 3:
        errors.append(f"{label}: reviewer name is required.")
    elif AUTOMATED.search(name):
        errors.append(f"{label}: reviewer must be a named person; automated agents cannot record human review.")
    elif PLACEHOLDER.search(name):
        errors.append(f"{label}: test or placeholder identities cannot record production decisions.")
    if decision.get("attestation") is not True:
        errors.append(f"{label}: the reviewer must confirm they opened the official sources and compared each claim.")
    try:
        reviewed = date.fromisoformat(decision.get("reviewedOn", ""))
        if reviewed > today:
            errors.append(f"{label}: review date is in the future.")
        if reviewed < date.fromisoformat(profile["checkedOn"]):
            errors.append(f"{label}: review date is before the evidence was captured.")
    except ValueError:
        errors.append(f"{label}: reviewedOn must be YYYY-MM-DD.")
    if decision.get("profileDigest") != profile_digest(profile):
        errors.append(f"{label}: decision was made on different profile content (stale packet); regenerate the packet and review again.")
    if profile["status"] != "published":
        errors.append(f"{label}: only published profiles are reviewed in this workflow (status is {profile['status']}).")
    verdict = decision.get("decision")
    if verdict not in DECISIONS:
        errors.append(f"{label}: decision must be approve, revise or hold.")
    items = {item["key"]: item for item in claim_items(profile)}
    decided = decision.get("claims", {})
    missing, unknown = set(items) - set(decided), set(decided) - set(items)
    if missing:
        errors.append(f"{label}: every claim needs a decision; missing {sorted(missing)}.")
    if unknown:
        errors.append(f"{label}: decisions for claims that do not exist: {sorted(unknown)}.")
    flagged = set(decision.get("flaggedClaims", []))
    for key, entry in decided.items():
        if entry.get("decision") not in DECISIONS:
            errors.append(f"{label}/{key}: claim decision must be approve, revise or hold.")
        elif entry["decision"] != "approve" and not normalise(entry.get("note", "")):
            errors.append(f"{label}/{key}: a revise or hold decision needs a note saying what is wrong.")
        elif entry["decision"] == "approve" and key in flagged and entry.get("sourceOpened") is not True:
            errors.append(f"{label}/{key}: flagged claim approved without confirming the source was opened.")
    if verdict == "approve" and any(entry.get("decision") != "approve" for entry in decided.values()):
        errors.append(f"{label}: a profile can only be approved when every claim is approved.")
    if verdict in {"revise", "hold"} and not normalise(decision.get("notes", "")):
        errors.append(f"{label}: a revise or hold decision needs a profile-level note.")
    return errors


def decision_id(decision: dict) -> str:
    core = {k: decision.get(k) for k in ("websiteId", "profileDigest", "reviewer", "reviewedOn", "decision", "claims", "notes")}
    return "rev-" + hashlib.sha256(json.dumps(core, sort_keys=True, ensure_ascii=False).encode("utf-8")).hexdigest()[:16]


def import_decisions(exported: dict, claims: dict, reviews: dict, source: str, today: date | None = None) -> dict:
    """Validates every decision in an exported file; appends all of them or none."""
    errors, additions = [], []
    text = json.dumps(exported, ensure_ascii=False)
    if exported.get("artifactType") or "aiRecommendation" in text or "stagedAction" in text:
        raise ReviewError(["This is an AI recommendation or staging file, not human decisions; it can never be imported."])
    known = {d["reviewId"] for d in reviews["decisions"]}
    snapshots = snapshot_files()
    profiles = {p["websiteId"]: p for p in claims["websites"]}
    for decision in exported.get("decisions", []):
        decision = dict(decision)
        # The flagged list is recomputed here; a file cannot skip confirmation by omitting it.
        profile = profiles.get(decision.get("websiteId"))
        if profile:
            decision["flaggedClaims"] = [i["key"] for i in claim_items(profile) if claim_flags(profile, i, snapshots)]
        decision["reviewer"] = exported.get("reviewer", decision.get("reviewer"))
        decision["attestation"] = exported.get("attestation", decision.get("attestation"))
        decision.setdefault("reviewedOn", exported.get("reviewedOn"))
        errors += validate_decision(decision, claims, today)
        decision["reviewId"] = decision_id(decision)
        if decision["reviewId"] in known:
            errors.append(f"{decision.get('websiteId')}: this decision was already imported ({decision['reviewId']}).")
        decision["importedFrom"] = source
        additions.append(decision)
    if not additions:
        errors.append("The file contains no decisions.")
    if errors:
        raise ReviewError(errors)
    return {**reviews, "decisions": reviews["decisions"] + additions}


def check_revisions(claims: dict, reviews: dict) -> None:
    """A profile changed after a human decision must say so: no silent overwrites of reviewed content."""
    errors = []
    profiles = {p["websiteId"]: p for p in claims["websites"]}
    for decision in reviews["decisions"]:
        profile = profiles.get(decision["websiteId"])
        if profile is None:
            errors.append(f"Reviewed profile {decision['websiteId']} was removed from claims.json.")
        elif decision["profileDigest"] != profile_digest(profile) and not any(
                r.get("previousDigest") == decision["profileDigest"] for r in profile.get("revisionHistory", [])):
            errors.append(f"{decision['websiteId']}: content changed after review {decision['reviewId']} without a revisionHistory entry.")
    if errors:
        raise ReviewError(errors)


def effective(profile: dict, reviews: dict) -> dict:
    """The review state that applies to the current content of one profile."""
    digest = profile_digest(profile)
    history = [d for d in reviews["decisions"] if d["websiteId"] == profile["websiteId"]]
    current = [d for d in history if d["profileDigest"] == digest]
    latest = max(current, key=lambda d: (d["reviewedOn"], reviews["decisions"].index(d)), default=None)
    state = {"digest": digest, "decisions": len(history), "staleDecisions": len(history) - len(current), "latest": latest,
             "reviewStatus": profile["reviewStatus"], "withheld": [], "published": profile["status"] == "published"}
    if latest:
        if latest["decision"] == "approve":
            state["reviewStatus"] = "human_reviewed"
            state["reviewedOn"] = latest["reviewedOn"]
        elif latest["decision"] == "hold":
            state["published"] = False
        state["withheld"] = sorted(k for k, v in latest["claims"].items() if v["decision"] != "approve")
    return state


def load_reviews() -> dict:
    return read_json(REVIEWS) if REVIEWS.exists() else empty_reviews()


# ---------------------------------------------------------------- reviewer packet

def load_advice() -> dict[tuple[str, str], dict]:
    """Advisory AI pre-review recommendations, keyed by (websiteId, claim key). Never a decision."""
    advice = {}
    for path in sorted(PACKET_DIR.glob("ai-prereview-*.json")):
        data = read_json(path)
        for profile in data.get("profiles", []):
            for claim in profile.get("claims", []):
                advice[(profile["websiteId"], claim["key"])] = {
                    "source": path.name, "recommendation": claim.get("aiRecommendation"), "reason": claim.get("reason"),
                    "suggestedText": claim.get("suggestedText"), "observation": (claim.get("aiWebObservation") or {}).get("note"),
                    "adviceDigest": profile.get("sourceProfileDigest"), "adviceClaimText": claim.get("claimText")}
    return advice


def load_staging() -> dict[tuple[str, str], dict]:
    """Latest Phase 6D staging (preliminary AI findings with a review order). Never a decision."""
    files = sorted(PACKET_DIR.glob("staging-*.json"))
    if not files:
        return {}
    data = read_json(files[-1])
    keep = ("reviewOrder", "riskCategory", "stagedAction", "outcome", "confidence", "requiredHumanAction", "unresolvedConcerns",
            "followUp", "safetyAction", "currentDigest")
    return {(r["websiteId"], r["claimKey"]): {k: r.get(k) for k in keep} | {"source": files[-1].name} for r in data["staged"]}


def packet_data(claims: dict, index: dict, snapshots: dict, reviews: dict) -> list[dict]:
    rows, advice, staging = [], load_advice(), load_staging()
    for profile in claims["websites"]:
        if profile["status"] != "published":
            continue
        evidence = {e["id"]: e for e in profile["evidence"]}
        state = effective(profile, reviews)
        claims_out = []
        for item in claim_items(profile):
            sources = []
            for eid in item["evidenceIds"]:
                e, pin = evidence[eid], index["pins"].get(f"{profile['websiteId']}/{eid}", {})
                sources.append({"id": eid, "url": e["url"], "claim": e["claim"], "quote": e["quote"], "checkedOn": e["checkedOn"],
                                "location": {k: pin.get(k) for k in ("snapshot", "match", "offset", "capturedAt")},
                                "context": pin.get("context", "")})
            tip = advice.get((profile["websiteId"], item["key"]))
            if tip:
                tip = tip | {"wordingChangedSince": tip["adviceClaimText"] != item["text"]}
            staged = staging.get((profile["websiteId"], item["key"]))
            if staged:
                staged = staged | {"stale": staged["currentDigest"] != state["digest"]}
            claims_out.append(item | {"sources": sources, "flags": claim_flags(profile, item, snapshots),
                                      "targetLoaded": bool(snapshots.get(item["url"])) if "url" in item else None,
                                      "advisory": tip, "staging": staged,
                                      "publication": {"inApp": item["key"] not in state["withheld"] and state["published"],
                                                      "reviewStatus": state["reviewStatus"]}})
        claims_out.sort(key=lambda c: (c["staging"] or {}).get("reviewOrder") or 10**6)
        rows.append({"websiteId": profile["websiteId"], "title": profile.get("title", profile["websiteId"]),
                     "revisions": [{k: r.get(k) for k in ("date", "by", "change")} for r in profile.get("revisionHistory", [])],
                     "officialUrl": profile.get("officialUrl"), "batchId": profile.get("batchId"),
                     "status": profile["status"], "automatedReview": profile["reviewStatus"], "effectiveReview": state["reviewStatus"],
                     "decisionsSoFar": state["decisions"], "digest": state["digest"], "checkedOn": profile["checkedOn"],
                     "heldFields": profile.get("heldFields", []), "uncertainty": profile.get("uncertainty", []), "claims": claims_out})
    return rows


PACKET_TEMPLATE = """<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>JanSetu Review Packet</title>
<style>
:root{--bg:#f7f7f4;--card:#fff;--ink:#1d2321;--muted:#5a6461;--line:#d8dcda;--accent:#0d5f66;--warn:#fff4d6;--warnline:#e2b648;--ok:#e6f4ea}
@media (prefers-color-scheme:dark){:root:not([data-theme="light"]){--bg:#141817;--card:#1d2321;--ink:#e8ecea;--muted:#a7b1ad;--line:#36403d;--accent:#7fd0d6;--warn:#3a3218;--warnline:#9b7d2a;--ok:#1f3326}}
body{margin:0;background:var(--bg);color:var(--ink);font:16px/1.5 system-ui,sans-serif}
main{max-width:980px;margin:0 auto;padding:16px}
h1{font-size:1.6rem;margin:.2em 0}h2{font-size:1.25rem;margin:.2em 0}h3{font-size:1rem;margin:.6em 0 .2em}
.card{background:var(--card);border:1px solid var(--line);border-radius:12px;padding:16px;margin:14px 0}
.muted{color:var(--muted)}.flag{background:var(--warn);border-left:4px solid var(--warnline);padding:6px 10px;margin:6px 0;border-radius:6px}
.claim{border-top:1px solid var(--line);padding:12px 0}.claim:first-of-type{border-top:0}
.quote{font-family:ui-monospace,monospace;font-size:.9rem;white-space:pre-wrap;word-break:break-word;background:var(--bg);padding:8px;border-radius:6px}
.ctx mark{background:var(--ok);color:inherit}
.advice{border:1px dashed var(--line);border-radius:6px;padding:6px 10px;margin:6px 0;color:var(--muted)}
.stage{border-left:4px solid var(--accent);padding:4px 10px;margin:6px 0}
label{margin-right:14px;white-space:nowrap}textarea,input[type=text],input[type=date]{width:100%;box-sizing:border-box;font:inherit;padding:6px;border:1px solid var(--line);border-radius:6px;background:var(--card);color:var(--ink)}
button{font:inherit;padding:10px 16px;border-radius:8px;border:0;background:var(--accent);color:#fff;cursor:pointer}
a{color:var(--accent)}code{word-break:break-all}.pill{display:inline-block;padding:1px 8px;border-radius:99px;border:1px solid var(--line);font-size:.85rem}
#status{margin-top:8px}
</style></head><body><main>
<h1>JanSetu website profile review</h1>
<p class="muted">Internal review packet generated __GENERATED__ · packet version <code>__VERSION__</code>. It contains short quotations and excerpts from official pages, kept only as review evidence: do not publish it. Decisions are saved to a file you send back; nothing is uploaded. Staged actions and AI advice are preliminary findings, not decisions.</p>
<div class="card" id="start"><h2>Start here: highest-risk claims</h2><p class="muted">Operator and department first, then jurisdiction and audience, purpose, task links, language, and help links.</p><ol id="startlist"></ol></div>
<div class="card">
<h2>Reviewer</h2>
<p><label for="rname">Your full name</label><input id="rname" type="text" autocomplete="name"></p>
<p><label for="rrole">Role (optional)</label><input id="rrole" type="text"></p>
<p><label for="rdate">Review date</label><input id="rdate" type="date"></p>
<p><input id="attest" type="checkbox"> <label for="attest">For every profile I decide below, I opened the official pages and compared each claim with its source myself.</label></p>
<p class="muted">How to review: for each claim open the official page, find the quoted text, and choose Approve, Revise or Hold. Revise and Hold need a note. Flagged claims also need the "I opened the source" box before they can be approved. A profile can be approved only when all its claims are approved. Leave a profile undecided to skip it.</p>
</div>
<div id="profiles"></div>
<div class="card"><button id="export" type="button">Save decisions file</button><div id="status" role="status" aria-live="polite"></div>
<p class="muted">Then run <code>python tools/editorial_review.py import &lt;file&gt;</code>. Import checks every rule again and rejects the whole file if anything is missing.</p></div>
</main>
<script id="data" type="application/json">__DATA__</script>
<script>
const data = JSON.parse(document.getElementById('data').textContent);
const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const safeHref = u => /^https:\\/\\/[^\\s"<>]+$/.test(u) ? u : '#';
document.getElementById('rdate').value = new Date().toISOString().slice(0,10);
const root = document.getElementById('profiles');
const ranked = data.flatMap(p => p.claims.filter(c => c.staging).map(c => ({p, c}))).sort((x, y) => x.c.staging.reviewOrder - y.c.staging.reviewOrder);
document.getElementById('startlist').innerHTML = ranked.slice(0, 30).map(({p, c}) =>
  `<li><a href="#claim-${esc(c.staging.reviewOrder)}">${esc(p.title)}: ${esc(c.field)}</a> · ${esc(c.staging.riskCategory)} · staged ${esc(c.staging.stagedAction)}</li>`).join('')
  || '<li>No staging file: run <code>python tools/review_reconciliation.py</code>.</li>';
data.forEach((p, pi) => {
  const claims = p.claims.map((c, ci) => {
    const id = `p${pi}c${ci}`;
    const sources = c.sources.map(s => `<div><h3>Evidence ${esc(s.id)} <span class="pill">checked ${esc(s.checkedOn)}</span></h3>
      <p>Claim supported: ${esc(s.claim)}</p>
      <p>Source: <a href="${safeHref(s.url)}" target="_blank" rel="noopener noreferrer">${esc(s.url)}</a></p>
      <p class="muted">Saved copy: <code>${esc(s.location.snapshot)}</code> · found in ${esc(s.location.match)}${s.location.offset != null ? ' at character ' + esc(s.location.offset) : ''} · captured ${esc(s.location.capturedAt)}</p>
      <div class="quote">Quote: ${esc(s.quote)}</div>
      <div class="quote ctx">Context: ${esc(s.context).replace('⟦','<mark>').replace('⟧','</mark>')}</div></div>`).join('');
    const link = c.url ? `<p>Link: <a href="${safeHref(c.url)}" target="_blank" rel="noopener noreferrer">${esc(c.url)}</a> · ${c.targetLoaded ? 'target page was loaded' : '<strong>target page was not loaded (link seen only)</strong>'}</p>` : '';
    const flags = c.flags.map(f => `<div class="flag">${esc(f)}</div>`).join('');
    const a = c.advisory;
    const advisory = a ? `<div class="advice"><strong>AI pre-review (advisory only, not a decision):</strong> ${esc(a.recommendation)}. ${esc(a.reason)}
      ${a.suggestedText ? `<br>Suggested wording: “${esc(a.suggestedText)}”` : ''}
      ${a.observation ? `<br>AI web check: ${esc(a.observation)}` : ''}
      ${a.wordingChangedSince ? `<br><em>This advice was given on earlier wording (“${esc(a.adviceClaimText)}”); the claim above has since been revised. Judge the current wording.</em>` : ''}</div>` : '';
    const st = c.staging;
    const pub = c.publication || {};
    const staged = st ? `<div class="stage"><span class="pill">#${esc(st.reviewOrder)}</span> <span class="pill">${esc(st.riskCategory)}</span>
      <span class="pill">staged: ${esc(st.stagedAction)}</span> <span class="pill">${esc(st.outcome)}</span> <span class="pill">confidence ${esc(st.confidence)}</span>
      ${st.stale ? '<div class="flag"><strong>Staging is stale for this profile: re-run reconciliation.</strong></div>' : ''}
      <p><strong>What you must check:</strong> ${esc(st.requiredHumanAction)}</p>
      ${(st.unresolvedConcerns || []).length ? `<ul>${st.unresolvedConcerns.map(u => `<li>${esc(u)}</li>`).join('')}</ul>` : ''}
      ${(st.followUp || []).length ? `<p class="muted">${st.followUp.map(esc).join(' ')}</p>` : ''}</div>` : '';
    const original = a && a.wordingChangedSince ? `<p class="muted">Original automated claim: “${esc(a.adviceClaimText)}”</p>` : '';
    return `<div class="claim" id="claim-${st ? esc(st.reviewOrder) : 'x' + pi + '-' + ci}" data-key="${esc(c.key)}" data-flagged="${c.flags.length ? 1 : 0}">
      <h3>${esc(c.field)}: “${esc(c.text)}”</h3>
      <p class="muted">Current publication state: ${pub.inApp ? 'shown in the app' : 'not shown in the app'} · ${esc(pub.reviewStatus)} (automated check unless it says human_reviewed)</p>
      ${original}${staged}${link}${flags}${advisory}${sources}
      <fieldset><legend>Decision for this claim</legend>
      ${['approve','revise','hold'].map(v => `<label><input type="radio" name="${id}" value="${v}"> ${v[0].toUpperCase()+v.slice(1)}</label>`).join('')}
      ${c.flags.length ? `<p><input type="checkbox" id="${id}o"> <label for="${id}o">I opened the source and the link destination for this flagged claim.</label></p>` : ''}
      <label for="${id}n">Note (required for Revise or Hold)</label><textarea id="${id}n" rows="2"></textarea></fieldset></div>`;
  }).join('');
  const held = p.heldFields.map(h => `<li>${esc(h.field)}: ${esc(h.reason)}</li>`).join('') || '<li>None</li>';
  const revs = (p.revisions || []).map(r => `<li>${esc(r.date)} · ${esc(r.by)}: ${esc(r.change)}</li>`).join('') || '<li>None</li>';
  const unc = p.uncertainty.map(u => `<li>${esc(u)}</li>`).join('') || '<li>None</li>';
  root.insertAdjacentHTML('beforeend', `<section class="card" data-profile="${pi}">
    <h2>${esc(p.title)}</h2>
    <p>Official website: <a href="${safeHref(p.officialUrl)}" target="_blank" rel="noopener noreferrer">${esc(p.officialUrl)}</a></p>
    <p class="muted">ID <code>${esc(p.websiteId)}</code> · batch ${esc(p.batchId)} · automated status <strong>${esc(p.automatedReview)}</strong> (machine check, not a human review) · current effective status ${esc(p.effectiveReview)} · ${p.decisionsSoFar} earlier decision(s) · content digest <code>${esc(p.digest.slice(0,16))}</code></p>
    <h3>Held fields</h3><ul>${held}</ul><h3>Notes from the automated check</h3><ul>${unc}</ul><h3>Revisions (automated, recorded)</h3><ul>${revs}</ul>
    ${claims}
    <fieldset><legend>Decision for the whole profile</legend>
    ${['approve','revise','hold'].map(v => `<label><input type="radio" name="p${pi}" value="${v}"> ${v[0].toUpperCase()+v.slice(1)}</label>`).join('')}
    <label for="p${pi}n">Profile note (required for Revise or Hold)</label><textarea id="p${pi}n" rows="2"></textarea></fieldset></section>`);
});
document.getElementById('export').addEventListener('click', () => {
  const problems = [], decisions = [];
  const name = document.getElementById('rname').value.trim();
  if (name.length < 3) problems.push('Enter your full name.');
  if (!document.getElementById('attest').checked) problems.push('Confirm that you compared the claims with the official pages.');
  data.forEach((p, pi) => {
    const choice = document.querySelector(`input[name="p${pi}"]:checked`);
    if (!choice) return;
    const claims = {};
    p.claims.forEach((c, ci) => {
      const id = `p${pi}c${ci}`, pick = document.querySelector(`input[name="${id}"]:checked`);
      const note = document.getElementById(id + 'n').value.trim();
      if (!pick) { problems.push(`${p.title}: decide “${c.text}”.`); return; }
      if (pick.value !== 'approve' && !note) problems.push(`${p.title}: add a note for “${c.text}”.`);
      const opened = c.flags.length ? document.getElementById(id + 'o').checked : null;
      if (pick.value === 'approve' && c.flags.length && !opened) problems.push(`${p.title}: confirm you opened the source for “${c.text}”.`);
      claims[c.key] = {decision: pick.value, note, sourceOpened: opened};
    });
    const pnote = document.getElementById(`p${pi}n`).value.trim();
    if (choice.value === 'approve' && Object.values(claims).some(c => c.decision !== 'approve')) problems.push(`${p.title}: approve the profile only when every claim is approved.`);
    if (choice.value !== 'approve' && !pnote) problems.push(`${p.title}: add a profile note.`);
    decisions.push({websiteId: p.websiteId, profileDigest: p.digest, decision: choice.value, notes: pnote, claims});
  });
  if (!decisions.length) problems.push('No profile has a decision yet.');
  const status = document.getElementById('status');
  if (problems.length) { status.textContent = problems.slice(0, 8).join(' '); return; }
  const out = {schemaVersion: 1, packetVersion: '__VERSION__', reviewer: {name, role: document.getElementById('rrole').value.trim()},
               reviewedOn: document.getElementById('rdate').value, attestation: true, decisions};
  const blob = new Blob([JSON.stringify(out, null, 1)], {type: 'application/json'});
  const a = Object.assign(document.createElement('a'), {href: URL.createObjectURL(blob), download: `jansetu-review-${out.reviewedOn}-${name.replace(/[^A-Za-z0-9]+/g,'-')}.json`});
  document.body.appendChild(a); a.click(); a.remove();
  status.textContent = `Saved ${decisions.length} decision(s). Send the file to the maintainer for import.`;
});
</script></body></html>
"""


def packet_version(rows: list[dict]) -> str:
    """Identifies the exact content reviewed: every profile digest plus the staging file used."""
    staging = sorted(PACKET_DIR.glob("staging-*.json"))
    basis = "|".join(f"{r['websiteId']}:{r['digest']}" for r in sorted(rows, key=lambda r: r["websiteId"]))
    basis += "|" + (hashlib.sha256(staging[-1].read_bytes()).hexdigest() if staging else "no-staging")
    return hashlib.sha256(basis.encode()).hexdigest()[:12]


def write_packet(claims: dict, index: dict, reviews: dict, sites: dict, batch: str | None = None, all_profiles: bool = False) -> Path:
    snapshots = snapshot_files()
    rows = [row for row in packet_data(claims, index, snapshots, reviews) if all_profiles or batch is None or row["batchId"] == batch]
    stale = sorted({r["websiteId"] for r in rows for c in r["claims"] if (c.get("staging") or {}).get("stale")})
    if stale:
        raise ReviewError([f"Staging is stale for {wid}; run `python tools/review_reconciliation.py` first." for wid in stale])
    for row in rows:
        site = sites.get(row["websiteId"], {})
        row["title"], row["officialUrl"] = site.get("title", row["websiteId"]), site.get("url")
        row["firstOrder"] = min(((c.get("staging") or {}).get("reviewOrder") or 10**6) for c in row["claims"]) if row["claims"] else 10**6
    rows.sort(key=lambda r: (r["firstOrder"], r["websiteId"]))
    version = packet_version(rows)
    payload = json.dumps(rows, ensure_ascii=False).replace("</", "<\\/")
    PACKET_DIR.mkdir(parents=True, exist_ok=True)
    name = f"review-packet-all-{date.today().isoformat()}-v{version}" if all_profiles else (f"review-packet-{batch}" if batch else "review-packet")
    path = PACKET_DIR / f"{name}.html"
    path.write_text(PACKET_TEMPLATE.replace("__DATA__", payload).replace("__GENERATED__", html.escape(date.today().isoformat()))
                    .replace("__VERSION__", version), encoding="utf-8")
    (PACKET_DIR / f"{name}.json").write_text(json.dumps({"packetVersion": version, "profiles": rows} if all_profiles else rows,
                                                        ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    return path


# ---------------------------------------------------------------- command line

def write_json(path: Path, value: dict) -> None:
    path.write_text(json.dumps(value, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")


def main() -> None:
    command = sys.argv[1] if len(sys.argv) > 1 else ""
    claims = read_json(CLAIMS)
    if command == "index":
        index = build_index(claims, snapshot_files(), read_json(INDEX) if INDEX.exists() else None)
        write_json(INDEX, index)
        print(json.dumps(check_index(claims, index)))
    elif command == "check":
        reviews = load_reviews()
        check_revisions(claims, reviews)
        print(json.dumps(check_index(claims, read_json(INDEX)) | {"decisions": len(reviews["decisions"])}))
    elif command == "packet":
        from service_catalog import merged_catalog
        sites = {s["id"]: s for s in merged_catalog()["websites"]}
        check_index(claims, read_json(INDEX))
        batch = sys.argv[sys.argv.index("--batch") + 1] if "--batch" in sys.argv else None
        print(write_packet(claims, read_json(INDEX), load_reviews(), sites, batch, all_profiles="--all" in sys.argv).relative_to(ROOT).as_posix())
    elif command == "import":
        source = Path(sys.argv[2])
        before = load_reviews()
        updated = import_decisions(read_json(source), claims, before, source.name)
        write_json(REVIEWS, updated)
        print(f"Imported {len(updated['decisions']) - len(before['decisions'])} decision(s) from {source.name}.")
    elif command == "status":
        reviews = load_reviews()
        for profile in claims["websites"]:
            state = effective(profile, reviews)
            print(f"{profile['websiteId']:<32} {profile['status']:<10} automated={profile['reviewStatus']:<15} effective={state['reviewStatus']:<15} "
                  f"decisions={state['decisions']} stale={state['staleDecisions']} withheld={state['withheld']} digest={state['digest']}")
    else:
        raise SystemExit(__doc__)


if __name__ == "__main__":
    main()
