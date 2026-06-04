package uk.gov.govuk.dvla.ui.model

internal sealed interface UiState {
    data class Default(val drivingView: DrivingView) : UiState
    data object Hidden : UiState
}

enum class DrivingView {
    VEHICLES, LICENCE
}

internal sealed interface VehiclesSummaryUiState {
    data object Loading : VehiclesSummaryUiState
    data class Success(val vehicles: List<VehicleSummaryUiModel>) : VehiclesSummaryUiState
    data object Error : VehiclesSummaryUiState
}

internal sealed interface LicenceSummaryUiState {
    data object Loading : LicenceSummaryUiState
    data object Success : LicenceSummaryUiState
    data object Error : LicenceSummaryUiState
}
