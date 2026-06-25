package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun CountdownBar(
    percentage: Float,
    modifier: Modifier = Modifier
) {
    val barHeight = 12.dp

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (percentage > 0) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .height(barHeight)
                    .weight(percentage)
                    .background(GovUkTheme.colourScheme.surfaces.countdownBar)
            )
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(barHeight)
                .background(GovUkTheme.colourScheme.textAndIcons.primary)
        )

        if (percentage < 100) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(barHeight)
                    .weight(100 - percentage)
                    .background(GovUkTheme.colourScheme.surfaces.screenBackground)
            )
        }
    }
}
