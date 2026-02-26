package uk.gov.govuk.analytics.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.analytics.AnalyticsViewModel
import uk.gov.govuk.analytics.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumHorizontalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrivacyPolicyLink
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun AnalyticsConsentRoute(
    analyticsConsentCompleted: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AnalyticsViewModel = hiltViewModel()

    AnalyticsConsentScreen(
        onConsentGranted = {
            viewModel.onConsentGranted()
            analyticsConsentCompleted()
        },
        onConsentDenied = {
            viewModel.onConsentDenied()
            analyticsConsentCompleted()
        },
        launchBrowser = launchBrowser,
        modifier = modifier
    )
}

@Composable
private fun AnalyticsConsentScreen(
    onConsentGranted: () -> Unit,
    onConsentDenied: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPortrait =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Column(
        modifier
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(
                    top = if (isPortrait) GovUkTheme.spacing.extraLarge * 2
                    else GovUkTheme.spacing.extraLarge
                )
        ) {
            LargeTitleBoldLabel(
                text = stringResource(R.string.analytics_consent_title),
                modifier = Modifier.semantics { heading() }
            )
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.analytics_consent_bullet_title))
            MediumVerticalSpacer()
            BulletList()
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.analytics_consent_anonymous))
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.analytics_consent_stop))
            MediumVerticalSpacer()
            PrivacyPolicyLink(onClick = { _, url -> launchBrowser(url) })
            MediumVerticalSpacer()
        }

        val enableButtonText = stringResource(R.string.analytics_consent_button_enable)
        val disableButtonText = stringResource(R.string.analytics_consent_button_disable)

        FixedDoubleButtonGroup(
            primaryText = enableButtonText,
            onPrimary = onConsentGranted,
            secondaryText = disableButtonText,
            onSecondary = onConsentDenied
        )
    }
}

@Composable
private fun BulletList(
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        BulletItem(stringResource(R.string.analytics_consent_bullet_1))
        SmallVerticalSpacer()
        BulletItem(stringResource(R.string.analytics_consent_bullet_2))
        SmallVerticalSpacer()
        BulletItem(stringResource(R.string.analytics_consent_bullet_3))
        SmallVerticalSpacer()
        BulletItem(stringResource(R.string.analytics_consent_bullet_4))
    }
}

@Composable
private fun BulletItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(GovUkTheme.colourScheme.textAndIcons.primary)
        )
        MediumHorizontalSpacer()
        BodyRegularLabel(text)
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalyticsConsentPreview() {
    GovUkTheme {
        AnalyticsConsentScreen(
            onConsentGranted = { },
            onConsentDenied = { },
            launchBrowser = { }
        )
    }
}
