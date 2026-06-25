package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.CalloutRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.ProgressBarListItem
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.ProgressBarListItemUiModel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun ExpiringLicenceStatusItem(
    uiModel: ProgressBarListItemUiModel,
    onRenewClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium)
            .padding(vertical = GovUkTheme.spacing.large)
    ) {
        SmallVerticalSpacer()
        
        ProgressBarListItem(
            topText = uiModel.topText,
            percentage = uiModel.percentage,
            bottomText = uiModel.bottomText
        )

        MediumVerticalSpacer()

        val text = stringResource(R.string.renew_licence_button)
        PrimaryButton(
            text = text,
            onClick = {
                onRenewClick?.invoke(text)
            }
        )

        MediumVerticalSpacer()

        CalloutRegularLabel(
            text = stringResource(R.string.renew_licence_caption),
            color = GovUkTheme.colourScheme.textAndIcons.primary
        )
    }
}

@PreviewLightDark
@Composable
private fun ExpiringLicenceStatusItemPreview() {
    GovUkTheme {
        ExpiringLicenceStatusItem(
            ProgressBarListItemUiModel(
                AccessibleString("12 December"), 50, AccessibleString("5 days left")
            ),
            onRenewClick = { _ -> }
        )
    }
}
