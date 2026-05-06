package uk.gov.govuk.sar.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.sar.R
import uk.gov.govuk.sar.SubjectAccessRequestViewModel

@Composable
internal fun SubjectAccessRequestRoute(
    onConfirm: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SubjectAccessRequestViewModel = hiltViewModel()

    SubjectAccessRequestScreen(
        onPageView = { viewModel.onExplainerPageView() },
        onConfirm = { text ->
            viewModel.onButtonClick(text)
            viewModel.saveUserData()
            onConfirm()
        },
        onClose = { text ->
            viewModel.onButtonClick(text)
            onClose()
        },
        modifier = modifier
    )
}

@Composable
private fun SubjectAccessRequestScreen(
    onPageView: () -> Unit,
    onConfirm: (String) -> Unit,
    onClose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        bottomBar = {
            BottomNavBar(
                onConfirm = onConfirm,
                onClose = onClose,
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
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SmallVerticalSpacer()
            LargeTitleBoldLabel(
                text = stringResource(R.string.sar_title),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() }
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    onConfirm: (String) -> Unit,
    onClose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(GovUkTheme.spacing.medium)
            .background(GovUkTheme.colourScheme.surfaces.background)
    ) {
        val confirmText = stringResource(R.string.confirm)
        val closeText = stringResource(R.string.close)

        PrimaryButton(
            text = confirmText,
            onClick = { onConfirm(confirmText) }
        )
        SecondaryButton(
            text = closeText,
            onClick = { onClose(closeText) }
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun LightModePreview() {
    GovUkTheme {
        SubjectAccessRequestScreen(
            onPageView = {},
            onConfirm = {},
            onClose = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DarkModePreview() {
    GovUkTheme {
        SubjectAccessRequestScreen(
            onPageView = {},
            onConfirm = {},
            onClose = {}
        )
    }
}
