package uk.gov.govuk.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.gov.govuk.notifications.data.NotificationsRepo
import javax.inject.Inject

@HiltViewModel
internal class NotificationsPromptWidgetViewModel @Inject constructor(
    private val notificationsRepo: NotificationsRepo
) : ViewModel() {

    internal fun onClick() {
        viewModelScope.launch {
            notificationsRepo.firstPermissionRequestCompleted()
            notificationsRepo.requestPermission()
        }
    }
}
