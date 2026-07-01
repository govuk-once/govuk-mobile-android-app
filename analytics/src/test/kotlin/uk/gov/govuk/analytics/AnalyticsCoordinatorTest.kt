package uk.gov.govuk.analytics

import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import java.util.Locale

class AnalyticsCoordinatorTest {

    private val firebaseAnalyticClient = mockk<FirebaseAnalyticsClient>(relaxed = true)
    private val qualtricsAnalyticsClient = mockk<QualtricsAnalyticsClient>(relaxed = true)

    private lateinit var analyticsCoordinator: AnalyticsCoordinator

    @Before
    fun setup() {
        analyticsCoordinator = AnalyticsCoordinator(
            firebaseAnalyticClient,
            qualtricsAnalyticsClient
        )
    }

    @Test
    fun `Given an initialize call, then call the Qualtrics initialize method`() = runTest {
        analyticsCoordinator.initialize()

        verify {
            qualtricsAnalyticsClient.initialize()
        }
    }

    @Test
    fun `Given a log event, then log event`() = runTest {
        val event = "event"
        val params = mapOf(
            "type" to "type",
            "external" to false,
            "language" to Locale.getDefault().language,
            "text" to "text"
        )

        analyticsCoordinator.logEvent(event, params)

        verify {
            firebaseAnalyticClient.logEvent(event, params)
            qualtricsAnalyticsClient.logEvent(event, params)
        }
    }

    @Test
    fun `Given a log ecommerce event, then log ecommerce event`() {
        val event = "event"
        val ecommerceEvent = EcommerceEvent(
            itemListName = "list name",
            itemListId = "list id",
            items = listOf(),
            totalItemCount = 0
        )
        val selectedItemIndex = 0

        analyticsCoordinator.logEcommerceEvent(
            event = event,
            ecommerceEvent = ecommerceEvent,
            selectedItemIndex = selectedItemIndex
        )

        verify {
            firebaseAnalyticClient.logEcommerceEvent(
                event = event,
                ecommerceEvent = ecommerceEvent,
                selectedItemIndex = selectedItemIndex
            )
            qualtricsAnalyticsClient.logEcommerceEvent(
                eventName = event,
                ecommerceEvent = ecommerceEvent
            )
        }
    }
}
