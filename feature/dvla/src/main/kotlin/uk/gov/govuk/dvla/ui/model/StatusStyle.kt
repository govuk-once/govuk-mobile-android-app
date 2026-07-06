package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.AccessibleString

internal sealed interface StatusStyle {
    data class ActionButton(
        val text: AccessibleString,
        val url: String,
        val caption: AccessibleString? = null,
        val isPrimary: Boolean = true
    ) : StatusStyle

    data class Caption(
        val text: AccessibleString,
    ) : StatusStyle
}
