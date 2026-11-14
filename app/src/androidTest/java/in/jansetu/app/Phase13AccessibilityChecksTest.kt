package `in`.jansetu.app

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.accessibility.enableAccessibilityChecks
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.*
import `in`.jansetu.app.vault.*
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.time.Instant

/**
 * Phase 13: Accessibility Test Framework checks over the screens added in Phases 7–12, in light and dark themes
 * and at 2× font scale. Automated checks find labelling, touch-target and structure problems; they do not replace
 * a person using TalkBack (see the Phase 13 manual checklist).
 */
class Phase13AccessibilityChecksTest {
    @get:Rule val rule = createComposeRule(effectContext = StandardTestDispatcher())
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext.also { check(it.packageName.endsWith(".qa")) }
    private val catalog by lazy { CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() }) }
    private val vault by lazy { VaultFixture(context) }

    @After fun cleanup() { vault.close(); context.getSharedPreferences("jansetu_preferences", 0).edit().clear().commit() }

    @Test fun newScreensPassAccessibilityChecksInBothThemesAndAtLargeText() {
        rule.enableAccessibilityChecks()
        context.getSharedPreferences("jansetu_preferences", 0).edit().clear().commit()
        val settings = LocalSettings(context).apply { finishOnboarding() }
        val service = catalog.services.first { it.id == "dl-income-certificate" }
        val photo = vault.repository.import(SyntheticDocuments.source(SyntheticDocuments.png(), "p.png"), DocumentCategory.PHOTO, "Synthetic photo")
        vault.repository.import(SyntheticDocuments.source(SyntheticDocuments.pdf(1), "c.pdf"), DocumentCategory.INCOME, "Synthetic certificate")
        vault.repository.choose(Preparation.newChoice(service.id, Preparation.requirements(service).items.first(), photo.id, Instant.now()))
        var screen by mutableStateOf("documents")
        var dark by mutableStateOf(false)
        var fontScale by mutableStateOf(1f)
        rule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                JanSetuTheme(darkTheme = dark) {
                    when (screen) {
                        "documents" -> MyDocumentsScreen(settings, vault.dependencies, 0, onOpen = {})
                        "document" -> DocumentDetailScreen(photo.id, vault.dependencies, 0, onBack = {}, onDeleted = {})
                        "prepare" -> PrepareDocumentsScreen(service, settings, vault.dependencies, 0, onBack = {}, onLink = {}, onDocuments = {}, onTrack = {})
                        "service" -> ServiceScreen(service, settings, {}, {}, {}, {}, onPrepare = {})
                        "home" -> HomeScreen(catalog, settings, onSearch = {}, onCategory = {}, onDetail = {}, onSettings = {}, onBrowse = {},
                            onApplications = {}, onHelp = {}, onDocuments = {})
                    }
                }
            }
        }
        for ((mode, scale) in listOf(false to 1f, true to 1f, false to 2f)) {
            for (name in listOf("documents", "document", "prepare", "service", "home")) {
                rule.runOnIdle { dark = mode; fontScale = scale; screen = name }
                when (name) {
                    // At 2× text the document cards are below the first screen of the lazy list; the Add button is
                    // always visible and is enabled only once the vault has loaded.
                    "documents" -> rule.waitUntil(15_000) { rule.onAllNodes(hasTestTag("documents-add") and isEnabled()).fetchSemanticsNodes().isNotEmpty() }
                    "document" -> rule.waitUntil(15_000) { rule.onAllNodesWithTag("document-edit").fetchSemanticsNodes().isNotEmpty() }
                    "prepare" -> rule.waitUntil(15_000) { rule.onAllNodesWithTag("prepare-summary").fetchSemanticsNodes().isNotEmpty() }
                }
                rule.onRoot().tryPerformAccessibilityChecks()
            }
        }
    }
}
