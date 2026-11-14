package `in`.jansetu.app.tracker

import java.time.*

enum class ReminderState(val explanation: String) {
    OFF("Reminders are off."), NOTIFICATIONS_OFF("Notifications are disabled. Reminder preference saved; no reminder is scheduled. Enable notifications to schedule future reminders."),
    PAST("The reminder time has passed; no new reminder is scheduled."), POSTED("A notification was already posted for this deadline. Delivery or reading is not confirmed."),
    SCHEDULED("Local reminder scheduled. Android may delay it; delivery is not confirmed."), FAILED("The record is saved, but the reminder could not be scheduled. Reopen My Applications to retry.")
}

interface ReminderScheduler {
    fun available(): Boolean
    fun schedule(id: String, key: String, at: Instant)
    fun cancel(id: String)
    fun cancelAll()
}

class ApplicationTracker(val repository: ApplicationRepository, private val scheduler: ReminderScheduler,
    private val clock: Clock = Clock.systemUTC(), private val zoneProvider: () -> ZoneId = {ZoneId.systemDefault()}) {
    fun notificationsAvailable(): Boolean = scheduler.available()
    fun sync(record: ApplicationRecord): ReminderState = synchronized(LOCK) { try {
        val current=repository.get(record.id)
        if(current==null) {scheduler.cancel(record.id);return@synchronized ReminderState.OFF}
        val key = ApplicationRules.reminderKey(current)
        when {
            key == null -> { scheduler.cancel(current.id); ReminderState.OFF }
            !scheduler.available() -> { scheduler.cancel(current.id); ReminderState.NOTIFICATIONS_OFF }
            repository.deliveredKey(current.id) == key -> ReminderState.POSTED
            !ApplicationRules.reminderInstant(requireNotNull(current.deadline),current.reminderDaysBefore,zoneProvider()).isAfter(clock.instant()) -> {
                scheduler.cancel(record.id); ReminderState.PAST
            }
            else -> {
                scheduler.schedule(current.id,key,ApplicationRules.reminderInstant(requireNotNull(current.deadline),current.reminderDaysBefore,zoneProvider()))
                ReminderState.SCHEDULED
            }
        }
    } catch (_: Exception) { ReminderState.FAILED } }

    fun reconcile(): Map<String, ReminderState> {
        val snapshot = repository.read()
        snapshot.unreadableIds.forEach(scheduler::cancel)
        return snapshot.records.associate { it.id to sync(it) }
    }
    fun save(draft: ApplicationDraft, id: String? = null, revision: Long? = null, service: ServiceSnapshot? = null): Pair<ApplicationRecord, ReminderState> = synchronized(LOCK) {
        val record = repository.save(draft,id,revision,service)
        record to sync(record)
    }
    fun delete(id: String) { synchronized(LOCK) { repository.delete(id); scheduler.cancel(id) } }
    fun clear() { synchronized(LOCK) { scheduler.cancelAll(); repository.clear() } }

    fun receive(id: String, key: String, post: (ApplicationRecord) -> Boolean): Boolean = synchronized(LOCK) { repository.deliverOnce(id,key) { record ->
        val deadline = record.deadline?.let(ApplicationRules::date)
        val zone=zoneProvider()
        val due = deadline?.let { ApplicationRules.reminderInstant(it.toString(),record.reminderDaysBefore,zone) }
        scheduler.available() && deadline != null && due != null && clock.instant() >= due &&
            clock.instant() < deadline.plusDays(1).atStartOfDay(zone).toInstant() && post(record)
    } }
    private companion object {val LOCK=Any()}
}
