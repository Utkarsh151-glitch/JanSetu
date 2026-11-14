package `in`.jansetu.app

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.JanSetuTheme
import kotlinx.coroutines.test.StandardTestDispatcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CitizenJourneysTest {
    @get:Rule val rule=createComposeRule(effectContext=StandardTestDispatcher())
    private var intents=false
    private fun start() {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        check(context.packageName.endsWith(".qa"))
        context.getSharedPreferences("jansetu_preferences",0).edit().clear().commit()
        val settings=LocalSettings(context).apply {updateLanguage("en");updateState("All India");finishOnboarding()}
        rule.setContent {JanSetuTheme {JanSetuApp(settings)}}
        rule.waitUntil(30_000) {rule.onAllNodesWithText("What government service do you need?").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("Search").performClick()
    }
    @After fun cleanup() {if(intents) Intents.release()}
    @Test fun misspelledPortalQueryFiltersDetailsOfficialHandoffAndBackKeepContext() {
        Intents.init();intents=true
        Intents.intending(hasAction(Intent.ACTION_VIEW)).respondWith(ActivityResult(Activity.RESULT_OK,null))
        start()
        rule.onNodeWithText("What government service do you need?").performTextInput("Digilockr")
        rule.onNodeWithText("Services and websites").performClick();rule.onNodeWithText("Websites only").performClick()
        rule.onNodeWithText("All sectors").performClick();rule.onNodeWithText("Documents & identity").performScrollTo().performClick()
        rule.onNodeWithText("All levels").performClick();rule.onAllNodesWithText("Central",substring=false).onLast().performClick()
        rule.waitUntil(20_000) {rule.onAllNodesWithText("www.digilocker.gov.in",substring=true).fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("www.digilocker.gov.in",substring=true).performScrollTo().performClick()
        rule.onNodeWithText("Open official website").performScrollTo().performClick()
        Intents.intended(allOf(hasAction(Intent.ACTION_VIEW),hasData("https://www.digilocker.gov.in/")))
        rule.onNodeWithContentDescription("Back").performScrollTo().performClick()
        rule.onNodeWithText("What government service do you need?").assertTextContains("Digilockr")
        rule.onNodeWithText("Websites only").assertExists();rule.onNodeWithText("Documents & identity").assertExists()
    }
    @Test fun regionalServiceSourcesUnknownsAndUnsupportedStateKeepSearchHonest() {
        start();rule.onNodeWithText("What government service do you need?").performTextInput("income certificate")
        rule.onNodeWithText("Services and websites").performClick();rule.onNodeWithText("Verified services",substring=false).performClick()
        rule.onNodeWithTag("filter:All states / UTs").performClick();rule.onNodeWithText("Kerala",substring=false).performScrollTo().performClick()
        rule.waitUntil(20_000) {rule.onAllNodesWithTag("service:kl-income-certificate").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithTag("service:kl-income-certificate").performScrollTo().performClick()
        rule.onNodeWithText("Who may apply").performScrollTo().assertExists()
        rule.onAllNodesWithText("Not verified in this pilot. Check the official information page.").onFirst().assertExists()
        rule.onNodeWithText("Evidence and review").performScrollTo().assertExists()
        rule.onNodeWithContentDescription("Back").performScrollTo().performClick()
        rule.onNodeWithText("What government service do you need?").assertTextContains("income certificate")
        rule.onNodeWithTag("filter:All states / UTs").performClick();rule.onNodeWithText("Bihar",substring=false).performScrollTo().performClick()
        rule.waitUntil(20_000) {rule.onAllNodesWithText("No matching portals").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithTag("service:kl-income-certificate").assertDoesNotExist()
    }
}
