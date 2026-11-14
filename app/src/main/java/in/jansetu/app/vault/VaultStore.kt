package `in`.jansetu.app.vault

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.Handler
import android.os.HandlerThread
import android.os.ParcelFileDescriptor
import android.os.ProxyFileDescriptorCallback
import android.os.storage.StorageManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.system.ErrnoException
import android.system.OsConstants
import androidx.core.content.edit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.security.KeyStore
import java.time.Clock
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** The vault's master key. It is separate from the tracker's key and never leaves the Android Keystore. */
interface VaultKeys : VaultKeyCipher {
    fun keyExists(): Boolean
    fun createKey()
    fun deleteKey()
}

class KeystoreVaultKeys(private val alias: String = DEFAULT_ALIAS) : VaultKeys {
    companion object { const val DEFAULT_ALIAS = "jansetu_vault_v1" }
    private fun store() = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private fun key(): SecretKey = synchronized(KEY_LOCK) { requireNotNull(store().getKey(alias, null) as? SecretKey) { "Vault key unavailable" } }
    override fun keyExists(): Boolean = synchronized(KEY_LOCK) { store().containsAlias(alias) }
    override fun createKey() = synchronized(KEY_LOCK) {
        // Never replaces an existing key: generating under a used alias would orphan every saved document.
        if (!store().containsAlias(alias)) KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(256).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true).build())
        }.generateKey()
        Unit
    }
    override fun deleteKey() = synchronized(KEY_LOCK) { store().deleteEntry(alias) }
    override fun seal(data: ByteArray, context: String): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key()); updateAAD(context.toByteArray()) }
        return byteArrayOf(1) + cipher.iv + cipher.doFinal(data)
    }
    override fun open(data: ByteArray, context: String): ByteArray {
        require(data.size > 29 && data[0] == 1.toByte())
        return Cipher.getInstance("AES/GCM/NoPadding").run {
            init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, data.copyOfRange(1, 13)))
            updateAAD(context.toByteArray()); doFinal(data.copyOfRange(13, data.size))
        }
    }
}
private val KEY_LOCK = Any()

/** Version 1. A corrupt or newer database is never recreated automatically. */
class VaultDatabase(context: Context, file: File) : SQLiteOpenHelper(context, file.absolutePath, null, 1, { throw VaultStorageException() }) {
    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
        db.rawQuery("PRAGMA secure_delete=ON", null).use { check(it.moveToFirst() && it.getInt(0) == 1) }
    }
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE documents (id TEXT PRIMARY KEY NOT NULL, payload BLOB NOT NULL)")
        db.execSQL("CREATE TABLE choices (id TEXT PRIMARY KEY NOT NULL, document_id TEXT NOT NULL REFERENCES documents(id) ON DELETE CASCADE, payload BLOB NOT NULL)")
        db.execSQL("CREATE INDEX choice_document ON choices(document_id)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = throw VaultStorageException()
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = throw VaultStorageException()
}

/** READY: documents can be read and added. LOCKED: documents exist but the key is gone; they are kept, never deleted automatically. */
enum class VaultKeyState { READY, LOCKED }

data class VaultSnapshot(val documents: List<VaultDocument>, val unreadableIds: List<String>, val keyState: VaultKeyState,
    val usedBytes: Long, val unlistedFiles: Int) {
    val total: Int get() = documents.size + unreadableIds.size
}

/** Where an import comes from. Only the display name, stated type and size are taken; the URI is never stored. */
class VaultImportSource(val displayName: String?, val declaredMime: String?, val declaredSize: Long?, val open: () -> InputStream)

/** Valid(previewAvailable) or null for a damaged file. */
fun interface DocumentInspector { fun inspect(type: DocumentType, open: () -> VaultReader): Boolean? }

