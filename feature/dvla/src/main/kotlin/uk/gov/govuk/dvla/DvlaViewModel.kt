package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.data.DeviceIdProvider
import uk.gov.govuk.dvla.data.DvlaRepo
import javax.inject.Inject

@HiltViewModel
internal class DvlaViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo,
    private val deviceIdProvider: DeviceIdProvider
) : ViewModel() {

    sealed interface LinkingEvent {
        data object LinkComplete : LinkingEvent
    }

    sealed interface UiState {
        data object Loading : UiState
        data object Error : UiState
    }

    private val _linkingEvent = MutableSharedFlow<LinkingEvent>()
    val linkingEvent = _linkingEvent.asSharedFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        linkAccount()
    }

    private fun linkAccount() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // use temporarily for POC until we get linkingId for DVLA
            val tempId = deviceIdProvider.getDeviceId()
            if (dvlaRepo.linkAccount(tempId) is Result.Success) {
                _linkingEvent.emit(LinkingEvent.LinkComplete)
            } else {
                _uiState.value = UiState.Error
            }
        }
    }
}