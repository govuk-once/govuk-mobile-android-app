package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.DriverSummaryResponse

class DriverSummaryMapperTest {

    @Test
    fun `Given DriverSummaryResponse, when mapped to domain model, it maps correctly`() {
        val networkResponse = mockk<DriverSummaryResponse>(relaxed = true) {
            every { driverViewResponse.driver.drivingLicenceNumber } returns "DECER607085K99AE"
            every { driverViewResponse.driver.firstNames } returns "Driver"
            every { driverViewResponse.driver.lastName } returns "McDriver"
            every { driverViewResponse.driver.penaltyPoints } returns 3
            every { driverViewResponse.licence.status } returns "Valid"
            every { driverViewResponse.token.validToDate } returns "2035-05-01"
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals("DECER607085K99AE", domainModel.licenceNumber)
        assertEquals("Driver", domainModel.firstName)
        assertEquals("McDriver", domainModel.lastName)
        assertEquals(3, domainModel.penaltyPoints)
        assertEquals("Valid", domainModel.status)
        assertEquals("2035-05-01", domainModel.expiryDate)
    }
}