/** Blocking repository: call on an IO dispatcher. Nothing here shares storage or keys with My Applications. */
interface VaultRepository {
    fun snapshot(): VaultSnapshot
    fun get(id: String): VaultDocument?
    fun import(source: VaultImportSource, category: DocumentCategory, name: String? = null): VaultDocument
    fun update(id: String, name: String, category: DocumentCategory, revision: Long): VaultDocument
    fun delete(id: String)
    fun open(id: String): VaultReader
    fun choices(): List<PreparationChoice>
    fun choose(choice: PreparationChoice): PreparationChoice
    fun removeChoice(id: String)
    fun removeUnlisted(): Int
    fun clear()
}

class EncryptedVaultRepository(context: Context, val directory: File, private val keys: VaultKeys,
    private val inspector: DocumentInspector, private val clock: Clock = Clock.systemUTC()) : VaultRepository, AutoCloseable {
    private val appContext = context.applicationContext
    private val objects = File(directory, "objects")
    private val databaseFile = File(directory, "documents.db")
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private var helper = VaultDatabase(appContext, databaseFile)
    private var prepared = false

    /** Runs on the first call (always off the main thread), not at construction. */
    private fun prepare() {
        if (prepared) return
        check(objects.isDirectory || objects.mkdirs())
        // An interrupted import leaves only ciphertext that never became a document.
        objects.listFiles { file -> file.name.endsWith(".partial") }?.forEach { it.delete() }
        prepared = true
    }

    private fun objectFile(id: String) = File(objects, "${UUID.fromString(id)}.jsv")

    /** Space Android can make available, including clearable cache; falls back to plain free space. */
    private fun freeBytes(): Long = runCatching {
        val storage = appContext.getSystemService(StorageManager::class.java)
        storage.getAllocatableBytes(storage.getUuidForPath(objects))
    }.getOrElse { objects.usableSpace }

    private fun <T> guarded(block: () -> T): T = synchronized(LOCK) {
        try { prepare(); block() } catch (e: VaultImportException) { throw e } catch (e: VaultConflictException) { throw e }
        catch (_: Exception) { throw VaultStorageException() }
    }

    private fun <T> transaction(block: (SQLiteDatabase) -> T): T {
        val db = helper.writableDatabase; db.beginTransaction()
        try { return block(db).also { db.setTransactionSuccessful() } } finally { db.endTransaction() }
    }

    private fun rows(db: SQLiteDatabase): List<Pair<String, ByteArray>> =
        db.query("documents", arrayOf("id", "payload"), null, null, null, null, "rowid ASC").use { c ->
            buildList { while (c.moveToNext()) add(c.getString(0) to c.getBlob(1)) }
        }

    private fun decode(id: String, payload: ByteArray): VaultDocument? = runCatching {
        json.decodeFromString<VaultDocument>(keys.open(payload, "document:$id").decodeToString())
            .also { require(it.id == id); VaultRules.validateStored(it); require(objectFile(id).isFile) }
    }.getOrNull()

    private fun snapshotLocked(): VaultSnapshot {
        val rows = rows(helper.readableDatabase)
        val keyPresent = keys.keyExists()
        val documents = mutableListOf<VaultDocument>(); val unreadable = mutableListOf<String>()
        rows.forEach { (id, payload) -> (if (keyPresent) decode(id, payload) else null)?.let(documents::add) ?: unreadable.add(id) }
        val ids = rows.map { it.first }.toSet()
        val files = objects.listFiles { file -> file.name.endsWith(".jsv") }.orEmpty()
        return VaultSnapshot(documents, unreadable, if (rows.isNotEmpty() && !keyPresent) VaultKeyState.LOCKED else VaultKeyState.READY,
            files.sumOf { it.length() }, files.count { it.name.removeSuffix(".jsv") !in ids })
    }

    override fun snapshot(): VaultSnapshot = guarded { snapshotLocked() }

    override fun get(id: String): VaultDocument? = guarded {
        helper.readableDatabase.query("documents", arrayOf("payload"), "id=?", arrayOf(id), null, null, null).use {
            if (it.moveToFirst()) decode(id, it.getBlob(0)) else null
        }
    }

    override fun import(source: VaultImportSource, category: DocumentCategory, name: String?): VaultDocument = guarded {
        val snapshot = snapshotLocked()
        if (snapshot.keyState == VaultKeyState.LOCKED) throw VaultImportException(ImportProblem.KEY_UNAVAILABLE)
        VaultRules.capacityProblem(snapshot.total, snapshot.usedBytes, source.declaredSize, freeBytes())?.let { throw VaultImportException(it) }
        if (!keys.keyExists()) keys.createKey()
        val id = UUID.randomUUID().toString()
        val partial = File(objects, "$id.partial")
        try {
            val input = try { source.open() } catch (_: Exception) { throw VaultImportException(ImportProblem.UNREADABLE_SOURCE) }
            val result = SourceStream(input).use { stream ->
                FileOutputStream(partial).use { file ->
                    val buffered = BufferedOutputStream(file, SegmentedCipher.SEGMENT_SIZE)
                    SegmentedCipher.encrypt(id, stream, buffered, keys, VaultLimits.MAX_FILE_BYTES).also { buffered.flush(); file.fd.sync() }
                }
            }
            val type = DocumentSignature.detect(result.head) ?: throw VaultImportException(ImportProblem.UNSUPPORTED)
            if (!VaultRules.declaredMatches(source.declaredMime, type)) throw VaultImportException(ImportProblem.MISMATCH)
            if (!DocumentSignature.structurallyValid(type, result.head, result.tail)) throw VaultImportException(ImportProblem.MALFORMED)
            result.head.fill(0); result.tail.fill(0)
            if (snapshot.usedBytes + partial.length() > VaultLimits.MAX_TOTAL_BYTES) throw VaultImportException(ImportProblem.QUOTA)
            if (snapshot.documents.any { it.sha256 == result.sha256 }) throw VaultImportException(ImportProblem.DUPLICATE)
            val preview = inspector.inspect(type) { SegmentedCipher.open(id, RandomAccessFile(partial, "r"), keys) }
                ?: throw VaultImportException(ImportProblem.MALFORMED)
            val now = clock.instant().toString()
            val chosenName = name?.let(VaultRules::normaliseName)?.takeIf { it.isNotEmpty() } ?: VaultRules.cleanName(source.displayName)
            val document = VaultDocument(id, chosenName, category, type, result.plaintextBytes, result.sha256, now, now, previewAvailable = preview)
            VaultRules.validateStored(document)
            val final = objectFile(id)
            check(partial.renameTo(final))
            try {
                transaction { db ->
                    db.insertOrThrow("documents", null, ContentValues().apply {
                        put("id", id); put("payload", keys.seal(json.encodeToString(document).toByteArray(), "document:$id"))
                    })
                }
            } catch (error: Exception) { final.delete(); throw error }
            document
        } finally { partial.delete() }
    }

    override fun update(id: String, name: String, category: DocumentCategory, revision: Long): VaultDocument = guarded {
        require(VaultRules.nameErrors(name).isEmpty())
        transaction { db ->
            val existing = db.query("documents", arrayOf("payload"), "id=?", arrayOf(id), null, null, null).use {
                if (it.moveToFirst()) decode(id, it.getBlob(0)) else null
            } ?: throw VaultConflictException()
            if (existing.revision != revision) throw VaultConflictException()
            val updated = existing.copy(name = VaultRules.normaliseName(name), category = category,
                updatedAt = clock.instant().toString(), revision = existing.revision + 1)
            VaultRules.validateStored(updated)
            check(db.update("documents", ContentValues().apply {
                put("payload", keys.seal(json.encodeToString(updated).toByteArray(), "document:$id"))
            }, "id=?", arrayOf(id)) == 1)
            updated
        }
    }

    /** Removes the record (and its preparation choices) first, then the encrypted file. */
    override fun delete(id: String) = guarded {
        UUID.fromString(id)
        transaction { it.delete("documents", "id=?", arrayOf(id)) }
        objectFile(id).delete()
        Unit
    }

    override fun open(id: String): VaultReader = guarded {
        requireNotNull(helper.readableDatabase.query("documents", arrayOf("payload"), "id=?", arrayOf(id), null, null, null).use {
            if (it.moveToFirst()) decode(id, it.getBlob(0)) else null
        })
        SegmentedCipher.open(id, RandomAccessFile(objectFile(id), "r"), keys)
    }

    override fun choices(): List<PreparationChoice> = guarded {
        if (!keys.keyExists()) return@guarded emptyList()
        helper.readableDatabase.query("choices", arrayOf("id", "document_id", "payload"), null, null, null, null, "rowid ASC").use { c ->
            buildList {
                while (c.moveToNext()) runCatching {
                    val id = c.getString(0)
                    json.decodeFromString<PreparationChoice>(keys.open(c.getBlob(2), "choice:$id").decodeToString())
                        .also { require(it.id == id && it.documentId == c.getString(1)); Preparation.validateStored(it) }
                }.getOrNull()?.let(::add)
            }
        }
    }

    override fun choose(choice: PreparationChoice): PreparationChoice = guarded {
        Preparation.validateStored(choice)
        val existing = choices().firstOrNull { it.serviceId == choice.serviceId && it.requirementKey == choice.requirementKey && it.documentId == choice.documentId }
        existing ?: transaction { db ->
            db.insertOrThrow("choices", null, ContentValues().apply {
                put("id", choice.id); put("document_id", choice.documentId)
                put("payload", keys.seal(json.encodeToString(choice).toByteArray(), "choice:${choice.id}"))
            })
            choice
        }
    }

    override fun removeChoice(id: String) = guarded { transaction { it.delete("choices", "id=?", arrayOf(id)) }; Unit }

    /** Deliberate clean-up of encrypted files with no record (for example after a crash during an import). */
    override fun removeUnlisted(): Int = guarded {
        val ids = rows(helper.readableDatabase).map { it.first }.toSet()
        objects.listFiles { file -> file.name.endsWith(".jsv") && file.name.removeSuffix(".jsv") !in ids }.orEmpty().count { it.delete() }
    }

    /** Deletes every document, the database and the vault key, so any remnants on flash storage can no longer be decrypted. */
    override fun clear() = guarded {
        helper.close()
        check(!databaseFile.exists() || SQLiteDatabase.deleteDatabase(databaseFile))
        objects.listFiles().orEmpty().forEach { it.delete() }
        keys.deleteKey()
        helper = VaultDatabase(appContext, databaseFile)
        prepared = false
    }

    override fun close() { synchronized(LOCK) { helper.close() } }

    /** Read failures from the chosen file are reported as an unreadable source, not as vault damage. */
    private class SourceStream(input: InputStream) : FilterInputStream(input) {
        override fun read(): Int = try { super.read() } catch (_: IOException) { throw VaultImportException(ImportProblem.UNREADABLE_SOURCE) }
        override fun read(b: ByteArray, off: Int, len: Int): Int = try { super.read(b, off, len) } catch (_: IOException) { throw VaultImportException(ImportProblem.UNREADABLE_SOURCE) }
    }

    private companion object { val LOCK = Any() }
}

