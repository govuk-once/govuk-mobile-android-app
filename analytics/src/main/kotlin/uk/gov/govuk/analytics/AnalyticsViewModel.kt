package uk.gov.govuk.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
internal class AnalyticsViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    fun onConsentGranted() {
        viewModelScope.launch {
            analyticsClient.enable()
        }
    }

    fun onConsentDenied() {
        viewModelScope.launch {
            analyticsClient.disable()
        }
    }
}
