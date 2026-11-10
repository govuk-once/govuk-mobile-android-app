package uk.gov.govuk.search

import uk.gov.govuk.search.data.remote.model.SearchResult
import java.util.UUID


internal data class SearchUiState(
    val previousSearches: List<String> = emptyList(),
    val suggestions: Suggestions? = null,
    val searchResults: SearchResults? = null,
    val performingSearch: Boolean = false,
    val error: Error? = null
) {
    data class Suggestions(
        val searchTerm: String,
        val values: List<String>
    )

    data class SearchResults(
        val searchTerm: String,
        val values: List<SearchResult>
    )

    internal sealed class Error(
        val uuid: UUID
    ) {
        internal class Empty(uuid: UUID, val searchTerm: String) : Error(uuid)

        internal class Offline(uuid: UUID, val searchTerm: String) : Error(uuid)

        internal class ServiceError(uuid: UUID) : Error(uuid)
    }
}
