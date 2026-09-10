package uk.gov.govuk.travelalerts.ui.editcountries

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.LoadingScreen
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.error.ErrorPage
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.model.InternalLinkListItemStyle.TrailingIcon
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.travelalerts.R
import uk.gov.govuk.travelalerts.data.model.Country

@Composable
fun EditCountriesScreen(
    onBack: () -> Unit,
    onFollowAnotherCountry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: EditCountriesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onPageView()
    }

    Column(
        modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        ChildPageHeader(
            text = stringResource(R.string.edit_countries_title),
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )

        when (val state = uiState) {
            is EditCountriesViewModel.State.Loading -> LoadingScreen()
            is EditCountriesViewModel.State.Error -> EditCountriesError(onRetry = viewModel::onRetry)
            is EditCountriesViewModel.State.Loaded -> EditCountriesLoaded(
                countries = state.countries,
                onFollowAnotherCountry = onFollowAnotherCountry
            )
        }
    }
}

@Composable
private fun EditCountriesLoaded(
    countries: List<Country>,
    onFollowAnotherCountry: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium)
    ) {
        item {
            MediumVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(R.string.edit_countries_description),
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
            MediumVerticalSpacer()
        }
        itemsIndexed(countries) { index, country ->
            InternalLinkListItem(
                title = AccessibleString(country.name),
                isFirst = index == 0,
                isLast = index == countries.lastIndex,
                style = TrailingIcon(uk.gov.govuk.design.R.drawable.ic_more)
            )
        }
        item {
            MediumVerticalSpacer()
            InternalLinkListItem(
                title = AccessibleString(stringResource(R.string.edit_countries_follow_another)),
                onClick = onFollowAnotherCountry,
                isFirst = true,
                isLast = true,
                style = TrailingIcon(uk.gov.govuk.design.R.drawable.ic_add)
            )
            LargeVerticalSpacer()
        }
    }
}

@Composable
private fun EditCountriesError(
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
private fun EditCountriesLoadingPreview() {
    GovUkTheme {
        Column(Modifier.fillMaxSize()) {
            ChildPageHeader(
                text = "Edit countries",
                dismissStyle = HeaderDismissStyle.Back {}
            )
            LoadingScreen()
        }
    }
}

@Composable
@PreviewLightDark
private fun EditCountriesErrorPreview() {
    GovUkTheme {
        Column(Modifier.fillMaxSize()) {
            ChildPageHeader(
                text = "Edit countries",
                dismissStyle = HeaderDismissStyle.Back {}
            )
            EditCountriesError(onRetry = {})
        }
    }
}

@Composable
@PreviewLightDark
private fun EditCountriesLoadedPreview() {
    val countries = listOf(
        Country("Bosnia and Herzegovina", "bosnia-and-herzegovina", "2022-01-01T00:00:00Z", listOf()),
        Country("Fiji", "fiji", "2023-01-01T00:00:00Z", listOf()),
        Country("Italy", "italy", "2024-01-01T00:00:00Z", listOf()),
        Country("St Helena, Ascension and Tristan da Cunha", "st-helena", "2025-01-01T00:00:00Z", listOf()),
    )
    GovUkTheme {
        Column(Modifier.fillMaxSize()) {
            ChildPageHeader(
                text = "Edit countries",
                dismissStyle = HeaderDismissStyle.Back {}
            )
            EditCountriesLoaded(countries = countries, onFollowAnotherCountry = {})
        }
    }
}
