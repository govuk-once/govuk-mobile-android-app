package uk.gov.govuk.dvla.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dvlaDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

fun LocalDate.toSummaryDisplayFormat(): String = this.format(dvlaDateFormatter)