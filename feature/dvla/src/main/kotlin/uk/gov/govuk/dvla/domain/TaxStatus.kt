package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.common.TaxStatus as RemoteTaxStatus


enum class TaxStatus {
    NOT_TAXED_FOR_ON_ROAD_USE,
    SORN,
    TAXED,
    UNTAXED,
    UNKNOWN
}

internal fun RemoteTaxStatus?.toDomain(): TaxStatus {
    if (this == null) return TaxStatus.UNKNOWN

    return when (this) {
        RemoteTaxStatus.TAXED -> TaxStatus.TAXED
        RemoteTaxStatus.UNTAXED -> TaxStatus.UNTAXED
        RemoteTaxStatus.NOT_TAXED_FOR_ON_ROAD_USE -> TaxStatus.NOT_TAXED_FOR_ON_ROAD_USE
        RemoteTaxStatus.SORN -> TaxStatus.SORN
    }
}