package uk.gov.govuk.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.extension.areAnimationsEnabled
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun SplashScreen(
    onSplashDone: () -> Unit
) {
    val altText: String = stringResource(id = uk.gov.govuk.design.R.string.gov_uk_alt_text)

    val govukSplashComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.govuk_splash)
    )
    val crownSplashComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.crown_splash)
    )

    var govukProgress by remember { mutableFloatStateOf(0f) }
    var crownProgress by remember { mutableFloatStateOf(0f) }

    val govukStateProgress = animateLottieCompositionAsState(
        composition = govukSplashComposition,
        isPlaying = LocalContext.current.areAnimationsEnabled(),
        restartOnPlay = false
    )
    val crownStateProgress = animateLottieCompositionAsState(
        composition = crownSplashComposition,
        isPlaying = LocalContext.current.areAnimationsEnabled(),
        restartOnPlay = false
    )

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val govukAlignment = if (isLandscape) Alignment.TopCenter else Alignment.Center
    val govukWidth = if (isLandscape) 0.325f else 0.65f
    val crownWidth = if (isLandscape) 0.10f else 0.20f
    val crownPadding = if (isLandscape) 0.0f else 0.10f

    val windowInfo = LocalWindowInfo.current
    val screenWidthInPx = windowInfo.containerSize.width
    val screenHeightInPx = windowInfo.containerSize.height

    val govukWidthInDp = screenWidthInPx.toDpWithRatio(govukWidth)
    val crownWidthInDp = screenWidthInPx.toDpWithRatio(crownWidth)
    val crownPaddingInDp = screenHeightInPx.toDpWithRatio(crownPadding)

    if (LocalContext.current.areAnimationsEnabled()) {
        govukProgress = govukStateProgress.progress
        crownProgress = crownStateProgress.progress
        LaunchedEffect(govukProgress, crownProgress) {
            if (govukProgress == 1f && crownProgress == 1f) {
                onSplashDone()
            }
        }
    } else {
        govukProgress = 1.0f
        crownProgress = 1.0f
        LaunchedEffect(Unit) {
            delay(1_000)
            onSplashDone()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.splash)
    ) {
        LottieAnimation(
            composition = govukSplashComposition,
            progress = { govukProgress },
            modifier = Modifier
                .align(govukAlignment)
                .semantics {
                    contentDescription = altText
                }
                .width(govukWidthInDp)
        )

        LottieAnimation(
            composition = crownSplashComposition,
            progress = { crownProgress },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .semantics {
                    contentDescription = altText
                }
                .width(crownWidthInDp)
                .padding(bottom = crownPaddingInDp)
        )
    }
}

@Composable
private fun Int.toDpWithRatio(
    ratio: Float
) = with(LocalDensity.current) {
    (this@toDpWithRatio * ratio).toDp()
}
