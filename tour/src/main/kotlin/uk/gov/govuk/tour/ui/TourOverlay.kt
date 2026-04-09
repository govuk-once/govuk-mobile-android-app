package uk.gov.govuk.tour.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svenjacobs.reveal.Reveal
import com.svenjacobs.reveal.RevealCanvas
import com.svenjacobs.reveal.RevealOverlayArrangement
import com.svenjacobs.reveal.RevealOverlayScope
import com.svenjacobs.reveal.compat.android.inserter.FullscreenRevealOverlayInserter
import com.svenjacobs.reveal.rememberRevealCanvasState
import com.svenjacobs.reveal.rememberRevealState
import com.svenjacobs.reveal.shapes.balloon.Arrow
import com.svenjacobs.reveal.shapes.balloon.Balloon
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.tour.TourConfig
import uk.gov.govuk.tour.TourRepository

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface TourRepositoryEntryPoint {
    fun tourRepository(): TourRepository
}

private enum class TourPlacement { Top, Bottom, Start, End }

/**
 * Wraps [content] with a spotlight tour driven by [config].
 *
 * Mark elements inside [content] as tour targets using [Modifier.tourTarget]. The tour is shown
 * once per [TourConfig.id] and is persisted across app restarts via DataStore.
 *
 * The implementation delegates all overlay and tooltip rendering to the Reveal library.
 * Callers are entirely insulated from that detail — only this composable and [tourTarget] need
 * to change if the underlying implementation is swapped.
 */
