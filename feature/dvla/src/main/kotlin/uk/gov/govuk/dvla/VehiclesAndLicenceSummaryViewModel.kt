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
import uk.gov.govuk.dvla.ui.model.VehiclesSummaryUiState
import javax.inject.Inject

@HiltViewModel
internal class VehiclesAndLicenceSummaryViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo,
    private val mapper: VehicleSummaryMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Hidden)
    val uiState = _uiState.asStateFlow()

    private val _vehiclesSummaryUiState =
        MutableStateFlow<VehiclesSummaryUiState>(VehiclesSummaryUiState.Loading)
    val vehiclesSummaryUiState = _vehiclesSummaryUiState.asStateFlow()

    private val _licenceSummaryUiState =
        MutableStateFlow<LicenceSummaryUiState>(LicenceSummaryUiState.Loading)
    val licenceSummaryUiState = _licenceSummaryUiState.asStateFlow()

    init {
        viewModelScope.launch {
            dvlaRepo.linkState.collect { state ->
                when (state) {
                    DvlaLinkState.LINKED -> {
                        setUiStateToDefault()
                        fetchDriverSummary()
                        fetchCustomerSummary()
                        createListCancelCheckCode()
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
            _uiState.value = UiState.Default(drivingView = drivingView)
        }
    }

    private fun setUiStateToDefault() {
        viewModelScope.launch {
            val drivingView = dvlaRepo.getSelectedDrivingView() ?: DrivingView.VEHICLES // Default to 'VEHICLES'
            _uiState.value = UiState.Default(drivingView = drivingView)
        }
    }

    private fun fetchDriverSummary() {
        viewModelScope.launch {
            // TODO: this is to demonstrate the endpoint call data, until we decide which endpoint to use
            val result = dvlaRepo.getDriverSummary()
        }
    }

    private fun fetchCustomerSummary() {
        viewModelScope.launch {
            _vehiclesSummaryUiState.value = VehiclesSummaryUiState.Loading
            _licenceSummaryUiState.value = LicenceSummaryUiState.Loading

            when (val result = dvlaRepo.getCustomerSummary()) {
                is Result.Success -> {
                    val vehicleList = result.value.vehicles.map { mapper.toUiModel(it) }
                    _vehiclesSummaryUiState.value = VehiclesSummaryUiState.Success(vehicles = vehicleList)
                }

                else -> {
                    _vehiclesSummaryUiState.value = VehiclesSummaryUiState.Error
                    _licenceSummaryUiState.value = LicenceSummaryUiState.Error
                }
            }
        }
    }

    private fun createListCancelCheckCode() {
        // TODO: this is to demonstrate the endpoint call data, to be removed
        viewModelScope.launch {

            // get codes
            val codesResult = dvlaRepo.getCheckCodes()
            if (codesResult !is Result.Success) {
                return@launch
            }

            // create code
            val createResult = dvlaRepo.createCheckCode()
            if (createResult !is Result.Success) {
                return@launch
            }

            val newCode = createResult.value
            val tokenIdToCancel = newCode.tokenId

            // cancel code
            val cancelResult = dvlaRepo.cancelCheckCode(tokenIdToCancel)
            if (cancelResult !is Result.Success) {
                return@launch
            }
        }
    }
}
