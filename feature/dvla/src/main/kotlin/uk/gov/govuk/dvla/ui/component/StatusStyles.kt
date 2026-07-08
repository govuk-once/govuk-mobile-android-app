package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.CalloutRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.extension.withAltText
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun StatusButton(
    text: AccessibleString,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    caption: AccessibleString? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = GovUkTheme.spacing.medium)
            .padding(bottom = GovUkTheme.spacing.small)
    ) {
        if (isPrimary) {
            PrimaryButton(
                text = text.displayText,
                onClick = onClick,
                modifier = Modifier.withAltText(text.altText)
            )
        } else {
            SecondaryButton(
                text = text.displayText,
                onClick = onClick,
                modifier = Modifier.withAltText(text.altText)
            )
        }

        caption?.let {
            Caption(
                text = caption
            )
        }
    }
}

@Composable
internal fun Caption(
    text: AccessibleString,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        MediumVerticalSpacer()

        CalloutRegularLabel(
            text = text.displayText,
            modifier = Modifier.withAltText(text.altText),
            color = GovUkTheme.colourScheme.textAndIcons.secondary
        )
    }
}
