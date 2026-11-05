package uk.gov.govuk.dvla.remote.model.common

import com.google.gson.annotations.SerializedName

data class AvailableAction(
    @SerializedName("actionType") val actionType: String,
    @SerializedName("isRequired") val isRequired: Boolean?
)