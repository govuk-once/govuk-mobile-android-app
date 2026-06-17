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
import androidx.compose.runtime.collectAsState
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
import uk.gov.govuk.design.ui.component.AddressListItem
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.design.ui.component.SpecificationsIcons
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.component.Title2BoldLabel
import uk.gov.govuk.design.ui.component.Title3RegularLabel
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.HeaderActionStyle
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.model.InternalLinkListItemStyle
import uk.gov.govuk.design.ui.model.SpecificationIconUiModel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.VehicleDetailsUiState
import uk.gov.govuk.dvla.VehicleDetailsViewModel
import uk.gov.govuk.dvla.ui.component.RegistrationPlate
import uk.gov.govuk.dvla.ui.model.KeeperUiModel
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel
import uk.gov.govuk.dvla.ui.model.VehicleDetailsUiModel

@Composable
internal fun VehicleDetailsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VehicleDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

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

        is VehicleDetailsUiState.Success -> SuccessScreen(
            onBack = onBack,
            onPageView = { viewModel.onPageView(it) },
            details = state.details
        )
    }
}

@Composable
private fun SuccessScreen(
    onBack: () -> Unit,
    onPageView: (title: String) -> Unit,
    details: VehicleDetailsUiModel,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.vehicle_details_success_title)
    RunOnceLaunchedEffect {
        onPageView(title)
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

            MediumVerticalSpacer()

            StatusListItem(
                title = details.taxStatus.title?.let {
                    AccessibleString(displayText = it)
                },
                description = AccessibleString(displayText = details.taxStatus.description),
                icon = details.taxStatus.icon,
                isFirst = true,
                background = Color.Transparent
            )

            StatusListItem(
                title = details.motStatus.title?.let {
                    AccessibleString(
                        displayText = it,
                        altText = stringResource(R.string.acronym_mot_alt_text)
                    )
                },
                description = AccessibleString(displayText = details.motStatus.description),
                icon = details.motStatus.icon,
                isLast = true,
                background = Color.Transparent
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
                InternalLinkListItem(
                    title = detail.title,
                    isFirst = index == 0,
                    isLast = index == details.specifications.size,
                    background = Color.Transparent,
                    style = InternalLinkListItemStyle.Info(
                        info = detail.info,
                        altText = detail.altText
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun SuccessScreenPreview() {
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
                "Calendar",
                "Calendar alt text"
            ),
            SpecificationIconUiModel(
                R.drawable.ic_petrol_diesel,
                "Diesel",
                "Diesel alt text"
            ),
            SpecificationIconUiModel(
                R.drawable.ic_colour,
                "Red",
                "Colour alt text"
            )
        ),
        StatusRowUiModel(
            title = "Tax status",
            description = "",
            icon = uk.gov.govuk.design.R.drawable.ic_check_round
        ),
        StatusRowUiModel(
            title = "MOT status",
            description = "",
            icon = uk.gov.govuk.design.R.drawable.ic_check_round
        ),
        specifications = listOf()
    )
    GovUkTheme {
        SuccessScreen({}, {}, details)
    }
}
