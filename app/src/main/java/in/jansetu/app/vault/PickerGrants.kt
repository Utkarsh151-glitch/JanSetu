package `in`.jansetu.app.vault

import java.security.MessageDigest
import java.time.Clock
import java.time.Duration
import java.util.Base64
import java.util.UUID

/**
 * Document IDs handed to Android's file picker. Each one names a document, an expiry and a MAC under a key
 * only this app holds, so a website or app that received a document can keep re-reading it only until the
 * expiry. Rotating the key ([mac] changes) withdraws every outstanding ID at once.
 */
class PickerGrants(private val mac: (ByteArray) -> ByteArray, private val clock: Clock = Clock.systemUTC(),
    private val validity: Duration = VALIDITY) {
    fun issue(documentId: String): String {
        UUID.fromString(documentId)
        val expiry = clock.instant().plus(validity).epochSecond
        return "$documentId.$expiry.${tag(documentId, expiry)}"
    }

    /** The document ID if [token] is genuine and unexpired; otherwise null. */
    fun resolve(token: String): String? {
        val parts = token.split('.')
        if (parts.size != 3) return null
        val documentId = parts[0].takeIf { runCatching { UUID.fromString(it) }.isSuccess } ?: return null
        val expiry = parts[1].toLongOrNull() ?: return null
        if (expiry <= clock.instant().epochSecond || expiry > clock.instant().plus(validity).epochSecond + 60) return null
        val expected = tag(documentId, expiry)
        return documentId.takeIf { MessageDigest.isEqual(expected.toByteArray(), parts[2].toByteArray()) }
    }

    private fun tag(documentId: String, expiry: Long): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(mac("picker:v1:$documentId:$expiry".toByteArray()).copyOf(16))

    companion object {
        /** Long enough for a person to finish a typical online form after choosing the file. */
        val VALIDITY: Duration = Duration.ofHours(1)
        const val ROOT_ID = "jansetu-documents"
    }
}
