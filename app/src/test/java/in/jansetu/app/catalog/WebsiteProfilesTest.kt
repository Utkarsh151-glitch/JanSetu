package `in`.jansetu.app.catalog

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Phase 6B: checked website information, kept separate from directory text and from service records. */
class WebsiteProfilesTest {
    companion object {
        // Parsed once per class: the catalog is large and the unit-test JVM heap is shared with other suites.
        private val runtimeText by lazy { File("build/generated/runtimeAssets/catalog.json").readText() }
        private val runtime by lazy { CatalogParser.parse(runtimeText) }
    }
    private fun rejected(catalog: Catalog) = runCatching { WebsiteProfileValidation.validate(catalog) }.isFailure
    private fun withFirst(change: (WebsiteProfile) -> WebsiteProfile) =
        runtime.copy(websiteProfiles = listOf(change(runtime.websiteProfiles.first())) + runtime.websiteProfiles.drop(1))

    @Test fun firstBatchIsPublishedAsAutomatedSourceCheckOnly() {
        // Phase 6B (15) + Phase 6C second batch (10) published profiles; no human review has been recorded yet.
        assertEquals(25, runtime.websiteProfiles.size)
        assertTrue(runtime.websiteProfiles.all { it.reviewStatus == "source_checked" && !it.isHumanReviewed && it.reviewedOn == null })
        assertTrue(runtime.websiteProfiles.all { it.purpose != null && it.evidence.isNotEmpty() && it.checkedOn in setOf("2026-10-17", "2026-10-18") })
        // Held in the claims file (HTTP 500 / 403 / captcha): never packaged.
        listOf("uidai", "india-portal", "igod-1d591544b1c2e25d", "igod-9783d3afd73ca927", "igod-44b4a596b1818cab")
            .forEach { assertNull(runtime.profileFor(it)) }
        // Drafts awaiting a publication decision: never packaged.
        listOf("igod-421dca3648f50c0b", "igod-18649bde195e5a01", "igod-0eda9ab58f816d6f", "igod-2bdd905020581df7")
            .forEach { assertNull(runtime.profileFor(it)) }
    }

    @Test fun completenessCountsSeparateCheckedReviewedAndUnderReview() {
        val counts = runtime.websites.groupingBy { runtime.informationStatus(it) }.eachCount()
        assertEquals(25, counts[InformationStatus.PURPOSE_CHECKED])
        assertEquals(2, counts[InformationStatus.REVIEWED_RECORD])
        assertEquals(runtime.websites.size - 27, counts[InformationStatus.UNDER_REVIEW])
        assertEquals(7347, runtime.websites.size)
    }

    @Test fun humanReviewNeedsItsOwnDateAndAutomatedChecksNeverCarryOne() {
        val first = runtime.websiteProfiles.first()
        val reviewed = first.copy(reviewStatus = "human_reviewed", reviewedOn = "2026-10-18")
        assertFalse(rejected(withFirst { reviewed }))
        assertTrue(rejected(withFirst { it.copy(reviewStatus = "human_reviewed") }))
        assertTrue(rejected(withFirst { it.copy(reviewStatus = "human_reviewed", reviewedOn = "2026-10-01") }))
        assertTrue(rejected(withFirst { it.copy(reviewStatus = "human_reviewed", reviewedOn = "2999-01-01") }))
        assertTrue(rejected(withFirst { it.copy(reviewedOn = "2026-10-18") }))
    }

    @Test fun unknownStaysUnknownAndNotStatedIsDistinct() {
        val eci = requireNotNull(runtime.profileFor("igod-85affa23dc47255b"))
        assertTrue(eci.help.isEmpty() && "help" in eci.notStated && eci.audience == null)
        val kerala = requireNotNull(runtime.profileFor("igod-50b73c3937b8d29b"))
        assertFalse("help" in kerala.notStated) // partly held, but one checked help link is published
        // A directory record without a profile gains no profile facts.
        val directory = runtime.websites.first { it.isDirectoryListed && runtime.profileFor(it.id) == null }
        assertEquals(InformationStatus.UNDER_REVIEW, runtime.informationStatus(directory))
        assertFalse(runtime.hasCheckedInformation(directory))
    }

    @Test fun everyFactAndLinkCarriesProvenanceAndOfficialLinksOnly() {
        runtime.websiteProfiles.forEach { profile ->
            val ids = profile.evidence.map { it.id }.toSet()
            listOfNotNull(profile.purpose, profile.operator, profile.audience, profile.jurisdiction, profile.languages)
                .forEach { assertTrue(it.evidenceIds.isNotEmpty() && ids.containsAll(it.evidenceIds)) }
            (profile.help + profile.tasks).forEach { link ->
                assertTrue(link.url, WebsiteProfileValidation.officialLink(link.url))
                assertFalse(link.url, WebsiteProfileValidation.sensitiveJourney(link.url, link.label))
            }
            assertTrue(profile.evidence.all { it.url.startsWith("https://") && it.claim.isNotBlank() })
        }
    }

    @Test fun websiteProfilesNeverCarryServiceChecklistsOrContactDetails() {
        val profiles = Json.parseToJsonElement(runtimeText).jsonObject.getValue("websiteProfiles").jsonArray
        val forbidden = setOf("eligibility", "documents", "requiredDocuments", "fees", "deadline", "phone", "email", "helpline", "address", "quote")
        profiles.forEach { profile ->
            assertTrue(profile.jsonObject.keys.intersect(forbidden).isEmpty())
            profile.jsonObject.getValue("evidence").jsonArray.forEach { assertFalse("quote" in it.jsonObject.keys) }
        }
        assertFalse(Regex("\\b1800\\s?\\d{3}\\s?\\d{4}\\b|@[a-z]+\\.gov\\.in|\\[at\\]").containsMatchIn(profiles.toString()))
    }

