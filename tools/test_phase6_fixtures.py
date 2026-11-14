"""Phase 6A static-fixture tests for page assistance.

These run in local headless Chrome against files in tools/fixtures/phase6/
(no network, no live government page). Stylesheets are inserted as author
styles, an approximation of the extension's user-origin insertion. They test
mechanics only; they are not live-page checks or human review.
"""
from __future__ import annotations

import unittest
from pathlib import Path

from site_interventions import lint_css

ROOT = Path(__file__).resolve().parents[1]
FIXTURES = ROOT / "tools/fixtures/phase6"
READABILITY = ROOT / "data/retired/nsp-readable-text.css"
PROPOSAL = ROOT / "data/site_intervention_proposals/tn-esevai-service-list.css"
CHROME = Path(r"C:\Program Files\Google\Chrome\Application\chrome.exe")

CRITICAL = r"""() => [...document.querySelectorAll('[data-critical]')].map(el => {
  el.scrollIntoView({block: 'center', inline: 'nearest'});
  const r = el.getBoundingClientRect(), s = getComputedStyle(el);
  const x = r.left + r.width / 2, y = r.top + r.height / 2;
  const hit = document.elementFromPoint(Math.min(Math.max(x, 0), innerWidth - 1), Math.min(Math.max(y, 0), innerHeight - 1));
  return {id: el.dataset.critical, text: el.textContent.trim(), visible: r.width > 0 && r.height > 0 && s.visibility === 'visible' && s.display !== 'none',
          withinWidth: r.left >= 0 && r.right <= innerWidth + 1, covered: !!hit && hit !== el && !el.contains(hit) && !hit.contains(el)};
})"""

SNAPSHOT = r"""(skip) => [...document.querySelectorAll('*')].filter(el => !["STYLE", "SCRIPT", "LINK", "META", "TITLE", "HEAD"].includes(el.tagName)).map(el => { const s = getComputedStyle(el);
  return ['fontSize', 'lineHeight', 'letterSpacing', 'display', 'visibility', 'position', 'color', 'backgroundColor', 'overflowX', 'width']
    .filter(k => !skip.includes(k)).map(k => s[k]).join('|'); }).join('\n')"""


