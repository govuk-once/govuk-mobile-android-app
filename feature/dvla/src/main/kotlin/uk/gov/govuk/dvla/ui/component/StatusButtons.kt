package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.ui.component.CalloutRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun RenewLicenceButton(
    onRenewClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ButtonWithCaption(
        text = stringResource(R.string.renew_licence_button),
        caption = stringResource(R.string.renew_licence_caption),
        onClick = onRenewClick,
        modifier = modifier
    )
}

@Composable
internal fun ButtonWithCaption(
    text: String,
    caption: String,
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
            text = text,
            onClick = {
                onClick(text)
            }
        )

        MediumVerticalSpacer()

        CalloutRegularLabel(
            text = caption,
            color = GovUkTheme.colourScheme.textAndIcons.secondary
        )
    }
}
