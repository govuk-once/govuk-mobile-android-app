package uk.gov.govuk.topics.extension

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.domain.model.TopicItem
import uk.gov.govuk.topics.ui.model.TopicItemUi

class TopicItemTest{

    @Test
    fun `Given topic items, When mapping to topic item ui, then return a topic item ui with correct icon`() {
        val topicItems = listOf(
            Pair(R.drawable.ic_topic_benefits, TopicItem("benefits", "Title", "Description", true)),
            Pair(R.drawable.ic_topic_business, TopicItem("business", "Title", "Description", false)),
            Pair(R.drawable.ic_topic_care, TopicItem("care", "Title", "Description", true)),
            Pair(R.drawable.ic_topic_transport, TopicItem("driving-transport", "Title", "Description", false)),
            Pair(R.drawable.ic_topic_employment, TopicItem("employment", "Title", "Description", true)),
            Pair(R.drawable.ic_topic_health, TopicItem("health-disability", "Title", "Description", false)),
            Pair(R.drawable.ic_topic_money, TopicItem("money-tax", "Title", "Description", true)),
            Pair(R.drawable.ic_topic_parenting, TopicItem("parenting-guardianship", "Title", "Description", false)),
            Pair(R.drawable.ic_topic_retirement, TopicItem("retirement", "Title", "Description", true)),
            Pair(R.drawable.ic_topic_studying, TopicItem("studying-training", "Title", "Description", false)),
            Pair(R.drawable.ic_topic_travel, TopicItem("travel-abroad", "Title", "Description", true)),
            Pair(R.drawable.ic_topic_default, TopicItem("unknown", "Title", "Description", false)),
        )

        for (topicItem in topicItems) {
            val expected = TopicItemUi(
                ref = topicItem.second.ref,
                icon = topicItem.first,
                title = topicItem.second.title,
                description = topicItem.second.description,
                isSelected = topicItem.second.isSelected
            )

            assertEquals(topicItem.second.toTopicItemUi(), expected)
        }
    }
}