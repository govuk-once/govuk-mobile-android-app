package uk.gov.govuk.dvla.ui.model

internal data class VehicleSummaryUiModel(
    val registration: String,
    val make: String,
    val model: String,
    val taxStatus: StatusUiModel,
    val motStatus: StatusUiModel
)

internal sealed interface StatusUiModel {
    data class StatusRow(
        val statusRowUi: StatusRowUiModel
    ) : StatusUiModel

    data class CountdownRow(
        val countdownBarUi: StatusCountdownUiModel
    ) : StatusUiModel

    data class InfoRow(
        val infoRowUi: InfoRowUiModel
    ) : StatusUiModel

    data object NoStatus : StatusUiModel
}
