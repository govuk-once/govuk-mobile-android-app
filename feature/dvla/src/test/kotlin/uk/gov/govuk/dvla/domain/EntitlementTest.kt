package uk.gov.govuk.dvla.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.dvla.remote.model.LicenceResponse
import java.time.LocalDate
import uk.gov.govuk.dvla.remote.model.Entitlement as RemoteEntitlement
import uk.gov.govuk.dvla.remote.model.Restriction as RemoteRestriction
import uk.gov.govuk.dvla.remote.model.common.EntitlementType as RemoteEntitlementType

class EntitlementTest {

    @Test
    fun `Given an entitlement with no code, when mapped, then it is dropped`() {
        val domainModel = createResponse(
            createEntitlement(categoryCode = null),
            createEntitlement(categoryCode = "B")
        ).toDomainModel()

        assertEquals(listOf(Entitlement("B", EntitlementType.FULL)), domainModel.entitlements)
    }

    @Test
    fun `Given full and provisional entitlements, when mapped, then their types are kept`() {
        val domainModel = createResponse(
            createEntitlement(categoryCode = "B", categoryType = RemoteEntitlementType.FULL),
            createEntitlement(categoryCode = "A", categoryType = RemoteEntitlementType.PROVISIONAL)
        ).toDomainModel()

        assertEquals(
            listOf(
                Entitlement("B", EntitlementType.FULL),
                Entitlement("A", EntitlementType.PROVISIONAL)
            ),
            domainModel.entitlements
        )
    }

    @Test
    fun `Given an entitlement with a missing or unrecognised type, when mapped, then its type is UNKNOWN`() {
        val domainModel = createResponse(createEntitlement(categoryType = null)).toDomainModel()

        assertEquals(listOf(Entitlement("B", EntitlementType.UNKNOWN)), domainModel.entitlements)
    }

    @Test
    fun `Given valid dates, when mapped, then from and expiry dates are parsed`() {
        val entitlement = createResponse(
            createEntitlement(fromDate = "2006-01-27", expiryDate = "2051-09-17")
        ).toDomainModel().entitlements.single()

        assertEquals(LocalDate.of(2006, 1, 27), entitlement.fromDate)
        assertEquals(LocalDate.of(2051, 9, 17), entitlement.expiryDate)
    }

    @Test
    fun `Given missing or invalid dates, when mapped, then the dates are null`() {
        val entitlement = createResponse(
            createEntitlement(fromDate = null, expiryDate = "not a date")
        ).toDomainModel().entitlements.single()

        assertEquals(null, entitlement.fromDate)
        assertEquals(null, entitlement.expiryDate)
    }

    @Test
    fun `Given restrictions, when mapped, then restrictions without a code are dropped and missing literals are empty`() {
        val entitlement = createResponse(
            createEntitlement(
                restrictions = listOf(
                    RemoteRestriction(restrictionCode = "79(03)", restrictionLiteral = "Restricted to tricycles"),
                    RemoteRestriction(restrictionCode = null, restrictionLiteral = "No code"),
                    RemoteRestriction(restrictionCode = "70", restrictionLiteral = null)
                )
            )
        ).toDomainModel().entitlements.single()

        assertEquals(
            listOf(
                Restriction("79(03)", "Restricted to tricycles"),
                Restriction("70", "")
            ),
            entitlement.restrictions
        )
    }

    @Test
    fun `Given no restrictions, when mapped, then restrictions are empty`() {
        val entitlement = createResponse(createEntitlement(restrictions = null))
            .toDomainModel().entitlements.single()

        assertEquals(emptyList<Restriction>(), entitlement.restrictions)
    }

    private fun createResponse(vararg entitlements: RemoteEntitlement) =
        mockk<LicenceResponse>(relaxed = true) {
            every { drivingLicence.entitlements } returns entitlements.toList()
        }

    private fun createEntitlement(
        categoryCode: String? = "B",
        categoryType: RemoteEntitlementType? = RemoteEntitlementType.FULL,
        fromDate: String? = null,
        expiryDate: String? = null,
        restrictions: List<RemoteRestriction>? = null,
        categoryLegalLiteral: String? = null
    ) = RemoteEntitlement(
        categoryCode = categoryCode,
        categoryType = categoryType,
        fromDate = fromDate,
        expiryDate = expiryDate,
        restrictions = restrictions,
        categoryLegalLiteral = categoryLegalLiteral
    )
}
