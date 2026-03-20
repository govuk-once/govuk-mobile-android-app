package uk.gov.govuk.notificationcentre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.notificationcentre.NotificationCentreRepo
import uk.gov.govuk.data.notificationcentre.model.Notification
import uk.gov.govuk.data.notificationcentre.model.UpdateNotificationRequestBody
import uk.gov.govuk.notificationcentre.navigation.NOTIFICATION_CENTRE_DETAIL_ID_ARG
import javax.inject.Inject

internal sealed class NotificationCentreDetailUiState {
    data object Loading: NotificationCentreDetailUiState()
    data class Loaded(val notification: Notification): NotificationCentreDetailUiState()
    data object Error: NotificationCentreDetailUiState()
    data object NotFound: NotificationCentreDetailUiState()
}


@HiltViewModel
internal class NotificationCentreDetailViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val notificationCentreRepo: NotificationCentreRepo,
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

    fun onLinkTap(url: String) {
        analyticsClient.notificationCentreUrlLaunched(url)
    }


    fun onTapRetry() {
        loadData()
    }

    fun onTapMarkUnread() {
        (_uiState.value as? NotificationCentreDetailUiState.Loaded)?.let {
            viewModelScope.launch {
                notificationCentreRepo.updateNotification(it.notification.id, UpdateNotificationRequestBody.Status.UNREAD)
            }
        }
    }

    private fun loadData() {
        savedStateHandle.get<String>(NOTIFICATION_CENTRE_DETAIL_ID_ARG)?.let { id ->
            viewModelScope.launch {
                val result = notificationCentreRepo.getSingleNotification(id)

                withContext(Dispatchers.Main) {
                    _uiState.value = when(result) {
                        is Result.Success -> {
                            val notification = result.value
                            if (notification != null) {
                                if (notification.isUnread) {
                                    viewModelScope.launch {
                                        notificationCentreRepo.updateNotification(notification.id,
                                            UpdateNotificationRequestBody.Status.READ)
                                    }
                                }
                                NotificationCentreDetailUiState.Loaded(notification)
                            } else {
                                NotificationCentreDetailUiState.NotFound
                            }
                        }

                        else -> NotificationCentreDetailUiState.Error
                    }
                }
            }
        }

    }
}