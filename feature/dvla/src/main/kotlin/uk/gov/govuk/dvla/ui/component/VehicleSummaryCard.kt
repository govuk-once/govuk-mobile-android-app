package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel

@Composable
internal fun VehicleSummaryCard(
    vehicleSummary: VehicleSummaryUiModel,
    onVehicleDetailsClick: (registration: String) -> Unit,
    onMoreClick: () -> Unit,
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
            onMoreClick = onMoreClick
        )

        // tax
        StatusListItem(
            title = vehicleSummary.taxStatus.title?.let {
                AccessibleString(displayText = it)
            },
            description = AccessibleString(
                displayText = vehicleSummary.taxStatus.description
            ),
            icon = vehicleSummary.taxStatus.icon,
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
            icon = vehicleSummary.motStatus.icon,
        )

        // details
        InternalLinkListItem(
            title = stringResource(R.string.vehicle_details_title),
            onClick = { onVehicleDetailsClick(vehicleSummary.registration) },
            isFirst = false,
            isLast = true
        )
    }
}

@Composable
fun VehicleSummaryHeader(
    registration: String,
    make: String,
    model: String,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SummaryCardHeader(
        modifier = modifier,
        leadingContent = {
            // reg plate
            RegistrationPlate(registration = registration)
        },
        onMoreClick = onMoreClick
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
            onMoreClick = {}
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
                    icon = uk.gov.govuk.design.R.drawable.ic_check_round,
                ),
                motStatus = StatusRowUiModel(
                    title = "MOT",
                    description = "Valid until 24 April 2026",
                    icon = uk.gov.govuk.design.R.drawable.ic_cancel_round
                )
            ),
            onVehicleDetailsClick = {},
            onMoreClick = {}
        )
    }
}