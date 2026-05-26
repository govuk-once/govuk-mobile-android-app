package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.LicenceResponse

data class LicenceDetails(
    val licenceNumber: String,
    val validFrom: String,
    val validTo: String,
    val type: String,
    val status: String
)

fun LicenceResponse.toDomainModel() = LicenceDetails(
    licenceNumber = this.driver.drivingLicenceNumber,
    validFrom = this.token.validFromDate,
    validTo = this.token.validToDate,
    type = this.licence.type,
    status = this.licence.status
)