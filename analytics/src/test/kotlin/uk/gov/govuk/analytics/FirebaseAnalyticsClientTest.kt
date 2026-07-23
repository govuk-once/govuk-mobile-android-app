package uk.gov.govuk.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent

class FirebaseAnalyticsClientTest {

    private val firebaseAnalytics = mockk<FirebaseAnalytics>(relaxed = true)
    private val firebaseCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
    private val firebaseIdentifiers = mockk<FirebaseIdentifiers>(relaxed = true)

    private lateinit var firebaseAnalyticsClient: FirebaseAnalyticsClient

    @Before
    fun setup() {
        firebaseAnalyticsClient = FirebaseAnalyticsClient(
            firebaseAnalytics,
            firebaseCrashlytics,
            firebaseIdentifiers
        )
    }

    @Test
    fun `Given analytics have been enabled, then enable`() {
        firebaseAnalyticsClient.enable()

        verify {
            firebaseAnalytics.setAnalyticsCollectionEnabled(true)
            firebaseCrashlytics.isCrashlyticsCollectionEnabled = true
        }
    }

    @Test
    fun `Given analytics have been disabled, then disable`() {
        firebaseAnalyticsClient.disable()

        verify {
            firebaseAnalytics.setAnalyticsCollectionEnabled(false)
            firebaseCrashlytics.isCrashlyticsCollectionEnabled = false
        }
    }

    @Test
    fun `Given a user property is set, then set user property`() {
        firebaseAnalyticsClient.setUserProperty("name", "value")

        verify {
            firebaseAnalytics.setUserProperty("name", "value")
        }
    }

    @Test
    fun `Given an event is logged, then log event and register visit`() {
        val params = mapOf("param1" to "value1")
        firebaseAnalyticsClient.logEvent("event_name", params)

        verify {
            firebaseAnalytics.logEvent(any(), any())
        }
    }

    @Test
    fun `Given the firebase identifiers are not available, when an event is logged, log the event anyway`() {
        every { firebaseIdentifiers.userPseudoId } returns null
        every { firebaseIdentifiers.sessionId } returns null

        firebaseAnalyticsClient.logEvent("event_name", mapOf("param" to "value"))

        verify(exactly = 1) {
            firebaseAnalytics.logEvent("event_name", any())
        }
    }

    @Test
    fun `Given an event is logged, then refresh the Firebase identifiers`() {
        firebaseAnalyticsClient.logEvent("event_name", mapOf("param" to "value"))

        verify(exactly = 1) {
            firebaseIdentifiers.refresh()
        }
    }

    @Test
    fun `Given an ecommerce event is logged, then log ecommerce event and register visit`() {
        val ecommerceEvent = EcommerceEvent(
            itemListId = "list_id",
            itemListName = "list_name",
            items = emptyList(),
            totalItemCount = 0
        )
        firebaseAnalyticsClient.logEcommerceEvent("event_name", ecommerceEvent)

        verify {
            firebaseAnalytics.logEvent("event_name", any())
        }
    }

    @Test
    fun `Given the firebase identifiers are not available, when an ecommerce event is logged, log the event anyway`() {
        every { firebaseIdentifiers.userPseudoId } returns null
        every { firebaseIdentifiers.sessionId } returns null

        val ecommerceEvent = EcommerceEvent(
            itemListId = "list_id",
            itemListName = "list_name",
            items = emptyList(),
            totalItemCount = 0
        )

        firebaseAnalyticsClient.logEcommerceEvent("event_name", ecommerceEvent)

        verify(exactly = 1) {
            firebaseAnalytics.logEvent("event_name", any())
        }
    }

    @Test
    fun `Given an exception is logged, then log an exception`() {
        val exception = IllegalArgumentException()

        firebaseAnalyticsClient.logException(exception)

        verify {
            firebaseCrashlytics.recordException(exception)
        }
    }
}
