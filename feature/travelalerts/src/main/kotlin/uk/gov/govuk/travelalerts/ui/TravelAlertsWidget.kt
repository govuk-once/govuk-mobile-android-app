package uk.gov.govuk.travelalerts.ui

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.CentredCardWithIcon
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.ExtraSmallVerticalSpacer
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.SectionHeadingLabelButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.travelalerts.R

@Composable
fun TravelAlertsWidget (launchBrowser: (String) -> Unit) {
    val viewModel: TravelAlertsWidgetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        TravelAlertsWidgetViewModel.State.Loading -> TravelAlertsLoading()
        TravelAlertsWidgetViewModel.State.Empty -> TravelAlertsEmpty()
        is TravelAlertsWidgetViewModel.State.Loaded -> TravelAlertsLoaded(state.rows) { row ->
            viewModel.onRowClick(row)
            launchBrowser(row.link)
        }
        TravelAlertsWidgetViewModel.State.Error -> TravelAlertsError()
    }

    LaunchedEffect(Unit) {
        viewModel.onPageView()
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
private fun TravelAlertsEmpty() {
    CentredCardWithIcon(
        onClick = { },
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
    onRowClick: (TravelAlertsWidgetViewModel.LoadedRow) -> Unit
) {
    Column {
        SectionHeadingLabel(
            title3 = stringResource(R.string.loaded_heading),
            button = SectionHeadingLabelButton(
                title = stringResource(R.string.loaded_button_edit),
                altText = stringResource(R.string.loaded_button_edit),
                onClick = { /* Not implemented yet */ }
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
                text = "Title",
                textAlign = TextAlign.Center
            )

            ExtraSmallVerticalSpacer()

            BodyRegularLabel("Body")

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
        TravelAlertsEmpty()
    }
}

@Composable
@PreviewLightDark
fun TravelAlertsWidgetLoadedPreview() {
    GovUkTheme {
        TravelAlertsLoaded(
            listOf(
                TravelAlertsWidgetViewModel.LoadedRow("Mock 1", "Updated on 12th September 26", "test"),
                TravelAlertsWidgetViewModel.LoadedRow("Mock 2", "Updated on 13th September 26", "test")
            )
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