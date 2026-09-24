package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject

internal sealed interface CheckVehicleSheetState {
    val registration: String

    data class Input(
        override val registration: String = ""
    ) : CheckVehicleSheetState

    data class Loading(
        override val registration: String
    ) : CheckVehicleSheetState

    data class Error(
        override val registration: String,
        val error: CheckVehicleError
    ) : CheckVehicleSheetState
}

internal enum class CheckVehicleError {
    SEARCH_UNAVAILABLE,
    NUMBER_PLATE_NOT_FOUND
}

@HiltViewModel
internal class CheckVehicleWidgetViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SECTION = "Driving"
        private const val MAX_REGISTRATION_LENGTH = 7
    }

    private val _sheetState = MutableStateFlow<CheckVehicleSheetState>(
        CheckVehicleSheetState.Input()
    )

    val sheetState = _sheetState.asStateFlow()

    fun onSearchVehicleClicked(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }

    fun onRegistrationChanged(value: String) {
        val registration = value
            .uppercase()
            .filter { character ->
                character.isLetterOrDigit() || character.isWhitespace()
            }
            .takeRegistrationCharacters(MAX_REGISTRATION_LENGTH)
        _sheetState.value = CheckVehicleSheetState.Input(
            registration = registration
        )
    }

    fun onClearClicked() {
        _sheetState.value = CheckVehicleSheetState.Input()
    }

    fun onSubmitClicked() {
        val registration = _sheetState.value.registration
            .filterNot(Char::isWhitespace)
        if (
            registration.isBlank() ||
            _sheetState.value is CheckVehicleSheetState.Loading
        ) {
            return
        }

        /*
        * API submission is coming in the next ticket.
        *
        * Do not switch permanently to Loading yet, otherwise the sheet will
        * remain loading with no API result to move it to another state.
        */
    }

    fun onSheetDismissed() {
        _sheetState.value = CheckVehicleSheetState.Input()
    }

    fun showSearchUnavailableError() {
        _sheetState.update {
            CheckVehicleSheetState.Error(
                registration = it.registration,
                error = CheckVehicleError.SEARCH_UNAVAILABLE
            )
        }
    }

    fun showNumberPlateNotFoundError() {
        _sheetState.update {
            CheckVehicleSheetState.Error(
                registration = it.registration,
                error = CheckVehicleError.NUMBER_PLATE_NOT_FOUND
            )
        }
    }


}

private fun String.takeRegistrationCharacters(maxCharacters: Int): String {
    val result = StringBuilder()
    var characterCount = 0
    forEach { character ->
        when {
            character.isWhitespace() -> {
                if (
                    result.isNotEmpty() &&
                    result.last() != ' '
                ) {
                    result.append(' ')
                }
            }
                characterCount < maxCharacters -> {
            result.append(character)
            characterCount++
        }
        }
    }
    return result.toString()
}
