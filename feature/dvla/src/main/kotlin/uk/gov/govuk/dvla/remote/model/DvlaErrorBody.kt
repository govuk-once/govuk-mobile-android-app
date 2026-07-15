package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName

internal data class DvlaErrorBody(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?
)
