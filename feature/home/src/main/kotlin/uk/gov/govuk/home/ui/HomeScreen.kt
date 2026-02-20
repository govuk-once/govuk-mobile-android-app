package uk.gov.govuk.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.LayoutBoundsHolder
import androidx.compose.ui.layout.layoutBounds
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.home.HomeViewModel
import uk.gov.govuk.home.R
import uk.gov.govuk.home.ui.animation.AnimateIcon

@Composable
internal fun HomeRoute(
    widgets: List<@Composable (Modifier) -> Unit>,
    modifier: Modifier = Modifier,
    headerWidget: (@Composable (Modifier) -> Unit)? = null,
) {
    val viewModel: HomeViewModel = hiltViewModel()

    HomeScreen(
        widgets = widgets,
        onPageView = { viewModel.onPageView() },
        modifier = modifier,
        headerWidget = headerWidget
    )
}

@Composable
private fun HomeScreen(
    widgets: List<@Composable (Modifier) -> Unit>,
    onPageView: () -> Unit,
    modifier: Modifier = Modifier,
    headerWidget: (@Composable (Modifier) -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier.background(GovUkTheme.colourScheme.surfaces.screenBackground)) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(GovUkTheme.colourScheme.surfaces.homeHeader)
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            MediumVerticalSpacer()

            Image(
                painter = painterResource(id = uk.gov.govuk.design.R.drawable.logo),
                contentDescription = stringResource(id = R.string.logo_alt_text),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .semantics { heading() }
                    .focusRequester(focusRequester)
                    .focusable()
            )

            MediumVerticalSpacer()

            if (headerWidget != null) {
                headerWidget(Modifier.fillMaxWidth())
                MediumVerticalSpacer()
            }
        }

        val iconHeight = 38.dp
        val viewport = remember { LayoutBoundsHolder() }
        var showIcon by remember { mutableStateOf(false) }

        LazyColumn (
            modifier = Modifier
                .layoutBounds(viewport)
                .padding(horizontal = GovUkTheme.spacing.medium),
            state = rememberLazyListState()
        ) {
            items(widgets) { widget ->
                LargeVerticalSpacer()
                widget(Modifier
                    .fillMaxWidth()
                    .animateItem())
            }
            item {
                LargeVerticalSpacer()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(iconHeight)
                        .onVisibilityChanged(
                            viewportBounds = viewport
                        ) { visible ->
                            showIcon = visible
                        }
                ) {
                    AnimateIcon(
                        showIcon,
                        {
                            Icon(
                                painter = painterResource(id = uk.gov.govuk.design.R.drawable.crown),
                                contentDescription = null,
                                tint = GovUkTheme.colourScheme.textAndIcons.logoCrown,
                                modifier = Modifier
                                    .height(iconHeight)
                            )
                        }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    LaunchedEffect(Unit) {
        onPageView()
        focusRequester.requestFocus()
    }
}
