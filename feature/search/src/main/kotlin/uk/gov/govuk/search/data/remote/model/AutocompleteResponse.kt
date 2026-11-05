package uk.gov.govuk.search.data.remote.model

import com.google.gson.annotations.SerializedName

data class AutocompleteResponse(
    @SerializedName("suggestions") val suggestions: List<String>
)
