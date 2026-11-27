package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun LicenceNotAvailableCard(
    onClick: (text: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LicenceStatusCard(
        title = stringResource(R.string.licence_not_available_title),
        description = stringResource(R.string.licence_not_available_description),
        linkText = stringResource(R.string.licence_not_available_link_text),
        onLinkClick = onClick,
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
internal fun LicenceNotAvailableCardPreview() {
    GovUkTheme {
        LicenceNotAvailableCard(onClick = {})
    }
}
