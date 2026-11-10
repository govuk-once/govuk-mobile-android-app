package uk.gov.govuk.dvla.mapper

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.Entitlement
import uk.gov.govuk.dvla.domain.EntitlementType
import uk.gov.govuk.dvla.domain.Restriction
import uk.gov.govuk.dvla.ui.model.LicenceCategoryGroupUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryLinkUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryRestrictionUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryUiModel
import uk.gov.govuk.dvla.ui.model.dvlaUrls
import uk.gov.govuk.dvla.util.StringProvider
import java.time.LocalDate

class LicenceCategoriesMapperTest {

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val mapper = LicenceCategoriesMapper(stringProvider)

    @Before
    fun setup() {
        every { stringProvider.getString(R.string.categories_group_mopeds) } returns "Mopeds"
        every { stringProvider.getString(R.string.categories_group_motorcycles) } returns "Motorcycles"
        every { stringProvider.getString(R.string.categories_group_cars) } returns "Cars"
        every { stringProvider.getString(R.string.categories_group_light_vehicles) } returns "Light vehicles"
        every { stringProvider.getString(R.string.categories_group_medium_vehicles) } returns "Medium vehicles"
        every { stringProvider.getString(R.string.categories_group_large_vehicles) } returns "Large vehicles"
        every { stringProvider.getString(R.string.categories_group_minibuses) } returns "Minibuses"
        every { stringProvider.getString(R.string.categories_group_buses) } returns "Buses"
        every { stringProvider.getString(R.string.categories_group_other_vehicles) } returns "Other vehicles"
        every { stringProvider.getString(R.string.licence_categories_link_compare) } returns "Compare"
        every { stringProvider.getString(R.string.licence_categories_link_weights) } returns "Weights"
        every { stringProvider.getString(R.string.licence_categories_link_add_categories) } returns "Add"
    }

    private fun full(code: String) = Entitlement(code, EntitlementType.FULL)
    private fun provisional(code: String) = Entitlement(code, EntitlementType.PROVISIONAL)

    private fun List<LicenceCategoryGroupUiModel>.codes() =
        flatMap { group -> group.categories.map { it.code } }

    @Test
    fun `Given full entitlements, then they appear under can drive with the group title`() {
        val result = mapper.toUiModel(listOf(full("B"), full("BE")), dvlaUrls = null)

        assertEquals(1, result.canDrive.size)
        assertEquals("Cars", result.canDrive.first().title)
        assertEquals(listOf("B", "BE"), result.canDrive.codes())
        assertTrue(result.provisionallyDrive.isEmpty())
    }

    @Test
    fun `Given provisional entitlements, then they appear under provisionally drive only`() {
        val result = mapper.toUiModel(listOf(provisional("A")), dvlaUrls = null)

        assertTrue(result.canDrive.isEmpty())
        assertEquals("Motorcycles", result.provisionallyDrive.first().title)
        assertEquals(listOf("A"), result.provisionallyDrive.codes())
    }

    @Test
    fun `Given a category held as full and provisional, then it appears in both sections`() {
        val result = mapper.toUiModel(listOf(full("A"), provisional("A")), dvlaUrls = null)

        assertEquals(listOf("A"), result.canDrive.codes())
        assertEquals(listOf("A"), result.provisionallyDrive.codes())
    }

    @Test
    fun `Given a group with no held categories, then the group is not included`() {
        val result = mapper.toUiModel(listOf(full("P")), dvlaUrls = null)

        assertEquals(1, result.canDrive.size)
        assertEquals(listOf("P"), result.canDrive.codes())
    }

    @Test
    fun `Given motorcycle entitlements, then A, A1 and A2 are listed in definition order`() {
        val result = mapper.toUiModel(listOf(full("A2"), full("A"), full("A1")), dvlaUrls = null)

        assertEquals(listOf("A", "A1", "A2"), result.canDrive.codes())
    }

