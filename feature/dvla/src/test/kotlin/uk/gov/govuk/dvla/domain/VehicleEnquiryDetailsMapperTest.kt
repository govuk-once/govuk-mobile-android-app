package uk.gov.govuk.dvla.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.VehicleEnquiryResponse
import uk.gov.govuk.dvla.remote.model.common.FuelType as RemoteFuelType
import uk.gov.govuk.dvla.remote.model.common.MotStatus as RemoteMotStatus
import uk.gov.govuk.dvla.remote.model.common.TaxStatus as RemoteTaxStatus
import uk.gov.govuk.dvla.remote.model.common.VehicleColour as RemoteVehicleColour
import java.time.LocalDate

class VehicleEnquiryDetailsMapperTest {

    @Test
    fun `Given fully populated VehicleEnquiryResponse, when mapped to domain model, all fields map correctly`() {
        val response = VehicleEnquiryResponse(
            vehicle = VehicleEnquiryResponse.Vehicle(
                vehicleId = 119964152,
                registrationNumber = "AA19AAA",
                taxStatus = RemoteTaxStatus.TAXED,
                taxedUntil = "2027-04-30",
                motStatus = RemoteMotStatus.NO_DETAILS_HELD,
                motExpiryDate = "2025-05-20",
                make = "FORD",
                dateOfFirstRegistration = "2018-04-01",
                engineCapacity = 2000,
                exhaustEmissionsCo2 = 300,
                fuelType = RemoteFuelType.PETROL,
                colour = RemoteVehicleColour.RED,
                secondaryColour = RemoteVehicleColour.BLUE
            )
        )

        val domain = response.toDomainModel()

        assertEquals(119964152, domain.vehicleId)
        assertEquals("AA19AAA", domain.registration)
        assertEquals("FORD", domain.make)
        assertEquals(TaxStatus.TAXED, domain.taxStatus)
        assertEquals(LocalDate.of(2027, 4, 30), domain.taxExpiryDate)
        assertEquals(MotStatus.NO_DETAILS_HELD, domain.motStatus)
        assertEquals(LocalDate.of(2025, 5, 20), domain.motExpiryDate)
        assertEquals(LocalDate.of(2018, 4, 1), domain.dateOfFirstRegistration)
        assertEquals(FuelType.PETROL, domain.fuelType)
        assertEquals(VehicleColour.RED, domain.colour)
        assertEquals(VehicleColour.BLUE, domain.secondaryColour)
        assertEquals(2000, domain.engineCapacity)
        assertEquals(300, domain.exhaustEmissionsCo2)
    }

    @Test
    fun `Given VehicleEnquiryResponse with null fields, when mapped, enums default to UNKNOWN and dates to null`() {
        val response = VehicleEnquiryResponse(
            vehicle = VehicleEnquiryResponse.Vehicle(
                vehicleId = 119964152,
                registrationNumber = "AA19AAA",
                taxStatus = null,
                taxedUntil = null,
                motStatus = null,
                motExpiryDate = null,
                make = null,
                dateOfFirstRegistration = null,
                engineCapacity = null,
                exhaustEmissionsCo2 = null,
                fuelType = null,
                colour = null,
                secondaryColour = null
            )
        )

        val domain = response.toDomainModel()

        assertEquals(119964152, domain.vehicleId)
        assertEquals("AA19AAA", domain.registration)
        assertNull(domain.make)
        assertEquals(TaxStatus.UNKNOWN, domain.taxStatus)
        assertNull(domain.taxExpiryDate)
        assertEquals(MotStatus.UNKNOWN, domain.motStatus)
        assertNull(domain.motExpiryDate)
        assertNull(domain.dateOfFirstRegistration)
        assertEquals(FuelType.OTHER, domain.fuelType)
        assertEquals(VehicleColour.UNKNOWN, domain.colour)
        assertEquals(VehicleColour.UNKNOWN, domain.secondaryColour)
        assertNull(domain.engineCapacity)
        assertNull(domain.exhaustEmissionsCo2)
    }
}
