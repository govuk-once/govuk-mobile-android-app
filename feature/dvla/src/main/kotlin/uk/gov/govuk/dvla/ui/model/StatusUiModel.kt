package uk.gov.govuk.dvla.ui.model

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

    data class LinkRow(
        val linkRowUi: LinkRowUiModel
    ) : StatusUiModel

    data object NoStatus : StatusUiModel
}
