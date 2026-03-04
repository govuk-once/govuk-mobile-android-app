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
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.collections.listOf

internal sealed class NotificationCentreUiState {
    data object Loading: NotificationCentreUiState()
    data object Empty: NotificationCentreUiState()
    data object Error: NotificationCentreUiState()
    data class Loaded(val notifications: List<Notification>): NotificationCentreUiState()
}

data class Notification(val id: String, val title: String, val body: String, val unread: Boolean, val date: LocalDateTime) {
    companion object {
        val mockNotifications: List<Notification>
            get() {
                val referenceDate = LocalDateTime.of(2026, 3,5,13,2,49)

                return listOf(
                    Notification(
                        "1",
                        "Test 1 with a really really really long title that will surely be chopped off if we add enough filler text to the end so it goes to more than two lines",
                        "Body",
                        true,
                        referenceDate
                    ),
                    Notification(
                        "2",
                        "Test 2",
                        "Body 2 with a really really really really really large amount of text that will absolutely definitely make the text longer than it should be and so will get chopped off and ellipsized",
                        true,
                        referenceDate.minusDays(1)
                    ),
                    Notification("3", "Test 3", "Body 3", false,referenceDate.minusDays(2)
                    ),
                    Notification("4", "Test 4", "Body 4", true, referenceDate.minusDays(3)
                    ),
                )
            }
    }
}

@HiltViewModel
internal class NotificationCentreViewModel @Inject constructor(
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

    fun onTapRetry() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _uiState.value = NotificationCentreUiState.Loading
            }
            delay(3000)
            withContext(Dispatchers.Main) {
                _uiState.value = NotificationCentreUiState.Loaded(Notification.mockNotifications)
            }
        }
    }

}