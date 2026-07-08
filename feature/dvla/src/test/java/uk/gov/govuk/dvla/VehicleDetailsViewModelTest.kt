package uk.gov.govuk.dvla

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
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.ui.model.VehicleDetailsMapper

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleDetailsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private lateinit var viewModel: VehicleDetailsViewModel
    private val dvlaRepo = mockk<DvlaRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val mapper = mockk<VehicleDetailsMapper>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = VehicleDetailsViewModel(dvlaRepo, analyticsClient, mapper, configRepo)
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

    @Test
    fun `When onExternalButtonClicked is called, then analytics event is fired with correct parameters`() = runTest(dispatcher) {
        viewModel.onExternalButtonClicked("Text", "https://www.test.com", "Section")

        verify(exactly = 1) {
            analyticsClient.buttonClick(
                text = "Text",
                url = "https://www.test.com",
                external = true,
                section = "Section"
            )
        }
    }
}
