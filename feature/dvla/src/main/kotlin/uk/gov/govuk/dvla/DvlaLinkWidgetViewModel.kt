package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
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

    private val _dvlaState = MutableStateFlow(DvlaLinkState.CHECKING)
    val dvlaState = _dvlaState.asStateFlow()

    fun checkStatus() {
        viewModelScope.launch {
            _dvlaState.value = DvlaLinkState.CHECKING

            val result = dvlaRepo.isAccountLinked()

            _dvlaState.value = if (result is Result.Success && result.value) {
                DvlaLinkState.LINKED
            } else {
                DvlaLinkState.UNLINKED
            }
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
