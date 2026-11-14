package `in`.jansetu.app

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.*
import `in`.jansetu.app.vault.*
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class DocumentsFlowTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>(effectContext = StandardTestDispatcher())
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var fixture: VaultFixture
    private var intents = false
    private val catalog by lazy { CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() }) }
    private fun settings() = LocalSettings(context).apply { updateLanguage("en"); updateState("All India"); finishOnboarding() }
    private fun secure() = rule.activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE != 0
    private fun waitText(text: String, timeout: Long = 15_000) = rule.waitUntil(timeout) { rule.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty() }
    private fun waitTag(tag: String, timeout: Long = 15_000) = rule.waitUntil(timeout) { rule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }

    @Before fun setUp() { fixture = VaultFixture(context) }
    @After fun tearDown() { rule.waitForIdle(); if (intents) Intents.release(); fixture.close() }

    @Test fun importSearchPreviewRenameAndDeleteThroughTheScreens() {
        // A synthetic file returned by a stubbed system picker; JanSetu reads it once and keeps only an encrypted copy.
        val source = File(context.cacheDir, "synthetic-import-${System.nanoTime()}.png").apply { writeBytes(SyntheticDocuments.png()) }
        try {
            Intents.init(); intents = true
            Intents.intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(ActivityResult(Activity.RESULT_OK, Intent().setData(Uri.fromFile(source))))
            rule.setContent { JanSetuTheme { JanSetuApp(settings(), documentVault = fixture.dependencies) } }
            rule.waitUntil(30_000) { rule.onAllNodesWithTag("home-documents").fetchSemanticsNodes().isNotEmpty() }
            assertFalse(secure())
            rule.onNodeWithTag("home-documents").performScrollTo().performClick()
            waitText("No documents saved yet")
            assertTrue("My Documents is hidden from screenshots", secure())
            rule.onNodeWithText("JanSetu never uploads, sends or submits your documents", substring = true).assertExists()

            rule.onNodeWithTag("documents-add").performClick()
            waitTag("document-name")
            rule.onNodeWithTag("document-name").performTextReplacement("Synthetic photo")
            rule.onNodeWithTag("document-category:PHOTO").performScrollTo().performClick()
            rule.onNodeWithTag("document-confirm").performClick()
            waitText("Saved and encrypted on this device.", 30_000)
            assertEquals(listOf("Synthetic photo"), fixture.repository.snapshot().documents.map { it.name })
            assertEquals(DocumentCategory.PHOTO, fixture.repository.snapshot().documents.single().category)
            assertTrue("The picked file itself is unchanged", source.exists())

            rule.onNodeWithTag("documents-search").performTextInput("no-such-document")
            waitText("No documents match")
            rule.onNodeWithContentDescription("Clear search").performClick()
            val id = fixture.repository.snapshot().documents.single().id
            waitTag("document:$id")
            rule.onNodeWithTag("document:$id").performScrollTo().performClick()
            waitTag("document-edit")
            assertTrue(secure())
            rule.onNodeWithTag("document-show").performScrollTo().performClick()
            waitTag("document-preview", 30_000)

            rule.onNodeWithTag("document-edit").performScrollTo().performClick()
            waitTag("document-name")
            rule.onNodeWithTag("document-name").performTextReplacement("bad/name")
            rule.onNodeWithTag("document-confirm").assertIsNotEnabled()
            rule.onNodeWithTag("document-name").performTextReplacement("Renamed photo")
            rule.onNodeWithTag("document-confirm").performClick()
            waitText("Changes saved.")
            assertEquals("Renamed photo", fixture.repository.get(id)?.name)

            rule.onNodeWithTag("document-delete").performScrollTo().performClick()
            rule.onNodeWithText("Cancel").performClick()
            assertNotNull(fixture.repository.get(id))
            rule.onNodeWithTag("document-delete").performScrollTo().performClick()
            rule.onNodeWithText("Delete permanently").performClick()
            waitText("Document deleted from this device.")
            assertTrue(fixture.repository.snapshot().documents.isEmpty())
        } finally { source.delete() }
    }

    @Test fun rejectedFilesExplainWhyAndSaveNothing() {
        val source = File(context.cacheDir, "synthetic-reject-${System.nanoTime()}.pdf").apply { writeBytes("not a document".toByteArray()) }
        try {
            Intents.init(); intents = true
            Intents.intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(ActivityResult(Activity.RESULT_OK, Intent().setData(Uri.fromFile(source))))
            rule.setContent { JanSetuTheme { MyDocumentsScreen(settings(), fixture.dependencies, 0, onOpen = {}) } }
            waitTag("documents-add")
            rule.onNodeWithTag("documents-add").performClick()
            waitTag("document-confirm")
            rule.onNodeWithTag("document-confirm").performClick()
            waitTag("document-error", 30_000)
            rule.onNodeWithText(ImportProblem.UNSUPPORTED.message).assertExists()
            assertEquals(0, fixture.repository.snapshot().total)
            rule.onNodeWithText("Cancel").performClick()
            // Cancelling the system picker changes nothing.
            Intents.release(); Intents.init()
            Intents.intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(ActivityResult(Activity.RESULT_CANCELED, null))
            rule.onNodeWithTag("documents-add").performClick()
            rule.waitForIdle()
            assertTrue(rule.onAllNodesWithTag("document-confirm").fetchSemanticsNodes().isEmpty())
            assertEquals(0, fixture.repository.snapshot().total)
        } finally { source.delete() }
    }

    @Test fun preparationShowsOnlyRecordedRequirementsAndNeverClaimsTheyAreMet() {
        val service = catalog.services.first { it.documents != null && it.conditionalDocuments == null }
        val requirement = Preparation.requirements(service).required!!.first()
        val doc = fixture.repository.import(SyntheticDocuments.source(SyntheticDocuments.pdf(1), "x.pdf"), DocumentCategory.CERTIFICATE, "Synthetic certificate")
        val opened = mutableListOf<String>()
        rule.setContent { JanSetuTheme { PrepareDocumentsScreen(service, settings(), fixture.dependencies, 0, onBack = {}, onLink = { opened += it }, onDocuments = {}) } }
        waitTag("prepare-summary")
        assertTrue(secure())
        rule.onNodeWithText(requirement.text).assertExists()
        rule.onNodeWithText("Listed as required").assertExists()
        rule.onNodeWithText("No conditional documents are confirmed", substring = true).performScrollTo().assertExists()
        rule.onNodeWithText("You chose files for 0 of", substring = true).assertExists()
        rule.onNodeWithTag("requirement-choose:${requirement.key}").performScrollTo().performClick()
        waitTag("choose:${doc.id}")
        rule.onNodeWithTag("choose:${doc.id}").performClick()
        waitText("You chose: Synthetic certificate")
        rule.onNodeWithText("You chose files for 1 of", substring = true).assertExists()
        rule.onAllNodesWithText("JanSetu has not checked whether this file meets the requirement.", substring = true).onFirst().assertExists()
        listOf("Requirement met", "Requirements met", "Satisfied", "Ready to submit", "Verified document").forEach {
            assertTrue(it, rule.onAllNodesWithText(it, substring = true, ignoreCase = true).fetchSemanticsNodes().isEmpty())
        }
        assertEquals(listOf(requirement.key), fixture.repository.choices().map { it.requirementKey })
        rule.onNodeWithText("Remove choice").performScrollTo().performClick()
        waitText("You chose files for 0 of")
        assertTrue(fixture.repository.choices().isEmpty())
        assertTrue("Nothing was opened or uploaded", opened.isEmpty())
    }

    @Test fun servicePagesOfferPreparationOnlyWhenRequirementsAreRecorded() {
        val withDocuments = catalog.services.first { it.documents != null }
        val withoutDocuments = catalog.services.first { it.documents == null && it.conditionalDocuments == null }
        var current by mutableStateOf(withDocuments)
        rule.setContent { JanSetuTheme { ServiceScreen(current, settings(), onBack = {}, onWebsite = {}, onLink = {}, onPrepare = {}) } }
        rule.onNodeWithTag("service-prepare").performScrollTo().assertExists()
        rule.runOnIdle { current = withoutDocuments }
        rule.waitForIdle()
        assertTrue(rule.onAllNodesWithTag("service-prepare").fetchSemanticsNodes().isEmpty())
    }
}
