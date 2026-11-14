package `in`.jansetu.app.ui

import org.junit.Assert.*
import org.junit.Test

class BrowserTextSizeTest {
    @Test fun websiteTextStartsFromTheAndroidFontSize() {
        assertEquals(100, BrowserTextSize.initial(1.0f))
        assertEquals(130, BrowserTextSize.initial(1.3f))
        assertEquals(200, BrowserTextSize.initial(2.0f))
        // Never smaller than the site's own size, never beyond 200 %.
        assertEquals(100, BrowserTextSize.initial(0.85f))
        assertEquals(200, BrowserTextSize.initial(3.0f))
    }

    @Test fun largerStepsBy25UpTo200() {
        assertEquals(listOf(125, 150, 175, 200, 200), generateSequence(100) { BrowserTextSize.larger(it) }.drop(1).take(5).toList())
        assertEquals(155, BrowserTextSize.larger(130))
    }

    @Test fun readingPositionSurvivesReflowAsAShareOfTheScrollableHeight() {
        assertEquals(0.5f, BrowserTextSize.ratio(500, 1000), 0.0001f)
        assertEquals(0f, BrowserTextSize.ratio(10, 0))
        assertEquals(1f, BrowserTextSize.ratio(5000, 1000))
        // After a font change the page is taller; the reader returns to the same proportion.
        assertEquals(1000, BrowserTextSize.offset(0.5f, 2000))
        assertEquals(0, BrowserTextSize.offset(0.7f, 0))
        assertEquals(2000, BrowserTextSize.offset(1.5f, 2000))
    }
}
