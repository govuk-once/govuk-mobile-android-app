package uk.gov.govuk.dvla.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
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
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.VehiclesAndLicenceSummaryViewModel
import uk.gov.govuk.dvla.ui.component.AddVehicleListItem
import uk.gov.govuk.dvla.ui.component.LicenceSummaryCard
import uk.gov.govuk.dvla.ui.component.VehicleSummaryCard
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiModel
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiState
import uk.gov.govuk.dvla.ui.model.UiState
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel
import uk.gov.govuk.dvla.ui.model.VehiclesSummaryUiState
import uk.gov.govuk.design.ui.component.ConnectedButton.FIRST as VehiclesButton
import uk.gov.govuk.design.ui.component.ConnectedButton.SECOND as LicenceButton

@Composable
fun VehiclesAndLicenceSummaryWidget(
    onLaunchBrowser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VehiclesAndLicenceSummaryViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()


    when (val currentState = state) {
        is UiState.Hidden -> return // draw nothing if not linked

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

                when (currentState.drivingView) {
                    DrivingView.VEHICLES -> {

                        // TODO move this somewhere else?
                        val addVehicleUrl = "https://driver-and-vehicles-account.service.gov.uk/add_vehicle"

                        VehiclesViewContent(
                            vehiclesState = currentState.vehiclesState,
                            onAddVehicleClick = { label ->
                                viewModel.onAddVehiclesClicked(label, addVehicleUrl)
                                onLaunchBrowser(addVehicleUrl)
                            },
                            modifier = modifier
                        )
                    }

                    DrivingView.LICENCE -> {

                        val context = LocalContext.current
                        val hapticFeedback = LocalHapticFeedback.current
                        val licenceClipboardLabel =
                            stringResource(R.string.clipboard_data_label_licence_number)

                        LicenceViewContent(
                            licenceState = currentState.licenceState,
                            onLicenceNumberLongClick = { licenceNumber ->
                                licenceNumber.copyToClipboard(
                                    context = context,
                                    label = licenceClipboardLabel,
                                    hapticFeedback = hapticFeedback
                                )
                                viewModel.onLicenceNumberCopied()
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
    vehiclesState: VehiclesSummaryUiState,
    onAddVehicleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (vehiclesState) {
        is VehiclesSummaryUiState.Loading -> VehiclesAndLicenceSummaryLoading(modifier)
        is VehiclesSummaryUiState.Error -> {
            // TODO placeholder for now, tbc in future tickets
        }

        is VehiclesSummaryUiState.Success -> {
            if (vehiclesState.vehicles.isEmpty()) {
                VehiclesSummaryEmpty(
                    onAddVehiclesClick = onAddVehicleClick,
                    modifier = modifier
                )
            } else {
                VehiclesSummarySuccess(
                    vehicles = vehiclesState.vehicles,
                    onAddVehicleClick = onAddVehicleClick,
                    onDetailsClick = { /* TODO to be handled in next ticket(s) */ },
                    onMoreClick = { /* TODO to be handled in next ticket(s) */ },
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun LicenceViewContent(
    licenceState: LicenceSummaryUiState,
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
                licenceSummary = licenceState.licence,
                onMoreClick = { /* TODO to be handled in next ticket(s) */ },
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
    vehicles: List<VehicleSummaryUiModel>,
    onAddVehicleClick: (String) -> Unit,
    onDetailsClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(GovUkTheme.spacing.medium)
    ) {
        vehicles.forEach { vehicle ->
            VehicleSummaryCard(
                vehicleSummary = vehicle,
                onDetailsClick = { onDetailsClick() },
                onMoreClick = { onMoreClick() },
                modifier = Modifier.fillMaxWidth()
            )
        }

        AddVehicleListItem(
            title = stringResource(R.string.add_vehicle),
            icon = uk.gov.govuk.design.R.drawable.ic_add,
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
        )
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
            verticalPadding = 52.dp,
            modifier = Modifier.fillMaxWidth()
        )
        SmallVerticalSpacer()
    }
}

@Composable
private fun LicenceSummarySuccess(
    licenceSummary: LicenceSummaryUiModel,
    onMoreClick: () -> Unit,
    onLicenceNumberLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        LicenceSummaryCard(
            licenceSummary = licenceSummary,
            onMoreClick = { onMoreClick() },
            onLicenceNumberLongClick = { onLicenceNumberLongClick() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}