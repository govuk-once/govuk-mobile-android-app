package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.dvla.util.toAccessibleStreetName
import uk.gov.govuk.dvla.util.toSpacedString

data class KeeperUiModel(
    val name: String,
    val addressLines: List<String>
) {
    val formattedAddressLines: List<String>
        get() = addressLines

    val accessibleAddressLines: List<String>
        get() = addressLines.mapIndexed { index, line ->
            when (index) {
                0 -> line.toAccessibleStreetName()
                addressLines.lastIndex -> line.toSpacedString()
                else -> line
            }
        }
}
