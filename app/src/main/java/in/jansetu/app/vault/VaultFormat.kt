package `in`.jansetu.app.vault

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.Locale
import java.util.UUID

/** The only file types the vault accepts. Detected from content, never from the name alone. */
@Serializable
enum class DocumentType(val mime: String, val extension: String, val label: String) {
    PDF("application/pdf", "pdf", "PDF"),
    JPEG("image/jpeg", "jpg", "JPEG image"),
    PNG("image/png", "png", "PNG image");
    val isImage: Boolean get() = this != PDF
    companion object {
        val mimeTypes: Array<String> get() = entries.map { it.mime }.toTypedArray()
    }
}

/** A user-chosen label. It never says the document is valid or accepted by any office. */
@Serializable
enum class DocumentCategory(val label: String) {
    IDENTITY("Identity proof"), ADDRESS("Address proof"), INCOME("Income"), EDUCATION("Education"),
    COMMUNITY("Caste or community"), BIRTH("Birth or age"), PHOTO("Photo or signature"),
    CERTIFICATE("Other certificate"), OTHER("Other")
}

/** Encrypted metadata for one saved document. The original path and source URI are never stored. */
@Serializable
data class VaultDocument(
    val id: String, val name: String, val category: DocumentCategory, val type: DocumentType,
    val sizeBytes: Long, val sha256: String, val importedAt: String, val updatedAt: String,
    val revision: Long = 1, val previewAvailable: Boolean = true, val formatVersion: Int = 1,
) {
    /** The name used when the person exports or chooses this document elsewhere. */
    val fileName: String get() = "$name.${type.extension}"
    override fun toString() = "VaultDocument(redacted)"
}

object VaultLimits {
    const val MAX_FILE_BYTES = 15L * 1024 * 1024
    const val MAX_DOCUMENTS = 300
    const val MAX_TOTAL_BYTES = 500L * 1024 * 1024
    /** Kept free on the device after an import, so the vault never fills the storage completely. */
    const val FREE_SPACE_RESERVE = 64L * 1024 * 1024
    const val NAME_MAX = 120
    /** Larger images are refused rather than decoded (decompression-bomb guard). */
    const val MAX_IMAGE_PIXELS = 60_000_000L
    const val MAX_PDF_PAGES = 2_000
}

/** Why an import was refused. Every message tells the person what to do next. */
enum class ImportProblem(val message: String) {
    EMPTY("This file is empty. Choose the original document again."),
    TOO_LARGE("This file is larger than 15 MB. Save a smaller copy (for example, a compressed PDF) and try again."),
    UNSUPPORTED("Only PDF, JPEG and PNG files can be saved. Convert the file to one of these types and try again."),
    MISMATCH("The file's contents do not match its stated type. It may be damaged or renamed. Choose the original file."),
    MALFORMED("This file appears damaged and could not be read. Choose the original file or a fresh copy."),
    DUPLICATE("This file is already saved in My Documents."),
    TOO_MANY("My Documents already holds 300 documents. Delete some you no longer need, then try again."),
    QUOTA("My Documents can use up to 500 MB. Delete some documents, then try again."),
    NO_SPACE("There is not enough free storage on this device. Free up space, then try again."),
    KEY_UNAVAILABLE("New documents cannot be saved while earlier documents are locked. Read the notice on My Documents first."),
    UNREADABLE_SOURCE("The file could not be read from where it is stored. Check that it is still available and try again."),
    STORAGE("The document could not be saved. Nothing was changed. Try again.")
}

class VaultImportException(val problem: ImportProblem) : Exception(problem.message)
class VaultStorageException : Exception("My Documents could not be read or saved. Existing documents have not been deleted.")
class VaultConflictException : Exception("This document changed. Reopen it before editing.")

object DocumentSignature {
    private val PNG = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
    private val PDF = "%PDF-".toByteArray(Charsets.US_ASCII)

    /** Detects the type from the first bytes. A PDF header may follow up to 1 KiB of leading bytes, as the format allows. */
    fun detect(head: ByteArray): DocumentType? = when {
        head.size >= 8 && head.copyOfRange(0, 8).contentEquals(PNG) -> DocumentType.PNG
        head.size >= 3 && head[0] == 0xFF.toByte() && head[1] == 0xD8.toByte() && head[2] == 0xFF.toByte() -> DocumentType.JPEG
        indexOf(head, PDF, limit = 1024) >= 0 -> DocumentType.PDF
        else -> null
    }

    /** Cheap structural checks on the first and last KiB; deeper decoding happens on the device. */
    fun structurallyValid(type: DocumentType, head: ByteArray, tail: ByteArray): Boolean = when (type) {
        // IHDR must be the first chunk; IEND closes the file (trailing bytes are tolerated up to the tail window).
        DocumentType.PNG -> head.size >= 33 && String(head, 12, 4, Charsets.US_ASCII) == "IHDR" &&
            indexOf(tail, "IEND".toByteArray(Charsets.US_ASCII)) >= 0
        DocumentType.JPEG -> lastIndexOf(tail, byteArrayOf(0xFF.toByte(), 0xD9.toByte())) >= 0
        DocumentType.PDF -> indexOf(tail, "%%EOF".toByteArray(Charsets.US_ASCII)) >= 0
    }

