package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.dvla.remote.model.common.MotStatus
import uk.gov.govuk.dvla.remote.model.common.TaxStatus

data class CustomerVehiclesResponse(
    @SerializedName("customerVehicles") val customerVehicles: List<VehicleSummary>
)

data class VehicleSummary(
    @SerializedName("vehicleId") val vehicleId: Int,
    @SerializedName("registrationNumber") val registrationNumber: String,
    @SerializedName("make") val make: String,
    @SerializedName("model") val model: String?,
    @SerializedName("taxStatus") val taxStatus: TaxStatus?,
    @SerializedName("dateOfLiability") val dateOfLiability: String?,
    @SerializedName("sornStart") val sornStart: String?,
    @SerializedName("taxedUntil") val taxedUntil: String?,
    @SerializedName("currentLicencePaymentMethod") val currentLicencePaymentMethod: String?,
    @SerializedName("motStatus") val motStatus: MotStatus,
    @SerializedName("motExpiryDate") val motExpiryDate: String?
)
