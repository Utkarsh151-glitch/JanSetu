package `in`.jansetu.app.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CatalogTest {
    private val bundled = File("src/main/assets/catalog.json").readText()

    @Test fun bundledCatalogHasOnlyAttributedVerifiedHttpsRecords() {
        val catalog = CatalogParser.parse(bundled)
        assertEquals(9, catalog.websites.count { !it.isDirectoryListed })
        assertTrue(catalog.websites.size >= 9)
        assertEquals(catalog.websites.size, catalog.websites.map { it.url }.distinct().size)
        assertTrue(catalog.websites.all { it.sourceUrl.startsWith("https://") })
    }

    @Test fun searchFindsNeedAndCategory() {
        val catalog = CatalogParser.parse(bundled).let { it.copy(websites = it.websites.filter { site -> !site.isDirectoryListed }) }
        assertEquals("cpgrams", CatalogSearch.find(catalog, "file a complaint").first().id)
        assertEquals("digilocker", CatalogSearch.find(catalog, "digital documents").first().id)
        assertEquals(listOf("national-career-service"), CatalogSearch.find(catalog, categoryId = "jobs").map { it.id })
        assertTrue(CatalogSearch.find(catalog, "unlikely-query-123").isEmpty())
    }

    @Test fun parserRejectsUnverifiedAndUnsafeRecords() {
        assertFalse(bundled.contains("http://"))
        val unverified = bundled.replaceFirst(Regex("\"verificationStatus\"\\s*:\\s*\"verified\""), "\"verificationStatus\":\"candidate\"")
        assertTrue(runCatching { CatalogParser.parse(unverified) }.isFailure)
        val insecure = bundled.replaceFirst("https://www.india.gov.in/", "http://www.india.gov.in/")
        assertTrue(runCatching { CatalogParser.parse(insecure) }.isFailure)
        val oddPort = bundled.replaceFirst("https://www.india.gov.in/", "https://www.india.gov.in:8080/")
        assertTrue(runCatching { CatalogParser.parse(oddPort) }.isFailure)
        val uncheckedJavaScript = bundled.replaceFirst("\"browserSupport\": \"in_app\"", "\"browserSupport\": \"external\"")
        assertTrue(runCatching { CatalogParser.parse(uncheckedJavaScript) }.isFailure)
        val duplicateId = bundled.replaceFirst(Regex("\"id\"\\s*:\\s*\"digilocker\""), "\"id\":\"india-portal\"")
        assertTrue(runCatching { CatalogParser.parse(duplicateId) }.isFailure)
    }

    @Test fun searchUsesAllMeaningfulTermsAndSafeFilters() {
        val catalog = CatalogParser.parse(bundled).let { it.copy(websites = it.websites.filter { site -> !site.isDirectoryListed }) }
        assertEquals("cpgrams", CatalogSearch.find(catalog, "file a complainnt").first().id)
        assertEquals("national-career-service", CatalogSearch.find(catalog, "find govt jobs").first().id)
        assertTrue(CatalogSearch.find(catalog, "income certificate").isEmpty())
        assertTrue(CatalogSearch.find(catalog, "land records").isEmpty())
        assertEquals(9, CatalogSearch.find(catalog, level = "Central").size)
        assertTrue(CatalogSearch.find(catalog, level = "State").isEmpty())
        assertEquals("national-scholarship-portal", CatalogSearch.find(catalog, "find scholarships").first().id)
    }

    @Test fun guidesAreSourceBackedAndMissingGuidesAreAllowed() {
        val catalog = CatalogParser.parse(bundled)
        assertEquals(2, catalog.guides.size)
        assertTrue(catalog.guides.all { it.sourceUrl.startsWith("https://") && it.steps.isNotEmpty() })
        assertTrue(catalog.guides.none { it.websiteId == "india-portal" })
        val unsafe = bundled.replaceFirst("https://wb.digilocker.gov.in/web/about/faq", "http://wb.digilocker.gov.in/web/about/faq")
        assertTrue(runCatching { CatalogParser.parse(unsafe) }.isFailure)
    }
}
