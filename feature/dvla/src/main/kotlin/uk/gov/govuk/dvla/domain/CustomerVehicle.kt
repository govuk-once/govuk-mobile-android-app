package uk.gov.govuk.dvla.domain

import uk.gov.govuk.dvla.remote.model.Vehicle

data class CustomerVehicle(
    val registration: String,
    val make: String,
    val model: String,
    val taxStatus: TaxStatus,
    val taxExpiryDate: String?,
    val taxClass: String,
    val motStatus: MotStatus,
    val motExpiryDate: String?
)

internal fun Vehicle.toCustomerVehicle(): CustomerVehicle {
    return CustomerVehicle(
        registration = this.registrationNumber,
        make = this.make,
        model = this.model,
        taxStatus = this.taxStatus.toDomain(),
        taxExpiryDate = this.taxedUntil,
        taxClass = this.taxClass,
        motStatus = this.motStatus.toDomain(),
        motExpiryDate = this.motExpiryDate
    )
}