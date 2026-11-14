"""Resumable, cached official directory collection; no automatic publication.

python tools/discover_government_sites.py --source igod --dry-run
python tools/discover_government_sites.py --source igod
python tools/discover_government_sites.py --source igod --retry-failed
"""

import argparse
import hashlib
import json
import re
import os
from pathlib import Path
from urllib.parse import urlsplit

from government_sources import ADAPTERS, CachedClient, check_robots, normalize_url, now, save_json

ROOT = Path(__file__).resolve().parents[1]
COLLECTION = ROOT / "data/collection"
VERSION = 1


def collect(args):
    adapter = ADAPTERS[args.source]()
    directory = COLLECTION / args.batch
    path = directory / "manifest.json"
    manifest = json.loads(path.read_text(encoding="utf-8")) if path.exists() else {
        "version": VERSION, "source": args.source, "batch": args.batch, "startedAt": now(),
        "pages": {}, "stateBranches": {}, "policy": "Local development only; NIC reproduction permission unresolved"}
    scope = {"state": args.state, "category": args.category}
    if path.exists() and manifest.get("scope", {"state": None, "category": None}) != scope:
        raise ValueError("This batch has a different scope; use a separate --batch for a state/category run")
    manifest["scope"] = scope
    roots = [args.category] if args.category else adapter.roots
    if args.state:
        # Discover the actual state links first, rather than construct guessed routes.
        roots = ["https://igod.gov.in/sg/states", "https://igod.gov.in/sg/district/states",
                 "https://igod.gov.in/leg/L007/states", "https://igod.gov.in/leg/L008/states", "https://igod.gov.in/apx/states"]
    for url in roots:
        if not adapter.allowed_directory(url):
            raise ValueError("Category must be a public IGOD navigation/listing URL")
        manifest["pages"].setdefault(url, {"url": url, "name": urlsplit(url).path, "status": "PENDING"})
    if args.dry_run:
        print(json.dumps({"mode": "dry_run_no_requests_no_writes", "roots": roots,
                          "existingPages": len(manifest["pages"]), "batch": args.batch}, indent=2))
        return
    manifest.setdefault("invocations", []).append({"startedAt": now(), "roots": roots,
        "delaySeconds": args.delay, "timeoutSeconds": args.timeout,
        "collectorCodeSha256": hashlib.sha256(Path(__file__).read_bytes()).hexdigest(),
        "adapterCodeSha256": hashlib.sha256((Path(__file__).parent / "government_sources.py").read_bytes()).hexdigest()})
    client = CachedClient(directory / "snapshots", args.delay, args.timeout)
    robots_meta, robots = check_robots(client, "https://igod.gov.in")
    manifest["robots"] = robots_meta
    if robots is None:
        manifest["blockedReason"] = "Robots could not be established: " + robots_meta["status"]
        save_json(path, manifest)
        print(manifest["blockedReason"])
        return
    client.delay = max(args.delay, robots_meta.get("crawlDelay") or 0)
    if args.retry_failed:
        for task in manifest["pages"].values():
            if task["status"] in {"FAILED", "PARSE_ERROR"}:
                task["status"] = "PENDING"
    if args.reparse:
        for task in manifest["pages"].values():
            if task["status"] in {"PROCESSED", "PARSE_ERROR"}:
                task["status"] = "PENDING"
    processed = 0
    while True:
        pending_tasks = [p for p in manifest["pages"].values() if p["status"] == "PENDING"]
        # Enumerate navigation and category first pages before continuation
        # fragments so every state branch and source total is known early.
        task = min(pending_tasks, key=lambda p: "listingPage" in p) if pending_tasks else None
        if not task or (args.max_pages and processed >= args.max_pages):
            break
        url = task["url"]
        if not robots.can_fetch("JanSetuLocalInventory", url):
            task.update(status="ROBOTS_DISALLOWED", error="robots.txt disallows this path")
            save_json(path, manifest)
            continue
        meta, html = client.fetch(url, retry_failed=args.retry_failed)
        task["retrieval"] = meta
        processed += 1
        if meta["status"] != "OK" or not 200 <= meta["httpStatus"] < 300:
            task.update(status="FAILED", error=meta["status"] + ": " + meta.get("error", ""))
        else:
            try:
                parsed = adapter.parse(html, url, task)
                task.update(parsed, status="PROCESSED")
                if task.get("expectedRows") is not None and len(task["entries"]) != task["expectedRows"]:
                    task.update(status="PARSE_ERROR", error="Pagination returned unexpected row count")
                for child in parsed["children"] + parsed["pagination"]:
                    link = child["url"]
                    state_match = re.search(r"/(?:sg|leg|apx)/([A-Z]{2})/", link)
                    if args.state and state_match and state_match[1] != args.state.upper():
                        continue
                    # Scoped runs do not drift back into the unscoped category tree.
                    if args.state and not state_match:
                        continue
                    manifest["pages"].setdefault(link, {**child, "status": "PENDING"})
                    if re.fullmatch(r"https://igod.gov.in/sg/[A-Z]{2}/categories", link):
                        code = link.split("/")[4]
                        manifest["stateBranches"][code] = {"name": child["name"], "url": link}
            except (ValueError, KeyError) as error:
                task.update(status="PARSE_ERROR", error=str(error))
        manifest["updatedAt"] = now()
        save_json(path, manifest)
        if processed % 10 == 0 or task["status"] != "PROCESSED":
            count = sum(len(t.get("entries", [])) for t in manifest["pages"].values())
            pending = sum(t["status"] == "PENDING" for t in manifest["pages"].values())
            print(f"{processed} fetched this invocation; {len(manifest['pages'])} discovered pages; {count} raw rows; {pending} pending", flush=True)
    manifest["invocations"][-1].update(finishedAt=now(), fetchedPages=processed)
    manifest["updatedAt"] = now()
    save_json(path, manifest)
    entries = [dict(e, checkedAt=p["retrieval"]["checkedAt"], snapshot=p["retrieval"].get("snapshot"))
               for p in manifest["pages"].values() for e in p.get("entries", []) if p["status"] == "PROCESSED"]
    save_json(directory / "raw_entries.json", entries)
    print(json.dumps({"manifest": str(path), "rawEntries": len(entries), "pages": len(manifest["pages"]),
                      "processed": sum(p["status"] == "PROCESSED" for p in manifest["pages"].values()),
                      "pending": sum(p["status"] == "PENDING" for p in manifest["pages"].values()),
                      "failed": sum(p["status"] in {"FAILED", "PARSE_ERROR"} for p in manifest["pages"].values())}), flush=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source", choices=ADAPTERS, default="igod")
    parser.add_argument("--batch", default="igod-2026-10-14")
    parser.add_argument("--category", help="Exact source-linked category URL")
    parser.add_argument("--state", help="IGOD state/UT code; uses source-linked branches")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--retry-failed", action="store_true")
    parser.add_argument("--reparse", action="store_true", help="Re-extract cached snapshots after adapter changes")
    parser.add_argument("--max-pages", type=int, default=0, help="Optional bounded batch; zero processes all pending pages")
    parser.add_argument("--delay", type=float, default=2.0)
    parser.add_argument("--timeout", type=int, default=20)
    args = parser.parse_args()
    if args.state:
        args.state = args.state.upper()
    if args.batch == "igod-2026-10-14":
        if args.state:
            args.batch += "-" + args.state.lower()
        elif args.category:
            args.batch += "-" + hashlib.sha256(args.category.encode()).hexdigest()[:8]
    if args.delay < 1 or not re.fullmatch(r"[\w-]+", args.batch):
        parser.error("Use a delay of at least one second and a simple batch name")
    collect(args)


if __name__ == "__main__":
    main()
