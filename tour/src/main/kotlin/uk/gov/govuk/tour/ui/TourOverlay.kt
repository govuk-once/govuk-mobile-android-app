package uk.gov.govuk.tour.ui

import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch
import uk.gov.govuk.tour.TourConfig
import uk.gov.govuk.tour.TourRepository
import kotlin.math.roundToInt

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface TourRepositoryEntryPoint {
    fun tourRepository(): TourRepository
}

private val OverlayColour = Color.Black.copy(alpha = 0.7f)
private const val SpotlightPaddingPx = 16f
private const val SpotlightCornerRadiusPx = 12f
private const val AnimationDurationMs = 300

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
    val registry = remember { TourTargetRegistry() }
    val coroutineScope = rememberCoroutineScope()
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    var containerBounds by remember { mutableStateOf(Rect.Zero) }
    var tooltipSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val isTourSeen by tourRepository.isTourSeen(config.id).collectAsStateWithLifecycle(true)
    val isActive = !isTourSeen && config.steps.isNotEmpty()

    val currentTargetKey = if (isActive) config.steps.getOrNull(currentStep)?.targetKey else null
    val targetRect = currentTargetKey?.let { registry.bounds[it] }

    val animatedRect by animateRectAsState(
        targetValue = targetRect ?: Rect.Zero,
        animationSpec = tween(AnimationDurationMs),
        label = "spotlight"
    )

    val onNext: () -> Unit = {
        coroutineScope.launch {
            val nextIndex = currentStep + 1
            if (nextIndex < config.steps.size) {
                currentStep = nextIndex
            } else {
                tourRepository.markTourSeen(config.id)
            }
        }
    }

    val onSkip: () -> Unit = {
        coroutineScope.launch { tourRepository.markTourSeen(config.id) }
    }

    CompositionLocalProvider(LocalTourTargetRegistry provides registry) {
        Box(
            modifier
                .fillMaxSize()
                .onGloballyPositioned { coords -> containerBounds = coords.boundsInRoot() }
        ) {
            content()

            if (isActive && targetRect != null) {
                val paddedRect = animatedRect.inflate(SpotlightPaddingPx)

                Canvas(Modifier.fillMaxSize().graphicsLayer(alpha = 0.99f)) {
                    drawIntoCanvas { canvas ->
                        val overlayPaint = Paint().apply { color = OverlayColour }
                        canvas.saveLayer(Rect(0f, 0f, size.width, size.height), overlayPaint)

                        canvas.drawRect(0f, 0f, size.width, size.height, overlayPaint)

                        canvas.drawRoundRect(
                            left = paddedRect.left,
                            top = paddedRect.top,
                            right = paddedRect.right,
                            bottom = paddedRect.bottom,
                            radiusX = SpotlightCornerRadiusPx,
                            radiusY = SpotlightCornerRadiusPx,
                            paint = Paint().apply {
                                color = Color.Transparent
                                blendMode = BlendMode.Clear
                            }
                        )

                        canvas.restore()
                    }
                }

                val step = config.steps.getOrNull(currentStep)
                if (step != null) {
                    val arrowDirection = choosePlacement(targetRect, containerBounds, density)
                    val tooltipModifier = tooltipModifier(
                        arrowDirection = arrowDirection,
                        targetRect = targetRect,
                        containerBounds = containerBounds,
                        tooltipSize = tooltipSize,
                        density = density,
                        onSizeChanged = { tooltipSize = it }
                    )
                    TourTooltip(
                        step = step,
                        stepIndex = currentStep,
                        totalSteps = config.steps.size,
                        onNext = onNext,
                        onSkip = onSkip,
                        arrowDirection = arrowDirection,
                        modifier = tooltipModifier
                    )
                }
            }
        }
    }
}

/**
 * Selects which side of the target to place the tooltip on.
 *
 * Narrow elements (less than half the container width) prefer left/right placement — the side
 * with more available space wins. Wide elements use top/bottom placement instead.
 */
