package uk.gov.govuk.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.messages.data.MessagesRepo
import uk.gov.govuk.messages.data.model.MessageGroups
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

internal sealed class MessagesUiState {
    data object Default: MessagesUiState()
    data object Loading: MessagesUiState()
    data object Empty : MessagesUiState()
    data object Error: MessagesUiState()
    data object NoInternet: MessagesUiState()
    data class Loaded(val notifications: MessageGroups): MessagesUiState()
}

@HiltViewModel
internal class MessagesViewModel @Inject constructor(
    private val messagesRepo: MessagesRepo,
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "MessagesScreen"
        private const val SCREEN_NAME = "Messages"
        private const val TITLE = "MessagesScreen"
    }

    private val _uiState: MutableStateFlow<MessagesUiState> =
        MutableStateFlow(MessagesUiState.Default)
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
            _uiState.value = MessagesUiState.Loading

            val notifications = messagesRepo.getMessages()
            _uiState.value = when (notifications) {
                is Result.Success -> {
                    if (notifications.value.isEmpty()) {
                        MessagesUiState.Empty
                    } else {
                        val sorted = notifications.value.sortedBy { it.date }
                        val sevenDaysBack = Instant.now().minus(7, ChronoUnit.DAYS)
                        val groups = MessageGroups(
                            sorted.filter { it.date >= sevenDaysBack },
                            sorted.filter { it.date < sevenDaysBack }
                        )
                        MessagesUiState.Loaded(groups)
                    }
                }

                is Result.DeviceOffline -> MessagesUiState.NoInternet
                else -> MessagesUiState.Error
            }
        }
    }
}