    @Test
    fun `Given large vehicle and bus entitlements, then they appear in their own groups`() {
        val result =
            mapper.toUiModel(listOf(full("DE"), full("C"), full("CE"), full("D")), dvlaUrls = null)

        assertEquals(listOf("Large vehicles", "Buses"), result.canDrive.map { it.title })
        assertEquals(listOf("C", "CE", "D", "DE"), result.canDrive.codes())
    }

    @Test
    fun `Given other vehicle entitlements, then codes are displayed in defined order`() {
        val result = mapper.toUiModel(
            listOf(full("N"), full("M"), full("L"), full("K"), full("H"), full("G"), full("F")),
            dvlaUrls = null
        )

        assertEquals("Other vehicles", result.canDrive.first().title)
        assertEquals(listOf("f", "G", "H", "k", "l", "M", "n"), result.canDrive.codes())
    }

    @Test
    fun `Given a lowercase category code, then it still matches`() {
        val result = mapper.toUiModel(listOf(full("be")), dvlaUrls = null)

        assertEquals(listOf("BE"), result.canDrive.codes())
    }

    @Test
    fun `Given a category that is not in the definitions, then it is not shown`() {
        val result = mapper.toUiModel(listOf(full("ZZ"), full("B")), dvlaUrls = null)

        assertEquals(listOf("B"), result.canDrive.codes())
    }

    @Test
    fun `Given an entitlement with an unknown type, then it is not shown in either section`() {
        val result = mapper.toUiModel(
            listOf(Entitlement("B", EntitlementType.UNKNOWN)),
            dvlaUrls = null
        )

        assertTrue(result.canDrive.isEmpty())
        assertTrue(result.provisionallyDrive.isEmpty())
    }

    @Test
    fun `Given no entitlements, then both sections are empty`() {
        val result = mapper.toUiModel(emptyList(), dvlaUrls = null)

        assertTrue(result.canDrive.isEmpty())
        assertTrue(result.provisionallyDrive.isEmpty())
    }

    @Test
    fun `Given dvlaUrls, then further information links are mapped with their text and url`() {
        val result = mapper.toUiModel(emptyList(), dvlaUrls)

        assertEquals(
            listOf(
                LicenceCategoryLinkUiModel("Compare", dvlaUrls.compareLicenceCategories),
                LicenceCategoryLinkUiModel("Weights", dvlaUrls.vehicleWeightExplained),
                LicenceCategoryLinkUiModel("Add", dvlaUrls.addLicenceCategories)
            ),
            result.furtherInformationLinks
        )
    }

    @Test
    fun `Given dvlaUrls is null, then there are no further information links`() {
        val result = mapper.toUiModel(emptyList(), dvlaUrls = null)

        assertTrue(result.furtherInformationLinks.isEmpty())
    }

    @Test
    fun `Given an entitlement from every group, then all groups are returned with their titles in order`() {
        val result = mapper.toUiModel(
            listOf(
                full("F"), full("D"), full("D1"), full("C"), full("C1"),
                full("B"), full("B1"), full("A"), full("P")
            ),
            dvlaUrls = null
        )

        assertEquals(
            listOf(
                "Mopeds",
                "Motorcycles",
                "Light vehicles",
                "Cars",
                "Medium vehicles",
                "Large vehicles",
                "Minibuses",
                "Buses",
                "Other vehicles"
            ),
            result.canDrive.map { it.title }
        )
    }

    @Test
    fun `Given an entitlement, when mapped to UI model, then the title is its catalogue group`() {
        assertEquals("Minibuses", mapper.toDetailUiModel(full("D1E"))?.title)
    }

    @Test
    fun `Given an entitlement with an icon, when mapped to UI model, then the catalogue code and icon are used`() {
        assertEquals(
            LicenceCategoryUiModel("D1E", R.drawable.ic_licence_cat_d1e),
            mapper.toDetailUiModel(full("D1E"))?.category
        )
    }

