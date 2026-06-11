package uk.gov.govuk.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.qualtrics.digital.Qualtrics
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import javax.inject.Inject

class QualtricsAnalyticsClient @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val qualtrics: Qualtrics,
    private val activityProvider: ActivityProviderInterface
) {

    internal var isInitialized = false

    fun initialize() {
        if (isInitialized) return

        qualtrics.initializeProject(
            BuildConfig.QUALTRICS_BRAND_ID,
            BuildConfig.QUALTRICS_PROJECT_ID,
            context
        )
        isInitialized = true
    }

    fun logEvent(eventName: String, parameters: Map<String, Any>) {
        setParameters(parameters)

        registerVisitAndEvaluateForTriggers(eventName)
    }

    fun logEcommerceEvent(eventName: String, ecommerceEvent: EcommerceEvent) {
        setParameters(
            mapOf(
                FirebaseAnalytics.Param.ITEM_LIST_ID to ecommerceEvent.itemListId,
                FirebaseAnalytics.Param.ITEM_LIST_NAME to ecommerceEvent.itemListName
            )
        )

        registerVisitAndEvaluateForTriggers(eventName)
    }

    private fun registerVisitAndEvaluateForTriggers(eventName: String) {
        qualtrics.registerViewVisit(eventName)

        qualtrics.evaluateProject { results ->
            if (results.values.any { it.passed() }) {
                activityProvider.currentActivity?.let { activity ->
                    qualtrics.display(activity)
                }
            }
        }
    }

    /**
     * The Qualtrics SDK data storage mechanism is implemented as a single (flat) map
     * that is cached across events. So, it does not handle objects for sending
     * data - specifically maps, arrays of maps and nested arrays. It also means we
     * need to flat-map all the keys and values we would ever want to send - making
     * the keys unique in some way - for example, e-commerce events that have 'items'.
     *
     * As a consequence of the above - if a key is not overwritten in newer events
     * - it will be resent in subsequent events, causing incorrect event data to
     * be 'leaked' across events.
     *
     * This seems to be a deliberate 'feature'.
     *
     * To date, the only solution to this seems to be creating a unique, defined and
     * distinct set of keys that are set to the new events value or an empty string
     * before being sent. It seems this is the only way to ensure that only valid
     * data is sent.
     */
    private val analyticsParameterKeys = listOf(
        "action", "external", "item_list_id", "item_list_name",
        "language", "screen_class", "screen_name", "screen_title",
        "section", "text", "type", "url"
    )

    private fun setParameters(parameters: Map<String, Any>) {
        analyticsParameterKeys.forEach { key ->
            qualtrics.properties.setString(key, parameters[key]?.toString() ?: "")
        }
    }
}
