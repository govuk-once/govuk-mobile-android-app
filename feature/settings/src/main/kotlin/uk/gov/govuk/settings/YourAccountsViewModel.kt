package uk.gov.govuk.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject

internal sealed interface YourAccountsUiState {
    data object Loading : YourAccountsUiState
    data object HasAddedAccounts : YourAccountsUiState
    data object NoAddedAccounts : YourAccountsUiState
}

@HiltViewModel
internal class YourAccountsViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "YourAccountsScreen"
        private const val SCREEN_NAME = "Your Accounts"
        private const val TITLE = "Your Accounts"
    }

    private val _uiState = MutableStateFlow<YourAccountsUiState>(YourAccountsUiState.NoAddedAccounts)
    val uiState = _uiState.asStateFlow()

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }
}