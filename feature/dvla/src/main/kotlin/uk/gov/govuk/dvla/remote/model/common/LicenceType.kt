package uk.gov.govuk.dvla.remote.model.common

import com.google.gson.annotations.SerializedName

enum class LicenceType {
    @SerializedName("Provisional")
    PROVISIONAL,

    @SerializedName("Full")
    FULL
}
