package uk.gov.govuk.dvla.remote.model

import com.google.gson.annotations.SerializedName

data class LinkStatusResponse(
    @SerializedName("linked") val linked: Boolean
)
