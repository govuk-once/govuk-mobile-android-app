package uk.govuk.app.local.data.remote.model

import com.google.gson.annotations.SerializedName

data class RemoteLocalAuthority(
    @SerializedName("name") val name: String,
    @SerializedName("homepage_url") val homePageUrl: String,
    @SerializedName("tier") val tier: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("parent") val parent: RemoteLocalAuthority? = null
)
