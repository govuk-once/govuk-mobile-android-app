package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlin.math.roundToInt

/**
 * A [LazyListState] that keeps the same content in view when the list's width changes (e.g. on rotation).
 *
 * The standard list state keeps the first visible item's pixel offset, but tall items reflow to a
 * different height at a new width, so the same pixel offset shows different content. Instead, the offset
 * is tracked as a fraction of the item's height and restored when the width changes.
 *
 * - If the user was at the bottom of the list it stays at the bottom, as the items reflowing can
 *   otherwise leave the end of the list just out of view.
 * - The anchor is only updated when the user scrolls, so returning to the original width restores the
 *   exact original offset.
 * - The anchor is saveable so it survives the activity being recreated.
 * - The restore is re-applied if the height changes while the layout settles (e.g. insets being applied),
 *   as a briefly taller viewport can clamp the restored offset.
 */
@Composable
fun rememberWidthAwareLazyListState(): LazyListState {
    val listState = rememberLazyListState()

    var lastWidth by rememberSaveable { mutableIntStateOf(0) }
    var anchorWidth by rememberSaveable { mutableIntStateOf(0) }
    var anchorIndex by rememberSaveable { mutableIntStateOf(0) }
    var anchorOffset by rememberSaveable { mutableIntStateOf(0) }
    var anchorFraction by rememberSaveable { mutableFloatStateOf(0f) }
    var anchorIsAtEnd by rememberSaveable { mutableStateOf(false) }
    var isRestored by rememberSaveable { mutableStateOf(false) }
    var restoredHeight by remember { mutableIntStateOf(0) }

    // A user scroll ends the restored position, the restore's own scrollToItem
    // starts and ends without suspending, so isn't seen here
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) isRestored = false
            }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val width = layoutInfo.viewportSize.width
                val height = layoutInfo.viewportSize.height
                if (width == 0) return@collect

                val isWidthChange = lastWidth != 0 && width != lastWidth
                val isSettling = isRestored && !listState.isScrollInProgress && height != restoredHeight
                lastWidth = width
                if (isWidthChange || isSettling) {
                    // Scrolling to the top of the last item is clamped to the end of the list
                    val target = when {
                        anchorIsAtEnd -> layoutInfo.totalItemsCount - 1 to 0
                        width == anchorWidth -> anchorIndex to anchorOffset
                        else ->
                            layoutInfo.visibleItemsInfo
                                .firstOrNull { it.index == anchorIndex }
                                ?.let { anchorIndex to (anchorFraction * it.size).roundToInt() }
                    }
                    if (target != null) {
                        listState.scrollToItem(target.first, target.second)
                        isRestored = true
                        restoredHeight = height
                        return@collect
                    }
                }

                // Keep the anchor from the user's last scroll until they scroll again,
                // the restored position can be clamped (e.g. near the end of the list) and isn't exact
                if (isRestored) return@collect

                val index = listState.firstVisibleItemIndex
                val offset = listState.firstVisibleItemScrollOffset
                layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }?.let { firstItem ->
                    anchorWidth = width
                    anchorIndex = index
                    anchorOffset = offset
                    anchorFraction = if (firstItem.size > 0) offset.toFloat() / firstItem.size else 0f
                    // Not if the whole list fits on screen, it's shown from the top
                    anchorIsAtEnd = !listState.canScrollForward && listState.canScrollBackward
                }
            }
    }

    return listState
}
