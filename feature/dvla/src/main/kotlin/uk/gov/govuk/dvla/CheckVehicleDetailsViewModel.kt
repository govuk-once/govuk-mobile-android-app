package uk.gov.govuk.dvla

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.mapper.CheckVehicleDetailsMapper
import uk.gov.govuk.dvla.navigation.ARG_REG_NUMBER
import uk.gov.govuk.dvla.ui.model.VehicleDetailsUiModel

internal sealed interface CheckVehicleDetailsUiState {
    data class Success(val details: VehicleDetailsUiModel) : CheckVehicleDetailsUiState
    data object Error : CheckVehicleDetailsUiState
}

@HiltViewModel
internal class CheckVehicleDetailsViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val mapper: CheckVehicleDetailsMapper,
    private val savedStateHandle: SavedStateHandle,
    private val dvlaRepo: DvlaRepo,
    private val configRepo: ConfigRepo
) : ViewModel() {

    private companion object {
        const val SCREEN_CLASS = "VehicleDetailsScreen"
    }

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState = _uiState.asStateFlow()

    private fun createInitialState(): CheckVehicleDetailsUiState {
        val regNumber: String = savedStateHandle[ARG_REG_NUMBER]
            ?: return CheckVehicleDetailsUiState.Error
        val vehicle = dvlaRepo.findVehicleEnquiryDetails(regNumber)
            ?: return CheckVehicleDetailsUiState.Error
        return CheckVehicleDetailsUiState.Success(
            details = mapper.toUiModel(
                vehicle = vehicle,
                dvlaUrls = configRepo.dvlaUrls
            )
        )
    }

    fun onPageView(title: String) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = title,
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
}
