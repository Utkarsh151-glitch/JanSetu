package `in`.jansetu.app.catalog

import java.io.File
import kotlinx.serialization.json.*
import org.junit.Test

class SearchPerformanceTest {
    @Test fun measureActualCatalogSearch() {
        val catalog=CatalogParser.parse(File("build/generated/runtimeAssets/catalog.json").readText())
        val queries=listOf("I need a scholarship for engineering", "How can I get a caste certificate", "Find government jobs", "I want to apply for a pension", "How do I obtain a birth certificate", "I need a subsidy for my small business", "DigiLocker", "file a complaint", "Kerala income certificate", "Delhi OBC certificate", "Odisha resident certificate", "Puducherry residence certificate", "get a new instant e-PAN", "register as a new voter")
        val start=System.nanoTime();CitizenSearch.prepare(catalog);val indexMs=(System.nanoTime()-start)/1_000_000.0
        queries.forEach {CitizenSearch.find(catalog,it)}
        val timings=mutableListOf<Double>()
        repeat(5) {queries.forEach {q -> val before=System.nanoTime();CitizenSearch.find(catalog,q);timings+=(System.nanoTime()-before)/1_000_000.0}}
        val report=buildJsonObject {
            put("websites",catalog.websites.size);put("services",catalog.services.size);put("indexMs",indexMs)
            put("medianQueryMs",timings.sorted()[timings.size/2]);put("p95QueryMs",timings.sorted()[(timings.size*0.95).toInt()-1])
            putJsonArray("queries") {queries.forEach {q->add(buildJsonObject {put("query",q);putJsonArray("topResults") {CitizenSearch.find(catalog,q).take(5).forEach {add(buildJsonObject {put("id",it.service?.id ?: it.website.id);put("type",if(it.service!=null) "service" else "website");put("title",it.title)})}}})}}
        }
        System.getenv("JANSETU_SEARCH_BENCHMARK")?.let {File(it).apply {parentFile?.mkdirs();writeText(report.toString())}}
        println("SEARCH_BENCHMARK $report")
    }
}
