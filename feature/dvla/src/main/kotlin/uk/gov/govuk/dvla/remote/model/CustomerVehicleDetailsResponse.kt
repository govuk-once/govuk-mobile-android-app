package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.dvla.remote.model.common.FuelType
import uk.gov.govuk.dvla.remote.model.common.MotStatus
import uk.gov.govuk.dvla.remote.model.common.TaxStatus
import uk.gov.govuk.dvla.remote.model.common.VehicleColour

data class CustomerVehicleDetailsResponse(
    @SerializedName("customerVehicleDetails") val customerVehicleDetails: CustomerVehicleDetails
)

data class CustomerVehicleDetails(
    @SerializedName("vehicleId") val vehicleId: Int,
    @SerializedName("registrationNumber") val registrationNumber: String,
    @SerializedName("make") val make: String,
    @SerializedName("model") val model: String?,
    @SerializedName("taxStatus") val taxStatus: TaxStatus?,
    @SerializedName("sornStart") val sornStart: String?,
    @SerializedName("taxedUntil") val taxedUntil: String?,
    @SerializedName("currentLicencePaymentMethod") val currentLicencePaymentMethod: String?,
    @SerializedName("motStatus") val motStatus: MotStatus,
    @SerializedName("motExpiryDate") val motExpiryDate: String?,
    @SerializedName("dateOfFirstRegistration") val dateOfFirstRegistration: String?,
    @SerializedName("fuelType") val fuelType: FuelType?,
    @SerializedName("colour") val colour: VehicleColour?,
    @SerializedName("secondaryColour") val secondaryColour: VehicleColour?,
    @SerializedName("engineCapacity") val engineCapacity: Int?,
    @SerializedName("exhaustEmissionsCo2") val exhaustEmissionsCo2: Int?,
    @SerializedName("keeperTitle") val keeperTitle: String?,
    @SerializedName("keeperFirstNames") val keeperFirstNames: String?,
    @SerializedName("keeperLastName") val keeperLastName: String?,
    @SerializedName("keeperFullAddress") val keeperFullAddress: String?
)
