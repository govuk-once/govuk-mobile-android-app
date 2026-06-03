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
import uk.gov.govuk.dvla.VehicleAndLicenceSummaryViewModel
import uk.gov.govuk.dvla.ui.component.LicenceSummaryCard
import uk.gov.govuk.dvla.ui.component.VehicleSummaryCard
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiModel
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiState
import uk.gov.govuk.dvla.ui.model.UiState
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiState
import uk.gov.govuk.design.ui.component.ConnectedButton.FIRST as VehicleButton
import uk.gov.govuk.design.ui.component.ConnectedButton.SECOND as LicenceButton

@Composable
fun VehicleAndLicenceSummaryWidget(
    modifier: Modifier = Modifier
) {
    val viewModel: VehicleAndLicenceSummaryViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val licenceClipboardLabel = stringResource(R.string.clipboard_data_label_licence_number)

    when (val currentState = state) {
        is UiState.Hidden -> return // draw nothing if not linked

        is UiState.Default -> {
            val activeButtonState = when (currentState.drivingView) {
                DrivingView.VEHICLE -> VehicleButton
                DrivingView.LICENCE -> LicenceButton
            }

            Column(modifier = modifier) {
                SmallVerticalSpacer()

                // Render the segmented toggle buttons
                ConnectedButtonGroup(
                    firstText = stringResource(R.string.vehicle),
                    secondText = stringResource(R.string.licence),
                    activeButton = activeButtonState,
                    onActiveStateChange = { button ->
                        when (button) {
                            VehicleButton -> viewModel.onVehicleSelected()
                            LicenceButton -> viewModel.onLicenceSelected()
                        }
                    },
                    colours = ButtonColours(
                        containerActive = GovUkTheme.colourScheme.surfaces.connectedButtonGroupActive,
                        containerInactive = GovUkTheme.colourScheme.surfaces.list
                    )
                )

                MediumVerticalSpacer()

                // Render the active tab content by unwrapping the sub-states inside Default
                when (currentState.drivingView) {
                    DrivingView.VEHICLE -> {
                        when (val vehicleState = currentState.vehicleState) {
                            is VehicleSummaryUiState.Loading -> VehicleAndLicenceSummaryLoading(modifier)
                            is VehicleSummaryUiState.Error -> {
                                // TODO placeholder for now, tbc in future tickets
                            }
                            is VehicleSummaryUiState.Success -> {
                                VehicleSummarySuccess(
                                    vehicles = vehicleState.vehicles,
                                    onDetailsClick = {
                                        // TODO to be handled in next ticket(s)
                                    },
                                    onMoreClick = {
                                        // TODO to be handled in next ticket(s)
                                    },
                                    modifier = modifier
                                )
                            }
                        }
                    }

                    DrivingView.LICENCE -> {
                        when (val licenceState = currentState.licenceState) {
                            is LicenceSummaryUiState.Loading -> VehicleAndLicenceSummaryLoading(modifier)
                            is LicenceSummaryUiState.Error -> {
                                // TODO placeholder for now, tbc in future tickets
                            }
                            is LicenceSummaryUiState.Success -> {
                                LicenceSummarySuccess(
                                    licenceSummary = licenceState.licence,
                                    onMoreClick = {
                                        // TODO to be handled in next ticket(s)
                                    },
                                    onLicenceNumberLongClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText(
                                            licenceClipboardLabel,
                                            licenceState.licence.licenceNumber
                                        )
                                        clipboard.setPrimaryClip(clip)
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    modifier = modifier
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun VehicleAndLicenceSummaryLoading(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LoaderCard(modifier = Modifier.fillMaxWidth())
        SmallVerticalSpacer()
    }
}

@Composable
private fun VehicleSummarySuccess(
    vehicles: List<VehicleSummaryUiModel>,
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

