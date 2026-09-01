package uk.gov.govuk.travelalerts.data.model

import com.google.gson.annotations.SerializedName

data class Group(
    @SerializedName("Namespace")
    val namespace: String,
    @SerializedName("Group")
    val group: String,
    @SerializedName("Subgroup")
    val subgroup: String,
)