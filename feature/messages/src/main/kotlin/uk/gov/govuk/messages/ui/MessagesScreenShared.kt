package uk.gov.govuk.messages.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.BodySemiboldLabel
import uk.gov.govuk.design.ui.component.CalloutRegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.messages.R

@Composable
internal fun MessagesScreenNoInternet() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Filled.ErrorOutline,
            stringResource(R.string.error_icon_content_description),
            Modifier
                .padding(bottom = 16.dp, top = 32.dp)
                .size(32.dp)
                .semantics { hideFromAccessibility() },
            colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.textAndIcons.iconTertiary)
        )

        Column(Modifier.semantics(true) {}) {
            BodySemiboldLabel(
                stringResource(uk.gov.govuk.design.R.string.no_internet_title),
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                GovUkTheme.colourScheme.textAndIcons.primary,
                TextAlign.Center
            )

            BodyRegularLabel(
                stringResource(uk.gov.govuk.design.R.string.no_internet_description_short),
                Modifier.fillMaxWidth(),
                GovUkTheme.colourScheme.textAndIcons.primary,
                TextAlign.Center
            )
        }
    }
}

@Composable
internal fun MessagesScreenError() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Filled.ErrorOutline,
            stringResource(R.string.error_icon_content_description),
            Modifier
                .padding(bottom = 16.dp, top = 32.dp)
                .size(32.dp)
                .semantics { hideFromAccessibility() },
            colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.textAndIcons.iconTertiary)
        )

        Column(Modifier.semantics(true) {}) {
            BodySemiboldLabel(
                stringResource(R.string.error_title),
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                GovUkTheme.colourScheme.textAndIcons.primary,
                TextAlign.Center
            )

            BodyRegularLabel(
                stringResource(R.string.error_body),
                Modifier.fillMaxWidth(),
                GovUkTheme.colourScheme.textAndIcons.primary,
                TextAlign.Center
            )
        }
    }
}

@Composable
internal fun Footer() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
    ) {
        CalloutRegularLabel(
            stringResource(R.string.footer),
            Modifier
                .fillMaxWidth(),
            GovUkTheme.colourScheme.textAndIcons.secondary,
            TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    GovUkTheme {
        MessagesScreenError()
    }
}

@Preview(showBackground = true)
@Composable
private fun NoInternetPreview() {
    GovUkTheme {
        MessagesScreenNoInternet()
    }
}

@Preview(showBackground = true)
@Composable
private fun FooterPreview() {
    GovUkTheme {
        Footer()
    }
}

