package uk.gov.govuk.dvla.ui.model

internal sealed interface UiState {
    data object Hidden : UiState
    data class Error(val fallbackUrl: UrlModel) : UiState
    data class Default(
        val drivingView: DrivingView = DrivingView.VEHICLES,
        val vehiclesState: VehiclesSummaryUiState = VehiclesSummaryUiState.Loading,
        val licenceState: LicenceSummaryUiState = LicenceSummaryUiState.Loading
    ) : UiState
}

enum class DrivingView {
    VEHICLES, LICENCE
}

internal sealed interface VehiclesSummaryUiState {
    data object Loading : VehiclesSummaryUiState
    data class Success(val vehicles: List<VehicleSummaryUiModel>) : VehiclesSummaryUiState
    data class Error(val fallbackUrl: UrlModel) : VehiclesSummaryUiState
}

internal sealed interface LicenceSummaryUiState {
    data object Loading : LicenceSummaryUiState
    data class Success(val licence: LicenceSummaryUiModel) : LicenceSummaryUiState
    data class NotAvailable(val url: UrlModel) : LicenceSummaryUiState
    data class Error(val fallbackUrl: UrlModel) : LicenceSummaryUiState
}