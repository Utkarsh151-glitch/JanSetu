"""Phase 6A read-only page screening for the government-website UX audit.

Usage: python tools/phase6_site_audit.py [YYYY-MM-DD]

Each listed public page is loaded once in local headless Chrome with normal TLS
validation and Chrome's own headless user agent (no spoofing, no retry to get
past a 403). Nothing is clicked, typed, submitted or signed in. The script
measures the loaded document at a 390px phone viewport, then resizes the same
document to 320px (the WCAG reflow reference width) and 195px (the CSS width a
390px phone has at 200% browser zoom).

Only for the existing National Scholarship Portal pilot does the script apply
JanSetu's packaged extension/readability.css, locally in this headless session,
to compare the page with and without it and confirm that removing it restores
the original styles. This changes nothing on the website. Playwright inserts
the stylesheet with author origin, whereas the extension uses a user-origin
stylesheet, so cascade results are an approximation of the installed extension.

Results are dated single observations from this machine, not benchmarks,
WCAG conformance results or claims about every visitor's experience.
"""
from __future__ import annotations

import json
import sys
import time
from datetime import datetime, timezone
from pathlib import Path

from playwright.sync_api import sync_playwright

ROOT = Path(__file__).resolve().parents[1]
CHROME = r"C:\Program Files\Google\Chrome\Application\chrome.exe"
SHOTS = ROOT / "docs/screenshots/phase6"
READABILITY_CSS = ROOT / "data/retired/nsp-readable-text.css"

# (pageId, portalId, url, why it is in the sample)
PAGES = [
    ("scholarship-home", "national-scholarship-portal", "https://scholarships.gov.in/home",
     "Existing Chrome readability pilot (re-evaluation)"),
    ("india-portal-home", "india-portal", "https://www.india.gov.in/",
     "Existing Android in-app browser with A+/A (Chrome check; Android checked separately)"),
    ("digilocker-home", "digilocker", "https://www.digilocker.gov.in/",
     "Earlier B (text scaling) prospect"),
    ("rti-home", "rti-online", "https://rtionline.gov.in/",
     "Earlier D; recheck structural reflow"),
    ("kerala-edistrict-faq", "igod-50b73c3937b8d29b", "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
     "Official information page for 8 JanSetu services"),
    ("odisha-edistrict-home", "igod-5e885f0e333d5206", "https://edistrict.odisha.gov.in/",
     "Official information page for 8 JanSetu services"),
    ("tn-esevai-service-list", "igod-2715637a8f399700", "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
     "Official information page for 7 JanSetu services"),
    ("uidai-my-aadhaar", "uidai", "https://www.uidai.gov.in/en/my-aadhaar",
     "Official information page for 4 JanSetu services"),
    ("cpgrams-faq", "cpgrams", "https://pgportal.gov.in/Home/Faq",
     "Official information page for 2 JanSetu services"),
    ("rti-guidelines", "rti-online", "https://rtionline.gov.in/guidelines.php?request",
     "Official information page for 2 JanSetu services"),
    ("ncs-faq", "national-career-service", "https://ncs.gov.in/faq",
     "Official information page for 2 JanSetu services"),
    ("incometax-epan-help", "income-tax-efiling",
     "https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/instant-e-pan/instant-UM",
     "Official help page for 2 JanSetu services"),
]

