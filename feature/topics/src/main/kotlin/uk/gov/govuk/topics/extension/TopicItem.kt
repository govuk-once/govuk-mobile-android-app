package uk.gov.govuk.topics.extension

import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.domain.model.TopicItem
import uk.gov.govuk.topics.ui.model.TopicItemUi
import uk.gov.govuk.topics.ui.model.TopicRef

internal fun TopicItem.toTopicItemUi(): TopicItemUi {
    val topicRef = TopicRef.fromString(ref)
    val icon = topicRef?.iconResId ?: R.drawable.ic_topic_default

    return TopicItemUi(
        ref = ref,
        icon = icon,
        title = title,
        description = description,
        isSelected = isSelected
    )
}
