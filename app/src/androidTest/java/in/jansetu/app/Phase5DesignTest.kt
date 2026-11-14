package `in`.jansetu.app

import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.settings.ThemeMode
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

/** Phase 5 branding, settings and motion checks. Runs only in the isolated QA package. */
class Phase5DesignTest {
    @get:Rule val rule = createComposeRule(effectContext = StandardTestDispatcher())
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext.also { check(it.packageName.endsWith(".qa")) }
    private val unique = UUID.randomUUID().toString()
    private val alias = "phase5-$unique"
    private val file by lazy { File(context.noBackupFilesDir, "phase5-$unique.db") }
    private val repo by lazy { SqliteApplicationRepository(context, file, KeystoreTrackerCipher(alias)) }
    private val tracker by lazy { ApplicationTracker(repo, object : ReminderScheduler {
        override fun available() = false
        override fun schedule(id: String, key: String, at: Instant) = error("No reminders in this fixture")
        override fun cancel(id: String) {}
        override fun cancelAll() {}
    }) }
    private var usedTracker = false

    private fun freshSettings(): LocalSettings {
        context.getSharedPreferences("jansetu_preferences", 0).edit().clear().commit()
        return LocalSettings(context).apply { finishOnboarding() }
    }

    @After fun cleanup() {
        if (usedTracker) {
            repo.close(); SQLiteDatabase.deleteDatabase(file)
            KeyStore.getInstance("AndroidKeyStore").apply { load(null); deleteEntry(alias) }
        }
        context.getSharedPreferences("jansetu_preferences", 0).edit().clear().commit()
    }

    @Test fun settingsControlsShowStateChangeBehaviourAndPersist() {
        val settings = freshSettings()
        val systemScale = context.resources.configuration.fontScale
        rule.setContent {
            JanSetuTheme(settings) {
                Column {
                    Text(if (JanSetuTheme.isDark) "probe:dark" else "probe:light")
                    Text(if (JanSetuTheme.motion.reduced) "probe:reduced" else "probe:full")
                    Text("probe:scale:${LocalDensity.current.fontScale}", Modifier.testTag("scale-probe"))
                    Text("probe:bg:${MaterialTheme.colorScheme.background.toArgb()}", Modifier.testTag("bg-probe"))
                    Box(Modifier.weight(1f)) {
                        SettingsScreen(settings, null, onBack = {}, onPrivacy = {}, onSupport = {}, notificationsAvailable = { false })
                    }
                }
            }
        }
        rule.onNodeWithTag("theme:system").assertIsSelected()
        rule.onNodeWithText("probe:light").assertExists()
        val lightBackground = rule.onNodeWithTag("bg-probe").fetchSemanticsNode().config[SemanticsProperties.Text].first().text

        rule.onNodeWithTag("theme:dark").performScrollTo().performClick()
        rule.onNodeWithTag("theme:dark").assertIsSelected(); rule.onNodeWithTag("theme:light").assertIsNotSelected()
        rule.onNodeWithText("probe:dark").assertExists()
        assertNotEquals(lightBackground, rule.onNodeWithTag("bg-probe").fetchSemanticsNode().config[SemanticsProperties.Text].first().text)

        rule.onNodeWithTag("setting:reduce-motion").performScrollTo().assertIsOff().performClick()
        rule.onNodeWithTag("setting:reduce-motion").assertIsOn()
        rule.onNodeWithText("probe:reduced").assertExists()

        rule.onNodeWithTag("setting:larger-text").performScrollTo().assertIsOff().performClick()
        rule.onNodeWithTag("setting:larger-text").assertIsOn()
        rule.onNodeWithText("probe:scale:${LocalSettings.effectiveFontScale(systemScale, true)}").assertExists()

        rule.onNodeWithTag("language:hi").performScrollTo().performClick()
        rule.onNodeWithTag("language:hi").assertIsSelected()
        rule.onNodeWithText("सेटिंग्स").assertExists()

        // A new instance reads only what was stored, as after an app restart.
        val reopened = LocalSettings(context)
        assertEquals(ThemeMode.DARK, reopened.themeMode)
        assertTrue(reopened.reduceMotion)
        assertTrue(reopened.largerText)
        assertEquals("hi", reopened.language)

        rule.onNodeWithTag("theme:light").performScrollTo().performClick()
        rule.onNodeWithText("probe:light").assertExists()
        assertEquals(ThemeMode.LIGHT, LocalSettings(context).themeMode)
    }

