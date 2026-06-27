package uk.gov.govuk.dvla.domain

import uk.gov.govuk.data.extension.toLocalDateOrNull
import uk.gov.govuk.dvla.remote.model.ExhaustEmissions
import uk.gov.govuk.dvla.remote.model.Vehicle
import uk.gov.govuk.dvla.remote.model.VehicleKeeper
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
    val sornStart: String?,

    // TODO: remove below when vehicle details endpoint is live
    val keeper: VehicleKeeper?,
    val dateOfFirstRegistration: LocalDate?,
    val fuelType: FuelType,
    val colour: VehicleColour,
    val secondaryColour: VehicleColour?,
    val engineCapacity: Int?,
    val exhaustEmissions: ExhaustEmissions?
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
        sornStart = this.sornStart,

        // TODO: remove below when vehicle details endpoint is live
        keeper = this.keeper,
        dateOfFirstRegistration = this.dateOfFirstRegistration.toLocalDateOrNull(),
        fuelType = this.fuelType.toDomain(),
        colour = this.colour.toDomain(),
        secondaryColour = this.secondaryColour.toDomain(),
        engineCapacity = this.engineCapacity,
        exhaustEmissions = this.exhaustEmissions
    )
}