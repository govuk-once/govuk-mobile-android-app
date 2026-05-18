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
import uk.gov.govuk.dvla.ui.model.VehicleSummaryMapper
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel
import javax.inject.Inject

internal sealed interface VehicleAndLicenceSummaryUiState {
    data object Hidden : VehicleAndLicenceSummaryUiState
    data object Loading : VehicleAndLicenceSummaryUiState
    data class Success(val vehicles: List<VehicleSummaryUiModel>) : VehicleAndLicenceSummaryUiState
    data object Error : VehicleAndLicenceSummaryUiState
}

@HiltViewModel
internal class VehicleAndLicenceSummaryViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo,
    private val mapper: VehicleSummaryMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<VehicleAndLicenceSummaryUiState>(VehicleAndLicenceSummaryUiState.Hidden)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dvlaRepo.linkState.collect { state ->
                when (state) {
                    DvlaLinkState.LINKED -> {
                        //                    fetchLicenceData()
                        // TODO: this is to demonstrate driver & customer summary endpoint call data,
                        //  until we decide which endpoint to use
//                    fetchDriverSummaryData()
                        fetchCustomerSummary()
                    }

                    DvlaLinkState.UNLINKED,
                    DvlaLinkState.CHECKING -> _uiState.value = VehicleAndLicenceSummaryUiState.Hidden
                }
            }
        }
    }

//    private fun fetchLicenceData() {
//        viewModelScope.launch {
//            _uiState.value = LicenceSummaryUiState.Loading
//
//            val result = dvlaRepo.getLicenceDetails()
//
//            _uiState.value = if (result is Result.Success)
//                LicenceSummaryUiState.Success(result.value) else LicenceSummaryUiState.Error
//        }
//    }

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
            _uiState.value = VehicleAndLicenceSummaryUiState.Loading

            when (val result = dvlaRepo.getCustomerSummary()) {
                is Result.Success -> {
                    val vehicleUiModels = result.value.vehicles.map { mapper.toUiModel(it) }
                    _uiState.value = VehicleAndLicenceSummaryUiState.Success(vehicles = vehicleUiModels)
                }
                else -> {
                    _uiState.value = VehicleAndLicenceSummaryUiState.Error
                }
            }
        }
    }
}
