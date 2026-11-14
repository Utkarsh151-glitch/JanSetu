package `in`.jansetu.app.tracker

import org.junit.Assert.*
import org.junit.Test
import java.time.*
import java.util.UUID

class ApplicationRulesTest {
    private val now=Instant.parse("2026-10-16T04:00:00Z")
    private val zone=ZoneId.of("Asia/Kolkata")
    private fun record(title:String="Application",deadline:String?=null,submitted:String?=null,updated:String=now.toString()) = ApplicationRecord(
        UUID.randomUUID().toString(),title,deadline=deadline,submittedOn=submitted,createdAt=now.toString(),updatedAt=updated)

    @Test fun realDatesOptionalFieldsAndInconsistentDatesAreValidated() {
        assertTrue(ApplicationRules.errors(ApplicationDraft("Manual"),now,zone).isEmpty())
        assertNull(ApplicationRules.date("2026-02-30"));assertNull(ApplicationRules.date("26-1-1"))
        for(d in listOf(ApplicationDraft(""),ApplicationDraft("X",submittedOn="2026-10-17"),
            ApplicationDraft("X",submittedOn="2026-10-16",deadline="2026-10-15"),ApplicationDraft("X",progressDate="2026-10-17")))
            assertTrue(ApplicationRules.errors(d,now,zone).isNotEmpty())
        assertTrue(ApplicationRules.errors(ApplicationDraft("Old",deadline="2025-01-01"),now,zone).isEmpty())
    }
    @Test fun reminderNeedsFutureTimeAndAnOpenApplication() {
        for(d in listOf(ApplicationDraft("X",reminderEnabled=true),ApplicationDraft("X",deadline="2026-10-16",reminderEnabled=true),
            ApplicationDraft("X",deadline="2026-10-18",reminderEnabled=true,reminderDaysBefore=3),
            ApplicationDraft("X",deadline="2026-10-18",reminderEnabled=true,status=ApplicationStatus.APPROVED)))
            assertTrue(ApplicationRules.errors(d,now,zone).isNotEmpty())
        assertTrue(ApplicationRules.errors(ApplicationDraft("X",deadline="2026-10-18",reminderEnabled=true,reminderDaysBefore=1),now,zone).isEmpty())
    }
    @Test fun reminderUsesLocalCalendarDaysAcrossTimezonesAndDst() {
        assertEquals(Instant.parse("2026-10-17T03:30:00Z"),ApplicationRules.reminderInstant("2026-10-18",1,zone))
        assertEquals(Instant.parse("2026-11-01T14:00:00Z"),ApplicationRules.reminderInstant("2026-11-02",1,ZoneId.of("America/New_York")))
        assertEquals(Instant.parse("2026-03-08T13:00:00Z"),ApplicationRules.reminderInstant("2026-03-09",1,ZoneId.of("America/New_York")))
    }
    @Test fun filteringSortingAndEmptyResultsAreDeterministic() {
        val future=record("Future","2026-10-18");val later=record("Later","2026-10-20")
        val past=record("Past","2026-10-01");val noDeadline=record("None",submitted="2026-10-15",updated="2026-10-17T00:00:00Z")
        val rows=listOf(noDeadline,later,past,future)
        assertEquals(listOf("Future","Later","Past","None"),ApplicationRules.visible(rows,null,ApplicationSort.DEADLINE,LocalDate.parse("2026-10-16")).map {it.title})
        assertEquals("None",ApplicationRules.visible(rows,null,ApplicationSort.UPDATED).first().title)
        assertEquals("None",ApplicationRules.visible(rows,null,ApplicationSort.SUBMITTED).first().title)
        assertTrue(ApplicationRules.visible(rows,ApplicationStatus.APPROVED,ApplicationSort.UPDATED).isEmpty())
    }
    @Test fun reminderIdentityChangesOnlyForDeadlinePreferencesAndClosedStatesCancel() {
        val r=record(deadline="2026-10-18").copy(reminderEnabled=true)
        assertEquals(ApplicationRules.reminderKey(r),ApplicationRules.reminderKey(r.copy(title="Rename",notes="Private")))
        assertNotEquals(ApplicationRules.reminderKey(r),ApplicationRules.reminderKey(r.copy(deadline="2026-10-19")))
        assertNull(ApplicationRules.reminderKey(r.copy(status=ApplicationStatus.WITHDRAWN)))
        assertNull(ApplicationRules.reminderKey(r.copy(reminderEnabled=false)))
        assertFalse(r.toString().contains("Rename"));assertFalse(ApplicationDraft("Private").toString().contains("Private"))
    }
    @Test fun malformedOfficialLinksAndStoredProvenanceAreRejected() {
        assertFalse(officialTrackerUrl("https://portal.gov.in.evil.example"));assertFalse(officialTrackerUrl("https://user@portal.gov.in/"))
        assertFalse(officialTrackerUrl("http://portal.gov.in/"));assertFalse(officialTrackerUrl("bad"));assertTrue(officialTrackerUrl("https://rtionline.gov.in/"))
        assertThrows(IllegalArgumentException::class.java) {ApplicationRules.validateStored(record().copy(statusProvenance="government_verified"))}
    }
}
