package `in`.jansetu.app.tracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.security.KeyStore
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

interface TrackerCipher {
    fun encrypt(data: ByteArray, identity: String): ByteArray
    fun decrypt(data: ByteArray, identity: String): ByteArray
}

class KeystoreTrackerCipher(private val alias: String = "jansetu_tracker_v1") : TrackerCipher {
    private fun encryptionKey(): SecretKey = synchronized(KEY_LOCK) {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(alias, null) as? SecretKey) ?: KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(256).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }.generateKey()
    }
    private fun existingKey(): SecretKey = synchronized(KEY_LOCK) {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        requireNotNull(store.getKey(alias, null) as? SecretKey) { "Tracker encryption key is unavailable" }
    }
    override fun encrypt(data: ByteArray, identity: String): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, encryptionKey()); updateAAD(identity.toByteArray()) }
        return byteArrayOf(1) + cipher.iv + cipher.doFinal(data)
    }
    override fun decrypt(data: ByteArray, identity: String): ByteArray {
        require(data.size > 29 && data[0] == 1.toByte())
        return Cipher.getInstance("AES/GCM/NoPadding").run {
            init(Cipher.DECRYPT_MODE, existingKey(), GCMParameterSpec(128, data.copyOfRange(1,13)))
            updateAAD(identity.toByteArray()); doFinal(data.copyOfRange(13,data.size))
        }
    }
    private companion object { val KEY_LOCK = Any() }
}

/** Version 2 only adds delivery deduplication. No destructive fallback on corruption/downgrade. */
class TrackerDatabase(context: Context, val file: File) : SQLiteOpenHelper(context, file.absolutePath, null, 2,
    { throw TrackerStorageException() }) {
    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
        db.rawQuery("PRAGMA secure_delete=ON",null).use { check(it.moveToFirst() && it.getInt(0)==1) }
    }
    override fun onCreate(db: SQLiteDatabase) { createVersionOne(db); addDeliveryTable(db) }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        require(oldVersion == 1 && newVersion == 2)
        addDeliveryTable(db)
    }
    companion object {
        fun createVersionOne(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE applications (id TEXT PRIMARY KEY NOT NULL, payload BLOB NOT NULL)")
            db.execSQL("CREATE TABLE progress_events (id TEXT PRIMARY KEY NOT NULL, application_id TEXT NOT NULL REFERENCES applications(id) ON DELETE CASCADE, payload BLOB NOT NULL)")
            db.execSQL("CREATE INDEX progress_application ON progress_events(application_id)")
        }
        private fun addDeliveryTable(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE reminder_deliveries (application_id TEXT PRIMARY KEY NOT NULL REFERENCES applications(id) ON DELETE CASCADE, fingerprint TEXT NOT NULL)")
        }
    }
}

