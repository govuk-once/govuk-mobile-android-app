package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class UpdateUserDataResponse(
    @SerializedName("preferences") val preferences: Preferences
)
