package uk.gov.govuk.data.model

sealed class Result<T> {
    data class Success<T>(val value: T): Result<T>()
    class DeviceOffline<T>: Result<T>()
    class ServiceNotResponding<T>: Result<T>()
    class InvalidSignature<T>: Result<T>()
    class Error<T>: Result<T>()
}
