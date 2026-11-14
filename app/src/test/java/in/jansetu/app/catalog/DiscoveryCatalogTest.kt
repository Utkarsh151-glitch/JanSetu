package `in`.jansetu.app.catalog

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.system.measureNanoTime

class DiscoveryCatalogTest {
    private val content = File("src/main/assets/catalog.json").readText()

    @Test fun realExpandedCatalogSupportsAliasesAndCombinedRegionalFilters() {
        val catalog = CatalogParser.parse(content)
        assertTrue(catalog.websites.size > 100)
        val district = catalog.websites.first { it.isDirectoryListed && it.state != null && it.district != null }
        val results = CatalogSearch.find(catalog, state = district.state, district = district.district, level = district.level)
        assertTrue(results.any { it.id == district.id })
        assertTrue(results.all { it.state == district.state && it.district == district.district && it.level == district.level })
        assertTrue(CatalogSearch.find(catalog, district.aliases.first()).any { it.id == district.id })
        assertTrue(CatalogSearch.find(catalog, state = "Not a real state").isEmpty())
        assertTrue(catalog.services.isEmpty())
    }

    @Test fun directoryEvidenceCannotMasqueradeAsReviewedOrPubliclyLicensedData() {
        val full = CatalogParser.parse(content)
        val site = full.websites.first { it.isDirectoryListed }
        val single = full.copy(websites = listOf(site), guides = emptyList(), services = emptyList())
        assertEquals(1, CatalogParser.parse(Json.encodeToString(single)).websites.size)
        for (bad in listOf(site.copy(sourceEvidence = emptyList()), site.copy(healthStatus = "NOT_CHECKED"),
            site.copy(publicationScope = "public"), site.copy(sourceId = "untrusted"))) {
            assertTrue(runCatching { CatalogParser.parse(Json.encodeToString(single.copy(websites = listOf(bad)))) }.isFailure)
        }
    }

    @Test fun actualCatalogParseIndexAndRepeatedSearchAreMeasured() {
        lateinit var catalog: Catalog
        val parseMs = measureNanoTime { catalog = CatalogParser.parse(content) } / 1_000_000.0
        val indexMs = measureNanoTime { CatalogSearch.prepare(catalog) } / 1_000_000.0
        val queries = listOf("DigiLocker", "Madhya Pradesh", "health", "Delhi", "National Scholarship Portal", "not-a-real-portal-991299")
        val searchMs = measureNanoTime { repeat(5) { queries.forEach { CatalogSearch.find(catalog, it) } } } / 1_000_000.0
        println("REAL_CATALOG_METRICS websites=${catalog.websites.size} bytes=${content.toByteArray().size} parseMs=$parseMs indexMs=$indexMs thirtySearchesMs=$searchMs")
        assertTrue(CatalogSearch.find(catalog, "not-a-real-portal-991299").isEmpty())
    }

    @Test fun newerInconclusiveCheckStaysSeparateFromEarlierAdmissionEvidence() {
        val full = CatalogParser.parse(content)
        val site = full.websites.first { it.isDirectoryListed }
        val uncertain = site.copy(latestHealthStatus = "SERVER_ERROR", latestHealthCheckedAt = "2026-10-15T12:00:00Z")
        val parsed = CatalogParser.parse(Json.encodeToString(full.copy(websites = listOf(uncertain), guides = emptyList())))
        assertTrue(parsed.websites.single().hasInconclusiveLatestCheck)
        assertEquals("RESPONDING", parsed.websites.single().healthStatus)
        assertEquals(site.healthCheckedAt, parsed.websites.single().healthCheckedAt)
        assertTrue(!uncertain.copy(latestHealthStatus = "RESPONDING").hasInconclusiveLatestCheck)
    }

    @Test fun categoryAndJurisdictionFiltersHaveUniqueExactUrlResults() {
        val catalog = CatalogParser.parse(content)
        catalog.categories.forEach { category ->
            val results = CatalogSearch.find(catalog, categoryId = category.id)
            assertTrue(results.all { category.id in it.categories })
            assertEquals(results.size, results.map { it.url }.distinct().size)
        }
        val unknown = catalog.websites.firstOrNull { it.isDirectoryListed && it.state == null }
        if (unknown != null) assertTrue(CatalogSearch.find(catalog, state = "Goa").none { it.id == unknown.id })
    }
}
