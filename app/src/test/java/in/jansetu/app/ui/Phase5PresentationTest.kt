package `in`.jansetu.app.ui

import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.TweenSpec
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.catalog.CitizenSearch
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.settings.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.LocalDate

class Phase5PresentationTest {
    @Test fun everyEditorialHomeShortcutFindsAPublishedServiceInTheBundledCatalog() {
        val catalog = CatalogParser.parse(File("build/generated/runtimeAssets/catalog.json").readText())
        assertEquals(6, HomeTaskShortcuts.size)
        assertEquals(HomeTaskShortcuts.size, HomeTaskShortcuts.map { it.illustration }.distinct().size)
        HomeTaskShortcuts.forEach { shortcut ->
            val results = CitizenSearch.find(catalog, shortcut.query)
            assertTrue("No service for '${shortcut.query}'", results.take(5).any { it.service != null })
            assertTrue(shortcut.titleHi.isNotBlank())
        }
    }

    @Test fun deadlineWordingDistinguishesPassedSoonAndLaterWithoutColour() {
        val today = LocalDate.of(2026, 10, 17)
        assertEquals(DeadlineUrgency.NONE, TrackerPresentation.deadline(null, false, today).urgency)
        assertEquals(DeadlineUrgency.PASSED, TrackerPresentation.deadline("2026-10-16", false, today).urgency)
        assertTrue(TrackerPresentation.deadline("2026-10-16", false, today).text.startsWith("Your deadline passed"))
        assertEquals(DeadlineUrgency.TODAY, TrackerPresentation.deadline("2026-10-17", false, today).urgency)
        assertEquals("Due in 1 day: 18 Oct 2026", TrackerPresentation.deadline("2026-10-18", false, today).text)
        assertEquals(DeadlineUrgency.SOON, TrackerPresentation.deadline("2026-10-24", false, today).urgency)
        assertEquals(DeadlineUrgency.LATER, TrackerPresentation.deadline("2026-10-25", false, today).urgency)
        // A closed application never shows urgency, even for a past date.
        assertEquals(DeadlineUrgency.CLOSED, TrackerPresentation.deadline("2026-10-01", true, today).urgency)
        assertEquals(DeadlineUrgency.NONE, TrackerPresentation.deadline("2026-02-30", false, today).urgency)
    }

    @Test fun reducedMotionReplacesTimedAnimationWithAnImmediateChange() {
        assertTrue(motionSpec<Float>(reduced = true) is SnapSpec)
        assertTrue(motionSpec<Float>(reduced = false) is TweenSpec)
        assertTrue(JanSetuMotion(reduced = true).standard<Float>() is SnapSpec)
    }

    @Test fun largerTextAddsFifteenPercentButNeverExceedsTwiceOrShrinksSystemText() {
        assertEquals(1f, LocalSettings.effectiveFontScale(1f, false), 0.0001f)
        assertEquals(1.15f, LocalSettings.effectiveFontScale(1f, true), 0.0001f)
        assertEquals(2f, LocalSettings.effectiveFontScale(1.8f, true), 0.0001f)
        assertEquals(2f, LocalSettings.effectiveFontScale(2f, true), 0.0001f)
        assertEquals(2.2f, LocalSettings.effectiveFontScale(2.2f, true), 0.0001f)
    }

    @Test fun storedThemeKeysFallBackToSystemDefault() {
        assertEquals(ThemeMode.DARK, ThemeMode.fromKey("dark"))
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromKey("light"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromKey(null))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromKey("sepia"))
    }
}
