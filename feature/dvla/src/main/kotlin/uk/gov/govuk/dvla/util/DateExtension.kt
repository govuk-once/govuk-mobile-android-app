package uk.gov.govuk.dvla.util

import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dayMonthYearFormat = DateTimeFormatter.ofPattern("d MMMM yyyy")
private val yearFormat = DateTimeFormatter.ofPattern("yyyy")

internal fun LocalDate.toSummaryDisplayFormat(): String = this.format(dayMonthYearFormat)

internal fun LocalDate.toYearDisplayFormat(): String = this.format(yearFormat)

internal fun LocalDate.getNumberOfDaysUntilExpiryAsPercentage(daysUntilExpiry: Int): Int {
    val daysLeft = this.getNumberOfDaysFromNow().toFloat()
    return daysLeft.div(daysUntilExpiry).times(100).toInt()
}

internal fun LocalDate.getNumberOfDaysFromNow() =
    Duration.between(LocalDate.now().atStartOfDay(), this.atStartOfDay())
        .toDays().toInt()

internal fun LocalDate.isLicenceExpiring(daysUntilExpiry: Int) =
    this.getNumberOfDaysFromNow() < daysUntilExpiry + 1

internal fun LocalDate.isToday() =
    this.getNumberOfDaysFromNow() == 0
