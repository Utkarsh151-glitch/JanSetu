# Internal review material: not for release

Everything in this folder is internal working material for checking website profiles:

- saved page snapshots and their quotations (`research/`)
- the evidence index
- the AI pre-review and its handoff
- the staging file and the review queue
- reviewer packets and human-review decisions

Keep it out of release artifacts, packaged app data and public exports until the reuse permissions are documented: NIC permission for IGOD-derived data, and a decision on reusing official-page text.

The app receives only what `tools/package_catalog.py` emits: an allowlist of profile fields, with evidence reduced to ID, URL, date and claim, and no quotations. `tools/test_review_reconciliation.py` checks the built APKs and the extension folder for leaks.

AI-assisted review is not human approval. The `ai-prereview-*`, `staging-*` and `review-queue.json` files are proposals. The importer refuses them. Only a decisions file saved from a review packet by a real, named reviewer can be imported into `reviews.json`, using `python tools/editorial_review.py import`.
