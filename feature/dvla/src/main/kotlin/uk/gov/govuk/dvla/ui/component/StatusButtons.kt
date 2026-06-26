package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import uk.gov.govuk.design.ui.component.CalloutRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun RenewLicenceButton(
    onRenewClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ButtonWithCaption(
        text = AccessibleString(displayText = stringResource(R.string.renew_licence_button)),
        caption = AccessibleString(displayText = stringResource(R.string.renew_licence_caption)),
        onClick = onRenewClick,
        modifier = modifier
    )
}

@Composable
internal fun ButtonWithCaption(
    text: AccessibleString,
    caption: AccessibleString,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium)
            .padding(bottom = GovUkTheme.spacing.large)
    ) {
        PrimaryButton(
            text = text.displayText,
            onClick = {
                onClick(text.displayText)
            },
            modifier = Modifier.semantics {
                text.altText?.let { altText ->
                    contentDescription = altText
                }
            }
        )

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
