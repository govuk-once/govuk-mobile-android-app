package uk.gov.govuk.travelalerts.ui.editcountries

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CaptionRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.DestructiveButton
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.LoadingScreen
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.ToggleSwitch
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
                onFollowAnotherCountry = onFollowAnotherCountry,
                onToggleNotifications = viewModel::toggleNotifications,
                onUnfollowCountry = viewModel::unfollowCountry
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EditCountriesLoaded(
    countries: List<Country>,
    onFollowAnotherCountry: () -> Unit,
    onToggleNotifications: (slug: String, enabled: Boolean) -> Unit,
    onUnfollowCountry: (slug: String, currentNotificationsEnabled: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCountry = rememberSaveable { mutableStateOf<Country?>(null) }
    val notificationsEnabled = rememberSaveable { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

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
            itemsIndexed(countries) { index, country ->
                InternalLinkListItem(
                    title = AccessibleString(country.name),
                    isFirst = index == 0,
                    isLast = index == countries.lastIndex,
                    style = TrailingIcon(uk.gov.govuk.design.R.drawable.ic_more),
                    onClick = {
                        selectedCountry.value = country
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

    selectedCountry.value?.let { country ->
        CountryOptionsBottomSheet(
            country = country,
            sheetState = sheetState,
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        selectedCountry.value = null
                    }
                }
            },
            onNotificationsToggle = { isEnabled ->
                notificationsEnabled.value = isEnabled
                onToggleNotifications(country.slug, isEnabled)
            },
            onUnfollow = {
                onUnfollowCountry(country.slug, notificationsEnabled.value)
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        selectedCountry.value = null
                    }
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
@OptIn(ExperimentalMaterial3Api::class)
private fun CountryOptionsBottomSheet(
    country: Country,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onUnfollow: () -> Unit
) {
    val notificationsEnabled = rememberSaveable { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GovUkTheme.colourScheme.surfaces.screenBackground,
        contentColor = GovUkTheme.colourScheme.textAndIcons.primary
    ) {
        CountryOptionsBottomSheetContent(
            country = country,
            notificationsEnabled = notificationsEnabled.value,
            onDismiss = onDismiss,
            onNotificationsToggle = { isEnabled ->
                notificationsEnabled.value = isEnabled
                onNotificationsToggle(isEnabled)
            },
            onUnfollow = onUnfollow
        )
    }
}

@Composable
private fun CountryOptionsBottomSheetContent(
    country: Country,
    notificationsEnabled: Boolean,
    onDismiss: () -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onUnfollow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium)
            .padding(bottom = GovUkTheme.spacing.large)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.primary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        MediumVerticalSpacer()

        Text(
            text = country.name,
            style = GovUkTheme.typography.title2Bold,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier.fillMaxWidth()
        )

        MediumVerticalSpacer()
        MediumVerticalSpacer()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = GovUkTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyRegularLabel(
                text = stringResource(R.string.edit_countries_notifications),
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
            ToggleSwitch(
                checked = notificationsEnabled,
                onCheckedChange = onNotificationsToggle,
                testDescription = "notifications"
            )
        }

        SmallVerticalSpacer()

        DestructiveButton(
            text = stringResource(R.string.edit_countries_unfollow),
            onClick = onUnfollow,
            modifier = Modifier.padding(vertical = GovUkTheme.spacing.medium)
        )

        CaptionRegularLabel(
            stringResource(R.string.edit_bottom_sheet_footer),
            color = GovUkTheme.colourScheme.textAndIcons.secondary
        )
    }
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

    GovUkTheme {
        EditCountriesLoaded(
            countries = countries,
            onFollowAnotherCountry = {},
            onToggleNotifications = { _, _ -> },
            onUnfollowCountry = { _, _ -> }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@PreviewLightDark
private fun CountryOptionsBottomSheetContentPreview() {
    val country = Country("Bosnia and Herzegovina", "bosnia-and-herzegovina", "2022-01-01T00:00:00Z", listOf())

    GovUkTheme {
        CountryOptionsBottomSheetContent(
            country = country,
            notificationsEnabled = true,
            onDismiss = {},
            onNotificationsToggle = {},
            onUnfollow = {}
        )
    }
}
