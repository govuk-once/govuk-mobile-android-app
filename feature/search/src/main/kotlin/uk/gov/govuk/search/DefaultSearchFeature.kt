package uk.gov.govuk.search

import javax.inject.Inject
import uk.gov.govuk.search.data.SearchRepo

internal class DefaultSearchFeature @Inject constructor(private val searchRepo: SearchRepo) : SearchFeature {

    override suspend fun clear() {
        searchRepo.clear()
    }
}
