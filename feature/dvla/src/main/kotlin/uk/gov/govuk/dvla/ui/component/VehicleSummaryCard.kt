package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.component.Title3RegularLabel
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.MenuAction
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel
import uk.gov.govuk.dvla.util.toSpacedString

@Composable
internal fun VehicleSummaryCard(
    vehicleSummary: VehicleSummaryUiModel,
    onDetailsClick: () -> Unit,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.cardDefault
        )
    ) {
        // header
        VehicleSummaryHeader(
            registration = vehicleSummary.registration,
            make = vehicleSummary.make,
            model = vehicleSummary.model,
            menuItems = vehicleSummary.menuItems,
            onMenuItemClick = onMenuItemClick
        )

        // tax
        StatusListItem(
            title = vehicleSummary.taxStatus.title?.let {
                AccessibleString(displayText = it)
            },
            description = AccessibleString(
                displayText = vehicleSummary.taxStatus.description
            ),
            iconStyle = vehicleSummary.taxStatus.iconStyle,
        )

        // MOT
        StatusListItem(
            title = vehicleSummary.motStatus.title?.let {
                AccessibleString(
                    displayText = it,
                    altText = vehicleSummary.motStatus.titleAltText
                )
            },
            description = AccessibleString(
                displayText = vehicleSummary.motStatus.description
            ),
            iconStyle = vehicleSummary.motStatus.iconStyle,
        )

        // details
        InternalLinkListItem(
            title = stringResource(R.string.vehicle_details_title),
            onClick = onDetailsClick,
            isFirst = false,
            isLast = true
        )
    }
}

@Composable
internal fun RegistrationPlate(
    registration: String,
    modifier: Modifier = Modifier
) {
    val accessibleNumberPlate = registration.toSpacedString()
    val altText = stringResource(id = R.string.registration_plate_alt_text, accessibleNumberPlate)

    Box(
        modifier = modifier
            .height(36.dp)
            .background(
                color = GovUkTheme.colourScheme.surfaces.registrationPlate,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = GovUkTheme.colourScheme.strokes.registrationPlate,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                top = GovUkTheme.spacing.small,
                start = GovUkTheme.spacing.small,
                end = GovUkTheme.spacing.small,
                bottom = 5.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = registration,
            style = GovUkTheme.typography.registrationPlate,
            color = GovUkTheme.colourScheme.textAndIcons.registrationPlateText,
            modifier = Modifier.semantics {
                contentDescription = altText
            }
        )
    }
}

@Composable
internal fun VehicleSummaryHeader(
    registration: String,
    make: String,
    model: String,
    menuItems: List<OverflowMenuItem>,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    SummaryCardHeader(
        modifier = modifier,
        leadingContent = {
            // reg plate
            RegistrationPlate(registration = registration)
        },
        menuItems = menuItems,
        onMenuItemClick = onMenuItemClick
    ) {
        // make and model
        Column(modifier = Modifier.semantics(mergeDescendants = true) {}) {
            Title1BoldLabel(
                text = make,
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )

            Title3RegularLabel(
                text = model,
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun RegistrationPlatePreview() {
    GovUkTheme {
        RegistrationPlate(registration = "FH08 PDH")
    }
}

@PreviewLightDark
@Composable
private fun VehicleHeaderPreview() {
    GovUkTheme {
        VehicleSummaryHeader(
            registration = "FH08 PDH",
            make = "Volkswagen",
            model = "ID4",
            menuItems = emptyList(),
            onMenuItemClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun VehicleSummaryCardPreview() {
    GovUkTheme {
        VehicleSummaryCard(
            vehicleSummary = VehicleSummaryUiModel(
                registration = "FH08 PDH",
                make = "Volkswagen",
                model = "ID4",
                taxStatus = StatusRowUiModel(
                    title = "Tax",
                    description = "Valid until 1 February 2027",
                    iconStyle = uk.gov.govuk.design.ui.model.StatusListItemIconStyle.Success,
                ),
                motStatus = StatusRowUiModel(
                    title = "MOT",
                    description = "Valid until 24 April 2026",
                    iconStyle = uk.gov.govuk.design.ui.model.StatusListItemIconStyle.Warning
                )
            ),
            onDetailsClick = {},
            onMenuItemClick = {}
        )
    }
}