@Composable
fun TourOverlay(
    config: TourConfig,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val tourRepository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            TourRepositoryEntryPoint::class.java
        ).tourRepository()
    }

    val coroutineScope = rememberCoroutineScope()
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    val registry = remember { TourTargetRegistry() }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val isTourSeen by tourRepository.isTourSeen(config.id).collectAsStateWithLifecycle(true)
    val isActive = !isTourSeen && config.steps.isNotEmpty()

    val revealCanvasState = rememberRevealCanvasState()
    val revealState = rememberRevealState()

    val currentTargetKey = if (isActive) config.steps.getOrNull(currentStep)?.targetKey else null

    // Reveal the current step's target as soon as it is registered in the composition.
    LaunchedEffect(currentTargetKey) {
        if (currentTargetKey != null) {
            snapshotFlow { revealState.revealableKeys.contains(currentTargetKey) }
                .first { it }
            revealState.reveal(currentTargetKey)
        }
    }

    // Hide the overlay whenever the tour is no longer active.
    LaunchedEffect(isActive) {
        if (!isActive && revealState.isVisible) {
            revealState.hide()
        }
    }

    val onNext: () -> Unit = {
        coroutineScope.launch {
            val nextIndex = currentStep + 1
            if (nextIndex < config.steps.size) {
                currentStep = nextIndex
            } else {
                revealState.hide()
                tourRepository.markTourSeen(config.id)
            }
        }
    }

    val onSkip: () -> Unit = {
        coroutineScope.launch {
            revealState.hide()
            tourRepository.markTourSeen(config.id)
        }
    }

    CompositionLocalProvider(
        LocalRevealState provides revealState,
        LocalTourTargetRegistry provides registry
    ) {
        RevealCanvas(
            revealCanvasState = revealCanvasState,
            overlayInserter = FullscreenRevealOverlayInserter(),
            modifier = modifier
                .fillMaxSize()
                .onSizeChanged { containerSize = it }
        ) {
            Reveal(
                revealCanvasState = revealCanvasState,
                revealState = revealState,
                overlayContent = { key ->
                    val keyStr = key.toString()
                    val stepIndex = config.steps.indexOfFirst { it.targetKey == keyStr }
                    val step = config.steps.getOrNull(stepIndex)
                    if (step != null) {
                        val placement = choosePlacement(
                            targetRect = registry.bounds[keyStr],
                            containerSize = containerSize,
                            density = density
                        )
                        val arrow = placementToArrow(placement)
                        Balloon(
                            arrow = arrow,
                            backgroundColor = GovUkTheme.colourScheme.surfaces.primary,
                            elevation = 4.dp,
                            modifier = placementToModifier(placement).padding(GovUkTheme.spacing.medium)
                        ) {
                            Column(modifier = Modifier
                                .padding(GovUkTheme.spacing.medium)
                                .fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = step.title,
                                        style = GovUkTheme.typography.bodyBold,
                                        color = GovUkTheme.colourScheme.textAndIcons.primary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${stepIndex + 1} / ${config.steps.size}",
                                        style = GovUkTheme.typography.bodyRegular,
                                        color = GovUkTheme.colourScheme.textAndIcons.secondary
                                    )
                                }

                                Spacer(Modifier.height(GovUkTheme.spacing.small))

                                Text(
                                    text = step.body,
                                    style = GovUkTheme.typography.bodyRegular,
                                    color = GovUkTheme.colourScheme.textAndIcons.primary
                                )

                                Spacer(Modifier.height(GovUkTheme.spacing.medium))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (stepIndex < config.steps.size - 1) {
                                        TextButton(onClick = onSkip) {
                                            Text(
                                                text = "Skip",
                                                color = GovUkTheme.colourScheme.textAndIcons.secondary
                                            )
                                        }
                                        Spacer(Modifier.width(GovUkTheme.spacing.small))
                                    }
                                    Button(
                                        onClick = onNext,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = GovUkTheme.colourScheme.surfaces.buttonPrimary
                                        )
                                    ) {
                                        Text(
                                            text = if (stepIndex == config.steps.size - 1) "Done" else "Next",
                                            color = GovUkTheme.colourScheme.textAndIcons.buttonPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            ) {
                content()
            }
        }
    }
}

private const val MIN_SIDE_DP = 160

private fun choosePlacement(
    targetRect: Rect?,
    containerSize: IntSize,
    density: Density
): TourPlacement {
    if (targetRect == null || containerSize.width == 0) return TourPlacement.Bottom

    val spaceAbove = targetRect.top
    val spaceBelow = containerSize.height - targetRect.bottom
    val spaceLeft = targetRect.left
    val spaceRight = containerSize.width - targetRect.right

    val elementIsNarrow = targetRect.width < containerSize.width / 2f
    val minSidePx = with(density) { MIN_SIDE_DP.dp.toPx() }

    return if (elementIsNarrow && (spaceLeft >= minSidePx || spaceRight >= minSidePx)) {
        if (spaceLeft >= spaceRight) TourPlacement.Start else TourPlacement.End
    } else {
        if (spaceBelow >= spaceAbove) TourPlacement.Bottom else TourPlacement.Top
    }
}

// Arrow points toward the highlighted element, so it's on the opposite side from the tooltip body.
@Composable
private fun placementToArrow(placement: TourPlacement): Arrow = when (placement) {
    TourPlacement.Bottom -> Arrow.top()    // tooltip below  → arrow points up
    TourPlacement.Top    -> Arrow.bottom() // tooltip above  → arrow points down
    TourPlacement.Start  -> Arrow.end()    // tooltip left   → arrow points right
    TourPlacement.End    -> Arrow.start()  // tooltip right  → arrow points left
}

private fun RevealOverlayScope.placementToModifier(placement: TourPlacement): Modifier =
    when (placement) {
        TourPlacement.Bottom -> Modifier.align(
            verticalArrangement = RevealOverlayArrangement.Bottom,
            confineWidth = false
        )
        TourPlacement.Top    -> Modifier.align(
            verticalArrangement = RevealOverlayArrangement.Top,
            confineWidth = false
        )
        TourPlacement.Start  -> Modifier.align(
            horizontalArrangement = RevealOverlayArrangement.Start
        )
        TourPlacement.End    -> Modifier.align(
            horizontalArrangement = RevealOverlayArrangement.End
        )
    }
