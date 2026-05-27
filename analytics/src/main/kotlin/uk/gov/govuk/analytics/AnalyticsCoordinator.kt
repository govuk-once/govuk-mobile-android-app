package uk.gov.govuk.analytics

import javax.inject.Inject
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent

class AnalyticsCoordinator @Inject constructor(
    private val firebaseAnalyticsClient: FirebaseAnalyticsClient,
    private val qualtricsAnalyticsClient: QualtricsAnalyticsClient
) : AnalyticsCoordinatorInterface {

    override fun initialize() {
        qualtricsAnalyticsClient.initialize()
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
            parameters
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
            ecommerceEvent
        )
    }
}
