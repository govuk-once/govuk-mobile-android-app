package uk.gov.govuk.travelalerts.data.model

import com.google.gson.annotations.SerializedName

data class SubscriptionRequest(
    @SerializedName("Namespace") val namespace: String,
    @SerializedName("Group") val group: String,
    @SerializedName("Subgroup") val subgroup: Subgroup,
    @SerializedName("Type") val type: String = "NOTIFICATION",
    @SerializedName("Action") val action: String = "JOIN"
)
