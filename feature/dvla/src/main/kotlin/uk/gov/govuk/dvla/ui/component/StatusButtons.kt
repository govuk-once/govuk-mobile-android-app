package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import uk.gov.govuk.design.ui.component.CalloutRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.SecondaryButton
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
            .padding(bottom = GovUkTheme.spacing.medium)
    ) {
        if (isPrimary) {
            PrimaryButton(
                text = text.displayText,
                onClick = onClick,
                modifier = Modifier.semantics {
                    text.altText?.let { altText ->
                        contentDescription = altText
                    }
                }
            )
        } else {
            SecondaryButton(
                text = text.displayText,
                onClick = onClick,
                modifier = Modifier.semantics {
                    text.altText?.let { altText ->
                        contentDescription = altText
                    }
                }
            )
        }

        caption?.let {
            MediumVerticalSpacer()

            CalloutRegularLabel(
                text = caption.displayText,
                modifier = Modifier.semantics {
                    caption.altText?.let { altText ->
                        contentDescription = altText
                    }
                },
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }
    }
}
