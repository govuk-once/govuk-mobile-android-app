package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class ConfigResponse(
    @SerializedName("config") val config: Config,
    @SerializedName("signature") val signature: String
)
