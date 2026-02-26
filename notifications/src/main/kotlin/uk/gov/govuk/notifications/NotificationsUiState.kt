package uk.gov.govuk.notifications

internal sealed class NotificationsUiState {
    internal data object Default : NotificationsUiState()
    internal data object Alert : NotificationsUiState()
}
