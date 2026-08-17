package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.common.LicenceStatus as RemoteLicenceStatus

enum class LicenceStatus {
    VALID,
    DISQUALIFIED,
    REVOKED,
    REVOKED_FOR_MEDICAL_REASONS,
    SURRENDERED,
    SURRENDERED_VOLUNTARILY,
    SURRENDERED_FOR_MEDICAL_REASONS,
    EXPIRED,
    EXCHANGED,
    REFUSED,
    REFUSED_FOR_MEDICAL_REASONS,
    UNKNOWN
}

internal fun RemoteLicenceStatus?.toDomain(): LicenceStatus {
    if (this == null) return LicenceStatus.UNKNOWN

    return when (this) {
        RemoteLicenceStatus.VALID -> LicenceStatus.VALID
        RemoteLicenceStatus.DISQUALIFIED -> LicenceStatus.DISQUALIFIED
        RemoteLicenceStatus.REVOKED -> LicenceStatus.REVOKED
        RemoteLicenceStatus.REVOKED_FOR_MEDICAL_REASONS -> LicenceStatus.REVOKED_FOR_MEDICAL_REASONS
        RemoteLicenceStatus.SURRENDERED -> LicenceStatus.SURRENDERED
        RemoteLicenceStatus.SURRENDERED_VOLUNTARILY -> LicenceStatus.SURRENDERED_VOLUNTARILY
        RemoteLicenceStatus.SURRENDERED_FOR_MEDICAL_REASONS -> LicenceStatus.SURRENDERED_FOR_MEDICAL_REASONS
        RemoteLicenceStatus.EXPIRED -> LicenceStatus.EXPIRED
        RemoteLicenceStatus.EXCHANGED -> LicenceStatus.EXCHANGED
        RemoteLicenceStatus.REFUSED -> LicenceStatus.REFUSED
        RemoteLicenceStatus.REFUSED_FOR_MEDICAL_REASONS -> LicenceStatus.REFUSED_FOR_MEDICAL_REASONS
    }
}
