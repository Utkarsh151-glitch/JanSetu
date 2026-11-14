package `in`.jansetu.app.catalog

import java.io.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class ServicesTest {
    private fun catalog()=CatalogParser.parse(File("build/generated/runtimeAssets/catalog.json").readText())
    @Test fun actualPilotHasSourcesAndPreservesAllWebsiteIdentities() {
        val c=catalog();val original=CatalogParser.parse(File("src/main/assets/catalog.json").readText())
        assertEquals(52,c.services.size);assertEquals(7347,c.websites.size)
        assertEquals(original.websites.map {it.id to it.url},c.websites.map {it.id to it.url})
        assertEquals(37,c.services.count {it.eligibility==null});assertEquals(23,c.services.count {it.documents==null})
        assertTrue(c.services.all {it.evidence.isNotEmpty() && it.publicationStatus=="published"})
    }
    @Test fun malformedUnpublishedAndUnattributedServicesCannotLoad() {
        val c=catalog();val s=c.services.first()
        val invalid=listOf(s.copy(informationUrl="http://example.gov.in"),s.copy(applicationUrl="https://gov.in.evil.example/apply"),
            s.copy(websiteId="missing"),s.copy(organizationId="missing"),s.copy(publicationStatus="draft"),
            s.copy(evidence=emptyList()),s.copy(eligibility=ServiceFact("Made up",listOf("missing"))),
            s.copy(documents=emptyList()),s.copy(level="State",state=null))
        invalid.forEach {bad-> assertTrue("Rejected ${bad.id}",runCatching {CatalogParser.parse(Json.encodeToString(c.copy(services=listOf(bad))))}.isFailure)}
    }
    @Test fun citizenQueriesPrioritizeRealServicesAndRetainWebsiteResults() {
        val c=catalog()
        val expected=mapOf("I need a scholarship for engineering" to "aicte-pragati-degree",
            "How can I get a caste certificate" to "tn-community-certificate", "Find government jobs" to "ncs-government-job-search",
            "I want to apply for a pension" to "tn-old-age-pension", "How do I obtain a birth certificate" to "tn-birth-certificate-print",
            "I need a subsidy for my small business" to "pmegp-enterprise-support")
        expected.forEach {(q,id)->val result=CitizenSearch.find(c,q);assertTrue(q,result.any {it.service?.id==id});assertNotNull(q,result.first().service)}
        assertEquals("digilocker",CitizenSearch.find(c,"DigiLocker").first().website.id)
        assertNull(CitizenSearch.find(c,"DigiLocker").first().service)
        assertTrue(CitizenSearch.find(c,"not-a-real-service-991299").isEmpty())
        assertTrue(CitizenSearch.find(c,"income certificates").any {it.service?.id=="tn-income-certificate"})
        assertTrue(CitizenSearch.find(c,"scholrship technical").first().service!=null)
        assertEquals("cpgrams-public-grievance",CitizenSearch.find(c,"file a complaint").first().service?.id)
    }
    @Test fun typesAndJurisdictionFiltersDoNotPretendCoverage() {
        val c=catalog()
        assertTrue(CitizenSearch.find(c,"pension",state="Tamil Nadu",type="services").all {it.service?.state=="Tamil Nadu"})
        assertTrue(CitizenSearch.find(c,"pension",state="Bihar",type="services").isEmpty())
        assertTrue(CitizenSearch.find(c,categoryId="education",type="services").all {"education" in it.service!!.categories})
        assertEquals(52,CitizenSearch.find(c,type="services").size)
        assertEquals(7347,CitizenSearch.find(c,type="websites").size)
        assertTrue(CitizenSearch.find(c,level="Central",type="services").all {it.service!!.level=="Central"})
        val all=CitizenSearch.find(c);assertEquals(all.size,all.map {it.id}.distinct().size)
    }
    @Test fun expandedQueriesRespectJurisdictionAndExactPortalNames() {
        val c=catalog()
        listOf(Triple("income certificate","Kerala","kl-income-certificate"),Triple("OBC certificate","Delhi","dl-obc-certificate"),
            Triple("resident certificate","Odisha","od-resident-certificate"),Triple("residence certificate","Puducherry","py-residence-certificate")).forEach {(q,state,id)->
            val results=CitizenSearch.find(c,q,state=state,type="services")
            assertEquals(id,results.first().service?.id);assertTrue(results.all {it.service?.state==state})
        }
        assertEquals("instant-epan-new",CitizenSearch.find(c,"get a new instant e-PAN",type="services").first().service?.id)
        assertEquals("eci-new-elector-form6",CitizenSearch.find(c,"register as a new voter",type="services").first().service?.id)
        assertTrue(CitizenSearch.find(c,type="services",level="Local").isEmpty())
        assertTrue(CitizenSearch.find(c,type="services",state="Himachal Pradesh").isEmpty())
        assertNull(CitizenSearch.find(c,"DigiLocker").first().service)
    }
    @Test fun routeReviewsAndUnknownsCannotBePromotedIntoApply() {
        val c=catalog();val s=c.services.first {it.id=="dl-income-certificate"};val r=requireNotNull(s.applicationRouteEvidence)
        val bad=listOf(s.copy(applicationRouteEvidence=r.copy(status="unconfirmed")),s.copy(applicationRouteEvidence=r.copy(entryReviewed=false)),
            s.copy(applicationRouteEvidence=r.copy(finalUrl="https://another.gov.in/")),s.copy(applicationMode="information"),
            s.copy(possibleDuplicateOf="other"),s.copy(state="Kerala"),s.copy(applicationUrl="https://example.gov.in/bad path"),
            s.copy(beneficiaryGroups=listOf(ServiceFact("Invented",listOf("missing")))))
        bad.forEach {row ->assertTrue(runCatching {ServiceValidation.validate(c.copy(services=listOf(row)))}.isFailure)}
        assertNull(c.services.first {it.id=="py-income-certificate"}.documents)
        assertNull(c.services.first {it.id=="od-ews-certificate"}.eligibility)
        assertTrue(c.services.filter {it.id.startsWith("od-")}.all {it.applicationUrl==null && it.applicationMode=="information"})
    }
    @Test fun duplicateServiceIdentityCannotLoadUnderAnotherId() {
        val c=catalog();val s=c.services.first()
        assertTrue(runCatching {ServiceValidation.validate(c.copy(services=c.services+s.copy(id="duplicate-identity",title=s.title.uppercase()+"!")))}.isFailure)
    }
    @Test fun unionTerritoryFilterUsesActualJurisdictionWithoutChangingSerializedFacts() {
        val c=catalog()
        val delhi=CitizenSearch.find(c,state="Delhi",level="Union Territory",type="services")
        assertEquals(5,delhi.size);assertTrue(delhi.all {it.service?.state=="Delhi"})
        assertEquals(2,CitizenSearch.find(c,state="Puducherry",level="Union Territory",type="services").size)
        assertTrue(CitizenSearch.find(c,level="State",type="services").none {it.service?.state in setOf("Delhi","Puducherry")})
        assertTrue(c.services.filter {it.state=="Delhi"}.all {it.level=="State"}) // Protected wire data remains unchanged.
    }
}
