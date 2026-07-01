package uk.gov.govuk.dvla.ui.model


data class VehicleSummaryUiModel(
    val registration: String,
    val make: String,
    val model: String,
    val taxStatus: StatusRowUiModel,
    val motStatus: StatusRowUiModel,
    val menuItems: List<OverflowMenuItem> = emptyList()
)