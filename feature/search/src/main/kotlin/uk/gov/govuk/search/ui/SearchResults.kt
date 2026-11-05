package uk.gov.govuk.search.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SearchResultCard
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.search.R
import uk.gov.govuk.search.data.remote.model.SearchResult
import uk.gov.govuk.search.domain.StringUtils

@Composable
internal fun SearchResults(
    searchTerm: String,
    searchResults: List<SearchResult>,
    onClick: (SearchResult, Int) -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    var previousSearchTerm by rememberSaveable { mutableStateOf("") }
    val numberOfSearchResults =
        pluralStringResource(
            id = R.plurals.number_of_search_results,
            count = searchResults.size,
            searchResults.size
        )


    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState
    ) {
        item {
            Header(
                focusRequester = focusRequester,
                resultCountAltText = numberOfSearchResults
            )
        }
        itemsIndexed(searchResults) { index, searchResult ->
            val title = StringUtils.collapseWhitespace(searchResult.title)
            val description = searchResult.description?.let { StringUtils.collapseWhitespace(it) }
            val url = StringUtils.buildFullUrl(searchResult.link)
            SearchResultCard(
                title = title,
                description = description,
                onClick = {
                    onClick(searchResult, index)
                    launchBrowser(url)
                },

                modifier = Modifier.padding(
                    GovUkTheme.spacing.medium,
                    GovUkTheme.spacing.medium,
                    GovUkTheme.spacing.medium,
                    0.dp
                )
            )
        }
        item {
            MediumVerticalSpacer()
        }
    }


    LaunchedEffect(searchTerm) {
        // We only want to trigger scroll and focus if we have a new search (rather than orientation change)
        if (searchTerm != previousSearchTerm) {
            listState.animateScrollToItem(0)
            focusRequester.requestFocus()
            previousSearchTerm = searchTerm
        }
    }
}

@Composable
private fun Header(
    focusRequester: FocusRequester,
    resultCountAltText: String,
    modifier: Modifier = Modifier
) {
    val heading = stringResource(R.string.search_results_heading)
    val combinedDescription = "$resultCountAltText. $heading"

    Title3BoldLabel(
        text = heading,
        modifier = modifier
            .padding(horizontal = GovUkTheme.spacing.extraLarge)
            .padding(top = GovUkTheme.spacing.medium)
            .focusRequester(focusRequester)
            .focusable()
            .semantics {
                heading()
                contentDescription = combinedDescription
            }
    )
}