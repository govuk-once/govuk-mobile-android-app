package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.design.ui.model.SpecificationIconUiModel
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.component.CheckVehicleDetailsHeader
import uk.gov.govuk.dvla.ui.model.InfoRowUiModel
import uk.gov.govuk.dvla.ui.model.MenuAction
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel
import uk.gov.govuk.dvla.ui.model.StatusUiModel
import uk.gov.govuk.dvla.ui.model.UrlModel
import uk.gov.govuk.dvla.ui.model.VehicleDetailsUiModel

@Composable
internal fun CheckVehicleDetailsScreen(
    details: VehicleDetailsUiModel,
    onClose: () -> Unit,
    onSearch: () -> Unit,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    launchBrowser: (text: String, url: UrlModel) -> Unit,
    onPageView: () -> Unit,
    modifier: Modifier = Modifier
) {
    RunOnceLaunchedEffect {
        onPageView()
    }

    Column(modifier = modifier) {
        CheckVehicleDetailsHeader(
            onClose = onClose,
            onSearch = onSearch,
            menuItems = details.menuItems,
            onMenuItemClick = onMenuItemClick
        )

        VehicleDetailsContent(
            details = details,
            launchBrowser = launchBrowser
        )
    }
}

@PreviewLightDark
@Composable
internal fun CheckVehicleDetailsScreenPreview() {
    GovUkTheme {
        CheckVehicleDetailsScreen(
            details = VehicleDetailsUiModel(
                make = "VAUXHALL",
                model = null,
                registration = "AB12CDE",
                keeper = null,
                specificationsIcons = listOf(
                    SpecificationIconUiModel(
                        icon = R.drawable.ic_calendar,
                        description = AccessibleString("2009")
                    ),
                    SpecificationIconUiModel(
                        icon = R.drawable.ic_petrol_diesel,
                        description = AccessibleString("Petrol")
                    ),
                    SpecificationIconUiModel(
                        icon = R.drawable.ic_colour,
                        description = AccessibleString("Brown")
                    )
                ),
                taxStatus = StatusUiModel.InfoRow(
                    infoRowUi = InfoRowUiModel(
                        title = AccessibleString("Off the road (SORN)"),
                        icon = R.drawable.ic_circle_p
                    )
                ),
                motStatus = StatusUiModel.StatusRow(
                    statusRowUi = StatusRowUiModel(
                        title = AccessibleString("MOT"),
                        description = AccessibleString("Expired 30 April 2026"),
                        iconStyle = StatusListItemIconStyle.Warning
                    )
                ),
                specifications = listOf(
                    InternalLinkListItemModel.Info(
                        title = AccessibleString("Make"),
                        info = AccessibleString("VAUXHALL")
                    ),
                    InternalLinkListItemModel.Info(
                        title = AccessibleString("First registered"),
                        info = AccessibleString("February 2009")
                    ),
                    InternalLinkListItemModel.Info(
                        title = AccessibleString("Fuel type"),
                        info = AccessibleString("Petrol")
                    ),
                    InternalLinkListItemModel.Info(
                        title = AccessibleString("Colour"),
                        info = AccessibleString("Brown")
                    ),
                    InternalLinkListItemModel.Info(
                        title = AccessibleString("Engine size"),
                        info = AccessibleString("1700cc")
                    ),
                    InternalLinkListItemModel.Info(
                        title = AccessibleString("CO2 emissions"),
                        info = AccessibleString("0")
                    )
                ),
                menuItems = listOf(
                    OverflowMenuItem(
                        text = AccessibleString("Used vehicle checks"),
                        action = MenuAction.WebLink("https://www.gov.uk/checks-when-buying-a-used-car")
                    )
                )
            ),
            onClose = {},
            onSearch = {},
            onMenuItemClick = {},
            launchBrowser = { _, _ -> },
            onPageView = {},
            modifier = Modifier.background(GovUkTheme.colourScheme.surfaces.background)
        )
    }
}
