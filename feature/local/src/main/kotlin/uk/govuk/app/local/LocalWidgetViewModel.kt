package uk.govuk.app.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.local.LocalWidgetUiState.LocalAuthoritySelected
import uk.govuk.app.local.LocalWidgetUiState.NoLocalAuthority
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.domain.model.LocalAuthority
import javax.inject.Inject

internal sealed class LocalWidgetUiState {
    internal data object NoLocalAuthority : LocalWidgetUiState()
    internal data class LocalAuthoritySelected(
        val localAuthority: LocalAuthority
    ): LocalWidgetUiState()
}

@HiltViewModel
internal class LocalWidgetViewModel @Inject constructor(
    localRepo: LocalRepo
): ViewModel() {

    private val _uiState: MutableStateFlow<LocalWidgetUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            localRepo.localAuthority.collect { localAuthority ->
                if (localAuthority != null) {
                    _uiState.value = LocalAuthoritySelected(localAuthority)
                } else {
                    _uiState.value = NoLocalAuthority
                }
            }
        }
    }
}