METRICS = r"""() => {
  const visible = el => {
    const r = el.getBoundingClientRect(), s = getComputedStyle(el);
    return r.width > 0 && r.height > 0 && s.visibility !== 'hidden' && s.display !== 'none' && parseFloat(s.opacity || '1') > 0;
  };
  const median = a => { if (!a.length) return null; const b = [...a].sort((x, y) => x - y); return b[Math.floor(b.length / 2)]; };
  const parse = c => { const m = c.match(/rgba?\(([^)]+)\)/); if (!m) return null; const p = m[1].split(/[ ,\/]+/).filter(Boolean).map(Number); return {r: p[0], g: p[1], b: p[2], a: p.length > 3 ? p[3] : 1}; };
  const lum = c => { const f = v => { v /= 255; return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4); }; return 0.2126 * f(c.r) + 0.7152 * f(c.g) + 0.0722 * f(c.b); };
  const background = el => {
    for (let n = el; n && n.nodeType === 1; n = n.parentElement) {
      const s = getComputedStyle(n);
      if (s.backgroundImage && s.backgroundImage !== 'none') return null; // image backgrounds cannot be measured here
      const c = parse(s.backgroundColor); if (c && c.a >= 0.99) return c;
    }
    return {r: 255, g: 255, b: 255, a: 1};
  };
  const blocks = [];
  for (const el of document.body ? document.body.querySelectorAll('*') : []) {
    if (['SCRIPT', 'STYLE', 'NOSCRIPT', 'SVG', 'OPTION'].includes(el.tagName)) continue;
    let own = '';
    for (const n of el.childNodes) if (n.nodeType === 3) own += n.textContent;
    if (own.replace(/\s+/g, ' ').trim().length >= 30 && visible(el)) blocks.push(el);
  }
  const sizes = blocks.map(el => parseFloat(getComputedStyle(el).fontSize));
  const ratios = blocks.map(el => { const s = getComputedStyle(el), f = parseFloat(s.fontSize); return (s.lineHeight === 'normal' ? 1.2 * f : parseFloat(s.lineHeight)) / f; });
  let lowContrast = 0, measured = 0, minContrast = null;
  for (const el of blocks) {
    const fg = parse(getComputedStyle(el).color), bg = background(el);
    if (!fg || !bg) continue;
    const a = lum(fg), b = lum(bg), ratio = (Math.max(a, b) + 0.05) / (Math.min(a, b) + 0.05);
    measured++; if (ratio < 4.5) lowContrast++; minContrast = minContrast === null ? ratio : Math.min(minContrast, ratio);
  }
  const controls = [...document.querySelectorAll('a[href],button,input:not([type=hidden]),select,textarea,[role=button]')].filter(visible);
  const overlays = [...document.querySelectorAll('body *')].filter(el => { const p = getComputedStyle(el).position; return (p === 'fixed' || p === 'sticky') && visible(el); });
  const overlayArea = overlays.reduce((sum, el) => { const r = el.getBoundingClientRect();
    return sum + Math.max(0, Math.min(r.right, innerWidth) - Math.max(r.left, 0)) * Math.max(0, Math.min(r.bottom, innerHeight) - Math.max(r.top, 0)); }, 0);
  const textOf = el => (el.textContent || '').replace(/\s+/g, ' ').trim();
  const nativeText = [...document.querySelectorAll('a,button')].some(el => /^A\s*[+\-−]$|^A\s*\+?\s*A\s*-?$/.test(textOf(el)) ||
    /font size|text size|increase text|decrease text/i.test((el.getAttribute('aria-label') || '') + ' ' + (el.title || '')));
  const html = document.documentElement.outerHTML;
  return {
    viewport: {width: innerWidth, height: innerHeight},
    documentWidth: document.documentElement.scrollWidth,
    horizontalOverflow: document.documentElement.scrollWidth > innerWidth + 2,
    documentHeight: document.documentElement.scrollHeight,
    title: document.title, lang: document.documentElement.lang || null,
    viewportMeta: (document.querySelector('meta[name=viewport]') || {}).content || null,
    h1: document.querySelectorAll('h1').length, headings: document.querySelectorAll('h1,h2,h3,h4,h5,h6').length,
    textBlocks: blocks.length, medianFontPx: median(sizes), minFontPx: sizes.length ? Math.min(...sizes) : null,
    blocksUnder16px: sizes.filter(s => s < 16).length, blocksUnder13px: sizes.filter(s => s < 13).length,
    medianLineHeightRatio: ratios.length ? Math.round(median(ratios) * 100) / 100 : null,
    contrastMeasuredBlocks: measured, blocksContrastUnder4_5: lowContrast,
    minContrast: minContrast === null ? null : Math.round(minContrast * 100) / 100,
    visibleControls: controls.length,
    controlsUnder24px: controls.filter(el => { const r = el.getBoundingClientRect(); return r.width < 24 || r.height < 24; }).length,
    controlsBeyondRightEdge: controls.filter(el => el.getBoundingClientRect().left >= innerWidth).length,
    fixedOrStickyViewportShare: Math.round(overlayArea / (innerWidth * innerHeight) * 100) / 100,
    nativeTextSizeControls: nativeText,
    skipLink: [...document.querySelectorAll('a[href^="#"]')].some(a => /skip/i.test(textOf(a))),
    passwordFields: document.querySelectorAll('input[type=password]').length,
    fileInputs: document.querySelectorAll('input[type=file]').length,
    formControls: document.querySelectorAll('input:not([type=hidden]),select,textarea').length,
    captchaSignal: /captcha/i.test(html),
    paymentSignal: /payment gateway|pay now|make payment/i.test(document.body ? document.body.innerText : ''),
    sameDocumentHashLinks: [...document.querySelectorAll('a[href]')].filter(a => { try { const u = new URL(a.href);
      return u.origin === location.origin && u.pathname === location.pathname && u.search === location.search && !!u.hash; } catch (e) { return false; } }).length
  };
}"""

