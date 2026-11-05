package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.common.LicenceType as RemoteLicenceType

enum class LicenceType {
    PROVISIONAL,
    FULL,
    UNKNOWN
}

internal fun RemoteLicenceType?.toDomain(): LicenceType {
    return when (this) {
        RemoteLicenceType.PROVISIONAL -> LicenceType.PROVISIONAL
        RemoteLicenceType.FULL -> LicenceType.FULL
        else -> LicenceType.UNKNOWN
    }
}
