package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class GetUserInfoResponse(
    @SerializedName("notificationId") val notificationId: String
)
