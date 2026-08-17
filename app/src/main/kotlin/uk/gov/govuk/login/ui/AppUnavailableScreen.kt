package uk.gov.govuk.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.error.AppUnavailableScreen
import uk.gov.govuk.govkit.browser.Urls
import uk.gov.govuk.govkit.browser.rememberUrlLauncher

@Composable
internal fun AppUnavailableRoute(
    modifier: Modifier = Modifier
) {
    AppUnavailableScreen(onGoToGovUkClick = rememberUrlLauncher(Urls.GOV_UK_HOME), modifier = modifier)
}
