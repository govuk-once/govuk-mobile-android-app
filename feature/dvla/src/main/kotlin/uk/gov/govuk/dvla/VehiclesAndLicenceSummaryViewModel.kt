package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.domain.DvlaLinkState
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.ui.model.LicenceSummaryMapper
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiState
import uk.gov.govuk.dvla.ui.model.UiState
import uk.gov.govuk.dvla.ui.model.VehicleSummaryMapper
import uk.gov.govuk.dvla.ui.model.VehiclesSummaryUiState
import javax.inject.Inject

@HiltViewModel
internal class VehiclesAndLicenceSummaryViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo,
    private val vehicleMapper: VehicleSummaryMapper,
    private val licenceMapper: LicenceSummaryMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        if (dvlaRepo.linkState.value == DvlaLinkState.LINKED) UiState.Default()
        else UiState.Hidden
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dvlaRepo.linkState.collect { state ->
                when (state) {
                    DvlaLinkState.LINKED -> {
                        setUiStateToDefault()
                        fetchDriverSummary()
                        fetchCustomerSummary()
                    }

                    DvlaLinkState.UNLINKED,
                    DvlaLinkState.CHECKING -> _uiState.value = UiState.Hidden
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

    private fun setUiStateToDefault() {
        viewModelScope.launch {
            val drivingView = dvlaRepo.getSelectedDrivingView() ?: DrivingView.VEHICLES
            _uiState.update { state ->
                if (state is UiState.Default) state.copy(drivingView = drivingView)
                else UiState.Default(drivingView = drivingView)
            }
        }
    }

    private fun fetchDriverSummary() {
        viewModelScope.launch {
            updateLicenceState(LicenceSummaryUiState.Loading)

            val newState = when (val result = dvlaRepo.getDriverSummary()) {
                is Result.Success -> {
                    val licence = licenceMapper.toUiModel(result.value)
                    LicenceSummaryUiState.Success(licence)
                }
                else -> LicenceSummaryUiState.Error
            }

            updateLicenceState(newState)
        }
    }

    private fun fetchCustomerSummary() {
        viewModelScope.launch {
            updateVehiclesState(VehiclesSummaryUiState.Loading)

            val newState = when (val result = dvlaRepo.getCustomerSummary()) {
                is Result.Success -> {
                    val vehicles = result.value.vehicles.map { vehicleMapper.toUiModel(it) }
                    VehiclesSummaryUiState.Success(vehicles)
                }
                else -> VehiclesSummaryUiState.Error
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
