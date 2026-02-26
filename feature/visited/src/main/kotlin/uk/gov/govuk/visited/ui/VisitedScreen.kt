package uk.gov.govuk.visited.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.NonTappableCard
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.model.ExternalLinkListItemStyle
import uk.gov.govuk.design.ui.model.HeaderActionStyle
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.visited.R
import uk.gov.govuk.visited.VisitedUiState
import uk.gov.govuk.visited.VisitedViewModel
import uk.gov.govuk.visited.ui.model.VisitedUi

@Composable
internal fun VisitedRoute(
    onBack: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VisitedViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    VisitedScreen(
        uiState = uiState,
        onPageView = { viewModel.onPageView() },
        onBack = onBack,
        onClick = { title, url ->
            coroutineScope.launch {
                delay(500)
                viewModel.onVisitedItemClicked(title, url)
            }
            launchBrowser(url)
        },
        onRemoveAllClick = {
            viewModel.onRemoveAllVisitedItemsClicked()
        },
        onRemoveClick = { title, url ->
            viewModel.onVisitedItemRemoveClicked(title, url)
        },
        modifier = modifier
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun VisitedScreen(
    uiState: VisitedUiState?,
    onPageView: () -> Unit,
    onBack: () -> Unit,
    onClick: (title: String, url: String) -> Unit,
    onRemoveAllClick: () -> Unit,
    onRemoveClick: (title: String, url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    val title = stringResource(R.string.visited_items_title)
    val removeAllText = stringResource(R.string.visited_items_remove_all_button)
    val removeAllAltText = stringResource(R.string.visited_items_remove_all)
    val visitedItems = uiState?.visited
    var showRemoveAllDialog by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {

        ChildPageHeader(
            text = title,
            dismissStyle = HeaderDismissStyle.Back(onBack),
            actionStyle = if (visitedItems.isNullOrEmpty()) {
                HeaderActionStyle.None
            } else {
                HeaderActionStyle.ActionButton(
                    onClick = { showRemoveAllDialog = true },
                    title = removeAllText,
                    altText = removeAllAltText
                )
            }
        )
        if (visitedItems.isNullOrEmpty()) {
            NoVisitedItems()
        } else {
            ShowVisitedItems(visitedItems, onClick, onRemoveClick)
        }

        if (showRemoveAllDialog) {
            RemoveAllConfirmationDialog(
                onConfirm = onRemoveAllClick,
                onDismiss = { showRemoveAllDialog = false }
            )
        }
    }
}

@Composable
private fun NoVisitedItems(
    modifier: Modifier = Modifier
) {
    MediumVerticalSpacer()

    NonTappableCard(
        body = stringResource(R.string.visited_items_no_pages_description),
        modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium)
    )
}

@Composable
private fun ShowVisitedItems(
    items: Map<String, List<VisitedUi>>,
    onClick: (title: String, url: String) -> Unit,
    onRemoveClick: (title: String, url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    MediumVerticalSpacer()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = GovUkTheme.spacing.medium),
        state = rememberLazyListState()
    ) {
        items.forEach { (sectionTitle, visitedItems) ->
            if (visitedItems.isNotEmpty()) {
                item {
                    SectionHeadingLabel(title3 = sectionTitle)
                }
                itemsIndexed(
                    items = visitedItems,
                    key = { _, item -> item.id }
                ) { index, item ->
                    val title = item.title
                    val url = item.url
                    val description = "${stringResource(R.string.visited_items_last_visited)} ${item.lastVisited}"
                    val style = ExternalLinkListItemStyle.Button(
                        icon = uk.gov.govuk.design.R.drawable.ic_cancel_round,
                        altText = "${stringResource(uk.gov.govuk.design.R.string.content_desc_remove)} $title",
                        onClick = {
                            onRemoveClick(title, url)
                        }
                    )

                    if (index == 0) {
                        ExternalLinkListItem(
                            title = title,
                            description = description,
                            onClick = { onClick(title, url) },
                            modifier = Modifier.animateItem(),
                            isFirst = true,
                            isLast = visitedItems.size == 1,
                            style = style
                        )
                    } else {
                        ExternalLinkListItem(
                            title = title,
                            description = description,
                            onClick = { onClick(title, url) },
                            modifier = Modifier.animateItem(),
                            isFirst = false,
                            isLast = index == visitedItems.size - 1,
                            style = style
                        )
                    }
                }
                item {
                    LargeVerticalSpacer()
                }
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
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.visited_items_remove_all_button),
                    color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.visited_items_cancel_button),
                    color = GovUkTheme.colourScheme.textAndIcons.linkSecondary
                )
            }
        },
        modifier = modifier,
        title = {
            BodyBoldLabel(stringResource(R.string.visited_items_remove_all))
        },
        containerColor = GovUkTheme.colourScheme.surfaces.alert
    )
}
