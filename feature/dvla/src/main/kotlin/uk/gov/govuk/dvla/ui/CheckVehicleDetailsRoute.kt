package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.dvla.CheckVehicleDetailsUiState
import uk.gov.govuk.dvla.CheckVehicleDetailsViewModel
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.component.CheckVehicleDetailsHeader
import uk.gov.govuk.dvla.ui.model.MenuAction
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CheckVehicleDetailsRoute(
    launchBrowser: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CheckVehicleDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val title = stringResource(R.string.vehicle_details_success_title)

    when (val state = uiState) {
        is CheckVehicleDetailsUiState.Default -> {
            val handleMenuItemClick: (OverflowMenuItem) -> Unit = { item ->
                if (item.action is MenuAction.WebLink) {
                    viewModel.onExternalButtonClicked(
                        text = item.text.displayText,
                        url = item.action.url,
                        section = title
                    )
                    launchBrowser(item.action.url)
                }
            }

            RunOnceLaunchedEffect {
                viewModel.onPageView(title)
            }

            Column(
                modifier = modifier.safeDrawingPadding()
            ) {
                CheckVehicleDetailsHeader(
                    onClose = onBack,
                    menuItems = state.details.menuItems,
                    onMenuItemClick = handleMenuItemClick
                )

                VehicleDetailsScreen(
                    launchBrowser = { text, url ->
                        viewModel.onExternalButtonClicked(
                            text = text,
                            url = url.originalUrl,
                            section = title
                        )
                        launchBrowser(url.urlToOpen)
                    },
                    details = state.details
                )
            }
        }

        is CheckVehicleDetailsUiState.Hidden -> { /* Do nothing */ }
    }
}
