package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("notifications") val notifications: Notifications
)
