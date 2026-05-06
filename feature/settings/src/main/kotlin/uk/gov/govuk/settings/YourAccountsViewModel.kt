package uk.gov.govuk.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import uk.gov.govuk.data.model.Result.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

            when (result) {
                is Success -> {
                    _accountsUiState.value = LinkedAccountsUiState.Default
                }

                else -> {
                    _accountsUiState.value = LinkedAccountsUiState.Error
                }
            }
        }
    }

    fun resetError() {
        _accountsUiState.value = LinkedAccountsUiState.Default
    }

    fun onUnlinkCancelled(serviceName: String, buttonLabel: String) {
        analyticsClient.buttonClick(
            text = "${serviceName.uppercase()} $buttonLabel",
            external = false,
            section = SECTION
        )
    }
}