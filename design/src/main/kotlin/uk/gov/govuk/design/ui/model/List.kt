package uk.gov.govuk.design.ui.model

import androidx.annotation.DrawableRes

sealed interface ExternalLinkListItemStyle {
    data object Default : ExternalLinkListItemStyle
    data object Icon : ExternalLinkListItemStyle
    data class Button(
        @param:DrawableRes val icon: Int,
        val altText: String,
        val onClick: () -> Unit
    ) : ExternalLinkListItemStyle
}

sealed interface InternalLinkListItemStyle {
    data object Default : InternalLinkListItemStyle
    data class Status(
        val status: String
    ) : InternalLinkListItemStyle

    data class Info(
        val info: AccessibleString
    ) : InternalLinkListItemStyle

    data class Button(
        @param:DrawableRes val icon: Int,
        val altText: String,
        val onClick: () -> Unit
    ) : InternalLinkListItemStyle
}

sealed interface IconListItemStyle {
    data object Regular : IconListItemStyle
    data object Bold : IconListItemStyle
}

sealed interface StatusListItemIconStyle {
    data object Success : StatusListItemIconStyle
    data object Warning : StatusListItemIconStyle
}

sealed interface InternalLinkListItemModel {
    val title: AccessibleString

    data class Info(
        override val title: AccessibleString,
        val info: AccessibleString
    ) : InternalLinkListItemModel
}

data class ProgressBarListItemUiModel(
    val topText: AccessibleString,
    val percentage: Float,
    val bottomText: AccessibleString
)
