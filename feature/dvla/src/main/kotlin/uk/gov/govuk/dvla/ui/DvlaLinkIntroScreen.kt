package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.ui.component.BookendToWebScreen
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun DvlaLinkIntroScreen(
    onClose: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreenLinkAccount)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        BookendToWebScreen(
            title = stringResource(R.string.link_dvla_intro_title),
            description = stringResource(R.string.link_dvla_intro_description),
            actionMessage = stringResource(R.string.link_dvla_intro_action_message),
            buttonText = stringResource(R.string.link_dvla_intro_button),
            onClose = onClose,
            onContinue = onContinue,
            modifier = modifier
        )
    }
}
