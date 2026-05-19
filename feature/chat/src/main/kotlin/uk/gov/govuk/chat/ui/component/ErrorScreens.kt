package uk.gov.govuk.chat.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabelTrailingLink
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.error.ErrorPage
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ChatErrorPageWithRetry(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorPage(
        headerText = stringResource(id = R.string.error_page_header),
        subText = arrayOf(
            stringResource(id = R.string.error_retry_page_subtext),
            stringResource(id = R.string.error_retry_page_additional_text)
        ),
        modifier = modifier,
        footerContent = {
            FixedPrimaryButton(
                text = stringResource(id = R.string.error_retry_button_text),
                onClick = onRetry
            )
        }
    )
}

@Composable
internal fun ChatErrorPageNoRetry(
    modifier: Modifier = Modifier
) {
    ErrorPage(
        headerText = stringResource(id = R.string.error_page_header),
        subText = arrayOf(stringResource(id = R.string.error_page_subtext)),
        modifier = modifier,
        additionalContent = {
            MediumVerticalSpacer()
            AdditionalText()
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
