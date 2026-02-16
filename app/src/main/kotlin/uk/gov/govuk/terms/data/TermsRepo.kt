package uk.gov.govuk.terms.data

import uk.gov.govuk.terms.data.local.TermsDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TermsRepo @Inject constructor(
    private val termsDataStore: TermsDataStore
) {
    internal suspend fun getTermsAcceptedDate() = termsDataStore.getTermsAcceptedDate()

    internal suspend fun setTermsAcceptedDate(acceptedDate: Long) {
        termsDataStore.setTermsAcceptedDate(acceptedDate)
    }

    internal suspend fun shouldDisplayTerms() = getTermsAcceptedDate() == null

    internal suspend fun clear() = termsDataStore.clear()
}
