package uk.gov.govuk.settings

sealed interface LinkedAccountsUiState {
    object Default : LinkedAccountsUiState
    object Unlinking : LinkedAccountsUiState
    object Error : LinkedAccountsUiState
}