package uk.gov.govuk.govkit.date

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private val london = ZoneId.of("Europe/London")

/**
 * Formats this [Instant] as a human-readable relative date, suitable for use in list views.
 * Time is only shown when the date is today.
 *
 * - Today     → "Today, 9:45pm"
 * - Yesterday → "Yesterday"
 * - < 7 days  → "Tuesday"
 * - Older     → "7 December"
 *
 * @param now the reference instant used to compute relative labels; defaults to [Instant.now].
 */
fun Instant.toRelativeDate(now: Instant = Instant.now()): String {
    val zonedSelf = atZone(london)
    val today = now.atZone(london)

    val isToday = zonedSelf.toLocalDate() == today.toLocalDate()
    val isYesterday = zonedSelf.toLocalDate() == today.toLocalDate().minusDays(1)
    val daysAgo = ChronoUnit.DAYS.between(zonedSelf.toLocalDate(), today.toLocalDate())

    return when {
        isToday -> DateTimeFormatter.ofPattern("'Today,' h:mma", Locale.getDefault())
            .withZone(london)
            .format(this)
            .replace("AM", "am")
            .replace("PM", "pm")
        isYesterday -> "Yesterday"
        daysAgo < 7 -> zonedSelf.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        else -> DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
            .withZone(london)
            .format(this)
    }
}

/**
 * Formats this [Instant] as a human-readable relative date and time, suitable for use in detail
 * views. Unlike [toRelativeDate], the time component is always included.
 *
 * - Today     → "Today, 9:45pm"
 * - Yesterday → "Yesterday, 9:45pm"
 * - Older     → "7 December, 9:45pm"
 *
 * @param now the reference instant used to compute relative labels; defaults to [Instant.now].
 */
fun Instant.toRelativeDateTime(now: Instant = Instant.now()): String {
    val zonedSelf = atZone(london)
    val today = now.atZone(london)

    val isToday = zonedSelf.toLocalDate() == today.toLocalDate()
    val isYesterday = zonedSelf.toLocalDate() == today.toLocalDate().minusDays(1)

    val pattern = when {
        isToday -> "'Today,' h:mma"
        isYesterday -> "'Yesterday,' h:mma"
        else -> "d MMMM, h:mma"
    }

    return DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        .withZone(london)
        .format(this)
        .replace("AM", "am")
        .replace("PM", "pm")
}
