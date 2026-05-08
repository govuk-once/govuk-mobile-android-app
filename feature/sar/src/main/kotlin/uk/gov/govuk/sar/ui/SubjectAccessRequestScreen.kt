package uk.gov.govuk.sar.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.BuildConfig.PRIVACY_POLICY_URL
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabelTrailingLink
import uk.gov.govuk.design.ui.component.CaptionRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.sar.R
import uk.gov.govuk.sar.SubjectAccessRequestViewModel

@Composable
internal fun SubjectAccessRequestRoute(
    onConfirm: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SubjectAccessRequestViewModel = hiltViewModel()

    SubjectAccessRequestScreen(
        onPageView = { viewModel.onExplainerPageView() },
        onConfirm = { text ->
            viewModel.onButtonClick(text)
            viewModel.saveUserData()
            onConfirm()
        },
        onClose = { text ->
            viewModel.onButtonClick(text)
            onClose()
        },
        modifier = modifier
    )
}

@Composable
private fun SubjectAccessRequestScreen(
    onPageView: () -> Unit,
    onConfirm: (String) -> Unit,
    onClose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        topBar = {
            val titleText = stringResource(R.string.sar_title)
            ChildPageHeader(
                text = titleText,
                dismissStyle = HeaderDismissStyle.Back {
                    onClose(titleText)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val bodyContent1 = stringResource(R.string.sar_body_content_1)
            val bodyContent1Bullet1 = stringResource(R.string.sar_body_content_1_bp_1)
            val bodyContent1Bullet2 = stringResource(R.string.sar_body_content_1_bp_2)
            val bodyContent2 = stringResource(R.string.sar_body_content_2)
            val bodyContent3Intro = stringResource(R.string.sar_body_content_3_intro)
            val bodyContent3Link = stringResource(R.string.sar_body_content_3_link)
            val opensInBrowser = stringResource(R.string.sar_opens_in_browser)
            val confirmText = stringResource(R.string.confirm)

            MediumVerticalSpacer()
            BodyRegularLabel(
                text = bodyContent1,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Left
            )

            val bullet = "\u2022"
            val messages = listOf(
                bodyContent1Bullet1,
                bodyContent1Bullet2
            )
            val paragraphStyle = ParagraphStyle(
                textIndent = TextIndent(restLine = 12.sp)
            )
            MediumVerticalSpacer()
            BodyRegularLabel(
                buildAnnotatedString {
                    messages.forEach {
                        withStyle(style = paragraphStyle) {
                            append(bullet)
                            append("  ")
                            append(it)
                        }
                    }
                }
            )

            MediumVerticalSpacer()
            BodyRegularLabel(
                text = bodyContent2,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Left
            )

            val uriHandler = LocalUriHandler.current
            val url = PRIVACY_POLICY_URL
            val altText = "$bodyContent3Intro $bodyContent3Link $opensInBrowser"

            MediumVerticalSpacer()
            BodyRegularLabelTrailingLink(
                introText = bodyContent3Intro,
                outroText = ".",
                linkText = bodyContent3Link,
                onClick = { uriHandler.openUri(url) },
                altText = altText,
                textColor = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Left,
                textDecoration = TextDecoration.Underline
            )

            MediumVerticalSpacer()
            PrimaryButton(
                text = confirmText,
                onClick = { onConfirm(confirmText) }
            )

            MediumVerticalSpacer()
            CaptionRegularLabel(
                text = stringResource(R.string.sar_body_button_subtext),
                color = GovUkTheme.colourScheme.textAndIcons.secondary,
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.large)
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun LightModePreview() {
    GovUkTheme {
        SubjectAccessRequestScreen(
            onPageView = {},
            onConfirm = {},
            onClose = {}
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
        SubjectAccessRequestScreen(
            onPageView = {},
            onConfirm = {},
            onClose = {}
        )
    }
}
