package `in`.jansetu.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.DetailScreen
import `in`.jansetu.app.ui.JanSetuTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.StandardTestDispatcher

class DetailGuideTest {
    @get:Rule val composeRule = createComposeRule(effectContext = StandardTestDispatcher())

    @Test fun sourceBackedGuideAndMissingGuideRender() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val catalog = CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() })
        val settings = LocalSettings(context)
        settings.updateLanguage("en")
        var openedSource = false
        var reported = false
        val site = catalog.websites.first { it.id == "digilocker" }
        composeRule.setContent {
            JanSetuTheme {
                DetailScreen(site, catalog.guides.first { it.websiteId == site.id }, settings,
                    onBack = {}, onOpen = {}, onSource = {}, onGuideSource = { openedSource = true },
                    onReport = { reported = true })
            }
        }
        composeRule.onNodeWithText("Find an issued document").assertExists()
        composeRule.onNodeWithText("Guide source ↗").performScrollTo().performClick()
        assertTrue(openedSource)
        composeRule.onNodeWithText("Report a problem").performScrollTo().performClick()
        assertTrue(reported)
    }
}
