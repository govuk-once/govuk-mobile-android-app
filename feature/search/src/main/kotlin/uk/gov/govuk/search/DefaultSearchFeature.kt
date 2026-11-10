package uk.gov.govuk.search

import uk.gov.govuk.search.data.SearchRepo
import javax.inject.Inject

internal class DefaultSearchFeature @Inject constructor(
    private val searchRepo: SearchRepo
): SearchFeature {

    override suspend fun clear() {
        searchRepo.clear()
    }

}