    @Test fun recordsAndIdentifiersArePreservedAndServicesStaySeparate() {
        val source = CatalogParser.parse(File("src/main/assets/catalog.json").readText())
        assertTrue(source.websiteProfiles.isEmpty()) // catalogs without profiles (older signed updates) still parse
        assertEquals(source.websites.map { it.id }, runtime.websites.map { it.id })
        assertEquals(source.websites.map { it.description }, runtime.websites.map { it.description })
        assertEquals(52, runtime.services.size)
        assertTrue(runtime.websiteProfiles.all { p -> runtime.websites.any { it.id == p.websiteId } })
        assertEquals(listOf("cpgrams-grievance-appeal", "cpgrams-public-grievance"), runtime.servicesFor("cpgrams").map { it.id }.sorted())
        assertTrue(runtime.servicesFor("igod-ec86b21083980772").isEmpty())
    }

    @Test fun validationRejectsUnsafeOrUnsupportedProfiles() {
        assertFalse(rejected(runtime))
        assertTrue(rejected(withFirst { it.copy(websiteId = "not-a-website") }))
        assertTrue(rejected(withFirst { it.copy(reviewStatus = "not_reviewed") }))
        assertTrue(rejected(withFirst { it.copy(purpose = null) }))
        assertTrue(rejected(withFirst { it.copy(checkedOn = "2999-01-01") }))
        assertTrue(rejected(withFirst { it.copy(purpose = it.purpose!!.copy(evidenceIds = listOf("missing"))) }))
        assertTrue(rejected(withFirst { it.copy(help = listOf(ProfileLink("Help", "http://www.tnesevai.tn.gov.in/", "help", listOf(it.evidence.first().id)))) }))
        assertTrue(rejected(withFirst { it.copy(help = listOf(ProfileLink("Help", "https://example.com/help", "help", listOf(it.evidence.first().id)))) }))
        assertTrue(rejected(withFirst { it.copy(tasks = listOf(ProfileLink("Sign in", "https://www.tnesevai.tn.gov.in/login", "task", listOf(it.evidence.first().id)))) }))
        assertTrue(rejected(withFirst { it.copy(tasks = listOf(ProfileLink("FAQ", "https://www.tnesevai.tn.gov.in/Pages/FAQ.aspx", "faq", listOf(it.evidence.first().id)))) }))
        assertTrue(rejected(withFirst { it.copy(notStated = listOf("eligibility")) }))
        assertTrue(rejected(runtime.copy(websiteProfiles = runtime.websiteProfiles + runtime.websiteProfiles.first())))
    }

    @Test fun parserAndSignedImportsApplyTheSameProfileChecks() {
        val unreviewed = runtimeText.replaceFirst("\"reviewStatus\":\"source_checked\"", "\"reviewStatus\":\"proposed\"")
        assertTrue(unreviewed != runtimeText && runCatching { CatalogParser.parse(unreviewed) }.isFailure)
        val insecure = runtimeText.replaceFirst("\"url\":\"https://pgportal.gov.in/Home/Faq\"", "\"url\":\"http://pgportal.gov.in/Home/Faq\"")
        assertTrue(insecure != runtimeText && runCatching { CatalogParser.parse(insecure) }.isFailure)
    }

    @Test fun citizenSearchUsesCheckedTextAndLabelsWithoutInventingFacts() {
        val grievance = CitizenSearch.find(runtime, "grievance")
        assertEquals("cpgrams", grievance.first().service?.websiteId)
        assertTrue(grievance.any { it.service == null && it.website.id == "cpgrams" })
        // Checked records lead unchecked listings of similar strength (here: title matches on "Grievance").
        // Since Phase 6C, Odisha's checked grievance portal (title match) can rank alongside CPGRAMS.
        // Scores: checked title match 8 + 3, checked keyword match (CPGRAMS) 6 + 3, unchecked title matches 8.
        val grievanceSites = CitizenSearch.find(runtime, "grievance", type = "websites").take(2)
        assertTrue(grievanceSites.all { runtime.hasCheckedInformation(it.website) })
        assertEquals(setOf("igod-5891bfd68a83ed15", "cpgrams"), grievanceSites.map { it.website.id }.toSet())
        val stateServices = CitizenSearch.find(runtime, "state government services", type = "websites").map { it.website.id }
        assertTrue("igod-ec86b21083980772" in stateServices.take(5))
        val checked = CitizenSearch.find(runtime, "scholarship", type = "checked")
        assertTrue(checked.isNotEmpty() && checked.all { it.service == null && runtime.hasCheckedInformation(it.website) })
        assertTrue(listOf("national-scholarship-portal", "igod-781379c678ce1e21", "igod-dc063c5a252aabe5")
            .all { id -> checked.any { it.website.id == id } })
        assertTrue(CitizenSearch.find(runtime, "pension").any { it.service?.id == "tn-old-age-pension" })
        assertTrue(CitizenSearch.find(runtime, "certificate", type = "checked").none { it.website.isDirectoryListed && runtime.profileFor(it.website.id) == null })
        // The checked filter never adds records; it only narrows the website results.
        val all = CitizenSearch.find(runtime, "portal", type = "websites").map { it.website.id }.toSet()
        assertTrue(all.containsAll(CitizenSearch.find(runtime, "portal", type = "checked").map { it.website.id }))
    }
}
