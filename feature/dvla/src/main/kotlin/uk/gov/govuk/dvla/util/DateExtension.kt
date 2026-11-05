package uk.gov.govuk.dvla.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val dayMonthYearFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
private val yearFormatter = DateTimeFormatter.ofPattern("yyyy")
private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

internal fun LocalDate.toSummaryDisplayFormat(): String = this.format(dayMonthYearFormatter)

internal fun LocalDate.toYearDisplayFormat(): String = this.format(yearFormatter)

internal fun LocalDate.toMonthYearDisplayFormat(): String =  this.format(monthYearFormatter)


internal fun LocalDate.getNumberOfDaysWithinDayRangeAsPercentage(dayRange: Int): Float {
    val daysLeft = this.getNumberOfDaysFromNow().toFloat()
    return daysLeft.div(dayRange).times(100)
}

internal fun LocalDate.getNumberOfDaysFromNow() =
    ChronoUnit.DAYS.between(LocalDate.now(), this).toInt()

internal fun LocalDate.isDateWithinDayRange(dayRange: Int) =
    this.getNumberOfDaysFromNow() < dayRange + 1

internal fun LocalDate.isToday() =
    this.getNumberOfDaysFromNow() == 0

/**
 * Returns true if the date is in the future.
 * Returns false if the date is now, in the past or null.
 */
internal fun LocalDate?.isInTheFuture() =
    this?.let { getNumberOfDaysFromNow() > 0 } ?: run { false }

/**
 * Returns true if the date is in the past.
 * Returns false if the date is now, in the future or null.
 */
internal fun LocalDate?.isInThePast() =
    this?.let { getNumberOfDaysFromNow() < 0 } ?: run { false }
