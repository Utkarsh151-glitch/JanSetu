package `in`.jansetu.app

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.CatalogParser
import `in`.jansetu.app.tracker.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.KeyStore
import java.time.*
import java.util.UUID

class TrackerRepositoryTest {
    private val context get()=InstrumentationRegistry.getInstrumentation().targetContext
    private fun fixture(block:(SqliteApplicationRepository,File,KeystoreTrackerCipher)->Unit) {
        val id=UUID.randomUUID().toString();val file=File(context.noBackupFilesDir,"tracker-test-$id.db")
        val cipher=KeystoreTrackerCipher("tracker-test-$id");val repo=SqliteApplicationRepository(context,file,cipher)
        try {block(repo,file,cipher)} finally {
            repo.close();SQLiteDatabase.deleteDatabase(file)
            KeyStore.getInstance("AndroidKeyStore").apply {load(null);deleteEntry("tracker-test-$id")}
        }
    }
    @Test fun crudReopenAndEditsPreserveHistoryAndUnrelatedRecords()=fixture {repo,file,cipher ->
        val first=repo.save(ApplicationDraft("First",referenceNumber="PRIVATE-REF-876543",notes="PRIVATE-NOTE-987654"))
        val second=repo.save(ApplicationDraft("Second"))
        val changed=repo.save(ApplicationDraft("Renamed",status=ApplicationStatus.SUBMITTED,submittedOn=LocalDate.now().toString(),progressNote="User says submitted"),first.id,first.revision)
        assertEquals(2,changed.history.size);assertEquals(first.history.first(),changed.history.first())
        assertEquals("user_entered",changed.statusProvenance)
        assertThrows(TrackerConflictException::class.java) {repo.save(ApplicationDraft("Stale"),first.id,first.revision)}
        repo.close()
        SqliteApplicationRepository(context,file,cipher).use {reopened ->
            assertEquals(changed,reopened.get(first.id));assertEquals(2,reopened.read().records.size)
            val edited=reopened.save(ApplicationDraft("Another title",status=ApplicationStatus.SUBMITTED),first.id,changed.revision)
            assertEquals(changed.history,edited.history)
            reopened.delete(first.id);assertNull(reopened.get(first.id));assertNotNull(reopened.get(second.id))
            SQLiteDatabase.openDatabase(file.absolutePath,null,SQLiteDatabase.OPEN_READONLY).use {db ->
                db.rawQuery("SELECT count(*) FROM progress_events",null).use {it.moveToFirst();assertEquals(1,it.getInt(0))}
                db.rawQuery("PRAGMA secure_delete",null).use {it.moveToFirst();assertEquals(1,it.getInt(0))}
            }
            val bytes=file.readBytes().toString(Charsets.ISO_8859_1)
            assertFalse(bytes.contains("PRIVATE-REF-876543"));assertFalse(bytes.contains("PRIVATE-NOTE-987654"))
            reopened.clear();assertTrue(reopened.read().records.isEmpty())
        }
    }
    @Test fun multipleApplicationsForOneServiceAndManualEntriesAreIndependent()=fixture {repo,_,_ ->
        val catalog=CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use {it.readText()})
        val s=catalog.services.first {it.id=="rti-central-request"}.trackerSnapshot(catalog.version)
        val a=repo.save(ApplicationDraft("Request A"),service=s);val b=repo.save(ApplicationDraft("Request B"),service=s)
        val manual=repo.save(ApplicationDraft("Outside pilot"))
        assertNotEquals(a.id,b.id);assertEquals(s,a.service);assertNull(manual.service)
        assertEquals("Planning to apply",a.status.label);assertNull(a.submittedOn)
        // Replacing a catalog value is independent of this repository; snapshots retain exact jurisdiction and URLs.
        val revised=catalog.copy(version=catalog.version+1,services=emptyList())
        assertTrue(revised.services.isEmpty());assertEquals(a,repo.get(a.id));assertEquals(3,repo.read().records.size)
        assertFalse(catalog.services.first {it.applicationMode=="assisted"}.hasConfirmedApplicationRoute())
        assertFalse(catalog.services.first {it.applicationMode=="information"}.hasConfirmedApplicationRoute())
    }
    @Test fun additiveVersionOneMigrationKeepsEncryptedRecordAndHistory()=fixture {repo,file,cipher ->
        val r=repo.save(ApplicationDraft("Before migration",progressNote="Retained history"));repo.close()
        SQLiteDatabase.openDatabase(file.absolutePath,null,SQLiteDatabase.OPEN_READWRITE).use {
            it.execSQL("DROP TABLE reminder_deliveries");it.version=1
        }
        SqliteApplicationRepository(context,file,cipher).use {migrated ->
            assertEquals(r,migrated.get(r.id));assertNull(migrated.deliveredKey(r.id))
            assertEquals(2,SQLiteDatabase.openDatabase(file.absolutePath,null,SQLiteDatabase.OPEN_READONLY).use {it.version})
        }
    }
    @Test fun corruptAndInvalidPayloadsArePreservedAndDoNotOverwriteGoodData()=fixture {repo,file,cipher ->
        val good=repo.save(ApplicationDraft("Good"));val broken=repo.save(ApplicationDraft("Broken"));val invalid=repo.save(ApplicationDraft("Invalid"));repo.close()
        SQLiteDatabase.openDatabase(file.absolutePath,null,SQLiteDatabase.OPEN_READWRITE).use {db ->
            db.update("applications",ContentValues().apply {put("payload",byteArrayOf(1,2,3))},"id=?",arrayOf(broken.id))
            db.update("applications",ContentValues().apply {put("payload",cipher.encrypt(Json.encodeToString(invalid.copy(title="")).toByteArray(),"application:${invalid.id}"))},"id=?",arrayOf(invalid.id))
        }
        SqliteApplicationRepository(context,file,cipher).use {reopened ->
            val snapshot=reopened.read();assertEquals(listOf(good),snapshot.records);assertEquals(2,snapshot.unreadableIds.size)
            assertThrows(TrackerStorageException::class.java) {reopened.save(ApplicationDraft("Overwrite"),broken.id,broken.revision)}
            reopened.delete(broken.id);assertEquals(good,reopened.get(good.id));assertEquals(1,reopened.read().unreadableIds.size)
            reopened.clear();assertTrue(reopened.read().records.isEmpty())
        }
    }
    @Test fun wholeDatabaseCorruptionDoesNotTriggerDestructiveRecovery()=fixture {repo,file,_ ->
        repo.save(ApplicationDraft("Existing"));repo.close();file.writeText("intentional-invalid-database")
        val before=file.readBytes()
        assertThrows(TrackerStorageException::class.java) {repo.read()}
        assertArrayEquals(before,file.readBytes())
        repo.clear();assertTrue(repo.read().records.isEmpty())
    }
    @Test fun authenticatedEncryptionRejectsPayloadSwapsAndContainsNoPlaintext()=fixture {repo,file,cipher ->
        val r=repo.save(ApplicationDraft("Secret application",referenceNumber="Secret reference",notes="Secret note"))
        val bytes=SQLiteDatabase.openDatabase(file.absolutePath,null,SQLiteDatabase.OPEN_READONLY).use {db ->
            db.rawQuery("SELECT payload FROM applications WHERE id=?",arrayOf(r.id)).use {it.moveToFirst();it.getBlob(0)}
        }
        val raw=file.readBytes().toString(Charsets.ISO_8859_1)
        assertFalse(raw.contains("Secret application"));assertFalse(raw.contains("Secret reference"));assertFalse(raw.contains("Secret note"))
        assertThrows(Exception::class.java) {cipher.decrypt(bytes,"application:another-id")}
        assertTrue(file.canonicalPath.startsWith(context.noBackupFilesDir.canonicalPath))
    }
    @Test fun reminderDeliveryGuardSurvivesReopenAndRechecksDeletedOrChangedDates()=fixture {repo,file,cipher ->
        val r=repo.save(ApplicationDraft("Reminder",deadline=LocalDate.now().plusDays(2).toString(),reminderEnabled=true))
        val key=requireNotNull(ApplicationRules.reminderKey(r));var posts=0
        assertTrue(repo.deliverOnce(r.id,key) {posts++;true});repo.close()
        SqliteApplicationRepository(context,file,cipher).use {reopened ->
            assertFalse(reopened.deliverOnce(r.id,key) {posts++;true});assertEquals(1,posts)
            val changed=reopened.save(ApplicationDraft("Reminder",deadline=LocalDate.now().plusDays(3).toString(),reminderEnabled=true),r.id,r.revision)
            assertFalse(reopened.deliverOnce(r.id,key) {error("Stale")})
            assertTrue(reopened.deliverOnce(r.id,requireNotNull(ApplicationRules.reminderKey(changed))) {posts++;true})
            reopened.delete(r.id);assertFalse(reopened.deliverOnce(r.id,key) {error("Deleted")})
        }
    }
    @Test fun progressDatesUseCurrentTimezoneAcrossMidnightWithoutRecreatingRepository()=fixture {repo,file,cipher ->
        repo.close()
        var zone=ZoneId.of("America/Los_Angeles")
        val clock=Clock.fixed(Instant.parse("2026-10-16T00:30:00Z"),ZoneOffset.UTC)
        SqliteApplicationRepository(context,file,cipher,clock,{zone}).use {zoned ->
            val a=zoned.save(ApplicationDraft("Before timezone change"))
            assertEquals("2026-10-15",a.history.single().occurredOn)
            assertThrows(IllegalArgumentException::class.java) {zoned.save(ApplicationDraft("Future submission",submittedOn="2026-10-16"))}
            zone=ZoneId.of("Asia/Kolkata")
            val b=zoned.save(ApplicationDraft("After timezone change",submittedOn="2026-10-16"))
            assertEquals("2026-10-16",b.history.single().occurredOn)
            assertEquals("2026-10-15",zoned.get(a.id)?.history?.single()?.occurredOn)
        }
    }
}
