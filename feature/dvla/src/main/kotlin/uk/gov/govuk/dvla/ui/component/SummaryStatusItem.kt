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
internal fun SummaryStatusItem(
    status: StatusRowUiModel,
    onActionClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val actionButton = status.action
    val showActionBlock = status.action != null && onActionClick != null

    Column(modifier = modifier.fillMaxWidth()) {
        StatusListItem(
            title = status.title,
            description = status.description,
            iconStyle = status.iconStyle,
            drawDivider = status.action == null
        )

        if (showActionBlock) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GovUkTheme.spacing.medium)
                    .padding(bottom = GovUkTheme.spacing.large)
            ) {
                PrimaryButton(
                    text = actionButton.buttonText,
                    onClick = { onActionClick.invoke(actionButton.buttonText) }
                )

                MediumVerticalSpacer()

                CalloutRegularLabel(
                    text = actionButton.caption,
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SummaryStatusItemValidPreview() {
    GovUkTheme {
        SummaryStatusItem(
            status = StatusRowUiModel(
                description = AccessibleString("Valid until 1 February 2027"),
                iconStyle = StatusListItemIconStyle.Success
            ),
            onActionClick = { _ -> }
        )
    }
}

@PreviewLightDark
@Composable
private fun SummaryStatusItemExpiredPreview() {
    GovUkTheme {
        SummaryStatusItem(
            status = StatusRowUiModel(
                description = AccessibleString("Expired 24 April 2026"),
                iconStyle = StatusListItemIconStyle.Warning
            ),
            onActionClick = { _ -> }
        )
    }
}
