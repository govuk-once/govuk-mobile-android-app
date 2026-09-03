package uk.gov.govuk.travelalerts.data.model

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class Country(
    val name: String,
    val slug: String,
    @SerializedName("lastUpdate")
    val rawLastUpdated: String,
    val synonyms: List<String>
) {
    val date: Instant
        get() = Instant.parse(rawLastUpdated)
}
