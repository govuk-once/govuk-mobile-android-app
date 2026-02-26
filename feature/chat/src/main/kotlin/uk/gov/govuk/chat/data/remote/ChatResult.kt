package uk.gov.govuk.chat.data.remote

sealed class ChatResult<T> {
    data class Success<T>(val value: T): ChatResult<T>()
    class AwaitingAnswer<T>: ChatResult<T>()
    class AuthError<T>: ChatResult<T>()
    class NotFound<T>: ChatResult<T>()
    class ValidationError<T>: ChatResult<T>()
    class RateLimitExceeded<T>: ChatResult<T>()
    class DeviceOffline<T>: ChatResult<T>()
    class Error<T>: ChatResult<T>()
}