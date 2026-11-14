package `in`.jansetu.app.ui

import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.vault.Preparation
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.net.URI

class BeforeYouRequestTest {
    private companion object {
        val catalog by lazy { CatalogParser.parse(File("build/generated/runtimeAssets/catalog.json").readText()) }
    }
    private val rti get() = catalog.services.first { it.id == "rti-central-request" }

    @Test fun theRtiRequestShowsFiveSourceBackedRulesInOrder() {
        val items = BeforeYouRequest.items(rti, catalog.services)
        assertEquals(listOf("If you are below the poverty line", "Identity documents", "Paying the fee",
            "If no registration number appears after payment", "If you later appeal"), items.map { it.heading })
        val text = items.joinToString(" ") { it.fact.text }
        listOf("BPL certificate", "only exception is a BPL card", "UPI", "RuPay", "24–48 working hours", "do not pay again", "first appeal")
            .forEach { assertTrue(it, text.contains(it, ignoreCase = true)) }
        items.forEach { item ->
            assertTrue(item.fact.evidenceIds.isNotEmpty())
            item.fact.evidenceIds.forEach { id ->
                val source = item.service.evidence.single { it.id == id }
                val uri = URI(source.url)
                assertEquals("https", uri.scheme)
                assertTrue(source.url, uri.host.endsWith(".gov.in") || uri.host.endsWith(".nic.in"))
                assertTrue(source.checkedOn.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
            }
        }
        assertEquals("rti-first-appeal", items.last().service.id)
        assertEquals(BeforeYouRequest.GUIDELINES, rti.informationUrl)
    }

    @Test fun noOtherServiceGetsTheCardAndMissingFactsAreNeverInvented() {
        catalog.services.filter { it.id != "rti-central-request" }.forEach { assertTrue(it.id, BeforeYouRequest.items(it, catalog.services).isEmpty()) }
        // If a rule is removed from the record, the card shows fewer items rather than filling the gap.
        val withoutFees = rti.copy(fees = null)
        assertEquals(4, BeforeYouRequest.items(withoutFees, catalog.services).size)
        assertEquals(4, BeforeYouRequest.items(rti, catalog.services.filter { it.id != "rti-first-appeal" }).size)
    }

    @Test fun preparationListsOnlyTheBplCertificateAsAConditionalDocument() {
        val requirements = Preparation.requirements(rti)
        assertNull(requirements.required)
        assertEquals(1, requirements.conditional?.size)
        assertTrue(requirements.conditional!!.single().text.contains("BPL certificate"))
        assertFalse(requirements.items.any { it.text.contains("Aadhaar") })
    }

    @Test fun rtiOnlineIsNeverHostedInsideJanSetu() {
        val site = catalog.websites.first { it.id == rti.websiteId }
        assertNotEquals("in_app", site.browserSupport)
    }
}
