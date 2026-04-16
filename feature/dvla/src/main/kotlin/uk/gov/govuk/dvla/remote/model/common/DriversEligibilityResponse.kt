package uk.gov.govuk.dvla.remote.model.common

import com.google.gson.annotations.SerializedName

data class DriversEligibilityResponse(
    @SerializedName("applications") val applications: List<Application>
)