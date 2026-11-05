package uk.gov.govuk.search.data.remote.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("results") val results: List<SearchResult>
)
