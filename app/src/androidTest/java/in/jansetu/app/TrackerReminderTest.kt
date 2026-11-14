package `in`.jansetu.app

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.tracker.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.KeyStore
import java.time.*
import java.util.UUID

class TrackerReminderTest {
    private val instrumentation get()=InstrumentationRegistry.getInstrumentation()
    private val context get()=instrumentation.targetContext
    @Test fun actualAlarmIdentityReplacementCancellationAndPrivateNotificationPosting() {
        val unique=UUID.randomUUID().toString();val file=File(context.noBackupFilesDir,"reminder-test-$unique.db")
        val alias="reminder-test-$unique";val registry="reminder-test-$unique"
        val repo=SqliteApplicationRepository(context,file,KeystoreTrackerCipher(alias))
        val scheduler=AndroidReminderScheduler(context,registry)
        try {
            if(Build.VERSION.SDK_INT>=33) instrumentation.uiAutomation.grantRuntimePermission(context.packageName,Manifest.permission.POST_NOTIFICATIONS)
            assertTrue(scheduler.available())
            val deadline=LocalDate.now().plusDays(2)
            val r=repo.save(ApplicationDraft("PRIVATE TITLE",referenceNumber="PRIVATE REFERENCE",notes="PRIVATE NOTE",deadline=deadline.toString(),reminderEnabled=true))
            val tracker=ApplicationTracker(repo,scheduler)
            repeat(3) {assertEquals(ReminderState.SCHEDULED,tracker.sync(r))}
            val intent=Intent(context,DeadlineReminderReceiver::class.java).setData(Uri.parse("jansetu-local://reminder/${r.id}"))
            val first=PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            assertNotNull(first)
            val changed=repo.save(ApplicationDraft("PRIVATE TITLE",deadline=deadline.plusDays(1).toString(),reminderEnabled=true),r.id,r.revision)
            tracker.sync(changed)
            assertEquals(first,PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE))
            assertEquals(setOf(r.id),context.getSharedPreferences(registry,0).getStringSet("ids",emptySet()))
            val due=ApplicationRules.reminderInstant(requireNotNull(changed.deadline),0,ZoneId.systemDefault()).plusSeconds(60)
            val delivery=ApplicationTracker(repo,scheduler,Clock.fixed(due,ZoneId.systemDefault()))
            assertFalse(delivery.receive(r.id,requireNotNull(ApplicationRules.reminderKey(r)),scheduler::post))
            assertTrue(delivery.receive(r.id,requireNotNull(ApplicationRules.reminderKey(changed)),scheduler::post))
            assertFalse(delivery.receive(r.id,requireNotNull(ApplicationRules.reminderKey(changed)),scheduler::post))
            val notifications=context.getSystemService(NotificationManager::class.java)
            val posted=notifications.activeNotifications.first {it.tag==r.id}.notification
            val text=posted.extras.getCharSequence("android.title").toString()+posted.extras.getCharSequence("android.text").toString()
            assertFalse(text.contains("PRIVATE"));assertEquals(android.app.Notification.VISIBILITY_PRIVATE,posted.visibility)
            assertNotNull(posted.contentIntent)
            tracker.delete(r.id)
            assertNull(PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE))
            assertTrue(notifications.activeNotifications.none {it.tag==r.id})
            assertFalse(delivery.receive(r.id,requireNotNull(ApplicationRules.reminderKey(changed)),scheduler::post))
        } finally {
            scheduler.cancelAll();repo.close();SQLiteDatabase.deleteDatabase(file)
            context.deleteSharedPreferences(registry)
            KeyStore.getInstance("AndroidKeyStore").apply {load(null);deleteEntry(alias)}
        }
    }
}
