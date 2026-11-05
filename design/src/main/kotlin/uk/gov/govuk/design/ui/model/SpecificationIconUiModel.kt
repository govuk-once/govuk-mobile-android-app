package uk.gov.govuk.design.ui.model

import androidx.annotation.DrawableRes

data class SpecificationIconUiModel(
    @param:DrawableRes val icon: Int,
    val description: AccessibleString
)
