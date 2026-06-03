package uk.gov.govuk.dvla.ui.model

internal sealed interface UiState {
    data object Hidden : UiState
    data class Default(
        val drivingView: DrivingView = DrivingView.VEHICLE,
        val vehicleState: VehicleSummaryUiState = VehicleSummaryUiState.Loading,
        val licenceState: LicenceSummaryUiState = LicenceSummaryUiState.Loading
    ) : UiState
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
    data class Success(val licence: LicenceSummaryUiModel) : LicenceSummaryUiState
    data object Error : LicenceSummaryUiState
}
