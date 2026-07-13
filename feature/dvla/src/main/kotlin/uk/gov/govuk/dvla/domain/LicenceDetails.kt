package uk.gov.govuk.dvla.domain

import uk.gov.govuk.data.extension.toLocalDateOrNull
import uk.gov.govuk.dvla.remote.model.LicenceResponse
import java.time.LocalDate

data class LicenceDetails(
    val licenceType: LicenceType,
    val drivingLicenceNumber: String,
    val driverTitle: String,
    val driverFirstNames: String,
    val driverLastName: String,
    val driverFullAddress: String,
    val tokenValidToDate: LocalDate?,
    val licenceStatus: LicenceStatus
) {
    val fullName: String
        get() = listOf(driverTitle, driverFirstNames, driverLastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}

fun LicenceResponse.toDomainModel() = LicenceDetails(
    licenceType = this.drivingLicence.licenceType.toDomain(),
    drivingLicenceNumber = this.drivingLicence.drivingLicenceNumber,
    driverTitle = this.drivingLicence.driverTitle ?: "",
    driverFirstNames = this.drivingLicence.driverFirstNames ?: "",
    driverLastName = this.drivingLicence.driverLastName ?: "",
    driverFullAddress = this.drivingLicence.driverFullAddress ?: "",
    tokenValidToDate = this.drivingLicence.tokenValidToDate.toLocalDateOrNull(),
    licenceStatus = this.drivingLicence.licenceStatus.toDomain()
)
