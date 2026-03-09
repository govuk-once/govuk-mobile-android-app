package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.dvla.data.DeviceIdProvider
import uk.gov.govuk.dvla.data.DvlaRepo
import javax.inject.Inject

@HiltViewModel
internal class DvlaViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo,
    private val deviceIdProvider: DeviceIdProvider
) : ViewModel() {

    sealed interface LinkingEvent {
        data object LinkComplete : LinkingEvent
    }

    private val _linkingEvent = MutableSharedFlow<LinkingEvent>()
    val linkingEvent = _linkingEvent.asSharedFlow()

    init {
        linkAccount()
    }

    private fun linkAccount() {
        viewModelScope.launch {
            // use temporarily for POC until we get linkingId for DVLA
            val tempId = deviceIdProvider.getDeviceId()
            dvlaRepo.linkAccount(tempId)

            // fake delay mimicking api call
            delay(3000)

            _linkingEvent.emit(LinkingEvent.LinkComplete)
        }
    }
}