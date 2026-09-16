package uk.gov.govuk.travelalerts.ui.editcountries

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
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
                state = state,
                onFollowAnotherCountry = onFollowAnotherCountry,
                onToggleNotifications = viewModel::toggleNotifications,
                onUnfollowCountry = viewModel::unfollowCountry,
                onClearToggleError = viewModel::clearToggleError,
                onClearUnfollowError = viewModel::clearUnfollowError
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EditCountriesLoaded(
    state: EditCountriesViewModel.State.Loaded,
    onFollowAnotherCountry: () -> Unit,
    onToggleNotifications: (slug: String, enabled: Boolean) -> Unit,
    onUnfollowCountry: (slug: String, currentNotificationsEnabled: Boolean) -> Unit,
    onClearToggleError: () -> Unit,
    onClearUnfollowError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedSlug = rememberSaveable { mutableStateOf<String?>(null) }
    val selectedCountry = state.countries.find { it.slug == selectedSlug.value }
    val notificationsEnabled = rememberSaveable { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val groupsBySlug = state.groups.associateBy { it.group }

    // Update initial toggle state when country is selected
    LaunchedEffect(selectedCountry) {
        selectedCountry?.let { country ->
            val group = groupsBySlug[country.slug]
            notificationsEnabled.value = group?.subgroup == "daily"
        }
    }

    // Dismiss sheet when unfollow error occurs
    LaunchedEffect(state.unfollowError) {
        if (state.unfollowError != null) {
            sheetState.hide()
            selectedSlug.value = null
        }
    }

    Column(modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            item {
                MediumVerticalSpacer()
                BodyRegularLabel(
                    text = stringResource(R.string.edit_countries_description),
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )
                MediumVerticalSpacer()
            }
            itemsIndexed(state.countries) { index, country ->
                InternalLinkListItem(
                    title = AccessibleString(country.name),
                    isFirst = index == 0,
                    isLast = index == state.countries.lastIndex,
                    style = TrailingIcon(uk.gov.govuk.design.R.drawable.ic_more),
                    onClick = {
                        selectedSlug.value = country.slug
                    }
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

    selectedCountry?.let { country ->
        CountryOptionsBottomSheet(
            country = country,
            sheetState = sheetState,
            notificationsEnabled = notificationsEnabled.value,
            isTogglingNotifications = state.isTogglingNotifications,
            isUnfollowing = state.isUnfollowing,
            toggleError = state.toggleError,
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        selectedSlug.value = null
                    }
                }
            },
            onNotificationsToggle = { isEnabled ->
                notificationsEnabled.value = isEnabled
                onToggleNotifications(country.slug, isEnabled)
            },
            onUnfollow = {
                onUnfollowCountry(country.slug, notificationsEnabled.value)
            },
            onClearToggleError = onClearToggleError
        )
    }

    if (state.unfollowError != null) {
        AlertDialog(
            onDismissRequest = onClearUnfollowError,
            title = { Text(stringResource(R.string.edit_countries_error_title)) },
            text = { Text(stringResource(R.string.edit_countries_error_description)) },
            confirmButton = {
                Button(onClick = onClearUnfollowError) {
                    Text("OK")
                }
            }
        )
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
        LoadingScreen()
    }
}

@Composable
@PreviewLightDark
private fun EditCountriesErrorPreview() {
    GovUkTheme {
        EditCountriesError(onRetry = {})
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

    val state = EditCountriesViewModel.State.Loaded(
        countries = countries,
        groups = listOf()
    )

    GovUkTheme {
        EditCountriesLoaded(
            state = state,
            onFollowAnotherCountry = {},
            onToggleNotifications = { _, _ -> },
            onUnfollowCountry = { _, _ -> },
            onClearToggleError = {},
            onClearUnfollowError = {}
        )
    }
}
