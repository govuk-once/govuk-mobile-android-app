package uk.gov.govuk.dvla.util

fun String.toTitleCase(): String =
    this.lowercase()
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.titlecase() }
        }

/**
 * Formats string to be announced char by char by Talkback, for example 'FH08PDH' to 'F H 0 8 P D H'
 */
fun String.toSpacedString(): String {
    if (this.isBlank()) return this
    return this.toList().joinToString(separator = " ")
}

/**
 * Prevents Talkback from announcing addresses like "1 St John" as "First John".
 */
fun String.toAccessibleStreetName(): String {
    return this
        .replace("1 St ", "1, St ", ignoreCase = true)
        .replace("1 St. ", "1, St. ", ignoreCase = true)
}

internal fun String?.isDirectDebit() = this == "Direct Debit"

internal fun String.insertRegistration(registration: String): String {
    val registrationNoWhitespace = registration.filterNot { it.isWhitespace() }
    return this.replace("[NUMBER PLATE]", registrationNoWhitespace)
}