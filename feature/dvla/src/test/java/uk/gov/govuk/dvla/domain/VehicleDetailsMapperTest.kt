package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.VehicleEnquiryResponse

class VehicleDetailsMapperTest {

    @Test
    fun `Given fully populated VehicleEnquiryResponse, when mapped to domain model, it maps all fields correctly`() {
        val networkResponse = mockk<VehicleEnquiryResponse>(relaxed = true) {
            every { registrationNumber } returns "AA19AAA"
            every { make } returns "FORD"
            every { colour } returns "RED"
            every { yearOfManufacture } returns 2019
            every { taxStatus?.name } returns "Taxed"
            every { taxDueDate } returns "2027-04-30T00:00:00.000Z"
            every { motStatus?.name } returns "No details held by DVLA"
            every { motExpiryDate } returns "2025-05-20T00:00:00.000Z"
            every { fuelType } returns "PETROL"
            every { engineCapacity } returns 2000
            every { co2Emissions } returns 300
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals("AA19AAA", domainModel.registrationNumber)
        assertEquals("FORD", domainModel.make)
        assertEquals("RED", domainModel.colour)
        assertEquals(2019, domainModel.yearOfManufacture)
        assertEquals("Taxed", domainModel.taxStatus)
        assertEquals("2027-04-30T00:00:00.000Z", domainModel.taxDueDate)
        assertEquals("No details held by DVLA", domainModel.motStatus)
        assertEquals("2025-05-20T00:00:00.000Z", domainModel.motExpiryDate)
        assertEquals("PETROL", domainModel.fuelType)
        assertEquals(2000, domainModel.engineCapacity)
        assertEquals(300, domainModel.co2Emissions)
    }

    @Test
    fun `Given VehicleEnquiryResponse with missing fields, when mapped, it sets correct defaults`() {
        val networkResponse = mockk<VehicleEnquiryResponse>(relaxed = true) {
            every { registrationNumber } returns "AA19AAA"
            every { make } returns null
            every { colour } returns null
            every { yearOfManufacture } returns null
            every { taxStatus } returns null
            every { taxDueDate } returns null
            every { motStatus } returns null
            every { motExpiryDate } returns null
            every { fuelType } returns null
            every { engineCapacity } returns null
            every { co2Emissions } returns null
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals("AA19AAA", domainModel.registrationNumber)
        assertEquals("", domainModel.make)
        assertEquals("", domainModel.colour)
        assertNull(domainModel.yearOfManufacture)
        assertEquals("", domainModel.taxStatus)
        assertNull(domainModel.taxDueDate)
        assertEquals("", domainModel.motStatus)
        assertNull(domainModel.motExpiryDate)
        assertEquals("", domainModel.fuelType)
        assertNull(domainModel.engineCapacity)
        assertNull(domainModel.co2Emissions)
    }
}