    @Test
    fun `Given an uppercase other vehicle code, when mapped to UI model, then the lowercase display code is used with no icon`() {
        val result = mapper.toDetailUiModel(full("F"))

        assertEquals("Other vehicles", result?.title)
        assertEquals(LicenceCategoryUiModel("f"), result?.category)
    }

    @Test
    fun `Given a code that is not in the catalogue, when mapped to UI model, then the result is null`() {
        assertNull(mapper.toDetailUiModel(full("ZZ")))
    }

    @Test
    fun `Given restrictions, when mapped to UI model, then they are mapped in order`() {
        val entitlement = full("A").copy(
            restrictions = listOf(
                Restriction("79(03)", "Restricted to tricycles"),
                Restriction("70", "Exchange of licence")
            )
        )

        assertEquals(
            listOf(
                LicenceCategoryRestrictionUiModel("79(03)", "Restricted to tricycles"),
                LicenceCategoryRestrictionUiModel("70", "Exchange of licence")
            ),
            mapper.toDetailUiModel(entitlement)?.restrictions
        )
    }

    @Test
    fun `Given no restrictions, when mapped to UI model, then restrictions are empty`() {
        assertTrue(mapper.toDetailUiModel(full("B"))?.restrictions?.isEmpty() == true)
    }

    @Test
    fun `Given a legal literal, when mapped to UI model, then it is the you can drive text`() {
        val entitlement = full("F").copy(categoryLegalLiteral = "Agricultural")

        assertEquals("Agricultural", mapper.toDetailUiModel(entitlement)?.youCanDrive)
    }

    @Test
    fun `Given a blank legal literal, when mapped to UI model, then you can drive is null`() {
        assertNull(mapper.toDetailUiModel(full("F").copy(categoryLegalLiteral = " "))?.youCanDrive)
    }

    @Test
    fun `Given from and expiry dates, when mapped to UI model, then they are correctly formatted`() {
        val entitlement = full("B").copy(
            fromDate = LocalDate.of(1990, 12, 21),
            expiryDate = LocalDate.of(2043, 5, 23)
        )

        val result = mapper.toDetailUiModel(entitlement)

        assertEquals("21 December 1990", result?.validFrom)
        assertEquals("23 May 2043", result?.validTo)
    }

    @Test
    fun `Given no dates, when mapped to UI model, then dates and expiry indicator are null`() {
        val result = mapper.toDetailUiModel(full("B"))

        assertNull(result?.validFrom)
        assertNull(result?.validTo)
        assertNull(result?.expiryIndicator)
    }

    @Test
    fun `Given expiry is today, when mapped to UI model, then the indicator says expires today`() {
        every { stringProvider.getString(R.string.licence_category_detail_expires_today) } returns "Expires today"

        val result = mapper.toDetailUiModel(full("B").copy(expiryDate = LocalDate.now()))

        assertEquals("Expires today", result?.expiryIndicator)
    }

    @Test
    fun `Given expiry is in 1 day, when mapped to UI model, then the indicator uses the day count`() {
        every {
            stringProvider.getQuantityString(R.plurals.licence_category_detail_expires_in_days, 1, 1)
        } returns "Expires in 1 day"

        val result = mapper.toDetailUiModel(full("B").copy(expiryDate = LocalDate.now().plusDays(1)))

        assertEquals("Expires in 1 day", result?.expiryIndicator)
    }

    @Test
    fun `Given expiry is in 60 days, when mapped to UI model, then the indicator is shown`() {
        every {
            stringProvider.getQuantityString(R.plurals.licence_category_detail_expires_in_days, 60, 60)
        } returns "Expires in 60 days"

        val result = mapper.toDetailUiModel(full("B").copy(expiryDate = LocalDate.now().plusDays(60)))

        assertEquals("Expires in 60 days", result?.expiryIndicator)
    }

    @Test
    fun `Given expiry is in 61 days, when mapped to UI model, then there is no indicator`() {
        val result = mapper.toDetailUiModel(full("B").copy(expiryDate = LocalDate.now().plusDays(61)))

        assertNull(result?.expiryIndicator)
    }
}