class Phase6FixtureTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        try:
            from playwright.sync_api import sync_playwright
        except ImportError as error:  # Reported as a skip, never as a pass.
            raise unittest.SkipTest(f"Playwright unavailable: {error}")
        if not CHROME.exists():
            raise unittest.SkipTest("Chrome unavailable")
        cls.playwright = sync_playwright().start()
        cls.browser = cls.playwright.chromium.launch(headless=True, executable_path=str(CHROME))

    @classmethod
    def tearDownClass(cls):
        cls.browser.close()
        cls.playwright.stop()

    def open(self, name: str, mobile: bool = False):
        context = self.browser.new_context(viewport={"width": 390, "height": 844}, is_mobile=mobile, has_touch=mobile)
        self.addCleanup(context.close)
        page = context.new_page()
        page.goto((FIXTURES / name).as_uri())
        return page

    def test_readable_text_keeps_every_critical_control_and_restores_exactly(self):
        page = self.open("public_info.html")
        before = page.evaluate(CRITICAL)
        styles = page.evaluate(SNAPSHOT, [])
        width = page.evaluate("document.documentElement.scrollWidth")
        style = page.add_style_tag(path=str(READABILITY))
        after = page.evaluate(CRITICAL)
        self.assertEqual([c["text"] for c in before], [c["text"] for c in after], "Text of critical elements must not change")
        for control in after:
            self.assertTrue(control["visible"], control)
            self.assertTrue(control["withinWidth"], control)
            self.assertFalse(control["covered"], control)
        self.assertLessEqual(page.evaluate("document.documentElement.scrollWidth"), max(width, 390))
        sizes = page.evaluate("[...document.querySelectorAll('main p, main li')].map(e => [parseFloat(getComputedStyle(e).fontSize), parseFloat(getComputedStyle(e).lineHeight)])")
        for size, line in sizes:
            self.assertGreaterEqual(size, 16)
            self.assertGreaterEqual(line / size, 1.5)
        style.evaluate("el => el.remove()")
        self.assertEqual(styles, page.evaluate(SNAPSHOT, []), "Restore original must return identical computed styles")

    def test_stylesheet_cannot_repair_a_page_without_a_viewport_meta(self):
        page = self.open("desktop_only_guidelines.html", mobile=True)
        measure = "({layout: document.documentElement.clientWidth, scale: visualViewport.scale, font: parseFloat(getComputedStyle(document.querySelector('li')).fontSize)})"
        before = page.evaluate(measure)
        self.assertGreater(before["layout"], 900, "Phones lay out a page without viewport meta at desktop width")
        self.assertLess(before["font"] * before["scale"], 9, "Text appears at under 9px at first view")
        page.add_style_tag(path=str(READABILITY))
        after = page.evaluate(measure)
        self.assertEqual(before["layout"], after["layout"])
        self.assertAlmostEqual(before["scale"], after["scale"], places=3)
        self.assertLess(after["font"] * after["scale"], 9, "A typography stylesheet still leaves the text tiny: guidance (class D) is the safer route")
        gate = page.evaluate("[...document.querySelectorAll('[data-critical]')].map(e => [e.dataset.critical, e.getBoundingClientRect().width > 0])")
        self.assertEqual([["ack", True], ["submit", True], ["cancel", True]], gate)

    def test_overflow_proposal_restores_scrolling_to_clipped_columns_and_reverses(self):
        page = self.open("clipped_table.html", mobile=True)
        container = "document.querySelector('.smooth-overflow')"
        apply_header = "document.querySelector('[data-critical=apply-header]').getBoundingClientRect()"
        self.assertEqual("hidden", page.evaluate(f"getComputedStyle({container}).overflowX"))
        self.assertGreaterEqual(page.evaluate(f"{apply_header}.left"), 390, "Apply column starts off-screen")
        page.mouse.move(195, 120)
        page.mouse.wheel(600, 0)
        page.wait_for_timeout(300)
        self.assertEqual(0, page.evaluate(f"{container}.scrollLeft"), "Without the rule a person cannot scroll to the clipped columns")
        texts = page.evaluate("document.body.innerText")
        others = page.evaluate(SNAPSHOT, ["overflowX"])
        rule = page.add_style_tag(path=str(PROPOSAL))
        self.assertEqual("auto", page.evaluate(f"getComputedStyle({container}).overflowX"))
        page.mouse.move(195, 120)
        page.mouse.wheel(600, 0)
        page.wait_for_timeout(300)
        self.assertGreater(page.evaluate(f"{container}.scrollLeft"), 0, "With the rule the same gesture scrolls sideways")
        self.assertLess(page.evaluate(f"{apply_header}.left"), 390, "Apply column becomes reachable")
        self.assertEqual(texts, page.evaluate("document.body.innerText"), "No text added, removed or changed")
        self.assertEqual(others, page.evaluate(SNAPSHOT, ["overflowX"]), "Only the container's overflow-x may change")
        rule.evaluate("el => el.remove()")
        self.assertGreater(page.evaluate(f"{container}.scrollLeft"), 0,
                           "Removing the rule alone leaves the table shifted, so Restore must also reset the scroll position")
        page.evaluate(f"{container}.scrollLeft = 0")
        self.assertEqual(others, page.evaluate(SNAPSHOT, ["overflowX"]))
        self.assertEqual("hidden", page.evaluate(f"getComputedStyle({container}).overflowX"))


class AssistCssLintTest(unittest.TestCase):
    def test_reviewed_and_proposed_stylesheets_only_touch_allowed_properties(self):
        lint_css(READABILITY.read_text(encoding="utf-8"), "readability.css")
        lint_css(PROPOSAL.read_text(encoding="utf-8"), "proposal")

    def test_lint_rejects_hiding_moving_or_recolouring(self):
        for css in (":where(.banner) { display: none; }", ":where(p) { color: #333; }", ":where(.x) { position: absolute; }",
                    "p { font-size: 18px; }", ":where(a) { outline: 2px solid red; }", ":where(div) { overflow-x: hidden; }"):
            with self.assertRaises(AssertionError, msg=css):
                lint_css(css, "negative example")


if __name__ == "__main__":
    unittest.main()
