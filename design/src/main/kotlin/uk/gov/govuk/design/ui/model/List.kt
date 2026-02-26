package uk.gov.govuk.design.ui.model

import androidx.annotation.DrawableRes

sealed interface ExternalLinkListItemStyle {
    data object Default : ExternalLinkListItemStyle
    data object Icon : ExternalLinkListItemStyle
    data class Button(
        @DrawableRes val icon: Int,
        val altText: String,
        val onClick: () -> Unit
    ) : ExternalLinkListItemStyle
}

sealed interface InternalLinkListItemStyle {
    data object Default : InternalLinkListItemStyle
    data class Status(
        val title: String
    ) : InternalLinkListItemStyle
}

sealed interface IconListItemStyle {
    data object Regular : IconListItemStyle
    data object Bold : IconListItemStyle
}
