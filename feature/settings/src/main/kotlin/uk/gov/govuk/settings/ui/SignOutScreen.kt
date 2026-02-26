package uk.gov.govuk.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.settings.NavigationEvent
import uk.gov.govuk.settings.R
import uk.gov.govuk.settings.SignOutViewModel
import uk.gov.govuk.settings.navigation.navigateToErrorScreen

@Composable
internal fun SignOutRoute(
    navController: NavController,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SignOutViewModel = hiltViewModel()

    SignOutScreen(
        onPageView = { viewModel.onPageView() },
        onBack = onBack,
        onSignOut = { text ->
            viewModel.onSignOut(text)
        },
        modifier = modifier
    )

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.Success -> onSignOut()
                is NavigationEvent.Error -> navController.navigateToErrorScreen()
            }
        }
    }
}

@Composable
private fun SignOutScreen(
    onPageView: () -> Unit,
    onBack: () -> Unit,
    onSignOut: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(modifier.fillMaxSize()) {
        FullScreenHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )
        Column(Modifier
            .verticalScroll(rememberScrollState())
            .weight(1f)
            .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            LargeTitleBoldLabel(
                text = stringResource(R.string.sign_out_title),
                modifier = Modifier.semantics { heading() }
            )

            MediumVerticalSpacer()

            BodyRegularLabel(stringResource(R.string.sign_out_body))

            LargeVerticalSpacer()
        }

        val primaryButtonText = stringResource(R.string.sign_out_button)
        FixedDoubleButtonGroup(
            primaryText = primaryButtonText,
            onPrimary = { onSignOut(primaryButtonText) },
            secondaryText = stringResource(R.string.sign_out_cancel_button),
            onSecondary = onBack,
            primaryDestructive = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignOutPreview() {
    GovUkTheme {
        SignOutScreen(
            onPageView = { },
            onBack = { },
            onSignOut = { _ -> }
        )
    }
}
