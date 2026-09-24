package uk.gov.govuk.travelalerts.data

import uk.gov.govuk.data.model.Result
import uk.gov.govuk.travelalerts.data.model.Country
import uk.gov.govuk.travelalerts.data.model.Group
import java.time.Instant

interface DateProvider {
    val date: Instant
}

interface TravelAlertsRepo {
    suspend fun getGroups(): Result<List<Group>>
    suspend fun getCountries(): Result<List<Country>>
    suspend fun followCountry(slug: String, notificationsEnabled: Boolean): Result<Unit>
    suspend fun toggleNotifications(slug: String, enabled: Boolean): Result<Unit>
    suspend fun unfollowCountry(slug: String, currentNotificationsEnabled: Boolean): Result<Unit>
}