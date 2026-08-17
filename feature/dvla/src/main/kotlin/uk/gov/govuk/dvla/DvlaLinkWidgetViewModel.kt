package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.dvla.data.DvlaRepo
import javax.inject.Inject

@HiltViewModel
internal class DvlaLinkWidgetViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SECTION = "account link"
    }

    val dvlaState: Flow<ServiceLinkStatus> = dvlaRepo.linkState

    fun checkStatus() {
        viewModelScope.launch {
            // if linked don't check again
            if (dvlaRepo.currentLinkState == ServiceLinkStatus.LINKED) return@launch

            // link state is checked/updated by the repo and observed in DvlaLinkHeader, ui is updated accordingly
            dvlaRepo.refreshLinkStatus()
        }
    }

    fun onLinkCardClicked(text: String) {
        analyticsClient.cardClick(
            text = text,
            external = false,
            section = SECTION
        )
    }
}
