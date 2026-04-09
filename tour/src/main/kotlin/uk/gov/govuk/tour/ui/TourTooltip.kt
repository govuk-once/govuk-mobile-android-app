package uk.gov.govuk.tour.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.tour.TourStep

/** Direction the tooltip arrow points — toward the highlighted element. */
internal enum class ArrowDirection { Up, Down, Left, Right }

// Arrow dimensions: depth is how far the tip protrudes, base is the opposite edge width.
private val ArrowDepth = 12.dp
private val ArrowBase = 24.dp
private val CardCornerRadius = 12.dp

private class TooltipShape(private val arrowDirection: ArrowDirection) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val depth = with(density) { ArrowDepth.toPx() }
        val base = with(density) { ArrowBase.toPx() }
        val corner = with(density) { CardCornerRadius.toPx() }

        val path = Path()

        when (arrowDirection) {
            ArrowDirection.Up -> {
                // Card below the arrow
                path.addRoundRect(RoundRect(0f, depth, size.width, size.height, CornerRadius(corner)))
                val cx = size.width / 2f
                path.moveTo(cx - base / 2f, depth)
                path.lineTo(cx, 0f)
                path.lineTo(cx + base / 2f, depth)
                path.close()
            }
            ArrowDirection.Down -> {
                // Card above the arrow
                val cardBottom = size.height - depth
                path.addRoundRect(RoundRect(0f, 0f, size.width, cardBottom, CornerRadius(corner)))
                val cx = size.width / 2f
                path.moveTo(cx - base / 2f, cardBottom)
                path.lineTo(cx, size.height)
                path.lineTo(cx + base / 2f, cardBottom)
                path.close()
            }
            ArrowDirection.Left -> {
                // Card to the right of the arrow
                path.addRoundRect(RoundRect(depth, 0f, size.width, size.height, CornerRadius(corner)))
                val cy = size.height / 2f
                path.moveTo(depth, cy - base / 2f)
                path.lineTo(0f, cy)
                path.lineTo(depth, cy + base / 2f)
                path.close()
            }
            ArrowDirection.Right -> {
                // Card to the left of the arrow
                val cardRight = size.width - depth
                path.addRoundRect(RoundRect(0f, 0f, cardRight, size.height, CornerRadius(corner)))
                val cy = size.height / 2f
                path.moveTo(cardRight, cy - base / 2f)
                path.lineTo(size.width, cy)
                path.lineTo(cardRight, cy + base / 2f)
                path.close()
            }
        }

        return Outline.Generic(path)
    }
}

@Composable
internal fun TourTooltip(
    step: TourStep,
    stepIndex: Int,
    totalSteps: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    arrowDirection: ArrowDirection = ArrowDirection.Down,
    modifier: Modifier = Modifier
) {
    val shape = remember(arrowDirection) { TooltipShape(arrowDirection) }

    Column(
        modifier = modifier
            // Outer margin: suppress the margin on the arrow side since the arrow is flush
            // with the highlighted element; keep it on all other sides.
            .padding(
                start = if (arrowDirection == ArrowDirection.Left) 0.dp else GovUkTheme.spacing.medium,
                end = if (arrowDirection == ArrowDirection.Right) 0.dp else GovUkTheme.spacing.medium
            )
            .shadow(elevation = 4.dp, shape = shape)
            .background(color = GovUkTheme.colourScheme.surfaces.primary, shape = shape)
            // Inner content padding: reserve space on the arrow side so text clears the triangle.
            .padding(
                top = if (arrowDirection == ArrowDirection.Up) ArrowDepth + GovUkTheme.spacing.medium
                      else GovUkTheme.spacing.medium,
                bottom = if (arrowDirection == ArrowDirection.Down) ArrowDepth + GovUkTheme.spacing.medium
                         else GovUkTheme.spacing.medium,
                start = if (arrowDirection == ArrowDirection.Left) ArrowDepth + GovUkTheme.spacing.medium
                        else GovUkTheme.spacing.medium,
                end = if (arrowDirection == ArrowDirection.Right) ArrowDepth + GovUkTheme.spacing.medium
                      else GovUkTheme.spacing.medium
            )
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
                text = "${stepIndex + 1} / $totalSteps",
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
            if (stepIndex < totalSteps - 1) {
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
                    text = if (stepIndex == totalSteps - 1) "Done" else "Next",
                    color = GovUkTheme.colourScheme.textAndIcons.buttonPrimary
                )
            }
        }
    }
}
