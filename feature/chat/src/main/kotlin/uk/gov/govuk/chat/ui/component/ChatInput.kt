package uk.gov.govuk.chat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uk.gov.govuk.chat.ChatUiState
import uk.gov.govuk.chat.R
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import kotlin.math.abs

@Composable
internal fun ChatInput(
    uiState: ChatUiState.Default,
    hasConversation: Boolean,
    onNavigationActionItemClicked: (String, String) -> Unit,
    onFunctionActionItemClicked: (String, String, String) -> Unit,
    onClear: () -> Unit,
    onQuestionUpdated: (String) -> Unit,
    onSubmit: (String) -> Unit,
    chatUrls: ChatUrls,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by rememberSaveable { mutableStateOf(false) }
    var justSubmitted by remember { mutableStateOf(false) }

    LaunchedEffect(justSubmitted) {
        if (justSubmitted) {
            delay(500)
            justSubmitted = false
        }
    }

    Column(
        modifier = modifier
            .semantics { isTraversalGroup = true }
    ) {
        if (isFocused) {
            CharacterCountMessage(
                charactersRemaining = uiState.charactersRemaining,
                displayCharacterWarning = uiState.displayCharacterWarning,
                displayCharacterError = uiState.displayCharacterError
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        if (focusedWithInput(isFocused, uiState)) 1f else 0.88f
                    )
                    .animateContentSize(
                        animationSpec = tween(durationMillis = 100)
                    )
            ) {
                val value = if (isFocused) uiState.question else ""
                BasicTextField(
                    value = value,
                    onValueChange = onQuestionUpdated,
                    textStyle = TextStyle(
                        color = GovUkTheme.colourScheme.textAndIcons.primary,
                        fontSize = GovUkTheme.typography.bodyRegular.fontSize,
                        fontWeight = GovUkTheme.typography.bodyRegular.fontWeight,
                        fontFamily = GovUkTheme.typography.bodyRegular.fontFamily,
                        lineHeight = GovUkTheme.typography.bodyRegular.lineHeight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .focusable(true)
                        .onFocusChanged {
                            isFocused = it.isFocused
                        }
                        .then(
                            if (justSubmitted) {
                                Modifier.clearAndSetSemantics { }
                            } else {
                                Modifier.semantics { this.traversalIndex = 0f }
                            }
                        ),
                    cursorBrush = SolidColor(GovUkTheme.colourScheme.textAndIcons.primary),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .defaultMinSize(minHeight = 48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(GovUkTheme.colourScheme.surfaces.chatTextFieldBackground)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(
                                        start = 16.dp,
                                        end = GovUkTheme.spacing.medium,
                                    )
                                    .padding(vertical = 8.dp)
                                    .defaultMinSize(minHeight = 32.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (value.isEmpty()) {
                                    PlaceholderText(question = uiState.question)
                                }
                                innerTextField()
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(end = 8.dp)
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                AnimateIcon(
                                    focusedWithInput(isFocused, uiState),
                                    {
                                        SubmitIconButton(
                                            onClick = {
                                                onSubmit(uiState.question)
                                                justSubmitted = true
                                                focusRequester.requestFocus()
                                            },
                                            enabled = !uiState.displayCharacterError
                                                    && !uiState.isPiiError && !uiState.isLoading
                                        )
                                    }
                                )
                            }
                        }
                    }
                )
            }

            AnimateIcon(
                (uiState.question.isEmpty() || !focusedWithInput(isFocused, uiState)),
                {
                    ActionMenu(
                        hasConversation = hasConversation,
                        isLoading = uiState.isLoading,
                        onClear = onClear,
                        onNavigationItemClicked = { text, url ->
                            onNavigationActionItemClicked(text, url)
                        },
                        onFunctionItemClicked = { text, section, action ->
                            onFunctionActionItemClicked(text, section, action)
                        },
                        chatUrls = chatUrls,
                        modifier = Modifier.semantics { this.traversalIndex = 1f }
                    )
                }
            )
        }
    }
}

private fun focusedWithInput(
    isFocused: Boolean,
    uiState: ChatUiState.Default
) : Boolean {
    return isFocused && uiState.question.isNotEmpty()
}

@Composable
private fun PlaceholderText(
    question: String,
    modifier: Modifier = Modifier
) {
    val text = question.ifEmpty { stringResource(id = R.string.input_label) }
    val maxLines = if (question.isEmpty()) Int.MAX_VALUE else 1
    val overflow = if (question.isEmpty()) TextOverflow.Clip else TextOverflow.Ellipsis

    Text(
        text = text,
        color = GovUkTheme.colourScheme.textAndIcons.secondary,
        fontSize = GovUkTheme.typography.bodyRegular.fontSize,
        fontWeight = GovUkTheme.typography.bodyRegular.fontWeight,
        fontFamily = GovUkTheme.typography.bodyRegular.fontFamily,
        lineHeight = GovUkTheme.typography.bodyRegular.lineHeight,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}

@Composable
private fun SubmitIconButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(60.dp))
            .size(36.dp),
        enabled = enabled,
        colors = IconButtonColors(
            containerColor = GovUkTheme.colourScheme.surfaces.chatButtonBackgroundEnabled,
            contentColor = GovUkTheme.colourScheme.textAndIcons.chatButtonIconEnabled,
            disabledContainerColor = GovUkTheme.colourScheme.surfaces.chatButtonBackgroundDisabled,
            disabledContentColor = GovUkTheme.colourScheme.textAndIcons.chatButtonIconDisabled
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_arrow_upward_24),
            contentDescription = stringResource(id = R.string.button_alt),
        )
    }
}

@Composable
private fun CharacterCountMessage(
    charactersRemaining: Int,
    displayCharacterWarning: Boolean,
    displayCharacterError: Boolean,
    modifier: Modifier = Modifier
) {
    val charactersRemaining = abs(charactersRemaining)
    val paddingModifier = modifier
        .padding(horizontal = GovUkTheme.spacing.medium)
        .padding(bottom = 12.dp)

    when {
        displayCharacterWarning -> {
            val text = pluralStringResource(
                id = R.plurals.characterCountUnderOrAtLimit,
                count = charactersRemaining,
                charactersRemaining
            )
            BodyRegularLabel(
                text = text,
                modifier = paddingModifier,
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
        }
        displayCharacterError -> {
            val text = pluralStringResource(
                id = R.plurals.characterCountOverLimit,
                count = charactersRemaining,
                charactersRemaining
            )
            BodyRegularLabel(
                text = text,
                modifier = paddingModifier,
                color = GovUkTheme.colourScheme.textAndIcons.textFieldError
            )
        }
    }
}

@Composable
private fun AnimateIcon(
    visible: Boolean,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val animationSpeed = 300

    // Start at alpha = 0 and scale of 50%
    // Finish at alpha = 1 and scale of 100%

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = tween(durationMillis = animationSpeed),
            initialScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        ) + fadeIn(
            animationSpec = tween(durationMillis = animationSpeed),
            initialAlpha = 0f
        ),
        exit = scaleOut(
            animationSpec = tween(durationMillis = animationSpeed),
            targetScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        ) + fadeOut(
            animationSpec = tween(durationMillis = animationSpeed),
            targetAlpha = 0f
        ),
        modifier = modifier
    ) {
        icon()
    }
}
