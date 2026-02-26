package uk.gov.govuk.chat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.ui.model.ChatEntry
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer

@Composable
internal fun ChatEntry(
    chatEntry: ChatEntry,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    animationDuration: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        MediumVerticalSpacer()
        Question(question = chatEntry.question)
        MediumVerticalSpacer()
        AnimatedChatEntry(
            chatEntry = chatEntry,
            onMarkdownLinkClicked = onMarkdownLinkClicked,
            animationDuration = animationDuration,
            onSourcesExpanded = onSourcesExpanded
        )
    }
}

@Composable
private fun AnimatedChatEntry(
    chatEntry: ChatEntry,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    animationDuration: Int,
    modifier: Modifier = Modifier
) {
    var showLoading by rememberSaveable(chatEntry.id) { mutableStateOf(false) }
    var showAnswer by rememberSaveable(chatEntry.id) { mutableStateOf(false) }
    var hasAnnouncedLoading by rememberSaveable(chatEntry.id) { mutableStateOf(false) }
    var hasAnnouncedAnswer by rememberSaveable(chatEntry.id) { mutableStateOf(false) }

    val loadingText = stringResource(R.string.loading_text)
    val answerReceivedText = stringResource(R.string.answer_received)

    LaunchedEffect(chatEntry.answer) {
        if (chatEntry.answer.isNotBlank()) {
            if (showLoading && chatEntry.shouldAnimate) {
                showLoading = false
                delay(animationDuration.toLong())
            }
            showAnswer = true
        } else {
            showAnswer = false
            delay(animationDuration.toLong())
            showLoading = true
        }
    }

    val shouldAnnounceLoading = showLoading && !hasAnnouncedLoading && chatEntry.shouldAnimate
    val shouldAnnounceAnswer = showAnswer && !hasAnnouncedAnswer && chatEntry.shouldAnimate

    LaunchedEffect(shouldAnnounceLoading) {
        if (shouldAnnounceLoading) {
            delay(500)
            hasAnnouncedLoading = true
        }
    }

    LaunchedEffect(shouldAnnounceAnswer) {
        if (shouldAnnounceAnswer) {
            delay(500)
            hasAnnouncedAnswer = true
        }
    }

    val loadingModifier = if (shouldAnnounceLoading) {
        Modifier.semantics {
            liveRegion = LiveRegionMode.Polite
            contentDescription = loadingText
        }
    } else Modifier

    val answerModifier = if (shouldAnnounceAnswer) {
        Modifier.semantics {
            liveRegion = LiveRegionMode.Polite
            contentDescription = answerReceivedText
        }
    } else Modifier

    Column(modifier = modifier) {
        if (chatEntry.shouldAnimate) {
            AnimatedVisibility(
                visible = showLoading,
                enter = fadeIn(animationSpec = tween(animationDuration)),
                exit = fadeOut(animationSpec = tween(animationDuration))
            ) {
                Loading(modifier = loadingModifier)
            }

            AnimatedVisibility(
                visible = showAnswer,
                enter = fadeIn(animationSpec = tween(animationDuration)),
                exit = fadeOut(animationSpec = tween(animationDuration))
            ) {
                Answer(
                    answer = chatEntry.answer,
                    sources = chatEntry.sources,
                    onMarkdownLinkClicked = onMarkdownLinkClicked,
                    onSourcesExpanded = onSourcesExpanded,
                    modifier = answerModifier
                )
            }
        } else {
            if (showLoading) Loading()
            if (showAnswer) Answer(
                answer = chatEntry.answer,
                sources = chatEntry.sources,
                onMarkdownLinkClicked = onMarkdownLinkClicked,
                onSourcesExpanded = onSourcesExpanded
            )
        }
    }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.generating_answer)
        )

        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            isPlaying = true,
            speed = 1.5f
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(32.dp)
        )

        SmallHorizontalSpacer()

        BodyRegularLabel(
            text = stringResource(R.string.loading_text),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
