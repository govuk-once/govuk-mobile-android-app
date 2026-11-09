package uk.gov.govuk.dvla.domain

import uk.gov.govuk.data.extension.toLocalDateOrNull
import java.time.LocalDate
import uk.gov.govuk.dvla.remote.model.Entitlement as RemoteEntitlement
import uk.gov.govuk.dvla.remote.model.common.EntitlementType as RemoteEntitlementType
import uk.gov.govuk.dvla.remote.model.Restriction as RemoteRestriction


data class Entitlement(
    val categoryCode: String,
    val categoryType: EntitlementType,
    val fromDate: LocalDate? = null,
    val expiryDate: LocalDate? = null,
    val restrictions: List<Restriction> = emptyList(),
    val categoryLegalLiteral: String = ""
)

enum class EntitlementType {
    FULL,
    PROVISIONAL,
    UNKNOWN
}

data class Restriction(
    val restrictionCode: String,
    val restrictionLiteral: String
)

internal fun RemoteEntitlement.toDomain(): Entitlement? =
    categoryCode?.let { code ->
        Entitlement(
            categoryCode = code,
            categoryType = categoryType.toDomain(),
            fromDate = fromDate.toLocalDateOrNull(),
            expiryDate = expiryDate.toLocalDateOrNull(),
            restrictions = restrictions.orEmpty().mapNotNull { it.toDomain() },
            categoryLegalLiteral = categoryLegalLiteral.orEmpty()
        )
    }

internal fun RemoteEntitlementType?.toDomain(): EntitlementType {
    return when (this) {
        RemoteEntitlementType.PROVISIONAL -> EntitlementType.PROVISIONAL
        RemoteEntitlementType.FULL -> EntitlementType.FULL
        else -> EntitlementType.UNKNOWN
    }
}

internal fun RemoteRestriction.toDomain(): Restriction? =
    restrictionCode?.let { code ->
        Restriction(restrictionCode = code, restrictionLiteral = restrictionLiteral.orEmpty())
    }

