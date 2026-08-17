package uk.gov.govuk.settings

/** State of the linked accounts screen. Error is event instead of a state */
sealed interface LinkedAccountsUiState {
    object Default : LinkedAccountsUiState
    object Unlinking : LinkedAccountsUiState
}