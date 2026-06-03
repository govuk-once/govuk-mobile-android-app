package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.dvla.util.toSpacedString

data class LicenceSummaryUiModel(
    val licenceType: String,
    val licenceNumber: String,
    val name: String,
    val addressLine1: String,
    val city: String,
    val postcode: String,
    val licenceStatus: StatusRowUiModel
) {
    val formattedAddressLines: List<String>
        get() = asAddressList(formattedPostcode = postcode)

    val accessibleAddressLines: List<String>
        get() = asAddressList(formattedPostcode = postcode.toSpacedString())

    private fun asAddressList(formattedPostcode: String): List<String> =
        listOf(addressLine1, city, formattedPostcode).filter { it.isNotBlank() }
}
