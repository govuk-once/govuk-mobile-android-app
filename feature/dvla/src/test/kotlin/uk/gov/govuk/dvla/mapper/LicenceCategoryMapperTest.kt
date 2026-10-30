package uk.gov.govuk.dvla.mapper

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.Entitlement
import uk.gov.govuk.dvla.domain.EntitlementType
import uk.gov.govuk.dvla.ui.model.LicenceCategoryGroupUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryLinkUiModel
import uk.gov.govuk.dvla.ui.model.dvlaUrls
import uk.gov.govuk.dvla.util.StringProvider

class LicenceCategoriesMapperTest {

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val mapper = LicenceCategoriesMapper(stringProvider)

    @Before
    fun setup() {
        every { stringProvider.getString(R.string.categories_group_mopeds) } returns "Mopeds"
        every { stringProvider.getString(R.string.categories_group_motorcycles) } returns "Motorcycles"
        every { stringProvider.getString(R.string.categories_group_cars) } returns "Cars"
        every { stringProvider.getString(R.string.categories_group_large_vehicles) } returns "Large vehicles"
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
    fun `Given motorcycle entitlements, then A1 A2 and A are listed in definition order`() {
        val result = mapper.toUiModel(listOf(full("A2"), full("A"), full("A1")), dvlaUrls = null)

        assertEquals(listOf("A", "A1", "A2"), result.canDrive.codes())
    }

    @Test
    fun `Given large vehicle and bus entitlements, then they appear in their own groups`() {
        val result = mapper.toUiModel(listOf(full("DE"), full("C"), full("CE"), full("D")), dvlaUrls = null)

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
}