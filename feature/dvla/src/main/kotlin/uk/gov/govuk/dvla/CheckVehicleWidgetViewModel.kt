package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.dvla.data.DvlaRepo
import uk.gov.govuk.data.model.Result
import javax.inject.Inject

internal sealed interface CheckVehicleSheetState {
    val regNumber: String

    data class Input(
        override val regNumber: String = ""
    ) : CheckVehicleSheetState

    data class Loading(
        override val regNumber: String
    ) : CheckVehicleSheetState

    data class Error(
        override val regNumber: String,
        val error: CheckVehicleError
    ) : CheckVehicleSheetState

    data class Success(override val regNumber: String) : CheckVehicleSheetState
}

internal val CheckVehicleSheetState.canSubmit: Boolean
    get() = this !is CheckVehicleSheetState.Loading && regNumber.isNotBlank()

internal enum class CheckVehicleError {
    SEARCH_UNAVAILABLE,
    NUMBER_PLATE_NOT_FOUND
}

@HiltViewModel
internal class CheckVehicleWidgetViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SECTION = "Driving"
        private const val ACTION_SEARCH = "Search number plate"
        private const val ACTION_CANCEL_SEARCH = "Cancel search number plate"
        private const val MAX_REG_NUMBER_LENGTH = 7
        private const val HTTP_NOT_FOUND = 404
    }

    private val _sheetState = MutableStateFlow<CheckVehicleSheetState>(
        CheckVehicleSheetState.Input()
    )

    private var vehicleLookup: Job? = null

    val sheetState = _sheetState.asStateFlow()

    fun onSearchVehicleClicked(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }

    fun onRegistrationChanged(value: String) {
        val regNumber = value
            .uppercase()
            .filter { it.isAllowedRegNumberChar() }

        if (regNumber.count { it != ' ' } > MAX_REG_NUMBER_LENGTH) return

        _sheetState.value = CheckVehicleSheetState.Input(regNumber = regNumber)
    }
    fun onClearClicked() {
        _sheetState.value = CheckVehicleSheetState.Input()
    }

    fun onSubmitClicked(buttonText: String) {
        val state = _sheetState.value
        if (!state.canSubmit) return

        analyticsClient.widgetFunction(
            text = buttonText,
            section = SECTION,
            action = ACTION_SEARCH
        )

        val regNumber = state.regNumber.filterNot { it.isWhitespace() }

        _sheetState.value = CheckVehicleSheetState.Loading(
            regNumber = state.regNumber
        )

        viewModelScope.launch {
            val result = dvlaRepo.lookupVehicleByRegistration(
                state.regNumber.filterNot { it.isWhitespace() }
            )
            when (result) {
                is Result.Success -> {
                    _sheetState.value = CheckVehicleSheetState.Success(regNumber)
                }

                is Result.ServiceNotResponding -> showError(
                    if (result.code == HTTP_NOT_FOUND) {
                        CheckVehicleError.NUMBER_PLATE_NOT_FOUND
                    } else {
                        CheckVehicleError.SEARCH_UNAVAILABLE
                    }
                )

                else -> showError(CheckVehicleError.SEARCH_UNAVAILABLE)
            }
        }
    }

    fun onCancelClicked(buttonText: String) {
        analyticsClient.widgetFunction(
            text = buttonText,
            section = SECTION,
            action = ACTION_CANCEL_SEARCH
        )
        onSheetDismissed()
    }

    fun onSheetDismissed() {
        vehicleLookup?.cancel()
        _sheetState.value = CheckVehicleSheetState.Input()
    }

    private fun showError(error: CheckVehicleError) {
        _sheetState.update { CheckVehicleSheetState.Error(it.regNumber, error) }
    }
}

private fun Char.isAllowedRegNumberChar(): Boolean =
    this in 'A'..'Z' || this in '0'..'9' || this == ' '


