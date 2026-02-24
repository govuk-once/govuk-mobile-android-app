package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.chat.BuildConfig
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabelCenteredTrailingLink
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun OnboardingPageThreeRoute(
    onClick: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ChatViewModel = hiltViewModel()
    val cancelText = stringResource(id = R.string.onboarding_page_cancel_text)
    val continueText = stringResource(R.string.onboarding_page_three_button)

    OnboardingPageThreeScreen(
        {
            viewModel.onPageView(
                screenClass = Analytics.ONBOARDING_SCREEN_CLASS,
                screenName = Analytics.ONBOARDING_SCREEN_THREE_NAME,
                title = Analytics.ONBOARDING_SCREEN_THREE_TITLE
            )
        },
        onClick = {
            viewModel.onButtonClicked(
                text = continueText,
                section = Analytics.ONBOARDING_SCREEN_THREE_NAME
            )
            viewModel.setChatIntroSeen()
            onClick()
        },
        onCancel = {
            viewModel.onButtonClicked(
                text = cancelText,
                section = Analytics.ONBOARDING_SCREEN_THREE_NAME
            )
            onCancel()
        },
        onBack = {
            viewModel.onButtonClicked(
                text = Analytics.ONBOARDING_SCREEN_THREE_BACK_TEXT,
                section = Analytics.ONBOARDING_SCREEN_THREE_NAME
            )
            onBack()
        },
        modifier = modifier
    )
}

@Composable
private fun OnboardingPageThreeScreen(
    onPageView: () -> Unit,
    onClick: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    OnboardingPage(
        title = stringResource(id = R.string.onboarding_page_three_header),
        headerContent = {
            FullScreenHeader(
                dismissStyle = HeaderDismissStyle.Back(onBack)
            )
        },
        screenContent = {
            MediumVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(id = R.string.onboarding_page_three_text_one),
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()
            val introText = stringResource(id = R.string.onboarding_page_three_text_two)
            val outroText = "."
            val linkText = stringResource(id = R.string.onboarding_page_three_text_two_link_text)
            val altText = "$introText $linkText ${stringResource(R.string.sources_open_in_text)}$outroText"
            val uriHandler = LocalUriHandler.current
            val url = BuildConfig.PRIVACY_POLICY_URL

            BodyRegularLabelCenteredTrailingLink(
                introText = introText,
                outroText = outroText,
                linkText = linkText,
                onClick = { uriHandler.openUri(url) },
                altText = altText,
                textColor = GovUkTheme.colourScheme.textAndIcons.primary
            )
        },
        buttonContent = {
            PrimaryButton(
                text = stringResource(id = R.string.onboarding_page_three_button),
                onClick = onClick,
                modifier = Modifier
                    .padding(horizontal = GovUkTheme.spacing.medium)
                    .background(GovUkTheme.colourScheme.surfaces.fixedContainer),
                enabled = true,
                externalLink = false
            )
            SecondaryButton(
                text = stringResource(id = R.string.onboarding_page_three_decline_button),
                onClick = onCancel,
                modifier = Modifier
                    .padding(
                        start = GovUkTheme.spacing.medium,
                        end = GovUkTheme.spacing.medium,
                        top = GovUkTheme.spacing.medium
                    )
            )
        },
        modifier = modifier,
        animationRes = R.raw.chat_onboarding_three
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun LightModePreview() {
    GovUkTheme {
        OnboardingPageThreeScreen(
            onPageView = {},
            onClick = {},
            onCancel = {},
            onBack = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DarkModePreview() {
    GovUkTheme {
        OnboardingPageThreeScreen(
            onPageView = {},
            onClick = {},
            onCancel = {},
            onBack = {}
        )
    }
}
