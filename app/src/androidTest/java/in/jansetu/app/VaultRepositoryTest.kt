package `in`.jansetu.app

import android.graphics.pdf.PdfRenderer
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.vault.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.time.Instant

class VaultRepositoryTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private fun fixture(block: (VaultFixture) -> Unit) = VaultFixture(context).use(block)
    private fun problem(block: () -> Unit) = (runCatching(block).exceptionOrNull() as? VaultImportException)?.problem
    private fun storedFiles(f: VaultFixture) = f.directory.walkTopDown().filter { it.isFile }.toList()

    @Test fun importListOpenRenameDeleteAndRestartLifecycle() = fixture { f ->
        val png = SyntheticDocuments.png(); val pdf = SyntheticDocuments.pdf(2)
        val photo = f.repository.import(SyntheticDocuments.source(png, "/sdcard/Download/My photo.png", "image/png"), DocumentCategory.PHOTO)
        val certificate = f.repository.import(SyntheticDocuments.source(pdf, "certificate.pdf", "application/pdf"), DocumentCategory.CERTIFICATE, "Income certificate")
        assertEquals("My photo", photo.name)
        assertEquals(DocumentType.PNG, photo.type)
        assertEquals("Income certificate", certificate.name)
        assertEquals(DocumentType.PDF, certificate.type)
        assertTrue(certificate.previewAvailable)
        assertEquals(listOf(photo.id, certificate.id).sorted(), f.repository.snapshot().documents.map { it.id }.sorted())
        assertArrayEquals(png, f.repository.open(photo.id).use { it.readAll() })
        assertArrayEquals(pdf, f.repository.open(certificate.id).use { it.readAll() })

        val renamed = f.repository.update(photo.id, "Passport-size photo", DocumentCategory.PHOTO, photo.revision)
        assertEquals(2L, renamed.revision)
        assertThrows(VaultConflictException::class.java) { f.repository.update(photo.id, "Stale edit", DocumentCategory.OTHER, photo.revision) }

        // Restart: a new repository over the same storage and key sees the same documents and contents.
        val reopened = f.reopen()
        assertEquals(renamed, reopened.get(photo.id))
        assertArrayEquals(pdf, reopened.open(certificate.id).use { it.readAll() })

        reopened.delete(photo.id)
        assertNull(reopened.get(photo.id))
        assertFalse(File(f.directory, "objects/${photo.id}.jsv").exists())
        assertNotNull(reopened.get(certificate.id))
        assertEquals(1, reopened.snapshot().documents.size)
    }

    @Test fun invalidFilesAreRefusedWithAReasonAndLeaveNothingBehind() = fixture { f ->
        val png = SyntheticDocuments.png()
        f.repository.import(SyntheticDocuments.source(png, "first.png"), DocumentCategory.PHOTO)
        val before = storedFiles(f).map { it.name }.toSet()
        assertEquals(ImportProblem.UNSUPPORTED, problem { f.repository.import(SyntheticDocuments.source("GIF89a not supported".toByteArray(), "a.gif"), DocumentCategory.OTHER) })
        assertEquals(ImportProblem.UNSUPPORTED, problem { f.repository.import(SyntheticDocuments.source("<html>renamed</html>".toByteArray(), "page.pdf", "application/pdf"), DocumentCategory.OTHER) })
        assertEquals(ImportProblem.MISMATCH, problem { f.repository.import(SyntheticDocuments.source(SyntheticDocuments.png(30, 30), "photo.jpg", "image/jpeg"), DocumentCategory.PHOTO) })
        assertEquals(ImportProblem.MALFORMED, problem { f.repository.import(SyntheticDocuments.source(png.copyOf(png.size / 2), "cut.png"), DocumentCategory.PHOTO) })
        assertEquals(ImportProblem.MALFORMED, problem { f.repository.import(SyntheticDocuments.source(SyntheticDocuments.headerOnlyPng(), "empty-image.png"), DocumentCategory.PHOTO) })
        assertEquals(ImportProblem.MALFORMED, problem { f.repository.import(SyntheticDocuments.source("%PDF-1.4\nnot really a pdf\n%%EOF\n".toByteArray(), "fake.pdf"), DocumentCategory.OTHER) })
        assertEquals(ImportProblem.EMPTY, problem { f.repository.import(SyntheticDocuments.source(ByteArray(0), "empty.pdf"), DocumentCategory.OTHER) })
        assertEquals(ImportProblem.TOO_LARGE, problem { f.repository.import(SyntheticDocuments.source(png, "huge.png", size = VaultLimits.MAX_FILE_BYTES + 1), DocumentCategory.OTHER) })
        // An undeclared size is enforced while streaming.
        val oversized = SyntheticDocuments.pdf(1).let { it + ByteArray((VaultLimits.MAX_FILE_BYTES - it.size + 10).toInt()) }
        assertEquals(ImportProblem.TOO_LARGE, problem { f.repository.import(SyntheticDocuments.source(oversized, "big.pdf", size = null), DocumentCategory.OTHER) })
        assertEquals(ImportProblem.DUPLICATE, problem { f.repository.import(SyntheticDocuments.source(png, "again.png"), DocumentCategory.PHOTO) })
        val failing = VaultImportSource("broken", null, null) { object : InputStream() {
            var count = 0
            override fun read(): Int = if (count++ < 5000) 0x25 else throw IOException("Provider went away")
            override fun read(b: ByteArray, off: Int, len: Int): Int = if (count++ < 2) { b.fill(0x25, off, off + len); len } else throw IOException("Provider went away")
        } }
        assertEquals(ImportProblem.UNREADABLE_SOURCE, problem { f.repository.import(failing, DocumentCategory.OTHER) })
        assertEquals(ImportProblem.UNREADABLE_SOURCE, problem { f.repository.import(VaultImportSource("gone", null, null) { throw SecurityException("revoked") }, DocumentCategory.OTHER) })
        assertEquals(before, storedFiles(f).map { it.name }.toSet())
        assertEquals(1, f.repository.snapshot().documents.size)
    }

    @Test fun storageIsPrivateExcludedFromBackupAndHoldsNoPlaintext() = fixture { f ->
        val doc = f.repository.import(SyntheticDocuments.source(SyntheticDocuments.jpegWithMarker(), "${SyntheticDocuments.MARKER}.jpg", "image/jpeg"),
            DocumentCategory.IDENTITY, "Name ${SyntheticDocuments.MARKER}")
        assertTrue(f.directory.canonicalPath.startsWith(context.noBackupFilesDir.canonicalPath))
        val files = storedFiles(f)
        assertTrue(files.any { it.name == "${doc.id}.jsv" })
        val marker = SyntheticDocuments.MARKER.toByteArray()
        files.forEach { file -> assertEquals("Plaintext found in ${file.name}", -1, DocumentSignature.indexOf(file.readBytes(), marker)) }
        // No plaintext copy appears anywhere else in the app's storage either.
        listOf(context.cacheDir, context.filesDir, context.codeCacheDir).forEach { dir ->
            dir.walkTopDown().filter { it.isFile && it.length() < 20_000_000 }.forEach { file ->
                assertEquals("Plaintext found in ${file.path}", -1, DocumentSignature.indexOf(file.readBytes(), marker))
            }
        }
        assertTrue(f.repository.open(doc.id).use { reader -> DocumentSignature.indexOf(reader.readAll(), marker) >= 0 })
        assertFalse(storedFiles(f).any { it.name.contains("Name") || it.name.contains(".jpg") })
    }

    @Test fun losingTheKeyLocksDocumentsButNeverDeletesThem() = fixture { f ->
        val doc = f.repository.import(SyntheticDocuments.source(SyntheticDocuments.png(), "photo.png"), DocumentCategory.PHOTO)
        val file = File(f.directory, "objects/${doc.id}.jsv")
        KeyStore.getInstance("AndroidKeyStore").apply { load(null); deleteEntry(f.keyAlias) }
        val locked = f.reopen().snapshot()
        assertEquals(VaultKeyState.LOCKED, locked.keyState)
        assertEquals(listOf(doc.id), locked.unreadableIds)
        assertTrue(locked.documents.isEmpty())
        assertTrue(file.exists())
        assertEquals(ImportProblem.KEY_UNAVAILABLE, problem { f.repository.import(SyntheticDocuments.source(SyntheticDocuments.png(20, 20), "new.png"), DocumentCategory.PHOTO) })
        // A new key is never generated over existing documents.
        assertFalse(f.keys.keyExists())
        assertTrue(file.exists())
        // Only a deliberate removal deletes it; afterwards new documents can be saved under a new key.
        f.repository.delete(doc.id)
        assertFalse(file.exists())
        assertEquals(VaultKeyState.READY, f.repository.snapshot().keyState)
        assertNotNull(f.repository.import(SyntheticDocuments.source(SyntheticDocuments.png(20, 20), "new.png"), DocumentCategory.PHOTO))
    }

    @Test fun interruptedImportsAreCleanedAndUnlinkedFilesNeedADeliberateAction() = fixture { f ->
        f.repository.import(SyntheticDocuments.source(SyntheticDocuments.png(), "photo.png"), DocumentCategory.PHOTO)
        val objects = File(f.directory, "objects")
        File(objects, "00000000-0000-4000-8000-000000000001.partial").writeBytes(ByteArray(100))
        val orphan = File(objects, "00000000-0000-4000-8000-000000000002.jsv").apply { writeBytes(ByteArray(100)) }
        val snapshot = f.reopen().snapshot()
        assertFalse(File(objects, "00000000-0000-4000-8000-000000000001.partial").exists())
        assertTrue(orphan.exists())
        assertEquals(1, snapshot.unlistedFiles)
        assertEquals(1, snapshot.documents.size)
        assertEquals(1, f.repository.removeUnlisted())
        assertFalse(orphan.exists())
        assertEquals(1, f.repository.snapshot().documents.size)
    }

    @Test fun preparationChoicesFollowTheirDocumentsAndClearRemovesEverything() = fixture { f ->
        val catalog = CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use { it.readText() })
        val service = catalog.services.first { it.documents != null }
        val requirement = Preparation.requirements(service).items.first()
        val doc = f.repository.import(SyntheticDocuments.source(SyntheticDocuments.pdf(1), "form.pdf"), DocumentCategory.OTHER)
        val choice = f.repository.choose(Preparation.newChoice(service.id, requirement, doc.id, Instant.now()))
        assertEquals(choice, f.repository.choose(Preparation.newChoice(service.id, requirement, doc.id, Instant.now())))
        assertEquals(listOf(choice), f.repository.choices())
        f.repository.removeChoice(choice.id)
        assertTrue(f.repository.choices().isEmpty())
        f.repository.choose(Preparation.newChoice(service.id, requirement, doc.id, Instant.now()))
        f.repository.delete(doc.id)
        assertTrue(f.repository.choices().isEmpty())
        f.repository.import(SyntheticDocuments.source(SyntheticDocuments.png(), "photo.png"), DocumentCategory.PHOTO)
        f.repository.clear()
        assertFalse(f.keys.keyExists())
        assertTrue(File(f.directory, "objects").listFiles().orEmpty().isEmpty())
        assertEquals(0, f.repository.snapshot().total)
    }

    @Test fun pdfsOpenThroughAReadOnlyProxyDescriptor() = fixture { f ->
        val doc = f.repository.import(SyntheticDocuments.source(SyntheticDocuments.pdf(3), "three pages.pdf"), DocumentCategory.EDUCATION)
        val before = listOf(context.cacheDir, context.filesDir).flatMap { dir -> dir.walkTopDown().filter { it.isFile }.map { it.path }.toList() }.toSet()
        PdfRenderer(f.proxy.open(f.repository.open(doc.id))).use { renderer -> assertEquals(3, renderer.pageCount) }
        val after = listOf(context.cacheDir, context.filesDir).flatMap { dir -> dir.walkTopDown().filter { it.isFile }.map { it.path }.toList() }.toSet()
        assertEquals("Viewing must not write temporary files", before, after)
    }

    @Test fun vaultAndTrackerKeysAndStorageAreSeparate() = fixture { f ->
        assertEquals("jansetu_vault_v1", KeystoreVaultKeys.DEFAULT_ALIAS)
        // The tracker's own key alias (KeystoreTrackerCipher) is jansetu_tracker_v1; importing must not create or touch it.
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val trackerKeyBefore = store.containsAlias("jansetu_tracker_v1")
        f.repository.import(SyntheticDocuments.source(SyntheticDocuments.png(), "photo.png"), DocumentCategory.PHOTO)
        assertEquals(trackerKeyBefore, KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.containsAlias("jansetu_tracker_v1"))
        assertTrue(f.keys.keyExists())
        val vaultDir = File(context.noBackupFilesDir, "vault").canonicalPath
        val trackerDb = File(context.noBackupFilesDir, "tracker/applications.db").canonicalPath
        assertFalse(trackerDb.startsWith(vaultDir))
        assertFalse(f.directory.walkTopDown().any { it.name.startsWith("applications") })
    }
}
