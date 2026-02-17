package uk.gov.govuk.terms.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.terms.TermsViewModel

@Composable
internal fun TermsRoute(
    onCompleted: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TermsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    TermsScreen(
        onAccept = {
            viewModel.onTermsAccepted()
            onCompleted()
        },
        onTerms = { uiState?.let { launchBrowser(it.termsUrl) } },
        onPrivacyPolicy = { }, // Todo - launch browser with privacy policy url
        modifier = modifier
    )

    LaunchedEffect(Unit) {
        viewModel.termsAccepted.collect {
            onCompleted()
        }
    }
}

@Composable
private fun TermsScreen(
    onAccept: () -> Unit,
    onTerms: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier
) {
    CentreAlignedScreen(
        modifier = modifier,
        screenContent = {
            val shouldShowLogo =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

            if (shouldShowLogo) {
                Image(
                    painter = painterResource(id = R.drawable.ic_terms),
                    contentDescription = null,
                )
                MediumVerticalSpacer()
            }

            LargeTitleBoldLabel(
                text = stringResource(R.string.terms_new_user_title),
                modifier = Modifier.semantics { heading() },
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            SecondaryButton(
                stringResource(R.string.terms_and_conditions),
                onClick = onTerms
            )

            SmallVerticalSpacer()

            SecondaryButton(
                stringResource(R.string.terms_privacy_notice),
                onClick = onPrivacyPolicy
            )
        },
        footerContent = {
            FixedDoubleButtonGroup(
                primaryText = stringResource(R.string.terms_accept),
                onPrimary = onAccept,
                secondaryText = stringResource(R.string.terms_do_not_accept),
                onSecondary = { }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun TermsPreview() {
    GovUkTheme {
        TermsScreen(
            onAccept = { },
            onTerms = { },
            onPrivacyPolicy = { }
        )
    }
}