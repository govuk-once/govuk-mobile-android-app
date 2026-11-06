package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.model.SpecificationIconUiModel
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.VehicleDetailsUiState
import uk.gov.govuk.dvla.VehicleDetailsViewModel
import uk.gov.govuk.dvla.ui.component.SummaryErrorCard
import uk.gov.govuk.dvla.ui.component.VehicleDetailsHeader
import uk.gov.govuk.dvla.ui.model.KeeperUiModel
import uk.gov.govuk.dvla.ui.model.MenuAction
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel
import uk.gov.govuk.dvla.ui.model.StatusUiModel
import uk.gov.govuk.dvla.ui.model.UrlModel
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
                    modifier = Modifier.size(36.dp),
                    color = GovUkTheme.colourScheme.surfaces.primary
                )
            }
        }

        is VehicleDetailsUiState.Error -> {
            val section = stringResource(R.string.driving_title)
            ErrorScreen(
                onBack = onBack,
                onPageView = { title -> viewModel.onPageView(title) },
                onClick = { text ->
                    launchBrowser(state.fallbackUrl.urlToOpen)
                    viewModel.onExternalButtonClicked(text, state.fallbackUrl.originalUrl, section)
                }
            )
        }

        is VehicleDetailsUiState.Success -> {
            val section = stringResource(R.string.vehicle_details_success_title)
            val handleMenuItemClick: (OverflowMenuItem) -> Unit = { item ->
                when (val action = item.action) {
                    is MenuAction.WebLink -> {
                        viewModel.onMenuItemClicked(
                            text = item.text.displayText,
                            url = action.url
                        )
                        launchBrowser(action.url)
                    }

                    is MenuAction.ClipboardCopy -> Unit
                }
            }
            VehicleDetailsScreen(
                launchBrowser = { text, url ->
                    launchBrowser(url.urlToOpen)
                    viewModel.onExternalButtonClicked(text, url.originalUrl, section)
                },
                onBack = onBack,
                onPageView = { viewModel.onPageView(section) },
                onMenuItemClick = handleMenuItemClick,
                details = state.details,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun VehicleDetailsScreen(
    launchBrowser: (text: String, url: UrlModel) -> Unit,
    onBack: () -> Unit,
    onPageView: () -> Unit,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
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
        VehicleDetailsHeader(
            onBack = onBack,
            menuItems = details.menuItems,
            onMenuItemClick = onMenuItemClick
        )
        VehicleDetailsContent(
            launchBrowser = launchBrowser,
            details = details
        )
    }
}

@Composable
private fun ErrorScreen(
    onBack: () -> Unit,
    onPageView: (title: String) -> Unit,
    onClick: (text: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val linkText = stringResource(R.string.vehicles_summary_loading_error_link_text)
    val errorText = stringResource(R.string.vehicles_summary_loading_error_text)

    RunOnceLaunchedEffect {
        onPageView(errorText)
    }

    Column(
        modifier = modifier
            .safeDrawingPadding()
            .fillMaxWidth()
    ) {
        FullScreenHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )
        SummaryErrorCard(
            text = AccessibleString(errorText),
            subIntroText = stringResource(R.string.vehicles_summary_loading_error_sub_text),
            subOutroText = "",
            subLinkText = linkText,
            onClick = {
                onClick(linkText)
            },
            modifier = Modifier.padding(all = GovUkTheme.spacing.medium)
        )
    }
}

@Preview
@Composable
internal fun VehicleDetailsScreenPreview() {
    val taxStatus = StatusUiModel.StatusRow(
        StatusRowUiModel(
            AccessibleString("Tax"),
            AccessibleString("Valid until 1 February 2027"),
            iconStyle = StatusListItemIconStyle.Success
        )
    )

    val motStatus = StatusUiModel.StatusRow(
        StatusRowUiModel(
            AccessibleString("Mot"),
            AccessibleString("Valid until 1 February 2027"),
            iconStyle = StatusListItemIconStyle.Success
        )
    )

    val details = VehicleDetailsUiModel(
        make = "Volkswagen",
        model = "ID4",
        registration = "TE5T PL8",
        keeper = KeeperUiModel(
            "Name",
            listOf("Street", "City", "Postcode")
        ),
        specificationsIcons = listOf(
            SpecificationIconUiModel(R.drawable.ic_calendar, AccessibleString("Calendar")),
            SpecificationIconUiModel(R.drawable.ic_petrol_diesel, AccessibleString("Diesel")),
            SpecificationIconUiModel(R.drawable.ic_colour, AccessibleString("Red"))
        ),
        taxStatus = taxStatus,
        motStatus = motStatus,
        specifications = listOf(),
        menuItems = listOf(
            OverflowMenuItem(
                text = AccessibleString("Change address"),
                action = MenuAction.WebLink("https://www.gov.uk/change-naddress")
            )
        )
    )

    GovUkTheme {
        VehicleDetailsScreen(
            launchBrowser = { _, _ -> },
            onBack = {},
            onPageView = {},
            onMenuItemClick = {},
            details = details
        )
    }
}

@PreviewLightDark
@Composable
internal fun ErrorScreenPreview() {
    GovUkTheme {
        ErrorScreen(
            onBack = {},
            onPageView = {},
            onClick = {}
        )
    }
}