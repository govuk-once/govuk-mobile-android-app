package uk.gov.govuk.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent.Item
import uk.gov.govuk.config.data.local.model.HomeWidget
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "HomeScreen"
        private const val SCREEN_NAME = "Homepage"
        private const val TITLE = "Homepage"
        private const val HOME_BANNERS = "home_banners"
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
        val items = mutableListOf<Item>()
        homeWidgets?.forEach {
            when (it) {
                is HomeWidget.Banner -> {
                    items.add(
                        Item(
                            itemId = it.emergencyBanner.id,
                            itemName = it.emergencyBanner.title ?: "",
                            itemCategory = humaniseName(it.emergencyBanner),
                            locationId = it.emergencyBanner.link?.url ?: ""
                        )
                    )
                }
                is HomeWidget.Chat -> {
                    items.add(
                        Item(
                            itemId = it.chatBanner.id,
                            itemName = it.chatBanner.title,
                            itemCategory = humaniseName(it.chatBanner),
                            locationId = it.chatBanner.link.url
                        )
                    )
                }
                is HomeWidget.Promo -> {
                    items.add(
                        Item(
                            itemId = it.promoBanner.id,
                            itemName = it.promoBanner.title,
                            itemCategory = humaniseName(it.promoBanner),
                            locationId = it.promoBanner.link.url
                        )
                    )
                }
//                TODO: We'll need these for a full ecomm on the home screen
//                is HomeWidget.Topics -> {
//                    println("Topics: $it")
//                }
//                is HomeWidget.RecentActivity -> {
//                    println("RecentActivity: $it")
//                }
//                is HomeWidget.Local -> {
//                    println("Local: $it")
//                }
                is HomeWidget.UserFeedback -> {
                    items.add(
                        Item(
                            itemName = it.userFeedbackBanner.link.title,
                            itemCategory = humaniseName(it.userFeedbackBanner),
                            locationId = it.userFeedbackBanner.link.url
                        )
                    )
                }
                else -> { /* do nothing */ }
            }
        }

        analyticsClient.viewItemListEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = HOME_BANNERS,
                itemListId = HOME_BANNERS,
                items = items,
                totalItemCount = items.size
            )
        )
    }

    private fun humaniseName(obj: Any) : String {
        val fullClassName = obj.javaClass.name
        return fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
    }
}
