package uk.gov.govuk.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.data.AnalyticsRepo
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.DISABLED
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.ENABLED
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState.NOT_SET
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import java.util.Locale

class AnalyticsClientTest {

    private val analyticsRepo = mockk<AnalyticsRepo>(relaxed = true)
    private val firebaseAnalyticClient = mockk<FirebaseAnalyticsClient>(relaxed = true)
    private val analyticsCoordinator = mockk<AnalyticsCoordinator>(relaxed = true)

    private lateinit var analyticsClient: AnalyticsClient

    @Before
    fun setup() {
        analyticsClient = AnalyticsClient(analyticsRepo, firebaseAnalyticClient, analyticsCoordinator)

        every { analyticsRepo.analyticsEnabledState } returns ENABLED
        analyticsClient.isUserSessionActive = { true }
    }

    @Test
    fun `Given analytics are disabled, when an event is logged, then do not log event`() = runTest {
        coEvery { analyticsRepo.analyticsEnabledState } returns DISABLED

        analyticsClient.screenView(
            screenClass = "screenClass",
            screenName = "screenName",
            title = "title"
        )

        verify(exactly = 0) {
            analyticsCoordinator.logEvent(any(), any())
        }
    }

    @Test
    fun `Given analytics are not set, when an event is logged, then do not log event`() = runTest {
        analyticsClient.isUserSessionActive = { false }

        analyticsClient.screenView(
            screenClass = "screenClass",
            screenName = "screenName",
            title = "title"
        )

        verify(exactly = 0) {
            analyticsCoordinator.logEvent(any(), any())
        }
    }

    @Test
    fun `Given user session is not active, when an event is logged, then do not log event`() = runTest {
        coEvery { analyticsRepo.analyticsEnabledState } returns NOT_SET

        analyticsClient.screenView(
            screenClass = "screenClass",
            screenName = "screenName",
            title = "title"
        )

        verify(exactly = 0) {
            analyticsCoordinator.logEvent(any(), any())
        }
    }

