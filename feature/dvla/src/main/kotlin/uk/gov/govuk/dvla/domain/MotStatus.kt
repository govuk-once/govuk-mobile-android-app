package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.common.MotStatus as RemoteMotStatus


enum class MotStatus {
    VALID,
    EXPIRED,
    UNKNOWN
}

internal fun RemoteMotStatus?.toDomain(): MotStatus {
    if (this == null) return MotStatus.UNKNOWN

    return when (this) {
        RemoteMotStatus.VALID -> MotStatus.VALID
        RemoteMotStatus.NOT_VALID -> MotStatus.EXPIRED
        else -> MotStatus.UNKNOWN
    }
}