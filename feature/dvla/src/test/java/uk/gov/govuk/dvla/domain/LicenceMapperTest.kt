package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.LicenceResponse
import java.time.LocalDate
import uk.gov.govuk.dvla.remote.model.common.LicenceStatus as RemoteLicenceStatus
import uk.gov.govuk.dvla.remote.model.common.LicenceType as RemoteLicenceType

class LicenceMapperTest {
    @Test
    fun `Given fully populated LicenceResponse, when mapped to domain model, it maps all fields correctly`() {
        val networkResponse = mockk<LicenceResponse>(relaxed = true) {
            every { drivingLicence.licenceType} returns RemoteLicenceType.FULL
            every { drivingLicence.drivingLicenceNumber } returns "DECER607085K99AE"
            every { drivingLicence.driverTitle } returns "Ms"
            every { drivingLicence.driverFirstNames } returns "Anna Ornella"
            every { drivingLicence.driverLastName } returns "Arenö"
            every { drivingLicence.driverFullAddress  } returns "29 Orchard Drive\nMilton Keynes\nPA98 J83"
            every { drivingLicence.licenceStatus } returns RemoteLicenceStatus.VALID
            every { drivingLicence.tokenValidToDate } returns "2035-05-01"
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals(LicenceType.FULL, domainModel.licenceType)
        assertEquals("DECER607085K99AE", domainModel.drivingLicenceNumber)
        assertEquals("Ms", domainModel.driverTitle)
        assertEquals("Anna Ornella", domainModel.driverFirstNames)
        assertEquals("Arenö", domainModel.driverLastName)
        assertEquals("29 Orchard Drive\nMilton Keynes\nPA98 J83", domainModel.driverFullAddress)
        assertEquals(LicenceStatus.VALID, domainModel.licenceStatus)
        assertEquals(LocalDate.of(2035, 5, 1), domainModel.tokenValidToDate)
        assertEquals("Ms Anna Ornella Arenö", domainModel.fullName)
    }

    @Test
    fun `Given LicenceResponse with missing fields, when mapped, it sets correct defaults`() {
        val networkResponse = mockk<LicenceResponse>(relaxed = true) {
            every { drivingLicence.driverTitle } returns null
            every { drivingLicence.driverFirstNames } returns null
            every { drivingLicence.driverLastName } returns null
            every { drivingLicence.driverFullAddress } returns null
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals("", domainModel.driverFullAddress)
        assertEquals("", domainModel.driverTitle)
        assertEquals("", domainModel.driverFirstNames)
        assertEquals("", domainModel.driverLastName)
        assertNull(domainModel.tokenValidToDate)
        assertEquals("", domainModel.fullName)
    }
}
