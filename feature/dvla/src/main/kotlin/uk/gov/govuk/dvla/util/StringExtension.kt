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
