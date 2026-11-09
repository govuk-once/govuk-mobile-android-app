package uk.gov.govuk.design.ui.component

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun StatusBar(
    hideBackground: Boolean,
    useDarkIcons: Boolean,
    modifier: Modifier = Modifier
) {
    val localView = LocalView.current
    val window = (localView.context as Activity).window

    if (!hideBackground) {
        Box(
            modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(GovUkTheme.colourScheme.surfaces.homeHeader)
        )
    }

    WindowCompat.getInsetsController(window, localView).apply {
        isAppearanceLightStatusBars = useDarkIcons
        isAppearanceLightNavigationBars = !hideBackground && !isSystemInDarkTheme()
    }
}

@Composable
fun FullScreenWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        StatusBar(
            hideBackground = true,
            useDarkIcons = !isSystemInDarkTheme()
        )
        content()
    }
}