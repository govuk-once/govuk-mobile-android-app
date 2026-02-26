package uk.gov.govuk.analytics.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.DISABLED
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.ENABLED
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.NOT_SET

class AnalyticsDataStoreTest {

    private val dataStore = mockk<DataStore<Preferences>>(relaxed = true)
    private val preferences = mockk<Preferences>()

    @Test
    fun `Given the data store is empty, then return not set`() {
        every { dataStore.data } returns emptyFlow()

        val datastore = AnalyticsDataStore(dataStore)

        assertEquals(NOT_SET, datastore.analyticsEnabledState)
    }

    @Test
    fun `Given analytics enabled preference is null, then return not set`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AnalyticsDataStore.ANALYTICS_ENABLED_KEY)] } returns null

        val datastore = AnalyticsDataStore(dataStore)

        assertEquals(NOT_SET, datastore.analyticsEnabledState)
    }

    @Test
    fun `Given analytics are enabled, then return enabled`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AnalyticsDataStore.ANALYTICS_ENABLED_KEY)] } returns true

        val datastore = AnalyticsDataStore(dataStore)

        assertEquals(ENABLED, datastore.analyticsEnabledState)
    }

    @Test
    fun `Given analytics are disabled, then return disabled`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AnalyticsDataStore.ANALYTICS_ENABLED_KEY)] } returns false

        val datastore = AnalyticsDataStore(dataStore)

        assertEquals(DISABLED, datastore.analyticsEnabledState )
    }

    @Test
    fun `Given the user enables analytics, then return enabled`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AnalyticsDataStore.ANALYTICS_ENABLED_KEY)] } returns false

        val datastore = AnalyticsDataStore(dataStore)

        runTest {
            assertEquals(DISABLED, datastore.analyticsEnabledState)

            datastore.analyticsEnabled()

            assertEquals(ENABLED, datastore.analyticsEnabledState)
        }
    }

    @Test
    fun `Given the user disables analytics, then return disabled`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AnalyticsDataStore.ANALYTICS_ENABLED_KEY)] } returns true

        val datastore = AnalyticsDataStore(dataStore)

        runTest {
            assertEquals(ENABLED, datastore.analyticsEnabledState)

            datastore.analyticsDisabled()

            assertEquals(DISABLED, datastore.analyticsEnabledState)
        }
    }

    @Test
    fun `Given the user clears analytics, then return not set`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AnalyticsDataStore.ANALYTICS_ENABLED_KEY)] } returns true

        val datastore = AnalyticsDataStore(dataStore)

        runTest {
            assertEquals(ENABLED, datastore.analyticsEnabledState)

            datastore.clear()

            assertEquals(NOT_SET, datastore.analyticsEnabledState)
        }
    }
}