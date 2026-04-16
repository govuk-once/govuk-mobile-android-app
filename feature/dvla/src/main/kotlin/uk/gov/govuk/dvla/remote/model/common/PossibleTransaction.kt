package uk.gov.govuk.dvla.remote.model.common

import com.google.gson.annotations.SerializedName

data class PossibleTransaction(
    @SerializedName("transactionType") val transactionType: String,
    @SerializedName("isRequired") val isRequired: Boolean?
)