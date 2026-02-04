package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class Preferences(
    @SerializedName("notificationsConsented") val notificationsConsented: Boolean,
    @SerializedName("updatedAt") val updatedAt: String
)
