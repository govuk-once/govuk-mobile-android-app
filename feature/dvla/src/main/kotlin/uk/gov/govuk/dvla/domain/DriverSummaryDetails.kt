package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.DriverSummaryResponse

// TODO: this is to demonstrate the endpoint call data, until we decide which endpoint to use
data class DriverSummaryDetails(
    val licenceNumber: String,
    val firstName: String,
    val lastName: String,
    val penaltyPoints: Int,
    val status: String,
    val expiryDate: String
)

fun DriverSummaryResponse.toDomainModel() = DriverSummaryDetails(
    licenceNumber = this.driverViewResponse.driver.drivingLicenceNumber,
    firstName = this.driverViewResponse.driver.firstNames,
    lastName = this.driverViewResponse.driver.lastName,
    penaltyPoints = this.driverViewResponse.driver.penaltyPoints,
    status = this.driverViewResponse.licence.status,
    expiryDate = this.driverViewResponse.token.validToDate
)