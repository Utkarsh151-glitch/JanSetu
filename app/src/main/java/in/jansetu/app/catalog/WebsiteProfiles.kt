package `in`.jansetu.app.catalog

import kotlinx.serialization.Serializable
import java.net.URI
import java.time.LocalDate

/**
 * Phase 6B website information, checked against dated snapshots of official pages.
 * A profile is website-level only: eligibility, documents, fees, deadlines and contact
 * numbers stay on service records and are never part of it. Only published profiles
 * are packaged; held facts and the quoted page text stay in data/website_enrichment.
 */
@Serializable data class ProfileEvidence(val id: String, val url: String, val checkedOn: String, val claim: String)
@Serializable data class ProfileFact(val value: String, val evidenceIds: List<String>)
@Serializable data class ProfileLink(val label: String, val url: String, val kind: String, val evidenceIds: List<String>,
                                     val serviceId: String? = null)

@Serializable data class WebsiteProfile(
    val websiteId: String,
    val reviewStatus: String,
    val checkedOn: String,
    val evidence: List<ProfileEvidence>,
    /** Date of the named human decision (from reviews.json). Present only when reviewStatus is human_reviewed. */
    val reviewedOn: String? = null,
    val uncertainty: List<String> = emptyList(),
    /** Fields looked for on the official pages but not stated there. Distinct from "not yet checked". */
    val notStated: List<String> = emptyList(),
    val purpose: ProfileFact? = null,
    val operator: ProfileFact? = null,
    val audience: ProfileFact? = null,
    val jurisdiction: ProfileFact? = null,
    val languages: ProfileFact? = null,
    val help: List<ProfileLink> = emptyList(),
    val tasks: List<ProfileLink> = emptyList()
) {
    val isHumanReviewed: Boolean get() = reviewStatus == "human_reviewed"
    fun evidenceFor(fact: ProfileFact?): List<ProfileEvidence> =
        fact?.evidenceIds?.mapNotNull { id -> evidence.firstOrNull { it.id == id } }.orEmpty()
}

/** What a person can rely on for a website record. Never inferred from directory text. */
enum class InformationStatus { PURPOSE_CHECKED, REVIEWED_RECORD, UNDER_REVIEW }

fun Catalog.profileFor(websiteId: String): WebsiteProfile? = websiteProfiles.firstOrNull { it.websiteId == websiteId }

fun Catalog.informationStatus(site: Website): InformationStatus = when {
    profileFor(site.id)?.purpose != null -> InformationStatus.PURPOSE_CHECKED
    !site.isDirectoryListed -> InformationStatus.REVIEWED_RECORD
    else -> InformationStatus.UNDER_REVIEW
}

fun Catalog.hasCheckedInformation(site: Website): Boolean = informationStatus(site) != InformationStatus.UNDER_REVIEW

/** Published services that link to this website. Website and service records stay separate. */
fun Catalog.servicesFor(websiteId: String): List<Service> =
    services.filter { it.websiteId == websiteId && it.verificationStatus == "verified" && it.publicationStatus == "published" }

object WebsiteProfileValidation {
    val FIELDS = setOf("purpose", "operator", "audience", "jurisdiction", "languages", "help", "tasks")
    private val linkKinds = mapOf("help" to setOf("help", "contact", "grievance", "faq"), "tasks" to setOf("task"))
    private val date = Regex("\\d{4}-\\d{2}-\\d{2}")
    // Mirrors BrowserPolicy: sign-in, application and payment journeys are never offered as website links.
    private val sensitive = Regex("(?:^|[/?#&=:_.-])(login|signin|sign-in|signup|register|registration|apply|otp|password|auth|oauth2?|payment|checkout|upload)(?:$|[/?#&=._-])")

    /** Official HTTPS government host. Unlike service URLs, a page-section fragment (#contact) is allowed. */
    internal fun officialLink(url: String): Boolean = runCatching {
        val uri = URI(url); val host = uri.host.orEmpty().lowercase()
        uri.scheme == "https" && uri.userInfo == null && uri.port in listOf(-1, 443) &&
            (host.endsWith(".gov.in") || host.endsWith(".nic.in"))
    }.getOrDefault(false)

    internal fun sensitiveJourney(url: String, label: String = ""): Boolean = runCatching {
        val uri = URI(url)
        sensitive.containsMatchIn(listOfNotNull(uri.rawPath, uri.rawQuery, uri.rawFragment).joinToString("/").lowercase()) ||
            sensitive.containsMatchIn(label.lowercase().replace(' ', '-'))
    }.getOrDefault(true)

    private fun checkedDate(value: String) {
        require(value.matches(date)) { "Bad profile date" }
        require(!LocalDate.parse(value).isAfter(LocalDate.now())) { "Profile date is in the future" }
    }

    fun validate(catalog: Catalog) {
        val websites = catalog.websites.map { it.id }.toSet()
        val services = catalog.services.map { it.id }.toSet()
        require(catalog.websiteProfiles.map { it.websiteId }.distinct().size == catalog.websiteProfiles.size) { "Duplicate website profile" }
        catalog.websiteProfiles.forEach { profile ->
            require(profile.websiteId in websites) { "Profile for unknown website" }
            require(profile.reviewStatus in setOf("source_checked", "human_reviewed")) { "Unreviewed profile" }
            checkedDate(profile.checkedOn)
            // A human review always has its own date, on or after the evidence was captured; an automated check never has one.
            if (profile.isHumanReviewed) {
                val reviewed = requireNotNull(profile.reviewedOn) { "Human review without a review date" }
                checkedDate(reviewed)
                require(reviewed >= profile.checkedOn) { "Review date before the evidence was captured" }
            } else require(profile.reviewedOn == null) { "Automated check must not carry a human review date" }
            require(profile.purpose != null) { "Profile without a checked purpose" }
            val ids = profile.evidence.map { it.id }.toSet()
            require(ids.isNotEmpty() && ids.size == profile.evidence.size) { "Profile evidence missing or duplicated" }
            profile.evidence.forEach { e ->
                require(officialLink(e.url) && !sensitiveJourney(e.url) && e.claim.isNotBlank()) { "Bad profile evidence" }
                checkedDate(e.checkedOn)
            }
            require(profile.notStated.all { it in FIELDS }) { "Unknown field" }
            listOfNotNull(profile.purpose, profile.operator, profile.audience, profile.jurisdiction, profile.languages).forEach { fact ->
                require(fact.value.isNotBlank() && fact.evidenceIds.isNotEmpty() && fact.evidenceIds.all { it in ids }) { "Unsupported profile fact" }
            }
            listOf("help" to profile.help, "tasks" to profile.tasks).forEach { (field, links) ->
                links.forEach { link ->
                    require(link.label.isNotBlank() && link.kind in linkKinds.getValue(field)) { "Bad profile link kind" }
                    require(officialLink(link.url) && !sensitiveJourney(link.url, link.label)) { "Profile link must be an official, non-sign-in page" }
                    require(link.evidenceIds.isNotEmpty() && link.evidenceIds.all { it in ids }) { "Unsupported profile link" }
                    require(link.serviceId == null || link.serviceId in services) { "Profile link names an unknown service" }
                }
            }
        }
    }
}
