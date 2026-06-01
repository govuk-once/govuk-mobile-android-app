package uk.gov.govuk.dvla.ui.model

internal sealed interface UiState {
    data class Default(val drivingView: DrivingView) : UiState
    data object Hidden : UiState
}

enum class DrivingView {
    VEHICLE, LICENCE
}

internal sealed interface VehicleSummaryUiState {
    data object Loading : VehicleSummaryUiState
    data class Success(val vehicles: List<VehicleSummaryUiModel>) : VehicleSummaryUiState
    data object Error : VehicleSummaryUiState
}

internal sealed interface LicenceSummaryUiState {
    data object Loading : LicenceSummaryUiState
    data object Success : LicenceSummaryUiState
    data object Error : LicenceSummaryUiState
}
