package uk.gov.govuk.dvla.domain

import uk.gov.govuk.data.extension.toLocalDateOrNull
import uk.gov.govuk.dvla.remote.model.CustomerVehicleDetails
import java.time.LocalDate

data class VehicleDetails(
    val summary: VehicleSummary,
    val dateOfFirstRegistration: LocalDate?,
    val fuelType: FuelType,
    val colour: VehicleColour,
    val secondaryColour: VehicleColour?,
    val engineCapacity: Int?,
    val exhaustEmissionsCo2: Int?,
    val keeperTitle: String?,
    val keeperFirstNames: String?,
    val keeperLastName: String?,
    val keeperFullAddress: String?
)

internal fun CustomerVehicleDetails.toDomainModel(): VehicleDetails {
    return VehicleDetails(
        summary = VehicleSummary(
            vehicleId = this.vehicleId,
            registration = this.registrationNumber,
            make = this.make,
            model = this.model,
            taxStatus = this.taxStatus.toDomain(),
            taxExpiryDate = this.taxedUntil.toLocalDateOrNull(),
            motStatus = this.motStatus.toDomain(),
            motExpiryDate = this.motExpiryDate.toLocalDateOrNull(),
            sornStart = this.sornStart.toLocalDateOrNull(),
            currentLicencePaymentMethod = this.currentLicencePaymentMethod
        ),
        dateOfFirstRegistration = this.dateOfFirstRegistration.toLocalDateOrNull(),
        fuelType = this.fuelType.toDomain(),
        colour = this.colour.toDomain(),
        secondaryColour = this.secondaryColour.toDomain(),
        engineCapacity = this.engineCapacity,
        exhaustEmissionsCo2 = this.exhaustEmissionsCo2,
        keeperTitle = this.keeperTitle,
        keeperFirstNames = this.keeperFirstNames,
        keeperLastName = this.keeperLastName,
        keeperFullAddress = this.keeperFullAddress
    )
}
