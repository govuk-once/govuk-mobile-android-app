package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FixedContainerDivider
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.extension.areAnimationsEnabled
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun OnboardingPage(
    title: String,
    headerContent: @Composable () -> Unit,
    screenContent: @Composable ColumnScope.() -> Unit,
    buttonContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    @RawRes animationRes: Int
) {
    Column(
        modifier.fillMaxSize()
            .background(
                GovUkTheme.colourScheme.surfaces.background
            )
    ) {
        headerContent()

        Column(Modifier.weight(weight = 1f)) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(
                        horizontal = GovUkTheme.spacing.medium,
                        vertical = GovUkTheme.spacing.large
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val isImageVisible = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

                if (isImageVisible) {
                    val animationsEnabled = LocalContext.current.areAnimationsEnabled()

                    val animationComposition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(animationRes)
                    )

                    val animationState = animateLottieCompositionAsState(
                        composition = animationComposition,
                        isPlaying = animationsEnabled,
                        restartOnPlay = false
                    )

                    LottieAnimation(
                        composition = animationComposition,
                        progress = {
                            if (animationsEnabled)
                                animationState.progress
                            else 1f
                        }
                    )
                }

                LargeTitleBoldLabel(
                    text = title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .semantics { heading() }
                )

                screenContent()
            }
        }

        FixedContainerDivider()

        MediumVerticalSpacer()

        buttonContent()

        ExtraLargeVerticalSpacer()
    }
}
