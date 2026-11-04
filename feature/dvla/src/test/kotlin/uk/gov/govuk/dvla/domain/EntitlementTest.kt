package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.LicenceResponse
import uk.gov.govuk.dvla.remote.model.Entitlement as RemoteEntitlement
import uk.gov.govuk.dvla.remote.model.common.EntitlementType as RemoteEntitlementType

class EntitlementTest {

    @Test
    fun `Given an entitlement with no code, when mapped, then it is dropped`() {
        val networkResponse = mockk<LicenceResponse>(relaxed = true) {
            every { drivingLicence.entitlements } returns listOf(
                RemoteEntitlement(null, RemoteEntitlementType.FULL),
                RemoteEntitlement("B", RemoteEntitlementType.FULL)
            )
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals(listOf(Entitlement("B", EntitlementType.FULL)), domainModel.entitlements)
    }

    @Test
    fun `Given an entitlement with an unrecognised type, when mapped, then its type is UNKNOWN`() {
        val networkResponse = mockk<LicenceResponse>(relaxed = true) {
            every { drivingLicence.entitlements } returns listOf(
                RemoteEntitlement("B", null)
            )
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals(
            listOf(Entitlement("B", EntitlementType.UNKNOWN)),
            domainModel.entitlements
        )
    }

    @Test
    fun `Given entitlements with missing values, when mapped, then entries without code are dropped and missing type is UNKNOWN`() {
        val networkResponse = mockk<LicenceResponse>(relaxed = true) {
            every { drivingLicence.entitlements } returns listOf(
                RemoteEntitlement(null, RemoteEntitlementType.FULL),
                RemoteEntitlement("B", null),
                RemoteEntitlement("A", RemoteEntitlementType.PROVISIONAL)
            )
        }

        val domainModel = networkResponse.toDomainModel()

        assertEquals(
            listOf(
                Entitlement("B", EntitlementType.UNKNOWN),
                Entitlement("A", EntitlementType.PROVISIONAL)
            ),
            domainModel.entitlements
        )
    }
}
