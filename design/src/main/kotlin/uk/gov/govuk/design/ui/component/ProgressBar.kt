package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun ProgressBar(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val boxMaxWidth = this.maxWidth
        val indicatorAndSpacedByWidth = 16.dp
        val screenWidthMinusIndicatorAndSpacedBy = boxMaxWidth - indicatorAndSpacedByWidth
        val onePercentOfScreenWidth = screenWidthMinusIndicatorAndSpacedBy / 100
        val leadingBarWidth = onePercentOfScreenWidth * percentage
        val trailingBarWidth = screenWidthMinusIndicatorAndSpacedBy - leadingBarWidth
        val barHeight = 12.dp

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .height(barHeight)
                    .width(leadingBarWidth)
                    .background(GovUkTheme.colourScheme.surfaces.progressBar)
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(barHeight)
                    .background(GovUkTheme.colourScheme.textAndIcons.primary)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .height(barHeight)
                    .width(trailingBarWidth)
                    .background(GovUkTheme.colourScheme.surfaces.screenBackground)
            )
        }
    }
}
