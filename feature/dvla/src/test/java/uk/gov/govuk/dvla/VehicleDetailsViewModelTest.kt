package uk.gov.govuk.dvla

import androidx.lifecycle.SavedStateHandle
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.ui.model.VehicleDetailsMapper

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleDetailsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private lateinit var viewModel: VehicleDetailsViewModel
    private val dvlaRepo = mockk<DvlaRepo>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val mapper = mockk<VehicleDetailsMapper>(relaxed = true)
    private val registration = "TE5T PL8"

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { savedStateHandle.get<String>("vehicle_registration") } returns registration
        viewModel = VehicleDetailsViewModel(savedStateHandle, dvlaRepo, analyticsClient, mapper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given screen title, when onPageView is called, then track screen view`() =
        runTest(dispatcher) {
            viewModel.onPageView("title")

            verify {
                analyticsClient.screenView(
                    screenClass = "VehicleDetailsScreen",
                    screenName = "VehicleDetailsScreen",
                    title = "title"
                )
            }
        }
}
