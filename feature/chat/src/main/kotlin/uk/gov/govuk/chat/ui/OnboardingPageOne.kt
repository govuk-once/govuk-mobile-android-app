package uk.gov.govuk.chat.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.design.ui.model.HeaderActionStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun OnboardingPageOneRoute(
    onClick: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ChatViewModel = hiltViewModel()
    val cancelText = stringResource(id = R.string.onboarding_page_cancel_text)
    val continueText = stringResource(id = R.string.onboarding_page_one_button)

    OnboardingPageOneScreen(
        onPageView = {
            viewModel.onPageView(
                screenClass = Analytics.ONBOARDING_SCREEN_CLASS,
                screenName = Analytics.ONBOARDING_SCREEN_ONE_NAME,
                title = Analytics.ONBOARDING_SCREEN_ONE_TITLE
            )
        },
        onClick = {
            viewModel.onButtonClicked(
                text = continueText,
                section = Analytics.ONBOARDING_SCREEN_ONE_NAME
            )
            onClick()
        },
        onCancel = {
            viewModel.onButtonClicked(
                text = cancelText,
                section = Analytics.ONBOARDING_SCREEN_ONE_NAME
            )
            onCancel()
        },
        modifier = modifier
    )
}

@Composable
private fun OnboardingPageOneScreen(
    onPageView: () -> Unit,
    onClick: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onCancel)

    RunOnceLaunchedEffect {
        onPageView()
    }

    OnboardingPage(
        title = stringResource(id = R.string.onboarding_page_one_header),
        headerContent = {
            FullScreenHeader(
                actionStyle = HeaderActionStyle.TextActionButton(
                    title = stringResource(id = R.string.onboarding_page_cancel_text),
                    onClick = onCancel
                )
            )
        },
        screenContent = {
            MediumVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(id = R.string.onboarding_page_one_text),
                textAlign = TextAlign.Center
            )
        },
        buttonContent = {
            PrimaryButton(
                text = stringResource(id = R.string.onboarding_page_one_button),
                onClick = onClick,
                modifier = Modifier
                    .padding(horizontal = GovUkTheme.spacing.medium)
                    .background(GovUkTheme.colourScheme.surfaces.fixedContainer),
                enabled = true,
                externalLink = false
            )
        },
        modifier = modifier,
        animationRes = R.raw.chat_onboarding_one
    )
}

@PreviewLightDark
@Composable
private fun OnboardingPageOnePreview() {
    GovUkTheme {
        OnboardingPageOneScreen(
            onPageView = {},
            onClick = {},
            onCancel = {}
        )
    }
}

