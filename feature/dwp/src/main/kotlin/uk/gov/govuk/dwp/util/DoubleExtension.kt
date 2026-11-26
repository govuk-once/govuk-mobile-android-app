package uk.gov.govuk.dwp.util

import java.text.DecimalFormat

fun Double.convert(): String {
    val format = DecimalFormat("#,##0.00")
    format.isDecimalSeparatorAlwaysShown = false
    return format.format(this).toString()
}