class SqliteApplicationRepository(context: Context,
    val file: File = File(context.noBackupFilesDir, "tracker/applications.db"),
    private val cipher: TrackerCipher = KeystoreTrackerCipher(), private val clock: Clock = Clock.systemUTC(),
    private val zoneProvider: () -> ZoneId = {ZoneId.systemDefault()},
) : ApplicationRepository, AutoCloseable {
    init { file.parentFile?.mkdirs() }
    private var helper = TrackerDatabase(context.applicationContext, file)
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private fun <T> guarded(block: () -> T): T = synchronized(LOCK) {
        try { block() } catch (e: TrackerConflictException) { throw e }
        catch (_: Exception) { throw TrackerStorageException() }
    }
    private fun <T> transaction(block: (SQLiteDatabase) -> T): T = guarded {
        val db = helper.writableDatabase; db.beginTransaction()
        try { block(db).also { db.setTransactionSuccessful() } } finally { db.endTransaction() }
    }
    private fun load(db: SQLiteDatabase, id: String): ApplicationRecord? {
        val record = db.query("applications", arrayOf("payload"), "id=?", arrayOf(id), null,null,null).use {
            if (!it.moveToFirst()) return null
            json.decodeFromString<ApplicationRecord>(cipher.decrypt(it.getBlob(0), "application:$id").decodeToString())
        }
        require(record.id == id)
        val events = db.query("progress_events", arrayOf("id","payload"), "application_id=?", arrayOf(id),null,null,"rowid ASC").use { cursor ->
            buildList { while (cursor.moveToNext()) {
                val eventId = cursor.getString(0)
                val event = json.decodeFromString<ProgressEvent>(cipher.decrypt(cursor.getBlob(1), "event:$id:$eventId").decodeToString())
                require(event.id == eventId); add(event)
            } }
        }
        return record.copy(history=events).also { ApplicationRules.validateStored(it) }
    }
    override fun read(): TrackerSnapshot = guarded {
        val db = helper.readableDatabase
        val ids = db.query("applications", arrayOf("id"),null,null,null,null,null).use { c -> buildList { while(c.moveToNext()) add(c.getString(0)) } }
        val records = mutableListOf<ApplicationRecord>(); val unreadable = mutableListOf<String>()
        ids.forEach { id -> try { records += requireNotNull(load(db,id)) } catch (_: Exception) { unreadable += id } }
        TrackerSnapshot(records,unreadable)
    }
    override fun get(id: String): ApplicationRecord? = guarded { load(helper.readableDatabase,id) }
    override fun save(draft: ApplicationDraft, id: String?, revision: Long?, service: ServiceSnapshot?): ApplicationRecord {
        require(ApplicationRules.errors(draft, clock.instant(), zoneProvider()).isEmpty()) { "Review application fields before saving." }
        return transaction { db ->
            val existing = id?.let { load(db,it) }
            if (id != null && (existing == null || existing.revision != revision)) throw TrackerConflictException()
            val recordId = existing?.id ?: UUID.randomUUID().toString()
            val timestamp = clock.instant().toString()
            val event = if (existing == null || existing.status != draft.status || draft.progressNote.isNotBlank() || draft.progressDate.isNotBlank())
                ProgressEvent(UUID.randomUUID().toString(), draft.status, draft.progressDate.ifBlank { clock.instant().atZone(zoneProvider()).toLocalDate().toString() }, draft.progressNote.trim(), timestamp) else null
            val record = ApplicationRecord(recordId,draft.title.trim(),draft.department.trim(),if(existing!=null) existing.service else service,
                draft.submittedOn.ifBlank { null },draft.referenceNumber.trim(),draft.status,draft.deadline.ifBlank { null },
                draft.reminderEnabled,draft.reminderDaysBefore,draft.notes.trim(),existing?.createdAt ?: timestamp,timestamp,
                (existing?.revision ?: 0)+1,history=(existing?.history ?: emptyList())+listOfNotNull(event))
            ApplicationRules.validateStored(record)
            val values = ContentValues().apply { put("id",recordId); put("payload",cipher.encrypt(json.encodeToString(record.copy(history=emptyList())).toByteArray(),"application:$recordId")) }
            if (existing == null) db.insertOrThrow("applications",null,values)
            else check(db.update("applications",values,"id=?", arrayOf(recordId)) == 1)
            if (event != null) db.insertOrThrow("progress_events",null,ContentValues().apply {
                put("id",event.id); put("application_id",recordId); put("payload",cipher.encrypt(json.encodeToString(event).toByteArray(),"event:$recordId:${event.id}"))
            })
            record
        }
    }
    override fun delete(id: String) { transaction { it.delete("applications","id=?", arrayOf(id)) } }
    override fun clear() { guarded { helper.close(); check(!file.exists() || SQLiteDatabase.deleteDatabase(file)); } }
    override fun deliveredKey(id: String): String? = guarded {
        helper.readableDatabase.query("reminder_deliveries",arrayOf("fingerprint"),"application_id=?",arrayOf(id),null,null,null).use { if(it.moveToFirst()) it.getString(0) else null }
    }
    override fun deliverOnce(id: String, key: String, action: (ApplicationRecord) -> Boolean): Boolean = transaction { db ->
        val record = load(db,id) ?: return@transaction false
        if (ApplicationRules.reminderKey(record) != key || deliveredKey(id) == key) return@transaction false
        if (!action(record)) return@transaction false
        db.insertWithOnConflict("reminder_deliveries",null,ContentValues().apply { put("application_id",id); put("fingerprint",key) },SQLiteDatabase.CONFLICT_REPLACE)
        true
    }
    override fun close() { synchronized(LOCK) { helper.close() } }
    private companion object { val LOCK = Any() }
}
