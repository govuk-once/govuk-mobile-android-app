package uk.gov.govuk.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.error.AppUnavailableScreen
import uk.gov.govuk.govkit.browser.Urls

@Composable
internal fun AppUnavailableRoute(
    launchBrowser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppUnavailableScreen(
        onGoToGovUkClick = { launchBrowser(Urls.GOV_UK_HOME) },
        modifier = modifier
    )
}
