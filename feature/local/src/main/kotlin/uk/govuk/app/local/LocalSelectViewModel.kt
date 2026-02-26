package uk.govuk.app.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.govuk.app.local.data.LocalRepo
import javax.inject.Inject

@HiltViewModel
internal class LocalSelectViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val localRepo: LocalRepo,
): ViewModel() {
    companion object {
        private const val SELECT_BY_LOCAL_AUTHORITY_SCREEN_CLASS = "LocalAuthoritySelectScreen"
        private const val SELECT_BY_ADDRESS_SCREEN_CLASS = "LocalAddressSelectScreen"
        private const val SELECT_SCREEN_NAME = "Local Select"
        private const val SELECT_TITLE = "Local Select"
    }

    val localAuthorities
        get() = localRepo.localAuthorities.sortedBy { it.name }

    val addresses
        get() = localRepo.addresses.sortedBy { it.address }

    fun onSelectByLocalAuthorityPageView() {
        analyticsClient.screenView(
            screenClass = SELECT_BY_LOCAL_AUTHORITY_SCREEN_CLASS,
            screenName = SELECT_SCREEN_NAME,
            title = SELECT_TITLE
        )
    }

    fun onSelectByAddressPageView() {
        analyticsClient.screenView(
            screenClass = SELECT_BY_ADDRESS_SCREEN_CLASS,
            screenName = SELECT_SCREEN_NAME,
            title = SELECT_TITLE
        )
    }

    fun onSelectByAddressButtonClick(buttonText: String) {
        analyticsClient.buttonClick(
            text = buttonText,
            section = SECTION
        )
    }

    fun updateLocalAuthority(buttonText: String, slug: String) {
        analyticsClient.buttonClick(
            text = buttonText,
            section = SECTION
        )

        viewModelScope.launch {
            localRepo.cacheLocalAuthority(slug)
        }
    }
}
