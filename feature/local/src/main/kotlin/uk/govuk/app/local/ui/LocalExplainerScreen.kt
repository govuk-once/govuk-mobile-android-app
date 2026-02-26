package uk.govuk.app.local.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalExplainerViewModel
import uk.govuk.app.local.R

@Composable
internal fun LocalExplainerRoute(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LocalExplainerViewModel = hiltViewModel()

    LocalExplainerScreen(
        onBack = onBack,
        onPageView = { viewModel.onPageView() },
        onContinueClick = { text ->
            viewModel.onButtonClick(text)
            onContinue()
        },
        modifier = modifier
    )
}

@Composable
private fun LocalExplainerScreen(
    onBack: () -> Unit,
    onPageView: () -> Unit,
    onContinueClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        topBar = {
            LocalFullScreenHeader(
                onBack = onBack,
                modifier = Modifier.padding(bottom = GovUkTheme.spacing.large)
            )
        },
        bottomBar = {
            BottomNavBar(
                onContinueClick = onContinueClick,
                modifier = Modifier
                    .semantics {
                        isTraversalGroup = true
                        traversalIndex = 1f
                    }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SmallVerticalSpacer()
            Image(
                painter = painterResource(id = R.drawable.local_icon),
                contentDescription = null
            )
            MediumVerticalSpacer()
            LargeTitleBoldLabel(
                text = stringResource(R.string.local_title),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() }
            )
            SmallVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(R.string.local_explainer),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    onContinueClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
        val buttonText = stringResource(R.string.local_continue_button)
        FixedPrimaryButton(
            text = buttonText,
            onClick = { onContinueClick(buttonText) },
        )
    }
}

@Preview
@Composable
private fun LocalScreenPreview() {
    GovUkTheme {
        LocalExplainerScreen(
            onBack = {},
            onPageView = {},
            onContinueClick = {}
        )
    }
}