    @Test fun changingSettingsDoesNotTouchTrackerRecordsOrHistory() {
        usedTracker = true
        val saved = repo.save(ApplicationDraft("Synthetic phase 5 record", notes = "QA synthetic only", progressNote = "Synthetic progress"))
        val before = repo.get(saved.id)
        val settings = freshSettings()
        settings.updateThemeMode(ThemeMode.DARK); settings.updateReduceMotion(true); settings.updateLargerText(true); settings.updateLanguage("hi")
        settings.updateThemeMode(ThemeMode.SYSTEM); settings.updateLanguage("en")
        assertEquals(before, repo.get(saved.id))
        assertEquals(1, repo.read().records.size)
        assertEquals(before?.history, repo.read().records.single().history)
    }

    @Test fun decorativeArtIsHiddenFromScreenReadersButInformativeArtIsDescribed() {
        val catalog = CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() })
        val settings = freshSettings()
        rule.setContent {
            JanSetuTheme {
                Column {
                    JanSetuIllustration(Illustration.HELP, Modifier.size(40.dp).testTag("informative"), contentDescription = "Signpost")
                    HomeScreen(catalog, settings, onSearch = {}, onCategory = {}, onDetail = {}, onSettings = {}, onBrowse = {}, onApplications = {}, onHelp = {})
                }
            }
        }
        val hidden = SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription) and
            SemanticsMatcher.keyNotDefined(SemanticsProperties.Role)
        rule.onNodeWithTag("illustration:hero").assert(hidden)
        rule.onAllNodesWithTag("jansetu-logo").onFirst().assert(hidden)
        rule.onAllNodesWithTag("illustration:certificates", useUnmergedTree = true).onFirst().assert(hidden)
        rule.onNodeWithContentDescription("Signpost").assertExists()
        // The brand name is real text, read once as a heading.
        rule.onNodeWithText("JanSetu").assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
    }

    @Test fun logoAndIllustrationsRenderVisiblyInLightAndDarkThemes() {
        var dark by mutableStateOf(false)
        rule.setContent {
            JanSetuTheme(darkTheme = dark) {
                Column {
                    JanSetuLogoMark(size = 48.dp)
                    Illustration.entries.filter { it != Illustration.HERO }.forEach { JanSetuIllustration(it, Modifier.size(64.dp)) }
                    JanSetuIllustration(Illustration.HERO, Modifier.size(160.dp, 75.dp))
                }
            }
        }
        for (mode in listOf(false, true)) {
            rule.runOnIdle { dark = mode }
            val tags = listOf("jansetu-logo") + Illustration.entries.map { "illustration:${it.name.lowercase()}" }
            tags.forEach { tag ->
                val pixels = rule.onNodeWithTag(tag).captureToImage().toPixelMap()
                val colours = (0 until pixels.width step 2).flatMap { x -> (0 until pixels.height step 2).map { y -> pixels[x, y].toArgb() } }.toSet()
                assertTrue("$tag drew too few colours in dark=$mode", colours.size >= 3)
            }
        }
    }

    @Test fun reducedMotionMakesExpandableSectionsChangeImmediately() {
        assertEquals(true, expandedHeightAfterThreeFrames(reduced = true).let { (early, settled) -> early == settled })
    }

    @Test fun fullMotionAnimatesExpandableSectionsOverSeveralFrames() {
        assertEquals(true, expandedHeightAfterThreeFrames(reduced = false).let { (early, settled) -> early < settled })
    }

    private fun expandedHeightAfterThreeFrames(reduced: Boolean): Pair<Int, Int> {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            JanSetuTheme(reducedMotion = reduced) {
                Box(Modifier.testTag("section-box")) {
                    JanSetuExpandableSection("Evidence", "3 sources", "Show", "Hide", testTag = "section-toggle") {
                        repeat(6) { Text("Source line $it") }
                    }
                }
            }
        }
        rule.mainClock.advanceTimeBy(1_000)
        val collapsed = rule.onNodeWithTag("section-box").fetchSemanticsNode().size.height
        rule.onNodeWithTag("section-toggle").performClick()
        repeat(3) { rule.mainClock.advanceTimeByFrame() }
        val early = rule.onNodeWithTag("section-box").fetchSemanticsNode().size.height
        rule.mainClock.advanceTimeBy(2_000)
        val settled = rule.onNodeWithTag("section-box").fetchSemanticsNode().size.height
        assertTrue("Section did not expand", settled > collapsed)
        return early to settled
    }
}
