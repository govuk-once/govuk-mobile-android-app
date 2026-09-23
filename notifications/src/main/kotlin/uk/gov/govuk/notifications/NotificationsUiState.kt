package uk.gov.govuk.notifications

sealed class NotificationsUiState {
    data object Default : NotificationsUiState()
    data object Alert : NotificationsUiState()
}
