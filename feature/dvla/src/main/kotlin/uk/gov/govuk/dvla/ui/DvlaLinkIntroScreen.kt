package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.ui.component.BookendToWebScreen
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun DvlaLinkIntroScreen(
    onClose: () -> Unit,
    onContinue: (String) -> Unit,
    onPageView: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    val continueButtonText = stringResource(R.string.link_dvla_intro_button)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreenLinkAccount)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        BookendToWebScreen(
            title = stringResource(R.string.link_dvla_intro_title),
            description = stringResource(R.string.link_dvla_intro_description),
            actionMessage = stringResource(R.string.link_dvla_intro_action_message),
            buttonText = continueButtonText,
            onClose = onClose,
            onContinue = { onContinue(continueButtonText) },
        )
    }
}
