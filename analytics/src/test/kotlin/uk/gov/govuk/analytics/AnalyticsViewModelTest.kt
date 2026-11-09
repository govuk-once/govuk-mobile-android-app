package uk.gov.govuk.analytics

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given analytics have been enabled, then update and emit ui state`() {
        val viewModel = AnalyticsViewModel(analyticsClient)

        viewModel.onConsentGranted()

        runTest {
            coVerify {
                analyticsClient.enable()
            }
        }
    }

    @Test
    fun `Given analytics have been disabled, then update and emit ui state`() {
        val viewModel = AnalyticsViewModel(analyticsClient)

        viewModel.onConsentDenied()

        runTest {
            coVerify {
                analyticsClient.disable()
            }
        }
    }
}