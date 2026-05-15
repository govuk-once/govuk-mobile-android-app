package uk.gov.govuk.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.qualtrics.digital.Properties
import com.qualtrics.digital.Qualtrics
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent

class QualtricsAnalyticsClientTest {

    private val context = mockk<Context>(relaxed = true)
    private val qualtrics = mockk<Qualtrics>(relaxed = true)
    private val qualtricsProperties = mockk<Properties>(relaxed = true)

    private lateinit var qualtricsAnalyticsClient: QualtricsAnalyticsClient

    @Before
    fun setUp() {
        qualtrics.properties = qualtricsProperties

        qualtricsAnalyticsClient = QualtricsAnalyticsClient(context, qualtrics)
    }

    @After
    fun tearDown() {
        qualtrics.properties = null
    }

    @Test
    fun `Given a user property is set, then set user property`() {
        qualtricsAnalyticsClient.setUserProperty("name", "value")

        verify {
            qualtrics.properties.setString("name", "value")
        }
    }

    @Test
    fun `Given an event is logged, then log event and register visit`() {
        val params = mapOf("param1" to "value1")
        qualtricsAnalyticsClient.logEvent("event_name", params)

        verify {
            qualtricsProperties.setString("param1", "value1")
            qualtrics.registerViewVisit("event_name")
            qualtrics.evaluateProject(any())
        }
    }

    @Test
    fun `Given an ecommerce event without items is logged, then log ecommerce event and register visit`() {
        val ecommerceEvent = EcommerceEvent(
            itemListId = "list_id",
            itemListName = "list_name",
            items = emptyList(),
            totalItemCount = 0
        )

        qualtricsAnalyticsClient.logEcommerceEvent("event_name", ecommerceEvent)

        verify {
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_ID, "list_id")
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "list_name")
            qualtricsProperties.setString("items", "[]")
            qualtricsProperties.setString("total_item_count", "0")
            qualtrics.registerViewVisit("event_name")
            qualtrics.evaluateProject(any())
        }
    }

    @Test
    fun `Given an ecommerce event with a single item is logged, then log ecommerce event and register visit`() {
        val ecommerceEvent = EcommerceEvent(
            itemListId = "list_id",
            itemListName = "list_name",
            items = listOf(
                EcommerceEvent.Item(
                    itemId = "item_id_one",
                    itemName = "item_name_one",
                    itemCategory = "item_category_one",
                    locationId = "item_location_id_one",
                    term = "item_term_one"
                )
            ),
            totalItemCount = 1
        )

        qualtricsAnalyticsClient.logEcommerceEvent("event_name", ecommerceEvent)

        verify {
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_ID, "list_id")
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "list_name")
            qualtricsProperties.setString("item_id_0", "item_id_one")
            qualtricsProperties.setString("item_name_0", "item_name_one")
            qualtricsProperties.setString("item_category_0", "item_category_one")
            qualtricsProperties.setString("item_location_id_0", "item_location_id_one")
            qualtricsProperties.setString("item_term_0", "item_term_one")
            qualtricsProperties.setString("total_item_count", "1")
            qualtrics.registerViewVisit("event_name")
            qualtrics.evaluateProject(any())
        }
    }

    @Test
    fun `Given an ecommerce event with more than one item but no selected index is logged, then log ecommerce event and register visit`() {
        val ecommerceEvent = EcommerceEvent(
            itemListId = "list_id",
            itemListName = "list_name",
            items = listOf(
                EcommerceEvent.Item(
                    itemId = "item_id_one",
                    itemName = "item_name_one",
                    itemCategory = "item_category_one",
                    locationId = "item_location_id_one",
                    term = "item_term_one"
                ),
                EcommerceEvent.Item(
                    itemId = "item_id_two",
                    itemName = "item_name_two",
                    itemCategory = "item_category_two",
                    locationId = "item_location_id_two",
                    term = "item_term_two"
                )
            ),
            totalItemCount = 2
        )

        qualtricsAnalyticsClient.logEcommerceEvent("event_name", ecommerceEvent)

        verify {
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_ID, "list_id")
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "list_name")
            qualtricsProperties.setString("item_id_0", "item_id_one")
            qualtricsProperties.setString("item_name_0", "item_name_one")
            qualtricsProperties.setString("item_category_0", "item_category_one")
            qualtricsProperties.setString("item_location_id_0", "item_location_id_one")
            qualtricsProperties.setString("item_term_0", "item_term_one")
            qualtricsProperties.setString("item_id_1", "item_id_two")
            qualtricsProperties.setString("item_name_1", "item_name_two")
            qualtricsProperties.setString("item_category_1", "item_category_two")
            qualtricsProperties.setString("item_location_id_1", "item_location_id_two")
            qualtricsProperties.setString("item_term_1", "item_term_two")
            qualtricsProperties.setString("total_item_count", "2")
            qualtrics.registerViewVisit("event_name")
            qualtrics.evaluateProject(any())
        }
    }

    @Test
    fun `Given an ecommerce event with more than one item adn a selected index is logged, then log ecommerce event and register visit`() {
        val ecommerceEvent = EcommerceEvent(
            itemListId = "list_id",
            itemListName = "list_name",
            items = listOf(
                EcommerceEvent.Item(
                    itemId = "item_id_one",
                    itemName = "item_name_one",
                    itemCategory = "item_category_one",
                    locationId = "item_location_id_one",
                    term = "item_term_one"
                ),
                EcommerceEvent.Item(
                    itemId = "item_id_two",
                    itemName = "item_name_two",
                    itemCategory = "item_category_two",
                    locationId = "item_location_id_two",
                    term = "item_term_two"
                )
            ),
            totalItemCount = 2
        )

        qualtricsAnalyticsClient.logEcommerceEvent("event_name", ecommerceEvent, 1)

        verify {
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_ID, "list_id")
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "list_name")
            qualtricsProperties.setString("item_id_1", "item_id_two")
            qualtricsProperties.setString("item_name_1", "item_name_two")
            qualtricsProperties.setString("item_category_1", "item_category_two")
            qualtricsProperties.setString("item_location_id_1", "item_location_id_two")
            qualtricsProperties.setString("item_term_1", "item_term_two")
            qualtricsProperties.setString("total_item_count", "2")
            qualtrics.registerViewVisit("event_name")
            qualtrics.evaluateProject(any())
        }
    }
}
