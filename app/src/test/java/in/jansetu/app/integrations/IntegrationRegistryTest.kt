package `in`.jansetu.app.integrations

import `in`.jansetu.app.catalog.CatalogParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class IntegrationRegistryTest {
    private val site=CatalogParser.parse(File("src/main/assets/catalog.json").readText()).websites.first()
    @Test fun absentAuthorizationNeverInventsStatusOrCallsAService()=runBlocking {
        val result=IntegrationRegistry().execute(site,"application-status",emptyMap())
        assertTrue(result is IntegrationResult.OfficialWebsiteRequired)
        assertEquals(site.url,(result as IntegrationResult.OfficialWebsiteRequired).url)
    }
    @Test fun providerFailureAndUntrustedOutputFallBackToTheOfficialWebsite()=runBlocking {
        val fake=object:AuthorizedIntegration {
            override val descriptor=IntegrationDescriptor("fixture",setOf(site.id),site.sourceUrl,"test fixture only",mapOf("public-info" to emptyMap()),mapOf("title" to "string"),1,"Not a live provider")
            override suspend fun execute(operation:String,publicInputs:Map<String,String>):IntegrationResult = IntegrationResult.PublicInformation(mapOf("title" to "fixture"),"javascript:unsafe")
        }
        assertTrue(IntegrationRegistry(listOf(fake)).execute(site,"public-info",emptyMap()) is IntegrationResult.OfficialWebsiteRequired)
        assertTrue(IntegrationRegistry(listOf(fake)).execute(site,"not-supported",emptyMap()) is IntegrationResult.OfficialWebsiteRequired)
    }
}
