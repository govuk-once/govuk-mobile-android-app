package uk.gov.govuk.login.ui

import androidx.activity.compose.LocalActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.LargeHorizontalSpacer
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.BiometricViewModel

@Composable
internal fun BiometricRoute(
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: BiometricViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val activity = LocalActivity.current as FragmentActivity

    BiometricScreen(
        onSetupBiometrics = {
            viewModel.onContinue(activity)
        },
        onSkip = {
            viewModel.onSkip()
            onCompleted()
        },
        descriptionOne = viewModel.getDescriptionOne(),
        modifier = modifier
    )

    LaunchedEffect(uiState) {
        if (uiState) {
            onCompleted()
        }
    }
}

@Composable
private fun BiometricScreen(
    onSetupBiometrics: () -> Unit,
    onSkip: () -> Unit,
    @StringRes descriptionOne: Int,
    modifier: Modifier = Modifier
) {
    CentreAlignedScreen(
        modifier = modifier,
        screenContent = {
            IconRow()

            MediumVerticalSpacer()

            LargeTitleBoldLabel(
                text = stringResource(R.string.login_biometrics_title),
                modifier = Modifier.semantics { heading() },
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(descriptionOne),
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(R.string.login_biometrics_description_2),
                textAlign = TextAlign.Center
            )
        },
        footerContent = {
            FixedDoubleButtonGroup(
                primaryText = stringResource(R.string.login_biometrics_button),
                onPrimary = { onSetupBiometrics() },
                secondaryText = stringResource(R.string.login_biometrics_skip_button),
                onSecondary = { onSkip() }
            )
        }
    )
}

@Composable
private fun IconRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_face),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.iconBiometric
        )

        LargeHorizontalSpacer()

        VerticalDivider(
            thickness = 2.dp,
            color = GovUkTheme.colourScheme.strokes.iconSeparator
        )

        LargeHorizontalSpacer()

        Icon(
            painter = painterResource(id = R.drawable.ic_fingerprint),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.iconBiometric
        )

        LargeHorizontalSpacer()

        VerticalDivider(
            thickness = 2.dp,
            color = GovUkTheme.colourScheme.strokes.iconSeparator
        )

        LargeHorizontalSpacer()

        Icon(
            painter = painterResource(id = R.drawable.ic_iris),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.iconBiometric
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BiometricPreview() {
    GovUkTheme {
        BiometricScreen(
            onSetupBiometrics = { },
            onSkip = { },
            R.string.login_biometrics_android_11_description_1
        )
    }
}
