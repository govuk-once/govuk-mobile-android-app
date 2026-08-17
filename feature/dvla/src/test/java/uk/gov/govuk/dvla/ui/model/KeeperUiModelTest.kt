package uk.gov.govuk.dvla.ui.model

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.dvla.util.toAccessibleStreetName
import uk.gov.govuk.dvla.util.toSpacedString

class KeeperUiModelTest {

    @Test
    fun `Given multiple address lines, when formattedAddressLines requested, it returns the lines unchanged`() {
        val addressLines = listOf("1 St John Street", "Morriston", "Swansea", "SA6 7JL")
        val keeper = KeeperUiModel(name = "Name", addressLines = addressLines)

        assertEquals(addressLines, keeper.formattedAddressLines)
    }

    @Test
    fun `Given multiple address lines, when accessibleAddressLines requested, the first line is street-formatted and the last is spaced for the postcode`() {
        val addressLines = listOf("1 St John Street", "Morriston", "Swansea", "SA6 7JL")
        val keeper = KeeperUiModel(name = "Name", addressLines = addressLines)

        assertEquals(
            listOf(
                "1 St John Street".toAccessibleStreetName(),
                "Morriston",
                "Swansea",
                "SA6 7JL".toSpacedString()
            ),
            keeper.accessibleAddressLines
        )
    }

    @Test
    fun `Given a single address line, when accessibleAddressLines requested, street formatting is applied`() {
        val keeper = KeeperUiModel(name = "Name", addressLines = listOf("SA6 7JL"))

        assertEquals(listOf("SA6 7JL".toAccessibleStreetName()), keeper.accessibleAddressLines)
    }

    @Test
    fun `Given no address lines, when address lines requested, both are empty`() {
        val keeper = KeeperUiModel(name = "Name", addressLines = emptyList())

        assertEquals(emptyList<String>(), keeper.formattedAddressLines)
        assertEquals(emptyList<String>(), keeper.accessibleAddressLines)
    }
}
