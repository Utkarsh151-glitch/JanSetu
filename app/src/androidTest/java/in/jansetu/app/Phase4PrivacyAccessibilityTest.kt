package `in`.jansetu.app

import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
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
import java.util.UUID

class Phase4PrivacyAccessibilityTest {
    @get:Rule val rule=createComposeRule(effectContext=StandardTestDispatcher())
    private val context get()=InstrumentationRegistry.getInstrumentation().targetContext.also {check(it.packageName.endsWith(".qa"))}
    private val unique=UUID.randomUUID().toString()
    private val alias="phase4-$unique"
    private val file by lazy {File(context.noBackupFilesDir,"phase4-$unique.db")}
    private val cipher by lazy {KeystoreTrackerCipher(alias)}
    private val repo by lazy {SqliteApplicationRepository(context,file,cipher)}
    private val tracker by lazy {ApplicationTracker(repo,object:ReminderScheduler {
        override fun available()=false
        override fun schedule(id:String,key:String,at:Instant)=error("No real reminders in this fixture")
        override fun cancel(id:String) {}
        override fun cancelAll() {}
    })}
    private fun settings()=LocalSettings(context).apply {updateLanguage("en");updateState("All India");finishOnboarding()}
    @After fun cleanup() {
        repo.close();SQLiteDatabase.deleteDatabase(file)
        KeyStore.getInstance("AndroidKeyStore").apply {load(null);deleteEntry(alias)}
    }
    @Test fun lostKeyReadPreservesCiphertextAndDoesNotGenerateAReplacementKey() {
        repo.save(ApplicationDraft("Synthetic key-loss fixture",referenceNumber="QA-KEY-LOSS",notes="QA synthetic only"));repo.close()
        val before=file.readBytes();val store=KeyStore.getInstance("AndroidKeyStore").apply {load(null);deleteEntry(alias)}
        val reopened=SqliteApplicationRepository(context,file,cipher)
        try {
            val snapshot=reopened.read();assertTrue(snapshot.records.isEmpty());assertEquals(1,snapshot.unreadableIds.size)
            assertArrayEquals(before,file.readBytes());assertFalse("Reading ciphertext must not replace a missing key",store.containsAlias(alias))
        } finally {reopened.close()}
    }
    @Test fun shortViewportAtTwoTimesTextKeepsSavedApplicationsReachable() {
        val r=repo.save(ApplicationDraft("Reachable synthetic row"))
        rule.setContent {
            val density=LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density,2f)) {
                JanSetuTheme {Box(Modifier.requiredSize(320.dp,240.dp)) {MyApplicationsScreen(settings(),tracker,0,{}, {})}}
            }
        }
        rule.waitUntil(15_000) {rule.onAllNodesWithText("Loading local applications…").fetchSemanticsNodes().isEmpty()}
        rule.onNodeWithTag("tracker-list").performScrollToNode(hasTestTag("application:${r.id}"))
        rule.onNodeWithTag("application:${r.id}").assertIsDisplayed().performClick()
    }
    @Test fun aSavedSnapshotCannotOfferAnApplicationActionAfterCurrentRouteBecomesUnconfirmed() {
        val c=CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use {it.readText()})
        val service=c.services.first {it.id=="rti-central-request"}
        val r=repo.save(ApplicationDraft("Synthetic retained snapshot"),service=service.trackerSnapshot(c.version))
        val changed=c.copy(services=c.services.map {if(it.id==service.id) it.copy(applicationMode="information",applicationUrl=null) else it})
        rule.setContent {JanSetuTheme {ApplicationDetailScreen(r.id,tracker,changed,0,{},{},{},{error("Unconfirmed action")})}}
        rule.waitUntil(15_000) {rule.onAllNodesWithText("Synthetic retained snapshot").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("Captured official application route").assertDoesNotExist()
        rule.onNodeWithText("Captured official information").performScrollTo().assertExists()
        assertEquals(service.applicationUrl,repo.get(r.id)?.service?.applicationUrl)
    }
}
