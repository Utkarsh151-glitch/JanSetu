package `in`.jansetu.app.vault

import android.database.Cursor
import android.database.MatrixCursor
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract.Document
import android.provider.DocumentsContract.Root
import android.provider.DocumentsProvider
import `in`.jansetu.app.R
import java.io.FileNotFoundException
import java.time.Instant

/**
 * Lets the person pick a saved document in Android's own file picker, for example in a browser's upload field.
 *
 * - Off by default. When off, there is no root and nothing can be opened.
 * - Protected by MANAGE_DOCUMENTS, so only the system picker can list documents; an app or website receives a
 *   read-only grant for the single file the person chose.
 * - IDs expire after [PickerGrants.VALIDITY] and are withdrawn at once by "Stop file-picker access".
 * - No writing, deleting, renaming, recents, search or thumbnails.
 */
class VaultDocumentsProvider : DocumentsProvider() {
    private val dependencies get() = VaultDependencies.get(requireNotNull(context))

    override fun onCreate(): Boolean = true

    override fun queryRoots(projection: Array<out String>?): Cursor {
        val cursor = MatrixCursor(projection ?: ROOT_COLUMNS)
        if (!dependencies.picker.enabled) return cursor
        cursor.newRow().apply {
            add(Root.COLUMN_ROOT_ID, PickerGrants.ROOT_ID)
            add(Root.COLUMN_DOCUMENT_ID, PickerGrants.ROOT_ID)
            add(Root.COLUMN_TITLE, "JanSetu: My Documents")
            add(Root.COLUMN_SUMMARY, "Documents you saved in JanSetu (read-only)")
            add(Root.COLUMN_FLAGS, Root.FLAG_LOCAL_ONLY)
            add(Root.COLUMN_ICON, R.mipmap.ic_launcher)
            add(Root.COLUMN_MIME_TYPES, DocumentType.mimeTypes.joinToString("\n"))
        }
        return cursor
    }

    override fun queryDocument(documentId: String, projection: Array<out String>?): Cursor {
        val cursor = MatrixCursor(projection ?: DOCUMENT_COLUMNS)
        if (!dependencies.picker.enabled) throw FileNotFoundException()
        if (documentId == PickerGrants.ROOT_ID) {
            cursor.newRow().apply {
                add(Document.COLUMN_DOCUMENT_ID, PickerGrants.ROOT_ID)
                add(Document.COLUMN_DISPLAY_NAME, "My Documents")
                add(Document.COLUMN_MIME_TYPE, Document.MIME_TYPE_DIR)
                add(Document.COLUMN_FLAGS, 0)
                add(Document.COLUMN_SIZE, null)
                add(Document.COLUMN_LAST_MODIFIED, null)
            }
            return cursor
        }
        val document = resolve(documentId)
        addDocument(cursor, documentId, document)
        return cursor
    }

    override fun queryChildDocuments(parentDocumentId: String, projection: Array<out String>?, sortOrder: String?): Cursor {
        val cursor = MatrixCursor(projection ?: DOCUMENT_COLUMNS)
        if (!dependencies.picker.enabled || parentDocumentId != PickerGrants.ROOT_ID) return cursor
        val grants = dependencies.picker.grants()
        runCatching { dependencies.repository.snapshot().documents }.getOrDefault(emptyList())
            .sortedByDescending { it.importedAt }
            .forEach { addDocument(cursor, grants.issue(it.id), it) }
        return cursor
    }

    override fun openDocument(documentId: String, mode: String, signal: CancellationSignal?): ParcelFileDescriptor {
        if (mode != "r") throw UnsupportedOperationException("Saved documents are read-only")
        val document = resolve(documentId)
        signal?.throwIfCanceled()
        val reader = try { dependencies.repository.open(document.id) } catch (_: Exception) { throw FileNotFoundException() }
        return try { dependencies.proxy.open(reader) } catch (_: Exception) { throw FileNotFoundException() }
    }

    /** Valid only while the picker is on, the ID is genuine and unexpired, and the document still exists. */
    private fun resolve(documentId: String): VaultDocument {
        if (!dependencies.picker.enabled) throw FileNotFoundException()
        val id = dependencies.picker.grants().resolve(documentId) ?: throw FileNotFoundException()
        return runCatching { dependencies.repository.get(id) }.getOrNull() ?: throw FileNotFoundException()
    }

    private fun addDocument(cursor: MatrixCursor, documentId: String, document: VaultDocument) {
        cursor.newRow().apply {
            add(Document.COLUMN_DOCUMENT_ID, documentId)
            add(Document.COLUMN_DISPLAY_NAME, document.fileName)
            add(Document.COLUMN_MIME_TYPE, document.type.mime)
            add(Document.COLUMN_FLAGS, 0)
            add(Document.COLUMN_SIZE, document.sizeBytes)
            add(Document.COLUMN_LAST_MODIFIED, Instant.parse(document.updatedAt).toEpochMilli())
        }
    }

    companion object {
        private val ROOT_COLUMNS = arrayOf(Root.COLUMN_ROOT_ID, Root.COLUMN_DOCUMENT_ID, Root.COLUMN_TITLE, Root.COLUMN_SUMMARY,
            Root.COLUMN_FLAGS, Root.COLUMN_ICON, Root.COLUMN_MIME_TYPES)
        private val DOCUMENT_COLUMNS = arrayOf(Document.COLUMN_DOCUMENT_ID, Document.COLUMN_DISPLAY_NAME, Document.COLUMN_MIME_TYPE,
            Document.COLUMN_FLAGS, Document.COLUMN_SIZE, Document.COLUMN_LAST_MODIFIED)
    }
}
