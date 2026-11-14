package `in`.jansetu.app

import android.database.sqlite.SQLiteDatabase
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.tracker.*
import `in`.jansetu.app.ui.JanSetuTheme
import `in`.jansetu.app.vault.*
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.security.KeyStore
import java.time.Instant
import java.util.UUID

/** The whole citizen journey through real navigation: service → checklist → own document → local application record. */
class PreparationJourneyTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>(effectContext = StandardTestDispatcher())
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val unique = UUID.randomUUID().toString()
    private val trackerFile by lazy { File(context.noBackupFilesDir, "journey-$unique.db") }
    private val repo by lazy { SqliteApplicationRepository(context, trackerFile, KeystoreTrackerCipher("journey-$unique")) }
    private val tracker by lazy { ApplicationTracker(repo, object : ReminderScheduler {
        override fun available() = false
        override fun schedule(id: String, key: String, at: Instant) {}
        override fun cancel(id: String) {}
        override fun cancelAll() {}
    }) }
    private val vault by lazy { VaultFixture(context) }

    @After fun tearDown() {
        rule.waitForIdle(); repo.close(); SQLiteDatabase.deleteDatabase(trackerFile)
        KeyStore.getInstance("AndroidKeyStore").apply { load(null); deleteEntry("journey-$unique") }
        vault.close()
    }

    private fun waitTag(tag: String, timeout: Long = 20_000) = rule.waitUntil(timeout) { rule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }

    @Test fun serviceToChecklistToOwnDocumentToApplicationRecord() {
        val catalog = CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() })
        val service = catalog.services.first { it.id == "dl-income-certificate" }
        assertTrue(service.hasConfirmedApplicationRoute())
        val requirement = Preparation.requirements(service).required!!.first()
        val doc = vault.repository.import(SyntheticDocuments.source(SyntheticDocuments.pdf(1), "x.pdf"), DocumentCategory.ADDRESS, "Synthetic address proof")
        val settings = LocalSettings(context).apply { updateLanguage("en"); updateState("All India"); finishOnboarding() }
        rule.setContent { JanSetuTheme { JanSetuApp(settings, applicationTracker = tracker, documentVault = vault.dependencies) } }
        rule.waitUntil(30_000) { rule.onAllNodesWithText("What government service do you need?").fetchSemanticsNodes().isNotEmpty() }

        rule.onNodeWithText("Search").performClick()
        rule.onNodeWithText("What government service do you need?").performTextInput("Delhi income certificate")
        waitTag("service:${service.id}")
        rule.onNodeWithTag("service:${service.id}").performScrollTo().performClick()
        waitTag("service-prepare")
        rule.onNodeWithTag("service-prepare").performScrollTo().performClick()

        waitTag("requirement-choose:${requirement.key}")
        rule.onNodeWithTag("requirement-choose:${requirement.key}").performScrollTo().performClick()
        waitTag("choose:${doc.id}")
        rule.onNodeWithTag("choose:${doc.id}").performClick()
        rule.waitUntil(15_000) { rule.onAllNodesWithText("You chose: Synthetic address proof").fetchSemanticsNodes().isNotEmpty() }

        rule.onNodeWithTag("prepare-track").performScrollTo().performClick()
        waitTag("tracker-title")
        rule.onNodeWithTag("tracker-title").assertTextContains(service.title)
        rule.onNodeWithTag("tracker-consent").performScrollTo().performClick()
        rule.onNodeWithTag("tracker-save").performScrollTo().performClick()
        waitTag("tracker-edit")
        rule.onNodeWithText("Your recorded status: Planning to apply").assertExists()
        waitTag("tracker-preparation")
        rule.waitUntil(15_000) { rule.onAllNodesWithText("You chose files for 1 of", substring = true).fetchSemanticsNodes().isNotEmpty() }

        val record = repo.read().records.single()
        assertEquals(service.id, record.service?.serviceId)
        assertEquals(ApplicationStatus.PLANNING, record.status)
        assertNull("Choosing documents never records a submission", record.submittedOn)
        assertEquals(listOf(doc.id), vault.repository.choices().map { it.documentId })
        // The two stores stay separate: the tracker record holds no document data, and the vault holds no application data.
        assertFalse(record.toString().contains(doc.id))
        assertEquals(1, vault.repository.snapshot().documents.size)
    }
}
