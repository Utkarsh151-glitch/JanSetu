package `in`.jansetu.app.catalog

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Serializable
data class Category(
    val id: String,
    val title: String,
    val titleHi: String,
    val symbol: String,
    val parentId: String? = null
)

@Serializable data class CategoryGroup(val id: String, val title: String, val titleHi: String)

@Serializable
data class Website(
    val id: String,
    val title: String,
    val description: String,
    val url: String,
    val organization: String,
    val level: String,
    val categories: List<String>,
    val keywords: List<String>,
    val sourceUrl: String,
    val sourceType: String,
    val verificationStatus: String,
    val verifiedOn: String,
    val parentOrganization: String? = null,
    val jurisdiction: String? = null,
    val state: String? = null,
    val languages: List<String> = emptyList(),
    val useCases: List<String> = emptyList(),
    val knownLimitations: String? = null,
    val browserSupport: String = "external",
    val browserJavaScript: Boolean = false,
    val aliases: List<String> = emptyList(),
    val district: String? = null,
    val primarySector: String? = null,
    val secondarySectors: List<String> = emptyList(),
    val sourceId: String? = null,
    val sourceRetrievedOn: String = "",
    val healthStatus: String? = null,
    val healthCheckedAt: String? = null,
    val latestHealthStatus: String? = null,
    val latestHealthCheckedAt: String? = null,
    val observedFinalUrl: String? = null,
    val publicationScope: String? = null,
    val ownershipConfidence: String? = null,
    val sourceEvidence: List<DirectoryEvidence> = emptyList(),
    val descriptionHi: String? = null
) {
    val domain: String get() = URI(url).host.orEmpty()
    val isDirectoryListed: Boolean get() = verificationStatus == "directory_listed"
    val hasInconclusiveLatestCheck: Boolean get() = latestHealthStatus != null && latestHealthStatus != "RESPONDING"
}

@Serializable
data class DirectoryEvidence(
    val sourceId: String,
    val sourcePage: String,
    val originalUrl: String,
    val retrievedAt: String
)

@Serializable
data class Guide(
    val websiteId: String,
    val title: String,
    val steps: List<String>,
    val sourceUrl: String,
    val reviewedOn: String,
    val note: String = "",
    val titleHi: String? = null,
    val stepsHi: List<String> = emptyList(),
    val noteHi: String? = null
)

@Serializable
data class Catalog(
    val version: Int,
    val categories: List<Category>,
    val websites: List<Website>,
    val services: List<Service> = emptyList(),
    val guides: List<Guide> = emptyList(),
    val categoryGroups: List<CategoryGroup> = emptyList(),
    val serviceOrganizations: List<ServiceOrganization> = emptyList(),
    val websiteProfiles: List<WebsiteProfile> = emptyList()
)

