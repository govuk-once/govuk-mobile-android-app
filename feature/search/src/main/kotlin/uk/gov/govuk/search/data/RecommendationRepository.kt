package uk.gov.govuk.search.data

import uk.gov.govuk.search.data.remote.model.SearchResult
import javax.inject.Inject
import javax.inject.Singleton

internal interface RecommendationRepository {
    fun getRecommendations(searchTerm: String): List<SearchResult>
}

@Singleton
internal class HardcodedRecommendationRepository @Inject constructor() : RecommendationRepository {

    private val recommendations: Map<String, List<SearchResult>> = mapOf(
        "passport" to listOf(
            SearchResult(
                contentId = "b8271dea-0375-4e2c-a426-7a1b8a76acd3",
                title = "Apply online for a UK passport",
                description = "Apply for, renew, replace or update your passport and pay for it online.",
                link = "/apply-renew-passport"
            )
        ),
        "universal credit" to listOf(
            SearchResult(
                contentId = "1940a7ad-3956-4c23-9cfe-a71e9cf4ac38",
                title = "Universal Credit",
                description = "Universal Credit is a payment to help with your living costs.",
                link = "/universal-credit"
            )
        ),
        "driving licence" to listOf(
            SearchResult(
                contentId = "cdcc2d2e-7b9a-4b0d-8c1e-4e2d5f3b7a9c",
                title = "Apply for your first provisional driving licence",
                description = "Apply online for your first provisional driving licence.",
                link = "/apply-first-provisional-driving-licence"
            )
        )
    )

    override fun getRecommendations(searchTerm: String): List<SearchResult> {
        return recommendations
            .filter { (term, _) -> searchTerm.contains(term, ignoreCase = true) }
            .values
            .flatten()
            .distinctBy { it.contentId }
    }
}
