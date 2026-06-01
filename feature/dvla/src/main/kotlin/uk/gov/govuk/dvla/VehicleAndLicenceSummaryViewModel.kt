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
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiModel
import uk.gov.govuk.dvla.ui.model.VehicleSummaryMapper
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel
import javax.inject.Inject

// top level state for the widget
internal sealed interface VehicleAndLicenceSummaryUiState {
    data object Hidden : VehicleAndLicenceSummaryUiState
    data class Content(
        val vehicleState: VehicleUiState = VehicleUiState.Loading,
        val licenceState: LicenceUiState = LicenceUiState.Loading
    ) : VehicleAndLicenceSummaryUiState
}

// states for vehicle/licence summaries
internal sealed interface VehicleUiState {
    data object Loading : VehicleUiState
    data class Success(val vehicles: List<VehicleSummaryUiModel>) : VehicleUiState
    data object Error : VehicleUiState
}

internal sealed interface LicenceUiState {
    data object Loading : LicenceUiState
    data class Success(val licence: LicenceSummaryUiModel) : LicenceUiState
    data object Error : LicenceUiState
}

@HiltViewModel
internal class VehicleAndLicenceSummaryViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo,
    private val mapper: VehicleSummaryMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        if (dvlaRepo.linkState.value == DvlaLinkState.LINKED) VehicleAndLicenceSummaryUiState.Content()
        else VehicleAndLicenceSummaryUiState.Hidden
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dvlaRepo.linkState.collect { state ->
                when (state) {
                    DvlaLinkState.LINKED -> {
                        _uiState.value = VehicleAndLicenceSummaryUiState.Content()

                        fetchLicenceData()
                        // TODO: this is to demonstrate driver & customer summary endpoint call data,
                        //  until we decide which endpoint to use
                        fetchDriverSummary()
                        fetchCustomerSummary()
                    }

                    DvlaLinkState.UNLINKED,
                    DvlaLinkState.CHECKING -> _uiState.value = VehicleAndLicenceSummaryUiState.Hidden
                }
            }
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

    private fun fetchDriverSummary() {
        viewModelScope.launch {
            updateLicenceState(LicenceUiState.Loading)

            val newState = when (val result = dvlaRepo.getDriverSummary()) {
                is Result.Success -> {
                    if (BuildConfig.DEBUG) println("DriverSummary: SUCCESS: ${result.value}")
                    // TODO map result.value to LicenceSummaryUiModel
                    LicenceUiState.Error
                }
                else -> {
                    if (BuildConfig.DEBUG) println("DriverSummary: ERROR - Failed to fetch driver summary")
                    LicenceUiState.Error
                }
            }

            updateLicenceState(newState)
        }
    }

    private fun fetchCustomerSummary() {
        viewModelScope.launch {
            updateVehicleState(VehicleUiState.Loading)

            val newState = when (val result = dvlaRepo.getCustomerSummary()) {
                is Result.Success -> VehicleUiState.Success(result.value.vehicles.map {
                    mapper.toUiModel(it)
                })

                else -> VehicleUiState.Error
            }

            updateVehicleState(newState)
        }
    }

    private fun updateVehicleState(newState: VehicleUiState) {
        _uiState.update { state ->
            if (state is VehicleAndLicenceSummaryUiState.Content) state.copy(vehicleState = newState) else state
        }
    }

    private fun updateLicenceState(newState: LicenceUiState) {
        _uiState.update { state ->
            if (state is VehicleAndLicenceSummaryUiState.Content) state.copy(licenceState = newState) else state
        }
    }
}
