package `in`.jansetu.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.BeforeYouRequest
import `in`.jansetu.app.ui.JanSetuTheme
import `in`.jansetu.app.ui.ServiceScreen
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Phase 17: the "Before you request" card on the RTI request page. */
class RtiGuidanceTest {
    @get:Rule val rule = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val catalog by lazy { CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() }) }
    private fun settings() = LocalSettings(context).apply { updateLanguage("en"); finishOnboarding() }

    @Test fun cardShowsTheFiveRulesWithSourcesAndLeavesTheOfficialGateToThePerson() {
        val rti = catalog.services.first { it.id == "rti-central-request" }
        val opened = mutableListOf<String>()
        rule.setContent { JanSetuTheme { ServiceScreen(rti, settings(), onBack = {}, onWebsite = {}, onLink = { opened += it }, related = catalog.services) } }
        rule.onNodeWithTag("rti-before-request").performScrollTo().assertExists()
        assertEquals(5, rule.onAllNodesWithTag("rti-rule").fetchSemanticsNodes().size)
        listOf("If you are below the poverty line", "Identity documents", "Paying the fee", "If no registration number appears after payment", "If you later appeal")
            .forEach { rule.onNodeWithText(it).assertExists() }
        rule.onNodeWithTag("rti-boundary").performScrollTo()
            .assertTextContains("tick the acknowledgement yourself", substring = true)
            .assertTextContains("JanSetu does not tick it, submit, pay for or track your request", substring = true)
        // Every source link and the guidelines link open official HTTPS pages in the browser; nothing is submitted.
        rule.onNodeWithTag("rti-guidelines").performScrollTo().performClick()
        rule.onAllNodesWithText("Source:", substring = true).onFirst().performScrollTo().performClick()
        rule.runOnIdle {
            assertEquals(BeforeYouRequest.GUIDELINES, opened.first())
            assertTrue(opened.all { it.startsWith("https://") && (it.contains(".gov.in/") || it.contains(".nic.in/")) })
        }
        listOf("JanSetu submits", "submitted for you", "Requirement met", "approved by", "track your request status")
            .forEach { assertTrue(it, rule.onAllNodesWithText(it, substring = true, ignoreCase = true).fetchSemanticsNodes().isEmpty()) }
    }

    @Test fun otherServicesDoNotShowTheCard() {
        var current by mutableStateOf(catalog.services.first { it.id == "rti-first-appeal" })
        rule.setContent { JanSetuTheme { ServiceScreen(current, settings(), onBack = {}, onWebsite = {}, onLink = {}, related = catalog.services) } }
        rule.waitForIdle()
        assertTrue(rule.onAllNodesWithTag("rti-before-request").fetchSemanticsNodes().isEmpty())
        rule.runOnIdle { current = catalog.services.first { it.id == "dl-income-certificate" } }
        rule.waitForIdle()
        assertTrue(rule.onAllNodesWithTag("rti-before-request").fetchSemanticsNodes().isEmpty())
    }
}
