package `in`.jansetu.app.tracker

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.util.UUID

class ApplicationExportTest {
    private val now = Instant.parse("2026-10-18T09:30:00Z")
    private fun record(title: String, created: String, service: ServiceSnapshot? = null) = ApplicationRecord(
        UUID.randomUUID().toString(), title, "Revenue Department", service, "2026-10-01", "REF-SYNTHETIC-1", ApplicationStatus.SUBMITTED,
        "2026-11-01", true, 3, "Synthetic note", created, created, 2, history = listOf(
            ProgressEvent(UUID.randomUUID().toString(), ApplicationStatus.PLANNING, "2026-09-30", "", created),
            ProgressEvent(UUID.randomUUID().toString(), ApplicationStatus.SUBMITTED, "2026-10-01", "Submitted on the official site", created)))

    @Test fun exportIsTheUsersOwnRecordClearlyLabelledAndComplete() {
        val service = ServiceSnapshot("dl-income-certificate", "Delhi Income Certificate", "Revenue Department", 13, "State", "Delhi", null,
            "https://edistrict.delhigovt.nic.in/", "https://edistrict.delhigovt.nic.in/Account/Login", "https://revenue.delhi.gov.in/", "2026-10-16")
        val text = ApplicationExport.export(TrackerSnapshot(listOf(record("Second", "2026-10-02T00:00:00Z"), record("First", "2026-10-01T00:00:00Z", service)),
            listOf(UUID.randomUUID().toString())), now)
        val root = Json.parseToJsonElement(text).jsonObject
        assertEquals("jansetu-my-applications", root["format"]!!.jsonPrimitive.content)
        assertTrue(root["notice"]!!.jsonPrimitive.content.contains("none of it is a government record"))
        assertTrue(root["notice"]!!.jsonPrimitive.content.contains("not encrypted"))
        assertEquals("1", root["unreadableRecordsNotExported"]!!.jsonPrimitive.content)
        val apps = root["applications"]!!.jsonArray
        assertEquals(listOf("First", "Second"), apps.map { it.jsonObject["title"]!!.jsonPrimitive.content })
        val first = apps[0].jsonObject
        assertEquals("Submitted", first["yourStatus"]!!.jsonPrimitive.content)
        assertEquals("REF-SYNTHETIC-1", first["referenceNumber"]!!.jsonPrimitive.content)
        assertEquals("3 day(s) before your deadline", first["reminder"]!!.jsonPrimitive.content)
        assertEquals("dl-income-certificate", first["linkedService"]!!.jsonObject["serviceId"]!!.jsonPrimitive.content)
        assertEquals(2, first["history"]!!.jsonArray.size)
        // Internal record and event IDs and reminder bookkeeping stay out of the file.
        assertFalse(text.contains("\"id\"")); assertFalse(text.contains("revision")); assertFalse(text.contains("fingerprint"))
        assertEquals("jansetu-my-applications-2026-10-18.json", ApplicationExport.fileName(now))
    }

    @Test fun emptyTrackerExportsAnEmptyList() {
        val root = Json.parseToJsonElement(ApplicationExport.export(TrackerSnapshot(emptyList()), now)).jsonObject
        assertTrue(root["applications"]!!.jsonArray.isEmpty())
        assertEquals("0", root["unreadableRecordsNotExported"]!!.jsonPrimitive.content)
    }
}
