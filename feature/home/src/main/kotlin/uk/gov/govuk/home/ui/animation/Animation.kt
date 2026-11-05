package uk.gov.govuk.home.ui.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin

@Composable
internal fun AnimateIcon(
    visible: Boolean,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val animationSpeed = 300

    val animateIn: FiniteAnimationSpec<Float> = tween(
        durationMillis = animationSpeed,
        easing = EaseIn
    )

    val animateOut: FiniteAnimationSpec<Float> = tween(
        durationMillis = animationSpeed,
        easing = EaseOut
    )

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = animateIn,
            initialScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        ) + fadeIn(
            animationSpec = animateIn,
            initialAlpha = 0f
        ),
        exit = scaleOut(
            animationSpec = animateOut,
            targetScale = 0.1f,
            transformOrigin = TransformOrigin.Center
        ) + fadeOut(
            animationSpec = animateOut,
            targetAlpha = 0f
        ),
        modifier = modifier
    ) {
        icon()
    }
}
