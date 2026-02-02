package uk.gov.govuk.data.user

sealed class UserApiResult<T> {
    data class Success<T>(val value: T) : UserApiResult<T>()
    class Error<T> : UserApiResult<T>()
}
