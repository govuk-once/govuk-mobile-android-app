package uk.gov.govuk.dvla.ui.component

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiModel
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel

@Composable
internal fun LicenceSummaryCard(
    licenceSummary: LicenceSummaryUiModel,
    onMoreClick: () -> Unit,
    onLicenceNumberLongPress: () -> Unit,
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
        LicenceSummaryHeader(
            licenceType = licenceSummary.licenceType,
            licenceNumber = licenceSummary.licenceNumber,
            onMoreClick = onMoreClick
        )

        // valid until
        StatusListItem(
            title = licenceSummary.licenceStatus.title,
            description = licenceSummary.licenceStatus.description,
            icon = licenceSummary.licenceStatus.icon,
        )
    }
}

@Composable
fun LicenceSummaryHeader(
    licenceType: String,
    licenceNumber: String,
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
                .padding(GovUkTheme.spacing.medium)
        ) {
            // reg plate and overflow menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                BodyRegularLabel(
                    text = licenceType,
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )

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

            // licence number
            Title1BoldLabel(
                text = licenceNumber,
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LicenceHeaderPreview() {
    GovUkTheme {
        LicenceSummaryHeader(
            licenceType = "Full licence",
            licenceNumber = "ARENO803236AA170",
            onMoreClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun LicenceSummaryCardPreview() {
    GovUkTheme {
        LicenceSummaryCard(
            licenceSummary = LicenceSummaryUiModel(
                licenceType = "Full licence",
                licenceNumber = "ARENO803236AA170",
                name = "Ms Anna Ornella Arenö",
                address = "29 Orchard Drive",
                licenceStatus = StatusRowUiModel(
                    description = "Valid until 1 February 2027",
                    icon = uk.gov.govuk.design.R.drawable.ic_check_round,
                )
            ),
            onMoreClick = {},
            onLicenceNumberLongPress = {}
        )
    }
}