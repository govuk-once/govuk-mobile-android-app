package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.chat.BuildConfig
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.model.HeaderActionStyle
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
                dismissStyle = HeaderDismissStyle.Back(onBack),
                actionStyle = HeaderActionStyle.ActionButton(
                    title = stringResource(id = R.string.onboarding_page_cancel_text),
                    onClick = onCancel
                )
            )
        },
        screenContent = {
            MediumVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(id = R.string.onboarding_page_three_text_one),
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()
            ContentWithLink()
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
                modifier = Modifier.padding(top = GovUkTheme.spacing.medium)
            )
        },
        modifier = modifier,
        animationRes = R.raw.chat_onboarding_three
    )
}

@Composable
private fun ContentWithLink(
    modifier: Modifier = Modifier
) {
    val intro = stringResource(id = R.string.onboarding_page_three_text_two)
    val linkText = stringResource(id = R.string.onboarding_page_three_text_two_link_text)
    val outro = "."
    val url = BuildConfig.PRIVACY_POLICY_URL

    val uriHandler = LocalUriHandler.current

    val annotatedString = buildAnnotatedString {
        append(intro)
        append(" ")
        pushStringAnnotation(tag = "URL", annotation = url)
        withStyle(
            style = SpanStyle(
                color = GovUkTheme.colourScheme.textAndIcons.link
            )
        ) {
            append(linkText)
        }
        pop()
        append(outro)
    }

    Text(
        text = annotatedString,
        style = GovUkTheme.typography.bodyRegular.copy(
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            textAlign = TextAlign.Center
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                annotatedString
                    .getStringAnnotations(tag = "URL", start = 0, end = annotatedString.length)
                    .firstOrNull()
                    ?.let { annotation ->
                        uriHandler.openUri(annotation.item)
                    }
            }
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
