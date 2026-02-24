package uk.gov.govuk.chat.ui.component

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabelTrailingLink
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.LargeHorizontalSpacer
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ChatErrorPageWithRetry(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChatErrorPage(
        subText = stringResource(id = R.string.error_retry_page_subtext),
        modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium),
        additionalText = stringResource(id = R.string.error_retry_page_additional_text),
        buttonText = stringResource(id = R.string.error_retry_button_text),
        onRetry = onRetry
    )
}

@Composable
internal fun ChatErrorPageNoRetry(
    modifier: Modifier = Modifier
) {
    ChatErrorPage(
        subText = stringResource(id = R.string.error_page_subtext),
        modifier = modifier
    )
}

@Composable
private fun ChatErrorPage(
    subText: String,
    modifier: Modifier = Modifier,
    additionalText: String? = null,
    buttonText: String? = null,
    onRetry: (() -> Unit)? = null
) {
    CentreAlignedScreen(
        modifier = modifier,
        screenContent = {
            Icon(
                painter = painterResource(id = uk.gov.govuk.design.R.drawable.ic_error),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.height(IntrinsicSize.Min)
                    .padding(all = GovUkTheme.spacing.medium)
            )

            LargeHorizontalSpacer()

            LargeTitleBoldLabel(
                text = stringResource(id = R.string.error_page_header),
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            BodyRegularLabel(
                text = subText,
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            if (additionalText != null) {
                BodyRegularLabel(
                    text = additionalText,
                    textAlign = TextAlign.Center
                )
            } else {
                AdditionalText()
            }
        },
        footerContent = {
            if (buttonText != null && onRetry != null) {
                MediumVerticalSpacer()
                PrimaryButton(
                    text = buttonText,
                    onClick = onRetry,
                    modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
                    enabled = true,
                    externalLink = false
                )
                ExtraLargeVerticalSpacer()
            }
        }
    )
}

@Composable
private fun AdditionalText(
    modifier: Modifier = Modifier
) {
    val intro = stringResource(id = R.string.error_page_additional_text_intro)
    val linkText = stringResource(id = R.string.error_page_additional_text_link_text)
    val outro = stringResource(id = R.string.error_page_additional_text_outro)
    val url = stringResource(id = R.string.error_page_additional_text_url)
    val altText = "$intro $linkText ${stringResource(R.string.sources_open_in_text)} $outro"
    val uriHandler = LocalUriHandler.current

    BodyRegularLabelTrailingLink(
        introText = intro,
        outroText = " $outro",
        linkText = linkText,
        onClick = { uriHandler.openUri(url) },
        altText = altText,
        modifier = modifier,
        textColor = GovUkTheme.colourScheme.textAndIcons.primary,
        textAlign = TextAlign.Center
    )
}
