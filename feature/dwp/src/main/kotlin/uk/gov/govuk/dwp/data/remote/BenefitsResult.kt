package uk.gov.govuk.dwp.data.remote

sealed class BenefitsResult<T> {
    data class Success<T>(
        val value: T
    ) : BenefitsResult<T>()

    class NotFound<T> : BenefitsResult<T>()

    class DeviceOffline<T> : BenefitsResult<T>()

    class Error<T> : BenefitsResult<T>()
}