    @Test
    fun `Given analytics are disabled, when a select item ecommerce event is logged, then do not log event`() = runTest {
        coEvery { analyticsRepo.analyticsEnabledState } returns DISABLED

        analyticsClient.selectItemEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = "Topics",
                itemListId = "Benefits",
                items = emptyList(),
                totalItemCount = 0
            ),
            selectedItemIndex = 42
        )

        verify(exactly = 0) {
            analyticsCoordinator.logEcommerceEvent(any(), any(), null)
        }
    }

    @Test
    fun `Given analytics are not set, when a select item ecommerce event is logged, then do not log event`() = runTest {
        coEvery { analyticsRepo.analyticsEnabledState } returns NOT_SET

        analyticsClient.selectItemEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = "Topics",
                itemListId = "Benefits",
                items = emptyList(),
                totalItemCount = 0
            ),
            selectedItemIndex = 42
        )

        verify(exactly = 0) {
            analyticsCoordinator.logEcommerceEvent(any(), any(), 42)
        }
    }

    @Test
    fun `Given analytics are disabled, when a view list item ecommerce event is logged, then do not log event`() = runTest {
        coEvery { analyticsRepo.analyticsEnabledState } returns DISABLED

        analyticsClient.viewItemListEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = "Topics",
                itemListId = "Benefits",
                items = emptyList(),
                totalItemCount = 0
            )
        )

        verify(exactly = 0) {
            analyticsCoordinator.logEcommerceEvent(any(), any(), null)
        }
    }

    @Test
    fun `Given analytics are not set, when a view list item ecommerce event is logged, then do not log event`() = runTest {
        coEvery { analyticsRepo.analyticsEnabledState } returns NOT_SET

        analyticsClient.viewItemListEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = "Topics",
                itemListId = "Benefits",
                items = emptyList(),
                totalItemCount = 0
            )
        )

        verify(exactly = 0) {
            analyticsCoordinator.logEcommerceEvent(any(), any(), null)
        }
    }

    @Test
    fun `Given a user session is not active, when an ecommerce event is logged, then do not log event`() = runTest {
        analyticsClient.isUserSessionActive = { false }

        analyticsClient.selectItemEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = "Topics",
                itemListId = "Benefits",
                items = emptyList(),
                totalItemCount = 0
            ),
            selectedItemIndex = 42
        )

        verify(exactly = 0) {
            analyticsCoordinator.logEcommerceEvent(any(), any(), null)
        }
    }

    @Test
    fun `Given a screen view, then log event`() {
        analyticsClient.screenView(
            screenClass = "screenClass",
            screenName = "screenName",
            title = "title"
        )

        val event = FirebaseAnalytics.Event.SCREEN_VIEW
        val params = mapOf(
            FirebaseAnalytics.Param.SCREEN_CLASS to "screenClass",
            FirebaseAnalytics.Param.SCREEN_NAME to "screenName",
            "screen_title" to "title",
            "language" to Locale.getDefault().language
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a home screen view, then log event with a type`() {
        analyticsClient.screenViewWithType(
            screenClass = "screenClass",
            screenName = "screenName",
            title = "title",
            type = "type"
        )

        val event = FirebaseAnalytics.Event.SCREEN_VIEW
        val params = mapOf(
            FirebaseAnalytics.Param.SCREEN_CLASS to "screenClass",
            FirebaseAnalytics.Param.SCREEN_NAME to "screenName",
            "screen_title" to "title",
            "type" to "type",
            "language" to Locale.getDefault().language
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
   }

    @Test
    fun `Given a button click, then log event`() {
        analyticsClient.buttonClick("text")

        val event = "Navigation"
        val params = mapOf(
            "type" to "Button",
            "external" to false,
            "language" to Locale.getDefault().language,
            "text" to "text"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a button click with optional parameters, then log event`() {
        analyticsClient.buttonClick(
            text = "text",
            url = "url",
            external = true,
            section = "section"
        )

        val event = "Navigation"
        val params = mapOf(
            "type" to "Button",
            "external" to true,
            "url" to "url",
            "section" to "section",
            "language" to Locale.getDefault().language,
            "text" to "text"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a chat question, then log event`() {
        analyticsClient.chat()

        val event = "Chat"
        val params = mapOf(
            "action" to "Ask Question",
            "type" to "typed",
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a search, then log event`() {
        analyticsClient.search("search term")

        val event = "Search"
        val params = mapOf(
            "type" to "typed",
            "text" to "search term"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a search with postcode, then redact and log event`() {
        analyticsClient.search("search term A1 1AA")

        val event = "Search"
        val params = mapOf(
            "type" to "typed",
            "text" to "search term [postcode]"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a search with email address, then redact and log event`() {
        analyticsClient.search("search term test@email.com")

        val event = "Search"
        val params = mapOf(
            "type" to "typed",
            "text" to "search term [email]"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a search with NI number, then redact and log event`() {
        analyticsClient.search("search term AA 00 00 00 A")

        val event = "Search"
        val params = mapOf(
            "type" to "typed",
            "text" to "search term [NI number]"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given an autocomplete, then log event`() {
        analyticsClient.autocomplete("input")

        val event = "Search"
        val params = mapOf(
            "type" to "autocomplete",
            "text" to "input"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given an autocomplete with postcode, then redact and log event`() {
        analyticsClient.autocomplete("input A1 1AA")

        val event = "Search"
        val params = mapOf(
            "type" to "autocomplete",
            "text" to "input [postcode]"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given an autocomplete with email address, then redact and log event`() {
        analyticsClient.autocomplete("input test@email.com")

        val event = "Search"
        val params = mapOf(
            "type" to "autocomplete",
            "text" to "input [email]"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given an autocomplete with NI number, then redact and log event`() {
        analyticsClient.autocomplete("input AA 00 00 00 A")

        val event = "Search"
        val params = mapOf(
            "type" to "autocomplete",
            "text" to "input [NI number]"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a history search, then log event`() {
        analyticsClient.history("input")

        val event = "Search"
        val params = mapOf(
            "type" to "history",
            "text" to "input"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a history search with postcode, then redact and log event`() {
        analyticsClient.history("input A1 1AA")

        val event = "Search"
        val params = mapOf(
            "type" to "history",
            "text" to "input [postcode]"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a history search with email address, then redact and log event`() {
        analyticsClient.history("input test@email.com")

        val event = "Search"
        val params = mapOf(
            "type" to "history",
            "text" to "input [email]"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a history search with NI number, then redact and log event`() {
        analyticsClient.history("input AA 00 00 00 A")

        val event = "Search"
        val params = mapOf(
            "type" to "history",
            "text" to "input [NI number]"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a search result click, then log event`() {
        analyticsClient.searchResultClick("search result title", "search result link")

        val event = "Navigation"
        val params = mapOf(
            "type" to "SearchResult",
            "external" to true,
            "language" to Locale.getDefault().language,
            "text" to "search result title",
            "url" to "search result link"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a question answer is returned in chat, then log event`() {
        analyticsClient.chatQuestionAnswerReturnedEvent()

        val event = "Navigation"
        val params = mapOf(
            "type" to "ChatQuestionAnswerReturned",
            "external" to false,
            "language" to Locale.getDefault().language,
            "text" to "Chat Question Answer Returned"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a chat response markdown link click, then log event`() {
        analyticsClient.chatMarkdownLinkClick("chat title", "chat link")

        val event = "Navigation"
        val params = mapOf(
            "type" to "ChatMarkdownLink",
            "external" to true,
            "language" to Locale.getDefault().language,
            "text" to "chat title",
            "url" to "chat link"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a visited item click, then log event`() {
        analyticsClient.visitedItemClick("visited item title", "visited item link")

        val event = "Navigation"
        val params = mapOf(
            "type" to "VisitedItem",
            "external" to true,
            "language" to Locale.getDefault().language,
            "text" to "visited item title",
            "url" to "visited item link"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given an external settings item click, then log event`() {
        analyticsClient.settingsItemClick("settings item title", "settings item link")

        val event = "Navigation"
        val params = mapOf(
            "type" to "SettingsItem",
            "external" to true,
            "language" to Locale.getDefault().language,
            "text" to "settings item title",
            "url" to "settings item link"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a internal settings item click, then log event`() {
        analyticsClient.settingsItemClick("settings item title", external = false)

        val event = "Navigation"
        val params = mapOf(
            "type" to "SettingsItem",
            "external" to false,
            "language" to Locale.getDefault().language,
            "text" to "settings item title"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a tab click, then log event`() {
        analyticsClient.tabClick("text")

        val event = "Navigation"
        val params = mapOf(
            "type" to "Tab",
            "external" to false,
            "language" to Locale.getDefault().language,
            "text" to "text"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a widget click with no url, then log event`() {
        analyticsClient.widgetClick(
            text = "text",
            external = false,
            section = "section"
        )

        val event = "Navigation"
        val params = mapOf(
            "type" to "Widget",
            "external" to false,
            "language" to Locale.getDefault().language,
            "text" to "text",
            "section" to "section"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a widget click with url, then log event`() {
        analyticsClient.widgetClick(
            text = "text",
            url = "url",
            external = true,
            section = "section"
        )

        val event = "Navigation"
        val params = mapOf(
            "type" to "Widget",
            "url" to "url",
            "external" to true,
            "language" to Locale.getDefault().language,
            "text" to "text",
            "section" to "section"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a suppress widget click, then log event`() {
        analyticsClient.suppressWidgetClick("id", "section")

        val event = "Function"
        val params = mapOf(
            "type" to "Widget",
            "language" to Locale.getDefault().language,
            "text" to "id",
            "section" to "section",
            "action" to "Remove"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a deep link event, When the app has the deep link, then log event`() {
        analyticsClient.deepLinkEvent(true, "url")

        val event = "Navigation"
        val params = mapOf(
            "type" to "DeepLink",
            "external" to false,
            "language" to Locale.getDefault().language,
            "text" to "Opened",
            "url" to "url"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a deep link event, When the app doesn't have the deep link, then log event`() {
        analyticsClient.deepLinkEvent(false, "url")

        val event = "Navigation"
        val params = mapOf(
            "type" to "DeepLink",
            "external" to false,
            "language" to Locale.getDefault().language,
            "text" to "Failed",
            "url" to "url"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a toggle function, then log event`() {
        analyticsClient.toggleFunction(
            text = "text",
            section = "section",
            action = "action"
        )

        val event = "Function"
        val params = mapOf(
            "type" to "Toggle",
            "language" to Locale.getDefault().language,
            "text" to "text",
            "section" to "section",
            "action" to "action"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a button function, then log event`() {
        analyticsClient.buttonFunction(
            text = "text",
            section = "section",
            action = "action"
        )

        val event = "Function"
        val params = mapOf(
            "type" to "Button",
            "language" to Locale.getDefault().language,
            "text" to "text",
            "section" to "section",
            "action" to "action"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given analytics are not set, when is analytics consent required, then return true`() {
        coEvery { analyticsRepo.analyticsEnabledState } returns NOT_SET

        runTest {
            assertTrue(analyticsClient.isAnalyticsConsentRequired())
        }
    }

    @Test
    fun `Given analytics are enabled, when is analytics consent required, then return false`() {
        coEvery { analyticsRepo.analyticsEnabledState } returns ENABLED

        runTest {
            assertFalse(analyticsClient.isAnalyticsConsentRequired())
        }
    }

    @Test
    fun `Given analytics are disabled, when is analytics consent required, then return false`() {
        coEvery { analyticsRepo.analyticsEnabledState } returns DISABLED

        runTest {
            assertFalse(analyticsClient.isAnalyticsConsentRequired())
        }
    }

    @Test
    fun `Given analytics are not set, when is analytics enabled, then return false`() {
        coEvery { analyticsRepo.analyticsEnabledState } returns NOT_SET

        runTest {
            assertFalse(analyticsClient.isAnalyticsEnabled())
        }
    }

    @Test
    fun `Given analytics are enabled, when is analytics enabled, then return true`() {
        coEvery { analyticsRepo.analyticsEnabledState } returns ENABLED

        runTest {
            assertTrue(analyticsClient.isAnalyticsEnabled())
        }
    }

    @Test
    fun `Given analytics are disabled, when is analytics enabled, then return false`() {
        coEvery { analyticsRepo.analyticsEnabledState } returns DISABLED

        runTest {
            assertFalse(analyticsClient.isAnalyticsEnabled())
        }
    }

    @Test
    fun `Given analytics have been enabled, then enable`() {
        runTest {
            analyticsClient.enable()

            coVerify {
                analyticsRepo.analyticsEnabled()
                firebaseAnalyticClient.enable()
            }
        }
    }

    @Test
    fun `Given analytics have been disabled, then disable`() {
        runTest {
            analyticsClient.disable()

            coVerify {
                analyticsRepo.analyticsDisabled()
                firebaseAnalyticClient.disable()
            }
        }
    }

    @Test
    fun `Given analytics have been cleared, then clear`() {
        runTest {
            analyticsClient.clear()

            coVerify {
                analyticsRepo.clear()
            }
        }
    }

    @Test
    fun `Given topics have been customised, then set user property`() {
        analyticsClient.topicsCustomised()

        verify {
            firebaseAnalyticClient.setUserProperty("topics_customised", "true")
        }
    }

    @Test
    fun `Given a topic has been selected, then log an event`() {
        val topicItems = listOf(
            EcommerceEvent.Item(
                itemName = "Universal Credit",
                itemCategory = "Popular pages in this topic",
                locationId = "/universal-credit"
            )
        )

        val ecommerceEvent = EcommerceEvent(
            itemListName = "Topics",
            itemListId = "Benefits",
            items = topicItems,
            totalItemCount = 5
        )

        analyticsClient.selectItemEvent(
            ecommerceEvent = ecommerceEvent,
            selectedItemIndex = 42
        )

        verify {
            analyticsCoordinator.logEcommerceEvent(
                event = FirebaseAnalytics.Event.SELECT_ITEM,
                ecommerceEvent = ecommerceEvent,
                selectedItemIndex = 42
            )
        }
    }

    @Test
    fun `Given a topic has been viewed and it has items, then log an event`() {
        val topicItems = listOf(
            EcommerceEvent.Item(
                itemName = "Universal Credit",
                itemCategory = "Popular pages in this topic",
                locationId = "/universal-credit"
            ),
            EcommerceEvent.Item(
                itemName = "How to claim Universal Credit",
                itemCategory = "Step by Step guides",
                locationId = "/how-to-claim-universal-credit"
            ),
            EcommerceEvent.Item(
                itemName = "Managing your benefits",
                itemCategory = "Browse",
                locationId = ""
            )
        )

        val ecommerceEvent = EcommerceEvent(
            itemListName = "Topics",
            itemListId = "Benefits",
            items = topicItems,
            totalItemCount = 5
        )

        analyticsClient.viewItemListEvent(
            ecommerceEvent = ecommerceEvent
        )

        verify {
            analyticsCoordinator.logEcommerceEvent(
                event = FirebaseAnalytics.Event.VIEW_ITEM_LIST,
                ecommerceEvent = ecommerceEvent,
                null
            )
        }
    }

    @Test
    fun `Given a topic has been viewed and it has no items, then log an event`() {
        val topicItems = emptyList<EcommerceEvent.Item>()

        val ecommerceEvent = EcommerceEvent(
            itemListName = "Topics",
            itemListId = "Benefits",
            items = topicItems,
            totalItemCount = 5
        )

        analyticsClient.viewItemListEvent(
            ecommerceEvent = ecommerceEvent
        )

        verify {
            analyticsCoordinator.logEcommerceEvent(
                event = FirebaseAnalytics.Event.VIEW_ITEM_LIST,
                ecommerceEvent = ecommerceEvent,
                null
            )
        }
    }

    @Test
    fun `Given an exception is logged, then log an exception`() {
        val exception = IllegalArgumentException()

        analyticsClient.logException(exception)

        verify {
            firebaseAnalyticClient.logException(exception)
        }
    }

    @Test
    fun `Given a card click, then log event`() {
        analyticsClient.cardClick("text")

        val event = "Navigation"
        val params = mapOf(
            "type" to "trigger card",
            "external" to false,
            "language" to Locale.getDefault().language,
            "text" to "text"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given a card click with optional parameters, then log event`() {
        analyticsClient.cardClick(
            text = "text",
            url = "url",
            external = true,
            section = "section"
        )

        val event = "Navigation"
        val params = mapOf(
            "type" to "trigger card",
            "external" to true,
            "url" to "url",
            "section" to "section",
            "language" to Locale.getDefault().language,
            "text" to "text"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }

    @Test
    fun `Given an icon click, then log event`() {
        analyticsClient.iconClick(
            type = "Icon type",
            external = false
        )

        val event = "Navigation"
        val params = mapOf(
            "type" to "Icon type",
            "external" to false,
            "language" to Locale.getDefault().language,
            "text" to "N/A"
        )

        verify {
            analyticsCoordinator.logEvent(event, params)
        }
    }
}