private fun choosePlacement(
    targetRect: Rect,
    containerBounds: Rect,
    density: Density
): ArrowDirection {
    if (containerBounds.width == 0f) return ArrowDirection.Down

    val spaceAbove = targetRect.top - containerBounds.top - SpotlightPaddingPx
    val spaceBelow = containerBounds.bottom - targetRect.bottom - SpotlightPaddingPx
    val spaceLeft = targetRect.left - containerBounds.left - SpotlightPaddingPx
    val spaceRight = containerBounds.right - targetRect.right - SpotlightPaddingPx

    val elementIsNarrow = targetRect.width < containerBounds.width / 2f
    // Minimum horizontal space for a readable side tooltip
    val minSidePx = with(density) { 160.dp.toPx() }

    return if (elementIsNarrow && (spaceLeft >= minSidePx || spaceRight >= minSidePx)) {
        // Place to the side — arrow points toward the element
        if (spaceLeft >= spaceRight) ArrowDirection.Right else ArrowDirection.Left
    } else {
        if (spaceBelow >= spaceAbove) ArrowDirection.Up else ArrowDirection.Down
    }
}

/**
 * Builds the positioning [Modifier] for the tooltip.
 *
 * For Up/Down, the tooltip is full-width and offset vertically adjacent to the target.
 * For Left/Right, the tooltip is width-constrained to the available side space and centred
 * vertically on the target element so the arrow aligns with its centre.
 */
private fun tooltipModifier(
    arrowDirection: ArrowDirection,
    targetRect: Rect,
    containerBounds: Rect,
    tooltipSize: IntSize,
    density: Density,
    onSizeChanged: (IntSize) -> Unit
): Modifier {
    val targetLocalTop = targetRect.top - containerBounds.top
    val targetLocalBottom = targetRect.bottom - containerBounds.top
    val targetLocalCenterY = targetRect.center.y - containerBounds.top

    return when (arrowDirection) {
        ArrowDirection.Up, ArrowDirection.Down -> {
            val yPx = if (arrowDirection == ArrowDirection.Up) {
                // Tooltip below the target
                (targetLocalBottom + SpotlightPaddingPx).roundToInt()
            } else {
                // Tooltip above the target
                (targetLocalTop - SpotlightPaddingPx - tooltipSize.height).roundToInt()
            }
            Modifier
                .fillMaxWidth()
                .onSizeChanged(onSizeChanged)
                .offset { IntOffset(x = 0, y = yPx.coerceAtLeast(0)) }
        }

        ArrowDirection.Right -> {
            // Tooltip to the LEFT of the element, arrow points right
            val availableWidthDp = with(density) {
                (targetRect.left - containerBounds.left - SpotlightPaddingPx).toDp()
            }
            val centeredY = (targetLocalCenterY - tooltipSize.height / 2f).roundToInt()
                .coerceIn(0, (containerBounds.height - tooltipSize.height).roundToInt().coerceAtLeast(0))
            Modifier
                .widthIn(max = availableWidthDp)
                .onSizeChanged(onSizeChanged)
                .offset { IntOffset(x = 0, y = centeredY) }
        }

        ArrowDirection.Left -> {
            // Tooltip to the RIGHT of the element, arrow points left
            val availableWidthDp = with(density) {
                (containerBounds.right - targetRect.right - SpotlightPaddingPx).toDp()
            }
            val xPx = (targetRect.right - containerBounds.left + SpotlightPaddingPx).roundToInt()
            val centeredY = (targetLocalCenterY - tooltipSize.height / 2f).roundToInt()
                .coerceIn(0, (containerBounds.height - tooltipSize.height).roundToInt().coerceAtLeast(0))
            Modifier
                .widthIn(max = availableWidthDp)
                .onSizeChanged(onSizeChanged)
                .offset { IntOffset(x = xPx, y = centeredY) }
        }
    }
}
