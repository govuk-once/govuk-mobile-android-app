package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.LicenceStatus
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel

@Composable
internal fun LicenceStatusItem(
    status: LicenceStatus,
    licenceStatus: StatusRowUiModel,
    onRenewClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (status) {
        LicenceStatus.EXPIRED -> ExpiredLicenceStatusItem(
            status = licenceStatus,
            onRenewClick = onRenewClick,
            modifier = modifier
        )
        else -> {
            val licenceExpiration = licenceStatus.description

            StatusListItem(
                title = licenceStatus.title?.let {
                    AccessibleString(displayText = it)
                },
                description = AccessibleString(
                    displayText = licenceExpiration,
                    altText = stringResource(R.string.licence_expiration_alt_text, licenceExpiration)
                ),
                iconStyle = licenceStatus.iconStyle,
                isLast = true,
                modifier = modifier
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LicenceStatusItemValidPreview() {
    GovUkTheme {
        LicenceStatusItem(
            status = LicenceStatus.VALID,
            licenceStatus = StatusRowUiModel(
                description = "Valid until 1 February 2027",
                iconStyle = StatusListItemIconStyle.Success
            ),
            onRenewClick = {_ -> }
        )
    }
}

@PreviewLightDark
@Composable
private fun LicenceStatusItemExpiredPreview() {
    GovUkTheme {
        LicenceStatusItem(
            status = LicenceStatus.EXPIRED,
            licenceStatus = StatusRowUiModel(
                description = "Expired 24 April 2026",
                iconStyle = StatusListItemIconStyle.Warning
            ),
            onRenewClick = {_ -> }
        )
    }
}
