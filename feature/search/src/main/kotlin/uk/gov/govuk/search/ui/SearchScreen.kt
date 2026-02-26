package uk.gov.govuk.search.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.delay
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.search.R
import uk.gov.govuk.search.SearchUiState
import uk.gov.govuk.search.SearchViewModel
import uk.gov.govuk.search.data.remote.model.SearchResult
import uk.gov.govuk.search.ui.component.SearchField
import uk.gov.govuk.search.ui.component.SearchFieldActions

@Composable
internal fun SearchRoute(
    onBack: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    SearchScreen(
        uiState = uiState,
        actions = SearchScreenActions(
            onPageView = { viewModel.onPageView() },
            onBack = onBack,
            onSearch = { searchTerm ->
                keyboardController?.hide()
                viewModel.onSearch(searchTerm)
            },
            onClear = {
                viewModel.onClear()
            },
            onResultClick = { term, result, index, count ->
                viewModel.onSearchResultClicked(
                    term = term,
                    result = result,
                    index = index,
                    count = count
                )
            },
            onRetry = { searchTerm ->
                viewModel.onSearch(searchTerm)
            },
            onRemoveAllPreviousSearches = {
                viewModel.onRemoveAllPreviousSearches()
            },
            onRemovePreviousSearch = { searchTerm ->
                viewModel.onRemovePreviousSearch(searchTerm)
            },
            onPreviousSearchClick = { searchTerm ->
                keyboardController?.hide()
                viewModel.onPreviousSearchClick(searchTerm)
            },
            onAutocomplete = { searchTerm ->
                viewModel.onAutocomplete(searchTerm)
            },
            onAutocompleteResultClick = { searchTerm ->
                keyboardController?.hide()
                viewModel.onAutocompleteResultClick(searchTerm)
            }
        ),
        launchBrowser = launchBrowser,
        modifier = modifier
    )
}

private class SearchScreenActions(
    val onPageView: () -> Unit,
    val onBack: () -> Unit,
    val onSearch: (String) -> Unit,
    val onClear: () -> Unit,
    val onResultClick: (String, SearchResult, Int, Int) -> Unit,
    val onRetry: (String) -> Unit,
    val onRemoveAllPreviousSearches: () -> Unit,
    val onRemovePreviousSearch: (String) -> Unit,
    val onAutocomplete: (String) -> Unit,
    val onPreviousSearchClick: (String) -> Unit,
    val onAutocompleteResultClick: (String) -> Unit,
)

@Composable
private fun SearchScreen(
    uiState: SearchUiState,
    actions: SearchScreenActions,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        actions.onPageView()
    }

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    var searchTerm by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier.clickable(
            indication = null,
            interactionSource = interactionSource
        ){
            keyboard?.hide()
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(GovUkTheme.colourScheme.surfaces.homeHeader)
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            MediumVerticalSpacer()

            val isLogoVisible = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

            if (isLogoVisible) {
                Image(
                    painter = painterResource(id = uk.gov.govuk.design.R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                MediumVerticalSpacer()
            }

            SearchField(
                searchTerm = searchTerm,
                placeholder = stringResource(R.string.search_placeholder),
                actions = SearchFieldActions(
                    onBack = actions.onBack,
                    onSearchTermChange = { newValue ->
                        if (searchTerm.text != newValue.text) {
                            if (newValue.text.isBlank()) {
                                actions.onClear()
                            } else {
                                actions.onAutocomplete(newValue.text)
                            }
                        }
                        searchTerm = newValue

                    },
                    onSearch = { actions.onSearch(searchTerm.text) },
                    onClear = actions.onClear
                ),
                focusRequester = focusRequester
            )

            MediumVerticalSpacer()
        }

        SearchContent(
            uiState = uiState,
            actions = actions,
            onSuggestionClick = {
                searchTerm = TextFieldValue(
                    text = it,
                    selection = TextRange(it.length)
                )
            },
            focusRequester = focusRequester,
            keyboard = keyboard,
            launchBrowser = launchBrowser
        )
    }
}

@Composable
private fun SearchContent(
    uiState: SearchUiState,
    actions: SearchScreenActions,
    onSuggestionClick: (String) -> Unit,
    focusRequester: FocusRequester,
    keyboard: SoftwareKeyboardController?,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var displayKeyboard by remember { mutableStateOf(false) }

    when {
        uiState.error != null ->
            SearchError(uiState.error, actions.onRetry)
        uiState.searchResults != null ->
            SearchResults(
                searchTerm = uiState.searchResults.searchTerm,
                searchResults = uiState.searchResults.values,
                onClick = { result, index ->
                    actions.onResultClick(
                        uiState.searchResults.searchTerm,
                        result,
                        index,
                        uiState.searchResults.values.size
                    )
                },
                launchBrowser = launchBrowser,
                modifier = modifier
            )
        uiState.suggestions != null -> {
            SearchAutocomplete(
                searchTerm = uiState.suggestions.searchTerm,
                suggestions = uiState.suggestions.values,
                onSearch = {
                    onSuggestionClick(it)
                    actions.onAutocompleteResultClick(it)
                },
                modifier = modifier
            )
            displayKeyboard = true
        }
        else -> {
            if (!uiState.performingSearch) {
                PreviousSearches(
                    previousSearches = uiState.previousSearches,
                    onClick = {
                        onSuggestionClick(it)
                        actions.onPreviousSearchClick(it)
                    },
                    onRemoveAll = actions.onRemoveAllPreviousSearches,
                    onRemove = actions.onRemovePreviousSearch,
                    modifier = modifier
                )
                displayKeyboard = true
            }
        }
    }

    LaunchedEffect(displayKeyboard) {
        if (displayKeyboard) {
            focusRequester.requestFocus()
            delay(100)
            keyboard?.show()
        }
    }
}
