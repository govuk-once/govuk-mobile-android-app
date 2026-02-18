package uk.gov.govuk.terms.data

import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.terms.data.local.TermsDataStore
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

internal sealed class TermsAcceptanceState {
    internal data object Accepted: TermsAcceptanceState()
    internal data class NewUser(val termsUrl: String): TermsAcceptanceState()
    internal data class Updated(val termsUrl: String): TermsAcceptanceState()
    internal data object Error: TermsAcceptanceState()
}

@Singleton
internal class TermsRepo @Inject constructor(
    private val termsDataStore: TermsDataStore,
    private val configRepo: ConfigRepo
) {
    internal suspend fun termsAccepted(acceptedDate: Long = System.currentTimeMillis()) {
        termsDataStore.setTermsAcceptedDate(acceptedDate)
    }

    internal suspend fun getTermsAcceptanceState(): TermsAcceptanceState {
        val terms = configRepo.termsAndConditions ?: return TermsAcceptanceState.Error
        val termsAcceptedAt = getTermsAcceptedDate() ?: return TermsAcceptanceState.NewUser(terms.url)

        return try {
            val termsUpdatedAt = Instant.parse(terms.lastUpdated)
            if (termsUpdatedAt.toEpochMilli() > termsAcceptedAt) {
                TermsAcceptanceState.Updated(terms.url)
            } else {
                TermsAcceptanceState.Accepted
            }
        } catch (_: Exception) {
            TermsAcceptanceState.Error
        }
    }

    private suspend fun getTermsAcceptedDate() = termsDataStore.getTermsAcceptedDate()

    internal suspend fun clear() = termsDataStore.clear()
}
