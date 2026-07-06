package uk.gov.govuk.dvla.ui.model

internal data class VehicleSummaryUiModel(
    val registration: String,
    val make: String,
    val model: String,
    val taxStatus: StatusUiModel,
    val motStatus: StatusUiModel,
    val menuItems: List<OverflowMenuItem> = emptyList()
)
