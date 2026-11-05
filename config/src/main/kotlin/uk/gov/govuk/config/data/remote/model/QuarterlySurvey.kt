package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class QuarterlySurvey(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String
)
