package uk.gov.govuk.dvla.domain

import uk.gov.govuk.data.extension.toLocalDateOrNull
import uk.gov.govuk.dvla.remote.model.VehicleEnquiryResponse
import java.time.LocalDate

data class VehicleEnquiryDetails(
    val vehicleId: Int,
    val registration: String?,
    val make: String?,
    val taxStatus: TaxStatus,
    val taxExpiryDate: LocalDate?,
    val motStatus: MotStatus,
    val motExpiryDate: LocalDate?,
    val dateOfFirstRegistration: LocalDate?,
    val fuelType: FuelType,
    val colour: VehicleColour,
    val secondaryColour: VehicleColour?,
    val engineCapacity: Int?,
    val exhaustEmissionsCo2: Int?
)

internal fun VehicleEnquiryResponse.toDomainModel(): VehicleEnquiryDetails {
    return VehicleEnquiryDetails(
        vehicleId = vehicle.vehicleId,
        registration = vehicle.registrationNumber,
        make = vehicle.make,
        taxStatus = vehicle.taxStatus.toDomain(),
        taxExpiryDate = vehicle.taxedUntil.toLocalDateOrNull(),
        motStatus = vehicle.motStatus.toDomain(),
        motExpiryDate = vehicle.motExpiryDate.toLocalDateOrNull(),
        dateOfFirstRegistration = vehicle.dateOfFirstRegistration.toLocalDateOrNull(),
        fuelType = vehicle.fuelType.toDomain(),
        colour = vehicle.colour.toDomain(),
        secondaryColour = vehicle.secondaryColour.toDomain(),
        engineCapacity = vehicle.engineCapacity,
        exhaustEmissionsCo2 = vehicle.exhaustEmissionsCo2
    )
}
