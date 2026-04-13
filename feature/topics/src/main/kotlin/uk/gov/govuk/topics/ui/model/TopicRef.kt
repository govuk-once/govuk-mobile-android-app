package uk.gov.govuk.topics.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import uk.gov.govuk.topics.R

internal enum class TopicRef(
    val ref: String,
    @StringRes val titleResId: Int,
    @DrawableRes val iconResId: Int
) {
    BENEFITS("benefits", R.string.benefits, R.drawable.ic_topic_benefits),
    BUSINESS("business", R.string.business, R.drawable.ic_topic_business),
    CARE("care", R.string.care, R.drawable.ic_topic_care),
    DRIVING_AND_TRANSPORT("driving-transport", R.string.driving_and_transport, R.drawable.ic_topic_transport),
    EMPLOYMENT("employment", R.string.employment, R.drawable.ic_topic_employment),
    HEALTH_DISABILITY("health-disability", R.string.health_and_disability, R.drawable.ic_topic_health),
    MONEY_TAX("money-tax", R.string.money_and_tax, R.drawable.ic_topic_money),
    PARENTING_GUARDIANSHIP("parenting-guardianship", R.string.parenting_and_guardianship, R.drawable.ic_topic_parenting),
    RETIREMENT("retirement", R.string.retirement, R.drawable.ic_topic_retirement),
    STUDYING_TRAINING("studying-training", R.string.studying_and_training, R.drawable.ic_topic_studying),
    TRAVEL_ABROAD("travel-abroad", R.string.travel_abroad, R.drawable.ic_topic_travel);

    companion object {
        fun fromString(ref: String?): TopicRef? {
            return entries.find { it.ref == ref }
        }
    }
}

/** Public helper for app module to check if topic is driving */
fun String?.isDrivingTopic(): Boolean = this == TopicRef.DRIVING_AND_TRANSPORT.ref
