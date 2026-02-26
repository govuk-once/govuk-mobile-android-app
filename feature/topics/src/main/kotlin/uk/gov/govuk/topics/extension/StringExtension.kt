package uk.gov.govuk.topics.extension

import android.content.Context
import uk.gov.govuk.topics.R
import java.util.Locale

/**
 * Maps a topic reference to a topic name and returns it.
 * If the reference cannot be mapped then the reference is formatted and returned.
 */
fun String.toTopicName(context: Context): String {
    return when (this) {
        "benefits" -> context.getString(R.string.benefits)
        "business" -> context.getString(R.string.business)
        "care" -> context.getString(R.string.care)
        "driving-transport" -> context.getString(R.string.driving_and_transport)
        "employment" -> context.getString(R.string.employment)
        "health-disability" -> context.getString(R.string.health_and_disability)
        "money-tax" -> context.getString(R.string.money_and_tax)
        "parenting-guardianship" -> context.getString(R.string.parenting_and_guardianship)
        "retirement" -> context.getString(R.string.retirement)
        "studying-training" -> context.getString(R.string.studying_and_training)
        "travel-abroad" -> context.getString(R.string.travel_abroad)
        else -> this.replace("-", " ")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }
}