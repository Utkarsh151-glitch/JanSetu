package `in`.jansetu.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.catalog.CatalogSearch
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.JanSetuTheme
import `in`.jansetu.app.ui.SearchScreen
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlin.system.measureNanoTime

class RegionalCatalogTest {
    @get:Rule val composeRule = createComposeRule(effectContext = StandardTestDispatcher())

    @Test fun actualRegionalRecordCanBeFoundThroughStateAndDistrictControls() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val content = context.assets.open("catalog.json").bufferedReader().use { it.readText() }
        lateinit var catalog: `in`.jansetu.app.catalog.Catalog
        val parseMs = measureNanoTime { catalog = CatalogParser.parse(content) } / 1_000_000.0
        val indexMs = measureNanoTime { CatalogSearch.prepare(catalog) } / 1_000_000.0
        val searchMs = measureNanoTime {
            repeat(5) { listOf("DigiLocker", "Madhya Pradesh", "health", "Delhi", "scholarship", "no-portal-991299").forEach {
                CatalogSearch.find(catalog, it)
            } }
        } / 1_000_000.0
        android.util.Log.i("JanSetuPerf", "REAL_DEVICE_CATALOG websites=${catalog.websites.size} bytes=${content.toByteArray().size} parseMs=$parseMs indexMs=$indexMs thirtySearchesMs=$searchMs")
        val district = catalog.websites.first { it.isDirectoryListed && it.state != null && it.district != null }
        val settings = LocalSettings(context)
        settings.updateLanguage("en")
        settings.updateState("All India")
        composeRule.setContent { JanSetuTheme { SearchScreen(catalog, settings, "", {}, {}) } }
        composeRule.onNodeWithText("All states / UTs").performScrollTo().performClick()
        composeRule.onNodeWithText(district.state!!).performScrollTo().performClick()
        composeRule.onNodeWithText("All districts").performScrollTo().performClick()
        composeRule.onNodeWithText(district.district!!).performScrollTo().performClick()
        composeRule.onNodeWithText("What government service do you need?").performTextInput(district.title)
        composeRule.waitUntil(20_000) {
            composeRule.onAllNodesWithText(district.domain, substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(district.domain, substring = true).assertExists()
        composeRule.onNodeWithText("${district.domain} · Listed by IGOD").assertExists()
    }
}
