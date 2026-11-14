package `in`.jansetu.app

import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.BrowserScreen
import `in`.jansetu.app.ui.JanSetuTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Phase 12: the National Portal in-app browser follows Android's font size and keeps the reader's choices on recreation. */
class BrowserReadingTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val portal by lazy {
        CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() }).websites.single { it.browserSupport == "in_app" }
    }
    private fun settings() = LocalSettings(context).apply { updateLanguage("en"); finishOnboarding() }

    private fun webView(): WebView {
        fun find(view: View): WebView? = view as? WebView ?: (view as? ViewGroup)?.let { g -> (0 until g.childCount).firstNotNullOfOrNull { find(g.getChildAt(it)) } }
        return requireNotNull(find(rule.activity.window.decorView))
    }

    private fun zoom(): Int { var value = 0; rule.runOnIdle { value = webView().settings.textZoom }; return value }

    @Test fun websiteTextFollowsTheAndroidFontSizeAndTheControls() {
        val large = Configuration(context.resources.configuration).apply { fontScale = 2.0f }
        rule.setContent { CompositionLocalProvider(LocalConfiguration provides large) {
            JanSetuTheme { BrowserScreen(portal, settings(), onClose = {}, onExternal = {}) } } }
        rule.onNodeWithTag("browser-text-size").assertTextEquals("200%")
        assertEquals(200, zoom())
        rule.onNodeWithTag("browser-text-larger").assertIsNotEnabled()
        rule.onNodeWithTag("browser-text-normal").performClick()
        rule.onNodeWithTag("browser-text-size").assertTextEquals("100%")
        assertEquals(100, zoom())
        rule.onNodeWithTag("browser-text-larger").performClick()
        assertEquals(125, zoom())
        rule.onNodeWithContentDescription("Website text size 125 percent").assertExists()
        // The page's own settings stay locked down: zoom is the only thing JanSetu changes.
        rule.runOnIdle {
            val s = webView().settings
            assertFalse(s.allowFileAccess); assertFalse(s.allowContentAccess)
            assertEquals(android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW, s.mixedContentMode)
        }
    }

    @Test fun choicesSurviveRecreationSuchAsRotationOrFontChange() {
        val restoration = StateRestorationTester(rule)
        restoration.setContent { JanSetuTheme { BrowserScreen(portal, settings(), onClose = {}, onExternal = {}) } }
        val base = zoom()
        rule.onNodeWithTag("browser-text-larger").performClick()
        val chosen = zoom()
        assertTrue(chosen > base)
        restoration.emulateSavedInstanceStateRestore()
        rule.onNodeWithTag("browser-text-size").assertTextEquals("$chosen%")
        assertEquals(chosen, zoom())
    }
}
