package uk.gov.govuk.design.ui.model

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
