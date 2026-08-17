package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle

internal data class StatusRowUiModel(
    val title: AccessibleString? = null,
    val description: AccessibleString,
    val iconStyle: StatusListItemIconStyle?,
    val style: StatusStyle? = null
)
