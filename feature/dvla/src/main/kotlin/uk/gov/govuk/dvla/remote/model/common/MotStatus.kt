package uk.gov.govuk.dvla.remote.model.common

import com.google.gson.annotations.SerializedName

enum class MotStatus {
    @SerializedName("No details held by DVLA")
    NO_DETAILS_HELD,

    @SerializedName("No results returned")
    NO_RESULTS_RETURNED,

    @SerializedName("Not valid")
    NOT_VALID,

    @SerializedName("Valid")
    VALID
}