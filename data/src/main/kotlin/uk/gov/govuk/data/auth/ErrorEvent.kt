package uk.gov.govuk.data.auth

sealed class ErrorEvent {
    data object UnableToSignInError: ErrorEvent()
    data object UnableToSignOutError: ErrorEvent()
    data object UserApiError: ErrorEvent()
}
