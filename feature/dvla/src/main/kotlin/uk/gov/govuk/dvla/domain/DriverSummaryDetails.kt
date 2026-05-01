package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.DriverSummaryResponse

// TODO: this is to demonstrate the endpoint call data, until we decide which endpoint to use
data class DriverSummaryDetails(
    val licenceNumber: String,
    val firstName: String,
    val lastName: String,
    val penaltyPoints: Int?,
    val status: String,
    val expiryDate: String?
)

fun DriverSummaryResponse.toDomainModel() = DriverSummaryDetails(
    licenceNumber = this.driverView.driver.drivingLicenceNumber,
    firstName = this.driverView.driver.firstNames ?: "",
    lastName = this.driverView.driver.lastName ?: "",
    penaltyPoints = this.driverView.driver.penaltyPoints,
    status = this.driverView.licence?.status ?: "Unknown",
    expiryDate = this.driverView.token?.validToDate
)