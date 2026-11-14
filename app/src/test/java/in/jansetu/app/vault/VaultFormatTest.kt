package `in`.jansetu.app.vault

import org.junit.Assert.*
import org.junit.Test

class VaultFormatTest {
    private val png = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 13) + "IHDR".toByteArray() + ByteArray(17)
    private val pngTail = ByteArray(4) + "IEND".toByteArray() + byteArrayOf(0xAE.toByte(), 0x42, 0x60, 0x82.toByte())
    private val jpeg = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte()) + ByteArray(40)
    private val jpegTail = ByteArray(10) + byteArrayOf(0xFF.toByte(), 0xD9.toByte())
    private val pdf = "%PDF-1.7\n".toByteArray() + ByteArray(40)
    private val pdfTail = "trailer\n<<>>\nstartxref\n0\n%%EOF\n".toByteArray()

    @Test fun signaturesComeFromContentNotNames() {
        assertEquals(DocumentType.PNG, DocumentSignature.detect(png))
        assertEquals(DocumentType.JPEG, DocumentSignature.detect(jpeg))
        assertEquals(DocumentType.PDF, DocumentSignature.detect(pdf))
        // A PDF header may follow a short preamble, as the format allows.
        assertEquals(DocumentType.PDF, DocumentSignature.detect(ByteArray(100) + pdf))
        assertNull(DocumentSignature.detect(ByteArray(2000) + pdf))
        assertNull(DocumentSignature.detect("GIF89a".toByteArray() + ByteArray(20)))
        assertNull(DocumentSignature.detect("PK\u0003\u0004 renamed.pdf".toByteArray()))
        assertNull(DocumentSignature.detect(ByteArray(0)))
    }

    @Test fun truncatedOrHeaderOnlyFilesAreMalformed() {
        assertTrue(DocumentSignature.structurallyValid(DocumentType.PNG, png, pngTail))
        assertFalse(DocumentSignature.structurallyValid(DocumentType.PNG, png, ByteArray(12)))
        assertFalse(DocumentSignature.structurallyValid(DocumentType.PNG, png.copyOf(20), pngTail))
        assertTrue(DocumentSignature.structurallyValid(DocumentType.JPEG, jpeg, jpegTail))
        assertFalse(DocumentSignature.structurallyValid(DocumentType.JPEG, jpeg, ByteArray(12)))
        assertTrue(DocumentSignature.structurallyValid(DocumentType.PDF, pdf, pdfTail))
        assertFalse(DocumentSignature.structurallyValid(DocumentType.PDF, pdf, "trailer".toByteArray()))
    }

    @Test fun declaredTypeMustAgreeWithContent() {
        assertTrue(VaultRules.declaredMatches("application/pdf", DocumentType.PDF))
        assertTrue(VaultRules.declaredMatches("IMAGE/JPEG; charset=binary", DocumentType.JPEG))
        assertTrue(VaultRules.declaredMatches(null, DocumentType.PNG))
        assertTrue(VaultRules.declaredMatches("application/octet-stream", DocumentType.PNG))
        assertFalse(VaultRules.declaredMatches("image/png", DocumentType.JPEG))
        assertFalse(VaultRules.declaredMatches("application/pdf", DocumentType.PNG))
        assertFalse(VaultRules.declaredMatches("text/html", DocumentType.PDF))
    }

    @Test fun namesNeverKeepPathsExtensionsOrControlCharacters() {
        assertEquals("Income certificate", VaultRules.cleanName("/storage/emulated/0/Download/Income certificate.PDF"))
        assertEquals("scan", VaultRules.cleanName("C:\\Users\\x\\scan.jpeg"))
        assertEquals("Document", VaultRules.cleanName(null))
        assertEquals("Document", VaultRules.cleanName("   .pdf"))
        // Direction overrides could disguise "evil\u202Efdp.exe" as a PDF; they are removed.
        assertEquals("abcevil", VaultRules.cleanName("abc\u202Eevil\u0000"))
        assertEquals(VaultLimits.NAME_MAX, VaultRules.cleanName("a".repeat(500)).length)
        assertTrue(VaultRules.nameErrors("Caste certificate").isEmpty())
        assertTrue(VaultRules.nameErrors("scan.pdf").isEmpty())
        assertFalse(VaultRules.nameErrors("").isEmpty())
        assertFalse(VaultRules.nameErrors("a/b").isEmpty())
        assertFalse(VaultRules.nameErrors("x".repeat(121)).isEmpty())
    }

    @Test fun capacityIsCheckedBeforeReading() {
        val plenty = 10L * 1024 * 1024 * 1024
        assertNull(VaultRules.capacityProblem(0, 0, 1000, plenty))
        assertNull(VaultRules.capacityProblem(0, 0, null, plenty))
        assertEquals(ImportProblem.TOO_MANY, VaultRules.capacityProblem(VaultLimits.MAX_DOCUMENTS, 0, 10, plenty))
        assertEquals(ImportProblem.TOO_LARGE, VaultRules.capacityProblem(0, 0, VaultLimits.MAX_FILE_BYTES + 1, plenty))
        assertEquals(ImportProblem.QUOTA, VaultRules.capacityProblem(1, VaultLimits.MAX_TOTAL_BYTES - 10, 11, plenty))
        assertEquals(ImportProblem.NO_SPACE, VaultRules.capacityProblem(0, 0, 1000, VaultLimits.FREE_SPACE_RESERVE))
        // An unknown size assumes the largest file, so a nearly full device is refused up front.
        assertEquals(ImportProblem.NO_SPACE, VaultRules.capacityProblem(0, 0, null, VaultLimits.FREE_SPACE_RESERVE + 1000))
    }

    @Test fun everyImportProblemTellsThePersonWhatToDo() {
        ImportProblem.entries.forEach { assertTrue(it.name, it.message.length > 30 && it.message.endsWith(".")) }
        assertFalse(ImportProblem.entries.any { it.message.contains("Exception") })
    }

    @Test fun searchFiltersAndSortsWithoutChangingDocuments() {
        fun doc(name: String, category: DocumentCategory, type: DocumentType, at: String) =
            VaultDocument(java.util.UUID.randomUUID().toString(), name, category, type, 10, "a".repeat(64), at, at)
        val docs = listOf(doc("Income certificate 2025", DocumentCategory.INCOME, DocumentType.PDF, "2026-10-01T10:00:00Z"),
            doc("Passport photo", DocumentCategory.PHOTO, DocumentType.JPEG, "2026-10-03T10:00:00Z"),
            doc("Marksheet", DocumentCategory.EDUCATION, DocumentType.PNG, "2026-10-02T10:00:00Z"))
        assertEquals(listOf("Passport photo", "Marksheet", "Income certificate 2025"),
            DocumentQuery.visible(docs, "", null, null, DocumentSort.NEWEST).map { it.name })
        assertEquals(listOf("Income certificate 2025"), DocumentQuery.visible(docs, "income 2025", null, null, DocumentSort.NAME).map { it.name })
        assertEquals(listOf("Passport photo"), DocumentQuery.visible(docs, "", DocumentCategory.PHOTO, null, DocumentSort.NAME).map { it.name })
        assertEquals(listOf("Marksheet"), DocumentQuery.visible(docs, "", null, DocumentType.PNG, DocumentSort.NAME).map { it.name })
        assertEquals(listOf("Marksheet"), DocumentQuery.visible(docs, "education", null, null, DocumentSort.NAME).map { it.name })
        assertTrue(DocumentQuery.visible(docs, "aadhaar", null, null, DocumentSort.NAME).isEmpty())
        assertEquals("Income certificate 2025.pdf", docs[0].fileName)
        assertEquals(listOf("862 bytes", "12 KB", "1.5 MB", "500 MB"),
            listOf(862L, 12_288L, 1_572_864L, VaultLimits.MAX_TOTAL_BYTES).map(DocumentQuery::sizeLabel))
        assertEquals("VaultDocument(redacted)", docs[0].toString())
    }

    @Test fun storedMetadataIsValidated() {
        val ok = VaultDocument(java.util.UUID.randomUUID().toString(), "Photo", DocumentCategory.PHOTO, DocumentType.PNG, 10, "b".repeat(64),
            "2026-10-18T00:00:00Z", "2026-10-18T00:00:00Z")
        VaultRules.validateStored(ok)
        listOf(ok.copy(id = "not-a-uuid"), ok.copy(name = " "), ok.copy(name = "a/b"), ok.copy(sizeBytes = 0),
            ok.copy(sizeBytes = VaultLimits.MAX_FILE_BYTES + 1), ok.copy(sha256 = "xyz"), ok.copy(revision = 0), ok.copy(formatVersion = 2))
            .forEach { bad -> assertTrue(runCatching { VaultRules.validateStored(bad) }.isFailure) }
    }
}
