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

class ServiceFlowTest {
    @get:Rule val rule=createComposeRule(effectContext=StandardTestDispatcher())
    private val context get()=InstrumentationRegistry.getInstrumentation().targetContext
    private fun catalog()=CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use {it.readText()})
    private fun settings()=LocalSettings(context).apply {updateLanguage("en");updateState("All India");finishOnboarding()}

    @Test fun citizenSearchOpensARealServiceThroughAppNavigation() {
        context.getSharedPreferences("jansetu_preferences",0).edit().clear().commit()
        rule.setContent {JanSetuTheme {JanSetuApp(settings())}}
        rule.waitUntil(25_000) {rule.onAllNodesWithText("What government service do you need?").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("Search").performClick()
        rule.onNodeWithText("What government service do you need?").performTextInput("How can I get a caste certificate")
        rule.waitUntil(20_000) {rule.onAllNodesWithTag("service:tn-community-certificate").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithTag("service:tn-community-certificate").performScrollTo().performClick()
        rule.onNodeWithText("Verified service information").assertExists()
        rule.onNodeWithText("Community Certificate (Tamil Nadu)").assertExists()
        rule.onNodeWithText("Who may apply").performScrollTo().assertExists()
        rule.onAllNodesWithText("Not verified in this pilot. Check the official information page.").onFirst().assertExists()
        rule.onNodeWithTag("service-apply").assertDoesNotExist()
        rule.onNodeWithContentDescription("Back").performScrollTo().performClick()
        rule.onNodeWithText("Search portals").assertExists()
    }

    @Test fun serviceOfficialLinksUseExactEvidenceAndApplicationUrls() {
        val s=catalog().services.first {it.id=="rti-first-appeal"};var opened:String?=null
        rule.setContent {JanSetuTheme {ServiceScreen(s,settings(),{},{},{opened=it})}}
        rule.onNodeWithTag("service-information").performScrollTo().performClick()
        rule.runOnIdle {assertEquals(s.informationUrl,opened)}
        rule.onNodeWithTag("service-apply").performScrollTo().performClick()
        rule.runOnIdle {assertEquals(s.applicationUrl,opened)}
        rule.onNodeWithText("Fees").performScrollTo().assertExists()
        rule.onNodeWithText("No fee for the first appeal.").assertExists()
    }
    @Test fun newInformationOnlyServiceShowsUnknownsSourcesAndNoApply() {
        val s=catalog().services.first {it.id=="py-income-certificate"};var opened:String?=null
        rule.setContent {JanSetuTheme {ServiceScreen(s,settings(),{},{},{opened=it},onAddApplication={error("No unconfirmed shortcut")})}}
        rule.onNodeWithTag("service-apply").assertDoesNotExist();rule.onNodeWithTag("service-track").assertDoesNotExist()
        rule.onNodeWithText("Documents required").performScrollTo().assertExists()
        rule.onAllNodesWithText("Not verified in this pilot. Check the official information page.").onFirst().assertExists()
        rule.onNodeWithTag("service-information").performScrollTo().performClick()
        rule.runOnIdle {assertEquals(s.informationUrl,opened)}
    }
    @Test fun newConfirmedServiceOpensReviewedLoginEntryAndOffersLocalTracking() {
        val s=catalog().services.first {it.id=="dl-income-certificate"};var opened:String?=null;var tracking=false
        rule.setContent {JanSetuTheme {ServiceScreen(s,settings(),{},{},{opened=it},onAddApplication={tracking=true})}}
        rule.onNodeWithTag("service-track").performScrollTo().performClick();rule.runOnIdle {assertTrue(tracking)}
        rule.onNodeWithTag("service-apply").performScrollTo().performClick()
        rule.runOnIdle {assertEquals("https://edistrict.delhigovt.nic.in/Account/Login",opened)}
    }
}
