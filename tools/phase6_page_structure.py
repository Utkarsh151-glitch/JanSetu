"""Phase 6A: read-only structural inspection of the two selected pages.

Usage: python tools/phase6_page_structure.py

One headless Chrome load per page with mobile viewport handling enabled
(is_mobile=True honours or, when absent, defaults the meta viewport as phones
do). Chrome's own headless user agent is kept. Nothing is clicked or typed.
Output: data/validation_reports/phase6a-page-structure.json
"""
from __future__ import annotations

import json
from datetime import datetime, timezone
from pathlib import Path

from playwright.sync_api import sync_playwright

ROOT = Path(__file__).resolve().parents[1]
CHROME = r"C:\Program Files\Google\Chrome\Application\chrome.exe"
OUT = ROOT / "data/validation_reports/phase6a-page-structure.json"

TN = r"""() => {
  const table = [...document.querySelectorAll('table')].sort((a, b) => b.rows.length - a.rows.length)[0];
  if (!table) return {table: null};
  const headers = [...table.querySelectorAll('tr')][0] ? [...[...table.querySelectorAll('tr')][0].cells].map(c => c.innerText.replace(/\s+/g, ' ').trim()) : [];
  const rows = [...table.rows].slice(1);
  const rects = [...[...table.querySelectorAll('tr')][0].cells].map(c => c.getBoundingClientRect());
  let scroller = null;
  for (let n = table.parentElement; n && n !== document.body; n = n.parentElement) {
    const s = getComputedStyle(n);
    if (['auto', 'scroll', 'hidden', 'clip'].includes(s.overflowX)) { scroller = {tag: n.tagName, className: n.className, overflowX: s.overflowX, clientWidth: n.clientWidth, scrollWidth: n.scrollWidth}; break; }
  }
  const heights = rows.slice(0, 60).map(r => r.getBoundingClientRect().height).sort((a, b) => a - b);
  const colText = headers.map((_, i) => { const lengths = rows.slice(0, 60).map(r => (r.cells[i] ? r.cells[i].innerText.length : 0)); return Math.round(lengths.reduce((a, b) => a + b, 0) / Math.max(1, lengths.length)); });
  const body = getComputedStyle(document.body), html = getComputedStyle(document.documentElement);
  return {
    layoutViewport: document.documentElement.clientWidth, visualScale: window.visualViewport ? visualViewport.scale : null,
    viewportMeta: (document.querySelector('meta[name=viewport]') || {}).content || null,
    headers, columnLeft: rects.map(r => Math.round(r.left)), columnWidth: rects.map(r => Math.round(r.width)),
    columnsStartingBeyondViewport: rects.filter(r => r.left >= innerWidth).length,
    tableWidth: Math.round(table.getBoundingClientRect().width), rows: rows.length,
    medianRowHeight: heights.length ? Math.round(heights[Math.floor(heights.length / 2)]) : null,
    averageCellTextLength: colText, scrollContainer: scroller,
    bodyOverflowX: body.overflowX, htmlOverflowX: html.overflowX,
    documentHeight: document.documentElement.scrollHeight,
    nativeTextControls: [...document.querySelectorAll('a,button')].map(e => e.innerText.trim()).filter(t => /^A[+\-]?$/.test(t))
  };
}"""

RTI = r"""() => {
  const items = [...document.querySelectorAll('li')].filter(li => li.innerText.trim().length > 20);
  const controls = [...document.querySelectorAll('input:not([type=hidden]),button,select,textarea')].map(el => {
    const r = el.getBoundingClientRect();
    return {tag: el.tagName, type: el.type || null, value: el.value || null, label: (el.labels && el.labels[0] ? el.labels[0].innerText : '').trim().slice(0, 60),
            cssWidth: Math.round(r.width), cssHeight: Math.round(r.height)};
  });
  const text = document.body.innerText;
  return {
    layoutViewport: document.documentElement.clientWidth, visualScale: window.visualViewport ? visualViewport.scale : null,
    viewportMeta: (document.querySelector('meta[name=viewport]') || {}).content || null,
    lang: document.documentElement.lang || null, headings: document.querySelectorAll('h1,h2,h3,h4,h5,h6').length,
    guidelineItems: items.length, bodyFontPx: parseFloat(getComputedStyle(items[0] || document.body).fontSize),
    controls,
    keyStatements: {
      noAadhaarPanUpload: /Do not upload Aadhar Card or PAN Card/i.test(text),
      bplNoFee: /below poverty line/i.test(text),
      paymentModes: /Internet banking/i.test(text) && /UPI/i.test(text),
      waitBeforeRepayment: /do not make additional attempt to make\s+payment/i.test(text),
      firstAppealNoFee: /no fee has to be paid for first appeal/i.test(text)
    },
    acknowledgementGate: /I have read and understood the above guidelines/i.test(text)
  };
}"""


def main() -> None:
    result: dict[str, object] = {"checkedAt": datetime.now(timezone.utc).isoformat(timespec="seconds"),
                                 "method": "One read-only headless Chrome load per page, 390x844, is_mobile=True, Chrome's own headless user agent, normal TLS validation."}
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True, executable_path=CHROME)
        result["browser"] = f"Chrome {browser.version} (headless)"
        try:
            for key, url, script in (("tn-esevai-service-list", "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx", TN),
                                     ("rti-guidelines", "https://rtionline.gov.in/guidelines.php?request", RTI)):
                context = browser.new_context(viewport={"width": 390, "height": 844}, device_scale_factor=2, is_mobile=True,
                                              has_touch=True, ignore_https_errors=False)
                page = context.new_page()
                response = page.goto(url, wait_until="domcontentloaded", timeout=30000)
                page.wait_for_timeout(3000)
                result[key] = {"url": url, "httpStatus": response.status if response else None, "finalUrl": page.url,
                               "structure": page.evaluate(script)}
                context.close()
        finally:
            browser.close()
    OUT.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
