package uk.gov.govuk.travelalerts.data

import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.travelalerts.data.remote.TravelAlertsApi
import uk.gov.govuk.travelalerts.data.model.Group
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

class DateProviderImpl: DateProvider {
    override val date: Instant
        get() = Instant.now()
}

@Singleton
internal class TravelAlertsRepoImpl @Inject constructor(
    private val travelAlertsApi: TravelAlertsApi,
    private val authRepo: AuthRepo,
    private val dateProvider: DateProvider
) : TravelAlertsRepo {

    data class CacheEntry<T>(val value: T, val lastUpdated: Instant = Instant.now()) {
        fun hasExpired(now: Instant): Boolean =
            now.isAfter(lastUpdated.plus(300, ChronoUnit.SECONDS))
    }

    private var groups: CacheEntry<List<Group>>? = null

    override suspend fun getGroups(): Result<List<Group>> {
        val currGroups = groups

        if (currGroups != null && !currGroups.hasExpired(dateProvider.date)) {
            return Success(currGroups.value)
        }

        val res = safeAuthApiCall(apiCall = {
            travelAlertsApi.getGroups()
        }, authRepo = authRepo)

        if (res is Success) {
            groups = CacheEntry(res.value)
        }

        return res
    }
}
