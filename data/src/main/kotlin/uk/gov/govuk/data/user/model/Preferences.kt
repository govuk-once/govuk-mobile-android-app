package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class Preferences(
    @SerializedName("notifications") val notifications: Notifications
)
