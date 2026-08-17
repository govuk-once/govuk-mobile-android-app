package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PHONE
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.chat.BuildConfig
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabelTrailingLink
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
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
        onPrivacyNoticeClick = { text, url ->
            viewModel.onPrivacyPolicyView(text = text, url = url)
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
    onPrivacyNoticeClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    RunOnceLaunchedEffect {
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

            BodyRegularLabelTrailingLink(
                introText = introText,
                outroText = outroText,
                linkText = linkText,
                onClick = {
                    onPrivacyNoticeClick(linkText, url)
                    uriHandler.openUri(url)
                },
                altText = altText,
                textColor = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center
            )
        },
        buttonContent = {
            val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(GovUkTheme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Buttons(
                        onClick = onClick,
                        onCancel = onCancel,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(GovUkTheme.spacing.medium)
                ) {
                    Buttons(
                        onClick = onClick,
                        onCancel = onCancel,
                        modifier = Modifier
                    )
                }
            }
        },
        modifier = modifier,
        animationRes = R.raw.chat_onboarding_three
    )
}

@Composable
private fun Buttons(
    onClick: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier
) {
    PrimaryButton(
        text = stringResource(id = R.string.onboarding_page_three_button),
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = GovUkTheme.spacing.medium)
            .background(GovUkTheme.colourScheme.surfaces.fixedContainer),
        enabled = true,
        externalLink = false
    )

    SecondaryButton(
        text = stringResource(id = R.string.onboarding_page_three_decline_button),
        onClick = onCancel,
        modifier = modifier
            .padding(
                start = GovUkTheme.spacing.medium,
                end = GovUkTheme.spacing.medium,
                top = GovUkTheme.spacing.medium
            )
    )
}

@PreviewLightDark
@Composable
private fun OnboardingPageThreePreview() {
    GovUkTheme {
        OnboardingPageThreeScreen(
            onPageView = {},
            onClick = {},
            onCancel = {},
            onBack = {},
            onPrivacyNoticeClick = { _, _ -> }
        )
    }
}

// Looks like @PreviewLightDark can't handle landscape light/dark mode yet

@Preview(
    device = "$PHONE, orientation=landscape",
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
private fun OnboardingPageThreePreviewLandscapeLight() {
    GovUkTheme {
        OnboardingPageThreeScreen(
            onPageView = {},
            onClick = {},
            onCancel = {},
            onBack = {},
            onPrivacyNoticeClick = { _, _ -> }
        )
    }
}

@Preview(
    device = "$PHONE, orientation=landscape",
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun OnboardingPageThreePreviewLandscapeDark() {
    GovUkTheme {
        OnboardingPageThreeScreen(
            onPageView = {},
            onClick = {},
            onCancel = {},
            onBack = {},
            onPrivacyNoticeClick = { _, _ -> }
        )
    }
}
