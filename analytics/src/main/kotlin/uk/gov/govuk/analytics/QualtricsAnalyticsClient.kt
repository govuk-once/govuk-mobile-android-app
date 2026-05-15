package uk.gov.govuk.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.qualtrics.digital.Qualtrics
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import javax.inject.Inject

class QualtricsAnalyticsClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val qualtrics: Qualtrics
) {

    fun logEvent(
        name: String,
        parameters: Map<String, Any>
    ) {
        parameters.forEach { (key, value) ->
            qualtrics.properties.setString(key, value.toString())
        }

        val qualtricsEventName = if (name == FirebaseAnalytics.Event.SCREEN_VIEW) {
            parameters[FirebaseAnalytics.Param.SCREEN_NAME] as? String ?: name
        } else {
            name
        }

        processQualtricsDisplay(qualtricsEventName)
    }

    fun logEcommerceEvent(
        event: String,
        ecommerceEvent: EcommerceEvent,
        selectedItemIndex: Int? = null
    ) {
        qualtrics.properties.setString(FirebaseAnalytics.Param.ITEM_LIST_ID, ecommerceEvent.itemListId)
        qualtrics.properties.setString(FirebaseAnalytics.Param.ITEM_LIST_NAME, ecommerceEvent.itemListName)
        qualtrics.properties.setString("total_item_count", ecommerceEvent.totalItemCount.toString())

        if (ecommerceEvent.items.any()) {
            if (selectedItemIndex != null) {
                selectedItemIndex.let { index ->
                    val item = ecommerceEvent.items.getOrNull(index)
                    item?.let {
                        addItem(index, item)
                    }
                }
            } else {
                ecommerceEvent.items.forEachIndexed { index, item ->
                    addItem(index, item)
                }
            }
        } else {
            qualtrics.properties.setString("items", "[]")
        }

        processQualtricsDisplay(event)
    }

    fun setUserProperty(name: String, value: String) {
        qualtrics.properties.setString(name, value)
    }

    private fun addItem(index: Int, item: EcommerceEvent.Item) {
        qualtrics.properties.setString("item_id_$index", item.itemId)
        qualtrics.properties.setString("item_name_$index", item.itemName)
        qualtrics.properties.setString("item_category_$index", item.itemCategory)
        qualtrics.properties.setString("item_location_id_$index", item.locationId)
        qualtrics.properties.setString("item_term_$index", item.term)
    }

    private fun processQualtricsDisplay(eventName: String) {
        qualtrics.registerViewVisit(eventName)

        qualtrics.evaluateProject { results ->
            if (results.values.any { it.passed() }) {
                qualtrics.display(context)
            }
        }
    }
}
