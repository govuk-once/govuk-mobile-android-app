package uk.gov.govuk.analytics.data

import uk.gov.govuk.analytics.data.local.AnalyticsDataStore
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.user.UserRepo
import uk.gov.govuk.data.user.model.UpdateUserDataResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepo @Inject constructor(
    private val dataStore: AnalyticsDataStore,
    private val userRepo: UserRepo
) {
    internal suspend fun sendConsent(): Result<UpdateUserDataResponse> {
        return userRepo.updateAnalytics(true)
    }

    internal suspend fun sendRemoveConsent(): Result<UpdateUserDataResponse> {
        return userRepo.updateAnalytics(false)
    }

    internal val analyticsEnabledState: AnalyticsEnabledState
        get() = dataStore.analyticsEnabledState

    internal suspend fun analyticsEnabled() = dataStore.analyticsEnabled()

    internal suspend fun analyticsDisabled() = dataStore.analyticsDisabled()

    internal suspend fun clear() = dataStore.clear()
}