/** Rejects unpublished or unattributed data instead of quietly displaying it as official. */
object CatalogParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(content: String): Catalog {
        val catalog = json.decodeFromString<Catalog>(content)
        require(catalog.version > 0)
        require(catalog.categories.map { it.id }.distinct().size == catalog.categories.size)
        val groupIds = catalog.categoryGroups.map { it.id }.toSet()
        require(groupIds.size == catalog.categoryGroups.size)
        require(catalog.categories.all { it.parentId == null || it.parentId in groupIds })
        require(catalog.websites.map { it.id }.distinct().size == catalog.websites.size)
        require(catalog.websites.map { URI(it.url).normalize().toString().removePrefix("https://www.").removePrefix("https://") }.distinct().size == catalog.websites.size)
        val categoryIds = catalog.categories.map { it.id }.toSet()
        catalog.websites.forEach { website ->
            require(website.id.isNotBlank() && website.title.isNotBlank())
            require(website.verificationStatus in setOf("verified", "directory_listed"))
            require(website.browserSupport in setOf("external", "in_app"))
            require(!website.browserJavaScript || website.browserSupport == "in_app")
            if (website.browserSupport == "in_app") {
                require(website.id == "india-portal" && URI(website.url).host == "www.india.gov.in")
            }
            require(website.sourceType.isNotBlank())
            if (website.isDirectoryListed) {
                require(website.publicationScope == "local_development" && website.sourceId == "igod")
                require(website.sourceRetrievedOn.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
                require(website.healthStatus == "RESPONDING" && !website.healthCheckedAt.isNullOrBlank())
                require(website.browserSupport == "external" && !website.browserJavaScript)
                require(website.sourceEvidence.isNotEmpty() && website.sourceEvidence.all {
                    it.sourceId == "igod" && isHttps(it.sourcePage) && URI(it.sourcePage).host == "igod.gov.in" &&
                        isHttps(it.originalUrl) && it.retrievedAt.isNotBlank()
                })
            } else {
                require(website.verifiedOn.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
            }
            require(isHttps(website.url) && isHttps(website.sourceUrl))
            require(website.categories.isNotEmpty() && website.categories.all { it in categoryIds })
        }
        val websiteIds = catalog.websites.map { it.id }.toSet()
        ServiceValidation.validate(catalog)
        WebsiteProfileValidation.validate(catalog)
        require(catalog.guides.map { it.websiteId }.distinct().size == catalog.guides.size)
        catalog.guides.forEach { guide ->
            require(guide.websiteId in websiteIds && guide.title.isNotBlank())
            require(guide.steps.isNotEmpty() && guide.steps.all { it.isNotBlank() })
            require(guide.stepsHi.isEmpty() || guide.stepsHi.size == guide.steps.size && guide.stepsHi.all { it.isNotBlank() })
            require(isHttps(guide.sourceUrl))
            require(guide.reviewedOn.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
        }
        return catalog
    }

    private fun isHttps(value: String): Boolean = try {
        val uri = URI(value)
        uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank() &&
            uri.userInfo == null && uri.port in listOf(-1, 443)
    } catch (_: Exception) { false }
}

interface CatalogRepository {
    suspend fun load(): Catalog
}

class BundledCatalogRepository(private val context: Context) : CatalogRepository {
    override suspend fun load(): Catalog = withContext(Dispatchers.IO) {
        val content = context.assets.open("catalog.json").bufferedReader().use { it.readText() }
        CatalogParser.parse(content).also { CitizenSearch.prepare(it) }
    }
}

object CatalogSearch {
    private val words = Regex("[\\p{L}\\p{M}\\p{N}]+")
    private val stopWords = setOf("i", "a", "an", "the", "to", "for", "need", "find", "show", "apply", "check", "file", "get", "my", "in", "me", "how", "can", "do", "want", "obtain", "please", "would", "like", "is", "are")
    private val synonyms = mapOf(
        "govt" to "government", "naukri" to "jobs", "job" to "jobs",
        "complain" to "complaint", "complaints" to "complaint", "grievances" to "grievance",
        "docs" to "documents", "document" to "documents", "certificate" to "certificates",
        "scholarships" to "scholarship", "pensions" to "pension", "caste" to "community",
        "engineering" to "technical", "businesses" to "business",
        "पेंशन" to "pension", "जन्म" to "birth", "छात्रवृत्ति" to "scholarship", "जाति" to "community", "नौकरी" to "jobs"
    )

    /**
     * [boost] lifts records with checked information (reviewed records and published profiles) over
     * unchecked listings: a checked keyword match (6 + 3) outranks an unchecked title match (8).
     * It never removes a result.
     */
    private data class Indexed(val site: Website, val fields: List<Pair<Set<String>, Int>>, val boost: Int = 0)
    private const val CHECKED_BOOST = 3
    private var indexedCatalog: Catalog? = null
    private var entries: List<Indexed> = emptyList()

    fun prepare(catalog: Catalog) { index(catalog) }

    @Synchronized
    private fun index(catalog: Catalog): List<Indexed> {
        if (indexedCatalog !== catalog) {
            val categories = catalog.categories.associate { it.id to it.title }
            val profiles = catalog.websiteProfiles.associateBy { it.websiteId }
            // Reuse identical title/alias/organization/category token sets across
            // records instead of allocating and tokenizing the same text again.
            val tokenized = HashMap<String, Set<String>>()
            entries = catalog.websites.filter { it.verificationStatus in setOf("verified", "directory_listed") }
                .sortedBy { it.title }.map { site ->
                    // Only source-checked profile text is added; nothing is inferred for other records.
                    val profile = profiles[site.id]
                    val fields = listOf(site.title to 8, site.aliases.joinToString(" ") to 7,
                        site.keywords.joinToString(" ") to 6, site.useCases.joinToString(" ") to 6,
                        site.categories.mapNotNull { categories[it] }.joinToString(" ") to 4,
                        site.description to 3, site.organization to 2,
                        listOfNotNull(site.state, site.district).joinToString(" ") to 6,
                        profile?.purpose?.value.orEmpty() to 5, profile?.tasks.orEmpty().joinToString(" ") { it.label } to 4,
                        profile?.operator?.value.orEmpty() to 2)
                    Indexed(site, fields.map { (value, weight) ->
                        tokenized.getOrPut(value) {
                            terms(value).toSet()
                        } to weight
                    }, if (!site.isDirectoryListed || profile?.purpose != null) CHECKED_BOOST else 0)
                }
            indexedCatalog = catalog
        }
        return entries
    }

    fun find(catalog: Catalog, query: String = "", categoryId: String? = null, level: String? = null,
             state: String? = null, district: String? = null): List<Website> {
        val tokens = terms(query)
        val filtered = index(catalog).asSequence()
            .filter { categoryId == null || categoryId in it.site.categories }
            .filter { level == null || it.site.level.equals(level, ignoreCase = true) }
            .filter { state == null || it.site.state.equals(state, ignoreCase = true) }
            .filter { district == null || it.site.district.equals(district, ignoreCase = true) }
        if (tokens.isEmpty()) return filtered.map { it.site }.toList()
        return filtered.map { item -> item.site to score(item, tokens) }
            .filter { tokens.isEmpty() || it.second > 0 }
            .sortedWith(compareByDescending<Pair<Website, Int>> { it.second }.thenBy { it.first.title })
            .map { it.first }
            .toList()
    }

    internal fun terms(value: String): List<String> = words.findAll(value.lowercase()).map { synonyms[it.value] ?: it.value }
        .filter { it !in stopWords }.distinct().toList()

    private fun score(item: Indexed, tokens: List<String>): Int = scoreFields(item.fields,tokens).let { if (it > 0) it + item.boost else 0 }

    internal fun scoreFields(fields: List<Pair<Set<String>,Int>>, tokens: List<String>): Int {
        val scores = tokens.map { token ->
            fields.maxOf { (candidates, weight) ->
                when {
                    token in candidates -> weight
                    candidates.any { it.startsWith(token) && token.length >= 4 } -> weight - 1
                    token.length >= 5 && candidates.any { editDistanceAtMostOne(it, token) } -> weight - 2
                    else -> 0
                }
            }
        }
        return if (scores.any { it <= 0 }) 0 else scores.sum()
    }

    private fun editDistanceAtMostOne(a: String, b: String): Boolean {
        if (kotlin.math.abs(a.length - b.length) > 1) return false
        var i = 0; var j = 0; var edits = 0
        while (i < a.length && j < b.length) {
            if (a[i] == b[j]) { i++; j++; continue }
            if (++edits > 1) return false
            if (a.length >= b.length) i++
            if (b.length >= a.length) j++
        }
        return edits + (a.length - i) + (b.length - j) <= 1
    }
}
