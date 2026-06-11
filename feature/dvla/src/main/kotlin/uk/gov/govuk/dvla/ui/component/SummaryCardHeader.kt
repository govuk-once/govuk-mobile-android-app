package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun CardOverflowButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(GovUkTheme.colourScheme.surfaces.cardOverflowButton)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = uk.gov.govuk.design.R.drawable.ic_more),
            contentDescription = stringResource(R.string.more_options_alt_text),
            tint = GovUkTheme.colourScheme.textAndIcons.cardOverflowIcon
        )
    }
}

@Composable
fun SummaryCardHeader(
    leadingContent: @Composable () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
    mainContent: @Composable ColumnScope.() -> Unit
) {
    CardListItem(
        modifier = modifier,
        isFirst = true,
        isLast = false,
        drawDivider = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingContent()
                CardOverflowButton(onClick = onMoreClick)
            }

            Spacer(modifier = Modifier.height(12.dp))

            mainContent()
        }
    }
}