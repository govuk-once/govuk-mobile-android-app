package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class DvlaUrls(
    @SerializedName("addVehicle") val addVehicle: String,
    @SerializedName("renewLicence") val renewLicence: String,
    @SerializedName("manageTaxPayment") val manageTaxPayment: String,
    @SerializedName("taxVehicle") val taxVehicle: String
)
