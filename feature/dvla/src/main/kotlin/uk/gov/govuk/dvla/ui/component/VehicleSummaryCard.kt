package uk.gov.govuk.dvla.ui.component

import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel

@Composable
internal fun VehicleSummaryCard(
    vehicleSummary: VehicleSummaryUiModel,
    onDetailsClick: () -> Unit,
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
            onMoreClick = onMoreClick,
            isFirst = true
        )

        // tax
        StatusListItem(
            title = vehicleSummary.taxStatus.title,
            description = vehicleSummary.taxStatus.description,
            icon = vehicleSummary.taxStatus.icon,
            isFirst = false,
            isLast = false
        )

        // MOT
        StatusListItem(
            title = vehicleSummary.motStatus.title,
            titleAltText = vehicleSummary.motStatus.titleAltText,
            description = vehicleSummary.motStatus.description,
            icon = vehicleSummary.motStatus.icon,
            isFirst = false,
            isLast = false
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
    val accessibleNumberPlate = registration.map { "$it " }.joinToString("").trim() // 'FH08PDH' to 'F H 0 8 P D H'
    val altText = stringResource(id = R.string.registration_plate_alt_text, accessibleNumberPlate)

    Box(
        modifier = modifier
            .background(
                color = GovUkTheme.colourScheme.surfaces.registrationPlate,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(top = 8.dp, bottom = 5.dp, start = 8.dp, end = 8.dp)
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
fun VehicleSummaryHeader(
    registration: String,
    make: String,
    model: String,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFirst: Boolean = true
) {
    CardListItem(
        modifier = modifier,
        isFirst = isFirst,
        isLast = false,
        drawDivider = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // reg plate and overflow menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                RegistrationPlate(registration = registration)

                // overflow
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GovUkTheme.colourScheme.surfaces.cardOverflowButton)
                        .clickable(onClick = onMoreClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = uk.gov.govuk.design.R.drawable.ic_more),
                        contentDescription = stringResource(R.string.more_options_alt_text),
                        tint = GovUkTheme.colourScheme.textAndIcons.cardOverflowIcon
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // make and model
            Title1BoldLabel(
                text = make,
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )

            BodyRegularLabel(
                text = model,
                color = GovUkTheme.colourScheme.textAndIcons.secondary
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
            onDetailsClick = {},
            onMoreClick = {}
        )
    }
}