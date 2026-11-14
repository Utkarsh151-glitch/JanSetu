package `in`.jansetu.app.ui

import `in`.jansetu.app.tracker.ApplicationRules
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** How a user-entered deadline relates to today. Presentation only; nothing here is an official date. */
enum class DeadlineUrgency { NONE, CLOSED, PASSED, TODAY, SOON, LATER }

data class DeadlineSummary(val urgency: DeadlineUrgency, val text: String)

object TrackerPresentation {
    const val SOON_DAYS = 7L
    // Day-month-year, the usual Indian convention, with an unambiguous month name.
    private val dateFormat = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
    private val dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ENGLISH)

    fun formatDate(value: String?): String? = value?.let(ApplicationRules::date)?.format(dateFormat)

    fun formatInstant(value: String, zone: ZoneId = ZoneId.systemDefault()): String = runCatching {
        Instant.parse(value).atZone(zone).format(dateTimeFormat)
    }.getOrDefault(value)

    fun updatedDate(value: String, zone: ZoneId = ZoneId.systemDefault()): String = runCatching {
        Instant.parse(value).atZone(zone).toLocalDate().format(dateFormat)
    }.getOrDefault(value)

    /**
     * Restrained wording for the list and detail screens. The distinction is
     * always in the words, so it never depends on colour.
     */
    fun deadline(deadline: String?, closed: Boolean, today: LocalDate = LocalDate.now()): DeadlineSummary {
        val date = deadline?.let(ApplicationRules::date) ?: return DeadlineSummary(DeadlineUrgency.NONE, "No deadline entered")
        val shown = date.format(dateFormat)
        if (closed) return DeadlineSummary(DeadlineUrgency.CLOSED, "Deadline you recorded: $shown")
        val days = ChronoUnit.DAYS.between(today, date)
        return when {
            days < 0 -> DeadlineSummary(DeadlineUrgency.PASSED, "Your deadline passed: $shown")
            days == 0L -> DeadlineSummary(DeadlineUrgency.TODAY, "Your deadline is today: $shown")
            days <= SOON_DAYS -> DeadlineSummary(DeadlineUrgency.SOON, "Due in $days day${if (days == 1L) "" else "s"}: $shown")
            else -> DeadlineSummary(DeadlineUrgency.LATER, "Your deadline: $shown")
        }
    }
}
