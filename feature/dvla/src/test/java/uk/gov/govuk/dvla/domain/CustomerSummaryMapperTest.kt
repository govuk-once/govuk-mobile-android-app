package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.CustomerSummaryResponse

class CustomerSummaryMapperTest {

    @Test
    fun `Given fully populated CustomerSummaryResponse, when mapped to domain model, it maps all fields correctly`() {
        val networkResponse = mockk<CustomerSummaryResponse>(relaxed = true) {
            every { customerResponse.customer.customerId } returns "8bc39b9f-9cdc-4dc6-8364-632d1f7b5916"
            every { customerResponse.customer.recordStatus } returns "Substantive"
            every { customerResponse.customer.emailAddress } returns "kriss.bennett@digital.cabinet-office.gov.uk"
            every { customerResponse.customer.individualDetails.firstNames } returns "KENNETH"
            every { customerResponse.customer.individualDetails.lastName } returns "DECERQUEIRA"
            every { customerResponse.customer.individualDetails.dateOfBirth } returns "1965-07-08"
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals("8bc39b9f-9cdc-4dc6-8364-632d1f7b5916", domainModel.customerId)
        assertEquals("Substantive", domainModel.recordStatus)
        assertEquals("kriss.bennett@digital.cabinet-office.gov.uk", domainModel.emailAddress)
        assertEquals("KENNETH", domainModel.firstName)
        assertEquals("DECERQUEIRA", domainModel.lastName)
        assertEquals("1965-07-08", domainModel.dateOfBirth)
    }

    @Test
    fun `Given CustomerSummaryResponse with missing fields, when mapped, it sets correct defaults`() {
        val networkResponse = mockk<CustomerSummaryResponse>(relaxed = true) {
            every { customerResponse.customer.customerId } returns "8bc39b9f-9cdc-4dc6-8364-632d1f7b5916"
            every { customerResponse.customer.recordStatus } returns "Substantive"
            every { customerResponse.customer.individualDetails.lastName } returns "DECERQUEIRA"
            every { customerResponse.customer.individualDetails.dateOfBirth } returns "1965-07-08"
            every { customerResponse.customer.individualDetails.firstNames } returns null
            every { customerResponse.customer.emailAddress } returns null
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals("8bc39b9f-9cdc-4dc6-8364-632d1f7b5916", domainModel.customerId)
        assertEquals("DECERQUEIRA", domainModel.lastName)
        assertEquals("1965-07-08", domainModel.dateOfBirth)
        assertEquals("Substantive", domainModel.recordStatus)
        assertEquals("", domainModel.firstName)
        assertNull(domainModel.emailAddress)
    }
}
