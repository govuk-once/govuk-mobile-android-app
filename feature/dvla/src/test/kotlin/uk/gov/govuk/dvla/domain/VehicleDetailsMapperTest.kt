package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.CustomerVehicleDetails
import uk.gov.govuk.dvla.remote.model.common.FuelType as RemoteFuelType
import uk.gov.govuk.dvla.remote.model.common.MotStatus as RemoteMotStatus
import uk.gov.govuk.dvla.remote.model.common.TaxStatus as RemoteTaxStatus
import uk.gov.govuk.dvla.remote.model.common.VehicleColour as RemoteVehicleColour
import java.time.LocalDate

class VehicleDetailsMapperTest {

    @Test
    fun `Given a fully populated CustomerVehicleDetails, when mapped to domain model, it maps all fields correctly`() {
        val remoteDetails = mockk<CustomerVehicleDetails> {
            every { vehicleId } returns 156487251
            every { registrationNumber } returns "AA19AAA"
            every { make } returns "HONDA"
            every { model } returns "CIVIC"
            every { taxStatus } returns RemoteTaxStatus.TAXED
            every { sornStart } returns "2025-01-01"
            every { taxedUntil } returns "2025-12-01"
            every { currentLicencePaymentMethod } returns "Direct Debit"
            every { motStatus } returns RemoteMotStatus.VALID
            every { motExpiryDate } returns "2026-06-01"
            every { dateOfFirstRegistration } returns "2020-06-01"
            every { fuelType } returns RemoteFuelType.PETROL
            every { colour } returns RemoteVehicleColour.RED
            every { secondaryColour } returns RemoteVehicleColour.BLACK
            every { engineCapacity } returns 2000
            every { exhaustEmissionsCo2 } returns 199
            every { keeperTitle } returns "MR"
            every { keeperFirstNames } returns "DAWN"
            every { keeperLastName } returns "WILLIAMS"
            every { keeperFullAddress } returns "Long View Rd\nSwansea\nSA6 7JL"
        }

        val result = remoteDetails.toDomainModel()

        assertEquals(156487251, result.summary.vehicleId)
        assertEquals("AA19AAA", result.summary.registration)
        assertEquals("HONDA", result.summary.make)
        assertEquals("CIVIC", result.summary.model)
        assertEquals(TaxStatus.TAXED, result.summary.taxStatus)
        assertEquals(LocalDate.of(2025, 1, 1), result.summary.sornStart)
        assertEquals(LocalDate.of(2025, 12, 1), result.summary.taxExpiryDate)
        assertEquals("Direct Debit", result.summary.currentLicencePaymentMethod)
        assertEquals(MotStatus.VALID, result.summary.motStatus)
        assertEquals(LocalDate.of(2026, 6, 1), result.summary.motExpiryDate)
        assertEquals(LocalDate.of(2020, 6, 1), result.dateOfFirstRegistration)
        assertEquals(FuelType.PETROL, result.fuelType)
        assertEquals(VehicleColour.RED, result.colour)
        assertEquals(VehicleColour.BLACK, result.secondaryColour)
        assertEquals(2000, result.engineCapacity)
        assertEquals(199, result.exhaustEmissionsCo2)
        assertEquals("MR", result.keeperTitle)
        assertEquals("DAWN", result.keeperFirstNames)
        assertEquals("WILLIAMS", result.keeperLastName)
        assertEquals("Long View Rd\nSwansea\nSA6 7JL", result.keeperFullAddress)
    }

    @Test
    fun `Given a CustomerVehicleDetails with missing fields, when mapped, it sets correct defaults`() {
        val remoteDetails = mockk<CustomerVehicleDetails> {
            every { vehicleId } returns 123456789
            every { registrationNumber } returns "BB20BBB"
            every { make } returns "FORD"
            every { model } returns null
            every { taxStatus } returns null
            every { sornStart } returns null
            every { taxedUntil } returns null
            every { currentLicencePaymentMethod } returns null
            every { motStatus } returns RemoteMotStatus.NO_DETAILS_HELD
            every { motExpiryDate } returns null
            every { dateOfFirstRegistration } returns null
            every { fuelType } returns null
            every { colour } returns null
            every { secondaryColour } returns null
            every { engineCapacity } returns null
            every { exhaustEmissionsCo2 } returns null
            every { keeperTitle } returns null
            every { keeperFirstNames } returns null
            every { keeperLastName } returns null
            every { keeperFullAddress } returns null
        }

        val result = remoteDetails.toDomainModel()

        assertEquals(123456789, result.summary.vehicleId)
        assertEquals("BB20BBB", result.summary.registration)
        assertEquals("FORD", result.summary.make)
        assertNull(result.summary.model)
        assertEquals(TaxStatus.UNKNOWN, result.summary.taxStatus)
        assertNull(result.summary.sornStart)
        assertNull(result.summary.taxExpiryDate)
        assertNull(result.summary.currentLicencePaymentMethod)
        assertEquals(MotStatus.NO_DETAILS_HELD, result.summary.motStatus)
        assertNull(result.summary.motExpiryDate)
        assertNull(result.dateOfFirstRegistration)
        assertEquals(FuelType.OTHER, result.fuelType)
        assertEquals(VehicleColour.UNKNOWN, result.colour)
        assertEquals(VehicleColour.UNKNOWN, result.secondaryColour)
        assertNull(result.engineCapacity)
        assertNull(result.exhaustEmissionsCo2)
        assertNull(result.keeperTitle)
        assertNull(result.keeperFirstNames)
        assertNull(result.keeperLastName)
        assertNull(result.keeperFullAddress)
    }
}
