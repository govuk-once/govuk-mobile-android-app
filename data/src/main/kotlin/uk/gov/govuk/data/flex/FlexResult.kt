package uk.gov.govuk.data.flex

sealed class FlexResult<T> {
    data class Success<T>(val value: T) : FlexResult<T>()
    class Error<T> : FlexResult<T>()
}