# For the pilot: are interactive controls still visible, on-screen and not covered?
CONTROLS = r"""() => [...document.querySelectorAll('a[href],button,input:not([type=hidden]),select,textarea,[role=button]')]
  .filter(el => { const r = el.getBoundingClientRect(), s = getComputedStyle(el); return r.width > 0 && r.height > 0 && s.visibility !== 'hidden' && s.display !== 'none'; })
  .map(el => { const r = el.getBoundingClientRect(); const x = r.left + r.width / 2, y = r.top + r.height / 2;
    const inView = x >= 0 && x < innerWidth && y >= 0 && y < innerHeight;
    const hit = inView ? document.elementFromPoint(x, y) : null;
    return {text: (el.innerText || el.value || el.getAttribute('aria-label') || '').trim().slice(0, 40), covered: inView && !!hit && hit !== el && !el.contains(hit) && !hit.contains(el),
            beyondRight: r.left >= innerWidth, width: Math.round(r.width), height: Math.round(r.height)}; })"""

STYLE_SNAPSHOT = r"""() => [...document.querySelectorAll('body *')].slice(0, 4000).map(el => { const s = getComputedStyle(el);
  return [s.fontSize, s.lineHeight, s.letterSpacing, s.display, s.visibility, s.textDecorationThickness, s.outlineStyle].join('|'); }).join('\n')"""


def measure(page, widths=(320, 195)):
    result = {"390": page.evaluate(METRICS)}
    for width in widths:
        page.set_viewport_size({"width": width, "height": round(844 * width / 390)})
        page.wait_for_timeout(700)
        m = page.evaluate(METRICS)
        result[str(width)] = {k: m[k] for k in ("viewport", "documentWidth", "horizontalOverflow", "controlsBeyondRightEdge",
                                                "medianFontPx", "fixedOrStickyViewportShare")}
    page.set_viewport_size({"width": 390, "height": 844})
    page.wait_for_timeout(700)
    return result


def shot(page, name: str) -> str:
    SHOTS.mkdir(parents=True, exist_ok=True)
    path = SHOTS / f"{name}.png"
    page.screenshot(path=str(path))
    return path.relative_to(ROOT).as_posix()


