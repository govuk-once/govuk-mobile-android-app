package uk.gov.govuk.topics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.AllViewModel
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.ui.model.TopicUi.TopicContent

@Composable
internal fun AllPopularPagesRoute(
    onBack: () -> Unit,
    onClick: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AllViewModel = hiltViewModel()
    val popularPages by viewModel.popularPages.collectAsState()

    AllPopularPagesScreen(
        popularPages = popularPages,
        onPageView = { title -> viewModel.onPopularPagesView(popularPages, title) },
        onBack = onBack,
        onExternalLink = { section, text, url, selectedItemIndex ->
            viewModel.onPopularPagesClick(
                section = section,
                text = text,
                url = url,
                selectedItemIndex = selectedItemIndex,
                popularPagesCount = popularPages.size
            )
            onClick(url)
        },
        modifier = modifier
    )
}

@Composable
private fun AllPopularPagesScreen(
    popularPages: List<TopicContent>,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    onExternalLink: (section: String, text: String, url: String, selectedItemIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        val title = stringResource(R.string.popular_pages_title)

        LaunchedEffect(popularPages) {
            onPageView(title)
        }

        ChildPageHeader(
            text = title,
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )

        LazyColumn(Modifier.padding(horizontal = GovUkTheme.spacing.medium)) {
            item {
                MediumVerticalSpacer()
            }

            popularPages(
                selectedItemIndex = 1,
                popularPages = popularPages,
                onClick = { text, url, selectedItemIndex ->
                    onExternalLink(title, text, url, selectedItemIndex)
                }
            )
        }
    }
}

private fun LazyListScope.popularPages(
    selectedItemIndex: Int,
    popularPages: List<TopicContent>,
    onClick: (text: String, url: String, selectedItemIndex: Int) -> Unit
) {
    itemsIndexed(popularPages) { index, content ->
        ExternalLinkListItem(
            title = content.title,
            onClick = { onClick(content.title, content.url, selectedItemIndex + index) },
            isFirst = index == 0,
            isLast = index == popularPages.lastIndex
        )
    }

    item {
        LargeVerticalSpacer()
    }
}
