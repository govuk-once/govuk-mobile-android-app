package uk.gov.govuk.search.data

import kotlinx.coroutines.flow.map
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.remote.safeApiCall
import uk.gov.govuk.search.data.local.SearchLocalDataSource
import uk.gov.govuk.search.data.remote.AutocompleteApi
import uk.gov.govuk.search.data.remote.SearchApi
import uk.gov.govuk.search.data.remote.model.AutocompleteResponse
import uk.gov.govuk.search.data.remote.model.SearchResponse
import uk.gov.govuk.search.domain.SearchConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchRepo @Inject constructor(
    private val searchApi: SearchApi,
    private val autocompleteApi: AutocompleteApi,
    private val localDataSource: SearchLocalDataSource
) {

    val previousSearches = localDataSource.previousSearches.map { localSearches ->
        localSearches.map { it.searchTerm }
    }

    suspend fun removePreviousSearch(searchTerm: String) {
        localDataSource.removePreviousSearch(searchTerm)
    }

    suspend fun removeAllPreviousSearches() {
        localDataSource.removeAllPreviousSearches()
    }

    suspend fun performSearch(
        searchTerm: String, count: Int = SearchConfig.DEFAULT_RESULTS_PER_PAGE
    ): Result<SearchResponse> {
        localDataSource.insertOrUpdatePreviousSearch(searchTerm)
        return safeApiCall { searchApi.getSearchResults(searchTerm, count) }
    }

    suspend fun performLookup(searchTerm: String): Result<AutocompleteResponse> {
        return safeApiCall { autocompleteApi.getSuggestions(searchTerm) }
    }

    suspend fun clear() {
        localDataSource.clear()
    }
}

