package uk.gov.govuk.chat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.GovUkOutlinedCard
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.extension.talkBackText
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun IntroMessages(
    hasConversation: Boolean,
    question: String,
    isImeVisible: Boolean,
    isLoading: Boolean,
    onExampleQuestionClicked: (String) -> Unit,
    chatExampleQuestions: List<String>?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (!hasConversation) {
            var messageVisible by remember { mutableStateOf(false) }

            val animationDelay = 1000L
            val animationDuration = 200

            LaunchedEffect(key1 = true) {
                delay(animationDelay)
                messageVisible = true
            }

            AnimatedVisibility(
                visible = messageVisible,
                enter =
                    fadeIn(
                        animationSpec = tween(durationMillis = animationDuration),
                        initialAlpha = 0f
                    ) +
                    slideInVertically(
                        animationSpec = tween(durationMillis = animationDuration),
                        initialOffsetY = { 16 }
                    )
            ) {
                Column {
                    Message()
                    MediumVerticalSpacer()
                    ExampleQuestions(
                        hasConversation,
                        question,
                        isImeVisible,
                        isLoading,
                        onExampleQuestionClicked,
                        chatExampleQuestions
                    )
                }
            }
        } else {
            Column {
                Message()
                MediumVerticalSpacer()
                ExampleQuestions(
                    hasConversation,
                    question,
                    isImeVisible,
                    isLoading,
                    onExampleQuestionClicked,
                    chatExampleQuestions
                )
            }
        }
    }
}

@Composable
private fun Message(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.chatBotMessageBackground,
            contentColor = GovUkTheme.colourScheme.textAndIcons.chatBotMessageText
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        val welcomeMessageEmoji = stringResource(R.string.bot_message_emoji)
        val welcomeMessage = stringResource(id = R.string.bot_message, welcomeMessageEmoji)

        Text(
            text = welcomeMessage,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            style = GovUkTheme.typography.bodyRegular,
            modifier = Modifier
                .padding(GovUkTheme.spacing.medium)
                .talkBackText(welcomeMessage.replace(welcomeMessageEmoji, ""))
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExampleQuestions(
    hasConversation: Boolean,
    question: String,
    isImeVisible: Boolean,
    isLoading: Boolean,
    onClick: (String) -> Unit,
    chatExampleQuestions: List<String>?,
    modifier: Modifier = Modifier
) {
    if (!chatExampleQuestions.isNullOrEmpty() && !hasConversation) {
        val isVisible = question.isEmpty() && !isImeVisible && !isLoading

        val prompt = stringResource(R.string.example_question_prompt)

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(modifier = modifier) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clearAndSetSemantics {},
                    horizontalArrangement = Arrangement.End
                ) {
                    BodyRegularLabel(
                        text = prompt,
                        color = GovUkTheme.colourScheme.textAndIcons.secondary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(end = GovUkTheme.spacing.medium)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(IntrinsicSize.Max)
                        .padding(start = 60.dp)
                        .semantics { isTraversalGroup = true },
                    horizontalAlignment = Alignment.End

                ) {
                    chatExampleQuestions.forEachIndexed { index, exampleQuestion ->
                        SmallVerticalSpacer()
                        ExampleQuestion(
                            exampleQuestion,
                            prompt,
                            onClick,
                            modifier = Modifier
                                .testTag("exampleQuestion_$index")
                                .semantics {
                                    isTraversalGroup = true
                                    traversalIndex = index.toFloat()
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExampleQuestion(
    text: String,
    prompt: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    GovUkOutlinedCard(
        onClick = { onClick(text) },
        backgroundColour = GovUkTheme.colourScheme.surfaces.cardDefault,
        borderColour = GovUkTheme.colourScheme.strokes.chatExampleQuestionCardBorder,
        shape = RoundedCornerShape(18.dp),
        padding = GovUkTheme.spacing.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        BodyRegularLabel(
            text = text,
            color = GovUkTheme.colourScheme.textAndIcons.chatExampleQuestionText,
            modifier = Modifier.talkBackText("$prompt $text")
        )
    }
}
