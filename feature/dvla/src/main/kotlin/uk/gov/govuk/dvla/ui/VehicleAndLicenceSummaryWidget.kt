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
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.LicenceUiState
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.VehicleAndLicenceSummaryUiState
import uk.gov.govuk.dvla.VehicleAndLicenceSummaryViewModel
import uk.gov.govuk.dvla.VehicleUiState
import uk.gov.govuk.dvla.ui.component.LicenceSummaryCard
import uk.gov.govuk.dvla.ui.component.VehicleSummaryCard
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiModel
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel

@Composable
fun VehicleAndLicenceSummaryWidget(
    modifier: Modifier = Modifier,
) {
    val viewModel: VehicleAndLicenceSummaryViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val licenceClipboardLabel = stringResource(R.string.clipboard_data_label_licence_number)

    when (val currentState = state) {
        is VehicleAndLicenceSummaryUiState.Hidden -> return // draw nothing if not linked
        is VehicleAndLicenceSummaryUiState.Content -> {

            // TODO show vehicles and licence below each other for now until segmented control is added
            Column(modifier = modifier) {

                when (val licenceState = currentState.licenceState) {
                    is LicenceUiState.Loading -> VehicleAndLicenceSummaryLoading(modifier = modifier)
                    is LicenceUiState.Error -> {
                        // TODO placeholder for now, tbc in future tickets
                    }

                    is LicenceUiState.Success -> {
                        LicenceSummarySuccess(
                            licenceSummary = licenceState.licence,
                            onMoreClick = {
                                // TODO to be handled in next ticket(s)
                            },
                            onLicenceNumberLongClick = {
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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

                // TODO for demonstration purpose at the moment, remove when segmented control is added
                SmallVerticalSpacer()

                when (val vehicleState = currentState.vehicleState) {
                    is VehicleUiState.Loading -> VehicleAndLicenceSummaryLoading(modifier = modifier)
                    is VehicleUiState.Error -> {
                        // TODO placeholder for now, tbc in future tickets
                    }

                    is VehicleUiState.Success -> {
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