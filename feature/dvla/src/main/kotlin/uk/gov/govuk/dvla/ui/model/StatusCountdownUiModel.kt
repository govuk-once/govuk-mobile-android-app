package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.AccessibleString

internal data class StatusCountdownUiModel(
    val topText: AccessibleString,
    val percentage: Float,
    val bottomText: AccessibleString,
    val title: AccessibleString? = null,
    val style: StatusStyle? = null
)
