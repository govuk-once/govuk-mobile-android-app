package uk.gov.govuk.login.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.BuildConfig
import uk.gov.govuk.R
import uk.gov.govuk.data.auth.ErrorEvent
import uk.gov.govuk.design.ui.component.CaptionRegularLabel
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.LoadingScreen
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1RegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.LoginEvent
import uk.gov.govuk.login.LoginViewModel

@Composable
internal fun LoginRoute(
    onLoginCompleted: (LoginEvent) -> Unit,
    onError: (ErrorEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val isLoading by viewModel.isLoading.collectAsState()

    val authLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onAuthResponse(result.data)
        }
    }

    if (isLoading == true) {
        LoadingScreen(
            modifier = modifier,
            accessibilityText = stringResource(R.string.login_loading_accessibility_text)
        )
    } else {
        LoginScreen(
            onContinueClick = {
                try {
                    authLauncher.launch(viewModel.authIntent)
                } catch (_: ActivityNotFoundException) {
                    onError(ErrorEvent.UnableToSignInError)
                }
            },
            modifier = modifier
        )
    }

    val activity = LocalActivity.current as FragmentActivity

    LaunchedEffect(Unit) {
        viewModel.init(activity)
    }

    LaunchedEffect(Unit) {
        viewModel.loginCompleted.collect { loginEvent ->
            onLoginCompleted(loginEvent)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect {
            onError(it)
        }
    }
}

@Composable
private fun LoginScreen(
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CentreAlignedScreen(
        modifier = modifier,
        screenContent = {
            val shouldShowLogo =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

            if (shouldShowLogo) {
                Image(
                    painter = painterResource(id = R.drawable.ic_welcome),
                    contentDescription = null,
                    modifier = Modifier.height(209.dp)
                )
                ExtraLargeVerticalSpacer()
            }

            LargeTitleBoldLabel(
                text = stringResource(id = R.string.login_title),
                modifier = Modifier.semantics { heading() },
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            Title1RegularLabel(
                text = stringResource(id = R.string.login_description),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )
        },
        bottomContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CaptionRegularLabel(
                    "${stringResource(id = R.string.version)} ${BuildConfig.VERSION_NAME}",
                    textAlign = TextAlign.Center
                )
            }
            MediumVerticalSpacer()
        },
        footerContent = {
            FixedPrimaryButton(
                text = stringResource(R.string.login_continue_button),
                onClick = { onContinueClick() }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    GovUkTheme {
        LoginScreen(
            onContinueClick = { }
        )
    }
}