package uk.gov.govuk.chat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.R

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

            val delay = 1000L
            val duration = 2000

            LaunchedEffect(key1 = true) {
                delay(delay)
                messageVisible = true
            }

            AnimatedVisibility(
                visible = messageVisible,
                enter =
                    fadeIn(
                        animationSpec = tween(durationMillis = duration),
                        initialAlpha = 0f
                    ) +
                    slideInVertically(
                        animationSpec = tween(durationMillis = duration),
                        initialOffsetY = { 16 }
                    )
            ) {
                Message()
            }
        } else {
            Message()
        }
    }
}

@Composable
private fun Message(
    modifier: Modifier = Modifier
) {
    Answer(
        answer = stringResource(id = R.string.bot_message),
        onMarkdownLinkClicked = { _, _ -> },
        onSourcesExpanded = { },
        modifier = modifier
    )
}
