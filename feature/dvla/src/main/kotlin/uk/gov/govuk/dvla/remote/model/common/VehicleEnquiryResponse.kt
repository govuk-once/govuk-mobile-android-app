package uk.gov.govuk.dvla.remote.model.common

import com.google.gson.annotations.SerializedName

data class VehicleEnquiryResponse(
    @SerializedName("vehicle")
    val vehicle: Vehicle
) {
    data class Vehicle(
        @SerializedName("vehicleId") val vehicleId: Int,
        @SerializedName("registrationNumber") val registrationNumber: String?,
        @SerializedName("taxStatus") val taxStatus: String?,
        @SerializedName("taxedUntil") val taxedUntil: String?,
        @SerializedName("motStatus") val motStatus: String?,
        @SerializedName("motExpiryDate") val motExpiryDate: String?,
        @SerializedName("make") val make: String?,
        @SerializedName("dateOfFirstRegistration") val dateOfFirstRegistration: String?,
        @SerializedName("engineCapacity") val engineCapacity: Int?,
        @SerializedName("exhaustEmissionsCo2") val exhaustEmissionsCo2: Int?,
        @SerializedName("fuelType") val fuelType: String?,
        @SerializedName("colour") val colour: String?,
        @SerializedName("secondaryColour") val secondaryColour: String?
    )
}
