package uk.gov.govuk.topics.ui.model

import androidx.annotation.DrawableRes

internal data class TopicItemUi(
    val ref: String,
    @DrawableRes val icon: Int,
    val title: String,
    val description: String,
    val isSelected: Boolean
)
