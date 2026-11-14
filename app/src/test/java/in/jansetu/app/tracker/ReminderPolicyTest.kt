package `in`.jansetu.app.tracker

import org.junit.Assert.*
import org.junit.Test
import java.time.*
import java.util.UUID

class ReminderPolicyTest {
    private val now=Instant.parse("2026-10-16T02:00:00Z")
    private val record=ApplicationRecord(UUID.randomUUID().toString(),"Private title",deadline="2026-10-17",reminderEnabled=true,createdAt=now.toString(),updatedAt=now.toString())
    private class MemoryRepo(var row:ApplicationRecord?) : ApplicationRepository {
        var delivered:String?=null
        override fun read()=TrackerSnapshot(listOfNotNull(row))
        override fun get(id:String)=row?.takeIf {it.id==id}
        override fun save(draft:ApplicationDraft,id:String?,revision:Long?,service:ServiceSnapshot?):ApplicationRecord=error("Not used")
        override fun delete(id:String) {if(row?.id==id)row=null}
        override fun clear() {row=null}
        override fun deliveredKey(id:String)=delivered
        override fun deliverOnce(id:String,key:String,action:(ApplicationRecord)->Boolean):Boolean {
            val r=get(id) ?: return false
            if(ApplicationRules.reminderKey(r)!=key || delivered==key || !action(r))return false
            delivered=key;return true
        }
    }
    private class Scheduler : ReminderScheduler {
        var enabled=true;val alarms=mutableMapOf<String,Pair<String,Instant>>();val cancelled=mutableListOf<String>()
        override fun available()=enabled
        override fun schedule(id:String,key:String,at:Instant) {alarms[id]=key to at}
        override fun cancel(id:String) {alarms.remove(id);cancelled+=id}
        override fun cancelAll() {alarms.clear()}
    }
    private fun engine(repo:MemoryRepo,scheduler:Scheduler,time:Instant=now,zone:String="Asia/Kolkata")=ApplicationTracker(repo,scheduler,Clock.fixed(time,ZoneId.of(zone)),{ZoneId.of(zone)})
    @Test fun repeatedSchedulingReplacesOneAlarmAndDeadlineChangesReplaceIt() {
        val repo=MemoryRepo(record);val scheduler=Scheduler();val engine=engine(repo,scheduler)
        repeat(3) {assertEquals(ReminderState.SCHEDULED,engine.sync(record))};assertEquals(1,scheduler.alarms.size)
        val changed=record.copy(deadline="2026-10-19");repo.row=changed;engine.sync(changed)
        assertEquals(ApplicationRules.reminderKey(changed),scheduler.alarms[record.id]?.first)
    }
    @Test fun denialCancellationDeletionAndExpiredDeadlinesDoNotPost() {
        val repo=MemoryRepo(record);val scheduler=Scheduler();val engine=engine(repo,scheduler)
        engine.sync(record);scheduler.enabled=false
        assertEquals(ReminderState.NOTIFICATIONS_OFF,engine.sync(record));assertTrue(scheduler.alarms.isEmpty())
        assertFalse(engine.receive(record.id,requireNotNull(ApplicationRules.reminderKey(record))) {error("Must not post")})
        scheduler.enabled=true;repo.row=record.copy(reminderEnabled=false);assertEquals(ReminderState.OFF,engine.sync(requireNotNull(repo.row)))
        repo.row=record.copy(deadline="2026-10-15");assertEquals(ReminderState.PAST,engine.sync(requireNotNull(repo.row)))
        engine.delete(record.id);assertNull(repo.row);assertFalse(engine.receive(record.id,"old") {true})
    }
    @Test fun deliveryRechecksCurrentRecordTimeAndPersistsDuplicateGuard() {
        val repo=MemoryRepo(record);val scheduler=Scheduler();val key=requireNotNull(ApplicationRules.reminderKey(record))
        assertFalse(engine(repo,scheduler).receive(record.id,key) {error("Too early")})
        val due=Instant.parse("2026-10-17T03:31:00Z");var posts=0
        assertFalse(engine(repo,scheduler,due).receive(record.id,"stale") {error("Wrong deadline")})
        assertTrue(engine(repo,scheduler,due).receive(record.id,key) {posts++;true})
        assertFalse(engine(repo,scheduler,due).receive(record.id,key) {posts++;true});assertEquals(1,posts)
        assertEquals(ReminderState.POSTED,engine(repo,scheduler,due).sync(record))
        repo.delivered=null
        assertFalse(engine(repo,scheduler,Instant.parse("2026-10-18T00:00:00Z")).receive(record.id,key) {error("Expired")})
    }
    @Test fun timezoneReconciliationChangesInstantWithoutChangingDeliveryIdentity() {
        val repo=MemoryRepo(record);val scheduler=Scheduler()
        var zone=ZoneId.of("Asia/Kolkata")
        val tracker=ApplicationTracker(repo,scheduler,Clock.fixed(now,ZoneOffset.UTC),{zone})
        tracker.reconcile();val india=scheduler.alarms.getValue(record.id)
        zone=ZoneId.of("America/New_York")
        tracker.reconcile();val us=scheduler.alarms.getValue(record.id)
        assertEquals(india.first,us.first);assertNotEquals(india.second,us.second)
        engine(repo,scheduler).clear();assertTrue(scheduler.alarms.isEmpty());assertNull(repo.row)
    }
}
