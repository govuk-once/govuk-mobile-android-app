package uk.gov.govuk.analytics

import uk.gov.govuk.analytics.data.local.model.EcommerceEvent

interface AnalyticsCoordinatorInterface {
    fun initialize()

    fun logEvent(
        name: String,
        parameters: Map<String, Any>
    )

    fun logEcommerceEvent(
        event: String,
        ecommerceEvent: EcommerceEvent,
        selectedItemIndex: Int?
    )
}
