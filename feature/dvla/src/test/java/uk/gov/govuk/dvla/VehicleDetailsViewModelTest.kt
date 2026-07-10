package uk.gov.govuk.dvla

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.domain.VehicleDetails
import uk.gov.govuk.dvla.navigation.ARG_VEHICLE_ID
import uk.gov.govuk.dvla.ui.model.UrlModel
import uk.gov.govuk.dvla.ui.model.VehicleDetailsMapper
import uk.gov.govuk.dvla.ui.model.VehicleDetailsUiModel

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleDetailsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private val savedStateHandle = SavedStateHandle(mapOf(ARG_VEHICLE_ID to 156487251))
    private val dvlaRepo = mockk<DvlaRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val mapper = mockk<VehicleDetailsMapper>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)
    private val viewModel by lazy {
        VehicleDetailsViewModel(savedStateHandle, dvlaRepo, analyticsClient, mapper, configRepo)
    }

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
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
                    screenName = "title",
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

    @Test
    fun `Given vehicle details api returns success, when viewModel initialised, then state becomes Success`() =
        runTest(dispatcher) {
            val vehicleDetails = mockk<VehicleDetails>()
            val uiModel = mockk<VehicleDetailsUiModel>()
            coEvery { dvlaRepo.getVehicleDetails(156487251) } returns Result.Success(vehicleDetails)
            every { mapper.toUiModel(vehicleDetails, any()) } returns uiModel

            val initialisedViewModel = viewModel
            advanceUntilIdle()

            coVerify(exactly = 1) { dvlaRepo.getVehicleDetails(156487251) }
            assertEquals(VehicleDetailsUiState.Success(uiModel), initialisedViewModel.uiState.value)
        }

    @Test
    fun `Given vehicle details api returns error, when dvla urls is null and viewModel initialised, then state becomes Error`() =
        runTest(dispatcher) {
            coEvery { dvlaRepo.getVehicleDetails(156487251) } returns Result.Error()
            every { configRepo.dvlaUrls?.account } returns null

            val initialisedViewModel = viewModel
            advanceUntilIdle()

            assertEquals(VehicleDetailsUiState.Error(UrlModel("https://www.gov.uk")), initialisedViewModel.uiState.value)
        }

    @Test
    fun `Given vehicle details api returns error, when dvla urls has account and viewModel initialised, then state becomes Error`() =
        runTest(dispatcher) {
            coEvery { dvlaRepo.getVehicleDetails(156487251) } returns Result.Error()
            every { configRepo.dvlaUrls?.account } returns "https://www.test.com"

            val initialisedViewModel = viewModel
            advanceUntilIdle()

            assertEquals(VehicleDetailsUiState.Error(UrlModel("https://www.test.com")), initialisedViewModel.uiState.value)
        }

    @Test
    fun `Given no vehicleId in savedStateHandle, when viewModel initialised, then getVehicleDetails is not called`() =
        runTest(dispatcher) {
            val emptySavedStateHandle = SavedStateHandle()
            val localViewModel = VehicleDetailsViewModel(
                emptySavedStateHandle,
                dvlaRepo,
                analyticsClient,
                mapper,
                configRepo
            )
            advanceUntilIdle()

            coVerify(exactly = 0) { dvlaRepo.getVehicleDetails(any()) }
        }
}
