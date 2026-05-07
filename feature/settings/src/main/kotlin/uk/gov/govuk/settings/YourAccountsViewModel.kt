package uk.gov.govuk.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import uk.gov.govuk.data.model.Result.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.settings.domain.LinkedAccountsRepo
import uk.gov.govuk.settings.ui.model.LinkedAccountUiModel
import javax.inject.Inject

@HiltViewModel
internal class YourAccountsViewModel @Inject constructor(
    private val linkedAccountsRepo: LinkedAccountsRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SECTION = "Settings"
    }

    val linkedAccounts: StateFlow<List<LinkedAccountUiModel>> =
        linkedAccountsRepo.getLinkedAccounts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _accountsUiState =
        MutableStateFlow<LinkedAccountsUiState>(LinkedAccountsUiState.Default)
    val accountsUiState = _accountsUiState.asStateFlow()

    // error is a one-off event instead of a state, we need to navigate away to a full screen 'curtain' screen
    // and prevent navigation loop when the full screen error is dismissed
    private val _errorEvent = Channel<Unit>(Channel.BUFFERED)
    val errorEvent = _errorEvent.receiveAsFlow()

    fun onRemoveIconClicked(serviceName: String) {
        analyticsClient.buttonFunction(
            text = "$serviceName unlink",
            section = SECTION,
            action = "action"
        )
    }

    fun unlinkAccount(serviceName: String, buttonLabel: String) {
        analyticsClient.buttonClick(
            text = "${serviceName.uppercase()} $buttonLabel",
            external = false,
            section = SECTION
        )

        viewModelScope.launch {
            _accountsUiState.value = LinkedAccountsUiState.Unlinking
            val result = linkedAccountsRepo.unlinkAccount(serviceName)
            _accountsUiState.value = LinkedAccountsUiState.Default

            if (result !is Success) {
                _errorEvent.send(Unit)
            }
        }
    }

    fun onUnlinkCancelled(serviceName: String, buttonLabel: String) {
        analyticsClient.buttonClick(
            text = "${serviceName.uppercase()} $buttonLabel",
            external = false,
            section = SECTION
        )
    }
}