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

    private lateinit var analyticsClient: AnalyticsClient

    @Before
    fun setup() {
        analyticsClient = AnalyticsClient(analyticsRepo, firebaseAnalyticClient)

        every { analyticsRepo.analyticsEnabledState } returns ENABLED
        analyticsClient.isUserSessionActive = { true }
    }

    @Test
    fun `Given analytics are disabled, when an event is logged, then do not log to firebase`() = runTest {
        coEvery { analyticsRepo.analyticsEnabledState } returns DISABLED

        analyticsClient.screenView(
            screenClass = "screenClass",
            screenName = "screenName",
            title = "title"
        )

        verify(exactly = 0) {
            firebaseAnalyticClient.logEvent(any(), any())
        }
    }

    @Test
    fun `Given analytics are not set, when an event is logged, then do not log to firebase`() = runTest {
        analyticsClient.isUserSessionActive = { false }

        analyticsClient.screenView(
            screenClass = "screenClass",
            screenName = "screenName",
            title = "title"
        )

        verify(exactly = 0) {
            firebaseAnalyticClient.logEvent(any(), any())
        }
    }

    @Test
    fun `Given user session is not active, when an event is logged, then do not log to firebase`() = runTest {
        coEvery { analyticsRepo.analyticsEnabledState } returns NOT_SET

        analyticsClient.screenView(
            screenClass = "screenClass",
            screenName = "screenName",
            title = "title"
        )

        verify(exactly = 0) {
            firebaseAnalyticClient.logEvent(any(), any())
        }
    }

    @Test
    fun `Given analytics are disabled, when a select item ecommerce event is logged, then do not log to firebase`() = runTest {
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
            firebaseAnalyticClient.logEcommerceEvent(any(), any())
        }
    }

    @Test
    fun `Given analytics are not set, when a select item ecommerce event is logged, then do not log to firebase`() = runTest {
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
            firebaseAnalyticClient.logEcommerceEvent(any(), any())
        }
    }

    @Test
    fun `Given analytics are disabled, when a view list item ecommerce event is logged, then do not log to firebase`() = runTest {
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
            firebaseAnalyticClient.logEcommerceEvent(any(), any())
        }
    }

    @Test
    fun `Given analytics are not set, when a view list item ecommerce event is logged, then do not log to firebase`() = runTest {
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
            firebaseAnalyticClient.logEcommerceEvent(any(), any())
        }
    }

    @Test
    fun `Given a user session is not active, when an ecommerce event is logged, then do not log to firebase`() = runTest {
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
            firebaseAnalyticClient.logEcommerceEvent(any(), any())
        }
    }

    @Test
    fun `Given a screen view, then log event`() {
        analyticsClient.screenView(
            screenClass = "screenClass",
            screenName = "screenName",
            title = "title"
        )

        verify {
            firebaseAnalyticClient.logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                mapOf(
                    FirebaseAnalytics.Param.SCREEN_CLASS to "screenClass",
                    FirebaseAnalytics.Param.SCREEN_NAME to "screenName",
                    "screen_title" to "title",
                    "language" to Locale.getDefault().language
                )
            )
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

        verify {
            firebaseAnalyticClient.logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                mapOf(
                    FirebaseAnalytics.Param.SCREEN_CLASS to "screenClass",
                    FirebaseAnalytics.Param.SCREEN_NAME to "screenName",
                    "screen_title" to "title",
                    "type" to "type",
                    "language" to Locale.getDefault().language
                )
            )
        }
    }

    @Test
    fun `Given a button click, then log event`() {
        analyticsClient.buttonClick("text")

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "Button",
                    "external" to false,
                    "language" to Locale.getDefault().language,
                    "text" to "text"
                )
            )
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

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "Button",
                    "external" to true,
                    "url" to "url",
                    "section" to "section",
                    "language" to Locale.getDefault().language,
                    "text" to "text"
                )
            )
        }
    }

    @Test
    fun `Given a chat question, then log event`() {
        analyticsClient.chat()

        verify {
            firebaseAnalyticClient.logEvent(
                "Chat",
                mapOf(
                    "action" to "Ask Question",
                    "type" to "typed",
                )
            )
        }
    }

    @Test
    fun `Given a search, then log event`() {
        analyticsClient.search("search term")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "typed",
                    "text" to "search term"
                )
            )
        }
    }

    @Test
    fun `Given a search with postcode, then redact and log event`() {
        analyticsClient.search("search term A1 1AA")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "typed",
                    "text" to "search term [postcode]"
                )
            )
        }
    }

    @Test
    fun `Given a search with email address, then redact and log event`() {
        analyticsClient.search("search term test@email.com")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "typed",
                    "text" to "search term [email]"
                )
            )
        }
    }

    @Test
    fun `Given a search with NI number, then redact and log event`() {
        analyticsClient.search("search term AA 00 00 00 A")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "typed",
                    "text" to "search term [NI number]"
                )
            )
        }
    }

    @Test
    fun `Given an autocomplete, then log event`() {
        analyticsClient.autocomplete("input")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "autocomplete",
                    "text" to "input"
                )
            )
        }
    }

    @Test
    fun `Given an autocomplete with postcode, then redact and log event`() {
        analyticsClient.autocomplete("input A1 1AA")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "autocomplete",
                    "text" to "input [postcode]"
                )
            )
        }
    }

    @Test
    fun `Given an autocomplete with email address, then redact and log event`() {
        analyticsClient.autocomplete("input test@email.com")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "autocomplete",
                    "text" to "input [email]"
                )
            )
        }
    }

    @Test
    fun `Given an autocomplete with NI number, then redact and log event`() {
        analyticsClient.autocomplete("input AA 00 00 00 A")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "autocomplete",
                    "text" to "input [NI number]"
                )
            )
        }
    }

    @Test
    fun `Given a history search, then log event`() {
        analyticsClient.history("input")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "history",
                    "text" to "input"
                )
            )
        }
    }

    @Test
    fun `Given a history search with postcode, then redact and log event`() {
        analyticsClient.history("input A1 1AA")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "history",
                    "text" to "input [postcode]"
                )
            )
        }
    }

    @Test
    fun `Given a history search with email address, then redact and log event`() {
        analyticsClient.history("input test@email.com")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "history",
                    "text" to "input [email]"
                )
            )
        }
    }

    @Test
    fun `Given a history search with NI number, then redact and log event`() {
        analyticsClient.history("input AA 00 00 00 A")

        verify {
            firebaseAnalyticClient.logEvent(
                "Search",
                mapOf(
                    "type" to "history",
                    "text" to "input [NI number]"
                )
            )
        }
    }

    @Test
    fun `Given a search result click, then log event`() {
        analyticsClient.searchResultClick("search result title", "search result link")

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "SearchResult",
                    "external" to true,
                    "language" to Locale.getDefault().language,
                    "text" to "search result title",
                    "url" to "search result link"
                )
            )
        }
    }

    @Test
    fun `Given a question answer is returned in chat, then log event`() {
        analyticsClient.chatQuestionAnswerReturnedEvent()

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "ChatQuestionAnswerReturned",
                    "external" to false,
                    "language" to Locale.getDefault().language,
                    "text" to "Chat Question Answer Returned"
                )
            )
        }
    }

    @Test
    fun `Given a chat response markdown link click, then log event`() {
        analyticsClient.chatMarkdownLinkClick("chat title", "chat link")

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "ChatMarkdownLink",
                    "external" to true,
                    "language" to Locale.getDefault().language,
                    "text" to "chat title",
                    "url" to "chat link"
                )
            )
        }
    }

    @Test
    fun `Given a visited item click, then log event`() {
        analyticsClient.visitedItemClick("visited item title", "visited item link")

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "VisitedItem",
                    "external" to true,
                    "language" to Locale.getDefault().language,
                    "text" to "visited item title",
                    "url" to "visited item link"
                )
            )
        }
    }

    @Test
    fun `Given an external settings item click, then log event`() {
        analyticsClient.settingsItemClick("settings item title", "settings item link")

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "SettingsItem",
                    "external" to true,
                    "language" to Locale.getDefault().language,
                    "text" to "settings item title",
                    "url" to "settings item link"
                )
            )
        }
    }

    @Test
    fun `Given a internal settings item click, then log event`() {
        analyticsClient.settingsItemClick("settings item title", external = false)

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "SettingsItem",
                    "external" to false,
                    "language" to Locale.getDefault().language,
                    "text" to "settings item title"
                )
            )
        }
    }

    @Test
    fun `Given a tab click, then log event`() {
        analyticsClient.tabClick("text")

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "Tab",
                    "external" to false,
                    "language" to Locale.getDefault().language,
                    "text" to "text"
                )
            )
        }
    }

    @Test
    fun `Given a widget click with no url, then log event`() {
        analyticsClient.widgetClick(
            text = "text",
            external = false,
            section = "section"
        )

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "Widget",
                    "external" to false,
                    "language" to Locale.getDefault().language,
                    "text" to "text",
                    "section" to "section"
                )
            )
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

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "Widget",
                    "url" to "url",
                    "external" to true,
                    "language" to Locale.getDefault().language,
                    "text" to "text",
                    "section" to "section"
                )
            )
        }
    }

    @Test
    fun `Given a suppress widget click, then log event`() {
        analyticsClient.suppressWidgetClick("id", "section")

        verify {
            firebaseAnalyticClient.logEvent(
                "Function",
                mapOf(
                    "type" to "Widget",
                    "language" to Locale.getDefault().language,
                    "text" to "id",
                    "section" to "section",
                    "action" to "Remove"
                )
            )
        }
    }

    @Test
    fun `Given a deep link event, When the app has the deep link, then log event`() {
        analyticsClient.deepLinkEvent(true, "url")

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "DeepLink",
                    "external" to false,
                    "language" to Locale.getDefault().language,
                    "text" to "Opened",
                    "url" to "url"
                )
            )
        }
    }

    @Test
    fun `Given a deep link event, When the app doesn't have the deep link, then log event`() {
        analyticsClient.deepLinkEvent(false, "url")

        verify {
            firebaseAnalyticClient.logEvent(
                "Navigation",
                mapOf(
                    "type" to "DeepLink",
                    "external" to false,
                    "language" to Locale.getDefault().language,
                    "text" to "Failed",
                    "url" to "url"
                )
            )
        }
    }

    @Test
    fun `Given a toggle function, then log event`() {
        analyticsClient.toggleFunction(
            text = "text",
            section = "section",
            action = "action"
        )

        verify {
            firebaseAnalyticClient.logEvent(
                "Function",
                mapOf(
                    "type" to "Toggle",
                    "language" to Locale.getDefault().language,
                    "text" to "text",
                    "section" to "section",
                    "action" to "action"
                )
            )
        }
    }

    @Test
    fun `Given a button function, then log event`() {
        analyticsClient.buttonFunction(
            text = "text",
            section = "section",
            action = "action"
        )

        verify {
            firebaseAnalyticClient.logEvent(
                "Function",
                mapOf(
                    "type" to "Button",
                    "language" to Locale.getDefault().language,
                    "text" to "text",
                    "section" to "section",
                    "action" to "action"
                )
            )
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

        println(ecommerceEvent)

        analyticsClient.selectItemEvent(
            ecommerceEvent = ecommerceEvent,
            selectedItemIndex = 42
        )

        verify {
            firebaseAnalyticClient.logEcommerceEvent(
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
            firebaseAnalyticClient.logEcommerceEvent(
                event = FirebaseAnalytics.Event.VIEW_ITEM_LIST,
                ecommerceEvent = ecommerceEvent
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
            firebaseAnalyticClient.logEcommerceEvent(
                event = FirebaseAnalytics.Event.VIEW_ITEM_LIST,
                ecommerceEvent = ecommerceEvent
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
}
