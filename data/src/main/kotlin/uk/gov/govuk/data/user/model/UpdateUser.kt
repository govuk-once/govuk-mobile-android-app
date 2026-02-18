package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class UpdateUser(
    @SerializedName("preferences") val preferences: Preferences
)
