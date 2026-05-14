package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.dvla.domain.LicenceDetails
import javax.inject.Inject

internal sealed interface LicenceSummaryUiState {
    data object Hidden : LicenceSummaryUiState
    data object Loading : LicenceSummaryUiState
    data class Success(val licence: LicenceDetails) : LicenceSummaryUiState
    data object Error : LicenceSummaryUiState
}

@HiltViewModel
internal class LicenceSummaryViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<LicenceSummaryUiState>(LicenceSummaryUiState.Hidden)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dvlaRepo.isLinked.collect { isLinked ->
                if (isLinked) {
//                    fetchLicenceData()
                    // TODO: this is to demonstrate driver & customer summary endpoint call data,
                    //  until we decide which endpoint to use
//                    fetchDriverSummaryData()
                    fetchCustomerSummary()
                } else {
                    _uiState.value = LicenceSummaryUiState.Hidden
                }
            }
        }
    }

    private fun fetchLicenceData() {
        viewModelScope.launch {
            _uiState.value = LicenceSummaryUiState.Loading

            val result = dvlaRepo.getLicenceDetails()

            _uiState.value = if (result is Result.Success)
                LicenceSummaryUiState.Success(result.value) else LicenceSummaryUiState.Error
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
            _uiState.value = LicenceSummaryUiState.Loading
            val result = dvlaRepo.getCustomerSummary()

            // TODO: this is to demonstrate the endpoint call data, until we decide which endpoint to use
            if (BuildConfig.DEBUG) {
                when (result) {
                    is Result.Success -> println("CustomerSummary: SUCCESS: ${result.value}")
                    else -> println("CustomerSummary: ERROR - Failed to fetch customer summary")
                }
            }
        }
    }
}
