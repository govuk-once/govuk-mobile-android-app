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
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.extension.talkBackText
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun IntroMessages(
    animated: Boolean,
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
                    ExampleQuestions()
                }
            }
        } else {
            Column {
                Message()
                MediumVerticalSpacer()
                ExampleQuestions()
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
    modifier: Modifier = Modifier
) {
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

//    TODO: get the questions from the remote config...
    SmallVerticalSpacer()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(start = 60.dp)
        ) {
            ExampleQuestion("How do I renew my driving licence?")
            SmallVerticalSpacer()
            ExampleQuestion("What support can I get as a carer for my partner?")
            SmallVerticalSpacer()
            ExampleQuestion("I want to set up a business. What are the steps?")
        }
    }
}

@Composable
private fun ExampleQuestion(
    text: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = { /* TODO: click to send text as question to API */ },
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
