package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.GovUkButtonColours
import uk.gov.govuk.design.ui.component.GovUkButtonState
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.CheckVehicleError
import uk.gov.govuk.dvla.CheckVehicleSheetState
import uk.gov.govuk.dvla.R

private val SheetShape = RoundedCornerShape(topStart = 33.dp, topEnd = 33.dp)

@Composable
private fun submitButtonColours() = GovUkButtonColours(
    defaultContainerColour = GovUkTheme.colourScheme.surfaces.sheetButtonAction,
    defaultContentColour = GovUkTheme.colourScheme.textAndIcons.primaryInverse,
    focussedContainerColour = GovUkTheme.colourScheme.surfaces.focused,
    focussedContentColour = GovUkTheme.colourScheme.textAndIcons.focused,
    pressedContainerColour = GovUkTheme.colourScheme.surfaces.sheetButtonAction,
    pressedContentColour = GovUkTheme.colourScheme.textAndIcons.primaryInverse,
    disabledContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryDisabled,
    disabledContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryDisabled
)

@Composable
private fun cancelButtonColours() = GovUkButtonColours(
    defaultContainerColour = GovUkTheme.colourScheme.surfaces.sheetButtonCancel,
    defaultContentColour = GovUkTheme.colourScheme.textAndIcons.primary,
    focussedContainerColour = GovUkTheme.colourScheme.surfaces.focused,
    focussedContentColour = GovUkTheme.colourScheme.textAndIcons.focused,
    pressedContainerColour = GovUkTheme.colourScheme.surfaces.sheetButtonCancel,
    pressedContentColour = GovUkTheme.colourScheme.textAndIcons.primary,
    disabledContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryDisabled,
    disabledContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryDisabled
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CheckVehicleSheet(
    state: CheckVehicleSheetState,
    onRegistrationChange: (String) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val focusRequester = remember {
        FocusRequester()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sheetTitle = stringResource(R.string.check_vehicle_sheet_prompt)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = {
            keyboardController?.hide()
            onCancel()
        },
        modifier = modifier.semantics {
                paneTitle = sheetTitle
            },
        sheetState = sheetState,
        shape = SheetShape,
        containerColor = GovUkTheme.colourScheme.surfaces.bottomSheet,
        dragHandle = null
    ) {
        CheckVehicleSheetContent(
            state = state,
            onRegistrationChange = onRegistrationChange,
            onClear = onClear,
            onSubmit = onSubmit,
            onCancel = {
                keyboardController?.hide()
                onCancel()
            },
            focusRequester = focusRequester
        )
    }
}

@Composable
private fun CheckVehicleSheetContent(
    state: CheckVehicleSheetState,
    onRegistrationChange: (String) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val isLoading = state is CheckVehicleSheetState.Loading

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = GovUkTheme.colourScheme.strokes.bottomSheet,
                shape = SheetShape
            )
            .padding(
                horizontal = GovUkTheme.spacing.medium,
                vertical = GovUkTheme.spacing.medium
            )
    ) {
        CheckVehicleSheetLabel(state = state)

        RegistrationInput(
            registration = state.regNumber,
            enabled = !isLoading,
            onRegistrationChange = onRegistrationChange,
            onClear = onClear,
            onSubmit = onSubmit,
            focusRequester = focusRequester,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(
                GovUkTheme.spacing.small
            )
        ) {
            val submitButtonState = if (state is CheckVehicleSheetState.Loading) {
                GovUkButtonState.Loading(stringResource(R.string.check_vehicle_loading_alt_text))
            } else {
                GovUkButtonState.Enabled
            }

            PrimaryButton(
                text = stringResource(R.string.check_vehicle_cancel),
                onClick = onCancel,
                colours = cancelButtonColours(),
                modifier = Modifier.weight(1f)
            )

            if (state.regNumber.isNotBlank()) {
                PrimaryButton(
                    text = stringResource(R.string.check_vehicle_submit),
                    onClick = onSubmit,
                    state = submitButtonState,
                    colours = submitButtonColours(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CheckVehicleSheetLabel(
    state: CheckVehicleSheetState,
    modifier: Modifier = Modifier
) {
    val isError = state is CheckVehicleSheetState.Error

    val text = when (state) {
        is CheckVehicleSheetState.Input,
        is CheckVehicleSheetState.Loading,
        is CheckVehicleSheetState.Success -> {
            stringResource(R.string.check_vehicle_sheet_prompt)
        }

        is CheckVehicleSheetState.Error -> {
            when (state.error) {
                CheckVehicleError.SEARCH_UNAVAILABLE ->
                    stringResource(R.string.check_vehicle_search_unavailable)

                CheckVehicleError.NUMBER_PLATE_NOT_FOUND ->
                    stringResource(R.string.check_vehicle_number_plate_not_found)
            }
        }
    }

    val (textColour, errorModifier) = if (isError) {
        GovUkTheme.colourScheme.textAndIcons.bottomSheetErrorLabel to Modifier
            .background(
                color = GovUkTheme.colourScheme.surfaces.bottomSheetErrorLabel,
                shape = RoundedCornerShape(6.dp)
            )
            .semantics { liveRegion = LiveRegionMode.Assertive }
    } else {
        GovUkTheme.colourScheme.textAndIcons.primary to Modifier
    }

    BodyRegularLabel(
        text = text,
        color = textColour,
        modifier = modifier
            .then(errorModifier)
            .padding(horizontal = GovUkTheme.spacing.small)
            .semantics {
                if (isError) {
                    liveRegion = LiveRegionMode.Assertive
                }
            }
    )
}

@Composable
private fun RegistrationInput(
    registration: String,
    enabled: Boolean,
    onRegistrationChange: (String) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val clearDescription = stringResource(
        R.string.check_vehicle_clear_registration
    )

    BasicTextField(
        value = registration,
        onValueChange = onRegistrationChange,
        modifier = modifier
            .focusRequester(focusRequester),
        enabled = enabled,
        singleLine = true,
        textStyle = GovUkTheme.typography.registrationPlateLarge.copy(
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(
            GovUkTheme.colourScheme.textAndIcons.primary
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onSubmit()
            }
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 36.dp, top = 12.dp, bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    innerTextField()
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .semantics {
                            contentDescription = clearDescription
                            role = Role.Button
                        }
                        .clickable(
                            enabled = enabled && registration.isNotEmpty(),
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null,
                            onClick = onClear
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (registration.isNotEmpty()) {
                        Icon(
                            painter = painterResource(
                                uk.gov.govuk.design.R.drawable.ic_cancel_round
                            ),
                            contentDescription = null,
                            tint = GovUkTheme.colourScheme.textAndIcons.primary,
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = GovUkTheme.colourScheme.surfaces.list,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun CheckVehicleSheetEmptyPreview() {
    SheetPreview(CheckVehicleSheetState.Input())
}

@PreviewLightDark
@Composable
private fun CheckVehicleSheetTypingPreview() {
    SheetPreview(CheckVehicleSheetState.Input("AB12"))
}

@PreviewLightDark
@Composable
private fun CheckVehicleSheetCompletePreview() {
    SheetPreview(CheckVehicleSheetState.Input("AB12CDE"))
}

@PreviewLightDark
@Composable
private fun CheckVehicleSheetLoadingPreview() {
    SheetPreview(CheckVehicleSheetState.Loading("AB12CDE"))
}

@PreviewLightDark
@Composable
private fun CheckVehicleSheetSearchUnavailablePreview() {
    SheetPreview(
        CheckVehicleSheetState.Error(
            regNumber = "AB12CDE",
            error = CheckVehicleError.SEARCH_UNAVAILABLE
        )
    )
}

@PreviewLightDark
@Composable
private fun CheckVehicleSheetNotFoundPreview() {
    SheetPreview(
        CheckVehicleSheetState.Error(
            regNumber = "AB12CDE",
            error = CheckVehicleError.NUMBER_PLATE_NOT_FOUND
        )
    )
}

@Composable
private fun SheetPreview(state: CheckVehicleSheetState) {
    GovUkTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = GovUkTheme.colourScheme.surfaces.cardDefault,
                    shape = SheetShape
                )
        ) {
            CheckVehicleSheetContent(
                state = state,
                onRegistrationChange = {},
                onClear = {},
                onSubmit = {},
                onCancel = {}
            )
        }
    }
}