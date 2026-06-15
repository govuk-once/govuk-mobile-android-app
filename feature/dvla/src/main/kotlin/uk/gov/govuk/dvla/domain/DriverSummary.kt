package uk.gov.govuk.dvla.domain

import uk.gov.govuk.data.extension.toLocalDateOrNull
import uk.gov.govuk.dvla.remote.model.DriverSummaryResponse
import java.time.LocalDate

import uk.gov.govuk.dvla.remote.model.LicenceStatus as RemoteLicenceStatus
import uk.gov.govuk.dvla.remote.model.LicenceType as RemoteLicenceType

data class DriverSummary(
    val licenceType: LicenceType,
    val licenceNumber: String,
    val title: String,
    val firstNames: String,
    val lastName: String,
    val addressLine1: String,
    val addressLine5: String,
    val postcode: String,
    val status: LicenceStatus,
    val expiryDate: LocalDate?
) {
    val fullName: String
        get() = listOf(title, firstNames, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}

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

enum class LicenceType {
    PROVISIONAL,
    FULL,
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

internal fun RemoteLicenceType?.toDomain(): LicenceType {
    if (this == null) return LicenceType.UNKNOWN

    return when (this) {
        RemoteLicenceType.PROVISIONAL -> LicenceType.PROVISIONAL
        RemoteLicenceType.FULL -> LicenceType.FULL
    }
}

fun DriverSummaryResponse.toDomainModel(): DriverSummary {
    val driver = this.driverView.driver
    val address = driver.address?.unstructuredAddress
    val licence = this.driverView.licence
    val token = this.driverView.token

    return DriverSummary(
        licenceType = licence?.type.toDomain(),
        licenceNumber = driver.drivingLicenceNumber,
        title = driver.title ?: "",
        firstNames = driver.firstNames ?: "",
        lastName = driver.lastName ?: "",
        addressLine1 = address?.line1 ?: "",
        addressLine5 = address?.line5 ?: "",
        postcode = address?.postcode ?: "",
        status = licence?.status.toDomain(),
        expiryDate = token?.validToDate?.toLocalDateOrNull()
    )
}