/** Seekable, read-only file descriptors served from decrypted segments in memory; no plaintext file is written. */
class VaultProxyFiles(context: Context) {
    private val storage = context.applicationContext.getSystemService(StorageManager::class.java)
    private val handler by lazy { Handler(HandlerThread("jansetu-vault-proxy").apply { start() }.looper) }

    fun open(reader: VaultReader): ParcelFileDescriptor = try {
        storage.openProxyFileDescriptor(ParcelFileDescriptor.MODE_READ_ONLY, object : ProxyFileDescriptorCallback() {
            override fun onGetSize(): Long = reader.size
            override fun onRead(offset: Long, size: Int, data: ByteArray): Int =
                try { reader.read(offset, data, 0, size) } catch (_: Exception) { throw ErrnoException("onRead", OsConstants.EIO) }
            override fun onRelease() { reader.close() }
        }, handler)
    } catch (error: Exception) { reader.close(); throw IOException("Document could not be opened", error) }
}

/** Decodes images (bounds and a small sample) and opens PDFs, so damaged files are refused at import. */
class AndroidDocumentInspector(private val proxy: VaultProxyFiles) : DocumentInspector {
    override fun inspect(type: DocumentType, open: () -> VaultReader): Boolean? = try {
        if (type.isImage) open().use { reader ->
            val bytes = reader.readAll()
            try {
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
                val pixels = bounds.outWidth.toLong() * bounds.outHeight
                val expected = if (type == DocumentType.PNG) "image/png" else "image/jpeg"
                if (bounds.outWidth <= 0 || bounds.outHeight <= 0 || pixels > VaultLimits.MAX_IMAGE_PIXELS || bounds.outMimeType != expected) null
                else {
                    var sample = 1
                    while (maxOf(bounds.outWidth, bounds.outHeight) / sample > 512) sample *= 2
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
                        ?.let { it.recycle(); true }
                }
            } finally { bytes.fill(0) }
        } else try {
            PdfRenderer(proxy.open(open())).use { renderer -> if (renderer.pageCount in 1..VaultLimits.MAX_PDF_PAGES) true else null }
        } catch (_: SecurityException) {
            // A password-protected PDF (for example a downloaded e-document) is kept, but JanSetu cannot show it.
            false
        }
    } catch (_: Exception) { null }
}

