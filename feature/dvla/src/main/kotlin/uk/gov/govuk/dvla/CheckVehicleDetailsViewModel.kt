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
        const val SCREEN_NAME = "VehicleDetailsScreenSearchResult"
        const val SECTION = "Driving"
        const val CLOSE_TEXT = "Back"
        const val SEARCH_TEXT = "Search"
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

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = SCREEN_NAME
        )
    }

    fun onCloseClicked() {
        analyticsClient.buttonClick(
            text = CLOSE_TEXT,
            section = SECTION
        )
    }

    fun onSearchClicked() {
        analyticsClient.buttonClick(
            text = SEARCH_TEXT,
            section = SECTION
        )
    }

    fun onMenuItemClicked(text: String, url: String) {
        analyticsClient.menuItemClick(
            text = text,
            external = true,
            section = SECTION,
            url = url
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
