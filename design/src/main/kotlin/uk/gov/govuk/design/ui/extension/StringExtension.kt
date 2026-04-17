package uk.gov.govuk.design.ui.extension

/**
 * Replaces an acronym only if it is a word (preventing for example replacing 'mot' in 'remote').
 * Used for accessibility text to announce acronyms.
 */
internal fun String.replaceAcronym(acronym: String, replacement: String): String =
     this.replace("\\b$acronym\\b".toRegex(), replacement)
