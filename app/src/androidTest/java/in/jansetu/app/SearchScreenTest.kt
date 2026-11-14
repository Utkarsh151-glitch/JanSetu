package `in`.jansetu.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.JanSetuTheme
import `in`.jansetu.app.ui.SearchScreen
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.StandardTestDispatcher

class SearchScreenTest {
    @get:Rule val composeRule = createComposeRule(effectContext = StandardTestDispatcher())

    @Test fun searchDisplaysVerifiedPortal() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val catalog = CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() })
        val settings = LocalSettings(context)
        settings.updateLanguage("en")
        composeRule.setContent {
            JanSetuTheme {
                SearchScreen(catalog, settings, "", onDetail = {}, onBrowse = {})
            }
        }
        composeRule.onNodeWithText("What government service do you need?").performTextInput("DigiLocker")
        composeRule.waitUntil(20_000) {
            composeRule.onAllNodesWithText("www.digilocker.gov.in", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("www.digilocker.gov.in", substring = true).assertExists()
    }

    @Test fun categorySearchShowsPublishedPortalAndHonestEmptyState() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val catalog = CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() })
        val settings = LocalSettings(context)
        settings.updateLanguage("en")
        composeRule.setContent {
            JanSetuTheme { SearchScreen(catalog, settings, "education", onDetail = {}, onBrowse = {}) }
        }
        composeRule.onNodeWithText("What government service do you need?").performTextInput("National Scholarship Portal")
        composeRule.waitUntil(20_000) {
            composeRule.onAllNodesWithText("scholarships.gov.in", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("scholarships.gov.in", substring = true).assertExists()
        composeRule.onNodeWithText("What government service do you need?").performTextReplacement("not-a-real-portal-991299")
        composeRule.waitUntil(20_000) {
            composeRule.onAllNodesWithText("No matching portals").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("No matching portals").assertExists()
    }
}
