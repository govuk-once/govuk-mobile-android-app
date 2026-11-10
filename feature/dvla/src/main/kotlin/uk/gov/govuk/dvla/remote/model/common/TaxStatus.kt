package uk.gov.govuk.dvla.remote.model.common

import com.google.gson.annotations.SerializedName

enum class TaxStatus {
    @SerializedName("Not Taxed for on Road Use")
    NOT_TAXED_FOR_ON_ROAD_USE,

    @SerializedName("SORN")
    SORN,

    @SerializedName("Taxed")
    TAXED,

    @SerializedName("Untaxed")
    UNTAXED
}