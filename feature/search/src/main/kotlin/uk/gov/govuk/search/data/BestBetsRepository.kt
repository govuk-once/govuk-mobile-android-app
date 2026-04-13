package uk.gov.govuk.search.data

import uk.gov.govuk.search.data.remote.model.SearchResult
import javax.inject.Inject
import javax.inject.Singleton

internal interface BestBetsRepository {
    fun applyBestBets(searchTerm: String, results: List<SearchResult>): List<SearchResult>
}

@Singleton
internal class HardcodedBestBetsRepository @Inject constructor() : BestBetsRepository {

    private val bestBets: Map<String, List<String>> = mapOf(
        "budget" to listOf("/government/publications/budget-2025-document")
    )

    override fun applyBestBets(searchTerm: String, results: List<SearchResult>): List<SearchResult> {
        val bestBetLinks = bestBets
            .filter { (term, _) -> searchTerm.contains(term, ignoreCase = true) }
            .values
            .flatten()
            .distinct()

        if (bestBetLinks.isEmpty()) return results

        val promoted = results
            .filter { it.link in bestBetLinks }
            .sortedBy { bestBetLinks.indexOf(it.link) }
        val rest = results.filter { it.link !in bestBetLinks }
        return promoted + rest
    }
}
