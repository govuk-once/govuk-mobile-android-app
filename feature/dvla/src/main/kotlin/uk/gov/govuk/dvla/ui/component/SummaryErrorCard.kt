package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabelTrailingLink
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.ExtraSmallVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.extension.withAltText
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun SummaryErrorCard(
    text: AccessibleString,
    subIntroText: String,
    subOutroText: String,
    subLinkText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subAltText: String? = null
) {
    CardListItem(
        modifier = modifier,
        isFirst = true,
        isLast = true,
        drawDivider = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExtraLargeVerticalSpacer()

            Icon(
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.iconTertiary,
                modifier = Modifier
                    .size(32.dp)
            )

            MediumVerticalSpacer()

            BodyBoldLabel(
                text = text.displayText,
                modifier = Modifier.withAltText(text.altText),
                textAlign = TextAlign.Center
            )

            ExtraSmallVerticalSpacer()

            BodyRegularLabelTrailingLink(
                introText = subIntroText,
                outroText = subOutroText,
                linkText = subLinkText,
                onClick = onClick,
                altText = subAltText,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline
            )

            ExtraLargeVerticalSpacer()
        }
    }
}

@PreviewLightDark
@Composable
private fun SummaryErrorCardPreview() {
    GovUkTheme {
        SummaryErrorCard(
            AccessibleString("Text"),
            "Sub text",
            "",
            "link text",
            {}
        )
    }
}
