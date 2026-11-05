package uk.gov.govuk.search.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import uk.gov.govuk.search.data.remote.model.SearchResponse
import uk.gov.govuk.search.domain.SearchConfig

interface SearchApi {
    @GET(SearchConfig.SEARCH_PATH)
    suspend fun getSearchResults(
        @Query("q") searchTerm: String,
        @Query("count") count: Int
    ): Response<SearchResponse>
}
