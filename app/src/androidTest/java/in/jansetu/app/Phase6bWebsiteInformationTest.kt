package `in`.jansetu.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.*
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.*
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Phase 6B: the six-part website page, honest missing states, labels, the checked filter and navigation. */
class Phase6bWebsiteInformationTest {
    @get:Rule val rule = createComposeRule(effectContext = StandardTestDispatcher())
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private fun catalog() = CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() })
    private fun settings(language: String = "en") = LocalSettings(context).apply { updateLanguage(language); updateState("All India"); finishOnboarding() }
    private val sections = listOf("What this website is for", "Department or organisation", "Who it serves and where",
        "Verified services and tasks", "Official help and contact", "When checked and where from")

    private fun detail(catalog: Catalog, id: String, settings: LocalSettings, onService: (String) -> Unit = {}, onLink: (String) -> Unit = {}) {
        val site = catalog.websites.first { it.id == id }
        rule.setContent {
            JanSetuTheme {
                DetailScreen(site, catalog.guides.firstOrNull { it.websiteId == id }, settings, {}, {}, {}, {}, {}, catalog.categories,
                    profile = catalog.profileFor(id), services = catalog.servicesFor(id), onService = onService, onLink = onLink)
            }
        }
    }

    @Test fun checkedWebsiteShowsSixSectionsWithSourcesAndOfficialLinks() {
        val catalog = catalog(); var opened: String? = null; var service: String? = null
        detail(catalog, "cpgrams", settings(), onService = { service = it }, onLink = { opened = it })
        sections.forEach { rule.onNodeWithText(it).performScrollTo().assertExists() }
        rule.onNodeWithText("Purpose checked").assertExists()
        rule.onNodeWithTag("website-purpose").assertTextContains("Lodge public grievances", substring = true)
        rule.onNodeWithText("Run by (checked)").performScrollTo().assertExists()
        rule.onNodeWithTag("website-service:cpgrams-public-grievance").performScrollTo().performClick()
        rule.runOnIdle { assertEquals("cpgrams-public-grievance", service) }
        rule.onNodeWithTag("website-help:https://pgportal.gov.in/Home/Faq").performScrollTo().performClick()
        rule.runOnIdle { assertEquals("https://pgportal.gov.in/Home/Faq", opened) }
        rule.onNodeWithTag("website-task:https://pgportal.gov.in/Status").performScrollTo().assertExists()
        rule.onNodeWithTag("website-review-status").performScrollTo().assertTextContains("A person has not reviewed it yet", substring = true)
        rule.onNodeWithText("Notes from the check").performScrollTo().assertExists()
        rule.onNodeWithTag("website-sources").performScrollTo().performClick()
        rule.onAllNodesWithText("Open pgportal.gov.in ↗").onFirst().performScrollTo().performClick()
        rule.runOnIdle { assertTrue(opened!!.startsWith("https://pgportal.gov.in/")) }
        rule.onNodeWithText("Open official website").performScrollTo().assertExists()
        rule.onNodeWithText("Report a problem").performScrollTo().assertExists()
    }

    @Test fun directoryOnlyWebsiteSaysNotCheckedInsteadOfFiller() {
        val catalog = catalog()
        val site = catalog.websites.first { it.isDirectoryListed && catalog.profileFor(it.id) == null && catalog.servicesFor(it.id).isEmpty() }
        detail(catalog, site.id, settings())
        rule.onNodeWithText("Information under review").assertExists()
        rule.onNodeWithText("Listed by IGOD").assertExists()
        rule.onNodeWithTag("website-purpose-missing").performScrollTo().assertTextEquals("Not checked yet")
        rule.onNodeWithTag("website-services-missing").performScrollTo().assertExists()
        rule.onNodeWithTag("website-help-missing").performScrollTo()
            .assertTextContains("No official help or contact page has been checked yet", substring = true)
        rule.onNodeWithTag("website-review-status").assertDoesNotExist()
        rule.onNodeWithText("Purpose checked").assertDoesNotExist()
    }

    @Test fun notStatedIsDistinctFromNotChecked() {
        val catalog = catalog()
        detail(catalog, "igod-85affa23dc47255b", settings())
        rule.onNodeWithTag("website-help-missing").performScrollTo()
            .assertTextContains("No official help or contact page was found on the pages JanSetu checked", substring = true)
        rule.onNodeWithText("Not stated on the official pages JanSetu checked").performScrollTo().assertExists()
        rule.onNodeWithText("Purpose checked").assertExists()
    }

    @Test fun hindiDetailKeepsSectionsAndMarksEnglishSummaries() {
        val catalog = catalog()
        detail(catalog, "igod-9908672336cdd912", settings("hi"))
        listOf("यह वेबसाइट किस लिए है", "विभाग या संगठन", "किसके लिए और कहाँ", "सत्यापित सेवाएँ और कार्य", "आधिकारिक सहायता और संपर्क", "कब और कहाँ से जाँचा गया")
            .forEach { rule.onNodeWithText(it).performScrollTo().assertExists() }
        rule.onAllNodesWithText("जाँची गई जानकारी अंग्रेज़ी में जनसेतु का सारांश है।").onFirst().performScrollTo().assertExists()
        rule.onNodeWithText("उद्देश्य जाँचा गया").assertExists()
    }

    @Test fun searchLabelsAndCheckedFilterNeverWidenResults() {
        val catalog = catalog()
        rule.setContent { JanSetuTheme { SearchScreen(catalog, settings(), "", onDetail = {}, onBrowse = {}, initialQuery = "scholarship") } }
        // Services rank first; lazily composed website cards further down are not asserted before filtering.
        rule.waitUntil(20_000) { rule.onAllNodesWithText("Verified service").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithTag("filter:Services and websites").performScrollTo().performClick()
        rule.onNodeWithText("Websites with checked information").performClick()
        rule.waitUntil(20_000) { rule.onAllNodesWithTag("website:national-scholarship-portal").fetchSemanticsNodes().isNotEmpty() }
        rule.onAllNodesWithText("Verified service").assertCountEquals(0)
        rule.onAllNodesWithText("Purpose checked").onFirst().assertExists()
        rule.onAllNodesWithText("Information under review").assertCountEquals(0)
        val expected = CitizenSearch.find(catalog, "scholarship", type = "checked")
        assertTrue(expected.isNotEmpty() && expected.all { catalog.hasCheckedInformation(it.website) })
    }

    @Test fun websitePageOpensAVerifiedServiceThroughAppNavigation() {
        context.getSharedPreferences("jansetu_preferences", 0).edit().clear().commit()
        rule.setContent { JanSetuTheme { JanSetuApp(settings()) } }
        rule.waitUntil(25_000) { rule.onAllNodesWithText("What government service do you need?").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("Search").performClick()
        rule.onNodeWithText("What government service do you need?").performTextInput("RTI Online")
        rule.waitUntil(20_000) { rule.onAllNodesWithTag("website:rti-online").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithTag("website:rti-online").performScrollTo().performClick()
        rule.onNodeWithText("What this website is for").assertExists()
        rule.onNodeWithTag("website-service:rti-first-appeal").performScrollTo().performClick()
        rule.onNodeWithText("Verified service information").assertExists()
        rule.onNodeWithContentDescription("Back").performScrollTo().performClick()
        rule.onNodeWithText("What this website is for").performScrollTo().assertExists()
    }
}
