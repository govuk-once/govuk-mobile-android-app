package uk.gov.govuk.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.config.data.local.model.HOME_BANNERS
import uk.gov.govuk.config.data.local.model.HomeWidget
import uk.gov.govuk.config.data.local.model.toAnalyticsItems
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "HomeScreen"
        private const val SCREEN_NAME = "Homepage"
        private const val TITLE = "Homepage"
    }

    fun onPageView(
        homeWidgets: List<HomeWidget>?
    ) {
        onHomeWidgetsView(homeWidgets)

        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onHomeWidgetsView(
        homeWidgets: List<HomeWidget>?
    ) {
        val items = homeWidgets?.toAnalyticsItems() ?: emptyList()

        analyticsClient.viewItemListEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = HOME_BANNERS,
                itemListId = HOME_BANNERS,
                items = items,
                totalItemCount = items.size
            )
        )
    }
}
