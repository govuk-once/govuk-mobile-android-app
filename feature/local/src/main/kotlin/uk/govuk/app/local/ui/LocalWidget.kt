package uk.govuk.app.local.ui

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CentredCardWithIcon
import uk.gov.govuk.design.ui.component.DialogButton
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.NavigationCard
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.extension.withAltText
import uk.gov.govuk.design.ui.model.SectionHeadingLabelButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalWidgetUiState.LocalAuthoritySelected
import uk.govuk.app.local.LocalWidgetUiState.NoLocalAuthority
import uk.govuk.app.local.LocalWidgetViewModel
import uk.govuk.app.local.R
import uk.govuk.app.local.domain.model.LocalAuthority

@Composable
fun LocalWidget(
    onLookupClick: (String) -> Unit,
    onLocalAuthorityClick: (String, String) -> Unit,
    onChangeClick: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LocalWidgetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    uiState?.let {
        when (it) {
            is LocalAuthoritySelected ->
                LocalAuthorityCard(
                    localAuthority = it.localAuthority,
                    onClick = onLocalAuthorityClick,
                    onEditClick = viewModel::onEditClick,
                    onChangeClick = { text ->
                        viewModel.onChangeClick(text)
                        onChangeClick()
                    },
                    onRemoveConfirmClick = viewModel::onRemoveConfirmClick,
                    launchBrowser = launchBrowser,
                    modifier = modifier
                )
            is NoLocalAuthority -> NoLocalAuthorityCard(onLookupClick, modifier)
        }
    }
}

@Composable
private fun LocalAuthorityCard(
    localAuthority: LocalAuthority,
    onClick: (String, String) -> Unit,
    onEditClick: (String) -> Unit,
    onChangeClick: (String) -> Unit,
    onRemoveConfirmClick: (String) -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    var showRemoveDialog by rememberSaveable { mutableStateOf(false) }

    Column(modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val editButtonText = stringResource(R.string.local_edit_button)
                val editButtonAltText = stringResource(R.string.local_edit_button_alt_text)

                SectionHeadingLabel(
                    title3 = stringResource(R.string.local_widget_title),
                    button = SectionHeadingLabelButton(
                        title = editButtonText,
                        altText = editButtonAltText,
                        onClick = {
                            onEditClick(editButtonText)
                            menuExpanded = true
                        }
                    )
                )
            }

            Box(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                EditMenu(
                    expanded = menuExpanded,
                    onDismiss = { menuExpanded = false },
                    onChangeClick = { text ->
                        menuExpanded = false
                        onChangeClick(text)
                    },
                    onRemoveClick = {
                        menuExpanded = false
                        showRemoveDialog = true
                    }
                )
            }
        }

        if (showRemoveDialog) {
            RemoveConfirmationDialog(
                isTwoTier = localAuthority.parent != null,
                onConfirm = onRemoveConfirmClick,
                onDismiss = { showRemoveDialog = false }
            )
        }

        val description = if (localAuthority.parent != null) {
            stringResource(R.string.local_child_authority_description)
        } else {
            null
        }

        localAuthority.parent?.let { parent ->
            BodyRegularLabel(stringResource(R.string.local_two_tier_title))
            SmallVerticalSpacer()
            NavigationCard(
                title = parent.name,
                description = stringResource(R.string.local_parent_authority_description),
                onClick = {
                    onClick(parent.name, parent.url)
                    launchBrowser(parent.url)
                }
            )
            LargeVerticalSpacer()
        }

        NavigationCard(
            title = localAuthority.name,
            description = description,
            onClick = {
                onClick(localAuthority.name, localAuthority.url)
                launchBrowser(localAuthority.url)
            }
        )
    }
}

@Composable
private fun EditMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onChangeClick: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    val isTalkBackOn = isTalkBackEnabled()

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList),
        containerColor = GovUkTheme.colourScheme.surfaces.actionMenu,
        offset = DpOffset(x = 0.dp, y = GovUkTheme.spacing.extraSmall)
    ) {
        EditMenuItems(
            isTalkBackOn = isTalkBackOn,
            onDismiss = onDismiss,
            onChangeClick = onChangeClick,
            onRemoveClick = onRemoveClick
        )
    }
}

@Composable
private fun EditMenuItems(
    isTalkBackOn: Boolean,
    onDismiss: () -> Unit,
    onChangeClick: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    val changeText = stringResource(R.string.local_change_button)
    EditMenuItem(
        text = changeText,
        altText = stringResource(R.string.local_change_button_alt_text),
        onClick = { onChangeClick(changeText) }
    )

    EditMenuItem(
        text = stringResource(R.string.local_remove_button),
        altText = stringResource(R.string.local_remove_button_alt_text),
        onClick = onRemoveClick,
        textColour = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
    )

    if (isTalkBackOn) {
        HorizontalDivider()
        EditMenuItem(
            text = stringResource(R.string.local_close_menu),
            onClick = onDismiss
        )
    }
}

