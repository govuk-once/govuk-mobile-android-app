package uk.gov.govuk.chat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.ChatUiState
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.extension.talkBackText
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun IntroMessages(
    uiState: ChatUiState.Default,
    animated: Boolean,
    onExampleQuestionClicked: (String) -> Unit,
    chatExampleQuestions: List<String>?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (animated) {
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
                        uiState,
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
                    uiState,
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

@Composable
private fun ExampleQuestions(
    uiState: ChatUiState.Default,
    onClick: (String) -> Unit,
    chatExampleQuestions: List<String>?,
    modifier: Modifier = Modifier
) {
    // =======================================
    // TODO list:
    //   ✅ Scenario 1: User first enters Chat screen with no conversation saved, example questions are displayed beneath the welcome message
    //   ✅ Scenario 2: If a user begins to type in the Chat screen input box, the example questions are hidden after user inputs the first character
    //   ✅ Scenario 3: If a user has started typing into the Chat screen input box, the example questions are shown again after user deletes all input character
    //   ✅ Scenario 4: If a user clicks on any of the example questions then the question is sent to Chat to answer and example questions are hidden
    //   ✅ Scenario 5: User enters Chat screen with a conversation saved, example questions are not displayed
    //   ❌ Scenario 6: Logical focus order
    //   ❌ Scenario 7: Landscape
    //   ✅ Scenario 8: Dark mode
    // =======================================

    if (!chatExampleQuestions.isNullOrEmpty()) {
        if (uiState.question.isEmpty() and uiState.chatEntries.isEmpty()) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.example_question_prompt),
                    color = GovUkTheme.colourScheme.textAndIcons.secondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(end = GovUkTheme.spacing.medium)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .padding(start = 60.dp)
                ) {
                    chatExampleQuestions.forEach { question ->
                        SmallVerticalSpacer()
                        ExampleQuestion(question, onClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExampleQuestion(
    text: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = { onClick(text) },
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.cardDefault,
            contentColor = GovUkTheme.colourScheme.textAndIcons.chatExampleQuestionText
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, GovUkTheme.colourScheme.strokes.chatExampleQuestionCardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        BodyRegularLabel(
            text = text,
            color = GovUkTheme.colourScheme.textAndIcons.chatExampleQuestionText,
            modifier = Modifier
                .padding(GovUkTheme.spacing.medium)
                .talkBackText(text)
        )
    }
}
