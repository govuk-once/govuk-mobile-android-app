package uk.gov.govuk.design.ui.model

data class ProgressBarUiModel(
    val topText: AccessibleString,
    val percentage: Int,
    val bottomText: AccessibleString
)
