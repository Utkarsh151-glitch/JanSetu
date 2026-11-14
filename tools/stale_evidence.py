"""List services and website profiles whose source check is older than a freshness window.

Usage (from the project root):
  python tools/stale_evidence.py [--days 90] [--today YYYY-MM-DD]

Prints JSON: everything due for a fresh read-only check, oldest first. It never edits data. Use it before a release
(release blocker RB-11) and on the post-launch schedule in docs/PHASE16_OPERATIONS_RUNBOOK.md.
"""
from __future__ import annotations

import argparse
import json
from datetime import date
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def stale(services: list[dict], profiles: list[dict], today: date, days: int) -> list[dict]:
    due = []
    for s in services:
        if s.get("publicationStatus") == "published":
            age = (today - date.fromisoformat(s["verifiedOn"])).days
            if age > days:
                due.append({"kind": "service", "id": s["id"], "checkedOn": s["verifiedOn"], "ageDays": age, "url": s["informationUrl"]})
    for p in profiles:
        if p.get("status") == "published" and p.get("checkedOn"):
            age = (today - date.fromisoformat(p["checkedOn"])).days
            if age > days:
                due.append({"kind": "websiteProfile", "id": p["websiteId"], "checkedOn": p["checkedOn"], "ageDays": age})
    return sorted(due, key=lambda d: (-d["ageDays"], d["kind"], d["id"]))


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--days", type=int, default=90)
    parser.add_argument("--today", default=date.today().isoformat())
    args = parser.parse_args()
    services = json.loads((ROOT / "data/services/catalog.json").read_text(encoding="utf-8"))["services"]
    profiles = json.loads((ROOT / "data/website_enrichment/claims.json").read_text(encoding="utf-8"))["websites"]
    due = stale(services, profiles, date.fromisoformat(args.today), args.days)
    print(json.dumps({"today": args.today, "windowDays": args.days, "due": len(due), "items": due}, indent=2))


if __name__ == "__main__":
    main()
