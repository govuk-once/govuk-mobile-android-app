package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class UserApiResponse(
    @SerializedName("notificationId") val notificationId: String
)
