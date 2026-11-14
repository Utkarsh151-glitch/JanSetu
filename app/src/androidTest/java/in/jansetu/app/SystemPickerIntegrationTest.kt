package `in`.jansetu.app

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Intent
import android.provider.DocumentsContract
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.vault.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Drives Android's real system file picker (DocumentsUI) against the JanSetu provider, as a browser upload
 * field would: the person opens the picker, sees only what they saved, picks one file, and the caller can read
 * that single file. Cancelling returns nothing.
 */
class SystemPickerIntegrationTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext
    private lateinit var fixture: VaultFixture

    /** A picker left open by an earlier run would show stale state; close it before and after each test. */
    private fun closePicker() = listOf("com.google.android.documentsui", "com.android.documentsui").forEach {
        instrumentation.uiAutomation.executeShellCommand("am force-stop $it").close()
    }
    @Before fun setUp() { closePicker(); fixture = VaultFixture(context); VaultDependencies.testOverride = fixture.dependencies }
    @After fun tearDown() { closePicker(); fixture.close() }

    private fun pick(onPicker: () -> Unit): ActivityResult? {
        val latch = CountDownLatch(1)
        var result: ActivityResult? = null
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("*/*")
            .putExtra(DocumentsContract.EXTRA_INITIAL_URI, DocumentsContract.buildRootUri(fixture.picker.authority, PickerGrants.ROOT_ID))
        rule.runOnUiThread {
            rule.activity.activityResultRegistry.register("picker-${System.nanoTime()}", ActivityResultContracts.StartActivityForResult()) {
                result = it; latch.countDown()
            }.launch(intent)
        }
        onPicker()
        assertTrue("The picker returned", latch.await(30, TimeUnit.SECONDS))
        return result
    }

    private fun findText(text: String, timeoutMs: Long = 20_000): AccessibilityNodeInfo? {
        val end = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < end) {
            instrumentation.uiAutomation.windows.mapNotNull { it.root }.plus(listOfNotNull(instrumentation.uiAutomation.rootInActiveWindow))
                .forEach { root -> root.findAccessibilityNodeInfosByText(text).firstOrNull()?.let { return it } }
            Thread.sleep(300)
        }
        return null
    }

    /** DocumentsUI handles touches itself, so tap the node's on-screen position as a person would. */
    private fun tap(node: AccessibilityNodeInfo) {
        val bounds = android.graphics.Rect().also { node.getBoundsInScreen(it) }
        instrumentation.uiAutomation.executeShellCommand("input tap ${bounds.centerX()} ${bounds.centerY()}").close()
    }

    @Test fun theSystemPickerListsSavedDocumentsAndReturnsOnlyTheChosenFile() {
        val png = SyntheticDocuments.png()
        fixture.repository.import(SyntheticDocuments.source(png, "a.png"), DocumentCategory.PHOTO, "Synthetic picker photo")
        fixture.repository.import(SyntheticDocuments.source(SyntheticDocuments.pdf(1), "b.pdf"), DocumentCategory.OTHER, "Synthetic picker form")
        fixture.picker.setEnabled(true)
        val result = pick {
            assertNotNull("JanSetu documents appear in the system picker", findText("Synthetic picker photo"))
            assertNotNull(findText("Synthetic picker form"))
            // In grid view this DocumentsUI version opens a preview on tap; list view chooses the file, as a person would.
            findText("List view", 3_000)?.let { tap(it); Thread.sleep(1_500) }
            tap(requireNotNull(findText("Synthetic picker photo")))
        }
        assertEquals(Activity.RESULT_OK, result?.resultCode)
        val uri = result?.data?.data!!
        assertEquals(fixture.picker.authority, uri.authority)
        assertArrayEquals(png, context.contentResolver.openInputStream(uri)!!.use { it.readBytes() })
        // The returned URI names a time-limited picker ID, never the internal document ID.
        val documentId = DocumentsContract.getDocumentId(uri)
        assertFalse(fixture.repository.snapshot().documents.any { it.id == documentId })
        // Withdrawing access makes that URI unreadable.
        fixture.picker.revokeAll()
        assertTrue(runCatching { context.contentResolver.openInputStream(uri)!!.use { it.readBytes() } }.isFailure)
    }

    @Test fun cancellingThePickerReturnsNothingAndChangesNothing() {
        fixture.repository.import(SyntheticDocuments.source(SyntheticDocuments.png(), "a.png"), DocumentCategory.PHOTO, "Synthetic picker photo")
        fixture.picker.setEnabled(true)
        val result = pick {
            assertNotNull(findText("Synthetic picker photo"))
            instrumentation.uiAutomation.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
        }
        assertEquals(Activity.RESULT_CANCELED, result?.resultCode)
        assertNull(result?.data?.data)
        assertEquals(1, fixture.repository.snapshot().documents.size)
    }
}
