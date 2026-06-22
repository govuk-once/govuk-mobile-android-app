package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle

data class StatusRowUiModel(
    val title: AccessibleString? = null,
    val description: AccessibleString,
    val iconStyle: StatusListItemIconStyle?,
    val action: StatusActionUiModel? = null
)


