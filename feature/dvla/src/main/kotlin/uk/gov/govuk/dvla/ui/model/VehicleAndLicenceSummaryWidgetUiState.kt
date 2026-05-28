package uk.gov.govuk.dvla.ui.model

internal sealed interface UiState {
    data class Default(val category: Category) : UiState
    data object Hidden : UiState
}

internal enum class Category {
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
