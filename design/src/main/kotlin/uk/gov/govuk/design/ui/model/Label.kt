package uk.gov.govuk.design.ui.model

data class SectionHeadingLabelButton(
    val title: String,
    val altText: String,
    val onClick: () -> Unit
)
