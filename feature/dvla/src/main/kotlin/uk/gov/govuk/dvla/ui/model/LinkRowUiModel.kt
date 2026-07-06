package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.AccessibleString

internal data class LinkRowUiModel(
    val title: AccessibleString,
    val text: AccessibleString,
    val url: String
)
