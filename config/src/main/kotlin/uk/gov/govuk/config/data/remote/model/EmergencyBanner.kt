package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class EmergencyBanner(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
    @SerializedName("link") val link: Link?,
    @SerializedName("type") val type: EmergencyBannerType? = EmergencyBannerType.INFORMATION,
    @SerializedName("allowsDismissal") val allowsDismissal: Boolean,
    @SerializedName("dismissAltText") val dismissAltText: String? = null
)

enum class EmergencyBannerType {
    @SerializedName("notable-death")
    NOTABLE_DEATH,

    @SerializedName("national-emergency")
    NATIONAL_EMERGENCY,

    @SerializedName("local-emergency")
    LOCAL_EMERGENCY,

    @SerializedName("information")
    INFORMATION
}
