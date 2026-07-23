package uk.gov.govuk.analytics

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.data.AnalyticsRepo
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import java.util.Locale

class AnalyticsCoordinatorTest {
    private val analyticsRepo = mockk<AnalyticsRepo>(relaxed = true)
    private val firebaseAnalyticClient = mockk<FirebaseAnalyticsClient>(relaxed = true)
    private val qualtricsAnalyticsClient = mockk<QualtricsAnalyticsClient>(relaxed = true)

    private lateinit var analyticsCoordinator: AnalyticsCoordinator

    @Before
    fun setup() {
        every { analyticsRepo.analyticsEnabledState } returns AnalyticsEnabledState.ENABLED

        analyticsCoordinator = AnalyticsCoordinator(
            analyticsRepo,
            firebaseAnalyticClient,
            qualtricsAnalyticsClient
        )
    }

    @Test
    fun `Given analytics consent has been given, when initialize is called, then call the Qualtrics initialize method`() = runTest {
        every { analyticsRepo.analyticsEnabledState } returns AnalyticsEnabledState.ENABLED

        analyticsCoordinator.initialize()

        verify {
            qualtricsAnalyticsClient.initialize()
        }
    }

    @Test
    fun `Given analytics consent has not been given, when initialize is called, then do not call the Qualtrics initialize method`() = runTest {
        every { analyticsRepo.analyticsEnabledState } returns AnalyticsEnabledState.NOT_SET

        analyticsCoordinator.initialize()

        verify(exactly = 0) {
            qualtricsAnalyticsClient.initialize()
        }
    }

    @Test
    fun `Given analytics consent has been disabled, when initialize is called, then do not call the Qualtrics initialize method`() = runTest {
        every { analyticsRepo.analyticsEnabledState } returns AnalyticsEnabledState.DISABLED

        analyticsCoordinator.initialize()

        verify(exactly = 0) {
            qualtricsAnalyticsClient.initialize()
        }
    }

    @Test
    fun `Given analytics consent has been given, when initialize is called, then register a survey closed listener`() = runTest {
        every { analyticsRepo.analyticsEnabledState } returns AnalyticsEnabledState.ENABLED

        analyticsCoordinator.initialize()

        verify {
            qualtricsAnalyticsClient.setOnSurveyClosedListener(any())
        }
    }

    @Test
    fun `Given a survey was closed, when the survey closed listener fires, then log a qualtrics_survey_closed event for each targeting id`() = runTest {
        every { analyticsRepo.analyticsEnabledState } returns AnalyticsEnabledState.ENABLED

        val listenerSlot = slot<(List<String>) -> Unit>()
        every { qualtricsAnalyticsClient.setOnSurveyClosedListener(capture(listenerSlot)) } returns Unit

        analyticsCoordinator.initialize()
        listenerSlot.captured.invoke(listOf("survey_one", "survey_two"))

        verify {
            firebaseAnalyticClient.logEvent(
                "qualtrics_survey_closed",
                mapOf<String, Any>("qualtrics_targeting_id" to "survey_one")

            )
            firebaseAnalyticClient.logEvent(
                "qualtrics_survey_closed",
                mapOf<String, Any>("qualtrics_targeting_id" to "survey_two")

            )
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
            qualtricsAnalyticsClient.logEvent(event, params, any())
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
                ecommerceEvent = ecommerceEvent,
                onSurveyShown = any()
            )
        }
    }
}
