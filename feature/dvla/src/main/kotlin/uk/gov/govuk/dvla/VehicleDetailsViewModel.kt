package uk.gov.govuk.dvla

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.navigation.ARG_VEHICLE_REGISTRATION
import uk.gov.govuk.dvla.ui.model.VehicleDetailsUiModel
import uk.gov.govuk.dvla.ui.model.VehicleDetailsMapper
import javax.inject.Inject

internal sealed interface VehicleDetailsUiState {
    data object Loading : VehicleDetailsUiState
    data class Success(val details: VehicleDetailsUiModel) : VehicleDetailsUiState
    data object Error : VehicleDetailsUiState
}

@HiltViewModel
internal class VehicleDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dvlaRepo: DvlaRepo,
    private val analyticsClient: AnalyticsClient,
    private val mapper: VehicleDetailsMapper
) : ViewModel() {

    private companion object {
        const val SCREEN_CLASS = "VehicleDetailsScreen"
        const val SECTION = "Driving"
        const val BACK_BUTTON = "Back Button"
    }

    private val _uiState = MutableStateFlow<VehicleDetailsUiState>(VehicleDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchVehicleDetails()
    }

    fun onPageView(title: String) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_CLASS,
            title = title
        )
    }

    fun onBackClicked() {
        analyticsClient.buttonClick(
            text = BACK_BUTTON,
            section = SECTION
        )
    }

    private fun fetchVehicleDetails() {
        // TODO temporarily get details from summary endpoint until details endpoint is live
        val vehicleRegistration: String = savedStateHandle[ARG_VEHICLE_REGISTRATION] ?: return
        viewModelScope.launch {
            when (val result = dvlaRepo.getCustomerSummary()) {
                is uk.gov.govuk.data.model.Result.Success -> {
                    val vehicle = result.value.vehicles[0]
                    val vehicleDetails = mapper.toDetailsUiModel(vehicle)
                    _uiState.value = VehicleDetailsUiState.Success(vehicleDetails)
                }

                else -> _uiState.value = VehicleDetailsUiState.Error
            }
        }
    }
}
