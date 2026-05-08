package uk.gov.govuk.design.ui.component.error

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeHorizontalSpacer
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun ErrorPage(
    headerText: String,
    subText: String,
    footer: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    additionalText: String? = null,
) {
    CentreAlignedScreen(
        modifier = modifier,
        screenContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.height(IntrinsicSize.Min)
                    .padding(all = GovUkTheme.spacing.medium)
            )

            LargeHorizontalSpacer()

            LargeTitleBoldLabel(
                text = headerText,
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            BodyRegularLabel(
                text = subText,
                textAlign = TextAlign.Center
            )

            if (additionalText != null) {
                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = additionalText,
                    textAlign = TextAlign.Center
                )
            }
        },
        footerContent = {
            footer()
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ErrorPageWithoutAdditionalTextPreview() {
    GovUkTheme {
        ErrorPage(
            headerText = "Header text",
            subText = "Sub text",
            footer = {
                FixedPrimaryButton(
                    text = "Button text",
                    onClick = { }
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPageWithAdditionalTextPreview() {
    GovUkTheme {
        ErrorPage(
            headerText = "Header text",
            subText = "Sub text",
            footer = {
                FixedPrimaryButton(
                    text = "Button text",
                    onClick = { }
                )
            },
            additionalText = "Additional text",
        )
    }
}