    internal fun indexOf(data: ByteArray, pattern: ByteArray, limit: Int = data.size): Int {
        val end = minOf(data.size, limit + pattern.size) - pattern.size
        outer@ for (i in 0..end) {
            for (j in pattern.indices) if (data[i + j] != pattern[j]) continue@outer
            return i
        }
        return -1
    }

    private fun lastIndexOf(data: ByteArray, pattern: ByteArray): Int {
        outer@ for (i in data.size - pattern.size downTo 0) {
            for (j in pattern.indices) if (data[i + j] != pattern[j]) continue@outer
            return i
        }
        return -1
    }
}

object VaultRules {
    private val GENERIC = setOf(null, "", "application/octet-stream", "*/*", "binary/octet-stream")

    /** A stated MIME type must agree with the content; a missing or generic one defers to the content. */
    fun declaredMatches(declaredMime: String?, detected: DocumentType): Boolean {
        val mime = declaredMime?.lowercase(Locale.ROOT)?.substringBefore(';')?.trim()
        if (mime in GENERIC) return true
        val aliases = when (detected) {
            DocumentType.PDF -> setOf("application/pdf", "application/x-pdf")
            DocumentType.JPEG -> setOf("image/jpeg", "image/jpg", "image/pjpeg")
            DocumentType.PNG -> setOf("image/png")
        }
        return mime in aliases
    }

    /** Removes control and direction-override characters and characters that file pickers reject. */
    fun normaliseName(name: String): String =
        name.replace(Regex("[\\p{Cntrl}\\u200B-\\u200F\\u202A-\\u202E\\u2066-\\u2069]"), "")
            .replace(Regex("[\\\\/:*?\"<>|]"), " ").replace(Regex("\\s+"), " ").trim()
            .take(VaultLimits.NAME_MAX).trim()

    /** A display name from the picker, with any path and the file extension removed. */
    fun cleanName(raw: String?): String {
        val base = raw.orEmpty().substringAfterLast('/').substringAfterLast('\\')
        return normaliseName(base.replace(Regex("\\.(pdf|jpe?g|png)$", RegexOption.IGNORE_CASE), "")).ifEmpty { "Document" }
    }

    fun nameErrors(name: String): List<String> = buildList {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || trimmed.length > VaultLimits.NAME_MAX) add("Enter a name of 1–${VaultLimits.NAME_MAX} characters.")
        else if (trimmed != normaliseName(trimmed)) add("Names cannot contain / \\ : * ? \" < > | or control characters.")
    }

    /** Refuses before reading when the declared size or current usage already rules the import out. */
    fun capacityProblem(count: Int, usedBytes: Long, incomingBytes: Long?, usableBytes: Long): ImportProblem? = when {
        count >= VaultLimits.MAX_DOCUMENTS -> ImportProblem.TOO_MANY
        incomingBytes != null && incomingBytes > VaultLimits.MAX_FILE_BYTES -> ImportProblem.TOO_LARGE
        incomingBytes != null && usedBytes + incomingBytes > VaultLimits.MAX_TOTAL_BYTES -> ImportProblem.QUOTA
        usableBytes < (incomingBytes ?: VaultLimits.MAX_FILE_BYTES) + VaultLimits.FREE_SPACE_RESERVE -> ImportProblem.NO_SPACE
        else -> null
    }

    fun validateStored(d: VaultDocument) {
        UUID.fromString(d.id); Instant.parse(d.importedAt); Instant.parse(d.updatedAt)
        require(d.name.isNotBlank() && d.name == normaliseName(d.name))
        require(d.sizeBytes in 1..VaultLimits.MAX_FILE_BYTES && d.sha256.matches(Regex("[0-9a-f]{64}")))
        require(d.revision > 0 && d.formatVersion == 1)
    }
}

enum class DocumentSort(val label: String) { NEWEST("Newest first"), OLDEST("Oldest first"), NAME("Name (A–Z)") }

object DocumentQuery {
    fun visible(documents: List<VaultDocument>, text: String, category: DocumentCategory?, type: DocumentType?, sort: DocumentSort): List<VaultDocument> {
        val terms = text.lowercase(Locale.ROOT).split(Regex("\\s+")).filter { it.isNotBlank() }
        val filtered = documents.filter { d ->
            (category == null || d.category == category) && (type == null || d.type == type) &&
                terms.all { term -> d.name.lowercase(Locale.ROOT).contains(term) || d.category.label.lowercase(Locale.ROOT).contains(term) }
        }
        return when (sort) {
            DocumentSort.NEWEST -> filtered.sortedWith(compareByDescending<VaultDocument> { it.importedAt }.thenBy { it.id })
            DocumentSort.OLDEST -> filtered.sortedWith(compareBy<VaultDocument> { it.importedAt }.thenBy { it.id })
            DocumentSort.NAME -> filtered.sortedWith(compareBy<VaultDocument> { it.name.lowercase(Locale.ROOT) }.thenBy { it.id })
        }
    }

    fun sizeLabel(bytes: Long): String = when {
        bytes < 1024 -> "$bytes bytes"
        bytes < 1024 * 1024 -> "%.0f KB".format(Locale.ROOT, bytes / 1024.0)
        bytes % (1024 * 1024) == 0L -> "${bytes / (1024 * 1024)} MB"
        else -> "%.1f MB".format(Locale.ROOT, bytes / (1024.0 * 1024.0))
    }
}
