package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun LicenceRevokedForMedicalReasonsCard(
    onClick: (text: String) -> Unit,
    onPageView: () -> Unit,
    modifier: Modifier = Modifier
) {
    RunOnceLaunchedEffect {
        onPageView()
    }

    LicenceStatusCard(
        title = stringResource(R.string.licence_revoked_medical_title),
        description = stringResource(R.string.licence_revoked_medical_description),
        linkText = stringResource(R.string.licence_revoked_medical_link_text),
        cardBackground = GovUkTheme.colourScheme.textAndIcons.primary,
        titleBackground = GovUkTheme.colourScheme.textAndIcons.primary,
        titleTextColor = GovUkTheme.colourScheme.surfaces.cardDefault,
        titleDrawDivider = false,
        onLinkClick = onClick,
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
internal fun LicenceRevokedForMedicalReasonsCardPreview() {
    GovUkTheme {
        LicenceRevokedForMedicalReasonsCard(onClick = {}, onPageView = {})
    }
}
