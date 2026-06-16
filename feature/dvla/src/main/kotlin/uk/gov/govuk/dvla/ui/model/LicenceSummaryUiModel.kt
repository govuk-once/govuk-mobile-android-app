package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.dvla.util.toAccessibleStreetName
import uk.gov.govuk.dvla.util.toSpacedString

data class LicenceSummaryUiModel(
    val licenceType: String,
    val licenceNumber: String,
    val name: String,
    val addressLine1: String,
    val city: String,
    val postcode: String,
    val licenceStatus: StatusRowUiModel,
    val isExpired: Boolean = false
) {
    val formattedAddressLines: List<String>
        get() = asAddressList(
            formattedAddressLine1 = addressLine1,
            formattedPostcode = postcode
        )

    val accessibleAddressLines: List<String>
        get() = asAddressList(
            formattedAddressLine1 = addressLine1.toAccessibleStreetName(),
            formattedPostcode = postcode.toSpacedString()
        )

    private fun asAddressList(
        formattedAddressLine1: String,
        formattedPostcode: String): List<String> =
        listOf(formattedAddressLine1, city, formattedPostcode).filter { it.isNotBlank() }
}
