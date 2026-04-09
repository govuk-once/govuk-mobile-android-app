package uk.gov.govuk.tour.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned

fun Modifier.tourTarget(key: String): Modifier = composed {
    val registry = LocalTourTargetRegistry.current ?: return@composed this
    onGloballyPositioned { coords ->
        registry.register(key, coords.boundsInRoot())
    }
}
