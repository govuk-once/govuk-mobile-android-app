package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.OverflowButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem

@Composable
internal fun SummaryCardHeader(
    leadingContent: @Composable () -> Unit,
    menuItems: List<OverflowMenuItem>,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
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

                CardOverflowMenu(
                    menuItems = menuItems,
                    onMenuItemClick = onMenuItemClick
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            mainContent()
        }
    }
}

@Composable
private fun CardOverflowMenu(
    menuItems: List<OverflowMenuItem>,
    onMenuItemClick: (OverflowMenuItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OverflowButton(onClick = { expanded = true })

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList),
            containerColor = GovUkTheme.colourScheme.surfaces.cardDefault,
            offset = DpOffset(x = 0.dp, y = GovUkTheme.spacing.extraSmall)
        ) {
            menuItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        BodyRegularLabel(
                            text = item.text.displayText,
                            color = GovUkTheme.colourScheme.textAndIcons.primary,
                            modifier = item.text.altText?.let {
                                Modifier.semantics { contentDescription = it }
                            } ?: Modifier
                        )
                    },
                    onClick = {
                        onMenuItemClick(item)
                        expanded = false
                    },
                    contentPadding = PaddingValues(
                        horizontal = GovUkTheme.spacing.medium,
                        vertical = GovUkTheme.spacing.small
                    ),
                    colors = MenuDefaults.itemColors(
                        textColor = GovUkTheme.colourScheme.textAndIcons.primary
                    )
                )
            }
        }
    }
}
