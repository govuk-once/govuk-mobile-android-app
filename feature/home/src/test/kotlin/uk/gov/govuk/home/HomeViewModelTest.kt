package uk.gov.govuk.home

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.config.data.local.model.HomeWidget
import uk.gov.govuk.config.data.remote.model.ChatBanner
import uk.gov.govuk.config.data.remote.model.EmergencyBanner
import uk.gov.govuk.config.data.remote.model.EmergencyBannerType
import uk.gov.govuk.config.data.remote.model.Link
import uk.gov.govuk.config.data.remote.model.PromoBanner
import uk.gov.govuk.config.data.remote.model.PromoBannerType

class HomeViewModelTest {

    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    @Test
    fun `Given a page view, and the user has opted into chat, then log analytics`() {
        val viewModel = HomeViewModel(analyticsClient)

        viewModel.onPageView(emptyList())

        verify {
            analyticsClient.screenView(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage"
            )
        }
    }

    @Test
    fun `Given a page view, and the user has opted out of chat, then log analytics`() {
        val viewModel = HomeViewModel(analyticsClient)

        viewModel.onPageView(emptyList())

        verify {
            analyticsClient.screenView(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage"
            )
        }
    }

    @Test
    fun `Given a page view, and there is a screen banner, then log analytics`() {
        val viewModel = HomeViewModel(analyticsClient)

        val homeWidgets = listOf<HomeWidget>(
            HomeWidget.Promo(
                promoBanner = PromoBanner(
                    id = "itemId",
                    title = "itemTitle",
                    body = "itemBody",
                    link = Link("linkTitle", "linkUrl"),
                    image = "itemImage",
                    type = PromoBannerType.EXTERNAL
                )
            )
        )

        viewModel.onPageView(homeWidgets)

        verify {
            analyticsClient.screenView(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage"
            )
        }

        verify {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "home_banners",
                    itemListId = "home_banners",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemId = "itemId",
                            itemName = "itemTitle",
                            itemCategory = "PromoBanner",
                            locationId = "linkUrl"
                        )
                    ),
                    totalItemCount = 1
                )
            )
        }
    }

    @Test
    fun `Given a page view, and there are promo banners, then log analytics`() {
        val viewModel = HomeViewModel(analyticsClient)

        val homeWidgets = listOf(
            HomeWidget.Banner(
                emergencyBanner = EmergencyBanner(
                    id = "emergencyItemId",
                    title = "emergencyItemTitle",
                    body = "emergencyItemBody",
                    link = Link("emergencyLinkTitle", "emergencyLinkUrl"),
                    type = EmergencyBannerType.NATIONAL_EMERGENCY,
                    allowsDismissal = false
                )
            ),
            HomeWidget.Chat(
                chatBanner = ChatBanner(
                    id = "chatItemId",
                    title = "chatItemTitle",
                    body = "chatItemBody",
                    link = Link("chatLinkTitle", "chatLinkUrl")
                )
            )
        )

        viewModel.onPageView(homeWidgets)

        verify {
            analyticsClient.screenView(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage"
            )
        }

        verify {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "home_banners",
                    itemListId = "home_banners",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemId = "emergencyItemId",
                            itemName = "emergencyItemTitle",
                            itemCategory = "EmergencyBanner",
                            locationId = "emergencyLinkUrl"
                        ),
                        EcommerceEvent.Item(
                            itemId = "chatItemId",
                            itemName = "chatItemTitle",
                            itemCategory = "ChatBanner",
                            locationId = "chatLinkUrl"
                        )
                    ),
                    totalItemCount = 2
                )
            )
        }
    }

    @Test
    fun `Given a page view, and there are no promo banners, then do not log analytics`() {
        val viewModel = HomeViewModel(analyticsClient)

        val homeWidgets = emptyList<HomeWidget>()

        viewModel.onPageView(homeWidgets)

        verify {
            analyticsClient.screenView(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage"
            )
        }

        verify(exactly = 0) {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = any()
            )
        }
    }
}
