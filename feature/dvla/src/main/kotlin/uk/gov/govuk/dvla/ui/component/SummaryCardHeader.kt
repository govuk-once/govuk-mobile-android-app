package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.ListDivider
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem

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
internal fun SummaryCardHeader(
    leadingContent: @Composable () -> Unit,
    menuItems: List<OverflowMenuItem>,
    onMenuItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    mainContent: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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

                Box {
                    CardOverflowButton(onClick = { expanded = true })

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList),
                        containerColor = GovUkTheme.colourScheme.surfaces.cardDefault,
                    ) {
                        menuItems.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    BodyRegularLabel(
                                        text = item.text,
                                        color = GovUkTheme.colourScheme.textAndIcons.primary
                                    )
                                },
                                onClick = {
                                    onMenuItemClick(item.url)
                                    expanded = false
                                },
                                contentPadding = PaddingValues(
                                    horizontal = GovUkTheme.spacing.medium,
                                    vertical = GovUkTheme.spacing.small
                                ),
                                colors = MenuDefaults.itemColors(
                                    textColor = GovUkTheme.colourScheme.textAndIcons.primary
                                ),
                                modifier = item.altText?.let {
                                    Modifier.semantics { contentDescription = it }
                                } ?: Modifier
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            mainContent()
        }
    }
}
