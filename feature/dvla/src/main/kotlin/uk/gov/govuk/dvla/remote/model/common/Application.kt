package uk.gov.govuk.dvla.remote.model.common

import com.google.gson.annotations.SerializedName

data class Application(
    @SerializedName("applicationType") val applicationType: String,
    @SerializedName("isRequired") val isRequired: Boolean?,
    @SerializedName("ineligibleReason") val ineligibleReason: String?,
    @SerializedName("availableActions") val availableActions: List<AvailableAction>,
    @SerializedName("possibleTransactions") val possibleTransactions: List<PossibleTransaction>?
)
