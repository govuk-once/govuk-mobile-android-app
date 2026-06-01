package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.domain.DvlaLinkState
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiState
import uk.gov.govuk.dvla.ui.model.UiState
import uk.gov.govuk.dvla.ui.model.VehicleSummaryMapper
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiState
import javax.inject.Inject

@HiltViewModel
internal class VehicleAndLicenceSummaryViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo,
    private val mapper: VehicleSummaryMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Hidden)
    val uiState = _uiState.asStateFlow()

    private val _vehicleSummaryUiState =
        MutableStateFlow<VehicleSummaryUiState>(VehicleSummaryUiState.Loading)
    val vehicleSummaryUiState = _vehicleSummaryUiState.asStateFlow()

    private val _licenceSummaryUiState =
        MutableStateFlow<LicenceSummaryUiState>(LicenceSummaryUiState.Loading)
    val licenceSummaryUiState = _licenceSummaryUiState.asStateFlow()

    init {
        viewModelScope.launch {
            dvlaRepo.linkState.collect { state ->
                when (state) {
                    DvlaLinkState.LINKED -> {
                        setUiStateToDefault()
                        fetchLicenceData()
                        // TODO: this is to demonstrate driver & customer summary endpoint call data,
                        //  until we decide which endpoint to use
                        fetchDriverSummaryData()
                        fetchCustomerSummary()
                    }

                    DvlaLinkState.UNLINKED,
                    DvlaLinkState.CHECKING -> _uiState.value = UiState.Hidden
                }
            }
        }
    }

    fun onVehicleSelected() {
        setSelectedDrivingView(drivingView = DrivingView.VEHICLE)
    }

    fun onLicenceSelected() {
        setSelectedDrivingView(drivingView = DrivingView.LICENCE)
    }

    private fun setSelectedDrivingView(drivingView: DrivingView) {
        viewModelScope.launch {
            dvlaRepo.setSelectedDrivingView(drivingView = drivingView)
            _uiState.value = UiState.Default(drivingView = drivingView)
        }
    }

    private fun setUiStateToDefault() {
        viewModelScope.launch {
            val drivingView = dvlaRepo.getSelectedDrivingView() ?: DrivingView.VEHICLE // Default to 'VEHICLE'
            _uiState.value = UiState.Default(drivingView = drivingView)
        }
    }

    private fun fetchLicenceData() {
        viewModelScope.launch {
            val result = dvlaRepo.getLicenceDetails()

            // TODO: this is to demonstrate the endpoint call data, until we decide which endpoint to use
            if (BuildConfig.DEBUG) {
                when (result) {
                    is Result.Success -> println("Licence data: SUCCESS: ${result.value}")
                    else -> println("Licence data: ERROR - Failed to fetch Licence data")
                }
            }
        }
    }

    private fun fetchDriverSummaryData() {
        viewModelScope.launch {
            val result = dvlaRepo.getDriverSummary()

            // TODO: this is to demonstrate the endpoint call data, until we decide which endpoint to use
            if (BuildConfig.DEBUG) {
                when (result) {
                    is Result.Success -> println("DriverSummary: SUCCESS: ${result.value}")
                    else -> println("DriverSummary: ERROR - Failed to fetch driver summary")
                }
            }
        }
    }

    private fun fetchCustomerSummary() {
        viewModelScope.launch {
            _vehicleSummaryUiState.value = VehicleSummaryUiState.Loading
            _licenceSummaryUiState.value = LicenceSummaryUiState.Loading

            when (val result = dvlaRepo.getCustomerSummary()) {
                is Result.Success -> {
                    val vehicleList = result.value.vehicles.map { mapper.toUiModel(it) }
                    _vehicleSummaryUiState.value = VehicleSummaryUiState.Success(vehicles = vehicleList)
                }

                else -> {
                    _vehicleSummaryUiState.value = VehicleSummaryUiState.Error
                    _licenceSummaryUiState.value = LicenceSummaryUiState.Error
                }
            }
        }
    }
}
