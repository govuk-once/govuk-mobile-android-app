package uk.gov.govuk.dvla.util

import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dayMonthYearFormat = DateTimeFormatter.ofPattern("d MMMM yyyy")
private val yearFormat = DateTimeFormatter.ofPattern("yyyy")

internal fun LocalDate.toSummaryDisplayFormat(): String = this.format(dayMonthYearFormat)

internal fun LocalDate.toYearDisplayFormat(): String = this.format(yearFormat)

internal fun LocalDate.getDifferenceBetweenDaysAsPercentage(daysThreshold: Int): Int {
    val daysLeft = this.getDaysBetweenNow().toFloat()
    return daysLeft.div(daysThreshold).times(100).toInt()
}

internal fun LocalDate.getDaysBetweenNow() =
    Duration.between(LocalDate.now().atStartOfDay(), this.atStartOfDay())
        .toDays().toInt()
