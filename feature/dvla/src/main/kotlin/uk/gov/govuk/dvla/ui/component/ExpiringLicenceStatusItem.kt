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
import uk.gov.govuk.design.ui.component.CountdownBarListItem
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.CountdownBarListItemUiModel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun ExpiringLicenceStatusItem(
    uiModel: CountdownBarListItemUiModel,
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
        
        CountdownBarListItem(
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
            CountdownBarListItemUiModel(
                AccessibleString("12 December"), 50f, AccessibleString("5 days left")
            ),
            onRenewClick = { _ -> }
        )
    }
}