@Composable
private fun EditMenuItem(
    text: String,
    onClick: () -> Unit,
    altText: String? = null,
    textColour: Color = GovUkTheme.colourScheme.textAndIcons.primary
) {
    DropdownMenuItem(
        text = {
            BodyRegularLabel(
                text = text,
                color = textColour,
                modifier = Modifier.withAltText(altText)
            )
        },
        onClick = onClick,
        contentPadding = PaddingValues(
            horizontal = GovUkTheme.spacing.medium,
            vertical = GovUkTheme.spacing.small
        )
    )
}

@Composable
private fun RemoveConfirmationDialog(
    isTwoTier: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val (title, description) = if (isTwoTier) {
        R.string.local_remove_dialog_title_two_tier to R.string.local_remove_dialog_description_two_tier
    } else {
        R.string.local_remove_dialog_title_unitary to R.string.local_remove_dialog_description_unitary
    }
    val removeText = stringResource(R.string.local_remove_button)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList),
        title = {
            BodyBoldLabel(
                text = stringResource(title),
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
        },
        text = {
            BodyRegularLabel(
                text = stringResource(description),
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        },
        confirmButton = {
            DialogButton(
                text = removeText,
                onClick = {
                    onDismiss()
                    onConfirm(removeText)
                },
                defaultTextColour = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
            )
        },
        dismissButton = {
            DialogButton(
                text = stringResource(R.string.local_cancel_button),
                onClick = onDismiss,
                defaultTextColour = GovUkTheme.colourScheme.textAndIcons.linkSecondary
            )
        },
        containerColor = GovUkTheme.colourScheme.surfaces.alert
    )
}

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
private fun NoLocalAuthorityCard(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        SectionHeadingLabel(
            title3 = stringResource(R.string.local_widget_title)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val description = stringResource(R.string.local_widget_description)

            CentredCardWithIcon(
                onClick = { onClick(description) },
                icon = uk.gov.govuk.design.R.drawable.ic_add,
                description = description
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun NoLocalAuthorityPreview() {
    GovUkTheme {
        NoLocalAuthorityCard(
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun UnitaryLocalAuthorityPreview() {
    GovUkTheme {
        LocalAuthorityCard(
            localAuthority = LocalAuthority(
                name = "London Borough of Tower Hamlets",
                url = "",
                slug = ""
            ),
            onClick = { _, _ -> },
            onEditClick = { },
            onChangeClick = { },
            onRemoveConfirmClick = { },
            launchBrowser = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun TwoTierLocalAuthorityPreview() {
    GovUkTheme {
        LocalAuthorityCard(
            localAuthority = LocalAuthority(
                name = "Derbyshire Dales District Council",
                url = "",
                slug = "",
                parent = LocalAuthority(
                    name = "Derbyshire County Council",
                    url = "",
                    slug = ""
                )
            ),
            onClick = { _, _ -> },
            onEditClick = { },
            onChangeClick = { },
            onRemoveConfirmClick = { },
            launchBrowser = {}
        )
    }
}

@Preview (showBackground = true)
@Composable
internal fun EditMenuPreview() {
    GovUkTheme {
        EditMenuItemsPreview(isTalkBackOn = false)
    }
}

@Preview (showBackground = true)
@Composable
internal fun EditMenuTalkBackPreview() {
    GovUkTheme {
        EditMenuItemsPreview(isTalkBackOn = true)
    }
}

@Composable
private fun EditMenuItemsPreview(isTalkBackOn: Boolean) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .background(
                color = GovUkTheme.colourScheme.surfaces.actionMenu,
                shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList)
            )
    ) {
        EditMenuItems(
            isTalkBackOn = isTalkBackOn,
            onDismiss = { },
            onChangeClick = { },
            onRemoveClick = { }
        )
    }
}

@Preview (showBackground = true)
@Composable
internal fun UnitaryRemoveConfirmationDialogPreview() {
    GovUkTheme {
        RemoveConfirmationDialog(
            isTwoTier = false,
            onConfirm = { },
            onDismiss = { }
        )
    }
}

@Preview (showBackground = true)
@Composable
internal fun TwoTierRemoveConfirmationDialogPreview() {
    GovUkTheme {
        RemoveConfirmationDialog(
            isTwoTier = true,
            onConfirm = { },
            onDismiss = { }
        )
    }
}
