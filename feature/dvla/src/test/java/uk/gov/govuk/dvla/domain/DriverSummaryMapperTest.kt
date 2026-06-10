package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.DriverSummaryResponse
import java.time.LocalDate
import uk.gov.govuk.dvla.remote.model.LicenceStatus as RemoteLicenceStatus
import uk.gov.govuk.dvla.remote.model.LicenceType as RemoteLicenceType

class DriverSummaryMapperTest {

    @Test
    fun `Given fully populated DriverSummaryResponse, when mapped to domain model, it maps all fields correctly`() {
        val networkResponse = mockk<DriverSummaryResponse>(relaxed = true) {
            every { driverView.licence?.type } returns RemoteLicenceType.FULL
            every { driverView.driver.drivingLicenceNumber } returns "DECER607085K99AE"
            every { driverView.driver.title } returns "Ms"
            every { driverView.driver.firstNames } returns "Anna Ornella"
            every { driverView.driver.lastName } returns "Arenö"
            every { driverView.driver.address?.unstructuredAddress?.line1 } returns "29 Orchard Drive"
            every { driverView.driver.address?.unstructuredAddress?.line5 } returns "Milton Keynes"
            every { driverView.driver.address?.unstructuredAddress?.postcode } returns "PA98 J83"
            every { driverView.licence?.status } returns RemoteLicenceStatus.VALID
            every { driverView.token?.validToDate } returns "2035-05-01"
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals(LicenceType.FULL, domainModel.licenceType)
        assertEquals("DECER607085K99AE", domainModel.licenceNumber)
        assertEquals("Ms", domainModel.title)
        assertEquals("Anna Ornella", domainModel.firstNames)
        assertEquals("Arenö", domainModel.lastName)
        assertEquals("29 Orchard Drive", domainModel.addressLine1)
        assertEquals("Milton Keynes", domainModel.addressLine5)
        assertEquals("PA98 J83", domainModel.postcode)
        assertEquals(LicenceStatus.VALID, domainModel.status)
        assertEquals(LocalDate.of(2035, 5, 1), domainModel.expiryDate)
        assertEquals("Ms Anna Ornella Arenö", domainModel.fullName)
    }

    @Test
    fun `Given DriverSummaryResponse with missing fields, when mapped, it sets correct defaults`() {
        val networkResponse = mockk<DriverSummaryResponse>(relaxed = true) {
            every { driverView.driver.drivingLicenceNumber } returns "DECER607085K99AE"
            every { driverView.driver.title } returns null
            every { driverView.driver.firstNames } returns null
            every { driverView.driver.lastName } returns null
            every { driverView.driver.address } returns null
            every { driverView.licence } returns null
            every { driverView.token } returns null
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals(LicenceType.UNKNOWN, domainModel.licenceType)
        assertEquals("DECER607085K99AE", domainModel.licenceNumber)
        assertEquals("", domainModel.title)
        assertEquals("", domainModel.firstNames)
        assertEquals("", domainModel.lastName)
        assertEquals("", domainModel.addressLine1)
        assertEquals("", domainModel.addressLine5)
        assertEquals("", domainModel.postcode)
        assertEquals(LicenceStatus.UNKNOWN, domainModel.status)
        assertNull(domainModel.expiryDate)
        assertEquals("", domainModel.fullName)
    }

    @Test
    fun `RemoteLicenceStatus toDomain maps enum values correctly`() {
        assertEquals(LicenceStatus.VALID, RemoteLicenceStatus.VALID.toDomain())
        assertEquals(LicenceStatus.DISQUALIFIED, RemoteLicenceStatus.DISQUALIFIED.toDomain())
        assertEquals(LicenceStatus.REVOKED, RemoteLicenceStatus.REVOKED.toDomain())
        assertEquals(LicenceStatus.REVOKED_FOR_MEDICAL_REASONS, RemoteLicenceStatus.REVOKED_FOR_MEDICAL_REASONS.toDomain())
        assertEquals(LicenceStatus.SURRENDERED, RemoteLicenceStatus.SURRENDERED.toDomain())
        assertEquals(LicenceStatus.SURRENDERED_VOLUNTARILY, RemoteLicenceStatus.SURRENDERED_VOLUNTARILY.toDomain())
        assertEquals(LicenceStatus.SURRENDERED_FOR_MEDICAL_REASONS, RemoteLicenceStatus.SURRENDERED_FOR_MEDICAL_REASONS.toDomain())
        assertEquals(LicenceStatus.EXPIRED, RemoteLicenceStatus.EXPIRED.toDomain())
        assertEquals(LicenceStatus.EXCHANGED, RemoteLicenceStatus.EXCHANGED.toDomain())
        assertEquals(LicenceStatus.REFUSED, RemoteLicenceStatus.REFUSED.toDomain())
        assertEquals(LicenceStatus.REFUSED_FOR_MEDICAL_REASONS, RemoteLicenceStatus.REFUSED_FOR_MEDICAL_REASONS.toDomain())
    }
}
