package `in`.jansetu.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import `in`.jansetu.app.vault.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.KeyStore
import java.util.UUID

/** Synthetic documents only: generated shapes and text, never real identity documents or personal data. */
object SyntheticDocuments {
    const val MARKER = "JANSETU-SYNTHETIC-TEST-MARKER-7f3a"

    private fun bitmap(width: Int = 60, height: Int = 40) = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        eraseColor(Color.rgb(20, 120, 90)); setPixel(3, 3, Color.WHITE)
    }

    fun png(width: Int = 60, height: Int = 40): ByteArray = ByteArrayOutputStream().also { bitmap(width, height).compress(Bitmap.CompressFormat.PNG, 100, it) }.toByteArray()

    /** A JPEG carrying [MARKER] in a comment segment, so storage can be searched for plaintext. */
    fun jpegWithMarker(): ByteArray {
        val jpeg = ByteArrayOutputStream().also { bitmap().compress(Bitmap.CompressFormat.JPEG, 90, it) }.toByteArray()
        val comment = MARKER.toByteArray()
        val length = comment.size + 2
        val segment = byteArrayOf(0xFF.toByte(), 0xFE.toByte(), (length shr 8).toByte(), length.toByte()) + comment
        return jpeg.copyOfRange(0, 2) + segment + jpeg.copyOfRange(2, jpeg.size)
    }

    fun pdf(pages: Int = 2): ByteArray {
        val document = PdfDocument()
        try {
            repeat(pages) { index ->
                val page = document.startPage(PdfDocument.PageInfo.Builder(300, 400, index + 1).create())
                page.canvas.drawText("Synthetic JanSetu test page ${index + 1}", 20f, 40f, Paint().apply { textSize = 14f })
                document.finishPage(page)
            }
            return ByteArrayOutputStream().also { document.writeTo(it) }.toByteArray()
        } finally { document.close() }
    }

    /** A PNG signature, a valid header and an end marker, with no image data. */
    fun headerOnlyPng(): ByteArray {
        val full = png()
        val ihdrEnd = 8 + 4 + 4 + 13 + 4
        val iend = byteArrayOf(0, 0, 0, 0) + "IEND".toByteArray() + byteArrayOf(0xAE.toByte(), 0x42, 0x60, 0x82.toByte())
        return full.copyOfRange(0, ihdrEnd) + iend
    }

    fun source(bytes: ByteArray, name: String = "Synthetic file", mime: String? = null, size: Long? = bytes.size.toLong()) =
        VaultImportSource(name, mime, size) { ByteArrayInputStream(bytes) }
}

/** An isolated vault: its own directory, Keystore aliases and preferences, removed afterwards. */
class VaultFixture(private val context: Context) : AutoCloseable {
    private val unique = UUID.randomUUID().toString()
    val directory = File(context.noBackupFilesDir, "vault-test-$unique")
    val keyAlias = "vault-test-$unique"
    val macAlias = "vault-picker-test-$unique"
    val prefsName = "vault-test-prefs-$unique"
    val keys = KeystoreVaultKeys(keyAlias)
    val proxy = VaultProxyFiles(context)
    var repository = EncryptedVaultRepository(context, directory, keys, AndroidDocumentInspector(proxy)); private set
    val picker = VaultPickerAccess(context, prefsName, macAlias)
    val dependencies get() = VaultDependencies(repository, picker, proxy)

    /** Simulates an app restart: a new repository over the same files and key. */
    fun reopen(): EncryptedVaultRepository { repository.close(); repository = EncryptedVaultRepository(context, directory, keys, AndroidDocumentInspector(proxy)); return repository }

    override fun close() {
        VaultDependencies.testOverride = null
        runCatching { repository.close() }
        directory.deleteRecursively()
        KeyStore.getInstance("AndroidKeyStore").apply { load(null); deleteEntry(keyAlias); deleteEntry(macAlias) }
        context.deleteSharedPreferences(prefsName)
    }
}
