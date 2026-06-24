package uk.gov.govuk.dvla.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dayMonthYearFormat = DateTimeFormatter.ofPattern("d MMMM yyyy")
private val yearFormat = DateTimeFormatter.ofPattern("yyyy")

internal fun LocalDate.toSummaryDisplayFormat(): String = this.format(dayMonthYearFormat)

internal fun LocalDate.toYearDisplayFormat(): String = this.format(yearFormat)
