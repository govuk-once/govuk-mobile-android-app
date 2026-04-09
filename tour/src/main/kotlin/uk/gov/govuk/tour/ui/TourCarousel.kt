package uk.gov.govuk.tour.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.tour.TourConfig
import uk.gov.govuk.tour.TourRepository

/**
 * Displays a popup carousel on the first visit to a screen, driven by [config].
 *
 * The carousel renders one [TourPage] per [TourStep], with page indicators and Next/Skip/Done
 * actions. It is a no-op if the tour has already been seen.
 *
 * Usage:
 * ```kotlin
 * TourCarousel(config = chatTourConfig, tourRepository = tourRepository)
 * ```
 */
@Composable
fun TourCarousel(
    config: TourConfig,
    tourRepository: TourRepository,
    onDismiss: () -> Unit = {}
) {
    val isTourSeen by tourRepository.isTourSeen(config.id).collectAsStateWithLifecycle(true)

    if (isTourSeen || config.steps.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { config.steps.size })
    val coroutineScope = rememberCoroutineScope()

    val dismiss: () -> Unit = {
        coroutineScope.launch {
            tourRepository.markTourSeen(config.id)
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = dismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GovUkTheme.colourScheme.surfaces.primary)
                    .padding(bottom = GovUkTheme.spacing.large)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    TourPage(step = config.steps[page])
                }

                Spacer(Modifier.height(GovUkTheme.spacing.medium))

                PageIndicators(
                    pageCount = config.steps.size,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(GovUkTheme.spacing.medium))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = GovUkTheme.spacing.medium),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isLastPage = pagerState.currentPage == config.steps.size - 1

                    if (!isLastPage) {
                        TextButton(onClick = dismiss) {
                            Text(
                                text = "Skip",
                                color = GovUkTheme.colourScheme.textAndIcons.secondary
                            )
                        }
                        Spacer(Modifier.width(GovUkTheme.spacing.small))
                    }

                    Button(
                        onClick = {
                            if (isLastPage) {
                                dismiss()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GovUkTheme.colourScheme.surfaces.buttonPrimary
                        )
                    ) {
                        Text(
                            text = if (isLastPage) "Done" else "Next",
                            color = GovUkTheme.colourScheme.textAndIcons.buttonPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageIndicators(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .size(if (isSelected) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            GovUkTheme.colourScheme.surfaces.buttonPrimary
                        } else {
                            GovUkTheme.colourScheme.strokes.pageControlsInactive
                        }
                    )
            )
        }
    }
}
