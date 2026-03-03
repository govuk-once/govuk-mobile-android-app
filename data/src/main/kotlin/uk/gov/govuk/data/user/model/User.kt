package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("notificationId") val notificationId: String,
    @SerializedName("preferences") val preferences: Preferences
)
