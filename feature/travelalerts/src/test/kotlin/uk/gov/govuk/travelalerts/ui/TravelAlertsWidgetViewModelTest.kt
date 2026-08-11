package uk.gov.govuk.travelalerts.ui

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import uk.gov.govuk.travelalerts.fixtures.TravelAlertsFixtures

@OptIn(ExperimentalCoroutinesApi::class)
class TravelAlertsWidgetViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val travelAlertsRepo = mockk<TravelAlertsRepo>(relaxed = true)
    private lateinit var viewModel: TravelAlertsWidgetViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = TravelAlertsWidgetViewModel(travelAlertsRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given view created, then state is Loading`() {
        assertTrue(viewModel.uiState.value is TravelAlertsWidgetViewModel.State.Loading)
    }

    @Test
    fun `Given page view, when repo succeeds, then state is Loaded`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.Success(TravelAlertsFixtures.mockGroups)

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is TravelAlertsWidgetViewModel.State.Loaded)
    }

    @Test
    fun `Given page view, when repo fails, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.ServiceNotResponding(500)

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is TravelAlertsWidgetViewModel.State.Error)
    }

    @Test
    fun `Given page view, when offline, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.getGroups() } returns Result.DeviceOffline()

        viewModel.onPageView()

        assertTrue(viewModel.uiState.value is TravelAlertsWidgetViewModel.State.Error)
    }
}
