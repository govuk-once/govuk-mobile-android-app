package uk.gov.govuk.dvla

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.domain.CustomerSummary
import uk.gov.govuk.dvla.domain.CustomerVehicle
import uk.gov.govuk.dvla.domain.DvlaLinkState
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.ui.model.UiState
import uk.gov.govuk.dvla.ui.model.VehicleSummaryMapper
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiState

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleAndLicenceSummaryViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repo = mockk<DvlaRepo>(relaxed = true)
    private val mapper = mockk<VehicleSummaryMapper>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given linkState emits UNLINKED, when viewModel initialised, then state is Hidden and no calls are made`() =
        runTest(dispatcher) {
            every { repo.linkState } returns MutableStateFlow(DvlaLinkState.UNLINKED)

            val viewModel = VehicleAndLicenceSummaryViewModel(repo, mapper)
            advanceUntilIdle()

            assertEquals(UiState.Hidden, viewModel.uiState.value)
            coVerify(exactly = 0) { repo.getCustomerSummary() }
            coVerify(exactly = 0) { repo.getLicenceDetails() }
            coVerify(exactly = 0) { repo.getDriverSummary() }
        }

    @Test
    fun `Given linkState emits LINKED and getCustomerSummary returns success, when viewModel initialised, then state becomes Success`() =
        runTest(dispatcher) {
            val vehicle = mockk<CustomerVehicle>()
            val vehicleSummaryUiModel = mockk<VehicleSummaryUiModel>()
            val customerSummary = mockk<CustomerSummary> {
                every { vehicles } returns listOf(vehicle)
            }
            every { repo.linkState } returns MutableStateFlow(DvlaLinkState.LINKED)
            coEvery { repo.getCustomerSummary() } returns Result.Success(customerSummary)
            coEvery { repo.getLicenceDetails() } returns Result.Success(mockk())
            coEvery { repo.getDriverSummary() } returns Result.Success(mockk())

            every { mapper.toUiModel(vehicle) } returns vehicleSummaryUiModel

            val viewModel = VehicleAndLicenceSummaryViewModel(repo, mapper)

            advanceUntilIdle()

            coVerify(exactly = 1) { repo.getCustomerSummary() }
            coVerify(exactly = 1) { repo.getLicenceDetails() }
            coVerify(exactly = 1) { repo.getDriverSummary() }

            assertEquals(
                VehicleSummaryUiState.Success(listOf(vehicleSummaryUiModel)),
                viewModel.vehicleSummaryUiState.value
            )
        }

    @Test
    fun `Given isLinked emits true and getLicenceDetails() returns error, when viewModel initialised, then state becomes Error`() =
        runTest(dispatcher) {
            every { repo.linkState } returns MutableStateFlow(DvlaLinkState.LINKED)
            coEvery { repo.getCustomerSummary() } returns Result.Error()
            coEvery { repo.getLicenceDetails() } returns Result.Error()
            coEvery { repo.getDriverSummary() } returns Result.Error()

            val viewModel = VehicleAndLicenceSummaryViewModel(repo, mapper)
            advanceUntilIdle()

            coVerify(exactly = 1) { repo.getLicenceDetails() }
            coVerify(exactly = 1) { repo.getDriverSummary() }
            assertEquals(VehicleSummaryUiState.Error, viewModel.vehicleSummaryUiState.value)
        }

    @Test
    fun `Given account is linked then unlinked, state switches from Success to Hidden`() =
        runTest(dispatcher) {
            val linkStateFlow = MutableStateFlow(DvlaLinkState.LINKED)

            val vehicle = mockk<CustomerVehicle>()
            val vehicleSummaryUiModel = mockk<VehicleSummaryUiModel>()
            val customerSummary = mockk<CustomerSummary> {
                every { vehicles } returns listOf(vehicle)
            }

            every { repo.linkState } returns linkStateFlow
            coEvery { repo.getCustomerSummary() } returns Result.Success(customerSummary)
            every { mapper.toUiModel(vehicle) } returns vehicleSummaryUiModel

            val viewModel = VehicleAndLicenceSummaryViewModel(repo, mapper)
            advanceUntilIdle()

            assertEquals(
                VehicleSummaryUiState.Success(listOf(vehicleSummaryUiModel)),
                viewModel.vehicleSummaryUiState.value
            )

            // unlinking account in settings
            linkStateFlow.value = DvlaLinkState.UNLINKED
            advanceUntilIdle()

            assertEquals(UiState.Hidden, viewModel.uiState.value)
        }

    @Test
    fun `When onVehicleSelected, then ui state category is vehicle`() = runTest(dispatcher) {
        val linkStateFlow = MutableStateFlow(DvlaLinkState.LINKED)
        every { repo.linkState } returns linkStateFlow

        val viewModel = VehicleAndLicenceSummaryViewModel(repo, mapper)
        advanceUntilIdle()

        viewModel.onVehicleSelected()
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.setSelectedDrivingView(drivingView = DrivingView.VEHICLE) }
        assertEquals(UiState.Default(drivingView = DrivingView.VEHICLE), viewModel.uiState.value)
    }

    @Test
    fun `When onLicenceSelected, then ui state category is licence`() = runTest(dispatcher) {
        val linkStateFlow = MutableStateFlow(DvlaLinkState.LINKED)
        every { repo.linkState } returns linkStateFlow

        val viewModel = VehicleAndLicenceSummaryViewModel(repo, mapper)
        advanceUntilIdle()

        viewModel.onLicenceSelected()
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.setSelectedDrivingView(drivingView = DrivingView.LICENCE) }
        assertEquals(UiState.Default(drivingView = DrivingView.LICENCE), viewModel.uiState.value)
    }
}
