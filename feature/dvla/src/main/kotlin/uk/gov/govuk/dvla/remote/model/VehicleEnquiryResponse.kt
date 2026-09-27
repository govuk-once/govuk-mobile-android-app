package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.dvla.remote.model.common.FuelType
import uk.gov.govuk.dvla.remote.model.common.MotStatus
import uk.gov.govuk.dvla.remote.model.common.TaxStatus
import uk.gov.govuk.dvla.remote.model.common.VehicleColour

data class VehicleEnquiryResponse(
    @SerializedName("vehicle")
    val vehicle: Vehicle
) {
    data class Vehicle(
        @SerializedName("vehicleId") val vehicleId: Int,
        @SerializedName("registrationNumber") val registrationNumber: String?,
        @SerializedName("taxStatus") val taxStatus: TaxStatus?,
        @SerializedName("taxedUntil") val taxedUntil: String?,
        @SerializedName("motStatus") val motStatus: MotStatus?,
        @SerializedName("motExpiryDate") val motExpiryDate: String?,
        @SerializedName("make") val make: String?,
        @SerializedName("dateOfFirstRegistration") val dateOfFirstRegistration: String?,
        @SerializedName("engineCapacity") val engineCapacity: Int?,
        @SerializedName("exhaustEmissionsCo2") val exhaustEmissionsCo2: Int?,
        @SerializedName("fuelType") val fuelType: FuelType?,
        @SerializedName("colour") val colour: VehicleColour?,
        @SerializedName("secondaryColour") val secondaryColour: VehicleColour?
    )
}
