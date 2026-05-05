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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.sar.R
import uk.gov.govuk.sar.SubjectAccessRequestViewModel
import uk.gov.govuk.sar.data.SubjectAccessRequestFile

@Composable
internal fun SubjectAccessRequestDisplayRoute(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SubjectAccessRequestViewModel = hiltViewModel()

    SubjectAccessRequestDisplayScreen(
        onPageView = {
            viewModel.onDisplayPageView()
        },
        onClose = { text ->
            viewModel.onButtonClick(text)
            onClose()
        },
        modifier = modifier
    )
}

@Composable
private fun SubjectAccessRequestDisplayScreen(
    onPageView: () -> Unit,
    onClose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dispatcher: CoroutineDispatcher = Dispatchers.IO

    var fileContent by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        onPageView()

        scope.launch(dispatcher) {
            val file = SubjectAccessRequestFile(context)
            fileContent = file.readFile()
        }
    }

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        bottomBar = {
            BottomNavBar(
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
                text = stringResource(R.string.sar_display_title),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() }
            )
            SmallVerticalSpacer()
            BodyRegularLabel(
                text = fileContent,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    onClose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val closeText = stringResource(R.string.close)

    Column(
        modifier = modifier
            .padding(GovUkTheme.spacing.medium)
            .background(GovUkTheme.colourScheme.surfaces.background)
    ) {
        SecondaryButton(
            text = closeText,
            onClick = { onClose(closeText) },
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
        SubjectAccessRequestDisplayScreen(
            onPageView = {},
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
        SubjectAccessRequestDisplayScreen(
            onPageView = {},
            onClose = {}
        )
    }
}
