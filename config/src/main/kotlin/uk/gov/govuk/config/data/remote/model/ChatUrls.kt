package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class ChatUrls(
    @SerializedName("termsAndConditions") val termsAndConditions: String,
    @SerializedName("privacyNotice") val privacyNotice: String,
    @SerializedName("about") val about: String,
    @SerializedName("feedback") val feedback: String
)
