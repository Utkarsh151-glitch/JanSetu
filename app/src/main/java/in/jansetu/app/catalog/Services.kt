package `in`.jansetu.app.catalog

import kotlinx.serialization.Serializable
import java.net.URI
import java.time.LocalDate

@Serializable data class ServiceFact(val text:String,val evidenceIds:List<String>)
@Serializable data class ServiceEvidence(val id:String,val url:String,val publisher:String,val checkedOn:String,
    val summary:String,val locator:String,val contentSha256:String?=null)
@Serializable data class ServiceOrganization(val id:String,val name:String,val websiteIds:List<String>)
@Serializable data class ApplicationRouteEvidence(val status:String,val checkedOn:String,val evidenceIds:List<String>,val notes:String,
    val url:String?=null,val finalUrl:String?=null,val httpStatus:Int?=null,val tlsVerified:Boolean=false,
    val linkedFromSource:Boolean=false,val entryReviewed:Boolean=false,val redirectReviewed:Boolean=false)
@Serializable data class Service(
    val id:String,val websiteId:String,val organizationId:String,val title:String,val description:String,
    val categories:List<String>,val department:String,val level:String,val informationUrl:String,
    val sourceUrl:String,val verificationStatus:String,val publicationStatus:String,val verifiedOn:String,
    val citizenIntents:List<String>,val synonyms:List<String>,val evidence:List<ServiceEvidence>,
    val linkEvidenceIds:List<String>,val reviewNotes:String,val applicationMode:String,
    val applicationUrl:String?=null,val state:String?=null,val district:String?=null,
    val eligibility:ServiceFact?=null,val documents:List<ServiceFact>?=null,
    val conditionalDocuments:List<ServiceFact>?=null,val procedure:List<ServiceFact>?=null,
    val fees:ServiceFact?=null,val deadline:ServiceFact?=null,val processing:ServiceFact?=null,
    val beneficiaryGroups:List<ServiceFact>?=null,val applicationRouteEvidence:ApplicationRouteEvidence?=null,
    val possibleDuplicateOf:String?=null,val duplicateOf:String?=null
)

object ServiceValidation {
    internal fun officialUrl(url:String):Boolean=runCatching {
        val uri=URI(url); val host=uri.host.orEmpty().lowercase()
        uri.scheme=="https" && uri.userInfo==null && uri.port in listOf(-1,443) && uri.fragment==null &&
            (host.endsWith(".gov.in") || host.endsWith(".nic.in"))
    }.getOrDefault(false)
    fun validate(catalog:Catalog) {
        require(catalog.services.map {it.id}.distinct().size==catalog.services.size)
        val sites=catalog.websites.associateBy {it.id};val categories=catalog.categories.map {it.id}.toSet()
        val organizations=catalog.serviceOrganizations.associateBy {it.id}
        require(organizations.size==catalog.serviceOrganizations.size)
        val identities=mutableSetOf<List<String>>()
        catalog.services.forEach {s ->
            require(s.possibleDuplicateOf==null && s.duplicateOf==null)
            val identity=listOf(s.title,s.department,s.level,s.state.orEmpty(),s.district.orEmpty()).map {it.lowercase().replace(Regex("[^\\p{L}\\p{N}_]+")," ").trim()}
            require(identities.add(identity)) {"Duplicate service in jurisdiction"}
            require(s.id.matches(Regex("[a-z0-9][a-z0-9-]{2,100}")) && s.title.isNotBlank() && s.description.isNotBlank())
            val site=requireNotNull(sites[s.websiteId]);val org=requireNotNull(organizations[s.organizationId])
            require(s.websiteId in org.websiteIds && org.name==site.organization)
            require(s.department.isNotBlank() && s.categories.isNotEmpty() && s.categories.all {it in categories})
            require(s.level in setOf("Central","State","District","Local"))
            require(s.level=="Central" || !s.state.isNullOrBlank())
            require(s.level!="Central" || (s.state==null && s.district==null))
            require(s.state==null || catalog.websites.any {it.state==s.state})
            require(s.state==null || site.state==null || site.state==s.state)
            require(s.level !in setOf("District","Local") || !s.district.isNullOrBlank())
            require(s.verificationStatus=="verified" && s.publicationStatus=="published" && s.reviewNotes.isNotBlank())
            val checked=LocalDate.parse(s.verifiedOn);require(!checked.isAfter(LocalDate.now()))
            require(officialUrl(s.informationUrl) && officialUrl(s.sourceUrl))
            require(s.applicationUrl==null || officialUrl(s.applicationUrl))
            require(s.applicationMode in setOf("online","assisted","information"))
            require(s.applicationMode!="online" || s.applicationUrl!=null)
            require(s.applicationMode=="online" || s.applicationUrl==null)
            val ids=s.evidence.map {it.id}.toSet()
            require(ids.isNotEmpty() && ids.size==s.evidence.size)
            require(s.linkEvidenceIds.isNotEmpty() && s.linkEvidenceIds.all {it in ids})
            require(s.evidence.any {it.url==s.informationUrl})
            s.evidence.forEach {e ->require(officialUrl(e.url) && e.publisher.isNotBlank() && e.summary.isNotBlank() && e.locator.isNotBlank() && e.checkedOn==s.verifiedOn)}
            fun fact(f:ServiceFact) {require(f.text.isNotBlank() && f.evidenceIds.isNotEmpty() && f.evidenceIds.all {it in ids})}
            listOfNotNull(s.eligibility,s.fees,s.deadline,s.processing).forEach(::fact)
            listOfNotNull(s.documents,s.conditionalDocuments,s.procedure,s.beneficiaryGroups).forEach {facts ->require(facts.isNotEmpty());facts.forEach(::fact)}
            s.applicationRouteEvidence?.let {r ->
                require(r.status in setOf("confirmed","unconfirmed","not_applicable") && r.notes.isNotBlank() && r.checkedOn==s.verifiedOn)
                require(r.evidenceIds.isNotEmpty() && r.evidenceIds.all {it in ids})
                if(r.status=="confirmed") {
                    require(s.applicationMode=="online" && s.applicationUrl!=null && r.url==s.applicationUrl)
                    require(r.finalUrl!=null && officialUrl(r.finalUrl) && r.httpStatus==200 && r.tlsVerified && r.linkedFromSource && r.entryReviewed)
                    require(r.finalUrl==r.url || r.redirectReviewed)
                } else require(s.applicationUrl==null && s.applicationMode!="online")
            }
            require(s.citizenIntents.isNotEmpty() && s.synonyms.isNotEmpty())
        }
    }
}

