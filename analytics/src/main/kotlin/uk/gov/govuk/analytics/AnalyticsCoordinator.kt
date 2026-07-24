package uk.gov.govuk.analytics

import com.qualtrics.digital.TargetingResult
import uk.gov.govuk.analytics.data.AnalyticsRepo
import uk.gov.govuk.analytics.data.local.AnalyticsEnabledState
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import javax.inject.Inject

class AnalyticsCoordinator @Inject constructor(
    private val analyticsRepo: AnalyticsRepo,
    private val firebaseAnalyticsClient: FirebaseAnalyticsClient,
    private val qualtricsAnalyticsClient: QualtricsAnalyticsClient
) : AnalyticsCoordinatorInterface {

    private val qualtricsTargetingId = "qualtrics_targeting_id"
    private val qualtricsSurveyShown = "qualtrics_survey_shown"
    private val qualtricsSurveyClosed = "qualtrics_survey_closed"

    override fun initialize() {
        // Respect the user's privacy preference
        if (analyticsRepo.analyticsEnabledState == AnalyticsEnabledState.ENABLED) {
            qualtricsAnalyticsClient.initialize()
            qualtricsAnalyticsClient.setOnSurveyClosedListener { targetingIds ->
                onSurveyClosed(targetingIds)
            }
        }
    }

    override fun logEvent(
        name: String,
        parameters: Map<String, Any>
    ) {
        firebaseAnalyticsClient.logEvent(
            name,
            parameters
        )
        qualtricsAnalyticsClient.logEvent(
            name,
            parameters,
            onSurveyShown = { results -> onSurveyShown(results) }
        )
    }

    override fun logEcommerceEvent(
        event: String,
        ecommerceEvent: EcommerceEvent,
        selectedItemIndex: Int?
    ) {
        firebaseAnalyticsClient.logEcommerceEvent(
            event,
            ecommerceEvent,
            selectedItemIndex
        )
        qualtricsAnalyticsClient.logEcommerceEvent(
            event,
            ecommerceEvent,
            onSurveyShown = { results -> onSurveyShown(results) }
        )
    }

    private fun onSurveyShown(results: Map<String, TargetingResult>) {
        results.forEach { (targetingId, result) ->
            if (result.passed()) {
                firebaseAnalyticsClient.logEvent(
                    qualtricsSurveyShown,
                    mapOf<String, Any>(
                        qualtricsTargetingId to targetingId
                    )
                )
            }
        }
    }

    private fun onSurveyClosed(targetingIds: List<String>) {
        targetingIds.forEach { targetingId ->
            firebaseAnalyticsClient.logEvent(
                qualtricsSurveyClosed,
                mapOf<String, Any>(
                    qualtricsTargetingId to targetingId
                )
            )
        }
    }
}
