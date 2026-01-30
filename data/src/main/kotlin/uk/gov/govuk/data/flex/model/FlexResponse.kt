package uk.gov.govuk.data.flex.model

import com.google.gson.annotations.SerializedName

data class FlexResponse(
    @SerializedName("notificationId") val notificationId: String
)
