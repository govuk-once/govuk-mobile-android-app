package uk.gov.govuk.topics.extension

import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.domain.model.TopicItem
import uk.gov.govuk.topics.ui.model.TopicItemUi

internal fun TopicItem.toTopicItemUi(): TopicItemUi {
    val icon = when (ref) {
        "benefits" -> R.drawable.ic_topic_benefits
        "business" -> R.drawable.ic_topic_business
        "care" -> R.drawable.ic_topic_care
        "driving-transport" -> R.drawable.ic_topic_transport
        "employment" -> R.drawable.ic_topic_employment
        "health-disability" -> R.drawable.ic_topic_health
        "money-tax" -> R.drawable.ic_topic_money
        "parenting-guardianship" -> R.drawable.ic_topic_parenting
        "retirement" -> R.drawable.ic_topic_retirement
        "studying-training" -> R.drawable.ic_topic_studying
        "travel-abroad" -> R.drawable.ic_topic_travel
        else -> R.drawable.ic_topic_default
    }

    return TopicItemUi(
        ref = ref,
        icon = icon,
        title = title,
        description = description,
        isSelected = isSelected
    )
}
