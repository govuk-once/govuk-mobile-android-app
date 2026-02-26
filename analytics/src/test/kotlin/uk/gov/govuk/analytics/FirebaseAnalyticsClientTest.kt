package uk.gov.govuk.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class FirebaseAnalyticsClientTest {

    private val firebaseAnalytics = mockk<FirebaseAnalytics>(relaxed = true)
    private val firebaseCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)

    private lateinit var firebaseAnalyticsClient: FirebaseAnalyticsClient

    @Before
    fun setup() {
        firebaseAnalyticsClient = FirebaseAnalyticsClient(firebaseAnalytics, firebaseCrashlytics)
    }

    @Test
    fun `Given analytics have been enabled, then enable`() {
        firebaseAnalyticsClient.enable()

        verify {
            firebaseAnalytics.setAnalyticsCollectionEnabled(true)
            firebaseCrashlytics.setCrashlyticsCollectionEnabled(true)
        }
    }

    @Test
    fun `Given analytics have been disabled, then disable`() {
        firebaseAnalyticsClient.disable()

        verify {
            firebaseAnalytics.setAnalyticsCollectionEnabled(false)
            firebaseCrashlytics.setCrashlyticsCollectionEnabled(false)
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
    fun `Given an exception is logged, then log an exception`() {
        val exception = IllegalArgumentException()

        firebaseAnalyticsClient.logException(exception)

        verify {
            firebaseCrashlytics.recordException(exception)
        }
    }
}
