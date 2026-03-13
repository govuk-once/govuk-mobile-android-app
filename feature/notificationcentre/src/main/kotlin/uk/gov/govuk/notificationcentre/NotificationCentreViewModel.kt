package uk.gov.govuk.notificationcentre

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
import uk.gov.govuk.data.notificationcentre.model.NotificationGroups
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

internal sealed class NotificationCentreUiState {
    data object Loading: NotificationCentreUiState()
    data object Empty : NotificationCentreUiState()
    data object Error: NotificationCentreUiState()
    data object NoInternet: NotificationCentreUiState()
    data class Loaded(val notifications: NotificationGroups): NotificationCentreUiState()
}

@HiltViewModel
internal class NotificationCentreViewModel @Inject constructor(
    private val notificationCentreRepo: NotificationCentreRepo,
    private val analyticsClient: AnalyticsClient
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

    private fun loadData() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _uiState.value = NotificationCentreUiState.Loading
            }

            val notifications = notificationCentreRepo.getNotifications()
            withContext(Dispatchers.Main) {
                _uiState.value = when (notifications) {
                    is Result.Success -> {
                        if (notifications.value.isEmpty()) {
                            NotificationCentreUiState.Empty
                        } else {
                            val sorted = notifications.value.sortedBy { it.date }
                            val sevenDaysBack = Instant.now().minus(7, ChronoUnit.HOURS)
                            val groups = NotificationGroups(
                                sorted.filter { it.date >= sevenDaysBack },
                                sorted.filter { it.date < sevenDaysBack }
                            )
                            NotificationCentreUiState.Loaded(groups)
                        }
                    }
                    is Result.DeviceOffline -> NotificationCentreUiState.NoInternet
                    else -> NotificationCentreUiState.Error
                }
            }
        }
    }

}