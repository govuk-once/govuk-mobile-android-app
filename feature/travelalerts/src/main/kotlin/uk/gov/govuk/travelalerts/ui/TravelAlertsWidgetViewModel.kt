package uk.gov.govuk.travelalerts.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import javax.inject.Inject

@HiltViewModel
class TravelAlertsWidgetViewModel @Inject constructor(private val travelAlertsRepo: TravelAlertsRepo) : ViewModel() {
    sealed class State {
        data object Loading: State()
        data object Loaded: State()
        data object Error: State()
    }

    private val _uiState: MutableStateFlow<State> =
        MutableStateFlow(State.Loading)
    val uiState = _uiState.asStateFlow()

    fun onPageView() {
        viewModelScope.launch {
            _uiState.value = State.Loading
            val res = travelAlertsRepo.getGroups()
            when (res) {
                is Result.Success<*> -> _uiState.value = State.Loaded
                else -> _uiState.value = State.Error
            }
        }
    }
}