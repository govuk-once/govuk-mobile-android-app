package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.dvla.util.toAccessibleStreetName
import uk.gov.govuk.dvla.util.toSpacedString

internal data class LicenceSummaryUiModel(
    val licenceType: String,
    val licenceNumber: String,
    val name: String,
    val statusUi: StatusUiModel,
    private val addressLines: List<String>,
    val menuItems: List<OverflowMenuItem> = emptyList()
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
