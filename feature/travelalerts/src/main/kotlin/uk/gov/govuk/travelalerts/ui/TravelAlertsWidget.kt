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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.CentredCardWithIcon
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.ExtraSmallVerticalSpacer
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.travelalerts.R

@Composable
fun TravelAlertsWidget () {
    val viewModel: TravelAlertsWidgetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        TravelAlertsWidgetViewModel.State.Loading -> TravelAlertsLoading()
        TravelAlertsWidgetViewModel.State.Loaded -> TravelAlertsLoaded()
        TravelAlertsWidgetViewModel.State.Error -> TravelAlertsError()
    }

    LaunchedEffect(Unit) {
        viewModel.onPageView()
    }
}

@Composable
private fun TravelAlertsLoaded() {
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
private fun TravelAlertsLoading() {
    Column {
        LoaderCard(modifier = Modifier.fillMaxWidth())
        SmallVerticalSpacer()
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
fun TravelAlertsWidgetLoadedPreview() {
    GovUkTheme {
        TravelAlertsLoaded()
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
private fun TravelAlertsErrorPreview() {
    GovUkTheme {
        TravelAlertsError()
    }
}