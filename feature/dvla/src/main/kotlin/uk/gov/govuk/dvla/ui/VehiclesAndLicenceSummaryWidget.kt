package uk.gov.govuk.dvla.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.CentredCardWithIcon
import uk.gov.govuk.design.ui.component.ConnectedButtonGroup
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.ButtonColours
import uk.gov.govuk.dvla.ui.model.UrlModel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.VehiclesAndLicenceSummaryViewModel
import uk.gov.govuk.dvla.ui.component.AddVehicleListItem
import uk.gov.govuk.dvla.ui.component.LicenceSummaryCard
import uk.gov.govuk.dvla.ui.component.VehicleSummaryCard
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiModel
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiState
import uk.gov.govuk.dvla.ui.model.MenuAction
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem
import uk.gov.govuk.dvla.ui.model.UiState
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel
import uk.gov.govuk.dvla.ui.model.VehiclesSummaryUiState
import uk.gov.govuk.design.ui.component.ConnectedButton.FIRST as VehiclesButton
import uk.gov.govuk.design.ui.component.ConnectedButton.SECOND as LicenceButton

@Composable
fun VehiclesAndLicenceSummaryWidget(
    launchBrowser: (String) -> Unit,
    onVehicleDetailsClick: (registration: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VehiclesAndLicenceSummaryViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()


    when (val currentState = state) {
        is UiState.Hidden -> {
            return // draw nothing if not linked
        }

        is UiState.Default -> {
            val activeButtonState = when (currentState.drivingView) {
                DrivingView.VEHICLES -> VehiclesButton
                DrivingView.LICENCE -> LicenceButton
            }

            Column(modifier = modifier) {
                SmallVerticalSpacer()

                ConnectedButtonGroup(
                    firstText = stringResource(R.string.vehicles),
                    secondText = stringResource(R.string.licence),
                    activeButton = activeButtonState,
                    onActiveStateChange = { button ->
                        when (button) {
                            VehiclesButton -> viewModel.onVehiclesSelected()
                            LicenceButton -> viewModel.onLicenceSelected()
                        }
                    },
                    colours = ButtonColours(
                        containerActive = GovUkTheme.colourScheme.surfaces.connectedButtonGroupActive,
                        containerInactive = GovUkTheme.colourScheme.surfaces.list
                    )
                )

                MediumVerticalSpacer()

                val context = LocalContext.current
                val hapticFeedback = LocalHapticFeedback.current
                val licenceClipboardLabel =
                    stringResource(R.string.clipboard_data_label_licence_number)
                val handleMenuItemClick: (OverflowMenuItem) -> Unit = { item ->
                    when (val action = item.action) {
                        is MenuAction.WebLink -> {
                            viewModel.onMenuItemClicked(
                                text = item.text.displayText,
                                url = action.url
                            )
                            launchBrowser(action.url)
                        }

                        is MenuAction.ClipboardCopy -> {
                            viewModel.onCopyLicenceMenuOptionClicked()
                            action.textToCopy.copyToClipboard(
                                context,
                                licenceClipboardLabel,
                                hapticFeedback
                            )
                        }
                    }
                }

                when (currentState.drivingView) {
                    DrivingView.VEHICLES -> {

                        val addVehicleUrl = viewModel.dvlaUrls?.addVehicle

                        VehiclesViewContent(
                            launchBrowser = { text, url ->
                                launchBrowser(url.external)
                                viewModel.onExternalButtonClicked(text, url.original)
                            },
                            onVehicleDetailsClick = { text, registration ->
                                viewModel.onButtonClicked(text)
                                onVehicleDetailsClick(registration)
                            },
                            vehiclesState = currentState.vehiclesState,
                            onMenuItemClick = handleMenuItemClick,
                            onAddVehiclesClick = addVehicleUrl?.let { url ->
                                { label ->
                                    viewModel.onAddVehiclesClicked(label, url)
                                    launchBrowser(url)
                                }
                            },
                            onAddAnotherVehicleClick = addVehicleUrl?.let { url ->
                                { label ->
                                    viewModel.onAddAnotherVehicleClicked(label, url)
                                    launchBrowser(url)
                                }
                            },
                            modifier = modifier
                        )
                    }

                    DrivingView.LICENCE -> {

                        LicenceViewContent(
                            launchBrowser = { text, url ->
                                launchBrowser(url.external)
                                viewModel.onExternalButtonClicked(text, url.original)
                            },
                            licenceState = currentState.licenceState,
                            onMenuItemClick = handleMenuItemClick,
                            onLicenceNumberLongClick = { licenceNumber ->
                                licenceNumber.copyToClipboard(
                                    context = context,
                                    label = licenceClipboardLabel,
                                    hapticFeedback = hapticFeedback
                                )
                                viewModel.onLicenceNumberLongPressed()
                            },
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VehiclesViewContent(
    launchBrowser: (text: String, url: UrlModel) -> Unit,
    onVehicleDetailsClick: (text: String, registration: String) -> Unit,
    vehiclesState: VehiclesSummaryUiState,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    onAddVehiclesClick: ((String) -> Unit)?,
    onAddAnotherVehicleClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    when (vehiclesState) {
        is VehiclesSummaryUiState.Loading -> VehiclesAndLicenceSummaryLoading(modifier)
        is VehiclesSummaryUiState.Error -> {
            // TODO placeholder for now, tbc in future tickets
        }

        is VehiclesSummaryUiState.Success -> {
            if (vehiclesState.vehicles.isEmpty()) {
                if (onAddVehiclesClick != null) {
                    VehiclesSummaryEmpty(
                        onAddVehiclesClick = onAddVehiclesClick,
                        modifier = modifier
                    )
                }
            } else {
                VehiclesSummarySuccess(
                    launchBrowser = launchBrowser,
                    vehicles = vehiclesState.vehicles,
                    onAddVehicleClick = onAddAnotherVehicleClick,
                    onVehicleDetailsClick = onVehicleDetailsClick,
                    onMenuItemClick = onMenuItemClick,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun LicenceViewContent(
    launchBrowser: (text: String, url: UrlModel) -> Unit,
    licenceState: LicenceSummaryUiState,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    onLicenceNumberLongClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (licenceState) {
        is LicenceSummaryUiState.Loading -> VehiclesAndLicenceSummaryLoading(modifier)
        is LicenceSummaryUiState.Error -> {
            // TODO placeholder for now, tbc in future tickets
        }
        is LicenceSummaryUiState.Success -> {
            LicenceSummarySuccess(
                launchBrowser = launchBrowser,
                licenceSummary = licenceState.licence,
                onMenuItemClick = onMenuItemClick,
                onLicenceNumberLongClick = { onLicenceNumberLongClick(licenceState.licence.licenceNumber) },
                modifier = modifier
            )
        }
    }
}

private fun String.copyToClipboard(
    context: Context,
    label: String,
    hapticFeedback: HapticFeedback
) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, this)
    clipboard.setPrimaryClip(clip)

    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
}

@Composable
private fun VehiclesAndLicenceSummaryLoading(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LoaderCard(modifier = Modifier.fillMaxWidth())
        SmallVerticalSpacer()
    }
}

@Composable
private fun VehiclesSummarySuccess(
    launchBrowser: (text: String, url: UrlModel) -> Unit,
    vehicles: List<VehicleSummaryUiModel>,
    onAddVehicleClick: ((String) -> Unit)?,
    onVehicleDetailsClick: (text: String, registration: String) -> Unit,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(GovUkTheme.spacing.medium)
    ) {
        vehicles.forEach { vehicle ->
            VehicleSummaryCard(
                launchBrowser = launchBrowser,
                vehicleSummary = vehicle,
                onVehicleDetailsClick = onVehicleDetailsClick,
                onMenuItemClick = onMenuItemClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (onAddVehicleClick != null) {
            val title = stringResource(R.string.add_vehicle)

            AddVehicleListItem(
                title = title,
                icon = uk.gov.govuk.design.R.drawable.ic_add,
                onClick = { onAddVehicleClick(title) },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun VehiclesSummaryEmpty(
    onAddVehiclesClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val description = stringResource(R.string.add_your_vehicles)

    Column(modifier = modifier) {
        CentredCardWithIcon(
            onClick = { onAddVehiclesClick(description) },
            icon = uk.gov.govuk.design.R.drawable.ic_add,
            description = description,
            drawBottomStroke = false,
            paddingValues = PaddingValues(
                vertical = 52.dp,
                horizontal = GovUkTheme.spacing.extraLarge
            ),
            modifier = Modifier.fillMaxWidth()
        )
        SmallVerticalSpacer()
    }
}

@Composable
private fun LicenceSummarySuccess(
    launchBrowser: (text: String, url: UrlModel) -> Unit,
    licenceSummary: LicenceSummaryUiModel,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    onLicenceNumberLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        LicenceSummaryCard(
            launchBrowser = launchBrowser,
            licenceSummary = licenceSummary,
            onMenuItemClick = onMenuItemClick,
            onLicenceNumberLongClick = { onLicenceNumberLongClick() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}