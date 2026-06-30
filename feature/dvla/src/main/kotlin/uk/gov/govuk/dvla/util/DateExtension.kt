package uk.gov.govuk.dvla.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val dayMonthYearFormat = DateTimeFormatter.ofPattern("d MMMM yyyy")
private val yearFormat = DateTimeFormatter.ofPattern("yyyy")

internal fun LocalDate.toSummaryDisplayFormat(): String = this.format(dayMonthYearFormat)

internal fun LocalDate.toYearDisplayFormat(): String = this.format(yearFormat)

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

internal fun LocalDate.isInThePast() =
    this.getNumberOfDaysFromNow() < 0
