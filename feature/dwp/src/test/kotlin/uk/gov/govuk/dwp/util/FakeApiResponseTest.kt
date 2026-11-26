package uk.gov.govuk.dwp.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FakeApiResponseTest {
    @Test
    fun `converts the JSON response into a AwardDetailsResponse object`() {
        val response = latestUpcomingPayments()

        assertEquals("success", response.status)
        assertEquals("200", response.statusCode)
    }

    @Test
    fun `converts the JSON response into a PaymentsQueryResponse object`() {
        val response = historicalPayments()

        assertEquals("success", response.status)
        assertEquals("200", response.statusCode)
    }

    @Test
    fun `converts the content item HTML table into a map of benefit and frequency`() {
        val benefitFrequencies = mapOf(
            "Attendance Allowance" to "Usually every 4 weeks",
            "Carer's Allowance" to "Weekly in advance or every 4 weeks",
            "Child Benefit" to "Usually every 4 weeks - or weekly if you’re a single parent or you or your partner get certain benefits.",
            "Disability Living Allowance" to "Usually every 4 weeks",
            "Employment and Support Allowance" to "Usually every 2 weeks",
            "Jobseeker's Allowance" to "Usually every 2 weeks",
            "Maternity Allowance" to "Every 2 or 4 weeks",
            "Pension Credit" to "Usually every 4 weeks",
            "Personal Independence Payment" to "Usually every 4 weeks",
            "State Pension" to "Usually every 4 weeks",
            "Universal Credit" to "Every month"
        )

        assertEquals(benefitFrequencies, benefitFrequencyContentItem())
    }
}
