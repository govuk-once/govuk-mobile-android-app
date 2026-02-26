package uk.gov.govuk.topics.extension

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import uk.gov.govuk.topics.R
import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionTest {
    private val context = mockk<Context>(relaxed = true)

    @Test
    fun `Given a topic reference, When mapping to topic name, then return correct topic name`() {

        every { context.getString(R.string.benefits) } returns "Benefits"
        every { context.getString(R.string.business) } returns "Business"
        every { context.getString(R.string.care) } returns "Care"
        every { context.getString(R.string.driving_and_transport) } returns "Driving and transport"
        every { context.getString(R.string.employment) } returns "Employment"
        every { context.getString(R.string.health_and_disability) } returns "Health and disability"
        every { context.getString(R.string.money_and_tax) } returns "Money and tax"
        every { context.getString(R.string.parenting_and_guardianship) } returns "Parenting and guardianship"
        every { context.getString(R.string.retirement) } returns "Retirement"
        every { context.getString(R.string.studying_and_training) } returns "Studying and training"
        every { context.getString(R.string.travel_abroad) } returns "Travel abroad"

        val topicReferenceAndName = listOf(
            Pair("benefits", "Benefits"),
            Pair("business", "Business"),
            Pair("care", "Care"),
            Pair("driving-transport", "Driving and transport"),
            Pair("employment", "Employment"),
            Pair("health-disability", "Health and disability"),
            Pair("money-tax", "Money and tax"),
            Pair("parenting-guardianship", "Parenting and guardianship"),
            Pair("retirement", "Retirement"),
            Pair("studying-training", "Studying and training"),
            Pair("travel-abroad", "Travel abroad")
        )

        topicReferenceAndName.forEach {
            assertEquals(it.second, it.first.toTopicName(context))
        }
    }

    @Test
    fun `Given a topic reference that we don't have a string resource for, When mapping to topic name, then return formatted reference`() {
        val unknownTopicReference = Pair("unknown-reference", "Unknown reference")
        assertEquals(unknownTopicReference.second, unknownTopicReference.first.toTopicName(context))
    }
}