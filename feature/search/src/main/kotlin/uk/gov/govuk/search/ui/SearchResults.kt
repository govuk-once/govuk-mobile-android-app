package uk.gov.govuk.search.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.GovUkOutlinedCard
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SearchResultCard
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.search.R
import uk.gov.govuk.search.data.remote.model.SearchResult
import uk.gov.govuk.search.domain.StringUtils
import uk.gov.govuk.design.R as DesignR

@Composable
internal fun SearchResults(
    searchTerm: String,
    searchResults: List<SearchResult>,
    recommendedResults: List<SearchResult>,
    onClick: (SearchResult, Int) -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val dummyFocusRequester = remember { FocusRequester() }
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
        if (recommendedResults.isNotEmpty()) {
            item {
                RecommendedHeader(focusRequester = focusRequester)
            }
            itemsIndexed(recommendedResults) { index, searchResult ->
                val title = StringUtils.collapseWhitespace(searchResult.title)
                val description = searchResult.description?.let { StringUtils.collapseWhitespace(it) }
                val url = StringUtils.buildFullUrl(searchResult.link)
                RecommendedSearchResultCard(
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
            if (searchResults.isNotEmpty()) {
                item { MediumVerticalSpacer() }
            }
        }

        if (searchResults.isNotEmpty()) {
            item {
                Header(
                    focusRequester = if (recommendedResults.isEmpty()) focusRequester else dummyFocusRequester,
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
private fun RecommendedHeader(
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val heading = stringResource(R.string.recommended_results_heading)

    Title3BoldLabel(
        text = heading,
        modifier = modifier
            .padding(horizontal = GovUkTheme.spacing.extraLarge)
            .padding(top = GovUkTheme.spacing.medium)
            .focusRequester(focusRequester)
            .focusable()
            .semantics {
                heading()
                contentDescription = heading
            }
    )
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

@Composable
private fun RecommendedSearchResultCard(
    title: String,
    description: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GovUkOutlinedCard(
        modifier = modifier,
        onClick = onClick,
        backgroundColour = Color(0xFFBDD9CE),
        borderColour = Color(0xFF4DA583)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            BodyBoldLabel(
                text = title,
                modifier = Modifier.weight(1f),
                color = GovUkTheme.colourScheme.textAndIcons.link,
            )
            Icon(
                painter = painterResource(DesignR.drawable.ic_external_link),
                contentDescription = stringResource(DesignR.string.opens_in_web_browser),
                tint = GovUkTheme.colourScheme.textAndIcons.link,
                modifier = Modifier.padding(start = GovUkTheme.spacing.medium)
            )
        }
        if (!description.isNullOrBlank()) {
            SmallVerticalSpacer()
            BodyRegularLabel(description)
        }
    }
}
