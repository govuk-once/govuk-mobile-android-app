package uk.gov.govuk.search.data.remote.model

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.search.domain.SearchConfig.DESCRIPTION_RESPONSE_FIELD

data class SearchResult(
    @SerializedName("content_id") val contentId: String,
    @SerializedName("title") val title: String,
    @SerializedName(DESCRIPTION_RESPONSE_FIELD) val description: String?,
    @SerializedName("link") val link: String
)
