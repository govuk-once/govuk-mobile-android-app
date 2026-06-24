package uk.gov.govuk.dvla.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.ConnectedButtonGroup
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.ButtonColours
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.VehiclesAndLicenceSummaryViewModel
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
    launchBrowser: (String) -> Unit,
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

                when (currentState.drivingView) {
                    DrivingView.VEHICLES -> {
                        VehiclesViewContent(
                            vehiclesState = currentState.vehiclesState,
                            onMenuItemClick = launchBrowser,
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
                            onMenuItemClick = launchBrowser,
                            onLicenceNumberLongClick = { licenceNumber ->
                                licenceNumber.copyToClipboard(
                                    context = context,
                                    label = licenceClipboardLabel,
                                    hapticFeedback = hapticFeedback
                                )
                                viewModel.onLicenceNumberCopied()
                            },
                            onRenewLicenceClick = viewModel.dvlaUrls?.renewLicence?.let { url ->
                                { text: String ->
                                    viewModel.onRenewLicenceClicked(text = text, url = url)
                                    launchBrowser(url)
                                }
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
    onMenuItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (vehiclesState) {
        is VehiclesSummaryUiState.Loading -> VehiclesAndLicenceSummaryLoading(modifier)
        is VehiclesSummaryUiState.Error -> {
            // TODO placeholder for now, tbc in future tickets
        }
        is VehiclesSummaryUiState.Success -> {
            VehiclesSummarySuccess(
                vehicles = vehiclesState.vehicles,
                onDetailsClick = { /* TODO to be handled in next ticket(s) */ },
                onMenuItemClick = onMenuItemClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun LicenceViewContent(
    licenceState: LicenceSummaryUiState,
    onMenuItemClick: (String) -> Unit,
    onLicenceNumberLongClick: (String) -> Unit,
    onRenewLicenceClick: ((String) -> Unit)?,
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
                onMenuItemClick = onMenuItemClick,
                onLicenceNumberLongClick = { onLicenceNumberLongClick(licenceState.licence.licenceNumber) },
                onRenewLicenceClick = onRenewLicenceClick,
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
    onDetailsClick: () -> Unit,
    onMenuItemClick: (String) -> Unit,
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
                onMenuItemClick = onMenuItemClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LicenceSummarySuccess(
    licenceSummary: LicenceSummaryUiModel,
    onMenuItemClick: (String) -> Unit,
    onLicenceNumberLongClick: () -> Unit,
    onRenewLicenceClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        LicenceSummaryCard(
            licenceSummary = licenceSummary,
            onMenuItemClick = onMenuItemClick,
            onLicenceNumberLongClick = { onLicenceNumberLongClick() },
            onRenewClick = onRenewLicenceClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}