package uk.gov.govuk.design.ui.component

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
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun ErrorPage(
    headerText: String,
    subText: String,
    buttonText: String,
    onBack: (String) -> Unit,
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
            FixedPrimaryButton(
                text = buttonText,
                onClick = { onBack(buttonText) }
            )
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
            buttonText = "Button text",
            onBack = {}
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
            additionalText = "Additional text",
            buttonText = "Button text",
            onBack = {}
        )
    }
}
