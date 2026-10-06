package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.AddressListItem
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SpecificationsIcons
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.component.Title2BoldLabel
import uk.gov.govuk.design.ui.component.Title3RegularLabel
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.design.ui.model.InternalLinkListItemStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.component.RegistrationPlate
import uk.gov.govuk.dvla.ui.component.StatusUiItem
import uk.gov.govuk.dvla.ui.model.UrlModel
import uk.gov.govuk.dvla.ui.model.VehicleDetailsUiModel

@Composable
internal fun VehicleDetailsContent(
    launchBrowser: (text: String, url: UrlModel) -> Unit,
    details: VehicleDetailsUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Title1BoldLabel(
            text = details.make,
            modifier = Modifier
                .padding(top = 3.dp)
                .padding(horizontal = GovUkTheme.spacing.medium)
        )

        details.model?.let { model ->
            Title3RegularLabel(
                text = model,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(horizontal = GovUkTheme.spacing.medium)
            )
        }

        MediumVerticalSpacer()

        SpecificationsIcons(
            details.specificationsIcons,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        )

        MediumVerticalSpacer()

        Title2BoldLabel(
            text = stringResource(R.string.status_title),
            modifier = Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
                .semantics { heading() }
        )

        StatusUiItem(
            launchBrowser = launchBrowser,
            statusUiModel = details.taxStatus,
            background = Color.Transparent
        )

        StatusUiItem(
            launchBrowser = launchBrowser,
            statusUiModel = details.motStatus,
            background = Color.Transparent,
            isLast = true
        )

        details.keeper?.let {
            LargeVerticalSpacer()

            Title2BoldLabel(
                text = stringResource(R.string.registered_to_title),
                modifier = Modifier
                    .padding(horizontal = GovUkTheme.spacing.medium)
                    .semantics { heading() }
            )

            AddressListItem(
                name = AccessibleString(
                    displayText = details.keeper.name
                ),
                address = AccessibleString(
                    displayText = details.keeper.formattedAddressLines.joinToString(separator = "\n"),
                    altText = details.keeper.accessibleAddressLines.toString()
                ),
                isFirst = true,
                isLast = true,
                background = Color.Transparent
            )
        }

        LargeVerticalSpacer()

        Title2BoldLabel(
            text = stringResource(R.string.specification_title),
            modifier = Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
                .semantics { heading() }
        )

        MediumVerticalSpacer()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            RegistrationPlate(
                registration = details.registration,
                isLarge = true
            )
        }

        MediumVerticalSpacer()

        details.specifications.forEachIndexed { index, detail ->
            when (detail) {
                is InternalLinkListItemModel.Info -> {
                    InternalLinkListItem(
                        title = detail.title,
                        isFirst = index == 0,
                        isLast = index == details.specifications.lastIndex,
                        background = Color.Transparent,
                        style = InternalLinkListItemStyle.Info(
                            info = detail.info
                        )
                    )
                }
            }
        }
    }
}
