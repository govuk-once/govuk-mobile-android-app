package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.StatusListItemIconStyle

data class StatusRowUiModel(
    val title: String? = null,
    val titleAltText: String? = null,
    val description: String,
    val iconStyle: StatusListItemIconStyle?
)
