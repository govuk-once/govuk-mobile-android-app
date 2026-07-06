package uk.gov.govuk.dvla.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.MediumHorizontalSpacer
import uk.gov.govuk.design.ui.extension.withAltText
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun InfoStatusItem(
    title: AccessibleString,
    modifier: Modifier = Modifier,
    subtitle: AccessibleString? = null,
    @DrawableRes icon: Int? = null,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    CardListItem(
        modifier = modifier,
        isFirst = isFirst,
        isLast = isLast,
        drawDivider = true
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = GovUkTheme.spacing.medium)
                .padding(vertical = GovUkTheme.spacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Image(
                    painter = painterResource(R.drawable.ic_circle_p),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.textAndIcons.primary)
                )
                MediumHorizontalSpacer()
            }

            Column {
                BodyBoldLabel(
                    text = title.displayText,
                    modifier = Modifier.withAltText(title.altText)
                )

                subtitle?.let {
                    BodyRegularLabel(
                        text = subtitle.displayText,
                        modifier = Modifier.withAltText(subtitle.altText)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun InfoStatusItemPreview() {
    GovUkTheme {
        InfoStatusItem(
            title = AccessibleString("Title"),
            icon = R.drawable.ic_circle_p
        )
    }
}

@PreviewLightDark
@Composable
private fun InfoStatusItemWithSubtitlePreview() {
    GovUkTheme {
        InfoStatusItem(
            title = AccessibleString("Title"),
            subtitle = AccessibleString("Subtitle"),
            icon = R.drawable.ic_circle_p
        )
    }
}

@PreviewLightDark
@Composable
private fun InfoStatusItemWithNoIconPreview() {
    GovUkTheme {
        InfoStatusItem(
            title = AccessibleString("Title"),
            subtitle = AccessibleString("Subtitle")
        )
    }
}
