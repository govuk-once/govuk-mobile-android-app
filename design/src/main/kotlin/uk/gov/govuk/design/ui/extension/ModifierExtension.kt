package uk.gov.govuk.design.ui.extension

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R

@Composable
fun Modifier.drawBottomStroke(colour: Color?, cornerRadius: Dp): Modifier {
    colour ?: return this

    val strokeWidth = 3.dp

    return this
        .drawBehind {
            val cornerRadiusPx = cornerRadius.toPx()
            val strokeWidthPx = strokeWidth.toPx()
            val offset = 2f.dp.toPx()
            val arcY = size.height - cornerRadiusPx * 2 - offset
            val lineY = size.height - offset

            // Bottom line
            drawLine(
                color = colour,
                start = Offset(x = cornerRadiusPx, y = lineY),
                end = Offset(x = size.width - cornerRadiusPx, y = lineY),
                strokeWidth = strokeWidthPx
            )

            // Bottom-left corner
            drawArc(
                color = colour,
                startAngle = 180f,
                sweepAngle = -90f,
                useCenter = false,
                topLeft = Offset(x = offset, y = arcY),
                size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                style = Stroke(width = strokeWidthPx)
            )

            // Bottom-right corner
            drawArc(
                color = colour,
                startAngle = 360f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(
                    x = size.width - cornerRadiusPx * 2 - offset,
                    y = arcY
                ),
                size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                style = Stroke(width = strokeWidthPx)
            )
        }
        .padding(bottom = strokeWidth)
}

@Composable
fun Modifier.talkBackText(vararg textParts: String?): Modifier {
    val description = textParts
        .filterNot { it.isNullOrBlank() }
        .joinToString(". ")
        .replace(
            stringResource(R.string.gov_uk),
            stringResource(R.string.gov_uk_alt_text)
        )

    return this.then(
        Modifier.semantics {
            if (description.isNotEmpty()) {
                contentDescription = description
            }
        }
    )
}
