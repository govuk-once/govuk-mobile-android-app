package uk.gov.govuk.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ExtraSmallHorizontalSpacer
import uk.gov.govuk.design.ui.component.ListDivider
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.search.R

@Composable
internal fun PreviousSearches(
    previousSearches: List<String>,
    onClick: (String) -> Unit,
    onRemoveAll: () -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    if (previousSearches.isNotEmpty()) {
        var showDialog by remember { mutableStateOf(false) }
        val numberOfPreviousSearches =
            pluralStringResource(
                id = R.plurals.number_of_previous_searches,
                count = previousSearches.size,
                previousSearches.size
            )

        LazyColumn(
            modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            item {
                Header(
                    focusRequester,
                    onRemoveAll = {
                        showDialog = true
                    },
                    altText = numberOfPreviousSearches
                )
            }
            items(previousSearches) { searchTerm ->
                PreviousSearch(
                    searchTerm = searchTerm,
                    onClick = onClick,
                    onRemove = onRemove
                )
            }
            item {
                ListDivider()
            }
        }

        if (showDialog) {
            RemoveAllConfirmationDialog(
                onConfirm = onRemoveAll,
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
private fun Header(
    focusRequester: FocusRequester,
    onRemoveAll: () -> Unit,
    altText: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val title = stringResource(R.string.previous_searches_heading)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = GovUkTheme.spacing.small,
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodyBoldLabel(
            text = title,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .focusable()
                .semantics {
                    heading()
                    contentDescription = "$altText. $title"
                    liveRegion = LiveRegionMode.Polite
                }
        )

        SmallHorizontalSpacer()

        TextButton(
            onClick = onRemoveAll
        ) {
            BodyRegularLabel(
                text = stringResource(R.string.remove_all_button),
                modifier = Modifier
                    .semantics {
                        contentDescription = context.getString(R.string.content_desc_delete_all)
                    },
                color = GovUkTheme.colourScheme.textAndIcons.buttonSecondary,
            )
        }
    }
}

@Composable
private fun PreviousSearch(
    searchTerm: String,
    onClick: (String) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier) {
        ListDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(searchTerm) }
                .semantics {
                    onClick(label = context.getString(R.string.content_desc_search)) { true }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = GovUkTheme.colourScheme.textAndIcons.secondary
            )
            ExtraSmallHorizontalSpacer()
            BodyRegularLabel(
                text = searchTerm,
                modifier = Modifier.weight(1f)
            )
            ExtraSmallHorizontalSpacer()
            TextButton(
                onClick = { onRemove(searchTerm) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = stringResource(R.string.content_desc_remove_from_search_history),
                    modifier = Modifier.size(18.dp),
                    tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon
                )
            }
        }
    }
}

@Composable
private fun RemoveAllConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList),
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.remove_confirmation_dialog_button),
                    color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
                )
            }
        },
        modifier = modifier,
        title = {
            BodyBoldLabel(stringResource(R.string.remove_confirmation_dialog_title))
        },
        text = {
            BodyRegularLabel(
                text = stringResource(R.string.remove_confirmation_dialog_message),
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        },
        containerColor = GovUkTheme.colourScheme.surfaces.alert
    )
}
