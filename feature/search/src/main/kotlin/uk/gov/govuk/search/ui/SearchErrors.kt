package uk.gov.govuk.search.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.error.OfflineMessage
import uk.gov.govuk.design.ui.component.error.ProblemMessage
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.search.R
import uk.gov.govuk.search.SearchUiState

@Composable
internal fun SearchError(
    error: SearchUiState.Error,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier.verticalScroll(rememberScrollState())) {
        when (error) {
            is SearchUiState.Error.Empty ->
                NoResults(
                    searchTerm = error.searchTerm,
                    focusRequester = focusRequester
                )
            is SearchUiState.Error.Offline ->
                OfflineMessage(
                    onButtonClick = { onRetry(error.searchTerm) },
                    focusRequester = focusRequester
                )
            is SearchUiState.Error.ServiceError ->
                ProblemMessage(
                    focusRequester = focusRequester
                )
            else -> { } // Do nothing
        }
    }

    LaunchedEffect(error.uuid) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun NoResults(
    searchTerm: String,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.padding(
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.large,
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.medium
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BodyRegularLabel(
            text = "${stringResource(R.string.search_no_results)} '${searchTerm}'",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .focusRequester(focusRequester)
                .focusable()
        )
    }
}