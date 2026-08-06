package uk.gov.govuk.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.error.AppUnavailableScreen
import uk.gov.govuk.govkit.browser.rememberGovUkLauncher

@Composable
internal fun AppUnavailableRoute(
    modifier: Modifier = Modifier
) {
    AppUnavailableScreen(onGoToGovUkClick = rememberGovUkLauncher(), modifier = modifier)
}
