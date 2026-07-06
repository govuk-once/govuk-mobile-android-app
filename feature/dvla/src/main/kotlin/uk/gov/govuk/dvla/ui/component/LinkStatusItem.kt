package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.extension.withAltText
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme
import kotlin.Unit

@Composable
internal fun LinkStatusItem(
    title: AccessibleString,
    text: AccessibleString,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    CardListItem(
        modifier = modifier,
        isFirst = isFirst,
        isLast = isLast,
        drawDivider = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            LargeVerticalSpacer()

            Title3BoldLabel(
                text = title.displayText,
                modifier = Modifier
                    .withAltText(title.altText)
            )

            SmallVerticalSpacer()

            Row(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .clickable(true) {
                        onClick.invoke()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                BodyRegularLabel(
                    text = text.displayText,
                    modifier = Modifier
                        .withAltText(text.altText)
                        .weight(1f),
                    color = GovUkTheme.colourScheme.textAndIcons.linkPrimary
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_external_link),
                    contentDescription = stringResource(R.string.opens_in_web_browser),
                    modifier = Modifier
                        .padding(start = GovUkTheme.spacing.small),
                    tint = GovUkTheme.colourScheme.textAndIcons.linkPrimary
                )
            }
            LargeVerticalSpacer()
        }
    }
}

@PreviewLightDark
@Composable
private fun LinkStatusItemPreview() {
    GovUkTheme {
        LinkStatusItem(
            title = AccessibleString("Title"),
            text = AccessibleString("Text"),
            onClick = {}
        )
    }
}
