package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.OverflowButton
import uk.gov.govuk.design.ui.theme.GovUkTheme

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
                OverflowButton(onClick = onMoreClick)
            }

            Spacer(modifier = Modifier.height(12.dp))

            mainContent()
        }
    }
}