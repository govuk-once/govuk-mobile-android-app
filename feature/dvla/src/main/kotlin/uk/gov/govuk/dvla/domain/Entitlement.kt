package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.Entitlement as RemoteEntitlement
import uk.gov.govuk.dvla.remote.model.common.EntitlementType as RemoteEntitlementType


data class Entitlement(val categoryCode: String, val categoryType: EntitlementType)

enum class EntitlementType {
    FULL,
    PROVISIONAL,
    UNKNOWN
}

internal fun RemoteEntitlement.toDomain(): Entitlement? =
    categoryCode?.let { code ->
        Entitlement(
            categoryCode = code,
            categoryType = categoryType.toDomain()
        )
    }

internal fun RemoteEntitlementType?.toDomain(): EntitlementType {
    return when (this) {
        RemoteEntitlementType.PROVISIONAL -> EntitlementType.PROVISIONAL
        RemoteEntitlementType.FULL -> EntitlementType.FULL
        else -> EntitlementType.UNKNOWN
    }
}

