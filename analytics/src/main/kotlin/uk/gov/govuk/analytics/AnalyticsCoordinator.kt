package uk.gov.govuk.analytics

import uk.gov.govuk.analytics.data.local.model.EcommerceEvent

interface AnalyticsCoordinatorInterface {
    fun logEvent(name: String, parameters: Map<String, Any>)
    fun logEcommerceEvent(event: String, ecommerceEvent: EcommerceEvent, selectedItemIndex: Int?)
}

class AnalyticsCoordinator(
    private val firebaseAnalyticsClient: FirebaseAnalyticsClient,
    private val qualtricsAnalyticsClient: QualtricsAnalyticsClient
) : AnalyticsCoordinatorInterface {

    override fun logEvent(name: String, parameters: Map<String, Any>) {
        firebaseAnalyticsClient.logEvent(name, parameters)
        qualtricsAnalyticsClient.logEvent(name, parameters)
    }

    override fun logEcommerceEvent(event: String, ecommerceEvent: EcommerceEvent, selectedItemIndex: Int?) {
        firebaseAnalyticsClient.logEcommerceEvent(event, ecommerceEvent, selectedItemIndex)
        qualtricsAnalyticsClient.logEcommerceEvent(event, ecommerceEvent)
    }
}
