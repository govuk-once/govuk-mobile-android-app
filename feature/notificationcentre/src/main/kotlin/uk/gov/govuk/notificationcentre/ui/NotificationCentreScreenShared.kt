package uk.gov.govuk.notificationcentre.ui

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
import uk.gov.govuk.notificationcentre.R

@Composable
internal fun NotificationCentreScreenNoInternet() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Filled.ErrorOutline,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 32.dp)
                .size(32.dp)
                .semantics { hideFromAccessibility() },
            contentDescription = stringResource(R.string.error_icon_content_description),
            colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.textAndIcons.iconTertiary)
        )

        Column(modifier = Modifier.semantics(true) {}) {
            BodySemiboldLabel(
                stringResource(uk.gov.govuk.design.R.string.no_internet_title),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                color = GovUkTheme.colourScheme.textAndIcons.primary

            )

            BodyRegularLabel(
                stringResource(uk.gov.govuk.design.R.string.no_internet_description_short),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
        }
    }
}

@Composable
internal fun NotificationCentreScreenError() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Filled.ErrorOutline,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 32.dp)
                .size(32.dp)
                .semantics { hideFromAccessibility() },
            contentDescription = stringResource(R.string.error_icon_content_description),
            colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.textAndIcons.iconTertiary)
        )

        Column(modifier = Modifier.semantics(true) {}) {
            BodySemiboldLabel(
                stringResource(R.string.error_title),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                color = GovUkTheme.colourScheme.textAndIcons.primary

            )

            BodyRegularLabel(
                stringResource(R.string.error_body),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
        }
    }
}

@Composable
internal fun Footer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
    ) {
        CalloutRegularLabel(
            stringResource(R.string.footer),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(),
            color = GovUkTheme.colourScheme.textAndIcons.secondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    GovUkTheme {
        NotificationCentreScreenError()
    }
}

@Preview(showBackground = true)
@Composable
private fun NoInternetPreview() {
    GovUkTheme {
        NotificationCentreScreenNoInternet()
    }
}

@Preview(showBackground = true)
@Composable
private fun FooterPreview() {
    GovUkTheme {
        Footer()
    }
}

