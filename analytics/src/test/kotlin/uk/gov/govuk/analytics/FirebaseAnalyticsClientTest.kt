package uk.gov.govuk.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.qualtrics.digital.Properties
import com.qualtrics.digital.Qualtrics
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class FirebaseAnalyticsClientTest {

    private val context = mockk<Context>(relaxed = true)
    private val firebaseAnalytics = mockk<FirebaseAnalytics>(relaxed = true)
    private val firebaseCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
    private val qualtrics = mockk<Qualtrics>(relaxed = true)
    private val qualtricsProperties = mockk<Properties>(relaxed = true)

    private lateinit var firebaseAnalyticsClient: FirebaseAnalyticsClient

    @Before
    fun setup() {
        qualtrics.properties = qualtricsProperties
        firebaseAnalyticsClient = FirebaseAnalyticsClient(
            context,
            firebaseAnalytics,
            firebaseCrashlytics,
            qualtrics
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
            qualtricsProperties.setString("name", "value")
        }
    }

    @Test
    fun `Given an event is logged, then log event and register visit`() {
        val params = mapOf("param1" to "value1")
        firebaseAnalyticsClient.logEvent("event_name", params)

        verify {
            firebaseAnalytics.logEvent(any(), any())
            qualtricsProperties.setString("param1", "value1")
            qualtrics.registerViewVisit("event_name")
            qualtrics.evaluateProject(any())
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
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_ID, "list_id")
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "list_name")
            qualtrics.registerViewVisit("event_name")
            qualtrics.evaluateProject(any())
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
