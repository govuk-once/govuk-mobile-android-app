package uk.gov.govuk.dvla.domain

import uk.gov.govuk.data.extension.toLocalDateOrNull
import uk.gov.govuk.dvla.remote.model.Vehicle
import java.time.LocalDate

data class CustomerVehicle(
    val registration: String,
    val make: String,
    val model: String?,
    val taxStatus: TaxStatus,
    val taxExpiryDate: LocalDate?,
    val taxClass: String,
    val motStatus: MotStatus,
    val motExpiryDate: LocalDate?,
    val dateOfFirstRegistration: LocalDate?,
)

internal fun Vehicle.toCustomerVehicle(): CustomerVehicle {
    return CustomerVehicle(
        registration = this.registrationNumber,
        make = this.make,
        model = this.model,
        taxStatus = this.taxStatus.toDomain(),
        taxExpiryDate = this.taxedUntil.toLocalDateOrNull(),
        taxClass = this.taxClass,
        motStatus = this.motStatus.toDomain(),
        motExpiryDate = this.motExpiryDate.toLocalDateOrNull(),
        dateOfFirstRegistration = this.dateOfFirstRegistration.toLocalDateOrNull()
    )
}