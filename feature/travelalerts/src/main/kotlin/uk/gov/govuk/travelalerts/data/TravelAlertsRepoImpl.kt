package uk.gov.govuk.travelalerts.data

import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.travelalerts.data.model.Country
import uk.gov.govuk.travelalerts.data.model.Group
import uk.gov.govuk.travelalerts.data.model.Subgroup
import uk.gov.govuk.travelalerts.data.model.SubscriptionRequest
import uk.gov.govuk.travelalerts.data.remote.GroupsApi
import uk.gov.govuk.travelalerts.data.remote.TravelApi
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
    private val groupsApi: GroupsApi,
    private val travelApi: TravelApi,
    private val authRepo: AuthRepo,
    private val dateProvider: DateProvider
) : TravelAlertsRepo {

    data class CacheEntry<T>(val value: T, val lastUpdated: Instant = Instant.now()) {
        fun hasExpired(now: Instant): Boolean =
            now.isAfter(lastUpdated.plus(300, ChronoUnit.SECONDS))
    }

    private var groups: CacheEntry<List<Group>>? = null
    private var countries: CacheEntry<List<Country>>? = null

    override suspend fun getGroups(): Result<List<Group>> {
        val currGroups = groups

        if (currGroups != null && !currGroups.hasExpired(dateProvider.date)) {
            return Success(currGroups.value)
        }

        val res = safeAuthApiCall(apiCall = {
            groupsApi.getGroups()
        }, authRepo = authRepo)

        if (res is Success) {
            groups = CacheEntry(res.value)
        }

        return res
    }

    override suspend fun getCountries(): Result<List<Country>> {
        val currCountries = countries

        if (currCountries != null && !currCountries.hasExpired(dateProvider.date)) {
            return Success(currCountries.value)
        }

        val res = safeAuthApiCall(apiCall = {
            travelApi.getCountries()
        }, authRepo = authRepo)

        if (res is Success) {
            countries = CacheEntry(res.value)
        }

        return res
    }

    override suspend fun subscribeToCountry(slug: String): Result<Unit> {
        val result = safeAuthApiCall(apiCall = {
            groupsApi.subscribeToGroups(
                listOf(
                    SubscriptionRequest(
                        namespace = "travel",
                        group = slug,
                        subgroup = Subgroup.DAILY
                    )
                )
            )
        }, authRepo = authRepo)

        if (result is Success) groups = null

        return result
    }
}
