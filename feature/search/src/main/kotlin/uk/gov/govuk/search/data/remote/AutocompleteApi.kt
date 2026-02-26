package uk.gov.govuk.search.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import uk.gov.govuk.search.data.remote.model.AutocompleteResponse
import uk.gov.govuk.search.domain.SearchConfig

interface AutocompleteApi {
    @GET(SearchConfig.AUTOCOMPLETE_PATH)
    suspend fun getSuggestions(
        @Query("q") searchTerm: String
    ): Response<AutocompleteResponse>
}
