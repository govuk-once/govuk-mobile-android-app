package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.dvla.LicenceCategoriesUiState
import uk.gov.govuk.dvla.LicenceCategoriesViewModel
import uk.gov.govuk.dvla.domain.EntitlementType

@Composable
internal fun LicenceCategoriesRoute(
    launchBrowser: (String) -> Unit,
    onBack: () -> Unit,
    onCategoryInfoClick: (code: String, type: EntitlementType) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LicenceCategoriesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is LicenceCategoriesUiState.Success -> {
            LicenceCategoriesScreen(
                categories = state.categories,
                onBack = dropUnlessResumed { onBack() },

                onPageView = { title ->
                    viewModel.onPageView(title)
                },
                onCategoryInfoClick = { code, type ->
                    viewModel.onCategoryInfoClicked()
                    onCategoryInfoClick(code, type)
                },
                onFurtherInfoLinkClick = { text, url ->
                    viewModel.onFurtherInfoLinkClicked(text, url)
                    launchBrowser(url)
                },
                modifier = modifier
            )
        }

        LicenceCategoriesUiState.Error -> {
            LicenceCategoriesError(
                onBack = dropUnlessResumed { onBack() },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun LicenceCategoriesError(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.safeDrawingPadding().fillMaxWidth()) {
        FullScreenHeader(dismissStyle = HeaderDismissStyle.Back(onBack))

        // TODO tbc in separate ticket, awaiting requirements
    }
}
