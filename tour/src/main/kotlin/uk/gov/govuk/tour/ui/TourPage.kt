package uk.gov.govuk.tour.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.tour.TourStep

/**
 * A single page in the [TourCarousel]. Shows an illustration (or placeholder) above the step
 * title and body text.
 */
@Composable
internal fun TourPage(
    step: TourStep,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        if (step.illustrationRes != null) {
            Image(
                painter = painterResource(step.illustrationRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(GovUkTheme.colourScheme.surfaces.screenBackground)
            ) {
                Text(
                    text = step.targetKey,
                    style = GovUkTheme.typography.bodyRegular,
                    color = GovUkTheme.colourScheme.textAndIcons.secondary
                )
            }
        }

        Spacer(Modifier.height(GovUkTheme.spacing.medium))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            Text(
                text = step.title,
                style = GovUkTheme.typography.title3Bold,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(GovUkTheme.spacing.small))

            Text(
                text = step.body,
                style = GovUkTheme.typography.bodyRegular,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
