package `in`.jansetu.app

import android.database.sqlite.SQLiteDatabase
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.tracker.*
import `in`.jansetu.app.ui.*
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.security.KeyStore
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class TrackerFlowTest {
    @get:Rule val rule=createComposeRule(effectContext=StandardTestDispatcher())
    private val context get()=InstrumentationRegistry.getInstrumentation().targetContext
    private val unique=UUID.randomUUID().toString()
    private val file by lazy {File(context.noBackupFilesDir,"tracker-ui-$unique.db")}
    private val repo by lazy {SqliteApplicationRepository(context,file,KeystoreTrackerCipher("tracker-ui-$unique"))}
    private val tracker by lazy {ApplicationTracker(repo,object:ReminderScheduler {
        override fun available()=false
        override fun schedule(id:String,key:String,at:Instant) {error("Permission denied")}
        override fun cancel(id:String) {}
        override fun cancelAll() {}
    })}
    private fun settings()=LocalSettings(context).apply {updateLanguage("en");updateState("All India");finishOnboarding()}
    private fun start() {
        rule.setContent {JanSetuTheme {JanSetuApp(settings(),applicationTracker=tracker)}}
        rule.waitUntil(30_000) {rule.onAllNodesWithText("What government service do you need?").fetchSemanticsNodes().isNotEmpty()}
    }
    @After fun cleanUp() {
        rule.waitForIdle();repo.close();SQLiteDatabase.deleteDatabase(file)
        KeyStore.getInstance("AndroidKeyStore").apply {load(null);deleteEntry("tracker-ui-$unique")}
    }
    private fun awaitDetails()=rule.waitUntil(15_000) {rule.onAllNodesWithTag("tracker-edit").fetchSemanticsNodes().isNotEmpty()}

    @Test fun manualCreateEditHistoryMaskingValidationAndConfirmedDeleteWork() {
        start();rule.onNodeWithText("Applications",useUnmergedTree=false).performClick()
        rule.waitUntil(10_000) {rule.onAllNodesWithText("No applications yet").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("No applications yet").assertExists()
        rule.onNodeWithTag("tracker-add-manual").performClick()
        rule.waitUntil(10_000) {rule.onAllNodesWithTag("tracker-title").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithTag("tracker-title").performScrollTo().performTextInput("Manual test application")
        rule.onNodeWithTag("tracker-reference").performScrollTo().performTextInput("REF-PRIVATE-77")
        rule.onNodeWithTag("tracker-deadline").performScrollTo().performTextInput("2026-02-30")
        rule.onNodeWithTag("tracker-save").performScrollTo().performClick()
        rule.onAllNodesWithTag("tracker-error").onFirst().assertExists();assertTrue(repo.read().records.isEmpty())
        rule.onNodeWithTag("tracker-deadline").performScrollTo().performTextReplacement(LocalDate.now().plusDays(5).toString())
        rule.onNodeWithTag("tracker-reminder").performScrollTo().performClick()
        rule.onNodeWithText(ReminderState.NOTIFICATIONS_OFF.explanation).assertExists()
        rule.onNodeWithTag("tracker-consent").performScrollTo().performClick()
        rule.onNodeWithTag("tracker-save").performScrollTo().performClick();awaitDetails()
        rule.onNodeWithText("Your recorded status: Planning to apply").assertExists()
        rule.onNodeWithText("Reference: ••••••••").performScrollTo().assertExists()
        rule.onNodeWithText("Show reference").performClick();rule.onNodeWithText("Reference: REF-PRIVATE-77").assertExists()
        assertEquals(1,repo.read().records.size);assertTrue(repo.read().records.single().reminderEnabled)
        rule.onNodeWithTag("tracker-edit").performScrollTo().performClick()
        rule.waitUntil(10_000) {rule.onAllNodesWithTag("tracker-status").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithTag("tracker-status").performScrollTo().performClick();rule.onNodeWithText("Submitted").performClick()
        rule.onNodeWithTag("tracker-submitted").performScrollTo().performTextInput(LocalDate.now().toString())
        rule.onNodeWithTag("tracker-progress-note").performScrollTo().performTextInput("I submitted on the official site")
        rule.onNodeWithTag("tracker-save").performScrollTo().performClick();awaitDetails()
        rule.onNodeWithText("Your recorded status: Submitted").assertExists()
        rule.onNodeWithText("I submitted on the official site").performScrollTo().assertExists();assertEquals(2,repo.read().records.single().history.size)
        rule.onNodeWithTag("tracker-delete").performScrollTo().performClick();rule.onNodeWithText("Cancel").performClick();assertEquals(1,repo.read().records.size)
        rule.onNodeWithTag("tracker-delete").performClick();rule.onNodeWithText("Delete permanently").performClick()
        rule.waitUntil(10_000) {rule.onAllNodesWithText("No applications yet").fetchSemanticsNodes().isNotEmpty()};assertTrue(repo.read().records.isEmpty())
    }

    @Test fun serviceSearchPrefillsOnlyConfirmedFactsAndDoesNotInferSubmission() {
        start();rule.onNodeWithText("Search").performClick()
        rule.onNodeWithText("What government service do you need?").performTextInput("rti central request")
        rule.waitUntil(20_000) {rule.onAllNodesWithTag("service:rti-central-request").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithTag("service:rti-central-request").performScrollTo().performClick()
        rule.onNodeWithTag("service-track").performScrollTo().performClick()
        rule.waitUntil(10_000) {rule.onAllNodesWithTag("tracker-title").fetchSemanticsNodes().isNotEmpty()}
        val catalog=CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use {it.readText()})
        val service=catalog.services.first {it.id=="rti-central-request"}
        rule.onNodeWithTag("tracker-title").assertTextContains(service.title)
        rule.onNodeWithTag("tracker-consent").performScrollTo().performClick()
        rule.onNodeWithTag("tracker-save").performScrollTo().performClick();awaitDetails()
        val record=repo.read().records.single();assertEquals(service.id,record.service?.serviceId)
        assertEquals(service.applicationUrl,record.service?.applicationUrl);assertNull(record.submittedOn);assertEquals(ApplicationStatus.PLANNING,record.status)
        rule.onNodeWithText("Your recorded status: Planning to apply").assertExists()
    }

    @Test fun informationAndAssistedServicePagesNeverOfferApplyOrTrackerShortcut() {
        val catalog=CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use {it.readText()})
        var current by androidx.compose.runtime.mutableStateOf(catalog.services.first {it.applicationMode=="information"})
        rule.setContent {JanSetuTheme {ServiceScreen(current,settings(),{},{},{},{error("Unavailable route")})}}
        rule.onNodeWithTag("service-track").assertDoesNotExist();rule.onNodeWithTag("service-apply").assertDoesNotExist()
        rule.runOnIdle {current=catalog.services.first {it.applicationMode=="assisted"}}
        rule.onNodeWithTag("service-track").assertDoesNotExist();rule.onNodeWithTag("service-apply").assertDoesNotExist()
    }

    @Test fun editorDraftSurvivesSavedStateRestorationWithoutPersisting() {
        val restoration=StateRestorationTester(rule)
        restoration.setContent {JanSetuTheme {ApplicationEditorScreen(tracker,null,null,{}, {})}}
        rule.waitUntil(10_000) {rule.onAllNodesWithTag("tracker-title").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithTag("tracker-title").performTextInput("Rotation draft")
        restoration.emulateSavedInstanceStateRestore()
        rule.waitUntil(10_000) {rule.onAllNodesWithTag("tracker-title").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithTag("tracker-title").assertTextContains("Rotation draft")
        assertTrue(repo.read().records.isEmpty())
    }

    @Test fun removeAllRequiresConfirmationAndDoesNotChangePreferences() {
        repo.save(ApplicationDraft("First saved record"));repo.save(ApplicationDraft("Second saved record"))
        val settings=settings();val favorites=settings.favorites
        rule.setContent {JanSetuTheme {MyApplicationsScreen(settings,tracker,0,{}, {})}}
        rule.waitUntil(10_000) {rule.onAllNodesWithText("First saved record").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithTag("tracker-list").performScrollToNode(hasTestTag("tracker-clear"))
        rule.onNodeWithTag("tracker-clear").performClick();rule.onNodeWithText("Cancel").performClick();assertEquals(2,repo.read().records.size)
        rule.onNodeWithTag("tracker-clear").performClick();rule.onNodeWithText("Remove all",substring=false).performClick()
        rule.waitUntil(10_000) {rule.onAllNodesWithText("No applications yet").fetchSemanticsNodes().isNotEmpty()}
        assertTrue(repo.read().records.isEmpty());assertEquals(favorites,LocalSettings(context).favorites)
    }
}
