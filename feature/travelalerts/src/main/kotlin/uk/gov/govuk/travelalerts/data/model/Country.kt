package uk.gov.govuk.travelalerts.data.model

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class Country(
    @SerializedName("Name")
    val name: String,
    @SerializedName("Slug")
    val slug: String,
    @SerializedName("LastUpdated")
    val rawLastUpdated: String
) {
    val date: Instant
        get() = Instant.parse(rawLastUpdated)
}
