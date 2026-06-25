package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.AccessibleString

data class OverflowMenuItem(
    val text: AccessibleString,
    val action: MenuAction
)

sealed interface MenuAction {
    data class WebLink(val url: String) : MenuAction
    data class ClipboardCopy(val textToCopy: String) : MenuAction
}
