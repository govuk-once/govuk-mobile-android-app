package uk.gov.govuk.dvla.ui.model

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
        get() = listOf(addressLine1, city, postcode).filter { it.isNotBlank() }
}