/** Whether saved documents appear in Android's file picker, and the key behind the time-limited picker IDs. */
class VaultPickerAccess(context: Context, prefsName: String = "jansetu_vault_preferences",
    private val macAlias: String = "jansetu_vault_picker_v1", private val clock: Clock = Clock.systemUTC()) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    val authority: String = "${appContext.packageName}.vault"
    val enabled: Boolean get() = preferences.getBoolean("picker_enabled", false)

    fun setEnabled(value: Boolean) {
        preferences.edit(commit = true) { putBoolean("picker_enabled", value) }
        if (!value) revokeAll()
        notifyRoots()
    }

    /** Withdraws every document ID already given to the file picker or to an app. */
    fun revokeAll() = synchronized(LOCK) { KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.deleteEntry(macAlias) }

    fun grants(): PickerGrants = PickerGrants(::mac, clock)

    fun notifyRoots() {
        runCatching { appContext.contentResolver.notifyChange(android.provider.DocumentsContract.buildRootsUri(authority), null) }
    }

    private fun mac(data: ByteArray): ByteArray = synchronized(LOCK) {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val key = (store.getKey(macAlias, null) as? SecretKey) ?: KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, "AndroidKeyStore")
            .apply { init(KeyGenParameterSpec.Builder(macAlias, KeyProperties.PURPOSE_SIGN).build()) }.generateKey()
        Mac.getInstance("HmacSHA256").apply { init(key) }.doFinal(data)
    }

    private companion object { val LOCK = Any() }
}

class VaultDependencies(val repository: VaultRepository, val picker: VaultPickerAccess, val proxy: VaultProxyFiles) {
    companion object {
        @Volatile private var instance: VaultDependencies? = null
        /** Instrumentation tests point the app and the provider at an isolated vault. */
        @Volatile var testOverride: VaultDependencies? = null
        fun get(context: Context): VaultDependencies = testOverride ?: instance ?: synchronized(this) {
            instance ?: run {
                val app = context.applicationContext
                val proxy = VaultProxyFiles(app)
                VaultDependencies(EncryptedVaultRepository(app, File(app.noBackupFilesDir, "vault"), KeystoreVaultKeys(), AndroidDocumentInspector(proxy)),
                    VaultPickerAccess(app), proxy)
            }.also { instance = it }
        }
    }
}
