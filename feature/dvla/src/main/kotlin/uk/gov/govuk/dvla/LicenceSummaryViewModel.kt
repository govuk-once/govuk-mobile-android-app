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

internal sealed interface LicenceSummaryState {
    data object Hidden : LicenceSummaryState
    data object Loading : LicenceSummaryState
    data class Success(val licence: LicenceDetails) : LicenceSummaryState
    data object Error : LicenceSummaryState
}

@HiltViewModel
internal class LicenceSummaryViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<LicenceSummaryState>(LicenceSummaryState.Hidden)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dvlaRepo.isLinked.collect { isLinked ->
                if (isLinked) {
                    fetchLicenceData()
                    // TODO: this is to demonstrate driver & customer summary endpoint call data,
                    //  until we decide which endpoint to use
                    fetchDriverSummaryData()
                    fetchCustomerSummaryData()

                    // TODO: this is to demonstrate share code endpoint call data, remove
                    createListCancelShareCode()
                } else {
                    _uiState.value = LicenceSummaryState.Hidden
                }
            }
        }
    }

    private fun fetchLicenceData() {
        viewModelScope.launch {
            _uiState.value = LicenceSummaryState.Loading

            val result = dvlaRepo.getLicenceDetails()

            _uiState.value = if (result is Result.Success)
                LicenceSummaryState.Success(result.value) else LicenceSummaryState.Error
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

    private fun fetchCustomerSummaryData() {
        viewModelScope.launch {
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

    private fun createListCancelShareCode() {
        // TODO: this is to demonstrate the endpoint call data, to be removed
        viewModelScope.launch {
            println("------------------ DVLA SHARE CODES DEMO ------------------")

            suspend fun logCodes(step: String) {
                when (val result = dvlaRepo.getShareCodes()) {
                    is Result.Success -> {
                        val codes = result.value
                        val codesString = codes.joinToString { "${it.tokenId} [${it.validity.name}]" }
                        println("DVLA Share codeS: $step. Total codes: ${codes.size}. Current codes: [$codesString]")
                    }
                    else -> {
                        println("DVLA Share codeS: $step. Failed to fetch codes.")
                    }
                }
            }

            // list existing codes
            logCodes("Fetching existing codes")

            // create code
            val createResult = dvlaRepo.createShareCode()
            if (createResult !is Result.Success) {
                println("DVLA Share codes: Failed to create code")
                return@launch
            }

            val newCode = createResult.value
            val tokenIdToCancel = newCode.tokenId
            println("DVLA Share codes: Created share code with id: $tokenIdToCancel")

            // list codes after creation
            logCodes("Fetching after creation")

            // cancel
            val cancelResult = dvlaRepo.cancelShareCode(tokenIdToCancel)
            if (cancelResult is Result.Success) {
                println("DVLA Share codes: Cancelled share code: $tokenIdToCancel")
            } else {
                println("DVLA Share codes: failed to cancel code")
            }

            // list codes after cancelling
            logCodes("Fetching after cancellation")
        }
    }
}
