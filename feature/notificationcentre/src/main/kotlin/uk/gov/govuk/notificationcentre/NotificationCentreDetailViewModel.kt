package uk.gov.govuk.notificationcentre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.notificationcentre.navigation.NOTIFICATION_CENTRE_DETAIL_ID_ARG
import javax.inject.Inject

internal sealed class NotificationCentreDetailUiState {
    data object Loading: NotificationCentreDetailUiState()
    data class Loaded(val notificationId: String): NotificationCentreDetailUiState()
}


@HiltViewModel
internal class NotificationCentreDetailViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "NotificationCentreDetailScreen"
        private const val SCREEN_NAME = "NotificationCentreDetail"
        private const val TITLE = "NotificationCentreDetailScreen"
    }

    private val _uiState: MutableStateFlow<NotificationCentreDetailUiState> = MutableStateFlow(
        NotificationCentreDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )

        loadData()
    }

    fun onTapRetry() {
        loadData()
    }

    private fun loadData() {
        savedStateHandle.get<String>(NOTIFICATION_CENTRE_DETAIL_ID_ARG)?.let { id ->
            viewModelScope.launch {
                delay(3000)
                withContext(Dispatchers.Main) {
                    _uiState.value = NotificationCentreDetailUiState.Loaded(id)
                }
            }
        }

    }

    fun onTapNotification(notification: Notification) {

    }
}