package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.safeDrawingPadding
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
import uk.gov.govuk.dvla.ui.model.MenuAction

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
        is CheckVehicleDetailsUiState.Success -> {
            RunOnceLaunchedEffect {
                viewModel.onPageView(title)
            }

            CheckVehicleDetailsScreen(
                details = state.details,
                onClose = onBack,
                onMenuItemClick = { item ->
                    val action = item.action

                    if (action is MenuAction.WebLink) {
                        viewModel.onExternalButtonClicked(
                            text = item.text.displayText,
                            url = action.url,
                            section = title
                        )
                        launchBrowser(action.url)
                    }
                },
                launchBrowser = { text, url ->
                    viewModel.onExternalButtonClicked(
                        text = text,
                        url = url.originalUrl,
                        section = title
                    )
                    launchBrowser(url.urlToOpen)
                },
                modifier = modifier.safeDrawingPadding()
            )
        }

        CheckVehicleDetailsUiState.Error -> {
            // TODO coming in next tickets
        }
    }
}