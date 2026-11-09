package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class Notifications (
    @SerializedName("consentStatus") val consentStatus: ConsentStatus,
    @SerializedName("pushId") val pushId: String
)
