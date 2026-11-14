package `in`.jansetu.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.*
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.*
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

/** Phase 6C: how review state and second-batch profiles appear. Uses the isolated QA package only. */
class Phase6cReviewDisplayTest {
    @get:Rule val rule = createComposeRule(effectContext = StandardTestDispatcher())
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext.also { check(it.packageName.endsWith(".qa")) }
    private fun catalog() = CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() })
    private fun settings() = LocalSettings(context).apply { updateLanguage("en"); updateState("All India"); finishOnboarding() }

    private fun show(catalog: Catalog, id: String, profile: WebsiteProfile?) {
        val site = catalog.websites.first { it.id == id }
        rule.setContent {
            JanSetuTheme {
                DetailScreen(site, null, settings(), {}, {}, {}, {}, {}, catalog.categories,
                    profile = profile, services = catalog.servicesFor(id))
            }
        }
    }

    @Test fun automatedProfilesSayNoPersonHasReviewedThem() {
        val catalog = catalog()
        show(catalog, "igod-ddb798b880079969", catalog.profileFor("igod-ddb798b880079969"))
        rule.onNodeWithTag("website-purpose").assertTextContains("co-operative banks", substring = true)
        rule.onNodeWithText("Co-operative banks that disburse social security pensions").performScrollTo().assertExists()
        rule.onNodeWithText("Not stated on the official pages JanSetu checked").performScrollTo().assertExists()
        rule.onNodeWithTag("website-review-status").performScrollTo().assertTextContains("A person has not reviewed it yet", substring = true)
    }

    @Test fun humanReviewShowsItsOwnDateAndIsNeverImplied() {
        val catalog = catalog()
        val profile = requireNotNull(catalog.profileFor("cpgrams")).copy(reviewStatus = "human_reviewed", reviewedOn = "2026-10-18")
        WebsiteProfileValidation.validate(catalog.copy(websiteProfiles = catalog.websiteProfiles.map { if (it.websiteId == "cpgrams") profile else it }))
        show(catalog, "cpgrams", profile)
        rule.onNodeWithTag("website-review-status").performScrollTo()
            .assertTextContains("Reviewed by a person against the official website on Oct 18, 2026", substring = true)
            .assertTextContains("Sources saved on Oct 17, 2026", substring = true)
    }

    @Test fun draftsAndHeldProfilesNeverReachTheApp() {
        val catalog = catalog()
        listOf("igod-421dca3648f50c0b", "igod-6e1f53ab402e424c", "igod-9783d3afd73ca927", "igod-44b4a596b1818cab")
            .forEach { assertNull(catalog.profileFor(it)) }
        show(catalog, "igod-421dca3648f50c0b", null)
        rule.onNodeWithText("Information under review").assertExists()
        rule.onNodeWithTag("website-purpose-missing").performScrollTo().assertTextEquals("Not checked yet")
    }
}
