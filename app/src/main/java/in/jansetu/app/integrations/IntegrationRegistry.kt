package `in`.jansetu.app.integrations

import `in`.jansetu.app.catalog.Website
import `in`.jansetu.app.browser.BrowserPolicy
import kotlinx.coroutines.CancellationException

data class IntegrationDescriptor(
    val id: String, val websiteIds: Set<String>, val documentationUrl: String,
    val authorization: String, val operations: Map<String, Map<String, String>>,
    val outputFields: Map<String, String>, val requestsPerMinute: Int, val limitations: String
)
sealed interface IntegrationResult {
    data class PublicInformation(val fields: Map<String,String>,val sourceUrl: String) : IntegrationResult
    data class OfficialWebsiteRequired(val url: String,val reason: String) : IntegrationResult
}
interface AuthorizedIntegration {
    val descriptor: IntegrationDescriptor
    suspend fun execute(operation: String, publicInputs: Map<String,String>): IntegrationResult
}

/** No government API adapter is enabled without documented authorization. */
class IntegrationRegistry(private val providers: List<AuthorizedIntegration> = emptyList()) {
    init {
        require(providers.map { it.descriptor.id }.distinct().size==providers.size)
        providers.forEach { require(BrowserPolicy.isSafeHttps(it.descriptor.documentationUrl) && it.descriptor.authorization.isNotBlank() && it.descriptor.requestsPerMinute>0) }
    }
    fun provider(site: Website): AuthorizedIntegration? = providers.singleOrNull { site.id in it.descriptor.websiteIds }
    suspend fun execute(site: Website, operation: String, inputs: Map<String,String>): IntegrationResult {
        val adapter=provider(site) ?: return IntegrationResult.OfficialWebsiteRequired(site.url,"No authorized native integration is configured")
        val schema=adapter.descriptor.operations[operation] ?: return IntegrationResult.OfficialWebsiteRequired(site.url,"Operation unavailable")
        require(inputs.keys.all { it in schema } && inputs.values.all { it.length<=200 }) {"Unsupported public input"}
        require(inputs.keys.none { key -> Regex("(?i)password|otp|token|secret|aadhaar|panNumber").containsMatchIn(key) }) {"Sensitive inputs are not accepted"}
        return try {
            val result=adapter.execute(operation,inputs)
            if(result is IntegrationResult.PublicInformation) {
                require(BrowserPolicy.isSafeHttps(result.sourceUrl) && result.fields.keys.all { it in adapter.descriptor.outputFields })
            } else if(result is IntegrationResult.OfficialWebsiteRequired) {
                require(BrowserPolicy.isSafeHttps(result.url))
            }
            result
        } catch (error: CancellationException) {throw error}
        catch (_: Exception) {IntegrationResult.OfficialWebsiteRequired(site.url,"Provider unavailable; use the official website")}
    }
}
