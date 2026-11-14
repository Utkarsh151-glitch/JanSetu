package `in`.jansetu.app.tracker

import `in`.jansetu.app.catalog.Service
import kotlinx.serialization.Serializable
import java.net.URI
import java.time.*
import java.util.UUID
import java.security.MessageDigest

@Serializable
enum class ApplicationStatus(val label: String) {
    PLANNING("Planning to apply"), PREPARING("Preparing documents"), READY("Ready to submit"),
    SUBMITTED("Submitted"), FOLLOW_UP("Follow-up needed"), APPROVED("Approved"), REJECTED("Rejected"), WITHDRAWN("Withdrawn");
    val closed: Boolean get() = this in setOf(APPROVED, REJECTED, WITHDRAWN)
}

@Serializable
data class ServiceSnapshot(
    val serviceId: String, val serviceName: String, val department: String,
    val catalogVersion: Int, val level: String, val state: String? = null, val district: String? = null,
    val informationUrl: String, val applicationUrl: String, val sourceUrl: String, val checkedOn: String,
)

fun Service.hasConfirmedApplicationRoute(): Boolean =
    publicationStatus == "published" && verificationStatus == "verified" && applicationMode == "online" &&
        applicationUrl != null && officialTrackerUrl(applicationUrl) &&
        (applicationRouteEvidence == null || (applicationRouteEvidence.status == "confirmed" &&
            applicationRouteEvidence.url == applicationUrl && applicationRouteEvidence.tlsVerified &&
            applicationRouteEvidence.entryReviewed && applicationRouteEvidence.linkedFromSource))

fun Service.trackerSnapshot(version: Int): ServiceSnapshot {
    require(hasConfirmedApplicationRoute())
    return ServiceSnapshot(id, title, department, version, level, state, district,
        informationUrl, requireNotNull(applicationUrl), sourceUrl, verifiedOn)
}

fun officialTrackerUrl(url: String): Boolean = runCatching {
    val u = URI(url); val h = u.host.orEmpty().lowercase()
    u.scheme == "https" && u.userInfo == null && u.port in listOf(-1, 443) && u.fragment == null &&
        (h.endsWith(".gov.in") || h.endsWith(".nic.in"))
}.getOrDefault(false)

@Serializable
data class ProgressEvent(val id: String, val status: ApplicationStatus, val occurredOn: String,
    val note: String = "", val recordedAt: String) {
    override fun toString() = "ProgressEvent(redacted)"
}

@Serializable
data class ApplicationRecord(
    val id: String, val title: String, val department: String = "", val service: ServiceSnapshot? = null,
    val submittedOn: String? = null, val referenceNumber: String = "", val status: ApplicationStatus = ApplicationStatus.PLANNING,
    val deadline: String? = null, val reminderEnabled: Boolean = false, val reminderDaysBefore: Int = 0,
    val notes: String = "", val createdAt: String, val updatedAt: String, val revision: Long = 1,
    val statusProvenance: String = "user_entered", val history: List<ProgressEvent> = emptyList(),
) {
    override fun toString() = "ApplicationRecord(redacted)"
}

data class ApplicationDraft(
    val title: String, val department: String = "", val submittedOn: String = "", val referenceNumber: String = "",
    val status: ApplicationStatus = ApplicationStatus.PLANNING, val deadline: String = "",
    val reminderEnabled: Boolean = false, val reminderDaysBefore: Int = 0, val notes: String = "",
    val progressNote: String = "", val progressDate: String = "",
) { override fun toString() = "ApplicationDraft(redacted)" }

enum class ApplicationSort(val label: String) { UPDATED("Most recent update"), DEADLINE("Upcoming deadline"), SUBMITTED("Submission date") }

