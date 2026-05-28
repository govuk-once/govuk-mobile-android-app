package uk.gov.govuk.dvla.ui.model

data class LicenceSummaryUiModel(
    val licenceType: String,
    val licenceNumber: String,
    val name: String,
    val address: String,
    val licenceStatus: StatusRowUiModel
)
