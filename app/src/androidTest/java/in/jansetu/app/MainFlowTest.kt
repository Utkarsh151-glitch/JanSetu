package `in`.jansetu.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.JanSetuTheme
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.StandardTestDispatcher

class MainFlowTest {
    @get:Rule val composeRule = createComposeRule(effectContext = StandardTestDispatcher())

    @Test fun onboardingAndTopLevelNavigationWork() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("jansetu_preferences", 0).edit().clear().commit()
        val settings = LocalSettings(context)
        composeRule.setContent { JanSetuTheme { JanSetuApp(settings) } }

        composeRule.onNodeWithText("Explore JanSetu").performClick()
        composeRule.waitUntil(20_000) {
            composeRule.onAllNodesWithText("What government service do you need?").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("What government service do you need?").assertExists()
        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNodeWithText("Search portals").assertExists()
        composeRule.onNodeWithText("Browse").performClick()
        composeRule.onNodeWithText("Browse by need").assertExists()
        composeRule.onNodeWithText("Saved").performClick()
        composeRule.onNodeWithText("Saved & recent").assertExists()
        composeRule.onNodeWithText("Home").performClick()
        composeRule.onNodeWithText("What government service do you need?").assertExists()
        composeRule.onNodeWithText("Directory listings").performScrollTo().assertExists()
        composeRule.onNodeWithText("Browse directory").performClick()
        composeRule.onNodeWithText("Search portals").assertExists()
        composeRule.onNodeWithText("Home").performClick()
        // Navigation restores Home's scroll position from the directory preview.
        // Return the scroll container to the top before using its Settings icon.
        try {
            composeRule.waitUntil(20_000) {
                composeRule.onAllNodesWithTag("home-content").fetchSemanticsNodes().size == 1
            }
        } catch (error: Throwable) {
            composeRule.onRoot().printToLog("JanSetuNav")
            throw error
        }
        composeRule.onNodeWithTag("home-content").performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, -100_000f) }
        composeRule.waitUntil(20_000) {
            composeRule.onAllNodesWithContentDescription("Settings").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithContentDescription("Settings").performScrollTo().performClick()
        composeRule.onNodeWithText("Privacy policy").performScrollTo().performClick()
        composeRule.onNodeWithText("JanSetu Privacy Policy", substring = true).assertExists()
    }
}
