package uk.gov.govuk

internal sealed class AppUiState {
    internal class Default(
        val shouldDisplayRecommendUpdate: Boolean = false,
        val shouldShowExternalBrowser: Boolean = false,
        val isChatEnabled: Boolean = false
    ) : AppUiState()

    internal data object Loading : AppUiState()

    internal data object AppUnavailable : AppUiState()

    internal data object DeviceOffline : AppUiState()

    internal data object ForcedUpdate : AppUiState()
}