object ApplicationRules {
    fun date(value: String): LocalDate? = runCatching {
        require(value.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
        LocalDate.parse(value).also { require(it.year in 1900..2200) }
    }.getOrNull()

    fun errors(d: ApplicationDraft, now: Instant = Instant.now(), zone: ZoneId = ZoneId.systemDefault()): List<String> {
        val today = now.atZone(zone).toLocalDate()
        val errors = mutableListOf<String>()
        if (d.title.isBlank() || d.title.length > 160) errors += "Enter a title of 1–160 characters."
        if (d.department.length > 200 || d.referenceNumber.length > 120 || d.notes.length > 4000 || d.progressNote.length > 1000)
            errors += "A field is too long. Department: 200, reference: 120, notes: 4000, progress note: 1000 characters."
        val submitted = date(d.submittedOn); val deadline = date(d.deadline); val progress = date(d.progressDate)
        for ((label, value) in listOf("Submission date" to d.submittedOn, "Deadline" to d.deadline, "Progress date" to d.progressDate))
            if (value.isNotBlank() && date(value) == null) errors += "$label must be a real date in YYYY-MM-DD format (1900–2200)."
        if (submitted != null && submitted > today) errors += "Submission date cannot be in the future."
        if (progress != null && progress > today) errors += "Progress date cannot be in the future."
        if (submitted != null && deadline != null && deadline < submitted) errors += "Deadline cannot be earlier than submission date. Check both dates."
        if (d.reminderDaysBefore !in listOf(0, 1, 3, 7)) errors += "Choose an available reminder interval."
        if (d.reminderEnabled) {
            if (deadline == null) errors += "A valid deadline is required for a reminder."
            else if (!reminderInstant(deadline.toString(), d.reminderDaysBefore, zone).isAfter(now))
                errors += "The reminder time has passed. Choose a later deadline or turn the reminder off."
            if (d.status.closed) errors += "Turn off reminders for approved, rejected or withdrawn applications."
        }
        return errors
    }

    fun reminderInstant(deadline: String, daysBefore: Int, zone: ZoneId): Instant =
        requireNotNull(date(deadline)).minusDays(daysBefore.toLong()).atTime(9, 0).atZone(zone).toInstant()

    fun reminderKey(r: ApplicationRecord): String? = if (!r.reminderEnabled || r.status.closed || r.deadline == null) null
        else MessageDigest.getInstance("SHA-256").digest("${r.id}:${r.deadline}:${r.reminderDaysBefore}".toByteArray()).joinToString("") {"%02x".format(it)}

    fun visible(records: List<ApplicationRecord>, status: ApplicationStatus?, sort: ApplicationSort, today: LocalDate = LocalDate.now()): List<ApplicationRecord> {
        val filtered = records.filter { status == null || it.status == status }
        return when (sort) {
            ApplicationSort.UPDATED -> filtered.sortedWith(compareByDescending<ApplicationRecord> { it.updatedAt }.thenBy { it.id })
            ApplicationSort.SUBMITTED -> filtered.sortedWith(compareByDescending<ApplicationRecord> { it.submittedOn ?: "" }.thenBy { it.id })
            ApplicationSort.DEADLINE -> filtered.sortedWith(compareBy<ApplicationRecord> {
                it.deadline?.let(::date)?.takeIf { date -> date >= today } == null
            }.thenBy { it.deadline ?: "9999-12-31" }.thenBy { it.id })
        }
    }

    fun validateStored(r: ApplicationRecord) {
        UUID.fromString(r.id); Instant.parse(r.createdAt); Instant.parse(r.updatedAt)
        require(r.title.isNotBlank() && r.title.length <= 160 && r.department.length <= 200 && r.notes.length <= 4000 && r.referenceNumber.length <= 120)
        require(r.revision > 0 && r.statusProvenance == "user_entered" && r.reminderDaysBefore in listOf(0,1,3,7))
        require(r.submittedOn == null || date(r.submittedOn) != null)
        require(r.deadline == null || date(r.deadline) != null)
        require(!r.reminderEnabled || r.deadline != null)
        r.service?.let { require(officialTrackerUrl(it.informationUrl) && officialTrackerUrl(it.applicationUrl) && officialTrackerUrl(it.sourceUrl)) }
        r.history.forEach { UUID.fromString(it.id); require(date(it.occurredOn) != null && it.note.length <= 1000); Instant.parse(it.recordedAt) }
    }
}

data class TrackerSnapshot(val records: List<ApplicationRecord>, val unreadableIds: List<String> = emptyList())
class TrackerStorageException : Exception("Application storage is unavailable. Existing data has not been reset.")
class TrackerConflictException : Exception("This application changed. Reopen it before editing.")

/** Blocking repository: invoke on IO. Catalog repositories never write to this store. */
interface ApplicationRepository {
    fun read(): TrackerSnapshot
    fun get(id: String): ApplicationRecord?
    fun save(draft: ApplicationDraft, id: String? = null, revision: Long? = null, service: ServiceSnapshot? = null): ApplicationRecord
    fun delete(id: String)
    fun clear()
    fun deliveredKey(id: String): String?
    fun deliverOnce(id: String, key: String, action: (ApplicationRecord) -> Boolean): Boolean
}
