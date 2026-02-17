package uk.gov.govuk.analytics.data

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.data.local.AnalyticsDataStore
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.DISABLED
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.ENABLED
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.NOT_SET

class AnalyticsRepoTest {

    private val dataStore = mockk<AnalyticsDataStore>(relaxed = true)

    private lateinit var analyticsRepo: AnalyticsRepo

    @Before
    fun setup() {
        analyticsRepo = AnalyticsRepo(dataStore)
    }

    @Test
    fun `Given analytics are not set, then return not set`() {
        every { dataStore.analyticsEnabledState } returns NOT_SET

        assertEquals(NOT_SET, analyticsRepo.analyticsEnabledState)
    }

    @Test
    fun `Given analytics are enabled, then return enabled`() {
        every { dataStore.analyticsEnabledState  } returns ENABLED

        assertEquals(ENABLED, analyticsRepo.analyticsEnabledState )
    }

    @Test
    fun `Given analytics are disabled, then return disabled`() {
        every { dataStore.analyticsEnabledState  } returns DISABLED

        assertEquals(DISABLED, analyticsRepo.analyticsEnabledState )
    }

    @Test
    fun `Given analytics have been enabled, then update data store`() {
        runTest {
            analyticsRepo.analyticsEnabled()

            coVerify { dataStore.analyticsEnabled() }
        }
    }

    @Test
    fun `Given analytics have been disabled, then update data store`() {
        runTest {
            analyticsRepo.analyticsDisabled()

            coVerify { dataStore.analyticsDisabled() }
        }
    }

    @Test
    fun `Given analytics have been cleared, then update data store`() {
        runTest {
            analyticsRepo.clear()

            coVerify { dataStore.clear() }
        }
    }
}