package uk.gov.govuk.dvla.ui.model

import androidx.annotation.DrawableRes

data class StatusRowUiModel(
    val title: String,
    val description: String,
    @param:DrawableRes val icon: Int
)
