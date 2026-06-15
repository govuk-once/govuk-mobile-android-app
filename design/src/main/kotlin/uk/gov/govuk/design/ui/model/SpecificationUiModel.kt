package uk.gov.govuk.design.ui.model

import androidx.annotation.DrawableRes

data class SpecificationUiModel(
    @param:DrawableRes val icon: Int?,
    val description: String,
    val altText: String
)
