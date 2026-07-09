package uk.gov.govuk.dvla

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.navigation.ARG_VEHICLE_ID
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
    private val mapper: VehicleDetailsMapper,
    configRepo: ConfigRepo
) : ViewModel() {

    private companion object {
        const val SCREEN_CLASS = "VehicleDetailsScreen"
    }

    private val _uiState = MutableStateFlow<VehicleDetailsUiState>(VehicleDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val dvlaUrls = configRepo.dvlaUrls

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

    fun onExternalButtonClicked(text: String, url: String, section: String) {
        analyticsClient.buttonClick(
            text = text,
            url = url,
            external = true,
            section = section
        )
    }

    private fun fetchVehicleDetails() {
        val vehicleId: Int = savedStateHandle[ARG_VEHICLE_ID] ?: return

        viewModelScope.launch {
            when (val result = dvlaRepo.getVehicleDetails(vehicleId)) {
                is Result.Success -> {
                    val vehicleDetails = mapper.toUiModel(result.value, dvlaUrls)
                    _uiState.value = VehicleDetailsUiState.Success(vehicleDetails)
                }

                else -> _uiState.value = VehicleDetailsUiState.Error
            }
        }
    }
}
