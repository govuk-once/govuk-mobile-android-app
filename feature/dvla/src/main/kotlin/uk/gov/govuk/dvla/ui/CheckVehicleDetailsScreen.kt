package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.dvla.ui.component.CheckVehicleDetailsHeader
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem
import uk.gov.govuk.dvla.ui.model.UrlModel
import uk.gov.govuk.dvla.ui.model.VehicleDetailsUiModel

@Composable
internal fun CheckVehicleDetailsScreen(
    details: VehicleDetailsUiModel,
    onClose: () -> Unit,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    launchBrowser: (text: String, url: UrlModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CheckVehicleDetailsHeader(
            onClose = onClose,
            menuItems = details.menuItems,
            onMenuItemClick = onMenuItemClick
        )

        VehicleDetailsContent(
            details = details,
            launchBrowser = launchBrowser
        )
    }
}