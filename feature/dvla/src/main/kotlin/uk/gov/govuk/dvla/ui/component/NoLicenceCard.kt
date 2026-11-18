package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun NoLicenceCard(
    modifier: Modifier = Modifier
) {
    LicenceStatusCard(
        title = stringResource(R.string.licence_no_licence_title),
        description = stringResource(R.string.licence_no_licence_description),
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
internal fun NoLicenceCardPreview() {
    GovUkTheme {
        NoLicenceCard()
    }
}