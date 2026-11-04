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
import uk.gov.govuk.dvla.LicenceCategoryDetailUiState
import uk.gov.govuk.dvla.LicenceCategoryDetailViewModel

@Composable
internal fun LicenceCategoryDetailRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LicenceCategoryDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is LicenceCategoryDetailUiState.Success -> {
            LicenceCategoryDetailScreen(
                detail = state.detail,
                onBack = dropUnlessResumed { onBack() },
                onPageView = { title ->
                    viewModel.onPageView(title)
                },
                modifier = modifier
            )
        }

        LicenceCategoryDetailUiState.Error -> {
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