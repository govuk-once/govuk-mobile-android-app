package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.dvla.CheckVehicleDetailsUiState
import uk.gov.govuk.dvla.CheckVehicleDetailsViewModel
import uk.gov.govuk.dvla.CheckVehicleSheetState
import uk.gov.govuk.dvla.CheckVehicleWidgetViewModel
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.component.CheckVehicleSheet
import uk.gov.govuk.dvla.ui.model.MenuAction

@Composable
internal fun CheckVehicleDetailsRoute(
    launchBrowser: (String) -> Unit,
    onBack: () -> Unit,
    onVehicleFound: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val detailsViewModel: CheckVehicleDetailsViewModel = hiltViewModel()
    val detailsState by detailsViewModel.uiState.collectAsStateWithLifecycle()

    val searchViewModel: CheckVehicleWidgetViewModel = hiltViewModel()
    val searchSheetState by searchViewModel.sheetState.collectAsStateWithLifecycle()

    var showSearchSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val title = stringResource(R.string.vehicle_details_success_title)

    LaunchedEffect(searchSheetState) {
        val state = searchSheetState

        if (state is CheckVehicleSheetState.Success) {
            showSearchSheet = false
            searchViewModel.onSheetDismissed()
            onVehicleFound(state.regNumber)
        }
    }

    when (val state = detailsState) {
        is CheckVehicleDetailsUiState.Success -> {
            RunOnceLaunchedEffect {
                detailsViewModel.onPageView(title)
            }

            CheckVehicleDetailsScreen(
                details = state.details,
                onClose = onBack,
                onSearch = {
                    showSearchSheet = true
                },
                onMenuItemClick = { item ->
                    val action = item.action

                    if (action is MenuAction.WebLink) {
                        detailsViewModel.onExternalButtonClicked(
                            text = item.text.displayText,
                            url = action.url,
                            section = title
                        )
                        launchBrowser(action.url)
                    }
                },
                launchBrowser = { text, url ->
                    detailsViewModel.onExternalButtonClicked(
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

    if (showSearchSheet) {
        val submitLabel = stringResource(R.string.check_vehicle_submit)
        val cancelLabel = stringResource(R.string.check_vehicle_cancel)

        CheckVehicleSheet(
            state = searchSheetState,
            onRegistrationChange = searchViewModel::onRegistrationNumberChanged,
            onClear = searchViewModel::onClearClicked,
            onSubmit = {
                searchViewModel.onSubmitClicked(submitLabel)
            },
            onCancel = {
                showSearchSheet = false
                searchViewModel.onCancelClicked(cancelLabel)
            }
        )
    }
}