package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class DvlaUrls(
    @SerializedName("addVehicle") val addVehicle: String,
)
