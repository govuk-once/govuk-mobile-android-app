package uk.gov.govuk.dvla.ui.component

import android.content.Context
import android.view.accessibility.AccessibilityManager
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.OverflowButton
import uk.gov.govuk.design.ui.extension.withAltText
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem

@Composable
private fun isTalkBackEnabled(): Boolean {
    val context = LocalContext.current
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

    var isEnabled by remember {
        mutableStateOf(accessibilityManager.isTouchExplorationEnabled)
    }

    DisposableEffect(accessibilityManager) {
        val listener = AccessibilityManager.TouchExplorationStateChangeListener { enabled ->
            isEnabled = enabled
        }
        accessibilityManager.addTouchExplorationStateChangeListener(listener)

        onDispose {
            accessibilityManager.removeTouchExplorationStateChangeListener(listener)
        }
    }

    return isEnabled
}

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
    val isTalkBackOn = isTalkBackEnabled()

    Box {
        OverflowButton(
            onClick = { expanded = true },
            altText = stringResource(R.string.more_options_alt_text)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList),
            containerColor = GovUkTheme.colourScheme.surfaces.actionMenu,
            offset = DpOffset(x = 0.dp, y = GovUkTheme.spacing.extraSmall)
        ) {
            menuItems.forEach { item ->
                OverflowMenuItemRow(
                    title = AccessibleString(item.text.displayText, item.text.altText),
                    onClick = {
                        onMenuItemClick(item)
                        expanded = false
                    }
                )
            }

            if (isTalkBackOn) {
                HorizontalDivider()
                OverflowMenuItemRow(
                    title = AccessibleString(stringResource(R.string.menu_close_menu)),
                    onClick = { expanded = false }
                )
            }
        }
    }
}

@Composable
private fun OverflowMenuItemRow(
    title: AccessibleString,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            BodyRegularLabel(
                text = title.displayText,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.withAltText(title.altText)
            )
        },
        onClick = onClick,
        contentPadding = PaddingValues(
            horizontal = GovUkTheme.spacing.medium,
            vertical = GovUkTheme.spacing.small
        ),
        colors = MenuDefaults.itemColors(
            textColor = GovUkTheme.colourScheme.textAndIcons.primary
        )
    )
}
