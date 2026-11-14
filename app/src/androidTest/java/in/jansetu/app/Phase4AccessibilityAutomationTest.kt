package `in`.jansetu.app

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.accessibility.enableAccessibilityChecks
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import `in`.jansetu.app.ui.JanSetuTheme
import org.junit.Rule
import org.junit.Test

/**
 * API 37 automated accessibility smoke check. Manual TalkBack and real-device
 * assessment remain in the Phase 4 checklist because semantics cannot verify
 * every spoken announcement or browser document.
 */
class Phase4AccessibilityAutomationTest {
    @get:Rule val rule = createComposeRule()

    @Test fun labelledInteractiveControlsPassTheAndroidAccessibilityFrameworkGate() {
        // Without this call tryPerformAccessibilityChecks() is a no-op (found in Phase 5).
        rule.enableAccessibilityChecks()
        rule.setContent {
            JanSetuTheme {
                Button(onClick = {}) { Text("Open official information") }
            }
        }
        rule.onNodeWithText("Open official information").performClick()
        rule.onRoot().tryPerformAccessibilityChecks()
    }
}
