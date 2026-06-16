package uk.gov.govuk.dvla.domain

import uk.gov.govuk.data.extension.toLocalDateOrNull
import uk.gov.govuk.dvla.remote.model.VehicleEnquiryResponse
import java.time.LocalDate

// TODO: this is to demonstrate the endpoint call data, to be decided which data to use

/** Representation of vehicle data returned by the Vehicle Enquiry Service (VES). */
data class VesVehicle(
    val registrationNumber: String,
    val make: String,
    val model: String,
    val colour: VehicleColour,
    val yearOfManufacture: Int?,
    val dateOfFirstRegistration: LocalDate?,
    override val taxStatus: TaxStatus,
    val taxDueDate: LocalDate?,
    override val taxExpiryDate: LocalDate?,
    override val taxClass: String,
    override val motStatus: MotStatus,
    override val motExpiryDate: LocalDate?,
    val fuelType: FuelType,
    val engineCapacity: Int?,
    val co2Emissions: Int?
): VehicleSummary

fun VehicleEnquiryResponse.toDomainModel() = VesVehicle(
    registrationNumber = this.registrationNumber,
    make = this.make ?: "",
    model = this.model ?: "",
    colour = this.colour.toDomain() ?: VehicleColour.NOT_STATED,
    yearOfManufacture = this.yearOfManufacture,
    dateOfFirstRegistration = this.monthOfFirstRegistration.toLocalDateOrNull(),
    taxStatus = this.taxStatus?.toDomain() ?: TaxStatus.UNKNOWN,
    taxDueDate = this.taxDueDate.toLocalDateOrNull(),
    taxExpiryDate = this.taxDueDate.toLocalDateOrNull(),
    taxClass = this.taxClass,
    motStatus = this.motStatus?.toDomain() ?: MotStatus.UNKNOWN,
    motExpiryDate = this.motExpiryDate.toLocalDateOrNull(),
    fuelType = this.fuelType.toDomain(),
    engineCapacity = this.engineCapacity,
    co2Emissions = this.co2Emissions
)
