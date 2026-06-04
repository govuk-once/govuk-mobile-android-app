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
import uk.gov.govuk.dvla.ui.model.LicenceSummaryMapper
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiModel
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiState
import uk.gov.govuk.dvla.ui.model.UiState
import uk.gov.govuk.dvla.ui.model.VehicleSummaryMapper
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiState

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleAndLicenceSummaryViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repo = mockk<DvlaRepo>(relaxed = true)
    private val vehicleMapper = mockk<VehicleSummaryMapper>(relaxed = true)
    private val licenceMapper = mockk<LicenceSummaryMapper>(relaxed = true)

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

            val viewModel = VehicleAndLicenceSummaryViewModel(repo, vehicleMapper, licenceMapper)
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
            coEvery { repo.getDriverSummary() } returns Result.Success(mockk())

            every { vehicleMapper.toUiModel(vehicle) } returns vehicleSummaryUiModel

            val viewModel = VehicleAndLicenceSummaryViewModel(repo, vehicleMapper, licenceMapper)

            advanceUntilIdle()

            coVerify(exactly = 1) { repo.getCustomerSummary() }
            coVerify(exactly = 1) { repo.getDriverSummary() }


            assertEquals(
                VehicleSummaryUiState.Success(listOf(vehicleSummaryUiModel)),
                (viewModel.uiState.value as UiState.Default).vehicleState
            )
        }

    @Test
    fun `Given isLinked emits true and getLicenceDetails() returns error, when viewModel initialised, then state becomes Error`() =
        runTest(dispatcher) {
            every { repo.linkState } returns MutableStateFlow(DvlaLinkState.LINKED)
            coEvery { repo.getCustomerSummary() } returns Result.Error()
            coEvery { repo.getLicenceDetails() } returns Result.Error()
            coEvery { repo.getDriverSummary() } returns Result.Error()

            val viewModel = VehicleAndLicenceSummaryViewModel(repo, vehicleMapper, licenceMapper)
            advanceUntilIdle()

            coVerify(exactly = 1) { repo.getDriverSummary() }

            val currentState = viewModel.uiState.value as UiState.Default
            assertEquals(VehicleSummaryUiState.Error, currentState.vehicleState)
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
            every { vehicleMapper.toUiModel(vehicle) } returns vehicleSummaryUiModel

            val viewModel = VehicleAndLicenceSummaryViewModel(repo, vehicleMapper, licenceMapper)
            advanceUntilIdle()

            assertEquals(
                VehicleSummaryUiState.Success(listOf(vehicleSummaryUiModel)),
                (viewModel.uiState.value as UiState.Default).vehicleState
            )

            // unlinking account in settings
            linkStateFlow.value = DvlaLinkState.UNLINKED
            advanceUntilIdle()

            assertEquals(UiState.Hidden, viewModel.uiState.value)
        }

    @Test
    fun `When onVehicleSelected, then ui state driving view is vehicle`() = runTest(dispatcher) {
        every { repo.linkState } returns MutableStateFlow(DvlaLinkState.LINKED)
        coEvery { repo.getSelectedDrivingView() } returns DrivingView.LICENCE

        val viewModel = VehicleAndLicenceSummaryViewModel(repo, vehicleMapper, licenceMapper)
        advanceUntilIdle()

        viewModel.onVehicleSelected()
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.setSelectedDrivingView(drivingView = DrivingView.VEHICLE) }

        val currentState = viewModel.uiState.value as UiState.Default
        assertEquals(DrivingView.VEHICLE, currentState.drivingView)
    }

    @Test
    fun `When onLicenceSelected, then ui state driving view is licence`() = runTest(dispatcher) {
        every { repo.linkState } returns MutableStateFlow(DvlaLinkState.LINKED)
        coEvery { repo.getSelectedDrivingView() } returns DrivingView.VEHICLE

        val viewModel = VehicleAndLicenceSummaryViewModel(repo, vehicleMapper, licenceMapper)
        advanceUntilIdle()

        viewModel.onLicenceSelected()
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.setSelectedDrivingView(drivingView = DrivingView.LICENCE) }

        val currentState = viewModel.uiState.value as UiState.Default
        assertEquals(DrivingView.LICENCE, currentState.drivingView)
    }

    @Test
    fun `Given getDriverSummary returns success, when viewModel initialised, then licence state becomes Success`() =
        runTest(dispatcher) {
            val licenceUiModel = mockk<LicenceSummaryUiModel>()
            every { repo.linkState } returns MutableStateFlow(DvlaLinkState.LINKED)
            coEvery { repo.getDriverSummary() } returns Result.Success(mockk())
            coEvery { repo.getCustomerSummary() } returns Result.Success(mockk(relaxed = true))
            every { licenceMapper.toUiModel(any()) } returns licenceUiModel

            val viewModel = VehicleAndLicenceSummaryViewModel(repo, vehicleMapper, licenceMapper)
            advanceUntilIdle()

            val currentState = viewModel.uiState.value as UiState.Default
            assertEquals(LicenceSummaryUiState.Success(licenceUiModel), currentState.licenceState)
        }

    @Test
    fun `Given getDriverSummary returns error, when viewModel initialised, then licence state becomes Error`() =
        runTest(dispatcher) {
            every { repo.linkState } returns MutableStateFlow(DvlaLinkState.LINKED)
            coEvery { repo.getDriverSummary() } returns Result.Error()
            coEvery { repo.getCustomerSummary() } returns Result.Error()

            val viewModel = VehicleAndLicenceSummaryViewModel(repo, vehicleMapper, licenceMapper)
            advanceUntilIdle()

            val currentState = viewModel.uiState.value as UiState.Default
            assertEquals(LicenceSummaryUiState.Error, currentState.licenceState)
        }
}
