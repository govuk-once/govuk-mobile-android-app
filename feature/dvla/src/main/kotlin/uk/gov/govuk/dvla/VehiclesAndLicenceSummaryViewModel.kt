package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.design.ui.component.error.ErrorConstants.GOV_UK_URL
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.domain.LicenceDetailsResult
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.ui.model.LicenceSummaryMapper
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiState
import uk.gov.govuk.dvla.ui.model.UiState
import uk.gov.govuk.dvla.ui.model.UrlModel
import uk.gov.govuk.dvla.ui.model.VehicleSummaryMapper
import uk.gov.govuk.dvla.ui.model.VehiclesSummaryUiState
import javax.inject.Inject

@HiltViewModel
internal class VehiclesAndLicenceSummaryViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo,
    private val vehicleMapper: VehicleSummaryMapper,
    private val licenceMapper: LicenceSummaryMapper,
    private val analyticsClient: AnalyticsClient,
    configRepo: ConfigRepo
) : ViewModel() {

    companion object {
        private const val SECTION_DRIVING = "Driving"
        private const val SECTION_DRIVER_ACCOUNT = "Driver account"
        private const val ACTION_COPY = "Copy"
        private const val ANALYTICS_EVENT_CLIPBOARD_COPY = "Copy to clipboard"
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Hidden)
    val uiState = _uiState.asStateFlow()
    val dvlaUrls = configRepo.dvlaUrls

    init {
        viewModelScope.launch {
            dvlaRepo.linkState.collect { state ->
                when (state) {
                    ServiceLinkStatus.LINKED -> {
                        val drivingView = dvlaRepo.getSelectedDrivingView() ?: DrivingView.VEHICLES
                        _uiState.update { current ->
                            if (current is UiState.Default) current.copy(drivingView = drivingView)
                            else UiState.Default(drivingView = drivingView)
                        }
                        fetchLicenceDetails()
                        fetchVehicles()
                    }

                    ServiceLinkStatus.UNLINKED,
                    ServiceLinkStatus.CHECKING -> _uiState.value = UiState.Hidden
                    ServiceLinkStatus.ERROR -> {
                        val fallbackUrl = UrlModel(dvlaUrls?.account ?: GOV_UK_URL)
                        _uiState.value = UiState.Error(fallbackUrl)
                    }
                }
            }
        }
    }

    fun onVehiclesSelected() {
        setSelectedDrivingView(drivingView = DrivingView.VEHICLES)
    }

    fun onLicenceSelected() {
        setSelectedDrivingView(drivingView = DrivingView.LICENCE)
    }

    fun onLicenceNumberLongPressed() {
        analyticsClient.buttonFunction(
            text = ANALYTICS_EVENT_CLIPBOARD_COPY,
            section = SECTION_DRIVING,
            action = ACTION_COPY
        )
    }

    fun onAddVehiclesClicked(text: String, url: String) {
        analyticsClient.accountCardClick(
            text = text,
            url = url,
            external = true,
            section = SECTION_DRIVING
        )
    }

    fun onAddAnotherVehicleClicked(text: String, url: String) {
        analyticsClient.buttonClick(
            text = text,
            url = url,
            external = true,
            section = SECTION_DRIVING
        )
    }

    fun onButtonClicked(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION_DRIVING
        )
    }

    fun onExternalButtonClicked(text: String, url: String) {
        analyticsClient.buttonClick(
            text = text,
            url = url,
            external = true,
            section = SECTION_DRIVING
        )
    }

    fun onMenuItemClicked(text: String, url: String) {
        analyticsClient.menuItemClick(
            text = text,
            external = true,
            section = SECTION_DRIVER_ACCOUNT,
            url = url
        )
    }

    fun onCopyLicenceMenuOptionClicked() {
        analyticsClient.menuItemFunction(
            text = ANALYTICS_EVENT_CLIPBOARD_COPY,
            section = SECTION_DRIVER_ACCOUNT,
            action = ACTION_COPY
        )
    }

    private fun setSelectedDrivingView(drivingView: DrivingView) {
        viewModelScope.launch {
            dvlaRepo.setSelectedDrivingView(drivingView = drivingView)
            _uiState.update { state ->
                if (state is UiState.Default) {
                    state.copy(drivingView = drivingView)
                } else state
            }
        }
    }

    private fun fetchLicenceDetails() {
        viewModelScope.launch {
            updateLicenceState(LicenceSummaryUiState.Loading)

            val newState = when (val result = dvlaRepo.getLicenceDetails()) {
                is LicenceDetailsResult.Success -> licenceMapper.toUiModel(result.details, dvlaUrls)

                is LicenceDetailsResult.NotFound,
                is LicenceDetailsResult.NotAvailableForEnquiry ->
                    LicenceSummaryUiState.NotAvailable(licenceMapper.notAvailableUrl(dvlaUrls))

                is LicenceDetailsResult.Failure -> {
                    val fallbackUrl = UrlModel(dvlaUrls?.driverDetails ?: GOV_UK_URL)
                    LicenceSummaryUiState.Error(fallbackUrl)
                }
            }

            updateLicenceState(newState)
        }
    }

    private fun fetchVehicles() {
        viewModelScope.launch {
            updateVehiclesState(VehiclesSummaryUiState.Loading)

            val newState = when (val result = dvlaRepo.getCustomerVehicles()) {
                is Result.Success -> {
                    val vehicles = result.value.map { vehicleMapper.toUiModel(it, dvlaUrls) }
                    VehiclesSummaryUiState.Success(vehicles)
                }
                else -> {
                    val fallbackUrl = UrlModel(dvlaUrls?.account ?: GOV_UK_URL)
                    VehiclesSummaryUiState.Error(fallbackUrl)
                }
            }

            updateVehiclesState(newState)
        }
    }

    private fun updateVehiclesState(newState: VehiclesSummaryUiState) {
        _uiState.update { state ->
            if (state is UiState.Default) state.copy(vehiclesState = newState) else state
        }
    }

    private fun updateLicenceState(newState: LicenceSummaryUiState) {
        _uiState.update { state ->
            if (state is UiState.Default) state.copy(licenceState = newState) else state
        }
    }
}