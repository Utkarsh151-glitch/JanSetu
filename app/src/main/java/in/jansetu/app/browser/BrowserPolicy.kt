package `in`.jansetu.app.browser

import java.net.URI
import java.net.URLDecoder

/** Only the explicitly configured portal host stays inside JanSetu's WebView. */
object BrowserPolicy {
    fun isSafeHttps(url: String): Boolean = try {
        val uri = URI(url)
        uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank() &&
            uri.userInfo == null && uri.port in listOf(-1, 443) &&
            !hasUnsafeEscapes(uri)
    } catch (_: Exception) { false }

    fun staysInApp(url: String, officialUrl: String): Boolean {
        if (!isSafeHttps(url) || !isSafeHttps(officialUrl)) return false
        val destination = URI(url).host.lowercase().removePrefix("www.")
        val official = URI(officialUrl).host.lowercase().removePrefix("www.")
        val destinationUri = URI(url)
        val journey = canonicalJourney(destinationUri)
        // Login, payment, uploads, and application submission may process personal
        // data. JanSetu's embedded browser is deliberately read-only for those flows.
        val externalJourney = Regex("(?:^|[/?#&=:_-])(login|signin|sign-in|register|signup|auth|oauth2?|payment|checkout|upload|application)(?:$|[/?#&=._-])")
            .containsMatchIn(journey)
        return destination == official && !externalJourney
    }

    private fun hasUnsafeEscapes(uri: URI): Boolean = listOf(uri.rawPath, uri.rawQuery, uri.rawFragment)
        .filterNotNull()
        .any { raw ->
            Regex("%(?:0d|0a|5c)", RegexOption.IGNORE_CASE).containsMatchIn(raw) ||
                canonicalize(raw).any { it == '\r' || it == '\n' || it == '\\' }
        }

    /** Decode nested URL escapes before looking for a sensitive journey. */
    private fun canonicalJourney(uri: URI): String {
        return canonicalize(listOf(uri.rawPath, uri.rawQuery, uri.rawFragment).filterNotNull().joinToString("/")).lowercase()
    }

    private fun canonicalize(input: String): String {
        var value = input
        repeat(3) {
            val decoded = runCatching { URLDecoder.decode(value, "UTF-8") }.getOrDefault(value)
            if (decoded == value) return value
            value = decoded
        }
        return value
    }
}
