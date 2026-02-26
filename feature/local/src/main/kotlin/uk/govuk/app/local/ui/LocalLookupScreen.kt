package uk.govuk.app.local.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalLookupViewModel
import uk.govuk.app.local.LocalUiState
import uk.govuk.app.local.NavigationEvent
import uk.govuk.app.local.R
import uk.govuk.app.local.domain.PostcodeSanitizer

@Composable
internal fun LocalLookupRoute(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onLocalAuthoritySelected: () -> Unit,
    onAddresses: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: LocalLookupViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LocalLookupScreen(
        uiState = uiState,
        onBack = onBack,
        onCancel = onCancel,
        onPageView = { viewModel.onPageView() },
        onPostcodeLookup = { buttonText, postcode ->
            viewModel.onSearchPostcode(
                buttonText = buttonText,
                postcode = postcode
            )
        },
        onPostcodeChange = { viewModel.onPostcodeChange() },
        modifier = modifier
    )

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.LocalAuthoritySelected -> onLocalAuthoritySelected()
                is NavigationEvent.Addresses -> onAddresses(event.postcode)
            }
        }
    }
}

@Composable
private fun LocalLookupScreen(
    uiState: LocalUiState?,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onPageView: () -> Unit,
    onPostcodeLookup: (String, String) -> Unit,
    onPostcodeChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    var postcode by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        onPageView()
    }

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        topBar = {
            LocalFullScreenHeader(onBack = onBack, onCancel = onCancel)
        },
        bottomBar = {
            BottomNavBar(
                postcode = postcode,
                onPostcodeLookup = onPostcodeLookup,
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
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxWidth()
        ) {
            SmallVerticalSpacer()
            Title1BoldLabel(
                text = stringResource(R.string.local_whats_your_postcode),
                modifier = Modifier.semantics { heading() }
            )
            SmallVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(R.string.local_postcode_example)
            )
            SmallVerticalSpacer()
            TextField(
                value = postcode,
                onValueChange = {
                    onPostcodeChange()
                    postcode = PostcodeSanitizer.sanitize(it)
                },
                label = {
                    val contentDescPostcodeEntry =
                        stringResource(R.string.local_content_desc_postcode_entry)
                    Text(
                        text = stringResource(R.string.local_postcode_default_text),
                        Modifier.semantics {
                            contentDescription = contentDescPostcodeEntry
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(focusRequester)
                    .focusable(true),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters
                ),
                singleLine = true,
                isError = uiState is LocalUiState.Error,
                supportingText = {
                    if (uiState is LocalUiState.Error) {
                        focusRequester.requestFocus()
                        val errorMessage = stringResource(uiState.message)
                        BodyBoldLabel(
                            color = GovUkTheme.colourScheme.textAndIcons.textFieldError,
                            text = errorMessage
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    cursorColor = GovUkTheme.colourScheme.strokes.textFieldCursor,
                    errorContainerColor = GovUkTheme.colourScheme.surfaces.textFieldBackground,
                    errorCursorColor = GovUkTheme.colourScheme.strokes.textFieldCursor,
                    errorIndicatorColor = GovUkTheme.colourScheme.strokes.textFieldError,
                    errorLabelColor = GovUkTheme.colourScheme.textAndIcons.primary,
                    errorPrefixColor = GovUkTheme.colourScheme.textAndIcons.textFieldError,
                    errorPlaceholderColor = GovUkTheme.colourScheme.textAndIcons.textFieldError,
                    errorSuffixColor = GovUkTheme.colourScheme.textAndIcons.textFieldError,
                    errorSupportingTextColor = GovUkTheme.colourScheme.textAndIcons.textFieldError,
                    errorTextColor = GovUkTheme.colourScheme.textAndIcons.primary,
                    focusedContainerColor = GovUkTheme.colourScheme.surfaces.textFieldBackground,
                    focusedIndicatorColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    focusedLabelColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    focusedPlaceholderColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    focusedTextColor = GovUkTheme.colourScheme.textAndIcons.primary,
                    selectionColors = TextSelectionColors(
                        handleColor = GovUkTheme.colourScheme.surfaces.textFieldHighlighted,
                        backgroundColor = GovUkTheme.colourScheme.surfaces.textFieldHighlighted
                    ),
                    unfocusedContainerColor = GovUkTheme.colourScheme.surfaces.textFieldBackground,
                    unfocusedIndicatorColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    unfocusedLabelColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    unfocusedPlaceholderColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    unfocusedTextColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                )
            )
            LargeVerticalSpacer()
            BodyBoldLabel(
                text = stringResource(R.string.local_postcode_use_title),
                modifier = Modifier.semantics { heading() }
            )
            LargeVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.local_postcode_use_description))
        }
    }
}

@Composable
private fun BottomNavBar(
    postcode: String,
    onPostcodeLookup: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
        val buttonText = stringResource(R.string.local_confirm_button)
        FixedPrimaryButton(
            text = buttonText,
            onClick = { onPostcodeLookup(buttonText, postcode) },
            enabled = postcode.isNotBlank(),
        )
    }
}

@Preview
@Composable
private fun LocalEntryScreenPreview() {
    GovUkTheme {
        LocalLookupScreen(
            onBack = {},
            onPageView = {},
            onCancel = {},
            uiState = null,
            onPostcodeLookup = { _, _ -> },
            onPostcodeChange = {}
        )
    }
}
