package uk.gov.govuk.dvla.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import uk.gov.govuk.dvla.LicenceCategoriesUiState
import uk.gov.govuk.dvla.LicenceCategoriesViewModel

@Composable
internal fun LicenceCategoriesRoute(
    launchBrowser: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LicenceCategoriesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is LicenceCategoriesUiState.Success -> {
            LicenceCategoriesScreen(
                categories = state.categories,
                onBack = dropUnlessResumed { onBack() },
                onPageView = { viewModel.onPageView() },
                onCategoryInfoClick = { code ->
                    viewModel.onCategoryInfoClicked()
                    // TODO open the category detail (separate ticket)
                },
                onFurtherInfoLinkClick = { text, url ->
                    viewModel.onFurtherInfoLinkClicked(text, url)
                    launchBrowser(url)
                },
                modifier = modifier
            )
        }

        LicenceCategoriesUiState.Error -> {
            // TODO GOVUKAPP-4152
        }
    }
}