package uk.gov.govuk.tour

import androidx.annotation.DrawableRes

data class TourStep(
    val targetKey: String,
    val title: String,
    val body: String,
    @DrawableRes val illustrationRes: Int? = null
)
