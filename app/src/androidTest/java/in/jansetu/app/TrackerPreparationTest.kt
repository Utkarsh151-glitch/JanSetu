package `in`.jansetu.app

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.tracker.*
import `in`.jansetu.app.ui.*
import `in`.jansetu.app.vault.*
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.security.KeyStore
import java.time.Instant
import java.util.UUID

/** Phase 10: service → preparation → document choice → application tracking, without inventing requirements. */
class TrackerPreparationTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>(effectContext = StandardTestDispatcher())
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val unique = UUID.randomUUID().toString()
    private val trackerFile by lazy { File(context.noBackupFilesDir, "tracker-prep-$unique.db") }
    private val repo by lazy { SqliteApplicationRepository(context, trackerFile, KeystoreTrackerCipher("tracker-prep-$unique")) }
    private val tracker by lazy { ApplicationTracker(repo, object : ReminderScheduler {
        override fun available() = false
        override fun schedule(id: String, key: String, at: Instant) {}
        override fun cancel(id: String) {}
        override fun cancelAll() {}
    }) }
    private lateinit var vault: VaultFixture
    private var intents = false
    private val catalog by lazy { CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() }) }
    private fun settings() = LocalSettings(context).apply { updateLanguage("en"); updateState("All India"); finishOnboarding() }

    @Before fun setUp() { vault = VaultFixture(context) }
    @After fun tearDown() {
        rule.waitForIdle(); if (intents) Intents.release()
        repo.close(); SQLiteDatabase.deleteDatabase(trackerFile)
        KeyStore.getInstance("AndroidKeyStore").apply { load(null); deleteEntry("tracker-prep-$unique") }
        vault.close()
    }

    @Test fun checklistOffersTrackingOnlyForConfirmedOnlineRoutes() {
        val confirmed = catalog.services.first { it.hasConfirmedApplicationRoute() && Preparation.requirements(it).hasAny }
        val unconfirmed = catalog.services.first { !it.hasConfirmedApplicationRoute() && Preparation.requirements(it).hasAny }
        var current by mutableStateOf(confirmed)
        var tracked = 0
        rule.setContent { JanSetuTheme { PrepareDocumentsScreen(current, settings(), vault.dependencies, 0, onBack = {}, onLink = {}, onDocuments = {}, onTrack = { tracked++ }) } }
        rule.waitUntil(15_000) { rule.onAllNodesWithTag("prepare-track").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithTag("prepare-track").performScrollTo().performClick()
        rule.runOnIdle { assertEquals(1, tracked) }
        rule.onNodeWithText("This does not submit an application", substring = true).assertExists()
        rule.runOnIdle { current = unconfirmed }
        rule.waitUntil(15_000) { rule.onAllNodesWithText(unconfirmed.title).fetchSemanticsNodes().isNotEmpty() }
        assertTrue(rule.onAllNodesWithTag("prepare-track").fetchSemanticsNodes().isEmpty())
    }

    @Test fun applicationPageShowsTheChecklistSummaryAndChangedRequirements() {
        val service = catalog.services.first { it.hasConfirmedApplicationRoute() && Preparation.requirements(it).required?.isNotEmpty() == true }
        val record = repo.save(ApplicationDraft("Synthetic planned application"), service = service.trackerSnapshot(catalog.version))
        val doc = vault.repository.import(SyntheticDocuments.source(SyntheticDocuments.pdf(1), "x.pdf"), DocumentCategory.CERTIFICATE, "Synthetic proof")
        val requirement = Preparation.requirements(service).items.first()
        vault.repository.choose(Preparation.newChoice(service.id, requirement, doc.id, Instant.now()))
        val opened = mutableListOf<String>()
        var refresh by mutableIntStateOf(0)
        rule.setContent { JanSetuTheme { ApplicationDetailScreen(record.id, tracker, catalog, refresh, onBack = {}, onEdit = {}, onDeleted = {}, onLink = {},
            vault = vault.dependencies, onPrepare = { opened += it }) } }
        rule.waitUntil(15_000) { rule.onAllNodesWithText("You chose files for 1 of", substring = true).fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithTag("tracker-preparation").performScrollTo().assertTextContains("has not checked", substring = true)
        rule.onNodeWithTag("tracker-prepare").performScrollTo().performClick()
        rule.runOnIdle { assertEquals(listOf(service.id), opened) }
        assertTrue(rule.onAllNodesWithText("changed after you chose", substring = true).fetchSemanticsNodes().isEmpty())
        // A choice recorded against wording the service record no longer has is flagged, not reassigned.
        vault.repository.choose(PreparationChoice(UUID.randomUUID().toString(), service.id, "0".repeat(24), "An older wording", doc.id, Instant.now().toString()))
        rule.runOnIdle { refresh++ }
        rule.waitUntil(15_000) { rule.onAllNodesWithText("changed after you chose", substring = true).fetchSemanticsNodes().isNotEmpty() }
        assertEquals(record, repo.get(record.id))
    }

    @Test fun exportWritesTheUsersOwnLabelledRecordsOnlyAfterConfirmation() {
        val target = File(context.cacheDir, "synthetic-export-${System.nanoTime()}.json")
        try {
            repo.save(ApplicationDraft("Synthetic exported application", referenceNumber = "REF-SYNTH-42", notes = "Synthetic note"))
            Intents.init(); intents = true
            Intents.intending(hasAction(Intent.ACTION_CREATE_DOCUMENT)).respondWith(ActivityResult(Activity.RESULT_OK, Intent().setData(Uri.fromFile(target))))
            rule.setContent { JanSetuTheme { MyApplicationsScreen(settings(), tracker, 0, onAdd = {}, onOpen = {}) } }
            rule.waitUntil(15_000) { rule.onAllNodesWithTag("tracker-export").fetchSemanticsNodes().isNotEmpty() }
            rule.onNodeWithTag("tracker-list").performScrollToNode(hasTestTag("tracker-export"))
            rule.onNodeWithTag("tracker-export").performClick()
            rule.onNodeWithText("Cancel").performClick()
            assertFalse(target.exists() && target.length() > 0)
            rule.onNodeWithTag("tracker-export").performClick()
            rule.onNodeWithText("Choose where to save").performClick()
            rule.waitUntil(15_000) { target.exists() && target.length() > 0 }
            val text = target.readText()
            assertTrue(text.contains("Synthetic exported application"))
            assertTrue(text.contains("REF-SYNTH-42"))
            assertTrue(text.contains(ApplicationExport.NOTICE))
            rule.waitUntil(15_000) { rule.onAllNodesWithText("exported where you chose", substring = true).fetchSemanticsNodes().isNotEmpty() }
            assertEquals(1, repo.read().records.size)
        } finally { target.delete() }
    }
}
