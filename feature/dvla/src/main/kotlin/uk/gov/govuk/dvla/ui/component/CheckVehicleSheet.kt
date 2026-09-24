package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.CheckVehicleError
import uk.gov.govuk.dvla.CheckVehicleSheetState
import uk.gov.govuk.dvla.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CheckVehicleSheet(
    state: CheckVehicleSheetState,
    onRegistrationChange: (String) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val focusRequester = remember {
        FocusRequester()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val isLoading = state is CheckVehicleSheetState.Loading
    val sheetTitle = stringResource(R.string.check_vehicle_sheet_prompt)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    ModalBottomSheet(
        onDismissRequest = {
            if (!isLoading) {
                keyboardController?.hide()
                onDismiss()
            }
        },
        modifier = modifier.semantics {
            paneTitle = sheetTitle
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(
            topStart = 33.dp,
            topEnd = 33.dp
        ),
        containerColor = GovUkTheme.colourScheme.surfaces.cardDefault,
        contentWindowInsets = {
            WindowInsets.ime
        },
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = GovUkTheme.spacing.medium,
                    vertical = GovUkTheme.spacing.medium
                )
        ) {
            CheckVehicleSheetLabel(state = state)

            RegistrationInput(
                registration = state.registration,
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
                SecondaryButton(
                    text = stringResource(R.string.check_vehicle_cancel),
                    onClick = {
                        keyboardController?.hide()
                        onDismiss()
                    },
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f)
                )

                PrimaryButton(
                    text = stringResource(R.string.check_vehicle_submit),
                    onClick = onSubmit,
                    enabled = state.registration.isNotBlank() && !isLoading,
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
        is CheckVehicleSheetState.Loading -> {
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

    BodyRegularLabel(
        text = text,
        modifier = modifier
            .then(
                if (isError) {
                    Modifier
                        .background(
                            color = GovUkTheme.colourScheme.surfaces.alert,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(
                            horizontal = GovUkTheme.spacing.small,
                            vertical = 2.dp
                        )
                } else {
                    Modifier
                }
            )
            .semantics {
                if (isError) {
                    liveRegion = LiveRegionMode.Assertive
                    contentDescription = text
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
                if (registration.isNotBlank()) {
                    onSubmit()
                }
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
                        .padding(start = 36.dp),
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
                                .size(18.dp)
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

@Preview(showBackground = true)
@Composable
private fun CheckVehicleSheetPreview() {
    GovUkTheme {
        CheckVehicleSheet(
            state = CheckVehicleSheetState.Input("AB12CDE"),
            onRegistrationChange = {},
            onClear = {},
            onSubmit = {},
            onDismiss = {}
        )
    }
}