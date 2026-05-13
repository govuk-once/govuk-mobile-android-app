package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.VehicleEnquiryResponse

// TODO: this is to demonstrate the endpoint call data, to be decided which data to use
data class VehicleDetails(
    val registrationNumber: String,
    val make: String,
    val colour: String,
    val yearOfManufacture: Int?,
    val taxStatus: TaxStatus,
    val taxDueDate: String?,
    val motStatus: MotStatus,
    val motExpiryDate: String?,
    val fuelType: String,
    val engineCapacity: Int?,
    val co2Emissions: Int?
)

fun VehicleEnquiryResponse.toDomainModel() = VehicleDetails(
    registrationNumber = this.registrationNumber,
    make = this.make ?: "",
    colour = this.colour ?: "",
    yearOfManufacture = this.yearOfManufacture,
    taxStatus = this.taxStatus?.toDomain() ?: TaxStatus.UNKNOWN,
    taxDueDate = this.taxDueDate,
    motStatus = this.motStatus?.toDomain() ?: MotStatus.UNKNOWN,
    motExpiryDate = this.motExpiryDate,
    fuelType = this.fuelType ?: "",
    engineCapacity = this.engineCapacity,
    co2Emissions = this.co2Emissions
)

