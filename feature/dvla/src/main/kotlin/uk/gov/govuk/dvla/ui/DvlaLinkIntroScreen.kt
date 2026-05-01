package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.ui.component.BookendToWebScreen
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun DvlaLinkIntroScreen(
    onClose: () -> Unit,
    onContinue: (String) -> Unit,
    onPageView: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenTitle = stringResource(R.string.link_dvla_intro_title)
    val continueButtonText = stringResource(R.string.link_dvla_intro_button)
    val description = stringResource(R.string.link_dvla_intro_description)

    var hasTrackedPageView by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // protect against config change
        if (!hasTrackedPageView) {
            onPageView(screenTitle)
            hasTrackedPageView = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreenLinkAccount)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        BookendToWebScreen(
            title = screenTitle,
            description = description,
            descriptionAltText = description.replace(
                stringResource(R.string.acronym_mot),
                stringResource(R.string.acronym_mot_alt_text)
            ),
            actionMessage = stringResource(R.string.link_dvla_intro_action_message),
            buttonText = continueButtonText,
            onClose = onClose,
            onContinue = { onContinue(continueButtonText) },
        )
    }
}
