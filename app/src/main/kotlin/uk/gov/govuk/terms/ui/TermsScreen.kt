package uk.gov.govuk.terms.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.LoginEvent

@Composable
internal fun TermsRoute(
    onCompleted: (LoginEvent) -> Unit,
    modifier: Modifier = Modifier
) {
//    val viewModel: LoginViewModel = hiltViewModel()
//    val isLoading by viewModel.isLoading.collectAsState()

    TermsScreen(
        onAccept = { },
        modifier = modifier
    )
}

@Composable
private fun TermsScreen(
    onAccept: () -> Unit,
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
                onClick = { }
            )

            SmallVerticalSpacer()

            SecondaryButton(
                stringResource(R.string.terms_privacy_notice),
                onClick = { }
            )
        },
        footerContent = {
            FixedDoubleButtonGroup(
                primaryText = stringResource(R.string.terms_accept),
                onPrimary = { },
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
            onAccept = { }
        )
    }
}