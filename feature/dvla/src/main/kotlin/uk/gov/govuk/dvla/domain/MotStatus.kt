package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.common.MotStatus as RemoteMotStatus


enum class MotStatus {
    VALID,
    EXPIRED,
    NO_DETAILS_HELD,
    NO_RESULTS_RETURNED,
    UNKNOWN
}

internal fun RemoteMotStatus?.toDomain(): MotStatus {
    if (this == null) return MotStatus.UNKNOWN

    return when (this) {
        RemoteMotStatus.VALID -> MotStatus.VALID
        RemoteMotStatus.NOT_VALID -> MotStatus.EXPIRED
        RemoteMotStatus.NO_DETAILS_HELD-> MotStatus.NO_DETAILS_HELD
        RemoteMotStatus.NO_RESULTS_RETURNED -> MotStatus.NO_RESULTS_RETURNED
    }
}