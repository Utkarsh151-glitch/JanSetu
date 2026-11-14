package `in`.jansetu.app.vault

import `in`.jansetu.app.catalog.CatalogParser
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

class PreparationTest {
    private companion object {
        val catalog by lazy { CatalogParser.parse(File("build/generated/runtimeAssets/catalog.json").readText()) }
    }

    @Test fun requirementsAreExactlyTheRecordedFactsAndUnknownsStayUnknown() {
        val services = catalog.services
        services.forEach { service ->
            val r = Preparation.requirements(service)
            assertEquals(service.documents?.map { it.text }, r.required?.map { it.text })
            assertEquals(service.conditionalDocuments?.map { it.text }, r.conditional?.map { it.text })
            r.items.forEach { item ->
                val fact = (service.documents.orEmpty() + service.conditionalDocuments.orEmpty()).first { it.text == item.text }
                assertEquals(fact.evidenceIds, item.sources.map { it.id })
            }
        }
        // 29 services list required documents and 23 do not: those stay "not confirmed", never an empty "nothing needed".
        assertEquals(29, services.count { Preparation.requirements(it).required != null })
        assertEquals(23, services.count { Preparation.requirements(it).required == null })
        assertTrue(services.filter { it.documents == null }.all { Preparation.requirements(it).required == null })
        assertEquals(services.count { it.documents != null || it.conditionalDocuments != null }, services.count { Preparation.requirements(it).hasAny })
    }

    @Test fun keysAreStableAndChangeWhenWordingOrKindChanges() {
        val a = Preparation.key("tn-income-certificate", RequirementKind.REQUIRED, "Applicant photo.")
        assertEquals(a, Preparation.key("tn-income-certificate", RequirementKind.REQUIRED, "  Applicant   photo. "))
        assertNotEquals(a, Preparation.key("tn-income-certificate", RequirementKind.CONDITIONAL, "Applicant photo."))
        assertNotEquals(a, Preparation.key("tn-income-certificate", RequirementKind.REQUIRED, "Applicant photo and address."))
        assertNotEquals(a, Preparation.key("tn-nativity-certificate", RequirementKind.REQUIRED, "Applicant photo."))
        assertTrue(a.matches(Regex("[0-9a-f]{24}")))
    }

    @Test fun choicesAreReportedAsChoicesNeverAsMetRequirements() {
        val service = catalog.services.first { it.documents != null && it.documents!!.size >= 2 }
        val requirements = Preparation.requirements(service)
        val doc = VaultDocument(UUID.randomUUID().toString(), "Photo", DocumentCategory.PHOTO, DocumentType.JPEG, 10, "c".repeat(64),
            "2026-10-18T00:00:00Z", "2026-10-18T00:00:00Z")
        val choice = Preparation.newChoice(service.id, requirements.items.first(), doc.id, Instant.parse("2026-10-18T01:00:00Z"))
        Preparation.validateStored(choice)
        val view = Preparation.view(requirements, listOf(choice), listOf(doc))
        assertEquals(1, view.itemsWithChoice)
        assertTrue(view.summary().contains("You chose files for 1 of ${requirements.items.size} listed items"))
        assertTrue(view.summary().contains("has not checked"))
        val forbidden = Regex("\\b(complete|completed|satisfied|eligible|verified|approved|ready to submit)\\b", RegexOption.IGNORE_CASE)
        assertFalse(forbidden.containsMatchIn(view.summary()))
        RequirementKind.entries.forEach { assertFalse(forbidden.containsMatchIn(it.label)) }
        assertEquals("PreparationChoice(redacted)", choice.toString())
    }

    @Test fun choicesForChangedRequirementsAreKeptAsOutdatedNotReassigned() {
        val service = catalog.services.first { it.documents != null }
        val requirements = Preparation.requirements(service)
        val documentId = UUID.randomUUID().toString()
        val stale = PreparationChoice(UUID.randomUUID().toString(), service.id, "0".repeat(24), "An older wording", documentId, "2026-10-01T00:00:00Z")
        val otherService = stale.copy(id = UUID.randomUUID().toString(), serviceId = "some-other-service")
        val view = Preparation.view(requirements, listOf(stale, otherService), emptyList())
        assertEquals(0, view.itemsWithChoice)
        assertEquals(listOf(stale.id), view.outdatedChoices.map { it.first.id })
        assertNull(view.outdatedChoices.single().second)
    }

    @Test fun malformedStoredChoicesAreRejected() {
        val ok = PreparationChoice(UUID.randomUUID().toString(), "tn-income-certificate", "a".repeat(24), "Photo", UUID.randomUUID().toString(), "2026-10-18T00:00:00Z")
        listOf(ok.copy(id = "x"), ok.copy(serviceId = "Bad Id"), ok.copy(requirementKey = "short"), ok.copy(requirementText = ""),
            ok.copy(documentId = "y"), ok.copy(chosenAt = "yesterday")).forEach { assertTrue(runCatching { Preparation.validateStored(it) }.isFailure) }
    }
}

class PickerGrantsTest {
    private class MutableClock(var now: Instant) : Clock() {
        override fun getZone() = ZoneOffset.UTC
        override fun withZone(zone: java.time.ZoneId?) = this
        override fun instant() = now
    }
    private fun mac(secret: String): (ByteArray) -> ByteArray = { data ->
        javax.crypto.Mac.getInstance("HmacSHA256").apply { init(javax.crypto.spec.SecretKeySpec(secret.toByteArray(), "HmacSHA256")) }.doFinal(data)
    }

    @Test fun issuedIdsResolveUntilTheyExpire() {
        val clock = MutableClock(Instant.parse("2026-10-18T10:00:00Z"))
        val grants = PickerGrants(mac("k1"), clock)
        val id = UUID.randomUUID().toString()
        val token = grants.issue(id)
        assertEquals(id, grants.resolve(token))
        clock.now = clock.now.plus(Duration.ofMinutes(59))
        assertEquals(id, grants.resolve(token))
        clock.now = clock.now.plus(Duration.ofMinutes(2))
        assertNull(grants.resolve(token))
    }

    @Test fun forgedAlteredOrRevokedIdsAreRejected() {
        val clock = MutableClock(Instant.parse("2026-10-18T10:00:00Z"))
        val grants = PickerGrants(mac("k1"), clock)
        val id = UUID.randomUUID().toString()
        val token = grants.issue(id)
        val (doc, expiry, tag) = token.split('.')
        assertNull(grants.resolve("${UUID.randomUUID()}.$expiry.$tag"))
        assertNull(grants.resolve("$doc.${expiry.toLong() + 3600}.$tag"))
        assertNull(grants.resolve("$doc.$expiry.${tag.reversed()}"))
        assertNull(grants.resolve(id))
        assertNull(grants.resolve(PickerGrants.ROOT_ID))
        assertNull(grants.resolve("../../etc.1.x"))
        // Rotating the key (as "Stop all file-picker access" does) withdraws every outstanding ID.
        assertNull(PickerGrants(mac("k2"), clock).resolve(token))
        // An expiry far beyond the validity window is refused even with a valid MAC.
        val farFuture = PickerGrants(mac("k1"), clock, Duration.ofDays(30)).issue(id)
        assertNull(grants.resolve(farFuture))
    }
}
