package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.VehicleEnquiryResponse
import java.time.LocalDate

import uk.gov.govuk.dvla.remote.model.common.TaxStatus as RemoteTaxStatus
import uk.gov.govuk.dvla.remote.model.common.MotStatus as RemoteMotStatus
import uk.gov.govuk.dvla.remote.model.common.VehicleColour as RemoteVehicleColour
import uk.gov.govuk.dvla.remote.model.common.FuelType as RemoteFuelType

class VesVehicleMapperTest {

    @Test
    fun `Given fully populated VehicleEnquiryResponse, when mapped to domain model, it maps all fields correctly`() {
        val networkResponse = mockk<VehicleEnquiryResponse>(relaxed = true) {
            every { registrationNumber } returns "AA19AAA"
            every { make } returns "FORD"
            every { colour } returns RemoteVehicleColour.RED
            every { yearOfManufacture } returns 2019
            every { taxStatus } returns RemoteTaxStatus.TAXED
            every { taxDueDate } returns "1988-10-25"
            every { motStatus } returns RemoteMotStatus.NO_DETAILS_HELD
            every { motExpiryDate } returns "1988-11-25"
            every { fuelType } returns RemoteFuelType.PETROL
            every { engineCapacity } returns 2000
            every { co2Emissions } returns 300
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals("AA19AAA", domainModel.registrationNumber)
        assertEquals("FORD", domainModel.make)
        assertEquals(VehicleColour.RED, domainModel.colour)
        assertEquals(2019, domainModel.yearOfManufacture)
        assertEquals(TaxStatus.TAXED, domainModel.taxStatus)
        assertEquals(LocalDate.of(1988, 10, 25), domainModel.taxDueDate)
        assertEquals(MotStatus.NO_DETAILS_HELD, domainModel.motStatus)
        assertEquals(LocalDate.of(1988, 11, 25), domainModel.motExpiryDate)
        assertEquals(FuelType.PETROL, domainModel.fuelType)
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
        assertEquals(VehicleColour.UNKNOWN, domainModel.colour)
        assertNull(domainModel.yearOfManufacture)
        assertEquals(TaxStatus.UNKNOWN, domainModel.taxStatus)
        assertEquals(MotStatus.UNKNOWN, domainModel.motStatus)
        assertNull(domainModel.taxDueDate)
        assertNull(domainModel.motExpiryDate)
        assertEquals(FuelType.OTHER, domainModel.fuelType)
        assertNull(domainModel.engineCapacity)
        assertNull(domainModel.co2Emissions)
    }
}
