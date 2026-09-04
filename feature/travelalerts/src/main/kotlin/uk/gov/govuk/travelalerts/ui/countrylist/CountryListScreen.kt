package uk.gov.govuk.travelalerts.ui.countrylist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.LoadingScreen
import uk.gov.govuk.design.ui.component.MediumHorizontalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.component.Title2BoldLabel
import uk.gov.govuk.design.ui.component.error.ErrorPage
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.InternalLinkListItemStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.travelalerts.R
import uk.gov.govuk.travelalerts.data.model.Country

@Composable
fun CountryListScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CountryListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onPageView()
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { onClose() }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.surfaceModal)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        CountryListHeader(onClose = onClose)

        when (val state = uiState) {
            is CountryListViewModel.State.Loading -> LoadingScreen()
            is CountryListViewModel.State.Error -> CountryListError(onRetry = viewModel::onPageView)
            is CountryListViewModel.State.Loaded -> CountryListLoaded(
                countries = state.countries,
                searchQuery = state.searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onCountrySelected = viewModel::onCountrySelected
            )
        }
    }
}

@Composable
private fun CountryListHeader(onClose: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Title2BoldLabel(
            text = stringResource(R.string.follow_a_country_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 56.dp)
                .semantics { heading() },
            textAlign = TextAlign.Center
        )
        TextButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(uk.gov.govuk.design.R.string.content_desc_close),
                tint = GovUkTheme.colourScheme.textAndIcons.linkSecondary
            )
        }
    }
}

@Composable
private fun CountryListLoaded(
    countries: List<Country>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        CountrySearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier.padding(
                horizontal = GovUkTheme.spacing.medium,
                vertical = GovUkTheme.spacing.small
            )
        )
        if (countries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.country_list_no_results),
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = WindowInsets.navigationBars.asPaddingValues(),
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
            ) {
                item { MediumVerticalSpacer() }
                itemsIndexed(countries) { index, country ->
                    InternalLinkListItem(
                        title = AccessibleString(country.name),
                        onClick = { onCountrySelected(country) },
                        isFirst = index == 0,
                        isLast = index == countries.lastIndex,
                        style = InternalLinkListItemStyle.Simple,
                        background = GovUkTheme.colourScheme.surfaces.listAlt
                    )
                }
                item { LargeVerticalSpacer() }
            }
        }
    }
}

@Composable
private fun CountrySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = GovUkTheme.typography.bodyRegular.copy(
            color = GovUkTheme.colourScheme.textAndIcons.primary
        ),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(GovUkTheme.colourScheme.surfaces.textFieldBackground),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MediumHorizontalSpacer()
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.primary
                )
                SmallHorizontalSpacer()
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        BodyRegularLabel(
                            text = stringResource(R.string.country_list_search_placeholder),
                            color = GovUkTheme.colourScheme.textAndIcons.secondary
                        )
                    }
                    innerTextField()
                }
                MediumHorizontalSpacer()
            }
        }
    )
}

@Composable
private fun CountryListError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorPage(
        headerText = stringResource(R.string.country_list_error_title),
        subText = listOf(stringResource(R.string.country_list_error_description)),
        modifier = modifier,
        footerContent = {
            FixedPrimaryButton(
                text = stringResource(R.string.country_list_error_retry),
                onClick = onRetry
            )
        }
    )
}

@Composable
@PreviewLightDark
private fun CountryListLoadingPreview() {
    GovUkTheme {
        Column(Modifier.fillMaxSize()) {
            CountryListHeader(onClose = {})
            LoadingScreen()
        }
    }
}

@Composable
@PreviewLightDark
private fun CountryListErrorPreview() {
    GovUkTheme {
        Column(Modifier.fillMaxSize()) {
            CountryListHeader(onClose = {})
            CountryListError(onRetry = {})
        }
    }
}

@Composable
@PreviewLightDark
private fun CountryListLoadedPreview() {
    val countries = listOf(
        Country("Afghanistan", "afghanistan", "2024-01-01T00:00:00Z", listOf()),
        Country("Albania", "albania", "2024-01-01T00:00:00Z", listOf()),
        Country("Algeria", "algeria", "2024-01-01T00:00:00Z", listOf()),
        Country("Andorra", "andorra", "2024-01-01T00:00:00Z", listOf()),
        Country("Anguilla", "anguilla", "2024-01-01T00:00:00Z", listOf()),
    )
    GovUkTheme {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            CountryListHeader(onClose = {})
            CountryListLoaded(countries = countries, searchQuery = "", onSearchQueryChange = {}, onCountrySelected = {})
        }
    }
}
