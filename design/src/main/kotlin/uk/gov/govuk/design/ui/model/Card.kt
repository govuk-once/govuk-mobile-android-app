package uk.gov.govuk.design.ui.model

import androidx.compose.ui.graphics.Color

data class CardListItem(
    val title: String,
    val onClick: () -> Unit
)

sealed interface FocusableCardColours {
    interface Focussed : FocusableCardColours {
        data object Background : Focussed
        data object Content : Focussed
    }

    interface UnFocussed : FocusableCardColours {
        data object Background : UnFocussed
        data object Content : UnFocussed
    }
}

internal data class DrillInCardColours(
    val background: Color,
    val title: Color,
    val description: Color,
    val icon: Color,
    val stroke: Color
)
