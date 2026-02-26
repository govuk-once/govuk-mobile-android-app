package uk.gov.govuk.analytics.data

import uk.gov.govuk.analytics.data.local.AnalyticsDataStore
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepo @Inject constructor(
    private val dataStore: AnalyticsDataStore
) {
    internal val analyticsEnabledState: AnalyticsEnabledState
        get() = dataStore.analyticsEnabledState

    internal suspend fun analyticsEnabled() = dataStore.analyticsEnabled()

    internal suspend fun analyticsDisabled() = dataStore.analyticsDisabled()

    internal suspend fun clear() = dataStore.clear()
}