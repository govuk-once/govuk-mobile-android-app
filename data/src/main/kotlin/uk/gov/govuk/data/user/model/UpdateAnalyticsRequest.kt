package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class UpdateAnalyticsRequest(
    @SerializedName("analyticsConsented") val consented: Boolean
)
