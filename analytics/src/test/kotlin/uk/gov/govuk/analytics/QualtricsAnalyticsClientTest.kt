package uk.gov.govuk.analytics

import android.app.Activity
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.qualtrics.digital.IQualtricsProjectEvaluationCallback
import com.qualtrics.digital.Properties
import com.qualtrics.digital.Qualtrics
import com.qualtrics.digital.TargetingResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent

class QualtricsAnalyticsClientTest {

    private val context = mockk<Context>(relaxed = true)
    private val activity = mockk<Activity>(relaxed = true)
    private val qualtrics = mockk<Qualtrics>(relaxed = true)
    private val qualtricsProperties = mockk<Properties>(relaxed = true)
    private val activityProvider = mockk<ActivityProviderInterface>(relaxed = true)

    private lateinit var qualtricsAnalyticsClient: QualtricsAnalyticsClient

    @Before
    fun setUp() {
        qualtrics.properties = qualtricsProperties

        qualtricsAnalyticsClient = QualtricsAnalyticsClient(context, qualtrics, activityProvider)
    }

    @After
    fun tearDown() {
        qualtrics.properties = null
    }

    @Test
    fun `Given an initialization call, then the project is initialized`() {
        qualtricsAnalyticsClient.initialize()

        verify(exactly = 1) {
            qualtrics.initializeProject(any(), any(), any())
        }
    }

    @Test
    fun `Given the initial state, then isInitialized should be false`() {
        assertFalse(qualtricsAnalyticsClient.isInitialized)
    }

    @Test
    fun `Given an initialization call, when qualtrics is already initialized, then the project is not re-initialized`() {
        val qualtricsAnalyticsClient = mockk< QualtricsAnalyticsClient>(relaxed = true)

        every { qualtricsAnalyticsClient.isInitialized } returns true

        qualtricsAnalyticsClient.initialize()

        verify(exactly = 0) {
            qualtrics.initializeProject(any(), any(), any())
        }
    }

    @Test
    fun `Given an event, then log the event and set the property`() {
        val params = mapOf("text" to "value")

        qualtricsAnalyticsClient.logEvent("event_name", params)

        verify(exactly = 1) {
            qualtricsProperties.setString("text", "value")
        }
    }

    @Test
    fun `Given an event, then log event and register the view visit`() {
        val params = mapOf("text" to "value")

        qualtricsAnalyticsClient.logEvent("event_name", params)

        verify(exactly = 1) {
            qualtrics.registerViewVisit("event_name")
        }
    }

    @Test
    fun `Given an event, then log event and evaluate the project`() {
        val params = mapOf("text" to "value")

        qualtricsAnalyticsClient.logEvent("event_name", params)

        verify(exactly = 1) {
            qualtrics.evaluateProject(any())
        }
    }

    @Test
    fun `Given an event and the survey display is triggered, then display the survey`() {
        val params = mapOf("screen_name" to "Settings")
        val result = mockk<TargetingResult> {
            every { passed() } returns true
        }
        val mockResults = mapOf("survey" to result)
        val callbackSlot = slot<IQualtricsProjectEvaluationCallback>()

        every { activityProvider.currentActivity } returns activity
        every { qualtrics.evaluateProject(capture(callbackSlot)) } returns Unit

        qualtricsAnalyticsClient.logEvent("event_name", params)

        callbackSlot.captured.run(mockResults)

        verify(exactly = 1) {
            qualtrics.display(activity)
        }
    }

    @Test
    fun `Given an event and the survey display is not triggered, then do not display the survey`() {
        val params = mapOf("screen_name" to "Settings")
        val result = mockk<TargetingResult> {
            every { passed() } returns false
        }
        val mockResults = mapOf("survey" to result)
        val callbackSlot = slot<IQualtricsProjectEvaluationCallback>()

        every { qualtrics.evaluateProject(capture(callbackSlot)) } returns Unit

        qualtricsAnalyticsClient.logEvent("event_name", params)

        callbackSlot.captured.run(mockResults)

        verify(exactly = 0) {
            qualtrics.display(context)
        }
    }

    @Test
    fun `Given an ecommerce event, then log the event and set the properties`() {
        val ecommerceEvent = EcommerceEvent(
            itemListId = "list_id",
            itemListName = "list_name",
            items = emptyList(),
            totalItemCount = 0
        )

        qualtricsAnalyticsClient.logEcommerceEvent("event_name", ecommerceEvent)

        verify(exactly = 1) {
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_ID, "list_id")
            qualtricsProperties.setString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "list_name")
        }
    }

    @Test
    fun `Given an ecommerce event, then log the event and register the view visit`() {
        val ecommerceEvent = EcommerceEvent(
            itemListId = "list_id",
            itemListName = "list_name",
            items = emptyList(),
            totalItemCount = 0
        )

        qualtricsAnalyticsClient.logEcommerceEvent("event_name", ecommerceEvent)

        verify(exactly = 1) {
            qualtrics.registerViewVisit("event_name")
        }
    }

    @Test
    fun `Given an ecommerce event, then log the event and evaluate the project`() {
        val ecommerceEvent = EcommerceEvent(
            itemListId = "list_id",
            itemListName = "list_name",
            items = emptyList(),
            totalItemCount = 0
        )

        qualtricsAnalyticsClient.logEcommerceEvent("event_name", ecommerceEvent)

        verify(exactly = 1) {
            qualtrics.evaluateProject(any())
        }
    }

    @Test
    fun `Given an ecommerce event and the survey display is triggered, then display the survey`() {
        val ecommerceEvent = EcommerceEvent(
            itemListId = "list_id",
            itemListName = "list_name",
            items = emptyList(),
            totalItemCount = 0
        )
        val result = mockk<TargetingResult> {
            every { passed() } returns true
        }
        val mockResults = mapOf("survey" to result)
        val callbackSlot = slot<IQualtricsProjectEvaluationCallback>()

        every { activityProvider.currentActivity } returns activity
        every { qualtrics.evaluateProject(capture(callbackSlot)) } returns Unit

        qualtricsAnalyticsClient.logEcommerceEvent("event_name", ecommerceEvent)

        callbackSlot.captured.run(mockResults)

        verify(exactly = 1) {
            qualtrics.display(activity)
        }
    }

    @Test
    fun `Given an ecommerce event and the survey display is not triggered, then do not display the survey`() {
        val ecommerceEvent = EcommerceEvent(
            itemListId = "list_id",
            itemListName = "list_name",
            items = emptyList(),
            totalItemCount = 0
        )
        val result = mockk<TargetingResult> {
            every { passed() } returns false
        }
        val mockResults = mapOf("survey" to result)
        val callbackSlot = slot<IQualtricsProjectEvaluationCallback>()

        every { qualtrics.evaluateProject(capture(callbackSlot)) } returns Unit

        qualtricsAnalyticsClient.logEcommerceEvent("event_name", ecommerceEvent)

        callbackSlot.captured.run(mockResults)

        verify(exactly = 0) {
            qualtrics.display(context)
        }
    }
}
