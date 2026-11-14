package `in`.jansetu.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.accessibility.enableAccessibilityChecks
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.catalog.profileFor
import `in`.jansetu.app.catalog.servicesFor
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.*
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Rule
import org.junit.Test

/**
 * Runs the Android Accessibility Test Framework over the redesigned screens.
 * Checks are explicitly enabled; without enableAccessibilityChecks() the
 * tryPerformAccessibilityChecks() call does nothing. Automated checks do not
 * replace the manual TalkBack review in PHASE5_MANUAL_CHECKLIST.md.
 */
class Phase5AccessibilityChecksTest {
    @get:Rule val rule = createComposeRule(effectContext = StandardTestDispatcher())
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext.also { check(it.packageName.endsWith(".qa")) }
    private val catalog by lazy { CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() }) }

    private fun settings(): LocalSettings {
        context.getSharedPreferences("jansetu_preferences", 0).edit().clear().commit()
        return LocalSettings(context).apply { finishOnboarding() }
    }

    @After fun cleanup() { context.getSharedPreferences("jansetu_preferences", 0).edit().clear().commit() }

    @Test fun redesignedScreensPassAccessibilityFrameworkChecksInLightAndDark() {
        rule.enableAccessibilityChecks()
        val settings = settings()
        var screen by mutableStateOf("home")
        var dark by mutableStateOf(false)
        val service = catalog.services.first { it.id == "kl-income-certificate" }
        val site = catalog.websites.first { it.id == "digilocker" }
        rule.setContent {
            JanSetuTheme(darkTheme = dark) {
                when (screen) {
                    "home" -> HomeScreen(catalog, settings, onSearch = {}, onCategory = {}, onDetail = {}, onSettings = {}, onBrowse = {}, onApplications = {}, onHelp = {})
                    "search" -> SearchScreen(catalog, settings, "", onDetail = {}, onBrowse = {}, initialQuery = "income certificate")
                    "service" -> ServiceScreen(service, settings, {}, {}, {}, {})
                    "website" -> DetailScreen(site, catalog.guides.firstOrNull { it.websiteId == site.id }, settings, {}, {}, {}, {}, {}, catalog.categories,
                        profile = catalog.profileFor(site.id), services = catalog.servicesFor(site.id))
                    // Phase 6B: a checked profile with services and help links, and a directory-only record with missing states.
                    "website-profile" -> DetailScreen(catalog.websites.first { it.id == "cpgrams" }, null, settings, {}, {}, {}, {}, {}, catalog.categories,
                        profile = catalog.profileFor("cpgrams"), services = catalog.servicesFor("cpgrams"))
                    "website-directory" -> DetailScreen(catalog.websites.first { it.isDirectoryListed && catalog.profileFor(it.id) == null }, null, settings,
                        {}, {}, {}, {}, {}, catalog.categories)
                    "settings" -> SettingsScreen(settings, catalog, onBack = {}, onPrivacy = {}, onSupport = {}, onHelp = {}, notificationsAvailable = { false })
                    "help" -> HelpScreen(settings, catalog, onBack = {}, onGuide = {}, onSearch = {}, onPrivacy = {}, onSupport = {})
                    "onboarding" -> OnboardingScreen(settings) {}
                }
            }
        }
        for (mode in listOf(false, true)) {
            for (name in listOf("home", "search", "service", "website", "website-profile", "website-directory", "settings", "help", "onboarding")) {
                rule.runOnIdle { dark = mode; screen = name }
                if (name == "search") rule.waitUntil(20_000) { rule.onAllNodesWithTag("service:kl-income-certificate").fetchSemanticsNodes().isNotEmpty() }
                rule.onRoot().tryPerformAccessibilityChecks()
            }
        }
    }
}
