package uk.gov.govuk.analytics

import android.app.Activity
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.qualtrics.digital.IQualtricsProjectEvaluationCallback
import com.qualtrics.digital.Properties
import com.qualtrics.digital.Qualtrics
import com.qualtrics.digital.QualtricsPopOverActivity
import com.qualtrics.digital.QualtricsSurveyActivity
import com.qualtrics.digital.TargetingResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
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
    private val firebaseIdentifiers = mockk<FirebaseIdentifiers>(relaxed = true)
    private val activityProvider = mockk<ActivityProviderInterface>(relaxed = true)

    private lateinit var qualtricsAnalyticsClient: QualtricsAnalyticsClient

    @Before
    fun setUp() {
        qualtrics.properties = qualtricsProperties

        qualtricsAnalyticsClient = QualtricsAnalyticsClient(context, qualtrics, firebaseIdentifiers, activityProvider)
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
    fun `Given an initialization call, then a destroyed listener is registered`() {
        qualtricsAnalyticsClient.initialize()

        verify(exactly = 1) {
            activityProvider.addOnActivityDestroyedListener(any())
        }
    }

    @Test
    fun `Given a survey was shown, when the popover activity is destroyed, then notify the survey closed listener with the targeting ids`() {
        val passedResult = mockk<TargetingResult> {
            every { passed() } returns true
        }
        val failedResult = mockk<TargetingResult> {
            every { passed() } returns false
        }
        val mockResults = mapOf("passed_survey" to passedResult, "failed_survey" to failedResult)
        val evaluationCallbackSlot = slot<IQualtricsProjectEvaluationCallback>()
        val destroyedListenerSlot = slot<(Activity) -> Unit>()
        val onSurveyClosed = mockk<(List<String>) -> Unit>(relaxed = true)
        val popOverActivity = mockk<QualtricsPopOverActivity>(relaxed = true)

        every { activityProvider.currentActivity } returns activity
        every { qualtrics.evaluateProject(capture(evaluationCallbackSlot)) } returns Unit
        every { activityProvider.addOnActivityDestroyedListener(capture(destroyedListenerSlot)) } returns Unit

        qualtricsAnalyticsClient.initialize()
        qualtricsAnalyticsClient.setOnSurveyClosedListener { onSurveyClosed(it) }
        qualtricsAnalyticsClient.logEvent("event_name", mapOf("screen_name" to "Chat"))

        evaluationCallbackSlot.captured.run(mockResults)
        destroyedListenerSlot.captured.invoke(popOverActivity)

        verify(exactly = 1) {
            onSurveyClosed(listOf("passed_survey"))
        }
    }

    @Test
    fun `Given a survey was shown, when the survey activity is destroyed, then notify the survey closed listener with the targeting ids`() {
        val passedResult = mockk<TargetingResult> {
            every { passed() } returns true
        }
        val mockResults = mapOf("passed_survey" to passedResult)
        val evaluationCallbackSlot = slot<IQualtricsProjectEvaluationCallback>()
        val destroyedListenerSlot = slot<(Activity) -> Unit>()
        val onSurveyClosed = mockk<(List<String>) -> Unit>(relaxed = true)
        val surveyActivity = mockk<QualtricsSurveyActivity>(relaxed = true)

        every { activityProvider.currentActivity } returns activity
        every { qualtrics.evaluateProject(capture(evaluationCallbackSlot)) } returns Unit
        every { activityProvider.addOnActivityDestroyedListener(capture(destroyedListenerSlot)) } returns Unit

        qualtricsAnalyticsClient.initialize()
        qualtricsAnalyticsClient.setOnSurveyClosedListener { onSurveyClosed(it) }
        qualtricsAnalyticsClient.logEvent("event_name", mapOf("screen_name" to "Chat"))

        evaluationCallbackSlot.captured.run(mockResults)
        destroyedListenerSlot.captured.invoke(surveyActivity)

        verify(exactly = 1) {
            onSurveyClosed(listOf("passed_survey"))
        }
    }

    @Test
    fun `Given no survey was shown, when a Qualtrics activity is destroyed, then notify the survey closed listener with no targeting ids`() {
        val destroyedListenerSlot = slot<(Activity) -> Unit>()
        val onSurveyClosed = mockk<(List<String>) -> Unit>(relaxed = true)
        val popOverActivity = mockk<QualtricsPopOverActivity>(relaxed = true)

        every { activityProvider.addOnActivityDestroyedListener(capture(destroyedListenerSlot)) } returns Unit

        qualtricsAnalyticsClient.initialize()
        qualtricsAnalyticsClient.setOnSurveyClosedListener { onSurveyClosed(it) }

        destroyedListenerSlot.captured.invoke(popOverActivity)

        verify(exactly = 1) {
            onSurveyClosed(emptyList())
        }
    }

    @Test
    fun `Given an unrelated activity is destroyed, then do not notify the survey closed listener`() {
        val destroyedListenerSlot = slot<(Activity) -> Unit>()
        val onSurveyClosed = mockk<(List<String>) -> Unit>(relaxed = true)

        every { activityProvider.addOnActivityDestroyedListener(capture(destroyedListenerSlot)) } returns Unit

        qualtricsAnalyticsClient.initialize()
        qualtricsAnalyticsClient.setOnSurveyClosedListener { onSurveyClosed(it) }

        destroyedListenerSlot.captured.invoke(activity)

        verify(exactly = 0) {
            onSurveyClosed(any())
        }
    }

    @Test
    fun `Given the Firebase identifiers are cached, when an event is logged, then set them before registering the view visit`() {
        every { firebaseIdentifiers.userPseudoId } returns "user_pseudo_id"
        every { firebaseIdentifiers.sessionId } returns "session_id"

        qualtricsAnalyticsClient.logEvent("event_name", mapOf("key" to "value"))

        verifyOrder {
            qualtricsProperties.setString("fb_user_pseudo_id", "user_pseudo_id")
            qualtricsProperties.setString("fb_session_id", "session_id")
            qualtrics.registerViewVisit("event_name")
        }
    }

    @Test
    fun `Given the Firebase identifiers are not yet available, when an event is logged, then do not set them`() {
        every { firebaseIdentifiers.userPseudoId } returns null
        every { firebaseIdentifiers.sessionId } returns null

        qualtricsAnalyticsClient.logEvent("event_name", mapOf("key" to "value"))

        verify(exactly = 0) {
            qualtricsProperties.setString("fb_user_pseudo_id", any())
            qualtricsProperties.setString("fb_session_id", any())
        }
    }

    @Test
    fun `Given an event, then refresh the Firebase identifiers`() {
        qualtricsAnalyticsClient.logEvent("event_name", mapOf("key" to "value"))

        verify(exactly = 1) {
            firebaseIdentifiers.refresh()
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
