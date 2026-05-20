package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.Vehicle
import uk.gov.govuk.dvla.remote.model.common.MotStatus as RemoteMotStatus
import uk.gov.govuk.dvla.remote.model.common.TaxStatus as RemoteTaxStatus
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
        }

        val result = remoteVehicle.toCustomerVehicle()

        assertEquals("AA19AAA", result.registration)
        assertEquals("HONDA", result.make)
        assertEquals("CIVIC", result.model)
        assertEquals("Petrol Car", result.taxClass)
        assertEquals(LocalDate.of(2025, 12, 1), result.taxExpiryDate)
        assertEquals(LocalDate.of(2026, 6, 1), result.motExpiryDate)
        assertEquals(TaxStatus.TAXED, result.taxStatus)
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
        }

        val result = remoteVehicle.toCustomerVehicle()

        assertEquals("BB20BBB", result.registration)
        assertEquals("FORD", result.make)
        assertNull(result.model)
        assertEquals("Van", result.taxClass)
        assertNull(result.taxExpiryDate)
        assertNull(result.motExpiryDate)
        assertEquals(TaxStatus.UNKNOWN, result.taxStatus)
    }
}