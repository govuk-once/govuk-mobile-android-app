package uk.govuk.app.local.data.remote.model

import com.google.gson.annotations.SerializedName

data class RemoteAddress(
    @SerializedName("address") val address: String,
    @SerializedName("local_authority_slug") val slug: String,
    @SerializedName("local_authority_name") val name: String
)
