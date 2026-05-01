package uk.gov.govuk.data.model

sealed class Result<T> {
    data class Success<T>(val value: T): Result<T>()
    class DeviceOffline<T>: Result<T>()
    class ServiceNotResponding<T>: Result<T>()
    class AuthError<T>: Result<T>()
    class InvalidSignature<T>: Result<T>()
    class Error<T>: Result<T>()
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(this.value))
        else -> {
            @Suppress("UNCHECKED_CAST")
            this as Result<R>
        }
    }
}
