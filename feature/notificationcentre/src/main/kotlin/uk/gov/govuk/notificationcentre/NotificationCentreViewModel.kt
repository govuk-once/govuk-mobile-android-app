package uk.gov.govuk.notificationcentre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.notificationcentre.NotificationCentreRepo
import uk.gov.govuk.data.notificationcentre.model.Notification
import uk.gov.govuk.dvla.data.DvlaRepo
import javax.inject.Inject

internal sealed class NotificationCentreUiState {
    data object Loading: NotificationCentreUiState()
    data class Empty(val linkingId: String?): NotificationCentreUiState()
    data object Error: NotificationCentreUiState()
    data class Loaded(val notifications: List<Notification>, val linkingId: String?): NotificationCentreUiState()
}

@HiltViewModel
internal class NotificationCentreViewModel @Inject constructor(
    private val notificationCentreRepo: NotificationCentreRepo,
    private val analyticsClient: AnalyticsClient,
    private val dvlaRepo: DvlaRepo
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "NotificationCentreScreen"
        private const val SCREEN_NAME = "NotificationCentre"
        private const val TITLE = "NotificationCentreScreen"
    }

    private val _uiState: MutableStateFlow<NotificationCentreUiState> = MutableStateFlow(NotificationCentreUiState.Loading)
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
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _uiState.value = NotificationCentreUiState.Loading
            }

            val linkingId = when (val customerSummary = dvlaRepo.getCustomerSummary()) {
                is Result.Success -> {
                    customerSummary.value.linkingId
                }
                else -> null
            }

            val notifications = notificationCentreRepo.getNotifications()
            withContext(Dispatchers.Main) {
                _uiState.value = when (notifications) {
                    is Result.Success -> {
                        if (notifications.value.isEmpty()) {
                            NotificationCentreUiState.Empty(linkingId)
                        } else {
                            NotificationCentreUiState.Loaded(notifications.value, linkingId)
                        }
                    }
                    else -> NotificationCentreUiState.Error
                }
            }
        }
    }

}