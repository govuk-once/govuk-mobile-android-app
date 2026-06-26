package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.CountdownBarListItem
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.CountdownBarListItemUiModel
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ExpiringLicenceStatusItem(
    uiModel: CountdownBarListItemUiModel,
    onRenewClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = GovUkTheme.spacing.large)
    ) {
        CountdownBarListItem(
            topText = uiModel.topText,
            percentage = uiModel.percentage,
            bottomText = uiModel.bottomText
        )

        RenewLicenceButton(
            onRenewClick = { text -> onRenewClick?.invoke(text) }
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
