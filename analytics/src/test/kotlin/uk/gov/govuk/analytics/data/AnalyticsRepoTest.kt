package uk.gov.govuk.analytics.data

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.analytics.data.local.AnalyticsDataStore
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.DISABLED
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.ENABLED
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.NOT_SET

class AnalyticsRepoTest {

    private val dataStore = mockk<AnalyticsDataStore>(relaxed = true)

    @Test
    fun `Given analytics are not set, then return not set`() {
        every { dataStore.analyticsEnabledState } returns NOT_SET

        val repo = AnalyticsRepo(dataStore)

        assertEquals(NOT_SET, repo.analyticsEnabledState)
    }

    @Test
    fun `Given analytics are enabled, then return enabled`() {
        every { dataStore.analyticsEnabledState  } returns ENABLED

        val repo = AnalyticsRepo(dataStore)

        assertEquals(ENABLED, repo.analyticsEnabledState )
    }

    @Test
    fun `Given analytics are disabled, then return disabled`() {
        every { dataStore.analyticsEnabledState  } returns DISABLED

        val repo = AnalyticsRepo(dataStore)

        assertEquals(DISABLED, repo.analyticsEnabledState )
    }

    @Test
    fun `Given analytics have been enabled, then update data store`() {
        val repo = AnalyticsRepo(dataStore)

        runTest {
            repo.analyticsEnabled()

            coVerify { dataStore.analyticsEnabled() }
        }
    }

    @Test
    fun `Given analytics have been disabled, then update data store`() {
        val repo = AnalyticsRepo(dataStore)

        runTest {
            repo.analyticsDisabled()

            coVerify { dataStore.analyticsDisabled() }
        }
    }

    @Test
    fun `Given analytics have been cleared, then update data store`() {
        val repo = AnalyticsRepo(dataStore)

        runTest {
            repo.clear()

            coVerify { dataStore.clear() }
        }
    }
}