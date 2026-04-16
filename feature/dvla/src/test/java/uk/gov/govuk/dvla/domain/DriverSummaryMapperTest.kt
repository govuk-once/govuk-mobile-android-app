package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.DriverSummaryResponse

class DriverSummaryMapperTest {

    @Test
    fun `Given fully populated DriverSummaryResponse, when mapped to domain model, it maps all fields correctly`() {
        val networkResponse = mockk<DriverSummaryResponse>(relaxed = true) {
            every { driverViewResponse.driver.drivingLicenceNumber } returns "DECER607085K99AE"
            every { driverViewResponse.driver.firstNames } returns "Driver"
            every { driverViewResponse.driver.lastName } returns "McDriver"
            every { driverViewResponse.driver.penaltyPoints } returns 3
            every { driverViewResponse.licence?.status } returns "Valid"
            every { driverViewResponse.token?.validToDate } returns "2035-05-01"
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals("DECER607085K99AE", domainModel.licenceNumber)
        assertEquals("Driver", domainModel.firstName)
        assertEquals("McDriver", domainModel.lastName)
        assertEquals(3, domainModel.penaltyPoints)
        assertEquals("Valid", domainModel.status)
        assertEquals("2035-05-01", domainModel.expiryDate)
    }

    @Test
    fun `Given DriverSummaryResponse with missing fields, when mapped, it sets correct defaults`() {
        val networkResponse = mockk<DriverSummaryResponse>(relaxed = true) {
            every { driverViewResponse.driver.drivingLicenceNumber } returns "DECER607085K99AE"
            every { driverViewResponse.driver.firstNames } returns null
            every { driverViewResponse.driver.lastName } returns null
            every { driverViewResponse.driver.penaltyPoints } returns null
            every { driverViewResponse.licence } returns null
            every { driverViewResponse.token } returns null
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals("DECER607085K99AE", domainModel.licenceNumber)
        assertEquals("", domainModel.firstName)
        assertEquals("", domainModel.lastName)
        assertEquals("Unknown", domainModel.status)
        assertNull(domainModel.penaltyPoints)
        assertNull(domainModel.expiryDate)
    }
}
