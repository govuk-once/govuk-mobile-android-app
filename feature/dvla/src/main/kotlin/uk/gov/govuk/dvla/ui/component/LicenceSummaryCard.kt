package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.AddressListItem
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.extension.longClickWithAltText
import uk.gov.govuk.design.ui.extension.withAltText
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiModel
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel
import uk.gov.govuk.dvla.ui.model.StatusUiModel
import uk.gov.govuk.dvla.util.toSpacedString

@Composable
internal fun LicenceSummaryCard(
    launchBrowser: (text: String, url: String) -> Unit,
    licenceSummary: LicenceSummaryUiModel,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
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
            menuItems = licenceSummary.menuItems,
            onMenuItemClick = onMenuItemClick,
            onLicenceNumberLongClick = onLicenceNumberLongClick,
        )

        // address
        AddressListItem(
            name = AccessibleString(
                displayText = licenceSummary.name,
                altText = stringResource(R.string.licence_name_alt_text, licenceSummary.name)
            ),
            address = AccessibleString(
                displayText = licenceSummary.formattedAddressLines.joinToString(separator = "\n"),
                altText = stringResource(
                    R.string.licence_address_alt_text,
                    licenceSummary.accessibleAddressLines
                )
            )
        )

        StatusUiItem(
            launchBrowser = launchBrowser,
            licenceSummary.statusUi
        )
    }
}

@Composable
internal fun LicenceSummaryHeader(
    licenceType: String,
    licenceNumber: String,
    menuItems: List<OverflowMenuItem>,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    onLicenceNumberLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val licenceTypeAltText = stringResource(R.string.licence_type_alt_text, licenceType)
    val licenceNumberAltText =
        stringResource(R.string.licence_number_alt_text, licenceNumber.toSpacedString())
    val clipboardCopyLabel = stringResource(R.string.clipboard_data_label_licence_number)

    SummaryCardHeader(
        modifier = modifier,
        leadingContent = {
            BodyRegularLabel(
                text = licenceType,
                modifier = Modifier.withAltText(licenceTypeAltText),
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
        },
        menuItems = menuItems,
        onMenuItemClick = onMenuItemClick
    ) {
        // licence number
        Title1BoldLabel(
            text = licenceNumber,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier.longClickWithAltText(
                altText = licenceNumberAltText,
                actionLabel = clipboardCopyLabel,
                onLongClick = onLicenceNumberLongClick
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
            menuItems = emptyList(),
            onMenuItemClick = {},
            onLicenceNumberLongClick = {}
        )
    }
}


@PreviewLightDark
@Composable
private fun LicenceSummaryCardPreview() {
    GovUkTheme {
        LicenceSummaryCard(
            launchBrowser = {_,_ ->},
            licenceSummary = LicenceSummaryUiModel(
                licenceType = "Full licence",
                licenceNumber = "ARENO803236AA170",
                name = "Ms Anna Ornella Arenö",
                addressLine1 = "29 Orchard Drive",
                city = "Milton Keynes",
                postcode = "PA98 J83",
                statusUi = StatusUiModel.StatusRow(
                    statusRowUi = StatusRowUiModel(
                        description = AccessibleString("Valid until 1 February 2027"),
                        iconStyle = uk.gov.govuk.design.ui.model.StatusListItemIconStyle.Success,
                    )
                )
            ),
            onMenuItemClick = {},
            onLicenceNumberLongClick = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun LicenceSummaryCardExpiredPreview() {
    GovUkTheme {
        LicenceSummaryCard(
            launchBrowser = {_,_ ->},
            licenceSummary = LicenceSummaryUiModel(
                licenceType = "Full licence",
                licenceNumber = "ARENO803236AA170",
                name = "Ms Anna Ornella Arenö",
                addressLine1 = "29 Orchard Drive",
                city = "Milton Keynes",
                postcode = "PA98 J83",
                statusUi = StatusUiModel.StatusRow(
                    statusRowUi = StatusRowUiModel(
                        description = AccessibleString("Expired on 1 February 2027"),
                        iconStyle = uk.gov.govuk.design.ui.model.StatusListItemIconStyle.Warning,
                    )
                )
            ),
            onMenuItemClick = {},
            onLicenceNumberLongClick = {}
        )
    }
}
