package uk.gov.govuk.tour.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.svenjacobs.reveal.revealable

fun Modifier.tourTarget(key: String): Modifier = composed {
    val registry = LocalTourTargetRegistry.current ?: return@composed this
    val revealState = LocalRevealState.current ?: return@composed this
    revealable(key = key, state = revealState)
        .onGloballyPositioned { coords ->
            registry.register(key, coords.boundsInRoot())
        }
}