data class DiscoveryResult(val website:Website,val service:Service?=null) {
    val id:String get()=if(service==null) "website:${website.id}" else "service:${service.id}"
    val title:String get()=service?.title ?: website.title
}

/**
 * The source data retains its original level field. Citizen filtering presents
 * Delhi and Puducherry by their administrative jurisdiction without rewriting
 * that protected source value.
 */
fun Service.citizenJurisdictionLevel(): String =
    if (level.equals("State", ignoreCase = true) && state in setOf("Delhi", "Puducherry")) "Union Territory" else level

/** Extends the existing deterministic index; never converts a portal match into a service. */
object CitizenSearch {
    private data class Indexed(val service:Service,val website:Website,val fields:List<Pair<Set<String>,Int>>)
    private var indexedCatalog:Catalog?=null
    private var services:List<Indexed> = emptyList()
    @Synchronized private fun index(catalog:Catalog):List<Indexed> {
        if(indexedCatalog!==catalog) {
            val sites=catalog.websites.associateBy {it.id};val cats=catalog.categories.associate {it.id to it.title}
            services=catalog.services.filter {it.verificationStatus=="verified" && it.publicationStatus=="published"}.map {s ->
                val site=sites.getValue(s.websiteId)
                val fields=listOf(s.title to 12,s.citizenIntents.joinToString(" ") to 10,s.synonyms.joinToString(" ") to 9,
                    s.description to 7,s.department to 4,s.categories.mapNotNull {cats[it]}.joinToString(" ") to 3,
                    listOfNotNull(s.state,s.district).joinToString(" ") to 8,site.title to 2)
                Indexed(s,site,fields.map {(value,weight)->CatalogSearch.terms(value).toSet() to weight})
            }
            indexedCatalog=catalog
        }
        return services
    }
    fun prepare(catalog:Catalog) {CatalogSearch.prepare(catalog);index(catalog)}
    fun find(catalog:Catalog,query:String="",categoryId:String?=null,level:String?=null,state:String?=null,district:String?=null,type:String="all"):List<DiscoveryResult> {
        val tokens=CatalogSearch.terms(query)
        val matched=if(type=="websites" || type=="checked") emptyList() else index(catalog).asSequence()
            .filter {categoryId==null || categoryId in it.service.categories}
            .filter {level==null || level.equals(it.service.citizenJurisdictionLevel(),true)}
            .filter {state==null || state.equals(it.service.state,true)}
            .filter {district==null || district.equals(it.service.district,true)}
            .map {item ->
                val score=if(tokens.isEmpty()) 1 else CatalogSearch.scoreFields(item.fields,tokens)
                val exactIntent=tokens.isNotEmpty() && item.service.citizenIntents.any {CatalogSearch.terms(it)==tokens}
                item to if(score>0 && exactIntent) score+20 else score
            }
            .filter {it.second>0}.sortedWith(compareByDescending<Pair<Indexed,Int>> {it.second}.thenBy {it.first.service.title})
            .map {DiscoveryResult(it.first.website,it.first.service)}.toList()
        // "checked": websites whose purpose was checked against an official source (never inferred).
        val websites=if(type=="services") emptyList() else CatalogSearch.find(catalog,query,categoryId,level,state,district)
            .filter {type!="checked" || catalog.hasCheckedInformation(it)}.map {DiscoveryResult(it)}
        val brand=CatalogSearch.terms(query).joinToString(" ")
        val exact=if(brand.isBlank()) emptyList() else websites.filter {CatalogSearch.terms(it.website.title).joinToString(" ")==brand || it.website.domain.equals(query.trim(),true)}
        return exact+matched+websites.filterNot {it in exact}
    }
}
