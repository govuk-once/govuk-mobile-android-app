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
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel

@Composable
internal fun ExpiredLicenceStatusItem(
    status: StatusRowUiModel,
    onRenewClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        StatusListItem(
            title = status.title?.let { AccessibleString(displayText = it) },
            description = AccessibleString(
                displayText = status.description,
                altText = stringResource(R.string.licence_expiration_alt_text, status.description)
            ),
            iconStyle = status.iconStyle,
            drawDivider = false
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(bottom = GovUkTheme.spacing.large)
        ) {
            onRenewClick?.let {
                val text = stringResource(R.string.renew_licence_button)
                PrimaryButton(
                    text = text,
                    onClick = {
                        it(text)
                    }
                )

                MediumVerticalSpacer()
            }

            CalloutRegularLabel(
                text = stringResource(R.string.renew_licence_caption),
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ExpiredLicenceStatusItemPreview() {
    GovUkTheme {
        ExpiredLicenceStatusItem(
            status = StatusRowUiModel(
                description = "Expired 24 April 2026",
                iconStyle = StatusListItemIconStyle.Warning
            ),
            onRenewClick = { _ -> }
        )
    }
}

@PreviewLightDark
@Composable
private fun ExpiredLicenceStatusItemNoButtonPreview() {
    GovUkTheme {
        ExpiredLicenceStatusItem(
            status = StatusRowUiModel(
                description = "Expired 24 April 2026",
                iconStyle = StatusListItemIconStyle.Warning
            ),
            onRenewClick = null
        )
    }
}
