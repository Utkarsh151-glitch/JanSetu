package `in`.jansetu.app.catalog

import android.content.Context
import android.util.AtomicFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.File
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

@Serializable private data class UpdateEnvelope(val manifest: String, val catalog: String, val signature: String)
@Serializable private data class UpdateManifest(
    val format: Int, val scope: String, val version: Int, val websites: Int,
    val sha256: String, val bytes: Int, val generatedAt: String
)
data class VerifiedCatalogUpdate(val catalog: Catalog, val packageBytes: ByteArray)

object CatalogUpdateVerifier {
    const val MAX_PACKAGE_BYTES = 24_000_000
    private val json = Json { ignoreUnknownKeys = false }

    fun readBounded(input: InputStream): ByteArray {
        val result = java.io.ByteArrayOutputStream()
        val buffer = ByteArray(16_384)
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            require(result.size().toLong() + count <= MAX_PACKAGE_BYTES) { "Update file is too large" }
            result.write(buffer, 0, count)
        }
        return result.toByteArray()
    }

    fun verify(bytes: ByteArray, publicKey: ByteArray, minimumVersion: Int, allowCurrent: Boolean = false): VerifiedCatalogUpdate {
        require(bytes.size <= MAX_PACKAGE_BYTES) { "Update file is too large" }
        val envelope = json.decodeFromString<UpdateEnvelope>(bytes.toString(Charsets.UTF_8))
        val decoder = Base64.getDecoder()
        val manifestBytes = decoder.decode(envelope.manifest)
        require(manifestBytes.size <= 4096) { "Invalid update manifest" }
        val verifier = Signature.getInstance("SHA256withECDSA")
        verifier.initVerify(KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(publicKey)))
        verifier.update(manifestBytes)
        require(verifier.verify(decoder.decode(envelope.signature))) { "Update signature is not trusted" }
        val manifest = json.decodeFromString<UpdateManifest>(manifestBytes.toString(Charsets.UTF_8))
        require(manifest.format == 1 && manifest.scope == "local_development") { "Unsupported update format or scope" }
        require(manifest.version > minimumVersion || allowCurrent && manifest.version == minimumVersion) { "Update is not newer than this catalog" }
        require(manifest.bytes in 1..16_000_000 && manifest.websites in 1..50_000) { "Invalid update size" }
        val payload = decoder.decode(envelope.catalog)
        require(payload.size == manifest.bytes) { "Update size does not match" }
        val digest = MessageDigest.getInstance("SHA-256").digest(payload).joinToString("") { "%02x".format(it) }
        require(digest == manifest.sha256) { "Update content has changed" }
        val catalog = CatalogParser.parse(payload.toString(Charsets.UTF_8))
        require(catalog.version == manifest.version && catalog.websites.size == manifest.websites) { "Update manifest does not match catalog" }
        return VerifiedCatalogUpdate(catalog, bytes)
    }
}

/** App-private signed package. Atomic replacement keeps the last good update. */
class LocalCatalogRepository(private val context: Context, directory: File = context.filesDir,
    private val updatePublicKey: ByteArray? = null) : CatalogRepository {
    private val file = AtomicFile(File(directory.apply { mkdirs() }, "catalog-update.jansetu"))
    var source: String = "bundled"
        private set
    var recoveredInvalidUpdate: Boolean = false
        private set

    override suspend fun load(): Catalog = withContext(Dispatchers.IO) {
        recoveredInvalidUpdate = false
        val bundled = CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() })
        val update = if (file.baseFile.exists() || File(file.baseFile.path + ".bak").exists()) {
            try {
                val bytes = file.openRead().use { CatalogUpdateVerifier.readBounded(it) }
                CatalogUpdateVerifier.verify(bytes, key(), bundled.version, allowCurrent = true).catalog
            } catch (_: Exception) { recoveredInvalidUpdate = true; null }
        } else null
        source = if (update != null) "local_update" else "bundled"
        (update ?: bundled).also { CitizenSearch.prepare(it) }
    }

    suspend fun install(input: InputStream, currentVersion: Int): Catalog = withContext(Dispatchers.IO) {
        val update = input.use { CatalogUpdateVerifier.verify(CatalogUpdateVerifier.readBounded(it), key(), currentVersion) }
        var stream: java.io.FileOutputStream? = null
        try {
            stream = file.startWrite()
            stream.write(update.packageBytes)
            file.finishWrite(stream)
        } catch (error: Exception) { file.failWrite(stream); throw error }
        source = "local_update"
        recoveredInvalidUpdate = false
        update.catalog.also { CitizenSearch.prepare(it) }
    }

    suspend fun restoreBundled() = withContext(Dispatchers.IO) { file.delete(); source = "bundled"; recoveredInvalidUpdate = false }
    private fun key(): ByteArray = updatePublicKey ?: context.assets.open("catalog-update-public.der").use { it.readBytes() }
}