def pilot(page) -> dict:
    """Compare the reviewed NSP /home page without and with the packaged stylesheet."""
    out = {"note": "Local headless comparison only; author-origin approximation of the extension's user-origin stylesheet."}
    before_controls = page.evaluate(CONTROLS)
    before_styles = page.evaluate(STYLE_SNAPSHOT)
    out["before"] = page.evaluate(METRICS)
    handle = page.add_style_tag(path=str(READABILITY_CSS))
    page.wait_for_timeout(600)
    after_controls = page.evaluate(CONTROLS)
    out["after"] = page.evaluate(METRICS)
    out["screenshotAfter390"] = shot(page, "scholarship-home-readable-390")
    out["controls"] = {
        "before": len(before_controls), "after": len(after_controls),
        "coveredBefore": sum(c["covered"] for c in before_controls), "coveredAfter": sum(c["covered"] for c in after_controls),
        "beyondRightBefore": sum(c["beyondRight"] for c in before_controls), "beyondRightAfter": sum(c["beyondRight"] for c in after_controls),
        "newlyCovered": [c["text"] for c in after_controls if c["covered"]][:12],
    }
    page.set_viewport_size({"width": 1280, "height": 800})
    page.wait_for_timeout(800)
    out["after1280"] = {k: page.evaluate(METRICS)[k] for k in ("documentWidth", "horizontalOverflow", "medianFontPx", "medianLineHeightRatio")}
    out["screenshotAfter1280"] = shot(page, "scholarship-home-readable-1280")
    handle.evaluate("el => el.remove()")
    page.wait_for_timeout(600)
    out["original1280"] = {k: page.evaluate(METRICS)[k] for k in ("documentWidth", "horizontalOverflow", "medianFontPx", "medianLineHeightRatio")}
    out["screenshotOriginal1280"] = shot(page, "scholarship-home-original-1280")
    page.set_viewport_size({"width": 390, "height": 844})
    page.wait_for_timeout(800)
    out["restoredStylesIdentical"] = page.evaluate(STYLE_SNAPSHOT) == before_styles
    return out


def main() -> None:
    day = sys.argv[1] if len(sys.argv) > 1 else datetime.now().date().isoformat()
    out = ROOT / f"data/screening_runs/{day}.json"
    if out.exists():
        raise SystemExit(f"{out} exists; screening runs are dated records and are not overwritten")
    rows = []
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True, executable_path=CHROME)
        browser_version = browser.version
        user_agent = None
        try:
            for page_id, portal_id, url, reason in PAGES:
                context = browser.new_context(viewport={"width": 390, "height": 844}, device_scale_factor=2,
                                              is_mobile=False, ignore_https_errors=False)
                page = context.new_page()
                user_agent = user_agent or page.evaluate("navigator.userAgent")
                row = {"id": page_id, "portalId": portal_id, "url": url, "reason": reason,
                       "checkedAt": datetime.now(timezone.utc).isoformat(timespec="seconds")}
                try:
                    started = time.monotonic()
                    response = page.goto(url, wait_until="domcontentloaded", timeout=30000)
                    row["elapsedMsSingleRun"] = round((time.monotonic() - started) * 1000)
                    row["httpStatus"] = response.status if response else None
                    row["finalUrl"] = page.url
                    if response and response.status < 400:
                        page.wait_for_timeout(3000)
                        row["screenshot390"] = shot(page, f"{page_id}-390")
                        row["metrics"] = measure(page)
                        page.set_viewport_size({"width": 195, "height": 422})
                        page.wait_for_timeout(700)
                        row["screenshotZoom200"] = shot(page, f"{page_id}-zoom200")
                        page.set_viewport_size({"width": 390, "height": 844})
                        page.wait_for_timeout(700)
                        if page_id == "scholarship-home":
                            row["pilot"] = pilot(page)
                    else:
                        row["result"] = "access_or_server_response; no UI conclusion"
                except Exception as error:  # A failed load is recorded, never retried around.
                    row["result"] = f"inconclusive:{type(error).__name__}:{str(error)[:160]}"
                finally:
                    context.close()
                rows.append(row)
                print(f"{page_id}: {row.get('httpStatus', row.get('result'))}", flush=True)
                time.sleep(3)
        finally:
            browser.close()
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(json.dumps({
        "method": "Phase 6A: one read-only headless Chrome load per page; metrics at 390px, then the same document resized to 320px and 195px (about 200% zoom on a 390px phone). No interaction, sign-in or form use.",
        "browser": f"Chrome {browser_version} (headless)", "userAgent": user_agent,
        "tls": "Normal certificate validation (ignore_https_errors=False)",
        "pages": rows}, indent=2) + "\n", encoding="utf-8")
    print(f"Wrote {out}")


if __name__ == "__main__":
    main()
