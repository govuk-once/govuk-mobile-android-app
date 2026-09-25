package uk.gov.govuk.travelalerts.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.CentredCardWithIcon
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.ExtraSmallVerticalSpacer
import uk.gov.govuk.design.ui.component.InfoAlert
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.SectionHeadingLabelButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.travelalerts.R
import uk.gov.govuk.travelalerts.navigation.TRAVEL_ALERTS_FOLLOW_ERROR_KEY

@Composable
fun TravelAlertsWidget(
    launchBrowser: (String) -> Unit,
    onFollowCountry: () -> Unit,
    onEditCountries: () -> Unit = {}
) {
    val viewModel: TravelAlertsWidgetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // The result is written to the entry's own savedStateHandle, a different instance from the
    // one Hilt injects into the ViewModel, so it has to be observed here and forwarded.
    val backStackEntry = LocalLifecycleOwner.current as? NavBackStackEntry
    LaunchedEffect(backStackEntry) {
        val handle = backStackEntry?.savedStateHandle ?: return@LaunchedEffect
        handle.getStateFlow(TRAVEL_ALERTS_FOLLOW_ERROR_KEY, false).collect { pending ->
            if (pending) {
                handle[TRAVEL_ALERTS_FOLLOW_ERROR_KEY] = false
                viewModel.onFollowError()
            }
        }
    }

    when (val state = uiState.content) {
        TravelAlertsWidgetViewModel.State.Loading -> TravelAlertsLoading()
        TravelAlertsWidgetViewModel.State.Empty -> TravelAlertsEmpty(onFollowCountry)
        is TravelAlertsWidgetViewModel.State.Loaded -> TravelAlertsLoaded(
            rows = state.rows,
            onEditCountries = onEditCountries
        ) { row ->
            viewModel.onRowClick(row)
            launchBrowser(row.link)
        }
        TravelAlertsWidgetViewModel.State.Error -> TravelAlertsError()
    }

    LaunchedEffect(Unit) {
        viewModel.onPageView()
    }

    if (uiState.followError) {
        InfoAlert(
            title = R.string.follow_country_error_title,
            message = R.string.follow_country_error_description,
            buttonText = R.string.follow_country_error_button,
            onDismiss = { viewModel.onDismissFollowError() }
        )
    }
}

@Composable
private fun TravelAlertsLoading() {
    Column {
        LoaderCard(modifier = Modifier.fillMaxWidth())
        SmallVerticalSpacer()
    }
}

@Composable
private fun TravelAlertsEmpty(onFollowCountry: () -> Unit) {
    CentredCardWithIcon(
        onClick = onFollowCountry,
        icon = uk.gov.govuk.design.R.drawable.ic_add,
        title = stringResource(R.string.empty_title),
        description = stringResource(R.string.empty_description),
        paddingValues = PaddingValues(
            horizontal = GovUkTheme.spacing.extraLarge,
            vertical = GovUkTheme.spacing.extraLarge)
    )
}

@Composable
private fun TravelAlertsLoaded(
    rows: List<TravelAlertsWidgetViewModel.LoadedRow>,
    onEditCountries: () -> Unit,
    onRowClick: (TravelAlertsWidgetViewModel.LoadedRow) -> Unit
) {
    Column {
        SectionHeadingLabel(
            title3 = stringResource(R.string.loaded_heading),
            button = SectionHeadingLabelButton(
                title = stringResource(R.string.loaded_button_edit),
                altText = stringResource(R.string.loaded_button_edit),
                onClick = onEditCountries
            )
        )

        rows.forEachIndexed { index, row ->
            ExternalLinkListItem(
                title = row.headline,
                onClick = { onRowClick(row) },
                modifier = Modifier.semantics(mergeDescendants = true) { role = Role.Button },
                description = row.subtitle,
                isFirst = index == 0,
                isLast = index == rows.lastIndex
            )
        }
    }
}

@Composable
private fun TravelAlertsError() {
    CardListItem(
        modifier = Modifier,
        isFirst = true,
        isLast = true,
        drawDivider = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExtraLargeVerticalSpacer()

            Icon(
                painter = painterResource(id = uk.gov.govuk.design.R.drawable.ic_error),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.iconTertiary,
                modifier = Modifier
                    .size(32.dp)
            )

            MediumVerticalSpacer()

            BodyBoldLabel(
                text = stringResource(R.string.country_list_error_title),
                textAlign = TextAlign.Center
            )

            ExtraSmallVerticalSpacer()

            BodyRegularLabel(stringResource(R.string.country_list_error_description))

            ExtraLargeVerticalSpacer()
        }
    }
}

@Composable
@PreviewLightDark
private fun TravelAlertsLoadingPreview() {
    GovUkTheme {
        TravelAlertsLoading()
    }
}

@Composable
@PreviewLightDark
fun TravelAlertsWidgetEmptyPreview() {
    GovUkTheme {
        TravelAlertsEmpty(onFollowCountry = {})
    }
}

@Composable
@PreviewLightDark
fun TravelAlertsWidgetLoadedPreview() {
    GovUkTheme {
        TravelAlertsLoaded(
            rows = listOf(
                TravelAlertsWidgetViewModel.LoadedRow("Mock 1", "Updated on 12th September 26", "test"),
                TravelAlertsWidgetViewModel.LoadedRow("Mock 2", "Updated on 13th September 26", "test")
            ),
            onEditCountries = {}
        ) { }
    }
}

@Composable
@PreviewLightDark
private fun TravelAlertsErrorPreview() {
    GovUkTheme {
        TravelAlertsError()
    }
}