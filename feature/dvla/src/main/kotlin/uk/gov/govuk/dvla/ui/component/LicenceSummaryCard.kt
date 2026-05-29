package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.AddressListItem
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiModel
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel

@Composable
internal fun LicenceSummaryCard(
    licenceSummary: LicenceSummaryUiModel,
    onMoreClick: () -> Unit,
    onLicenceNumberLongClick: () -> Unit,
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
            onMoreClick = onMoreClick,
            onLicenceNumberLongClick = onLicenceNumberLongClick
        )

        // address
        AddressListItem(
            name = licenceSummary.name,
            addressLines = licenceSummary.formattedAddressLines
        )

        // valid until
        StatusListItem(
            title = licenceSummary.licenceStatus.title,
            description = licenceSummary.licenceStatus.description,
            icon = licenceSummary.licenceStatus.icon,
            isLast = true
        )
    }
}

@Composable
fun LicenceSummaryHeader(
    licenceType: String,
    licenceNumber: String,
    onMoreClick: () -> Unit,
    onLicenceNumberLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SummaryCardHeader(
        modifier = modifier,
        leadingContent = {
            BodyRegularLabel(
                text = licenceType,
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
        },
        onMoreClick = onMoreClick
    ) {
        // licence number
        Title1BoldLabel(
            text = licenceNumber,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier.combinedClickable(
                onClick = {}, // required for combinedClickable
                onLongClick = onLicenceNumberLongClick,
                onLongClickLabel = null     // TODO need long click label for accessibility?
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun LicenceHeaderPreview() {
    GovUkTheme {
        LicenceSummaryHeader(
            licenceType = "Full licence",
            licenceNumber = "ARENO803236AA170",
            onMoreClick = {},
            onLicenceNumberLongClick = {}
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
                addressLine1 = "29 Orchard Drive",
                city = "Milton Keynes",
                postcode = "PA98 J83",
                licenceStatus = StatusRowUiModel(
                    description = "Valid until 1 February 2027",
                    icon = uk.gov.govuk.design.R.drawable.ic_check_round,
                )
            ),
            onMoreClick = {},
            onLicenceNumberLongClick = {}
        )
    }
}