package uk.gov.govuk.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import javax.inject.Inject

@HiltViewModel
internal class NotificationsPromptWidgetViewModel @Inject constructor(
    private val notificationsProvider: NotificationsProvider,
    private val notificationsDataStore: NotificationsDataStore
) : ViewModel() {

    internal fun onClick() {
        viewModelScope.launch {
            notificationsDataStore.firstPermissionRequestCompleted()
        }
        notificationsProvider.requestPermission()
    }
}
