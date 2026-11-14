package `in`.jansetu.app.tracker

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import `in`.jansetu.app.MainActivity
import `in`.jansetu.app.R
import java.time.Instant
import java.util.UUID
import java.util.concurrent.Executors

class AndroidReminderScheduler(private val context: Context, registryName: String = "tracker_alarm_ids") : ReminderScheduler {
    private val alarm = context.getSystemService(AlarmManager::class.java)
    private val notifications = context.getSystemService(NotificationManager::class.java)
    private val registry = context.getSharedPreferences(registryName,Context.MODE_PRIVATE)
    private fun channel() {
        notifications.createNotificationChannel(NotificationChannel(CHANNEL,"Application deadline reminders",NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Optional local reminders for dates you record. No live government status."
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        })
    }
    override fun available(): Boolean {
        channel()
        return (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) &&
            NotificationManagerCompat.from(context).areNotificationsEnabled() && notifications.getNotificationChannel(CHANNEL)?.importance != NotificationManager.IMPORTANCE_NONE
    }
    private fun intent(id: String, key: String = "") = Intent(context,DeadlineReminderReceiver::class.java).apply {
        data = Uri.parse("jansetu-local://reminder/$id"); putExtra("key",key)
    }
    override fun schedule(id: String, key: String, at: Instant) {
        val pending = PendingIntent.getBroadcast(context,0,intent(id,key),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        // Identity is the record UUID; setting the same PendingIntent replaces the old alarm.
        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at.toEpochMilli(),pending)
        synchronized(REGISTRY_LOCK) { check(registry.edit().putStringSet("ids",registry.getStringSet("ids",emptySet()).orEmpty()+id).commit()) }
    }
    override fun cancel(id: String) {
        PendingIntent.getBroadcast(context,0,intent(id),PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)?.let {
            alarm.cancel(it); it.cancel()
        }
        notifications.cancel(id,0)
        synchronized(REGISTRY_LOCK) { check(registry.edit().putStringSet("ids",registry.getStringSet("ids",emptySet()).orEmpty()-id).commit()) }
    }
    override fun cancelAll() {
        val ids = synchronized(REGISTRY_LOCK) { registry.getStringSet("ids",emptySet()).orEmpty().toSet() }
        ids.forEach(::cancel)
    }
    @android.annotation.SuppressLint("MissingPermission") // available() checks runtime, app and channel permission immediately above notify.
    fun post(record: ApplicationRecord): Boolean {
        if (!available()) return false
        val open = Intent(context,MainActivity::class.java).apply {
            data = Uri.parse("jansetu-local://application/${record.id}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(context,0,open,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context,CHANNEL)
            .setSmallIcon(R.drawable.ic_deadline_notification)
            .setColor(ContextCompat.getColor(context,R.color.brand_primary))
            .setContentTitle("JanSetu deadline reminder")
            .setContentText("You recorded an application deadline. Open My Applications to review it.")
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE).setAutoCancel(true).setContentIntent(pending).build()
        // No application title, reference, private note, service name or status enters notification text.
        notifications.notify(record.id,0,notification)
        return true
    }
    companion object { const val CHANNEL = "application_deadlines"; private val REGISTRY_LOCK = Any() }
}

class TrackerDependencies private constructor(context: Context) {
    val scheduler = AndroidReminderScheduler(context)
    val repository = SqliteApplicationRepository(context)
    val tracker = ApplicationTracker(repository,scheduler)
    companion object {
        @Volatile private var instance: TrackerDependencies? = null
        fun get(context: Context): TrackerDependencies = instance ?: synchronized(this) {
            instance ?: TrackerDependencies(context.applicationContext).also { instance=it }
        }
    }
}

class DeadlineReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.data?.lastPathSegment ?: return
        if (runCatching { UUID.fromString(id) }.isFailure) return
        val key = intent.getStringExtra("key") ?: return
        val pending = goAsync()
        EXECUTOR.execute {
            try { val dependencies=TrackerDependencies.get(context); dependencies.tracker.receive(id,key,dependencies.scheduler::post) }
            catch (_: Exception) { /* Preserve records and do not log private data. */ }
            finally { pending.finish() }
        }
    }
}

class TrackerRescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in setOf(Intent.ACTION_BOOT_COMPLETED,Intent.ACTION_TIMEZONE_CHANGED,Intent.ACTION_TIME_CHANGED,Intent.ACTION_MY_PACKAGE_REPLACED)) return
        val pending = goAsync()
        EXECUTOR.execute {
            try { TrackerDependencies.get(context).tracker.reconcile() }
            catch (_: Exception) { /* Retry on app resume; no destructive recovery. */ }
            finally { pending.finish() }
        }
    }
}
private val EXECUTOR = Executors.newSingleThreadExecutor()
