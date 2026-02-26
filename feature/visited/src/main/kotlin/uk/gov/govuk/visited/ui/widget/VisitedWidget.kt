package uk.gov.govuk.visited.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.NonTappableCard
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.model.SectionHeadingLabelButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.visited.R
import uk.gov.govuk.visited.VisitedWidgetUiState
import uk.gov.govuk.visited.VisitedWidgetViewModel
import uk.gov.govuk.visited.ui.model.VisitedUi

@Composable
fun VisitedWidget(
    onSeeAllClick: (String) -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VisitedWidgetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    uiState?.let {
        when (it) {
            is VisitedWidgetUiState.Visited -> {
                VisitedItems(
                    items = it.items,
                    onSeeAllClick = onSeeAllClick,
                    onItemClick = { title, url ->
                        viewModel.onVisitedItemClicked(title, url)
                        launchBrowser(url)
                    },
                    modifier = modifier
                )
            }

            is VisitedWidgetUiState.NoVisited -> {
                NoVisitedItems(modifier = modifier)
            }
        }
    }
}

@Composable
private fun NoVisitedItems(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeadingLabel(title3 = stringResource(R.string.visited_items_title))
        NonTappableCard(
            body = stringResource(R.string.visited_items_no_pages_description)
        )
    }
}

@Composable
private fun VisitedItems(
    items: List<VisitedUi>,
    onSeeAllClick: (String) -> Unit,
    onItemClick: (title: String, url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val title = stringResource(R.string.visited_items_title)
        val buttonTitle = stringResource(R.string.visited_items_see_all)
        val button = SectionHeadingLabelButton(
            title = buttonTitle,
            altText = "$buttonTitle $title",
            onClick = { onSeeAllClick(title) }
        )
        SectionHeadingLabel(
            title3 = title,
            button = button
        )
        items.forEachIndexed { index, visitedUi ->
            ExternalLinkListItem(
                title = visitedUi.title,
                description = "${stringResource(R.string.visited_items_last_visited)} ${visitedUi.lastVisited}",
                onClick = { onItemClick(visitedUi.title, visitedUi.url) },
                isFirst = index == 0,
                isLast = index == items.size - 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoVisitedItemsPreview() {
    GovUkTheme {
        NoVisitedItems()
    }
}

@Preview(showBackground = true)
@Composable
private fun VisitedItemsPreview() {
    val items = listOf(
        VisitedUi(
            title = "Title",
            url = "www.preview.com",
            lastVisited = ""
        )
    )
    GovUkTheme {
        VisitedItems(
            items, {}, { _, _ -> }
        )
    }
}
