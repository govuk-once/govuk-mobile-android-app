package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.ExhaustEmissions
import uk.gov.govuk.dvla.remote.model.Vehicle
import uk.gov.govuk.dvla.remote.model.common.VehicleColour as RemoteVehicleColour
import uk.gov.govuk.dvla.remote.model.common.MotStatus as RemoteMotStatus
import uk.gov.govuk.dvla.remote.model.common.TaxStatus as RemoteTaxStatus
import uk.gov.govuk.dvla.remote.model.common.FuelType as RemoteFuelType
import java.time.LocalDate

class CustomerVehicleMapperTest {

    @Test
    fun `Given a fully populated Vehicle, when mapped toCustomerVehicle, it maps all fields correctly`() {
        val remoteVehicle = mockk<Vehicle> {
            every { registrationNumber } returns "AA19AAA"
            every { make } returns "HONDA"
            every { model } returns "CIVIC"
            every { taxClass } returns "Petrol Car"
            every { taxedUntil } returns "2025-12-01"
            every { motExpiryDate } returns "2026-06-01"
            every { taxStatus } returns RemoteTaxStatus.TAXED
            every { motStatus } returns RemoteMotStatus.NO_DETAILS_HELD
            every { dateOfFirstRegistration } returns "2020-06-01"
            every { fuelType } returns RemoteFuelType.PETROL
            every { colour } returns RemoteVehicleColour.MULTI_COLOUR
            every { secondaryColour } returns RemoteVehicleColour.MULTI_COLOUR
            every { engineCapacity } returns 1000
            every { exhaustEmissions } returns ExhaustEmissions(100, 2.0, 3.0, 4.0, 5.0, 6.0)
            every { keeper } returns null
        }

        val result = remoteVehicle.toCustomerVehicle()

        assertEquals("AA19AAA", result.registration)
        assertEquals("HONDA", result.make)
        assertEquals("CIVIC", result.model)
        assertEquals("Petrol Car", result.taxClass)
        assertEquals(LocalDate.of(2025, 12, 1), result.taxExpiryDate)
        assertEquals(LocalDate.of(2026, 6, 1), result.motExpiryDate)
        assertEquals(TaxStatus.TAXED, result.taxStatus)
        assertEquals(LocalDate.of(2020, 6, 1), result.dateOfFirstRegistration)
        assertEquals(FuelType.PETROL, result.fuelType)
        assertEquals(VehicleColour.MULTI_COLOUR, result.colour)
        assertEquals(VehicleColour.MULTI_COLOUR, result.secondaryColour)
        assertEquals(1000, result.engineCapacity)
        assertEquals(ExhaustEmissions(100, 2.0, 3.0, 4.0, 5.0, 6.0), result.exhaustEmissions)
        assertEquals(null, result.keeper)
    }

    @Test
    fun `Given a Vehicle with missing fields, when mapped, it sets correct defaults`() {
        val remoteVehicle = mockk<Vehicle> {
            every { registrationNumber } returns "BB20BBB"
            every { make } returns "FORD"
            every { model } returns null
            every { taxClass } returns "Van"
            every { taxedUntil } returns null
            every { motExpiryDate } returns null
            every { taxStatus } returns null
            every { motStatus } returns RemoteMotStatus.NO_DETAILS_HELD
            every { dateOfFirstRegistration } returns "2020-06-01"
            every { fuelType } returns RemoteFuelType.PETROL
            every { colour } returns RemoteVehicleColour.MULTI_COLOUR
            every { secondaryColour } returns null
            every { engineCapacity } returns null
            every { exhaustEmissions } returns null
            every { keeper } returns null
        }

        val result = remoteVehicle.toCustomerVehicle()

        assertEquals("BB20BBB", result.registration)
        assertEquals("FORD", result.make)
        assertNull(result.model)
        assertEquals("Van", result.taxClass)
        assertNull(result.taxExpiryDate)
        assertNull(result.motExpiryDate)
        assertEquals(TaxStatus.UNKNOWN, result.taxStatus)
        assertEquals(LocalDate.of(2020, 6, 1), result.dateOfFirstRegistration)
        assertEquals(FuelType.PETROL, result.fuelType)
        assertEquals(VehicleColour.MULTI_COLOUR, result.colour)
        assertNull(result.secondaryColour)
        assertNull(result.engineCapacity)
        assertNull(result.exhaustEmissions)
        assertNull(result.keeper)
    }
}