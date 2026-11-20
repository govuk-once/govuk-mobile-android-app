package uk.gov.govuk.dwp.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

private val dayMonthYearFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.UK)

fun LocalDate.toDistanceInWordsFormat(
    date: LocalDate
): String {
    val daysBetween = ChronoUnit.DAYS.between(this, date).toInt()
    val weeksBetween = ChronoUnit.WEEKS.between(this, date).toInt()
    val monthsBetween = ChronoUnit.MONTHS.between(this, date).toInt()
    val yearsBetween = ChronoUnit.YEARS.between(this, date).toInt()

    val timeFrame = when {
        yearsBetween == 1 -> "next year"
        yearsBetween == -1 -> "last year"
        yearsBetween > 1 -> "in $yearsBetween years"
        yearsBetween < -1 -> "${abs(yearsBetween)} years ago"
        monthsBetween == 1 -> "next month"
        monthsBetween == -1 -> "last month"
        monthsBetween > 1 -> "in $monthsBetween months"
        monthsBetween < -1 -> "${abs(monthsBetween)} months ago"
        weeksBetween == 1 -> "next week"
        weeksBetween == -1 -> "last week"
        weeksBetween > 1 -> "in $weeksBetween weeks"
        weeksBetween < -1 -> "${abs(weeksBetween)} weeks ago"
        daysBetween == 1 -> "tomorrow"
        daysBetween == -1 -> "yesterday"
        daysBetween > 1 -> "in $daysBetween days"
        daysBetween < -1 -> "${abs(daysBetween)} days ago"
        else -> "today"
    }

    return "${date.format(dayMonthYearFormatter)} ($timeFrame)"
}
