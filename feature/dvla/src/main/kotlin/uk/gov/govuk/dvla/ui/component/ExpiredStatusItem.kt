package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.CalloutRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel

@Composable
internal fun ExpiredStatusItem(
    status: StatusRowUiModel,
    buttonText: String,
    onRenewClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
    caption: String
) {
    Column(modifier = modifier.fillMaxWidth()) {
        StatusListItem(
            title = status.title,
            description = status.description,
            iconStyle = status.iconStyle,
            drawDivider = false
        )

        onRenewClick?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GovUkTheme.spacing.medium)
                    .padding(bottom = GovUkTheme.spacing.large)
            ) {
                PrimaryButton(
                    text = buttonText,
                    onClick = {
                        it(buttonText)
                    }
                )

                MediumVerticalSpacer()

                CalloutRegularLabel(
                    text = caption,
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ExpiredLicenceStatusItemPreview() {
    GovUkTheme {
        ExpiredStatusItem(
            status = StatusRowUiModel(
                description = AccessibleString("Expired 24 April 2026"),
                iconStyle = StatusListItemIconStyle.Warning
            ),
            buttonText = "Renew licence",
            caption = "Your licence status may not update immediately when you renew it",
            onRenewClick = { _ -> }
        )
    }
}

@PreviewLightDark
@Composable
private fun ExpiredLicenceStatusItemNoButtonPreview() {
    GovUkTheme {
        ExpiredStatusItem(
            status = StatusRowUiModel(
                description = AccessibleString("Expired 24 April 2026"),
                iconStyle = StatusListItemIconStyle.Warning
            ),
            buttonText = "Renew licence",
            caption = "Your licence status may not update immediately when you renew it",
            onRenewClick = null
        )
    }
}
