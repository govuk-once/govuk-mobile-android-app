package uk.gov.govuk.messages

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.messages.data.MessagesRepo
import uk.gov.govuk.messages.data.model.Notification
import uk.gov.govuk.messages.data.model.UpdateNotificationRequestBody
import uk.gov.govuk.messages.navigation.MESSAGES_DETAIL_ID_ARG
import javax.inject.Inject

internal sealed class MessagesDetailUiState {
    data object Default: MessagesDetailUiState()
    data object Loading: MessagesDetailUiState()
    data class Loaded(val message: Notification, val showDeleteConfirmation: Boolean): MessagesDetailUiState()
    data object Error: MessagesDetailUiState()
    data object NoInternet: MessagesDetailUiState()
}


@HiltViewModel
internal class MessagesDetailViewModel @Inject constructor(
    private val messagesRepo: MessagesRepo,
    private val analyticsClient: AnalyticsClient,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "MessagesDetailScreen"
        private const val SCREEN_NAME = "MessagesDetail"
        private const val TITLE = "MessagesDetailScreen"
    }

    private val _uiState: MutableStateFlow<MessagesDetailUiState> = MutableStateFlow(
        MessagesDetailUiState.Default)
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
        analyticsClient.messagesUrlLaunched(url)
    }

    fun onTapMarkUnread() {
        (_uiState.value as? MessagesDetailUiState.Loaded)?.let {
            analyticsClient.messagesMarkUnread()
            viewModelScope.launch {
                messagesRepo.updateMessage(it.message.id, UpdateNotificationRequestBody.Status.UNREAD)
            }
        }
    }

    fun onTapDelete() {
        (_uiState.value as? MessagesDetailUiState.Loaded)?.let { state: MessagesDetailUiState.Loaded ->
            _uiState.update { state.copy(showDeleteConfirmation = true) }
            analyticsClient.messagesDelete()
        }
    }

    fun onConfirmDelete() {
        (_uiState.value as? MessagesDetailUiState.Loaded)?.let { state: MessagesDetailUiState.Loaded ->
            _uiState.update { state.copy(showDeleteConfirmation = false) }
            analyticsClient.messagesConfirmDelete()
            viewModelScope.launch {
                messagesRepo.deleteMessage(state.message.id)
            }
        }
    }

    fun onCancelDelete() {
        (_uiState.value as? MessagesDetailUiState.Loaded)?.let { state: MessagesDetailUiState.Loaded ->
            _uiState.update { state.copy(showDeleteConfirmation = false) }
            analyticsClient.messagesCancelDelete()
        }
    }

    private fun loadData() {
        savedStateHandle.get<String>(MESSAGES_DETAIL_ID_ARG)?.let { id ->
            viewModelScope.launch {
                _uiState.value = MessagesDetailUiState.Loading
                val result = messagesRepo.getSingleMessage(id)

                _uiState.value = when(result) {
                    is Result.Success -> {
                        val notification = result.value
                        if (notification != null) {
                            markUnreadIfNecessary(notification)
                            MessagesDetailUiState.Loaded(notification, false)
                        } else {
                            MessagesDetailUiState.Error
                        }
                    }
                    is Result.DeviceOffline -> MessagesDetailUiState.NoInternet
                    else -> MessagesDetailUiState.Error
                }
            }
        }

    }

    private fun markUnreadIfNecessary(message: Notification) {
        if (message.isUnread) {
            viewModelScope.launch {
                messagesRepo.updateMessage(
                    message.id,
                    UpdateNotificationRequestBody.Status.READ
                )
            }
        }
    }
}