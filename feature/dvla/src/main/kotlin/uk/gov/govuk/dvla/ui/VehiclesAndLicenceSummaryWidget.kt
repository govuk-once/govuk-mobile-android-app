package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.ConnectedButtonGroup
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.ButtonColours
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.ui.model.DrivingView
import uk.gov.govuk.dvla.ui.model.LicenceSummaryUiState
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.UiState
import uk.gov.govuk.dvla.VehiclesAndLicenceSummaryViewModel
import uk.gov.govuk.dvla.ui.model.VehiclesSummaryUiState
import uk.gov.govuk.dvla.ui.component.VehicleSummaryCard
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel
import uk.gov.govuk.design.ui.component.ConnectedButton.FIRST as VehiclesButton
import uk.gov.govuk.design.ui.component.ConnectedButton.SECOND as LicenceButton

@Composable
fun VehiclesAndLicenceSummaryWidget(
    modifier: Modifier = Modifier
) {
    val viewModel: VehiclesAndLicenceSummaryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val vehiclesSummaryUiState by viewModel.vehiclesSummaryUiState.collectAsState()
    val licenceSummaryUiState by viewModel.licenceSummaryUiState.collectAsState()

    uiState.let {
        when (it) {
            is UiState.Hidden -> { /* Show nothing */ }

            is UiState.Default -> {
                val activeButtonState = when (it.drivingView) {
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

                    when (it.drivingView) {
                        DrivingView.VEHICLES -> VehiclesSummary(uiState = vehiclesSummaryUiState)
                        DrivingView.LICENCE -> LicenceSummary(uiState = licenceSummaryUiState)
                    }
                }
            }
        }
    }
}

@Composable
private fun VehiclesSummary(
    uiState: VehiclesSummaryUiState,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is VehiclesSummaryUiState.Loading -> VehiclesAndLicenceSummaryLoading(modifier = modifier)
        is VehiclesSummaryUiState.Error -> {
            // TODO placeholder for now, tbc in future tickets
        }

        is VehiclesSummaryUiState.Success -> {
            VehiclesSummarySuccess(
                vehicles = uiState.vehicles,
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

@Composable
private fun LicenceSummary(
    uiState: LicenceSummaryUiState,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is LicenceSummaryUiState.Loading -> VehiclesAndLicenceSummaryLoading(modifier = modifier)
        is LicenceSummaryUiState.Error -> {
            // TODO placeholder for now, tbc in future tickets
        }

        is LicenceSummaryUiState.Success -> {
            // TODO placeholder for now, tbc in future tickets
        }
    }
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
