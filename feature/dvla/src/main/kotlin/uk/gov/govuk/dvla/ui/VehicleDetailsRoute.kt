package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.AddressListItem
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.design.ui.component.SpecificationsIcons
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.component.Title2BoldLabel
import uk.gov.govuk.design.ui.component.Title3RegularLabel
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.HeaderActionStyle
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.design.ui.model.InternalLinkListItemStyle
import uk.gov.govuk.design.ui.model.SpecificationIconUiModel
import uk.gov.govuk.dvla.ui.model.UrlModel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.VehicleDetailsUiState
import uk.gov.govuk.dvla.VehicleDetailsViewModel
import uk.gov.govuk.dvla.ui.component.RegistrationPlate
import uk.gov.govuk.dvla.ui.component.StatusUiItem
import uk.gov.govuk.dvla.ui.model.KeeperUiModel
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel
import uk.gov.govuk.dvla.ui.model.StatusUiModel
import uk.gov.govuk.dvla.ui.model.VehicleDetailsUiModel

@Composable
internal fun VehicleDetailsRoute(
    launchBrowser: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VehicleDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is VehicleDetailsUiState.Loading -> {
            // TODO temporary until designed
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(36.dp),
                    color = GovUkTheme.colourScheme.surfaces.primary
                )
            }
        }

        is VehicleDetailsUiState.Error -> { /* TODO: no designs yet */ }

        is VehicleDetailsUiState.Success -> {
            val section = stringResource(R.string.vehicle_details_success_title)
            SuccessScreen(
                launchBrowser = { text, url ->
                    launchBrowser(url.urlToOpen)
                    viewModel.onExternalButtonClicked(text, url.originalUrl, section)
                },
                onBack = onBack,
                onPageView = { viewModel.onPageView(section) },
                details = state.details
            )
        }
    }
}

@Composable
private fun SuccessScreen(
    launchBrowser: (text: String, url: UrlModel) -> Unit,
    onBack: () -> Unit,
    onPageView: () -> Unit,
    details: VehicleDetailsUiModel,
    modifier: Modifier = Modifier
) {
    RunOnceLaunchedEffect {
        onPageView()
    }

    Column(
        modifier = modifier
            .safeDrawingPadding()
            .fillMaxWidth()
    ) {
        FullScreenHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack),
            actionStyle = HeaderActionStyle.OverflowActionButton(
                {
                    // TODO in future ticket
                }
            )
        )

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

            Title3RegularLabel(
                details.model,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(horizontal = GovUkTheme.spacing.medium)
            )

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
}

@Preview
@Composable
private fun SuccessScreenPreview() {
    val date = AccessibleString("Calendar")
    val fuelType = AccessibleString("Diesel")
    val colour = AccessibleString("Red")
    val taxStatus = StatusUiModel.StatusRow(
        StatusRowUiModel(
            AccessibleString("Tax"),
            AccessibleString("Valid until 1 February 2027"),
            iconStyle = uk.gov.govuk.design.ui.model.StatusListItemIconStyle.Success
        )
    )

    val motStatus = StatusUiModel.StatusRow(
        StatusRowUiModel(
            AccessibleString("Mot"),
            AccessibleString("Valid until 1 February 2027"),
            iconStyle = uk.gov.govuk.design.ui.model.StatusListItemIconStyle.Success
        )
    )
    val details = VehicleDetailsUiModel(
        "Volkswagen",
        "ID4",
        "TE5T PL8",
        KeeperUiModel(
            "Name",
            "Street",
            "City",
            "Postcode"
        ),
        listOf(
            SpecificationIconUiModel(
                R.drawable.ic_calendar,
                date
            ),
            SpecificationIconUiModel(
                R.drawable.ic_petrol_diesel,
                fuelType
            ),
            SpecificationIconUiModel(
                R.drawable.ic_colour,
                colour
            )
        ),
        taxStatus,
        motStatus,
        specifications = listOf()
    )
    GovUkTheme {
        SuccessScreen({ _, _ -> },{}, {}, details)
    }
}
