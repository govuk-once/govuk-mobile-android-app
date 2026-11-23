package uk.govuk.app.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.govuk.app.local.LocalWidgetUiState.LocalAuthoritySelected
import uk.govuk.app.local.LocalWidgetUiState.NoLocalAuthority
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.domain.model.LocalAuthority

internal sealed class LocalWidgetUiState {
    internal data object NoLocalAuthority : LocalWidgetUiState()
    internal data class LocalAuthoritySelected(
        val localAuthority: LocalAuthority
    ) : LocalWidgetUiState()
}

@HiltViewModel
internal class LocalWidgetViewModel @Inject constructor(
    private val localRepo: LocalRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val EDIT_SECTION = "Homescreen"
        private const val ACTION_EDIT = "Edit"
        private const val ACTION_REMOVE = "Remove"
    }

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

    fun onEditClick(
        text: String
    ) {
        analyticsClient.buttonFunction(
            text = text,
            section = EDIT_SECTION,
            action = ACTION_EDIT
        )
    }

    fun onChangeClick(
        text: String
    ) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }

    fun onRemoveConfirmClick(
        text: String
    ) {
        analyticsClient.buttonFunction(
            text = text,
            section = SECTION,
            action = ACTION_REMOVE
        )
        viewModelScope.launch {
            localRepo.clear()
        }
    }
}
