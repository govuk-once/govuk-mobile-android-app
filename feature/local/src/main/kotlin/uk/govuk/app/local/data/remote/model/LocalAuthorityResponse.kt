package uk.govuk.app.local.data.remote.model

import com.google.gson.annotations.SerializedName

data class LocalAuthorityResponse(
    @SerializedName("local_authority") val localAuthority: RemoteLocalAuthority?,
    @SerializedName("addresses") val addresses: List<RemoteAddress>?,
)
