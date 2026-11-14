package `in`.jansetu.app

import android.content.pm.ProviderInfo
import android.provider.DocumentsContract
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.vault.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.FileNotFoundException
import java.security.KeyStore

/** The file-picker provider, called through ContentResolver exactly as the system picker would call it. */
class VaultDocumentsProviderTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var fixture: VaultFixture
    private val authority get() = fixture.picker.authority
    private val resolver get() = context.contentResolver

    @Before fun setUp() { fixture = VaultFixture(context); VaultDependencies.testOverride = fixture.dependencies }
    @After fun tearDown() { fixture.close() }

    private fun roots() = resolver.query(DocumentsContract.buildRootsUri(authority), null, null, null, null)!!.use { it.count }
    private fun children(): List<Triple<String, String, String>> =
        resolver.query(DocumentsContract.buildChildDocumentsUri(authority, PickerGrants.ROOT_ID), null, null, null, null)!!.use { c ->
            buildList { while (c.moveToNext()) add(Triple(
                c.getString(c.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)),
                c.getString(c.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)),
                c.getString(c.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)))) }
        }
    private fun read(documentId: String): ByteArray =
        resolver.openFileDescriptor(DocumentsContract.buildDocumentUri(authority, documentId), "r")!!.use { pfd ->
            java.io.FileInputStream(pfd.fileDescriptor).readBytes()
        }

    @Test fun providerIsProtectedReadOnlyAndRegisteredForThisPackageOnly() {
        @Suppress("DEPRECATION") val info: ProviderInfo = context.packageManager.resolveContentProvider(authority, 0)!!
        assertEquals("android.permission.MANAGE_DOCUMENTS", info.readPermission)
        assertEquals("android.permission.MANAGE_DOCUMENTS", info.writePermission)
        assertTrue(info.exported); assertTrue(info.grantUriPermissions)
        assertEquals("${context.packageName}.vault", authority)
    }

    @Test fun nothingIsVisibleUntilThePersonTurnsItOn() {
        fixture.repository.import(SyntheticDocuments.source(SyntheticDocuments.png(), "photo.png"), DocumentCategory.PHOTO)
        assertFalse(fixture.picker.enabled)
        assertEquals(0, roots())
        assertTrue(children().isEmpty())
        fixture.picker.setEnabled(true)
        assertEquals(1, roots())
        assertEquals(1, children().size)
    }

    @Test fun aChosenDocumentIsReadableOnlyReadOnlyAndUntilAccessIsWithdrawn() {
        val png = SyntheticDocuments.png(); val pdf = SyntheticDocuments.pdf(1)
        val photo = fixture.repository.import(SyntheticDocuments.source(png, "photo.png"), DocumentCategory.PHOTO, "Passport photo")
        fixture.repository.import(SyntheticDocuments.source(pdf, "c.pdf"), DocumentCategory.CERTIFICATE, "Caste certificate")
        fixture.picker.setEnabled(true)
        val listed = children()
        assertEquals(setOf("Passport photo.png" to "image/png", "Caste certificate.pdf" to "application/pdf"), listed.map { it.second to it.third }.toSet())
        listed.forEach { (id, _, _) -> assertFalse("Raw document IDs are never exposed", id == photo.id); assertNotNull(fixture.picker.grants().resolve(id)) }
        val photoToken = listed.first { it.second == "Passport photo.png" }.first
        assertArrayEquals(png, read(photoToken))
        assertThrows(Exception::class.java) { resolver.openFileDescriptor(DocumentsContract.buildDocumentUri(authority, photoToken), "w") }
        assertThrows(Exception::class.java) { resolver.openFileDescriptor(DocumentsContract.buildDocumentUri(authority, photoToken), "rw") }
        assertThrows(FileNotFoundException::class.java) { read(photo.id) }
        assertThrows(FileNotFoundException::class.java) { read("${photo.id}.9999999999.forged") }

        // "Stop all file-picker access now" withdraws IDs already handed out, without hiding the documents.
        fixture.picker.revokeAll()
        assertThrows(FileNotFoundException::class.java) { read(photoToken) }
        val fresh = children().first { it.second == "Passport photo.png" }.first
        assertArrayEquals(png, read(fresh))

        // Deleting the document ends access to it.
        fixture.repository.delete(photo.id)
        assertThrows(FileNotFoundException::class.java) { read(fresh) }

        // Turning the setting off hides the root and refuses every ID.
        val certificate = children().single().first
        fixture.picker.setEnabled(false)
        assertEquals(0, roots())
        assertThrows(FileNotFoundException::class.java) { read(certificate) }
    }

    @Test fun lockedDocumentsAreNeitherListedNorOpened() {
        fixture.repository.import(SyntheticDocuments.source(SyntheticDocuments.png(), "photo.png"), DocumentCategory.PHOTO)
        fixture.picker.setEnabled(true)
        val token = children().single().first
        KeyStore.getInstance("AndroidKeyStore").apply { load(null); deleteEntry(fixture.keyAlias) }
        assertTrue(children().isEmpty())
        assertThrows(FileNotFoundException::class.java) { read(token) }
    }
}
