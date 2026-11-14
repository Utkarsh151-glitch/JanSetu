package `in`.jansetu.app.tracker

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

/**
 * The person's own copy of My Applications, written only when they ask and only where they choose.
 * It is readable JSON, not encrypted, and says so. Reminder bookkeeping and internal IDs of other stores are not
 * included; nothing in it is presented as a government record.
 */
object ApplicationExport {
    @Serializable data class ExportedEvent(val occurredOn: String, val status: String, val note: String, val recordedAt: String)
    @Serializable data class ExportedService(val serviceId: String, val serviceName: String, val department: String, val level: String,
        val state: String?, val district: String?, val informationUrl: String, val applicationUrl: String, val sourceUrl: String,
        val sourceCheckedOn: String, val catalogVersion: Int)
    @Serializable data class ExportedApplication(val title: String, val department: String, val yourStatus: String,
        val submittedOn: String?, val deadline: String?, val referenceNumber: String, val notes: String,
        val reminder: String, val linkedService: ExportedService?, val history: List<ExportedEvent>,
        val createdAt: String, val updatedAt: String)
    @Serializable data class Export(val format: String, val formatVersion: Int, val exportedAt: String, val notice: String,
        val unreadableRecordsNotExported: Int, val applications: List<ExportedApplication>)

    const val NOTICE = "Exported from JanSetu My Applications. Every status, date and note was recorded by you on your device; " +
        "none of it is a government record or confirmation. This file is not encrypted."
    private val json = Json { prettyPrint = true; encodeDefaults = true }

    fun fileName(now: Instant): String = "jansetu-my-applications-${now.toString().take(10)}.json"

    fun export(snapshot: TrackerSnapshot, now: Instant): String = json.encodeToString(Export("jansetu-my-applications", 1, now.toString(), NOTICE,
        snapshot.unreadableIds.size, snapshot.records.sortedBy { it.createdAt }.map { r ->
            ExportedApplication(r.title, r.department, r.status.label, r.submittedOn, r.deadline, r.referenceNumber, r.notes,
                if (r.reminderEnabled) "${r.reminderDaysBefore} day(s) before your deadline" else "Off",
                r.service?.let { s -> ExportedService(s.serviceId, s.serviceName, s.department, s.level, s.state, s.district,
                    s.informationUrl, s.applicationUrl, s.sourceUrl, s.checkedOn, s.catalogVersion) },
                r.history.map { ExportedEvent(it.occurredOn, it.status.label, it.note, it.recordedAt) }, r.createdAt, r.updatedAt)
        }))
}
