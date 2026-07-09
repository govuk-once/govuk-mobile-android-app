package uk.gov.govuk.dvla

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.domain.CheckCodeDetails
import uk.gov.govuk.dvla.domain.VehicleSummary
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.ui.model.LicenceSummaryMapper
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiModel
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiState
import uk.gov.govuk.dvla.ui.model.UiState
import uk.gov.govuk.dvla.ui.model.UrlModel
import uk.gov.govuk.dvla.ui.model.VehicleSummaryMapper
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel
import uk.gov.govuk.dvla.ui.model.VehiclesSummaryUiState

@OptIn(ExperimentalCoroutinesApi::class)
class VehiclesAndLicenceSummaryViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val dvlaRepo = mockk<DvlaRepo>(relaxed = true)
    private val vehicleMapper = mockk<VehicleSummaryMapper>(relaxed = true)
    private val licenceMapper = mockk<LicenceSummaryMapper>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)

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
            every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.UNLINKED)

            val viewModel = VehiclesAndLicenceSummaryViewModel(
                dvlaRepo,
                vehicleMapper,
                licenceMapper,
                analyticsClient,
                configRepo
            )
            advanceUntilIdle()

            assertEquals(UiState.Hidden, viewModel.uiState.value)
            coVerify(exactly = 0) { dvlaRepo.getCustomerVehicles() }
            coVerify(exactly = 0) { dvlaRepo.getLicenceDetails() }
        }

    @Test
    fun `Given linkState emits LINKED and getCustomerVehicles returns success, when viewModel initialised, then state becomes Success`() =
        runTest(dispatcher) {
            val vehicle = mockk<VehicleSummary>()
            val vehicleSummaryUiModel = mockk<VehicleSummaryUiModel>()
            every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)
            coEvery { dvlaRepo.getCustomerVehicles() } returns Result.Success(listOf(vehicle))
            coEvery { dvlaRepo.getLicenceDetails() } returns Result.Success(mockk())

            every { vehicleMapper.toUiModel(vehicle, any()) } returns vehicleSummaryUiModel

            val viewModel = VehiclesAndLicenceSummaryViewModel(
                dvlaRepo,
                vehicleMapper,
                licenceMapper,
                analyticsClient,
                configRepo
            )

            advanceUntilIdle()

            coVerify(exactly = 1) { dvlaRepo.getCustomerVehicles() }
            coVerify(exactly = 1) { dvlaRepo.getLicenceDetails() }

            assertEquals(
                VehiclesSummaryUiState.Success(listOf(vehicleSummaryUiModel)),
                (viewModel.uiState.value as UiState.Default).vehiclesState
            )
        }

    @Test
    fun `Given isLinked emits true, getLicenceDetails() returns error and dvla urls has account, when viewModel initialised, then state becomes Error`() =
        runTest(dispatcher) {
            every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)
            coEvery { dvlaRepo.getCustomerVehicles() } returns Result.Error()
            coEvery { dvlaRepo.getLicenceDetails() } returns Result.Error()
            coEvery { configRepo.dvlaUrls?.account } returns "https:www.test.com"

            val viewModel = VehiclesAndLicenceSummaryViewModel(
                dvlaRepo,
                vehicleMapper,
                licenceMapper,
                analyticsClient,
                configRepo
            )

            advanceUntilIdle()

            coVerify(exactly = 1) { dvlaRepo.getLicenceDetails() }

            val currentState = viewModel.uiState.value as UiState.Default
            assertEquals(VehiclesSummaryUiState.Error(UrlModel("https:www.test.com")), currentState.vehiclesState)
        }

    @Test
    fun `Given isLinked emits true, getLicenceDetails() returns error and dvla urls is null, when viewModel initialised, then state becomes Error`() =
        runTest(dispatcher) {
            every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)
            coEvery { dvlaRepo.getCustomerVehicles() } returns Result.Error()
            coEvery { dvlaRepo.getLicenceDetails() } returns Result.Error()
            coEvery { configRepo.dvlaUrls?.account } returns null

            val viewModel = VehiclesAndLicenceSummaryViewModel(
                dvlaRepo,
                vehicleMapper,
                licenceMapper,
                analyticsClient,
                configRepo
            )

            advanceUntilIdle()

            coVerify(exactly = 1) { dvlaRepo.getLicenceDetails() }

            val currentState = viewModel.uiState.value as UiState.Default
            assertEquals(VehiclesSummaryUiState.Error(UrlModel("https://www.gov.uk")), currentState.vehiclesState)
        }

    @Test
    fun `Given account is linked then unlinked, state switches from Success to Hidden`() =
        runTest(dispatcher) {
            val linkStateFlow = MutableStateFlow(ServiceLinkStatus.LINKED)

            val vehicle = mockk<VehicleSummary>()
            val vehicleSummaryUiModel = mockk<VehicleSummaryUiModel>()

            every { dvlaRepo.linkState } returns linkStateFlow
            coEvery { dvlaRepo.getCustomerVehicles() } returns Result.Success(listOf(vehicle))
            every { vehicleMapper.toUiModel(vehicle, any()) } returns vehicleSummaryUiModel

            val viewModel = VehiclesAndLicenceSummaryViewModel(
                dvlaRepo,
                vehicleMapper,
                licenceMapper,
                analyticsClient,
                configRepo
            )

            advanceUntilIdle()

            assertEquals(
                VehiclesSummaryUiState.Success(listOf(vehicleSummaryUiModel)),
                (viewModel.uiState.value as UiState.Default).vehiclesState
            )

            // unlinking account in settings
            linkStateFlow.value = ServiceLinkStatus.UNLINKED
            advanceUntilIdle()

            assertEquals(UiState.Hidden, viewModel.uiState.value)
        }

    @Test
    fun `When onVehiclesSelected, then ui state driving view is vehicles`() = runTest(dispatcher) {
        val linkStateFlow = MutableStateFlow(ServiceLinkStatus.LINKED)
        every { dvlaRepo.linkState } returns linkStateFlow
        coEvery { dvlaRepo.getSelectedDrivingView() } returns DrivingView.LICENCE

        val viewModel = VehiclesAndLicenceSummaryViewModel(
            dvlaRepo,
            vehicleMapper,
            licenceMapper,
            analyticsClient,
            configRepo
        )

        advanceUntilIdle()

        viewModel.onVehiclesSelected()
        advanceUntilIdle()

        coVerify(exactly = 1) { dvlaRepo.setSelectedDrivingView(drivingView = DrivingView.VEHICLES) }

        val currentState = viewModel.uiState.value as UiState.Default
        assertEquals(DrivingView.VEHICLES, currentState.drivingView)
    }

    @Test
    fun `When onLicenceSelected, then ui state driving view is licence`() = runTest(dispatcher) {
        val linkStateFlow = MutableStateFlow(ServiceLinkStatus.LINKED)
        every { dvlaRepo.linkState } returns linkStateFlow
        coEvery { dvlaRepo.getSelectedDrivingView() } returns DrivingView.VEHICLES

        val viewModel = VehiclesAndLicenceSummaryViewModel(
            dvlaRepo,
            vehicleMapper,
            licenceMapper,
            analyticsClient,
            configRepo
        )

        advanceUntilIdle()

        viewModel.onLicenceSelected()
        advanceUntilIdle()

        coVerify(exactly = 1) { dvlaRepo.setSelectedDrivingView(drivingView = DrivingView.LICENCE) }

        val currentState = viewModel.uiState.value as UiState.Default
        assertEquals(DrivingView.LICENCE, currentState.drivingView)
    }

    @Test
    fun `Given getLicenceDetails returns success, when viewModel initialised, then licence state becomes Success`() =
        runTest(dispatcher) {
            val licenceUiModel = mockk<LicenceSummaryUiModel>()
            every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)
            coEvery { dvlaRepo.getLicenceDetails() } returns Result.Success(mockk())
            coEvery { dvlaRepo.getCustomerVehicles() } returns Result.Success(emptyList())
            every { licenceMapper.toUiModel(any(), any()) } returns licenceUiModel

            val viewModel = VehiclesAndLicenceSummaryViewModel(
                dvlaRepo,
                vehicleMapper,
                licenceMapper,
                analyticsClient,
                configRepo
            )

            advanceUntilIdle()

            val currentState = viewModel.uiState.value as UiState.Default
            assertEquals(LicenceSummaryUiState.Success(licenceUiModel), currentState.licenceState)
        }

    @Test
    fun `Given linkState emits LINKED, when viewModel initialised, then check code creation and cancellation are called`() =
        runTest(dispatcher) {
            val token = "token-id"

            val mockCheckCode = mockk<CheckCodeDetails> {
                every { tokenId } returns token
            }

            every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)
            coEvery { dvlaRepo.getCheckCodes() } returns Result.Success(mockk())
            coEvery { dvlaRepo.createCheckCode() } returns Result.Success(mockCheckCode)
            coEvery { dvlaRepo.cancelCheckCode(any()) } returns Result.Success(mockk())

            val viewModel = VehiclesAndLicenceSummaryViewModel(
                dvlaRepo,
                vehicleMapper,
                licenceMapper,
                analyticsClient,
                configRepo
            )
            advanceUntilIdle()

            coVerify(exactly = 1) { dvlaRepo.getCheckCodes() }
            coVerify(exactly = 1) { dvlaRepo.createCheckCode() }
            coVerify(exactly = 1) { dvlaRepo.cancelCheckCode(token) }
        }

    @Test
    fun `Given getLicenceDetails returns error and dvla urls has driver details, when viewModel initialised, then licence state becomes Error`() =
        runTest(dispatcher) {
            every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)
            coEvery { dvlaRepo.getLicenceDetails() } returns Result.Error()
            coEvery { dvlaRepo.getCustomerVehicles() } returns Result.Error()
            coEvery { configRepo.dvlaUrls?.driverDetails } returns "https://www.test.com"

            val viewModel = VehiclesAndLicenceSummaryViewModel(
                dvlaRepo,
                vehicleMapper,
                licenceMapper,
                analyticsClient,
                configRepo
            )

            advanceUntilIdle()

            val currentState = viewModel.uiState.value as UiState.Default
            assertEquals(LicenceSummaryUiState.Error(UrlModel("https://www.test.com")), currentState.licenceState)
        }

    @Test
    fun `Given getLicenceDetails returns error and dvla urls is null, when viewModel initialised, then licence state becomes Error`() =
        runTest(dispatcher) {
            every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)
            coEvery { dvlaRepo.getLicenceDetails() } returns Result.Error()
            coEvery { dvlaRepo.getCustomerVehicles() } returns Result.Error()
            coEvery { configRepo.dvlaUrls?.driverDetails } returns null

            val viewModel = VehiclesAndLicenceSummaryViewModel(
                dvlaRepo,
                vehicleMapper,
                licenceMapper,
                analyticsClient,
                configRepo
            )

            advanceUntilIdle()

            val currentState = viewModel.uiState.value as UiState.Default
            assertEquals(LicenceSummaryUiState.Error(UrlModel("https://www.gov.uk")), currentState.licenceState)
        }

    @Test
    fun `When onButtonClicked is called, then analytics event is fired with correct parameters`() =
        runTest(dispatcher) {
            every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)

            val viewModel = VehiclesAndLicenceSummaryViewModel(
                dvlaRepo,
                vehicleMapper,
                licenceMapper,
                analyticsClient,
                configRepo
            )

            advanceUntilIdle()

            viewModel.onButtonClicked(
                text = "Text"
            )
            advanceUntilIdle()

            verify(exactly = 1) {
                analyticsClient.buttonClick(
                    text = "Text",
                    external = false,
                    section = "Driving"
                )
            }
        }

    @Test
    fun `When onExternalButtonClicked is called, then analytics event is fired with correct parameters`() = runTest(dispatcher) {
        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)

        val viewModel = VehiclesAndLicenceSummaryViewModel(
            dvlaRepo,
            vehicleMapper,
            licenceMapper,
            analyticsClient,
            configRepo
        )

        advanceUntilIdle()

        viewModel.onExternalButtonClicked(
            text = "Text",
            url = "https://www.test.com"
        )
        advanceUntilIdle()

        verify(exactly = 1) {
            analyticsClient.buttonClick(
                text = "Text",
                url = "https://www.test.com",
                external = true,
                section = "Driving"
            )
        }
    }

    @Test
    fun `When onLicenceNumberLongPressed is called, then analytics event is fired with correct parameters`() = runTest(dispatcher) {
        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)

        val viewModel = VehiclesAndLicenceSummaryViewModel(
            dvlaRepo,
            vehicleMapper,
            licenceMapper,
            analyticsClient,
            configRepo
        )

        advanceUntilIdle()

        viewModel.onLicenceNumberLongPressed()
        advanceUntilIdle()

        verify(exactly = 1) {
            analyticsClient.buttonFunction(
                text = "Copy to clipboard",
                section = "Driving",
                action = "Copy"
            )
        }
    }

    @Test
    fun `When onAddVehiclesClicked is called, then accountCardClick analytics event is fired`() = runTest(dispatcher) {
        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)
        val text = "Add your vehicles"
        val url = "https://www.gov.uk/add-vehicle"

        val viewModel = VehiclesAndLicenceSummaryViewModel(
            dvlaRepo,
            vehicleMapper,
            licenceMapper,
            analyticsClient,
            configRepo
        )

        advanceUntilIdle()

        viewModel.onAddVehiclesClicked(text = text, url = url)
        advanceUntilIdle()

        verify(exactly = 1) {
            analyticsClient.accountCardClick(
                text = text,
                url = url,
                external = true,
                section = "Driving"
            )
        }
    }

    @Test
    fun `When onAddAnotherVehicleClicked is called, then buttonClick analytics event is fired`() = runTest(dispatcher) {
        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)
        val text = "Add vehicle"
        val url = "https://www.gov.uk/add-vehicle"

        val viewModel = VehiclesAndLicenceSummaryViewModel(
            dvlaRepo,
            vehicleMapper,
            licenceMapper,
            analyticsClient,
            configRepo
        )

        advanceUntilIdle()

        viewModel.onAddAnotherVehicleClicked(text = text, url = url)
        advanceUntilIdle()

        verify(exactly = 1) {
            analyticsClient.buttonClick(
                text = text,
                url = url,
                external = true,
                section = "Driving"
            )
        }
    }

    @Test
    fun `When onMenuItemClicked is called, then menuItemClick analytics event is fired with correct parameters`() = runTest(dispatcher) {
        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)
        val text = "Change address"
        val url = "https://www.gov.uk/change-naddress"

        val viewModel = VehiclesAndLicenceSummaryViewModel(
            dvlaRepo,
            vehicleMapper,
            licenceMapper,
            analyticsClient,
            configRepo
        )

        advanceUntilIdle()

        viewModel.onMenuItemClicked(text = text, url = url)
        advanceUntilIdle()

        verify(exactly = 1) {
            analyticsClient.menuItemClick(
                text = text,
                url = url,
                external = true,
                section = "Driver account"
            )
        }
    }

    @Test
    fun `When onCopyLicenceMenuOptionClicked is called, then menuItemFunction analytics event is fired with correct parameters`() = runTest(dispatcher) {
        every { dvlaRepo.linkState } returns MutableStateFlow(ServiceLinkStatus.LINKED)

        val viewModel = VehiclesAndLicenceSummaryViewModel(
            dvlaRepo,
            vehicleMapper,
            licenceMapper,
            analyticsClient,
            configRepo
        )

        advanceUntilIdle()

        viewModel.onCopyLicenceMenuOptionClicked()
        advanceUntilIdle()

        verify(exactly = 1) {
            analyticsClient.menuItemFunction(
                text = "Copy to clipboard",
                section = "Driver account",
                action = "Copy"
            )
        }
    }
}